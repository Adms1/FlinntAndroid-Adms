package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class InvitationRequest {

	public static final String INVITATION_ID_KEY 				= "invitation_id"; 
	
	private String userID 				= "";
	private String invitationID 		= "";


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
	        returnedJObject.put(INVITATION_ID_KEY, invitationID);
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

	public String getInvitationID() {
		return invitationID;
	}

	public void setInvitationID(String invitationID) {
		this.invitationID = invitationID;
	}

}
