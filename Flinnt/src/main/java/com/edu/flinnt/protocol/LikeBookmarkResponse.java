package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/*
{
  "status": 1,
  "data": {
    "bookmarked": 1
  }
}

{
  "status": 1,
  "data": {
    "bookmarked": 0
  }
}

{
  "status": 1,
  "data": {
    "liked": 1
  }
}

{
  "status": 1,
  "data": {
    "disliked": 1
  }
}
 */

/**
 * class to parse response to object
 */
public class LikeBookmarkResponse extends BaseResponse {

	public static final String BOOKMARKED_KEY = "bookmarked";
	public static final String LIKED_KEY = "liked";
	public static final String DISLIKED_KEY = "disliked";

	public int isBookmarked = Flinnt.INVALID;
	public int isLiked = Flinnt.INVALID;
	public int isDisliked = Flinnt.INVALID;

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

		if(jsonData.has(BOOKMARKED_KEY)) {
			try {
				setIsBookmarked(jsonData.getInt(BOOKMARKED_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
		if(jsonData.has(DISLIKED_KEY)) {
			try {
				setIsDisliked(jsonData.getInt(DISLIKED_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
		if(jsonData.has(LIKED_KEY)) {
			try {
				setIsLiked(jsonData.getInt(LIKED_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}
		
	}

	public int getIsBookmarked() {
		return isBookmarked;
	}

	public void setIsBookmarked(int isBookmarked) {
		this.isBookmarked = isBookmarked;
	}

	public int getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(int isLiked) {
		this.isLiked = isLiked;
	}

	public int getIsDisliked() {
		return isDisliked;
	}

	public void setIsDisliked(int isDisliked) {
		this.isDisliked = isDisliked;
	}

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("isBookmarked : " + isBookmarked)
                .append(", isLiked : " + isLiked)
                .append(", isDisliked : " + isDisliked);
        return strBuffer.toString();
    }
}
