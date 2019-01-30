package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-1 on 27/8/16.
 */
public class ContentComment {

        @SerializedName("comment_id")
        @Expose
        private String commentId;
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
        private String commentDate;
        @SerializedName("can_delete")
        @Expose
        private String canDelete;
        @SerializedName("comment_user_id")
        @Expose
        private String commentUserId;

        /**
         * @return The commentId
         */
        public String getCommentId() {
            return commentId;
        }

        /**
         * @param commentId The comment_id
         */
        public void setCommentId(String commentId) {
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
        public String getCommentDate() {
            return commentDate;
        }

        /**
         * @param commentDate The comment_date
         */
        public void setCommentDate(String commentDate) {
            this.commentDate = commentDate;
        }

        /**
         * @return The canDelete
         */
        public int getCanDelete() {
            int canDeleteItn = Flinnt.INVALID;
            try {
                canDeleteItn = Integer.parseInt(canDelete);
            } catch (Exception e) {

            }
            return canDeleteItn;
        }

        /**
         * @param canDelete The can_delete
         */
        public void setCanDelete(String canDelete) {
            this.canDelete = canDelete;
        }

        /**
         * @return The commentUserId
         */
        public String getCommentUserId() {
            return commentUserId;
        }

        /**
         * @param commentUserId The comment_user_id
         */
        public void setCommentUserId(String commentUserId) {
            this.commentUserId = commentUserId;
        }

}
