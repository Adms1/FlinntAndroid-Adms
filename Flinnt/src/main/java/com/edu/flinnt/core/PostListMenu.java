package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostListMenuRequest;
import com.edu.flinnt.protocol.PostListMenuResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostListMenu {

    public static final String TAG = PostListMenu.class.getSimpleName();
    public static PostListMenuResponse mPostListMenuResponse = null;
    public Handler mHandler = null;
    private String mCourseID = "";

    public PostListMenu(Handler handler, String courseId) {
        mHandler = handler;
        mCourseID = courseId;
        getResponse();
    }

    static public PostListMenuResponse getResponse() {

        mPostListMenuResponse = new PostListMenuResponse();
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostListMenu response : " + mPostListMenuResponse.toString());
        return mPostListMenuResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_POST_LIST_MENU;
    }

    public void sendPostListMenuRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    //Log.d("Postt", "sendRequest()");
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
        synchronized (PostListMenu.class) {
            try {
                String url = buildURLString();

                PostListMenuRequest postListMenuRequest = new PostListMenuRequest();

                postListMenuRequest.setUserId(Config.getStringValue(Config.USER_ID));
                postListMenuRequest.setCourseId(mCourseID);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostListMenu Request :\nUrl : " + url + "\nData : " + postListMenuRequest.getJSONString());
                //Log.d("Postt", "PostListMenu Request :\nUrl : " + url + "\nData : " + postListMenuRequest.getJSONString());
                JSONObject jsonObject = postListMenuRequest.getJSONObject();

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
                //.d("Postt", "PostListMenu Response..");
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostListMenu Response :\n" + response.toString());
                ////Log.d("Postt", "PostListMenu Response :\n" + response.toString());
                if (mPostListMenuResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mPostListMenuResponse.getJSONData(response);
                    if (null != jsonData) {
                        mPostListMenuResponse.parseJSONObject(jsonData);
                        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostListMenu response : " + mPostListMenuResponse.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Postt", "PostListMenu error..");

                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("PostListMenu Error : " + error.getMessage());
                //Log.d("Postt", "PostListMenu Error : " + error.getMessage());
                mPostListMenuResponse.parseErrorResponse(error);
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
            //Log.d("Postt", "msgId : " + messageID);
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mPostListMenuResponse;
            mHandler.sendMessage(msg);
        }else {
            //Log.d("Postt", "else : msgId : " + messageID);
        }
    }
}
