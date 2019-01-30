package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CheckoutRequest;
import com.edu.flinnt.protocol.CheckoutResponse;
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
public class CheckoutCourse {

	public static final String TAG = CheckoutCourse.class.getSimpleName();
	public static CheckoutResponse mCheckoutResponse = null;
	public Handler mHandler = null;
	public String mCourseID = "";

	public CheckoutCourse(Handler handler, String courseID) {
		mHandler = handler;
		mCourseID = courseID;
		getLastResponse();
	}

	public CheckoutResponse getLastResponse() {
		if (mCheckoutResponse == null) {
			mCheckoutResponse = new CheckoutResponse();
		}
		return mCheckoutResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_CHECKOUT_COURSE;
	}

	public void sendCheckoutRequest() {
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
		synchronized (CheckoutCourse.class) {
			try {
				String url = buildURLString();
				
				CheckoutRequest checkoutRequest = new CheckoutRequest();
				checkoutRequest.setUserID(Config.getStringValue(Config.USER_ID));
				checkoutRequest.setCourseID(mCourseID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CheckoutCourse Request :\nUrl : " + url + "\nData : " + checkoutRequest.getJSONString());

				JSONObject jsonObject = checkoutRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CheckoutCourse response :\n" + response.toString());

				if( null != mCheckoutResponse && mCheckoutResponse.isSuccessResponse(response)) {
					String checkoutResponse = new String(response.toString());
					JSONObject jsonData = mCheckoutResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mCheckoutResponse = gson.fromJson(checkoutResponse,CheckoutResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("CheckoutCourseError : " + error.getMessage());

				if( null != mCheckoutResponse ) {
				mCheckoutResponse.parseErrorResponse(error);
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
			msg.obj = mCheckoutResponse;
			mHandler.sendMessage(msg);
		}
	}

}
