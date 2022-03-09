package com.example.smartdiettrack.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FoodData.class}, version = 3, exportSchema = false)
public abstract class FoodDB extends RoomDatabase {
    private static FoodDB database;
    public static String DATABASE_NAME = "FOOD_DB";
    public abstract FoodDao foodDao();

    public synchronized static FoodDB getInstance(Context context) {
        if(database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), FoodDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }

        return database;
    }
}
