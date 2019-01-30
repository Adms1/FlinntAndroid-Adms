package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.VerifyMobileRequest;
import com.edu.flinnt.protocol.VerifyMobileResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class VerifyMobile {

	public static final String TAG = VerifyMobile.class.getSimpleName();
	public static VerifyMobileResponse mVerifyMobileResponse = null;
	public Handler mHandler = null;
	private String mVerifyCode = "";
    private String userId = Config.getStringValue(Config.USER_ID);

	public VerifyMobile(Handler handler, String verifyCode, String userId){
		mHandler = handler;
		mVerifyCode = verifyCode;
		getLastResponse();
        this.userId = userId;
	}

	static public VerifyMobileResponse getLastResponse() {
		if (mVerifyMobileResponse == null) {
			mVerifyMobileResponse = new VerifyMobileResponse();
		}
		mVerifyMobileResponse.parseJSONString( Config.getStringValue(Config.LAST_VERIFYMOBILE_RESPONSE) );
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("VerifyMobile response : " + mVerifyMobileResponse.toString());
		return mVerifyMobileResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_VERIFY_MOBILE;
	}

	public void sendVerifyMobileRequest(){
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
		synchronized (VerifyMobile.class) {
			try {
				String url = buildURLString();
				VerifyMobileRequest verifyMobileRequest = new VerifyMobileRequest();

				verifyMobileRequest.setUserId(userId);
				verifyMobileRequest.setVerificationCode(mVerifyCode);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("VerifyMobile Request :\nUrl : " + url + "\nData : " + verifyMobileRequest.getJSONString());

				JSONObject jsonObject = verifyMobileRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("VerifyMobile Response :\n" + response.toString());

				if (mVerifyMobileResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mVerifyMobileResponse.getJSONData(response);
					if (null != jsonData) {
						mVerifyMobileResponse.parseJSONObject(jsonData);

						Config.setStringValue(Config.LAST_VERIFYMOBILE_RESPONSE, jsonData.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("VerifyMobile Error : " + error.getMessage());

				mVerifyMobileResponse.parseErrorResponse(error);
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
			msg.obj = mVerifyMobileResponse;
			mHandler.sendMessage(msg);
		}
	}

}
