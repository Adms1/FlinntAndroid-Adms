package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.QuizStartRequest;
import com.edu.flinnt.protocol.QuizStartResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class QuizStart {
    public static final String TAG = QuizStart.class.getSimpleName();
    public static QuizStartResponse mQuizStartResponse = null;
    public Handler mHandler = null;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";

    public QuizStart( Handler handler,String courseId, String quizId, String contentId ) {
        mHandler = handler;
        this.courseId = courseId;
        this.quizId = quizId;
        this.contentId = contentId;
        getLastResponse();
    }

    public QuizStartResponse getLastResponse() {
        mQuizStartResponse = new QuizStartResponse();
        return mQuizStartResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_QUIZ_START;
    }

    public void sendQuizStartRequest() {
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

                QuizStartRequest quizStartRequest = new QuizStartRequest();
                quizStartRequest.setUserID(Config.getStringValue(Config.USER_ID));
                quizStartRequest.setCourseId( courseId );
                quizStartRequest.setContentId( contentId );
                quizStartRequest.setQuizId(quizId);
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizStart Request :\nUrl : " + url + "\nData : " + quizStartRequest.getJSONString());
                JSONObject jsonObject = quizStartRequest.getJSONObject();
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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizStart Response : " + response.toString());

                if (mQuizStartResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mQuizStartResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mQuizStartResponse = gson.fromJson(response.toString(), QuizStartResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("QuizStart Error : " + error.getMessage());
                mQuizStartResponse.parseErrorResponse(error);
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
            msg.obj = mQuizStartResponse;
            mHandler.sendMessage(msg);
        }
    }
}
