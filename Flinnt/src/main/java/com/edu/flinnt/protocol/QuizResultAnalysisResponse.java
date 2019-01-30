package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class QuizResultAnalysisResponse extends BaseResponse {

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

        @SerializedName("test_name")
        @Expose
        private String testName;
        @SerializedName("course_name")
        @Expose
        private String courseName;
        @SerializedName("test_date")
        @Expose
        private String testDate;
        @SerializedName("grade")
        @Expose
        private String grade;
        @SerializedName("grade_text")
        @Expose
        private String gradeText;
        @SerializedName("grade_picture_url")
        @Expose
        private String gradePictureUrl;
        @SerializedName("grade_picture")
        @Expose
        private String gradePicture;
        @SerializedName("total_marks")
        @Expose
        private Integer totalMarks;
        @SerializedName("result")
        @Expose
        private ArrayList<Result> result = null;
        @SerializedName("summary")
        @Expose
        private ArrayList<Summary> summary = null;
        @SerializedName("grades")
        @Expose
        private List<Grade> grades = null;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getTestDate() {
            return testDate;
        }

        public void setTestDate(String testDate) {
            this.testDate = testDate;
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

        public String getGradePictureUrl() {
            return gradePictureUrl;
        }

        public void setGradePictureUrl(String gradePictureUrl) {
            this.gradePictureUrl = gradePictureUrl;
        }

        public String getGradePicture() {
            return gradePicture;
        }

        public void setGradePicture(String gradePicture) {
            this.gradePicture = gradePicture;
        }

        public Integer getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(Integer totalMarks) {
            this.totalMarks = totalMarks;
        }

        public ArrayList<Result> getResult() {
            return result;
        }

        public void setResult(ArrayList<Result> result) {
            this.result = result;
        }

        public ArrayList<Summary> getSummary() {
            return summary;
        }

        public void setSummary(ArrayList<Summary> summary) {
            this.summary = summary;
        }

        public List<Grade> getGrades() {
            return grades;
        }

        public void setGrades(List<Grade> grades) {
            this.grades = grades;
        }
    }

    public class Grade {

        @SerializedName("grade")
        @Expose
        private String grade;
        @SerializedName("range")
        @Expose
        private String range;

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }
    }

    public class Result {

        @SerializedName("no")
        @Expose
        private Integer no;
        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("selected_answer")
        @Expose
        private String selectedAnswer;
        @SerializedName("correct_answer")
        @Expose
        private String correctAnswer;
        @SerializedName("course_correct")
        @Expose
        private Integer courseCorrect;
        @SerializedName("score")
        @Expose
        private String score;

        public Integer getNo() {
            return no;
        }

        public void setNo(Integer no) {
            this.no = no;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getSelectedAnswer() {
            return selectedAnswer;
        }

        public void setSelectedAnswer(String selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public Integer getCourseCorrect() {
            return courseCorrect;
        }

        public void setCourseCorrect(Integer courseCorrect) {
            this.courseCorrect = courseCorrect;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }

    public class Summary {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("total_questions")
        @Expose
        private Integer totalQuestions;
        @SerializedName("total_correct")
        @Expose
        private Integer totalCorrect;
        @SerializedName("total_wrong")
        @Expose
        private Integer totalWrong;
        @SerializedName("total_unanswered")
        @Expose
        private Integer totalUnanswered;

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public Integer getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(Integer totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public Integer getTotalCorrect() {
            return totalCorrect;
        }

        public void setTotalCorrect(Integer totalCorrect) {
            this.totalCorrect = totalCorrect;
        }

        public Integer getTotalWrong() {
            return totalWrong;
        }

        public void setTotalWrong(Integer totalWrong) {
            this.totalWrong = totalWrong;
        }

        public Integer getTotalUnanswered() {
            return totalUnanswered;
        }

        public void setTotalUnanswered(Integer totalUnanswered) {
            this.totalUnanswered = totalUnanswered;
        }
    }
}