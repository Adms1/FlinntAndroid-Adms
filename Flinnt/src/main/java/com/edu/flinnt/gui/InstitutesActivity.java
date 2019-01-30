package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
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
import com.edu.flinnt.core.InstitutionList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.InstitutionResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 15/11/16.
 */

public class InstitutesActivity extends AppCompatActivity implements MyCoursesActivity.AppBarLayoutSwitchListener {
    public static final String TAG = InstitutesActivity.class.getSimpleName();
    Toolbar toolbar;
    private RecyclerView mSelectInstituteRecycler;
    private InstitutesAdapter institutesAdapter;
    public Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    ArrayList<InstitutionResponse.Institution> mInstitutions = new ArrayList<>();
    InstitutionList mInstitutionList;
    private InstitutionResponse mInstitutionResponse;
    private int hasMoreRecords = 0, reserveHashMore = 0;
    private int reserOffset = 0;
    int max_count = 100;
    private String queryTextChange = "";
    private boolean offlineSearchMode = false;
    private boolean isFirstSearch = true, isSearchSubmitte = false;
    private ArrayList<InstitutionResponse.Institution> mReserveInstitutionsList = new ArrayList<>();
    public TextView mEmptyTxt;
    private SearchView mSearchView = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.fragment_institutes);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Institute");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(InstitutesActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });

        mSelectInstituteRecycler = (RecyclerView) findViewById(R.id.selectInstituteRecycler);
        mEmptyTxt = (TextView) findViewById(R.id.empty_text);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectInstituteRecycler.setLayoutManager(layoutManager);
        //mSelectInstituteRecycler.addOnScrollListener(scrollListener);

        refreshView();


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
                            if (message.obj instanceof InstitutionResponse) {
                                mInstitutionResponse = (InstitutionResponse) message.obj;
                                hasMoreRecords = mInstitutionResponse.getData().getHasMore();
                                mInstitutions = (ArrayList<InstitutionResponse.Institution>) ((InstitutionResponse) message.obj).getData().getInstitutions();
                                updateInstituteList(mInstitutions);

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

        if (Helper.isConnected()) {
            startProgressDialog();
            mInstitutionList = new InstitutionList(mHandler, queryTextChange);
            mInstitutionList.sendInstitutionListRequest();
        } else {
            Helper.showNetworkAlertMessage(InstitutesActivity.this);
        }

    }

    /**
     * Refresh the view to grid or list type
     */
    public void refreshView() {

        try {

            institutesAdapter = new InstitutesAdapter(mInstitutions);

            mSelectInstituteRecycler.setAdapter(institutesAdapter);
            mInstitutionList = null;


            institutesAdapter.setOnItemClickListener(new InstitutesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(InstitutesActivity.this);
                    } else {
                        Intent intent = new Intent(InstitutesActivity.this, InstituteCourseListActivity.class);
                        intent.putExtra(Course.COURSE_ID_KEY, institutesAdapter.getItem(position).getUserId());
                        intent.putExtra(Course.COURSE_NAME_KEY, institutesAdapter.getItem(position).getUserSchoolName());
                        startActivity(intent);
                    }
                }
            });



        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
    /**
     * Clears the data and make new request
     */
    private void refreshList() {
        Requester.getInstance().cancelPendingRequests(InstitutionList.TAG);
        if (TextUtils.isEmpty(queryTextChange)) {
            institutesAdapter.setSearchMode(false);
            institutesAdapter.clearData();
            mInstitutions = null;
            refreshView();
            if (Helper.isConnected()) {
                mInstitutionList = new InstitutionList(mHandler, "");
                mInstitutionList.setSearchString("");
                institutesAdapter.setSearchSubmitted(false);
                mInstitutionList.sendInstitutionListRequest();
            } else {
                Helper.showNetworkAlertMessage(InstitutesActivity.this);
            }
        } else {
            if (Helper.isConnected()) {
                institutesAdapter.setSearchSubmitted(true);
                mInstitutionList = new InstitutionList(mHandler, queryTextChange);
                institutesAdapter.clearData();
                mInstitutionList.setSearchString(queryTextChange);
                mInstitutionList.sendInstitutionListRequest();
            } else {
                Helper.showNetworkAlertMessage(InstitutesActivity.this);
            }
        }
    }

    private void clearInstituteList() {
        institutesAdapter.clearAllData();
    }

    private void getInstitutelistFromAdapter() {
        mReserveInstitutionsList.addAll(institutesAdapter.getInstituteList());
    }

    private void updateInstituteList(ArrayList<InstitutionResponse.Institution> items) {

        Config.setStringValue(Config.INSTITUTE_USER_PICTURE_URL, mInstitutionResponse.getData().getUserPictureUrl());
        institutesAdapter.addItems(items);
        if (institutesAdapter.getItemCount() == 0) {
            mEmptyTxt.setVisibility(View.VISIBLE);
        } else {
            mEmptyTxt.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Institutes");
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
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getInstitutelistFromAdapter();
                    clearInstituteList();
                    offlineSearchMode = false;

                    queryTextChange = query;
                    reserveHashMore = hasMoreRecords;
                    isSearchSubmitte = true;

                    if (Helper.isConnected()) {
                        Requester.getInstance().cancelPendingRequests(InstitutionList.TAG);
                        startProgressDialog();
                        mInstitutionList = new InstitutionList(mHandler, query);
                        mInstitutionList.sendInstitutionListRequest();
                        scrollListener.setPreviousTotal(0);                 // new InstitutionCourse(mHandler, "47049","515203").sendInstitutionCourseRequest();
                        institutesAdapter.setSearchSubmitted(true);
                    } else {
                        Helper.showNetworkAlertMessage(InstitutesActivity.this);
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
                        institutesAdapter.removeFilter(newText);
                    } else {
                        offlineSearchMode = true;
                        isFirstSearch = false;
                        institutesAdapter.setFilter(newText);
                    }

                    if (newText.isEmpty()) {
                        offlineSearchMode = false;
                        institutesAdapter.setSearchQuery("");
                        institutesAdapter.setSearchSubmitted(false);
                        if (isSearchSubmitte) {
                            isFirstSearch = true;
                            clearInstituteList();
                            queryTextChange = "";
                            isSearchSubmitte = false;
                            hasMoreRecords = reserveHashMore;
                            updateInstituteList(mReserveInstitutionsList);
                            mReserveInstitutionsList.clear();
                            scrollListener.setPreviousTotal(institutesAdapter.getItemCount());
                            scrollListener.setLoading(false);
                        } else {
                            queryTextChange = "";
                            institutesAdapter.removeFilter("");
                            institutesAdapter.setSearchSubmitted(false);
                            updateInstituteList(mReserveInstitutionsList);
                            mReserveInstitutionsList.clear();

                        }
                        mInstitutionList = new InstitutionList(mHandler, "");
                        mInstitutionList.setOffset(institutesAdapter.getInstituteList().size());
                        mInstitutionList.sendInstitutionListRequest();
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
                            institutesAdapter.setSearchQuery("");
                            institutesAdapter.setSearchSubmitted(false);
                            if (isSearchSubmitte) {
                                isFirstSearch = true;
                                clearInstituteList();
                                queryTextChange = "";
                                isSearchSubmitte = false;
                                hasMoreRecords = reserveHashMore;
                                updateInstituteList(mReserveInstitutionsList);
                                mReserveInstitutionsList.clear();
                                scrollListener.setPreviousTotal(institutesAdapter.getItemCount());
                                scrollListener.setLoading(false);
                            } else {
                                queryTextChange = "";
                                institutesAdapter.removeFilter("");
                                institutesAdapter.setSearchSubmitted(false);
                                updateInstituteList(mReserveInstitutionsList);
                                mReserveInstitutionsList.clear();

                            }
                            mInstitutionList = new InstitutionList(mHandler,"");
                            mInstitutionList.setOffset(institutesAdapter.getInstituteList().size());
                            mInstitutionList.sendInstitutionListRequest();
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
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

        }
        return false;
    }
    LinearLayoutManager layoutManager = new LinearLayoutManager(InstitutesActivity.this);
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
            mInstitutionList = null;
            //startProgressDialog();
            mInstitutionList = new InstitutionList(mHandler, queryTextChange);
            mInstitutionList.sendInstitutionListRequest();
        } else {
            Helper.showNetworkAlertMessage(InstitutesActivity.this);
        }
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(InstitutesActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(InstitutesActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onOffsetChanged(int i) {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setEnabled(i == 0);
    }
}