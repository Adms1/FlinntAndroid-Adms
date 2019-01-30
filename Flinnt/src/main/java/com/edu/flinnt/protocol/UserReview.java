package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.io.Serializable;

public class UserReview implements Serializable {

    public static final String ID_KEY = "id";
    public static final String TEXT_KEY = "text";
    public static final String RATING_KEY = "rating";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String USER_PICTURE_KEY = "user_picture";
    public static final String USER_NAME = "user_name";

    private String reviewId = "";
    private String reviewText = "";
    private String reviewRating = Flinnt.DISABLED;
    private String reviewTimeStamp = "";
    private String reviewUserName = "";
    private String reviewUserPicture = "";

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
            setReviewId(jsonData.getString(ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setReviewText(jsonData.getString(TEXT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setReviewRating(jsonData.getString(RATING_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setReviewTimeStamp(jsonData.getString(TIMESTAMP_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setReviewUserName(jsonData.getString(USER_NAME));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setReviewUserPicture(jsonData.getString(USER_PICTURE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(String reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewTimeStamp() {
        return reviewTimeStamp;
    }

    public void setReviewTimeStamp(String reviewTimeStamp) {
        this.reviewTimeStamp = reviewTimeStamp;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public String getReviewUserPicture() {
        return reviewUserPicture;
    }

    public void setReviewUserPicture(String reviewUserPicture) {
        this.reviewUserPicture = reviewUserPicture;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("reviewId : " + reviewId)
                .append(", reviewText : " + reviewText)
                .append(", reviewRating : " + reviewRating)
                .append(", reviewTimeStamp : " + reviewTimeStamp)
                .append(", reviewUserName : " + reviewUserName)
                .append(", reviewUserPicture : " + reviewUserPicture);
        return strBuffer.toString();
    }

}
