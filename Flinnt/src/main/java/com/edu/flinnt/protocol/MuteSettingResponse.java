package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/** Output for 10_More_Mute_Popup
#response:
{
  "status": 1,
  "data": {
    "course": {
      "mute_comment": "0",
      "mute_sound": 0
    },
    "account": {
      "mute_comment": "1",
      "mute_sound": "1"
    }
  }
}

*/

/* Output for 29_Settings
{
  "status": 1,
  "data": {
    "account": {
      "mute_comment": "1",
      "mute_sound": "1"
    }
  }
}

*/


/**
 * class to parse response to object
 */
public class MuteSettingResponse extends BaseResponse {

	public static final String COURSE_KEY = "course";
	public static final String ACCOUNT_KEY = "account";
	public static final String MUTE_COMMENT_KEY = "mute_comment";
	public static final String MUTE_SOUND_KEY = "mute_sound";
	public static final String AUTO_DOWNLOAD_KEY = "auto_download_post_pns";

	private String accountMuteComment = "";
	private String accountMuteSound = "";
	private String autoDownload = "";
	private String courseMuteComment = "";
	private String courseMuteSound = "";
	private String courseID = "";

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
		JSONObject jsonObject;
		try {
			if( jsonData.has(COURSE_KEY) ) {
				jsonObject = jsonData.getJSONObject(COURSE_KEY);
				try {
					setCourseMuteComment(jsonObject.getString(MUTE_COMMENT_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				try {
					setCourseMuteSound(jsonObject.getString(MUTE_SOUND_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if( jsonData.has(ACCOUNT_KEY) ) {
				jsonObject = jsonData.getJSONObject(ACCOUNT_KEY);
				try {
					setAccountMuteComment(jsonObject.getString(MUTE_COMMENT_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				try {
					setAccountMuteSound(jsonObject.getString(MUTE_SOUND_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
				try {
					setAutoDownload(jsonObject.getString(AUTO_DOWNLOAD_KEY));
				} catch (Exception e) {
					LogWriter.err(e);
				}
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

	}


	public String getAccountMuteComment() {
		return accountMuteComment;
	}


	public void setAccountMuteComment(String accountMuteComment) {
		this.accountMuteComment = accountMuteComment;
	}


	public String getAccountMuteSound() {
		return accountMuteSound;
	}


	public void setAccountMuteSound(String accountMuteSound) {
		this.accountMuteSound = accountMuteSound;
	}


	public String getAutoDownload() {
		return autoDownload;
	}


	public void setAutoDownload(String autoDownload) {
		this.autoDownload = autoDownload;
	}


	public String getCourseMuteComment() {
		return courseMuteComment;
	}


	public void setCourseMuteComment(String courseMuteComment) {
		this.courseMuteComment = courseMuteComment;
	}


	public String getCourseMuteSound() {
		return courseMuteSound;
	}


	public void setCourseMuteSound(String courseMuteSound) {
		this.courseMuteSound = courseMuteSound;
	}


	public String getCourseID() {
		return courseID;
	}


	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("accountMuteComment : " + accountMuteComment)
		.append(", accountMuteSound : " + accountMuteSound)
		.append(", autoDownload : " + autoDownload)
		.append(", courseMuteComment : " + courseMuteComment)
		.append(", courseMuteSound : " + courseMuteSound);
		return strBuffer.toString();
	}
}
