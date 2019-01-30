package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class SelectUserTabResponse extends BaseResponse {
	
	public static final String ALLOWED_ROLES_KEY = "allowed_roles";
	
	ArrayList<Integer> allowedRoles = new ArrayList<Integer>();

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {
		
		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			parseJSONObject(jsonObject); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {
		
		try {
			JSONArray allowedRole = jsonData.getJSONArray(ALLOWED_ROLES_KEY);
			clearAllowedRolesList();
			for(int i = 0; i < allowedRole.length(); i++) {
				allowedRoles.add(allowedRole.getInt(i));
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
	}
	
	public ArrayList<Integer> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(ArrayList<Integer> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}
	
	public void clearAllowedRolesList() {
		this.allowedRoles.clear();
	}

}
