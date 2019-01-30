package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.LoginRequest;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class Login {

    public static final String TAG = Login.class.getSimpleName();
    public static LoginResponse mLoginResponse = null;
    public Handler mHandler = null;

    public int mRequestType = AUTO_LOGIN;
    public static final int USER_LOGIN = 1;
    public static final int AUTO_LOGIN = 2;

    public Login(Handler handler, int requestType) {
        mHandler = handler;
        mRequestType = requestType;
        getLastResponse();
    }

    public static LoginResponse getLastResponse() {
        if (mLoginResponse == null) {
            mLoginResponse = new LoginResponse();
        }
        mLoginResponse.parseJSONString(Config.getStringValue(Config.LAST_LOGIN_RESPONSE));
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("Login response : " + mLoginResponse.toString());
        return mLoginResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        if (mRequestType == USER_LOGIN) {
            return Flinnt.API_URL + Flinnt.URL_LOGIN;
        } else {
            return Flinnt.API_URL + Flinnt.URL_AUTO_LOGIN;
        }

    }

    public void sendLoginRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendRequest();
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
        synchronized (Login.class) {
            try {
                String url = buildURLString();

                LoginRequest loginRequest = new LoginRequest();

                if (mRequestType == USER_LOGIN) {
                    loginRequest.setUsername(Config.getStringValue(Config.USER_LOGIN));
                    loginRequest.setPassword(HTTPHelper.getMD5(Config.getStringValue(Config.PASSWORD)));
                } else {
                    loginRequest.setUserID(Config.getStringValue(Config.USER_ID));
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("Login Request :\nUrl : " + url + "\nData : " + loginRequest.getJSONString());

                JSONObject jsonObject = loginRequest.getJSONObject();

                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }

    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //@Nikhil  2262018

                Log.d("autploginapp", response.toString());

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("Login response :\n" + response.toString());

                if (mLoginResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mLoginResponse.getJSONData(response);
                    if (null != jsonData) {
                        mLoginResponse.parseJSONObject(jsonData);
                        Config.setStringValue(Config.LAST_LOGIN_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("Login Error : " + error.getMessage());

                mLoginResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.IMMEDIATE);
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }


    /**
     * Sends response to handler
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mLoginResponse;
            mHandler.sendMessage(msg);
        }
    }

}
