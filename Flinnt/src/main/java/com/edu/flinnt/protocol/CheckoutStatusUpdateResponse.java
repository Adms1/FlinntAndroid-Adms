package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by flinnt-android-2 on 15/10/16.
 */
public class CheckoutStatusUpdateResponse extends BaseResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("transaction_id")
        @Expose
        private Integer transactionId;
        @SerializedName("status_code")
        @Expose
        private String statusCode;

        /**
         *
         * @return
         * The transactionId
         */
        public Integer getTransactionId() {
            return transactionId;
        }

        /**
         *
         * @param transactionId
         * The transaction_id
         */
        public void setTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
        }

        /**
         *
         * @return
         * The statusCode
         */
        public String getStatusCode() {
            return statusCode;
        }

        /**
         *
         * @param statusCode
         * The status_code
         */
        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

    }
}
