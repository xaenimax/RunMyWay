package com.udacity.xaenimax.runmyway.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "run_session")
public class RunSession {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public long duration;
    public double distance;
    public int calories;
    @ColumnInfo(name = "session_date")
    public Date sessionDate;

    public RunSession(int id, long duration, double distance, int calories, Date sessionDate) {
        this.id = id;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.sessionDate = sessionDate;
    }

    @Ignore
    public RunSession(long duration, double distance, int calories, Date sessionDate) {
        this.id = id;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.sessionDate = sessionDate;
    }
}
