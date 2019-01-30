package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response
{
   "status":1,
   "data":{
      "result": 2 
   }
}
 */

/**
 * class to parse response to object
 */
public class RegisterDeviceResponse extends BaseResponse {

	public static final String RESULT_KEY = "result";

	public int result = Flinnt.INVALID;

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
			if(jsonData.has(RESULT_KEY)) setResult(jsonData.getInt(RESULT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(", result : " + result);
		return strBuffer.toString();
	}

}
