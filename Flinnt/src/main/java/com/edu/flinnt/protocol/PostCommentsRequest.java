package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 #request
 {
	 user_id: User ID 
	 course_id: Course ID 
	 post_id: Post ID 
	 offset : 0
	 max : 10
 }
 */
/**
 * Builds json request
 */
public class PostCommentsRequest {

	public static final String USER_ID_KEY 	=  "user_id";
	public static final String COURSE_ID_KEY 	=  "course_id";
	public static final String POST_ID_KEY 	=  "post_id";
	public static final String OFFSET_KEY		=  "offset";
	public static final String MAX_KEY		=  "max";

	private String userID 		= "";
	private String courseID 	= "";
	private String postID 		= "";
	private int offSet = 0;
	private int maxFetch = 25;

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
			returnedJObject.put(COURSE_ID_KEY, courseID);
			returnedJObject.put(POST_ID_KEY, postID);
			returnedJObject.put(OFFSET_KEY, offSet);
			returnedJObject.put(MAX_KEY, maxFetch);
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

	public int getOffSet() {
		return offSet;
	}

	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	public int getMaxFetch() {
		return maxFetch;
	}

	public void setMaxFetch(int maxFetch) {
		this.maxFetch = maxFetch;
	}

}
