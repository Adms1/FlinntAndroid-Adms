package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseViewRequest;
import com.edu.flinnt.protocol.CourseViewResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseDescription {

    public static final String TAG = CourseDescription.class.getSimpleName();
    public CourseViewRequest mCourseViewRequest = null;
    public CourseViewResponse mCourseViewResponse = null;
    public Handler mHandler = null;

    private String courseId;
    private String courseHash;

    public CourseDescription(Handler handler, String id) {
        mHandler = handler;
        courseId = id;
        if (null == mCourseViewResponse) mCourseViewResponse = new CourseViewResponse();
    }
    public CourseDescription(Handler handler, String id , String hash) {
        mHandler = handler;
        courseId = id;
        courseHash =hash;
        if (null == mCourseViewResponse) mCourseViewResponse = new CourseViewResponse();
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW;
    }

    public void sendCourseDescriptionRequest() {
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
        synchronized (CourseDescription.class) {
            try {
                String url = buildURLString();
                if (null == mCourseViewRequest) {
                    mCourseViewRequest = getCourseViewRequest();
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription Request :\nUrl : " + url + "\nData : " + mCourseViewRequest.getJSONString());

                JSONObject jsonObject = mCourseViewRequest.getJSONObject();

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

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription response :\n" + response.toString());

                if (mCourseViewResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseViewResponse.getJSONData(response);
                    if (null != jsonData) {
                        mCourseViewResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
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
                    LogWriter.write("MyCourses Error : " + error.getMessage());

                mCourseViewResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
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
            msg.obj = mCourseViewResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public CourseViewRequest getCourseViewRequest() {
        if (null == mCourseViewRequest) {
            mCourseViewRequest = new CourseViewRequest();
            mCourseViewRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mCourseViewRequest.setCourseId(courseId);
            mCourseViewRequest.setCouserHash(courseHash);
            mCourseViewRequest.setPaidPromoContact(Flinnt.ENABLED);
        }
        return mCourseViewRequest;
    }
}
