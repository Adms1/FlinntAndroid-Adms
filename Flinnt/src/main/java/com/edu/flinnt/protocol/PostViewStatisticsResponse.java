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
public class PostViewStatisticsResponse extends BaseResponse {

	public static final String USER_PICTURE_URL_KEY 						= "user_picture_url";
	public static final String TOTAL_LIKES_KEY 							= "total_likes";
	public static final String TOTAL_COMMENTS_KEY 						= "total_comments";
	public static final String TOTAL_VIEWERS_KEY 							= "total_viewers";
	public static final String VIEWERS_KEY 								= "viewers";

	private String userPictureUrl 		= "";
	private String totalLikes 			= "";
	private String totalComments 		= "";
	private String totalViews 			= "";
	private ArrayList<Viewers> viewersList	= new ArrayList<Viewers>();

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
			setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setTotalLikes(jsonData.getString(TOTAL_LIKES_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setTotalComments(jsonData.getString(TOTAL_COMMENTS_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setTotalViews(jsonData.getString(TOTAL_VIEWERS_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			JSONArray viewers = jsonData.getJSONArray(VIEWERS_KEY);
			clearViewersList();
			for(int i = 0; i < viewers.length(); i++) {
				JSONObject jObject = viewers.getJSONObject(i);
				Viewers viewer = new Viewers();	
				viewer.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Viewer :: " + viewer.toString() );
				viewersList.add(viewer);
				viewer = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getUserPictureUrl() {
		return userPictureUrl;
	}
	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}

	public void setTotalLikes(String totalLikes) {
		this.totalLikes = totalLikes;
	}

	public void setTotalComments(String totalComments) {
		this.totalComments = totalComments;
	}

//	Set "K" format in View Counter
	public int getTotalViewsCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(totalViews);
		}
		catch(Exception e) { }
		return count;
	}
	public String getTotalViews() {
		if ( getTotalViewsCount() >= 10000 ) {
			totalViews = String.format("%.1f", (double) getTotalViewsCount()/1000) + "k";
		}
		else if ( getTotalViewsCount() >= 1000 ) {
			totalViews = String.format("%.2f", (double) getTotalViewsCount()/1000) + "k";
		}
		return totalViews;
	}

//	Set "K" format in Like Counter
	public int getTotalLikesCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(totalLikes);
		}
		catch(Exception e) { }
		return count;
	}
	public String getTotalLikes() {
		if ( getTotalLikesCount() >= 10000 ) {
			totalLikes = String.format("%.1f", (double) getTotalLikesCount()/1000) + "k";
		}
		else if ( getTotalLikesCount() >= 1000 ) {
			totalLikes = String.format("%.2f", (double) getTotalLikesCount()/1000) + "k";
		}
		return totalLikes;
	}

//	Set "K" format in Comment Counter
	public int getTotalCommentsCount() {
		int count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(totalComments);
		}
		catch(Exception e) { }
		return count;
	}
	public String getTotalComments() {
		
		if ( getTotalCommentsCount() >= 10000 ) {
			totalComments = String.format("%.1f", (double) getTotalCommentsCount()/1000) + "k";
		}
		else if ( getTotalCommentsCount() >= 1000 ) {
			totalComments = String.format("%.2f", (double) getTotalCommentsCount()/1000) + "k";
		}
		return totalComments;
	}

	public void setTotalViews(String totalViews) {
		this.totalViews = totalViews;
	}

	public ArrayList<Viewers> getViewersList() {
		return viewersList;
	}
	public void setViewersList(ArrayList<Viewers> viewersList) {
		this.viewersList = viewersList;
	}
	public void clearViewersList() {
		this.viewersList.clear();
	}

	public class Viewers {

		public final String USER_FIRST_NAME_KEY 						= "user_firstname";
		public final String USER_LAST_NAME_KEY 						= "user_lastname";
		public final String USER_PICTURE_KEY 							= "user_picture";

		private String userFirstName = "";
		private String userLastName = "";
		private String userPicture = "";

        /**
         * parse json object to suitable data types
         * @param jObject json object
         */
        public synchronized void parseJSONObject(JSONObject jObject) {
			try {
				setUserFirstName( jObject.getString(USER_FIRST_NAME_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setUserLastName( jObject.getString(USER_LAST_NAME_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setUserPicture( jObject.getString(USER_PICTURE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
		}

		public String getUserFirstName() {
			return userFirstName;
		}
		public void setUserFirstName(String userFirstName) {
			this.userFirstName = userFirstName;
		}

		public String getUserLastName() {
			return userLastName;
		}
		public void setUserLastName(String userLastName) {
			this.userLastName = userLastName;
		}

		public String getUserPicture() {
			return userPicture;
		}
		public void setUserPicture(String userPicture) {
			this.userPicture = userPicture;
		}

		@Override
		public String toString() {
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("userFirstName : " + userFirstName)
			.append(", userLastName : " + userLastName)
			.append(", userPicture : " + userPicture);
			return strBuffer.toString();
		}
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(", userPictureUrl : " + userPictureUrl)
		.append(", totalLikes : " + totalLikes)
		.append(", totalComments : " + totalComments)
		.append(", totalViews : " + totalViews);
		return strBuffer.toString();
	}
}
