package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.store.BrowseCoursesNew;
import com.edu.flinnt.protocol.PromoCourseRequest;
import com.edu.flinnt.protocol.PromoCourseResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 9/1/17.
 */
public class PromoCourse {
    public static final String TAG = PromoCourse.class.getSimpleName();
    public PromoCourseResponse mPromoCourseResponse = null;
    public PromoCourseRequest mPromoCourseRequest = null;
    public Handler mHandler = null;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    private int source = 3;

    public PromoCourse(Handler handler) {
        mHandler = handler;
        getLastBrowseCoursesResponse();
    }

    public PromoCourseResponse getLastBrowseCoursesResponse() {
        if (mPromoCourseResponse == null) {
            mPromoCourseResponse = new PromoCourseResponse();
        }
        return mPromoCourseResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_PROMO_COURSE;

    }

    public void sendPromoCourseRequest() {
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
        synchronized (BrowseCoursesNew.class) {
            try {
                String url = buildURLString();

                if (null == mPromoCourseRequest) {
                    mPromoCourseRequest = getBrowseCoursesRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourses Request :\nUrl : " + url + "\nData : " + mPromoCourseRequest.getJSONString());

                JSONObject jsonObject = mPromoCourseRequest.getJSONObject();
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
                    LogWriter.write("mBrowseCourses Response :\n" + response.toString());
                if(source == PromoCourseRequest.PROMOTE_COURSE_MOBILE_BROWSE_COURSE)
                    Log.i("my course promo",response.toString());
                else
                    Log.i("browse course promo",response.toString());
                Gson gson = new Gson();
                mPromoCourseResponse = gson.fromJson(response.toString(),PromoCourseResponse.class);
                if (mPromoCourseResponse.getStatus()==Flinnt.SUCCESS) {
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("mBrowseCourses Error : " + error.getMessage());

                mPromoCourseResponse.parseErrorResponse(error);
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
            msg.obj = mPromoCourseResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public PromoCourseRequest getBrowseCoursesRequest() {
        if (null == mPromoCourseRequest) {
            mPromoCourseRequest = new PromoCourseRequest();
            mPromoCourseRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mPromoCourseRequest.setSource(source);
        }
        return mPromoCourseRequest;
    }
}
