package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class AddSectionRequest {

    public static final String COURSE_ID_KEY = "course_id";
    public static final String TITLE_KEY = "title";
    public static final String SECTION_ID_KEY = "section_id";

    private String userID = "";
    private String courseID = "";
    private String title = "";
    private String sectionID = "";

    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(LoginResponse.USER_ID_KEY, userID);
            returnedJObject.put(AddSectionRequest.COURSE_ID_KEY, courseID);
            returnedJObject.put(AddSectionRequest.TITLE_KEY, title);
            if (!TextUtils.isEmpty(sectionID)) {
                returnedJObject.put(SECTION_ID_KEY, sectionID);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }
}
