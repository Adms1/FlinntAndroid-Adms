package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Course response object class
 */
public class CourseInfo {

	public static final String COURSE_ID_KEY 								= "course_id";
	public static final String COURSE_NAME_KEY							= "course_name";
	public static final String COURSE_PICTURE 							= "course_picture";
	
	public String courseID 							= "";
	public String courseName 						= "";
	public String coursePicture 					= "";

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			setCourseID( jObject.getString(COURSE_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setCourseName( jObject.getString(COURSE_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			setCoursePicture( jObject.getString(COURSE_PICTURE) );
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

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("courseID : " + courseID)
		.append(", mCourseNameTxt : " + courseName)
		.append(", coursePicture : " + coursePicture);
		return strBuffer.toString();
	}
}
