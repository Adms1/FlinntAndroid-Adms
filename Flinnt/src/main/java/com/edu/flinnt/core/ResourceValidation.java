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
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ResourceValidation {
	public Handler mHandler = null;
	public static ResourceValidationResponse mResourceValidationResponse = null;
	
	public int mValidationType = RESOURCE_VALIDATION;
	
	public static final int RESOURCE_VALIDATION 						= 1; 
	public static final int RESOURCE_VALIDATION_COURSE_PICTURE 			= 2;
	public static final int RESOURCE_VALIDATION_PROFILE_PICTURE 		= 3;
	public static final int RESOURCE_VALIDATION_BANNER_PICTURE 		= 4;
	
	public ResourceValidation(Handler handler, int type) {
		mHandler = handler;
		mValidationType = type;
		getResourceValidationResponse();
	}

	public static ResourceValidationResponse getResourceValidationResponse() {
		if (mResourceValidationResponse == null) {
			mResourceValidationResponse = new ResourceValidationResponse();
		}
		return mResourceValidationResponse;
	}

	public static ResourceValidationResponse getLastResourceValidationResponse(int validationType) {
		ResourceValidationResponse resourceValidationResponse = new ResourceValidationResponse();
		switch (validationType) {
			case RESOURCE_VALIDATION:
				if( !TextUtils.isEmpty( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_RESPONSE) ) ) {
					resourceValidationResponse.parseJSONString( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_RESPONSE) );
				}
			break;
			case RESOURCE_VALIDATION_COURSE_PICTURE:
				if( !TextUtils.isEmpty( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_COURCE_RESPONSE) ) ) {
					resourceValidationResponse.parseJSONString( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_COURCE_RESPONSE ) );
				}
			break;
			case RESOURCE_VALIDATION_PROFILE_PICTURE:
				if( !TextUtils.isEmpty( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE) ) ) {
					resourceValidationResponse.parseJSONString( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE) );
				}
			break;
			case RESOURCE_VALIDATION_BANNER_PICTURE:
				if( !TextUtils.isEmpty( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_BANNER_RESPONSE) ) ) {
					resourceValidationResponse.parseJSONString( Config.getStringValue(Config.LAST_RESOURCE_VALIDATION_BANNER_RESPONSE) );
				}
			break;
		}
		return resourceValidationResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		switch (mValidationType) {
			case RESOURCE_VALIDATION:
				url = Flinnt.API_URL + Flinnt.URL_RESOURCE_VALIDATION;
			break;
			case RESOURCE_VALIDATION_COURSE_PICTURE:
				url = Flinnt.API_URL + Flinnt.URL_VALIDATION_COURSE_PICTURE;
			break;
			case RESOURCE_VALIDATION_PROFILE_PICTURE:
				url = Flinnt.API_URL + Flinnt.URL_VALIDATION_PROFILE_PICTURE;
			break;
			case RESOURCE_VALIDATION_BANNER_PICTURE:
				url = Flinnt.API_URL + Flinnt.URL_VALIDATION_BANNER_PICTURE;
			break;
			default:
				url = Flinnt.API_URL + Flinnt.URL_RESOURCE_VALIDATION;
			break;
		}
		return url;
	}

	public void sendResourceValidationRequest(){
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

				sendJsonObjectRequest(url, null);

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ResourceValidation Response :\n" + response.toString());

				if (mResourceValidationResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mResourceValidationResponse.getJSONData(response);
					if (null != jsonData) {
						mResourceValidationResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
						switch (mValidationType) {
							case RESOURCE_VALIDATION:
								Config.setStringValue(Config.LAST_RESOURCE_VALIDATION_RESPONSE, jsonData.toString());
							break;
							case RESOURCE_VALIDATION_COURSE_PICTURE:
								Config.setStringValue(Config.LAST_RESOURCE_VALIDATION_COURCE_RESPONSE, jsonData.toString());
							break;
							case RESOURCE_VALIDATION_PROFILE_PICTURE:
								Config.setStringValue(Config.LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE, jsonData.toString());
							break;
							case RESOURCE_VALIDATION_BANNER_PICTURE:
								Config.setStringValue(Config.LAST_RESOURCE_VALIDATION_BANNER_RESPONSE, jsonData.toString());
							break;
						}
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ResourceValidation Error : " + error.getMessage());

				mResourceValidationResponse.parseErrorResponse(error);
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
		if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ResourceValidation mHandler : " + mHandler);
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mResourceValidationResponse;
			mHandler.sendMessage(msg);
		}
	}
}
