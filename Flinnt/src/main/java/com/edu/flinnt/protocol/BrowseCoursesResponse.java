package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * {
 * "status": 1,
 * "data": {
 * "picture_url": "https://flinnt1.s3.amazonaws.com/courses/",
 * "user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/",
 * "institute_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/courses/",
 * "has_more": 1,
 * "courses": [
 * {
 * "id": "709",
 * "name": "The Economic Blog",
 * "institute_name": "Aditya Iyer",
 * "picture": "709_1403429460.jpg",
 * "ratings": "2.0"
 * “user_count”: 10
 * },
 * {
 * "id": "6562",
 * "name": "samir153 c12",
 * "institute_name": "Samira153 Flinnt Testing Institute",
 * "picture": "",
 * "ratings": "0.0"
 * “user_count”: 100
 * },
 * ...
 * ]
 * }
 * }
 */


/**
 * class to parse response to object
 */
public class BrowseCoursesResponse extends BaseResponse {

    public static final String PICTURE_URL_KEY = "picture_url";
    public static final String USER_PICTURE_URL_KEY = "user_picture_url";
    public static final String INSTITUTE_PICTURE_URL_KEY = "institute_picture_url";
    public static final String HAS_MORE_KEY = "has_more";

    public static final String COURSES_KEY = "courses";


    private String pictureUrl = "";
    private String userPictureUrl = "";
    private String institutePictureUrl = "";
    private int hasMore = 0;

    private ArrayList<BrowsableCourse> courseList = new ArrayList<>();

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {

        if (TextUtils.isEmpty(jsonData)) {
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
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setInstitutePictureUrl(jsonData.getString(INSTITUTE_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(COURSES_KEY)) {
                JSONArray courses = jsonData.getJSONArray(COURSES_KEY);
                courseList = new ArrayList<>();
                for (int i = 0; i < courses.length(); i++) {
                    JSONObject jObject = courses.getJSONObject(i);
                    BrowsableCourse course = new BrowsableCourse();
                    course.parseJSONObject(jObject);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("BrowsableCourse :: " + course.toString());
                    courseList.add(course);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
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

    public String getInstitutePictureUrl() {
        return institutePictureUrl;
    }

    public void setInstitutePictureUrl(String institutePictureUrl) {
        this.institutePictureUrl = institutePictureUrl;
    }

    public int getHasMore() {
        return hasMore;
    }

    public void setHasMore(int hasMore) {
        this.hasMore = hasMore;
    }

    public ArrayList<BrowsableCourse> getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList<BrowsableCourse> courseList) {
        this.courseList = courseList;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("pictureUrl : " + pictureUrl)
                .append(", userPictureUrl : " + userPictureUrl)
                .append(", institutePictureUrl : " + institutePictureUrl)
                .append(", hasMore : " + hasMore);
        return strBuffer.toString();
    }
}
