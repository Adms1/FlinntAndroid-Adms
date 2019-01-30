package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class PostCommentsResponse extends BaseResponse {
	
	public static final String COMMENTS_KEY 						= "comments";
	public static final String HAS_MORE_KEY 						= "has_more";

	private int hasMore 					= Flinnt.INVALID;
	private ArrayList<Comment> CommentList	= new ArrayList<Comment>();

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
			setHasMore( jsonData.getInt(HAS_MORE_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			JSONArray comments = jsonData.getJSONArray(COMMENTS_KEY);
			clearCommentList();
			for(int i = 0; i < comments.length(); i++) {
				JSONObject jObject = comments.getJSONObject(i);
				Comment comment = new Comment();	
				comment.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Comment " + i + " :: " + comment.getCommentText().toString() );
				CommentList.add(comment);
				comment = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}
	
	public int getHasMore() {
		return hasMore;
	}

	public void setHasMore(int hasMore) {
		this.hasMore = hasMore;
	}

	public ArrayList<Comment> getCommentList() {
		return CommentList;
	}

	public void setCommentList(ArrayList<Comment> CommentList) {
		this.CommentList = CommentList;
	}
	
	public void clearCommentList() {
		this.CommentList.clear();
	}

}
