package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostAllowOptionsRequest;
import com.edu.flinnt.protocol.PostAllowOptionsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;



/**
 * Send request and get response and pass it to GUI
 */
public class PostAllowOptions {

	public static final String TAG = PostAllowOptions.class.getSimpleName();
	public static PostAllowOptionsResponse mPostAllowOptionsResponse = new PostAllowOptionsResponse();
	public Handler mHandler = null;
	private String mCourseId = "";
	private int mPostType = Flinnt.FALSE;
	private String mAction = "";

	public PostAllowOptions(Handler handler, String courseId, int postType , String action) {
		mHandler = handler;
		mCourseId = courseId;
		mPostType = postType;
		mAction = action;
	}

	public static PostAllowOptionsResponse getLastResponse() {
		if( null == mPostAllowOptionsResponse ) {
			mPostAllowOptionsResponse = new PostAllowOptionsResponse();
		}
		return mPostAllowOptionsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COMMUNICATION_OPTIONS;
	}

	public void sendPostAllowOptionsRequest() {
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
		synchronized (PostAllowOptions.class) {
			try {
				String url = buildURLString();

				PostAllowOptionsRequest postAllowOptionsRequest = new PostAllowOptionsRequest();

				postAllowOptionsRequest.setUserId(Config.getStringValue(Config.USER_ID));
				if(!TextUtils.isEmpty(mCourseId)){
					postAllowOptionsRequest.setCourseId(mCourseId);
					postAllowOptionsRequest.setPostType(mPostType);
					postAllowOptionsRequest.setAction(mAction);

				}
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostAllowOptions Request :\nUrl : " + url + "\nData : " + postAllowOptionsRequest.getJSONString());

				JSONObject jsonObject = postAllowOptionsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostAllowOptions response :\n" + response.toString());

				if( null != mPostAllowOptionsResponse && mPostAllowOptionsResponse.isSuccessResponse(response)) {
					String postAllowOptionsResponse = new String(response.toString());
					JSONObject jsonData = mPostAllowOptionsResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mPostAllowOptionsResponse = gson.fromJson(postAllowOptionsResponse, PostAllowOptionsResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostAllowOptions Error : " + error.getMessage());

				mPostAllowOptionsResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
		});

		jsonObjReq.setShouldCache(false);
		// Adding request to request queue
		Requester.getInstance().addToRequestQueue(jsonObjReq);
	}

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mPostAllowOptionsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
