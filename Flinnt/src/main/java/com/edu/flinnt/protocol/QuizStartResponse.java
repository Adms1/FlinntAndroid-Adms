package com.edu.flinnt.protocol;

import com.edu.flinnt.downloads.AppInfoDataSet;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by flinnt-android-3 on 1/2/17.
 */
public class QuizStartResponse extends BaseResponse implements Serializable {

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

    public class Data implements Serializable {

        @SerializedName("auto_terminate")
        @Expose
        private String auto_terminate;
        @SerializedName("test_duration")
        @Expose
        private String test_duration;
        @SerializedName("test_start_timestamp")
        @Expose
        private Long test_start_timestamp;
        @SerializedName("video_preview_url")
        @Expose
        private String video_preview_url;
        @SerializedName("quiz")
        @Expose
        private List<Quiz> quiz = null;

        public List<Quiz> getQuiz() {
            return quiz;
        }

        public void setQuiz(List<Quiz> quiz) {
            this.quiz = quiz;
        }

        public String getAuto_terminate() {
            return auto_terminate;
        }

        public void setAuto_terminate(String auto_terminate) {
            this.auto_terminate = auto_terminate;
        }

        public String getTest_duration() {
            return test_duration;
        }

        public void setTest_duration(String test_duration) {
            this.test_duration = test_duration;
        }

        public Long getTest_start_timestamp() {
            return test_start_timestamp;
        }

        public void setTest_start_timestamp(Long test_start_timestamp) {
            this.test_start_timestamp = test_start_timestamp;
        }

        public String getVideo_preview_url() {
            return video_preview_url;
        }

        public void setVideo_preview_url(String video_preview_url) {
            this.video_preview_url = video_preview_url;
        }
    }

    public class Quiz implements Serializable {
        private AppInfoDataSet appInfoDataSets;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("id")
        @Expose
        private String id;
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
        private String markedForReview = "";
        @SerializedName("quiz_que_id")
        @Expose
        private String quizQueId;
        @SerializedName("attachments")
        @Expose
        private List<Attachment> attachments = null;
        @SerializedName("options")
        @Expose
        private List<Option> options = null;
        private int relativePosition = 0;

        private String answerId = "0";

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


        public List<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<Attachment> attachments) {
            this.attachments = attachments;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public String getAnswerId() {
            return answerId;
        }

        public void setAnswerId(String answerId) {
            this.answerId = answerId;
        }

        public int getRelativePosition() {
            return relativePosition;
        }

        public void setRelativePosition(int relativePosition) {
            this.relativePosition = relativePosition;
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
        @SerializedName("is_correct")
        @Expose
        private String isCorrect;
        @SerializedName("selected")
        @Expose
        private int selected = 0;

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

        public String getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(String isCorrect) {
            this.isCorrect = isCorrect;
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }


    }
}
