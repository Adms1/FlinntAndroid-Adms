package com.edu.flinnt.models.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterDataResponse {

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

    public class Course {

        @SerializedName("institution_book_vendor_id")
        @Expose
        private Integer institutionBookVendorId;
        @SerializedName("book_id")
        @Expose
        private Integer bookId;
        @SerializedName("vendor_id")
        @Expose
        private Integer vendorId;
        @SerializedName("list_price")
        @Expose
        private String listPrice;
        @SerializedName("sale_price")
        @Expose
        private String salePrice;
        @SerializedName("language_id")
        @Expose
        private Integer languageId;
        @SerializedName("subject_id")
        @Expose
        private Integer subjectId;
        @SerializedName("book_name")
        @Expose
        private String bookName;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("book_standard_id")
        @Expose
        private Integer bookStandardId;
        @SerializedName("standard_id")
        @Expose
        private Integer standardId;
        @SerializedName("standard_name")
        @Expose
        private String standardName;
        @SerializedName("category_tree_id")
        @Expose
        private Integer categoryTreeId;
        @SerializedName("book_image_path")
        @Expose
        private String bookImagePath;
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnailPath;
        @SerializedName("original_path")
        @Expose
        private String originalPath;
        @SerializedName("type")
        @Expose
        private String type;

        public Integer getInstitutionBookVendorId() {
            return institutionBookVendorId;
        }

        public void setInstitutionBookVendorId(Integer institutionBookVendorId) {
            this.institutionBookVendorId = institutionBookVendorId;
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

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
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

        public Integer getCategoryTreeId() {
            return categoryTreeId;
        }

        public void setCategoryTreeId(Integer categoryTreeId) {
            this.categoryTreeId = categoryTreeId;
        }

        public String getBookImagePath() {
            return bookImagePath;
        }

        public void setBookImagePath(String bookImagePath) {
            this.bookImagePath = bookImagePath;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


    public class Datum {

        @SerializedName("standard_id")
        @Expose
        private Integer standardId;
        @SerializedName("standard_name")
        @Expose
        private String standardName;
        @SerializedName("cart_total")
        @Expose
        private Integer cartTotal;
        @SerializedName("courses")
        @Expose
        private List<Course> courses = null;

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

        public Integer getCartTotal() {
            return cartTotal;
        }

        public void setCartTotal(Integer cartTotal) {
            this.cartTotal = cartTotal;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

    }
}
