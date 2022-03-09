package com.example.smartdiettrack;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartdiettrack.DB.FoodData;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.FoodViewHolder>{
    private List<FoodData> foodDataList;

    public RecycleAdapter(List<FoodData> foodDataList) {
        this.foodDataList = foodDataList;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView calories;
        private TextView protein;
        private TextView carbs;
        private TextView fats;

        public FoodViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.foodName);
            calories = view.findViewById(R.id.calorieAmount);
            protein = view.findViewById(R.id.proteinAmount);
            carbs = view.findViewById(R.id.carbsAmount);
            fats = view.findViewById(R.id.fatsAmount);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecycleAdapter.FoodViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodentry, parent, false);
        //itemView.setPadding(0, 20, 0, 20);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecycleAdapter.FoodViewHolder holder, int position) {
        FoodData foodData = foodDataList.get(position);
        BigDecimal calories = new BigDecimal(foodData.getCalories()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal carbs = new BigDecimal(foodData.getCarbs()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal protein = new BigDecimal(foodData.getProtein()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fats = new BigDecimal(foodData.getFats()).setScale(2, RoundingMode.HALF_UP);

        holder.name.setText(foodData.getName());
        holder.name.setTextColor(Color.BLACK);

        holder.calories.setText(calories.toString() + "cal");
        holder.calories.setTextColor(Color.BLACK);

        holder.carbs.setText(carbs.toString() + "g");
        holder.carbs.setTextColor(Color.parseColor("#f3d679"));

        holder.fats.setText(fats.toString() + "g");
        holder.fats.setTextColor(Color.parseColor("#f76c5e"));

        holder.protein.setText(protein.toString() + "g");
        holder.protein.setTextColor(Color.parseColor("#324376"));
    }

    @Override
    public int getItemCount() {
        return foodDataList.size();
    }

    public void updateData(List<FoodData> newFoodDataList) {
        final FoodDiffCallback diffCallback = new FoodDiffCallback(this.foodDataList, newFoodDataList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.foodDataList.clear();
        this.foodDataList.addAll(newFoodDataList);
        diffResult.dispatchUpdatesTo(this);
    }
}
