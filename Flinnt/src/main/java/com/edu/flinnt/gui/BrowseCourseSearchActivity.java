package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.BrowseCourseCategory;
import com.edu.flinnt.core.InstitutionList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 31/5/17.
 * Activity will show when user search in BrowseCourseFragment and click on search button.
 */

public class BrowseCourseSearchActivity extends AppCompatActivity {

    public static final String TAG = "BrowseCourseSearchActivity";
    private Toolbar mToolbar;
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private BrowseCourseSearchAdapter mBrowseCourseSearchAdapter;
    ArrayList<BrowsableCourse> mCourseList = new ArrayList<>();
    private ArrayList<BrowsableCourse> mReserveCourseList = new ArrayList<>();
    String searchTxt = "";
    private SearchView mSearchView = null;
    BrowseCourseCategory mBrowseCourseCategory;
    private boolean offlineSearchMode = false;
    private String queryTextChange = "";
    private int hasMoreRecords = 0, reserveHashMore = 0;
    private boolean isFirstSearch = true, isSearchSubmitte = false;
    public TextView mEmptyTxt;

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
            if (bundle.containsKey("searchText")) {
                searchTxt = bundle.getString("searchText");
            }
        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
        initView();
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
                                mCourseList = ((BrowseCoursesResponse) message.obj).getCourseList();
                                updateCourseList(mCourseList);
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                    }
                    break;
                    case Flinnt.FAILURE: {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof BrowseCoursesResponse) {
                            BrowseCoursesResponse response = (BrowseCoursesResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(BrowseCourseSearchActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
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
        if (Helper.isConnected()) {
            if (null == mBrowseCourseCategory) {
                mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
                mBrowseCourseCategory.setOffset(0);
                mBrowseCourseCategory.setSearchString(searchTxt);
                mBrowseCourseCategory.sendBrowseCoursesRequest();
                startProgressDialog();

            }
        } else {
            Helper.showNetworkAlertMessage(this);
        }


    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.course_recycler);
        mEmptyTxt = (TextView) findViewById(R.id.empty_text);
        mRecyclerView.setHasFixedSize(true);
        refreshView();
    }

    private void refreshView() {
        try {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(BrowseCourseSearchActivity.this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mBrowseCourseSearchAdapter = new BrowseCourseSearchAdapter(this,mCourseList);
            mRecyclerView.setAdapter(mBrowseCourseSearchAdapter);

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Update and display wish list
     *
     * @param mBrowseCoursesResponse
     */
    public void updateCourseList(ArrayList<BrowsableCourse> mBrowseCoursesResponse) {
        try {
            mBrowseCourseSearchAdapter.addItems(mBrowseCoursesResponse);
            if (mBrowseCourseSearchAdapter.getItemCount() == 0) {
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
            MyCommFun.sendTracker(this, "activity=" + Flinnt.BROWSECOURSE_SEARCH + "&user=" + Config.getStringValue(Config.USER_ID));
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
        getMenuInflater().inflate(R.menu.institute_course_menu, menu);
        MenuItem actionSearchItem = menu.findItem(R.id.action_search);
        actionSearchItem.setVisible(true);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (actionSearchItem != null) {
            // MenuItemCompat.getActionView(searchItem);
            mSearchView = (SearchView) actionSearchItem.getActionView();

            mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));
            mSearchView.setQuery(searchTxt, false);
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setFocusable(true);
            mSearchView.setIconified(false);
            mSearchView.requestFocusFromTouch();
            mSearchView.setFocusable(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getBrowsablelistFromAdapter();
                    clearInstituteList();
                    offlineSearchMode = false;

                    queryTextChange = query;
                    reserveHashMore = hasMoreRecords;
                    isSearchSubmitte = true;

                    if (Helper.isConnected()) {
                        Requester.getInstance().cancelPendingRequests(InstitutionList.TAG);
                        startProgressDialog();
                        mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
                        mBrowseCourseCategory.setSearchString(query);
                        mBrowseCourseCategory.sendBrowseCoursesRequest();
                        scrollListener.setPreviousTotal(0);                 // new InstitutionCourse(mHandler, "47049","515203").sendInstitutionCourseRequest();
                        mBrowseCourseSearchAdapter.setSearchSubmitted(true);
                    } else {
                        Helper.showNetworkAlertMessage(BrowseCourseSearchActivity.this);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mEmptyTxt.setVisibility(View.GONE);
                    offlineSearchMode = false;
                    if (newText.equals("") && !isFirstSearch) {
                        queryTextChange = "";
                        offlineSearchMode = false;
                        mBrowseCourseSearchAdapter.removeFilter(newText);
                    } else {
                        offlineSearchMode = true;
                        isFirstSearch = false;
                        mBrowseCourseSearchAdapter.setFilter(newText);
                    }

                    if (newText.isEmpty()) {
                        offlineSearchMode = false;
                        mBrowseCourseSearchAdapter.setSearchQuery("");
                        mBrowseCourseSearchAdapter.setSearchSubmitted(false);
                        if (isSearchSubmitte) {
                            isFirstSearch = true;
                            clearInstituteList();
                            queryTextChange = "";
                            isSearchSubmitte = false;
                            hasMoreRecords = reserveHashMore;
                            updateCourseList(mReserveCourseList);
                            mReserveCourseList.clear();
                            scrollListener.setPreviousTotal(mBrowseCourseSearchAdapter.getItemCount());
                            scrollListener.setLoading(false);
                        } else {
                            queryTextChange = "";
                            mBrowseCourseSearchAdapter.removeFilter("");
                            mBrowseCourseSearchAdapter.setSearchSubmitted(false);
                            updateCourseList(mReserveCourseList);
                            mReserveCourseList.clear();

                        }
                        mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
                        mBrowseCourseCategory.setSearchString("");
                        mBrowseCourseCategory.setOffset(mBrowseCourseSearchAdapter.getBrowsableCourseList().size());
                        mBrowseCourseCategory.sendBrowseCoursesRequest();
                    }
                    return false;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return false;
                }
            });


            MenuItemCompat.setOnActionExpandListener(actionSearchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                            offlineSearchMode = false;
                            mBrowseCourseSearchAdapter.setSearchQuery("");
                            mBrowseCourseSearchAdapter.setSearchSubmitted(false);
                            if (isSearchSubmitte) {
                                isFirstSearch = true;
                                clearInstituteList();
                                queryTextChange = "";
                                isSearchSubmitte = false;
                                hasMoreRecords = reserveHashMore;
                                updateCourseList(mReserveCourseList);
                                mReserveCourseList.clear();
                                scrollListener.setPreviousTotal(mBrowseCourseSearchAdapter.getItemCount());
                                scrollListener.setLoading(false);
                            } else {
                                queryTextChange = "";
                                mBrowseCourseSearchAdapter.removeFilter("");
                                mBrowseCourseSearchAdapter.setSearchSubmitted(false);
                                updateCourseList(mReserveCourseList);
                                mReserveCourseList.clear();
                            }
                            mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
                            mBrowseCourseCategory.setSearchString("");
                            mBrowseCourseCategory.setOffset(mBrowseCourseSearchAdapter.getBrowsableCourseList().size());
                            mBrowseCourseCategory.sendBrowseCoursesRequest();
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

    LinearLayoutManager layoutManager = new LinearLayoutManager(BrowseCourseSearchActivity.this);
    EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(layoutManager, false) {
        @Override
        public void onLoadMore(int current_page) {
            if (hasMoreRecords == 0)
                return;
            if (offlineSearchMode)
                return;
            loadMore();
        }
    };

    private void loadMore() {
        if (Helper.isConnected()) {
            mBrowseCourseCategory = null;
            //startProgressDialog();
            mBrowseCourseCategory = new BrowseCourseCategory(mHandler);
            mBrowseCourseCategory.setSearchString(queryTextChange);
            mBrowseCourseCategory.sendBrowseCoursesRequest();
        } else {
            Helper.showNetworkAlertMessage(BrowseCourseSearchActivity.this);
        }
    }


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BrowseCourseSearchActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BrowseCourseSearchActivity.this, getString(R.string.login), getString(R.string.please_wait), Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    private void clearInstituteList() {
        mBrowseCourseSearchAdapter.clearAllData();
    }

    private void getBrowsablelistFromAdapter() {
        mCourseList.addAll(mBrowseCourseSearchAdapter.getBrowsableCourseList());
    }

}