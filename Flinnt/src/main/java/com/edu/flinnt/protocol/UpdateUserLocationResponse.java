package com.edu.flinnt.protocol;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-3 on 13/10/16.
 */
public class UpdateUserLocationResponse {
    @SerializedName("status")
    private Integer status;
    @SerializedName("data")
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
        public Integer getDistance_variation() {
            return distance_variation;
        }

        public void setDistance_variation(Integer distance_variation) {
            this.distance_variation = distance_variation;
        }
        @SerializedName("distance_variation")
        private Integer distance_variation;
        @SerializedName("saved")
        private Integer saved;

        /**
         * @return The saved
         */
        public Integer getSaved() {
            return saved;
        }

        /**
         * @param saved The saved
         */
        public void setSaved(Integer saved) {
            this.saved = saved;
        }
    }
}