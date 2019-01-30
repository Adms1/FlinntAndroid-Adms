package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.RegisterDeviceRequest;
import com.edu.flinnt.protocol.RegisterDeviceResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class RegisterDevice {

	public static final String TAG = RegisterDevice.class.getSimpleName();
	public static RegisterDeviceResponse mRegisterDeviceResponse = null;
	public Handler mHandler = null;

	public int mRequestType = REGISTER_DEVICE;
	public static final int REGISTER_DEVICE = 1;
	public static final int UNREGISTER_DEVICE = 2;
    private String userId, mRegistrationId = "";
	
	public RegisterDevice(Handler handler, int requestType, String id) {
		mHandler = handler;
		mRequestType = requestType;
        userId = id;
		mRegistrationId = "";//Config.getStringValue(Config.FCM_TOKEN);
		getLastResponse();
	}

	public RegisterDevice(Handler handler, int requestType, String id,String regID) {
		mHandler = handler;
		mRequestType = requestType;
		userId = id;
		mRegistrationId = regID;
		getLastResponse();
	}


	public static RegisterDeviceResponse getLastResponse() {
		if (mRegisterDeviceResponse == null) {
			mRegisterDeviceResponse = new RegisterDeviceResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("RegisterDevice response : " + mRegisterDeviceResponse.toString());
		return mRegisterDeviceResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		if( mRequestType == REGISTER_DEVICE ) {
			return Flinnt.API_URL + Flinnt.URL_REGISTER_DEVICE;
		}
		else {
			return Flinnt.API_URL + Flinnt.URL_UNREGISTER_DEVICE;
		}
	}

	public void sendRegisterDeviceRequest() {
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
		synchronized (RegisterDevice.class) {
			try {
				String url = buildURLString();
				if(mRegistrationId.equalsIgnoreCase("")){
					while (TextUtils.isEmpty(Config.getStringValue(Config.FCM_TOKEN))){

					}
					mRegistrationId = Config.getStringValue(Config.FCM_TOKEN);
				}
				RegisterDeviceRequest registerDeviceRequest = new RegisterDeviceRequest();
				registerDeviceRequest.setUserId(userId);

				registerDeviceRequest.setRegId(mRegistrationId);
				registerDeviceRequest.setNotificationType(Flinnt.NOTIFICATION_TYPE_FCM);
                if (mRequestType == REGISTER_DEVICE ) {
                    registerDeviceRequest.setAppVersion(Helper.getAppVersionName(FlinntApplication.getContext()));
                }
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("RegisterDevice Request :\nUrl : " + url + "\nData : " + registerDeviceRequest.getJSONString() + "\nUserId : " + userId);

				JSONObject jsonObject = registerDeviceRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("RegisterDevice Response :\n" + response.toString());

				if (mRegisterDeviceResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mRegisterDeviceResponse.getJSONData(response);
					if (null != jsonData) {
						mRegisterDeviceResponse.parseJSONObject(jsonData);
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("mRegisterDeviceResponse :\n" + mRegisterDeviceResponse.toString() + "\nuserId : " + userId);

						// You should store a boolean that indicates whether the generated token has been
			            // sent to your server. If the boolean is false, send the token to your server,
			            // otherwise your server should have already received the token.
						if( mRequestType == REGISTER_DEVICE && mRegisterDeviceResponse.getResult() > 0 ) {
                            UserInterface.getInstance().setToken(userId, Flinnt.TRUE);
                            if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Token update : " + userId + " -> " + Flinnt.TRUE);
                        }
						else {
                            UserInterface.getInstance().setToken(userId, Flinnt.FALSE);
                            if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Token update : " + userId + " -> " + Flinnt.FALSE);
                        }

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
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("RegisterDevice Error : " + error.getMessage());

				mRegisterDeviceResponse.parseErrorResponse(error);
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
			msg.obj = mRegisterDeviceResponse;
			mHandler.sendMessage(msg);
		}
	}
}
