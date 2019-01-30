package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
If account is not verified yet
{
  "status": 1,
  "data": {
	"user_acc_verified": 0
  }
}

If account is already verified
{
  "status": 1,
  "data": {
	"user_acc_verified": 1
  }
}
 */

/**
 * class to parse response to object
 */
public class ResendorVerifiedResponse extends BaseResponse {

	public static final String USER_ACC_VERIFIED_KEY = "user_acc_verified";

	public String accVerified = "";

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
		try {
			setAccVerified(jsonData.getString(USER_ACC_VERIFIED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getAccVerified() {
		return accVerified;
	}

	public void setAccVerified(String accVerified) {
		this.accVerified = accVerified;
	}

}
