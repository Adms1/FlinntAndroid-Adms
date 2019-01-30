package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;


/**
 * Created by flinnt-android-2 on 7/4/17.
 */

public class AddContentRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String SECTION_ID_KEY = "section_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String CAN_PREVIEW_KEY = "can_preview";
    public static final String RESOURCE_ID_KEY = "resource_id";
    public static final String ATTACHMENT_URL_KEY = "attachment_url";
    public static final String ATTACHMENT_TYPE_KEY = "attachment_type";
    static final String RESOURSE_CHANGED_KEY = "resource_changed";


    private String userID = "";
    private String courseID = "";
    private String sectionID = "";
    private String contentID = "";
    private String title = "";
    private String description = "";
    private String canPreview = "";
    private String resourceId = "";
    private String attachmentUrl = "";
    private String attachmentType = "";
    private int resourseChanged = Flinnt.INVALID;


    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    synchronized public String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    synchronized public JSONObject getJSONObject() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COURSE_ID_KEY, courseID);
            returnedJObject.put(SECTION_ID_KEY, sectionID);
            returnedJObject.put(TITLE_KEY, title);

            if (!TextUtils.isEmpty(contentID)) {
                returnedJObject.put(CONTENT_ID_KEY, contentID);
            }
            if (!TextUtils.isEmpty(description)) {
                returnedJObject.put(DESCRIPTION_KEY, description);
            }
            if (!TextUtils.isEmpty(canPreview)) {
                returnedJObject.put(CAN_PREVIEW_KEY, canPreview);
            }
            if (!TextUtils.isEmpty(resourceId)) {
                returnedJObject.put(RESOURCE_ID_KEY, resourceId);
            }
            if (!TextUtils.isEmpty(attachmentUrl)) {
                returnedJObject.put(ATTACHMENT_URL_KEY, attachmentUrl);
            }
            if (!TextUtils.isEmpty(attachmentType)) {
                returnedJObject.put(ATTACHMENT_TYPE_KEY, attachmentType);
            }
            if (resourseChanged != Flinnt.INVALID) {
                returnedJObject.put(RESOURSE_CHANGED_KEY, resourseChanged);
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCanPreview() {
        return canPreview;
    }

    public void setCanPreview(String canPreview) {
        this.canPreview = canPreview;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public int getResourseChanged() {
        return resourseChanged;
    }

    public void setResourseChanged(int resourseChanged) {
        this.resourseChanged = resourseChanged;
    }
}
