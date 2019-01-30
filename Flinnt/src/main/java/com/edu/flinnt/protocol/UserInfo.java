package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * User response object class
 */
public class UserInfo {

	public static final String USER_ID_KEY 									= "user_id";
	public static final String ALLOW_COMMENT_KEY							= "allow_comment";
	public static final String ALLOW_MESSAGE_KEY 							= "allow_message";

	private String userID;
	private String allowComments = "";
	private String allowMessages = "";
	private int userChecked;

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			setUserID( jObject.getString(USER_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(ALLOW_COMMENT_KEY)) {
				setAllowComments( jObject.getString(ALLOW_COMMENT_KEY) );
			}
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(ALLOW_MESSAGE_KEY)){
				setAllowMessages( jObject.getString(ALLOW_MESSAGE_KEY) );
			}
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
	public String getAllowComments() {
		return allowComments;
	}

	public void setAllowComments(String allowComments) {
		this.allowComments = allowComments;
	}

	public String getAllowMessages() {
		return allowMessages;
	}

	public void setAllowMessages(String allowMessages) {
		this.allowMessages = allowMessages;
	}
	public int getUserChecked() {
		return userChecked;
	}
	public void setUserChecked(int userChecked) {
		this.userChecked = userChecked;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("userID : " + userID)
		.append(", allowed to comment : " + allowComments)
		.append(", allowed to message : " + allowMessages)
		.append(", userChecked : " + userChecked);
		return strBuffer.toString();
	}
}
