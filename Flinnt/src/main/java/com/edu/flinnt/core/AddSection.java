package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddSectionRequest;
import com.edu.flinnt.protocol.AddSectionResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class AddSection {

    public static final String TAG = AddSection.class.getSimpleName();
    public AddSectionResponse mAddSectionResponse = null;
    public Handler mHandler = null;
    public AddSectionRequest mAddSectionRequest;
    private int mPostType = Flinnt.INVALID;

    public AddSection(Handler handler, AddSectionRequest addPostRequest, int postType) {
        mHandler = handler;
        mAddSectionRequest = addPostRequest;
        mPostType = postType;
        getResponse();
    }

    public AddSectionResponse getResponse() {
        if (mAddSectionResponse == null) {
            mAddSectionResponse = new AddSectionResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("AddSection response : " + mAddSectionResponse.toString());
        return mAddSectionResponse;
    }


    public String buildURLString() {

        switch (mPostType) {

            case Flinnt.SECTION_ADD:
                return Flinnt.API_URL + Flinnt.URL_SECTIONS_ADD;

            case Flinnt.SECTION_EDIT:
                return Flinnt.API_URL + Flinnt.URL_SECTIONS_EDIT;

            default:
                return "";
        }
    }

    public void sendAddSectionRequest() {
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
        synchronized (AddSection.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("AddSection Request :\nUrl : " + url + "\nData : " + mAddSectionRequest.getJSONString());

                JSONObject jsonObject = mAddSectionRequest.getJSONObject();

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
                    LogWriter.write("AddSection response :\n" + response.toString());
                Log.i("response", response.toString());
                mAddSectionResponse = new Gson().fromJson(response.toString(), AddSectionResponse.class);
                if (mAddSectionResponse.getStatus() == 1) {
                    sendMesssageToGUI(Flinnt.SUCCESS);
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("AddSectionError : " + error.getMessage());

                mAddSectionResponse.parseErrorResponse(error);
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
            msg.obj = mAddSectionResponse;
            mHandler.sendMessage(msg);
        }
    }
}
