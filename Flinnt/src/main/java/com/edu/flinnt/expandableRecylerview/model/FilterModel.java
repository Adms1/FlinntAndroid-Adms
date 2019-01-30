package com.edu.flinnt.expandableRecylerview.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.edu.flinnt.expandableRecylerview.ExpandableGroup;

import java.util.List;

@SuppressLint("ParcelCreator")
public class FilterModel extends ExpandableGroup implements Parcelable  {

    private String name;

    public FilterModel(String title, List items) {
        super(title, items);
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterModel)) return false;

        FilterModel artist = (FilterModel) o;
        return getName() != null ? getName().equals(artist.getName()) : artist.getName() == null;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }
}


