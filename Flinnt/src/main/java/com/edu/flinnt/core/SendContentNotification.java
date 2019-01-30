package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SendContentNotificationRequest;
import com.edu.flinnt.protocol.SendContentNotificationResponse;
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
public class SendContentNotification {
	public static final String TAG = CheckoutCourse.class.getSimpleName();
	public static SendContentNotificationResponse mSendContentNotificationResponse = null;
	public Handler mHandler = null;
	public String mCourseID = "";
	public String mContentID = "";

	public SendContentNotification(Handler handler, String courseID, String contnetId) {
		mHandler = handler;
		mCourseID = courseID;
		mContentID = contnetId;
		getLastResponse();
	}

	public SendContentNotificationResponse getLastResponse() {
		if (mSendContentNotificationResponse == null) {
			mSendContentNotificationResponse = new SendContentNotificationResponse();
		}
		return mSendContentNotificationResponse;
	}

	/**
	 * Generates appropriate URL string to make request
	 * @return request URL
	 */
	public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_CONTENT_SEND_NOTIFICATION;
	}

	public void sendNotificationRequest() {
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
		synchronized (CheckoutCourse.class) {
			try {
				String url = buildURLString();

				SendContentNotificationRequest sendContentNotificationRequest = new SendContentNotificationRequest();
				sendContentNotificationRequest.setUserID(Config.getStringValue(Config.USER_ID));
				sendContentNotificationRequest.setCourseID(mCourseID);
				sendContentNotificationRequest.setContentID(mContentID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SendContentNotification Request :\nUrl : " + url + "\nData : " + sendContentNotificationRequest.getJSONString());

				JSONObject jsonObject = sendContentNotificationRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SendContentNotification response :\n" + response.toString());

				if( null != mSendContentNotificationResponse && mSendContentNotificationResponse.isSuccessResponse(response)) {
					String sendContentNotificationResponse = new String(response.toString());
					JSONObject jsonData = mSendContentNotificationResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mSendContentNotificationResponse = gson.fromJson(sendContentNotificationResponse, SendContentNotificationResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SendContentNotification Error : " + error.getMessage());

				if( null != mSendContentNotificationResponse ) {
					mSendContentNotificationResponse.parseErrorResponse(error);
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
			msg.obj = mSendContentNotificationResponse;
			mHandler.sendMessage(msg);
		}
	}
}
