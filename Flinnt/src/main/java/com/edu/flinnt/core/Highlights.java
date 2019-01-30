package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.HighlightsRequest;
import com.edu.flinnt.protocol.HighlightsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class Highlights {

	public static final String TAG = Highlights.class.getSimpleName();
	public static HighlightsResponse mHighlightResponse = null;
	public Handler mHandler = null;
	
	public Highlights(Handler handler) {
		mHandler = handler;
		getLastResponse();
	}

	public static HighlightsResponse getLastResponse() {
		if (mHighlightResponse == null) {
			mHighlightResponse = new HighlightsResponse();
			//mHighlightResponse.parseJSONString( Config.getStringValue(Config.LAST_HIGHLIGHTS_RESPONSE) );
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Highlight response : " + mHighlightResponse.toString());
		return mHighlightResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_HIGHLIGHT;
	}

	public void sendHighlightRequest() {
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
		synchronized (Highlights.class) {
			try {
				String url = buildURLString();

				HighlightsRequest highlightsRequest = new HighlightsRequest();
				highlightsRequest.setUserId( Config.getStringValue(Config.USER_ID) );		

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Highlights Request :\nUrl : " + url + "\nData : " + highlightsRequest.getJSONString());

				JSONObject jsonObject = highlightsRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("Highlight Response :\n" + response.toString());
						//Log.d("Menuu"," highlight response : "+response.toString());
						if( null != mHighlightResponse && mHighlightResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mHighlightResponse.getJSONData(response);
							if (null != jsonData) {
								mHighlightResponse.parseJSONObject(jsonData);
								//Config.setStringValue(Config.LAST_HIGHLIGHTS_RESPONSE, jsonData.toString());
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Highlight Error : " + error.getMessage());

						if( null != mHighlightResponse ) {
						mHighlightResponse.parseErrorResponse(error);
						sendMesssageToGUI(Flinnt.FAILURE);
						}
					}
				}
		);
		jsonObjReq.setPriority(Priority.LOW);
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
			msg.obj = mHighlightResponse;
			mHandler.sendMessage(msg);
		}
	}

}
