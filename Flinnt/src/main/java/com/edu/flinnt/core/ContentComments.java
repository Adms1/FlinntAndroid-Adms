package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentCommentsRequest;
import com.edu.flinnt.protocol.ContentCommentsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentComments {

    public static final String TAG = ContentComments.class.getSimpleName();
    public static ContentCommentsResponse mContentCommentsResponse = null;
    public ContentCommentsRequest mContentCommentsRequest = null;
    public Handler mHandler = null;
    private String mCourseID = "";
    private String mContentID = "";

    public ContentComments(Handler handler, String courseId, String contentID) {
        mHandler = handler;
        mCourseID = courseId;
        mContentID = contentID;
        getLastResponse();
    }

    public ContentCommentsResponse getLastResponse() {
        if (null == mContentCommentsResponse) {
            mContentCommentsResponse = new ContentCommentsResponse();
        }
        return mContentCommentsResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENTS_COMMETNS;
    }

    public void sendContentCommentsRequest(ContentCommentsRequest contentCommentsRequest) {
        mContentCommentsRequest = contentCommentsRequest;
        sendPostCommentsRequest();
    }

    public void sendPostCommentsRequest() {
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
        synchronized (ContentComments.class) {
            try {
                String url = buildURLString();

                if (null == mContentCommentsRequest) {
                    mContentCommentsRequest = new ContentCommentsRequest();
                    mContentCommentsResponse.getData().clearCommentList();
                }

                mContentCommentsRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mContentCommentsRequest.setCourseID(mCourseID);
                mContentCommentsRequest.setContentID(mContentID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentComments Request :\nUrl : " + url + "\nData : " + mContentCommentsRequest.getJSONString());

                JSONObject jsonObject = mContentCommentsRequest.getJSONObject();

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
                    LogWriter.write("ContentComments Response : " + response.toString());

                if (mContentCommentsResponse.isSuccessResponse(response)) {

                    String contentCommentResponse = new String(response.toString());

                    JSONObject jsonData = mContentCommentsResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mContentCommentsResponse = gson.fromJson(contentCommentResponse, ContentCommentsResponse.class);
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
                    LogWriter.write("PostComments Error : " + error.getMessage());
                mContentCommentsResponse.parseErrorResponse(error);
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
            msg.obj = mContentCommentsResponse;
            mHandler.sendMessage(msg);
        }
    }
}