package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class ContentCommentsResponse extends BaseResponse {

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

        @SerializedName("has_more")
        @Expose
        private Integer hasMore;
        @SerializedName("comments")
        @Expose
        private ArrayList<ContentComment> comments = new ArrayList<ContentComment>();

        /**
         * @return The hasMore
         */
        public Integer getHasMore() {
            return hasMore;
        }

        /**
         * @param hasMore The has_more
         */
        public void setHasMore(Integer hasMore) {
            this.hasMore = hasMore;
        }

        /**
         * @return The comments
         */
        public ArrayList<ContentComment> getComments() {
            return comments;
        }

        /**
         * @param comments The comments
         */
        public void setComments(ArrayList<ContentComment> comments) {
            this.comments = comments;
        }

        public void clearCommentList() {
            this.comments.clear();
        }
    }
}
