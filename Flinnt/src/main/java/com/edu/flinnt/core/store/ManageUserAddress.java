package com.edu.flinnt.core.store;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.AddComment;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ManageUserAddress {

    public static final String TAG = AddComment.class.getSimpleName();
    public static AddUserAddressResponse mAddCommentResponse = null;
    public Handler mHandler = null;
    public String UserId ="",address1 ="",address2 = "",city = "",stateId = "",pin = "",phone = "",fullname = "",addressType = "",userAddrID = "";
    private boolean isUpdate;

    //{"user_id":"1","address1":"iscon elegance","address2":"","city":"Ahmedabad","state_id":"12","pin":"","phone":""}
    public ManageUserAddress(Handler handler, String userid, String fullname, String address1 , String address2, String city, String stateId, String pin, String addresstype, String phone) {
        mHandler = handler;
        this.UserId = userid;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateId = stateId;
        this.pin = pin;
        this.phone = phone;
        this.fullname = fullname;
        this.addressType = addresstype;
        getLastResponse();
    }

    public ManageUserAddress(Handler handler, String userid, String fullname, String address1 , String address2, String city, String stateId, String pin, String addresstype, String phone, String userAddressId) {
        mHandler = handler;
        this.UserId = userid;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateId = stateId;
        this.pin = pin;
        this.phone = phone;
        this.fullname = fullname;
        this.addressType = addresstype;
        this.userAddrID = userAddressId;

        getLastResponse();
    }

    public ManageUserAddress(Handler handler, String userAddrID) {
        mHandler = handler;
        this.userAddrID = userAddrID;
        getLastResponse();
    }

    public static AddUserAddressResponse getLastResponse() {
        if (mAddCommentResponse == null) {
            mAddCommentResponse = new AddUserAddressResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AddComment response : " + mAddCommentResponse.toString());
        return mAddCommentResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.LOCAL_API_URL_NEW + Flinnt.ADD_USER_DATA_API;
    }

    public String buildURLString2() {
        return Flinnt.LOCAL_API_URL_NEW + Flinnt.UPDATE_USER_ADDRESS_API;
    }
    public String buildURLString3() {
        return Flinnt.LOCAL_API_URL_NEW + Flinnt.DELETE_ADDRESS_API;
    }


    public void sendDeleteUserAddressRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendDeleteRequest(userAddrID);
                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }

    public void sendUpdateUserAddressRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendUpdateRequest();
                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }
    /**
     * sends request along with parameters
     */
    public void sendRequest() {
        synchronized (AddComment.class) {
            try {
                String url = buildURLString();


                AddUserAddressRequest addCommentRequest = new AddUserAddressRequest();

                addCommentRequest.setUserId(Config.getStringValue(Config.USER_ID));
                addCommentRequest.setAddress1(address1);
                addCommentRequest.setAddress2(address2);
                addCommentRequest.setCity(city);
                addCommentRequest.setState_id(stateId);
                addCommentRequest.setPhone(phone);
                addCommentRequest.setPin(pin);
                addCommentRequest.setFullname(fullname);
                addCommentRequest.setPhone(phone);
                addCommentRequest.setAddress_type(addressType);

                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment Request :\nUrl : " + url + "\nData : " + addCommentRequest.getJSONString());

                JSONObject jsonObject = addCommentRequest.getJSONObject();

                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }


    private void sendUpdateRequest() {
        synchronized (ManageUserAddress.class) {
            try {
                String url = buildURLString2();


                AddUserAddressRequest addCommentRequest = new AddUserAddressRequest();

                addCommentRequest.setUserId(Config.getStringValue(Config.USER_ID));
                addCommentRequest.setAddress1(address1);
                addCommentRequest.setAddress2(address2);
                addCommentRequest.setCity(city);
                addCommentRequest.setState_id(stateId);
                addCommentRequest.setPhone(phone);
                addCommentRequest.setPin(pin);
                addCommentRequest.setFullname(fullname);
                addCommentRequest.setPhone(phone);
                addCommentRequest.setAddress_type(addressType);
                addCommentRequest.setUserAddressId(userAddrID);

                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment Request :\nUrl : " + url + "\nData : " + addCommentRequest.getJSONString());

                JSONObject jsonObject = addCommentRequest.getJSONObjectForUpdate();

                sendJsonObjectRequestForUpdate(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }


    public void sendDeleteRequest(String userAddrID) {
        synchronized (ManageUserAddress.class) {
            try {
                String url = buildURLString3();

                AddUserAddressRequest addCommentRequest = new AddUserAddressRequest();

                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment Request :\nUrl : " + url + "\nData : " + addCommentRequest.getJSONString());

                JSONObject jsonObject = addCommentRequest.getJSONObjectForDelete(userAddrID);

                sendJsonObjectRequestForDelete(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }



     private void sendJsonObjectRequestForUpdate(String url, JSONObject jsonObject) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment response :\n" + response.toString());

                if (null != response) {

                    int status = response.optInt("status");
                    if(status == 1){
                        sendMesssageToGUIForUpdate(Flinnt.SUCCESS);
                    }else{
                        sendMesssageToGUIForUpdate(Flinnt.FAILURE);

                    }
                }
                else {
                    sendMesssageToGUIForUpdate(Flinnt.FAILURE);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AddCommentError : " + error.getMessage());

                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void sendJsonObjectRequestForDelete(String url,JSONObject jsonObject) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment response :\n" + response.toString());

                if (null != response) {

                    int status = response.optInt("status");
                    if(status == 1){
                        sendMesssageToGUIForDelete(Flinnt.SUCCESS);
                    }else{
                        sendMesssageToGUIForDelete(Flinnt.FAILURE);

                    }
                }
                else {
                    sendMesssageToGUIForDelete(Flinnt.FAILURE);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AddCommentError : " + error.getMessage());

                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }



    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUIForUpdate(int messageID) {
        if( null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.arg1 = 200;
            mHandler.sendMessage(msg);
        }
    }

    public void sendMesssageToGUIForDelete(int messageID) {
        if( null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.arg1 = 200;
            mHandler.sendMessage(msg);
        }
    }
    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment response :\n" + response.toString());

                   if (null != response) {
                       Gson gson = new Gson();
                       mAddCommentResponse  = (AddUserAddressResponse)gson.fromJson(String.valueOf(response),AddUserAddressResponse.class);
                       sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                    else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AddCommentError : " + error.getMessage());

                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if( null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mAddCommentResponse;
            mHandler.sendMessage(msg);
        }
    }

}
