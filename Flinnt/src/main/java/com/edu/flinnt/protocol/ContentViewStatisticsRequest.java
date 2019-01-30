package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;
/**
 * #request
 * {
 * user_id: User ID
 * course_id: Course ID
 * content_id: Content ID
 * max_viewers : 20
 * }
 */

/**
 * Builds json request
 */
public class ContentViewStatisticsRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String MAX_VIEWRS_KEY = "max_viewers";

    private String userID = "";
    private String courseID = "";
    private String contentID = "";
    private int maxViewers = 20;

    /**
     * Converts the json object to string
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COURSE_ID_KEY, courseID);
            returnedJObject.put(CONTENT_ID_KEY, contentID);
            returnedJObject.put(MAX_VIEWRS_KEY, maxViewers);
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

    public int getMaxViewers() {
        return maxViewers;
    }

    public void setMaxViewers(int maxViewers) {
        this.maxViewers = maxViewers;
    }
}
