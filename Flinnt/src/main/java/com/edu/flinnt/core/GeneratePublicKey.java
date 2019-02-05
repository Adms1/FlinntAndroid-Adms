package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.GeneratePublicKeyRequest;
import com.edu.flinnt.protocol.GeneratePublicKeyResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

/**
 * Send request and get response and pass it to GUI
 */
public class GeneratePublicKey {

	public static final String TAG = GeneratePublicKey.class.getSimpleName();
	public static GeneratePublicKeyResponse mGeneratePublicKeyResponse = null;
	public Handler mHandler = null;
	public int mTransectionID;

	public GeneratePublicKey(Handler handler, int transectionID) {
		mHandler = handler;
		mTransectionID = transectionID;
		getLastResponse();
	}

	public GeneratePublicKeyResponse getLastResponse() {
		if (mGeneratePublicKeyResponse == null) {
			mGeneratePublicKeyResponse = new GeneratePublicKeyResponse();
		}
		return mGeneratePublicKeyResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_GENERATE_PUBLIC_KEY;
	}

	public void sendGeneratePublicKey() {
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
		synchronized (GeneratePublicKey.class) {
			try {
				String url = buildURLString();
				
				GeneratePublicKeyRequest generatePublicKeyRequest = new GeneratePublicKeyRequest();
				generatePublicKeyRequest.setTransactionID(mTransectionID);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("GeneratePublicKey Request :\nUrl : " + url + "\nData : " + generatePublicKeyRequest.getJSONString());

				JSONObject jsonObject = generatePublicKeyRequest.getJSONObject();

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

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST,url, jsonObject, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("GeneratePublicKey response :\n" + response.toString());

				if( null != mGeneratePublicKeyResponse && mGeneratePublicKeyResponse.isSuccessResponse(response)) {
					String generatePublicKeyResponse = new String(response.toString());
					JSONObject jsonData = mGeneratePublicKeyResponse.getJSONData(response);
					if (null != jsonData) {
						Gson gson = new Gson();
						mGeneratePublicKeyResponse = gson.fromJson(generatePublicKeyResponse,GeneratePublicKeyResponse.class);
						sendMesssageToGUI(Flinnt.SUCCESS);
					} else {
						sendMesssageToGUI(Flinnt.FAILURE);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("GeneratePublicKey Error : " + error.getMessage());

				if( null != mGeneratePublicKeyResponse ) {
					mGeneratePublicKeyResponse.parseErrorResponse(error);
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
			msg.obj = mGeneratePublicKeyResponse;
			mHandler.sendMessage(msg);
		}
	}

}
