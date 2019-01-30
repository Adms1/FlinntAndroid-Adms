package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Browse course response object class
 */
public class BrowseCourseItem {
	
	public static final String INVITATION_ID_KEY 						= "invitation_id";
	public static final String USER_NAME_KEY			 				= "user_name";
	public static final String INVITATION_DATE_KEY 						= "invitation_date";
	public static final String COURSE_NAME_KEY 							= "course_name";
	public static final String COURSES_PICTURE_KEY 						= "course_picture";
	
	private String invitationID 				= "";
	private String userName 					= "";
	private String invitationDate 				= "";
	private String courseName 					= "";
	private String coursePicture 				= "";

	
	public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			setInvitationID( jObject.getString(INVITATION_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setUserName( jObject.getString(USER_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setInvitationDate( jObject.getString(INVITATION_DATE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseName( jObject.getString(COURSE_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCoursePicture( jObject.getString(COURSES_PICTURE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
	}

	public String getInvitationID() {
		return invitationID;
	}

	public void setInvitationID(String invitationID) {
		this.invitationID = invitationID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInvitationDate() {
		return invitationDate;
	}

	public void setInvitationDate(String invitationDate) {
		this.invitationDate = invitationDate;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCoursePicture() {
		return coursePicture;
	}

	public void setCoursePicture(String coursePicture) {
		this.coursePicture = coursePicture;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("invitationID : " + invitationID)
		.append(", userName : " + userName)
		.append(", invitationDate : " + invitationDate)
		.append(", mCourseNameTxt : " + courseName)
		.append(", coursePicture : " + coursePicture);
		return strBuffer.toString();
	}

}
