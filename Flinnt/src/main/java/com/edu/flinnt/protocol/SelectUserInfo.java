package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * User response object class
 */
public class SelectUserInfo {
	
	public static final String USER_ID_KEY 									= "user_id";
	public static final String USER_NAME_KEY								= "user_name";
	public static final String USER_PICTURE_KEY								= "user_picture";
	public static final String CAN_COMMENT_KEY								= "can_comment";
	public static final String CAN_ADD_MESSAGE_KEY							= "can_add_message";
	
	private String userID;
	private String userName;
	private String userPicture;
	private String canAddMessage;
	private String canComment;
	private int userChecked;
	private int isTeacher;

	// 0 for students
	// 1 for teachers

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			if(jObject.has(USER_ID_KEY)) setUserID( jObject.getString(USER_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(USER_NAME_KEY)) setUserName( jObject.getString(USER_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(USER_PICTURE_KEY)) setUserPicture( jObject.getString(USER_PICTURE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(CAN_ADD_MESSAGE_KEY)) setCanAddMessage( jObject.getString(CAN_ADD_MESSAGE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(CAN_COMMENT_KEY)) setCanComment( jObject.getString(CAN_COMMENT_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPicture() {
		return userPicture;
	}
	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}
	public int getUserChecked() {
		return userChecked;
	}
	public void setUserChecked(int userChecked) {
		this.userChecked = userChecked;
	}
	public int getIsTeacher() {
		return isTeacher;
	}
	public void setIsTeacher(int isTeacher) {
		this.isTeacher = isTeacher;
	} 
	public String getCanAddMessage() {
		return canAddMessage;
	}
	
	public void setCanAddMessage(String canAddMessage) {
		this.canAddMessage = canAddMessage;
	}
	
	public String getCanComment() {
		return canComment;
	}
	
	public void setCanComment(String canComment) {
		this.canComment = canComment;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("userID : " + userID)
		.append(", userName : " + userName)
		.append(", userPicture : " + userPicture)
		.append(", userChecked : " + userChecked)
		.append(", canAddMessage : " + canAddMessage)
		.append(", canComment : " + canComment)
		.append(", isTeacher : " + isTeacher);
		return strBuffer.toString();
	}
}
