package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/*
 * 	user_id:
	post_id:

 * */

/**
 * Builds json request
 */
public class LikeUnlikeRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String GET_COUNT_KEY = "get_count";


    private String userID = "";
    private String courseID = "";
    private String contentID = "";
    private String getCount = "1";


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
            returnedJObject.put(CONTENT_ID_KEY, contentID);
            returnedJObject.put(GET_COUNT_KEY, getCount);
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

    public String getGetCount() {
        return getCount;
    }

    public void setGetCount(String getCount) {
        this.getCount = getCount;
    }
}
