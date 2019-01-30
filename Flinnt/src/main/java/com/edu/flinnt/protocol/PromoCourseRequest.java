package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 9/1/17.
 */
public class PromoCourseRequest {

    public static int PROMOTE_COURSE_MOBILE_MY_COURSE = 4;
    public static int PROMOTE_COURSE_MOBILE_BROWSE_COURSE = 5;
    private static final String SOURCE_KEY = "source";

    private String userID = "";
    private int source = PROMOTE_COURSE_MOBILE_MY_COURSE;

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
            returnedJObject.put(SOURCE_KEY, source);
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

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

}
