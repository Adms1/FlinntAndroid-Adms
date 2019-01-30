package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;
import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 3/9/16.
 */

/**
 * Builds json request
 */

public class CourseReviewReadRequest {

    private String userId = "";
    private String courseId = "";

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
            returnedJObject.put(LoginResponse.USER_ID_KEY, userId);
            returnedJObject.put(Course.COURSE_ID_KEY, courseId);
        } catch (Exception e) {
            LogWriter.err(e);
        }

        return returnedJObject;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
