package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CheckoutCourse;
import com.edu.flinnt.core.ContentsList;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import io.realm.RealmList;

import static com.edu.flinnt.gui.BrowseCourseDetailActivity.CHECKOUT_RESPONSE;


public class BrowseCourseDescriptionActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = BrowseCourseDescriptionActivity.class.getSimpleName();
    private Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private ContentsList mContentsList;
    private ContentsResponse mContentsResponse;
    private ContentsDisplayAdapter mContentsDisplayAdapter;
    private RecyclerView mContentRecycle;
    private String mCourseId = "", isPublic = "";
    private RealmList<Sections> mSectionList = new RealmList<>();
    private WebView mContentDescWebView;
    private TextView mContentTitleTxt, mReadMoreTxt, mBuyTxt, mFPriceTxt, mOPriceTxt;
    private int lineCount;
    private String mCourseName = "";
    private boolean isContentOnly = true;
    private LinearLayout mBottomSheetLinear;
    //@Nikhil removed
    //private ScrollView mDescriptionScroll;
    private BrowsableCourse mBrowsableCourse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.browse_course_description_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String courseDescription = "";
        Bundle bundle = getIntent().getExtras();


        if (null != bundle) {
            if (bundle.containsKey(BrowsableCourse.ID_KEY))
                mCourseId = bundle.getString(BrowsableCourse.ID_KEY);


            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                mCourseName = bundle.getString(Course.COURSE_NAME_KEY);

            if (bundle.containsKey(BrowsableCourse.IS_PUBLIC_KEY))
                isPublic = bundle.getString(BrowsableCourse.IS_PUBLIC_KEY);

            if (bundle.containsKey(BrowsableCourse.DESCRIPTION_KEY))
                courseDescription = bundle.getString(BrowsableCourse.DESCRIPTION_KEY);

            if (bundle.containsKey(BrowsableCourse.NAME_KEY))
                getSupportActionBar().setTitle(bundle.getString(BrowsableCourse.NAME_KEY));

            if (bundle.containsKey(BrowseCourseDetailActivity.CONTENT_ONLY))
                isContentOnly = bundle.getBoolean(BrowseCourseDetailActivity.CONTENT_ONLY);

            if (bundle.containsKey(BrowseCourseDetailActivity.BROWSABLE_COURSE))
                mBrowsableCourse = (BrowsableCourse) bundle.getSerializable(BrowseCourseDetailActivity.BROWSABLE_COURSE);
        }

        Log.d("desctest", "" + isContentOnly);

        mBottomSheetLinear = (LinearLayout) findViewById(R.id.bottom_sheet_linear);
        if (mBrowsableCourse != null) {
            if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
                mBottomSheetLinear.setVisibility(View.VISIBLE);
            } else {
                mBottomSheetLinear.setVisibility(View.GONE);
            }
        }

        if (mBrowsableCourse != null) {
            if (mBrowsableCourse.getCanSubscribe() != Flinnt.TRUE && mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE) {
                mBottomSheetLinear.setVisibility(View.GONE);
            }
        }

        //mDescriptionScroll = (ScrollView) findViewById(R.id.description_scroll);
        mContentRecycle = (RecyclerView) findViewById(R.id.content_recycle);
        mReadMoreTxt = (TextView) findViewById(R.id.read_more_txt);
        mContentTitleTxt = (TextView) findViewById(R.id.content_title_txt);
        mReadMoreTxt.setOnClickListener(this);
        mBuyTxt = (TextView) findViewById(R.id.buy_txt);
        mBuyTxt.setOnClickListener(this);
        mFPriceTxt = (TextView) findViewById(R.id.fPriceTxt);
        mOPriceTxt = (TextView) findViewById(R.id.oPriceTxt);
        mContentRecycle.setNestedScrollingEnabled(false);
        if (mBrowsableCourse != null) {
            if (mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE && mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
                mFPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBuy());
                if (mBrowsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
                    mOPriceTxt.setVisibility(View.VISIBLE);
                    mOPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBase());
                    mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    mOPriceTxt.setVisibility(View.GONE);
                }
            }
        }

        mContentDescWebView = (WebView) findViewById(R.id.content_desc_webview);
        mContentDescWebView.setBackgroundColor(Color.parseColor("#EDF2F5"));

        if (isContentOnly) {
            mContentRecycle.setVisibility(View.VISIBLE);
            mContentTitleTxt.setVisibility(View.VISIBLE);
            mReadMoreTxt.setVisibility(View.GONE);
            mContentDescWebView.setVisibility(View.GONE);
        } else {
//          mContentDescWebView.setMaxLines(4);
            mContentRecycle.setVisibility(View.GONE);
            mContentTitleTxt.setVisibility(View.GONE);
            mReadMoreTxt.setVisibility(View.GONE);
//            mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
//            mContentDescWebView.setText(Html.fromHtml(courseDescription, null, new MyTagHandler()));

            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"contentstyle.css\" />" + courseDescription;


            //@Nikhil
            Log.d("appdesc", htmlData);
//            mContentDescWebView.loadData(courseDescription,mimeType, encoding);
            mContentDescWebView.loadDataWithBaseURL("file:///android_asset/", htmlData, mimeType, encoding, "");
            // mContentDescWebView.loadUrl("javascript:document.body.style.color=\"white\";");
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d("desctest",msg.obj.toString());

                stopProgressDialog();

                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (msg.obj instanceof ContentsResponse) {
                            mContentsResponse = (ContentsResponse) msg.obj;
                            updateContentsList(mContentsResponse);
                        }
                        if (msg.obj instanceof CheckoutResponse) {
                            CheckoutResponse mCheckoutResponse = (CheckoutResponse) msg.obj;
                            if (mCheckoutResponse.getData().getTransactionId() != 0) {
                                BrowsableCourse.Price price = mBrowsableCourse.getPrice();
                                Intent intent = new Intent(BrowseCourseDescriptionActivity.this, CheckoutActivity.class);
                                intent.putExtra(BrowsableCourse.NAME_KEY, mBrowsableCourse.getName());
                                intent.putExtra(CHECKOUT_RESPONSE, mCheckoutResponse);
                                intent.putExtra(BrowsableCourse.PRICE_KEY, price);
                                intent.putExtra(BrowsableCourse.ID_KEY, mCourseId);
                                startActivityForResult(intent, BrowseCourseDetailActivity.CHECKOUT_CALLBACK);
                            }
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
        } else {
//            mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
            mReadMoreTxt.setVisibility(View.GONE);
            mContentTitleTxt.setVisibility(View.GONE);
        }
        if (isContentOnly)
            refreshView();
    }

    private void refreshView() {
        try {
            mContentsDisplayAdapter = new ContentsDisplayAdapter(BrowseCourseDescriptionActivity.this, mCourseId, mCourseName, mSectionList);
            GridLayoutManager manager = new GridLayoutManager(BrowseCourseDescriptionActivity.this, 1);
            mContentRecycle.setLayoutManager(manager);
            mContentsDisplayAdapter.setLayoutManager(manager);
            mContentRecycle.setAdapter(mContentsDisplayAdapter);

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BrowseCourseDescriptionActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BrowseCourseDescriptionActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
                mContentsDisplayAdapter.addItems(mContentsResponse.getData().getList(), mContentsResponse, 0, 0);
                mReadMoreTxt.setVisibility(lineCount > 3 ? View.VISIBLE : View.GONE);
                mContentTitleTxt.setVisibility(View.VISIBLE);
            } else {
//                mContentDescWebView.setMaxLines(Integer.MAX_VALUE);
                mReadMoreTxt.setVisibility(View.GONE);
                mContentTitleTxt.setVisibility(View.GONE);
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

            case R.id.buy_txt:
                startProgressDialog();
                new CheckoutCourse(mHandler, mCourseId).sendCheckoutRequest();
                break;
        }
    }
}
