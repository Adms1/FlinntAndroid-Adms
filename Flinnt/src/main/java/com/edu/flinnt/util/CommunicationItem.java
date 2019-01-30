package com.edu.flinnt.util;

/**
 * Created by flinnt-android-2 on 13/1/17.
 */

public class CommunicationItem {
    private int mDrawableRes;

    private String mTitle;

    public CommunicationItem(int drawable, String title) {
        mDrawableRes = drawable;
        mTitle = title;
    }

    public int getDrawableResource() {
        return mDrawableRes;
    }

    public String getTitle() {
        return mTitle;
    }
}
