package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.JoinCommunity;
import com.edu.flinnt.core.MyCourses;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.MyCoursesRequest;
import com.edu.flinnt.protocol.MyCoursesResponse;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * GUI class to browse courses by communities
 */
public class BrowseCoursesActivity extends AppCompatActivity {

    private final String TRUE = "1";
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    TextView mEmptyView;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    ArrayList<Course> mCourseList;

    MyCourses mMyCourses = null;

    BrowseCoursesAdapter mBrowseCoursesAdapter;
    String coursePicUrl = "";
    private int positionToRemove = Flinnt.INVALID;

    SwipeRefreshLayout swipeRefreshLayout;

    private String actionInvitationID = "";
    public static String coursePictureURLstatic = "https://flinnt1.s3.amazonaws.com/courses/";
    private LinearLayoutManager mLayoutManager;

    private String queryTextChange = "";

    ArrayList<Course> joinedCourseList = new ArrayList<>();

    EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    MyCoursesRequest myCoursesRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.browse_course_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mEmptyView = (TextView) findViewById(R.id.empty_text_browse_courses);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_browse_courses);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mCourseList = new ArrayList<Course>();

        refreshView();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof MyCoursesResponse) {
                            updateCourseList((MyCoursesResponse) message.obj);
                        }
                        if (message.obj instanceof JoinCourseResponse) {
                            removeJoinedCourse((JoinCourseResponse) message.obj);
                        }

                        break;
                    case Flinnt.FAILURE:

                        if (message.obj instanceof MyCoursesResponse) {
                            if (null != ((MyCoursesResponse) message.obj).errorResponse) {
                                Helper.showAlertMessage(BrowseCoursesActivity.this, "Error", ((MyCoursesResponse) message.obj).errorResponse.getMessage(), "CLOSE");

                            }
                        }
                        if (message.obj instanceof JoinCourseResponse) {
                            removeJoinedCourse((JoinCourseResponse) message.obj);
                            if (null != ((JoinCourseResponse) message.obj).errorResponse) {
                                Helper.showAlertMessage(BrowseCoursesActivity.this, "Error", ((JoinCourseResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                            }
                        }
                        mEmptyView.setVisibility((mBrowseCoursesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                        mEmptyView.setText("No Course Found");

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mMyCourses : " + mMyCourses);
        if (null == mMyCourses) {
            mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_BROWSE_COURSE);
            mMyCourses.setSearchString("");
            mMyCourses.sendMyCoursesRequest();
            startProgressDialog();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(BrowseCoursesActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });


    }

    private void refreshView() {

        mRecyclerView.setLayoutManager(mLayoutManager);
        mBrowseCoursesAdapter = new BrowseCoursesAdapter(mCourseList);

        mRecyclerView.setAdapter(mBrowseCoursesAdapter);

        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager, false) {

            @Override
            public synchronized void onLoadMore(int current_page) {

                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("hasMore :: " + mMyCourses.getLastMyCoursesResponse().getHasMore());

                if( mMyCourses.getLastMyCoursesResponse().getHasMore() > 0 ) {
                    mMyCourses.sendMyCoursesRequest();
                }
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);

        mBrowseCoursesAdapter.setOnItemClickListener(new BrowseCoursesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final Course courseItem = mBrowseCoursesAdapter.getItem(position);

                if (Helper.isConnected()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseCoursesActivity.this);
                    // set mResorceTitleTxt
                    //alertDialogBuilder.setTitle("Delete Message");
                    TextView titleText = new TextView(BrowseCoursesActivity.this);
                    // You Can Customise your Title here
                    titleText.setText("Join Course");
                    titleText.setPadding(40, 40, 40, 0);
                    titleText.setGravity(Gravity.CENTER_VERTICAL);
                    titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    titleText.setTextSize(20);
                    titleText.setTypeface(Typeface.DEFAULT_BOLD);
                    alertDialogBuilder.setCustomTitle(titleText);
                    // set dialog message
                    alertDialogBuilder.setMessage("Are you sure you want to join '" + courseItem.getCourseName() + "' course?");
                    alertDialogBuilder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Helper.isConnected()) {
                                positionToRemove = position;
                                new JoinCommunity(mHandler, courseItem.getCourseID(), false).sendJoinCommunityRequest();
                                startProgressDialog();
                            } else {
                                Helper.showNetworkAlertMessage(BrowseCoursesActivity.this);
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

                    if (!Helper.isFinishingOrIsDestroyed(BrowseCoursesActivity.this)) {
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
                    }
                } else {
                    Helper.showNetworkAlertMessage(BrowseCoursesActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeJoinedCourse(JoinCourseResponse response) {
        Course joinedCourse = response.getJoinedCourse();

        if (null != response.errorResponse) {
            mBrowseCoursesAdapter.remove(mBrowseCoursesAdapter.getItem(positionToRemove));
        }
        else if (response.getJoined().equals(TRUE)){
            joinedCourseList.add(joinedCourse);
            Helper.showToast("Successfully Joined", Toast.LENGTH_LONG);
            mBrowseCoursesAdapter.remove(joinedCourse);
        }

        if (mBrowseCoursesAdapter.isDataSetEmpty()) {
            mEmptyView.setText("Hurray! You joined all courses.\nKeep watching this space for upcoming courses.");
        }
        else {
            mEmptyView.setText("No Course Found");
        }
        mEmptyView.setVisibility(mBrowseCoursesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE );
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            //NavUtils.navigateUpTo(this, upIntent);
            Intent resultIntent = new Intent();
            // accepted course
            if (joinedCourseList.size() > 0) {
                Bundle mBundle = new Bundle();
                mBundle.putParcelableArrayList(JoinCourseResponse.JOINED_KEY, joinedCourseList);
                resultIntent.putExtras(mBundle);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("joinedCourseList : " + joinedCourseList.toString());
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private void refreshList() {

        if (TextUtils.isEmpty(queryTextChange)) {
            Requester.getInstance().cancelPendingRequests(MyCourses.TAG);
            mBrowseCoursesAdapter.clearData();

            refreshView();
            mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_BROWSE_COURSE);
            mMyCourses.setSearchString("");
            mMyCourses.sendMyCoursesRequest();
        } else {
            if (null == mMyCourses) {
                mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_BROWSE_COURSE);
            }
            mBrowseCoursesAdapter.removeFilter();
            mMyCourses.mMyCoursesRequest = null;
            mMyCourses.setSearchString(queryTextChange);
            mMyCourses.sendMyCoursesRequest();
        }
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Browse Courses");
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
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BrowseCoursesActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BrowseCoursesActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        try {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            MenuItem settingItm = menu.findItem(R.id.action_add_banner);
            settingItm.setVisible(false);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchItem != null) {
                // MenuItemCompat.getActionView(searchItem);

                searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String query) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("onQueryTextChange query : " + query);
                        queryTextChange = query;
                        if (TextUtils.isEmpty(query)) {
                            mBrowseCoursesAdapter.removeFilter();
                        } else {
                            mBrowseCoursesAdapter.setFilter(query);
                        }
                        mEmptyView.setVisibility((mBrowseCoursesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                        if (mBrowseCoursesAdapter.isDataSetEmpty()) {
                            mEmptyView.setText("Hurray! You joined all courses.\nKeep watching this space for upcoming courses.");
                        }
                        else {
                            mEmptyView.setText("No Course Found");
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("onQueryTextSubmit query : " + query);
                        if (Helper.isConnected()) {
                            if (null == mMyCourses) {
                                mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
                            }
                            mBrowseCoursesAdapter.removeFilter();
                            mMyCourses.mMyCoursesRequest = null;
                            mMyCourses.setSearchString(query);
                            mMyCourses.sendMyCoursesRequest();
                            startProgressDialog();
                            //Helper.hideKeyboardFromWindow(MyCoursesActivity.this);
                            searchView.clearFocus();
                        } else {
                            if (TextUtils.isEmpty(query)) {
                                mBrowseCoursesAdapter.removeFilter();
                            } else {
                                mBrowseCoursesAdapter.setFilter(query);
                            }
                        mEmptyView.setVisibility((mBrowseCoursesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                        }
                        Helper.hideKeyboardFromWindow(BrowseCoursesActivity.this);
                        return true;
                    }


                });
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onClose searchView");
                        mBrowseCoursesAdapter.removeFilter();
                        if (mBrowseCoursesAdapter.isDataSetEmpty()) {
                            mEmptyView.setText("Hurray! You joined all courses.\nKeep watching this space for upcoming courses.");
                        }
                        else {
                            mEmptyView.setText("No Course Found");
                        }
                        mEmptyView.setVisibility((mBrowseCoursesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                        return false;
                    }
                });
            }
            if (searchView != null) {
                try {
                    searchView.setSearchableInfo(searchManager
                            .getSearchableInfo(this.getComponentName()));

                    //final int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
                    final EditText searchTextView = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                    try {
                        Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                        mCursorDrawableRes.setAccessible(true);
                        mCursorDrawableRes.set(searchTextView, R.drawable.cursor_color); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                    } catch (Exception e) {
                    }

                } catch (Exception e) {
                    LogWriter.err(e);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }


        return true;
    }

    public void updateCourseList(MyCoursesResponse mMyCoursesResponse) {

            try {
                coursePictureURLstatic = mMyCoursesResponse.getCoursePictureUrl();
                if (mBrowseCoursesAdapter.getCoursePicUrl().isEmpty()) {
                    mBrowseCoursesAdapter.setCoursePicUrl(mMyCoursesResponse.getCoursePictureUrl());
                }

                if (!mMyCourses.getSearchString().isEmpty()) {
                    if (!mBrowseCoursesAdapter.getSearchMode()) {
                        mBrowseCoursesAdapter.setSearchMode(true);
                    }
                    mBrowseCoursesAdapter.addSearchedItems(mMyCoursesResponse.getCourseList());
                    mEmptyView.setText("No Course Found");
                }
                else {
                    mBrowseCoursesAdapter.addItems(mMyCoursesResponse.getCourseList());
                    mEmptyView.setText("Hurray! You joined all courses.\nKeep watching this space for upcoming courses.");
                }
                mEmptyView.setVisibility((mBrowseCoursesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);


                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("ItemCount : " + mBrowseCoursesAdapter.getItemCount());

            } catch (Exception e) {
                LogWriter.err(e);
            }
    }
}
