package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AddPollRequest;
import com.edu.flinnt.protocol.AddPollResponse;
import com.edu.flinnt.protocol.EditPollRequest;
import com.edu.flinnt.protocol.EditPollResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class EditPoll {

	private EditPollRequest mEditPollRequest;
	public static final String TAG = EditPoll.class.getSimpleName();
	public static EditPollResponse mEditPollResponse = null;
	public Handler mHandler = null;

	public EditPoll(Handler handler, EditPollRequest editPollRequest ) {
		mHandler = handler;
		mEditPollRequest = editPollRequest;
		getLastResponse();
	}

	public static EditPollResponse getLastResponse() {
		if (mEditPollResponse == null) {
			mEditPollResponse = new EditPollResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("EditPoll response : " + mEditPollResponse.toString());
		return mEditPollResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POLL_EDIT;
	}

	public void sendEditPollRequest() {
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
		synchronized (EditPoll.class) {
			try {
				Gson gson = new Gson();
				JSONObject object = new JSONObject(gson.toJson(mEditPollRequest));
				String url = buildURLString();
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("EditPoll Request :\nUrl : " + url + "\nData : " + object.toString());
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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("EditPoll response :\n" + response.toString());

						if (mEditPollResponse.isSuccessResponse(response)) {

							if (null != response) {
								Gson gson = new Gson();
								mEditPollResponse = gson.fromJson(response.toString(),EditPollResponse.class);
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("EditPoll Error : " + error.getMessage());

						mEditPollResponse.parseErrorResponse(error);
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
			msg.obj = mEditPollResponse;
			mHandler.sendMessage(msg);
		}
	}

}
