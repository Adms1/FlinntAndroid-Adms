package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseInvitiesResponse;
import com.edu.flinnt.protocol.DrawerItemListRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseInvities {

    public static final String TAG = CourseInvities.class.getSimpleName();
    public static CourseInvitiesResponse mCourseInvitiesResponse = null;
    public DrawerItemListRequest mCourseInvitiesRequest = null;
    public Handler mHandler = null;
    private int offset = 0;

    public CourseInvities(Handler handler, int Offset) {
        mHandler = handler;
        offset = Offset;
        getCourseInvitiesResponse();
    }

    public static CourseInvitiesResponse getCourseInvitiesResponse() {
        if (mCourseInvitiesResponse == null) {
            mCourseInvitiesResponse = new CourseInvitiesResponse();
            //mCourseInvitiesResponse.parseJSONString( Config.getStringValue(Config.LAST_MY_COURSES_RESPONSE) );
        }
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("response : " + mCourseInvitiesResponse.toString() );
        return mCourseInvitiesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_INVITATION;
    }

    public void sendCourseInvitiesRequest() {
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
        synchronized (CourseInvities.class) {
            try {
                String url = buildURLString();
                mCourseInvitiesRequest = new DrawerItemListRequest();
                mCourseInvitiesRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mCourseInvitiesRequest.setOffset(offset);
                mCourseInvitiesRequest.setMax(20);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseInvities Request :\nUrl : " + url + "\nData : " + mCourseInvitiesRequest.getJSONString());

                JSONObject jsonObject = mCourseInvitiesRequest.getJSONObject();

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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, "Response :: " + response.toString());
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseInvities Response :\n" + response.toString());

                if (mCourseInvitiesResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseInvitiesResponse.getJSONData(response);
                    if (null != jsonData) {
                        mCourseInvitiesResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

//								if( mCourseInvitiesResponse.getHasMore() > 0 ) {
//									sendRequest();
//								}
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Error :: " + error.getMessage());
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("CourseInvities Error : " + error.getMessage());

                mCourseInvitiesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
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
            msg.obj = mCourseInvitiesResponse;
            mHandler.sendMessage(msg);
        }
    }
}
