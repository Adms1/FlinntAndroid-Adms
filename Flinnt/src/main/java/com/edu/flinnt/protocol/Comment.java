package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Comments detail response
 */
public class Comment {

	public static final String POST_COMMENT_ID_KEY 				= "post_comment_id";
	public static final String COMMENT_TEXT_KEY 					= "comment_text";
	public static final String USER_NAME_KEY 						= "user_name";
	public static final String USER_PICTURE_KEY 					= "user_picture";
	public static final String USER_PICTURE_URL_KEY 				= "user_picture_url";
	public static final String COMMENT_DATE_KEY 					= "comment_date";
	public static final String CAN_DELETE_KEY 					= "can_delete";
	public static final String COMMENT_USER_ID_KEY 				= "comment_user_id";
	
	private String postCommentID 	= "";	
	private String commentText 		= "";
	private String userName 		= "";
	private String userPicture 		= "";
	private String userPictureUrl 	= "";
	private String commentDate 		= "";
	private int canDelete 		= Flinnt.INVALID;
	private String commentUserID 	= "";

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {
		try {
			setPostCommentID( jObject.getString(POST_COMMENT_ID_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}

		try {
			setCommentText( jObject.getString(COMMENT_TEXT_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}

		try {
			setUserName( jObject.getString(USER_NAME_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
		
		try {
			setUserPicture( jObject.getString(USER_PICTURE_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
		
		try {
			setUserPictureUrl( jObject.getString(USER_PICTURE_URL_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
		
		try {
			setCommentDate( jObject.getString(COMMENT_DATE_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
		
		try {
			setCanDelete( jObject.getInt(CAN_DELETE_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
		
		try {
			setCommentUserID( jObject.getString(COMMENT_USER_ID_KEY) );
		}
		catch(Exception e){	LogWriter.err(e);}
	}
	
	public String getPostCommentID() {
		return postCommentID;
	}
	public void setPostCommentID(String postCommentID) {
		this.postCommentID = postCommentID;
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
	public String getUserPictureUrl() {
		return userPictureUrl;
	}
	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}
	public String getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}
	public int getCanDelete() {
		return canDelete;
	}
	public void setCanDelete(int canDelete) {
		this.canDelete = canDelete;
	}
	public String getCommentUserID() {
		return commentUserID;
	}
	public void setCommentUserID(String commentUserID) {
		this.commentUserID = commentUserID;
	}
}