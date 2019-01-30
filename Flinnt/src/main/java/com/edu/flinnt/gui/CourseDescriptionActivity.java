package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentsList;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import io.realm.RealmList;


/**
 * GUI class to show course description
 */
public class CourseDescriptionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = CourseDescriptionActivity.class.getSimpleName();

    private Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private ContentsList mContentsList;
    private ContentsResponse mContentsResponse;
    private ContentsDisplayAdapter mContentsDisplayAdapter;
    private String mCourseId = "", isPublic = "";
    private RealmList<Sections> mSectionList = new RealmList<Sections>();

    WebView mContentDescWebView;
    TextView mReadMoreTxt;
    int lineCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.course_description_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String courseDescription = "";
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(BrowsableCourse.ID_KEY))
                mCourseId = bundle.getString(BrowsableCourse.ID_KEY);

            if (bundle.containsKey(BrowsableCourse.IS_PUBLIC_KEY))
                isPublic = bundle.getString(BrowsableCourse.IS_PUBLIC_KEY);

            if (bundle.containsKey(BrowsableCourse.DESCRIPTION_KEY))
                courseDescription = bundle.getString(BrowsableCourse.DESCRIPTION_KEY);

            if (bundle.containsKey(BrowsableCourse.NAME_KEY))
                getSupportActionBar().setTitle(bundle.getString(BrowsableCourse.NAME_KEY));
        }

        mReadMoreTxt = (TextView) findViewById(R.id.read_more_txt);
        mReadMoreTxt.setOnClickListener(this);
        mContentDescWebView = (WebView) findViewById(R.id.content_desc_webview);
        mContentDescWebView.setBackgroundColor(Color.parseColor("#EDF2F5"));

//        mContentDescWebView.setText(Html.fromHtml(courseDescription, null, new MyTagHandler()));

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"contentstyle.css\" />"+courseDescription;
        mContentDescWebView.loadDataWithBaseURL("file:///android_asset/", htmlData, mimeType, encoding, "");

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();

                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (msg.obj instanceof ContentsResponse) {
                            mContentsResponse = (ContentsResponse) msg.obj;
                            updateContentsList(mContentsResponse);
                        }
                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + msg.obj.toString());
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        if (null == mContentsList && isPublic.equals("1")) {
            mContentsList = new ContentsList(mHandler, mCourseId);
            mContentsList.setSearchString("");
            mContentsList.sendContentsListRequest();
            startProgressDialog();
        }else {
//            mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
            mReadMoreTxt.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Browse Course Description");
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

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(CourseDescriptionActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(CourseDescriptionActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        } finally {
            mProgressDialog = null;
        }
    }


    /**
     * Update and display course list
     *
     * @param mContentsResponse cource course list response
     */
    public void updateContentsList(ContentsResponse mContentsResponse) {
        try {
            if (mContentsResponse.getData().getList().size() > 0) {
                LogWriter.write("Current Count: " + mContentsResponse.getData().getList().size());
                mContentsDisplayAdapter.addItems(mContentsResponse.getData().getList(),mContentsResponse,0,0);
                mReadMoreTxt.setVisibility(lineCount > 3 ? View.VISIBLE : View.GONE);
            } else {
//                mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
                mReadMoreTxt.setVisibility(View.GONE);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mContentsDisplayAdapter.getItemCount());

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_more_txt:
                mReadMoreTxt.setVisibility(View.GONE);
//                mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
                break;
        }
    }
}
