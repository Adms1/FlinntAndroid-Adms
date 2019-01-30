package com.edu.flinnt.protocol;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * for edit course only
"course_picture_url": "https://flinnt1.s3.amazonaws.com/courses/",
"course_picture": "283_1445323470.jpg"*/

/**
 * class to parse response to object
 */
public class AddPostResponse implements Serializable {
	public static final String COURSE_KEY = "course";
	//*****change 31
	public static String COURSE_ID = "course_id";
	public ErrorResponse errorResponse = null;

//	public static final String ADD_STATUS_KEY = "added";
//	public static final String EDIT_STATUS_KEY = "edited";
//	public static final String REPOST_STATUS_KEY = "repost";
//	public static final String CHANGE_STATUS_KEY = "changed";

	public static final String ADD_STATUS_KEY = "added";
	public static final String EDIT_STATUS_KEY = "edited";
	public static final String REPOST_STATUS_KEY = "repost";
	public static final String CHANGE_STATUS_KEY = "changed";


	public static final String COURSE_PICTURE_URL_KEY = "course_picture_url";
	public static final String COURSE_PICTURE_KEY = "course_picture";

	private int isAdded = Flinnt.INVALID;
	private int isEdited = Flinnt.INVALID;
	private int isRepost = Flinnt.INVALID;
	private int isChange = Flinnt.INVALID;

	private String coursePictureUrl = "";
	private String coursePictureName = "";
	Course newCourse = new Course();

	/**
	 * Converts json string to json object
	 * @param jsonData json string
	 */
	synchronized public void parseJSONString(String jsonData) {

		if(TextUtils.isEmpty(jsonData)) {
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
			if(jsonData.has(ADD_STATUS_KEY)) setIsAdded(jsonData.getInt(ADD_STATUS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			if(jsonData.has(EDIT_STATUS_KEY)) setIsEdited(jsonData.getInt(EDIT_STATUS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			if(jsonData.has(REPOST_STATUS_KEY)) setIsRepost(jsonData.getInt(REPOST_STATUS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			if(jsonData.has(CHANGE_STATUS_KEY)) setIsChange(jsonData.getInt(CHANGE_STATUS_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(COURSE_PICTURE_URL_KEY)) setCoursePictureUrl(jsonData.getString(COURSE_PICTURE_URL_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(COURSE_PICTURE_KEY)) setCoursePictureName(jsonData.getString(COURSE_PICTURE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(COURSE_KEY)) {
				JSONObject jObject = jsonData.getJSONObject(COURSE_KEY);
				Course course = new Course();
				course.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Course :: " + course.toString() );
				setNewCourse(course);
				course = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

	}
	@SerializedName("status")
	private Integer status;
	@SerializedName("data")
	private Data data ;

	/**
	 *
	 * @return
	 * The status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 *
	 * @param status
	 * The status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 *
	 * @return
	 * The data
	 */
	public Data getData() {
		return data;
	}

	/**
	 *
	 * @param data
	 * The data
	 */
	public void setData(Data data) {
		this.data = data;
	}

	public Data getDataInstance(){
		return new Data();
	}

	public Post getPostInstance(){
		return new Post();
	}






	public void parseErrorResponse(VolleyError error) {
		if (error instanceof TimeoutError /*|| error instanceof NoConnectionError*/) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("TimeoutError");
			Helper.showToast("Your current internet connection is slow. Pl. try after some time.", Toast.LENGTH_SHORT);
		} else if (error instanceof NoConnectionError) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("NoConnectionError");
			Helper.showToast("No network connection !!", Toast.LENGTH_SHORT);
		} else if (error instanceof AuthFailureError) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AuthFailureError");
		} else if (error instanceof ServerError) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ServerError");
		} else if (error instanceof NetworkError) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("NetworkError");
		} else if (error instanceof ParseError) {
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ParseError");
		}

		String responseString = "";
		NetworkResponse response = error.networkResponse;
		if (response != null && response.data != null) {

			responseString = new String(response.data);
			if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Error Response : " + responseString);

			errorResponse = new ErrorResponse();
			boolean success = errorResponse.parse(responseString);
			if( success ) {
				ArrayList<Error> errorList = errorResponse.getErrorList();
				if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write( "Error List size : " + errorList.size());
				for(int i = 0; i < errorList.size(); i++) {
					if (LogWriter.isValidLevel(Log.ERROR))
						LogWriter.write( "\nError code : " + errorList.get(i).getCode()
								+ "\nError Message : " + errorList.get(i).getMessage()
								+ "\nError File : " + errorList.get(i).getFile()
								+ "\nError Line : " + errorList.get(i).getLine()
								+ "\nError Trace : " + errorList.get(i).getTrace());

					if (errorList.get(i).getCode() == 312) {
						Requester.getInstance().cancelPendingRequests();
						Intent intent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
						intent.putExtra("doWhat", "deleteUser");
						intent.putExtra("errMsg", errorList.get(i).getMessage());

						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						FlinntApplication.getContext().startActivity(intent);
					}
				}

			}
		}

	}

	public interface OnResponseListener {
		public void onSuccessResponse(BaseResponse response);
		public void onFailureResponse(BaseResponse response);
	}


	public class Data implements Serializable {

		private Post post;
		@SerializedName("added")
		private int isAdded = Flinnt.INVALID;
		@SerializedName("edited")
		private int isEdited = Flinnt.INVALID;
		@SerializedName("repost")
		private int isRepost = Flinnt.INVALID;

		public int getIsAdded() {
			return isAdded;
		}

		public void setIsAdded(int isAdded) {
			this.isAdded = isAdded;
		}

		public int getIsEdited() {
			return isEdited;
		}

		public void setIsEdited(int isEdited) {
			this.isEdited = isEdited;
		}

		public int getIsRepost() {
			return isRepost;
		}

		public void setIsRepost(int isRepost) {
			this.isRepost = isRepost;
		}

		public int getIsChange() {
			return isChange;
		}

		public void setIsChange(int isChange) {
			this.isChange = isChange;
		}

		@SerializedName("changed")
		private int isChange = Flinnt.FALSE;


		/**
		 *
		 * @return
		 * The post
		 */
		public Post getPost() {
			return post;
		}

		/**
		 *
		 * @param post
		 * The post
		 */
		public void setPost(Post post) {
			this.post = post;
		}

	}


	public class Post implements Serializable{

		@SerializedName("post_id")
		private int postId;
		@SerializedName("title")
		private String title;
		@SerializedName("description")
		private String description;
		@SerializedName("publish_date")
		private long publishDate;
		@SerializedName("post_type")
		private int postType;
		@SerializedName("post_content_type")
		private int postContentType;
		@SerializedName("total_likes")
		private int totalLikes;
		@SerializedName("total_comments")
		private int totalComments;
		@SerializedName("is_read")
		private int isRead;
		@SerializedName("is_bookmark")
		private int isBookmark;
		@SerializedName("can_delete_post")
		private int canDeletePost;
		@SerializedName("album_images")
		private String albumImages;
		@SerializedName("message_to_users")
		private String messageToUsers;
		@SerializedName("total_views")
		private int totalViews;
		@SerializedName("inserted")
		private long inserted;


		/**
		 *
		 * @return
		 * The postId
		 */
		public int getPostId() {
			return postId;
		}

		/**
		 *
		 * @param postId
		 * The post_id
		 */
		public void setPostId(int postId) {
			this.postId = postId;
		}

		/**
		 *
		 * @return
		 * The title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 *
		 * @param title
		 * The title
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 *
		 * @return
		 * The description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 *
		 * @param description
		 * The description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 *
		 * @return
		 * The publishDate
		 */
		public long getPublishDate() {
			return publishDate;
		}

		/**
		 *
		 * @param publishDate
		 * The publish_date
		 */
		public void setPublishDate(long publishDate) {
			this.publishDate = publishDate;
		}

		/**
		 *
		 * @return
		 * The postType
		 */
		public int getPostType() {
			return postType;
		}

		/**
		 *
		 * @param postType
		 * The post_type
		 */
		public void setPostType(int postType) {
			this.postType = postType;
		}

		/**
		 *
		 * @return
		 * The postContentType
		 */
		public int getPostContentType() {
			return postContentType;
		}

		/**
		 *
		 * @param postContentType
		 * The post_content_type
		 */
		public void setPostContentType(int postContentType) {
			this.postContentType = postContentType;
		}

		/**
		 *
		 * @return
		 * The totalLikes
		 */
		public int getTotalLikes() {
			return totalLikes;
		}

		/**
		 *
		 * @param totalLikes
		 * The total_likes
		 */
		public void setTotalLikes(int totalLikes) {
			this.totalLikes = totalLikes;
		}

		/**
		 *
		 * @return
		 * The totalComments
		 */
		public int getTotalComments() {
			return totalComments;
		}

		/**
		 *
		 * @param totalComments
		 * The total_comments
		 */
		public void setTotalComments(int totalComments) {
			this.totalComments = totalComments;
		}

		/**
		 *
		 * @return
		 * The isRead
		 */
		public int getIsRead() {
			return isRead;
		}

		/**
		 *
		 * @param isRead
		 * The is_read
		 */
		public void setIsRead(int isRead) {
			this.isRead = isRead;
		}

		/**
		 *
		 * @return
		 * The isBookmark
		 */
		public int getIsBookmark() {
			return isBookmark;
		}

		/**
		 *
		 * @param isBookmark
		 * The is_bookmark
		 */
		public void setIsBookmark(int isBookmark) {
			this.isBookmark = isBookmark;
		}

		/**
		 *
		 * @return
		 * The canDeletePost
		 */
		public int getCanDeletePost() {
			return canDeletePost;
		}

		/**
		 *
		 * @param canDeletePost
		 * The can_delete_post
		 */
		public void setCanDeletePost(int canDeletePost) {
			this.canDeletePost = canDeletePost;
		}

		/**
		 *
		 * @return
		 * The albumImages
		 */
		public String getAlbumImages() {
			return albumImages;
		}

		/**
		 *
		 * @param albumImages
		 * The album_images
		 */
		public void setAlbumImages(String albumImages) {
			this.albumImages = albumImages;
		}

		/**
		 *
		 * @return
		 * The messageToUsers
		 */
		public String getMessageToUsers() {
			return messageToUsers;
		}

		/**
		 *
		 * @param messageToUsers
		 * The message_to_users
		 */
		public void setMessageToUsers(String messageToUsers) {
			this.messageToUsers = messageToUsers;
		}

		/**
		 *
		 * @return
		 * The totalViews
		 */
		public int getTotalViews() {
			return totalViews;
		}

		/**
		 *
		 * @param totalViews
		 * The total_views
		 */
		public void setTotalViews(int totalViews) {
			this.totalViews = totalViews;
		}

		/**
		 *
		 * @return
		 * The inserted
		 */
		public long getInserted() {
			return inserted;
		}

		/**
		 *
		 * @param inserted
		 * The inserted
		 */
		public void setInserted(long inserted) {
			this.inserted = inserted;
		}

	}

	public int getIsAdded() {
		return isAdded;
	}

	public void setIsAdded(int isAdded) {
		this.isAdded = isAdded;
	}

	public int getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(int isEdited) {
		this.isEdited = isEdited;
	}

	public int getIsRepost() {
		return isRepost;
	}

	public void setIsRepost(int isRepost) {
		this.isRepost = isRepost;
	}

	public int getIsChange() {
		return isChange;
	}

	public void setIsChange(int isChange) {
		this.isChange = isChange;
	}

	public String getCoursePictureUrl() {
		return coursePictureUrl;
	}

	public void setCoursePictureUrl(String coursePictureUrl) {
		this.coursePictureUrl = coursePictureUrl;
	}

	public String getCoursePictureName() {
		return coursePictureName;
	}

	public void setCoursePictureName(String coursePictureName) {
		this.coursePictureName = coursePictureName;
	}

	public Course getNewCourse() {
		return newCourse;
	}

	public void setNewCourse(Course newCourse) {
		this.newCourse = newCourse;
	}


}
