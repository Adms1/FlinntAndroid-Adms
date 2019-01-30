package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.DeletePostResponse;
import com.edu.flinnt.protocol.PostDetailsRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class DeletePost {

	public static final String TAG = DeletePost.class.getSimpleName();
	public static DeletePostResponse mDeletePostResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mPostID = "";

	public DeletePost(Handler handler, String courseId, String postId) {
		mHandler = handler;
		mCourseID = courseId;
		mPostID = postId;
		getResponse();
	}

	static public DeletePostResponse getResponse() {
		mDeletePostResponse = new DeletePostResponse();
		return mDeletePostResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_DELETE;
	}

	public void sendDeletePostRequest() {
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
		synchronized (DeletePost.class) {
			try {
				String url = buildURLString();

				/**
					PostDetailsRequest parameter are same... 
					so use as request for deletePostRequest
				*/
				PostDetailsRequest deletePostRequest = new PostDetailsRequest();

				deletePostRequest.setUserID( Config.getStringValue(Config.USER_ID) );
				deletePostRequest.setCourseID( mCourseID );
				deletePostRequest.setPostID( mPostID );

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeletePost Request :\nUrl : " + url + "\nData : " + deletePostRequest.getJSONString());

				JSONObject jsonObject = deletePostRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeletePost Response :\n" + response.toString());
				if (mDeletePostResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mDeletePostResponse.getJSONData(response);
					if (null != jsonData) {
						mDeletePostResponse.parseJSONObject(jsonData);
						//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Delete Post response : " + mDeletePostResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("DeletePost Error :: " + error.getMessage());
				mDeletePostResponse.parseErrorResponse(error);
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
			msg.obj = mDeletePostResponse;
			mHandler.sendMessage(msg);
		}
	}
}
