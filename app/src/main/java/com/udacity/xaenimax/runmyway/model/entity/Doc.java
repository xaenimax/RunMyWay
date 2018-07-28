
package com.udacity.xaenimax.runmyway.model.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Doc {

    @SerializedName("web_url")
    @Expose
    public String webUrl;
    @SerializedName("snippet")
    @Expose
    public String snippet;
    @SerializedName("blog")
    @Expose
    public Blog blog;
    @SerializedName("multimedia")
    @Expose
    public List<Object> multimedia = null;
    @SerializedName("headline")
    @Expose
    public Headline headline;
    @SerializedName("keywords")
    @Expose
    public List<Object> keywords = null;
    @SerializedName("document_type")
    @Expose
    public String documentType;
    @SerializedName("section_name")
    @Expose
    public String sectionName;
    @SerializedName("type_of_material")
    @Expose
    public String typeOfMaterial;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("word_count")
    @Expose
    public Integer wordCount;
    @SerializedName("score")
    @Expose
    public Double score;

}
