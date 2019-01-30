package com.edu.flinnt.models.store;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("ParcelCreator")
public class StoreBookDetailResponse implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (status == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(status);
        }
        if (page == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(page);
        }
        parcel.writeString(message);
        parcel.writeParcelable(data, i);
    }


    public class Attribute {
        @SerializedName("Publisher Name")
        @Expose
        private String publisherName;
        @SerializedName("Language Name")
        @Expose
        private String languageName;
        @SerializedName("HS Code")
        @Expose
        private String hsCode;
        @SerializedName("ISBN")
        @Expose
        private String isbn;
        @SerializedName("Series")
        @Expose
        private String series;
        @SerializedName("Format")
        @Expose
        private String format;
        @SerializedName("ASIN")
        @Expose
        private String aSIN;

        public String getPublisherName() {
            return publisherName;
        }

        public void setPublisherName(String publisherName) {
            this.publisherName = publisherName;
        }

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }

        public String getHsCode() {
            return hsCode;
        }

        public void setHsCode(String hsCode) {
            this.hsCode = hsCode;
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

        public String getASIN() {
            return aSIN;
        }

        public void setASIN(String aSIN) {
            this.aSIN = aSIN;
        }

    }


    public class Author {

        @SerializedName("book_author_id")
        @Expose
        private Integer bookAuthorId;
        @SerializedName("book_id")
        @Expose
        private Integer bookId;
        @SerializedName("author_id")
        @Expose
        private Integer authorId;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("author_name")
        @Expose
        private String authorName;
        @SerializedName("about_author")
        @Expose
        private Object aboutAuthor;

        public Integer getBookAuthorId() {
            return bookAuthorId;
        }

        public void setBookAuthorId(Integer bookAuthorId) {
            this.bookAuthorId = bookAuthorId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }

        public Integer getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Integer authorId) {
            this.authorId = authorId;
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

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public Object getAboutAuthor() {
            return aboutAuthor;
        }

        public void setAboutAuthor(Object aboutAuthor) {
            this.aboutAuthor = aboutAuthor;
        }

    }


    @SuppressLint("ParcelCreator")
    public static class Data implements Parcelable {

        @SerializedName("book_id")
        @Expose
        private Integer bookId;
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
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
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
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("language_name")
        @Expose
        private String languageName;
        @SerializedName("publisher_name")
        @Expose
        private String publisherName;
        @SerializedName("description")
        @Expose
        private Object description;
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
        @SerializedName("authors")
        @Expose
        private List<Author> authors = null;
        @SerializedName("descriptions")
        @Expose
        private List<Description> descriptions = null;

        @SerializedName("attribute")
        @Expose
        private Attribute attribute;

        @SerializedName("images")
        @Expose
        private ArrayList<Image> images = null;

//        @SerializedName("cart_total")
//        @Expose
//        private int cart_total;


        protected Data(Parcel in) {
            if (in.readByte() == 0) {
                bookId = null;
            } else {
                bookId = in.readInt();
            }
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
                isActive = null;
            } else {
                isActive = in.readInt();
            }
            if (in.readByte() == 0) {
                isAcademic = null;
            } else {
                isAcademic = in.readInt();
            }
            createdAt = in.readString();
            updatedAt = in.readString();
            languageName = in.readString();
            publisherName = in.readString();
            vendorName = in.readString();
            email = in.readString();
            listPrice = in.readString();
            salePrice = in.readString();
            images = in.readArrayList(Image.class.getClassLoader());
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
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

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
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

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public void setPublisherName(String publisherName) {
            this.publisherName = publisherName;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
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

        public List<Author> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
        }

        public List<Description> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(List<Description> descriptions) {
            this.descriptions = descriptions;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(ArrayList<Image> images) {
            this.images = images;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (bookId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(bookId);
            }
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
            if(images != null){
                parcel.writeList(images);
            }

            parcel.writeString(bookName);
            parcel.writeString(isbn);
            parcel.writeString(series);
            parcel.writeString(format);
            parcel.writeString(bookGuid);
            parcel.writeString(hsCode);
            if (isActive == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isActive);
            }
            if (isAcademic == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isAcademic);
            }
            parcel.writeString(createdAt);
            parcel.writeString(updatedAt);
            parcel.writeString(languageName);
            parcel.writeString(publisherName);
            parcel.writeString(vendorName);
            parcel.writeString(email);
            parcel.writeString(listPrice);
            parcel.writeString(salePrice);
        }

//        public int getCart_total() {
//            return cart_total;
//        }
//
//        public void setCart_total(int cart_total) {
//            this.cart_total = cart_total;
//        }
    }


    public class Description {

        @SerializedName("book_description_id")
        @Expose
        private Integer bookDescriptionId;
        @SerializedName("book_id")
        @Expose
        private Integer bookId;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("description_order")
        @Expose
        private Integer descriptionOrder;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getBookDescriptionId() {
            return bookDescriptionId;
        }

        public void setBookDescriptionId(Integer bookDescriptionId) {
            this.bookDescriptionId = bookDescriptionId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getDescriptionOrder() {
            return descriptionOrder;
        }

        public void setDescriptionOrder(Integer descriptionOrder) {
            this.descriptionOrder = descriptionOrder;
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

    }

    @SuppressLint("ParcelCreator")
    public static class Image implements Parcelable{

        @SerializedName("book_image_id")
        @Expose
        private Integer bookImageId;
        @SerializedName("book_id")
        @Expose
        private Integer bookId;
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

        public Image(Parcel in) {


        }

        public Integer getBookImageId() {
            return bookImageId;
        }

        public void setBookImageId(Integer bookImageId) {
            this.bookImageId = bookImageId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
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



        public static final Creator<Image> CREATOR = new Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    }

}
