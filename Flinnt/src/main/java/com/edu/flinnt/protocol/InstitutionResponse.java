package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class InstitutionResponse extends BaseResponse {


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
        @SerializedName("has_more")
        @Expose
        private Integer hasMore;
        @SerializedName("institutions")
        @Expose
        private ArrayList<Institution> institutions = new ArrayList<Institution>();

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
         * @return The institutions
         */
        public List<Institution> getInstitutions() {
            return institutions;
        }

        /**
         * @param institutions The institutions
         */
        public void setInstitutions(ArrayList<Institution> institutions) {
            this.institutions = institutions;
        }

    }

    public class Institution {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("user_school_name")
        @Expose
        private String userSchoolName;

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

    }
}