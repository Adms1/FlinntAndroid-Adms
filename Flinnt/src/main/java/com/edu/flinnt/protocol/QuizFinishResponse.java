package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public class QuizFinishResponse extends BaseResponse {

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

        @SerializedName("finished")
        @Expose
        private Integer finished;

        public Integer getFinished() {
            return finished;
        }

        public void setFinished(Integer status) {
            this.finished = status;
        }

    }
}
