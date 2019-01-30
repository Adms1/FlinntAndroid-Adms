package com.edu.flinnt.protocol;

import org.json.JSONObject;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;


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
*/

public class Alert {
	
	public static final String ALERT_ID_KEY 						= "alert_id";
	public static final String ALERT_TEXT_KEY						= "alert_text";
	public static final String ALERT_DATE_KEY 						= "alert_dt";
	public static final String ALERT_BY_USER_KEY					= "alert_by_user";
	public static final String ALERT_USER_KEY 						= "alert_user";
	public static final String USER_PICTURE_KEY 					= "user_picture";
	public static final String CAN_EDIT_KEY 						= "can_edit";
	public static final String IS_READ_KEY 							= "is_read";
	public static final String CAN_DELETE_KEY 						= "can_delete";
	
	
	private String alertID 					= "";
	private String alertText 				= "";
	private String alertDate 				= "";
	private String alertUser				= "";
	private String userPicture 				= "";
	private String canEdit 					= "";
	private String alertByUser				= "";
	private String isRead 					= "";
	private String canDelete 					= "";

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			if(jObject.has(ALERT_ID_KEY)) setAlertID( jObject.getString(ALERT_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(ALERT_TEXT_KEY)) setAlertText( jObject.getString(ALERT_TEXT_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(ALERT_DATE_KEY)) setAlertDate( jObject.getString(ALERT_DATE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(ALERT_USER_KEY)) setAlertUser( jObject.getString(ALERT_USER_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(USER_PICTURE_KEY)) setUserPicture( jObject.getString(USER_PICTURE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(CAN_EDIT_KEY)) setCanEdit( jObject.getString(CAN_EDIT_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(IS_READ_KEY)) setIsRead( jObject.getString(IS_READ_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(CAN_DELETE_KEY)) setCanDelete(jObject.getString(CAN_DELETE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(ALERT_BY_USER_KEY)) setAlertByUser( jObject.getString(ALERT_BY_USER_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
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
		long date = Flinnt.INVALID;
		try {
			date = Long.parseLong(alertDate);
		} catch (Exception e) {
		}
		return date;
	}
	
	public void setAlertDate(String alertDate) {
		this.alertDate = alertDate;
	}


	public String getAlertUser() {
		return alertUser;
	}


	public void setAlertUser(String alertUser) {
		this.alertUser = alertUser;
	}
	
	public String getUserPicture() {
		return userPicture;
	}


	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}
	
	
	public String getCanEdit() {
		return canEdit;
	}


	public void setCanEdit(String canEdit) {
		this.canEdit = canEdit;
	}
	

	public String getAlertByUser() {
		return alertByUser;
	}


	public void setAlertByUser(String alertByUser) {
		this.alertByUser = alertByUser;
	}


	public String getIsRead() {
		return isRead;
	}


	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(String canDelete) {
		this.canDelete = canDelete;
	}
	
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("alertID : " + alertID)
		.append(", alertText : " + alertText)
		.append(", alertDate : " + alertDate)
		.append(", canEdit : " + canEdit)
		.append(", alertUser : " + alertUser)
		.append(", userPicture : " + userPicture)
		.append(", canDelete : " + canDelete);
		return strBuffer.toString();
	}


}