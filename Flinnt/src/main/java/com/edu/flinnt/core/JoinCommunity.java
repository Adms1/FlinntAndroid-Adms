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
public class JoinCommunity {

	public static final String TAG = JoinCommunity.class.getSimpleName();
	public static JoinCourseResponse joinCourseResponse = null;
	public Handler mHandler = null;
	public String courseID = "";
    public boolean isPublicCourse;

	public JoinCommunity(Handler handler, String courseID, boolean isPublicCourse) {
		mHandler = handler;
		this.courseID = courseID;
        this.isPublicCourse = isPublicCourse;
		getLastResponse();
	}

	public static JoinCourseResponse getLastResponse() {
		if (joinCourseResponse == null) {
            joinCourseResponse = new JoinCourseResponse();
			//mJoinCourseResponse.parseJSONString( Config.getStringValue(Config.LAST_JoinCourse_RESPONSE) );
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("JoinCourse response : " + joinCourseResponse.toString());
		return joinCourseResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        if (isPublicCourse) {
            return Flinnt.API_URL + Flinnt.URL_COURSE_JOIN_PUBLIC;
        }
		return Flinnt.API_URL + Flinnt.URL_COURSE_COMMUNITY_JOIN;
	}

	public void sendJoinCommunityRequest() {
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
		synchronized (JoinCommunity.class) {
			try {
				String url = buildURLString();

				JoinCourseRequest JoinCourseRequest = new JoinCourseRequest();

				JoinCourseRequest.setUserID(Config.getStringValue(Config.USER_ID));
				JoinCourseRequest.setCourseID(courseID);

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

						if (joinCourseResponse.isSuccessResponse(response)) {

							JSONObject jsonData = joinCourseResponse.getJSONData(response);
							if (null != jsonData) {
                                joinCourseResponse.parseJSONObject(jsonData);
								
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

                        joinCourseResponse.parseErrorResponse(error);
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
			msg.obj = joinCourseResponse;
			mHandler.sendMessage(msg);
		}
	}

}
