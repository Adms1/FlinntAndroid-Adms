package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.database.ContentDetailsInterface;
import com.edu.flinnt.protocol.BaseRealmWSRequest;
import com.edu.flinnt.protocol.ContentsDetailsRequest;
import com.edu.flinnt.protocol.contentdetails.ContentDetailsResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class ContentsDetails extends BaseRealmWSRequest {

	public static final String TAG = ContentsDetails.class.getSimpleName();
	public ContentDetailsResponse mContentsDetailsResponse;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mContentID = "";
	private String mPreview = "";
	private ContentDetailsInterface mContentDetailsInterface;
	public ContentsDetails(Handler handler, String courseId, String content_id, String preview) {
		mHandler = handler;
		mCourseID = courseId;
		mContentID = content_id;
		mPreview = preview;
		mContentDetailsInterface = ContentDetailsInterface.getInstance();
	}


    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_CONTENTS_VIEW;
	}

	public void sendContentsDetailsRequest() {
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
		synchronized (ContentsDetails.class) {
			try {
				String url = buildURLString();

				ContentsDetailsRequest contentsDetailsRequest = new ContentsDetailsRequest();

				contentsDetailsRequest.setUserID(Config.getStringValue(Config.USER_ID));

				contentsDetailsRequest.setCourseID( mCourseID );
				contentsDetailsRequest.setContentID( mContentID );
				contentsDetailsRequest.setPreview(mPreview);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentsDetails Request :\nUrl : " + url + "\nData : " + contentsDetailsRequest.getJSONString());

				JSONObject jsonObject = contentsDetailsRequest.getJSONObject();
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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("ContentsDetails Response :\n" + response.toString());

				try{
					if (isSuccessResponse(response)) {

						JSONObject jsonData = getJSONData(response);

						if(!mContentDetailsInterface.isCourseContentDetailsAlreadyExist(mContentID,Config.getStringValue(Config.USER_ID))) {
							jsonData.remove("has_more");
							jsonData.put("contentID", mContentID);
							jsonData.put("userId", Config.getStringValue(Config.USER_ID));
							jsonData.put("id", mContentID + Config.getStringValue(Config.USER_ID));
							JSONArray array = new JSONArray();
							array.put(jsonData);
							mContentDetailsInterface.addNewCourseContentDetails(array);
						}else{
							mContentDetailsInterface.delete(mContentID,Config.getStringValue(Config.USER_ID));
							jsonData.remove("has_more");
							jsonData.put("contentID", mContentID);
							jsonData.put("userId", mContentID + Config.getStringValue(Config.USER_ID));
							jsonData.put("id", mContentID + Config.getStringValue(Config.USER_ID));
							JSONArray array = new JSONArray();
							array.put(jsonData);
							mContentDetailsInterface.addNewCourseContentDetails(array);
						}

						if (null != jsonData) {
							mContentsDetailsResponse = mContentDetailsInterface.getContentDetailsData(mContentID,Config.getStringValue(Config.USER_ID));
							//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostDetails response : " + mPostDetailsResponse.toString());
							sendMesssageToGUI(Flinnt.SUCCESS);
						}
					}
				}catch (Exception e){
					LogWriter.write("PostDetails Error : " + e.getMessage());
				}


			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ContentsDetails Error : " + error.getMessage());
				parseErrorResponse(error);
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
			if(errorResponse != null)
				msg.obj = errorResponse;
			else
				msg.obj = mContentsDetailsResponse;
			mHandler.sendMessage(msg);
		}
	}
}
