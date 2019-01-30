package com.edu.flinnt.protocol.contentdetails;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by flinnt-android-3 on 13/12/16.
 */

public class ContentDetailsResponse extends RealmObject {

    @SerializedName("allow_comment")
    @Expose
    private long allow_comment;
    @SerializedName("approve_comment")
    @Expose
    private long approve_comment;
    @SerializedName("video_preview_url")
    @Expose
    private String video_preview_url;
    @SerializedName("attachment_url")
    @Expose
    private String attachment_url;
    @SerializedName("content")
    @Expose
    private Content content;

    private String contentID;

    private String userId;

    @PrimaryKey
    private String id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    /**
     *
     * @return
     * The allowComment
     */
    public long getAllowComment() {
        return allow_comment;
    }

    /**
     *
     * @param allow_comment
     * The allow_comment
     */
    public void setAllowComment(long allow_comment) {
        this.allow_comment = allow_comment;
    }

    /**
     *
     * @return
     * The approveComment
     */
    public long getApproveComment() {
        return approve_comment;
    }

    /**
     *
     * @param approve_comment
     * The approve_comment
     */
    public void setApproveComment(long approve_comment) {
        this.approve_comment = approve_comment;
    }

    /**
     *
     * @return
     * The videoPreviewUrl
     */
    public String getVideoPreviewUrl() {
        return video_preview_url;
    }

    /**
     *
     * @param video_preview_url
     * The video_preview_url
     */
    public void setVideoPreviewUrl(String video_preview_url) {
        this.video_preview_url = video_preview_url;
    }

    /**
     *
     * @return
     * The attachmentUrl
     */
    public String getAttachmentUrl() {
        return attachment_url;
    }

    /**
     *
     * @param attachment_url
     * The attachment_url
     */
    public void setAttachmentUrl(String attachment_url) {
        this.attachment_url = attachment_url;
    }

    /**
     *
     * @return
     * The content
     */
    public Content getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(Content content) {
        this.content = content;
    }

    public void  delete() throws Exception{

            content.delete();
            this.deleteFromRealm();

    }

    public static boolean isSuccessResponse(JSONObject response) {
        boolean ret = false;

        int status = 0;
        try {
            status = response.getInt(Flinnt.STATUS_KEY);
        }
        catch(Exception e){
            LogWriter.err(e);
        }

        if( Flinnt.SUCCESS == status ) {
            ret = true;
        }

        return ret;
    }
    public static JSONObject getJSONData(JSONObject response) {

        JSONObject data = null;
        try {
            data = response.getJSONObject(Flinnt.DATA_KEY);
        }
        catch(Exception e){
            LogWriter.err(e);
        }

        return data;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }


}
