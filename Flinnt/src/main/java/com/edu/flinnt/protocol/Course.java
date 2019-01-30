package com.edu.flinnt.protocol;

import android.os.Parcel;
import android.os.Parcelable;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
  {
    "course_id": "119",
    "course_name": "Marketing on Pinterest",
    "course_picture": "119_1400502971.jpg",
    "user_picture": "17_1430926314.jpg",
    "course_price": "0",
    "course_owner_name": "Harneet Bhalla",
    "course_is_free": "1",
    "course_status": "2",
    "user_mod_role_id": "1",
    "course_user_picture": "17_119_1400502985.jpg",
    "course_plan_expired": "1",
    "stat_total_posts": "0",
    "stat_total_view": "35",
    "course_community" : "0",
	"total_users" : "1",
    "user_school_name" : "Flinnt Testing Institution",
    "unread_post" : "0"
	"allow_mute": "1",
    "allow_invite_users": "1",
    "allow_change_settings": "0",
    "allowed_roles": "3,2"
  }
*/


public class Course implements Parcelable {

	@SerializedName("course_id")
	public String courseID 							= "";
	@SerializedName("course_name")
	public String courseName 						= "";
	@SerializedName("course_picture")
	public String coursePicture						= "";
	@SerializedName("user_picture")
	public String UserPicture						= "";
	@SerializedName("course_price")
	public String coursePrice 						= "";
	@SerializedName("course_owner_name")
	public String courseOwnerName 					= "";
	@SerializedName("course_is_free")
	public String courseIsFree						= "";
	@SerializedName("course_status")
	public String courseStatus						= "";
	@SerializedName("user_mod_role_id")
	public String userModRoleID						= "";
	@SerializedName("course_user_picture")
	public String courseUserPicture					= "";
	@SerializedName("course_plan_expired")
	public String coursePlanExpired					= "";
	@SerializedName("stat_total_posts")
	public String totalPosts						= "";
	@SerializedName("stat_total_view")
	public String totalView							= "";
	@SerializedName("total_users")
	public String totalUsers						= "";
	@SerializedName("course_community")
	public String courseCommmunity					= "";
	@SerializedName("user_school_name")
	public String userSchoolName					= "";
	@SerializedName("unread_post")
	public String unreadPost						= "0";
	@SerializedName("allow_mute")
	public String allowMute							= "";
	@SerializedName("allow_invite_users")
	public String allowInviteUsers					= "";
	@SerializedName("allow_change_settings")
	public String allowChangeSettings				= "";
	@SerializedName("allow_rate_course")
	public String allowRateCourse					= "";
	@SerializedName("allow_unsubscribe")
	public String allowUnsubscribe					= "";
	@SerializedName("allowed_roles")
	public String allowedRoles						= "";
//	@SerializedName("allow_mute")
//	public Bitmap courseBitmap						= null;
	@SerializedName("public_type")
	public String publicType						= "";
	@SerializedName("event_datetime")
	public String eventDateTime						= "";
	@SerializedName("allow_course_info")
	public String allowCourseInfo					= "";

	public static final String COURSE_ID_KEY 								= "course_id";
	public static final String COURSE_NAME_KEY								= "course_name";
	public static final String COURSE_PICTURE_KEY							= "course_picture";
	public static final String COURSE_PICTURE_URL_KEY							= "course_picture_url";
	public static final String USER_PICTURE_KEY 							= "user_picture";
	public static final String COURSE_PRICE_KEY 							= "course_price";
	public static final String COURSE_OWNER_NAME_KEY 						= "course_owner_name";
	public static final String COURSE_IS_FREE_KEY 							= "course_is_free";
	public static final String COURSE_STATUS_KEY 							= "course_status";
	public static final String USER_MOD_ROLE_ID_KEY 						= "user_mod_role_id";
	public static final String COURSE_USER_PICTURE_KEY 						= "course_user_picture";
	public static final String COURSE_PLAN_EXPIRED_KEY 						= "course_plan_expired";
	public static final String STAT_TOTAL_POSTS_KEY 						= "stat_total_posts";
	public static final String STAT_TOTAL_VIEW_KEY 							= "stat_total_view";
	public static final String TOTAL_USERS_KEY 								= "total_users";
	public static final String COURSE_COMMUNITY_KEY 						= "course_community";
	public static final String USER_SCHOOL_NAME_KEY 						= "user_school_name"; 
	public static final String UNREAD_POST_KEY 								= "unread_post";
	public static final String ALLOW_MUTE_KEY 								= "allow_mute";
	public static final String ALLOW_INVITE_USERS_KEY 						= "allow_invite_users";
	public static final String ALLOW_CHANGE_SETTINGS_KEY 					= "allow_change_settings";
	public static final String ALLOW_RATE_COURSE_KEY 						= "allow_rate_course";
	public static final String ALLOW_RATE_UNSUBSCRIBE_KEY 					= "allow_unsubscribe";
	public static final String ALLOWED_ROLES_KEY 							= "allowed_roles";
	public static final String ALLOWED_COURSE_INFO_KEY 						= "allow_course_info";
	public static final String PUBLIC_TYPE_KEY 								= "public_type";
	public static final String EVENT_DATETIME_KEY 							= "event_datetime";
	public static final String POST_TYPE									= "post_type";
	public static final String COURSE_CAN_COMMENT							= "can_comment";

	/*
	public synchronized String getJSONString() {

		return getJSONObject().toString();
	}

	public synchronized JSONObject getJSONObject() {

		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(COURSE_ID_KEY, courseID);
			returnedJObject.put(COURSE_NAME_KEY, mCourseNameTxt);
			returnedJObject.put(COURSE_PICTURE_KEY, coursePicture);
			returnedJObject.put(USER_PICTURE_KEY, UserPicture);
			returnedJObject.put(COURSE_PRICE_KEY, coursePrice);
			returnedJObject.put(COURSE_OWNER_NAME_KEY, coursePrice);
			returnedJObject.put(COURSE_IS_FREE_KEY, coursePrice);
			returnedJObject.put(COURSE_STATUS_KEY, coursePrice);
			returnedJObject.put(USER_MOD_ROLE_ID_KEY, coursePrice);
			returnedJObject.put(COURSE_USER_PICTURE_KEY, coursePrice);
			returnedJObject.put(COURSE_PLAN_EXPIRED_KEY, coursePrice);
			returnedJObject.put(STAT_TOTAL_POSTS_KEY, coursePrice);
			returnedJObject.put(STAT_TOTAL_VIEW_KEY, coursePrice);
			returnedJObject.put(TOTAL_USERS_KEY, coursePrice);
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}
	*/

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			setCourseID(jObject.getString(COURSE_ID_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setCourseName(jObject.getString(COURSE_NAME_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setCoursePicture(jObject.getString(COURSE_PICTURE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setUserPicture(jObject.getString(USER_PICTURE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setCoursePrice(jObject.getString(COURSE_PRICE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseOwnerName(jObject.getString(COURSE_OWNER_NAME_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseIsFree(jObject.getString(COURSE_IS_FREE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseStatus(jObject.getString(COURSE_STATUS_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setUserModRoleID(jObject.getString(USER_MOD_ROLE_ID_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseUserPicture(jObject.getString(COURSE_USER_PICTURE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCoursePlanExpired(jObject.getString(COURSE_PLAN_EXPIRED_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setTotalPosts(jObject.getString(STAT_TOTAL_POSTS_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setTotalView(jObject.getString(STAT_TOTAL_VIEW_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setTotalUsers(jObject.getString(TOTAL_USERS_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setCourseCommmunity(jObject.getString(COURSE_COMMUNITY_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			setUserSchoolName(jObject.getString(USER_SCHOOL_NAME_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
            if ( jObject.has(UNREAD_POST_KEY) ) {
                setUnreadPost(jObject.getString(UNREAD_POST_KEY));
            }
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
            if (jObject.has(ALLOW_MUTE_KEY)) {
                setAllowMute(jObject.getString(ALLOW_MUTE_KEY));
            }
		}
		catch(Exception e){
			LogWriter.err(e);
            }

            try {
                if (jObject.has(ALLOW_INVITE_USERS_KEY)) {
                    setAllowInviteUsers(jObject.getString(ALLOW_INVITE_USERS_KEY));
                }
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
            if (jObject.has(ALLOW_CHANGE_SETTINGS_KEY)) {
                setAllowChangeSettings(jObject.getString(ALLOW_CHANGE_SETTINGS_KEY));
            }
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setAllowRateCourse(jObject.getString(ALLOW_RATE_COURSE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setAllowUnsubscribe(jObject.getString(ALLOW_RATE_UNSUBSCRIBE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
            if (jObject.has(ALLOWED_ROLES_KEY)) {
                setAllowedRoles(jObject.getString(ALLOWED_ROLES_KEY));
            }
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if (jObject.has(ALLOWED_COURSE_INFO_KEY)) {
				setAllowCourseInfo(jObject.getString(ALLOWED_COURSE_INFO_KEY));
			}
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setPublicType(jObject.getString(PUBLIC_TYPE_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setEventDateTime(jObject.getString(EVENT_DATETIME_KEY));
		}
		catch(Exception e){
			LogWriter.err(e);
		}

	}
	
	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
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
		return UserPicture;
	}

	public void setUserPicture(String userPicture) {
		UserPicture = userPicture;
	}

	public String getCoursePrice() {
		return coursePrice;
	}

	public void setCoursePrice(String coursePrice) {
		this.coursePrice = coursePrice;
	}

	public String getCourseOwnerName() {
		return courseOwnerName;
	}

	public void setCourseOwnerName(String courseOwnerName) {
		this.courseOwnerName = courseOwnerName;
	}

	public String getCourseIsFree() {
		return courseIsFree;
	}

	public void setCourseIsFree(String courseIsFree) {
		this.courseIsFree = courseIsFree;
	}

	public String getCourseStatus() {
		return courseStatus;
	}

	public void setCourseStatus(String courseStatus) {
		this.courseStatus = courseStatus;
	}

	public String getUserModRoleID() {
		return userModRoleID;
	}

	public void setUserModRoleID(String userModRoleID) {
		this.userModRoleID = userModRoleID;
	}

	public String getCourseUserPicture() {
		return courseUserPicture;
	}

	public void setCourseUserPicture(String courseUserPicture) {
		this.courseUserPicture = courseUserPicture;
	}

	public String getCoursePlanExpired() {
		return coursePlanExpired;
	}

	public void setCoursePlanExpired(String coursePlanExpired) {
		this.coursePlanExpired = coursePlanExpired;
	}

	public String getTotalPosts() {
		
		if(getTotalPostsCount() >= 10000){
			totalPosts = String.format("%.1f", (double)getTotalPostsCount()/1000) + "k";
		}else if(getTotalPostsCount() >= 1000){
			totalPosts = String.format("%.2f", (double)getTotalPostsCount()/1000) + "k";
		}
		return totalPosts;
	}

	public void setTotalPosts(String totalPosts) {
		this.totalPosts = totalPosts;
	}

	public int getTotalPostsCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(totalPosts);
		}
		catch(Exception e) {
        }
		return count;
	}

	public String getTotalView() {
		return totalView;
	}

	public void setTotalView(String totalView) {
		this.totalView = totalView;
	}

	public String getTotalUsers() {
		if(getTotalUsersCount() >= 10000){
			totalUsers = String.format("%.1f", (double)getTotalUsersCount()/1000) + "k";
		}else if(getTotalUsersCount() >= 1000){
			totalUsers = String.format("%.2f", (double)getTotalUsersCount()/1000) + "k";
		}
		return totalUsers;
	}

	public void setTotalUsers(String totalUsers) {
		this.totalUsers = totalUsers;
	}
	
	public int getTotalUsersCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(totalUsers);
		}
		catch(Exception e) { }
		return count;
	}
	
	public String getCourseCommmunity() {
		return courseCommmunity;
	}

	public void setCourseCommmunity(String courseCommmunity) {
		this.courseCommmunity = courseCommmunity;
	}

	public String getUserSchoolName() {
		return userSchoolName;
	}

	public void setUserSchoolName(String userSchoolName) {
		this.userSchoolName = userSchoolName;
	}

	public String getUnreadPost() {
		if( getUnreadPostCount() > 99 ) {
			return Flinnt.MAX_UNREAD_COUNT;
		}
		return unreadPost;
	}

	public void setUnreadPost(String unreadPost) {
		this.unreadPost = unreadPost;
	}

	public int getUnreadPostCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(unreadPost);
		}
		catch(Exception e) { }
		return count;
	}

//	public Bitmap getCourseBitmap() {
//		return courseBitmap;
//	}
//
//	public void setCourseBitmap(Bitmap courseBitmap) {
//		this.courseBitmap = courseBitmap;
//	}
	
	public String getAllowMute() {
		return allowMute;
	}

	public void setAllowMute(String allowMute) {
		this.allowMute = allowMute;
	}

	public String getAllowInviteUsers() {
		return allowInviteUsers;
	}

	public void setAllowInviteUsers(String allowInviteUsers) {
		this.allowInviteUsers = allowInviteUsers;
	}

	public String getAllowChangeSettings() {
		return allowChangeSettings;
	}

	public void setAllowChangeSettings(String allowChangeSettings) {
		this.allowChangeSettings = allowChangeSettings;
	}

	public String getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(String allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	public String getAllowCourseInfo() {
		return allowCourseInfo;
	}

	public void setAllowCourseInfo(String allowCourseInfo) {
		this.allowCourseInfo = allowCourseInfo;
	}

	public String getPublicType() {
		return publicType;
	}

	public void setPublicType(String publicType) {
		this.publicType = publicType;
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	public String getAllowRateCourse() {
		return allowRateCourse;
	}

	public void setAllowRateCourse(String allowRateCourse) {
		this.allowRateCourse = allowRateCourse;
	}

	public String getAllowUnsubscribe() {
		return allowUnsubscribe;
	}

	public void setAllowUnsubscribe(String allowUnsubscribe) {
		this.allowUnsubscribe = allowUnsubscribe;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("courseID : " + courseID)
		.append(", mCourseNameTxt : " + courseName)
		.append(", coursePicture : " + coursePicture)
		.append(", UserPicture : " + UserPicture)
		.append(", courseOwnerName : " + courseOwnerName)
		.append(", courseIsFree : " + courseIsFree)
		.append(", courseStatus : " + courseStatus)
		.append(", userModRoleID : " + userModRoleID)
		.append(", courseUserPicture : " + courseUserPicture)
		.append(", coursePlanExpired : " + coursePlanExpired)
		.append(", totalPosts : " + totalPosts)
		.append(", totalView : " + totalView)
		.append(", totalUsers : " + totalUsers)
		.append(", courseCommmunity : " + courseCommmunity)
		.append(", userSchoolName : " + userSchoolName)
		.append(", unreadPost : " + unreadPost)
		.append(", allowMute : " + allowMute)
		.append(", allowInviteUsers : " + allowInviteUsers)
		.append(", allowChangeSettings : " + allowChangeSettings)
		.append(", allowRateCourse : " + allowRateCourse)
		.append(", allowedRoles : " + allowedRoles)
		.append(", publicType : " + publicType)
		.append(", eventDateTime : " + eventDateTime);
		return strBuffer.toString();
	}

	public static final Creator<Course> CREATOR = new Creator<Course>() {
		 public Course createFromParcel(Parcel source) {  
			 
			 Course mCourse = new Course();  
			 mCourse.courseID = source.readString(); 
			 mCourse.courseName = source.readString();
			 mCourse.coursePicture = source.readString();
			 mCourse.UserPicture = source.readString();
			 mCourse.coursePrice = source.readString();
			 mCourse.courseOwnerName = source.readString();
			 mCourse.courseIsFree = source.readString();
			 mCourse.courseStatus = source.readString();
			 mCourse.userModRoleID = source.readString();
			 mCourse.courseUserPicture = source.readString();
			 mCourse.coursePlanExpired = source.readString();
			 mCourse.totalPosts = source.readString();
			 mCourse.totalView = source.readString();
			 mCourse.totalUsers = source.readString();
			 mCourse.courseCommmunity = source.readString();
			 mCourse.userSchoolName = source.readString();
			 mCourse.unreadPost = source.readString();
			 mCourse.allowMute = source.readString();
			 mCourse.allowInviteUsers = source.readString();
			 mCourse.allowChangeSettings = source.readString();
			 mCourse.allowRateCourse = source.readString();
			 mCourse.allowedRoles = source.readString();
			 mCourse.publicType = source.readString();
			 mCourse.eventDateTime = source.readString();
		     return mCourse;  
		 }  
		 
		 public Course[] newArray(int size) {  
		     return new Course[size];  
		 }
	};
		 
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		
		parcel.writeString(courseID);  
		parcel.writeString(courseName);   
		parcel.writeString(coursePicture);
		parcel.writeString(UserPicture);
		parcel.writeString(coursePrice);
		parcel.writeString(courseOwnerName);
		parcel.writeString(courseIsFree);
		parcel.writeString(courseStatus);
		parcel.writeString(userModRoleID);
		parcel.writeString(courseUserPicture);
		parcel.writeString(coursePlanExpired);
		parcel.writeString(totalPosts);
		parcel.writeString(totalView);
		parcel.writeString(totalUsers);
		parcel.writeString(courseCommmunity);
		parcel.writeString(userSchoolName);
		parcel.writeString(unreadPost);
		parcel.writeString(allowMute);
		parcel.writeString(allowInviteUsers);
		parcel.writeString(allowChangeSettings);
		parcel.writeString(allowRateCourse);
		parcel.writeString(allowedRoles);
		parcel.writeString(publicType);
		parcel.writeString(eventDateTime);
	}
}
