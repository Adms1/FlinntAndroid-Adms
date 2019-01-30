package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-2 on 12/4/17.
 */

public class Sections {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("is_visible")
    @Expose
    private String isVisible;
    @SerializedName("sr_no")
    @Expose
    private String srNo;
    @SerializedName("contents")
    @Expose
    private List<Content> contents = new ArrayList<Content>();

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The isVisible
     */
    public String getIsVisible() {
        return isVisible;
    }

    /**
     * @param isVisible The is_visible
     */
    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return The srNo
     */
    public String getSrNo() {
        return srNo;
    }

    /**
     * @param srNo The sr_no
     */
    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    /**
     * @return The contents
     */
    public List<Content> getContents() {
        return contents;
    }

    /**
     * @param contents The contents
     */
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}
