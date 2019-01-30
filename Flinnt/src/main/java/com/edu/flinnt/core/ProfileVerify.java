package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ProfileVerifyRequest;
import com.edu.flinnt.protocol.ProfileVerifyResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ProfileVerify {

	public static final String TAG = ProfileVerify.class.getSimpleName();
	public static ProfileVerifyResponse mProfileVerifyResponse = null;
	public Handler mHandler = null;
	public ProfileVerifyRequest mProfileVerifyRequest;
	private int mRequestType = Flinnt.INVALID;
	
	public static final int PROFILE_MOBILE_VERIFY 							= 51; 
	public static final int PROFILE_MOBILE_RESEND_CODE 						= 52; 
	public static final int PROFILE_EMAIL_RESEND_LINK							= 53; 

	public ProfileVerify(Handler handler, ProfileVerifyRequest profileVerifyRequest, int reqType) {
		mHandler = handler;
		mProfileVerifyRequest = profileVerifyRequest;
		mRequestType = reqType;
		getResponse();
	}

	public static ProfileVerifyResponse getResponse() {
		if (mProfileVerifyResponse == null) {
			mProfileVerifyResponse = new ProfileVerifyResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ProfileVerify response : " + mProfileVerifyResponse.toString());
		return mProfileVerifyResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		switch (mRequestType) {

		case PROFILE_MOBILE_VERIFY:
			return Flinnt.API_URL + Flinnt.URL_PROFILE_MOBILE_VERIFY;

		case PROFILE_MOBILE_RESEND_CODE:
			return Flinnt.API_URL + Flinnt.URL_PROFILE_MOBILE_RESEND_CODE;

		case PROFILE_EMAIL_RESEND_LINK:
			return Flinnt.API_URL + Flinnt.URL_PROFILE_EMAIL_RESEND_LINK;


		default:
			return "";
		}
	}

	public void sendProfileVerifyRequest() {
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
		synchronized (ProfileVerify.class) {
			try {
				String url = buildURLString();

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileVerify Request :\nUrl : " + url + "\nData : " + mProfileVerifyRequest.getJSONString());

				JSONObject jsonObject = mProfileVerifyRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ProfileVerify response :\n" + response.toString());

				if (mProfileVerifyResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mProfileVerifyResponse.getJSONData(response);
					if (null != jsonData) {
						mProfileVerifyResponse.parseJSONObject(jsonData);

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
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ProfileVerify Error : " + error.getMessage());

				mProfileVerifyResponse.parseErrorResponse(error);
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
			msg.obj = mProfileVerifyResponse;
			mHandler.sendMessage(msg);
		}
	}

}
