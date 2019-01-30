package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class ProfileUpdateResponse extends BaseResponse {

	public static final String UPDATED_KEY 	= "updated";
	public static final String USER_PICTURE_URL= "user_picture_url";
	public static final String USER_PICTURE 	= "user_picture";
	public static final String EMAIL_KEY 	= "email";
	public static final String MOBILE_NO_KEY 	= "mobile_no";
	public static final String VERIFY_EMAIL_KEY 	= "verify_email";
	public static final String VERIFY_MOBILE_KEY 	= "verify_mobile";
	public static final String VERIFICATION_ID_KEY 	= "verification_id";
	
	private int updated = Flinnt.INVALID;
	private String userPictureUrl = "";
	private String userPicture = "";
	private int verifyEmail = Flinnt.INVALID;
	private String emailID = "";
	private int emailVerificationID = Flinnt.INVALID;
	private int verifyMobile = Flinnt.INVALID;
	private String mobileNo = "";
	private int mobileVerificationID = Flinnt.INVALID;

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {

		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			parseJSONObject(jsonObject); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {

		try {
			setUpdated( jsonData.getInt(UPDATED_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setUserPictureUrl( jsonData.getString(USER_PICTURE_URL) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			setUserPicture( jsonData.getString(USER_PICTURE) );
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			JSONObject emailObject = jsonData.getJSONObject(EMAIL_KEY);
			
			try {
				setVerifyEmail( emailObject.getInt(VERIFY_EMAIL_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
			
			try {
				setEmailID( emailObject.getString(EMAIL_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
			
			try {
				setEmailVerificationID( emailObject.getInt(VERIFICATION_ID_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
				
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			JSONObject mobileObject = jsonData.getJSONObject(MOBILE_NO_KEY);
			
			try {
				setVerifyMobile( mobileObject.getInt(VERIFY_MOBILE_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
			
			try {
				setMobileNo( mobileObject.getString(MOBILE_NO_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
			
			try {
				setMobileVerificationID( mobileObject.getInt(VERIFICATION_ID_KEY) );
			}catch (Exception e) {	LogWriter.err(e);}
				
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}

	public String getUserPictureUrl() {
		return userPictureUrl;
	}

	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}

	public String getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}

	public int getVerifyEmail() {
		return verifyEmail;
	}

	public void setVerifyEmail(int verifyEmail) {
		this.verifyEmail = verifyEmail;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public int getEmailVerificationID() {
		return emailVerificationID;
	}

	public void setEmailVerificationID(int emailVerificationID) {
		this.emailVerificationID = emailVerificationID;
	}

	public int getVerifyMobile() {
		return verifyMobile;
	}

	public void setVerifyMobile(int verifyMobile) {
		this.verifyMobile = verifyMobile;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getMobileVerificationID() {
		return mobileVerificationID;
	}

	public void setMobileVerificationID(int mobileVerificationID) {
		this.mobileVerificationID = mobileVerificationID;
	}
}
