package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentsEditRequest;
import com.edu.flinnt.protocol.ContentsEditResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 16/11/16.
 */

public class ContentsEditList {

    public static final String TAG = ContentsEditList.class.getSimpleName();
    public ContentsEditRequest mContentsEditRequest;
    public ContentsEditResponse mContentsEditResponse;
    public Handler mHandler = null;
    String searchString = "";
    String mCourseId;

    public ContentsEditList(Handler handler, String courseId) {
        mHandler = handler;
        mCourseId = courseId;
        getContentsListResponse();
    }

    public ContentsEditResponse getContentsListResponse() {
        if (mContentsEditResponse == null) {
            mContentsEditResponse = new ContentsEditResponse();
        }
        return mContentsEditResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENTS_EDIT_LIST;
    }


    public void sendContentsEditListRequest() {
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

                if (null == mContentsEditRequest) {
                    mContentsEditRequest=getContentsEditRequest();
                } else {
                    mContentsEditRequest.setCourse_id(mCourseId);
                    mContentsEditRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mContentsEditRequest.setMultiple_attachment(Flinnt.ENABLED);
                    mContentsEditRequest.setOffset(mContentsEditRequest.getOffset() + mContentsEditRequest.getMax());
                    mContentsEditRequest.setMax(10);
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentsEditList Request :\nUrl : " + url + "\nData : " + mContentsEditRequest.getJSONString());

                JSONObject jsonObject = mContentsEditRequest.getJSONObject();

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
                    LogWriter.write("ContentsEditList Response :\n" + response.toString());

                if (mContentsEditResponse.isSuccessResponse(response)) {

                    String ContentsEditResponse = new String(response.toString());

                    JSONObject jsonData = mContentsEditResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mContentsEditResponse = gson.fromJson(ContentsEditResponse, ContentsEditResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);


                        if (mContentsEditResponse.getData().getHasMore() > 0) {
                            sendRequest();
                        }
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("ContentsEditList Error : " + error.getMessage());

                mContentsEditResponse.parseErrorResponse(error);
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
            msg.obj = mContentsEditResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }


    public ContentsEditRequest getContentsEditRequest() {
        if (null == mContentsEditRequest) {
            mContentsEditRequest = new ContentsEditRequest();
            mContentsEditRequest.setCourse_id(mCourseId);
            mContentsEditRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mContentsEditRequest.setMultiple_attachment(Flinnt.ENABLED);
            mContentsEditRequest.setSearch(getSearchString());
            mContentsEditRequest.setOffset(0);
            mContentsEditRequest.setMax(0);
        }
        return mContentsEditRequest;
    }
}
