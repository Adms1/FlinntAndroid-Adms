package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * class to parse response to object
 */
public class ContentsDetailsResponse extends BaseResponse {

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

        @SerializedName("allow_comment")
        @Expose
        private Integer allowComment;
        @SerializedName("approve_comment")
        @Expose
        private Integer approveComment;
        @SerializedName("video_preview_url")
        @Expose
        private String videoPreviewUrl;
        @SerializedName("attachment_url")
        @Expose
        private String attachmentUrl;
        @SerializedName("content")
        @Expose
        private Content content;

        /**
         * @return The allowComment
         */
        public Integer getAllowComment() {
            return allowComment;
        }

        /**
         * @param allowComment The allow_comment
         */
        public void setAllowComment(Integer allowComment) {
            this.allowComment = allowComment;
        }

        /**
         * @return The approveComment
         */
        public Integer getApproveComment() {
            return approveComment;
        }

        /**
         * @param approveComment The approve_comment
         */
        public void setApproveComment(Integer approveComment) {
            this.approveComment = approveComment;
        }

        /**
         * @return The videoPreviewUrl
         */
        public String getVideoPreviewUrl() {
            return videoPreviewUrl;
        }

        /**
         * @param videoPreviewUrl The video_preview_url
         */
        public void setVideoPreviewUrl(String videoPreviewUrl) {
            this.videoPreviewUrl = videoPreviewUrl;
        }

        /**
         * @return The attachmentUrl
         */
        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        /**
         * @param attachmentUrl The attachment_url
         */
        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        /**
         * @return The content
         */
        public Content getContent() {
            return content;
        }

        /**
         * @param content The content
         */
        public void setContent(Content content) {
            this.content = content;
        }

    }

    public class Content {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("section_title")
        @Expose
        private String sectionTitle;
        @SerializedName("attachment_type")
        @Expose
        private String attachmentType;
        @SerializedName("attachment")
        @Expose
        private String attachment;
        @SerializedName("attachment_is_url")
        @Expose
        private String attachmentIsUrl;
        @SerializedName("attachment_video_thumb")
        @Expose
        private String attachmentVideoThumb;
        @SerializedName("attachment_do_encode")
        @Expose
        private String attachmentDoEncode;
        @SerializedName("like_status")
        @Expose
        private String likeStatus;
        @SerializedName("course_name")
        @Expose
        private String courseName;
        @SerializedName("course_picture")
        @Expose
        private String coursePicture;
        @SerializedName("course_picture_url")
        @Expose
        private String coursePictureUrl;

        /**
         * @return The id
         */
        public long getId() {
            long id = Flinnt.INVALID;
            try {
                id = Long.parseLong(this.id);
            } catch (Exception e) {
                LogWriter.err(e);
            }
            return id;
        }

        public String getContentId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return The description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description The description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return The sectionTitle
         */
        public String getSectionTitle() {
            return sectionTitle;
        }

        /**
         * @param sectionTitle The section_title
         */
        public void setSectionTitle(String sectionTitle) {
            this.sectionTitle = sectionTitle;
        }

        /**
         * @return The attachmentType
         */
        public int getAttachmentType() {
            int contentType = Flinnt.INVALID;
            try {
                contentType = Integer.parseInt(attachmentType);
            } catch (Exception e) {
                LogWriter.err(e);
            }
            return contentType;
        }

        /**
         * @param attachmentType The attachment_type
         */
        public void setAttachmentType(String attachmentType) {
            this.attachmentType = attachmentType;
        }

        /**
         * @return The attachment
         */
        public String getAttachment() {
            return attachment;
        }

        /**
         * @param attachment The attachment
         */
        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        /**
         * @return The attachmentIsUrl
         */
        public long getAttachmentIsUrl() {
            long attachmentIsUrl = Flinnt.INVALID;
            try {
                attachmentIsUrl = Long.parseLong(this.attachmentIsUrl);
            } catch (Exception e) {
                LogWriter.err(e);
            }
            return attachmentIsUrl;
        }

        /**
         * @param attachmentIsUrl The attachment_is_url
         */
        public void setAttachmentIsUrl(String attachmentIsUrl) {
            this.attachmentIsUrl = attachmentIsUrl;
        }

        /**
         * @return The attachmentVideoThumb
         */
        public String getAttachmentVideoThumb() {
            return attachmentVideoThumb;
        }

        /**
         * @param attachmentVideoThumb The attachment_video_thumb
         */
        public void setAttachmentVideoThumb(String attachmentVideoThumb) {
            this.attachmentVideoThumb = attachmentVideoThumb;
        }

        public String getAttachmentDoEncode() {
            return attachmentDoEncode;
        }

        public void setAttachmentDoEncode(String attachmentDoEncode) {
            this.attachmentDoEncode = attachmentDoEncode;
        }

        /**
         * @return The likeStatus
         */
        public int getLikeStatus() {
            int likesStatus = 0;
            try {
                likesStatus = Integer.parseInt(likeStatus);
            } catch (Exception e) {
                LogWriter.err(e);
            }
            return likesStatus;
        }

        /**
         * @param likeStatus The like_status
         */
        public void setLikeStatus(String likeStatus) {
            this.likeStatus = likeStatus;
        }

        /**
         *
         * @return
         * The mCourseNameTxt
         */
        public String getCourseName() {
            return courseName;
        }

        /**
         *
         * @param courseName
         * The course_name
         */
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        /**
         *
         * @return
         * The coursePicture
         */
        public String getCoursePicture() {
            return coursePicture;
        }

        /**
         *
         * @param coursePicture
         * The course_picture
         */
        public void setCoursePicture(String coursePicture) {
            this.coursePicture = coursePicture;
        }

        /**
         *
         * @return
         * The coursePictureUrl
         */
        public String getCoursePictureUrl() {
            return coursePictureUrl;
        }

        /**
         *
         * @param coursePictureUrl
         * The course_picture_url
         */
        public void setCoursePictureUrl(String coursePictureUrl) {
            this.coursePictureUrl = coursePictureUrl;
        }


    }

}
