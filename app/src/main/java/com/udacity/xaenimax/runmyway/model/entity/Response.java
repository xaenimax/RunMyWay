
package com.udacity.xaenimax.runmyway.model.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("docs")
    @Expose
    public List<Doc> docs = null;
    @SerializedName("meta")
    @Expose
    public Meta meta;

}
