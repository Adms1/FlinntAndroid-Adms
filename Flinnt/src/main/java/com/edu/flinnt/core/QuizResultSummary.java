package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.QuizResultSummaryResponse;
import com.edu.flinnt.protocol.QuizStatusRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class QuizResultSummary {
    public static final String TAG = QuizResultSummary.class.getSimpleName();
    public static QuizResultSummaryResponse mQuizResultSummaryResponse = null;
    public Handler mHandler = null;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";

    public QuizResultSummary(Handler handler, String courseId, String quizId, String contentId ) {
        mHandler = handler;
        this.courseId = courseId;
        this.quizId = quizId;
        this.contentId = contentId;
        getLastResponse();
    }

    public QuizResultSummaryResponse getLastResponse() {
        mQuizResultSummaryResponse = new QuizResultSummaryResponse();
        return mQuizResultSummaryResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_GET_RESULT_SUMMARY;
    }

    public void sendQuizResultSummaryRequest() {
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
        synchronized (QuizAnswer.class) {
            try {
                String url = buildURLString();

                QuizStatusRequest quizStatusRequest = new QuizStatusRequest();
                quizStatusRequest.setUserID(Config.getStringValue(Config.USER_ID));
                quizStatusRequest.setCourseId( courseId );
                quizStatusRequest.setContentId( contentId );
                quizStatusRequest.setQuizId(quizId);
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizResultSummary Request :\nUrl : " + url + "\nData : " + quizStatusRequest.getJSONString());
                JSONObject jsonObject = quizStatusRequest.getJSONObject();
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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizResultSummary Response : " + response.toString());

                if (mQuizResultSummaryResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mQuizResultSummaryResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mQuizResultSummaryResponse = gson.fromJson(response.toString(), QuizResultSummaryResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("QuizResultSummary Error : " + error.getMessage());
                mQuizResultSummaryResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if( null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mQuizResultSummaryResponse;
            mHandler.sendMessage(msg);
        }
    }
}
