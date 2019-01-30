package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-2 on 17/11/16.
 */

public class ContentsShowHideResponse extends BaseResponse {

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

        @SerializedName("content")
        @Expose
        private Content content;

        /**
         * @return The content
         */
        public Content getContent() {
            return content;
        }

        /**
         * @param content The content
         */
        public void setContent(Content content) {
            this.content = content;
        }

    }

    public class Content {

        @SerializedName("content_id")
        @Expose
        private String contentId;
        @SerializedName("section_id")
        @Expose
        private String sectionId;
        @SerializedName("content_visible")
        @Expose
        private String contentVisible;

        /**
         * @return The contentId
         */
        public String getContentId() {
            return contentId;
        }

        /**
         * @param contentId The content_id
         */
        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

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
         * @return The contentVisible
         */
        public String getContentVisible() {
            return contentVisible;
        }

        /**
         * @param contentVisible The content_visible
         */
        public void setContentVisible(String contentVisible) {
            this.contentVisible = contentVisible;
        }

    }

}
