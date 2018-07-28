
package com.udacity.xaenimax.runmyway.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Headline {

    @SerializedName("main")
    @Expose
    public String main;
    @SerializedName("kicker")
    @Expose
    public Object kicker;
    @SerializedName("content_kicker")
    @Expose
    public Object contentKicker;
    @SerializedName("print_headline")
    @Expose
    public Object printHeadline;
    @SerializedName("name")
    @Expose
    public Object name;
    @SerializedName("seo")
    @Expose
    public Object seo;
    @SerializedName("sub")
    @Expose
    public Object sub;

}
