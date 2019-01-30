package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.helper.NotificationHelper;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressLint("NewApi")
/**
 * GUI class to show
 */
public class InAppPreviewActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webviewFaq;
    ProgressBar progressBar;
    private String URLS = "";
    private String TITLE = "";
    private String courseIdKey = "";
    private String coursePictureKey = "";
    private String courseNameKey = "";
    private String allowedRolesKey = "";
    private int nextScrenn = 0;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.faq_activity);
        URLS = getIntent().getStringExtra(Flinnt.KEY_INAPP_URL);
        TITLE = getIntent().getStringExtra(Flinnt.KEY_INAPP_TITLE);
        nextScrenn = getIntent().getIntExtra(Flinnt.NOTIFICATION_SCREENID, 0);

        //@Chirag : 10/08/2018
        //Log.d("inapppres","userId : "+getIntent().getStringExtra(Config.USER_ID));
        if (getIntent().getExtras().containsKey(Config.USER_ID)) {
            userId = getIntent().getStringExtra(Config.USER_ID);
        }

        //@Chirag 07/08/2018
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {

            if (bundle.containsKey(Course.COURSE_ID_KEY))
                courseIdKey = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(Course.COURSE_PICTURE_KEY))
                coursePictureKey = bundle.getString(Course.COURSE_PICTURE_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                courseNameKey = bundle.getString(Course.COURSE_NAME_KEY);
            if (bundle.containsKey(Course.ALLOWED_ROLES_KEY))
                allowedRolesKey = bundle.getString(Course.ALLOWED_ROLES_KEY);

            //Log.d("inapppres","onCreate() bundle");
        }

        //Log.d("inapppres", URLS);
        //Log.d("inapppres", TITLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(TITLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webviewFaq = (WebView) findViewById(R.id.webview_faq);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        webviewFaq.getSettings().setLoadWithOverviewMode(true);
        webviewFaq.getSettings().setUseWideViewPort(true);
        webviewFaq.getSettings().setBuiltInZoomControls(false);
        webviewFaq.getSettings().setDisplayZoomControls(false);

        //@Chirag:31/08/2018 added for remove context menu like copy *****
        webviewFaq.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webviewFaq.setLongClickable(false);
        webviewFaq.setHapticFeedbackEnabled(false);
        //@chirag end *********

        webviewFaq.getSettings().setJavaScriptEnabled(true);
        webviewFaq.loadUrl(URLS);

        webviewFaq.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "FAQs");
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (nextScrenn == 0) {
            //Log.d("inapppres", "onOptionsItemSelected() : true nextScreen : " + nextScrenn);
            onBackPressed();
        } else if (nextScrenn == 2) {
            //.d("inapppres", "onOptionsItemSelected() : true nextScreen : " + nextScrenn);
            onBackPressed();
        } else if (nextScrenn == 1) {
            //Log.d("inapppres", "onOptionsItemSelected() : false nextScreen : " + nextScrenn);
//            startActivity(new Intent(InAppPreviewActivity.this,MyCoursesActivity.class));
//            finish();
            Bundle mBundle = new Bundle();
            mBundle.putInt("isFromNotification", Flinnt.FALSE);
            mBundle.putString("user_id", userId);//@Chirag 10/08/2018
            Intent intent = new Intent(InAppPreviewActivity.this, MyCoursesActivity.class);
            intent.putExtras(mBundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (nextScrenn == 0) {
            //Log.d("inapppres", "onBackPressed() : true nextScreen : " + nextScrenn);
            //onBackPressed();     //@Chirag 06/08/2018  Issue : 7740
            super.onBackPressed(); //@Chirag 06/08/2018  Issue : 7740
        } else if (nextScrenn == 1) {
            // startActivity(new Intent(InAppPreviewActivity.this,MyCoursesActivity.class));
            //Log.d("inapppres", "onBackPressed() : true nextScreen : " + nextScrenn);
            Bundle mBundle = new Bundle();
            mBundle.putInt("isFromNotification", Flinnt.FALSE);
            mBundle.putString("user_id", userId);//@Chirag 10/08/2018
            Intent intent = new Intent(InAppPreviewActivity.this, MyCoursesActivity.class);
            intent.putExtras(mBundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (nextScrenn == 2) { //@Chirag - for go to communication list page contain required fields
            //Log.d("inapppres","onBackPressed() and pass to Course details");
            Intent courseIntent = new Intent(this, CourseDetailsActivity.class);
            courseIntent.putExtra(Config.USER_ID, userId);
            courseIntent.putExtra(Course.COURSE_ID_KEY, courseIdKey);
            courseIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePictureKey);
            courseIntent.putExtra(Course.COURSE_NAME_KEY, courseNameKey);
            courseIntent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRolesKey);
            courseIntent.putExtra(Flinnt.NEXT_SCREENID, nextScrenn);
            //startActivityForResult(courseIntent, MyCoursesActivity.COURSE_UNSUBSCRIBED_CALL_BACK);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(courseIntent);
            finish();
        }
    }
}
