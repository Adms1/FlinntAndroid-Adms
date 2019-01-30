package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;



/**
Response :: 
{
   "status":1,
   "data":{
      "file_upload_request":"array (\n  'upload_file' => \n  array (\n    'name' => '496_1442048951.jpg',\n    'type' => '',\n    'tmp_name' => '\/tmp\/phpa12sZW',\n    'error' => 0,\n    'size' => 15842,\n  ),\n)",
      "resource_id":12
   }
}
*/

/**
 * class to parse response to object
 */
public class FileUploadResponse extends BaseResponse {

	public static final String RESOURCE_ID_KEY = "resource_id";

	private int resourceID = Flinnt.INVALID;

	synchronized public boolean parseJSONString(String jsonData) {
		
		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return false;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return parseJSONObject(jsonObject); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return false;
	}

	synchronized public boolean parseJSONObject(JSONObject jsonData) {
		try {
			setResourceID(jsonData.getInt(RESOURCE_ID_KEY));
			return true; 
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return false; 
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}


}
