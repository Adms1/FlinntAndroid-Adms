package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.FreeCheckoutRequest;
import com.edu.flinnt.protocol.FreeCheckoutResponse;
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
public class FreeCheckout {

	public static final String TAG = FreeCheckout.class.getSimpleName();
	public static FreeCheckoutResponse mFreeCheckoutResponse = null;
	public Handler mHandler = null;
	public int mTransectionID;

	public FreeCheckout(Handler handler, int transectionID) {
		mHandler = handler;
		mTransectionID = transectionID;
		getLastResponse();
	}

	public FreeCheckoutResponse getLastResponse() {
		if (mFreeCheckoutResponse == null) {
			mFreeCheckoutResponse = new FreeCheckoutResponse();
		}
		return mFreeCheckoutResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_FREE_CHECKOUT_KEY;
	}

	public void sendFreeCheckoutRequest() {
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
		synchronized (FreeCheckout.class) {
			try {
				String url = buildURLString();
				
				FreeCheckoutRequest freeCheckoutRequest = new FreeCheckoutRequest();
				freeCheckoutRequest.setUserID(Config.getStringValue(Config.USER_ID));
				freeCheckoutRequest.setTransactionID(mTransectionID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("FreeCheckout Request :\nUrl : " + url + "\nData : " + freeCheckoutRequest.getJSONString());

				JSONObject jsonObject = freeCheckoutRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("FreeCheckout response :\n" + response.toString());

				if( null != mFreeCheckoutResponse && mFreeCheckoutResponse.isSuccessResponse(response)) {
					String freeCheckoutResponse = new String(response.toString());
					JSONObject jsonData = mFreeCheckoutResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mFreeCheckoutResponse = gson.fromJson(freeCheckoutResponse, FreeCheckoutResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("FreeCheckoutError : " + error.getMessage());

				if( null != mFreeCheckoutResponse ) {
				mFreeCheckoutResponse.parseErrorResponse(error);
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
			msg.obj = mFreeCheckoutResponse;
			mHandler.sendMessage(msg);
		}
	}

}
