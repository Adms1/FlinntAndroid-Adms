package com.edu.flinnt.protocol.contentlist;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class Contents extends RealmObject implements Serializable {
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
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("viewed")
    @Expose
    private String viewed;
    @SerializedName("preview")
    @Expose
    private String preview;
    @SerializedName("type_id")
    @Expose
    private String type_id;
    @SerializedName("quiz_id")
    @Expose
    private String quiz_id;
    @SerializedName("statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("attachments")
    @Expose
    private RealmList<Attachment> attachments = new RealmList<Attachment>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The viewed
     */
    public int getViewed() {
        int viewed = Flinnt.INVALID;
        try {
            viewed = Integer.parseInt(this.viewed);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return viewed;
    }

    /**
     * @param viewed The viewed
     */
    public void setViewed(String viewed) {
        this.viewed = viewed;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    /**
     * @return The statistics
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * @param statistics The statistics
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }


    public RealmList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(RealmList<Attachment> attachments) {
        this.attachments = attachments;
    }


    public String getQuizId() {
        return quiz_id;
    }

    public void setQuizId(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getTypeId() {
        return type_id;
    }

    public void setTypeId(String type_id) {
        this.type_id = type_id;
    }


}