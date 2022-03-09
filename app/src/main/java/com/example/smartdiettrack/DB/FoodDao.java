package com.example.smartdiettrack.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodDao {
    //Define Query methods

    @Insert()
    void insertFood(FoodData... foodData);

    @Delete
    void delete(FoodData foodData);

    @Delete
    void reset (List<FoodData> foodDataList);

    @Query("UPDATE food_entities " +
    "SET name = :sName, calories = :sCalories, protein = :sProtein, carbs = :sCarbs, fats = :sFat WHERE id = :sID")
    void update(int sID, String sName, float sCalories, float sProtein, float sCarbs, float sFat);

    @Query("UPDATE food_entities SET timestamp = :sTimestamp WHERE id = :sID")
    void updateTimestamp(int sID, String sTimestamp);

    @Query("SELECT * FROM food_entities")
    List<FoodData> getAll();

    @Query("SELECT * FROM food_entities WHERE timestamp = :sTimestamp")
    List<FoodData> getEntitiesFromDate(String sTimestamp);

    @Query("SELECT SUM(calories) FROM food_entities WHERE timestamp = :sTimestamp")
    float getCaloriesFromDay(String sTimestamp);

    @Query("SELECT SUM(protein) FROM food_entities WHERE timestamp = :sTimestamp")
    float getProteinFromDay(String sTimestamp);

    @Query("SELECT SUM(carbs) FROM food_entities WHERE timestamp = :sTimestamp")
    float getCarbsFromDay(String sTimestamp);

    @Query("SELECT SUM(fats) FROM food_entities WHERE timestamp = :sTimestamp")
    float getFatsFromDay(String sTimestamp);
}
