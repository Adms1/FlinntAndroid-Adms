package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class ContentAddCommentRequest {

    public static final String COMMENT_TEXT_KEY = "comment";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String GET_COUNT_KEY = "get_count";

    private String userID = "";
    private String courseID = "";
    private String contentID = "";
    private String comment = "";
    private String getCount = "1";

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
            returnedJObject.put(COMMENT_TEXT_KEY, comment);
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGetCount() {
        return getCount;
    }

    public void setGetCount(String getCount) {
        this.getCount = getCount;
    }
}
