package com.edu.flinnt.models.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StoreBookSetDetailModel {

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
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Booklist {

        @SerializedName("book_set_id")
        @Expose
        private Integer bookSetId;
        @SerializedName("institution_id")
        @Expose
        private Integer institutionId;
        @SerializedName("book_set_name")
        @Expose
        private String bookSetName;
        @SerializedName("book_set_guid")
        @Expose
        private String bookSetGuid;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("institution_board_standard_bookset_id")
        @Expose
        private Integer institutionBoardStandardBooksetId;
        @SerializedName("institution_board_standard_id")
        @Expose
        private Integer institutionBoardStandardId;
        @SerializedName("board_id")
        @Expose
        private Integer boardId;
        @SerializedName("standard_id")
        @Expose
        private Integer standardId;
        @SerializedName("book_set_book_id")
        @Expose
        private Integer bookSetBookId;
        @SerializedName("book_id")
        @Expose
        private String bookId;
        @SerializedName("subject_id")
        @Expose
        private Integer subjectId;
        @SerializedName("vendor_id")
        @Expose
        private String vendorId;
        @SerializedName("publisher_id")
        @Expose
        private String publisherId;
        @SerializedName("covertype_id")
        @Expose
        private String covertypeId;
        @SerializedName("language_id")
        @Expose
        private String languageId;
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
        private String isAcademic;
        @SerializedName("book_width")
        @Expose
        private String bookWidth;
        @SerializedName("book_length")
        @Expose
        private String bookLength;
        @SerializedName("book_height")
        @Expose
        private String bookHeight;
        @SerializedName("board_name")
        @Expose
        private String boardName;
        @SerializedName("standard_name")
        @Expose
        private String standardName;
        @SerializedName("publisher_name")
        @Expose
        private String publisherName;
        @SerializedName("description")
        @Expose
        private String description;
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
        private String vendorStateId;
        @SerializedName("vendor_country_id")
        @Expose
        private String vendorCountryId;
        @SerializedName("vendor_status_id")
        @Expose
        private String vendorStatusId;
        @SerializedName("vendor_pin")
        @Expose
        private String vendorPin;
        @SerializedName("vendor_gst_number")
        @Expose
        private String vendorGstNumber;
        @SerializedName("flint_charge")
        @Expose
        private String flintCharge;
        @SerializedName("vendor_phone")
        @Expose
        private String vendorPhone;
        @SerializedName("email_verified_at")
        @Expose
        private String emailVerifiedAt;
        @SerializedName("subject_name")
        @Expose
        private String subjectName;
        @SerializedName("book_image_path")
        @Expose
        private String bookImagePath;
        @SerializedName("sale_price")
        @Expose
        private Integer salePrice;

        public Integer getBookSetId() {
            return bookSetId;
        }

        public void setBookSetId(Integer bookSetId) {
            this.bookSetId = bookSetId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public String getBookSetName() {
            return bookSetName;
        }

        public void setBookSetName(String bookSetName) {
            this.bookSetName = bookSetName;
        }

        public String getBookSetGuid() {
            return bookSetGuid;
        }

        public void setBookSetGuid(String bookSetGuid) {
            this.bookSetGuid = bookSetGuid;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
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

        public Integer getInstitutionBoardStandardBooksetId() {
            return institutionBoardStandardBooksetId;
        }

        public void setInstitutionBoardStandardBooksetId(Integer institutionBoardStandardBooksetId) {
            this.institutionBoardStandardBooksetId = institutionBoardStandardBooksetId;
        }

        public Integer getInstitutionBoardStandardId() {
            return institutionBoardStandardId;
        }

        public void setInstitutionBoardStandardId(Integer institutionBoardStandardId) {
            this.institutionBoardStandardId = institutionBoardStandardId;
        }

        public Integer getBoardId() {
            return boardId;
        }

        public void setBoardId(Integer boardId) {
            this.boardId = boardId;
        }

        public Integer getStandardId() {
            return standardId;
        }

        public void setStandardId(Integer standardId) {
            this.standardId = standardId;
        }

        public Integer getBookSetBookId() {
            return bookSetBookId;
        }

        public void setBookSetBookId(Integer bookSetBookId) {
            this.bookSetBookId = bookSetBookId;
        }

        public String getBookId() {
            return bookId;
        }

        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        public Integer getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Integer subjectId) {
            this.subjectId = subjectId;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        public String getPublisherId() {
            return publisherId;
        }

        public void setPublisherId(String publisherId) {
            this.publisherId = publisherId;
        }

        public String getCovertypeId() {
            return covertypeId;
        }

        public void setCovertypeId(String covertypeId) {
            this.covertypeId = covertypeId;
        }

        public String getLanguageId() {
            return languageId;
        }

        public void setLanguageId(String languageId) {
            this.languageId = languageId;
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

        public String getIsAcademic() {
            return isAcademic;
        }

        public void setIsAcademic(String isAcademic) {
            this.isAcademic = isAcademic;
        }

        public String getBookWidth() {
            return bookWidth;
        }

        public void setBookWidth(String bookWidth) {
            this.bookWidth = bookWidth;
        }

        public String getBookLength() {
            return bookLength;
        }

        public void setBookLength(String bookLength) {
            this.bookLength = bookLength;
        }

        public String getBookHeight() {
            return bookHeight;
        }

        public void setBookHeight(String bookHeight) {
            this.bookHeight = bookHeight;
        }

        public String getBoardName() {
            return boardName;
        }

        public void setBoardName(String boardName) {
            this.boardName = boardName;
        }

        public String getStandardName() {
            return standardName;
        }

        public void setStandardName(String standardName) {
            this.standardName = standardName;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public void setPublisherName(String publisherName) {
            this.publisherName = publisherName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getVendorStateId() {
            return vendorStateId;
        }

        public void setVendorStateId(String vendorStateId) {
            this.vendorStateId = vendorStateId;
        }

        public String getVendorCountryId() {
            return vendorCountryId;
        }

        public void setVendorCountryId(String vendorCountryId) {
            this.vendorCountryId = vendorCountryId;
        }

        public String getVendorStatusId() {
            return vendorStatusId;
        }

        public void setVendorStatusId(String vendorStatusId) {
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

        public String getFlintCharge() {
            return flintCharge;
        }

        public void setFlintCharge(String flintCharge) {
            this.flintCharge = flintCharge;
        }

        public String getVendorPhone() {
            return vendorPhone;
        }

        public void setVendorPhone(String vendorPhone) {
            this.vendorPhone = vendorPhone;
        }

        public String getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(String emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getBookImagePath() {
            return bookImagePath;
        }

        public void setBookImagePath(String bookImagePath) {
            this.bookImagePath = bookImagePath;
        }

        public Integer getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(Integer salePrice) {
            this.salePrice = salePrice;
        }

    }


    public class Data {

        @SerializedName("book_set_id")
        @Expose
        private Integer bookSetId;
        @SerializedName("institution_id")
        @Expose
        private Integer institutionId;
        @SerializedName("book_set_name")
        @Expose
        private String bookSetName;
        @SerializedName("book_set_guid")
        @Expose
        private Object bookSetGuid;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
//        @SerializedName("cart_total")
//        @Expose
//        private Integer cartTotal;
        @SerializedName("institution_book_set_vendor_id")
        @Expose
        private Integer institutionBookSetVendorId;
        @SerializedName("vendor_name")
        @Expose
        private String vendorName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("list_price")
        @Expose
        private String listPrice;
        @SerializedName("sale_price")
        @Expose
        private String salePrice;
        @SerializedName("descriptions")
        @Expose
        private List<String> descriptions = null;

        @SerializedName("images")
        @Expose
        private List<Image> images = null;
        @SerializedName("booklist")
        @Expose
        private ArrayList<Booklist> booklist = null;

        public Integer getBookSetId() {
            return bookSetId;
        }

        public void setBookSetId(Integer bookSetId) {
            this.bookSetId = bookSetId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public String getBookSetName() {
            return bookSetName;
        }

        public void setBookSetName(String bookSetName) {
            this.bookSetName = bookSetName;
        }

        public Object getBookSetGuid() {
            return bookSetGuid;
        }

        public void setBookSetGuid(Object bookSetGuid) {
            this.bookSetGuid = bookSetGuid;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
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

//        public Integer getCartTotal() {
//            return cartTotal;
//        }
//
//        public void setCartTotal(Integer cartTotal) {
//            this.cartTotal = cartTotal;
//        }

        public Integer getInstitutionBookSetVendorId() {
            return institutionBookSetVendorId;
        }

        public void setInstitutionBookSetVendorId(Integer institutionBookSetVendorId) {
            this.institutionBookSetVendorId = institutionBookSetVendorId;
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

        public List<String> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(List<String> descriptions) {
            this.descriptions = descriptions;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public ArrayList<Booklist> getBooklist() {
            return booklist;
        }

        public void setBooklist(ArrayList<Booklist> booklist) {
            this.booklist = booklist;
        }

    }






    public class Image {

        @SerializedName("book_set_image_id")
        @Expose
        private Integer bookSetImageId;
        @SerializedName("book_set_id")
        @Expose
        private Integer bookSetId;
        @SerializedName("book_set_image_name")
        @Expose
        private String bookSetImageName;
        @SerializedName("book_set_image_path")
        @Expose
        private String bookSetImagePath;
        @SerializedName("book_set_image_order")
        @Expose
        private Integer bookSetImageOrder;
        @SerializedName("is_primary")
        @Expose
        private Integer isPrimary;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnailPath;
        @SerializedName("original_path")
        @Expose
        private String originalPath;

        public Integer getBookSetImageId() {
            return bookSetImageId;
        }

        public void setBookSetImageId(Integer bookSetImageId) {
            this.bookSetImageId = bookSetImageId;
        }

        public Integer getBookSetId() {
            return bookSetId;
        }

        public void setBookSetId(Integer bookSetId) {
            this.bookSetId = bookSetId;
        }

        public String getBookSetImageName() {
            return bookSetImageName;
        }

        public void setBookSetImageName(String bookSetImageName) {
            this.bookSetImageName = bookSetImageName;
        }

        public String getBookSetImagePath() {
            return bookSetImagePath;
        }

        public void setBookSetImagePath(String bookSetImagePath) {
            this.bookSetImagePath = bookSetImagePath;
        }

        public Integer getBookSetImageOrder() {
            return bookSetImageOrder;
        }

        public void setBookSetImageOrder(Integer bookSetImageOrder) {
            this.bookSetImageOrder = bookSetImageOrder;
        }

        public Integer getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Integer isPrimary) {
            this.isPrimary = isPrimary;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
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
