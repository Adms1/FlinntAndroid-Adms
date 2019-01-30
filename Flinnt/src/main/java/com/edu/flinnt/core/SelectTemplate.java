package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SelectTemplateResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SelectTemplate {
	public Handler mHandler = null;
	public SelectTemplateResponse mSelectTemplateResponse = null;

	public SelectTemplate(Handler handler) {
		mHandler = handler;
		getSelectTemplateResponse();
	}

	public SelectTemplateResponse getSelectTemplateResponse() {
		if (mSelectTemplateResponse == null) {
			mSelectTemplateResponse = new SelectTemplateResponse();
		}
		return mSelectTemplateResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
				url = Flinnt.API_URL + Flinnt.URL_POST_TEMPLATES_LIST;
		return url;
	}

	public void sendSelectTemplateRequest(){
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

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectTemplate Request :\nUrl : " + url);

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

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
				jsonObject, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectTemplate Response :\n" + response.toString());

				if (mSelectTemplateResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mSelectTemplateResponse.getJSONData(response);
					if (null != jsonData) {
						mSelectTemplateResponse.parseJSONObject(jsonData);
						//Config.setStringValue(Config.LAST_ACCOUNT_COURSE_SETTING_RESPONSE, jsonData.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SelectTemplate Error : " + error.getMessage());
				mSelectTemplateResponse.parseErrorResponse(error);
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
			msg.obj = mSelectTemplateResponse;
			mHandler.sendMessage(msg);
		}
	}

}
