package com.udacity.xaenimax.runmyway.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "configuration")
public class Configuration {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int position;
    public String name;

    @Ignore
    public Configuration(int position, String name){
        this.position = position;
        this.name = name;
    }

    public Configuration(int id, int position, String name){
        this.id = id;
        this.position = position;
        this.name = name;
    }

}
