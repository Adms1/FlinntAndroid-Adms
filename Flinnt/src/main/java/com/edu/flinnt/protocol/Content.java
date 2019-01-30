package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-2 on 12/4/17.
 */

public class Content implements Serializable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("preview")
    @Expose
    private String preview;
    @SerializedName("is_visible")
    @Expose
    private String isVisible;
    @SerializedName("sr_no")
    @Expose
    private String srNo;
    @SerializedName("notification_sent")
    @Expose
    private String notificationSent;
    @SerializedName("type_id")
    @Expose
    private String type_id;
    @SerializedName("attachments")
    @Expose
    private List<Attachment> attachments = new ArrayList<Attachment>();

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

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
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The preview
     */
    public String getPreview() {
        return preview;
    }

    /**
     * @param preview The preview
     */
    public void setPreview(String preview) {
        this.preview = preview;
    }

    /**
     * @return The isVisible
     */
    public String getIsVisible() {
        return isVisible;
    }

    /**
     * @param isVisible The is_visible
     */
    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return The srNo
     */
    public String getSrNo() {
        return srNo;
    }

    /**
     * @param srNo The sr_no
     */
    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(String notificationSent) {
        this.notificationSent = notificationSent;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    /**
     * @return The attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments The attachments
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
