package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * {
 * "status": 1,
 * "data": {
 * "reviews": [
 * {
 * "id": "721",
 * "text": "very very good",
 * "rating": "4.0",
 * "timestamp": "1405951568",
 * "user_picture": "",
 * "user_name": "shrey patel"
 * },
 * {
 * "id": "1134",
 * "text": "YES I AGREE WITH THEM THAT THE ECONOMICS IS A SOCIAL SCIENCE",
 * "rating": "0.0",
 * "timestamp": "1407071611",
 * "user_picture": "",
 * "user_name": "Vijay Kotecha"
 * }
 * ],
 * "has_more": 0
 * }
 * }
 */

/**
 * class to parse response to object
 */
public class CourseReviewWriteResponse extends BaseResponse {

    public static final String REVIEW_KEY = "review";
    public static final String COURSE_KEY = "course";

    private BrowsableCourse browsableCourse;
    private UserReview userReview;

    /**
     * Converts json string to json object
     *
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {

        if (TextUtils.isEmpty(jsonData)) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            parseJSONObject(jsonObject);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * parse json object to suitable data types
     *
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {
        try {
            if (jsonData.has(REVIEW_KEY)) {
                JSONObject reviewJObject = jsonData.getJSONObject(REVIEW_KEY);
                userReview = new UserReview();
                userReview.parseJSONObject(reviewJObject);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("UserReview :: " + userReview.toString());
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(COURSE_KEY)) {
                JSONObject courseJObject = jsonData.getJSONObject(COURSE_KEY);
                browsableCourse = new BrowsableCourse();
                browsableCourse.parseJSONObject(courseJObject);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("BrowsableCourse :: " + browsableCourse.toString());
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public BrowsableCourse getBrowsableCourse() {
        return browsableCourse;
    }

    public void setBrowsableCourse(BrowsableCourse browsableCourse) {
        this.browsableCourse = browsableCourse;
    }

    public UserReview getUserReview() {
        return userReview;
    }

    public void setUserReview(UserReview userReview) {
        this.userReview = userReview;
    }
}
