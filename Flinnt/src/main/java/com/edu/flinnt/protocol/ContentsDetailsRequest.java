package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * #request
 * {
 * user_id: User ID
 * course_id: Course ID
 * content_id: Content ID
 * notification: 0
 * }
 */

/**
 * Builds json request
 */
public class ContentsDetailsRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENTS_ID_KEY = "content_id";
    public static final String PREVIEW_KEY = "preview";

    private String userID = "";
    private String courseID = "";
    private String contentID = "";
    private String preview = "";

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
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COURSE_ID_KEY, courseID);
            returnedJObject.put(CONTENTS_ID_KEY, contentID);
            if(!preview.equals(""))
            returnedJObject.put(PREVIEW_KEY, preview);
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

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
