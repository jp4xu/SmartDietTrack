package com.example.smartdiettrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BiometricInputActivity extends AppCompatActivity {

    private final String TAG = "BIOMETRICS";
    private TextInputEditText ageText;
    private TextInputEditText weightText;
    private TextInputEditText heightFt;
    private TextInputEditText heightInches;
    private RadioGroup genderRadioGroup;
    private RadioGroup activityRadioGroup;
    private RadioGroup weightGoalRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_input);

        ageText = findViewById(R.id.inputAgeText);
        weightText = findViewById(R.id.inputWeightText);
        heightFt = findViewById(R.id.inputHeightFtText);
        heightInches = findViewById(R.id.inputHeightInchesText);
        genderRadioGroup = findViewById(R.id.radioSexSelection);
        activityRadioGroup = findViewById(R.id.radioActivitySelection);
        weightGoalRadioGroup = findViewById(R.id.radioWeightGoalSelection);
    }

    public void onConfirm(View view) {
        RadioButton gender = findViewById(genderRadioGroup.getCheckedRadioButtonId());
        RadioButton activity = findViewById(activityRadioGroup.getCheckedRadioButtonId());
        RadioButton weightGoal = findViewById(weightGoalRadioGroup.getCheckedRadioButtonId());
        if(ageText.length() == 0){
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(weightText.length() == 0){
            Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(heightFt.length() == 0){
            Toast.makeText(this, "Please enter your height in feet", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(heightInches.length() == 0){
            Toast.makeText(this, "Please enter your height in inches", Toast.LENGTH_SHORT).show();
            return;
        }

        NutritionStats nutritionStats = new NutritionStats(
                gender.getText().toString(),
                heightFt.getText().toString(),
                heightInches.getText().toString(),
                weightText.getText().toString(),
                ageText.getText().toString(),
                weightGoal.getText().toString(),
                activity.getText().toString(),
                this);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Biometrics), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(getString(R.string.Age), ageText.getText().toString());
        editor.putString(getString(R.string.Weight), weightText.getText().toString());
        editor.putString(getString(R.string.ft), heightFt.getText().toString());
        editor.putString(getString(R.string.inches), heightInches.getText().toString());
        editor.putString(getString(R.string.Gender), gender.getText().toString());
        editor.putString(getString(R.string.Activity), activity.getText().toString());
        editor.putString(getString(R.string.WeightGoal), weightGoal.getText().toString());
        editor.putInt(getString(R.string.CalorieGoal), nutritionStats.dailyCalories(1));
        editor.commit();

        Log.d(TAG, "Age: " + ageText.getText().toString() + " Weight: " + weightText.getText().toString() +
                " Ft: " + heightFt.getText().toString() + " Inches: " + heightInches.getText().toString() +
                " Gender: " + gender.getText().toString() + " Activity: " + activity.getText().toString() + " WeightGoal: " + weightGoal.getText().toString());

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}