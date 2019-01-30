package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseSettingRequest;
import com.edu.flinnt.protocol.CourseSettingResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseSetting {
	public Handler mHandler = null;
	public CourseSettingResponse mCourseSettingResponse = null;
	CourseSettingRequest mCourseSettingRequest = null;

	public int mRequestType = GET_SETTING;
	
	public static final int GET_SETTING 			= 20; 
	public static final int SET_SETTING 			= 21;
	
	public CourseSettingRequest getCourseSettingRequest() {
		return mCourseSettingRequest;
	}

	public void setCourseSettingRequest(CourseSettingRequest mCourseSettingRequest) {
		this.mCourseSettingRequest = mCourseSettingRequest;
	}

	public int getRequestType() {
		return mRequestType;
	}

	public void setRequestType(int mRequestType) {
		this.mRequestType = mRequestType;
	}
	
	public CourseSetting(Handler handler) {
		mHandler = handler;
		getCourseSettingResponse();
	}

	public CourseSettingResponse getCourseSettingResponse() {
		if (mCourseSettingResponse == null) {
			mCourseSettingResponse = new CourseSettingResponse();
		}
		return mCourseSettingResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		String url = "";
		switch (mRequestType) {
			case GET_SETTING:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_SETTING_GET;
			break;
			case SET_SETTING:
				url = Flinnt.API_URL + Flinnt.URL_COURSE_SETTING_SET;
			break;
		}
		return url;
	}

	public void sendCourseSettingRequest(){
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

				if( null == mCourseSettingRequest ) {
					mCourseSettingRequest = new CourseSettingRequest();
				}
				mCourseSettingRequest.setUserId(Config.getStringValue(Config.USER_ID));
//				mCourseSettingRequest.setCourseID(courseID);
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CourseSetting Request :\nUrl : " + url + "\nData : " + mCourseSettingRequest.getJSONString());

				JSONObject jsonObject = mCourseSettingRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CourseSetting Response :\n" + response.toString());

				if (mCourseSettingResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mCourseSettingResponse.getJSONData(response);
					if (null != jsonData) {
						mCourseSettingResponse.parseJSONObject(jsonData);
						//Config.setStringValue(Config.LAST_ACCOUNT_COURSE_SETTING_RESPONSE, jsonData.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("CourseSetting Error : " + error.getMessage());
				mCourseSettingResponse.parseErrorResponse(error);
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
			msg.obj = mCourseSettingResponse;
			mHandler.sendMessage(msg);
		}
	}

}
