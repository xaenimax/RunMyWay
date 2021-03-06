package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;

import java.util.List;

@Dao
public interface ConfigurationStepDao {
    @Query("SELECT * FROM configuration_step where id_configuration = :configurationId ORDER BY position")
    LiveData<List<ConfigurationStep>> listAllConfigurationSteps(long configurationId);

    @Insert
    long insertConfiguration(ConfigurationStep configuration);

    @Insert
    void insertAll(List<ConfigurationStep> configurationSteps);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateConfigurationStep(ConfigurationStep configuration);

    @Delete
    void deleteConfigurationStep(ConfigurationStep configuration);
}
