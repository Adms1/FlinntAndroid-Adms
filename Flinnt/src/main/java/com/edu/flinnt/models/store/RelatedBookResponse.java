package com.edu.flinnt.models.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RelatedBookResponse {

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
    private ArrayList<Datum> data = null;

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

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
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
        @SerializedName("book_image_id")
        @Expose
        private Integer bookImageId;
        @SerializedName("book_image_name")
        @Expose
        private String bookImageName;
        @SerializedName("book_image_path")
        @Expose
        private String bookImagePath;
        @SerializedName("book_image_order")
        @Expose
        private Integer bookImageOrder;
        @SerializedName("is_primary")
        @Expose
        private Integer isPrimary;
        @SerializedName("book_standard_id")
        @Expose
        private Integer bookStandardId;
        @SerializedName("standard_id")
        @Expose
        private Integer standardId;
        @SerializedName("standard_name")
        @Expose
        private String standardName;
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnailPath;
        @SerializedName("original_path")
        @Expose
        private String originalPath;

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

        public Integer getBookImageId() {
            return bookImageId;
        }

        public void setBookImageId(Integer bookImageId) {
            this.bookImageId = bookImageId;
        }

        public String getBookImageName() {
            return bookImageName;
        }

        public void setBookImageName(String bookImageName) {
            this.bookImageName = bookImageName;
        }

        public String getBookImagePath() {
            return bookImagePath;
        }

        public void setBookImagePath(String bookImagePath) {
            this.bookImagePath = bookImagePath;
        }

        public Integer getBookImageOrder() {
            return bookImageOrder;
        }

        public void setBookImageOrder(Integer bookImageOrder) {
            this.bookImageOrder = bookImageOrder;
        }

        public Integer getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Integer isPrimary) {
            this.isPrimary = isPrimary;
        }

        public Integer getBookStandardId() {
            return bookStandardId;
        }

        public void setBookStandardId(Integer bookStandardId) {
            this.bookStandardId = bookStandardId;
        }

        public Integer getStandardId() {
            return standardId;
        }

        public void setStandardId(Integer standardId) {
            this.standardId = standardId;
        }

        public String getStandardName() {
            return standardName;
        }

        public void setStandardName(String standardName) {
            this.standardName = standardName;
        }

        public String getThumbnailPath() {
            return thumbnailPath;
        }

        public void setThumbnailPath(String thumbnailPath) {
            this.thumbnailPath = thumbnailPath;
        }

        public String getOriginalPath() {
            return originalPath;
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }

    }
}
