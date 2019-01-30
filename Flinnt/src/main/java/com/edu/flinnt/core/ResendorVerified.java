package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ResendorVerifiedRequest;
import com.edu.flinnt.protocol.ResendorVerifiedResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ResendorVerified {

	public static final String TAG = ResendorVerified.class.getSimpleName();
	public Handler mHandler = null;
	public static ResendorVerifiedResponse mResendorVerifiedResponse = null;
	
	public static final int RESEND_CODE = 0; 
	public static final int ALREADY_VERIFIED = 1;
	
	private int mClassType = ALREADY_VERIFIED;
    private String userId = Config.getStringValue(Config.USER_ID);

	public ResendorVerified(Handler handler, int classType, String userId){
		mHandler = handler;
		mClassType = classType;
		getResponse();
        this.userId = userId;
	}

	static public ResendorVerifiedResponse getResponse() {
		if (mResendorVerifiedResponse == null) {
			mResendorVerifiedResponse = new ResendorVerifiedResponse();
		}
		return mResendorVerifiedResponse;
	}


    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		if(mClassType == ALREADY_VERIFIED){
			return Flinnt.API_URL + Flinnt.URL_VERIFICATION_STATUS;
		}
		else{
			return Flinnt.API_URL + Flinnt.URL_RESEND_CODE;
		}
	}

	public void sendResendorVerifiedRequest(){
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

				ResendorVerifiedRequest resendorVerifiedRequest = new ResendorVerifiedRequest();
				resendorVerifiedRequest.setUserId(userId);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ResendOrVerified Request :\nUrl : " + url + "\nData : " + resendorVerifiedRequest.getJSONString());

				JSONObject jsonObject = resendorVerifiedRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ResendOrVerified Response :\n" + response.toString());

				if (mResendorVerifiedResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mResendorVerifiedResponse.getJSONData(response);
					if (null != jsonData) {
						mResendorVerifiedResponse.parseJSONObject(jsonData);
//						Config.setStringValue(Config.ACCOUNT_VERIFIED, mResendorVerifiedResponse.getAccVerified());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ResendOrVerified Error : " + error.getMessage());

				mResendorVerifiedResponse.parseErrorResponse(error);
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
			msg.obj = mResendorVerifiedResponse;
			mHandler.sendMessage(msg);
		}
	}
}
