package com.example.smartdiettrack.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "food_entities")
public class FoodData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "calories")
    private float calories;

    @ColumnInfo(name = "protein")
    private float protein;

    @ColumnInfo(name = "carbs")
    private float carbs;

    @ColumnInfo(name = "fats")
    private float fats;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "thumb")
    private String thumb;

    @ColumnInfo(name = "servingQty")
    private int servingQty;

    @ColumnInfo(name = "servingUnit")
    private String servingUnit;

    // Define getter and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCalories() {
        return this.calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return this.protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFats() {
        return this.fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getCarbs() {
        return this.carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getServingQty() {
        return servingQty;
    }

    public void setServingQty(int servingQty) {
        this.servingQty = servingQty;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }
}
