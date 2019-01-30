package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseSendRequest;
import com.edu.flinnt.protocol.CourseSendResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 21/10/16.
 */
public class CourseRequest {

    public static final String TAG = JoinCourse.class.getSimpleName();
    public static CourseSendResponse mCourseSendResponse = null;
    public Handler mHandler = null;
    private String courseId;
    public String notes = "";

    public CourseRequest(Handler handler,String courseId, String note) {
        mHandler = handler;
        this.courseId = courseId;
        this.notes = note;
        getLastResponse();
    }

    public static CourseSendResponse getLastResponse() {
        if (mCourseSendResponse == null) {
            mCourseSendResponse = new CourseSendResponse();
            //mJoinCourseResponse.parseJSONString( Config.getStringValue(Config.LAST_JoinCourse_RESPONSE) );
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("CourseSend Response : " + mCourseSendResponse.toString());
        return mCourseSendResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_INSTITUTION_COURSES_REQUEST_KEY;
    }

    public void sendCourseSendRequest() {
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
        synchronized (CourseRequest.class) {
            try {
                String url = buildURLString();

                CourseSendRequest mCourseSendRequest = new CourseSendRequest();

                mCourseSendRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mCourseSendRequest.setCourseID(courseId);
                mCourseSendRequest.setNotes(notes);

                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CourseSend Request :\nUrl : " + url + "\nData : " + mCourseSendRequest.getJSONString());

                JSONObject jsonObject = mCourseSendRequest.getJSONObject();

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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CourseSend response :\n" + response.toString());

                if (mCourseSendResponse.isSuccessResponse(response)) {

                    String CourseResponse = new String(response.toString());
                    JSONObject jsonData = mCourseSendResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mCourseSendResponse = gson.fromJson(CourseResponse, CourseSendResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("CourseSend Error : " + error.getMessage());

                mCourseSendResponse.parseErrorResponse(error);
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
            msg.obj = mCourseSendResponse;
            mHandler.sendMessage(msg);
        }
    }

}
