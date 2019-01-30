package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ForgotPasswordRequest;
import com.edu.flinnt.protocol.ForgotPasswordResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ForgotPassword {

	public static final String TAG = ForgotPassword.class.getSimpleName();
	public static ForgotPasswordResponse mForgotPasswordResponse = null;
	public Handler mHandler = null;
	
	public ForgotPassword(Handler handler) {
		mHandler = handler;
		getLastResponse();
	}

	public static ForgotPasswordResponse getLastResponse() {
		if (mForgotPasswordResponse == null) {
			mForgotPasswordResponse = new ForgotPasswordResponse();
			//mForgotPasswordResponse.parseJSONString( Config.getStringValue(Config.LAST_FORGOT_PASSWORD_RESPONSE) );
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ForgotPassword response : " + mForgotPasswordResponse.toString());
		return mForgotPasswordResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_FORGOT_PASSWORD;
	}

	public void sendForgotPasswordRequest() {
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
		synchronized (ForgotPassword.class) {
			try {
				String url = buildURLString();

				ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
				forgotPasswordRequest.setUsername(Config.getStringValue(Config.USER_LOGIN));
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ForgotPassword Request :\nUrl : " + url + "\nData : " + forgotPasswordRequest.getJSONString());

				JSONObject jsonObject = forgotPasswordRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ForgotPassword response :\n" + response.toString());

						if (mForgotPasswordResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mForgotPasswordResponse.getJSONData(response);
							if (null != jsonData) {
								mForgotPasswordResponse.parseJSONObject(jsonData);
								Config.setStringValue(Config.ACCOUNT_VERIFIED, mForgotPasswordResponse.getAccVerified());
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ForgotPassword Error :: " + error.getMessage());

						mForgotPasswordResponse.parseErrorResponse(error);
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
			msg.obj = mForgotPasswordResponse;
			mHandler.sendMessage(msg);
		}
	}

}
