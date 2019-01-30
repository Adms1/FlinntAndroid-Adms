package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response
{
   "status":1,
   "data":{
      "user_acc_verified":0
   }
}
*/

/**
 * class to parse response to object
 */
public class ForgotPasswordResponse extends BaseResponse  {

	public String accVerified = "";


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
			setAccVerified(jsonData.getString(LoginResponse.USER_ACC_VERIFIED_KEY));
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
