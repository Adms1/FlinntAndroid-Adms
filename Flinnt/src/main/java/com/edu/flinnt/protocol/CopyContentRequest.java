package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 27/4/17.
 */

public class CopyContentRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String SECTION_ID_KEY = "section_id";
    public static final String CONTENT_ID_KEY = "content_id";

    private String userID = "";
    private String courseID = "";
    private String sectionID = "";
    private String contentID = "";


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
            returnedJObject.put(CONTENT_ID_KEY, contentID);
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

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }
}


