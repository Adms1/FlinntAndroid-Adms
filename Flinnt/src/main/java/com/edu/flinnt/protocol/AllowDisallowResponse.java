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
public class AllowDisallowResponse extends BaseResponse {

	public static final String COMMENTS_KEY = "comments";
	public static final String MESSAGES_KEY = "messages";
	public static final String REMOVED_KEY = "removed";
	

	private ArrayList<UserInfo> userInfoList	= new ArrayList<UserInfo>();
	public int removed;
	private ArrayList<String> users = new ArrayList<String>();
	
	public int getRemoved() {
		return removed;
	}

	public void setRemoved(int removed) {
		this.removed = removed;
	}

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
			if(jsonData.has(MESSAGES_KEY)){
				JSONArray usersInfos = jsonData.getJSONArray(MESSAGES_KEY);
				clearUsersInfoList();
				for(int i = 0; i < usersInfos.length(); i++) {
					JSONObject jObject = usersInfos.getJSONObject(i);
					UserInfo userInfo = new UserInfo();	
					userInfo.parseJSONObject(jObject);
					if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "userInfo info :: " + userInfo.toString() );
					userInfoList.add(userInfo);
					userInfo = null;
				}
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(COMMENTS_KEY)){
				JSONArray usersInfos = jsonData.getJSONArray(COMMENTS_KEY);
				clearUsersInfoList();
				for(int i = 0; i < usersInfos.length(); i++) {
					JSONObject jObject = usersInfos.getJSONObject(i);
					UserInfo userInfo = new UserInfo();	
					userInfo.parseJSONObject(jObject);
					if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "userInfo info :: " + userInfo.toString() );
					userInfoList.add(userInfo);
					userInfo = null;
				}
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(REMOVED_KEY)){
			setRemoved( jsonData.getInt(REMOVED_KEY) );
			}
		}
		catch(Exception e){
			LogWriter.err(e);
		}

	}

	public ArrayList<UserInfo> getUserInfoList() {
		return userInfoList;
	}
	public void setUserInfoList(ArrayList<UserInfo> infoList) {
		userInfoList = infoList;
	}
	public void clearUsersInfoList() {
		this.userInfoList.clear();
	}
	
	public ArrayList<String> getUsers() {
		return users;
	}
	public void setUsers(ArrayList users) {
		this.users = users;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(", userInfoList : " + userInfoList)
		.append(", users : " + users);
		return strBuffer.toString();
	}
}
