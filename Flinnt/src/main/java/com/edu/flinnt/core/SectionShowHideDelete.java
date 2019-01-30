package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SectionDeleteResponse;
import com.edu.flinnt.protocol.SectionShowHideDeleteRequest;
import com.edu.flinnt.protocol.SectionShowHideResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 18/11/16.
 */

public class SectionShowHideDelete {
    public static String DELETE = "delete";
    public static final String TAG = ContentsEditList.class.getSimpleName();
    public SectionShowHideDeleteRequest mSectionShowHideDeleteRequest;
    public SectionShowHideResponse mSectionShowHideResponse;
    public SectionDeleteResponse mSectionDeleteResponse;
    public Handler mHandler = null;
    private String mCourseID = "";
    private String mSectionID = "";
    private String mVisibilityDeleteFlag = "";
    private String Url = "";


    public SectionShowHideDelete(Handler handler, String courseId, String sectionId, String flag) {
        mHandler = handler;
        mCourseID = courseId;
        mSectionID = sectionId;
        mVisibilityDeleteFlag = flag;
        if (mVisibilityDeleteFlag.equals(DELETE)) {
            getSectionDeleteResponse();
        } else {
            getContentsShowHideDeleteResponse();
        }

    }


    public SectionShowHideResponse getContentsShowHideDeleteResponse() {
        if (mSectionShowHideResponse == null) {
            mSectionShowHideResponse = new SectionShowHideResponse();
        }
        return mSectionShowHideResponse;
    }

    public SectionDeleteResponse getSectionDeleteResponse() {
        if (mSectionDeleteResponse == null) {
            mSectionDeleteResponse = new SectionDeleteResponse();
        }
        return mSectionDeleteResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        if (mVisibilityDeleteFlag.equals(DELETE)) {
            Url = Flinnt.API_URL + Flinnt.URL_SECTION_DELETE;
        } else {
            Url = Flinnt.API_URL + Flinnt.URL_SECTION_UPDATE_VISIBILITY;
        }
        return Url;
    }


    public void sendSectionShowHideDelete() {
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
                mSectionShowHideDeleteRequest = new SectionShowHideDeleteRequest();
                mSectionShowHideDeleteRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mSectionShowHideDeleteRequest.setCourseId(mCourseID);
                mSectionShowHideDeleteRequest.setSectionId(mSectionID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SectionShowHideDelete Request :\nUrl : " + url + "\nData : " + mSectionShowHideDeleteRequest.getJSONString());
                JSONObject jsonObject = mSectionShowHideDeleteRequest.getJSONObject();
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
                        LogWriter.write("SectionDelete Response :\n" + response.toString());
                        if (mSectionDeleteResponse.isSuccessResponse(response)) {
                            String SectionDeleteResponse = new String(response.toString());
                            JSONObject jsonData = mSectionDeleteResponse.getJSONData(response);
                            if (null != jsonData) {
                                Gson gson = new Gson();
                                mSectionDeleteResponse = gson.fromJson(SectionDeleteResponse, SectionDeleteResponse.class);
                                sendMesssageToGUI(Flinnt.SUCCESS);
                            } else {
                                sendMesssageToGUI(Flinnt.FAILURE);
                            }
                        }
                    } else {
                        LogWriter.write("SectionShowHide Response :\n" + response.toString());
                        if (mSectionShowHideResponse.isSuccessResponse(response)) {
                            String SectionShowHideResponse = new String(response.toString());
                            JSONObject jsonData = mSectionShowHideResponse.getJSONData(response);
                            if (null != jsonData) {
                                Gson gson = new Gson();
                                mSectionShowHideResponse = gson.fromJson(SectionShowHideResponse, SectionShowHideResponse.class);
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
                    LogWriter.write("SectionShowHideDelete Error : " + error.getMessage());
                if (mVisibilityDeleteFlag.equals(DELETE)) {
                    mSectionDeleteResponse.parseErrorResponse(error);
                    sendMesssageToGUI(Flinnt.FAILURE);
                } else {
                    mSectionShowHideResponse.parseErrorResponse(error);
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
                msg.obj = mSectionDeleteResponse;
            } else {
                msg.obj = mSectionShowHideResponse;
            }
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
}
