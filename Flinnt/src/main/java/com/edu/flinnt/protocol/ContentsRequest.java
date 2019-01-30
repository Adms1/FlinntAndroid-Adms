package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class ContentsRequest {
    public static final String OFFSET_KEY = "offset"; // Start position from where to fetch list
    public static final String MAX_KEY = "max";    // Maximum no. of entries to fetch
    public static final String SEARCH_KEY = "search";    // keyword to search in course name (Optional)
    public static final String COURSE_ID = "course_id";
    public static final String MULTIPLE_ATTACHMENT = "multiple_attachment";
    public static final String SHOW_QUIZ = "show_quiz";
    private String userID = "";
    private String course_id = "";
    private String multiple_attachment = "";
    private int offset = Flinnt.INVALID;
    private int max = Flinnt.INVALID;
    private String search = "";
    private ArrayList<String> arrayList = new ArrayList<>();

    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {

        JSONObject returnedJObject = new JSONObject();

        try {
            returnedJObject.put(LoginResponse.USER_ID_KEY, userID);

            returnedJObject.put(LoginResponse.USER_ID_KEY, getUserID());
            returnedJObject.put(COURSE_ID, getCourse_id());
            returnedJObject.put(MULTIPLE_ATTACHMENT, getMultiple_attachment());
            returnedJObject.put(SHOW_QUIZ,"1");

            if (Flinnt.INVALID != offset) {
                returnedJObject.put(OFFSET_KEY, offset);
            }
            if (Flinnt.INVALID != getMax()) {
                returnedJObject.put(MAX_KEY, getMax());
            }
            if (!TextUtils.isEmpty(search)) {
                returnedJObject.put(SEARCH_KEY, search);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMax() {
        if (0 == max) {
            max = 10;
        }
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getMultiple_attachment() {
        return multiple_attachment;
    }

    public void setMultiple_attachment(String multiple_attachment) {
        this.multiple_attachment = multiple_attachment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
