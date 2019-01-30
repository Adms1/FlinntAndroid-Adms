package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class UpdateAnswerResponse extends BaseResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("updated")
        @Expose
        private Integer updated;

        public Integer getUpdated() {
            return updated;
        }

        public void setUpdated(Integer updated) {
            this.updated = updated;
        }

    }
}
