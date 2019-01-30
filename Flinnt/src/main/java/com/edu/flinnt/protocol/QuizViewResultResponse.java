package com.edu.flinnt.protocol;

import com.edu.flinnt.downloads.AppInfoDataSet;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by flinnt-android-3 on 8/2/17.
 */
public class QuizViewResultResponse extends BaseResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("result")
        @Expose
        private List<Result> result = null;
        @SerializedName("video_preview_url")
        @Expose
        private String video_preview_url;

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }

        public String getVideo_preview_url() {
            return video_preview_url;
        }

        public void setVideo_preview_url(String video_preview_url) {
            this.video_preview_url = video_preview_url;
        }
    }

    public class Result implements Serializable {
        private AppInfoDataSet appInfoDataSets;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("section")
        @Expose
        private String section;
        @SerializedName("score")
        @Expose
        private String score;
        @SerializedName("reaction_time")
        @Expose
        private Double reactionTime;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("explanation")
        @Expose
        private String explanation;
        @SerializedName("viewed")
        @Expose
        private Integer viewed;
        @SerializedName("answered")
        @Expose
        private Integer answered;
        @SerializedName("marked_for_review")
        @Expose
        private String markedForReview;
        @SerializedName("quiz_que_id")
        @Expose
        private String quizQueId;
        @SerializedName("attachments")
        @Expose
        private List<QuizStartResponse.Attachment> attachments = null;
        @SerializedName("options")
        @Expose
        private List<Option> options = null;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public Double getReactionTime() {
            return reactionTime;
        }

        public void setReactionTime(Double reactionTime) {
            this.reactionTime = reactionTime;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public Integer getViewed() {
            return viewed;
        }

        public void setViewed(Integer viewed) {
            this.viewed = viewed;
        }

        public Integer getAnswered() {
            return answered;
        }

        public void setAnswered(Integer answered) {
            this.answered = answered;
        }

        public String getMarkedForReview() {
            return markedForReview;
        }

        public void setMarkedForReview(String markedForReview) {
            this.markedForReview = markedForReview;
        }

        public String getQuizQueId() {
            return quizQueId;
        }

        public void setQuizQueId(String quizQueId) {
            this.quizQueId = quizQueId;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public List<QuizStartResponse.Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<QuizStartResponse.Attachment> attachments) {
            this.attachments = attachments;
        }

        public AppInfoDataSet getAppInfoDataSets() {
            return appInfoDataSets;
        }

        public void setAppInfoDataSets(AppInfoDataSet appInfoDataSets) {
            this.appInfoDataSets = appInfoDataSets;
        }
    }

    public class Attachment implements Serializable {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("attach_type")
        @Expose
        private String attachType;
        @SerializedName("is_url")
        @Expose
        private String isUrl;
        @SerializedName("file_name")
        @Expose
        private String fileName;
        @SerializedName("attachment_url")
        @Expose
        private String attachmentUrl;
        @SerializedName("attachment_video_thumb")
        @Expose
        private String attachment_video_thumb;


        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttachType() {
            return attachType;
        }

        public void setAttachType(String attachType) {
            this.attachType = attachType;
        }

        public String getIsUrl() {
            return isUrl;
        }

        public void setIsUrl(String isUrl) {
            this.isUrl = isUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        public String getAttachment_video_thumb() {
            return attachment_video_thumb;
        }

        public void setAttachment_video_thumb(String attachment_video_thumb) {
            this.attachment_video_thumb = attachment_video_thumb;
        }
    }

    public class Option implements Serializable {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("selected")
        @Expose
        private Integer selected;
        @SerializedName("is_correct")
        @Expose
        private Integer isCorrect;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getSelected() {
            return selected;
        }

        public void setSelected(Integer selected) {
            this.selected = selected;
        }

        public Integer getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Integer isCorrect) {
            this.isCorrect = isCorrect;
        }

    }

}
