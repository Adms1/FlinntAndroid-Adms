package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddContentResponse;
import com.edu.flinnt.protocol.CopyContentRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 27/4/17.
 */

public class CopyContent {
    public static String DELETE = "delete";
    public static final String TAG = ContentsEditList.class.getSimpleName();
    public CopyContentRequest mCopyContentRequest;
    public AddContentResponse mAddContentResponse;
    public Handler mHandler = null;
    private String mCourseID = "";
    private String mContentID = "";
    private String mSectionID = "";


    public CopyContent(Handler handler, String courseId, String sectionId , String contentId) {
        mHandler = handler;
        mCourseID = courseId;
        mContentID = contentId;
        mSectionID = sectionId;
        getAddContentResponse();

    }

    public AddContentResponse getAddContentResponse() {
        if (mAddContentResponse == null) {
            mAddContentResponse = new AddContentResponse();
        }
        return mAddContentResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENTS_COPY;
    }


    public void sendCopyContent() {
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
        synchronized (MyCourses.class) {
            try {
                String url = buildURLString();
                mCopyContentRequest = new CopyContentRequest();
                mCopyContentRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mCopyContentRequest.setCourseID(mCourseID);
                mCopyContentRequest.setContentID(mContentID);
                mCopyContentRequest.setSectionID(mSectionID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CopyContent Request :\nUrl : " + url + "\nData : " + mCopyContentRequest.getJSONString());
                JSONObject jsonObject = mCopyContentRequest.getJSONObject();
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

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CopyContent Response :\n" + response.toString());
                if (mAddContentResponse.isSuccessResponse(response)) {
                    String AddContentResponse = new String(response.toString());
                    JSONObject jsonData = mAddContentResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mAddContentResponse = gson.fromJson(AddContentResponse, AddContentResponse.class);
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
                    LogWriter.write("CopyContent Error : " + error.getMessage());

                mAddContentResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
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
            msg.obj = mAddContentResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
}


