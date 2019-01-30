package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PersonalInformationRequest;
import com.edu.flinnt.protocol.PersonalInformationResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 26/11/16.
 */

public class PersonalInformation {


    public static final String TAG = ProfileUpdate.class.getSimpleName();
    public static PersonalInformationResponse mPersonalInformationResponse = null;
    public Handler mHandler = null;
    public PersonalInformationRequest mPersonalInformationRequest;

    public PersonalInformation(Handler handler, PersonalInformationRequest PersonalInformationRequest) {
        mHandler = handler;
        mPersonalInformationRequest = PersonalInformationRequest;
        getResponse();
    }

    public static PersonalInformationResponse getResponse() {
        if (mPersonalInformationResponse == null) {
            mPersonalInformationResponse = new PersonalInformationResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("ProfileUpdate response : " + mPersonalInformationResponse.toString());
        return mPersonalInformationResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_USER_PROFILE_SAVE_KEY;
    }

    public void sendPersonalInformationRequest() {
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
        synchronized (ProfileUpdate.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PersonalInformation Request :\nUrl : " + url + "\nData : " + mPersonalInformationRequest.getJSONString());

                JSONObject jsonObject = mPersonalInformationRequest.getJSONObject();

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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PersonalInformation response :\n" + response.toString());

                if (mPersonalInformationResponse.isSuccessResponse(response)) {
                    String PersonalInformationResponse = new String(response.toString());

                    JSONObject jsonData = mPersonalInformationResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mPersonalInformationResponse = gson.fromJson(PersonalInformationResponse, PersonalInformationResponse.class);
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
                    LogWriter.write("PersonalInformationError : " + error.getMessage());

                mPersonalInformationResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

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
            msg.obj = mPersonalInformationResponse;
            mHandler.sendMessage(msg);
        }
    }
}
