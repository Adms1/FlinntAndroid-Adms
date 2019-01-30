package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddContentRequest;
import com.edu.flinnt.protocol.AddContentResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;


/**
 * Created by flinnt-android-2 on 7/4/17.
 */

public class AddContent {

    public static final String TAG = AddContent.class.getSimpleName();
    public AddContentResponse mAddContentResponse = null;
    public Handler mHandler = null;
    public AddContentRequest mAddContentRequest;
    private int mPostType = Flinnt.INVALID;

    public AddContent(Handler handler, AddContentRequest addPostRequest, int postType) {
        mHandler = handler;
        mAddContentRequest = addPostRequest;
        mPostType = postType;
        getResponse();
    }

    public AddContentResponse getResponse() {
        if (mAddContentResponse == null) {
            mAddContentResponse = new AddContentResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("AddContent response : " + mAddContentResponse.toString());
        return mAddContentResponse;
    }


    public String buildURLString() {

        switch (mPostType) {

            case Flinnt.CONTENT_ADD:
                return Flinnt.API_URL + Flinnt.URL_CONTENTS_ADD;

            case Flinnt.CONTENT_EDIT:
                return Flinnt.API_URL + Flinnt.URL_CONTENTS_EDIT;

            default:
                return "";
        }
    }

    public void sendAddContentRequest() {
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
        synchronized (AddContent.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("AddContent Request :\nUrl : " + url + "\nData : " + mAddContentRequest.getJSONString());

                JSONObject jsonObject = mAddContentRequest.getJSONObject();

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
                    LogWriter.write("AddContent response :\n" + response.toString());
                //Log.i("response", response.toString());
                mAddContentResponse = new Gson().fromJson(response.toString(), AddContentResponse.class);
                if (mAddContentResponse.getStatus() == 1) {

                    sendMesssageToGUI(Flinnt.SUCCESS);
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("AddContentError : " + error.getMessage());

                mAddContentResponse.parseErrorResponse(error);
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
            msg.obj = mAddContentResponse;
            mHandler.sendMessage(msg);
        }
    }

}
