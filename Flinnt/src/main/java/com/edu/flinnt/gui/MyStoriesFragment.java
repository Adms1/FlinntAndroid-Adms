package com.edu.flinnt.gui;

import android.arch.lifecycle.LifecycleOwner;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.edu.flinnt.adapter.StoryAdapter;
import com.edu.flinnt.models.StoryDataModel;
import com.edu.flinnt.models.StoryListDataModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.StoriesRecyclerOnScrollListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;
//@Nikhil whole class created

public class MyStoriesFragment extends Fragment implements MyCoursesActivity.OnSearchListener {
    // mEmptyTextView.setText(getResources().getString(R.string.no_internet_msg));
    public AsyncHttpClient client;
    FloatingActionButton floatingActionButton;
    private static final String TAG = "MyStoriesFragment";
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<StoryDataModel.Story> storyDataModelList = new ArrayList<StoryDataModel.Story>();
    ArrayList<StoryDataModel.Story> filteredDataset = new ArrayList<StoryDataModel.Story>();
    ArrayList<StoryDataModel> storyDataModels = new ArrayList<StoryDataModel>();
    StoryAdapter storyAdapter;
    int offsetMain = 0, offset = 0;
    int max_count = 20;
    LinearLayoutManager layoutManager;
    StoriesRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    TextView textView;
    private boolean hasMore = false;
    public Button readMoreBtn;

    private String searchText = "";
    //mRetryBtn
    private OnFragmentInteractionListener mListener;

    public MyStoriesFragment() {
        // Required empty public constructor
    }

    public static MyStoriesFragment newInstance() {
        MyStoriesFragment fragment = new MyStoriesFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSearch(String query, Boolean isSubmit) {

        //Log.d(TAG, "onSearch() : isSubmit : " + isSubmit + "query : " + query + "hasMore : " + hasMore);

        searchText = query;
        hasMore = true;  // @Chirag:17/08/2018 'old was true'

        readMoreBtn.setVisibility(View.GONE);

        if (isSubmit) {

            callStoryApiForSearch(query);

        } else {

            filteredDataset.clear();

            //Log.d(TAG, "onSearch  else else: query : " + query);

            for (StoryDataModel.Story item : storyDataModelList) {

                if (item.getTitle().toLowerCase().contains(query.toLowerCase().trim())) {
                    filteredDataset.add(item);
                }
            }
//            Log.d(TAG, "storyDataModelList size=>>>  " + storyDataModelList.size());
//            Log.d(TAG, "filtereddataSet size=>>>  " + filteredDataset.size());
            storyAdapter.setData(filteredDataset);

            /*if (mEndlessRecyclerOnScrollListener != null) {
                mEndlessRecyclerOnScrollListener.setLoading(false);
            }*///@chirag:08/09/2018 commented

            if (filteredDataset.size() > 0) {
                textView.setVisibility(View.GONE);
                textView.setText("No Story Found \n Choose Other Category");
                    offset = offsetMain;
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText("No Story Found");
            }

//            Log.d(TAG, "onSearch end offset : " + offset);
//            Log.d(TAG, "onSearch end offsetMian : " + offsetMain);
//            Log.d(TAG, "onSearch end hasMore : " + hasMore);
        }
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView() hasMore : " + hasMore);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_stories_fragment, container, false);

        //hasMore = false; //@Chirag: 17/08/2018 added this line (Couse: after switch user LoadMore btn display on scroll even there is only two records avails)
        hasMore = false; //@Chirag: 08/09/2018 added
        readMoreBtn = (Button) view.findViewById(R.id.read_more_button);
        //mRetryBtn = (Button) view.findViewById(R.id.read_more_button);
        readMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                } else {
                    readMoreBtn.setVisibility(View.GONE);
//                    startProgressDialog();
//                    readMore();
                    try {
                        if (hasMore) {
                            //Log.d(TAG, "onreadMore searchText : " + searchText);
                            if (TextUtils.isEmpty(searchText)) {
                                WebService_StoryList();
                            } else {

                                WebServiceStoryListSearch(searchText);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        textView = (TextView) view.findViewById(R.id.txtemptystory);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isConnected()) {

                    hasMore = false;
                    readMoreBtn.setVisibility(View.GONE);//@Chirag:08/09/2018 added
                    if (TextUtils.isEmpty(searchText)) {
                        callStoryApi();//@Chirag:28/08/2018 changed
                    } else {
                        callStoryApiForSearch(searchText);//@Chirag:28/08/2018 added
                    }

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Helper.showNetworkAlertMessage(getActivity());
                }

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.my_story_recycler_view);

        storyAdapter = new StoryAdapter(storyDataModelList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(25));
        recyclerView.setAdapter(storyAdapter);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerOnScrollListener = new StoriesRecyclerOnScrollListener(layoutManager, false) {

            @Override
            public synchronized void onLoadMore(int current_page) {


                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("hasMore :: " + storyDataModels.get(0).getHasMore());

                //Log.d(TAG,"hasMore : " + hasMore);
                if (hasMore) {
                    readMoreBtn.setVisibility(View.VISIBLE);
                } else {
                    readMoreBtn.setVisibility(View.GONE);
                }
            }
        };
        //mEndlessRecyclerOnScrollListener.setVisibleThreshold(20); //@Chirag:07/09/2018 added
        recyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);


        if (Helper.isConnected()) {
            callStoryApi();
        }
        return view;
    }

    public void callStoryApi() {

        //Log.d(TAG, "callStoryApi");
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Log.d(TAG, "run : ");
                                        swipeRefreshLayout.setRefreshing(true);
                                        try {
                                            offsetMain = 0;
                                            storyDataModelList.clear();
                                            storyAdapter.clearData();
                                            /*if (mEndlessRecyclerOnScrollListener != null) {//@chirag:28/08/2018 added
                                                mEndlessRecyclerOnScrollListener.setLoading(false);
                                            }*/  //@Chirag: 08/09/2018 commented
                                            WebService_StoryList();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
        );
    }


    private void callStoryApiForSearch(String query) {
        try {
            offset = 0;
            filteredDataset.clear();
            WebServiceStoryListSearch(query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

    public void WebService_StoryList() throws JSONException, UnsupportedEncodingException {
        swipeRefreshLayout.setRefreshing(true);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("user_id", Config.getStringValue(Config.USER_ID));
        jsonParams.put("offset", offsetMain);
        //Log.d(TAG,"offset : " + offset);
        jsonParams.put("max", max_count);
        //Log.d(TAG, jsonParams.toString());
        StringEntity entity = new StringEntity(jsonParams.toString());
        client = new AsyncHttpClient();
        client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        try {
            client.setConnectTimeout(50000);
            //Log.d(TAG, "request offsetMain : " + offsetMain + "\tmax_count : " + max_count);
            client.post(getContext(), Flinnt.API_URL + Flinnt.URL_STORY_LIST, entity, "application/json", new BaseJsonHttpResponseHandler<StoryListDataModel>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final StoryListDataModel response) {


                    ///Log.d("storyres", rawJsonResponse);
                    //progressDialog.dismiss();
                    //Log.d(TAG, "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], rawJsonResponse = [" + rawJsonResponse + "], response = [" + response + "]");
                    try {
                        if (response.getStatus().equals(1)) {
                            storyDataModels.add(response.getData());

                            if (response.getData().getStories().size() > 0) {

                                storyAdapter.setGalleryUrl(response.getData().getGalleryUrl());
                                storyDataModelList.addAll(response.getData().getStories());
                                storyAdapter.setData(storyDataModelList);
                                /*if (mEndlessRecyclerOnScrollListener != null) {
                                    mEndlessRecyclerOnScrollListener.setLoading(false);
                                }*///@chirag:08/09/2018 commented
                                swipeRefreshLayout.setRefreshing(false);
                                offsetMain = offsetMain + max_count;
                            }

                            if (response.getData().getHasMore() > 0) {
                                hasMore = true;
                            } else {
                                hasMore = false;
                            }

                            swipeRefreshLayout.setRefreshing(false);

                            if (storyDataModelList.size() > 0) {
                                textView.setVisibility(View.GONE);
                                textView.setText("No Story Found \n Choose Other Category");
                            } else {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("No Story Found");
                            }
                            //Log.d(TAG, String.valueOf(storyDataModelList.size()));
                        }
                        //Log.d(TAG, "onResponse() - hasMore" + hasMore);
                        //Log.d(TAG, "onResponse last offsetMain : " + offsetMain);
                    } catch (Exception e) {
                        //Log.d(TAG, "onSuccess: " + e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, StoryListDataModel errorResponse) {
                    //progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "onFailure() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], throwable = [" + throwable + "], rawJsonData = [" + rawJsonData + "], errorResponse = [" + errorResponse + "]");
                }

                @Override
                protected StoryListDataModel parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    //Log.e(TAG, "parseResponse() called with: rawJsonData = [" + rawJsonData + "], isFailure = [" + isFailure + "]");
                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return new Gson().fromJson(rawJsonData, StoryListDataModel.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                }
            });

        } catch (Exception e) {

        }
    }


    public void WebServiceStoryListSearch(String searchtxt) throws JSONException, UnsupportedEncodingException {

        swipeRefreshLayout.setRefreshing(true);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("user_id", Config.getStringValue(Config.USER_ID));
        jsonParams.put("offset", offset);
        jsonParams.put("max", max_count);
        jsonParams.put("search", searchtxt);
        //Log.d(TAG, "WebServiceStory Search : offset : " + offset);
        StringEntity entity = new StringEntity(jsonParams.toString());
        client = new AsyncHttpClient();
        client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        try {
            client.setConnectTimeout(50000);
            client.post(getContext(), Flinnt.API_URL + Flinnt.URL_STORY_LIST, entity, "application/json", new BaseJsonHttpResponseHandler<StoryListDataModel>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final StoryListDataModel response) {
                    //progressDialog.dismiss();
                   // Log.d(TAG, "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], rawJsonResponse = [" + rawJsonResponse + "], response = [" + response + "]");
                    try {
                        if (response.getStatus().equals(1)) {
                            storyDataModels.add(response.getData());

                            if (response.getData().getStories().size() > 0) {

                                filteredDataset.addAll(response.getData().getStories());
                                storyAdapter.setData(filteredDataset);
                                /*if (mEndlessRecyclerOnScrollListener != null) {
                                    mEndlessRecyclerOnScrollListener.setLoading(false);
                                }*///@chirag:08/09/2018 commented
                                swipeRefreshLayout.setRefreshing(false);
                                offset = offset + max_count;
                            }

                            if (response.getData().getHasMore() > 0) {
                                hasMore = true;
                            } else {
                                hasMore = false;
                            }
                            swipeRefreshLayout.setRefreshing(false);

                            if (storyAdapter.getItemCount() > 0) {
                                textView.setVisibility(View.GONE);
                                textView.setText("No Story Found \n Choose Other Category");
                            } else {
                                textView.setText("No Story Found");
                                textView.setVisibility(View.VISIBLE);
                            }
                            //Log.d(TAG, "onResponse search last offset : " + offset + "\thasMore : " + hasMore);
                        }
                    } catch (Exception e) {
                        //Log.d(TAG, "onSuccess: " + e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, StoryListDataModel errorResponse) {
                    //progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "onFailure() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], throwable = [" + throwable + "], rawJsonData = [" + rawJsonData + "], errorResponse = [" + errorResponse + "]");
                }

                @Override
                protected StoryListDataModel parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    //Log.e(TAG, "parseResponse() called with: rawJsonData = [" + rawJsonData + "], isFailure = [" + isFailure + "]");
                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return new Gson().fromJson(rawJsonData, StoryListDataModel.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                }
            });

        } catch (Exception e) {

        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
