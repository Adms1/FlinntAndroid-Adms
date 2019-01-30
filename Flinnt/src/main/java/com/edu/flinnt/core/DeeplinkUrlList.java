package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.DeeplinkUrlResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class DeeplinkUrlList {

	public static final String TAG = DeeplinkUrlList.class.getSimpleName();
	public DeeplinkUrlResponse mDeeplinkUrlResponse = null;

	public Handler mHandler = null;

	public DeeplinkUrlList(Handler handler) {
		mHandler = handler;
		getDeeplinkUrlResponse();
	}

	public DeeplinkUrlResponse getDeeplinkUrlResponse() {
		if (mDeeplinkUrlResponse == null) {
			mDeeplinkUrlResponse = new DeeplinkUrlResponse();
		}
		return mDeeplinkUrlResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {

		return Flinnt.API_URL + Flinnt.URL_DEEPLINK_LIST;
	}

	public void sendDeeplinkUrlRequest() {
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
		synchronized (DeeplinkUrlList.class) {
			try {
				String url = buildURLString();
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeeplinkUrl Request :\nUrl : " + url);

				sendJsonObjectRequest(url, null);

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("DeeplinkUrl Response :\n" + response.toString());

						//Log.d("Deepp","DeeplinkUrl Response :\n" + response.toString());

						if (mDeeplinkUrlResponse.isSuccessResponse(response)) {

							String deeplinkUrlResponse = new String(response.toString());
							JSONObject jsonData = mDeeplinkUrlResponse.getJSONData(response);

							if (null != jsonData) {
								Gson gson = new Gson();
								mDeeplinkUrlResponse = gson.fromJson(deeplinkUrlResponse, DeeplinkUrlResponse.class);
								sendMesssageToGUI(Flinnt.SUCCESS);

							} else {
								sendMesssageToGUI(Flinnt.FAILURE);
							}
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AlertList Error : " + error.getMessage());

						mDeeplinkUrlResponse.parseErrorResponse(error);
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
			msg.obj = mDeeplinkUrlResponse;
			mHandler.sendMessage(msg);
		}
	}


}
