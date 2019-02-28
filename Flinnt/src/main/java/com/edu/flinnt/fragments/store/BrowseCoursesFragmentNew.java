
package com.edu.flinnt.fragments.store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.BrowseCourseCategoryAdapterNew;
import com.edu.flinnt.adapter.store.FilterMultiTypeAdapter;
import com.edu.flinnt.core.store.BrowseCoursesNew;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.store.FilterDataRequest;
import com.edu.flinnt.customviews.store.CustomAutoCompleteTextView;
import com.edu.flinnt.customviews.store.expandableRecylerview.model.FilterModel;
import com.edu.flinnt.customviews.store.expandableRecylerview.model.GroupDataModel;
import com.edu.flinnt.gui.BrowseCourseSearchActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.store.StoreBookSetActivity;
import com.edu.flinnt.helper.listner.store.DrawableClickListener;
import com.edu.flinnt.helper.listner.store.FilterListener;
import com.edu.flinnt.models.store.FilterDataModel;
import com.edu.flinnt.models.store.FilterDataResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesRequest;
import com.edu.flinnt.protocol.CategoryDataModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.store.StoreConstants;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class BrowseCoursesFragmentNew extends Fragment implements MyCoursesActivity.OnSearchListener, MyCoursesActivity.AppBarLayoutSwitchListener,FilterListener {
    //MaterialSearchView.OnQueryTextListener

    private Runnable runnable;
    public Handler mHandler = null;
    private String TAGS = "Brr";
    private RecyclerView mRecyclerView;
    private BrowseCourseCategoryAdapterNew mBrowseCourseCategoryAdapter;
    private ArrayList<CategoryDataModel> mCategoryDataModel = new ArrayList<>();
    private ArrayList<StoreModelResponse.Datum> storeDataList = new ArrayList<StoreModelResponse.Datum>();
    private ArrayList<FilterDataResponse.Datum> filterDataList = new ArrayList<FilterDataResponse.Datum>();

    private TextView mEmptyTextView;
    private Button retryBtn;
    private BrowseCoursesNew mBrowseCourses;
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
    private CustomAutoCompleteTextView edtSearch;
   // private MaterialSearchView searchHolder;
    private LinearLayout ll_filter,ll;
    private RelativeLayout rlFilterBottomContainer;
    private FilterDataRequest filterDataRequest;
    private RecyclerView rvFilterDataList;
    private ProgressBar filterProgressBar;
    private FilterListener filterListener;
    private BottomSheetDialog bottomSheetDialog;
    private ImageView btnFilterDialog;
    private StoreModelResponse mBrowseCoursesResponse;


    public static BrowseCoursesFragmentNew newInstance(/*String userId*/) {
        BrowseCoursesFragmentNew fragment = new BrowseCoursesFragmentNew();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        mImageLoader = Requester.getInstance().getImageLoader();


        View rootView = inflater.inflate(R.layout.browse_courses_fragment,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_browse_courses);
        retryBtn = (Button) rootView.findViewById(R.id.retry_btn);

        edtSearch = (CustomAutoCompleteTextView)getActivity().findViewById(R.id.edt_search_store_box);
        rlFilterBottomContainer = (RelativeLayout)rootView.findViewById(R.id.ll_footer);

        ll_filter = (LinearLayout)rootView.findViewById(R.id.ll_filter);

        filterListener = (FilterListener)this;

        rootView.findViewById(R.id.ll_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        rootView.findViewById(R.id.ll_alpha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),StoreBookSetActivity.class));
            }
        });
        rootView.findViewById(R.id.ll_rating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
       // searchHolder = (MaterialSearchView)getActivity().findViewById(R.id.material_searchview);

       // searchHolder.setVisibility(View.VISIBLE);

//        edtSearch.setVisibility(View.GONE);


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

        ll_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.setContentView(R.layout.dialog_filter_book_store);

                ImageView closeImage = (ImageView)bottomSheetDialog.findViewById(R.id.close_image);
                filterProgressBar = (ProgressBar)bottomSheetDialog.findViewById(R.id.progressbar);
                rvFilterDataList = (RecyclerView)bottomSheetDialog.findViewById(R.id.rv_filter_list);
                btnFilterDialog = (ImageView)bottomSheetDialog.findViewById(R.id.btn_apply_filter);
                btnFilterDialog.setVisibility(View.GONE);
                filterProgressBar.setVisibility(View.VISIBLE);
                rvFilterDataList.setVisibility(View.GONE);

                filterDataRequest = new FilterDataRequest(mHandler);
                filterDataRequest.sendFilterDataRequest();

                bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                bottomSheetDialog.getWindow().setGravity(Gravity.RIGHT|Gravity.LEFT);
                bottomSheetDialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
                WindowManager.LayoutParams layoutParams = bottomSheetDialog.getWindow().getAttributes();
                layoutParams.x = 100; // left margin
                layoutParams.y = 100; // right margin
                bottomSheetDialog.getWindow().setAttributes(layoutParams);


                closeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                btnFilterDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(StoreConstants.Category_Tree_id) || !TextUtils.isEmpty(StoreConstants.Lang_ids) || !TextUtils.isEmpty(StoreConstants.Formats) || !TextUtils.isEmpty(StoreConstants.Author_ids) || !TextUtils.isEmpty(StoreConstants.Price_max) || !TextUtils.isEmpty(StoreConstants.Price_min) || !TextUtils.isEmpty(StoreConstants.Formats)){
                            bottomSheetDialog.dismiss();
                            startProgressDialog();
                            filterCourses();
                        }else {
                            Helper.showAlertMessage(getActivity(),"Message","Please select values","Close");
                        }
                    }
                });


                bottomSheetDialog.show();

            }
        });



        refreshView();
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {

        }
    }


    private void refreshView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBrowseCourseCategoryAdapter = new BrowseCourseCategoryAdapterNew(getActivity(),storeDataList,1);
        mRecyclerView.setAdapter(mBrowseCourseCategoryAdapter);

//       searchHolder.setSearchRecyclerAdapter(mBrowseCourseCategoryAdapter);
//
//       searchHolder.addQueryTextListener(this);
    }

    private void refreshViewWithFilter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBrowseCourseCategoryAdapter = new BrowseCourseCategoryAdapterNew(getActivity(),filterDataList,1);
        mRecyclerView.setAdapter(mBrowseCourseCategoryAdapter);

//       searchHolder.setSearchRecyclerAdapter(mBrowseCourseCategoryAdapter);
//
//       searchHolder.addQueryTextListener(this);
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
                                mBrowseCoursesResponse = new StoreModelResponse();
                                mBrowseCoursesResponse = (StoreModelResponse) message.obj;
                                updateCourseListNew(mBrowseCoursesResponse);

                            }else if (message.obj instanceof FilterDataModel) {
                                filterProgressBar.setVisibility(View.GONE);
                                rvFilterDataList.setVisibility(View.VISIBLE);
                                FilterDataModel filterDataModel = (FilterDataModel) message.obj;

                                if (filterDataModel.getStatus() == Flinnt.TRUE) {


                                    ArrayList<FilterModel> dataHeader = new ArrayList<FilterModel>();


                                    //languages
                                    ArrayList<GroupDataModel> groupdatas1 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataModel = new GroupDataModel();
                                    groupDataModel.setTitle("Languages");
                                    groupDataModel.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
                                    groupDataModel.setList(filterDataModel.getData().getLanguages());
                                    groupdatas1.add(groupDataModel);
                                    FilterModel filterModel = new FilterModel("English & Indian Languages", groupdatas1);
                                    dataHeader.add(filterModel);


                                    //Authors
                                    ArrayList<GroupDataModel> groupdatas2 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataMode2 = new GroupDataModel();
                                    groupDataMode2.setTitle("Author");
                                    groupDataMode2.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
                                    groupDataMode2.setList(filterDataModel.getData().getAuthors());
                                    groupdatas2.add(groupDataMode2);
                                    FilterModel filterModel2 = new FilterModel("Author",groupdatas2);
                                    dataHeader.add(filterModel2);


                                    //Authors
                                    ArrayList<GroupDataModel> groupdatas3 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataMode3 = new GroupDataModel();
                                    groupDataMode3.setTitle("Board");
                                    groupDataMode3.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
                                    groupDataMode3.setList(filterDataModel.getData().getBoards());
                                    groupdatas3.add(groupDataMode3);
                                    FilterModel filterModel3 = new FilterModel("Board", groupdatas3);
                                    dataHeader.add(filterModel3);


                                    //Categories
                                    ArrayList<GroupDataModel> groupdatas4 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataMode4 = new GroupDataModel();
                                    groupDataMode4.setTitle("Categories");
                                    groupDataMode4.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
                                    groupDataMode4.setList(filterDataModel.getData().getCategories());
                                    groupdatas4.add(groupDataMode4);
                                    FilterModel filterModel4 = new FilterModel("Categories", groupdatas4);
                                    dataHeader.add(filterModel4);


                                    //publishers
//                                    ArrayList<GroupDataModel> groupdatas5 = new ArrayList<GroupDataModel>();
//                                    GroupDataModel groupDataModel5 = new GroupDataModel();
//                                    groupDataModel5.setTitle("Publisher");
//                                    groupDataModel5.setList(filterDataModel.getData().getPublishers());
//                                    groupDataModel5.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
//                                    groupdatas5.add(groupDataModel5);
//                                    FilterModel filterMode5 = new FilterModel("Publisher", groupdatas5);
//                                    dataHeader.add(filterMode5);

                                    ArrayList<GroupDataModel> groupdatas51 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataModel51 = new GroupDataModel();
                                    groupDataModel51.setTitle("Format");


                                    try {

                                        Gson gson = new Gson();

                                        String data = gson.toJson(filterDataModel.getData().getFormat(), FilterDataModel.Format.class);

                                        JSONObject formatData = new JSONObject(data);

                                        Iterator<String> dataKeys = formatData.keys();
                                        ArrayList<FilterDataModel.BookFormatDataModel> formatValues = new ArrayList<>();


                                        while (dataKeys.hasNext()) {

                                            FilterDataModel.BookFormatDataModel formatDataRef = new FilterDataModel.BookFormatDataModel();

                                            String key = dataKeys.next();
                                            String value = formatData.optString(key);
                                            formatDataRef.setKey(key);
                                            formatDataRef.setValue(value);
                                            formatValues.add(formatDataRef);

                                        }
                                        groupDataModel51.setTitle("Format");
                                        groupDataModel51.setList(formatValues);
                                        groupDataModel51.setChildViewType(FilterMultiTypeAdapter.CHECK_ITEM_VIEW);
                                        groupdatas51.add(groupDataModel51);
                                        FilterModel filterMode51 = new FilterModel("Format",groupdatas51);
                                        dataHeader.add(filterMode51);

                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                    //price
                                    ArrayList<GroupDataModel> groupdatas6 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataModel6 = new GroupDataModel();
                                    groupDataModel6.setTitle("Price");
                                    groupDataModel6.setList(null);
                                    groupDataModel6.setChildViewType(FilterMultiTypeAdapter.PRICE_ITEM_VIEW);
                                    groupdatas6.add(groupDataModel6);
                                    FilterModel filterMode6 = new FilterModel("Price", groupdatas6);
                                    dataHeader.add(filterMode6);


                                    //Avg. cust reviews.
                                    ArrayList<GroupDataModel> groupdatas7 = new ArrayList<GroupDataModel>();
                                    GroupDataModel groupDataModel7 = new GroupDataModel();
                                    groupDataModel7.setTitle("Avg. Customers Review");
                                    groupDataModel7.setList(null);
                                    groupDataModel7.setChildViewType(FilterMultiTypeAdapter.AVG_REVIEW_ITEM_VIEW);
                                    groupdatas7.add(groupDataModel7);
                                    FilterModel filterMode7 = new FilterModel("Avg. Customers Review", groupdatas7);
                                    dataHeader.add(filterMode7);


                                    FilterMultiTypeAdapter filterExpandAdapter = new FilterMultiTypeAdapter(getActivity(),dataHeader,filterListener);
                                    rvFilterDataList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    RecyclerView.ItemAnimator animator = rvFilterDataList.getItemAnimator();
                                    rvFilterDataList.setItemAnimator(animator);
                                    rvFilterDataList.setAdapter(filterExpandAdapter);

                                }
                            } else if(message.arg1 == 200) {
                                stopProgressDialog();
                                storeDataList.clear();
                                mBrowseCourseCategoryAdapter.clearData();
                                updateCourseListFilter((StoreModelResponse) message.obj);
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

//        mBrowseCourses = new BrowseCoursesNew(mHandler);
//        mBrowseCourses.setSearchString("");
//        mBrowseCourses.sendBrowseCoursesRequest();
//        isLoading = true;
//        startProgressDialog();

        //@Chirag 02/08/2018



        if (isCourseLoading){
            swipeRefreshLayout.setRefreshing(true);
        }
        //@vijay 07/02/2019


    }

    private void setSearchAutoCompleteData(){
        ArrayList<String> bookNames  = new ArrayList<String>();

        if(storeDataList != null){
            for(int count =0 ;count<storeDataList.size();count++){

                for(int subcount = 0 ;subcount<storeDataList.get(count).getCourses().size();subcount++){

                    bookNames.add(storeDataList.get(count).getCourses().get(subcount).getBookName());
                }

            }
        }

        edtSearch.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.search_item_layout,bookNames));
        //  edtSearch.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Helper.hideKeyboardFromWindow(getActivity());
                mBrowseCourseCategoryAdapter.getFilter().filter(edtSearch.getText().toString());
                //mBrowseCourseCategoryAdapter.setFilter(edtSearch.getText().toString());
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() > 0) {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_close_material, 0);
                }else{
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_blue,0,0, 0);
                }
                //mBrowseCourseCategoryAdapter.getFilter().filter(editable.toString());
            }
        });

        edtSearch.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        edtSearch.setText("");
                      //  mBrowseCourseCategoryAdapter.getFilter().filter("");
                        //clearCourseListNew(mBrowseCoursesResponse);
                        loadCourses();
                        break;

                    default:
                        break;
                }
            }

        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Helper.hideKeyboardFromWindow(getActivity());
                    mBrowseCourseCategoryAdapter.getFilter().filter(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
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

            mBrowseCourses = new BrowseCoursesNew(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
            isLoading = true;

        }else {

            //Log.d(TAGS, "mBrowseCourses not null..");
            Requester.getInstance().cancelPendingRequests(BrowseCoursesNew.TAG);
            mBrowseCourseCategoryAdapter.clearData();
            mCategoryDataModel.clear();

            refreshView();

            mBrowseCourses = new BrowseCoursesNew(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
            isLoading = true;

        }

    }


    private void filterCourses(){

        isCourseLoading = true;


        //Log.d(TAGS, "mBrowseCourses not null..");
        Requester.getInstance().cancelPendingRequests(BrowseCoursesNew.TAG);
        mBrowseCourses = new BrowseCoursesNew(mHandler);
        mBrowseCourses.setSearchString("");
        mBrowseCourses.sendFilterBrowseCoursesRequest();
        isLoading = true;



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
                if(storeDataList.size() > 0) {
                    cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
                    mBrowseCourseCategoryAdapter.clearData();
                    mBrowseCourseCategoryAdapter.addItems(storeDataList);
                    rlFilterBottomContainer.setVisibility(View.VISIBLE);


                    if (Helper.isConnected()) {
                       mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                       // mEmptyTextView.setText(getResources().getString(R.string.no_course_found));

                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }else{
                    mBrowseCourseCategoryAdapter.clearData();
                    rlFilterBottomContainer.setVisibility(View.GONE);

                    if (Helper.isConnected()) {

                       // mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " +mBrowseCourseCategoryAdapter.getItemCount());
            mEmptyTextView.setVisibility((mBrowseCourseCategoryAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
            showRetryButton();
            setSearchAutoCompleteData();

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }



    public void updateCourseListFilter(StoreModelResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            if (!mBrowseCourses.getSearchString().isEmpty()) {
                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
            } else {
                storeDataList = new ArrayList<>();
                storeDataList.addAll(mBrowseCoursesResponse.getData());
                if(storeDataList.size() > 0) {
                    cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
                    mBrowseCourseCategoryAdapter.clearData();
                    mBrowseCourseCategoryAdapter.addItems(storeDataList);
                    rlFilterBottomContainer.setVisibility(View.VISIBLE);


                    if (Helper.isConnected()) {
                        //mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                        mEmptyTextView.setText("Sorry did not match any products.");

                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }else{
                    mBrowseCourseCategoryAdapter.clearData();
                    rlFilterBottomContainer.setVisibility(View.GONE);

                    if (Helper.isConnected()) {

                        // mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                        mEmptyTextView.setText("Sorry did not match any products.");
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " +mBrowseCourseCategoryAdapter.getItemCount());
            mEmptyTextView.setVisibility((mBrowseCourseCategoryAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
            showRetryButton();
            setSearchAutoCompleteData();

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public void clearCourseListNew(StoreModelResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            if (!mBrowseCourses.getSearchString().isEmpty()) {
                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
            } else {
                storeDataList = new ArrayList<>();
                storeDataList.addAll(mBrowseCoursesResponse.getData());
                if(storeDataList.size() > 0) {
                    cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
                    mBrowseCourseCategoryAdapter.clearData();
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mBrowseCourseCategoryAdapter = new BrowseCourseCategoryAdapterNew(getActivity(),storeDataList,1);
                    mRecyclerView.setAdapter(mBrowseCourseCategoryAdapter);

                    if (Helper.isConnected()) {
                        //s  mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_found));

                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }else{
                    mBrowseCourseCategoryAdapter.clearData();
                    if (Helper.isConnected()) {
                        // mEmptyTextView.setText(getResources().getString(R.string.no_course_found_message));
                        mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
                    } else {
                        mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " +mBrowseCourseCategoryAdapter.getItemCount());
            mEmptyTextView.setVisibility((mBrowseCourseCategoryAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
            showRetryButton();
            setSearchAutoCompleteData();

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public void filterCourseListNew(FilterDataResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            if (!mBrowseCourses.getSearchString().isEmpty()) {
                mEmptyTextView.setText(getResources().getString(R.string.no_course_found));
            } else {
                filterDataList = new ArrayList<>();
                filterDataList.addAll(mBrowseCoursesResponse.getData());
                refreshViewWithFilter();
                cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
                mBrowseCourseCategoryAdapter.clearData();
                mBrowseCourseCategoryAdapter.addItems(filterDataList);
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
            Requester.getInstance().cancelPendingRequests(BrowseCoursesNew.TAG);
            mBrowseCourseCategoryAdapter.clearData();
            mCategoryDataModel.clear();

            refreshView();
            mBrowseCourses = new BrowseCoursesNew(mHandler);
            mBrowseCourses.setSearchString("");
            mBrowseCourses.sendBrowseCoursesRequest();
        } else {
            if (null == mBrowseCourses) {
                mBrowseCourses = new BrowseCoursesNew(mHandler);
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
    public void onSearch(String query,Boolean isSubmit) {

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
                        Requester.getInstance().cancelPendingRequests(BrowseCoursesNew.TAG);
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

    @Override
    public void onFilterValuesChanged(FilterType type,HashMap<String, String> data) {
        switch (type){
            case AUTHOR:
                StoreConstants.Author_ids = "";
                StringBuilder nameBuilder = new StringBuilder();
                if(data.size() > 0) {
                    btnFilterDialog.setVisibility(View.VISIBLE);

                    for (String id : data.values()) {
                        nameBuilder.append(id).append(",");
                        // can also do the following
                    }
                    nameBuilder.deleteCharAt(nameBuilder.length() - 1);
                    StoreConstants.Author_ids = nameBuilder.toString();
                    // filterCourses();

                }

                break;
            case PRICE:
                StoreConstants.Price_max = "";
                StoreConstants.Price_max = "";
                StoreConstants.Price_max = data.get(BrowseCoursesRequest.STORE_FILTER_PRICE_MAX_KEY);
                StoreConstants.Price_min = data.get(BrowseCoursesRequest.STORE_FILTER_PRICE_MIN_KEY);
                //filterCourses();
                break;
            case LANG:
                StoreConstants.Lang_ids = "";
                StringBuilder nameBuilder1 = new StringBuilder();

                if(data.size() > 0) {

                    btnFilterDialog.setVisibility(View.VISIBLE);

                    for (String id : data.values()) {
                        nameBuilder1.append(id).append(",");
                        // can also do the following
                    }
                    nameBuilder1.deleteCharAt(nameBuilder1.length() - 1);

                    StoreConstants.Lang_ids = nameBuilder1.toString();
                    //filterCourses();
                }



                break;
            case CATEGORY:

                StoreConstants.Category_Tree_id = "";
                StringBuilder nameBuilder2 = new StringBuilder();
                if(data.size() > 0) {

                    btnFilterDialog.setVisibility(View.VISIBLE);

                    for (String id : data.values()) {
                        nameBuilder2.append(id).append(",");
                        // can also do the following
                    }
                    nameBuilder2.deleteCharAt(nameBuilder2.length() - 1);

                    StoreConstants.Category_Tree_id = nameBuilder2.toString();
                  //  filterCourses();
                }

                break;
            case PUBLISHER:

                StoreConstants.Publisher_ids = "";
                StringBuilder nameBuilder3 = new StringBuilder();
                if(data.size() > 0) {
                    for (String id : data.values()) {
                        nameBuilder3.append(id).append(",");
                        // can also do the following
                    }
                    nameBuilder3.deleteCharAt(nameBuilder3.length() - 1);

                    StoreConstants.Publisher_ids = nameBuilder3.toString();
                   // filterCourses();
                }

                break;
            case FORMAT:
                StoreConstants.Formats = "";
                StringBuilder nameBuilder4 = new StringBuilder();
                if(data.size() > 0) {

                    btnFilterDialog.setVisibility(View.VISIBLE);


                    for (String id : data.values()) {
                        nameBuilder4.append(id).append(",");
                        // can also do the following

                    }
                    nameBuilder4.deleteCharAt(nameBuilder4.length() - 1);

                    StoreConstants.Formats = nameBuilder4.toString();
                }

                break;
            case BOARD:
                StoreConstants.Board_ids = "";
                StringBuilder nameBuilder5 = new StringBuilder();

                if(data.size() > 0) {
                    btnFilterDialog.setVisibility(View.VISIBLE);

                    for (String id : data.values()) {
                        // can also do the following
                        nameBuilder5.append(id).append(",");

                    }
                    nameBuilder5.deleteCharAt(nameBuilder5.length() - 1);
                    StoreConstants.Board_ids = nameBuilder5.toString();
                    //filterCourses();
                }
                break;


        }
    }


//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        searchHolder.hideRecycler();
//        mBrowseCourseCategoryAdapter.getFilter().filter(query);
//        return true;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//       searchHolder.showRecycler();
//        return true;
//    }
}