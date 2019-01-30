package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;
/**
#Request
{
    "user_id": "273"
}
*/

/**
 * Builds json request
 */
public class ResendorVerifiedRequest {
	
	public static final String USER_ID_KEY = "user_id";
	
	public String userId = "";

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

}
