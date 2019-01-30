package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ProfileUpdateRequest;
import com.edu.flinnt.protocol.ProfileUpdateResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ProfileUpdate {

	public static final String TAG = ProfileUpdate.class.getSimpleName();
	public static ProfileUpdateResponse mProfileUpdateResponse = null;
	public Handler mHandler = null;
	public ProfileUpdateRequest mProfileUpdateRequest;
	
	public ProfileUpdate(Handler handler, ProfileUpdateRequest profileUpdateRequest) {
		mHandler = handler;
		mProfileUpdateRequest = profileUpdateRequest;
		getResponse();
	}

	public static ProfileUpdateResponse getResponse() {
		if (mProfileUpdateResponse == null) {
			mProfileUpdateResponse = new ProfileUpdateResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ProfileUpdate response : " + mProfileUpdateResponse.toString());
		return mProfileUpdateResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_PROFILE_UPDATE;
	}

	public void sendProfileUpdateRequest() {
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
		synchronized (ProfileUpdate.class) {
			try {
				String url = buildURLString();

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileUpdate Request :\nUrl : " + url + "\nData : " + mProfileUpdateRequest.getJSONString());
				
				JSONObject jsonObject = mProfileUpdateRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileUpdate response :\n" + response.toString());

						if (mProfileUpdateResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mProfileUpdateResponse.getJSONData(response);
							if (null != jsonData) {
								mProfileUpdateResponse.parseJSONObject(jsonData);
								
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ProfileUpdateError : " + error.getMessage());

						mProfileUpdateResponse.parseErrorResponse(error);
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
			msg.obj = mProfileUpdateResponse;
			mHandler.sendMessage(msg);
		}
	}

}
