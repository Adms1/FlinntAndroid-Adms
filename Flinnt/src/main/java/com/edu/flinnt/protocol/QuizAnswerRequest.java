package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class QuizAnswerRequest {
	
	public static final String ANSWER_ID_KEY = "answer_id";
	
	private String userID 		= "";
	private String courseID 	= "";
	private String postID 		= "";
	private String answerID 	= "";

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
			returnedJObject.put(PostCommentsRequest.USER_ID_KEY, userID);  
			returnedJObject.put(PostCommentsRequest.COURSE_ID_KEY, courseID);
			returnedJObject.put(PostCommentsRequest.POST_ID_KEY, postID);
			returnedJObject.put(ANSWER_ID_KEY, answerID);
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
	public String getAnswerID() {
		return answerID;
	}
	public void setAnswerID(String answerID) {
		this.answerID = answerID;
	}

}
