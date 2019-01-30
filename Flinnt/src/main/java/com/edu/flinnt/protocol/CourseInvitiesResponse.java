package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
{
  "status": 1,
  "data": {
    "course_picture_url": "https://flinnt1.s3.amazonaws.com/courses/",
    "user_picture_url": "https:\/\/flinnt1.s3.amazonaws.com\/profile_image\/",
	"has_more": 0,
    "invitations": [
      {
        "invitation_id": "191",
        "user_name": "Concept EduServ Pvt. Ltd.",
 		"is_request": "1",
        "invitation_date": "1442066985",
        "course_name": "Learning Responsive Web Design",
        "course_picture_url": "115_1431530422.jpg"
        "user_mod_invite_notes": "I want to join this course, please grant me access."

 },
      ...
    ]
  }
}
*/


/**
 * class to parse response to object
 */
public class CourseInvitiesResponse extends BaseResponse {

	public static final String HAS_MORE_KEY 						= "has_more";
	public static final String COURSE_PICTURE_URL_KEY 				= "course_picture_url";
	public static final String COURSE_PROFILE_PICTURE_URL_KEY 		= "course_profile_picture_url";
	public static final String USER_PICTURE_URL_KEY 				= "user_picture_url";
	public static final String INVITAIONS_KEY 						= "invitations";

	private int hasMore 						= 0;
	private String coursePictureUrl 			= "";
	private String courseProfilePictureUrl 		= "";
	private String userPictureUrl 			    = "";
	private ArrayList<InvitationItem> invitationList	= new ArrayList<InvitationItem>();


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
			setHasMore(jsonData.getInt(HAS_MORE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setCoursePictureUrl(jsonData.getString(COURSE_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setCourseProfilePictureUrl(jsonData.getString(COURSE_PROFILE_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			JSONArray invitations = jsonData.getJSONArray(INVITAIONS_KEY);
			clearInvitationList();
			for(int i = 0; i < invitations.length(); i++) {
				JSONObject jObject = invitations.getJSONObject(i);
				InvitationItem invitation = new InvitationItem();	
				invitation.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Invitation :: " + invitation.toString() );
				invitationList.add(invitation);
				//allCourseList.add(course);
				invitation = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public int getHasMore() {
		return hasMore;
	}

	public void setHasMore(int hasMore) {
		this.hasMore = hasMore;
	}

	public String getCoursePictureUrl() {
		return coursePictureUrl;
	}

	public void setCoursePictureUrl(String coursePictureUrl) {
		this.coursePictureUrl = coursePictureUrl;
	}

	public String getUserPictureUrl() {
		return userPictureUrl;
	}

	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}

	public String getCourseProfilePictureUrl() {
		return courseProfilePictureUrl;
	}

	public void setCourseProfilePictureUrl(String courseProfilePictureUrl) {
		this.courseProfilePictureUrl = courseProfilePictureUrl;
	}

	public ArrayList<InvitationItem> getInvitationList() {
		return invitationList;
	}

	public void setInvitationList(ArrayList<InvitationItem> invitationList) {
		this.invitationList = invitationList;
	}
	
	public void clearInvitationList() {
		this.invitationList.clear();
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("hasMore : " + hasMore)
		.append(", coursePictureUrl : " + coursePictureUrl)
		.append(", courseProfilePictureUrl : " + courseProfilePictureUrl)
		.append(", userPictureUrl : " + userPictureUrl)
		.append(", Invitaion size : " + invitationList.size());
		return strBuffer.toString();
	}

}
