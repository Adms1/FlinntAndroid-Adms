
package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.BrowseCourseCategoryAdapter;
import com.edu.flinnt.adapter.FilterExpandAdapter;
import com.edu.flinnt.core.BrowseCourses;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.expandableRecylerview.model.FilterModel;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.protocol.CategoryDataModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;


public class BrowseCoursesFragment extends Fragment implements MyCoursesActivity.OnSearchListener, MyCoursesActivity.AppBarLayoutSwitchListener {

    private Runnable runnable;
    public Handler mHandler = null;
    private String TAGS = "Brr";
    private RecyclerView mRecyclerView;
    private  BrowseCourseCategoryAdapter mBrowseCourseCategoryAdapter;
    private ArrayList<CategoryDataModel> mCategoryDataModel = new ArrayList<>();
    private ArrayList<StoreModelResponse.Datum> storeDataList = new ArrayList<StoreModelResponse.Datum>();

    private TextView mEmptyTextView;
    private Button retryBtn;
    private BrowseCourses mBrowseCourses;
    private ProgressDialog mProgressDialog = null;
    private ImageLoader mImageLoader;
    public static String coursePictureURLstatic = "https://flinnt1.s3.amazonaws.com/courses/";
    private SwipeRefreshLayout swipeRefreshLayout;
    private String queryTextChange = "";
    private boolean isLoading = false;
    private boolean isCourseLoading = false;
    public static final int WEBVIEW_CALL_API_THEN_LOAD_URL = 1;
    public static final int WEBVIEW_REFRESH_RECEIVED_URL = 2;
    public static int cart_count ;




    public static BrowseCoursesFragment newInstance(/*String userId*/) {
        BrowseCoursesFragment fragment = new BrowseCoursesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        mImageLoader = Requester.getInstance().getImageLoader();
        View rootView = inflater.inflate(R.layout.browse_courses_fragment,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_browse_courses);
        retryBtn = (Button) rootView.findViewById(R.id.retry_btn);

        retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                } else {
                    refreshList();
                }
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_courses_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            mRecyclerView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

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
        return rootView;
    }

    private void refreshView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBrowseCourseCategoryAdapter = new BrowseCourseCategoryAdapter(getActivity(),storeDataList,1);
        mRecyclerView.setAdapter(mBrowseCourseCategoryAdapter);
    }


    public void createCategoryData(ArrayList<BrowsableCourse> mCourseList) {
        if (mCourseList.size() > 0) {
            CategoryDataModel dm = new CategoryDataModel();
            ArrayList<BrowsableCourse> singleItem = null;
            for (int i = 0; i < mCourseList.size(); i++) {
                if (!dm.getCategoryId().equals(mCourseList.get(i).getCategoryId())) {
                    dm = new CategoryDataModel();
                    singleItem = new ArrayList<BrowsableCourse>();
                    singleItem.add(mCourseList.get(i));
                    dm.setCategoryId(mCourseList.get(i).getCategoryId());
                    dm.setCategoryTitle(mCourseList.get(i).getCategoryName());
                    dm.setAllItemsInSection(singleItem);
                    mCategoryDataModel.add(dm);
                } else {
                    singleItem.add(mCourseList.get(i));
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAGS, "onCreate");

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
//                Log.d(TAGS, String.valueOf(message.what));
//                Log.d(TAGS, message.obj.toString());

                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        try {
                            //Log.d("Brr", "onSucccess(). ");
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " +message.obj.toString());
                            if (message.obj instanceof StoreModelResponse) {
                               // updateCourseList((StoreModelResponse) message.obj);
                                //08-01-19 by vijay
                                updateCourseListNew((StoreModelResponse) message.obj);

                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                            //Log.d(TAGS, e.getMessage());
                        }

                        break;

                    case Flinnt.FAILURE:
                        //Log.d("Brr", "onFailure(). : " + message.obj.toString());
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
        loadCourses();
    }

    @Override
    public void onResume() {
        super.onResume();
        //@Nikhil 06072018

//        mBrowseCourses = new BrowseCourses(mHandler);
//        mBrowseCourses.setSearchString("");
//        mBrowseCourses.sendBrowseCoursesRequest();
//        isLoading = true;
//        startProgressDialog();

        //@Chirag 02/08/2018
        if (isCourseLoading){
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //@Chirag 02/08/2018
        isCourseLoading = false;
    }

    private void loadCourses(){

        isCourseLoading = true;
        if (null == mBrowseCourses) {

            //Log.d(TAGS, "sendCourseBrowseRequest..");

            mBrowseCourses = new BrowseCourses(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
            isLoading = true;

        }else {

            //Log.d(TAGS, "mBrowseCourses not null..");
            Requester.getInstance().cancelPendingRequests(BrowseCourses.TAG);
            mBrowseCourseCategoryAdapter.clearData();
            mCategoryDataModel.clear();

            refreshView();
            mBrowseCourses = new BrowseCourses(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
            isLoading = true;

        }

    }

    /**
     * Update and display course list
     *
     * @param mBrowseCoursesResponse cource course list response
     */


//    public void updateCourseList(StoreModelResponse mBrowseCoursesResponse) {
//        try {
//            coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
//            if (!mBrowseCourses.getSearchString().isEmpty()) {
//                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
//            } else {
//                mCategoryDataModel = new ArrayList<>();
//                createCategoryData(mBrowseCoursesResponse.get);
//                mBrowseCourseCategoryAdapter.addItems(mCategoryDataModel);
//                if (Helper.isConnected()) {
//                    mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
//                } else {
//                    mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
//                }
//            }
//            if (LogWriter.isValidLevel(Log.INFO))
//                LogWriter.write("ItemCount : " + mBrowseCourseCategoryAdapter.getItemCount());
//
//            mEmptyTextView.setVisibility((mBrowseCourseCategoryAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
//
//            showRetryButton();
//        } catch (Exception e) {
//            LogWriter.err(e);
//        }
//    }

    //08-01-2019 by vijay
    public void updateCourseListNew(StoreModelResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            if (!mBrowseCourses.getSearchString().isEmpty()) {
                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
            } else {
                storeDataList = new ArrayList<>();
                storeDataList.addAll(mBrowseCoursesResponse.getData());
                cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
                mBrowseCourseCategoryAdapter.clearData();
                mBrowseCourseCategoryAdapter.addItems(storeDataList);
                if (Helper.isConnected()) {
                    mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                } else {
                    mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                }
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " +mBrowseCourseCategoryAdapter.getItemCount());
            mEmptyTextView.setVisibility((mBrowseCourseCategoryAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
            showRetryButton();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void refreshList() {

        if (TextUtils.isEmpty(queryTextChange)) {
            Requester.getInstance().cancelPendingRequests(BrowseCourses.TAG);
            mBrowseCourseCategoryAdapter.clearData();
            mCategoryDataModel.clear();

            refreshView();
            mBrowseCourses = new BrowseCourses(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
        } else {
            if (null == mBrowseCourses) {
                mBrowseCourses = new BrowseCourses(mHandler);
            }
            mBrowseCourseCategoryAdapter.clearData();
            mBrowseCourses.mBrowseCoursesRequest = null;
            mBrowseCourses.setSearchString("");
            mBrowseCourses.setOffset(0);
            mBrowseCourses.sendBrowseCoursesRequest();
        }
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "BrowseCourse", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public void onSearch(String query, Boolean isSubmit) {

        try {
            queryTextChange = query;
            if (TextUtils.isEmpty(query)) {

                if (mBrowseCourseCategoryAdapter.getItemCount() == 0 && !isLoading) {

                    mEmptyTextView.setVisibility(View.VISIBLE);
                    if (Helper.isConnected()) {
                        //Log.d("Brr", "mCategoryModel : " + mCategoryDataModel.size() + "\tonSearch : " + "query : " + query + "\tisSubmit : " + String.valueOf(isSubmit));
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }
            } else {
                if (isSubmit) {
                    if (Helper.isConnected()) {
                        Requester.getInstance().cancelPendingRequests(BrowseCourses.TAG);
                        Intent searchIntent = new Intent(getActivity(),BrowseCourseSearchActivity.class);
                        searchIntent.putExtra("searchText", query);
                        startActivity(searchIntent);
                    }
                } else {
                    if (mBrowseCourseCategoryAdapter.getItemCount() == 0) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                    } else {
                        mEmptyTextView.setVisibility(View.GONE);
                    }
                }
            }
            showRetryButton();
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    private void showRetryButton() {
        if (mBrowseCourseCategoryAdapter.getItemCount() == 0 && mEmptyTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.no_internet_msg))) {
            retryBtn.setVisibility(View.VISIBLE);
        } else {
            retryBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOffsetChanged(int i) {

        //Log.d("Brr", "onOffsetChanged-progressbar : " + mProgressDialog.isShowing());
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(getActivity(), "activity=" + Flinnt.BROWSE_COURSE + "user=" + Config.getStringValue(Config.USER_ID));
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