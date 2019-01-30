package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-2 on 21/10/16.
 */
public class CourseSendResponse extends BaseResponse {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("sent")
        @Expose
        private Integer sent;

        /**
         *
         * @return
         * The sent
         */
        public Integer getSent() {
            return sent;
        }

        /**
         *
         * @param sent
         * The sent
         */
        public void setSent(Integer sent) {
            this.sent = sent;
        }

    }
}
