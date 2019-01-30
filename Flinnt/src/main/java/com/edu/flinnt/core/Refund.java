package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.RefundRequest;
import com.edu.flinnt.protocol.RefundResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 22/5/17.
 */

public class Refund {
    public static final String TAG = Refund.class.getSimpleName();
    public RefundResponse mRefundResponse = null;
    public Handler mHandler = null;
    public RefundRequest mRefundRequest;

    public Refund(Handler handler, RefundRequest mRefundRequest) {
        mHandler = handler;
        this.mRefundRequest = mRefundRequest;
        getResponse();
    }

    public RefundResponse getResponse() {
        if (mRefundResponse == null) {
            mRefundResponse = new RefundResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("Refund response : " + mRefundResponse.toString());
        return mRefundResponse;
    }


    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_REFUND;
    }

    public void sendRefundRequest() {
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
        synchronized (Refund.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("Refund Request :\nUrl : " + url + "\nData : " + mRefundRequest.getJSONString());

                JSONObject jsonObject = mRefundRequest.getJSONObject();
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
                    LogWriter.write("Refund response :\n" + response.toString());
                //Log.i("response", response.toString());
                mRefundResponse = new Gson().fromJson(response.toString(), RefundResponse.class);
                if (mRefundResponse.getStatus() == 1) {
                    sendMesssageToGUI(Flinnt.SUCCESS);
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("RefundError : " + error.getMessage());
                mRefundResponse.parseErrorResponse(error);
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
            msg.obj = mRefundResponse;
            mHandler.sendMessage(msg);
        }
    }
}
