package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentDeleteCommentRequest;
import com.edu.flinnt.protocol.ContentDeleteCommentResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentDeleteComment {
    public static final String TAG = ContentDeleteComment.class.getSimpleName();
    public static ContentDeleteCommentResponse mContentDeleteCommentResponse = null;
    public Handler mHandler = null;
    private String mCommentID = "";

    public ContentDeleteComment(Handler handler, String commentID) {
        mHandler = handler;
        mCommentID = commentID;
        getResponse();
    }

    static public ContentDeleteCommentResponse getResponse() {
        mContentDeleteCommentResponse = new ContentDeleteCommentResponse();
        return mContentDeleteCommentResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENT_DELETE_COMMENT;
    }

    public void sendDeleteCommentRequest() {
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
        synchronized (ContentDeleteComment.class) {
            try {
                String url = buildURLString();

                /**
                 PostDetailsRequest parameter are same...
                 so use as request for DeleteCommentRequest
                 */
                ContentDeleteCommentRequest contentDeleteCommentRequest = new ContentDeleteCommentRequest();

                contentDeleteCommentRequest.setUserID(Config.getStringValue(Config.USER_ID));
                contentDeleteCommentRequest.setCommentID(mCommentID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentDeleteComment Request :\nUrl : " + url + "\nData : " + contentDeleteCommentRequest.getJSONString());

                JSONObject jsonObject = contentDeleteCommentRequest.getJSONObject();

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
                    LogWriter.write("ContentDeleteComment Response :\n" + response.toString());
                if (mContentDeleteCommentResponse.isSuccessResponse(response)) {

                    String contentDeleteCommentResponse = new String(response.toString());

                    JSONObject jsonData = mContentDeleteCommentResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mContentDeleteCommentResponse = gson.fromJson(contentDeleteCommentResponse, ContentDeleteCommentResponse.class);
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
                    LogWriter.write("ContentDeleteComment Error :: " + error.getMessage());
                mContentDeleteCommentResponse.parseErrorResponse(error);
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
            msg.obj = mContentDeleteCommentResponse;
            mHandler.sendMessage(msg);
        }
    }
}