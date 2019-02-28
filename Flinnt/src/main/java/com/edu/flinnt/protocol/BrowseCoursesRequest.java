package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
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

    public static final String STORE_FILTER_LANG_KEY = "language_id";
    public static final String STORE_FILTER_FORMAT_KEY = "format";
    public static final String STORE_FILTER_AUTHORID_KEY = "author_id";
    public static final String STORE_FILTER_PRICE_MAX_KEY = "max";
    public static final String STORE_FILTER_PRICE_MIN_KEY = "min";
    public static final String STORE_FILTER_CAT_TREE_ID_KEY = "category_tree_id";


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


    public synchronized JSONObject getJSONObjectForFilter(String languageIds,String formats,String authorIds,String max,String min,String category_tree_id) {

        JSONObject returnedJObject = new JSONObject();
        try {

            if(languageIds.length() > 0) {
                String[] languages = languageIds.split(",");

                JSONArray langIdArray = new JSONArray();
                for (int langCount = 0; langCount < languages.length; langCount++) {

                    langIdArray.put(languages[langCount]);
                }
                returnedJObject.put(STORE_FILTER_LANG_KEY,langIdArray);

            }else {
                //pass blank array
                JSONArray langIdArray = new JSONArray();
                returnedJObject.put(STORE_FILTER_LANG_KEY,langIdArray);
            }


            if(formats.length() > 0) {
                String[] formatArray = formats.split(",");

                JSONArray formatIdArray = new JSONArray();

                for (int Count = 0; Count < formatArray.length; Count++) {

                    formatIdArray.put(formatArray[Count]);
                }
                returnedJObject.put(STORE_FILTER_FORMAT_KEY,formatIdArray);
            }else {
                //pass blank array
                JSONArray formatIdArray = new JSONArray();
                returnedJObject.put(STORE_FILTER_FORMAT_KEY,formatIdArray);
            }


            if(authorIds.length() > 0) {
                String[] authors = authorIds.split(",");

                JSONArray authorIdArray = new JSONArray();
                for (int langCount = 0; langCount < authors.length; langCount++) {

                    authorIdArray.put(authors[langCount]);
                }
                returnedJObject.put(STORE_FILTER_AUTHORID_KEY,authorIdArray);

            }else {
                //pass blank array
                JSONArray authorIdArray = new JSONArray();
                returnedJObject.put(STORE_FILTER_AUTHORID_KEY,authorIdArray);
            }

            returnedJObject.put(STORE_FILTER_PRICE_MAX_KEY,max);
            returnedJObject.put(STORE_FILTER_PRICE_MIN_KEY,min);

            returnedJObject.put(STORE_FILTER_CAT_TREE_ID_KEY,category_tree_id);
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
