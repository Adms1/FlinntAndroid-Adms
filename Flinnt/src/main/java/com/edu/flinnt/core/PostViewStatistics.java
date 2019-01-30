package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostViewStatisticsRequest;
import com.edu.flinnt.protocol.PostViewStatisticsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostViewStatistics {

	public static final String TAG = PostViewStatistics.class.getSimpleName();
	public static PostViewStatisticsResponse mPostViewStatisticsResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mPostID = "";
	private int mMaxViewers = 20;

	public static final String VIEWERS 	= " Viewers";
	public static final String LIKES 		= " Likes";
	public static final String COMMENTS 	= " Comments";
	public static final String REPLIES		= " Replies";

	public PostViewStatistics(Handler handler, String courseId, String postId, int maxViewers) {
		mHandler = handler;
		mCourseID = courseId;
		mPostID = postId;
		mMaxViewers = maxViewers;
		getLastResponse();
	}

	static public PostViewStatisticsResponse getLastResponse() {

		/*if (mPostDetailsResponse == null)*/ {
			mPostViewStatisticsResponse = new PostViewStatisticsResponse();
		}
		//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("MenuBanner response : " + mMenuBannerResponse.toString());
		return mPostViewStatisticsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_STATS;
	}

	public void sendPostViewStatisticsRequest() {
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
		synchronized (PostViewStatistics.class) {
			try {
				String url = buildURLString();

				PostViewStatisticsRequest postViewStatisticsRequest = new PostViewStatisticsRequest();

				postViewStatisticsRequest.setUserID(Config.getStringValue(Config.USER_ID));
				postViewStatisticsRequest.setCourseID( mCourseID );
				postViewStatisticsRequest.setPostID( mPostID );
				postViewStatisticsRequest.setMaxViewers( mMaxViewers ); 

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostViewStatistics Request :\nUrl : " + url + "\nData : " + postViewStatisticsRequest.getJSONString());

				JSONObject jsonObject = postViewStatisticsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostViewStatistics Response :\n" + response.toString());

				if (mPostViewStatisticsResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mPostViewStatisticsResponse.getJSONData(response);
					if (null != jsonData) {
						mPostViewStatisticsResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostViewStatistics Error : " + error.getMessage());
				mPostViewStatisticsResponse.parseErrorResponse(error);
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
			msg.obj = mPostViewStatisticsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
