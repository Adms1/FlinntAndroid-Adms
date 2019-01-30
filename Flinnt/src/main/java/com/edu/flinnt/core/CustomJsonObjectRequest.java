package com.edu.flinnt.core;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * class to create custom json object request
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {
	
	Priority mPriority = Priority.NORMAL;

	public CustomJsonObjectRequest(int method, String url,
			JSONObject jsonRequest, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
	}
	
	@Override
	public Priority getPriority() {
		return mPriority;
	}
	
	public void setPriority(Priority priority) {
		mPriority = priority;
	}
	
}
