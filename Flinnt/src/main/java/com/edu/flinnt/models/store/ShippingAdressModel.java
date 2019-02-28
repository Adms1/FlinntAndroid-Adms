package com.edu.flinnt.models.store;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShippingAdressModel implements Parcelable{

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

    protected ShippingAdressModel(Parcel in) {
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        if (in.readByte() == 0) {
            page = null;
        } else {
            page = in.readInt();
        }
        message = in.readString();
        data = in.createTypedArrayList(Datum.CREATOR);
    }

    public static final Creator<ShippingAdressModel> CREATOR = new Creator<ShippingAdressModel>() {
        @Override
        public ShippingAdressModel createFromParcel(Parcel in) {
            return new ShippingAdressModel(in);
        }

        @Override
        public ShippingAdressModel[] newArray(int size) {
            return new ShippingAdressModel[size];
        }
    };

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
        parcel.writeTypedList(data);
    }

    public static class Datum implements Parcelable{

        @SerializedName("user_address_id")
        @Expose
        private Integer userAddressId;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("fullname")
        @Expose
        private String fullname;
        @SerializedName("address1")
        @Expose
        private String address1;
        @SerializedName("address2")
        @Expose
        private String address2;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state_id")
        @Expose
        private Integer stateId;
        @SerializedName("country_id")
        @Expose
        private Integer countryId;
        @SerializedName("status_id")
        @Expose
        private Object statusId;
        @SerializedName("address_type")
        @Expose
        private String addressType;
        @SerializedName("pin")
        @Expose
        private String pin;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("name")
        @Expose
        private String name;

        private boolean ischecked;

        protected Datum(Parcel in) {
            if (in.readByte() == 0) {
                userAddressId = null;
            } else {
                userAddressId = in.readInt();
            }
            if (in.readByte() == 0) {
                userId = null;
            } else {
                userId = in.readInt();
            }
            fullname = in.readString();
            address1 = in.readString();
            address2 = in.readString();
            city = in.readString();
            if (in.readByte() == 0) {
                stateId = null;
            } else {
                stateId = in.readInt();
            }
            if (in.readByte() == 0) {
                countryId = null;
            } else {
                countryId = in.readInt();
            }
            addressType = in.readString();
            pin = in.readString();
            phone = in.readString();
            if (in.readByte() == 0) {
                isActive = null;
            } else {
                isActive = in.readInt();
            }
            createdAt = in.readString();
            updatedAt = in.readString();
            name = in.readString();
            ischecked = in.readByte() != 0;
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

        public Integer getUserAddressId() {
            return userAddressId;
        }

        public void setUserAddressId(Integer userAddressId) {
            this.userAddressId = userAddressId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Integer getStateId() {
            return stateId;
        }

        public void setStateId(Integer stateId) {
            this.stateId = stateId;
        }

        public Integer getCountryId() {
            return countryId;
        }

        public void setCountryId(Integer countryId) {
            this.countryId = countryId;
        }

        public Object getStatusId() {
            return statusId;
        }

        public void setStatusId(Object statusId) {
            this.statusId = statusId;
        }

        public String getAddressType() {
            return addressType;
        }

        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIschecked() {
            return ischecked;
        }

        public void setIschecked(boolean ischecked) {
            this.ischecked = ischecked;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (userAddressId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(userAddressId);
            }
            if (userId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(userId);
            }
            parcel.writeString(fullname);
            parcel.writeString(address1);
            parcel.writeString(address2);
            parcel.writeString(city);
            if (stateId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(stateId);
            }
            if (countryId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(countryId);
            }
            parcel.writeString(addressType);
            parcel.writeString(pin);
            parcel.writeString(phone);
            if (isActive == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(isActive);
            }
            parcel.writeString(createdAt);
            parcel.writeString(updatedAt);
            parcel.writeString(name);
            parcel.writeByte((byte) (ischecked ? 1 : 0));
        }
    }

}
