package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/*
/**
 * Builds json request
 */
public class CourseSettingRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String CAN_COMMENT_KEY = "can_comment";
	public static final String SHOW_COMMENT_KEY = "show_comment_wt_approval";
	public static final String SHOW_TEACHER_NAME_KEY = "show_teacher_name_post";
	public static final String CAN_REPOST_KEY = "can_repost";
	public static final String MAX_POST_DELETE_KEY = "max_post_delete_day_teacher";
	public static final String CAN_ADD_MESSAGE_KEY = "can_add_message";
	public static final String TEACHER_TO_TEACHER_MESSAGE_KEY = "teacher_to_teacher_message";

	public String userId = "";
	public String courseID = "";
	public String canComment = "";
	public String showComment = "";
	public String showTeacherName = "";
	public String canRepost = "";
	public String maxPostDelete = "";
	public String canAddMessage = "";
	public String canTeacherMessage = "";

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
			returnedJObject.put(COURSE_ID_KEY, courseID);
			if(!TextUtils.isEmpty(canComment))	returnedJObject.put(CAN_COMMENT_KEY, canComment);
			if(!TextUtils.isEmpty(showComment)) returnedJObject.put(SHOW_COMMENT_KEY, showComment);
			if(!TextUtils.isEmpty(showTeacherName)) returnedJObject.put(SHOW_TEACHER_NAME_KEY, showTeacherName);
			if(!TextUtils.isEmpty(canRepost)) returnedJObject.put(CAN_REPOST_KEY, canRepost);
			if(!TextUtils.isEmpty(maxPostDelete)) returnedJObject.put(MAX_POST_DELETE_KEY, maxPostDelete);
			if(!TextUtils.isEmpty(canAddMessage)) returnedJObject.put(CAN_ADD_MESSAGE_KEY, canAddMessage);
			if(!TextUtils.isEmpty(canTeacherMessage)) returnedJObject.put(TEACHER_TO_TEACHER_MESSAGE_KEY, canTeacherMessage);
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

	public String getCanComment() {
		return canComment;
	}

	public void setCanComment(String canComment) {
		this.canComment = canComment;
	}

	public String getShowComment() {
		return showComment;
	}

	public void setShowComment(String showComment) {
		this.showComment = showComment;
	}

	public String getShowTeacherName() {
		return showTeacherName;
	}

	public void setShowTeacherName(String showTeacherName) {
		this.showTeacherName = showTeacherName;
	}

	public String getCanRepost() {
		return canRepost;
	}

	public void setCanRepost(String canRepost) {
		this.canRepost = canRepost;
	}

	public String getMaxPostDelete() {
		return maxPostDelete;
	}

	public void setMaxPostDelete(String maxPostDelete) {
		this.maxPostDelete = maxPostDelete;
	}

	public String getCanAddMessage() {
		return canAddMessage;
	}

	public void setCanAddMessage(String canAddMessage) {
		this.canAddMessage = canAddMessage;
	}

	public String getCanTeacherMessage() { return canTeacherMessage; }

	public void setCanTeacherMessage(String canTeacherMessage) { this.canTeacherMessage = canTeacherMessage; }


	public void resetAllValues() {
		userId = "";
		courseID = "";
		canComment = "";
		showComment = "";
		showTeacherName = "";
		canRepost = "";
		maxPostDelete = "";
		canAddMessage = "";
		canTeacherMessage = "";
	}

}

