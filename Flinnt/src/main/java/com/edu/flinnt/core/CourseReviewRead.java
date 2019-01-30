package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseReviewReadRequest;
import com.edu.flinnt.protocol.CourseReviewReadResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 3/9/16.
 */
public class CourseReviewRead {

    public static final String TAG = CourseReviewRead.class.getSimpleName();
    public CourseReviewReadResponse mCourseReviewReadResponse = null;
    public CourseReviewReadRequest mCourseReviewReadRequest = null;
    public Handler mHandler = null;
    private String courseId;


    public CourseReviewRead(Handler handler, String courseId) {
        mHandler = handler;
        this.courseId = courseId;
        getLastCourseReviewListResponse();
    }

    public CourseReviewReadResponse getLastCourseReviewListResponse() {
        if (mCourseReviewReadResponse == null) {
            mCourseReviewReadResponse = new CourseReviewReadResponse();
        }
        return mCourseReviewReadResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_REVIEW_READ;
    }

    public void sendCourseReadRequest() {
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
        synchronized (CourseReviewRead.class) {
            try {
                String url = buildURLString();

                if (null == mCourseReviewReadRequest) {
                    mCourseReviewReadRequest = getCourseReviewReadRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseReviewReadRequest :\nUrl : " + url + "\nData : " + mCourseReviewReadRequest.getJSONString());

                JSONObject jsonObject = mCourseReviewReadRequest.getJSONObject();

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

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseReviewRead response :\n" + response.toString());


                if (mCourseReviewReadResponse.isSuccessResponse(response)) {

                    String courseReviewResponse = new String(response.toString());

                    JSONObject jsonData = mCourseReviewReadResponse.getJSONData(response);


                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mCourseReviewReadResponse = gson.fromJson(courseReviewResponse, CourseReviewReadResponse.class);
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
                    LogWriter.write("CourseReviewRead Error : " + error.getMessage());

                mCourseReviewReadResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }


    /**
     * Sends response to handlers
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mCourseReviewReadResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }



    public CourseReviewReadRequest getCourseReviewReadRequest() {
        if (null == mCourseReviewReadRequest) {
            mCourseReviewReadRequest = new CourseReviewReadRequest();
            mCourseReviewReadRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mCourseReviewReadRequest.setCourseId(courseId);
        }
        return mCourseReviewReadRequest;
    }
}
