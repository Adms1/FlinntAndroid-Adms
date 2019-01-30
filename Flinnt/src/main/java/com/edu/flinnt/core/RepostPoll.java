package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.EditPollRequest;
import com.edu.flinnt.protocol.EditPollResponse;
import com.edu.flinnt.protocol.RepostPollRequest;
import com.edu.flinnt.protocol.RepostPollResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class RepostPoll {

	private RepostPollRequest mRepostPollRequest;
	public static final String TAG = RepostPoll.class.getSimpleName();
	public static RepostPollResponse mRepostPollResponse = null;
	public Handler mHandler = null;

	public RepostPoll(Handler handler, RepostPollRequest repostPollRequest ) {
		mHandler = handler;
		mRepostPollRequest = repostPollRequest;
		getLastResponse();
	}

	public static RepostPollResponse getLastResponse() {
		if (mRepostPollResponse == null) {
			mRepostPollResponse = new RepostPollResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Repost response : " + mRepostPollResponse.toString());
		return mRepostPollResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POLL_REPOST;
	}

	public void sendRepostPollRequest() {
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
		synchronized (RepostPoll.class) {
			try {
				Gson gson = new Gson();
				JSONObject object = new JSONObject(gson.toJson(mRepostPollRequest));
				String url = buildURLString();
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Repost Request :\nUrl : " + url + "\nData : " + object.toString());
				sendJsonObjectRequest(url,object );

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Repost response :\n" + response.toString());

						if (mRepostPollResponse.isSuccessResponse(response)) {

							if (null != response) {
								Gson gson = new Gson();
								mRepostPollResponse = gson.fromJson(response.toString(),RepostPollResponse.class);
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Repost Error : " + error.getMessage());

						mRepostPollResponse.parseErrorResponse(error);
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
			msg.obj = mRepostPollResponse;
			mHandler.sendMessage(msg);
		}
	}

}
