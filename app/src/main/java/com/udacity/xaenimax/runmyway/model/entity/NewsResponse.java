
package com.udacity.xaenimax.runmyway.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsResponse {

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("copyright")
    @Expose
    public String copyright;
    @SerializedName("response")
    @Expose
    public Response response;

}
