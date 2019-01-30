package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * #Request
 * {
 * "user_id": "273",
 * "course_id": "116"
 * }
 * Note : course_id: (optional) (FOR POST LIST PAGE ONLY)
 */

/**
 * Builds json request
 */
public class PostAllowOptionsRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String POST_TYPE_KEY = "post_type";
    public static final String ACTION_KEY = "action";

    public String userId = "";
    public String courseId = "";
    public int postType = Flinnt.FALSE;
    public String action = "";

    /**
     * Converts the json object to string
     * @return converted json string
     */
    synchronized public String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     * @return created json object
     */
    synchronized public JSONObject getJSONObject() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userId);
            if (!TextUtils.isEmpty(courseId)) {
                returnedJObject.put(COURSE_ID_KEY, courseId);
                returnedJObject.put(POST_TYPE_KEY, postType);
                returnedJObject.put(ACTION_KEY, action);
            }
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

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

