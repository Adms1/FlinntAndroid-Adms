package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostCommentsRequest;
import com.edu.flinnt.protocol.PostCommentsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostComments {

	public static final String TAG = PostComments.class.getSimpleName();
	public static PostCommentsResponse mPostCommentsResponse = null;
	public PostCommentsRequest mPostCommentsRequest = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mPostID = "";

	public PostComments( Handler handler, String courseId, String postId ) {
		mHandler = handler;
		mCourseID = courseId;
		mPostID = postId;
		getLastResponse();
	}

	public PostCommentsResponse getLastResponse() {
		if(null == mPostCommentsResponse){
			mPostCommentsResponse = new PostCommentsResponse();
		}
		return mPostCommentsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_COMMENTS_LIST;
	}
	
	public void sendPostCommentsRequest(PostCommentsRequest postCommentRequest) {
		mPostCommentsRequest = postCommentRequest; 
		sendPostCommentsRequest();
	}

	public void sendPostCommentsRequest() {
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
		synchronized (PostComments.class) {
			try {
				String url = buildURLString();

				if( null == mPostCommentsRequest ) {
					mPostCommentsRequest = new PostCommentsRequest();
					mPostCommentsResponse.clearCommentList();
				}

				mPostCommentsRequest.setUserID(Config.getStringValue(Config.USER_ID));
				mPostCommentsRequest.setCourseID( mCourseID );
				mPostCommentsRequest.setPostID( mPostID );

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostComments Request :\nUrl : " + url + "\nData : " + mPostCommentsRequest.getJSONString());

				JSONObject jsonObject = mPostCommentsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostComments Response : " + response.toString());

				if (mPostCommentsResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mPostCommentsResponse.getJSONData(response);
					if (null != jsonData) {
						mPostCommentsResponse.parseJSONObject(jsonData);
						if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostComments response : " + mPostCommentsResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostComments Error : " +  error.getMessage());
				mPostCommentsResponse.parseErrorResponse(error);
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
			msg.obj = mPostCommentsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
