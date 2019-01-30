package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{ 
"status":1, 
"data":
	 {
    "user_id": "275",
	"user_acc_verified": "1",
	"user_acc_auth_mode": "mobile",
	"user_login": "1234566666"
	}
}
 */

/**
 * class to parse response to object
 */
public class VerifyMobileResponse extends BaseResponse {

	public static final String USER_ID_KEY = "user_id";
	public static final String USER_LOGIN_KEY = "user_login";
	public static final String USER_ACC_VERIFIED_KEY = "user_acc_verified";
	public static final String USER_ACC_AUTH_MODE_KEY = "user_acc_auth_mode";

	public String userID = "";
	public String userLogin = "";
	public String accVerified = "";
	public String accAuthMode = "";

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    synchronized public void parseJSONString(String jsonData) {

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
    synchronized public void parseJSONObject(JSONObject jsonData) {
		// TODO Auto-generated method stub
		try {
			setUserID(jsonData.getString(USER_ID_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setUserLogin(jsonData.getString(USER_LOGIN_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setAccVerified(jsonData.getString(USER_ACC_VERIFIED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setAccAuthMode(jsonData.getString(USER_ACC_AUTH_MODE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getAccVerified() {
		return accVerified;
	}

	public void setAccVerified(String accVerified) {
		this.accVerified = accVerified;
	}

	public String getAccAuthMode() {
		return accAuthMode;
	}

	public void setAccAuthMode(String accAuthMode) {
		this.accAuthMode = accAuthMode;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("userID : " + userID)
		.append(", userLogin : " + userLogin)
		.append(", accVerified : " + accVerified)
		.append(", accAuthMode : " + accAuthMode);
		return strBuffer.toString();
	}
}
