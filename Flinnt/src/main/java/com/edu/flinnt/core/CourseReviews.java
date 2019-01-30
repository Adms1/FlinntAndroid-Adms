package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseReviewListRequest;
import com.edu.flinnt.protocol.CourseReviewListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseReviews {

    public static final String TAG = CourseReviews.class.getSimpleName();
    public CourseReviewListResponse mCourseReviewListResponse = null;
    public CourseReviewListRequest mCourseReviewListRequest = null;
    public Handler mHandler = null;
    private String courseId;
    private int max;

    public CourseReviews(Handler handler, String courseId, int max) {
        mHandler = handler;
        this.courseId = courseId;
        this.max = max;
        getLastCourseReviewListResponse();
    }

    public CourseReviewListResponse getLastCourseReviewListResponse() {
        if (mCourseReviewListResponse == null) {
            mCourseReviewListResponse = new CourseReviewListResponse();
        }
        return mCourseReviewListResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_REVIEWS_LIST;
    }

    public void sendCourseReviewListRequest() {
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
        synchronized (CourseReviews.class) {
            try {
                String url = buildURLString();

                if (null == mCourseReviewListRequest) {
                    mCourseReviewListRequest = getCourseReviewListRequest();
                } else {
                    // Reset offset to new request - New offset = old offset + max
                    mCourseReviewListRequest.setCourseId(courseId);
                    mCourseReviewListRequest.setOffset(mCourseReviewListRequest.getOffset() + mCourseReviewListRequest.getMax());
                    mCourseReviewListRequest.setMax(10);
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseReviews Request :\nUrl : " + url + "\nData : " + mCourseReviewListRequest.getJSONString());

                JSONObject jsonObject = mCourseReviewListRequest.getJSONObject();

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
                    LogWriter.write("CourseReviews response :\n" + response.toString());

                if (mCourseReviewListResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseReviewListResponse.getJSONData(response);
                    if (null != jsonData) {
                        mCourseReviewListResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                       /* if (mCourseReviewListResponse.getHasMore() > 0) {
                            if (max >= 10) sendRequest();
                        }*/

                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("CourseReviews Error : " + error.getMessage());

                mCourseReviewListResponse.parseErrorResponse(error);
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
            msg.obj = mCourseReviewListResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public CourseReviewListRequest getCourseReviewListRequest() {
        if (null == mCourseReviewListRequest) {
            mCourseReviewListRequest = new CourseReviewListRequest();
            mCourseReviewListRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mCourseReviewListRequest.setCourseId(courseId);
            mCourseReviewListRequest.setOffset(0);
            mCourseReviewListRequest.setMax(max);
        }
        return mCourseReviewListRequest;
    }
}
