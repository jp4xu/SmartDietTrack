package com.example.smartdiettrack;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MLAPI {

    private final String TAG = "MLAPI";
    private String appId;
    private String appKey;
    Context context;
    private final String queryUrl = "http://3.17.65.238:5000";
    String foodName;
    String foodImg;
    RequestQueue requestQueue;

    public MLAPI(Context context, String foodImg){
        this.context = context;
        this.foodImg = foodImg;
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Uses the food image given to the to make a machine learning request
     */
    public void identifyFoodRequest() {
        SimpleMultiPartRequest multiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, queryUrl+"/api/predict",
                response -> {try {
                    JSONObject jsonResponseObj = new JSONObject(response);
                    //parse ID of request then check status
                    String result_id = jsonResponseObj.getString("result-id");
                    getFoodResult(result_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }}, error -> error.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params go here
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers go here
                headers.put("content", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        multiPartRequest.setTag(TAG);
        multiPartRequest.addFile("food-img", foodImg);
        Log.d(TAG, multiPartRequest.toString());
        requestQueue.add(multiPartRequest);
    }

    public void getFoodResult(String request_id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, queryUrl+"/api/get-result/"+request_id,
                response -> {try {
                    JSONObject jsonResponseObj = new JSONObject(response);
                    //parse ID of request then check status
                    String status = jsonResponseObj.getString("status");
                    if (status.equals("SUCCESS")){
                        NutritionxAPI foodAPI = new NutritionxAPI(context, jsonResponseObj.getJSONObject("result").getString("prediction"));
                        foodAPI.nutrientQuery();
                    }else{
                        //try again
                        requestQueue.getCache().clear();
                        getFoodResult(request_id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }}, error -> error.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params go here
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers go here
                headers.put("content", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        stringRequest.setTag(TAG);
        Log.d(TAG, stringRequest.toString());
        requestQueue.add(stringRequest);
    }
}