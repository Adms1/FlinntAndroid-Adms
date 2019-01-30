package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-3 on 8/2/17.
 */
public class QuizResultSummaryResponse extends BaseResponse {

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
        private Result result;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

    }

    public class Result {

        @SerializedName("attempt_date")
        @Expose
        private String attemptDate;
        @SerializedName("duration")
        @Expose
        private String duration;
        @SerializedName("marks_max")
        @Expose
        private String marksMax;
        @SerializedName("marks_obtained")
        @Expose
        private String marksObtained;
        @SerializedName("score_percent")
        @Expose
        private Integer scorePercent;
        @SerializedName("grade")
        @Expose
        private String grade;
        @SerializedName("grade_text")
        @Expose
        private String gradeText;

        @SerializedName("grade_picture_url")
        @Expose
        private String gradePictureURL;

        @SerializedName("grade_picture")
        @Expose
        private String gradePicture;

        @SerializedName("rank")
        @Expose
        private Integer rank;

        @SerializedName("rank_total")
        @Expose
        private Integer rankTotal;

        @SerializedName("rank_text")
        @Expose
        private String rankText;


        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Integer getRankTotal() {
            return rankTotal;
        }

        public void setRankTotal(Integer rankTotal) {
            this.rankTotal = rankTotal;
        }

        public String getRankText() {
            return rankText;
        }

        public void setRankText(String rankText) {
            this.rankText = rankText;
        }

        public String getGradePicture() {
            return gradePicture;
        }

        public void setGradePicture(String gradePicture) {
            this.gradePicture = gradePicture;
        }

        public String getGradePictureURL() {
            return gradePictureURL;
        }

        public void setGradePictureURL(String gradePictureURL) {
            this.gradePictureURL = gradePictureURL;
        }

        public String getAttemptDate() {
            return attemptDate;
        }

        public void setAttemptDate(String attemptDate) {
            this.attemptDate = attemptDate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getMarksMax() {
            return marksMax;
        }

        public void setMarksMax(String marksMax) {
            this.marksMax = marksMax;
        }

        public String getMarksObtained() {
            return marksObtained;
        }

        public void setMarksObtained(String marksObtained) {
            this.marksObtained = marksObtained;
        }

        public Integer getScorePercent() {
            return scorePercent;
        }

        public void setScorePercent(Integer scorePercent) {
            this.scorePercent = scorePercent;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getGradeText() {
            return gradeText;
        }

        public void setGradeText(String gradeText) {
            this.gradeText = gradeText;
        }

    }
}
