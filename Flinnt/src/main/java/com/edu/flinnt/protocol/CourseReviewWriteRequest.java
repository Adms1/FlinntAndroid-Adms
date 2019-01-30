package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class CourseReviewWriteRequest {

    public static final String RATING_KEY = "rating";
    public static final String REVIEW_KEY = "review";

    private String userId = "";
    private String courseId = "";
    private String rating = "0.0";
    private String review = "";

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
            returnedJObject.put(RATING_KEY, rating);
            returnedJObject.put(REVIEW_KEY, review);
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
