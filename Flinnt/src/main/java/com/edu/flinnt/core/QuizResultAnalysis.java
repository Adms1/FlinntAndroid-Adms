package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.QuizResultAnalysisRequest;
import com.edu.flinnt.protocol.QuizResultAnalysisResponse;
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
public class QuizResultAnalysis {

	public static final String TAG = QuizResultAnalysis.class.getSimpleName();
	public static QuizResultAnalysisResponse mQuizResultAnalysisResponse = null;
	public Handler mHandler = null;
	public String mCourseID,mContentID,mQuizID;

	public QuizResultAnalysis(Handler handler, String courseID, String contentID, String quizID) {
		mHandler = handler;
		mCourseID = courseID;
		mContentID = contentID;
		mQuizID = quizID;
		getLastResponse();
	}

	public QuizResultAnalysisResponse getLastResponse() {
		if (mQuizResultAnalysisResponse == null) {
			mQuizResultAnalysisResponse = new QuizResultAnalysisResponse();
		}
		return mQuizResultAnalysisResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_QUIZ_RESULT_ANALYSIS;
	}

	public void sendQuizResultAnalysisRequest() {
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
		synchronized (QuizResultAnalysis.class) {
			try {
				String url = buildURLString();
				
				QuizResultAnalysisRequest quizResultAnalysisRequest = new QuizResultAnalysisRequest();
				quizResultAnalysisRequest.setUserID(Config.getStringValue(Config.USER_ID));
				quizResultAnalysisRequest.setCourseID(mCourseID);
				quizResultAnalysisRequest.setContentID(mContentID);
				quizResultAnalysisRequest.setQuizID(mQuizID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizResultAnalysis Request :\nUrl : " + url + "\nData : " + quizResultAnalysisRequest.getJSONString());

				JSONObject jsonObject = quizResultAnalysisRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("QuizResultAnalysis response :\n" + response.toString());

				if( null != mQuizResultAnalysisResponse && mQuizResultAnalysisResponse.isSuccessResponse(response)) {
					String couponListResponse = new String(response.toString());
					JSONObject jsonData = mQuizResultAnalysisResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mQuizResultAnalysisResponse = gson.fromJson(couponListResponse, QuizResultAnalysisResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (LogWriter.isValidLevel(Log.ERROR))
					LogWriter.write("QuizResultAnalysis Error : " + error.getMessage());
				if (null != mQuizResultAnalysisResponse) {
					mQuizResultAnalysisResponse.parseErrorResponse(error);
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
			msg.obj = mQuizResultAnalysisResponse;
			mHandler.sendMessage(msg);
		}
	}

}
