package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class SuggestedCoursesRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String MAX_KEY    = "max";

    private String userId = "";
    private String inst_book_vender_id = "";
    private String standardid = "";
    private String courseId = "";
    private int max = 0;

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
            returnedJObject.put(COURSE_ID_KEY, courseId);
            returnedJObject.put(MAX_KEY, max);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectNew() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(Flinnt.INST_BOOK_VENDOR_ID, getInst_book_vender_id());
            returnedJObject.put(Flinnt.STANDARD_ID, getStandardid());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }
    public synchronized JSONObject getJSONObjectNew1() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(Flinnt.INST_BOOK_VENDOR_ID, getInst_book_vender_id());

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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getMax() {
        return max == 0 ? 10 : max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getInst_book_vender_id() {
        return inst_book_vender_id;
    }

    public void setInst_book_vender_id(String inst_book_vender_id) {
        this.inst_book_vender_id = inst_book_vender_id;
    }

    public String getStandardid() {
        return standardid;
    }

    public void setStandardid(String standardid) {
        this.standardid = standardid;
    }
}
