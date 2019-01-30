package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-2 on 3/9/16.
 */
public class CourseReviewReadResponse extends BaseResponse{

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

        @SerializedName("user_picture_url")
        @Expose
        private String userPictureUrl;
        @SerializedName("reviewed")
        @Expose
        private String reviewed;
        @SerializedName("review")
        @Expose
        private Review review;

        /**
         *
         * @return
         * The userPictureUrl
         */
        public String getUserPictureUrl() {
            return userPictureUrl;
        }

        /**
         *
         * @param userPictureUrl
         * The user_picture_url
         */
        public void setUserPictureUrl(String userPictureUrl) {
            this.userPictureUrl = userPictureUrl;
        }


        /**
         *
         * @return
         * The reviewed
         */
        public String getReviewed() {
            return reviewed;
        }

        /**
         *
         * @param reviewed
         * The reviewed
         */
        public void setReviewed(String reviewed) {
            this.reviewed = reviewed;
        }

        /**
         *
         * @return
         * The review
         */
        public Review getReview() {
            return review;
        }

        /**
         *
         * @param review
         * The review
         */
        public void setReview(Review review) {
            this.review = review;
        }

    }

    public class Review {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("user_name")
        @Expose
        private String userName;

        /**
         * @return The id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text The text
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * @return The rating
         */
        public String getRating() {
            return rating;
        }

        /**
         * @param rating The rating
         */
        public void setRating(String rating) {
            this.rating = rating;
        }

        /**
         * @return The timestamp
         */
        public String getTimestamp() {
            return timestamp;
        }

        /**
         * @param timestamp The timestamp
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
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
         * @return The userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @param userName The user_name
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
