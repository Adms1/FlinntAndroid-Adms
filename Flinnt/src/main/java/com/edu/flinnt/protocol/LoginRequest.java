package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#Request
{
"username":"d@d.com",
"password":"123456"
}
*/


/**
 * Builds json request
 */
public class LoginRequest {

	public static final String USERNAME_KEY = "username";
	public static final String PASSWORD_KEY = "password";
	public static final String USER_ID_KEY 	= "user_id";
	
	public String username = "";
	public String password = "";
	public String userID = "";

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
	    	if( !TextUtils.isEmpty(username) ) returnedJObject.put(USERNAME_KEY, username);  
	    	if( !TextUtils.isEmpty(password) ) returnedJObject.put(PASSWORD_KEY, password);
	    	if( !TextUtils.isEmpty(userID) ) returnedJObject.put(USER_ID_KEY, userID);
	    }
	    catch(Exception e) {
	    	LogWriter.err(e);
	    }	    
	    return returnedJObject;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
}
