package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Builds json request
 */
public class MyCoursesRequest {

	public static final String OFFSET_KEY 				= "offset"; // Start position from where to fetch list
	public static final String MAX_KEY 					= "max";	// Maximum no. of entries to fetch
	public static final String SEARCH_KEY 				= "search";	// keyword to search in course name (Optional)
	public static final String EXCLUDE_COURSE_KEY 		= "exclude_courses"; // JSON array of course ids to exclude from output (Optional)
	
	// For offline database update 
	public static final String UPDATE_OFFLINE_KEY 		= "update_offline";	// This value must be 1.
	public static final String OFFLINE_COURSES_KEY 		= "offline_courses"; // JSON array of course id for which data should be fetched. The array must not be empty.
	
	
	private String userID 				= "";
	private int offset 					= Flinnt.INVALID;
	private int max 					= Flinnt.INVALID;
	private String search 				= "";
	private int updateOffline 			= Flinnt.INVALID;
	private ArrayList<String> excludeCourses = new ArrayList<String>();
	private ArrayList<String> offlineCourses = new ArrayList<String>();


    /**
     * Converts the json object to string
     * @return converted json string
     */
    public synchronized String getJSONString() {
		
	    return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
		
		JSONObject returnedJObject = new JSONObject();
	    try {
	        returnedJObject.put(LoginResponse.USER_ID_KEY, userID); 

	        if( Flinnt.TRUE == updateOffline ) {
	        	returnedJObject.put(UPDATE_OFFLINE_KEY, updateOffline);
		        if( offlineCourses.size() > 0 ) {
		        	returnedJObject.put(OFFLINE_COURSES_KEY, getJSONArray(offlineCourses));
		        }
	        }
	        else {
		        if( Flinnt.INVALID != offset ) {
		        	returnedJObject.put(OFFSET_KEY, offset);
		        }
		        if( Flinnt.INVALID != getMax() ) {
		        	returnedJObject.put(MAX_KEY, getMax());
		        }
		        if( !TextUtils.isEmpty(search) ) {
		        	returnedJObject.put(SEARCH_KEY, search);
		        }
		        if( excludeCourses.size() > 0 ) {
		        	returnedJObject.put(EXCLUDE_COURSE_KEY, getJSONArray(excludeCourses));
		        }
	        }

	    }
	    catch(Exception e) {
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMax() {
		if( 0 == max ) {
			max = 10;
		}
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public ArrayList<String> getExcludeCourses() {
		return excludeCourses;
	}

	public void setExcludeCourseList(ArrayList<Course> courses) {
		ArrayList<String> excludeCourseList = new ArrayList<String>();
		for( int i = 0; i < courses.size(); i++ ) {
			excludeCourseList.add(courses.get(i).getCourseID());
		}
		setExcludeCourses(excludeCourseList);
	}
	
	public void setExcludeCourses(ArrayList<String> excludeCourses) {
		this.excludeCourses = excludeCourses;
	}

	public int getUpdateOffline() {
		return updateOffline;
	}

	public void setUpdateOffline(int updateOffline) {
		this.updateOffline = updateOffline;
	}

	public ArrayList<String> getOfflineCourses() {
		return offlineCourses;
	}

	public void setOfflineCourses(ArrayList<String> offlineCourses) {
		this.offlineCourses = offlineCourses;
	}

	public void setOfflineCourseList(ArrayList<Course> courses) {
		ArrayList<String> excludeCourseList = new ArrayList<String>();
		for( int i = 0; i < courses.size(); i++ ) {
			excludeCourseList.add(courses.get(i).getCourseID());
		}
		setOfflineCourses(excludeCourseList);
	}
	
	private JSONArray getJSONArray(ArrayList<String> courseList) {
		JSONArray jArray = new JSONArray();
		if(null != courseList){
			for (int i = 0; i < courseList.size(); i++) {
				jArray.put( courseList.get(i) );
			}
		}
		return jArray;
	}
}
