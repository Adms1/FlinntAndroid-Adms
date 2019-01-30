package com.edu.flinnt.models.store;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StoreModelResponse {

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

    public static class Course implements Parcelable{

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
        @SerializedName("type")
        @Expose
        private String type;

        protected Course(Parcel in) {
            if (in.readByte() == 0) {
                institutionBookVendorId = null;
            } else {
                institutionBookVendorId = in.readInt();
            }
            if (in.readByte() == 0) {
                institutionId = null;
            } else {
                institutionId = in.readInt();
            }
            if (in.readByte() == 0) {
                bookId = null;
            } else {
                bookId = in.readInt();
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
            if (in.readByte() == 0) {
                publisherId = null;
            } else {
                publisherId = in.readInt();
            }
            if (in.readByte() == 0) {
                languageId = null;
            } else {
                languageId = in.readInt();
            }
            if (in.readByte() == 0) {
                subjectId = null;
            } else {
                subjectId = in.readInt();
            }
            bookName = in.readString();
            isbn = in.readString();
            series = in.readString();
            format = in.readString();
            bookGuid = in.readString();
            hsCode = in.readString();
            if (in.readByte() == 0) {
                isAcademic = null;
            } else {
                isAcademic = in.readInt();
            }
            if (in.readByte() == 0) {
                bookImageId = null;
            } else {
                bookImageId = in.readInt();
            }
            bookImageName = in.readString();
            bookImagePath = in.readString();
            if (in.readByte() == 0) {
                bookImageOrder = null;
            } else {
                bookImageOrder = in.readInt();
            }
            if (in.readByte() == 0) {
                isPrimary = null;
            } else {
                isPrimary = in.readInt();
            }
            if (in.readByte() == 0) {
                bookStandardId = null;
            } else {
                bookStandardId = in.readInt();
            }
            if (in.readByte() == 0) {
                standardId = null;
            } else {
                standardId = in.readInt();
            }
            standardName = in.readString();
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
            if (institutionBookVendorId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionBookVendorId);
            }
            if (institutionId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(institutionId);
            }
            if (bookId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookId);
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
            if (publisherId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(publisherId);
            }
            if (languageId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(languageId);
            }
            if (subjectId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(subjectId);
            }
            parcel.writeString(bookName);
            parcel.writeString(isbn);
            parcel.writeString(series);
            parcel.writeString(format);
            parcel.writeString(bookGuid);
            parcel.writeString(hsCode);
            if (isAcademic == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isAcademic);
            }
            if (bookImageId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookImageId);
            }
            parcel.writeString(bookImageName);
            parcel.writeString(bookImagePath);
            if (bookImageOrder == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookImageOrder);
            }
            if (isPrimary == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isPrimary);
            }
            if (bookStandardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookStandardId);
            }
            if (standardId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(standardId);
            }
            parcel.writeString(standardName);
            parcel.writeString(thumbnailPath);
            parcel.writeString(originalPath);
            parcel.writeString(type);
        }


    }


    public static class Datum implements Parcelable {

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
