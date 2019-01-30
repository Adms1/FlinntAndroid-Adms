package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostViewersRequest;
import com.edu.flinnt.protocol.PostViewersResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostViewersLikes {

    public static final String TAG = PostViewersLikes.class.getSimpleName();
    public static PostViewersResponse mPostViewersResponse = null;
    public PostViewersRequest mPostViewersRequest = null;
    public Handler mHandler = null;
    private String mCourseID = "";
    private String mPostID = "";
    private int mCurrentSelection = 0;
    private String mPostOrContent = "";
    private int offset = 0;


    public PostViewersLikes(Handler handler, String courseId, String postId,String postorcontent, int currentTab, int offSet) {
        mHandler = handler;
        mCourseID = courseId;
        mPostID = postId;
        mPostOrContent =  postorcontent;
        mCurrentSelection = currentTab;
        offset = offSet;
        getLastResponse();
    }

    public PostViewersResponse getLastResponse() {
        if (mPostViewersResponse == null) {
            mPostViewersResponse = new PostViewersResponse();
        }
        return mPostViewersResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        if(mPostOrContent.equals("Content")){
            return Flinnt.API_URL + Flinnt.URL_CONTENT_VIEWERS;
        }else{
            return Flinnt.API_URL + Flinnt.URL_POST_VIEWERS;
        }
    }
    public void sendPostViewersLikesRequest(PostViewersRequest postListRequest) {
        mPostViewersRequest = postListRequest;
        sendPostViewersLikesRequest();
    }

    public void sendPostViewersLikesRequest() {
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
        synchronized (PostViewersLikes.class) {
            try {
                String url = buildURLString();

                mPostViewersRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mPostViewersRequest.setCourseID(mCourseID);
                mPostViewersRequest.setPostID(mPostID);
                mPostViewersRequest.setPostOrContent(mPostOrContent);
                mPostViewersRequest.setIsLike(Flinnt.TRUE);
                mPostViewersRequest.setOffSet(offset);
                mPostViewersRequest.setMaxFetch(20);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostViewersLikes Request :\nUrl : " + url + "\nData : " + mPostViewersRequest.getJSONString());

                JSONObject jsonObject = mPostViewersRequest.getJSONObject();

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
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostViewersLikes Response :\n" + response.toString());

                if (mPostViewersResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mPostViewersResponse.getJSONData(response);
                    if (null != jsonData) {
                        mPostViewersResponse.parseJSONObject(jsonData);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("PostViewersLikes Error : " + error.getMessage());

                mPostViewersResponse.parseErrorResponse(error);
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
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mPostViewersResponse;
            mHandler.sendMessage(msg);
        }
    }
}
