package com.edu.flinnt.models.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RelatedVendorsResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("institution_book_vendor_id")
        @Expose
        private Integer institutionBookVendorId;
        @SerializedName("institution_id")
        @Expose
        private Integer institutionId;
        @SerializedName("book_id")
        @Expose
        private Integer bookId;
        @SerializedName("vendor_id")
        @Expose
        private Integer vendorId;
        @SerializedName("condition_id")
        @Expose
        private Integer conditionId;
        @SerializedName("list_price")
        @Expose
        private String listPrice;
        @SerializedName("sale_price")
        @Expose
        private String salePrice;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("is_preffered")
        @Expose
        private Integer isPreffered;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("publisher_id")
        @Expose
        private Integer publisherId;
        @SerializedName("covertype_id")
        @Expose
        private Object covertypeId;
        @SerializedName("language_id")
        @Expose
        private Integer languageId;
        @SerializedName("subject_id")
        @Expose
        private Integer subjectId;
        @SerializedName("book_name")
        @Expose
        private String bookName;
        @SerializedName("isbn")
        @Expose
        private String isbn;
        @SerializedName("series")
        @Expose
        private String series;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("book_guid")
        @Expose
        private String bookGuid;
        @SerializedName("hs_code")
        @Expose
        private String hsCode;
        @SerializedName("is_academic")
        @Expose
        private Integer isAcademic;
        @SerializedName("book_width")
        @Expose
        private Object bookWidth;
        @SerializedName("book_length")
        @Expose
        private Object bookLength;
        @SerializedName("book_height")
        @Expose
        private Object bookHeight;
        @SerializedName("vendor_name")
        @Expose
        private String vendorName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("remember_token")
        @Expose
        private String rememberToken;
        @SerializedName("vendor_address1")
        @Expose
        private String vendorAddress1;
        @SerializedName("vendor_address2")
        @Expose
        private String vendorAddress2;
        @SerializedName("vendor_city")
        @Expose
        private String vendorCity;
        @SerializedName("vendor_state_id")
        @Expose
        private Integer vendorStateId;
        @SerializedName("vendor_country_id")
        @Expose
        private Integer vendorCountryId;
        @SerializedName("vendor_status_id")
        @Expose
        private Integer vendorStatusId;
        @SerializedName("vendor_pin")
        @Expose
        private String vendorPin;
        @SerializedName("vendor_gst_number")
        @Expose
        private String vendorGstNumber;
        @SerializedName("flint_charge")
        @Expose
        private Integer flintCharge;
        @SerializedName("vendor_phone")
        @Expose
        private String vendorPhone;
        @SerializedName("email_verified_at")
        @Expose
        private Object emailVerifiedAt;

        public Integer getInstitutionBookVendorId() {
            return institutionBookVendorId;
        }

        public void setInstitutionBookVendorId(Integer institutionBookVendorId) {
            this.institutionBookVendorId = institutionBookVendorId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }

        public Integer getVendorId() {
            return vendorId;
        }

        public void setVendorId(Integer vendorId) {
            this.vendorId = vendorId;
        }

        public Integer getConditionId() {
            return conditionId;
        }

        public void setConditionId(Integer conditionId) {
            this.conditionId = conditionId;
        }

        public String getListPrice() {
            return listPrice;
        }

        public void setListPrice(String listPrice) {
            this.listPrice = listPrice;
        }

        public String getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(String salePrice) {
            this.salePrice = salePrice;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public Integer getIsPreffered() {
            return isPreffered;
        }

        public void setIsPreffered(Integer isPreffered) {
            this.isPreffered = isPreffered;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getPublisherId() {
            return publisherId;
        }

        public void setPublisherId(Integer publisherId) {
            this.publisherId = publisherId;
        }

        public Object getCovertypeId() {
            return covertypeId;
        }

        public void setCovertypeId(Object covertypeId) {
            this.covertypeId = covertypeId;
        }

        public Integer getLanguageId() {
            return languageId;
        }

        public void setLanguageId(Integer languageId) {
            this.languageId = languageId;
        }

        public Integer getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Integer subjectId) {
            this.subjectId = subjectId;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getSeries() {
            return series;
        }

        public void setSeries(String series) {
            this.series = series;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getBookGuid() {
            return bookGuid;
        }

        public void setBookGuid(String bookGuid) {
            this.bookGuid = bookGuid;
        }

        public String getHsCode() {
            return hsCode;
        }

        public void setHsCode(String hsCode) {
            this.hsCode = hsCode;
        }

        public Integer getIsAcademic() {
            return isAcademic;
        }

        public void setIsAcademic(Integer isAcademic) {
            this.isAcademic = isAcademic;
        }

        public Object getBookWidth() {
            return bookWidth;
        }

        public void setBookWidth(Object bookWidth) {
            this.bookWidth = bookWidth;
        }

        public Object getBookLength() {
            return bookLength;
        }

        public void setBookLength(Object bookLength) {
            this.bookLength = bookLength;
        }

        public Object getBookHeight() {
            return bookHeight;
        }

        public void setBookHeight(Object bookHeight) {
            this.bookHeight = bookHeight;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRememberToken() {
            return rememberToken;
        }

        public void setRememberToken(String rememberToken) {
            this.rememberToken = rememberToken;
        }

        public String getVendorAddress1() {
            return vendorAddress1;
        }

        public void setVendorAddress1(String vendorAddress1) {
            this.vendorAddress1 = vendorAddress1;
        }

        public String getVendorAddress2() {
            return vendorAddress2;
        }

        public void setVendorAddress2(String vendorAddress2) {
            this.vendorAddress2 = vendorAddress2;
        }

        public String getVendorCity() {
            return vendorCity;
        }

        public void setVendorCity(String vendorCity) {
            this.vendorCity = vendorCity;
        }

        public Integer getVendorStateId() {
            return vendorStateId;
        }

        public void setVendorStateId(Integer vendorStateId) {
            this.vendorStateId = vendorStateId;
        }

        public Integer getVendorCountryId() {
            return vendorCountryId;
        }

        public void setVendorCountryId(Integer vendorCountryId) {
            this.vendorCountryId = vendorCountryId;
        }

        public Integer getVendorStatusId() {
            return vendorStatusId;
        }

        public void setVendorStatusId(Integer vendorStatusId) {
            this.vendorStatusId = vendorStatusId;
        }

        public String getVendorPin() {
            return vendorPin;
        }

        public void setVendorPin(String vendorPin) {
            this.vendorPin = vendorPin;
        }

        public String getVendorGstNumber() {
            return vendorGstNumber;
        }

        public void setVendorGstNumber(String vendorGstNumber) {
            this.vendorGstNumber = vendorGstNumber;
        }

        public Integer getFlintCharge() {
            return flintCharge;
        }

        public void setFlintCharge(Integer flintCharge) {
            this.flintCharge = flintCharge;
        }

        public String getVendorPhone() {
            return vendorPhone;
        }

        public void setVendorPhone(String vendorPhone) {
            this.vendorPhone = vendorPhone;
        }

        public Object getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(Object emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

    }



}
