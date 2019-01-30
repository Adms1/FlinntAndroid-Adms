package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class SelectCourseResponse extends BaseResponse {
	
	public static final String HAS_MORE_KEY = "has_more";
	public static final String COURSES_KEY = "courses";
	public static final String COURSES_PICTURE_URL = "course_picture_url";
	
	private int hasMore = Flinnt.INVALID;
	private ArrayList<CourseInfo> courseInfoList		= new ArrayList<CourseInfo>();
	private String coursePictureUrl = "";

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
			if(jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
			if(jsonData.has(COURSES_PICTURE_URL)) setCoursePictureUrl(jsonData.getString(COURSES_PICTURE_URL));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			JSONArray courseInfos = jsonData.getJSONArray(COURSES_KEY);
			clearCourseInfoList();
			for(int i = 0; i < courseInfos.length(); i++) {
				JSONObject jObject = courseInfos.getJSONObject(i);
				CourseInfo courseInfo = new CourseInfo();	
				courseInfo.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "course info :: " + courseInfo.toString() );
				courseInfoList.add(courseInfo);
				courseInfo = null;
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

	public ArrayList<CourseInfo> getCourseInfoList() {
		return courseInfoList;
	}

	public void setCourseInfoList(ArrayList<CourseInfo> courseInfoList) {
		this.courseInfoList = courseInfoList;
	}

	public String getCoursePictureUrl() {
		return coursePictureUrl;
	}

	public void setCoursePictureUrl(String coursePictureUrl) {
		this.coursePictureUrl = coursePictureUrl;
	}

	public void clearCourseInfoList() {
		this.courseInfoList.clear();
	}

}
