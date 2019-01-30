package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds json request
 */
public class AddPostRequest {
	
	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String DESCRIPTION_KEY = "description";
	public static final String POST_TEMPLATE_ID_KEY = "post_template_id";
	public static final String POST_CONTANT_TYPE_KEY = "post_content_type";
	public static final String RESOURCE_ID_KEY = "resource_id";
	public static final String ALBUM_RESOURCE_ID_KEY = "album_resources";
	public static final String TO_USERS_KEY = "to_users";
	public static final String POST_URL_KEY = "post_url";
	public static final String PUB_DAY_KEY = "pub_day";
	public static final String PUB_MONTH_KEY  = "pub_month";
	public static final String PUB_YEAR_KEY = "pub_year";
	public static final String PUB_HOUR_KEY = "pub_hour";
	public static final String PUB_MINUTE_KEY = "pub_minute";



	public static final String REPOST_COURSE_ID_KEY = "repost_course";
	public static final String POST_ID_KEY = "post_id";
	public static final String COURSE_NAME_KEY = "course_name";
	public static final String TITLE_KEY = "title";
	public static final String POST_TAGS_KEY = "post_tags";
	public static final String RESOURCE_IDS_KEY = "resource_ids";
	public static final String OPTIONS_KEY = "options";
	public static final String ANSWER_IDS_KEY = "answer_ids";
	public static final String CORRECT_OPTION_KEY = "correct_option";
	public static final String RESOURSE_CHANGED_KEY = "resource_changed";
	public static final String ALERT_TEXT_KEY = "alert_text";
	public static final String ALBUM_IMAGES_KEY = "album_images";
	public static final String ALERT_ID_KEY = "alert_id";
	
	private String userID = "";
	private String courseID = "";
	private String repostCourseID = "";
	private String postID = "";
	private String courseName = "";
	private String title = "";
	private String description = "";
	private String postTemplateID = "";
	private String postTags = "";
	private int postContantType = Flinnt.INVALID; 
	private String resourseID = "";
	private String alertID = "";
	private String postUrl = "";
	private int pubDay = Flinnt.INVALID; 
	private int pubMonth = Flinnt.INVALID;
	private int pubYear = Flinnt.INVALID;
	private int pubHour = Flinnt.INVALID;
	private int pubMinute = Flinnt.INVALID;

	private JSONArray albumResources = null;
	private JSONArray resourseIDs = null;
	private JSONArray albumImages = null;
	private JSONArray options = null;
	private JSONArray answerIDs = null;
	private int correctOption = Flinnt.INVALID; 
	private JSONArray toUsers = null;
	private int resourseChanged = Flinnt.INVALID;
	private String alertText = "";

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
			
			if(!TextUtils.isEmpty(courseID)) {
				returnedJObject.put(COURSE_ID_KEY, courseID);
			}
			if(!TextUtils.isEmpty(repostCourseID)) {
				returnedJObject.put(REPOST_COURSE_ID_KEY, repostCourseID);
			}
			if(!TextUtils.isEmpty(postID)) {
				returnedJObject.put(POST_ID_KEY, postID);
			}
			if(!TextUtils.isEmpty(alertID)) {
				returnedJObject.put(ALERT_ID_KEY, alertID);
			}
			if(!TextUtils.isEmpty(courseName)) {
				returnedJObject.put(COURSE_NAME_KEY, courseName);
			}
			if(!TextUtils.isEmpty(title)) {
				returnedJObject.put(TITLE_KEY, title);
			}
			if(!TextUtils.isEmpty(description)) {
				returnedJObject.put(DESCRIPTION_KEY, description);
			}
			if(!TextUtils.isEmpty(postTemplateID)) {
				returnedJObject.put(POST_TEMPLATE_ID_KEY, postTemplateID);
			}
			if(!TextUtils.isEmpty(postTags)) {
				returnedJObject.put(POST_TAGS_KEY, postTags);
			}
			if(postContantType != Flinnt.INVALID) {
				returnedJObject.put(POST_CONTANT_TYPE_KEY, postContantType);
			}
			if(!TextUtils.isEmpty(resourseID)) {
				returnedJObject.put(RESOURCE_ID_KEY, resourseID);
			}
			if( null != resourseIDs ) {
				returnedJObject.put(RESOURCE_IDS_KEY, resourseIDs);
			}
			if(!TextUtils.isEmpty(postUrl)) {
				returnedJObject.put(POST_URL_KEY, postUrl);
			}
			if(resourseChanged != Flinnt.INVALID) {
				returnedJObject.put(RESOURSE_CHANGED_KEY, resourseChanged);
			}
			if(pubDay != Flinnt.INVALID) {
				returnedJObject.put(PUB_DAY_KEY, pubDay);
			}
			if(pubMonth != Flinnt.INVALID) {
				returnedJObject.put(PUB_MONTH_KEY, pubMonth);
			}
			if(pubYear != Flinnt.INVALID) {
				returnedJObject.put(PUB_YEAR_KEY, pubYear);
			}
			if(pubHour != Flinnt.INVALID) {
				returnedJObject.put(PUB_HOUR_KEY, pubHour);
			}
			if(pubMinute != Flinnt.INVALID) {
				returnedJObject.put(PUB_MINUTE_KEY, pubMinute);
			}
			if( null != options ) {
				returnedJObject.put(OPTIONS_KEY, options);
			}
			if( null != options ) {
				returnedJObject.put(ANSWER_IDS_KEY, answerIDs);
			}
			if(correctOption != Flinnt.INVALID) {
				returnedJObject.put(CORRECT_OPTION_KEY, correctOption);
			}
			if( null != toUsers ) {
				returnedJObject.put(TO_USERS_KEY, toUsers);
			}
			if (null != albumResources) {
				returnedJObject.put(ALBUM_RESOURCE_ID_KEY, albumResources);
			}
			if(!TextUtils.isEmpty(alertText)) {
				returnedJObject.put(ALERT_TEXT_KEY, alertText);
			}
			if( null != albumImages ) {
				returnedJObject.put(ALBUM_IMAGES_KEY, albumImages);
			}
			
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getRepostCourseID() {
		return repostCourseID;
	}

	public void setRepostCourseID(String repostCourseID) {
		this.repostCourseID = repostCourseID;
	}

	public String getPostID() {
		return postID;
	}

	public void setPostID(String postID) {
		this.postID = postID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPostTemplateID() {
		return postTemplateID;
	}

	public void setPostTemplateID(String postTemplateID) {
		this.postTemplateID = postTemplateID;
	}

	public String getPostTags() {
		return postTags;
	}

	public void setPostTags(String postTags) {
		this.postTags = postTags;
	}

	public int getPubDay() {
		return pubDay;
	}

	public void setPubDay(int pubDay) {
		this.pubDay = pubDay;
	}

	public int getPubMonth() {
		return pubMonth;
	}

	public void setPubMonth(int pubMonth) {
		this.pubMonth = pubMonth;
	}

	public int getPubYear() {
		return pubYear;
	}

	public void setPubYear(int pubYear) {
		this.pubYear = pubYear;
	}

	public int getPubHour() {
		return pubHour;
	}

	public void setPubHour(int pubHour) {
		this.pubHour = pubHour;
	}

	public int getPubMinute() {
		return pubMinute;
	}

	public void setPubMinute(int pubMinute) {
		this.pubMinute = pubMinute;
	}

	public JSONArray getOptions() {
		return options;
	}

	public void setOptions(JSONArray options) {
		this.options = options;
	}

	public JSONArray getAnswerIDs() {
		return answerIDs;
	}

	public void setAnswerIDs(JSONArray answerIds) {
		this.answerIDs = answerIds;
	}

	public int getCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(int correctOption) {
		this.correctOption = correctOption;
	}

	public JSONArray getToUsers() {
		return toUsers;
	}

	public void setToUsers(JSONArray toUsers) {
		this.toUsers = toUsers;
	}

	public int getResourseChanged() {
		return resourseChanged;
	}

	public void setResourseChanged(int resourseChanged) {
		this.resourseChanged = resourseChanged;
	}

	public int getPostContantType() {
		return postContantType;
	}

	public void setPostContantType(int postContantType) {
		this.postContantType = postContantType;
	}

	public String getResourseID() {
		return resourseID;
	}

	public void setResourseID(String resourseID) {
		this.resourseID = resourseID;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	public String getAlertText() {
		return alertText;
	}

	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public JSONArray getResourseIDs() {
		return resourseIDs;
	}

	public void setResourseIDs(JSONArray resourseIDs) {
		this.resourseIDs = resourseIDs;
	}

	public JSONArray getAlbumImages() {
		return albumImages;
	}

	public void setAlbumImages(JSONArray albumImages) {
		this.albumImages = albumImages;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public JSONArray getAlbumResources() {
		return albumResources;
	}

	public void setAlbumResources(JSONArray albumResources) {
		this.albumResources = albumResources;
	}
}
