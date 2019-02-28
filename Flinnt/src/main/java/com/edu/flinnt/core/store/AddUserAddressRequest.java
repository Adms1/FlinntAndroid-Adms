package com.edu.flinnt.core.store;

import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

public class AddUserAddressRequest {

    private String userId;
    private String address1;
    private String address2;
    private String city;
    private String state_id;
    private String pin;
    private String phone;
    private String fullname;
    private String address_type;
    private String userAddressId;

    private static final String USER_ID = "user_id";
    private static final String USER_ADDRESS_ID = "user_address_id";


    private static final String FULLNAME = "fullname";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static  final String PHONE = "phone";
    private static final String CITY = "city";
    private static final String STATE_ID = "state_id";
    private static final String PIN = "pin";
    private static final String ADDRESS_TYPE = "address_type";




    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID,Config.getStringValue(Config.USER_ID));
            returnedJObject.put(FULLNAME,getFullname());
            returnedJObject.put(ADDRESS1,getAddress1());
            returnedJObject.put(ADDRESS2,getAddress2());
            returnedJObject.put(CITY,getCity());
            returnedJObject.put(STATE_ID,getState_id());
            returnedJObject.put(PIN,getPin());
            returnedJObject.put(PHONE,getPhone());
            returnedJObject.put(ADDRESS_TYPE,getAddress_type());
        }
        catch(Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;

    }

    public synchronized JSONObject getJSONObjectForUpdate() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID,Config.getStringValue(Config.USER_ID));
            returnedJObject.put(FULLNAME,getFullname());
            returnedJObject.put(ADDRESS1,getAddress1());
            returnedJObject.put(ADDRESS2,getAddress2());
            returnedJObject.put(CITY,getCity());
            returnedJObject.put(STATE_ID,getState_id());
            returnedJObject.put(PIN,getPin());
            returnedJObject.put(PHONE,getPhone());
            returnedJObject.put(ADDRESS_TYPE,getAddress_type());
            returnedJObject.put(USER_ADDRESS_ID,getUserAddressId());

        }
        catch(Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;

    }


    public synchronized JSONObject getJSONObjectForDelete(String useraddressID) {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ADDRESS_ID,useraddressID);
        }
        catch(Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(String userAddressId) {
        this.userAddressId = userAddressId;
    }
}
