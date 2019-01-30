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
import com.edu.flinnt.protocol.PostCommentDeleteRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class DeleteComment {

	public static final String TAG = DeleteComment.class.getSimpleName();
	public static DeletePostResponse mDeleteCommentResponse = null;
	public Handler mHandler = null;
	private String mCommentID = "";

	public DeleteComment(Handler handler, String commentId) {
		mHandler = handler;
		mCommentID = commentId;
		getResponse();
	}

	static public DeletePostResponse getResponse() {
		mDeleteCommentResponse = new DeletePostResponse();
		return mDeleteCommentResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COMMENT_DELETE;
	}

	public void sendDeleteCommentRequest() {
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
		synchronized (DeleteComment.class) {
			try {
				String url = buildURLString();

				/**
					PostDetailsRequest parameter are same... 
					so use as request for DeleteCommentRequest
				*/
				PostCommentDeleteRequest deleteCommentRequest = new PostCommentDeleteRequest();

				deleteCommentRequest.setUserID( Config.getStringValue(Config.USER_ID) );
				deleteCommentRequest.setCommentID(mCommentID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeleteComment Request :\nUrl : " + url + "\nData : " + deleteCommentRequest.getJSONString());

				JSONObject jsonObject = deleteCommentRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeleteComment Response :\n" + response.toString());
				if (mDeleteCommentResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mDeleteCommentResponse.getJSONData(response);
					if (null != jsonData) {
						mDeleteCommentResponse.parseJSONObject(jsonData);
						//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Delete Post response : " + mDeletePostResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("DeleteComment Error :: " + error.getMessage());
				mDeleteCommentResponse.parseErrorResponse(error);
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
			msg.obj = mDeleteCommentResponse;
			mHandler.sendMessage(msg);
		}
	}
}
