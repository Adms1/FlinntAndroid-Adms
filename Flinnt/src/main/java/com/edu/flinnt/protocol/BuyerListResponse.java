package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by flinnt-android-2 on 18/4/17.
 */

public class BuyerListResponse extends BaseResponse{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

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
        @SerializedName("users")
        @Expose
        private List<User> users = null;

        public String getUserPictureUrl() {
            return userPictureUrl;
        }

        public void setUserPictureUrl(String userPictureUrl) {
            this.userPictureUrl = userPictureUrl;
        }

        public Integer getHasMore() {
            return hasMore;
        }

        public void setHasMore(Integer hasMore) {
            this.hasMore = hasMore;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

    }

    public class User {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("user_firstname")
        @Expose
        private String userFirstname;
        @SerializedName("user_lastname")
        @Expose
        private String userLastname;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("institute_name")
        @Expose
        private String instituteName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserFirstname() {
            return userFirstname;
        }

        public void setUserFirstname(String userFirstname) {
            this.userFirstname = userFirstname;
        }

        public String getUserLastname() {
            return userLastname;
        }

        public void setUserLastname(String userLastname) {
            this.userLastname = userLastname;
        }

        public String getUserPicture() {
            return userPicture;
        }

        public void setUserPicture(String userPicture) {
            this.userPicture = userPicture;
        }

        public String getInstituteName() {
            return instituteName;
        }

        public void setInstituteName(String instituteName) {
            this.instituteName = instituteName;
        }

    }
}
