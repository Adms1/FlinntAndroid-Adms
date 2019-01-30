package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * class to parse response to object
 */
public class PostAllowOptionsResponse extends BaseResponse {

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

        @SerializedName("options")
        @Expose
        private Options options;

        public Options getOptions() {
            return options;
        }

        public void setOptions(Options options) {
            this.options = options;
        }

    }

    public class Options {

        @SerializedName("can_schedule")
        @Expose
        private Integer canSchedule;
        @SerializedName("can_select_users")
        @Expose
        private Integer canSelectUsers;
        @SerializedName("can_add_album")
        @Expose
        private Integer canAddAlbum;
        @SerializedName("can_add_attachment")
        @Expose
        private Integer canAddAttachment;
        @SerializedName("can_select_template")
        @Expose
        private Integer canSelectTemplate;

        public Integer getCanSchedule() {
            return canSchedule;
        }

        public void setCanSchedule(Integer canSchedule) {
            this.canSchedule = canSchedule;
        }

        public Integer getCanSelectUsers() {
            return canSelectUsers;
        }

        public void setCanSelectUsers(Integer canSelectUsers) {
            this.canSelectUsers = canSelectUsers;
        }

        public Integer getCanAddAlbum() {
            return canAddAlbum;
        }

        public void setCanAddAlbum(Integer canAddAlbum) {
            this.canAddAlbum = canAddAlbum;
        }

        public Integer getCanAddAttachment() {
            return canAddAttachment;
        }

        public void setCanAddAttachment(Integer canAddAttachment) {
            this.canAddAttachment = canAddAttachment;
        }

        public Integer getCanSelectTemplate() {
            return canSelectTemplate;
        }

        public void setCanSelectTemplate(Integer canSelectTemplate) {
            this.canSelectTemplate = canSelectTemplate;
        }

    }


}
