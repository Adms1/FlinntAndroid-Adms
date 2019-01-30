package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*{
	  "status": 1,
	  "data": {
	    "user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/",
	    "can_manage_message": "1",
	    "can_manage_comment": "1",
	    "has_more": 1,
	    "users": [
	      {
	        "user_id": "41428",
	        "user_picture": "default.png",
	        "user_name": "br samur",
	        "can_comment": "1",
	        "can_add_message": "0"
	      },
	...
	    ]
	  }
	}
}
*/
/**
 * class to parse response to object
 */
public class SelectUsersResponse extends BaseResponse {

	public static final String HAS_MORE_KEY = "has_more";
	public static final String USER_PICTURE_URL_KEY = "user_picture_url";
	public static final String USERS_KEY = "users";

	public static final String CAN_MANAGE_MESSAGE_KEY = "can_manage_message";
	public static final String CAN_MANAGE_COMMENT_KEY = "can_manage_comment";
	public static final String CAN_REMOVE_SUBSCRIPTION_KEY = "can_remove_subscription";
	
	private int hasMore = Flinnt.INVALID;
	private String userPictureUrl = "";
	private ArrayList<SelectUserInfo> selectUserInfoList = new ArrayList<SelectUserInfo>();

	private String canManageMessage = "" ;
	private String canManageComment = "" ;

	private int canRemoveSibscription = Flinnt.FALSE ;

    /**
     * Converts json string to json object
     * @param jsonData json string
     * @param selectedIDs selected id
     */
    public synchronized void parseJSONString(String jsonData, JSONArray selectedIDs) {

		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			parseJSONObject(jsonObject, selectedIDs); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData, JSONArray selectedIDs) {

		try {
			setHasMore(jsonData.getInt(HAS_MORE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(USER_PICTURE_URL_KEY)) setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(CAN_MANAGE_MESSAGE_KEY)) setCanManageMessage(jsonData.getString(CAN_MANAGE_MESSAGE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(CAN_MANAGE_COMMENT_KEY)) setCanManageComment(jsonData.getString(CAN_MANAGE_COMMENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(CAN_REMOVE_SUBSCRIPTION_KEY)) setCanRemoveSibscription(jsonData.getInt(CAN_REMOVE_SUBSCRIPTION_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			JSONArray usersInfos = jsonData.getJSONArray(USERS_KEY);
			clearUsersInfoList();
			for(int i = 0; i < usersInfos.length(); i++) {
				JSONObject jObject = usersInfos.getJSONObject(i);
				SelectUserInfo userInfo = new SelectUserInfo();	
				userInfo.parseJSONObject(jObject);
				/*if(userIDexists(selectedIDs, userInfo.getUserID())){
					SelectUsersActivity.checkboxState.put(userInfo.getUserID(), true);
					userInfo.setUserChecked(Flinnt.TRUE);
				}*/
					
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "userInfo info :: " + userInfo.toString() );
				selectUserInfoList.add(userInfo);
				userInfo = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

	}

	private boolean userIDexists(JSONArray jsonArray, String userIDToFind){
	    return jsonArray.toString().contains("\""+userIDToFind+"\"");
	}


	public int getHasMore() {
		return hasMore;
	}
	public void setHasMore(int hasMore) {
		this.hasMore = hasMore;
	}
	public String getUserPictureUrl() {
		return userPictureUrl;
	}
	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}
	public ArrayList<SelectUserInfo> getSelectUserInfoList() {
		return selectUserInfoList;
	}
	public void setSelectUserInfoList(ArrayList<SelectUserInfo> userInfoList) {
		selectUserInfoList = userInfoList;
	}
	public void clearUsersInfoList() {
		this.selectUserInfoList.clear();
	}
	public String getCanManageMessage() {
		return canManageMessage;
	}
	public void setCanManageMessage(String canManageMessage) {
		this.canManageMessage = canManageMessage;
	}

	public String getCanManageComment() {
		return canManageComment;
	}

	public void setCanManageComment(String canManageComment) {
		this.canManageComment = canManageComment;
	}

	public int getCanRemoveSibscription() {
		return canRemoveSibscription;
	}

	public void setCanRemoveSibscription(int canRemoveSibscription) {
		this.canRemoveSibscription = canRemoveSibscription;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(", userPictureUrl : " + userPictureUrl)
		.append(", hasMore : " + hasMore)
		.append(", selectUserInfoList size : " + selectUserInfoList.size());
		return strBuffer.toString();
	}
}
