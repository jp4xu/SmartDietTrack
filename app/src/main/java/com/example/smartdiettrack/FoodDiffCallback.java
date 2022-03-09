package com.example.smartdiettrack;
import androidx.recyclerview.widget.DiffUtil;
import com.example.smartdiettrack.DB.FoodData;
import java.util.List;

public class FoodDiffCallback extends DiffUtil.Callback {
    private final List<FoodData> mOldFoodList;
    private final List<FoodData> mNewFoodList;

    public FoodDiffCallback(List<FoodData> mOldFoodList, List<FoodData> mNewFoodList) {
        this.mOldFoodList = mOldFoodList;
        this.mNewFoodList = mNewFoodList;
    }

    @Override
    public int getOldListSize() {
        return mOldFoodList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewFoodList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        FoodData oldFood = mOldFoodList.get(oldItemPosition);
        FoodData newFood = mNewFoodList.get(newItemPosition);

        return oldFood.getName() == newFood.getName() && oldFood.getCalories() == newFood.getCalories() && oldFood.getTimestamp() == newFood.getTimestamp();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        FoodData oldFood = mOldFoodList.get(oldItemPosition);
        FoodData newFood = mNewFoodList.get(newItemPosition);

        return oldFood.getName() == newFood.getName() && oldFood.getCalories() == newFood.getCalories()
                && newFood.getCarbs() == oldFood.getCarbs() && newFood.getFats() == oldFood.getFats() &&
                newFood.getProtein() == oldFood.getProtein();
    }
}
