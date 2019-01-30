package com.edu.flinnt.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 13-02-2018.
 */
public class PopularStoryList {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private PopularStoryDataModel data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public PopularStoryDataModel getData() {
        return data;
    }

    public void setData(PopularStoryDataModel data) {
        this.data = data;
    }

}