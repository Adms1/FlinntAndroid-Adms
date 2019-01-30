package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class SendContentNotificationRequest {

    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENT_ID_KEY = "content_id";

    private String userID = "";
    private String courseID = "";
    private String contentID = "";

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
            returnedJObject.put(Course.COURSE_ID_KEY, courseID);
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

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }
}
