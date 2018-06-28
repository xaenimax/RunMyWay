package com.udacity.xaenimax.runmyway.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "configuration_step")
public class ConfigurationStep {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int position;
    public int duration;
    @ColumnInfo(name = "step_type")
    public String stepType;
    @ColumnInfo(name = "id_configuration")
    public int configurationId;

    @Ignore
    public ConfigurationStep(int position, int duration, String stepType, int configurationId){
        this.position = position;
        this.duration = duration;
        this.stepType = stepType;
        this.configurationId = configurationId;
    }

    public ConfigurationStep(int id, int position, int duration, String stepType, int configurationId){
        this.id = id;
        this.position = position;
        this.duration = duration;
        this.stepType = stepType;
        this.configurationId = configurationId;
    }
}
