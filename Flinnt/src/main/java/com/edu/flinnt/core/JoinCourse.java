package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.JoinCourseRequest;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class JoinCourse {

	public static final String TAG = JoinCourse.class.getSimpleName();
	public static JoinCourseResponse mJoinCourseResponse = null;
	public Handler mHandler = null;
	public String code = "";
	
	public JoinCourse(Handler handler, String code) {
		mHandler = handler;
		this.code = code;
		getLastResponse();
	}

	public static JoinCourseResponse getLastResponse() {
		if (mJoinCourseResponse == null) {
			mJoinCourseResponse = new JoinCourseResponse();
			//mJoinCourseResponse.parseJSONString( Config.getStringValue(Config.LAST_JoinCourse_RESPONSE) );
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("JoinCourse response : " + mJoinCourseResponse.toString());
		return mJoinCourseResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_JOIN_COURSE;
	}

	public void sendJoinCourseRequest() {
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
		synchronized (JoinCourse.class) {
			try {
				String url = buildURLString();

				JoinCourseRequest JoinCourseRequest = new JoinCourseRequest();
				
				JoinCourseRequest.setUserID(Config.getStringValue(Config.USER_ID));
				JoinCourseRequest.setCode(code);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("JoinCourse Request :\nUrl : " + url + "\nData : " + JoinCourseRequest.getJSONString());
				
				JSONObject jsonObject = JoinCourseRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("JoinCourse response :\n" + response.toString());

						if (mJoinCourseResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mJoinCourseResponse.getJSONData(response);
							if (null != jsonData) {
								mJoinCourseResponse.parseJSONObject(jsonData);
								
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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("JoinCourse Error : " + error.getMessage());

						mJoinCourseResponse.parseErrorResponse(error);
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
			msg.obj = mJoinCourseResponse;
			mHandler.sendMessage(msg);
		}
	}

}
