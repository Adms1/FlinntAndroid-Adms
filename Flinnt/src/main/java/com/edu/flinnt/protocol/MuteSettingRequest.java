package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**

*/

/**
 * Builds json request
 */
public class MuteSettingRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String MUTE_COMMENT_KEY = "mute_comment";
	public static final String MUTE_SOUND_KEY = "mute_sound";
	public static final String AUTO_DOWNLOAD_KEY = "auto_download_post_pns";

	public String userId = "";
	public String courseID = "";
	public String muteComment = "";
	public String muteSound = "";
	public String autoDownload = "";

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
			if(!TextUtils.isEmpty(courseID)) returnedJObject.put(COURSE_ID_KEY, courseID);
			if(!TextUtils.isEmpty(muteComment)) returnedJObject.put(MUTE_COMMENT_KEY, muteComment);
			if(!TextUtils.isEmpty(muteSound)) returnedJObject.put(MUTE_SOUND_KEY, muteSound);
			if(!TextUtils.isEmpty(autoDownload)) returnedJObject.put(AUTO_DOWNLOAD_KEY, autoDownload);
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

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getMuteComment() {
		return muteComment;
	}

	public void setMuteComment(String muteComment) {
		this.muteComment = muteComment;
	}

	public String getMuteSound() {
		return muteSound;
	}

	public void setMuteSound(String muteSound) {
		this.muteSound = muteSound;
	}
	
	public String getAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(String autoDownload) {
		this.autoDownload = autoDownload;
	}
	
}

