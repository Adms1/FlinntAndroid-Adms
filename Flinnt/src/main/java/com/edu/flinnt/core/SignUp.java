package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.SignUpRequest;
import com.edu.flinnt.protocol.SignUpResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class SignUp {

	public static final String TAG = SignUp.class.getSimpleName();
	public static SignUpResponse mSignUpResponse = null;
	public Handler mHandler = null;
    protected String firstName;
    protected String lastName;
    protected String userLogin;
    protected String password = "";

	public SignUp(Handler handler, String firstName, String lastName, String userLogin, String password){
		mHandler = handler;
		getLastResponse();
        this.firstName = firstName;
        this.lastName = lastName;
        this.userLogin = userLogin;
        this.password = password;
	}

	static public SignUpResponse getLastResponse() {
		if (mSignUpResponse == null) {
			mSignUpResponse = new SignUpResponse();
		}
		mSignUpResponse.parseJSONString( Config.getStringValue(Config.LAST_SIGNUP_RESPONSE) );
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SignUp response : " + mSignUpResponse.toString());
		return mSignUpResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_SIGNUP;
	}

	public void sendSignUpRequest(){
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
		synchronized (SignUp.class) {
			try {
				String url = buildURLString();
				SignUpRequest signUpRequest = new SignUpRequest();

				signUpRequest.setFirstName(firstName);
				signUpRequest.setLastName(lastName);
				signUpRequest.setEmail(userLogin);
				signUpRequest.setPassword(password);

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SignUp Request :\nUrl : " + url + "\nData : " + signUpRequest.getJSONString());

				JSONObject jsonObject = signUpRequest.getJSONObject();

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
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("SignUp Response :\n" + response.toString());

				if (mSignUpResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mSignUpResponse.getJSONData(response);
					if (null != jsonData) {
						mSignUpResponse.parseJSONObject(jsonData);

						Config.setStringValue(Config.LAST_SIGNUP_RESPONSE, jsonData.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
						
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("SignUp Error : " + error.getMessage());

				mSignUpResponse.parseErrorResponse(error);
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
			msg.obj = mSignUpResponse;
			mHandler.sendMessage(msg);
		}
	}
}
