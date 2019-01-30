package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CourseViewRequest;
import com.edu.flinnt.protocol.UnsubscribeCourseResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class UnsubsribeCourse {

	public static final String TAG = UnsubsribeCourse.class.getSimpleName();
	public UnsubscribeCourseResponse mUnsubscribeCourseResponse = null;
	public Handler mHandler = null;
    private String courseId;

	public UnsubsribeCourse(Handler handler, String courseId) {
		mHandler = handler;
		this.courseId = courseId;
		getLastResponse();
	}

	public UnsubscribeCourseResponse getLastResponse() {
		if (mUnsubscribeCourseResponse == null) {
            mUnsubscribeCourseResponse = new UnsubscribeCourseResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UnsubscribeCourseResponse : " + mUnsubscribeCourseResponse.toString());
		return mUnsubscribeCourseResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COURSE_SUBSCRIPTIONS_ROMOVE_SELF;
	}

	public void sendUnsubribeCourseRequest() {
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
		synchronized (UnsubsribeCourse.class) {
			try {
				String url = buildURLString();

                CourseViewRequest mCourseViewRequest = new CourseViewRequest();

                mCourseViewRequest.setUserId(Config.getStringValue(Config.USER_ID));
                mCourseViewRequest.setCourseId(courseId);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UnsubscribeCourse Request :\nUrl : " + url + "\nData : " + mCourseViewRequest.getJSONString());
				
				JSONObject jsonObject = mCourseViewRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("UnsubscribeCourseResponse :\n" + response.toString());

						if (mUnsubscribeCourseResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mUnsubscribeCourseResponse.getJSONData(response);
							if (null != jsonData) {
                                mUnsubscribeCourseResponse.parseJSONObject(jsonData);

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
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("WishList Error : " + error.getMessage());

                        mUnsubscribeCourseResponse.parseErrorResponse(error);
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
			msg.obj = mUnsubscribeCourseResponse;
			mHandler.sendMessage(msg);
		}
	}

}
