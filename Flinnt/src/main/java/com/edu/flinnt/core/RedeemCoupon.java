package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.RedeemCouponRequest;
import com.edu.flinnt.protocol.RedeemCouponResponse;
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
public class RedeemCoupon {

	public static final String TAG = RedeemCoupon.class.getSimpleName();
	public static RedeemCouponResponse mRedeemCouponResponse = null;
	public Handler mHandler = null;
	public int mTransectionID;
	private String mCouponCode = "";

	public RedeemCoupon(Handler handler, int transectionID,String couponCode) {
		mHandler = handler;
		mTransectionID = transectionID;
		mCouponCode = couponCode;
		getLastResponse();
	}

	public RedeemCouponResponse getLastResponse() {
		if (mRedeemCouponResponse == null) {
			mRedeemCouponResponse = new RedeemCouponResponse();
		}
		return mRedeemCouponResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COUPON_REDEEM;
	}

	public void redeemCouponRequest() {
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
		synchronized (RedeemCoupon.class) {
			try {
				String url = buildURLString();
				
				RedeemCouponRequest redeemCouponRequest = new RedeemCouponRequest();
				redeemCouponRequest.setUserID(Config.getStringValue(Config.USER_ID));
				redeemCouponRequest.setTransactionID(mTransectionID);
				redeemCouponRequest.setCouponCode(mCouponCode);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("RedeemCoupon Request :\nUrl : " + url + "\nData : " + redeemCouponRequest.getJSONString());

				JSONObject jsonObject = redeemCouponRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("RedeemCoupon response :\n" + response.toString());

				if( null != mRedeemCouponResponse && mRedeemCouponResponse.isSuccessResponse(response)) {
					String redeemCoupon = new String(response.toString());
					JSONObject jsonData = mRedeemCouponResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mRedeemCouponResponse = gson.fromJson(redeemCoupon, RedeemCouponResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("RedeemCouponError : " + error.getMessage());

				if( null != mRedeemCouponResponse ) {
					mRedeemCouponResponse.parseErrorResponse(error);
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
			msg.obj = mRedeemCouponResponse;
			mHandler.sendMessage(msg);
		}
	}

}
