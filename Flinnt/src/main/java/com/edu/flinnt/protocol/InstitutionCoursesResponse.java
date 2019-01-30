package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-2 on 19/10/16.
 */
public class InstitutionCoursesResponse extends BaseResponse {
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

        @SerializedName("picture_url")
        @Expose
        private String pictureUrl;
        @SerializedName("user_picture_url")
        @Expose
        private String userPictureUrl;
        @SerializedName("institute_banner_url")
        @Expose
        private String instituteBannerUrl;
        @SerializedName("institute_banner")
        @Expose
        private String instituteBanner;
        @SerializedName("has_more")
        @Expose
        private Integer hasMore;
        @SerializedName("courses")
        @Expose
        private List<Course> courses = new ArrayList<Course>();

        /**
         * @return The pictureUrl
         */
        public String getPictureUrl() {
            return pictureUrl;
        }

        /**
         * @param pictureUrl The picture_url
         */
        public void setPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
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
         *
         * @return
         * The instituteBannerUrl
         */
        public String getInstituteBannerUrl() {
            return instituteBannerUrl;
        }

        /**
         *
         * @param instituteBannerUrl
         * The institute_banner_url
         */
        public void setInstituteBannerUrl(String instituteBannerUrl) {
            this.instituteBannerUrl = instituteBannerUrl;
        }

        /**
         *
         * @return
         * The instituteBanner
         */
        public String getInstituteBanner() {
            return instituteBanner;
        }

        /**
         *
         * @param instituteBanner
         * The institute_banner
         */
        public void setInstituteBanner(String instituteBanner) {
            this.instituteBanner = instituteBanner;
        }


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
         * @return The courses
         */
        public List<Course> getCourses() {
            return courses;
        }

        /**
         * @param courses The courses
         */
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
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("course_price")
        @Expose
        private String coursePrice;
        @SerializedName("course_public")
        @Expose
        private String coursePublic;
        @SerializedName("course_owner_name")
        @Expose
        private String courseOwnerName;
        @SerializedName("course_is_free")
        @Expose
        private String courseIsFree;
        @SerializedName("course_status")
        @Expose
        private String courseStatus;
        @SerializedName("course_user_picture")
        @Expose
        private String courseUserPicture;
        @SerializedName("course_plan_expired")
        @Expose
        private String coursePlanExpired;
        @SerializedName("stat_total_posts")
        @Expose
        private String statTotalPosts;
        @SerializedName("stat_total_view")
        @Expose
        private String statTotalView;
        @SerializedName("event_datetime")
        @Expose
        private String eventDatetime;
        @SerializedName("stat_total_rating")
        @Expose
        private String statTotalRating;
        @SerializedName("course_community")
        @Expose
        private String courseCommunity;
        @SerializedName("total_users")
        @Expose
        private String totalUsers;
        @SerializedName("user_school_name")
        @Expose
        private String userSchoolName;
        @SerializedName("course_public_type_id")
        @Expose
        private String coursePublicTypeId;
        @SerializedName("course_start_time")
        @Expose
        private String courseStartTime;
        @SerializedName("discount_applicable")
        @Expose
        private String discountApplicable;
        @SerializedName("discount_percent")
        @Expose
        private String discountPercent;
        @SerializedName("discount_amount")
        @Expose
        private String discountAmount;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("price_buy")
        @Expose
        private String priceBuy;

        /**
         * @return The courseId
         */
        public String getCourseId() {
            return courseId;
        }

        /**
         * @param courseId The course_id
         */
        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        /**
         * @return The mCourseNameTxt
         */
        public String getCourseName() {
            return courseName;
        }

        /**
         * @param courseName The course_name
         */
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        /**
         * @return The coursePicture
         */
        public String getCoursePicture() {
            return coursePicture;
        }

        /**
         * @param coursePicture The course_picture
         */
        public void setCoursePicture(String coursePicture) {
            this.coursePicture = coursePicture;
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
         * @return The coursePrice
         */
        public String getCoursePrice() {
            return coursePrice;
        }

        /**
         * @param coursePrice The course_price
         */
        public void setCoursePrice(String coursePrice) {
            this.coursePrice = coursePrice;
        }

        public String getCoursePublic() {
            return coursePublic;
        }

        public void setCoursePublic(String coursePublic) {
            this.coursePublic = coursePublic;
        }

        /**
         * @return The courseOwnerName
         */
        public String getCourseOwnerName() {
            return courseOwnerName;
        }

        /**
         * @param courseOwnerName The course_owner_name
         */
        public void setCourseOwnerName(String courseOwnerName) {
            this.courseOwnerName = courseOwnerName;
        }

        /**
         * @return The courseIsFree
         */
        public String getCourseIsFree() {
            return courseIsFree;
        }

        /**
         * @param courseIsFree The course_is_free
         */
        public void setCourseIsFree(String courseIsFree) {
            this.courseIsFree = courseIsFree;
        }

        /**
         * @return The courseStatus
         */
        public String getCourseStatus() {
            return courseStatus;
        }

        /**
         * @param courseStatus The course_status
         */
        public void setCourseStatus(String courseStatus) {
            this.courseStatus = courseStatus;
        }

        /**
         * @return The courseUserPicture
         */
        public String getCourseUserPicture() {
            return courseUserPicture;
        }

        /**
         * @param courseUserPicture The course_user_picture
         */
        public void setCourseUserPicture(String courseUserPicture) {
            this.courseUserPicture = courseUserPicture;
        }

        /**
         * @return The coursePlanExpired
         */
        public String getCoursePlanExpired() {
            return coursePlanExpired;
        }

        /**
         * @param coursePlanExpired The course_plan_expired
         */
        public void setCoursePlanExpired(String coursePlanExpired) {
            this.coursePlanExpired = coursePlanExpired;
        }

        /**
         * @return The statTotalPosts
         */
        public String getStatTotalPosts() {
            return statTotalPosts;
        }

        /**
         * @param statTotalPosts The stat_total_posts
         */
        public void setStatTotalPosts(String statTotalPosts) {
            this.statTotalPosts = statTotalPosts;
        }

        /**
         * @return The statTotalView
         */
        public String getStatTotalView() {
            return statTotalView;
        }

        /**
         * @param statTotalView The stat_total_view
         */
        public void setStatTotalView(String statTotalView) {
            this.statTotalView = statTotalView;
        }
        /**
         *
         * @return
         * The eventDatetime
         */
        public String getEventDatetime() {
            return eventDatetime;
        }

        /**
         *
         * @param eventDatetime
         * The event_datetime
         */
        public void setEventDatetime(String eventDatetime) {
            this.eventDatetime = eventDatetime;
        }

        /**
         * @return The statTotalRating
         */
        public String getStatTotalRating() {
            return statTotalRating;
        }

        /**
         * @param statTotalRating The stat_total_rating
         */
        public void setStatTotalRating(String statTotalRating) {
            this.statTotalRating = statTotalRating;
        }

        /**
         * @return The courseCommunity
         */
        public String getCourseCommunity() {
            return courseCommunity;
        }

        /**
         * @param courseCommunity The course_community
         */
        public void setCourseCommunity(String courseCommunity) {
            this.courseCommunity = courseCommunity;
        }

        /**
         * @return The totalUsers
         */
        public String getTotalUsers() {
            return totalUsers;
        }

        /**
         * @param totalUsers The total_users
         */
        public void setTotalUsers(String totalUsers) {
            this.totalUsers = totalUsers;
        }

        /**
         * @return The userSchoolName
         */
        public String getUserSchoolName() {
            return userSchoolName;
        }

        /**
         * @param userSchoolName The user_school_name
         */
        public void setUserSchoolName(String userSchoolName) {
            this.userSchoolName = userSchoolName;
        }

        /**
         * @return The coursePublicTypeId
         */
        public String getCoursePublicTypeId() {
            return coursePublicTypeId;
        }

        /**
         * @param coursePublicTypeId The course_public_type_id
         */
        public void setCoursePublicTypeId(String coursePublicTypeId) {
            this.coursePublicTypeId = coursePublicTypeId;
        }

        /**
         * @return The courseStartTime
         */
        public String getCourseStartTime() {
            return courseStartTime;
        }

        /**
         * @param courseStartTime The course_start_time
         */
        public void setCourseStartTime(String courseStartTime) {
            this.courseStartTime = courseStartTime;
        }

        /**
         * @return The discountApplicable
         */
        public String getDiscountApplicable() {
            return discountApplicable;
        }

        /**
         * @param discountApplicable The discount_applicable
         */
        public void setDiscountApplicable(String discountApplicable) {
            this.discountApplicable = discountApplicable;
        }

        /**
         * @return The discountPercent
         */
        public String getDiscountPercent() {
            return discountPercent;
        }

        /**
         * @param discountPercent The discount_percent
         */
        public void setDiscountPercent(String discountPercent) {
            this.discountPercent = discountPercent;
        }

        /**
         * @return The discountAmount
         */
        public String getDiscountAmount() {
            return discountAmount;
        }

        /**
         * @param discountAmount The discount_amount
         */
        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
        }

        /**
         * @return The price
         */
        public String getPrice() {
            return price;
        }

        /**
         * @param price The price
         */
        public void setPrice(String price) {
            this.price = price;
        }

        /**
         * @return The priceBuy
         */
        public String getPriceBuy() {
            return priceBuy;
        }

        /**
         * @param priceBuy The price_buy
         */
        public void setPriceBuy(String priceBuy) {
            this.priceBuy = priceBuy;
        }

    }
}
