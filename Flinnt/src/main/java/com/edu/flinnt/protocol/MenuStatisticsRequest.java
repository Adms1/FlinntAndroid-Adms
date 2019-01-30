package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#Request
{
    "user_id": "273",
    "stat_for": ""	
}
	Note : stat_for: Statistics to be fetched (Optional)
 */

/**
 * Builds json request
 */
public class MenuStatisticsRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String STAT_FOR_KEY = "stat_for";

	public String userId = "";
	public String statFor = "";

    /**
     * Converts the json object to string
     * @return converted json string
     */
    synchronized public String getJSONString() {

		return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    synchronized public JSONObject getJSONObject() {
		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(USER_ID_KEY, userId);
			if(!TextUtils.isEmpty(statFor)) {
				returnedJObject.put(STAT_FOR_KEY, statFor);
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatFor() {
		return statFor;
	}

	public void setStatFor(String statFor) {
		this.statFor = statFor;
	}
	
	
}

