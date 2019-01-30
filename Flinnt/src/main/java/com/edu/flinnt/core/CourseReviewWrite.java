package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseReviewWriteRequest;
import com.edu.flinnt.protocol.CourseReviewWriteResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseReviewWrite {

    public static final String TAG = CourseReviewWrite.class.getSimpleName();
    public CourseReviewWriteResponse mCourseReviewWriteResponse = null;
    public CourseReviewWriteRequest mCourseReviewWriteRequest = null;
    public Handler mHandler = null;
    private String courseId;
    private String ratings;
    private String review;

    public CourseReviewWrite(Handler handler, String courseId) {
        mHandler = handler;
        this.courseId = courseId;
        getLastCourseReviewListResponse();
    }

    public CourseReviewWriteResponse getLastCourseReviewListResponse() {
        if (mCourseReviewWriteResponse == null) {
            mCourseReviewWriteResponse = new CourseReviewWriteResponse();
        }
        return mCourseReviewWriteResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_REVIEW_WRITE;
    }

    public void sendCourseWriteRequest() {
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
        synchronized (CourseReviewWrite.class) {
            try {
                String url = buildURLString();

                if (null == mCourseReviewWriteRequest) {
                    mCourseReviewWriteRequest = getCourseReviewWriteRequest();
                }

                mCourseReviewWriteRequest.setRating(ratings);
                mCourseReviewWriteRequest.setReview(review);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mCourseReviewWriteRequest :\nUrl : " + url + "\nData : " + mCourseReviewWriteRequest.getJSONString());

                JSONObject jsonObject = mCourseReviewWriteRequest.getJSONObject();

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
                    LogWriter.write("CourseReviewWrite response :\n" + response.toString());

                if (mCourseReviewWriteResponse.isSuccessResponse(response)) {
                    JSONObject jsonData = mCourseReviewWriteResponse.getJSONData(response);
                    if (null != jsonData) {
                        mCourseReviewWriteResponse.parseJSONObject(jsonData);
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
                    LogWriter.write("CourseReviewWrite Error : " + error.getMessage());

                mCourseReviewWriteResponse.parseErrorResponse(error);
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
            msg.obj = mCourseReviewWriteResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public CourseReviewWriteRequest getCourseReviewWriteRequest() {
        if (null == mCourseReviewWriteRequest) {
            mCourseReviewWriteRequest = new CourseReviewWriteRequest();
            mCourseReviewWriteRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mCourseReviewWriteRequest.setCourseId(courseId);
        }
        return mCourseReviewWriteRequest;
    }
}
