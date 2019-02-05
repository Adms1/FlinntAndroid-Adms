package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.store.BrowseCoursesNew;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.WishCourses;
import com.edu.flinnt.core.WishList;
import com.edu.flinnt.gui.WishListAdapter.OnItemLongClickListener;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.WishResponse;
import com.edu.flinnt.protocol.WishableCourses;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.HashMap;

public class WishlistActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyTextView;
    private Button mEmptyButton;
    private ProgressDialog mProgressDialog = null;
    private ImageLoader mImageLoader;

    private WishListAdapter mWishListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<WishableCourses> mWishList = new ArrayList<>();
    private WishCourses wishCourses;
    public Handler mHandler = null;
    private WishCourses mWishCourses;
    public static String coursePictureURLstatic = "https://flinnt1.s3.amazonaws.com/courses/";
    public static final int WISHLIST_CALLBACK = 88;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_wishlist);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.wishlist_title));

        mImageLoader = Requester.getInstance().getImageLoader();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        try {
                            //Helper.showToast("Success", Toast.LENGTH_SHORT);
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof WishResponse) {
                                updateCourseList((WishResponse) message.obj);
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }

        };

        if (null == mWishCourses) {
            mWishCourses = new WishCourses(mHandler);
            mWishCourses.sendWishlistCoursesRequest();
            startProgressDialog();
        }
        initView();
    }

    /**
     * Update and display wish list
     *
     * @param mWishResponse Wish list response
     */
    public void updateCourseList(WishResponse mWishResponse) {
        try {
            coursePictureURLstatic = mWishResponse.getPictureUrl();

            mWishListAdapter.addItems(mWishResponse.getWishList());

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mWishListAdapter.getItemCount());

            mEmptyTextView.setVisibility((mWishListAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
            mEmptyButton.setVisibility((mWishListAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mEmptyTextView = (TextView) findViewById(R.id.empty_text_wishlist);
        mEmptyButton = (Button) findViewById(R.id.empty_browse_courses_btn);
        mEmptyButton.setOnClickListener(this);
        mEmptyButton.setLongClickable(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.wishlist_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(WishlistActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });

        refreshView();
    }

    private void refreshView() {
        try {
            mLayoutManager = new GridLayoutManager(WishlistActivity.this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            LogWriter.write("size of list  : " + mWishList.size());
            mWishListAdapter = new WishListAdapter(WishlistActivity.this,mWishList);

            mWishListAdapter.setOnItemClickListener(new WishListAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    final WishableCourses courseItem = mWishListAdapter.getItem(position);

                    if (Helper.isConnected()) {
                        Intent courseDescriptionIntent = new Intent(WishlistActivity.this, BrowseCourseDetailActivity.class);
                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseItem.getId());
                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getPicture());
                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, courseItem.getInstituteName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, courseItem.getRatings());
                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, courseItem.getUserCount());
                        startActivityForResult(courseDescriptionIntent,WISHLIST_CALLBACK);
                    } else {
                        Helper.showNetworkAlertMessage(WishlistActivity.this);
                    }
                }
            });

            mWishListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    final WishableCourses courseItem = mWishListAdapter.getItem(position);
                    // Remove from MyWishlist
                    if (null != mWishListAdapter.getItem(position)) {
                        showDeleteCourseWarning(WishlistActivity.this, courseItem.getId(), mWishListAdapter, mWishListAdapter.getItem(position));
                    }
                }
            });

            //mWishListAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mWishListAdapter);

            mEmptyTextView.setVisibility(View.GONE);
            mEmptyButton.setVisibility(View.GONE);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Clears the data and make new request
     */
    private void refreshList() {

        Requester.getInstance().cancelPendingRequests(BrowseCoursesNew.TAG);
        mWishListAdapter.clearData();

        refreshView();

        wishCourses = new WishCourses(mHandler);
        wishCourses.sendWishlistCoursesRequest();
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(WishlistActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(WishlistActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Unfavorite Course confirmation dialog
     *
     * @param context         activity context
     * @param courseID        course id
     * @param mAdapter        wish mContentsAdapter
     * @param wishableCourses course to be remove from wishlist
     */
    public void showDeleteCourseWarning(final Context context, final String courseID, final WishListAdapter mAdapter, final WishableCourses wishableCourses) {

        final Handler dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        mAdapter.remove(courseID);
                        // Delete Item from offline database
                        Helper.showToast(context.getString(R.string.successfully_deleted), Toast.LENGTH_SHORT);
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((WishResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(WishlistActivity.this, "Error", ((WishResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText("Delete Wish");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    new WishList(dialogHandler, courseID, 1).sendWishListRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(WishlistActivity.this);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(WishlistActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);
        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case WISHLIST_CALLBACK:
                        HashMap<Course, Boolean> addedAndRemovedCourses = (HashMap<Course, Boolean>) data.getSerializableExtra(JoinCourseResponse.JOINED_KEY);
                        if(addedAndRemovedCourses.size() > 0){
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("ComeFrom", "WishlistActivityResult");
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }

                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogWriter.write("Request code Exception : " + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity="+Flinnt.MY_WISHLIST+"&user="+Config.getStringValue(Config.USER_ID));
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
    public void onClick(View v) {
        if (v == mEmptyButton) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ComeFrom", "WishlistActivity");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}