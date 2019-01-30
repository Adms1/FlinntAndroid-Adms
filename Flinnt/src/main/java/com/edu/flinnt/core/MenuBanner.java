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
import com.edu.flinnt.protocol.MenuBannerRequest;
import com.edu.flinnt.protocol.MenuBannerResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class MenuBanner {

	public static final String TAG = MenuBanner.class.getSimpleName();
	public static MenuBannerResponse mMenuBannerResponse = new MenuBannerResponse();
	public Handler mHandler = null;
	private String mCourseId = "";

	public MenuBanner(Handler handler, String courseId) {
		mHandler = handler;
		mCourseId = courseId;
	}

	public static MenuBannerResponse getLastResponse() {
		if( null == mMenuBannerResponse ) {
			mMenuBannerResponse = new MenuBannerResponse();
		}
		mMenuBannerResponse.parseJSONString( Config.getStringValue(Config.LAST_MENU_BANNER_POST_RESPONSE) );
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("MenuBanner response : " + mMenuBannerResponse.toString());
		return mMenuBannerResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_COURSE_MENU_BANNER;
	}

	public void sendMenuBannerRequest() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				Helper.lockCPU();
				try {
					//Log.d("Menuu","sendRequest()..");
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
		synchronized (MenuBanner.class) {
			try {
				String url = buildURLString();

				MenuBannerRequest menuBannerRequest = new MenuBannerRequest();

				menuBannerRequest.setUserId(Config.getStringValue(Config.USER_ID));
				if(!TextUtils.isEmpty(mCourseId)){
					menuBannerRequest.setCourseId(mCourseId);
				}
				menuBannerRequest.setMergeAdd(Flinnt.ENABLED);
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MenuBanner Request :\nUrl : " + url + "\nData : " + menuBannerRequest.getJSONString());

				//Log.d("Menuu","MenuBanner Request :\nUrl : " + url + "\nData : " + menuBannerRequest.getJSONString());
				JSONObject jsonObject = menuBannerRequest.getJSONObject();

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

				//Log.d("Menuu","MenuBanner response");

				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("MenuBanner Response :\n" + response.toString());

				//Log.d("Menuu"," menu banner response : "+response.toString());
				if (mMenuBannerResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mMenuBannerResponse.getJSONData(response);
					if (null != jsonData) {
						mMenuBannerResponse.parseJSONObject(jsonData);
						if(TextUtils.isEmpty(mCourseId)){
							Config.setStringValue(Config.LAST_MENU_BANNER_POST_RESPONSE, jsonData.toString());
						}
						//if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("MenuBanner response : " + mMenuBannerResponse.toString());
						sendMesssageToGUI(Flinnt.SUCCESS);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				//Log.d("Menuu","MenuBanner Error ");

				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("MenuBanner Error : " + error.getMessage());

				//Log.d("Menuu","MenuBanner Error : " + error.getMessage());
				mMenuBannerResponse.parseErrorResponse(error);
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
			msg.obj = mMenuBannerResponse;
			mHandler.sendMessage(msg);
		}
	}
}
