package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by flinnt-android-3 on 9/1/17.
 */
public class PromoCourseResponse extends BaseResponse {
    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("course_picture_url")
        @Expose
        private String coursePictureUrl;
        @SerializedName("courses")
        @Expose
        private List<Course> courses = null;

        public String getCoursePictureUrl() {
            return coursePictureUrl;
        }

        public void setCoursePictureUrl(String coursePictureUrl) {
            this.coursePictureUrl = coursePictureUrl;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

    }


    public class Course {

        @SerializedName("course_id")
        @Expose
        private String courseId;
        @SerializedName("course_name")
        @Expose
        private String courseName;
        @SerializedName("course_picture")
        @Expose
        private String coursePicture;
        @SerializedName("user_school_name")
        @Expose
        private String userSchoolName;
        @SerializedName("location_match")
        @Expose
        private String locationMatch;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCoursePicture() {
            return coursePicture;
        }

        public void setCoursePicture(String coursePicture) {
            this.coursePicture = coursePicture;
        }

        public String getUserSchoolName() {
            return userSchoolName;
        }

        public void setUserSchoolName(String userSchoolName) {
            this.userSchoolName = userSchoolName;
        }

        public String getLocationMatch() {
            return locationMatch;
        }

        public void setLocationMatch(String locationMatch) {
            this.locationMatch = locationMatch;
        }

    }

}
