package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class ChangePasswordResponse extends BaseResponse {
	
	public static final String CHANGED_KEY 	= "changed";
	
	private int isChanged = Flinnt.INVALID;

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {

		try {
			setIsChanged( jsonData.getInt(CHANGED_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
	}

	public int getIsChanged() {
		return isChanged;
	}

	public void setIsChanged(int isChanged) {
		this.isChanged = isChanged;
	}
}
