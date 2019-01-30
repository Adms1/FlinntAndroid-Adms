package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Invitation response object class
 */
public class InvitationItem {
	
	public static final String INVITATION_ID_KEY 						= "invitation_id";
	public static final String USER_NAME_KEY			 				= "user_name";
	public static final String IS_REQUEST			 				    = "is_request";
	public static final String INVITATION_DATE_KEY 						= "invitation_date";
	public static final String COURSE_NAME_KEY 							= "course_name";
	public static final String COURSES_PICTURE_KEY 						= "course_picture";
	public static final String USER_PICTURE_KEY 						= "user_picture";
	public static final String COURSE_USER_PICTURE_KEY 					= "course_user_picture";
	public static final String USER_MOD_INVITE_NOTE						= "user_mod_invite_notes";
	public static final String REQUEST_USER_ID_KEY						= "req_user_id";

	
	private String invitationID 				= "";
	private String userName 					= "";
	private String isRequest					= "";
	private String invitationDate 				= "";
	private String courseName 					= "";
	private String coursePicture 				= "";
	private String courseUserPicture 			= "";
	private String userPicture 				    = "";
	private String userModInviteNote			= "";
	private String requestUserId				= "";

	
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
			setIsRequest( jObject.getString(IS_REQUEST) );
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

		try {
			setCourseUserPicture( jObject.getString(COURSE_USER_PICTURE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setUserPicture( jObject.getString(USER_PICTURE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setUserModInviteNote( jObject.getString(USER_MOD_INVITE_NOTE) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setRequestUserId( jObject.getString(REQUEST_USER_ID_KEY) );
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

	public String getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}

	public String getIsRequest() {
		return isRequest;
	}

	public void setIsRequest(String isRequest) {
		this.isRequest = isRequest;
	}

	public String getUserModInviteNote() {
		return userModInviteNote;
	}

	public void setUserModInviteNote(String userModInviteNote) {
		this.userModInviteNote = userModInviteNote;
	}

	public String getCourseUserPicture() {
		return courseUserPicture;
	}

	public void setCourseUserPicture(String courseUserPicture) {
		this.courseUserPicture = courseUserPicture;
	}

	public String getRequestUserId() {
		return requestUserId;
	}

	public void setRequestUserId(String requestUserId) {
		this.requestUserId = requestUserId;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("invitationID : " + invitationID)
		.append(", userName : " + userName)
		.append(", isRequest : " + isRequest)
		.append(", invitationDate : " + invitationDate)
		.append(", requestUserId : " + requestUserId)
		.append(", mCourseNameTxt : " + courseName)
		.append(", coursePicture : " + coursePicture)
		.append(", courseUserPicture : " + courseUserPicture)
		.append(", userPicture : " + userPicture)
		.append(", userModInviteNote : " + userModInviteNote);
		return strBuffer.toString();
	}

}
