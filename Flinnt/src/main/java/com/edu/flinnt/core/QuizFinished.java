package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.QuizFinishResponse;
import com.edu.flinnt.protocol.QuizFinishRequest;
import com.edu.flinnt.protocol.QuizStartRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class QuizFinished {
    public static final String TAG = QuizFinished.class.getSimpleName();
    public static QuizFinishResponse mQuizFinishResponse = null;
    public Handler mHandler = null;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";

    public QuizFinished(Handler handler, String courseId, String quizId, String contentId ) {
        mHandler = handler;
        this.courseId = courseId;
        this.quizId = quizId;
        this.contentId = contentId;
        getLastResponse();
    }

    public QuizFinishResponse getLastResponse() {
        mQuizFinishResponse = new QuizFinishResponse();
        return mQuizFinishResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_FINISH_QUIZ;
    }

    public void sendQuizFinishRequest() {
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

                QuizFinishRequest quizFinishRequest = new QuizFinishRequest();
                quizFinishRequest.setUserID(Config.getStringValue(Config.USER_ID));
                quizFinishRequest.setCourseId( courseId );
                quizFinishRequest.setContentId( contentId );
                quizFinishRequest.setQuizId(quizId);
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizFinished Request :\nUrl : " + url + "\nData : " + quizFinishRequest.getJSONString());
                JSONObject jsonObject = quizFinishRequest.getJSONObject();
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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizFinished Response : " + response.toString());

                if (mQuizFinishResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mQuizFinishResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mQuizFinishResponse = gson.fromJson(response.toString(), QuizFinishResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("QuizFinished Error : " + error.getMessage());
                mQuizFinishResponse.parseErrorResponse(error);
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
            msg.obj = mQuizFinishResponse;
            mHandler.sendMessage(msg);
        }
    }
}
