package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class AddPost {

    public static final String TAG = AddPost.class.getSimpleName();
    public AddPostResponse mAddPostResponse = null;
    public Handler mHandler = null;
    public AddPostRequest mAddPostRequest;
    private int mPostType = Flinnt.INVALID;

    public AddPost(Handler handler, AddPostRequest addPostRequest, int postType) {
        mHandler = handler;
        mAddPostRequest = addPostRequest;
        mPostType = postType;
        getResponse();
    }

    public AddPostResponse getResponse() {
        if (mAddPostResponse == null) {
            mAddPostResponse = new AddPostResponse();
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("AddPost response : " + mAddPostResponse.toString());
        return mAddPostResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {

        switch (mPostType) {

            case Flinnt.POST_COMMUNICATION_ADD:
                return Flinnt.API_URL + Flinnt.URL_POST_COMMUNICATION_ADD;

            case Flinnt.POST_BLOG_ADD:
                return Flinnt.API_URL + Flinnt.URL_POST_BLOG_ADD;

            case Flinnt.POST_BLOG_EDIT:
                return Flinnt.API_URL + Flinnt.URL_POST_BLOG_EDIT;

            case Flinnt.POST_BLOG_REPOST:
                return Flinnt.API_URL + Flinnt.URL_POST_BLOG_REPOST;

            case Flinnt.POST_QUIZ_ADD:
                return Flinnt.API_URL + Flinnt.URL_POST_QUIZ_ADD;

            case Flinnt.POST_QUIZ_EDIT:
                return Flinnt.API_URL + Flinnt.URL_POST_QUIZ_EDIT;

            case Flinnt.POST_QUIZ_REPOST:
                return Flinnt.API_URL + Flinnt.URL_POST_QUIZ_REPOST;

            case Flinnt.POST_MESSAGE_ADD:
                return Flinnt.API_URL + Flinnt.URL_POST_MESSAGE_ADD;

            case Flinnt.POST_MESSAGE_EDIT:
                return Flinnt.API_URL + Flinnt.URL_POST_MESSAGE_EDIT;

            case Flinnt.POST_ALBUM_ADD:
                return Flinnt.API_URL + Flinnt.URL_POST_ALBUM_ADD;

            case Flinnt.POST_ALBUM_EDIT:
                return Flinnt.API_URL + Flinnt.URL_POST_ALBUM_EDIT;

            case Flinnt.POST_ALBUM_REPOST:
                return Flinnt.API_URL + Flinnt.URL_POST_ALBUM_REPOST;

            case Flinnt.POST_ALERT_ADD:
                return Flinnt.API_URL + Flinnt.URL_ALERT_ADD;

            case Flinnt.POST_COURSE_ADD:
                return Flinnt.API_URL + Flinnt.URL_COURSE_ADD;

            case Flinnt.POST_COURSE_EDIT:
                return Flinnt.API_URL + Flinnt.URL_COURSE_EDIT;

            case Flinnt.POST_ALERT_EDIT:
                return Flinnt.API_URL + Flinnt.URL_ACCOUNT_ALERT_EDIT;

            case Flinnt.POST_BANNER_ADD:
                return Flinnt.API_URL + Flinnt.URL_BANNER_ADD;

            default:
                return "";
        }
    }

    public void sendAddPostRequest() {
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
        synchronized (AddPost.class) {
            try {
                String url = buildURLString();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("AddPost Request :\nUrl : " + url + "\nData : " + mAddPostRequest.getJSONString());

                JSONObject jsonObject = mAddPostRequest.getJSONObject();

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
                    LogWriter.write("AddPost response :\n" + response.toString());
                //Log.i("response", response.toString());
                mAddPostResponse = new Gson().fromJson(response.toString(), AddPostResponse.class);
                if (mAddPostResponse.getStatus() == 1) {

                    sendMesssageToGUI(Flinnt.SUCCESS);
                } else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("AddPostError : " + error.getMessage());

                mAddPostResponse.parseErrorResponse(error);
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
            msg.obj = mAddPostResponse;
            mHandler.sendMessage(msg);
        }
    }

}
