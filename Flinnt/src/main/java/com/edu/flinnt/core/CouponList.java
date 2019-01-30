package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CouponListRequest;
import com.edu.flinnt.protocol.CouponListResponse;
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
public class CouponList {

	public static final String TAG = CouponList.class.getSimpleName();
	public static CouponListResponse mCouponListResponse = null;
	public Handler mHandler = null;
	public int mTransectionID;

	public CouponList(Handler handler, int transectionID) {
		mHandler = handler;
		mTransectionID = transectionID;
		getLastResponse();
	}

	public CouponListResponse getLastResponse() {
		if (mCouponListResponse == null) {
			mCouponListResponse = new CouponListResponse();
		}
		return mCouponListResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COUPONSLIST_COURSE;
	}

	public void sendCouponListRequest() {
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
		synchronized (CouponList.class) {
			try {
				String url = buildURLString();
				
				CouponListRequest couponListRequest = new CouponListRequest();
				couponListRequest.setUserID(Config.getStringValue(Config.USER_ID));
				couponListRequest.setTransactionID(mTransectionID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CouponList Request :\nUrl : " + url + "\nData : " + couponListRequest.getJSONString());

				JSONObject jsonObject = couponListRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CouponList response :\n" + response.toString());

				if( null != mCouponListResponse && mCouponListResponse.isSuccessResponse(response)) {
					String couponListResponse = new String(response.toString());
					JSONObject jsonData = mCouponListResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mCouponListResponse = gson.fromJson(couponListResponse, CouponListResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("CouponListError : " + error.getMessage());

				if( null != mCouponListResponse ) {
					mCouponListResponse.parseErrorResponse(error);
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
			msg.obj = mCouponListResponse;
			mHandler.sendMessage(msg);
		}
	}

}
