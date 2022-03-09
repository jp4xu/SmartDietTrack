package com.example.smartdiettrack;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.smartdiettrack.DB.FoodData;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NutritionxAPI {

    private final String TAG = "API";
    private String appId;
    private String appKey;
    Context context;
    private final String userId = "0";
    private final String queryUrl = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    String foodName;
    FoodData foodData;
    RequestQueue requestQueue;

    public NutritionxAPI(Context context, String foodName){
        this.context = context;
        this.foodName = foodName;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            appId = applicationInfo.metaData.get("nutritionxID").toString();
            appKey = applicationInfo.metaData.get("nutritionxKey").toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the food name given to the activity to make a simple query in the database
     */
    public void nutrientQuery() {
        requestQueue = Volley.newRequestQueue(context);
        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, queryUrl,
                response -> {try {
                    JSONObject jsonResponseObj = new JSONObject(response).getJSONArray("foods").getJSONObject(0);

                    String foodName = jsonResponseObj.getString("food_name");
                    String calorieAmount = String.valueOf(jsonResponseObj.getDouble("nf_calories"));
                    String proteinAmount = String.valueOf(jsonResponseObj.getDouble("nf_protein"));
                    String fatAmount = String.valueOf(jsonResponseObj.getDouble("nf_total_fat"));
                    String carbAmount = String.valueOf(jsonResponseObj.getDouble("nf_total_carbohydrate"));
                    String servingSize = String.valueOf(jsonResponseObj.getInt("serving_qty"));
                    String servingUnit = jsonResponseObj.getString("serving_unit");
                    String imageThumbnail = jsonResponseObj.getJSONObject("photo").getString("thumb");
                    String parsedResult = "\nFood Name: " + foodName + "\nCalories: " + calorieAmount + "\nProtein: " + proteinAmount +
                            "\nFat: " + fatAmount + "\nCarb: " + carbAmount + "\nServing size: " + servingSize + "\nServing unit: " + servingUnit;
                    Log.d(TAG, parsedResult);


                    String timestamp = new SimpleDateFormat(context.getString(R.string.DateFormat)).format(Calendar.getInstance().getTime());
                    foodData = new FoodData();
                    foodData.setName(foodName);
                    foodData.setCalories(Float.valueOf(calorieAmount));
                    foodData.setCarbs(Float.valueOf(carbAmount));
                    foodData.setFats(Float.valueOf(fatAmount));
                    foodData.setProtein(Float.valueOf(proteinAmount));
                    foodData.setTimestamp(timestamp);
                    foodData.setThumb(imageThumbnail);
                    foodData.setServingQty(Integer.parseInt(servingSize));
                    foodData.setServingUnit(servingUnit);

                    Intent intent = new Intent(context, ConfirmFoodActivity.class);
                    intent.putExtra("FoodData", foodData);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }}, error -> error.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params go here
                params.put("query", foodName);
                params.put("timezone", "US/Eastern");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers go here

                headers.put("x-app-id", appId);
                headers.put("x-app-key", appKey);
                headers.put("x-remote-user-id", userId);
                return headers;
            }
        };
        stringRequest.setTag(TAG);
        Log.d(TAG, stringRequest.toString());
        requestQueue.add(stringRequest);
    }

}