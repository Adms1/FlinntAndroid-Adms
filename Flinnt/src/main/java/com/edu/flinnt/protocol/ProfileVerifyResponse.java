package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class ProfileVerifyResponse extends BaseResponse {

	public static final String VERIFIED_KEY 	= "verified";
	public static final String EXPIRED_KEY 	= "expired";
	public static final String SENT_KEY 		= "sent";
	
	private int isVerified = Flinnt.INVALID;
	private int isExpired = Flinnt.INVALID;
	private int isSent = Flinnt.INVALID;

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {

		try {
			if(jsonData.has(VERIFIED_KEY)) setIsVerified( jsonData.getInt(VERIFIED_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			if(jsonData.has(EXPIRED_KEY)) setIsExpired( jsonData.getInt(EXPIRED_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			if(jsonData.has(SENT_KEY)) setIsSent( jsonData.getInt(SENT_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
	}
	
	public int getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(int isVerified) {
		this.isVerified = isVerified;
	}
	public int getIsExpired() {
		return isExpired;
	}
	public void setIsExpired(int isExpired) {
		this.isExpired = isExpired;
	}
	public int getIsSent() {
		return isSent;
	}
	public void setIsSent(int isSent) {
		this.isSent = isSent;
	}
}
