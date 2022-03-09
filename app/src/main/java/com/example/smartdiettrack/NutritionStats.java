package com.example.smartdiettrack;

import android.content.Context;

public class NutritionStats {

    private final int gender;
    private final double heightCm;
    private final double weightKg;
    private final int age;
    private final int goal;
    private final double activityLevel;
    /**
     * Nutrition stats of user.
     * @param gender Male (1) or Female (2)
     * @param heightFeet Height - feet
     * @param heightInches Height - inches
     * @param weightLbs Weight in lbs
     * @param age Age in years
     * @param goal Lose (1), Gain (2), Maintain (3)
     * @param activityLevel activity level
     */
    public NutritionStats(String gender, String heightFeet, String heightInches,  String weightLbs, String age, String goal, String activityLevel, Context context){
        this.heightCm = Double.parseDouble(heightFeet) * 30.48 + Double.parseDouble(heightInches) * 2.54;
        this.weightKg = Double.parseDouble(weightLbs)/2.205;
        this.age = Integer.parseInt(age);

        if (gender.equals(context.getString(R.string.Male))){
            this.gender = 1;
        }
        else {
            this.gender = 2;
        }

        if (goal.equals(context.getString(R.string.LoseWeight))){
            this.goal = 1;
        }
        else if(goal.equals(context.getString(R.string.GainWeight))) {
            this.goal = 2;
        }
        else{
            this.goal = 3;
        }

        if (activityLevel.equals(context.getString(R.string.VeryActive))){
            this.activityLevel = 1.725;
        }
        else if(activityLevel.equals(context.getString(R.string.Active))) {
            this.activityLevel = 1.55;
        }
        else{
            this.activityLevel = 1.2;
        }
    }

    public double BMR(){
        if(gender == 1){
            return (activityLevel*(66 + (13.7*weightKg)+(5*heightCm)-(6.8*age)));
        }else{
            return (activityLevel*(655 + (9.6*weightKg)+(1.8*heightCm)-(4.7*age)));
        }
    }

    /**
     * How many calories the user must consume per day to reach their goal.
     * @param lbsPerWeek How many pounds per week to gain/lose
     * @return Daily calorie count.
     */
    public int dailyCalories(int lbsPerWeek){
        int BMR = (int) BMR();
        if(goal == 3){
            return BMR;
        } else if(goal == 2){
            return BMR+500*lbsPerWeek;
        } else {
            return BMR-500*lbsPerWeek;
        }
    }
}
