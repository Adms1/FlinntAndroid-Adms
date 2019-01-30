package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesRequest;
import com.edu.flinnt.protocol.SuggestedCoursesRequest;
import com.edu.flinnt.protocol.SuggestedCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SuggestedCourses {

    public static final String TAG = SuggestedCourses.class.getSimpleName();
    public static final int USERS_JOINED_COURSES = 1;
    public static final int COURSES_FROM_INSTITUTE = 2;
    public SuggestedCoursesResponse mSuggestedCoursesResponse = null;
    public SuggestedCoursesRequest mSuggestedCoursesRequest = null;
    public Handler mHandler = null;
    private int type = Flinnt.INVALID;
    private String courseId;

    public SuggestedCourses(Handler handler, String courseId, int type) {
        mHandler = handler;
        this.courseId = courseId;
        this.type = type;
        getSuggestedCoursesResponse();
    }

    public SuggestedCoursesResponse getSuggestedCoursesResponse() {
        if (mSuggestedCoursesResponse == null) {
            mSuggestedCoursesResponse = new SuggestedCoursesResponse();
        }
        return mSuggestedCoursesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        String params = "?" + BrowseCoursesRequest.FIELDS_KEY + "="
                + BrowsableCourse.ID_KEY + ","
                + BrowsableCourse.NAME_KEY + ","
                + BrowsableCourse.PICTURE_KEY + ","
                + BrowsableCourse.INSTITUTE_NAME_KEY + ","
                + BrowsableCourse.RATINGS_KEY + ","
                + BrowsableCourse.IS_FREE_KEY + ","
                + BrowsableCourse.DISCOUNT_APPLICABLE_BROWSE_KEY + ","
//                + BrowsableCourse.PRICE_BROWSE_KEY + ","
                + BrowsableCourse.PRICE_BUY_BROWSE_KEY + ","
                + BrowsableCourse.PRICE_KEY;


        if (type == USERS_JOINED_COURSES)       return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_JOINED + params;
        return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_MORE + params;
    }

    public void sendSuggestedCoursesRequest() {
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
        synchronized (SuggestedCourses.class) {
            try {
                String url = buildURLString();

                if (null == mSuggestedCoursesRequest) {
                    mSuggestedCoursesRequest = getSuggestedCoursesRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + mSuggestedCoursesRequest.getJSONString());

                JSONObject jsonObject = mSuggestedCoursesRequest.getJSONObject();

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
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());

                mSuggestedCoursesResponse.setType(type);

                if (mSuggestedCoursesResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mSuggestedCoursesResponse.getJSONData(response);
                    if (null != jsonData) {
                        mSuggestedCoursesResponse.parseJSONObject(jsonData);
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
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                mSuggestedCoursesResponse.parseErrorResponse(error);
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
            msg.obj = mSuggestedCoursesResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SuggestedCoursesRequest getSuggestedCoursesRequest() {
        if (null == mSuggestedCoursesRequest) {
            mSuggestedCoursesRequest = new SuggestedCoursesRequest();
            mSuggestedCoursesRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mSuggestedCoursesRequest.setCourseId(courseId);
            mSuggestedCoursesRequest.setMax(10);
        }
        return mSuggestedCoursesRequest;
    }
}
