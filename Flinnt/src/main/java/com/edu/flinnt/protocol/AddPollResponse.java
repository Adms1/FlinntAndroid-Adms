package com.edu.flinnt.protocol;

/**
 * Created by flinnt-android-3 on 27/2/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class AddPollResponse extends BaseResponse{

    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("added")
        @Expose
        private Long added;
        @SerializedName("post")
        @Expose
        private Post post;

        public Long getAdded() {
            return added;
        }

        public void setAdded(Long added) {
            this.added = added;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

    }

    public class Post {

        @SerializedName("post_id")
        @Expose
        private Long postId;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("publish_date")
        @Expose
        private Long publishDate;
        @SerializedName("post_type")
        @Expose
        private String postType;
        @SerializedName("post_content_type")
        @Expose
        private Long postContentType;
        @SerializedName("total_likes")
        @Expose
        private Long totalLikes;
        @SerializedName("total_comments")
        @Expose
        private Long totalComments;
        @SerializedName("is_comment")
        @Expose
        private Long isComment;
        @SerializedName("is_read")
        @Expose
        private Long isRead;
        @SerializedName("is_like")
        @Expose
        private Long isLike;
        @SerializedName("is_bookmark")
        @Expose
        private Long isBookmark;
        @SerializedName("can_delete_post")
        @Expose
        private Long canDeletePost;
        @SerializedName("attachments")
        @Expose
        private String attachments;
        @SerializedName("video_preview")
        @Expose
        private String videoPreview;
        @SerializedName("attachment_is_url")
        @Expose
        private Long attachmentIsUrl;
        @SerializedName("message_to_users")
        @Expose
        private String messageToUsers;
        @SerializedName("can_edit")
        @Expose
        private String canEdit;
        @SerializedName("allow_repost")
        @Expose
        private Long allowRepost;
        @SerializedName("attachment_url")
        @Expose
        private String attachmentUrl;
        @SerializedName("video_thumb_url")
        @Expose
        private String videoThumbUrl;
        @SerializedName("author")
        @Expose
        private String author;
        @SerializedName("author_picture")
        @Expose
        private String authorPicture;
        @SerializedName("author_picture_url")
        @Expose
        private String authorPictureUrl;
        @SerializedName("total_views")
        @Expose
        private Long totalViews;
        @SerializedName("inserted")
        @Expose
        private Long inserted;
        @SerializedName("poll_result_hours")
        @Expose
        private Long pollResultHours;

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(Long publishDate) {
            this.publishDate = publishDate;
        }

        public String getPostType() {
            return postType;
        }

        public void setPostType(String postType) {
            this.postType = postType;
        }

        public Long getPostContentType() {
            return postContentType;
        }

        public void setPostContentType(Long postContentType) {
            this.postContentType = postContentType;
        }

        public Long getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(Long totalLikes) {
            this.totalLikes = totalLikes;
        }

        public Long getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(Long totalComments) {
            this.totalComments = totalComments;
        }

        public Long getIsComment() {
            return isComment;
        }

        public void setIsComment(Long isComment) {
            this.isComment = isComment;
        }

        public Long getIsRead() {
            return isRead;
        }

        public void setIsRead(Long isRead) {
            this.isRead = isRead;
        }

        public Long getIsLike() {
            return isLike;
        }

        public void setIsLike(Long isLike) {
            this.isLike = isLike;
        }

        public Long getIsBookmark() {
            return isBookmark;
        }

        public void setIsBookmark(Long isBookmark) {
            this.isBookmark = isBookmark;
        }

        public Long getCanDeletePost() {
            return canDeletePost;
        }

        public void setCanDeletePost(Long canDeletePost) {
            this.canDeletePost = canDeletePost;
        }

        public String getAttachments() {
            return attachments;
        }

        public void setAttachments(String attachments) {
            this.attachments = attachments;
        }

        public String getVideoPreview() {
            return videoPreview;
        }

        public void setVideoPreview(String videoPreview) {
            this.videoPreview = videoPreview;
        }

        public Long getAttachmentIsUrl() {
            return attachmentIsUrl;
        }

        public void setAttachmentIsUrl(Long attachmentIsUrl) {
            this.attachmentIsUrl = attachmentIsUrl;
        }

        public String getMessageToUsers() {
            return messageToUsers;
        }

        public void setMessageToUsers(String messageToUsers) {
            this.messageToUsers = messageToUsers;
        }

        public String getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(String canEdit) {
            this.canEdit = canEdit;
        }

        public Long getAllowRepost() {
            return allowRepost;
        }

        public void setAllowRepost(Long allowRepost) {
            this.allowRepost = allowRepost;
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

        public Long getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(Long totalViews) {
            this.totalViews = totalViews;
        }

        public Long getInserted() {
            return inserted;
        }

        public void setInserted(Long inserted) {
            this.inserted = inserted;
        }

        public Long getPollResultHours() {
            return pollResultHours;
        }

        public void setPollResultHours(Long pollResultHours) {
            this.pollResultHours = pollResultHours;
        }

    }

}