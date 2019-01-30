package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PollVoteRequest;
import com.edu.flinnt.protocol.PollVoteResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 3/3/17.
 */

public class PollVote {

    public static final String TAG = PollVote.class.getSimpleName();
    public static PollVoteResponse mPollVoteResponse = null;
    public Handler mHandler = null;
    public PollVoteRequest mPollVoteRequest;


    public PollVote(Handler handler, PollVoteRequest pollVoteRequest) {
        mHandler = handler;
        mPollVoteRequest = pollVoteRequest;
        getLastResponse();

    }


    public static PollVoteResponse getLastResponse() {
        if (mPollVoteResponse == null) {
            mPollVoteResponse = new PollVoteResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("PollVote response : " + mPollVoteResponse.toString());
        return mPollVoteResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COMMUNICATION_POLL_VOTE;
    }

    public void sendPollVoteRequest() {
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
        synchronized (PollVote.class) {
            try {
                String url = buildURLString();
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PollVote Request :\nUrl : " + url + "\nData : " + mPollVoteRequest.getJSONString());

                JSONObject jsonObject = mPollVoteRequest.getJSONObject();
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
                    LogWriter.write("PollVote response :\n" + response.toString());

                if (mPollVoteResponse.isSuccessResponse(response)) {
                    String PollVoteResponse = new String(response.toString());
                    JSONObject jsonData = mPollVoteResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mPollVoteResponse = gson.fromJson(PollVoteResponse, PollVoteResponse.class);
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
                    LogWriter.write("PollVoteError : " + error.getMessage());

                mPollVoteResponse.parseErrorResponse(error);
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
            msg.obj = mPollVoteResponse;
            mHandler.sendMessage(msg);
        }
    }

}
