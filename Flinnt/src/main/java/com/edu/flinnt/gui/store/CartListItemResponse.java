package com.edu.flinnt.gui.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartListItemResponse {

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

    public class CartItem {

        @SerializedName("rowId")
        @Expose
        private String rowId;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("qty")
        @Expose
        private String qty;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("options")
        @Expose
        private Options options;
        @SerializedName("tax")
        @Expose
        private Integer tax;
        @SerializedName("subtotal")
        @Expose
        private Integer subtotal;
        @SerializedName("vendor_name")
        @Expose
        private String vendorName;
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnailPath;
        @SerializedName("type")
        @Expose
        private String type;

        public String getRowId() {
            return rowId;
        }

        public void setRowId(String rowId) {
            this.rowId = rowId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Options getOptions() {
            return options;
        }

        public void setOptions(Options options) {
            this.options = options;
        }

        public Integer getTax() {
            return tax;
        }

        public void setTax(Integer tax) {
            this.tax = tax;
        }

        public Integer getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Integer subtotal) {
            this.subtotal = subtotal;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getThumbnailPath() {
            return thumbnailPath;
        }

        public void setThumbnailPath(String thumbnailPath) {
            this.thumbnailPath = thumbnailPath;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


    public class Data {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("cart_item")
        @Expose
        private List<CartItem> cartItem = null;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<CartItem> getCartItem() {
            return cartItem;
        }

        public void setCartItem(List<CartItem> cartItem) {
            this.cartItem = cartItem;
        }

    }






    public class Options {

        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("type")
        @Expose
        private String type;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

}
