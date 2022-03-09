package com.example.smartdiettrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.smartdiettrack.DB.FoodData;
import com.google.android.material.textfield.TextInputEditText;

public class InputTextActivity extends AppCompatActivity {

    private final String TAG="InputFoodText";
    private TextInputEditText foodText;
    private AppCompatButton confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_text);
        foodText = findViewById(R.id.inputFoodText);
    }

    public void onConfirmText(View view) {
        if(foodText.length() == 0) {
            Toast.makeText(this, "Please enter your food", Toast.LENGTH_SHORT).show();
            return;
        }
        NutritionxAPI foodAPI = new NutritionxAPI(this, foodText.getText().toString());
        foodAPI.nutrientQuery();
        Log.d(TAG, foodText.getText().toString());
    }
}