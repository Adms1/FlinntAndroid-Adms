package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentsList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.contentlist.Data;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.OnUpdateMenuListner;
import com.google.android.gms.analytics.GoogleAnalytics;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.edu.flinnt.R.id.retry_btn;

public class ContentsFragment extends Fragment implements View.OnClickListener, CourseDetailsActivity.onSearchListener, CourseDetailsActivity.appBarLayoutOnOffsetChanged {
    private TextView mEmptyTextView;
    private Button mRetry_btn;
    public boolean isBrowseCourse = false;
    public static final int CONTENT_DELETED_CALL_BACK = 116;
    private Handler mHandler = null;
    private String mCourseId = "", courseName = "";
    private ContentsResponse mContentsResponse;
    private ProgressDialog mProgressDialog = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView contentRecycle;
    private ContentsList mContentsList;
    private ContentsAdapter mContentsAdapter;
    private RealmList<Sections> mSectionList = new RealmList<Sections>();
    private String queryTextChange = "";
    private boolean isLoading = false;
    private Realm myRealm;
    private OnUpdateMenuListner mOnUpdateMenuListner;
    private View rootView;

    private ImageView ivMsg; //@Chirag 06/08/2018

    // Another constructor function, enable to pass them arguments.
    public static ContentsFragment newInstance(String courseId, String courseName) {
        ContentsFragment fragment = new ContentsFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("course_id", courseId);
        args.putString("course_name", courseName);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(this.getClass().getSimpleName()+".realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        myRealm = Realm.getInstance(realmConfiguration);*/
        myRealm = Realm.getInstance(Helper.createRealmObj());

        if (getArguments() != null) {
            this.mCourseId = getArguments().getString("course_id");
            this.courseName = getArguments().getString("course_name");
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;

                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (msg.obj instanceof ContentsResponse) {
                            mContentsResponse = (ContentsResponse) msg.obj;
//                            Log.d("Conn", "onSuccess status : " + mContentsResponse.getData().getService().getStatus());
//                            Log.d("Conn", "onSuccess msg : " + mContentsResponse.getData().getService().getMessage());
                            if (mContentsResponse.getData().getService().getStatus() == 0) {
                                //mEmptyTextView.setText(mContentsResponse.getData().getService().getMessage());
                                ivMsg.setVisibility(View.VISIBLE);
                                Glide.with(getActivity())
                                        .load(mContentsResponse.getData().getService().getMessage())
                                        .into(ivMsg);
                            } else {
                                ivMsg.setVisibility(View.GONE);
                                updateContentsList(mContentsResponse);
                            }
                            try {
                                ((CourseDetailsActivity) getActivity()).rippleAnimationShowHide();
                            } catch (Exception e) {
                                LogWriter.err(e);
                            }
                        }
                        break;
                    case Flinnt.REFRESH_LIST:

                        //Log.d("Conn","handleMessage().refreshList");
                        try {
                            mContentsAdapter.clearData();
                            if (contentRecycle != null && !contentRecycle.isComputingLayout()) {
                                mContentsAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        break;

                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + msg.obj.toString());

                            //Log.d("Conn", "FAILURE_RESPONSE : " + msg.obj.toString());
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_fragment, container, false);

        //Log.d("Contt", "onCreatedView()");
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_content);
        ivMsg = rootView.findViewById(R.id.iv_msg);
        mRetry_btn = (Button) rootView.findViewById(retry_btn);
        mRetry_btn.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        contentRecycle = (RecyclerView) rootView.findViewById(R.id.content_recycle);

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

        refreshView();
        localContent(mCourseId);
        int sectionSize = 0;
        try {
            sectionSize = mSectionList.size();
        } catch (Exception e) {
            LogWriter.err(e);
        }

        //Log.d("Conn","sectioSize : " + sectionSize);
        if (sectionSize >= 1) {

            mContentsAdapter.addItems(mSectionList);
            if (Helper.isConnected()) {

                //@Chirag  02/08/2018
                if (mContentsAdapter.getItemCount() == 0) {

                    mEmptyTextView.setText(getActivity().getResources().getString(R.string.no_content_learner));
                }
            } else {
                mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
            }

            ((CourseDetailsActivity) getActivity()).contentItemCount = mSectionList.size();

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mContentsAdapter.getItemCount());

            mEmptyTextView.setVisibility((mContentsAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);

            mContentsList = new ContentsList(mHandler, mCourseId);
            mContentsList.setSearchString("");
            mContentsList.sendContentsListRequest();
            isLoading = true;
        } else {

            mContentsList = new ContentsList(mHandler, mCourseId);
            mContentsList.setSearchString("");
            mContentsList.sendContentsListRequest();
            startProgressDialog();
            isLoading = true;
        }

        showRetryButton();
        return rootView;
    }

    private void showRetryButton() {
        //Log.d("Contee", "showRetryButton");
        if (mContentsAdapter.getItemCount() == 0 && mEmptyTextView.isShown() && mEmptyTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.no_internet_msg))) {
            mRetry_btn.setVisibility(View.VISIBLE);
        } else {
            mRetry_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void refreshView() {

        try {
            mContentsAdapter = new ContentsAdapter(getActivity(), ContentsFragment.this, mCourseId, courseName, mSectionList);
            GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
            contentRecycle.setLayoutManager(manager);
//            mContentsAdapter.setLayoutManager(manager);
            contentRecycle.setAdapter(mContentsAdapter);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Clears the data and make new request
     */
    private void refreshList() {

        if (TextUtils.isEmpty(queryTextChange)) {
            Requester.getInstance().cancelPendingRequests(ContentsList.TAG);
            mContentsAdapter.clearData();

            refreshView();

            mContentsList = new ContentsList(mHandler, mCourseId);
            mContentsList.setSearchString("");
            mContentsList.sendContentsListRequest();
        } else {
            if (null == mContentsList) {
                mContentsList = new ContentsList(mHandler, mCourseId);
            }
//            mBrowseCoursesAdapter.removeFilter();
            mContentsAdapter.clearData();
            mContentsList.mContentsRequest = null;
            mContentsList.setSearchString(queryTextChange);
            mContentsList.sendContentsListRequest();
        }
    }

    public void refreshListFromActivity() {

        mContentsAdapter.clearData();

        refreshView();

        mContentsList = new ContentsList(mHandler, mCourseId);
        mContentsList.setSearchString("");
        mContentsList.sendContentsListRequest();
        startProgressDialog();
    }


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

        //Log.d("Conn", "updateContentsList : " + mContentsResponse.getData().toString());
        try {

            if (!mContentsList.getSearchString().isEmpty()) { // only if submit search
                if (!mContentsAdapter.getSearchMode()) {
                    mContentsAdapter.setSearchMode(true);
                }
                mContentsAdapter.addSearchedItems(mContentsResponse.getData().getList());
                mEmptyTextView.setText(getResources().getString(R.string.no_content_found));
            } else {

                if (mContentsResponse.getData().getList().size() > 0) {
                    mContentsAdapter.addItems(mContentsResponse.getData().getList());

                    if (Helper.isConnected()) {

                        if (mContentsResponse.getData().getService().getStatus() == 0) {
                            //mEmptyTextView.setText(mContentsResponse.getData().getService().getMessage());
                            //mEmptyTextView.setVisibility(View.GONE);
                            ivMsg.setVisibility(View.VISIBLE);
                            Glide.with(getActivity())
                                    .load(mContentsResponse.getData().getService().getMessage())
                                    .into(ivMsg);
                        }
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }

                }
                ((CourseDetailsActivity) getActivity()).contentItemCount = mContentsResponse.getData().getList().size();
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mContentsAdapter.getItemCount());

            if (mContentsAdapter.getItemCount() == 0) {
                if (mContentsResponse.getData().getService().getMessage().length() > 0) {
                    mEmptyTextView.setVisibility(View.GONE);
                    ivMsg.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(mContentsResponse.getData().getService().getMessage())
                            .into(ivMsg);
                } else {
                    ivMsg.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setText(getActivity().getResources().getString(R.string.no_content_learner));
                }

            }
            showRetryButton();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        //*******change v2.0.27
        mContentsAdapter.notifyDataSetChanged();
        try {
            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case CONTENT_DELETED_CALL_BACK:
                        mContentsAdapter.remove();
                        mContentsAdapter.notifyDataSetChanged();

                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mRetry_btn) {
            if (!Helper.isConnected()) {
                Helper.showNetworkAlertMessage(getActivity());
            } else {
                this.mOnUpdateMenuListner = (OnUpdateMenuListner) getActivity();
                mOnUpdateMenuListner.onUpdateMenuBanner();
                startProgressDialog();
                refreshList();
            }
        }
    }

    public void onBackPressed() {
        isBrowseCourse = true;
        getActivity().onBackPressed();
    }

    @Override
    public void onSearch(String query, Boolean isSubmit) {
        try {


            if (LogWriter.isValidLevel(Log.DEBUG))
                LogWriter.write("mContentsAdapter.getItemCount() : " + mContentsAdapter.getItemCount());

            queryTextChange = query;
            if (TextUtils.isEmpty(query)) {
                mContentsAdapter.removeFilter();
                if (mContentsAdapter.getItemCount() == 0 && !isLoading) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    if (Helper.isConnected()) {
                        if (mContentsResponse.getData().getService().getStatus() == 0) {
                            mEmptyTextView.setText(mContentsResponse.getData().getService().getMessage());
                        } else {
                            mEmptyTextView.setText(getActivity().getResources().getString(R.string.no_content_learner));
                        }
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }

                } else {
                    //mEmptyTextView.setVisibility(View.GONE);
                }
            } else {
                if (isSubmit) {
                    if (Helper.isConnected()) {
                        if (mContentsAdapter.getItemCount() == 0) {
                            mEmptyTextView.setVisibility(View.VISIBLE);
                            mEmptyTextView.setText(getResources().getString(R.string.no_content_found));
                        } else {
                            mEmptyTextView.setVisibility(View.GONE);
                        }
                        if (null == mContentsList) {
                            mContentsList = new ContentsList(mHandler, mCourseId);
                        }
                        mContentsAdapter.clearData();
                        mContentsList.mContentsRequest = null;
                        mContentsList.setSearchString(query);
                        mContentsList.sendContentsListRequest();
                        startProgressDialog();
                        //Helper.hideKeyboardFromWindow(MyCoursesActivity.this);
                    } else {
                        if (TextUtils.isEmpty(query)) {
                            mContentsAdapter.removeFilter();
                        } else {
                            mContentsAdapter.setFilter(query);
                        }
                    }
                } else {
                    mContentsAdapter.setFilter(query);
                    if (mContentsAdapter.getItemCount() == 0) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mEmptyTextView.setText(getResources().getString(R.string.no_content_found));
                    } else {
                        mEmptyTextView.setVisibility(View.GONE);
                    }
                }
            }
            showRetryButton();
        } catch (Exception e) {
            //Log.d("Contee", "exception : " + e.getMessage());
            LogWriter.err(e);
        }
    }

    @Override
    public void onOffsetChanged(int i) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(i == 0);
            //refreshList();
        }
    }

    public void localContent(String courseId) {
        try {
            RealmResults results = myRealm.where(Data.class).equalTo("courseID", courseId).equalTo("userID", Config.getStringValue(Config.USER_ID)).findAll();
            mSectionList = new RealmList<Sections>();
            for (int i = 0; i < results.size(); i++) {
                Data data = (Data) results.get(i);
                mSectionList.addAll(data.getList());
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MyCommFun.sendTracker(getActivity(), "activity=" + Flinnt.CONTENT_LIST + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseId);
            GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Common.IS_NETWORK_TOAST = true;
        try {
            GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
}