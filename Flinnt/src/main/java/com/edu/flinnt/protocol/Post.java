package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {
 * "post_id": "9626",
 * "title": "Testing post to delete",
 * "description": "There are two basic rules for writing efficient code:\r\n\r\n- Don't do work that you don't need to do.",
 * "publish_date": "1403606111",
 * "post_type": "1",
 * "post_content_type": "2",
 * "total_likes": "0",
 * "new_comments": "0",
 * "is_read": "1",
 * "can_delete_post": "1",
 * "total_views": "7",
 * "inserted": "1403606111",
 * "message_to_users": "Aditya Sharma, Devansh Suman, Nrupesh Shah, Priyanshi Joshi, Samir Bamaniya", // for messsage type
 * "album_images": "10223_1_1441793706.jpg,10223_2_1441793710.jpg,10223_3_1441793755.jpg" // for album type
 * }
 */


public class Post implements Serializable {

    public static final String POST_ID_KEY = "post_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PUBLISH_DATE_KEY = "publish_date";
    public static final String POST_TYPE_KEY = "post_type";
    public static final String POST_CONTENT_TYPE_KEY = "post_content_type";
    public static final String TOTAL_LIKES_KEY = "total_likes";
    public static final String TOTAL_COMMENTS_KEY = "total_comments";
    public static final String IS_READ_KEY = "is_read";
    public static final String IS_BOOKMARK_KEY = "is_bookmark";
    public static final String CAN_DELETE_POST_KEY = "can_delete_post";
    public static final String TOTAL_VIEWS_KEY = "total_views";
    public static final String INSERTED_KEY = "inserted";
    public static final String MESSAGE_TO_USERS_KEY = "message_to_users";
    public static final String ALBUM_IMAGES_KEY = "album_images";

    //----------------NEW PARAMETER-----------------------

    public static final String POST_ATTACHMENT_KEY = "attachments";
    public static final String VIDEO_PREVIEW_KEY = "video_preview";
    public static final String POST_ATTACHMENT_URL_KEY = "attachment_is_url";
    public static final String CAN_EDIT_KEY = "can_edit";
    public static final String ALLOW_REPOST_KEY = "allow_repost";
    public static final String AUTHOR_KEY = "author";
    public static final String AUTHOR_PICTURE_KEY = "author_picture";
    public static final String AUTHOR_PICTURE_URL_KEY = "author_picture_url";
    public static final String ATTACHMENT_URL_KEY = "attachment_url";
    public static final String VIDEO_THUMB_URL_KEY = "video_thumb_url";
    public static final String IS_LIKE_KEY = "is_like";
    public static final String IS_COMMENT_KEY = "is_comment";
    public static final String POLL_RESULT_HOURS_KEY = "poll_result_hours";

    public static final String CAN_COMMENT_KEY = "can_comment";
    public static final String POST_COURSE_NAME_KEY = "course_name";
    //@Nikhil2662018
    public static final String POST_STORY_ATTACHMENT_KEY = "attachment";
    public static final String POST_STORY_ID_KEY = "story_id";


    public String postID = "";
    public String courseID = "";
    public String title = "";
    public String description = "";
    public String publishDate = "0";
    public String postType = "";
    public String postContentType = "";
    public String totalLikes = "";
    public String totalComments = "";
    public String isRead = "";
    public String isBookmark = "";
    public String canDeletePost = "";
    public String totalViews = "";
    public String inserted = "";
    public String messageToUsers = "";
    public String albumImages = "";
    public boolean isRecentlyAdded = false;

    //----------------NEW PARAMETER-----------------------

    public String attachments = "";
    public String attachmentsIsUrl = "";
    public String videoPreview = "";
    public String canEdit = "";
    public String allowRepost = "";
    public String author = "";
    public String authorPicture = "";
    public String authorPictureUrl = "";
    public String attachmentUrl = "";
    public String videoThumbUrl = "";
    public String isLike = "";
    public String isComment = "";
    public String pollResultHours = "0";
    public String showMoreLess = "0";
    public String viewMoreLess = "0";
    public String pollListResponse = "";

    public String canComment = "0";
    public String courseName = "";
    //@Nikhil 2662018

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String attachment = "";
    public String story_id = "";

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public List<AppInfoDataSet> appInfoDataSets = new ArrayList<>();


    public Post() {

    }

    public Post(AddPostResponse response, String courseId) {
        postID = response.getData().getPost().getPostId() + "";
        courseID = courseId + "";
        title = response.getData().getPost().getTitle() + "";
        description = response.getData().getPost().getDescription() + "";
        publishDate = response.getData().getPost().getPublishDate() + "";
        postType = response.getData().getPost().getPostType() + "";
        postContentType = response.getData().getPost().getPostContentType() + "";
        totalLikes = response.getData().getPost().getTotalLikes() + "";
        totalComments = response.getData().getPost().getTotalComments() + "";
        isRead = response.getData().getPost().getIsRead() + "";
        isBookmark = response.getData().getPost().getIsBookmark() + "";
        canDeletePost = response.getData().getPost().getCanDeletePost() + "";
        totalViews = response.getData().getPost().getTotalViews() + "";
        inserted = response.getData().getPost().getInserted() + "";
        messageToUsers = response.getData().getPost().getMessageToUsers() + "";
        albumImages = response.getData().getPost().getAlbumImages() + "";
        isRecentlyAdded = true;
    }

    public boolean isRecentlyAdded() {
        return isRecentlyAdded;
    }

    public void setRecentlyAdded(boolean recentlyAdded) {
        isRecentlyAdded = recentlyAdded;
    }

    /**
     * parse json object to suitable data types
     *
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

        try {
            if (jObject.has(POST_ID_KEY)) setPostID(jObject.getString(POST_ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(POST_STORY_ATTACHMENT_KEY))
                setAttachment(jObject.getString(POST_STORY_ATTACHMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        } try {
            if (jObject.has(POST_STORY_ID_KEY))
                setStory_id(jObject.getString(POST_STORY_ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(COURSE_ID_KEY)) setCourseID(jObject.getString(COURSE_ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(TITLE_KEY)) setTitle(jObject.getString(TITLE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(DESCRIPTION_KEY)) setDescription(jObject.getString(DESCRIPTION_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(PUBLISH_DATE_KEY)) setPublishDate(jObject.getString(PUBLISH_DATE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(POST_TYPE_KEY)) setPostType(jObject.getString(POST_TYPE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(POST_CONTENT_TYPE_KEY))
                setPostContentType(jObject.getString(POST_CONTENT_TYPE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(TOTAL_LIKES_KEY)) setTotalLikes(jObject.getString(TOTAL_LIKES_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(TOTAL_COMMENTS_KEY))
                setTotalComments(jObject.getString(TOTAL_COMMENTS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(IS_READ_KEY)) setIsRead(jObject.getString(IS_READ_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(IS_BOOKMARK_KEY))
                setIsBookmark(jObject.getString(IS_BOOKMARK_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(CAN_DELETE_POST_KEY))
                setCanDeletePost(jObject.getString(CAN_DELETE_POST_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(TOTAL_VIEWS_KEY)) setTotalViews(jObject.getString(TOTAL_VIEWS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(INSERTED_KEY)) setInserted(jObject.getString(INSERTED_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(MESSAGE_TO_USERS_KEY))
                setMessageToUsers(jObject.getString(MESSAGE_TO_USERS_KEY));
        } catch (Exception e) {
            //LogWriter.err(e);
        }

        try {
            if (jObject.has(ALBUM_IMAGES_KEY)) setAlbumImages(jObject.getString(ALBUM_IMAGES_KEY));
        } catch (Exception e) {
            //LogWriter.err(e);
        }

        //----------------NEW PARAMETER-----------------------


        try {
            if (jObject.has(POST_ATTACHMENT_KEY))
                setAttachments(jObject.getString(POST_ATTACHMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(POST_ATTACHMENT_URL_KEY))
                setAttachmentsIsUrl(jObject.getString(POST_ATTACHMENT_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(VIDEO_PREVIEW_KEY))
                setVideoPreview(jObject.getString(VIDEO_PREVIEW_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(CAN_EDIT_KEY)) setCanEdit(jObject.getString(CAN_EDIT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(ALLOW_REPOST_KEY)) setAllowRepost(jObject.getString(ALLOW_REPOST_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(AUTHOR_KEY)) setAuthor(jObject.getString(AUTHOR_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(AUTHOR_PICTURE_KEY))
                setAuthorPicture(jObject.getString(AUTHOR_PICTURE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(AUTHOR_PICTURE_URL_KEY))
                setAuthorPictureUrl(jObject.getString(AUTHOR_PICTURE_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(ATTACHMENT_URL_KEY))
                setAttachmentUrl(jObject.getString(ATTACHMENT_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(VIDEO_THUMB_URL_KEY))
                setVideoThumbUrl(jObject.getString(VIDEO_THUMB_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(IS_LIKE_KEY))
                setIsLike(jObject.getString(IS_LIKE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(IS_COMMENT_KEY))
                setIsComment(jObject.getString(IS_COMMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(POLL_RESULT_HOURS_KEY))
                setPollResultHours(jObject.getString(POLL_RESULT_HOURS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(CAN_COMMENT_KEY))
                setCanComment(jObject.getString(CAN_COMMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(POST_COURSE_NAME_KEY))
                setCourseName(jObject.getString(POST_COURSE_NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCanComment() {
        return canComment;
    }

    public void setCanComment(String canComment) {
        this.canComment = canComment;
    }

    public String getPostID() {
        return postID;
    }


    public void setPostID(String postID) {
        this.postID = postID;
    }


    public String getCourseID() {
        return courseID;
    }


    public void setCourseID(String courseID) {
        this.courseID = courseID;
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


    public String getPublishDate() {
        return publishDate;
    }

    public long getPublishDateLong() {
        long date = Flinnt.INVALID;
        try {
            date = Long.parseLong(publishDate);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return date;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }


    public String getPostType() {
        return postType;
    }


    public int getPostTypeInt() {
        int type = Flinnt.INVALID;
        try {
            type = Integer.parseInt(postType);
        } catch (Exception e) {
        }
        return type;
    }


    public void setPostType(String postType) {
        this.postType = postType;
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

    public String getTotalLikes() {
        return totalLikes;
    }


    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }


    public String getTotalComments() {
        return totalComments;
    }


    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }


    public String getIsRead() {
        return isRead;
    }


    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public int getIsReadInt() {
        int read = Flinnt.INVALID;
        try {
            read = Integer.parseInt(isRead);
        } catch (Exception e) {
        }
        return read;
    }

    public String getIsBookmark() {
        return isBookmark;
    }


    public void setIsBookmark(String isBookmark) {
        this.isBookmark = isBookmark;
    }

    public int getIsBookmarkInt() {
        int bookmark = Flinnt.INVALID;
        try {
            bookmark = Integer.parseInt(isBookmark);
        } catch (Exception e) {
        }
        return bookmark;
    }

    public String getCanDeletePost() {
        return canDeletePost;
    }


    public void setCanDeletePost(String canDeletePost) {
        this.canDeletePost = canDeletePost;
    }

    public String getTotalViews() {
        return totalViews;
    }


    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }


    public String getInserted() {
        return inserted;
    }


    public void setInserted(String inserted) {
        this.inserted = inserted;
    }


    public String getMessageToUsers() {
        return messageToUsers;
    }


    public void setMessageToUsers(String messageToUsers) {
        this.messageToUsers = messageToUsers;
    }


    public String getAlbumImages() {
        return albumImages;
    }


    public void setAlbumImages(String albumImages) {
        this.albumImages = albumImages;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAttachmentsIsUrl() {
        return attachmentsIsUrl;
    }

    public void setAttachmentsIsUrl(String attachmentsIsUrl) {
        this.attachmentsIsUrl = attachmentsIsUrl;
    }

    public String getVideoPreview() {
        return videoPreview;
    }

    public void setVideoPreview(String videoPreview) {
        this.videoPreview = videoPreview;
    }

    public String getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(String canEdit) {
        this.canEdit = canEdit;
    }

    public String getAllowRepost() {
        return allowRepost;
    }

    public void setAllowRepost(String allowRepost) {
        this.allowRepost = allowRepost;
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

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getVideoThumbUrl() {
        return videoThumbUrl;
    }

    public void setVideoThumbUrl(String videoThumbUrl) {
        this.videoThumbUrl = videoThumbUrl;
    }

    public static String getPostIdKey() {
        return POST_ID_KEY;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getIsComment() {
        return isComment;
    }

    public void setIsComment(String isComment) {
        this.isComment = isComment;
    }

    public String getPollResultHours() {
        return pollResultHours;
    }

    public long getPollResultHoursLong() {
        long date = Flinnt.INVALID;
        try {
            date = Long.parseLong(pollResultHours);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return date;
    }

    public void setPollResultHours(String pollResultHours) {
        this.pollResultHours = pollResultHours;
    }

    public String getShowMoreLess() {
        return showMoreLess;
    }

    public void setShowMoreLess(String showMoreLess) {
        this.showMoreLess = showMoreLess;
    }

    public String getViewMoreLess() {
        return viewMoreLess;
    }

    public void setViewMoreLess(String viewMoreLess) {
        this.viewMoreLess = viewMoreLess;
    }

    public String getPollListResponse() {
        return pollListResponse;
    }

    public void setPollListResponse(String pollListResponse) {
        this.pollListResponse = pollListResponse;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("postID : " + postID)
                .append(", courseID : " + courseID)
                .append(", title : " + title)
                .append(", description : " + description)
                .append(", publishDate : " + publishDate)
                .append(", postType : " + postType)
                .append(", postContentType : " + postContentType)
                .append(", totalLikes : " + totalLikes)
                .append(", newComment : " + totalComments)
                .append(", isRead : " + isRead)
                .append(", canDeletePost : " + canDeletePost)
                .append(", totalViews : " + totalViews)
                .append(", inserted : " + inserted)
                .append(", messageToUsers : " + messageToUsers)
                .append(", albumImages : " + albumImages)
                .append(", attachments : " + attachments)
                .append(", attachmentsIsUrl : " + attachmentsIsUrl)
                .append(", videoPreview : " + videoPreview)
                .append(", canEdit : " + canEdit)
                .append(", allowRepost : " + allowRepost)
                .append(", author : " + author)
                .append(", authorPicture : " + authorPicture)
                .append(", authorPictureUrl : " + authorPictureUrl)
                .append(", attachmentUrl : " + attachmentUrl)
                .append(", videoThumbUrl : " + videoThumbUrl)
                .append(", isLike : " + isLike)
                .append(", isBookmark : " + isBookmark)
                .append(", isComment : " + isComment)
                .append(", canComment : " + canComment)
                .append(", mCourseNameTxt : " + courseName)
                .append(", pollResultHours : " + pollResultHours);

        return strBuffer.toString();
    }

}
