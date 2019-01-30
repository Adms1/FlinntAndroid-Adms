package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
  "status": 1,
  "data": {
    "image": {
      "max_file_size": "5242880",
      "scale_max_width": 800,
      "scale_max_height": 533,
      "min_width": "300",
      "min_height": "200"
    },
    "video": {
      "bitrate": 256000,
      "max_file_size": "10485760",
      "scale_max_width": 360,
      "file_types": "mp4,3gp"
    },
    "audio": {
      "bitrate": 98304,
      "max_file_size": "10485760",
      "file_types": "mp3"
    },
    "doc": {
      "file_types": "pdf",
      "max_file_size": "36700160"
    }
  }
}
*/

/*
{
  "status": 1,
  "data": {
    "can_comment": "1",
    "show_comment_wt_approval": "1",
    "show_teacher_name_post": "1",
    "can_repost": "1",
    "max_post_delete_day_teacher": "5",
    "can_add_message": "0"
  }
}
*/


/**
 * class to parse response to object
 */
public class CourseSettingResponse extends BaseResponse {

	public static final String CAN_COMMENT_KEY = "can_comment";
	public static final String SHOW_COMMENT_KEY = "show_comment_wt_approval";
	public static final String SHOW_TEACHER_NAME_KEY = "show_teacher_name_post";
	public static final String CAN_REPOST_KEY = "can_repost";
	public static final String MAX_POST_DELETE_KEY = "max_post_delete_day_teacher";
	public static final String CAN_ADD_MESSAGE_KEY = "can_add_message";
	public static final String TEACHER_TO_TEACHER_MESSAGE_KEY = "teacher_to_teacher_message";
	public static final String V_CAN_COMMENT_KEY = "v_can_comment";
	public static final String V_SHOW_COMMENT_KEY = "v_show_comment_wt_approval";
	public static final String V_SHOW_TEACHER_NAME_KEY = "v_show_teacher_name_post";
	public static final String V_CAN_REPOST_KEY = "v_can_repost";
	public static final String V_MAX_POST_DELETE_KEY = "v_max_post_delete_day_teacher";
	public static final String V_CAN_ADD_MESSAGE_KEY = "v_can_add_message";
	public static final String V_TEACHER_TO_TEACHER_MESSAGE_KEY = "v_teacher_to_teacher_message";

	public int canComment = 0;
	public int showComment = 0;
	public int showTeacherName = 0;
	public int canRepost = 0;
	public int maxPostDelete = 0;
	public int canAddMessage = 0;
	public int canTeacherMessage = 0;
	public int v_can_comment = 0;
	public int v_show_comment_wt_approval = 0;
	public int v_show_teacher_name_post = 0;
	public int v_can_repost = 0;
	public int v_max_post_delete_day_teacher = 0;
	public int v_can_add_message = 0;


	public int v_teacher_to_teacher_message = 0;

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
			setCanComment(jsonData.getInt(CAN_COMMENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setShowComment(jsonData.getInt(SHOW_COMMENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setShowTeacherName(jsonData.getInt(SHOW_TEACHER_NAME_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setCanRepost(jsonData.getInt(CAN_REPOST_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setMaxPostDelete(jsonData.getInt(MAX_POST_DELETE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setCanAddMessage(jsonData.getInt(CAN_ADD_MESSAGE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setCanTeacherMessage(jsonData.getInt(TEACHER_TO_TEACHER_MESSAGE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setV_can_comment(jsonData.getInt(V_CAN_COMMENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_show_comment_wt_approval(jsonData.getInt(V_SHOW_COMMENT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_show_teacher_name_post(jsonData.getInt(V_SHOW_TEACHER_NAME_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_can_repost(jsonData.getInt(V_CAN_REPOST_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_max_post_delete_day_teacher(jsonData.getInt(V_MAX_POST_DELETE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_can_add_message(jsonData.getInt(V_CAN_ADD_MESSAGE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			setV_teacher_to_teacher_message(jsonData.getInt(V_TEACHER_TO_TEACHER_MESSAGE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}


	public int getCanComment() {
		return canComment;
	}

	public void setCanComment(int canComment) {
		this.canComment = canComment;
	}

	public int getShowComment() {
		return showComment;
	}

	public void setShowComment(int showComment) {
		this.showComment = showComment;
	}

	public int getShowTeacherName() {
		return showTeacherName;
	}

	public void setShowTeacherName(int showTeacherName) {
		this.showTeacherName = showTeacherName;
	}

	public int getCanRepost() {
		return canRepost;
	}

	public void setCanRepost(int canRepost) {
		this.canRepost = canRepost;
	}

	public int getMaxPostDelete() {
		return maxPostDelete;
	}

	public void setMaxPostDelete(int maxPostDelete) {
		this.maxPostDelete = maxPostDelete;
	}

	public int getCanAddMessage() {
		return canAddMessage;
	}

	public void setCanAddMessage(int canAddMessage) {
		this.canAddMessage = canAddMessage;
	}

	public int getCanTeacherMessage() { return canTeacherMessage; }

	public void setCanTeacherMessage(int canTeacherMessage) { this.canTeacherMessage = canTeacherMessage; }

	public int getV_teacher_to_teacher_message() {
		return v_teacher_to_teacher_message;
	}

	public void setV_teacher_to_teacher_message(int v_teacher_to_teacher_message) {
		this.v_teacher_to_teacher_message = v_teacher_to_teacher_message;
	}

	public int getV_can_comment() {
		return v_can_comment;
	}

	public void setV_can_comment(int v_can_comment) {
		this.v_can_comment = v_can_comment;
	}

	public int getV_show_comment_wt_approval() {
		return v_show_comment_wt_approval;
	}

	public void setV_show_comment_wt_approval(int v_show_comment_wt_approval) {
		this.v_show_comment_wt_approval = v_show_comment_wt_approval;
	}

	public int getV_show_teacher_name_post() {
		return v_show_teacher_name_post;
	}

	public void setV_show_teacher_name_post(int v_show_teacher_name_post) {
		this.v_show_teacher_name_post = v_show_teacher_name_post;
	}

	public int getV_can_repost() {
		return v_can_repost;
	}

	public void setV_can_repost(int v_can_repost) {
		this.v_can_repost = v_can_repost;
	}

	public int getV_max_post_delete_day_teacher() {
		return v_max_post_delete_day_teacher;
	}

	public void setV_max_post_delete_day_teacher(int v_max_post_delete_day_teacher) {
		this.v_max_post_delete_day_teacher = v_max_post_delete_day_teacher;
	}

	public int getV_can_add_message() {
		return v_can_add_message;
	}

	public void setV_can_add_message(int v_can_add_message) {
		this.v_can_add_message = v_can_add_message;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("canComment : " + canComment)
		.append(", showComment : " + showComment)
		.append(", showTeacherName : " + showTeacherName)
		.append(", canRepost : " + canRepost)
		.append(", maxPostDelete : " + maxPostDelete)
		.append(", canAddMessage : " + canAddMessage)
		.append(", canTeacherMessage : " + canTeacherMessage);
		return strBuffer.toString();
	}
}
