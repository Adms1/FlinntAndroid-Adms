package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.MenuStatisticsRequest;
import com.edu.flinnt.protocol.MenuStatisticsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class MenuStatistics {

	public static final String TAG = MenuStatistics.class.getSimpleName();
	public static MenuStatisticsResponse mMenuStatisticsResponse = null;
	public Handler mHandler = null;
	private String mStatFor = "";

	public MenuStatistics(Handler handler, String statFor){
		mHandler = handler;
		mStatFor = statFor;
		getResponse();
	}

	static public MenuStatisticsResponse getResponse() {
		if (mMenuStatisticsResponse == null) {
			mMenuStatisticsResponse = new MenuStatisticsResponse();
		}
		return mMenuStatisticsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_SLIDER_MENU_STATISTICS;
	}

	public void sendMenuStatisticsRequest(){
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
		synchronized (MenuStatistics.class) {
			try {
				String url = buildURLString();

				MenuStatisticsRequest menuStatisticsRequest = new MenuStatisticsRequest();
				menuStatisticsRequest.setUserId(Config.getStringValue(Config.USER_ID));
				if(mStatFor.equals(Flinnt.STAT_ALERTS) || mStatFor.equals(Flinnt.STAT_INVITATIONS)){
					menuStatisticsRequest.setStatFor(mStatFor);
				}
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MenuStatistics Request :\nUrl : " + url + "\nData : " + menuStatisticsRequest.getJSONString());

				JSONObject jsonObject = menuStatisticsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MenuStatistics response :\n" + response.toString());

				//.d("Menuu"," menu statistics response : "+response.toString());

				if (mMenuStatisticsResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mMenuStatisticsResponse.getJSONData(response);
					if (null != jsonData) {
						mMenuStatisticsResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("MenuStatistics Error : " + error.getMessage());

				mMenuStatisticsResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
		});
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setPriority(Priority.LOW);
		
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
			msg.obj = mMenuStatisticsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
