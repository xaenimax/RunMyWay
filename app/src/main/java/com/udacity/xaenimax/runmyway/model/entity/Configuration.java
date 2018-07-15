package com.udacity.xaenimax.runmyway.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@Entity(tableName = "configuration")
public class Configuration implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    @ColumnInfo(name = "insert_date")
    public Date insertDate;

    @Ignore
    public Configuration(String name, Date insertDate){
        this.insertDate = insertDate;
        this.name = name;
    }

    public Configuration(long id, Date insertDate, String name){
        this.id = id;
        this.insertDate = insertDate;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.insertDate.getTime());
        dest.writeString(this.name);
    }

    @Ignore
    public static Creator<Configuration> CREATOR = new Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel source) {
            Configuration configuration = new Configuration(
                    source.readLong(),
                    new Date(source.readLong()),
                    source.readString()
            );
            return configuration;
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[0];
        }
    };
}
