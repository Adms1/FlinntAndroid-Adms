package com.edu.flinnt.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class StoryDataModel {
    @SerializedName("gallery_url")
    @Expose
    private String galleryUrl;
    @SerializedName("has_more")
    @Expose
    private Integer hasMore;
    @SerializedName("stories")
    @Expose
    private List<Story> stories = null;

    public String getGalleryUrl() {
        return galleryUrl;
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    public Integer getHasMore() {
        return hasMore;
    }

    public void setHasMore(Integer hasMore) {
        this.hasMore = hasMore;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }


    public class Story {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("attachment_type")
        @Expose
        private String attachmentType;
        @SerializedName("attachment")
        @Expose
        private String attachment;
        @SerializedName("course_name")
        @Expose
        private String courseName;
        @SerializedName("like_status")
        @Expose
        private String likeStatus;

        public long getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(long publishDate) {
            this.publishDate = publishDate;
        }

        @SerializedName("publish_date")
        @Expose
        private long publishDate;



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAttachmentType() {
            return attachmentType;
        }

        public void setAttachmentType(String attachmentType) {
            this.attachmentType = attachmentType;
        }

        public String getAttachment() {
            return attachment;
        }

        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(String likeStatus) {
            this.likeStatus = likeStatus;
        }

    }
}
