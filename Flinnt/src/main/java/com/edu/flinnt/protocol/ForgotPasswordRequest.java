package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#request
{
username:"1@1.com" 
}
*/

/**
 * Builds json request
 */
public class ForgotPasswordRequest {

	public static final String USERNAME_KEY = "username";
	
	public String username = "";

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
	        returnedJObject.put(USERNAME_KEY, username);  
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

	
}
