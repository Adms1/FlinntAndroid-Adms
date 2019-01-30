package com.edu.flinnt.models.store;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StoreBookSetResponse {

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

    public static class Course implements Parcelable {

        @SerializedName("institution_book_set_vendor_id")
        @Expose
        private Integer institutionBookSetVendorId;
        @SerializedName("institution_id")
        @Expose
        private Integer institutionId;
        @SerializedName("book_set_id")
        @Expose
        private Integer bookSetId;
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
        @SerializedName("book_set_name")
        @Expose
        private String bookSetName;
        @SerializedName("book_set_guid")
        @Expose
        private Object bookSetGuid;
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
        @SerializedName("board_name")
        @Expose
        private String boardName;
        @SerializedName("standard_name")
        @Expose
        private String standardName;
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
        @SerializedName("book_set_image_id")
        @Expose
        private Integer bookSetImageId;
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
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnailPath;
        @SerializedName("original_path")
        @Expose
        private String originalPath;
        @SerializedName("type")
        @Expose
        private String type;

        protected Course(Parcel in) {
            if (in.readByte() == 0) {
                institutionBookSetVendorId = null;
            } else {
                institutionBookSetVendorId = in.readInt();
            }
            if (in.readByte() == 0) {
                institutionId = null;
            } else {
                institutionId = in.readInt();
            }
            if (in.readByte() == 0) {
                bookSetId = null;
            } else {
                bookSetId = in.readInt();
            }
            if (in.readByte() == 0) {
                vendorId = null;
            } else {
                vendorId = in.readInt();
            }
            if (in.readByte() == 0) {
                conditionId = null;
            } else {
                conditionId = in.readInt();
            }
            listPrice = in.readString();
            salePrice = in.readString();
            if (in.readByte() == 0) {
                isActive = null;
            } else {
                isActive = in.readInt();
            }
            if (in.readByte() == 0) {
                isPreffered = null;
            } else {
                isPreffered = in.readInt();
            }
            createdAt = in.readString();
            updatedAt = in.readString();
            bookSetName = in.readString();
            if (in.readByte() == 0) {
                institutionBoardStandardBooksetId = null;
            } else {
                institutionBoardStandardBooksetId = in.readInt();
            }
            if (in.readByte() == 0) {
                institutionBoardStandardId = null;
            } else {
                institutionBoardStandardId = in.readInt();
            }
            if (in.readByte() == 0) {
                boardId = null;
            } else {
                boardId = in.readInt();
            }
            if (in.readByte() == 0) {
                standardId = null;
            } else {
                standardId = in.readInt();
            }
            boardName = in.readString();
            standardName = in.readString();
            vendorName = in.readString();
            email = in.readString();
            password = in.readString();
            rememberToken = in.readString();
            vendorAddress1 = in.readString();
            vendorAddress2 = in.readString();
            vendorCity = in.readString();
            if (in.readByte() == 0) {
                vendorStateId = null;
            } else {
                vendorStateId = in.readInt();
            }
            if (in.readByte() == 0) {
                vendorCountryId = null;
            } else {
                vendorCountryId = in.readInt();
            }
            if (in.readByte() == 0) {
                vendorStatusId = null;
            } else {
                vendorStatusId = in.readInt();
            }
            vendorPin = in.readString();
            vendorGstNumber = in.readString();
            if (in.readByte() == 0) {
                flintCharge = null;
            } else {
                flintCharge = in.readInt();
            }
            vendorPhone = in.readString();
            if (in.readByte() == 0) {
                bookSetImageId = null;
            } else {
                bookSetImageId = in.readInt();
            }
            bookSetImageName = in.readString();
            bookSetImagePath = in.readString();
            if (in.readByte() == 0) {
                bookSetImageOrder = null;
            } else {
                bookSetImageOrder = in.readInt();
            }
            if (in.readByte() == 0) {
                isPrimary = null;
            } else {
                isPrimary = in.readInt();
            }
            thumbnailPath = in.readString();
            originalPath = in.readString();
            type = in.readString();
        }

        public static final Creator<Course> CREATOR = new Creator<Course>() {
            @Override
            public Course createFromParcel(Parcel in) {
                return new Course(in);
            }

            @Override
            public Course[] newArray(int size) {
                return new Course[size];
            }
        };

        public Integer getInstitutionBookSetVendorId() {
            return institutionBookSetVendorId;
        }

        public void setInstitutionBookSetVendorId(Integer institutionBookSetVendorId) {
            this.institutionBookSetVendorId = institutionBookSetVendorId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getBookSetId() {
            return bookSetId;
        }

        public void setBookSetId(Integer bookSetId) {
            this.bookSetId = bookSetId;
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

        public Integer getBookSetImageId() {
            return bookSetImageId;
        }

        public void setBookSetImageId(Integer bookSetImageId) {
            this.bookSetImageId = bookSetImageId;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (institutionBookSetVendorId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionBookSetVendorId);
            }
            if (institutionId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionId);
            }
            if (bookSetId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookSetId);
            }
            if (vendorId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(vendorId);
            }
            if (conditionId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(conditionId);
            }
            parcel.writeString(listPrice);
            parcel.writeString(salePrice);
            if (isActive == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isActive);
            }
            if (isPreffered == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isPreffered);
            }
            parcel.writeString(createdAt);
            parcel.writeString(updatedAt);
            parcel.writeString(bookSetName);
            if (institutionBoardStandardBooksetId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionBoardStandardBooksetId);
            }
            if (institutionBoardStandardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionBoardStandardId);
            }
            if (boardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(boardId);
            }
            if (standardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(standardId);
            }
            parcel.writeString(boardName);
            parcel.writeString(standardName);
            parcel.writeString(vendorName);
            parcel.writeString(email);
            parcel.writeString(password);
            parcel.writeString(rememberToken);
            parcel.writeString(vendorAddress1);
            parcel.writeString(vendorAddress2);
            parcel.writeString(vendorCity);
            if (vendorStateId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(vendorStateId);
            }
            if (vendorCountryId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(vendorCountryId);
            }
            if (vendorStatusId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(vendorStatusId);
            }
            parcel.writeString(vendorPin);
            parcel.writeString(vendorGstNumber);
            if (flintCharge == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(flintCharge);
            }
            parcel.writeString(vendorPhone);
            if (bookSetImageId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookSetImageId);
            }
            parcel.writeString(bookSetImageName);
            parcel.writeString(bookSetImagePath);
            if (bookSetImageOrder == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookSetImageOrder);
            }
            if (isPrimary == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isPrimary);
            }
            parcel.writeString(thumbnailPath);
            parcel.writeString(originalPath);
            parcel.writeString(type);
        }
    }


    public static class Datum implements  Parcelable{

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

        protected Datum(Parcel in) {
            if (in.readByte() == 0) {
                standardId = null;
            } else {
                standardId = in.readInt();
            }
            standardName = in.readString();
            if (in.readByte() == 0) {
                cartTotal = null;
            } else {
                cartTotal = in.readInt();
            }
            courses = in.createTypedArrayList(Course.CREATOR);
        }

        public static final Creator<Datum> CREATOR = new Creator<Datum>() {
            @Override
            public Datum createFromParcel(Parcel in) {
                return new Datum(in);
            }

            @Override
            public Datum[] newArray(int size) {
                return new Datum[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (standardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(standardId);
            }
            parcel.writeString(standardName);
            if (cartTotal == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(cartTotal);
            }
            parcel.writeTypedList(courses);
        }
    }



}
