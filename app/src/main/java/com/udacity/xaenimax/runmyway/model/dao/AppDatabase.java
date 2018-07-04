package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.udacity.xaenimax.runmyway.model.Configuration;
import com.udacity.xaenimax.runmyway.model.ConfigurationStep;
import com.udacity.xaenimax.runmyway.model.RunSession;

@Database(entities = {Configuration.class, ConfigurationStep.class, RunSession.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
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
    public abstract RunSessionDao runSessionDao();
}
