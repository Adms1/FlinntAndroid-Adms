package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.InvitationRequest;
import com.edu.flinnt.protocol.InvitationResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class Invitation {

	public static final String TAG = Invitation.class.getSimpleName();
	public static InvitationResponse mInvitationResponse = null;
	public InvitationRequest mInvitationRequest = null;
	public Handler mHandler = null;
	String mInvitationID = "";
    String userID;
	public int mRrequestType = Flinnt.INVALID;
	public static int ACCEPT = 1;
	public static int REJECT = 0;
	
	public Invitation(Handler handler, String invitationID, int requestType, String userId) {
		mHandler = handler;
		mInvitationID = invitationID;
		mRrequestType = requestType;
        userID = userId;
		getInvitationResponse();
	}

	public static InvitationResponse getInvitationResponse() {
		if (mInvitationResponse == null) {
			mInvitationResponse = new InvitationResponse();
		}
		//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("response : " + mInvitationResponse.toString() );
		return mInvitationResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		if(mRrequestType == ACCEPT){
			return Flinnt.API_URL + Flinnt.URL_COURSE_INVITATION_ACCEPT;
		}else{
			return Flinnt.API_URL + Flinnt.URL_COURSE_INVITATION_REJECT;
		}
	}

	public void sendInvitationRequest() {
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
		synchronized (Invitation.class) {
			try {
				String url = buildURLString();
				if( null == mInvitationRequest ) {
					mInvitationRequest = new InvitationRequest();
					mInvitationRequest.setUserID( userID );
				}
				mInvitationRequest.setInvitationID(mInvitationID);
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Invitation Request :\nUrl : " + url + "\nData : " + mInvitationRequest.getJSONString());

				JSONObject jsonObject = mInvitationRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Invitation Response :\n" + response.toString());
						
						if (mInvitationResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mInvitationResponse.getJSONData(response);
							if (null != jsonData) {
								mInvitationResponse.parseJSONObject(jsonData);
								//Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Invitation Error : " + error.getMessage());

						mInvitationResponse.parseErrorResponse(error);
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
			msg.obj = mInvitationResponse;
			mHandler.sendMessage(msg);
		}
	}
}
