package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.LikeBookmarkRequest;
import com.edu.flinnt.protocol.LikeBookmarkResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class LikeBookmark {
	public Handler mHandler = null;
//	public static ResourceValidationResponse mResourceValidationResponse = null;
	
	public static LikeBookmarkRequest mBookmarkRequest = null;
	public static LikeBookmarkResponse mBookmarkResponse = null;

	public static int mOperationType; 
	
	public static final int BOOKMARK_ADD								= 1;
	public static final int BOOKMARK_REMOVE								= 2;
	public static final int POST_LIKE									= 3;
	public static final int POST_DISLIKE								= 4;
	
	public LikeBookmark(Handler handler, int type) {
		mHandler = handler;
		mOperationType = type;
		getBookmarksResponse();
	}

	public static LikeBookmarkResponse getBookmarksResponse() {
//		if (mBookmarkResponse == null) {
			mBookmarkResponse = new LikeBookmarkResponse();
//		}
		return mBookmarkResponse;
	}
	
	public void setLikeBookmarkRequest(LikeBookmarkRequest likeBookmarkRequest) {
		mBookmarkRequest = likeBookmarkRequest;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		switch (mOperationType) {
			case BOOKMARK_ADD:  		// Replace these URLs
				url = Flinnt.API_URL + Flinnt.URL_POST_BOOKMARK_ADD;
			break;
			case BOOKMARK_REMOVE:
				url = Flinnt.API_URL + Flinnt.URL_POST_BOOKMARK_REMOVE;
			break;
			case POST_LIKE:
				url = Flinnt.API_URL + Flinnt.URL_POST_LIKE;
			break;
			case POST_DISLIKE:
				url = Flinnt.API_URL + Flinnt.URL_POST_DISLIKE;
			break;
			default:
//				url = Flinnt.API_URL + Flinnt.URL_RESOURCE_VALIDATION;
			break;
		}
		return url;
	}

	public void sendLikeBookmarkRequest(){
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
		synchronized (ResendorVerified.class) {
			try {
				String url = buildURLString();

				if( null == mBookmarkRequest ) {
					mBookmarkRequest = new LikeBookmarkRequest();
				}
				mBookmarkRequest.setUserID(Config.getStringValue(Config.USER_ID));
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("LikeBookmark Request :\nUrl : " + url + "\nData : " + mBookmarkRequest.getJSONString());

				JSONObject jsonObject = mBookmarkRequest.getJSONObject();
				
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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("LikeBookmark Response :\n" + response.toString());

				if (mBookmarkResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mBookmarkResponse.getJSONData(response);
					if (null != jsonData) {
						mBookmarkResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("LikeBookmark Error : " + error.getMessage());

				mBookmarkResponse.parseErrorResponse(error);
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
			msg.obj = mBookmarkResponse;
			mHandler.sendMessage(msg);
		}
	}
}
