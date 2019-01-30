package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/** // Alert Details Response
{
   "status":1,
   "data":{
      "user_picture_url":"https:\/\/flinnt1.s3.amazonaws.com\/profile_image\/",
      "alert":{
         "alert_id":"1",
         "alert_text":"Subscribe to your courses by click on Join a Course and enter code provided to you.",
         "alert_dt":"1443165701",
         "alert_user":"Dipal Mehta",
         "user_picture":"7107_1438944437.jpg",
         "can_edit":"1"
      }
   }
}
*/

/* // Alert List Response
{
   "status":1,
   "data":{
      "has_more":0,
      "alerts":[
         {
            "alert_id":"1",
            "alert_text":"Subscribe to your courses by click on Join a Course and enter code provided to you.",
            "alert_by_user":"Dipal Mehta",
            "alert_dt":"1443165701",
            "is_read":"0"
         }
      ]
   }
}

Alert Edit Response
{
  "status": 1,
  "data": {
    "edited": 1
  }
}
*/

/**
 * class to parse response to object
 */
public class AlertListResponse extends BaseResponse {

	public static final String HAS_MORE_KEY 						= "has_more";
	public static final String USER_PICTURE_URL_KEY 				= "user_picture_url";
	public static final String ALERT_KEY 							= "alert";
	public static final String ALERTS_KEY 							= "alerts";

	public static final String EDITED_KEY							= "edited";
	
	private int edited												= 0  ;

	private int hasMore 						= 0;
	private String userPictureUrl 			= "";
	private ArrayList<Alert> alertList	= new ArrayList<Alert>();
	Alert alertDetail;

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
			if(jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(EDITED_KEY)) setHasMore(jsonData.getInt(EDITED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(USER_PICTURE_URL_KEY)) setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		if(jsonData.has(ALERTS_KEY)) {
			try {
				JSONArray alerts = jsonData.getJSONArray(ALERTS_KEY);
				clearAlertList();
				for(int i = 0; i < alerts.length(); i++) {
					JSONObject jObject = alerts.getJSONObject(i);
					Alert alert = new Alert();	
					alert.parseJSONObject(jObject);
					if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "alert :: " + alert.toString() );
					alertList.add(alert);
					//allCourseList.add(course);
					alert = null;
				}
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
		
		if(jsonData.has(ALERT_KEY)) {
			try {
				JSONObject jObject = jsonData.getJSONObject(ALERT_KEY);
				alertDetail = new Alert();	
				alertDetail.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "alertDetail :: " + alertDetail.toString() );
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
	}

	public int getHasMore() {
		return hasMore;
	}

	public void setHasMore(int hasMore) {
		this.hasMore = hasMore;
	}

	public String getUserPictureUrl() {
		return userPictureUrl;
	}

	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}

	public ArrayList<Alert> getAlertList() {
		return alertList;
	}

	public void setAlertList(ArrayList<Alert> alertList) {
		this.alertList = alertList;
	}
	
	public void clearAlertList() {
		this.alertList.clear();
	}

	public Alert getAlertDetail() {
		return alertDetail;
	}

	public void setAlertDetail(Alert alertDetail) {
		this.alertDetail = alertDetail;
	}

	public int getEdited() {
		return edited;
	}


	public void setEdited(int edited) {
		this.edited = edited;
	}


	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("hasMore : " + hasMore)
		.append(", coursePictureUrl : " + userPictureUrl)
		.append(", alertList size : " + alertList.size());
		return strBuffer.toString();
	}
}
