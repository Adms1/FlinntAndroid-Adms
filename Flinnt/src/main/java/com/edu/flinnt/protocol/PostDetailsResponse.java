package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 "status": 1, 
   	"data": { 
	   	"allow_comment": 1, 
	   	"approve_comment": 1, 
	   	"allow_repost": 1, 
	   	"can_delete_post": 0, 
		"user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/", 
		"video_preview_url": "https://flinnt1.s3.amazonaws.com/video/thumbs/",
	 	"attachment_url": "", 
	  	"author": "Concept EduServ Pvt. Ltd.", 
		"author_picture": "115_1431530422.jpg", 
		"author_picture_url": "", 
		"post": { 
			"post_id": "9593", 
			"post_attachment": "https://www.youtube.com/watch?v=4j3_23us1Os",
			"video_preview": "default.png",
			"publish_date": "1399896726", 
			"course_name": "Learning Responsive Web Design", 
			"post_content_url": "1", 
			"post_type": "1", 
			"post_content_type": "4", 
			"course_owner": "1", 
			"post_creator": "1", 
			"total_likes": "0", 
			"total_comments": "0", 
			"total_views": "26" 

			//15A_Post_Detailview 
			"title": "Alisha Chinoi: Made in India", 
			"description": "It's almost impossible that you belong to the era of...", 
			"tag_name": "Music,Pop", 

			//18_Quiz_Detailview 
			"question": "Now a days, Google and other Internet / email services?",
			"tag_name": "Music,Pop", 
			"quiz_answer": "11840",
			"options": [ 
				            { 
				            	"id": "11838", 
				            	"text": "Thread Level Security", 
				            	"is_correct": "0" 
				            },  
				            ...... 
						]

			//17_Message_Detailview
			"description": "It's almost impossible that you belong to the era of...", 
			"message_to_users": "Menon, Smith, Sharm, Shah", 

			//16_Album_Detailview 
			"description": "It's almost impossible that you belong to the era of...", 
			}, 

		"viewers": [ 
			{ 
			"user_firstname": "Urvish", 
			"user_lastname": "Patel", 
			"user_picture": "1_1434439983.jpg" 
			}, 
			.... 
		] 
  	} 
} 
 */

/**
 * class to parse response to object
 */
public class PostDetailsResponse extends BaseResponse {

	public static final String ALLOW_COMMENT_KEY 							= "allow_comment";
	public static final String APPROVE_COMMENT_KEY 						= "approve_comment";
	public static final String ALLOW_REPOST_KEY 							= "allow_repost";
	public static final String CAN_EDIT_POST_KEY 							= "can_edit_post";
	
	public static final String ATTACHMENT_URL_KEY 						= "attachment_url";
	public static final String AUTHOR_KEY 								= "author";
	public static final String AUTHOR_PICTURE_KEY 						= "author_picture";
	public static final String AUTHOR_PICTURE_URL_KEY 					= "author_picture_url";
	public static final String VIDEO_PREVIEW_URL_KEY 						= "video_preview_url";

	public static final String POST_KEY 									= "post";
	public static final String POST_ID_KEY 								= "post_id";
	public static final String POST_ATTACHMENT_KEY 						= "post_attachment";
	public static final String VIDEO_PREVIEW_KEY 							= "video_preview";
	public static final String PUBLISH_DATE_KEY 							= "publish_date";
	public static final String POST_COURSE_NAME_KEY 						= "course_name";
	public static final String POST_CONTENT_URL_KEY 						= "post_content_url";
	public static final String POST_TYPE_KEY 								= "post_type";
	public static final String POST_CONTENT_TYPE_KEY 						= "post_content_type";
	public static final String COURSE_OWNER_KEY 							= "course_owner";
	public static final String POST_CREATOR_KEY 							= "post_creator";
	public static final String TITLE_KEY 									= "title";
	public static final String DESCRIPTION_KEY 							= "description";
	public static final String TAG_NAME_KEY 								= "tag_name";
	public static final String MESSAGE_TO_USERS_KEY 						= "message_to_users";
	public static final String QUESTION_KEY 								= "question";
	public static final String QUIZ_ANSWER_KEY 							= "quiz_answer";

	public static final String OPTIONS_KEY 								= "options";
	public static final String MESSAGE_USERS_KEY 							= "message_users";

	public static final String IS_BOOKMARKED_KEY						= "bookmark_status";
	public static final String IS_LIKED_KEY								= "like_status";
	
	public static final String COURSE_KEY 									= "course";
	public static final String COURSE_NAME_KEY 									= "course_name";
	public static final String COURSE_PICTURE_URL_KEY 									= "course_picture_url";
	public static final String COURSE_PICTURE_KEY 									= "course_picture";
	public static final String COURSE_ALLOWED_ROLE_KEY 									= "allowed_roles";

	private String courseName = "";
	private String coursePicUrl = "";
	private String coursePic = "";
	private String courseAllowedRole = "";
	
	private int allowComment 			= 0;
	private int approveComment 		= 0;
	private int allowRepost 			= 0;
	private int canEditPost 			= 0;

	private String attachmentUrl 		= "";
	private String author 				= "";
	private String authorPicture 		= "";
	private String authorPictureUrl 	= "";
	private String videoPreviewUrl 		= "";
	
	private String videoPreview 		= "";
	private String postId 				= "";
	private String postAttachment 		= "";
	private String publishDate 			= "";
	private String postCourseName 		= "";
	private String postContentUrl 		= "";
	private String postType 			= "";
	private String postContentType 		= "";
	private String courseOwner 			= "";
	private String postCreator 			= "";
	private String title 				= "";
	private String description 			= "";
	private String tagName 				= "";
	private String messageToUsers 		= "";
	private String question 			= "";
	private String quizAnswer 			= "";

	private int isBookmarked;
	private int isLiked;

	private ArrayList<Options> optionsList	= new ArrayList<Options>(); 
	private ArrayList<MessageUsers> messageUsersList	= new ArrayList<MessageUsers>();

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
			setAllowComment(jsonData.getInt(ALLOW_COMMENT_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setApproveComment(jsonData.getInt(APPROVE_COMMENT_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setAllowRepost(jsonData.getInt(ALLOW_REPOST_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setCanEditPost(jsonData.getInt(CAN_EDIT_POST_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setAttachmentUrl(jsonData.getString(ATTACHMENT_URL_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			if(jsonData.has(VIDEO_PREVIEW_URL_KEY)) setVideoPreviewUrl(jsonData.getString(VIDEO_PREVIEW_URL_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setAuthor(jsonData.getString(AUTHOR_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setAuthorPicture(jsonData.getString(AUTHOR_PICTURE_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}

		try {
			setAuthorPictureUrl(jsonData.getString(AUTHOR_PICTURE_URL_KEY));
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		
		// Course JSONObject
		if(jsonData.has(COURSE_KEY)){
			try {
				JSONObject jsonCourseField = jsonData.getJSONObject(COURSE_KEY);
				
				try {
					setCourseName(jsonCourseField.getString(COURSE_NAME_KEY));
				} 
				catch (Exception e) {	LogWriter.err(e);}
				
				try {
					setCoursePicUrl(jsonCourseField.getString(COURSE_PICTURE_URL_KEY));
				} 
				catch (Exception e) {	LogWriter.err(e);}
				
				try {
					setCoursePic(jsonCourseField.getString(COURSE_PICTURE_KEY));
				} 
				catch (Exception e) {	LogWriter.err(e);}
				
				try {
					setCourseAllowedRole(jsonCourseField.getString(COURSE_ALLOWED_ROLE_KEY));
				} 
				catch (Exception e) {	LogWriter.err(e);}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		

		// Post JSONObject
		try {
			JSONObject jsonPostField = jsonData.getJSONObject(POST_KEY);

			try {
				setPostId(jsonPostField.getString(POST_ID_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostAttachment(jsonPostField.getString(POST_ATTACHMENT_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}
			
			try {
				if(jsonPostField.has(VIDEO_PREVIEW_KEY)) setVideoPreview(jsonPostField.getString(VIDEO_PREVIEW_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPublishDate(jsonPostField.getString(PUBLISH_DATE_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostCourseName(jsonPostField.getString(POST_COURSE_NAME_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostContentUrl(jsonPostField.getString(POST_CONTENT_URL_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostType(jsonPostField.getString(POST_TYPE_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostContentType(jsonPostField.getString(POST_CONTENT_TYPE_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setCourseOwner(jsonPostField.getString(COURSE_OWNER_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				setPostCreator(jsonPostField.getString(POST_CREATOR_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			

			try {
				if(jsonPostField.has(TITLE_KEY)) setTitle(jsonPostField.getString(TITLE_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(DESCRIPTION_KEY)) setDescription(jsonPostField.getString(DESCRIPTION_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(TAG_NAME_KEY)) setTagName(jsonPostField.getString(TAG_NAME_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(MESSAGE_TO_USERS_KEY)) setMessageToUsers(jsonPostField.getString(MESSAGE_TO_USERS_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(QUESTION_KEY)) setQuestion(jsonPostField.getString(QUESTION_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(QUIZ_ANSWER_KEY)) setQuizAnswer(jsonPostField.getString(QUIZ_ANSWER_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}

			try {
				if(jsonPostField.has(OPTIONS_KEY)){
					JSONArray options = jsonPostField.getJSONArray(OPTIONS_KEY);
					clearOptionsList();
					for(int i = 0; i < options.length(); i++) {
						JSONObject jObject = options.getJSONObject(i);
						Options option = new Options();	
						option.parseJSONObject(jObject);
						if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Option :: " + option.toString() );
						optionsList.add(option);
						option = null;
					}
				}
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				if(jsonPostField.has(MESSAGE_USERS_KEY)){
					JSONArray messageUsers = jsonPostField.getJSONArray(MESSAGE_USERS_KEY);
					clearMessageUsersList();
					for(int i = 0; i < messageUsers.length(); i++) {
						JSONObject jObject = messageUsers.getJSONObject(i);
						MessageUsers messageUser = new MessageUsers();	
						messageUser.parseJSONObject(jObject);
						if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "messageUser :: " + messageUser.toString() );
						messageUsersList.add(messageUser);
						messageUser = null;
					}
				}
			} catch (Exception e) {
				LogWriter.err(e);
			}

			//			For isLiked and isBookmarked status
			try {
				if(jsonPostField.has(IS_BOOKMARKED_KEY)) setIsBookmarked(jsonPostField.getInt(IS_BOOKMARKED_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}
			try {
				if(jsonPostField.has(IS_LIKED_KEY)) setIsLiked(jsonPostField.getInt(IS_LIKED_KEY));
			} 
			catch (Exception e) {	LogWriter.err(e);}


		} 
		catch (Exception e) {	LogWriter.err(e);}
	}

	public int getAllowComment() {
		return allowComment;
	}
	public void setAllowComment(int allowComment) {
		this.allowComment = allowComment;
	}

	public int getApproveComment() {
		return approveComment;
	}
	public void setApproveComment(int approveComment) {
		this.approveComment = approveComment;
	}

	public int getAllowRepost() {
		return allowRepost;
	}
	public void setAllowRepost(int allowRepost) {
		this.allowRepost = allowRepost;
	}

	public int getCanEditPost() {
		return canEditPost;
	}
	public void setCanEditPost(int canEditPost) {
		this.canEditPost = canEditPost;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}
	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorPicture() {
		return authorPicture;
	}
	public void setAuthorPicture(String authorPicture) {
		this.authorPicture = authorPicture;
	}

	public String getAuthorPictureUrl() {
		return authorPictureUrl;
	}

	public void setAuthorPictureUrl(String authorPictureUrl) {
		this.authorPictureUrl = authorPictureUrl;
	}

	public String getPostId() {
		return postId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}

	public long getPostIdLong() {
		long id = Flinnt.INVALID;
		try {
			id = Long.parseLong(this.postId);
		} catch (Exception e) {
		}
		return id;
	}
	
	public String getPostAttachment() {
		return postAttachment;
	}
	public void setPostAttachment(String postAttachment) {
		this.postAttachment = postAttachment;
	}

	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getPostCourseName() {
		return postCourseName;
	}
	public void setPostCourseName(String postCourseName) {
		this.postCourseName = postCourseName;
	}

	public String getPostContentUrl() {
		return postContentUrl;
	}
	public void setPostContentUrl(String postContentUrl) {
		this.postContentUrl = postContentUrl;
	}
	public int getPostContentUrlInt() {
		int contentUrl = Flinnt.INVALID;
		try {
			contentUrl = Integer.parseInt(postContentUrl);
		} catch (Exception e) {
		}
		return contentUrl;
	}


	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public int getPostTypeInt() {
		int type = Flinnt.INVALID;
		try {
			type = Integer.parseInt(postType);
		} catch (Exception e) {
		}
		return type;
	}

	public String getPostContentType() {
		return postContentType;
	}
	public void setPostContentType(String postContentType) {
		this.postContentType = postContentType;
	}
	public int getPostContentTypeInt() {
		int contentType = Flinnt.INVALID;
		try {
			contentType = Integer.parseInt(postContentType);
		} catch (Exception e) {
		}
		return contentType;
	}

	public String getCourseOwner() {
		return courseOwner;
	}
	public void setCourseOwner(String courseOwner) {
		this.courseOwner = courseOwner;
	}

	public String getPostCreator() {
		return postCreator;
	}
	public void setPostCreator(String postCreator) {
		this.postCreator = postCreator;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getMessageToUsers() {
		return messageToUsers;
	}
	public void setMessageToUsers(String messageToUsers) {
		this.messageToUsers = messageToUsers;
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuizAnswer() {
		return quizAnswer;
	}
	public void setQuizAnswer(String quizAnswer) {
		this.quizAnswer = quizAnswer;
	}

	public ArrayList<Options> getOptionsList() {
		return optionsList;
	}
	public void setOptionsList(ArrayList<Options> optionsList) {
		this.optionsList = optionsList;
	}
	public void clearOptionsList() {
		this.optionsList.clear();
	}

	public ArrayList<MessageUsers> getMessageUsersList() {
		return messageUsersList;
	}

	public void setMessageUsersList(ArrayList<MessageUsers> messageUsersList) {
		this.messageUsersList = messageUsersList;
	}

	public int getIsBookmarked() {
		return isBookmarked;
	}

	public void setIsBookmarked(int isBookmarked) {
		this.isBookmarked = isBookmarked;
	}

	public int getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(int isLiked) {
		this.isLiked = isLiked;
	}

	public String getVideoPreviewUrl() {
		return videoPreviewUrl;
	}

	public void setVideoPreviewUrl(String videoPreviewUrl) {
		this.videoPreviewUrl = videoPreviewUrl;
	}

	public String getVideoPreview() {
		return videoPreview;
	}

	public void setVideoPreview(String videoPreview) {
		this.videoPreview = videoPreview;
	}

	public void clearMessageUsersList() {
		this.messageUsersList.clear();
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCoursePicUrl() {
		return coursePicUrl;
	}

	public void setCoursePicUrl(String coursePicUrl) {
		this.coursePicUrl = coursePicUrl;
	}

	public String getCoursePic() {
		return coursePic;
	}

	public void setCoursePic(String coursePic) {
		this.coursePic = coursePic;
	}

	public String getCourseAllowedRole() {
		return courseAllowedRole;
	}

	public void setCourseAllowedRole(String courseAllowedRole) {
		this.courseAllowedRole = courseAllowedRole;
	}



	public class Options {

		public static final String OPTION_ID_KEY 								= "id";
		public static final String OPTION_TEXT_ID 							= "text";
		public static final String OPTION_IS_CORRECT_KEY 						= "is_correct";

		private String optionID = "";
		private String optionText = "";
		private String optionIsCorrect = "";

        /**
         * parse json object to suitable data types
         * @param jObject json object
         */
        public synchronized void parseJSONObject(JSONObject jObject) {
			try {
				setOptionID( jObject.getString(OPTION_ID_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setOptionText( jObject.getString(OPTION_TEXT_ID) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setOptionIsCorrect( jObject.getString(OPTION_IS_CORRECT_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
		}

		public String getOptionID() {
			return optionID;
		}
		public void setOptionID(String optionID) {
			this.optionID = optionID;
		}

		public String getOptionText() {
			return optionText;
		}
		public void setOptionText(String optionText) {
			this.optionText = optionText;
		}

		public String getOptionIsCorrect() {
			return optionIsCorrect;
		}
		public void setOptionIsCorrect(String optionIsCorrect) {
			this.optionIsCorrect = optionIsCorrect;
		}

		@Override
		public String toString() {
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("optionID : " + optionID)
			.append(", optionText : " + optionText)
			.append(", optionIsCorrect : " + optionIsCorrect);
			return strBuffer.toString();
		}
	}

	public class MessageUsers {

		public static final String MESSAGE_USER_ID_KEY 	= "user_id";
		public static final String MESSAGE_ROLE_KEY = "role";

		public static final String MESSAGE_USER_LEARNER	= "learner_id";
		public static final String MESSAGE_USER_TEACHER	= "teacher_id";

		private String messageUserID = "";
		private String messageRole = "";

        /**
         * parse json object to suitable data types
         * @param jObject json object
         */
        public synchronized void parseJSONObject(JSONObject jObject) {
			try {
				setMessageUserID( jObject.getString(MESSAGE_USER_ID_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}

			try {
				setMessageRole( jObject.getString(MESSAGE_ROLE_KEY) );
			}
			catch(Exception e){	LogWriter.err(e);}
		}

		public String getMessageUserID() {
			return messageUserID;
		}

		public void setMessageUserID(String messageUserID) {
			this.messageUserID = messageUserID;
		}

		public String getMessageRole() {
			return messageRole;
		}

		public void setMessageRole(String messageRole) {
			this.messageRole = messageRole;
		}

		@Override
		public String toString() {
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("messageUserID : " + messageUserID)
			.append(", messageRole : " + messageRole);
			return strBuffer.toString();
		}
	}

	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("allowComment : " + allowComment)
		.append(", approveComment : " + approveComment)
		.append(", allowRepost : " + allowRepost)
		.append(", canEditPost : " + canEditPost)
		.append(", attachmentUrl : " + attachmentUrl)
		.append(", author : " + author)
		.append(", authorPicture : " + authorPicture)
		.append(", postId : " + postId)
		.append(", postAttachment : " + postAttachment)
		.append(", publishDate : " + publishDate)
		.append(", mCourseNameTxt : " + postCourseName)
		.append(", postContentUrl : " + postContentUrl)
		.append(", postType : " + postType)
		.append(", postContentType : " + postContentType)
		.append(", courseOwner : " + courseOwner)
		.append(", postCreator : " + postCreator)
		.append(", title : " + title)
		.append(", description : " + description)
		.append(", tagName : " + tagName)
		.append(", messageToUsers : " + messageToUsers)
		.append(", question : " + question)
		.append(", quizAnswer : " + quizAnswer)
		.append(", isBookmarked : " + isBookmarked)
		.append(", isLiked : " + isLiked);
		return strBuffer.toString();
	}
}
