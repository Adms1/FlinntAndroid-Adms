package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 #request
 {
	 user_id: User ID 
	 post_comment_id: Comment ID 
 }
 */
/**
 * Builds json request
 */
public class PostCommentDeleteRequest {

	public static final String USER_ID_KEY 	=  "user_id";
	public static final String COMMENT_ID_KEY 	=  "post_comment_id";

	private String userID 		= "";
	private String commentID 	= "";

    /**
     * Converts the json object to string
     * @return converted json string
     */
    public synchronized String getJSONString() {

		return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(USER_ID_KEY, userID);  
			returnedJObject.put(COMMENT_ID_KEY, commentID);
		}
		catch(Exception e) {
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

	public String getCommentID() {
		return commentID;
	}

	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}

}
