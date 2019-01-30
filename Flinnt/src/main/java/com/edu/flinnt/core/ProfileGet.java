package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ProfileResponse;
import com.edu.flinnt.protocol.ResendorVerifiedRequest;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ProfileGet {

	public static final String TAG = ProfileGet.class.getSimpleName();

	public Handler mHandler = null;
	public ResendorVerifiedRequest mProfileRequest = null;
	public ProfileResponse mProfileResponse = null;
	private String userID = "";

	public ProfileGet(Handler handler, ResendorVerifiedRequest request,String userId) {
		mHandler = handler;
		mProfileRequest = request;
		userID = userId;
		getResponse();
	}

	public ProfileResponse getResponse() {
		if (mProfileResponse == null) {
			mProfileResponse = new ProfileResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ProfileGet response : " + mProfileResponse.toString());
		return mProfileResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_ACCOUNT_PROFILE_GET;
	}

	public void sendProfileGetRequest() {
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

				if( null == mProfileRequest ) {
					mProfileRequest = new ResendorVerifiedRequest();
				}

				mProfileRequest.setUserId(userID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileGet Request :\nUrl : " + url);

				JSONObject jsonObject = mProfileRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileGet Response :\n" + response.toString());

				if (mProfileResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mProfileResponse.getJSONData(response);
					if (null != jsonData) {
						mProfileResponse.parseJSONObject(jsonData);

						sendMesssageToGUI(Flinnt.SUCCESS);
						//									sendRequest();
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ProfileGet Error : " + error.getMessage());

				mProfileResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
		});
		jsonObjReq.setShouldCache(false);
		//		// Adding request to request queue
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
			msg.obj = mProfileResponse;
			mHandler.sendMessage(msg);
		}
	}

}
