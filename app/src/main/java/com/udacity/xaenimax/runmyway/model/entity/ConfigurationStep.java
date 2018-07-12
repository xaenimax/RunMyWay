package com.udacity.xaenimax.runmyway.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.xaenimax.runmyway.R;

@Entity(tableName = "configuration_step")
public class ConfigurationStep implements Parcelable {
    public enum StepType {Walk, Run};

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int position;
    public int duration;
    @ColumnInfo(name = "step_type")
    public String stepType;
    @ColumnInfo(name = "id_configuration")
    public int configurationId;

    @Ignore
    public ConfigurationStep(int duration, String stepType){
        this.position = 0;
        this.duration = duration;
        this.stepType = stepType;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.position);
        dest.writeInt(this.duration);
        dest.writeString(this.stepType);
        dest.writeInt(this.configurationId);
    }

    @Ignore
    public static Creator<ConfigurationStep> CREATOR = new Creator<ConfigurationStep>() {
        @Override
        public ConfigurationStep createFromParcel(Parcel source) {
            return new ConfigurationStep(
                    source.readInt(),
                    source.readInt(),
                    source.readInt(),
                    source.readString(),
                    source.readInt()
            );
        }

        @Override
        public ConfigurationStep[] newArray(int size) {
            return new ConfigurationStep[0];
        }
    };
}
