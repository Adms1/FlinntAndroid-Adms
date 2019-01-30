package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PollListRequest;
import com.edu.flinnt.protocol.PollListResponse;
import com.edu.flinnt.protocol.PostListRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;


/**
 * Created by flinnt-android-2 on 3/3/17.
 */

public class PollList {

    public static final String TAG = PollList.class.getSimpleName();
    public static PollListResponse mPollListResponse = null;
    public Handler mHandler = null;
    public PollListRequest mPollListRequest;

    public PollList(Handler handler, PollListRequest postListRequest) {
        mHandler = handler;
        mPollListRequest = postListRequest;
        getLastResponse();
    }

    public static PollListResponse getLastResponse() {
        if (mPollListResponse == null) {
            mPollListResponse = new PollListResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("PollList response : " + mPollListResponse.toString());
        return mPollListResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COMMUNICATION_POLL_OPTIONS;
    }

    public void sendPollListRequest() {
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
        synchronized (PollList.class) {
            try {
                String url = buildURLString();
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PollList Request :\nUrl : " + url + "\nData : " + mPollListRequest.getJSONString());

                JSONObject jsonObject = mPollListRequest.getJSONObject();
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
                    LogWriter.write("PollList response :\n" + response.toString());


                if (mPollListResponse.isSuccessResponse(response)) {
                    String PollListResponse = new String(response.toString());
                    JSONObject jsonData = mPollListResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mPollListResponse = gson.fromJson(PollListResponse, PollListResponse.class);
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
                    LogWriter.write("PostListError : " + error.getMessage());

                mPollListResponse.parseErrorResponse(error);
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
            msg.obj = mPollListResponse;
            mHandler.sendMessage(msg);
        }
    }

}