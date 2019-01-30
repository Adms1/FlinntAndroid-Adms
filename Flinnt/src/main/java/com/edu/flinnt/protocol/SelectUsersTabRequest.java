package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class SelectUsersTabRequest {
	
	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";

	public String userID = "";
	public String courseID = "";

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
			returnedJObject.put(USER_ID_KEY, userID);
			returnedJObject.put(COURSE_ID_KEY, courseID);
			
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
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
}
