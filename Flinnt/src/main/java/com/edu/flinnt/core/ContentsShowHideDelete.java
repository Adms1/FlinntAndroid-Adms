package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentsDeleteResponse;
import com.edu.flinnt.protocol.ContentsShowHideDeleteRequest;
import com.edu.flinnt.protocol.ContentsShowHideResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 17/11/16.
 */

public class ContentsShowHideDelete {

    public static String DELETE = "delete";
    public static final String TAG = ContentsEditList.class.getSimpleName();
    public ContentsShowHideDeleteRequest mContentsShowHideDeleteRequest;
    public ContentsShowHideResponse mContentsShowHideResponse;
    public ContentsDeleteResponse mContentsDeleteResponse;
    public Handler mHandler = null;
    private String mCourseID = "";
    private String mContentID = "";
    private String mSectionID = "";
    private String mVisibilityDeleteFlag = "";
    private String Url = "";


    public ContentsShowHideDelete(Handler handler, String courseId, String contentId, String sectionId, String flag) {
        mHandler = handler;
        mCourseID = courseId;
        mContentID = contentId;
        mSectionID = sectionId;
        mVisibilityDeleteFlag = flag;
        if (mVisibilityDeleteFlag.equals(DELETE)) {
            getContentsDeleteResponse();
        } else {
            getContentsShowHideDeleteResponse();
        }

    }


    public ContentsShowHideResponse getContentsShowHideDeleteResponse() {
        if (mContentsShowHideResponse == null) {
            mContentsShowHideResponse = new ContentsShowHideResponse();
        }
        return mContentsShowHideResponse;
    }

    public ContentsDeleteResponse getContentsDeleteResponse() {
        if (mContentsDeleteResponse == null) {
            mContentsDeleteResponse = new ContentsDeleteResponse();
        }
        return mContentsDeleteResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        if (mVisibilityDeleteFlag.equals(DELETE)) {
            Url = Flinnt.API_URL + Flinnt.URL_CONTENTS_DELETE;
        } else {
            Url = Flinnt.API_URL + Flinnt.URL_CONTENTS_UPDATE_VISIBILITY;
        }
        return Url;
    }


    public void sendContentsShowHideDelete() {
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
                mContentsShowHideDeleteRequest = new ContentsShowHideDeleteRequest();
                mContentsShowHideDeleteRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mContentsShowHideDeleteRequest.setCourseId(mCourseID);
                mContentsShowHideDeleteRequest.setContentId(mContentID);
                mContentsShowHideDeleteRequest.setSectionId(mSectionID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentsShowHideDelete Request :\nUrl : " + url + "\nData : " + mContentsShowHideDeleteRequest.getJSONString());
                JSONObject jsonObject = mContentsShowHideDeleteRequest.getJSONObject();
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
                    if (mVisibilityDeleteFlag.equals(DELETE)) {
                        LogWriter.write("ContentsDelete Response :\n" + response.toString());
                        if (mContentsDeleteResponse.isSuccessResponse(response)) {
                            String ContentsDeleteResponse = new String(response.toString());
                            JSONObject jsonData = mContentsDeleteResponse.getJSONData(response);
                            if (null != jsonData) {
                                Gson gson = new Gson();
                                mContentsDeleteResponse = gson.fromJson(ContentsDeleteResponse, ContentsDeleteResponse.class);
                                sendMesssageToGUI(Flinnt.SUCCESS);
                            } else {
                                sendMesssageToGUI(Flinnt.FAILURE);
                            }
                        }
                    } else {
                        LogWriter.write("ContentsShowHide Response :\n" + response.toString());
                        if (mContentsShowHideResponse.isSuccessResponse(response)) {
                            String ContentsShowHideResponse = new String(response.toString());
                            JSONObject jsonData = mContentsShowHideResponse.getJSONData(response);
                            if (null != jsonData) {
                                Gson gson = new Gson();
                                mContentsShowHideResponse = gson.fromJson(ContentsShowHideResponse, ContentsShowHideResponse.class);
                                sendMesssageToGUI(Flinnt.SUCCESS);
                            } else {
                                sendMesssageToGUI(Flinnt.FAILURE);
                            }
                        }
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("ContentsShowHideDelete Error : " + error.getMessage());


                if (mVisibilityDeleteFlag.equals(DELETE)) {
                    mContentsDeleteResponse.parseErrorResponse(error);
                    sendMesssageToGUI(Flinnt.FAILURE);
                } else {
                    mContentsShowHideResponse.parseErrorResponse(error);
                    sendMesssageToGUI(Flinnt.FAILURE);
                }
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
            if (mVisibilityDeleteFlag.equals(DELETE)) {
                msg.obj = mContentsDeleteResponse;
            } else {
                msg.obj = mContentsShowHideResponse;
            }
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
}
