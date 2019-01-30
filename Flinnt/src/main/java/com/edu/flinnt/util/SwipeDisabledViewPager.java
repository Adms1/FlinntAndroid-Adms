package com.edu.flinnt.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom view pager class that doesn't listen swipe events
 */
public class SwipeDisabledViewPager extends android.support.v4.view.ViewPager{
    private boolean enabled;

    public SwipeDisabledViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}
