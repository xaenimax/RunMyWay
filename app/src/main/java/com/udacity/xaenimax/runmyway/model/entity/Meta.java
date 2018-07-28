
package com.udacity.xaenimax.runmyway.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("hits")
    @Expose
    public Integer hits;
    @SerializedName("offset")
    @Expose
    public Integer offset;
    @SerializedName("time")
    @Expose
    public Integer time;

}
