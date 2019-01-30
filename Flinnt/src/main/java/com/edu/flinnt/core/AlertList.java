package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.protocol.AlertListResponse;
import com.edu.flinnt.protocol.DrawerItemListRequest;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Send request and get response and pass it to GUI
 */
public class AlertList {

	public static final String TAG = AlertList.class.getSimpleName();
	public AlertListResponse mAlertListResponse = null;
	public DrawerItemListRequest mAlertListRequest = null;
	
	public static final int REQUEST_TYPE_ALERT_LIST = 1;
	public static final int REQUEST_TYPE_ALERT_DETAIL = 2;
	public static final int REQUEST_TYPE_ALERT_REMOVE = 3;
	public int requestTupe = Flinnt.INVALID;
	
	public Handler mHandler = null;
	ArrayList<Integer> mPostTypes = new ArrayList<Integer>();
	private String alertID = "";
	private String alertText 		= ""; 
	//	alert_text:
	//	Alert Description text
	
	public AlertList(Handler handler, int type) {
		mHandler = handler;
		requestTupe = type;
		getAlertListResponse();
	}

	public AlertListResponse getAlertListResponse() {
		if (mAlertListResponse == null) {
			mAlertListResponse = new AlertListResponse();
			//mMyCoursesResponse.parseJSONString( Config.getStringValue(Config.LAST_MY_COURSES_RESPONSE) );
		}
		//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("response : " + mAlertListResponse.toString() );
		return mAlertListResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {

		switch (requestTupe) {
		case REQUEST_TYPE_ALERT_LIST:
			return Flinnt.API_URL + Flinnt.URL_ALERT_LIST;

		case REQUEST_TYPE_ALERT_DETAIL:
			return Flinnt.API_URL + Flinnt.URL_ALERT_DETAIL;

		case REQUEST_TYPE_ALERT_REMOVE:
			return Flinnt.API_URL + Flinnt.URL_ALERT_DELETE;

		default:
			return "";
		}
	}

	public void sendAlertListRequest() {
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
		synchronized (AlertList.class) {
			try {
				String url = buildURLString();
				JSONObject jsonObject;
				if( REQUEST_TYPE_ALERT_LIST == requestTupe ) {
					if( null == mAlertListRequest ) {
						mAlertListRequest = new DrawerItemListRequest();
						mAlertListRequest.setUserID( Config.getStringValue(Config.USER_ID) );
						mAlertListResponse.clearAlertList();
					}
					else {
						// Reset offset to new request - New offset = old offset + max
						mAlertListRequest.setOffset( mAlertListRequest.getOffset() + mAlertListRequest.getMax() );
					}
					jsonObject = mAlertListRequest.getJSONObject();
				} else if( REQUEST_TYPE_ALERT_DETAIL == requestTupe ) {
					jsonObject = new JSONObject();
					try {
						jsonObject.put(LoginResponse.USER_ID_KEY, Config.getStringValue(Config.USER_ID));
						if( !TextUtils.isEmpty(getAlertID()) )		jsonObject.put(Alert.ALERT_ID_KEY, getAlertID());
						if( !TextUtils.isEmpty(getAlertText()) )	jsonObject.put(Alert.ALERT_TEXT_KEY, getAlertText());
					}
					catch(Exception e) {
						LogWriter.err(e);
					}
				}else if( REQUEST_TYPE_ALERT_REMOVE == requestTupe ) {
					jsonObject = new JSONObject();
					try {
						jsonObject.put(LoginResponse.USER_ID_KEY, Config.getStringValue(Config.USER_ID));
						if( !TextUtils.isEmpty(getAlertID()) )		jsonObject.put(Alert.ALERT_ID_KEY, getAlertID());
					}
					catch(Exception e) {
						LogWriter.err(e);
					}
				} else {
					jsonObject = new JSONObject();
				    try {
				    	jsonObject.put(LoginResponse.USER_ID_KEY, Config.getStringValue(Config.USER_ID));  
						if( !TextUtils.isEmpty(getAlertID()) )		jsonObject.put(Alert.ALERT_ID_KEY, getAlertID());
						if( !TextUtils.isEmpty(getAlertText()) )	jsonObject.put(Alert.ALERT_TEXT_KEY, getAlertText());
				    }
				    catch(Exception e) {
				    	LogWriter.err(e);
				    }
				}
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AlertList Request :\nUrl : " + url + "\nData : " + jsonObject.toString() );

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

		CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
				jsonObject, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AlertList Response :\n" + response.toString());

						if (mAlertListResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mAlertListResponse.getJSONData(response);
							if (null != jsonData) {
								mAlertListResponse.parseJSONObject(jsonData);
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AlertList Error : " + error.getMessage());

						mAlertListResponse.parseErrorResponse(error);
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}
		);
		jsonObjReq.setPriority(Priority.HIGH);

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
			msg.obj = mAlertListResponse;
			mHandler.sendMessage(msg);
		}
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getAlertText() {
		return alertText;
	}

	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}

}
