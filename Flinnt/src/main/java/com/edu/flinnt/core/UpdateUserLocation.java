package com.edu.flinnt.core;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.UpdateUserLocationRequest;
import com.edu.flinnt.protocol.UpdateUserLocationResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Send request and get response and pass it to GUI
 */
public class UpdateUserLocation {
	private final String KEY_LOCATION = "locations";
	private final String KEY_DEVICEID = "reg_id";
	private final String KEY_USERID = "userId";
	private final String KEY_LATITUDE = "latitude";
	private final String KEY_LONGITUDE = "longitude";
	private final String KEY_CITY = "city";
	private final String KEY_STATE = "state";
	private final String KEY_COUNTRY = "country";
	private final String KEY_LAST_UPDATETED_TIME ="lastUpdatedTime";
	private static final long SECONDS_TO_ADD= 19800;
	public static final String TAG = UpdateUserLocation.class.getSimpleName();
	public UpdateUserLocationResponse resposne = null;
	public UpdateUserLocationRequest request = null;
	private Location location;
	private Context context;
	public UpdateUserLocation(Context context ,Location location) {
		this.location = location;
		this.context = context;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    private String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_UPDATE_USER_LOCATION;
	}

	public void sendUpdateUserLocationRequest() {
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
    private void sendRequest() {
		synchronized (UpdateUserLocation.class) {
			try {
				String url = buildURLString();
				JSONObject jsonObject = getRequestJSONObject();
				sendJsonObjectRequest(url, jsonObject);
			} catch (Exception e) {
				LogWriter.err(e);
			}

		}
	}

	private JSONObject getRequestJSONObject() throws JSONException {
		List<Address> addresses;
		String city = "", state = "" , country = "";
		try {
			addresses = getAddressFromLocation();
			city = addresses.get(0).getLocality();
			state = addresses.get(0).getAdminArea();
			country = addresses.get(0).getCountryName();
		}catch (Exception e){
			LogWriter.info(e.toString());
		}
		ArrayList<String> userIds = UserInterface.getInstance().getUserIdList();
		JSONArray userLocationArray = new JSONArray();
		for(String userId : userIds){
			JSONObject obejct = new JSONObject();
			obejct.put(KEY_USERID,Long.parseLong(userId));
			obejct.put(KEY_LATITUDE,location.getLatitude());
			obejct.put(KEY_LONGITUDE,location.getLongitude());
			obejct.put(KEY_CITY , city);
			obejct.put(KEY_STATE , state);
			obejct.put(KEY_COUNTRY ,country);
			obejct.put(KEY_LAST_UPDATETED_TIME,((System.currentTimeMillis() / 1000L)+ SECONDS_TO_ADD )+"");
			userLocationArray.put(obejct);
		}
		JSONObject locationObject = new JSONObject();
		locationObject.put(KEY_LOCATION,userLocationArray);
		locationObject.put(KEY_DEVICEID,Config.getStringValue(Config.FCM_TOKEN));
		return locationObject;
	}

	private List<Address> getAddressFromLocation() throws Exception{
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(context, Locale.getDefault());

		addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

		return addresses;
	}

	/**
	 * Method to send json object request.
	 */
	private void sendJsonObjectRequest(String url, JSONObject jsonObject) {
		if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("User Update Location Request :\n" + jsonObject.toString());
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
				jsonObject, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						//Log.d(TAG, "Response :: " + response.toString());
						if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("User Update Location Response :\n" + response.toString());
						Gson gson = new Gson();
						UpdateUserLocationResponse userLocationResponse = gson.fromJson(response.toString(),UpdateUserLocationResponse.class);
						Config.setIntValue(Config.DISTANCE_VARIATION_THRESHOLD,userLocationResponse.getData().getDistance_variation());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//Log.d(TAG, "Error :: " + error.getMessage());
						if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("User Update Location Error : " + error.getMessage());

					}
				});

		jsonObjReq.setShouldCache(false);
		// Adding request to request queue
		Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
	}

}
