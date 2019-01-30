package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class UnsubscribeCourseResponse extends BaseResponse {

	public static final String REMOVED_KEY = "removed";
	public static final String CAN_SUBSCRIBE_KEY        = "can_subscribe";


	private int isRemoved = Flinnt.INVALID;
	private int canSubscribe = Flinnt.FAILURE;

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

    public int getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(int isRemoved) {
        this.isRemoved = isRemoved;
    }

	public int getCanSubscribe() {
		return canSubscribe;
	}

	public void setCanSubscribe(int canSubscribe) {
		this.canSubscribe = canSubscribe;
	}

	/**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    synchronized public void parseJSONObject(JSONObject jsonData) {
		try {
			setIsRemoved(jsonData.getInt(REMOVED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setCanSubscribe(jsonData.getInt(CAN_SUBSCRIBE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}
}
