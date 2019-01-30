package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * class to parse response to object
 */
public class ContentAddCommentResponse extends BaseResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * @return The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("added")
        @Expose
        private Integer added;
        @SerializedName("show_comment")
        @Expose
        private Integer showComment;
        @SerializedName("count")
        @Expose
        private String count;
        @SerializedName("comment")
        @Expose
        private Comment comment;

        /**
         * @return The added
         */
        public Integer getAdded() {
            return added;
        }

        /**
         * @param added The added
         */
        public void setAdded(Integer added) {
            this.added = added;
        }

        /**
         * @return The showComment
         */
        public Integer getShowComment() {
            return showComment;
        }

        /**
         * @param showComment The show_comment
         */
        public void setShowComment(Integer showComment) {
            this.showComment = showComment;
        }

        /**
         * @return The count
         */
        public String getCount() {
            return count;
        }

        /**
         * @param count The count
         */
        public void setCount(String count) {
            this.count = count;
        }

        /**
         * @return The comment
         */
        public Comment getComment() {
            return comment;
        }

        /**
         * @param comment The comment
         */
        public void setComment(Comment comment) {
            this.comment = comment;
        }

    }

    public class Comment {
        @SerializedName("comment_id")
        @Expose
        private Integer commentId;
        @SerializedName("comment_text")
        @Expose
        private String commentText;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("user_picture")
        @Expose
        private String userPicture;
        @SerializedName("user_picture_url")
        @Expose
        private String userPictureUrl;
        @SerializedName("comment_date")
        @Expose
        private Integer commentDate;
        @SerializedName("can_delete")
        @Expose
        private Integer canDelete;
        @SerializedName("comment_user_id")
        @Expose
        private Integer commentUserId;

        /**
         * @return The commentId
         */
        public Integer getCommentId() {
            return commentId;
        }

        /**
         * @param commentId The comment_id
         */
        public void setCommentId(Integer commentId) {
            this.commentId = commentId;
        }

        /**
         * @return The commentText
         */
        public String getCommentText() {
            return commentText;
        }

        /**
         * @param commentText The comment_text
         */
        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        /**
         * @return The userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @param userName The user_name
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * @return The userPicture
         */
        public String getUserPicture() {
            return userPicture;
        }

        /**
         * @param userPicture The user_picture
         */
        public void setUserPicture(String userPicture) {
            this.userPicture = userPicture;
        }

        /**
         * @return The userPictureUrl
         */
        public String getUserPictureUrl() {
            return userPictureUrl;
        }

        /**
         * @param userPictureUrl The user_picture_url
         */
        public void setUserPictureUrl(String userPictureUrl) {
            this.userPictureUrl = userPictureUrl;
        }

        /**
         * @return The commentDate
         */
        public Integer getCommentDate() {
            return commentDate;
        }

        /**
         * @param commentDate The comment_date
         */
        public void setCommentDate(Integer commentDate) {
            this.commentDate = commentDate;
        }

        /**
         * @return The canDelete
         */
        public Integer getCanDelete() {
            return canDelete;
        }

        /**
         * @param canDelete The can_delete
         */
        public void setCanDelete(Integer canDelete) {
            this.canDelete = canDelete;
        }

        /**
         * @return The commentUserId
         */
        public Integer getCommentUserId() {
            return commentUserId;
        }

        /**
         * @param commentUserId The comment_user_id
         */
        public void setCommentUserId(Integer commentUserId) {
            this.commentUserId = commentUserId;
        }
    }
}