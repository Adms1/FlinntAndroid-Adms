package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
#response:
{
  "status": 1,
  "data": {
    	"joined": 1
  }
}
*/

/**
 * class to parse response to object
 */
public class JoinCourseResponse extends BaseResponse {

	public static final String COURSE_KEY = "course";
	public static final String JOINED_KEY = "joined";

    public static final String COURSE_PICTURE_URL_KEY 				= "course_picture_url";
    public static final String USER_PICTURE_URL_KEY 				= "user_picture_url";
    public static final String COURSE_USER_PICTURE_URL_KEY 			= "course_user_picture_url";

	public String joined 		= "";
    private String coursePictureUrl 		= "";
    private String UserPictureUrl 			= "";
    private String courseUserPictureUrl 	= "";

	Course joinedCourse = new Course();

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
            if (jsonData.has(JOINED_KEY )) setJoined(jsonData.getString(JOINED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

        try {
            if (jsonData.has(COURSE_PICTURE_URL_KEY) ) setCoursePictureUrl(jsonData.getString(COURSE_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(USER_PICTURE_URL_KEY) ) setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(COURSE_USER_PICTURE_URL_KEY) ) setCourseUserPictureUrl(jsonData.getString(COURSE_USER_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

		try {
			if(jsonData.has(COURSE_KEY)) {
				JSONObject jObject = jsonData.getJSONObject(COURSE_KEY);
                Course course = new Gson().fromJson(jObject.toString(),Course.class);

                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Joined Course :: " + course.toString() );
				setJoinedCourse(course);
				course = null;
			}		
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}


	public String getJoined() {
		return joined;
	}


	public void setJoined(String joined) {
		this.joined = joined;
	}
	
	public Course getJoinedCourse() {
		return joinedCourse;
	}


	public void setJoinedCourse(Course joinedCourse) {
		this.joinedCourse = joinedCourse;
	}


    public String getCourseUserPictureUrl() {
        return courseUserPictureUrl;
    }

    public void setCourseUserPictureUrl(String courseUserPictureUrl) {
        this.courseUserPictureUrl = courseUserPictureUrl;
    }

    public String getUserPictureUrl() {
        return UserPictureUrl;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        UserPictureUrl = userPictureUrl;
    }

    public String getCoursePictureUrl() {
        return coursePictureUrl;
    }

    public void setCoursePictureUrl(String coursePictureUrl) {
        this.coursePictureUrl = coursePictureUrl;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("joined : " + joined)
        .append(", courseId : " + joinedCourse.getCourseID())
                .append(", mCourseNameTxt : " + joinedCourse.getCourseName());
        return strBuffer.toString();
    }

}
