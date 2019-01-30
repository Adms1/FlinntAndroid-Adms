package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.UpdateAnswerRequest;
import com.edu.flinnt.protocol.UpdateAnswerResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class UpdateAnswer {
    public static final String TAG = UpdateAnswer.class.getSimpleName();
    public static UpdateAnswerResponse mUpdateAnswerResponse = null;
    public Handler mHandler = null;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizQueId = "";
    private String answerId = "";
    private String reactionTime = "";
    private String markForReview = "";
    private String terminate = "0";

    public UpdateAnswer(Handler mHandler, String courseId, String quizId, String contentId, String quizQueId, String answerId, String reactionTime, String markForReview) {
        this.mHandler = mHandler;
        this.courseId = courseId;
        this.quizId = quizId;
        this.contentId = contentId;
        this.quizQueId = quizQueId;
        this.answerId = answerId;
        this.reactionTime = reactionTime;
        this.markForReview = markForReview;
        getLastResponse();

    }

    public UpdateAnswerResponse getLastResponse() {
        mUpdateAnswerResponse = new UpdateAnswerResponse();
        return mUpdateAnswerResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_QUIZ_UPDATE_ANSWER;
    }

    public void sendUpdateAnswerRequest() {
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
                UpdateAnswerRequest mUpdateAnswerRequest = new UpdateAnswerRequest();
                mUpdateAnswerRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mUpdateAnswerRequest.setCourseId(courseId);
                mUpdateAnswerRequest.setContentId(contentId);
                mUpdateAnswerRequest.setQuizId(quizId);
                mUpdateAnswerRequest.setMarkForReview(markForReview);
                if (answerId == null)
                    mUpdateAnswerRequest.setAnswerId("0");
                else
                    mUpdateAnswerRequest.setAnswerId(answerId);
                mUpdateAnswerRequest.setTerminate(terminate);
                mUpdateAnswerRequest.setQuizQueId(quizQueId);
                mUpdateAnswerRequest.setReactionTime(reactionTime);
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("UpdateAnswer Request :\nUrl : " + url + "\nData : " + mUpdateAnswerRequest.getJSONString());
                JSONObject jsonObject = mUpdateAnswerRequest.getJSONObject();
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

                Log.d("oncreate",response.toString());
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("UpdateAnswer Response : " + response.toString());

                if (mUpdateAnswerResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mUpdateAnswerResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mUpdateAnswerResponse = gson.fromJson(response.toString(), UpdateAnswerResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("UpdateAnswer Error : " + error.getMessage());
                mUpdateAnswerResponse.parseErrorResponse(error);
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
            msg.obj = mUpdateAnswerResponse;
            mHandler.sendMessage(msg);
        }
    }


    public String getTerminate() {
        return terminate;
    }

    public void setTerminate(String terminate) {
        this.terminate = terminate;
    }


}
