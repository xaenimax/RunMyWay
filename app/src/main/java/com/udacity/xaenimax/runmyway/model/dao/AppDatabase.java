package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.udacity.xaenimax.runmyway.model.Configuration;
import com.udacity.xaenimax.runmyway.model.ConfigurationStep;

@Database(entities = {Configuration.class, ConfigurationStep.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "run_my_way";
    private  static AppDatabase mAppDatabase;

    public static AppDatabase getInstance(Context context){
        if(mAppDatabase == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database");
                mAppDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME).build();
            }
        }
        return mAppDatabase;
    }

    public abstract ConfigurationDao configurationDao();
    public abstract ConfigurationStepDao configurationStepDao();
}
