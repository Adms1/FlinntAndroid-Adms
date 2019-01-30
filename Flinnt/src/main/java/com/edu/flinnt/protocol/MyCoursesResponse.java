package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
{
  "status": 1,
  "data": {
    "has_more": 0,
	"course_picture_url": "https://flinnt1.s3.amazonaws.com/courses/",
    "user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/",
    "course_user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/courses/",
    "courses": [
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
        "total_users": "0"
      },
      {
        "course_id": "116",
        "course_name": "Android Programming III",
        "course_picture": "116_1435920369.jpg",
        ......
	  },
      ...
    ]
  }
}
*/


/**
 * class to parse response to object
 */
public class MyCoursesResponse extends BaseResponse {

	public static final String HAS_MORE_KEY 						= "has_more";
	public static final String COURSE_PICTURE_URL_KEY 				= "course_picture_url";
	public static final String USER_PICTURE_URL_KEY 				= "user_picture_url";
	public static final String COURSE_USER_PICTURE_URL_KEY 			= "course_user_picture_url";
	public static final String COURSES_KEY 							= "courses";

	private int hasMore 					= 0;

    private String coursePictureUrl 		= "";
	private String UserPictureUrl 			= "";
	private String courseUserPictureUrl 	= "";
	private ArrayList<Course> courseList	= new ArrayList<Course>();
	private ArrayList<Course> allCourseList	= new ArrayList<Course>();

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
            if (jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
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
			setCourseUserPictureUrl(jsonData.getString(COURSE_USER_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
            if (jsonData.has(COURSES_KEY)) {
                JSONArray courses = jsonData.getJSONArray(COURSES_KEY);
                clearCourseList();
                for(int i = 0; i < courses.length(); i++) {
                    JSONObject jObject = courses.getJSONObject(i);
					Course course = new Gson().fromJson(jObject.toString(),Course.class);
					if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Course :: " + course.toString() );
                    courseList.add(course);
                    allCourseList.add(course);
                    course = null;
                }
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
		return UserPictureUrl;
	}


	public void setUserPictureUrl(String userPictureUrl) {
		UserPictureUrl = userPictureUrl;
	}


	public String getCourseUserPictureUrl() {
		return courseUserPictureUrl;
	}


	public void setCourseUserPictureUrl(String courseUserPictureUrl) {
		this.courseUserPictureUrl = courseUserPictureUrl;
	}


	public ArrayList<Course> getCourseList() {
		return courseList;
	}


	public void setCourseList(ArrayList<Course> courseList) {
		this.courseList = courseList;
	}
	
	public void clearCourseList() {
		this.courseList.clear();
	}

	public ArrayList<Course> getAllCourseList() {
		return allCourseList;
	}


	public void setAllCourseList(ArrayList<Course> allCourseList) {
		this.allCourseList = allCourseList;
	}
	
    public void clearAllCourseList() {
        this.allCourseList.clear();
    }


	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("hasMore : " + hasMore)
                .append(", coursePictureUrl : " + coursePictureUrl)
		.append(", UserPictureUrl : " + UserPictureUrl)
		.append(", courseUserPictureUrl : " + courseUserPictureUrl)
		.append(", courseList size : " + courseList.size());
		return strBuffer.toString();
	}

}
