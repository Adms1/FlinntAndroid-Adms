package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by flinnt-android-2 on 22/5/17.
 */

public class RefundReasonOptionResponse extends BaseResponse {

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

        @SerializedName("course_name")
        @Expose
        private String courseName;
        @SerializedName("refund_reasons")
        @Expose
        private List<RefundReason> refundReasons = null;

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public List<RefundReason> getRefundReasons() {
            return refundReasons;
        }

        public void setRefundReasons(List<RefundReason> refundReasons) {
            this.refundReasons = refundReasons;
        }

    }

    public class RefundReason {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("text")
        @Expose
        private String text;

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

    }
}
