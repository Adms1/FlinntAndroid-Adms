package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.PostStatisticsRequest;
import com.edu.flinnt.protocol.PostStatisticsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Send request and get response and pass it to GUI
 */
public class PostStatistics {

	public static final String TAG = PostStatistics.class.getSimpleName();
	public static PostStatisticsResponse mPostStatisticsResponse = null;
	public Handler mHandler = null;
	ArrayList<Integer> mPostTypeArray = null;
	private String mCourseID = "";
	
	public PostStatistics(Handler handler, ArrayList<Integer> postTypeArray, String courseID){
		mHandler = handler;
		mPostTypeArray = postTypeArray;
		mCourseID = courseID;
		getResponse();
	}

	static public PostStatisticsResponse getResponse() {
		if (mPostStatisticsResponse == null) {
			mPostStatisticsResponse = new PostStatisticsResponse();
		}
		return mPostStatisticsResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_LIST_UNREAD_STATISTICS;
	}
	
	public void sendPostStatisticsRequest(){
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
		synchronized (PostStatistics.class) {
			try {
				String url = buildURLString();

				PostStatisticsRequest postStatisticsRequest = new PostStatisticsRequest();
				postStatisticsRequest.setUserId(Config.getStringValue(Config.USER_ID));
				postStatisticsRequest.setCourseId(mCourseID);
				if(mPostTypeArray != null && mPostTypeArray.size() > 0){
					postStatisticsRequest.setPostType(mPostTypeArray);
				}
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostStatistics Request :\nUrl : " + url + "\nData : " + postStatisticsRequest.getJSONString());

				JSONObject jsonObject = postStatisticsRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostStatistics Response :\n" + response.toString());

				if (mPostStatisticsResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mPostStatisticsResponse.getJSONData(response);
					if (null != jsonData) {
						mPostStatisticsResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostStatistics Error : " + error.getMessage());

				mPostStatisticsResponse.parseErrorResponse(error);
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
			msg.obj = mPostStatisticsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
