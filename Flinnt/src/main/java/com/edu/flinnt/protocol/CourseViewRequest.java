package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class CourseViewRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String COURSE_HASH_KEY = "course_hash";
    public static final String PAID_PROMO_CONTACT_KEY = "sp_paid_promo_contact";
    public static final String INST_BOOK_VENDOR_ID = "institution_book_vendor_id";
    public static final String INST_BOOK_SET_VENDOR_ID = "institution_book_set_vendor_id";
    private String userId = "";

    //09-01-2019
    private String inst_book_set_vendor_id = "";

    private String inst_book_vendor_id = "";
    private String courseId = "";
    private String couserHash = "";
    private String paidPromoContact = "1";

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
            returnedJObject.put(USER_ID_KEY,userId);
            returnedJObject.put(PAID_PROMO_CONTACT_KEY,Flinnt.ENABLED);
            if(courseId!=null && !courseId.isEmpty())
                returnedJObject.put(COURSE_ID_KEY, courseId);
            if(couserHash!=null && !couserHash.isEmpty())
                returnedJObject.put(COURSE_HASH_KEY, couserHash);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    //09-01-2019 by vijay
    public synchronized JSONObject getJSONObjectNew() {
        JSONObject returnedJObject = new JSONObject();
        try {
            //09-01-2019
            returnedJObject.put(INST_BOOK_VENDOR_ID,getInst_book_vendor_id());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectOfBooksetList() {
        JSONObject returnedJObject = new JSONObject();
        try {
            //09-01-2019
            returnedJObject.put(USER_ID_KEY,Config.getStringValue(Config.USER_ID));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectOfBookSetDetail() {
        JSONObject returnedJObject = new JSONObject();
        try {
            //09-01-2019
            returnedJObject.put(INST_BOOK_SET_VENDOR_ID,getInst_book_set_vendor_id());
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

    public void setCouserHash(String couserHash) {
        this.couserHash = couserHash;
    }

    public String getCouserHash() {
        return couserHash;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getPaidPromoContact() {
        return paidPromoContact;
    }

    public void setPaidPromoContact(String paidPromoContact) {
        this.paidPromoContact = paidPromoContact;
    }

    public String getInst_book_vendor_id() {
        return inst_book_vendor_id;
    }

    public void setInst_book_vendor_id(String inst_book_vendor_id) {
        this.inst_book_vendor_id = inst_book_vendor_id;
    }

    public String getInst_book_set_vendor_id() {
        return inst_book_set_vendor_id;
    }

    public void setInst_book_set_vendor_id(String inst_book_set_vendor_id) {
        this.inst_book_set_vendor_id = inst_book_set_vendor_id;
    }
}
