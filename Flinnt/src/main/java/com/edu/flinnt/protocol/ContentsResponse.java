package com.edu.flinnt.protocol;

import com.edu.flinnt.protocol.contentlist.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class ContentsResponse extends BaseResponse {
    public static int QUIZ_TYPE_ID = 17;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * @return The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }
}