package com.edu.flinnt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.webkit.WebView;

public class TouchyWebView extends WebView {

    public TouchyWebView(Context context) {
        super(context);
    }

    public TouchyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TouchyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }

    private void initView(Context context){
        // i am not sure with these inflater lines
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // you should not use a new instance of MyWebView here
        // MyWebView view = (MyWebView) inflater.inflate(R.layout.custom_webview, this);
        this.getSettings().setJavaScriptEnabled(true) ;
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setDomStorageEnabled(true);

    }
}