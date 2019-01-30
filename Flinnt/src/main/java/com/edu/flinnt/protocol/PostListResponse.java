package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
{
  "status": 1,
  "data": {
    "gallery_url": "https://flinnt1.s3.amazonaws.com/gallery/",
    "audio_url": "https://flinnt1.s3.amazonaws.com/audio/",
    "video_url": "https://flinnt1.s3.amazonaws.com/video/",
    "doc_url": "https://flinnt1.s3.amazonaws.com/docs/",
    "album_url": "https://flinnt1.s3.amazonaws.com/album/", (For album only)
    "has_more": 1,
    "posts": [
      {
        "post_id": "9626",
        "title": "Testing post to delete",
        "description": "There are two basic rules for writing efficient code:\r\n\r\n- Don't do work that you don't need to do.",
        "publish_date": "1403606111",
        "post_type": "1",
        "post_content_type": "2",
        "total_likes": "0",
        "new_comment": "0",
        "is_read": "1",
        "can_delete_post": "1",
        "total_views": "7",
        "inserted": "1403606111"
        "message_to_users": "Aditya Sharma, Devansh Suman, Nrupesh Shah, Priyanshi Joshi, Samir Bamaniya", // for messsage type
		"album_images": "10223_1_1441793706.jpg,10223_2_1441793710.jpg,10223_3_1441793755.jpg" // for album type
      },
        {
        "post_id": "9677",
         ...
      },
       ...
    ]
  }
}
*/


/**
 * class to parse response to object
 */
public class PostListResponse extends BaseResponse {

	public static final String HAS_MORE_KEY 						= "has_more";
	public static final String ALBUM_URL_KEY 						= "album_url";
	public static final String GALLERY_URL_KEY 						= "gallery_url";
	public static final String AUDIO_URL_KEY 						= "audio_url";
	public static final String VIDEO_URL_KEY 						= "video_url";
	public static final String DOC_URL_KEY 							= "doc_url";
	public static final String POSTS_KEY 							= "posts";
	
	private int hasMore 					= 0;
	private String albumUrl 				= "";
	private String galleryUrl 				= "";
	private String audioUrl 				= "";
	private String videoUrl 				= "";
	private String docUrl 					= "";
	private ArrayList<Post> postList		= new ArrayList<Post>();
	private static ArrayList<Post> allPostList = new ArrayList<Post>();
	private static ArrayList<Post> allMessageList = new ArrayList<Post>();
	private static ArrayList<Post> allAlbumList = new ArrayList<Post>();

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
			if(jsonData.has(HAS_MORE_KEY)) setHasMore(jsonData.getInt(HAS_MORE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(ALBUM_URL_KEY)) setAlbumUrl(jsonData.getString(ALBUM_URL_KEY));
		} catch (Exception e) {
			//LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(GALLERY_URL_KEY)) setGalleryUrl(jsonData.getString(GALLERY_URL_KEY));
		} catch (Exception e) {
			//LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(AUDIO_URL_KEY)) setAudioUrl(jsonData.getString(AUDIO_URL_KEY));
		} catch (Exception e) {
			//LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(VIDEO_URL_KEY)) setVideoUrl(jsonData.getString(VIDEO_URL_KEY));
		} catch (Exception e) {
			//LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(DOC_URL_KEY)) setDocUrl(jsonData.getString(DOC_URL_KEY));
		} catch (Exception e) {
			//LogWriter.err(e);
		}
		
		try {
			JSONArray posts = jsonData.getJSONArray(POSTS_KEY);
			clearPostList();
			for(int i = 0; i < posts.length(); i++) {
				JSONObject jObject = posts.getJSONObject(i);
				Post post = new Post();	
				post.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Post :: " + post.toString() );
				postList.add(post);
				
				int postType = post.getPostTypeInt();
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "postType :: " + postType );
				if( Flinnt.POST_TYPE_BLOG ==  postType ) {
					allPostList.add(post);
				}
				else if( Flinnt.POST_TYPE_MESSAGE == postType ) {
					allMessageList.add(post);
				}
				else if( Flinnt.POST_TYPE_ALBUM == postType ) {
					allAlbumList.add(post);
				}
				
				post = null;
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
	

	public String getAlbumUrl() {
		return albumUrl;
	}


	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}


	public String getGalleryUrl() {
		return galleryUrl;
	}


	public void setGalleryUrl(String galleryUrl) {
		this.galleryUrl = galleryUrl;
	}


	public String getAudioUrl() {
		return audioUrl;
	}


	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}


	public String getVideoUrl() {
		return videoUrl;
	}


	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	public String getDocUrl() {
		return docUrl;
	}


	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}


	public ArrayList<Post> getPostList() {
		return postList;
	}


	public void setPostList(ArrayList<Post> postList) {
		this.postList = postList;
	}
	
	
	public void clearPostList() {
		this.postList.clear();
	}
	
	public void copyResponse (PostListResponse postListResponse){
		
		setHasMore(postListResponse.getHasMore());
		setAlbumUrl(postListResponse.getAlbumUrl());
		setGalleryUrl(postListResponse.getGalleryUrl());
		setAudioUrl(postListResponse.getAudioUrl());
		setVideoUrl(postListResponse.getVideoUrl());
		setDocUrl(postListResponse.getDocUrl());
		postList.clear();
		postList.addAll(postListResponse.getPostList());
		
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("hasMore : " + hasMore)
		.append(", galleryUrl : " + galleryUrl)
		.append(", audioUrl : " + audioUrl)
		.append(", videoUrl : " + videoUrl)
		.append(", docUrl : " + docUrl)
		.append(", postList size : " + postList.size());
		return strBuffer.toString();
	}


	public static ArrayList<Post> getAllPostList() {
		return allPostList;
	}


	public static void setAllPostList(ArrayList<Post> allPostList) {
		PostListResponse.allPostList = allPostList;
	}


	public static ArrayList<Post> getAllMessageList() {
		return allMessageList;
	}


	public static void setAllMessageList(ArrayList<Post> allMessageList) {
		PostListResponse.allMessageList = allMessageList;
	}


	public static ArrayList<Post> getAllAlbumList() {
		return allAlbumList;
	}


	public static void setAllAlbumList(ArrayList<Post> allAlbumList) {
		PostListResponse.allAlbumList = allAlbumList;
	}

}
