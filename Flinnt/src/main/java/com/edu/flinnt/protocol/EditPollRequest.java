package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-3 on 24/2/17.
 */
public class EditPollRequest {

    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("post_id")
    @Expose
    private String post_id;
    @SerializedName("course_id")
    @Expose
    private String course_id;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("poll_end_year")
    @Expose
    private int poll_end_year;
    @SerializedName("poll_end_month")
    @Expose
    private int poll_end_month;
    @SerializedName("poll_end_day")
    @Expose
    private int poll_end_day;
    @SerializedName("poll_end_hour")
    @Expose
    private int poll_end_hour;
    @SerializedName("poll_end_minute")
    @Expose
    private int poll_end_minute;
    @SerializedName("pub_year")
    @Expose
    private int pub_year;
    @SerializedName("pub_month")
    @Expose
    private int pub_month;
    @SerializedName("pub_day")
    @Expose
    private int pub_day;
    @SerializedName("pub_hour")
    @Expose
    private int pub_hour;
    @SerializedName("pub_minute")
    @Expose
    private int pub_minute;
    @SerializedName("resource_id")
    @Expose
    private Long resource_id;
    @SerializedName("post_content_type")
    @Expose
    private int post_content_type;
    @SerializedName("options")
    @Expose
    private List<String> options = new ArrayList<>();
    @SerializedName("answer_ids")
    @Expose
    private List<String> answer_ids = new ArrayList<>();
    @SerializedName("resource_changed")
    @Expose
    private int resource_changed = 0;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getPoll_end_year() {
        return poll_end_year;
    }

    public void setPoll_end_year(int poll_end_year) {
        this.poll_end_year = poll_end_year;
    }

    public int getPoll_end_month() {
        return poll_end_month;
    }

    public void setPoll_end_month(int poll_end_month) {
        this.poll_end_month = poll_end_month;
    }

    public int getPoll_end_day() {
        return poll_end_day;
    }

    public void setPoll_end_day(int poll_end_day) {
        this.poll_end_day = poll_end_day;
    }

    public int getPoll_end_hour() {
        return poll_end_hour;
    }

    public void setPoll_end_hour(int poll_end_hour) {
        this.poll_end_hour = poll_end_hour;
    }

    public int getPoll_end_minute() {
        return poll_end_minute;
    }

    public void setPoll_end_minute(int poll_end_minute) {
        this.poll_end_minute = poll_end_minute;
    }

    public int getPub_year() {
        return pub_year;
    }

    public void setPub_year(int pub_year) {
        this.pub_year = pub_year;
    }

    public int getPub_month() {
        return pub_month;
    }

    public void setPub_month(int pub_month) {
        this.pub_month = pub_month;
    }

    public int getPub_day() {
        return pub_day;
    }

    public void setPub_day(int pub_day) {
        this.pub_day = pub_day;
    }

    public int getPub_hour() {
        return pub_hour;
    }

    public void setPub_hour(int pub_hour) {
        this.pub_hour = pub_hour;
    }

    public int getPub_minute() {
        return pub_minute;
    }

    public void setPub_minute(int pub_minute) {
        this.pub_minute = pub_minute;
    }

    public Long getResource_id() {
        return resource_id;
    }

    public void setResource_id(Long resource_id) {
        this.resource_id = resource_id;
    }

    public int getPost_content_type() {
        return post_content_type;
    }

    public void setPost_content_type(int post_content_type) {
        this.post_content_type = post_content_type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getAnswer_ids() {
        return answer_ids;
    }

    public void setAnswer_ids(List<String> answer_ids) {
        this.answer_ids = answer_ids;
    }

    public int getResource_changed() {
        return resource_changed;
    }

    public void setResource_changed(int resource_changed) {
        this.resource_changed = resource_changed;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
