package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class SuggestedCoursesResponse extends BaseResponse {

    public static final String PICTURE_URL_KEY 							= "picture_url";
    public static final String COURSES_KEY 		            			= "courses";

    private String pictureUrl = "";
    private int type;
    private ArrayList<BrowsableCourse> browsableCourses;

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
			setPictureUrl(jsonData.getString(PICTURE_URL_KEY));
		}  catch (Exception e) {	LogWriter.err(e);}

        try {
            if (jsonData.has(COURSES_KEY)) {
                JSONArray courseJArray = jsonData.getJSONArray(COURSES_KEY);
                browsableCourses = new ArrayList<>();
                for (int i=0; i < courseJArray.length() ; i++) {
                    BrowsableCourse browsableCourse = new BrowsableCourse();
                    browsableCourse.parseJSONObject(courseJArray.getJSONObject(i));
                    browsableCourses.add(browsableCourse);
                }
            }
        } catch (Exception e) { LogWriter.err(e); }
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public ArrayList<BrowsableCourse> getBrowsableCourses() {
        return browsableCourses;
    }

    public void setBrowsableCourses(ArrayList<BrowsableCourse> browsableCourses) {
        this.browsableCourses = browsableCourses;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("picture : " + pictureUrl)
        .append(", type : " + type)
        .append(", browsableCourses.size() : " + browsableCourses.size());
        return strBuffer.toString();
    }
}
