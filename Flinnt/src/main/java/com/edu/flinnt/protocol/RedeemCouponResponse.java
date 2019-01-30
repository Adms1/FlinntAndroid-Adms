package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RedeemCouponResponse extends BaseResponse {

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

        @SerializedName("discount_amount")
        @Expose
        private String discountAmount;
        @SerializedName("net_amount")
        @Expose
        private String netAmount;
        @SerializedName("transaction_id")
        @Expose
        private Integer transactionId;
        @SerializedName("coupon_code")
        @Expose
        private String couponCode;

        /**
         * @return The discountAmount
         */
        public String getDiscountAmount() {
            return discountAmount;
        }

        /**
         * @param discountAmount The discount_amount
         */
        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
        }

        /**
         * @return The netAmount
         */
        public String getNetAmount() {
            return netAmount;
        }

        /**
         * @param netAmount The net_amount
         */
        public void setNetAmount(String netAmount) {
            this.netAmount = netAmount;
        }

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
         * @return The couponCode
         */
        public String getCouponCode() {
            return couponCode;
        }

        /**
         * @param couponCode The coupon_code
         */
        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }
    }
}