package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.AppUpdateRequest;
import com.edu.flinnt.protocol.AppUpdateResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyConfig;

import org.json.JSONObject;

import java.io.File;

/**
 * Send request and get response and pass it to GUI
 */
public class AppUpdate {

	public static final String TAG = AppUpdate.class.getSimpleName();
	public static AppUpdateResponse mAppUpdateResponse = null;
	public Handler mHandler = null;
	public String mCurrentVersion = "";

	public AppUpdate(Handler handler, String currentVersion) {
		mHandler = handler;
		mCurrentVersion = currentVersion;
		getLastResponse();
	}

	public static AppUpdateResponse getLastResponse() {
		if (mAppUpdateResponse == null) {
			mAppUpdateResponse = new AppUpdateResponse();
		}
		mAppUpdateResponse.parseJSONString( Config.getStringValue(Config.LAST_APP_UPDATE_RESPONSE) );
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AppUpdate response : " + mAppUpdateResponse.toString());
		return mAppUpdateResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_DEVICE_APP_UPDATE;
	}

	public void sendAppUpdateRequest() {
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
		synchronized (AppUpdate.class) {
			try {
				String url = buildURLString();
				
				AppUpdateRequest appUpdateRequest = new AppUpdateRequest();
				appUpdateRequest.setCurrentVersion(mCurrentVersion);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AppUpdate Request :\nUrl : " + url + "\nData : " + appUpdateRequest.getJSONString());

				JSONObject jsonObject = appUpdateRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AppUpdate response :\n" + response.toString());

				if( null != mAppUpdateResponse && mAppUpdateResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mAppUpdateResponse.getJSONData(response);
					if (null != jsonData) {
						mAppUpdateResponse.parseJSONObject(jsonData);

						sendMesssageToGUI(Flinnt.SUCCESS);
						Config.setStringValue(Config.LAST_APP_UPDATE_RESPONSE, jsonData.toString());

					}
					else {
						sendMesssageToGUI(Flinnt.FAILURE);	
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AppUpdateError : " + error.getMessage());

				if( null != mAppUpdateResponse ) {
				mAppUpdateResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
			}
		});

		jsonObjReq.setShouldCache(false);
		// Adding request to request queue
		Requester.getInstance().addToRequestQueue(jsonObjReq);
		
		/** remove old unwanted files...*/
  		Helper.deleteOldFiles(new File(MyConfig.FLINNT_FOLDER_PATH));
	}


    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mAppUpdateResponse;
			mHandler.sendMessage(msg);
		}
	}

}
