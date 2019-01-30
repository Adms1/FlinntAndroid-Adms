package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response

If post_type not passed at all
{
  "status": 1,
  "data": {
	"unread_posts": "14",
	"unread_messages": "0",
	"unread_albums": "1"
  }
}
if post_type=[1,6]
{
  "status": 1,
  "data": {
	"unread_posts": "14",
  }
}
if post_type=[14]
{
  "status": 1,
  "data": {
	"unread_messages": "0",
  }
}
if post_type=[15]
{
  "status": 1,
  "data": {
	"unread_albums": "1",
  }
}
 */

/**
 * class to parse response to object
 */
public class PostStatisticsResponse extends BaseResponse {

	public static final String UNREAD_POSTS_KEY = "unread_posts";
	public static final String UNREAD_MESSAGES_KEY = "unread_messages";
	public static final String UNREAD_ALBUMS_KEY = "unread_albums";
	public static final String UNREAD_CONTENTS_KEY = "unread_contents";

	public String unreadPosts = "";
	public String unreadMessages = "";
	public String unreadAlbums = "";
	public String unreadContents = "";

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
			if(jsonData.has(UNREAD_POSTS_KEY)) setUnreadPosts(jsonData.getString(UNREAD_POSTS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(UNREAD_MESSAGES_KEY)) setUnreadMessages(jsonData.getString(UNREAD_MESSAGES_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(UNREAD_ALBUMS_KEY)) setUnreadAlbums(jsonData.getString(UNREAD_ALBUMS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(UNREAD_CONTENTS_KEY)) setUnreadContents(jsonData.getString(UNREAD_CONTENTS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getUnreadPosts() {
		/*if( getUnreadPostsCount() > 99 ) {
			unreadPosts = "99+";
		}*/
		return unreadPosts;
	}

	public void setUnreadPosts(String unreadPosts) {
		this.unreadPosts = unreadPosts;
	}

	public int getUnreadPostsCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(unreadPosts);
		}
		catch(Exception e) { }
		return count;
	}

	public String getUnreadMessages() {
		/*if( getUnreadMessagesCount() > 99 ) {
			unreadMessages = "99+";
		}*/
		return unreadMessages;
	}

	public void setUnreadMessages(String unreadMessages) {
		this.unreadMessages = unreadMessages;
	}

	public int getUnreadMessagesCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(unreadMessages);
		}
		catch(Exception e) { }
		return count;
	}

	public String getUnreadAlbums() {
		/*if( getUnreadAlbumsCount() > 99 ) {
			unreadAlbums = "99+";
		}*/
		return unreadAlbums;
	}

	public void setUnreadAlbums(String unreadAlbums) {
		this.unreadAlbums = unreadAlbums;
	}

	public int getUnreadAlbumsCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(unreadAlbums);
		}
		catch(Exception e) { }
		return count;
	}

	public String getUnreadContents() {
		return unreadContents;
	}

	public void setUnreadContents(String unreadContents) {
		this.unreadContents = unreadContents;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("unreadPosts : " + unreadPosts)
		.append(", unreadMessages : " + unreadMessages)
		.append(", unreadAlbums : " + unreadAlbums)
		.append(", unreadContents : " + unreadContents);
		return strBuffer.toString();
	}
}
