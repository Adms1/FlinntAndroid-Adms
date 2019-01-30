package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SelectUsersRequest;
import com.edu.flinnt.protocol.SelectUsersResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SelectUsers {

	public static final String TAG = SelectUsers.class.getSimpleName();
	public SelectUsersResponse mSelectUsersResponse = null;
	public Handler mHandler = null;
	public SelectUsersRequest mSelectUsersRequest = null, selectUsersRequestTemp;
	
	public SelectUsers(Handler handler, SelectUsersRequest SelectUsersRequest) {
		mHandler = handler;
		selectUsersRequestTemp = SelectUsersRequest;
		getResponse();
	}

	public SelectUsersResponse getResponse() {
		if (mSelectUsersResponse == null) {
			mSelectUsersResponse = new SelectUsersResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SelectUsers response : " + mSelectUsersResponse.toString());
		return mSelectUsersResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COURSE_USERS_LIST;
	}

	public void sendSelectUsersRequest() {
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
		synchronized (SelectUsers.class) {
			try {
				String url = buildURLString();
				
				if( null == mSelectUsersRequest ) {
					mSelectUsersRequest = selectUsersRequestTemp;
				}
				else {
					// Reset offset to new request - New offset = old offset + max
					mSelectUsersRequest.setOffset( mSelectUsersRequest.getOffset() + mSelectUsersRequest.getMax() );
				}
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectUsers Request :\nUrl : " + url + "\nData : " + mSelectUsersRequest.getJSONString());
				
				JSONObject jsonObject = mSelectUsersRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectUsers response :\n" + response.toString());

						if (mSelectUsersResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mSelectUsersResponse.getJSONData(response);
							if (null != jsonData) {
								mSelectUsersResponse.parseJSONObject(jsonData, mSelectUsersRequest.getSelected());
								
								sendMesssageToGUI(Flinnt.SUCCESS);
								
								/*if( mSelectUsersResponse.getHasMore() > 0 ) {
									sendRequest();
								}*/
							}
							else {
								sendMesssageToGUI(Flinnt.FAILURE);	
							}
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SelectUsers Error : " + error.getMessage());

						mSelectUsersResponse.parseErrorResponse(error);
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				});

		jsonObjReq.setShouldCache(false);
		// Adding request to request queue
		Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
	}


    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mSelectUsersResponse;
			mHandler.sendMessage(msg);
		}
	}

}
