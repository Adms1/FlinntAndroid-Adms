package com.edu.flinnt.protocol;

/**
 * Created by flinnt-android-3 on 27/2/17.
 */

import java.util.HashMap;
import java.util.Map;

public class RepostPollResponse extends BaseResponse{

    private long status;
    private Data data;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public class Data {

        private long added;
        private Post post;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public long getAdded() {
            return added;
        }

        public void setAdded(long added) {
            this.added = added;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

    public class Post {

        private long postId;
        private String title;
        private String description;
        private long publishDate;
        private String postType;
        private long postContentType;
        private long totalLikes;
        private long totalComments;
        private long isRead;
        private long isBookmark;
        private long canDeletePost;
        private String albumImages;
        private String messageToUsers;
        private long totalViews;
        private long inserted;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public long getPostId() {
            return postId;
        }

        public void setPostId(long postId) {
            this.postId = postId;
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

        public long getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(long publishDate) {
            this.publishDate = publishDate;
        }

        public String getPostType() {
            return postType;
        }

        public void setPostType(String postType) {
            this.postType = postType;
        }

        public long getPostContentType() {
            return postContentType;
        }

        public void setPostContentType(long postContentType) {
            this.postContentType = postContentType;
        }

        public long getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(long totalLikes) {
            this.totalLikes = totalLikes;
        }

        public long getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(long totalComments) {
            this.totalComments = totalComments;
        }

        public long getIsRead() {
            return isRead;
        }

        public void setIsRead(long isRead) {
            this.isRead = isRead;
        }

        public long getIsBookmark() {
            return isBookmark;
        }

        public void setIsBookmark(long isBookmark) {
            this.isBookmark = isBookmark;
        }

        public long getCanDeletePost() {
            return canDeletePost;
        }

        public void setCanDeletePost(long canDeletePost) {
            this.canDeletePost = canDeletePost;
        }

        public String getAlbumImages() {
            return albumImages;
        }

        public void setAlbumImages(String albumImages) {
            this.albumImages = albumImages;
        }

        public String getMessageToUsers() {
            return messageToUsers;
        }

        public void setMessageToUsers(String messageToUsers) {
            this.messageToUsers = messageToUsers;
        }

        public long getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(long totalViews) {
            this.totalViews = totalViews;
        }

        public long getInserted() {
            return inserted;
        }

        public void setInserted(long inserted) {
            this.inserted = inserted;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

}