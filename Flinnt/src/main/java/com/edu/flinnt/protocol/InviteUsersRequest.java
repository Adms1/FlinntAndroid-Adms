package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/*
 * Request Parameters
user_id: User ID
course_id: Course ID
invite_role: Teacher/Learner (2/3)
send_to: Email Id or Mobile No
*/


/**
 * Builds json request
 */
public class InviteUsersRequest {

	public static final String USER_ID_KEY			 = "user_id";
	public static final String COURSE_ID_KEY		 = "course_id";
	public static final String INVITE_ROLE_KEY 		 = "invite_role";
	public static final String SEND_TO_KEY 			 = "send_to";

	public String userID = "";
	public String courseID = "";
	public int inviteRole;
	public String sendTo = "";

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
			returnedJObject.put(USER_ID_KEY, userID);
			returnedJObject.put(COURSE_ID_KEY, courseID);
			returnedJObject.put(INVITE_ROLE_KEY, inviteRole);
			if(!TextUtils.isEmpty(sendTo)) returnedJObject.put(SEND_TO_KEY, sendTo);
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}


	public String getUserID() {
		return userID;
	}

	public void setUserId(String userId) {
		this.userID = userId;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public int getInviteRole() {
		return inviteRole;
	}
	
	public void setInviteRole(int inviteRole) {
		this.inviteRole = inviteRole;
	}
	
	public String getSendTo() {
		return sendTo;
	}
	
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

}

