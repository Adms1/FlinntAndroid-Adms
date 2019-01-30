package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * #request
 * {
 * user_id: User ID
 * course_id: Course ID
 * content_id: Content ID
 * offset : 0
 * max : 10
 * }
 */

/**
 * Builds json request
 */
public class ContentViewersRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String OFFSET_KEY = "offset";
    public static final String MAX_KEY = "max";

    private String userID = "";
    private String courseID = "";
    private String contentID = "";
    private int offSet = 0;
    private int maxFetch = 10;

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
            returnedJObject.put(CONTENT_ID_KEY, contentID);
            returnedJObject.put(OFFSET_KEY, offSet);
            returnedJObject.put(MAX_KEY, maxFetch);
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

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getMaxFetch() {
        return maxFetch;
    }

    public void setMaxFetch(int maxFetch) {
        this.maxFetch = maxFetch;
    }
}
