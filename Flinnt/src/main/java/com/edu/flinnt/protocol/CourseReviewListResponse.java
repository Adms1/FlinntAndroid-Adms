package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class CourseReviewListResponse extends BaseResponse {

    public static final String HAS_MORE_KEY = "has_more";

    public static final String REVIEWS_KEY = "reviews";

    private int hasMore = 0;
    public ArrayList<UserReview> userReviews;

    /**
     * Converts json string to json object
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
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {
        try {
            if (jsonData.has(REVIEWS_KEY)) {
                JSONArray reviewsJArray = jsonData.getJSONArray(REVIEWS_KEY);
                userReviews = new ArrayList<>();
                for (int i = 0; i < reviewsJArray.length(); i++) {
                    UserReview userReview = new UserReview();
                    JSONObject userReviewJObject = reviewsJArray.getJSONObject(i);
                    userReview.parseJSONObject(userReviewJObject);
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UserReview :: " + userReview.toString());
                    userReviews.add(userReview);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public int getHasMore() {
        return hasMore;
    }

    public void setHasMore(int hasMore) {
        this.hasMore = hasMore;
    }

    public ArrayList<UserReview> getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(ArrayList<UserReview> userReviews) {
        this.userReviews = userReviews;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("hasMore : " + hasMore);
        return strBuffer.toString();
    }
}
