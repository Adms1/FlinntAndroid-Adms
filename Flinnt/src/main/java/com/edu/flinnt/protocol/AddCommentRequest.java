package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class AddCommentRequest {

	public static final String COMMENT_TEXT_KEY = "comment";
	public static final String POST_ID_KEY = "post_id";

	private String userID  = "";
	private String courseID  = "";
	private String postID  = "";
	private String comment  = "";

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
			returnedJObject.put(LoginResponse.USER_ID_KEY, userID);  
			returnedJObject.put(Course.COURSE_ID_KEY, courseID); 
			returnedJObject.put(POST_ID_KEY, postID); 
			returnedJObject.put(COMMENT_TEXT_KEY, comment); 
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
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public String getPostID() {
		return postID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

}
