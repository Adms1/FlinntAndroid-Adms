package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.BuyerListRequest;
import com.edu.flinnt.protocol.BuyerListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 18/4/17.
 */

public class BuyerList {
    public static final String TAG = BuyerList.class.getSimpleName();
    public static BuyerListResponse mBuyerListResponse = null;
    public BuyerListRequest mBuyerListRequest = null;
    public Handler mHandler = null;
    private String mCourseID = "";
    private int offset = 0;


    public BuyerList(Handler handler, String courseId) {
        mHandler = handler;
        mCourseID = courseId;
        getLastResponse();
    }

    public BuyerListResponse getLastResponse() {
        if (mBuyerListResponse == null) {
            mBuyerListResponse = new BuyerListResponse();
        }
        return mBuyerListResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_BUYER_LIST;
    }

    public void sendBuyerListRequest(BuyerListRequest postListRequest) {
        mBuyerListRequest = postListRequest;
        sendBuyerListRequest();
    }

    public void sendBuyerListRequest() {
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
        synchronized (BuyerList.class) {
            try {
                String url = buildURLString();
                mBuyerListRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mBuyerListRequest.setCourseID(mCourseID);

                mBuyerListRequest.setOffSet(offset);
                mBuyerListRequest.setMaxFetch(20);
                offset = offset + 20;
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("BuyerList Request :\nUrl : " + url + "\nData : " + mBuyerListRequest.getJSONString());

                JSONObject jsonObject = mBuyerListRequest.getJSONObject();

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
                    LogWriter.write("BuyerList response :\n" + response.toString());
                Log.i("response", response.toString());
                mBuyerListResponse = new Gson().fromJson(response.toString(), BuyerListResponse.class);
                if (mBuyerListResponse.getStatus() == 1) {
                    sendMesssageToGUI(Flinnt.SUCCESS);
                    if (mBuyerListResponse.getData().getHasMore() > 0) {
                        sendRequest();
                    }
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("BuyerList Error : " + error.getMessage());

                mBuyerListResponse.parseErrorResponse(error);
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
            msg.obj = mBuyerListResponse;
            mHandler.sendMessage(msg);
        }
    }
}
