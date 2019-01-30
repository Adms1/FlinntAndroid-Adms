package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/*
 * 	user_id:
	post_id:

 * */

/**
 * Builds json request
 */
public class LikeBookmarkRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String POST_ID_KEY = "post_id";

	private String userID = "";
	private String postID = "";

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
			returnedJObject.put(POST_ID_KEY, postID);
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


	public String getPostID() {
		return postID;
	}


	public void setPostID(String postID) {
		this.postID = postID;
	}
}
