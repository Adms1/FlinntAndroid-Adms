package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class ProfileVerifyRequest {
	
	public static final String USER_ID_KEY 	=  "user_id";
	public static final String VERIFICATION_CODE_KEY 	=  "verification_code";
	public static final String VERIFICATION_ID_KEY 	=  "verification_id";
	
	private String userID = "";
	private String verificationCode = "";
	private int verificationID = Flinnt.INVALID;

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
			
			if(!TextUtils.isEmpty(verificationCode))
				returnedJObject.put(VERIFICATION_CODE_KEY, verificationCode);
			
			if(verificationID != Flinnt.INVALID)
				returnedJObject.put(VERIFICATION_ID_KEY, verificationID);
			
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
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	public int getVerificationID() {
		return verificationID;
	}
	public void setVerificationID(int verificationID) {
		this.verificationID = verificationID;
	}

}
