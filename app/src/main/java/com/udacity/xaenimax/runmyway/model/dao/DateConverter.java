package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp == null ? null :  new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return date == null ? null :  date.getTime();
    }
}
