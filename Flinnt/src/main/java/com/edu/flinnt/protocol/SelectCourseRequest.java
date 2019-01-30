package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Builds json request
 */
public class SelectCourseRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String ACTION_KEY = "action";
	public static final String POST_TYPE_KEY = "post_type";
	public static final String EXCLUDE_KEY = "exclude";
	public static final String OFFSET_KEY = "offset";
	public static final String MAX_KEY = "max";

	private String userID = "";
	private String action = "";
	//private int postType = Flinnt.INVALID;
	private String excludeIDs = "";
	private int offset 		= 0;
	private int max 		= 50;
	private ArrayList<Integer> postType 	= new ArrayList<Integer>();
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
			returnedJObject.put(ACTION_KEY, action);
//			//returnedJObject.put(POST_TYPE_KEY, postType);
			returnedJObject.put(POST_TYPE_KEY, getPostTypeJSONArray());
			if(!TextUtils.isEmpty(excludeIDs)) {
				returnedJObject.put(EXCLUDE_KEY, excludeIDs);
			}
			returnedJObject.put(OFFSET_KEY, offset);
	        returnedJObject.put(MAX_KEY, max);

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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	/*
	public int getPostType() {
		return postType;
	}
	public void setPostType(int postType) {
		this.postType = postType;
	}*/

	public ArrayList<Integer> getPostType() {
		return postType;
	}

	public void setPostType(ArrayList<Integer> postType) {
		this.postType = postType;
	}

	public String getExcludeIDs() {
		return excludeIDs;
	}
	public void setExcludeIDs(String excludeIDs) {
		this.excludeIDs = excludeIDs;
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

	private JSONArray getPostTypeJSONArray() {
		JSONArray jArray = new JSONArray();
		if(null != postType){
			for (int i = 0; i < postType.size(); i++) {
				jArray.put( postType.get(i) );
			}
		}
		return jArray;
	}
}
