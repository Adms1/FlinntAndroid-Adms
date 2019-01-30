package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class BrowseCoursesRequest {

    public static final String OFFSET_KEY = "offset"; // Start position from where to fetch list
    public static final String MAX_KEY = "max";    // Maximum no. of entries to fetch
    public static final String SEARCH_KEY = "search";    // keyword to search in course name (Optional)
    public static final String FIELDS_KEY = "fields";    // keyword to search in course name (Optional)
    public static final String CATEGORY_ID_KEY = "category_id";    // keyword to search in course name (Optional)


    private String userID = "";
    private int offset = Flinnt.INVALID;
    private int max = Flinnt.INVALID;
    private String search = "";
    private String categoryId = "";

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

            if (Flinnt.INVALID != offset) {
                returnedJObject.put(OFFSET_KEY, offset);
            }
            if (Flinnt.INVALID != getMax()) {
                returnedJObject.put(MAX_KEY, getMax());
            }
            if (!TextUtils.isEmpty(search)) {
                returnedJObject.put(SEARCH_KEY, search);
            }
            if (!TextUtils.isEmpty(categoryId)) {
                returnedJObject.put(CATEGORY_ID_KEY, categoryId);
            }
        } catch (Exception e) {
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
        if (0 == max) {
            max = 5;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
