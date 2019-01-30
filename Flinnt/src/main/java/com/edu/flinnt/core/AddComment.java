package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddCommentRequest;
import com.edu.flinnt.protocol.AddCommentResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class AddComment {

	public static final String TAG = AddComment.class.getSimpleName();
	public static AddCommentResponse mAddCommentResponse = null;
	public Handler mHandler = null;
	public String mCommentText = "", mPostID = "", mCourseID = "";

	public AddComment(Handler handler, String courseID, String postID , String comment) {
		mHandler = handler;
		mCourseID = courseID;
		mPostID = postID;
		mCommentText = comment;
		getLastResponse();
	}

	public static AddCommentResponse getLastResponse() {
		if (mAddCommentResponse == null) {
			mAddCommentResponse = new AddCommentResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AddComment response : " + mAddCommentResponse.toString());
		return mAddCommentResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_COMMENTS_ADD;
	}

	public void sendAddCommentRequest() {
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
		synchronized (AddComment.class) {
			try {
				String url = buildURLString();

				AddCommentRequest addCommentRequest = new AddCommentRequest();
				
				addCommentRequest.setUserID(Config.getStringValue(Config.USER_ID));
				addCommentRequest.setCourseID(mCourseID);
				addCommentRequest.setPostID(mPostID);
				addCommentRequest.setComment(mCommentText);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment Request :\nUrl : " + url + "\nData : " + addCommentRequest.getJSONString());
				
				JSONObject jsonObject = addCommentRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddComment response :\n" + response.toString());

						if (mAddCommentResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mAddCommentResponse.getJSONData(response);
							if (null != jsonData) {
								mAddCommentResponse.parseJSONObject(jsonData);
								
								sendMesssageToGUI(Flinnt.SUCCESS);
							}
							else {
								sendMesssageToGUI(Flinnt.FAILURE);	
							}
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AddCommentError : " + error.getMessage());

						mAddCommentResponse.parseErrorResponse(error);
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
			msg.obj = mAddCommentResponse;
			mHandler.sendMessage(msg);
		}
	}

}
