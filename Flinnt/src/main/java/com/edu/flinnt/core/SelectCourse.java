package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.SelectCourseResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SelectCourse {

	public static final String TAG = SelectCourse.class.getSimpleName();
	public static SelectCourseResponse mSelectCourseResponse = null;
	public Handler mHandler = null;
	public SelectCourseRequest mSelectCourseRequest = null, selectCourseRequestTemp;
	
	public SelectCourse(Handler handler, SelectCourseRequest selectCourseRequest) {
		mHandler = handler;
		selectCourseRequestTemp = selectCourseRequest;
		getResponse();
	}

	public static SelectCourseResponse getResponse() {
		if (mSelectCourseResponse == null) {
			mSelectCourseResponse = new SelectCourseResponse();
		}
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SelectCourse response : " + mSelectCourseResponse.toString());
		return mSelectCourseResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COURSE_LIST;
	}

	public void sendSelectCourseRequest() {
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
		synchronized (SelectCourse.class) {
			try {
				String url = buildURLString();
				
				if( null == mSelectCourseRequest ) {
					mSelectCourseRequest = selectCourseRequestTemp;
				}
				else {
					// Reset offset to new request - New offset = old offset + max
					mSelectCourseRequest.setOffset( mSelectCourseRequest.getOffset() + mSelectCourseRequest.getMax() );
				}
				
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectCourse Request :\nUrl : " + url + "\nData : " + mSelectCourseRequest.getJSONString());
				
				JSONObject jsonObject = mSelectCourseRequest.getJSONObject();

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
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SelectCourse response :\n" + response.toString());

						if (mSelectCourseResponse.isSuccessResponse(response)) {

							JSONObject jsonData = mSelectCourseResponse.getJSONData(response);
							if (null != jsonData) {
								mSelectCourseResponse.parseJSONObject(jsonData);
								
								sendMesssageToGUI(Flinnt.SUCCESS);
								
								if( mSelectCourseResponse.getHasMore() > 0 ) {
									sendRequest();
								}
							}
							else {
								sendMesssageToGUI(Flinnt.FAILURE);	
							}
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SelectCourse Error : " + error.getMessage());

						mSelectCourseResponse.parseErrorResponse(error);
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
			msg.obj = mSelectCourseResponse;
			mHandler.sendMessage(msg);
		}
	}

}
