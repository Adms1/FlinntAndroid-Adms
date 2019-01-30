package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
  "data": {
    "sent": 1
  }
}
*/


/**
 * class to parse response to object
 */
public class InviteUsersResponse extends BaseResponse {

	public static final String SENT_KEY = "sent";

	public int sent;

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
			setSent(jsonData.getInt(SENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}


	public int getSent() {
		return sent;
	}


	public void setSent(int sent) {
		this.sent = sent;
	}

	@Override
	public String toString() {
		String strBuffer;
		strBuffer = "sent : " + sent;
		return strBuffer;
	}
}
