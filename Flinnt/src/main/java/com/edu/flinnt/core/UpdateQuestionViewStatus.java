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
import com.edu.flinnt.protocol.UpdateQuestionViewStatusRequest;
import com.edu.flinnt.protocol.UpdateQuestionViewStatusResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class UpdateQuestionViewStatus {
    public static final String TAG = UpdateQuestionViewStatus.class.getSimpleName();
    public static UpdateQuestionViewStatusResponse mUpdateQuestionViewStatusResponse = null;
    public Handler mHandler = null;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizQueId = "";


    public UpdateQuestionViewStatus(Handler mHandler, String courseId, String quizId, String contentId, String quizQueId) {
        this.mHandler = mHandler;
        this.courseId = courseId;
        this.quizId = quizId;
        this.contentId = contentId;
        this.quizQueId = quizQueId;
        getLastResponse();

    }

    public UpdateQuestionViewStatusResponse getLastResponse() {
        mUpdateQuestionViewStatusResponse = new UpdateQuestionViewStatusResponse();
        return mUpdateQuestionViewStatusResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_QUIZ_VIEW_STATUS_UPDATE;
    }

    public void sendUpdateViewStatusRequest() {
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
                UpdateQuestionViewStatusRequest mUpdateQuestionViewStatusRequest = new UpdateQuestionViewStatusRequest();
                mUpdateQuestionViewStatusRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mUpdateQuestionViewStatusRequest.setCourseId( courseId );
                mUpdateQuestionViewStatusRequest.setContentId( contentId );
                mUpdateQuestionViewStatusRequest.setQuizId(quizId);
                mUpdateQuestionViewStatusRequest.setQuizQueId( quizQueId );
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UpdateQuestionViewStatus Request :\nUrl : " + url + "\nData : " + mUpdateQuestionViewStatusRequest.getJSONString());
                JSONObject jsonObject = mUpdateQuestionViewStatusRequest.getJSONObject();
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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UpdateQuestionViewStatus Response : " + response.toString());

                if (mUpdateQuestionViewStatusResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mUpdateQuestionViewStatusResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mUpdateQuestionViewStatusResponse = gson.fromJson(response.toString(), UpdateQuestionViewStatusResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("UpdateQuestionViewStatus Error : " + error.getMessage());
                mUpdateQuestionViewStatusResponse.parseErrorResponse(error);
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
            msg.obj = mUpdateQuestionViewStatusResponse;
            mHandler.sendMessage(msg);
        }
    }
}
