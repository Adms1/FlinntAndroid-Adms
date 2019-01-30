package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
  "status": 1,
  "data": {
    "alert": {
      "alert_id": "1",
      "alert_text": "Subscribe to your courses by click on Join a Course and enter code provided to you.",
      "alert_dt": "1442308342"
    }
  }
}
*/

/**
 * class to parse response to object
 */
public class HighlightsResponse extends BaseResponse {

	public static final String ALERT_KEY 									= "alert";
	public static final String ALERT_ID_KEY 								= "alert_id";
	public static final String ALERT_TEXT_KEY 								= "alert_text";
	public static final String ALERT_DATE_KEY 								= "alert_dt";

	public String alertID 		= "0";
	public String alertText 	= "";
	public String alertDate 	= "";


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
		JSONObject alertJSONobject = null;
		try {
			alertJSONobject = jsonData.getJSONObject(ALERT_KEY);
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		if( null != alertJSONobject ) {
			try {
				setAlertID(alertJSONobject.getString(ALERT_ID_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
	
			try {
				setAlertText(alertJSONobject.getString(ALERT_TEXT_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
	
			try {
				setAlertDate(alertJSONobject.getString(ALERT_DATE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
	}


	public String getAlertID() {
		return alertID;
	}


	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}


	public String getAlertText() {
		return alertText;
	}


	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}


	public String getAlertDate() {
		return alertDate;
	}

	public long getAlertDateLong() {
		long time = 0;
		try {
			time = Long.parseLong(alertDate);
		} catch (Exception e) {
		}
		return time;
	}

	public void setAlertDate(String alertDate) {
		this.alertDate = alertDate;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("alertID : " + alertID)
		.append(", alertText : " + alertText)
		.append(", alertDate : " + alertDate);
		return strBuffer.toString();
	}
}
