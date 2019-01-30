package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class ContentViewStatisticsResponse extends BaseResponse {

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

        @SerializedName("user_picture_url")
        @Expose
        private String userPictureUrl;
        @SerializedName("total_views")
        @Expose
        private String totalViews;
        @SerializedName("total_likes")
        @Expose
        private String totalLikes;
        @SerializedName("total_comments")
        @Expose
        private String totalComments;
        @SerializedName("viewers")
        @Expose
        private ArrayList<Viewers> viewers = new ArrayList<Viewers>();

        /**
         * @return The userPictureUrl
         */
        public String getUserPictureUrl() {
            return userPictureUrl;
        }

        /**
         * @param userPictureUrl The user_picture_url
         */
        public void setUserPictureUrl(String userPictureUrl) {
            this.userPictureUrl = userPictureUrl;
        }

        /**
         * @return The totalViews
         */
        public String getTotalViews() {
            return totalViews;
        }

        /**
         * @param totalViews The total_views
         */
        public void setTotalViews(String totalViews) {
            this.totalViews = totalViews;
        }

        /**
         * @return The totalLikes
         */
        public String getTotalLikes() {
            return totalLikes;
        }

        /**
         * @param totalLikes The total_likes
         */
        public void setTotalLikes(String totalLikes) {
            this.totalLikes = totalLikes;
        }

        /**
         * @return The totalComments
         */
        public String getTotalComments() {
            return totalComments;
        }

        /**
         * @param totalComments The total_comments
         */
        public void setTotalComments(String totalComments) {
            this.totalComments = totalComments;
        }

        /**
         * @return The viewers
         */
        public ArrayList<Viewers> getViewers() {
            return viewers;
        }

        /**
         * @param viewers The viewers
         */
        public void setViewers(ArrayList<Viewers> viewers) {
            this.viewers = viewers;
        }

    }


    public class Viewers {

        @SerializedName("user_firstname")
        @Expose
        private String userFirstname;
        @SerializedName("user_lastname")
        @Expose
        private String userLastname;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;

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
         * @return The userLastname
         */
        public String getUserLastname() {
            return userLastname;
        }

        /**
         * @param userLastname The user_lastname
         */
        public void setUserLastname(String userLastname) {
            this.userLastname = userLastname;
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

    }
}
