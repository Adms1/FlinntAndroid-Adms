package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds json request
 */
public class SelectUsersRequest {
	
	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String ROLE_KEY = "role";
	public static final String OFFSET_KEY = "offset";
	public static final String MAX_KEY = "max";
	public static final String SELECTED_KEY = "selected";
	public static final String LIST_SOURCE_KEY = "list_source";
	
	private String userID = "";
	private String courseID = "";
	private int role 					= Flinnt.INVALID;
	private int offset 					= 0;
	private int max 						= 10;
	private String listSource  = Flinnt.COURSE_USER_LIST_MYCOURSE;
	
	private JSONArray selected = new JSONArray();

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
			returnedJObject.put(ROLE_KEY, role);
			returnedJObject.put(OFFSET_KEY, offset);
	        returnedJObject.put(MAX_KEY, max);
	        returnedJObject.put(LIST_SOURCE_KEY, listSource);
	        if(selected.length() > 0) {
				returnedJObject.put(SELECTED_KEY, selected);
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
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
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
	public JSONArray getSelected() {
		return selected;
	}
	public void setSelected(JSONArray selected) {
		this.selected = selected;
	}

	public String getListSource() {
		return listSource;
	}

	public void setListSource(String listSource) {
		this.listSource = listSource;
	}
}
