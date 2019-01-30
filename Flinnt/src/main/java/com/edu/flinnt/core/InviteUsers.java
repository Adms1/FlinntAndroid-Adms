package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.InviteUsersRequest;
import com.edu.flinnt.protocol.InviteUsersResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class InviteUsers {
	public Handler mHandler = null;

	InviteUsersResponse mInviteUsersResponse = null;
	InviteUsersRequest mInviteUsersRequest = null;

	public int mInviteRole = Flinnt.COURSE_ROLE_LEARNER;
	public String sendTo;

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public InviteUsersRequest getInviteUsersRequest() {
		return mInviteUsersRequest;
	}

	public void setInviteUsersRequest(InviteUsersRequest mInviteUsersRequest) {
		this.mInviteUsersRequest = mInviteUsersRequest;
	}

	public int getInviteRole() {
		return mInviteRole;
	}

	public void setInviteRole(int mInviteRole) {
		this.mInviteRole = mInviteRole;
	}

	public InviteUsers(Handler handler) {
		mHandler = handler;
		getInviteUsersResponse();
	}

	public InviteUsersResponse getInviteUsersResponse() {
		if (mInviteUsersResponse == null) {
			mInviteUsersResponse = new InviteUsersResponse();
		}
		return mInviteUsersResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		url = Flinnt.API_URL + Flinnt.URL_COURSE_INVITATION_SEND;
		return url;
	}

	public void sendInviteUsersRequest(){
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

				if( null == mInviteUsersRequest ) {
					mInviteUsersRequest = new InviteUsersRequest();
				}
				mInviteUsersRequest.setUserId(Config.getStringValue(Config.USER_ID));

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("InviteUsers Request :\nUrl : " + url + "\nData : " + mInviteUsersRequest.getJSONString());

				JSONObject jsonObject = mInviteUsersRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("InviteUsers Response :\n" + response.toString());

				if (mInviteUsersResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mInviteUsersResponse.getJSONData(response);
					if (null != jsonData) {
						mInviteUsersResponse.parseJSONObject(jsonData);
						//Config.setStringValue(Config.LAST_ACCOUNT_COURSE_SETTING_RESPONSE, jsonData.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("InviteUsers Error : " + error.getMessage());
				mInviteUsersResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
		});
		jsonObjReq.setShouldCache(false);

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
			msg.obj = mInviteUsersResponse;
			mHandler.sendMessage(msg);
		}
	}

}
