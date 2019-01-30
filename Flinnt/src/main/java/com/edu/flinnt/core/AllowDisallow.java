package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AllowDisallowRequest;
import com.edu.flinnt.protocol.AllowDisallowResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class AllowDisallow {
	public Handler mHandler = null;
//	public static ResourceValidationResponse mResourceValidationResponse = null;
	
	public AllowDisallowRequest mAllowDisallowRequest = null;
	public AllowDisallowResponse mAllowDisallowResponse = null;

	public int mOperationType; 
	
	public static final int ALLOW_COMMENT								= 1;
	public static final int DISALLOW_COMMENT							= 2;
	public static final int ALLOW_MESSAGE								= 3;
	public static final int DISALLOW_MESSAGE							= 4;
	public static final int REMOVE_FROM_COURSE							= 5;
	
	public AllowDisallow(Handler handler, int type, AllowDisallowRequest allowDisallowRequest) {
		mHandler = handler;
		mOperationType = type;
		mAllowDisallowRequest = allowDisallowRequest;
		getUsersResponse();
	}

	public AllowDisallowResponse getUsersResponse() {
		if (mAllowDisallowResponse == null) {
			mAllowDisallowResponse = new AllowDisallowResponse();
		}
		return mAllowDisallowResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		switch (mOperationType) {
			case ALLOW_COMMENT:  		// Replace these URLs
				url = Flinnt.API_URL + Flinnt.URL_COURSE_COMMENT_ALLOW;
			break;
			case DISALLOW_COMMENT:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_COMMENT_DISALLOW;
			break;
			case ALLOW_MESSAGE:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_MESSAGE_ALLOW;
			break;
			case DISALLOW_MESSAGE:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_MESSAGE_DISALLOW;
			break;
			case REMOVE_FROM_COURSE:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_REMOVE;
			break;
			default:
			break;
		}
		return url;
	}

	public void sendAllowDisallowRequest(){
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

				if( null == mAllowDisallowRequest ) {
					mAllowDisallowRequest = new AllowDisallowRequest();
				}
				mAllowDisallowRequest.setUserID(Config.getStringValue(Config.USER_ID));
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Allow/Disallow Request :\nUrl : " + url + "\nData : " + mAllowDisallowRequest.getJSONString());

				JSONObject jsonObject = mAllowDisallowRequest.getJSONObject();
				
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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Allow/Disallow Response :\n" + response.toString());

				if (mAllowDisallowResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mAllowDisallowResponse.getJSONData(response);
					if (null != jsonData) {
						mAllowDisallowResponse.parseJSONObject(jsonData);
						mAllowDisallowResponse.setUsers(mAllowDisallowRequest.getUsers());
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
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Allow/Disallow Error : " + error.getMessage());

				mAllowDisallowResponse.parseErrorResponse(error);
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
			msg.obj = mAllowDisallowResponse;
			mHandler.sendMessage(msg);
		}
	}
}
