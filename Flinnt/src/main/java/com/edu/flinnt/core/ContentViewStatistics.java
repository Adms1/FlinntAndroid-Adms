package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentViewStatisticsRequest;
import com.edu.flinnt.protocol.ContentViewStatisticsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentViewStatistics {

	public static final String TAG = ContentViewStatistics.class.getSimpleName();
	public static ContentViewStatisticsResponse mContentViewStatisticsResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mContentID = "";
	private int mMaxViewers = 20;

	public static final String VIEWERS 	= " Viewers";
	public static final String LIKES 		= " Likes";
	public static final String COMMENTS 	= " Comments";
	public static final String REPLIES		= " Replies";

	public ContentViewStatistics(Handler handler, String courseId, String contentID,int maxViewers) {
		mHandler = handler;
		mCourseID = courseId;
		mContentID = contentID;
		mMaxViewers = maxViewers;
		getLastResponse();
	}

	static public ContentViewStatisticsResponse getLastResponse() {

		/*if (mPostDetailsResponse == null)*/ {
			mContentViewStatisticsResponse = new ContentViewStatisticsResponse();
		}
		//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("MenuBanner response : " + mMenuBannerResponse.toString());
		return mContentViewStatisticsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_CONTENTS_VIEW_STATISTICS;
	}

	public void sendContentViewStatisticsRequest() {
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
		synchronized (ContentViewStatistics.class) {
			try {
				String url = buildURLString();

				ContentViewStatisticsRequest contentViewStatisticsRequest = new ContentViewStatisticsRequest();
				contentViewStatisticsRequest.setUserID(Config.getStringValue(Config.USER_ID));
				contentViewStatisticsRequest.setCourseID( mCourseID );
				contentViewStatisticsRequest.setContentID(mContentID);
				contentViewStatisticsRequest.setMaxViewers( mMaxViewers );

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentViewStatistics Request :\nUrl : " + url + "\nData : " + contentViewStatisticsRequest.getJSONString());

				JSONObject jsonObject = contentViewStatisticsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentViewStatistics Response :\n" + response.toString());

				if (mContentViewStatisticsResponse.isSuccessResponse(response)) {


					String contentViewStatasticResponse = new String(response.toString());

					JSONObject jsonData = mContentViewStatisticsResponse.getJSONData(response);

					if (null != jsonData) {
						Gson gson = new Gson();
						mContentViewStatisticsResponse = gson.fromJson(contentViewStatasticResponse, ContentViewStatisticsResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);

					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ContentViewStatistics Error : " + error.getMessage());
				mContentViewStatisticsResponse.parseErrorResponse(error);
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
			msg.obj = mContentViewStatisticsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
