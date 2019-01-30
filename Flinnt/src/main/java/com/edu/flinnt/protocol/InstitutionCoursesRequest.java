package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 19/10/16.
 */
public class InstitutionCoursesRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String INSTITUTE_ID_KEY = "institute_id";
    public static final String OFFSET_KEY = "offset"; // Start position from where to fetch list
    public static final String MAX_KEY = "max";    // Maximum no. of entries to fetch
    public static final String SEARCH_KEY = "search";
    private String userId = "";
    private String instituteId = "";
    private int offset = 0;
    private int max = 20;
    private String search = "";
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
            returnedJObject.put(USER_ID_KEY, userId);
            returnedJObject.put(INSTITUTE_ID_KEY, instituteId);
            returnedJObject.put(OFFSET_KEY, offset);
            returnedJObject.put(MAX_KEY, max);
            returnedJObject.put(SEARCH_KEY,search);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMax() {
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
}
