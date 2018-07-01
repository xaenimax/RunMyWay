package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.xaenimax.runmyway.model.Configuration;

import java.util.List;

@Dao
public interface ConfigurationDao {
    @Query("SELECT * FROM configuration ORDER BY id DESC")
    LiveData<List<Configuration>> listAllConfigurations();

    @Insert
    void insertConfiguration(Configuration configuration);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateConfiguration(Configuration configuration);

    @Delete
    void deleteConfiguration(Configuration configuration);
}
