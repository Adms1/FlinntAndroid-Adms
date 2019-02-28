package com.edu.flinnt.customviews.store.expandableRecylerview.model;

import java.util.List;

public class GroupDataModel<T>  {

    private List<T> childData;
    private String title;
    private int childViewType;


    public List<T> getList() {
        return childData;
    }

    public void setList(List<T> childData) {
        this.childData = childData;
    }

    public int getChildViewType() {
        return childViewType;
    }

    public void setChildViewType(int childViewType) {
        this.childViewType = childViewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
