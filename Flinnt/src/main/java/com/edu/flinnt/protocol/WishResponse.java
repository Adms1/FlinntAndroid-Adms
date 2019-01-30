package com.edu.flinnt.protocol;

import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by flinntandr1 on 28/6/16.
 */
public class WishResponse extends BaseResponse {
    public static final String PICTURE_URL_KEY = "picture_url";
    public static final String USER_PICTURE_URL_KEY = "user_picture_url";
    public static final String INSTITUTE_PICTURE_URL_KEY = "institute_picture_url";
    public static final String HAS_MORE_KEY = "has_more";

    public static final String COURSES_KEY = "courses";

    private String pictureUrl = "";
    private String userPictureUrl = "";
    private String institutePictureUrl = "";
    private int hasMore = 0;

    private ArrayList<WishableCourses> wishList = new ArrayList<>();

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
                wishList = new ArrayList<>();
                for (int i = 0; i < courses.length(); i++) {
                    JSONObject jObject = courses.getJSONObject(i);
                    WishableCourses wishableCourses = new WishableCourses();
                    wishableCourses.parseJSONObject(jObject);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("WishableCourses :: " + wishableCourses.toString());
                    wishList.add(wishableCourses);
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

    public ArrayList<WishableCourses> getWishList() {
        return wishList;
    }

    public void setWishList(ArrayList<WishableCourses> wishList) {
        this.wishList = wishList;
    }
}
