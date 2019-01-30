package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#Request
{
    "user_id": "273",
    "verification_code": "xxxxxx"
}
 */

/**
 * Builds json request
 */
public class VerifyMobileRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String VERIFICATION_CODE_KEY = "verification_code";

	public String userId = "";
	public String verificationCode = "";

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
			returnedJObject.put(VERIFICATION_CODE_KEY, verificationCode);
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

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

}
