package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * class to parse response to object
 */
public class CheckoutResponse extends BaseResponse implements Serializable {

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


    public class Data implements Serializable {

        @SerializedName("transaction_id")
        @Expose
        private Integer transactionId;
        @SerializedName("gateway_trans_url")
        @Expose
        private String gatewayTransUrl;
        @SerializedName("payload")
        @Expose
        private Payload payload;

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
         * @return The gatewayTransUrl
         */
        public String getGatewayTransUrl() {
            return gatewayTransUrl;
        }

        /**
         * @param gatewayTransUrl The gateway_trans_url
         */
        public void setGatewayTransUrl(String gatewayTransUrl) {
            this.gatewayTransUrl = gatewayTransUrl;
        }

        /**
         * @return The payload
         */
        public Payload getPayload() {
            return payload;
        }

        /**
         * @param payload The payload
         */
        public void setPayload(Payload payload) {
            this.payload = payload;
        }

    }

    public class Payload implements Serializable {

        @SerializedName("merchant_id")
        @Expose
        private String merchantId;
        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("redirect_url")
        @Expose
        private String redirectUrl;
        @SerializedName("cancel_url")
        @Expose
        private String cancelUrl;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("billing_name")
        @Expose
        private String billingName;
        @SerializedName("billing_address")
        @Expose
        private String billingAddress;
        @SerializedName("billing_city")
        @Expose
        private String billingCity;
        @SerializedName("billing_state")
        @Expose
        private String billingState;
        @SerializedName("billing_zip")
        @Expose
        private String billingZip;
        @SerializedName("billing_country")
        @Expose
        private String billingCountry;
        @SerializedName("billing_tel")
        @Expose
        private String billingTel;
        @SerializedName("billing_email")
        @Expose
        private String billingEmail;
        @SerializedName("merchant_param1")
        @Expose
        private String merchantParam1;
        @SerializedName("access_code")
        @Expose
        private String accessCode;

        /**
         * @return The merchantId
         */
        public String getMerchantId() {
            return merchantId;
        }

        /**
         * @param merchantId The merchant_id
         */
        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        /**
         * @return The orderId
         */
        public Integer getOrderId() {
            return orderId;
        }

        /**
         * @param orderId The order_id
         */
        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        /**
         * @return The amount
         */
        public String getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(String amount) {
            this.amount = amount;
        }

        /**
         * @return The currency
         */
        public String getCurrency() {
            return currency;
        }

        /**
         * @param currency The currency
         */
        public void setCurrency(String currency) {
            this.currency = currency;
        }

        /**
         * @return The redirectUrl
         */
        public String getRedirectUrl() {
            return redirectUrl;
        }

        /**
         * @param redirectUrl The redirect_url
         */
        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        /**
         * @return The cancelUrl
         */
        public String getCancelUrl() {
            return cancelUrl;
        }

        /**
         * @param cancelUrl The cancel_url
         */
        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }

        /**
         * @return The language
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @param language The language
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /**
         * @return The billingName
         */
        public String getBillingName() {
            return billingName;
        }

        /**
         * @param billingName The billing_name
         */
        public void setBillingName(String billingName) {
            this.billingName = billingName;
        }

        /**
         * @return The billingAddress
         */
        public String getBillingAddress() {
            return billingAddress;
        }

        /**
         * @param billingAddress The billing_address
         */
        public void setBillingAddress(String billingAddress) {
            this.billingAddress = billingAddress;
        }

        /**
         * @return The billingCity
         */
        public String getBillingCity() {
            return billingCity;
        }

        /**
         * @param billingCity The billing_city
         */
        public void setBillingCity(String billingCity) {
            this.billingCity = billingCity;
        }

        /**
         * @return The billingState
         */
        public String getBillingState() {
            return billingState;
        }

        /**
         * @param billingState The billing_state
         */
        public void setBillingState(String billingState) {
            this.billingState = billingState;
        }

        /**
         * @return The billingZip
         */
        public String getBillingZip() {
            return billingZip;
        }

        /**
         * @param billingZip The billing_zip
         */
        public void setBillingZip(String billingZip) {
            this.billingZip = billingZip;
        }

        /**
         * @return The billingCountry
         */
        public String getBillingCountry() {
            return billingCountry;
        }

        /**
         * @param billingCountry The billing_country
         */
        public void setBillingCountry(String billingCountry) {
            this.billingCountry = billingCountry;
        }

        /**
         * @return The billingTel
         */
        public String getBillingTel() {
            return billingTel;
        }

        /**
         * @param billingTel The billing_tel
         */
        public void setBillingTel(String billingTel) {
            this.billingTel = billingTel;
        }

        /**
         * @return The billingEmail
         */
        public String getBillingEmail() {
            return billingEmail;
        }

        /**
         * @param billingEmail The billing_email
         */
        public void setBillingEmail(String billingEmail) {
            this.billingEmail = billingEmail;
        }

        /**
         * @return The merchantParam1
         */
        public String getMerchantParam1() {
            return merchantParam1;
        }

        /**
         * @param merchantParam1 The merchant_param1
         */
        public void setMerchantParam1(String merchantParam1) {
            this.merchantParam1 = merchantParam1;
        }

        /**
         * @return The accessCode
         */
        public String getAccessCode() {
            return accessCode;
        }

        /**
         * @param accessCode The access_code
         */
        public void setAccessCode(String accessCode) {
            this.accessCode = accessCode;
        }

    }
}