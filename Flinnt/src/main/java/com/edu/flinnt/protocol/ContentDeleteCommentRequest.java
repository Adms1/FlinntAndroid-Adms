package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * #request
 * {
 * user_id: User ID
 * comment_id: Comment ID
 * get_count Get Count
 * }
 */

/**
 * Builds json request
 */
public class ContentDeleteCommentRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COMMENT_ID_KEY = "comment_id";
    public static final String GET_COUNT_KEY = "get_count";

    private String userID = "";
    private String commentID = "";
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
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COMMENT_ID_KEY, commentID);
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

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getGetCount() {
        return getCount;
    }

    public void setGetCount(String getCount) {
        this.getCount = getCount;
    }
}
