package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.QuizAnswerRequest;
import com.edu.flinnt.protocol.QuizAnswerResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class QuizAnswer {

	public static final String TAG = QuizAnswer.class.getSimpleName();
	public static QuizAnswerResponse mQuizAnswerResponse = null;
	public Handler mHandler = null;
	private String mCourseID = "";
	private String mPostID = "";
	private String mAnswerID = "";

	public QuizAnswer( Handler handler, String courseId, String postId, String answerId ) {
		mHandler = handler;
		mCourseID = courseId;
		mPostID = postId;
		mAnswerID = answerId;
		getLastResponse();
	}

	public QuizAnswerResponse getLastResponse() {
		mQuizAnswerResponse = new QuizAnswerResponse();
		return mQuizAnswerResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_QUIZ_ANSWER;
	}

	public void sendQuizAnswerRequest() {
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
		synchronized (QuizAnswer.class) {
			try {
				String url = buildURLString();

				QuizAnswerRequest quizAnswerRequest = new QuizAnswerRequest();
				quizAnswerRequest.setUserID(Config.getStringValue(Config.USER_ID));
				quizAnswerRequest.setCourseID( mCourseID );
				quizAnswerRequest.setPostID( mPostID );
				quizAnswerRequest.setAnswerID(mAnswerID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizAnswer Request :\nUrl : " + url + "\nData : " + quizAnswerRequest.getJSONString());

				JSONObject jsonObject = quizAnswerRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizAnswer Response : " + response.toString());

				if (mQuizAnswerResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mQuizAnswerResponse.getJSONData(response);
					if (null != jsonData) {
						mQuizAnswerResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("QuizAnswer Error : " + error.getMessage());

				mQuizAnswerResponse.parseErrorResponse(error);
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
			msg.obj = mQuizAnswerResponse;
			mHandler.sendMessage(msg);
		}
	}
}
