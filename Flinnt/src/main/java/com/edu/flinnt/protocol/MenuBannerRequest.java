package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#Request
{
    "user_id": "273",
    "course_id": "116"	
}
	Note : course_id: (optional) (FOR POST LIST PAGE ONLY)
 */
/**
 * Builds json request
 */
public class MenuBannerRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String MERGE_ADD_KEY = "merge_add";

	public String userId = "";
	public String courseId = "";
	public String mergeAdd = "";

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
			if(!TextUtils.isEmpty(courseId)) {
				returnedJObject.put(COURSE_ID_KEY, courseId);
			}
			returnedJObject.put(MERGE_ADD_KEY, mergeAdd);
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

	public String getMergeAdd() {
		return mergeAdd;
	}

	public void setMergeAdd(String mergeAdd) {
		this.mergeAdd = mergeAdd;
	}
}

