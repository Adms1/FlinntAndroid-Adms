package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.UpdateBirthdateRequest;
import com.edu.flinnt.protocol.UpdateBirthdateResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

/**
 * Send request and get response and pass it to GUI
 */
public class UpdateBirthdate {


	public static final String TAG = UpdateBirthdate.class.getSimpleName();
	public static UpdateBirthdateResponse mUpdateBirthdateResponse = null;
	public Handler mHandler = null;
	public int mDobDay,mDobMonth,mDobYear;

	public UpdateBirthdate(Handler handler, int dobDay, int dobMonth, int dobYear) {
		mHandler = handler;
		mDobDay = dobDay;
		mDobMonth = dobMonth;
		mDobYear = dobYear;
		getLastResponse();
	}

	public UpdateBirthdateResponse getLastResponse() {
		if (mUpdateBirthdateResponse == null) {
			mUpdateBirthdateResponse = new UpdateBirthdateResponse();
		}
		return mUpdateBirthdateResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_UPDATE_PROFILE_BIRTHDATE_KEY;
	}

	public void sendUpdateBirthdateRequest() {
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
		synchronized (UpdateBirthdate.class) {
			try {
				String url = buildURLString();
				
				UpdateBirthdateRequest updateBirthdateRequest = new UpdateBirthdateRequest();
				updateBirthdateRequest.setUserID(Config.getStringValue(Config.USER_ID));
				updateBirthdateRequest.setDobDay(mDobDay);
				updateBirthdateRequest.setDobMonth(mDobMonth);
				updateBirthdateRequest.setDobYear(mDobYear);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UpdateBirthdate Request :\nUrl : " + url + "\nData : " + updateBirthdateRequest.getJSONString());

				JSONObject jsonObject = updateBirthdateRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UpdateBirthdate response :\n" + response.toString());

				if( null != mUpdateBirthdateResponse && mUpdateBirthdateResponse.isSuccessResponse(response)) {
					String updateBirthdateResponse = new String(response.toString());
					JSONObject jsonData = mUpdateBirthdateResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mUpdateBirthdateResponse = gson.fromJson(updateBirthdateResponse, UpdateBirthdateResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("UpdateBirthdate Error : " + error.getMessage());

				if( null != mUpdateBirthdateResponse ) {
					mUpdateBirthdateResponse.parseErrorResponse(error);
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
			msg.obj = mUpdateBirthdateResponse;
			mHandler.sendMessage(msg);
		}
	}

}
