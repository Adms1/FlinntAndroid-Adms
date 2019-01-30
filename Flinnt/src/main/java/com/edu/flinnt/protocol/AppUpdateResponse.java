package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class AppUpdateResponse extends BaseResponse {
	
	public static final String UPDATE_KEY = "update";
	
	public static final String LATEST_KEY = "latest";
	public static final String VERSION_KEY = "version";
	public static final String HARD_UPDATE_KEY = "hard_update";
	public static final String GRACE_PERIOD_KEY = "grace_period";
	public static final String LAST_UPDATE_DATE_KEY = "last_update_date";
	public static final String MESSAGE_KEY = "message";
	
	private int update = Flinnt.INVALID;
	private String version = "";
	private int hardUpdate = Flinnt.INVALID;
	private int gracePeriod = Flinnt.INVALID;
	private String lastUpdateDate = "";
	private String message = "";

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
			setUpdate(jsonData.getInt(UPDATE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(LATEST_KEY)) {
				JSONObject jObject = jsonData.getJSONObject(LATEST_KEY);
				
				try {
					setVersion(jObject.getString(VERSION_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				
				try {
					setHardUpdate(jObject.getInt(HARD_UPDATE_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				
				try {
					setGracePeriod(jObject.getInt(GRACE_PERIOD_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				
				try {
					setLastUpdateDate(jObject.getString(LAST_UPDATE_DATE_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				
				try {
					setMessage(jObject.getString(MESSAGE_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
	}
	
	public int getUpdate() {
		return update;
	}
	public void setUpdate(int update) {
		this.update = update;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getHardUpdate() {
		return hardUpdate;
	}
	public void setHardUpdate(int hardUpdate) {
		this.hardUpdate = hardUpdate;
	}
	public int getGracePeriod() {
		return gracePeriod;
	}
	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public long getLastUpdateDateLong() {
		// return long in millisecond
		return Long.parseLong(lastUpdateDate)*1000;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
