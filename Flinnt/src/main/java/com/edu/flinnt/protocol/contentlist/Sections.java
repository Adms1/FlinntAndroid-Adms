package com.edu.flinnt.protocol.contentlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class Sections extends RealmObject implements Serializable{
    public String courseID = "";

    @SerializedName("type")
    @Expose
    private String type;

//    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("contents")
    @Expose
    private RealmList<Contents> contents = new RealmList<>();


    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

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

    public RealmList<Contents> getContents() {
        return contents;
    }

    public void setContents(RealmList<Contents> contents) {
        this.contents = contents;
    }
}
