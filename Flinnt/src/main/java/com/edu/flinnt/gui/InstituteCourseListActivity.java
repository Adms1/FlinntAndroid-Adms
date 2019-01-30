package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.InstitutionCourse;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.InstitutionCoursesRequest;
import com.edu.flinnt.protocol.InstitutionCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 19/10/16.
 */
public class InstituteCourseListActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    public static final String TAG = "InstituteCourseListActivity";
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ImageView courseDetailImg;
    private Resources res = FlinntApplication.getContext().getResources();
    private Handler mHandler;
    private String instituteID = "", coursePicture = "", instituteName = "", bannerPath = "";
    private ImageLoader mImageLoader = null;
    private SearchView mSearchView = null;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private AppBarLayout appBarLayout;
    InstitutionCoursesRequest mInstitutionCoursesRequest;
    private InstituteCourseAdapter mInstituteCourseAdapter;
    private int hasMoreRecords = 0, reserveHashMore = 0;
    private int offset = 0, reserOffset = 0;
    int max_count = 20;
    private String searchQuery = "";
    private boolean offlineSearchMode = false;
    private boolean isFirstSearch = true, isSearchSubmitte = false;
    private ArrayList<InstitutionCoursesResponse.Course> mReserveInstitutionsList = new ArrayList<>();
    public TextView mEmptyTxt;
    public static final int COURSE_UNPUBLISH_CALLBACK = 119;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.activity_institute_course_list);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                instituteID = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                instituteName = bundle.getString(Course.COURSE_NAME_KEY);
        }


        mToolbar = (Toolbar) findViewById(R.id.institute_courses_details_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        setTitleToolbar();

        courseDetailImg = (ImageView) findViewById(R.id.institute_course_details_image);
        courseDetailImg.setColorFilter(getResources().getColor(R.color.course_image_transperent_bg), android.graphics.PorterDuff.Mode.MULTIPLY);


        appBarLayout = (AppBarLayout) findViewById(R.id.institute_course_appbar);
        TextView instituteNameTxt = (TextView) findViewById(R.id.institute_name_text);
        instituteNameTxt.setText(instituteName);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS: {
                        isLoading = false;
                        if (message.obj instanceof InstitutionCoursesResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE InstitutionCoursesResponse : " + message.obj.toString());
                            mReserveInstitutionsList = (ArrayList<InstitutionCoursesResponse.Course>) ((InstitutionCoursesResponse) message.obj).getData().getCourses();
                            updateCourseList(mReserveInstitutionsList);
                            bannerPath = ((InstitutionCoursesResponse) message.obj).getData().getInstituteBannerUrl();
                            coursePicture = ((InstitutionCoursesResponse) message.obj).getData().getInstituteBanner();
                            hasMoreRecords = ((InstitutionCoursesResponse) message.obj).getData().getHasMore();

                            updateCoursePicture();

                        }
                    }
                    break;
                    case Flinnt.FAILURE: {
                        isLoading = false;
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                    }
                    break;
                    default:
                        super.handleMessage(message);
                        break;
                }
            }
        };
        if (Helper.isConnected()) {
            startProgressDialog();
            mInstitutionCoursesRequest = new InstitutionCoursesRequest();
            //   InstitutionCourse mInstitutionCourse = new InstitutionCourse(mHandler, "45999", "45980");
            InstitutionCourse mInstitutionCourse = new InstitutionCourse(mHandler, Config.getStringValue(Config.USER_ID), instituteID);
            isLoading = true;
            mInstitutionCourse.setOffest(offset);
            mInstitutionCourse.sendInstitutionCourseRequest(mInstitutionCoursesRequest);
            // new InstitutionCourse(mHandler, "47049","515203").sendInstitutionCourseRequest();
        } else {
            Helper.showNetworkAlertMessage(this);
        }


        initView();
    }

    private void setTitleToolbar() {
        boolean needToAddView = false;

        if (mToolbarTitle == null) {
            mToolbarTitle = new TextView(this);
            needToAddView = true;
        }

        mToolbarTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mToolbarTitle.setTextSize(20);
        mToolbarTitle.setSingleLine(true);
        mToolbarTitle.setTextAppearance(this, android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
        mToolbarTitle.setTextColor(Color.WHITE);

        if (needToAddView) {
            mToolbar.addView(mToolbarTitle);
        }

    }

    private void initView() {

        mEmptyTxt = (TextView) findViewById(R.id.empty_text);

        mRecyclerView = (RecyclerView) findViewById(R.id.institute_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        refreshView();
    }

    /**
     * Update and display wish list
     *
     * @param mInstitutionCoursesResponse Wish list response
     */
    public void updateCourseList(ArrayList<InstitutionCoursesResponse.Course> mInstitutionCoursesResponse) {
        try {
            mInstituteCourseAdapter.addItems(mInstitutionCoursesResponse);
            if (mInstituteCourseAdapter.getItemCount() == 0) {
                mEmptyTxt.setVisibility(View.VISIBLE);
            } else {
                mEmptyTxt.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void clearInstituteList() {
        mInstituteCourseAdapter.clearData();
    }

    private void getInstitutelistFromAdapter() {
        mReserveInstitutionsList.addAll(mInstituteCourseAdapter.getCourseList());
    }

    private void refreshView() {
        try {

            mRecyclerView.setLayoutManager(mLayoutManager);
            mInstituteCourseAdapter = new InstituteCourseAdapter(mReserveInstitutionsList);
            mRecyclerView.setAdapter(mInstituteCourseAdapter);
            mInstituteCourseAdapter.setOnItemClickListener(new InstituteCourseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
                    } else {
                        InstitutionCoursesResponse.Course course = mInstituteCourseAdapter.getItem(position);
                        Intent courseDescriptionIntent = new Intent(InstituteCourseListActivity.this, BrowseCourseDetailActivity.class);
                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, course.getCourseId());
                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, course.getCoursePicture());
                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, course.getCourseName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, course.getCourseOwnerName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, course.getStatTotalRating());
                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, course.getTotalUsers());
                        startActivityForResult(courseDescriptionIntent, COURSE_UNPUBLISH_CALLBACK);
                    }
                }
            });

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                    hasMoreRecords = 0;
                    reserveHashMore = 0;
                    offset = 0;
                    reserOffset = 0;
                    //searchQuery = "";
                    offlineSearchMode = false;
                    isFirstSearch = true;
                    isSearchSubmitte = false;
                    mInstituteCourseAdapter.clearData();
                    mReserveInstitutionsList.clear();
                    Requester.getInstance().cancelPendingRequests(InstitutionCourse.TAG);
                    if (Helper.isConnected()) {
                        startProgressDialog();
                        swipeRefreshLayout.setEnabled(true);
                        mInstitutionCoursesRequest = new InstitutionCoursesRequest();
                        InstitutionCourse mInstitutionCourse = new InstitutionCourse(mHandler, Config.getStringValue(Config.USER_ID), instituteID);
                        isLoading = true;
                        mInstitutionCourse.setOffest(offset);
                        mInstitutionCourse.setSearch(searchQuery);
                        mInstitutionCourse.sendInstitutionCourseRequest(mInstitutionCoursesRequest);
                    } else {
                        Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
                    }

                }
            });
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(InstituteCourseListActivity.this, 2);
    EndlessRecyclerOnScrollListener mEndlessRecyclerScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager, true) {
        @Override
        public void onLoadMore(int current_page) {
            if (hasMoreRecords == 0)
                return;
            if (offlineSearchMode)
                return;
            loadMore();
        }
    };

    /**
     * Save and update course picture
     */
    private void updateCoursePicture() {
        if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("updateCoursePicture");
        final String imgUrl = bannerPath + Flinnt.USER_BANNER_LARGE + File.separator + coursePicture;
        mImageLoader = Requester.getInstance().getImageLoader();
        if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("imgUrl : " + imgUrl);
        mImageLoader.get(imgUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("onResponse Bitmap : " + response.getBitmap());
                Bitmap responseBitmap = response.getBitmap();

                if (null != responseBitmap) {
                    courseDetailImg.setImageBitmap(responseBitmap);
                    Helper.saveBitmapToSDcard(responseBitmap, Helper.getFlinntUrlPath(imgUrl), coursePicture);
                } else {
                    courseDetailImg.setImageResource(R.drawable.default_course_image);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                courseDetailImg.setImageResource(R.drawable.default_course_image);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (null != bundle) {

            if (LogWriter.isValidLevel(Log.DEBUG))
                LogWriter.write("contains, isCourseRemoved : " + bundle.containsKey("isCourseRemoved") + ", " + bundle.getBoolean("isCourseRemoved"));

            if (bundle.containsKey("isCourseRemoved") && bundle.getBoolean("isCourseRemoved")) {
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), instituteID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            boolean isPostDeleted = bundle.getBoolean("isPostDeleted");
            int isFromNotification = bundle.getInt("isFromNotification");
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("isPostDeleted : " + isPostDeleted + " , isFromNotification : " + isFromNotification);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Institute Course List");
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {

                    case COURSE_UNPUBLISH_CALLBACK:
                        String courseID = data.getStringExtra(BrowsableCourse.ID_KEY);
                        mInstituteCourseAdapter.remove(courseID);
                        if (mInstituteCourseAdapter.getItemCount() == 0) {
                            mEmptyTxt.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyTxt.setVisibility(View.GONE);
                        }
                        break;
                }
            }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.institute_course_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = null;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            // MenuItemCompat.getActionView(searchItem);
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    getInstitutelistFromAdapter();
                    clearInstituteList();
                    offlineSearchMode = false;
                    reserOffset = offset;
                    offset = 0;
                    searchQuery = query;
                    reserveHashMore = hasMoreRecords;
                    isSearchSubmitte = true;

                    if (Helper.isConnected()) {
                        startProgressDialog();
                        InstitutionCourse mInstitutionCourse = new InstitutionCourse(mHandler, Config.getStringValue(Config.USER_ID), instituteID);
                        mInstitutionCourse.setSearch(query);
                        mInstitutionCourse.sendInstitutionCourseRequest(mInstitutionCoursesRequest);                        // new InstitutionCourse(mHandler, "47049","515203").sendInstitutionCourseRequest();
                    } else {
                        Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
                    }


                    mEndlessRecyclerScrollListener.setPreviousTotal(0);
                    mSearchView.clearFocus();
                    Helper.hideKeyboardFromWindow(InstituteCourseListActivity.this);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onQueryTextChange query : " + query);
                    mEmptyTxt.setVisibility(View.GONE);
                    offlineSearchMode = false;
                    if (isSearchSubmitte) {
                        isFirstSearch = true;
                        clearInstituteList();
                        searchQuery = "";
                        isSearchSubmitte = false;
                        hasMoreRecords = reserveHashMore;
                        updateCourseList(mReserveInstitutionsList);
                        mEndlessRecyclerScrollListener.setPreviousTotal(mInstituteCourseAdapter.getItemCount());
                        mEndlessRecyclerScrollListener.setLoading(false);
                        offset = reserOffset;
                    } else {
                        if (query.equals("") && !isFirstSearch) {
                            searchQuery = "";
                            offlineSearchMode = false;
                            mInstituteCourseAdapter.removeFilter();
                        } else {
                            offlineSearchMode = true;
                            isFirstSearch = false;
                            mInstituteCourseAdapter.setFilter(query);
                        }
                    }
                    return false;
                }
            });
            mSearchView.setOnCloseListener(new OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onClose searchView");
                    return false;
                }
            });

            MenuItemCompat.setOnActionExpandListener(searchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                            offlineSearchMode = false;
                            if (isSearchSubmitte) {
                                isFirstSearch = true;
                                clearInstituteList();
                                searchQuery = "";
                                isSearchSubmitte = false;
                                hasMoreRecords = reserveHashMore;
                                updateCourseList(mReserveInstitutionsList);
                                mEndlessRecyclerScrollListener.setPreviousTotal(mInstituteCourseAdapter.getItemCount());
                                mEndlessRecyclerScrollListener.setLoading(false);
                                offset = reserOffset;
                            } else {
                                searchQuery = "";
                                mInstituteCourseAdapter.removeFilter();
                            }
                            return true;
                        }
                    });
        }
        if (mSearchView != null) {
            try {
                mSearchView.setSearchableInfo(searchManager
                        .getSearchableInfo(this.getComponentName()));

                final EditText searchTextView = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
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
        return true;
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
     * Bug found in some when toolbar is half-way collapsed and a touch is made on image (some phones only)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    private void loadMore() {
        if (Helper.isConnected()) {
            if (hasMoreRecords == 0)
                return;
            if (offlineSearchMode)
                return;
            offset = offset + max_count;
            InstitutionCourse mInstitutionCourse = new InstitutionCourse(mHandler, Config.getStringValue(Config.USER_ID), instituteID);
            mInstitutionCourse.setOffest(offset + 1);
            mInstitutionCourse.setSearch(searchQuery);
            mInstitutionCourse.sendInstitutionCourseRequest(mInstitutionCoursesRequest);                    // new InstitutionCourse(mHandler, "47049","515203").sendInstitutionCourseRequest();
        } else {
            Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        /*if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
        } else {
            Helper.showNetworkAlertMessage(InstituteCourseListActivity.this);
        }*/
    }


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(InstituteCourseListActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(InstituteCourseListActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            swipeRefreshLayout.setEnabled(true);
        } else {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }


    private String queryTextChange = "";
    private boolean isLoading = false;
}