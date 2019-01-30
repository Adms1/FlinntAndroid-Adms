package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-2 on 17/11/16.
 */

public class SectionShowHideResponse extends BaseResponse {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * @return The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("section")
        @Expose
        private Section section;

        /**
         * @return The section
         */
        public Section getSection() {
            return section;
        }

        /**
         * @param section The section
         */
        public void setSection(Section section) {
            this.section = section;
        }

    }

    public class Section {

        @SerializedName("section_id")
        @Expose
        private String sectionId;
        @SerializedName("section_visible")
        @Expose
        private String sectionVisible;

        /**
         * @return The sectionId
         */
        public String getSectionId() {
            return sectionId;
        }

        /**
         * @param sectionId The section_id
         */
        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        /**
         * @return The sectionVisible
         */
        public String getSectionVisible() {
            return sectionVisible;
        }

        /**
         * @param sectionVisible The section_visible
         */
        public void setSectionVisible(String sectionVisible) {
            this.sectionVisible = sectionVisible;
        }

    }
}
