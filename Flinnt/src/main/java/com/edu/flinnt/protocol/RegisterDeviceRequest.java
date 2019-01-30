package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;
/**
#request
{
 "user_id":"455"
 "reg_id":""
}
 */

/**
 * Builds json request
 */
public class RegisterDeviceRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String REG_ID_KEY = "reg_id";
	public static final String APP_VERSION_KEY = "app_version";
	public static final String NOTIFICATION_TYPE = "notification_type";
	public String userId = "";
	public String regId = "";
	public String appVersion = "";
	public String notificationType = Flinnt.NOTIFICATION_TYPE_FCM;
    /**
     * Converts the json object to string
     * @return converted json string
     */
    public synchronized String getJSONString() {

		return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {

		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(USER_ID_KEY, userId);  
			returnedJObject.put(REG_ID_KEY, regId);
			returnedJObject.put(APP_VERSION_KEY, appVersion);
			returnedJObject.put(NOTIFICATION_TYPE, notificationType);
		}
		catch(Exception e) {
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

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public void setNotificationType(String notificationType){
		this.notificationType = notificationType;
	}

	public String getNotificationType(){
		return notificationType;
	}
}
