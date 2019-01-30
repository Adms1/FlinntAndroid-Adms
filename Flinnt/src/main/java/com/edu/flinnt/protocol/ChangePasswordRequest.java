package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class ChangePasswordRequest {
	
	public static final String USER_ID_KEY = "user_id";
	public static final String CURRENT_PASSWORD_KEY = "current_password";
	public static final String NEW_PASSWORD_KEY = "new_password";
	
	private String userID = "";
	private String currentPassword = "";
	private String newPassword = "";

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
			returnedJObject.put(CURRENT_PASSWORD_KEY, currentPassword); 
			returnedJObject.put(NEW_PASSWORD_KEY, newPassword); 
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
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
