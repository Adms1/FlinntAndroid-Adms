package com.edu.flinnt.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Admin on 13-02-2018.
 */

public class PopularStoryDataModel {

    @SerializedName("gallery_url")
    @Expose
    private String galleryUrl;
    @SerializedName("stories")
    @Expose
    private List<Story> stories = null;

    public String getGalleryUrl() {
        return galleryUrl;
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
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
        @SerializedName("attachment_type")
        @Expose
        private String attachmentType;
        @SerializedName("attachment")
        @Expose
        private String attachment;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("course_name")
        @Expose
        private String courseName;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

    }
}
