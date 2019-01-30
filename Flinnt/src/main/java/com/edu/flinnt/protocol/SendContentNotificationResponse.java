package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * class to parse response to object
 */
public class SendContentNotificationResponse extends BaseResponse {

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

        @SerializedName("sent")
        @Expose
        private Integer sent;

        public Integer getSent() {
            return sent;
        }

        public void setSent(Integer sent) {
            this.sent = sent;
        }

    }
}
