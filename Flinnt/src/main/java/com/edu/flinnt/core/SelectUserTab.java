package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SelectUserTabResponse;
import com.edu.flinnt.protocol.SelectUsersTabRequest;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SelectUserTab {

	public static final String TAG = SelectUserTab.class.getSimpleName();
	public static SelectUserTabResponse mSelectUserTabResponse = null;
	public Handler mHandler = null;
	private String mUserID, mCourseID;
	
	public SelectUserTab(Handler handler, String userID, String courseID) {
		mHandler = handler;
		mUserID = userID;
		mCourseID = courseID;
		getResponse();
	}

	public static SelectUserTabResponse getResponse() {
		if (mSelectUserTabResponse == null) {
			mSelectUserTabResponse = new SelectUserTabResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SelectUserTab response : " + mSelectUserTabResponse.toString());
		return mSelectUserTabResponse;
	}

    /**
     * Generates the url for message role
     * @return message role url
     */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COURSE_MESSAGE_ROLE;
	}

	public void sendSelectUserTabRequest() {
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
		synchronized (SelectUserTab.class) {
			try {
				String url = buildURLString();
				
				SelectUsersTabRequest mSelectUserTabRequest = new SelectUsersTabRequest();
				mSelectUserTabRequest.setUserID(mUserID);
				mSelectUserTabRequest.setCourseID(mCourseID);
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectUserTab Request :\nUrl : " + url + "\nData : " + mSelectUserTabRequest.getJSONString());
				
				JSONObject jsonObject = mSelectUserTabRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectUserTab response :\n" + response.toString());

						if (mSelectUserTabResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mSelectUserTabResponse.getJSONData(response);
							if (null != jsonData) {
								mSelectUserTabResponse.parseJSONObject(jsonData);
								
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SelectUserTab Error : " + error.getMessage());

						mSelectUserTabResponse.parseErrorResponse(error);
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
			msg.obj = mSelectUserTabResponse;
			mHandler.sendMessage(msg);
		}
	}

}
