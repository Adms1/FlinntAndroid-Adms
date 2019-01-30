package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.MuteSettingRequest;
import com.edu.flinnt.protocol.MuteSettingResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class MuteSetting {
	public Handler mHandler = null;
	public static MuteSettingResponse mMuteSettingResponse = null;
	MuteSettingRequest mMuteSettingRequest = null;
	
	public int mAccountType = ACCOUNT_SETTING;
	public static final int ACCOUNT_SETTING 		= 10;
	public static final int COURSE_SETTING 			= 11;
	
	public int mRequestType = GET_SETTING;
	public static final int GET_SETTING 			= 20;
	public static final int SET_SETTING 			= 21;
	
	
	public MuteSetting(Handler handler, MuteSettingRequest muteSettingRequest, int accountType) {
		mHandler = handler;
		mAccountType = accountType;
		mMuteSettingRequest = muteSettingRequest;
		getMuteSettingResponse();
	}

	public static MuteSettingResponse getMuteSettingResponse() {
		if (mMuteSettingResponse == null) {
			mMuteSettingResponse = new MuteSettingResponse();
		}
		return mMuteSettingResponse;
	}
/*
	public static MuteSettingResponse getLastMuteSettingResponse(int accountType) {
		mMuteSettingResponse = null;
		switch (accountType) {
			case ACCOUNT_SETTING:
				if( !TextUtils.isEmpty( Config.getStringValue(Config.LAST_ACCOUNT_MUTE_SETTING_RESPONSE) ) ) {
					mMuteSettingResponse = new MuteSettingResponse();
					mMuteSettingResponse.parseJSONString( Config.getStringValue(Config.LAST_ACCOUNT_MUTE_SETTING_RESPONSE) );
				}
			break;
		}
		return mMuteSettingResponse;
	}
*/
    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		switch (mRequestType) {
			case GET_SETTING:
				url = Flinnt.API_URL + Flinnt.URL_NOTIFICATION_GET;
			break;
			case SET_SETTING:
				url = Flinnt.API_URL + Flinnt.URL_NOTIFICATION_SET;
			break;
		}
		return url;
	}

	public void sendMuteSettingRequest(){
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

				if( null == mMuteSettingRequest ) {
					mMuteSettingRequest = new MuteSettingRequest();
				}
				mMuteSettingRequest.setUserId(Config.getStringValue(Config.USER_ID));
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MuteSetting Request :\nUrl : " + url + "\nData : " + mMuteSettingRequest.getJSONString());

				JSONObject jsonObject = mMuteSettingRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MuteSetting Response :\n" + response.toString());

				if (mMuteSettingResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mMuteSettingResponse.getJSONData(response);
					if (null != jsonData) {
						mMuteSettingResponse.parseJSONObject(jsonData);
						mMuteSettingResponse.setCourseID(mMuteSettingRequest.getCourseID());
						sendMesssageToGUI(Flinnt.SUCCESS);
						/*
						if (mAccountType == ACCOUNT_SETTING ) {
							Config.setStringValue(Config.LAST_ACCOUNT_MUTE_SETTING_RESPONSE, jsonData.toString());
						}
						*/
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("MuteSetting Error : " + error.getMessage());
				mMuteSettingResponse.parseErrorResponse(error);
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
			msg.obj = mMuteSettingResponse;
			mHandler.sendMessage(msg);
		}
	}

	public MuteSettingRequest geMuteSettingRequest() {
		if( null == mMuteSettingRequest ) {
			mMuteSettingRequest = new MuteSettingRequest();
		}
		return mMuteSettingRequest;
	}

	public void setMuteSettingRequest(MuteSettingRequest mMuteSettingRequest) {
		this.mMuteSettingRequest = mMuteSettingRequest;
	}

	public int getRequestType() {
		return mRequestType;
	}

	public void setRequestType(int mRequestType) {
		this.mRequestType = mRequestType;
	}
}
