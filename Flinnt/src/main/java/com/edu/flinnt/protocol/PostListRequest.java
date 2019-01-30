package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Builds json request
 */
public class PostListRequest {

	public static final String POST_TYPE_KEY			= "post_type"; // json array of post types like blog, quiz, album etc
	public static final String POST_CONTENT_TYPE_KEY	= "post_content_type"; // Post content type like gallery, video, audio (Optional)
	public static final String OFFSET_KEY 				= "offset"; // Start position from where to fetch list
	public static final String MAX_KEY 					= "max";	// Maximum no. of entries to fetch
	public static final String SEARCH_KEY 				= "search";	// keyword to search (Optional)
	public static final String EXCLUDE_POSTS_KEY 		= "exclude_posts"; // JSON array of post ids to exclude from output (Optional)
	public static final String TAG_NAME_KEY 			= "tag_name"; // Filter the list by tag names (Optional)
	public static final String PUB_DATE_KEY 			= "pub_date"; /* If querying for latest post: set highest publish_date 
																		 If querying for older post: set lowest publish_date value from list. */    
	public static final String NEW_ONLY_KEY 			= "new_only"; /* If querying for latest post: this must be 1
																		 If querying for older post: this must be 0 */

	// For offline database update 
	public static final String UPDATE_OFFLINE_KEY 		= "update_offline";	// This value must be 1.
	public static final String OFFLINE_POSTS_KEY 		= "offline_posts"; // JSON array of course id for which data should be fetched. The array must not be empty.
	

	private int postContentType 			= Flinnt.INVALID;
	private String userID 					= "";
	private String courseID					= "";
	private int offset 						= 0;
	private int max 						= 10;
	private String search 					= "";
	private String tagName					= "";
	private String pubDate 					= "0";
	private String newOnly					= "1";
	private int updateOffline 				= Flinnt.INVALID;
	private ArrayList<Integer> postTypes 	= new ArrayList<Integer>();
	private ArrayList<String> excludePosts 	= new ArrayList<String>();
	private ArrayList<String> offlinePosts 	= new ArrayList<String>();

    /**
     * Converts the json object to string
     * @return converted json string
     */
    public synchronized String getJSONString() {
		
	    return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
		
		JSONObject returnedJObject = new JSONObject();
	    try {
	        returnedJObject.put(LoginResponse.USER_ID_KEY, userID);
	        returnedJObject.put(Course.COURSE_ID_KEY, courseID);
			if( postTypes.size() > 0 ) {
				returnedJObject.put(POST_TYPE_KEY, getPostTypeJSONArray());
			}
			
	        if( Flinnt.TRUE == updateOffline ) {
	        	returnedJObject.put(UPDATE_OFFLINE_KEY, updateOffline);
		        if( offlinePosts.size() > 0 ) {
		        	returnedJObject.put(OFFLINE_POSTS_KEY, getJSONArray(offlinePosts));
		        }
	        }
	        else {
		        returnedJObject.put(OFFSET_KEY, offset);
		        returnedJObject.put(MAX_KEY, max);
		        returnedJObject.put(PUB_DATE_KEY, pubDate);
		        returnedJObject.put(NEW_ONLY_KEY, newOnly);
		        if( !TextUtils.isEmpty( search ) ) {
		        	returnedJObject.put(SEARCH_KEY, search);
		        }
		        if( !TextUtils.isEmpty( tagName ) ) {
		        	returnedJObject.put(TAG_NAME_KEY, tagName);
		        }
		        if( excludePosts.size() > 0 ) {
		        	returnedJObject.put(EXCLUDE_POSTS_KEY, getJSONArray(excludePosts));
		        }
		        if( postContentType != Flinnt.INVALID ) {
		        	returnedJObject.put(POST_CONTENT_TYPE_KEY, postContentType);
		        }
	        }
	    }
	    catch(Exception e) {
	    	LogWriter.err(e);
	    }	    
	    return returnedJObject;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getNewOnly() {
		return newOnly;
	}

	public void setNewOnly(String newOnly) {
		this.newOnly = newOnly;
	}
	
	public ArrayList<String> getExcludePosts() {
		return excludePosts;
	}

	public void setExcludePosts(ArrayList<String> excludePosts) {
		this.excludePosts.clear();
		this.excludePosts.addAll(excludePosts);
	}

	public int getPostContentType() {
		return postContentType;
	}

	public void setPostContentType(int postContentType) {
		this.postContentType = postContentType;
	}

	public ArrayList<Integer> getPostTypes() {
		return postTypes;
	}

	public void setPostTypes(ArrayList<Integer> postTypes) {
		this.postTypes = postTypes;
	}
	
	public int getUpdateOffline() {
		return updateOffline;
	}

	public void setUpdateOffline(int updateOffline) {
		this.updateOffline = updateOffline;
	}
	
	public ArrayList<String> getOfflinePosts() {
		return offlinePosts;
	}

	public void setOfflinePosts(ArrayList<String> offlinePosts) {
		this.offlinePosts.clear();
		this.offlinePosts.addAll(offlinePosts);
	}

	private JSONArray getPostTypeJSONArray() {
		JSONArray jArray = new JSONArray();
		if(null != postTypes){
			for (int i = 0; i < postTypes.size(); i++) {
				jArray.put( postTypes.get(i) );
			}
		}
		return jArray;
	}

	private JSONArray getJSONArray(ArrayList<String> courseList) {
		JSONArray jArray = new JSONArray();
		if(null != courseList){
			for (int i = 0; i < courseList.size(); i++) {
				jArray.put( courseList.get(i) );
			}
		}
		return jArray;
	}
	
	
	public void copyRequest (PostListRequest postListRequest){
		
		setPostContentType(postListRequest.getPostContentType());
		setUserID(postListRequest.getUserID());
		setCourseID(postListRequest.getCourseID());
		setOffset(postListRequest.getOffset());
		setMax(postListRequest.getMax());
		setSearch(postListRequest.getSearch());
		setTagName(postListRequest.getTagName());
		setPubDate(postListRequest.getPubDate());
		setNewOnly(postListRequest.getNewOnly());
		setUpdateOffline(postListRequest.getUpdateOffline());
		setPostTypes(postListRequest.getPostTypes());
		setExcludePosts(postListRequest.getExcludePosts());
		setOfflinePosts(postListRequest.getOfflinePosts());
	}
}
