package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.UserProfileRequest;
import com.edu.flinnt.protocol.UserProfileResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class UserProfileGet {

	public static final String TAG = UserProfileGet.class.getSimpleName();

	public Handler mHandler = null;
	public UserProfileRequest mUserProfileRequest = null;
	public UserProfileResponse mUserProfileResponse = null;
	private String userID = "",courseID = "";

	public UserProfileGet(Handler handler, UserProfileRequest request, String userId,String courseiD) {
		mHandler = handler;
		mUserProfileRequest = request;
		userID = userId;
		courseID = courseiD;
		getResponse();
	}

	public UserProfileResponse getResponse() {
		if (mUserProfileResponse == null) {
			mUserProfileResponse = new UserProfileResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UserProfileGet response : " + mUserProfileResponse.toString());
		return mUserProfileResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_USER_PROFILE_GET;
	}

	public void sendUserProfileGetRequest() {
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

				if( null == mUserProfileRequest) {
					mUserProfileRequest = new UserProfileRequest();
				}

				mUserProfileRequest.setUserId(userID);
				mUserProfileRequest.setCourseId(courseID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UserProfileGet Request :Url : " +url+"\n"+mUserProfileRequest.getJSONString());

				JSONObject jsonObject = mUserProfileRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UserProfileGet Response :\n" + response.toString());

				if (mUserProfileResponse.isSuccessResponse(response)) {

					String userProfileResponse = new String(response.toString());
					JSONObject jsonData = mUserProfileResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mUserProfileResponse = gson.fromJson(userProfileResponse, UserProfileResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("UserProfileGet Error : " + error.getMessage());

				mUserProfileResponse.parseErrorResponse(error);
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
			msg.obj = mUserProfileResponse;
			mHandler.sendMessage(msg);
		}
	}

}
