package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentViewersRequest;
import com.edu.flinnt.protocol.ContentViewersResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentViewers {

	public static final String TAG = ContentViewers.class.getSimpleName();
	public static ContentViewersResponse mContentViewersResponse = null;
	public ContentViewersRequest mContentViewersRequest = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mContentID = "";

	public ContentViewers(Handler handler, String courseId, String contentID ) {
		mHandler = handler;
		mCourseID = courseId;
		mContentID =  contentID;
		getLastResponse();
	}

	public ContentViewersResponse getLastResponse() {
		if (mContentViewersResponse == null) {
			mContentViewersResponse = new ContentViewersResponse();
		}
		return mContentViewersResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_CONTENT_VIEWERS;
	}

	public void sendPostViewersRequest(ContentViewersRequest contentViewersRequest) {
		mContentViewersRequest = contentViewersRequest;
		sendPostViewersRequest();
	}

	public void sendPostViewersRequest() {
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
		synchronized (ContentViewers.class) {
			try {
				String url = buildURLString();

				if( null == mContentViewersRequest ) {
					mContentViewersRequest = new ContentViewersRequest();
					mContentViewersResponse.getData().clearViewersList();
				}
				mContentViewersRequest.setUserID(Config.getStringValue(Config.USER_ID));
				mContentViewersRequest.setCourseID( mCourseID );
				mContentViewersRequest.setContentID( mContentID );

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentViewers Request :\nUrl : " + url + "\nData : " + mContentViewersRequest.getJSONString());

				JSONObject jsonObject = mContentViewersRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentViewers Response :\n" + response.toString());

				if (mContentViewersResponse.isSuccessResponse(response)) {

					String contentViewersResponse = new String(response.toString());

					JSONObject jsonData = mContentViewersResponse.getJSONData(response);

					if (null != jsonData) {
						Gson gson = new Gson();
						mContentViewersResponse = gson.fromJson(contentViewersResponse, ContentViewersResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);

						if (mContentViewersResponse.getData().getHasMore() > 0) {
							sendRequest();
						}
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ContentViewers Error : " + error.getMessage());

				mContentViewersResponse.parseErrorResponse(error);
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
			msg.obj = mContentViewersResponse;
			mHandler.sendMessage(msg);
		}
	}
}