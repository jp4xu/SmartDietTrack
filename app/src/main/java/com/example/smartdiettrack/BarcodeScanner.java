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
import com.android.volley.toolbox.Volley;
import com.example.smartdiettrack.DB.FoodData;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BarcodeScanner {
    String barcode;
    String appId;
    String appKey;
    Context context;
    private final String queryUrl = "https://trackapi.nutritionix.com/v2/search/item/";
    private final String TAG = "BARCODEAPI";
    RequestQueue requestQueue;
    public BarcodeScanner(Context context,String barcode) {
        if (barcode.length() == 8) {
            //Need to convert UPCE to UPCA
            this.barcode = UPCE2UPCA(barcode);
        }
        else {
            this.barcode = barcode;
        }
        this.context = context;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            appId = applicationInfo.metaData.get("nutritionxID").toString();
            appKey = applicationInfo.metaData.get("nutritionxKey").toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String UPCE2UPCA(String UPCE) {
        String result = "";

        switch (UPCE.charAt(6)) {
            case '0':
            case '1':
            case '2': {
                result = UPCE.substring(0, 3) + "0000" + UPCE.charAt(6) + UPCE.substring(3, 6) + UPCE.charAt(7);
                break;
            }
            case '3': {
                result = UPCE.substring(0, 4) + "00000" + UPCE.substring(4, 5) + UPCE.charAt(7);
                break;
            }
            case '4': {
                result = UPCE.substring(0, 5) + "00000" + UPCE.charAt(5) + UPCE.charAt(7);
                break;
            }
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                result = UPCE.substring(0, 6) + "0000" + UPCE.charAt(6) + UPCE.charAt(7);
                break;
            }
        }
        return result;
    }

    public void nutrientQuery() {
        requestQueue = Volley.newRequestQueue(this.context);

        //cant use params since GET does not have a body
        String url = String.format(this.queryUrl + "?upc=%1$s", this.barcode);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponseObj = new JSONObject(response).getJSONArray("foods").getJSONObject(0);
                        String foodName = jsonResponseObj.get("brand_name") + " " + jsonResponseObj.getString("food_name");
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

                        //pass food data to confirm activity
                        String timestamp = new SimpleDateFormat(context.getString(R.string.DateFormat)).format(Calendar.getInstance().getTime());
                        FoodData foodData = new FoodData();
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
                    }
                },
                error -> {
                    error.printStackTrace();
                    try {
                        String errorOutput = new String(error.networkResponse.data, "UTF-8");
                        Log.d(TAG, "Here is the entire error: " + errorOutput);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers go here
                headers.put("x-app-id", appId);
                headers.put("x-app-key", appKey);
                return headers;
            }
        };
        stringRequest.setTag(TAG);
        requestQueue.add(stringRequest);
    }

}
