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
public class CourseViewResponse extends BaseResponse {

    public static final String PICTURE_URL_KEY 	= "picture_url";
    public static final String USER_PICTURE_URL_KEY = "user_picture_url";
    public static final String COURSE_KEY = "course";
    public static final String CATEGORIES_KEY = "categories";

    private String pictureUrl = "";
    private String userPictureUrl    = "";

    private BrowsableCourse browsableCourse;
    private ArrayList<BrowseCourseCategories> categoriesList = new ArrayList<>();

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
            if (jsonData.getJSONObject(COURSE_KEY).has(CATEGORIES_KEY)) {
                JSONArray categories = jsonData.getJSONObject(COURSE_KEY).getJSONArray(CATEGORIES_KEY);
                categoriesList = new ArrayList<>();
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject jObject = categories.getJSONObject(i);
                    BrowseCourseCategories browseCourseCategories = new BrowseCourseCategories();
                    browseCourseCategories.parseJSONObject(jObject);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Browse Course Categories :: " + browseCourseCategories.toString());
                    categoriesList.add(browseCourseCategories);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

		try {
			setPictureUrl(jsonData.getString(PICTURE_URL_KEY));
		}  catch (Exception e) {	LogWriter.err(e);}

        try {
            setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
        } catch (Exception e) { LogWriter.err(e); }

        try {
            if (jsonData.has(COURSE_KEY)) {
                JSONObject courseJSONObject = jsonData.getJSONObject(COURSE_KEY);
                browsableCourse = new BrowsableCourse();
                browsableCourse.parseJSONObject(courseJSONObject);
                setBrowsableCourse(browsableCourse);
            }
        } catch (Exception e) { LogWriter.err(e); }
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        this.userPictureUrl = userPictureUrl;
    }

    public BrowsableCourse getBrowsableCourse() {
        return browsableCourse;
    }

    public void setBrowsableCourse(BrowsableCourse browsableCourse) {
        this.browsableCourse = browsableCourse;
    }

    public ArrayList<BrowseCourseCategories> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(ArrayList<BrowseCourseCategories> categoriesList) {
        this.categoriesList = categoriesList;
    }

    @Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("course : " + browsableCourse.toString() );
		return strBuffer.toString();
	}
}
