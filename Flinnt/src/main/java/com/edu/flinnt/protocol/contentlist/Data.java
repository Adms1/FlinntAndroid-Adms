package com.edu.flinnt.protocol.contentlist;

import com.edu.flinnt.Flinnt;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by flinnt-android-1 on 11/11/16.
 */

public class Data extends RealmObject {
//    @PrimaryKey
    public String courseID = "";
    public String userID = "";
    private int offset = Flinnt.INVALID;

    @SerializedName("has_more")
    @Expose
    private Integer hasMore;
    @SerializedName("section_count")
    @Expose
    private Integer sectionCount;
    @SerializedName("content_count")
    @Expose
    private Integer contentCount;

    //@chiragp 26/07/2018
    @SerializedName("service")
    @Expose
    private Service service;

    @SerializedName("list")
    @Expose
    private RealmList<Sections> list = new RealmList<Sections>();

    /**
     * @return The hasMore
     */
    public Integer getHasMore() {
        return hasMore;
    }

    /**
     * @param hasMore The has_more
     */
    public void setHasMore(Integer hasMore) {
        this.hasMore = hasMore;
    }

    public Integer getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }

    public RealmList<Sections> getList() {
        return list;
    }

    public void setList(RealmList<Sections> list) {
        this.list = list;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}