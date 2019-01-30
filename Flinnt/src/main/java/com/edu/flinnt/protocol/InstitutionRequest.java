package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class InstitutionRequest {
    public static final String OFFSET_KEY = "offset"; // Start position from where to fetch list
    public static final String MAX_KEY = "max";    // Maximum no. of entries to fetch
    public static final String SEARCH_KEY = "search";
    private String userID = "";
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
            returnedJObject.put(SEARCH_KEY,getSearch());
            if (Flinnt.INVALID != offset) {
                returnedJObject.put(OFFSET_KEY, offset);
            }
            if (Flinnt.INVALID != getMax()) {
                returnedJObject.put(MAX_KEY, getMax());
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
