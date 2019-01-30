package com.edu.flinnt.protocol;


import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 30/5/17.
 */

public class CategoryDataModel {
    private String categoryId = "";
    private String categoryTitle = "";
    private ArrayList<BrowsableCourse> allItemsInSection;

    public CategoryDataModel() {

    }

    public CategoryDataModel(String categoryId, String categoryTitle, ArrayList<BrowsableCourse> allItemsInSection) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.allItemsInSection = allItemsInSection;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public ArrayList<BrowsableCourse> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<BrowsableCourse> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}
