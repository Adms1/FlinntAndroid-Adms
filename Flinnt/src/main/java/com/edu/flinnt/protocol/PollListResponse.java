package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by flinnt-android-2 on 3/3/17.
 */

public class PollListResponse extends BaseResponse implements Serializable {

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

    public class Data implements Serializable{

        @SerializedName("poll_finished")
        @Expose
        private Integer pollFinished;
        @SerializedName("poll_voted")
        @Expose
        private Integer pollVoted;
        @SerializedName("voted_option")
        @Expose
        private String votedOption;
        @SerializedName("options")
        @Expose
        private List<Option> options = null;

        public Integer getPollFinished() {
            return pollFinished;
        }

        public void setPollFinished(Integer pollFinished) {
            this.pollFinished = pollFinished;
        }

        public Integer getPollVoted() {
            return pollVoted;
        }

        public void setPollVoted(Integer pollVoted) {
            this.pollVoted = pollVoted;
        }

        public String getVotedOption() {
            return votedOption;
        }

        public void setVotedOption(String votedOption) {
            this.votedOption = votedOption;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

    }

    public class Option implements Serializable{

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("votes_received")
        @Expose
        private String votesReceived;
        @SerializedName("votes_percent")
        @Expose
        private String votesPercent;

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

        public String getVotesReceived() {
            return votesReceived;
        }

        public void setVotesReceived(String votesReceived) {
            this.votesReceived = votesReceived;
        }

        public String getVotesPercent() {
            return votesPercent;
        }

        public void setVotesPercent(String votesPercent) {
            this.votesPercent = votesPercent;
        }

    }
}
