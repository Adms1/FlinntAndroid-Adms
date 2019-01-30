package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#Request
{
    "user_id": "273",
    "course_id": "116"	
}
 */
/**
 * Builds json request
 */
public class PostListMenuRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";

	public String userId = "";
	public String courseId = "";

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
			returnedJObject.put(COURSE_ID_KEY, courseId);
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

