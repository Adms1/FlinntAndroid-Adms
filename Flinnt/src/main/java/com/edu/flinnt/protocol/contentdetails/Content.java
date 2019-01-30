package com.edu.flinnt.protocol.contentdetails;

/**
 * Created by flinnt-android-3 on 13/12/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Content  extends RealmObject{

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
    private String section_title;
    @SerializedName("attachment_type")
    @Expose
    private Integer attachment_type;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("attachment_is_url")
    @Expose
    private Integer attachment_is_url;
    @SerializedName("attachment_video_thumb")
    @Expose
    private String attachment_video_thumb;
    @SerializedName("attachment_do_encode")
    @Expose
    private long attachment_do_encode;
    @SerializedName("like_status")
    @Expose
    private Integer like_status;
    @SerializedName("course_name")
    @Expose
    private String course_name;
    @SerializedName("course_picture")
    @Expose
    private String course_picture;
    @SerializedName("course_picture_url")
    @Expose
    private String course_pictureUrl;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
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
     * The section_title
     */
    public String getSectionTitle() {
        return section_title;
    }

    /**
     *
     * @param section_title
     * The section_title
     */
    public void setSectionTitle(String section_title) {
        this.section_title = section_title;
    }

    /**
     *
     * @return
     * The attachment_type
     */
    public Integer getAttachmentType() {
        return attachment_type;
    }

    /**
     *
     * @param attachment_type
     * The attachment_type
     */
    public void setAttachmentType(Integer attachment_type) {
        this.attachment_type = attachment_type;
    }

    /**
     *
     * @return
     * The attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     *
     * @param attachment
     * The attachment
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    /**
     *
     * @return
     * The attachment_is_url
     */
    public Integer getAttachmentIsUrl() {
        return attachment_is_url;
    }

    /**
     *
     * @param attachment_is_url
     * The attachment_is_url
     */
    public void setAttachmentIsUrl(Integer attachment_is_url) {
        this.attachment_is_url = attachment_is_url;
    }

    /**
     *
     * @return
     * The attachment_video_thumb
     */
    public String getAttachmentVideoThumb() {
        return attachment_video_thumb;
    }

    /**
     *
     * @param attachment_video_thumb
     * The attachment_video_thumb
     */
    public void setAttachmentVideoThumb(String attachment_video_thumb) {
        this.attachment_video_thumb = attachment_video_thumb;
    }

    /**
     *
     * @return
     * The attachment_do_encode
     */
    public long getAttachmentDoEncode() {
        return attachment_do_encode;
    }

    /**
     *
     * @param attachment_do_encode
     * The attachment_do_encode
     */
    public void setAttachmentDoEncode(long attachment_do_encode) {
        this.attachment_do_encode = attachment_do_encode;
    }

    /**
     *
     * @return
     * The like_status
     */
    public Integer getLikeStatus() {
        return like_status;
    }

    /**
     *
     * @param like_status
     * The like_status
     */
    public void setLikeStatus(Integer like_status) {
        this.like_status = like_status;
    }

    /**
     *
     * @return
     * The course_name
     */
    public String getCourseName() {
        return course_name;
    }

    /**
     *
     * @param course_name
     * The course_name
     */
    public void setCourseName(String course_name) {
        this.course_name = course_name;
    }

    /**
     *
     * @return
     * The course_picture
     */
    public String getCoursePicture() {
        return course_picture;
    }

    /**
     *
     * @param course_picture
     * The course_picture
     */
    public void setCoursePicture(String course_picture) {
        this.course_picture = course_picture;
    }

    /**
     *
     * @return
     * The course_pictureUrl
     */
    public String getCoursePictureUrl() {
        return course_pictureUrl;
    }

    /**
     *
     * @param course_pictureUrl
     * The course_picture_url
     */
    public void setCoursePictureUrl(String course_pictureUrl) {
        this.course_pictureUrl = course_pictureUrl;
    }

    public void delete() throws Exception {
        this.deleteFromRealm();
    }
}