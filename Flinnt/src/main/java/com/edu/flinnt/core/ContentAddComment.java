package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentAddCommentRequest;
import com.edu.flinnt.protocol.ContentAddCommentResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentAddComment {

    public static final String TAG = ContentAddComment.class.getSimpleName();
    public static ContentAddCommentResponse mContentAddCommentResponse = null;
    public Handler mHandler = null;
    public String mCommentText = "", mContentID = "", mCourseID = "";

    public ContentAddComment(Handler handler, String courseID, String contentID, String comment) {
        mHandler = handler;
        mCourseID = courseID;
        mContentID = contentID;
        mCommentText = comment;
        getLastResponse();
    }

    public static ContentAddCommentResponse getLastResponse() {
        if (mContentAddCommentResponse == null) {
            mContentAddCommentResponse = new ContentAddCommentResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("ContentAddComment response : " + mContentAddCommentResponse.toString());
        return mContentAddCommentResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENT_ADD_COMMENT;
    }

    public void sendAddCommentRequest() {
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
        synchronized (ContentAddComment.class) {
            try {
                String url = buildURLString();

                ContentAddCommentRequest contentAddCommentRequest = new ContentAddCommentRequest();

                contentAddCommentRequest.setUserID(Config.getStringValue(Config.USER_ID));
                contentAddCommentRequest.setCourseID(mCourseID);
                contentAddCommentRequest.setContentID(mContentID);
                contentAddCommentRequest.setComment(mCommentText);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentAddComment Request :\nUrl : " + url + "\nData : " + contentAddCommentRequest.getJSONString());

                JSONObject jsonObject = contentAddCommentRequest.getJSONObject();

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
                    LogWriter.write("ContentAddComment Response :\n" + response.toString());

                if (mContentAddCommentResponse.isSuccessResponse(response)) {

                    String contentAddCommentResponse = new String(response.toString());

                    JSONObject jsonData = mContentAddCommentResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mContentAddCommentResponse = gson.fromJson(contentAddCommentResponse, ContentAddCommentResponse.class);
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
                    LogWriter.write("ContentAddComment Error : " + error.getMessage());

                mContentAddCommentResponse.parseErrorResponse(error);
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
            msg.obj = mContentAddCommentResponse;
            mHandler.sendMessage(msg);
        }
    }

}