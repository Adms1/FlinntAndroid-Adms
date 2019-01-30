package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;



/**
Response :: 
 
{
  "status" : 1,
  "data" : {
    "subscribed" : 1
  }
}

## OR ##

{
  "status" : 1,
  "data" : {
    "rejected" : 1
  }
}
*/

/**
 * class to parse response to object
 */
public class InvitationResponse extends BaseResponse {

	public static final String SUBSCRIBED_KEY = "subscribed";
	public static final String REJECTED_KEY = "rejected";
	public static final String COURSE_KEY = "course";

	private int rejected = Flinnt.INVALID;
	private int subscribed = Flinnt.INVALID;
	Course acceptedCourse = new Course();

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    synchronized public void parseJSONString(String jsonData) {

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
    synchronized public void parseJSONObject(JSONObject jsonData) {
		try {
			if(jsonData.has(SUBSCRIBED_KEY)) setSubscribed(jsonData.getInt(SUBSCRIBED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(REJECTED_KEY)) setRejected(jsonData.getInt(REJECTED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(COURSE_KEY)) {
				JSONObject jObject = jsonData.getJSONObject(COURSE_KEY);
				Course course = new Gson().fromJson(jObject.toString(),Course.class);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Accepted Course :: " + course.toString() );
				setAcceptedCourse(course);
				course = null;
			}		
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public int getRejected() {
		return rejected;
	}

	public void setRejected(int rejected) {
		this.rejected = rejected;
	}

	public int getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(int subscribed) {
		this.subscribed = subscribed;
	}

	public Course getAcceptedCourse() {
		return acceptedCourse;
	}

	public void setAcceptedCourse(Course acceptedCourse) {
		this.acceptedCourse = acceptedCourse;
	}


}
