package com.edu.flinnt.protocol.contentlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by flinnt-android-1 on 11/11/16.
 */

public class Attachment extends RealmObject {

    @SerializedName("type")
    @Expose
    private String type;

//    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("attach_type")
    @Expose
    private String attach_type;
    @SerializedName("is_url")
    @Expose
    private String is_url;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttach_type() {
        return attach_type;
    }

    public void setAttach_type(String attach_type) {
        this.attach_type = attach_type;
    }

    public String getIs_url() {
        return is_url;
    }

    public void setIs_url(String is_url) {
        this.is_url = is_url;
    }
}