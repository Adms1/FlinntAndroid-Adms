package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class AddCommentResponse extends BaseResponse {

	public static final String COMMENT_ADDED_KEY = "added";
	public static final String SHOW_COMMENTS_KEY = "show_comment";
	public static final String COMMENT_KEY = "comment";

	public static final String COMMENT_ID_KEY = "post_comment_id";
	public static final String COMMENT_TEXT_KEY = "comment_text";
	public static final String USER_NAME_KEY = "user_name";
	public static final String USER_PICTURE_KEY = "user_picture";
	public static final String USER_PICTURE_URL_KEY = "user_picture_url";
	public static final String COMMENT_DATE_KEY = "comment_date";
	public static final String CAN_DELETE_KEY = "can_delete";
	public static final String COMMENT_USER_ID_KEY = "comment_user_id";

	private int isAdded = Flinnt.INVALID;
	private int showComment = Flinnt.INVALID;

	private int commentID = Flinnt.INVALID;
	private String commentText = "";
	private String userName = "";
	private String userPicture = "";
	private String userPictureURL = "";
	private long commentDate = Flinnt.INVALID;
	private int canDelete = Flinnt.INVALID;
	private int userID = Flinnt.INVALID;

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
	synchronized public void parseJSONString(String jsonData) {

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
	synchronized public void parseJSONObject(JSONObject jsonData) {

		try {
			setIsAdded(jsonData.getInt(COMMENT_ADDED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setShowComment(jsonData.getInt(SHOW_COMMENTS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			JSONObject jsonComment = jsonData.getJSONObject(COMMENT_KEY);

			try {
				setCommentID(jsonComment.getInt(COMMENT_ID_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCommentText(jsonComment.getString(COMMENT_TEXT_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setUserName(jsonComment.getString(USER_NAME_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setUserPicture(jsonComment.getString(USER_PICTURE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setUserPictureURL(jsonComment.getString(USER_PICTURE_URL_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCommentDate(jsonComment.getLong(COMMENT_DATE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanDelete(jsonComment.getInt(CAN_DELETE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setUserID(jsonComment.getInt(COMMENT_USER_ID_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

		} catch (Exception e) {
			LogWriter.err(e);
		}

	}

	public int getIsAdded() {
		return isAdded;
	}
	public void setIsAdded(int isAdded) {
		this.isAdded = isAdded;
	}
	public int getShowComment() {
		return showComment;
	}
	public void setShowComment(int showComment) {
		this.showComment = showComment;
	}
	public int getCommentID() {
		return commentID;
	}
	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPicture() {
		return userPicture;
	}
	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}
	public String getUserPictureURL() {
		return userPictureURL;
	}
	public void setUserPictureURL(String userPictureURL) {
		this.userPictureURL = userPictureURL;
	}
	public long getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Long commentDate) {
		this.commentDate = commentDate;
	}
	public int getCanDelete() {
		return canDelete;
	}
	public void setCanDelete(int canDelete) {
		this.canDelete = canDelete;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}

}
