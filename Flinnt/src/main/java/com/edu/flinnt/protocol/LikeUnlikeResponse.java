package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * class to parse response to object
 */
public class LikeUnlikeResponse extends BaseResponse {

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

    public class Data {

        @SerializedName("count")
        @Expose
        private String count;
        @SerializedName("like")
        @Expose
        private Integer like;
        @SerializedName("dislike")
        @Expose
        private Integer dislike;

        /**
         * @return The count
         */
        public String getCount() {
            return count;
        }

        /**
         * @param count The count
         */
        public void setCount(String count) {
            this.count = count;
        }

        /**
         * @return The like
         */
        public Integer getLike() {
            return like;
        }

        /**
         * @param like The like
         */
        public void setLike(Integer like) {
            this.like = like;
        }

        /**
         * @return The dislike
         */
        public Integer getDislike() {
            return dislike;
        }

        /**
         * @param dislike The dislike
         */
        public void setDislike(Integer dislike) {
            this.dislike = dislike;
        }

    }
}