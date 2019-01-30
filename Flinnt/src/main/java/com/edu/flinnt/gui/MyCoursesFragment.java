
package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.MyCoursesListAdapter;
import com.edu.flinnt.core.MyCourses;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.MyCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;


/**
 * Main GUI class to show joined course list, navigation drawer and all other options available
 */
public class MyCoursesFragment extends Fragment implements MyCoursesActivity.OnSearchListener, MyCoursesActivity.AppBarLayoutSwitchListener, View.OnClickListener {

    public Handler mHandler = null;
    private RecyclerView mRecyclerView;
    public MyCoursesListAdapter mMyCoursesAdapter;
    private LayoutManager mLayoutManager;
    private ArrayList<String> mOfflineCouseIDs = new ArrayList<String>();
    private ArrayList<Course> mCourseList = new ArrayList<>();
    private MyCourses mMyCourses = null;
    public TextView mEmptyTextView;
    public Button mEmptyButton;
    private ProgressDialog mProgressDialog = null;
    public static String coursePictureURLstatic = "https://flinnt1.s3.amazonaws.com/courses/";
    private SwipeRefreshLayout swipeRefreshLayout;
    private String queryTextChange = "";
    private static String TAG = "Myy";
    private boolean isLoading = false;


    public static MyCoursesFragment newInstance(/*String userId*/) {
        MyCoursesFragment fragment = new MyCoursesFragment();

/*        Bundle args = new Bundle();
        args.putString("user_id", userId);
        fragment.setArguments(args);*/

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate()");
        //startProgressDialog();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        try {
                            //Log.d(TAG, "res Success : " + message.obj.toString());
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof MyCoursesResponse) {
                                updateCourseList((MyCoursesResponse) message.obj);

                            }
                        } catch (Exception e) {
                            //Log.d(TAG, "onCatch errror : " + e.getMessage());
                            LogWriter.err(e);
                        }

                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                            //Log.d(TAG, "onFailuar : " + message.obj.toString());
                        } catch (Exception e) {
                            //Log.d(TAG, "onCatch fail : " + e.getMessage());
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }

        };

        loadCourseList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.my_courses_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_browse_courses);

        mEmptyButton = (Button) rootView.findViewById(R.id.empty_browse_courses_btn);
        mEmptyButton.setOnClickListener(this);
        mEmptyButton.setLongClickable(false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_courses_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });
        try {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mMyCoursesAdapter = new MyCoursesListAdapter(mCourseList, true, getActivity());
            mMyCoursesAdapter.setOnItemClickListener(new MyCoursesListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //.d(TAG, "onItemClick()");
                    MyCoursesActivity.coursePositionOnItemClick = position;
                    if (null != mMyCoursesAdapter.getItem(position)) {
                        //Log.d(TAG, "onItemClick() - item is available ");
                        Intent courseIntent = new Intent(getActivity(), CourseDetailsActivity.class);
                        courseIntent.putExtra(Course.COURSE_ID_KEY, mMyCoursesAdapter.getItem(position).getCourseID());
                        courseIntent.putExtra(Course.COURSE_PICTURE_KEY, mMyCoursesAdapter.getItem(position).getCoursePicture());
                        courseIntent.putExtra(Course.COURSE_NAME_KEY, mMyCoursesAdapter.getItem(position).getCourseName());
                        courseIntent.putExtra(Course.ALLOWED_ROLES_KEY, mMyCoursesAdapter.getItem(position).getAllowedRoles());
                        getActivity().startActivityForResult(courseIntent, MyCoursesActivity.COURSE_UNSUBSCRIBED_CALL_BACK);
                    }
                }
            });

            mRecyclerView.setAdapter(mMyCoursesAdapter);

        } catch (Exception e) {
            LogWriter.err(e);
            //Log.d(TAG, "onCatch : " + e.getMessage());
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume()");
        if (isLoading) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //showEmptyButton();
    }

    private void showEmptyButton() { //@Chriag: 14/08/2018
        if (mMyCoursesAdapter.getItemCount() == 0) {
            if (mMyCoursesAdapter.getSearchMode()) {
                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                mEmptyButton.setVisibility(View.GONE);
            } else if (isLoading){
                mEmptyTextView.setText(getResources().getString(R.string.no_course_message));
                mEmptyTextView.setVisibility(View.VISIBLE);
                mEmptyButton.setVisibility(View.VISIBLE);
            }
        } else {
            mEmptyButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //@Chirag 02/08/2018
        //Log.d(TAG, "onPause()");
        isLoading = false;
    }

    private void loadCourseList() {

        if (!Helper.isConnected()) {
            //Log.d(TAG, "loadCourseList()-isConnected false : ");
            mOfflineCouseIDs = CourseInterface.getInstance().getAllCourseIDs();
            mCourseList = CourseInterface.getInstance().getAllCourses();
        } else {

            isLoading = true;

            if (null == mMyCourses) {

                //Log.d(TAG, "mMyCourses = null");
                //**********@Chirag : For save corces offline also when load the course | 08/08/2018
                CourseInterface.getInstance().deleteAllCourseForUser(Config.getStringValue(Config.USER_ID));
                // get offline courses from database
                mOfflineCouseIDs = CourseInterface.getInstance().getAllCourseIDs();
                mCourseList = CourseInterface.getInstance().getAllCourses();
                //**********

                mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
                mMyCourses.setSearchString("");

                //Log.d(TAG, "loadCourseList()- mOflineCourseIds.size : " + mOfflineCouseIDs.size());
                if (mOfflineCouseIDs.size() > 0) {
                    mMyCourses.setOfflineCourseIDs(mOfflineCouseIDs);
                }
                //Log.d(TAG, "loadCourseList()-sendMyCoursesRequest()");
                //@Chirag for loading 30/07/2018
                //startProgressDialog();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                mMyCourses.sendMyCoursesRequest();
                //.d(TAG, "app clled");
            } else {
                //@Chirag 30/07/2018
                Requester.getInstance().cancelPendingRequests(MyCourses.TAG);
                mMyCoursesAdapter.clearData();
                mCourseList.clear();
                mMyCoursesAdapter.notifyDataSetChanged();

                mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
                mMyCourses.setSearchString("");
                if (mOfflineCouseIDs.size() > 0) {
                    mMyCourses.setOfflineCourseIDs(mOfflineCouseIDs);
                }

                //startProgressDialog();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                mMyCourses.sendMyCoursesRequest();

                if (swipeRefreshLayout.isRefreshing()) {
                    //Log.d(TAG, "loadCourseList()-hide tv & btn..");
                    mEmptyTextView.setVisibility(View.GONE);
                    mEmptyButton.setVisibility(View.GONE);
                }
            }
        }
    }

    public void updateCourseList(MyCoursesResponse mMyCoursesResponse) {
        //Log.d(TAG, "updateCourseList");
        try {
            coursePictureURLstatic = mMyCoursesResponse.getCoursePictureUrl();

            if (mMyCoursesAdapter.getCoursPictureUrl().isEmpty()) {
                mMyCoursesAdapter.setCoursPictureUrl(mMyCoursesResponse.getCoursePictureUrl());
            }
            if (!mMyCourses.getSearchString().isEmpty()) {
                if (!mMyCoursesAdapter.getSearchMode()) {
                    mMyCoursesAdapter.setSearchMode(true);
                }
                mMyCoursesAdapter.addSearchedItems(mMyCoursesResponse.getCourseList());
            } else {
                if (!mMyCourses.isUpdateDB()) {
                    mMyCoursesAdapter.addItems(mMyCoursesResponse.getCourseList());
                } else {
                    mMyCoursesAdapter.updateItems(mMyCoursesResponse.getCourseList(), mOfflineCouseIDs);
                }
                ((MyCoursesActivity) getActivity()).updateFabVisibility();
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mMyCoursesAdapter.getItemCount());

            /*Log.d(TAG, "ItemCount : " + mMyCoursesAdapter.getItemCount()
                    + "\nmOfflineCourseIds : " + mOfflineCouseIDs.size()
                    + "\nmyCour.getSearch.isEmpty : " + mMyCourses.getSearchString().isEmpty()
                    + "\nmyCour.isUpdateDb : " + mMyCourses.isUpdateDB()
                    + "\nmyCour.gethasMore : " + mMyCourses.getLastMyCoursesResponse().getHasMore()
            );*/
            // now update database courses
            if (mOfflineCouseIDs.size() > 0 && mMyCourses.getSearchString().isEmpty() && !mMyCourses.isUpdateDB() && mMyCourses.getLastMyCoursesResponse().getHasMore() == Flinnt.FALSE) {
                if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Update database courses");
                mMyCourses.setUpdateDB(true);
                mMyCourses.sendMyCoursesRequest();

            }
            mEmptyTextView.setVisibility(mMyCoursesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            if (mMyCoursesAdapter.getItemCount() == 0) {
                if (mMyCoursesAdapter.getSearchMode()) {
                    mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                    mEmptyButton.setVisibility(View.GONE);
                } else {
                    mEmptyTextView.setText(getResources().getString(R.string.no_course_message));
                    mEmptyButton.setVisibility(View.VISIBLE);
                }
            } else {
                mEmptyButton.setVisibility(View.GONE);
            }

            //Log.d(TAG, "mMyCoursesResponse.getHasMore() : " + mMyCoursesResponse.getHasMore() + "\nisUpdateDB() : " + mMyCourses.isUpdateDB());
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("mMyCoursesResponse.getHasMore() : " + mMyCoursesResponse.getHasMore() + "\nisUpdateDB() : " + mMyCourses.isUpdateDB());
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void refreshList() {

        if (TextUtils.isEmpty(queryTextChange)) {
            Requester.getInstance().cancelPendingRequests(MyCourses.TAG);
            mMyCoursesAdapter.clearData();

            CourseInterface.getInstance().deleteAllCourseForUser(Config.getStringValue(Config.USER_ID));
            // get offline courses from database
            mOfflineCouseIDs = CourseInterface.getInstance().getAllCourseIDs();
            mCourseList = CourseInterface.getInstance().getAllCourses();
            mMyCoursesAdapter.notifyDataSetChanged();

            mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
            mMyCourses.setSearchString("");
            if (mOfflineCouseIDs.size() > 0) {
                mMyCourses.setOfflineCourseIDs(mOfflineCouseIDs);
            }
            mMyCourses.sendMyCoursesRequest();

        } else {
            if (null == mMyCourses) {
                mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
            }
            mMyCoursesAdapter.clearData();
            mMyCourses.mMyCoursesRequest = null;
            mMyCourses.setSearchString(queryTextChange);
            mMyCourses.setUpdateDB(false);
            mMyCourses.sendMyCoursesRequest();

        }
    }


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {

        //Log.d(TAG, "startProgressDialog()");
        if (Helper.isConnected()) {

            if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
                mProgressDialog = Helper.getProgressDialog(getActivity(), "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
                if (mProgressDialog != null && !mProgressDialog.isShowing()) mProgressDialog.show();
            }
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {
        //Log.d(TAG, "stopProgressDialog()");
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            //Log.d(TAG, "onCatchStopProgress()" + e.getMessage());
            LogWriter.err(e);
        } finally {
            //Log.d(TAG, "finally()");
            mProgressDialog = null;
        }
    }

    @Override
    public void onSearch(String query, Boolean isSubmit) {

        //Log.d(TAG," MyCourseFragment.java onSearch()");
        try {
            queryTextChange = query;
            if (TextUtils.isEmpty(query)) {
                //Log.d(TAG,"onSearch : " + query);
                mMyCoursesAdapter.removeFilter();
                mEmptyTextView.setVisibility(mMyCoursesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                if (mMyCoursesAdapter.getItemCount() == 0) {
                    //Log.d(TAG,"mMyCoursesAda = 0");
                    //@Chirag Below added if() only, not content | 02/08/2018
                    if (!swipeRefreshLayout.isRefreshing()) {
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_message));
                        mEmptyButton.setVisibility(View.VISIBLE);
                    } else {
                        //whole else part added by chirag
                        mEmptyTextView.setVisibility(View.GONE);
                        mEmptyButton.setVisibility(View.GONE);
                    }
                } else {

                    //Log.d(TAG,"mMyCoursesAdapter > 0 : "+ query);
                    mEmptyButton.setVisibility(View.GONE);
                    mMyCourses.mMyCoursesRequest = null;
                    mMyCourses.setSearchString(query);
                    mMyCourses.setUpdateDB(false);
                    mMyCourses.setOffset(mMyCoursesAdapter.getItemCount() - mMyCourses.getOfflineCourseIDs().size());
                    mMyCourses.sendMyCoursesRequest();

                }
            } else {
                //Log.d(TAG,"isNotEmpty : " + query);
                if (isSubmit) {
                    //Log.d(TAG,"isSubmit : true");
                    if (Helper.isConnected()) {
                        //Log.d(TAG,"isSubmit : isConnected");
                        Requester.getInstance().cancelPendingRequests(MyCourses.TAG);

                        if (null == mMyCourses) {
                            mMyCourses = new MyCourses(mHandler, MyCourses.TYPE_MY_COURSE);
                        }

                        mMyCoursesAdapter.clearData();
                        mMyCourses.mMyCoursesRequest = null;
                        mMyCourses.setSearchString(query);
                        mMyCourses.setOffset(0);
                        mMyCourses.setUpdateDB(false);
                        mMyCourses.sendMyCoursesRequest();
                        startProgressDialog();

                        mMyCoursesAdapter.setSearchMode(true);
                    } else {
                        if (TextUtils.isEmpty(query)) {
                            mMyCoursesAdapter.removeFilter();
                        } else {
                            mMyCoursesAdapter.setFilter(query);
                        }
                    }
                } else {
                    //Log.d(TAG,"isSubmit : false");
                    mMyCoursesAdapter.setFilter(query);
                    mEmptyTextView.setVisibility(mMyCoursesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                    mEmptyButton.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onOffsetChanged(int i) {

        //Log.d(TAG, "onOffsetChanged i : " + i);
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    public void onClick(View v) {
        if (v == mEmptyButton) {
            ((MyCoursesActivity) getActivity()).switchToBrowseCourses();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(getActivity(), "activity=" + Flinnt.MY_COURSE + "&user=" + Config.getStringValue(Config.USER_ID));
            GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
}