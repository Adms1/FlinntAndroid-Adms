package com.edu.flinnt.gui;


import android.app.ProgressDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.BrowseCourseCategoryMoreAdapter;
import com.edu.flinnt.core.BrowseCourseCategory;
import com.edu.flinnt.models.store.StoreBookSetDetailModel;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import static com.edu.flinnt.gui.UsersActivity.mCourseID;
import static com.edu.flinnt.protocol.BrowsableCourse.BUNDLE_LIST_KEY;


/**
 * Created by flinnt-android-2 on 30/5/17.
 * Course Category More open when click on More button from browse course fragment.
 */

public class BrowseCourseCategoryMoreActivity extends AppCompatActivity {

    public static final String TAG = "BrowseCourseCategoryMoreActivity";
    private Toolbar mToolbar;
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private BrowseCourseCategoryMoreAdapter mBrowseCourseCategoryMoreAdapter;
    ArrayList<BrowsableCourse> mCourseList = new ArrayList<>();
    String categoryId = "";
    String categoryName = "";
    BrowseCourseCategory mBrowseCourseCategory;
    ArrayList<StoreModelResponse.Course> bundleList = new ArrayList<>();
    public TextView mEmptyTxt;
    private int type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.activity_browse_course_category_more);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(BrowsableCourse.CATEGORY_ID_KEY)) {
                categoryId = bundle.getString(BrowsableCourse.CATEGORY_ID_KEY);
            }
            if (bundle.containsKey(BrowsableCourse.CATEGORY_NAME_KEY)) {
                categoryName = bundle.getString(BrowsableCourse.CATEGORY_NAME_KEY);
            }

            try {
                if (bundle.containsKey(BrowsableCourse.BUNDLE_LIST_KEY)) {
                    bundleList = bundle.getParcelableArrayList(BUNDLE_LIST_KEY);
                }
             }catch (Exception ex){
                ex.printStackTrace();
             }

             try {
                type = bundle.getInt("type");
             }catch (Exception ex){

             }
        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS: {

                        try {
                            //Helper.showToast("Success", Toast.LENGTH_SHORT);
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof BrowseCoursesResponse) {
                                if(type == 1) {
                                    updateCourseList((StoreModelResponse.Datum) message.obj);
                                }else if(type == 2){
                                    updateCourseList2((StoreBookSetResponse.Datum) message.obj);

                                }
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                    }
                    break;
                    case Flinnt.FAILURE: {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " +message.obj.toString());

                        if (message.obj instanceof BrowseCoursesResponse) {
                            BrowseCoursesResponse response = (BrowseCoursesResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(BrowseCourseCategoryMoreActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }
                    }
                    break;
                    default:
                        super.handleMessage(message);
                        break;
                }
            }
        };

//        if (Helper.isConnected()) {
//            if (null == mBrowseCourseCategory) {
//                mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
//                mBrowseCourseCategory.setOffset(0);
//                mBrowseCourseCategory.setCategoryId(categoryId);
//                mBrowseCourseCategory.setSearchString("");
//                mBrowseCourseCategory.sendBrowseCoursesRequest();
//                startProgressDialog();
//
//            }
//        } else {
//            Helper.showNetworkAlertMessage(this);
//        }

        initView();
    }


    private void initView() {
        mEmptyTxt = (TextView) findViewById(R.id.empty_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.course_recycler);
        mRecyclerView.setHasFixedSize(true);
        refreshView();
    }

    private void refreshView() {
//        try {
//            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(BrowseCourseCategoryMoreActivity.this, 2);
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mBrowseCourseCategoryMoreAdapter = new BrowseCourseCategoryMoreAdapter(this, mCourseList);
//            mRecyclerView.setAdapter(mBrowseCourseCategoryMoreAdapter);
//
//        } catch (Exception e) {
//            LogWriter.err(e);
//        }

        try {
            if(bundleList != null) {
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(BrowseCourseCategoryMoreActivity.this, 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mBrowseCourseCategoryMoreAdapter = new BrowseCourseCategoryMoreAdapter(this, bundleList,type);
                mRecyclerView.setAdapter(mBrowseCourseCategoryMoreAdapter);
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }


    }

    /**
     * Update and display wish list
     */
    public void updateCourseList(StoreModelResponse.Datum mBrowseCoursesResponse) {
        try {
            mBrowseCourseCategoryMoreAdapter.addItems(mBrowseCoursesResponse.getCourses());
            if (mBrowseCourseCategoryMoreAdapter.getItemCount() == 0) {
                mEmptyTxt.setVisibility(View.VISIBLE);
            } else {
                mEmptyTxt.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
    public void updateCourseList2(StoreBookSetResponse.Datum mBrowseCoursesResponse) {
        try {
            mBrowseCourseCategoryMoreAdapter.addItems(mBrowseCoursesResponse.getCourses());
            if (mBrowseCourseCategoryMoreAdapter.getItemCount() == 0) {
                mEmptyTxt.setVisibility(View.VISIBLE);
            } else {
                mEmptyTxt.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.BROWSECOURSE_CATEGORY_MORE + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID+ "&category=" + categoryId);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BrowseCourseCategoryMoreActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BrowseCourseCategoryMoreActivity.this,getString(R.string.login),getString(R.string.please_wait), Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
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

}
