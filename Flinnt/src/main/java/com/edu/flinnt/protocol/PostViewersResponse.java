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
public class PostViewersResponse extends BaseResponse {

	public static final String VIEWERS_KEY 	= "viewers";
	public static final String HAS_MORE_KEY 	= "has_more";

	private int hasMore 		= Flinnt.INVALID;
	private ArrayList<PostViewersItems> viewersList	= new ArrayList<PostViewersItems>();

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
			JSONArray viewers = jsonData.getJSONArray(VIEWERS_KEY);
			clearViewersList();
			for(int i = 0; i < viewers.length(); i++) {
				JSONObject jObject = viewers.getJSONObject(i);
				PostViewersItems postViewer = new PostViewersItems();	
				postViewer.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "postViewer " + i + " :: " + postViewer.toString() );
				viewersList.add(postViewer);
				postViewer = null;
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

	public ArrayList<PostViewersItems> getViewersList() {
		return viewersList;
	}

	public void setViewersList(ArrayList<PostViewersItems> viewersList) {
		this.viewersList = viewersList;
	}

	public void clearViewersList() {
		this.viewersList.clear();
	}

	public class PostViewersItems {

		public static final String VIEWER_USER_ID_KEY 				= "user_id";
		public static final String VIEWER_USER_PICTURE_KEY 			= "user_picture";
		public static final String VIEWER_USER_NAME_KEY 		    = "user_firstname";
		public static final String VIEWER_VIEW_DATE_KEY 			= "view_date";
		public static final String VIEWER_IS_LIKED_KEY 				= "is_liked";
		public static final String VIEWER_IS_COMMENTED_KEY 			= "is_commented";
		public static final String VIEWER_CAN_MESSAGE_KEY 			= "can_message";
		public static final String VIEWER_USER_ROLE_KEY 			= "user_role";


		String viewerID = "";
		String viewerPicture = "";
		String viewerName = "";
		String viewDate = "";
		String isLiked = "";
		String isCommented = "";
		String canMessage = "";
		String userRole = "";

        /**
         * parse json object to suitable data types
         * @param jObject json object
         */
        public synchronized void parseJSONObject(JSONObject jObject) {
			try {
				setViewerID( jObject.getString(VIEWER_USER_ID_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setViewerPicture( jObject.getString(VIEWER_USER_PICTURE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setViewerName( jObject.getString(VIEWER_USER_NAME_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setViewDate( jObject.getString(VIEWER_VIEW_DATE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setIsLiked( jObject.getString(VIEWER_IS_LIKED_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setIsCommented( jObject.getString(VIEWER_IS_COMMENTED_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
			try {
				setCanMessage( jObject.getString(VIEWER_CAN_MESSAGE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
			try {
				setUserRole( jObject.getString(VIEWER_USER_ROLE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
		}

		public String getViewerID() {
			return viewerID;
		}
		public void setViewerID(String viewerID) {
			this.viewerID = viewerID;
		}
		public String getViewerPicture() {
			return viewerPicture;
		}
		public void setViewerPicture(String viewerPicture) {
			this.viewerPicture = viewerPicture;
		}
		public String getViewerName() {
			return viewerName;
		}
		public void setViewerName(String viewerName) {
			this.viewerName = viewerName;
		}
		public String getViewDate() {
			return viewDate;
		}
		public void setViewDate(String viewDate) {
			this.viewDate = viewDate;
		}
		public String getIsLiked() {
			return isLiked;
		}
		public void setIsLiked(String isLiked) {
			this.isLiked = isLiked;
		}
		public String getIsCommented() {
			return isCommented;
		}
		public void setIsCommented(String isCommented) {
			this.isCommented = isCommented;
		}
		public String getCanMessage() {
			return canMessage;
		}
		public void setCanMessage(String canMessage) {
			this.canMessage = canMessage;
		}
		public String getUserRole() {
			return userRole;
		}
		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("viewerID : " + viewerID)
			.append(", viewerPicture : " + viewerPicture)
			.append(", viewerName : " + viewerName)
			.append(", viewDate : " + viewDate)
			.append(", isLiked : " + isLiked)
			.append(", isCommented : " + isCommented)
			.append(", canMessage : " + canMessage)
			.append(", userRole : " + userRole);
			return strBuffer.toString();
		}
	}
}
