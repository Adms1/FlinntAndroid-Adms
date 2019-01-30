package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class CouponListResponse extends BaseResponse {

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

        @SerializedName("coupons")
        @Expose
        private ArrayList<Coupon> coupons = new ArrayList<Coupon>();

        /**
         * @return The coupons
         */
        public ArrayList<Coupon> getCoupons() {
            return coupons;
        }

        /**
         * @param coupons The coupons
         */
        public void setCoupons(ArrayList<Coupon> coupons) {
            this.coupons = coupons;
        }

    }

    public class Coupon {

        @SerializedName("coupon_id")
        @Expose
        private String couponId;
        @SerializedName("coupon_code")
        @Expose
        private String couponCode;
        @SerializedName("discount_amount")
        @Expose
        private String discountAmount;
        @SerializedName("calculate_as")
        @Expose
        private String calculateAs;

        /**
         * @return The couponId
         */
        public String getCouponId() {
            return couponId;
        }

        /**
         * @param couponId The coupon_id
         */
        public void setCouponId(String couponId) {
            this.couponId = couponId;
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
         * @return The calculateAs
         */
        public String getCalculateAs() {
            return calculateAs;
        }

        /**
         * @param calculateAs The calculate_as
         */
        public void setCalculateAs(String calculateAs) {
            this.calculateAs = calculateAs;
        }

    }
}