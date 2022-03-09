package com.example.smartdiettrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smartdiettrack.DB.FoodDB;
import com.example.smartdiettrack.DB.FoodData;

public class ConfirmFoodActivity extends AppCompatActivity {
    private final String TAG = "FOODCONFIRM";
    TextView caloriesView;
    TextView carbsView;
    TextView proteinView;
    TextView fatsView;
    TextView unitView;
    TextView qtyView;
    TextView foodNameView;
    ImageView thumbnail;
    FoodData foodData;
    String foodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_food);
        caloriesView = findViewById(R.id.foodCalories);
        carbsView = findViewById(R.id.foodCarbs);
        proteinView = findViewById(R.id.foodProtein);
        fatsView = findViewById(R.id.foodFats);
        unitView = findViewById(R.id.servingUnit);
        qtyView = findViewById(R.id.servingQty);
        thumbnail = findViewById(R.id.thumbView);
        foodNameView = findViewById(R.id.confirmFoodName);

        foodData = (FoodData) getIntent().getSerializableExtra("FoodData");
        //Capitilize first letter
        StringBuilder sb = new StringBuilder(foodData.getName());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        foodName = sb.toString();

        String result = "\nFood Name: " + foodName + "\nCalories: " + foodData.getCalories() + "\nProtein: " + foodData.getProtein() +
                "\nFat: " + foodData.getFats() + "\nCarb: " + foodData.getCarbs() + "\nServing size: " + foodData.getServingQty()
                + "\nServing unit: " + foodData.getServingUnit();
        Log.d(TAG, result);
        foodNameView.setText(foodName);
        updateFoodInfoUI();
        Glide.with(this).load(foodData.getThumb()).circleCrop().into(thumbnail);
    }

    public void onConfirmFood(View view){
        //update food data for quantity change
        int foodQty = foodData.getServingQty();
        foodData.setCalories(foodData.getCalories()*foodQty);
        foodData.setCarbs(foodData.getCarbs()*foodQty);
        foodData.setProtein(foodData.getProtein()*foodQty);
        foodData.setFats(foodData.getFats()*foodQty);

        FoodDB foodDB = FoodDB.getInstance(this);
        foodDB.foodDao().insertFood(foodData);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onIncrementQty(View view){
        foodData.setServingQty(foodData.getServingQty()+1);
        updateFoodInfoUI();
    }

    public void onDecrementQty(View view){
        if(foodData.getServingQty() >1) {
            foodData.setServingQty(foodData.getServingQty() - 1);
        }
        updateFoodInfoUI();
    }

    public void updateFoodInfoUI(){
        int foodQty = foodData.getServingQty();
        qtyView.setText(Integer.toString(foodData.getServingQty()));
        caloriesView.setText(String.format("Calories: %.2f", (foodData.getCalories() * foodQty)));
        carbsView.setText(String.format("Carbs: %.2fg", (foodData.getCarbs() * foodQty)));
        proteinView.setText(String.format("Protein: %.2fg", (foodData.getProtein() * foodQty)));
        fatsView.setText(String.format("Fats: %.2fg", (foodData.getFats() * foodQty)));
        unitView.setText(foodData.getServingUnit());
    }
}