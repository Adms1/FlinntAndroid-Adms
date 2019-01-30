package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 17/11/16.
 */

public class SectionShowHideDeleteRequest {
    public static final String USER_ID = "user_id";
    public static final String COURSE_ID = "course_id";
    public static final String SECTION_ID = "section_id";


    private String userID = "";
    private String courseId = "";
    private String sectionId = "";

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

            returnedJObject.put(LoginResponse.USER_ID_KEY, getUserID());
            returnedJObject.put(COURSE_ID, getCourseId());
            returnedJObject.put(SECTION_ID, getSectionId());

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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
