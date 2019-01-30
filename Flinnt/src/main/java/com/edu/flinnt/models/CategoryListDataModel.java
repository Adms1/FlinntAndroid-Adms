package com.edu.flinnt.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mitesh on 10-Feb-18.
 */

public class CategoryListDataModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("categories")
        @Expose
        private List<CategoryModel> categories = null;

        public List<CategoryModel> getCategories() {
            return categories;
        }

        public void setCategories(List<CategoryModel> categories) {
            this.categories = categories;
        }

    }

}
