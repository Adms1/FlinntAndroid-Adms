package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by flinnt-android-2 on 12/4/17.
 */

public class Attachment implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("attach_type")
    @Expose
    private String attachType;
    @SerializedName("is_url")
    @Expose
    private String isUrl;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("attachment_url")
    @Expose
    private String attachmentUrl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public String getIsUrl() {
        return isUrl;
    }

    public void setIsUrl(String isUrl) {
        this.isUrl = isUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

}
