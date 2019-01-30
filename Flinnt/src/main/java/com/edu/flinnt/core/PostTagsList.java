package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class PostTagsList {

	public static final String TAG = PostTagsList.class.getSimpleName();
	public static PostTagsListResponse mPostTagsListResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";

	public PostTagsList(Handler handler, String courseId) {
		mHandler = handler;
		mCourseID = courseId;
		getResponse();
	}

	static public PostTagsListResponse getResponse() {
		mPostTagsListResponse = new PostTagsListResponse();
		return mPostTagsListResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_TAGS_LIST;
	}

	public void sendPostTagsListRequest() {
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
		synchronized (PostTagsList.class) {
			try {
				String url = buildURLString();
				PostTagsListRequest postTagsListRequest = new PostTagsListRequest();
				postTagsListRequest.setUserID(Config.getStringValue(Config.USER_ID));
				postTagsListRequest.setCourseID(mCourseID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostTagsList Request :\nUrl : " + url + "\nData : " + postTagsListRequest.getJSONString());

				JSONObject jsonObject = postTagsListRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("PostTagsList Response :\n" + response.toString());

				if (mPostTagsListResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mPostTagsListResponse.getJSONData(response);
					if (null != jsonData) {
						mPostTagsListResponse.parseJSONObject(jsonData);
						if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PostTagsList response : " + mPostTagsListResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("PostTagsList Error : " + error.getMessage());

				mPostTagsListResponse.parseErrorResponse(error);
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
			msg.obj = mPostTagsListResponse;
			mHandler.sendMessage(msg);
		}
	}


	public static class PostTagsListRequest {

		private String userID 					= "";
		private String courseID 				= "";

        /**
         * Converts the json object to string
         * @return converted json string
         */
        public synchronized String getJSONString() {

			return getJSONObject().toString();
		}

        /**
         * creates json object
         * @return created json object
         */
        public synchronized JSONObject getJSONObject() {

			JSONObject returnedJObject = new JSONObject();
			try {
				returnedJObject.put(LoginResponse.USER_ID_KEY, userID);
				returnedJObject.put(Course.COURSE_ID_KEY, courseID);
			}
			catch(Exception e) {
				LogWriter.err(e);
			}	    
			return returnedJObject;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public String getCourseID() {
			return courseID;
		}

		public void setCourseID(String courseID) {
			this.courseID = courseID;
		}

	}

	public static class PostTagsListResponse extends BaseResponse {
		public static final String TAGS_KEY 		= "tags";
		private String tags = "";

        /**
         * Converts json string to json object
         * @param jsonData json string
         */
        synchronized public void parseJSONString(String jsonData) {

			if( TextUtils.isEmpty(jsonData) ) {
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				parseJSONObject(jsonObject); 
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}

        /**
         * parse json object to suitable data types
         * @param jsonData json object
         */
        synchronized public void parseJSONObject(JSONObject jsonData) {

			try {
				setTags( jsonData.getString(TAGS_KEY) );
			} catch (Exception e) {
				LogWriter.err(e);
			}

		}

		public String getTags() {
			return tags;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}
	}
}
