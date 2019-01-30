
package com.edu.flinnt.api.models.erpModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Erp {

    @SerializedName("subscribed")
    @Expose
    private Integer subscribed;
    @SerializedName("published")
    @Expose
    private Integer published;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("expired")
    @Expose
    private Integer expired;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Erp() {
    }

    /**
     * 
     * @param message
     * @param expired
     * @param active
     * @param published
     * @param subscribed
     * @param url
     */
    public Erp(Integer subscribed, Integer published, Integer active, Integer expired, String message, String url) {
        super();
        this.subscribed = subscribed;
        this.published = published;
        this.active = active;
        this.expired = expired;
        this.message = message;
        this.url = url;
    }

    public Integer getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Integer subscribed) {
        this.subscribed = subscribed;
    }

    public Integer getPublished() {
        return published;
    }

    public void setPublished(Integer published) {
        this.published = published;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
