package com.edu.flinnt.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoryListDataModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private StoryDataModel data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public StoryDataModel getData() {
        return data;
    }

    public void setData(StoryDataModel data) {
        this.data = data;
    }

}
