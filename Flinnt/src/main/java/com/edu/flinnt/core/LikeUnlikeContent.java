package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.LikeUnlikeRequest;
import com.edu.flinnt.protocol.LikeUnlikeResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class LikeUnlikeContent {
    public Handler mHandler = null;
//	public static ResourceValidationResponse mResourceValidationResponse = null;

    public static LikeUnlikeRequest mLikeUnlikeRequest = null;
    public static LikeUnlikeResponse mLikeUnlikeResponse = null;

    public LikeUnlikeContent(Handler handler) {
        mHandler = handler;
        getBookmarksResponse();
    }

    public static LikeUnlikeResponse getBookmarksResponse() {
        if (mLikeUnlikeResponse == null) {
            mLikeUnlikeResponse = new LikeUnlikeResponse();
        }
        return mLikeUnlikeResponse;
    }

    public void setLikeBookmarkRequest(LikeUnlikeRequest mLikeUnlikeRequest) {
        this.mLikeUnlikeRequest = mLikeUnlikeRequest;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        String url = Flinnt.API_URL + Flinnt.URL_CONTENTS_LIKE_UNLIKE;

        return url;
    }

    public void sendLikeBookmarkRequest() {
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
        synchronized (ResendorVerified.class) {
            try {
                String url = buildURLString();

                if (null == mLikeUnlikeRequest) {
                    mLikeUnlikeRequest = new LikeUnlikeRequest();
                }


                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("LikeUnlike Request :\nUrl : " + url + "\nData : " + mLikeUnlikeRequest.getJSONString());

                JSONObject jsonObject = mLikeUnlikeRequest.getJSONObject();

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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("LikeUnlike Response :\n" + response.toString());

                if (mLikeUnlikeResponse.isSuccessResponse(response)) {


                    String likeUnlikeResponse = new String(response.toString());

                    JSONObject jsonData = mLikeUnlikeResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mLikeUnlikeResponse = gson.fromJson(likeUnlikeResponse, LikeUnlikeResponse.class);
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
                    LogWriter.write("LikeUnlike Error : " + error.getMessage());

                mLikeUnlikeResponse.parseErrorResponse(error);
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
            msg.obj = mLikeUnlikeResponse;
            mHandler.sendMessage(msg);
        }
    }
}
