package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
/**
 * Response of deeplink url
 * {
 * "status": 1,
 * "data": {
 * "urls": {
 * "verify_account": "https://devtest.flinnt.com/app/verify-account/",
 * "verify_profile_email": "https://devtest.flinnt.com/app/verify-email/",
 * "forgot_password": "https://devtest.flinnt.com/app/forgot_password/"
 * }
 * }
 * }
 */

/**
 * class to parse response to object
 */
public class DeeplinkUrlResponse extends BaseResponse implements Serializable {

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

    public class Data implements Serializable {
        @SerializedName("urls")
        @Expose
        private List<Url> urls = null;

        public List<Url> getUrls() {
            return urls;
        }

        public void setUrls(List<Url> urls) {
            this.urls = urls;
        }

    }

    public class Url implements Serializable {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("URL")
        @Expose
        private String uRL;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("message")
        @Expose
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getURL() {
            return uRL;
        }

        public void setURL(String uRL) {
            this.uRL = uRL;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
