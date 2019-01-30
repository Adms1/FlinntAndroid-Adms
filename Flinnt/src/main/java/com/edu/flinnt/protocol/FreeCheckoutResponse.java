package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * class to parse response to object
 */
public class FreeCheckoutResponse extends BaseResponse implements Serializable {

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

        @SerializedName("joined")
        @Expose
        private Integer joined;
        @SerializedName("course_picture_url")
        @Expose
        private String coursePictureUrl;
        @SerializedName("user_picture_url")
        @Expose
        private String userPictureUrl;
        @SerializedName("course_user_picture_url")
        @Expose
        private String courseUserPictureUrl;
        @SerializedName("course")
        @Expose
        private Course course;

        /**
         * @return The joined
         */
        public Integer getJoined() {
            return joined;
        }

        /**
         * @param joined The joined
         */
        public void setJoined(Integer joined) {
            this.joined = joined;
        }

        /**
         * @return The coursePictureUrl
         */
        public String getCoursePictureUrl() {
            return coursePictureUrl;
        }

        /**
         * @param coursePictureUrl The course_picture_url
         */
        public void setCoursePictureUrl(String coursePictureUrl) {
            this.coursePictureUrl = coursePictureUrl;
        }

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
         * @return The courseUserPictureUrl
         */
        public String getCourseUserPictureUrl() {
            return courseUserPictureUrl;
        }

        /**
         * @param courseUserPictureUrl The course_user_picture_url
         */
        public void setCourseUserPictureUrl(String courseUserPictureUrl) {
            this.courseUserPictureUrl = courseUserPictureUrl;
        }

        /**
         * @return The course
         */
        public Course getCourse() {
            return course;
        }

        /**
         * @param course The course
         */
        public void setCourse(Course course) {
            this.course = course;
        }

    }


}