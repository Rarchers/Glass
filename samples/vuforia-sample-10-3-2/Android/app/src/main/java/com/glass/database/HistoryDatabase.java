package com.glass.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HistoryBean.class},version = 1,exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
    private static final String DB_NAME = "UserDatabase.db";
    private static volatile HistoryDatabase instance;

    static synchronized HistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static HistoryDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                HistoryDatabase.class,
                DB_NAME).build();
    }

    public abstract HistoryDao getUserDao();
}