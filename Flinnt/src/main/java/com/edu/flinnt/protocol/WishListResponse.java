package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
  "status": 1,
  "data": {
    	"is_wishlist": 1
  }
}
*/

/**
 * class to parse response to object
 */
public class WishListResponse extends BaseResponse {

	public static final String IS_WISHLIST_KEY = "is_wishlist";

    private int isWishList = Flinnt.INVALID;

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
            if (jsonData.has(IS_WISHLIST_KEY )) setIsWishList(jsonData.getInt(IS_WISHLIST_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    public int getIsWishList() {
        return isWishList;
    }

    public void setIsWishList(int isWishList) {
        this.isWishList = isWishList;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("isWishList : " + isWishList);
        return strBuffer.toString();
    }
}
