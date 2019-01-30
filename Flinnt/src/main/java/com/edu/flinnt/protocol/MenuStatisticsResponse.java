package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response
if stat_for = S_INVITATIONS
{
  "status": 1,
  "data": {
	"invitations": "5"
  }
}

if stat_for = S_ALERTS
{
  "status": 1,
  "data": {
	"alerts": "10"
  }
}

if stat_for not mentioned at all (The parameter is not present in request)
{
  "status": 1,
  "data": {
	"invitations": "5",
	"alerts": "10"
  }
}
 */
/**
 * class to parse response to object
 */
public class MenuStatisticsResponse extends BaseResponse {
	
	public static final String INVITATION_KEY = "invitations";
	public static final String ALERT_KEY = "alerts";

	public String invitations = "";
	public String alerts = "";

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
			if(jsonData.has(INVITATION_KEY)) setInvitations(jsonData.getString(INVITATION_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(ALERT_KEY)) setAlerts(jsonData.getString(ALERT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getInvitations() {
		return invitations;
	}

	public void setInvitations(String invitations) {
		this.invitations = invitations;
	}

	public String getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("invitations : " + invitations)
		.append(", alerts : " + alerts);
		return strBuffer.toString();
	}
}
