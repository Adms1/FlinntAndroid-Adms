package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class ContentViewersResponse extends BaseResponse {

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
        @SerializedName("viewers")
        @Expose
        private ArrayList<Viewer> viewers = new ArrayList<Viewer>();

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
         * @return The viewers
         */
        public ArrayList<Viewer> getViewers() {
            return viewers;
        }

        /**
         * @param viewers The viewers
         */
        public void setViewers(ArrayList<Viewer> viewers) {
            this.viewers = viewers;
        }

        public void clearViewersList() {
            this.viewers.clear();
        }

    }


    public class Viewer {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("user_firstname")
        @Expose
        private String userFirstname;
        @SerializedName("view_date")
        @Expose
        private String viewDate;
        @SerializedName("is_liked")
        @Expose
        private String isLiked;
        @SerializedName("is_commented")
        @Expose
        private String isCommented;

        /**
         * @return The userId
         */
        public String getUserId() {
            return userId;
        }

        /**
         * @param userId The user_id
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * @return The userPicture
         */
        public String getUserPicture() {
            return userPicture;
        }

        /**
         * @param userPicture The user_picture
         */
        public void setUserPicture(String userPicture) {
            this.userPicture = userPicture;
        }

        /**
         * @return The userFirstname
         */
        public String getUserFirstname() {
            return userFirstname;
        }

        /**
         * @param userFirstname The user_firstname
         */
        public void setUserFirstname(String userFirstname) {
            this.userFirstname = userFirstname;
        }

        /**
         * @return The viewDate
         */
        public String getViewDate() {
            return viewDate;
        }

        /**
         * @param viewDate The view_date
         */
        public void setViewDate(String viewDate) {
            this.viewDate = viewDate;
        }

        /**
         * @return The isLiked
         */
        public String getIsLiked() {
            return isLiked;
        }

        /**
         * @param isLiked The is_liked
         */
        public void setIsLiked(String isLiked) {
            this.isLiked = isLiked;
        }

        /**
         * @return The isCommented
         */
        public String getIsCommented() {
            return isCommented;
        }

        /**
         * @param isCommented The is_commented
         */
        public void setIsCommented(String isCommented) {
            this.isCommented = isCommented;
        }

    }
}