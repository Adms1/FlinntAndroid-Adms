package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetRSAResponse extends BaseResponse {
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

        @SerializedName("transaction_id")
        @Expose
        private Integer transactionId;
        @SerializedName("rsa_key")
        @Expose
        private String rsaKey;

        /**
         * @return The transactionId
         */
        public Integer getTransactionId() {
            return transactionId;
        }

        /**
         * @param transactionId The transaction_id
         */
        public void setTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
        }

        /**
         * @return The rsaKey
         */
        public String getRsaKey() {
            return rsaKey;
        }

        /**
         * @param rsaKey The rsa_key
         */
        public void setRsaKey(String rsaKey) {
            this.rsaKey = rsaKey;
        }
    }
}