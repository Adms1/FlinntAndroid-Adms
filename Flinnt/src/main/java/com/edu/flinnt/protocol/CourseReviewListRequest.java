package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class CourseReviewListRequest {

    public static final String OFFSET_KEY = "offset"; // Start position from where to fetch list
    public static final String MAX_KEY = "max";    // Maximum no. of entries to fetch

    private String userId = "";
    private String courseId = "";
    private int offset = 0;
    private int max = 10;

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
            returnedJObject.put(OFFSET_KEY, offset);
            returnedJObject.put(MAX_KEY, max);
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
