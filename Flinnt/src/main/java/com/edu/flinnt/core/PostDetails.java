package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostDetailsRequest;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostDetails {

	public static final String TAG = PostDetails.class.getSimpleName();
	public static PostDetailsResponse mPostDetailsResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mPostID = "";
	private int isFromNotification = Flinnt.FALSE;

	public PostDetails(Handler handler, String courseId, String postId, int fromNotification) {
		mHandler = handler;
		mCourseID = courseId;
		mPostID = postId;
		isFromNotification = fromNotification;
		getLastResponse();
	}

	static public PostDetailsResponse getLastResponse() {

		/*if (mPostDetailsResponse == null)*/ {
			mPostDetailsResponse = new PostDetailsResponse();
		}
		//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("MenuBanner response : " + mMenuBannerResponse.toString());
		return mPostDetailsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_VIEW;
	}

	public void sendPostDetailsRequest() {
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
		synchronized (PostDetails.class) {
			try {
				String url = buildURLString();

				PostDetailsRequest postDetailsRequest = new PostDetailsRequest();

			    postDetailsRequest.setUserID(Config.getStringValue(Config.USER_ID));

                postDetailsRequest.setCourseID( mCourseID );
				postDetailsRequest.setPostID( mPostID );
				postDetailsRequest.setNotification(isFromNotification);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostDetails Request :\nUrl : " + url + "\nData : " + postDetailsRequest.getJSONString());

				JSONObject jsonObject = postDetailsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostDetails Response :\n" + response.toString());

				if (mPostDetailsResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mPostDetailsResponse.getJSONData(response);
					if (null != jsonData) {
						mPostDetailsResponse.parseJSONObject(jsonData);
						//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostDetails response : " + mPostDetailsResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostDetails Error : " + error.getMessage());

				mPostDetailsResponse.parseErrorResponse(error);
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
			msg.obj = mPostDetailsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
