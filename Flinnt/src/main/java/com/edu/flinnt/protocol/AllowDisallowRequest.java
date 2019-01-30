package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Builds json request
 */
public class AllowDisallowRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String USERS_KEY = "users";

	private String userID = "";
	private String courseID = "";
	private ArrayList<String> users = new ArrayList<String>();

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
			if(users.size() > 0) {
				JSONArray usersJsonArray = new JSONArray(users);
				returnedJObject.put(USERS_KEY, usersJsonArray);
			}

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
	public ArrayList<String> getUsers() {
		return users;
	}
	public void setUsers(ArrayList users) {
		this.users = users;
	}
}
