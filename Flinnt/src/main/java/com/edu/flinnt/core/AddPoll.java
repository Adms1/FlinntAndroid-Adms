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
import com.edu.flinnt.protocol.AddPollRequest;
import com.edu.flinnt.protocol.AddPollResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class AddPoll {

	private AddPollRequest mAddPollRequest;

	public static final String TAG = AddPoll.class.getSimpleName();
	public static AddPollResponse mAddPollResponse = null;
	public Handler mHandler = null;

	public AddPoll(Handler handler, AddPollRequest addPollRequest ) {
		mHandler = handler;
		mAddPollRequest = addPollRequest;
		getLastResponse();
	}

	public static AddPollResponse getLastResponse() {
		if (mAddPollResponse == null) {
			mAddPollResponse = new AddPollResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AddPoll response : " + mAddPollResponse.toString());
		return mAddPollResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POLL_ADD;
	}

	public void sendAddPollRequest() {
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
		synchronized (AddPoll.class) {
			try {
				Gson gson = new Gson();
				JSONObject object = new JSONObject(gson.toJson(mAddPollRequest));
				String url = buildURLString();
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddPoll Request :\nUrl : " + url + "\nData : " + object.toString());
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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AddPoll response :\n" + response.toString());

						if (mAddPollResponse.isSuccessResponse(response)) {

							if (null != response) {
								Gson gson = new Gson();
								mAddPollResponse = gson.fromJson(response.toString(),AddPollResponse.class);
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AddPoll Error : " + error.getMessage());

						mAddPollResponse.parseErrorResponse(error);
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
			msg.obj = mAddPollResponse;
			mHandler.sendMessage(msg);
		}
	}

}
