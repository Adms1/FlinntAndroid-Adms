package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.RefundReasonOptionRequest;
import com.edu.flinnt.protocol.RefundReasonOptionResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 22/5/17.
 */

public class RefundReasonOption {

    public static final String TAG = RefundReasonOption.class.getSimpleName();
    public RefundReasonOptionResponse mRefundReasonOptionResponse = null;
    public Handler mHandler = null;
    public RefundReasonOptionRequest mRefundReasonOptionRequest;

    public RefundReasonOption(Handler handler, RefundReasonOptionRequest mRefundReasonOptionRequest) {
        mHandler = handler;
        this.mRefundReasonOptionRequest = mRefundReasonOptionRequest;
        getResponse();
    }

    public RefundReasonOptionResponse getResponse() {
        if (mRefundReasonOptionResponse == null) {
            mRefundReasonOptionResponse = new RefundReasonOptionResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("RefundReasonOption response : " + mRefundReasonOptionResponse.toString());
        return mRefundReasonOptionResponse;
    }


    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_REFUND_REASONS;
    }

    public void sendRefundReasonOptionRequest() {
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


    public void sendRequest() {
        synchronized (RefundReasonOption.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("RefundReasonOption Request :\nUrl : " + url + "\nData : " + mRefundReasonOptionRequest.getJSONString());

                JSONObject jsonObject = mRefundReasonOptionRequest.getJSONObject();
                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }


    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("RefundReasonOption response :\n" + response.toString());
                //Log.i("response", response.toString());
                mRefundReasonOptionResponse = new Gson().fromJson(response.toString(), RefundReasonOptionResponse.class);
                if (mRefundReasonOptionResponse.getStatus() == 1) {
                    sendMesssageToGUI(Flinnt.SUCCESS);
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("RefundReasonOptionError : " + error.getMessage());
                mRefundReasonOptionResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mRefundReasonOptionResponse;
            mHandler.sendMessage(msg);
        }
    }
}
