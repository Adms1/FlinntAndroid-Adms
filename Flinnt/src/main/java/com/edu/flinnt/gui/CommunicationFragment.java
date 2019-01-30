package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.CommunicationListAdapter;
import com.edu.flinnt.core.DeletePost;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.PostList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.downloads.RecyclerViewAdapter;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.DeletePostResponse;
import com.edu.flinnt.protocol.LikeBookmarkResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostListRequest;
import com.edu.flinnt.protocol.PostListResponse;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.PostEndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.UpdateReadMoreListner;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.edu.flinnt.gui.CourseDetailsActivity.POST_COMMENT_CALL_BACK;


/**
 * Created by flinnt-android-2 on 20/2/17.
 */

public class CommunicationFragment extends Fragment implements CourseDetailsActivity.onSearchListener, CourseDetailsActivity.appBarLayoutOnOffsetChanged, UpdateReadMoreListner {

    private RecyclerView mRecyclerView;
    public TextView mEmptyView;
    public Button mRetryBtn, readMoreBtn;
    private ArrayList<Post> mPosts;
    private String courseID = "", filterBy = "", courseName = "", coursePicture = "", courseAllowedRoles = "";
    private int filterPostContentType = Flinnt.INVALID, filterPostType = Flinnt.INVALID;
    public Handler mHandler;
    private PostList mPostList = null;
    private CommunicationListAdapter mAdapter;
    private PostListRequest mPostListRequest;
    private PostListRequest mPostListRequestMain;
    private PostListResponse mPosListResponseMain;
    private ProgressDialog mProgressDialog = null;
    private ArrayList<String> mOfflinePostIDs = new ArrayList<String>();
    private boolean isUpdated = false;
    private PostEndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    private String searchString = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final int POST_DELETED_CALL_BACK = 115;
    private int postPositionOnItemClick = Flinnt.INVALID;
    private String queryTextChange = "";
    private Context mContext;
    public boolean isTeacher = false;
    Post downLoadPost = null;
    private DownloadReceiver mReceiver;
    public static int adapterPosition;
    String mDownloadType = "";
    public DownloadMediaFile mDownload;
    public static final int REPOST_SUCCESSFULL_CALL_BACK = 106;


    // Another constructor function, enable to pass them arguments.
    public static CommunicationFragment newInstance(String courseID, String filterBy, int postContentType, int postType, String courseName, String coursePicture, String courseAllowedRoles, boolean isTeacher) {
        CommunicationFragment fragment = new CommunicationFragment(/*courseID, filterBy,postContentType, postType, mCourseNameTxt, coursePicture, courseAllowedRoles*/);

        Bundle args = new Bundle();
        args.putString("course_id", courseID);
        args.putString("filter_by", filterBy);
        args.putInt("post_content_type", postContentType);
        args.putInt("post_type", postType);
        args.putString("course_name", courseName);
        args.putString("course_pic", coursePicture);
        args.putString("course_allowed_roles", courseAllowedRoles);
        args.putBoolean("is_teacher", isTeacher);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.courseID = getArguments().getString("course_id");
            this.filterBy = getArguments().getString("filter_by");
            this.filterPostContentType = getArguments().getInt("post_content_type");
            this.filterPostType = getArguments().getInt("post_type");
            this.courseName = getArguments().getString("course_name");
            this.coursePicture = getArguments().getString("course_pic");
            this.courseAllowedRoles = getArguments().getString("course_allowed_roles");
            this.isTeacher = getArguments().getBoolean("is_teacher");
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("CommunicationFragment onCreate courseID : " + courseID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onCreateView CommunicationFragment" );

        //Log.d("Couu","CommunicationFragment.java");
        View rootView = inflater.inflate(R.layout.post_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.post_recyclerView);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mEmptyView = (TextView) rootView.findViewById(R.id.empty_text_postfragment);
        mRetryBtn = (Button) rootView.findViewById(R.id.retry_btn);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                } else {
                    startProgressDialog();
                    refreshList();
                }
            }
        });
        readMoreBtn = (Button) rootView.findViewById(R.id.read_more_button);
        readMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                } else {
                    readMoreBtn.setVisibility(View.GONE);
                    startProgressDialog();
                    readMore();
                }
            }
        });
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                stopProgressDialog();
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        //Helper.showToast("Success");
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        //Log.d("Comm","SUCCESS_RESPONSE : " + message.obj.toString());

                        if (message.obj instanceof PostListResponse) {
                            //Log.d("Comm","PostListResponse..if");
                            //---CHANGE ALBUM_URL = ((PostListResponse) message.obj).getAlbumUrl();
                            updateCommunicationList((PostListResponse) message.obj);
                        } else if (message.obj instanceof LikeBookmarkResponse) {
                            //Log.d("Comm","PostListResponse..else");
                            stopProgressDialog();
                            updateLikeCommentButtons((LikeBookmarkResponse) message.obj);
                        }
                        break;
                    case Flinnt.FAILURE:
                        //Helper.showToast("Failure");
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());

                        //Log.d("Comm","FAILURE_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof PostListResponse) {
                            PostListResponse response = (PostListResponse) message.obj;
                            if (response.errorResponse != null) {
                                String errorMessage = response.errorResponse.getMessage();
                                for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                    com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        return;
                                    }
                                }
                                Helper.showAlertMessage(getActivity(), getString(R.string.error), errorMessage, getString(R.string.close_txt));
                            }
                        }

                        BaseResponse response = ((BaseResponse) message.obj);
                        if (response.errorResponse != null) {
                            String errorMessage = response.errorResponse.getMessage();
                            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                    showCourseDeletedDialog(errorMessage);
                                    return;
                                }

                                if (error.getCode() == 312) {
                                    Requester.getInstance().cancelPendingRequests();
                                    Intent intent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
                                    intent.putExtra("doWhat", "deleteUser");
                                    intent.putExtra("errMsg", error.getMessage());

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    FlinntApplication.getContext().startActivity(intent);
                                }
                            }
                            Helper.showAlertMessage(getActivity(), getString(R.string.error), errorMessage, getString(R.string.close_txt));
                        }
                        Activity activity = getActivity();
                        if (activity != null) {
                            if (Helper.isConnected()) {
                                if (isTeacher) {
                                    mEmptyView.setText(getResources().getString(R.string.no_post_teacher));
                                } else {
                                    mEmptyView.setText(getResources().getString(R.string.no_post_learner));
                                }
                            } else {
                                mEmptyView.setText(getResources().getString(R.string.no_internet_msg));
                            }
                            mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                            showRetryButton();
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onActivityCreated CommunicationFragment" );


        if (filterBy == null) {
            // get offline posts from database
            mOfflinePostIDs = PostInterface.getInstance().getAllPostIDs(courseID);
            mPosts = PostInterface.getInstance().getAllPosts(courseID);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("mOfflinePostIDs : " + mOfflinePostIDs.size() + ", mPosts : " + mPosts.size());
        } else {
            if (mPosts == null) mPosts = new ArrayList<Post>();
        }

        if (mPosts.size() == 0) {
            startProgressDialog();
            Common.IS_NETWORK_TOAST = false;
        } else {
            Common.IS_NETWORK_TOAST = true;
        }
        refreshView();

        // update offline post
        if ( /*mPostListRequest.getNewOnly().equals(Flinnt.ENABLED)*/!isUpdated /*&& postListResponse.getHasMore() == Flinnt.FALSE*/) {
            updateOfflinePost();
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

    }


    public void refreshView() {

        try {
            WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);


            mAdapter = new CommunicationListAdapter(getActivity(), mPosts, courseID, courseName, CommunicationFragment.this);
            mRecyclerView.setAdapter(mAdapter);
            mEndlessRecyclerOnScrollListener = new PostEndlessRecyclerOnScrollListener(layoutManager, false) {

                @Override
                public synchronized void onLoadMore(int current_page) {
                    if (null != mPostListRequest) {
                    }
                }
            };

            mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
            mAdapter.setOnItemClickListener(new CommunicationListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (Helper.isConnected()) {
                        try {
                            if (view.getId() == R.id.read_more_button) {
                                startProgressDialog();
                                readMore();
                            } else if (view.getId() == R.id.like_linear) {
                                postPositionOnItemClick = position;
                            } else if (view.getId() == R.id.comment_linear) {
                                postPositionOnItemClick = position;
                            } else if (view.getId() == R.id.add_comment_linear) {
                                postPositionOnItemClick = position;
                            } else if (null != mAdapter.getItem(position)) {
                                postPositionOnItemClick = position;
                                mAdapter.getItem(position).setIsRead("1");
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                    } else {
                        Helper.showNetworkAlertMessage(getActivity());
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

        if (TextUtils.isEmpty(queryTextChange)) {
            Requester.getInstance().cancelPendingRequests(PostList.TAG);
            mAdapter.clearAllData();

            PostInterface.getInstance().deleteAllPostForUser(Config.getStringValue(Config.USER_ID));


            mOfflinePostIDs = PostInterface.getInstance().getAllPostIDs(courseID);
            mPosts = PostInterface.getInstance().getAllPosts(courseID);

            refreshView();

            //sendRequest..;
            ArrayList<Integer> postTypes = new ArrayList<Integer>();
            if (filterPostType == Flinnt.INVALID || filterPostType == Flinnt.FALSE) {
                postTypes.add(Flinnt.POST_TYPE_BLOG);
            } else {
                postTypes.add(filterPostType);
            }

            PostListResponse.getAllPostList().clear();

            mPostListRequest = new PostListRequest();
            mPostListRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mPostListRequest.setCourseID(courseID);
            mPostListRequest.setNewOnly(Flinnt.ENABLED);

            if (filterPostType != Flinnt.INVALID && filterPostType != Flinnt.FALSE) {
                mPostListRequest.setPostTypes(postTypes);
            }

            if (mPosts.size() > 0) {
                mPostListRequest.setPubDate(mPosts.get(0).getPublishDate());
                mPostListRequest.setExcludePosts(mOfflinePostIDs);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("getExcludePosts size : " + mPostListRequest.getExcludePosts().size() + " , getPublishDate() : " + mPosts.get(0).getPublishDate());
            }
            if (filterBy != null &&
                    !TextUtils.isEmpty(filterBy)) {
                mPostListRequest.setTagName(filterBy);
            }
            if (filterPostContentType != Flinnt.INVALID
                    && filterPostContentType != Flinnt.FALSE) {
                mPostListRequest.setPostContentType(filterPostContentType);
            }

            mPostList = new PostList(mHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ);
            mPostList.sendPostListRequest(mPostListRequest);

            mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
        } else {
            onSearch(queryTextChange, true);
        }

    }

    /**
     * Delete post confirmation dialog
     *
     * @param context  activity context
     * @param courseID course id
     * @param post     post to be deleted
     */
    public void showDeletePostWarning(final Context context, final String courseID, final Post post) {

        final Handler dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        LogWriter.write("Temp Adapter size before : " + mAdapter.getItemCount());
                        LogWriter.write("Temp Adapter size after  : " + mAdapter.getItemCount());
                        PostInterface.getInstance().deletePost(courseID, post.getPostID());
                        Helper.showToast(context.getString(R.string.successfully_deleted), Toast.LENGTH_SHORT);
                        refreshList();
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((DeletePostResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(getActivity(), getString(R.string.error), ((DeletePostResponse) message.obj).errorResponse.getMessage(), getString(R.string.close_txt));
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set mResorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        if (post.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
            titleText.setText(R.string.delete_poll);
        } else {
            titleText.setText(R.string.delete_post);
        }

        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    startProgressDialog();
                    new DeletePost(dialogHandler, courseID, post.getPostID()).sendDeletePostRequest();
                } else {
                    Helper.showNetworkAlertMessage(getActivity());
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }


    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    public void stopProgressDialog() {
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
    public void onResume() {
        super.onResume();
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onResume CommunicationFragment");
        sendPostRequest(false);
        register();
    }

    /**
     * Send post request
     *
     * @param isSearchSubmit if is search result
     */
    private void sendPostRequest(boolean isSearchSubmit) {

        if (null == mPostList || isSearchSubmit) {
            ArrayList<Integer> postTypes = new ArrayList<Integer>();
            if (filterPostType == Flinnt.INVALID || filterPostType == Flinnt.FALSE) {
                postTypes.add(Flinnt.POST_TYPE_BLOG);
//                postTypes.add(Flinnt.POST_TYPE_QUIZ);
            } else {
                postTypes.add(filterPostType);
            }

            PostListResponse.getAllPostList().clear();

            mPostListRequest = new PostListRequest();
            mPostListRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mPostListRequest.setCourseID(courseID);
            mPostListRequest.setNewOnly(Flinnt.ENABLED);
            //************change************************
            if (filterPostType != Flinnt.INVALID && filterPostType != Flinnt.FALSE) {
                mPostListRequest.setPostTypes(postTypes);
            }
            if (mPosts.size() > 0 && !isSearchSubmit) {
                mPostListRequest.setPubDate(mPosts.get(0).getPublishDate());
                mPostListRequest.setExcludePosts(mOfflinePostIDs);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("getExcludePosts size : " + mPostListRequest.getExcludePosts().size() + " , getPublishDate() : " + mPosts.get(0).getPublishDate());
            }
            if (isSearchSubmit) {
                mPostListRequest.setSearch(getSearchString());
            }
            if (filterBy != null && !TextUtils.isEmpty(filterBy)) {
                mPostListRequest.setTagName(filterBy);
            }
            if (filterPostContentType != Flinnt.INVALID && filterPostContentType != Flinnt.FALSE) {
                mPostListRequest.setPostContentType(filterPostContentType);
            }

            if (null == mPostList) {
                mPostList = new PostList(mHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ);
            }
            mPostList.sendPostListRequest(mPostListRequest);
        }

    }

    boolean isDefaultWithFooter;

    private void updateCommunicationList(PostListResponse communicationListResponse) {

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("updateCommunicationList : HasMore : " + communicationListResponse.getHasMore()
                    + ", NewOnly : " + mPostListRequest.getNewOnly());

//        Log.d("Comm","updateCommunicationList : HasMore : " + communicationListResponse.getHasMore()
//                + ", NewOnly : " + mPostListRequest.getNewOnly());

        if (mPostListRequest.getNewOnly().equals(Flinnt.ENABLED) || communicationListResponse.getHasMore() > 0) {
            if ((mAdapter.getItemCount() + communicationListResponse.getPostList().size()) < Flinnt.MAX_OFFLINE_POST_SIZE) {
                readMore();
            } else {
                mAdapter.setWithFooter(true);
            }
        } else {
            mAdapter.setWithFooter(false);
        }
        if (Helper.isConnected()) {
            if (isTeacher) {
                mEmptyView.setText(getResources().getString(R.string.no_post_teacher));
            } else {
                if (isAdded()) {
                    mEmptyView.setText(getResources().getString(R.string.no_post_learner));
                }
            }
        } else {
            mEmptyView.setText(getResources().getString(R.string.no_internet_msg));
        }
        if (mAdapter.getSearchMode()/*!getSearchString().isEmpty()*/) {
            /*if( !mAdapter.getSearchMode() ) {
                mAdapter.setSearchMode(true);
			}*/
            mAdapter.addSearchedItems(communicationListResponse.getPostList());
            mEmptyView.setText(getResources().getString(R.string.no_post_found));
        } else {
            isDefaultWithFooter = mAdapter.isWithFooter();
            mPosts.addAll(communicationListResponse.getPostList());
            mAdapter.clearAllData();
            mAdapter.addItems(mPosts/*communicationListResponse.getPostList()*/);

        }

        mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
        showRetryButton();
        if (mEndlessRecyclerOnScrollListener != null) {
            mEndlessRecyclerOnScrollListener.setLoading(false);
        }
    }

    /**
     * Update like and bookmark buttons
     *
     * @param response like bookmark response
     */
    private void updateLikeCommentButtons(LikeBookmarkResponse response) {

        //Log.d("Comm","updateLikeCommentButtons - response : " + response.toString());
        if (response.getIsLiked() == Flinnt.TRUE) {
            mAdapter.getItem(postPositionOnItemClick).setIsLike("1");
            int likeCount = Integer.parseInt(mAdapter.getItem(postPositionOnItemClick).getTotalLikes()) + 1;
            mAdapter.getItem(postPositionOnItemClick).setTotalLikes("" + likeCount);
            mAdapter.notifyItemChanged(postPositionOnItemClick);
        } else if (response.getIsDisliked() == Flinnt.TRUE) {
            mAdapter.getItem(postPositionOnItemClick).setIsLike("0");
            int likeCount = Integer.parseInt(mAdapter.getItem(postPositionOnItemClick).getTotalLikes()) - 1;
            mAdapter.getItem(postPositionOnItemClick).setTotalLikes("" + likeCount);
            mAdapter.notifyItemChanged(postPositionOnItemClick);
        }

        if (response.getIsBookmarked() == Flinnt.TRUE) {
            mAdapter.getItem(adapterPosition).setIsBookmark("1");
            mAdapter.notifyItemChanged(adapterPosition);
        } else if (response.getIsBookmarked() == Flinnt.FALSE) {
            mAdapter.getItem(adapterPosition).setIsBookmark("0");
            mAdapter.notifyItemChanged(adapterPosition);
        }

        //Log.d("Comm","updateLikeCommentButt. end ");

    }


    /**
     * Update the offline posts as per new response
     */
    public void updateOfflinePost() {

        if (mOfflinePostIDs.size() > 0) {
            Handler offlineHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    // Gets the task from the incoming Message object.
                    switch (message.what) {
                        case Flinnt.SUCCESS:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            PostListResponse communicationListResponse = (PostListResponse) message.obj;
                            if (null != communicationListResponse) {
                                mAdapter.updateItems(communicationListResponse.getPostList(), mOfflinePostIDs);
                            }
                            isUpdated = true;
                            break;

                        case Flinnt.FAILURE:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                            //Helper.showAlertMessage(getActivity(), getString(R.string.error), ((DeletePostResponse) message.obj).errorResponse.getMessage(), getString(R.string.close_txt));
                            break;
                        default:
                            super.handleMessage(message);
                    }
                }
            };


            PostList postList = new PostList(offlineHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ);
            postList.setUpdateDB(true);
            postList.sendPostListRequest();
        }
    }


    @Override
    public void onSearch(String query, Boolean isSubmit) {
        try {
            queryTextChange = query;
            if (TextUtils.isEmpty(query)) {
                if (mEndlessRecyclerOnScrollListener != null)
                    mEndlessRecyclerOnScrollListener.setLoading(false);
                if (mPostList != null && mAdapter.getSearchMode()) {
                    mPostListRequest = new PostListRequest();
                    mPostListRequest.copyRequest(getPostListRequestMain());
                    mPostList.setPostListResponse(getPosListResponseMain());

                    mPostList.setSearch(false);
                }
                setSearchString("");

                if (mEndlessRecyclerOnScrollListener != null)
                    mEndlessRecyclerOnScrollListener.initializePostEndlessRecycler();
                mAdapter.removeFilter();
                if (Helper.isConnected()) {
                    if (isTeacher) {
                        mEmptyView.setText(getResources().getString(R.string.no_post_teacher));
                    } else {
                        mEmptyView.setText(getResources().getString(R.string.no_post_learner));
                    }
                } else {
                    mEmptyView.setText(getResources().getString(R.string.no_internet_msg));
                }
                mAdapter.setWithFooter(isDefaultWithFooter);
            } else {
                if (mEndlessRecyclerOnScrollListener != null)
                    mEndlessRecyclerOnScrollListener.setLoading(true);
                if (isSubmit) {
                    mEmptyView.setText(getResources().getString(R.string.no_post_found));
                    if (mPostList != null && !mAdapter.getSearchMode()) {
                        setPostListRequestMain(mPostListRequest);
                        setPosListResponseMain(mPostList.getPostListResponse());
                        //mAdapter.setSearchMode(true);
                        mPostList.setSearch(true);

                    }
                    //if( !mAdapter.getSearchMode() ) {
                    mAdapter.setSearchMode(true);
                    //}

                    setSearchString(query);
                    //mAdapter.removeFilter();
                    sendPostRequest(true);

                    if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                        startProgressDialog();
                    }
                    mAdapter.setWithFooter(false);
                } else {
                    mAdapter.setFilter(query);
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setText(getResources().getString(R.string.no_post_found));
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
        showRetryButton();

    }

    private void showRetryButton() {
        if (mAdapter.getItemCount() == 0 && mEmptyView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.no_internet_msg))) {
            mRetryBtn.setVisibility(View.VISIBLE);
        } else {
            mRetryBtn.setVisibility(View.GONE);
        }
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }


    public PostListRequest getPostListRequestMain() {
        return mPostListRequestMain;
    }

    public void setPostListRequestMain(PostListRequest postListRequest) {
        mPostListRequestMain = new PostListRequest();
        mPostListRequestMain.copyRequest(postListRequest);
    }

    public PostListResponse getPosListResponseMain() {
        return mPosListResponseMain;
    }

    public void setPosListResponseMain(PostListResponse communicationListResponse) {
        mPosListResponseMain = new PostListResponse();
        mPosListResponseMain.copyResponse(communicationListResponse);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code post: " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        if (requestCode == CourseDetailsActivity.ADD_NEW_CONTENT) {
            if (resultCode == Activity.RESULT_OK) {
                AddPostResponse response = (AddPostResponse) data.getSerializableExtra(CourseDetailsActivity.ADDED_CONTENT_DATA);

                //*****change 31
                if (courseID.equals(AddPostResponse.COURSE_ID)) {
                    getAddedPost(response);
                }
            }
            return;
        }

        if (requestCode == AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK && resultCode == RESULT_OK) {
            if (data.getExtras().getInt(Flinnt.POST_STATS_ACTION) == Flinnt.POST_POLL_EDIT || data.getExtras().getInt(Flinnt.POST_STATS_ACTION) == Flinnt.POST_POLL_ADD) {
                refreshList();
            }

            if (data.getExtras().containsKey("position") && data.getExtras().getInt("position") != -1)
                getEditedPostion(data.getExtras().getString(Post.POST_ID_KEY), data.getExtras().getInt("position"));
        }

        try {
            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case POST_COMMENT_CALL_BACK:
                        if (data.getStringExtra("Comment") != null) {

                            int commentCount = Integer.parseInt(data.getStringExtra("CommentCount"));
                            int listCommentCount = (Integer.parseInt(mAdapter.getItem(postPositionOnItemClick).getTotalComments()));
                            if (commentCount > 0) {
                                mAdapter.getItem(postPositionOnItemClick).setTotalComments("" + (listCommentCount + commentCount));
                            } else {
                                mAdapter.getItem(postPositionOnItemClick).setTotalComments("" + (listCommentCount - Math.abs(commentCount)));
                            }
                            if (data.getStringExtra("Comment").equals("not exist")) {
                                mAdapter.getItem(postPositionOnItemClick).setIsComment(Flinnt.DISABLED);
                            } else {
                                mAdapter.getItem(postPositionOnItemClick).setIsComment(Flinnt.ENABLED);
                            }
                            mAdapter.notifyItemChanged(postPositionOnItemClick);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void getEditedPostion(String postId, final int position) {
        startProgressDialog();
        PostList list = new PostList(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                stopProgressDialog();
                PostListResponse response = (PostListResponse) message.obj;
                switch (message.what) {
                    case Flinnt.SUCCESS:

                        mAdapter.updateItem(mRecyclerView, response.getPostList().get(0), position);
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof PostListResponse) {
                            if (response.errorResponse != null) {
                                String errorMessage = response.errorResponse.getMessage();
                                for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                    com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        break;
                                    }
                                }
                                Helper.showAlertMessage(getActivity(), getString(R.string.error), errorMessage, getString(R.string.close_txt));
                            }
                        }
                        break;
                }
                return false;
            }
        }), courseID, 5);
        PostListRequest request = new PostListRequest();
        request.setCourseID(courseID);
        ArrayList<String> offlineIds = new ArrayList<String>();
        offlineIds.add(postId + "");
        request.setOfflinePosts(offlineIds);
        request.setUpdateOffline(1);
        request.setUserID(Config.getStringValue(Config.USER_ID));
        list.sendPostListRequest(request);
    }

    private void getAddedPost(AddPostResponse response) {

        startProgressDialog();
        PostList list = new PostList(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                stopProgressDialog();
                PostListResponse response = (PostListResponse) message.obj;
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        mAdapter.add(0, response.getPostList().get(0));
                        mRecyclerView.scrollToPosition(0);
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof PostListResponse) {
                            if (response.errorResponse != null) {
                                String errorMessage = response.errorResponse.getMessage();
                                for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                    com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        break;
                                    }
                                }
                                Helper.showAlertMessage(getActivity(), getString(R.string.error), errorMessage, getString(R.string.close_txt));
                            }
                        }
                        break;
                }
                return false;
            }
        }), courseID, 5);
        PostListRequest request = new PostListRequest();
        request.setCourseID(courseID);
        ArrayList<String> offlineIds = new ArrayList<String>();
        offlineIds.add(response.getData().getPost().getPostId() + "");
        request.setOfflinePosts(offlineIds);
        request.setUpdateOffline(1);
        request.setUserID(Config.getStringValue(Config.USER_ID));
        list.sendPostListRequest(request);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    /**
     * Course delete confirmation dialog
     */
    public void showCourseDeletedDialog(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        // set mResorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(mContext);
        // You Can Customise your Title here
        titleText.setText(getString(R.string.error));
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(mContext.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(getString(R.string.removed_from_course));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Delete Course from offline database
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), courseID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);

                if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }

    }

    @Override
    public void onOffsetChanged(int i) {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setEnabled(i == 0);
    }


    public void readMore() {
        //Log.d("Comm","readMore");
        if (null != mPostListRequest) {
            String publishDate = mPostListRequest.getPubDate();

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("readMore : has_more : " + mPostList.getPostListResponse().getHasMore()
                        + ", new_only : " + mPostListRequest.getNewOnly() + " , ExcludePosts size : " + mPostListRequest.getExcludePosts().size());

//            Log.d("Comm","readMore : has_more : " + mPostList.getPostListResponse().getHasMore()
//                    + ", new_only : " + mPostListRequest.getNewOnly() + " , ExcludePosts size : " + mPostListRequest.getExcludePosts().size());

            if (mPostListRequest.getNewOnly().equals(Flinnt.ENABLED) || mPostListRequest.getExcludePosts().isEmpty()) {
                ArrayList<String> excludeIDs = new ArrayList<String>();
                excludeIDs.addAll(mPostListRequest.getExcludePosts());
                for (int i = 0; i < mPostList.getPostListResponse().getPostList().size(); i++) {
                    excludeIDs.add(mPostList.getPostListResponse().getPostList().get(i).getPostID());
                }
                mPostListRequest.setExcludePosts(excludeIDs);
                if (mPostList.getPostListResponse().getPostList().size() > 0) {
                    publishDate = mPostList.getPostListResponse().getPostList().get(mPostList.getPostListResponse().getPostList().size() - 1).getPublishDate();
                }
                mPostListRequest.setPubDate(publishDate);
            }


            if (mPostListRequest.getNewOnly().equals(Flinnt.ENABLED)) {
                mPostListRequest.setOffset(0);
                mPostListRequest.setNewOnly(Flinnt.DISABLED);
                mPostListRequest.setMax(mPostListRequest.getMax());
                mPostList.sendPostListRequest(mPostListRequest);
            } else if (mPostList.getPostListResponse().getHasMore() > 0) {
                // Reset offset to new request - New offset = old offset + max
                mPostListRequest.setOffset(mPostListRequest.getOffset() + mPostListRequest.getMax());
                mPostListRequest.setMax(mPostListRequest.getMax());
                //mPostList.sendPostListRequest();
                mPostList.sendPostListRequest(mPostListRequest);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(getActivity(), "activity=" + Flinnt.COMMUNICATION_LIST + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseID);
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


    @Override
    public void onPause() {
        super.onPause();
        unRegister();
        mAdapter.stopMediaControl();
    }

    private void register() {
        mReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        }
    }

    /**
     * Reset the player after playing media ends
     * Method for start downloading
     */

    public void download(int position, String tag, Post mPost, String downloadType, int albumposition) {
        mDownloadType = downloadType;
        downLoadPost = mPost;
        AppInfoDataSet info;
        if (downloadType.equals("post")) {
            info = mPost.appInfoDataSets.get(0);
        } else {
            info = mPost.appInfoDataSets.get(albumposition);
        }
        DownloadService.intentDownload(getActivity(), position, albumposition, tag, info);
    }

    public void pause(int position, String tag, Post mPost, String downloadType, int albumposition) {
        mDownloadType = downloadType;
        downLoadPost = mPost;
        AppInfoDataSet info;
        if (downloadType.equals("post")) {
            info = mPost.appInfoDataSets.get(0);
        } else {
            info = mPost.appInfoDataSets.get(albumposition);
        }
        DownloadService.intentPause(getActivity(), position, albumposition, tag, info);
    }

    public void cancel(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentCancel(getActivity(), position, tag, info);
    }

    @Override
    public void onUpdateReadMoreButton(boolean isShow) {
        LogWriter.write("Inside of onUpdateReadMoreButton : " + isShow);
        if (isShow) {
            readMoreBtn.setVisibility(View.VISIBLE);
        } else {
            readMoreBtn.setVisibility(View.GONE);
        }

    }

    /**
     * Reset the player after playing media ends
     * <p>
     * Receciver for download update
     */

    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action == null || !action.equals(DownloadService.ACTION_DOWNLOAD_BROAD_CAST)) {
                return;
            }
            final int position = intent.getIntExtra(DownloadService.EXTRA_POSITION, -1);
            final int albumPosition = intent.getIntExtra(DownloadService.EXTRA_POSITION_ALBUM, -1);
            final AppInfoDataSet tmpInfoDataSet = (AppInfoDataSet) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);

            if (downLoadPost == null) return;
            if (downLoadPost.getPostID().equals(tmpInfoDataSet.getId())) {
                if (tmpInfoDataSet == null || position == -1) {
                    return;
                }

                AppInfoDataSet appInfoDataSet;

                if (mDownloadType.equals("post")) {
                    appInfoDataSet = downLoadPost.appInfoDataSets.get(0);
                } else {
                    appInfoDataSet = downLoadPost.appInfoDataSets.get(albumPosition);
                }

                final int status = tmpInfoDataSet.getStatus();
                switch (status) {
                    case AppInfoDataSet.STATUS_CONNECTING:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(tmpInfoDataSet.getDownloadFilePath(), tmpInfoDataSet.getName())) {
                                        cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                                        holder.mProgressbarHint.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(tmpInfoDataSet.getDownloadFilePath(), tmpInfoDataSet.getName())) {
                                        RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                        cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                                        holder1.albumProgressBarHint.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;

                    case AppInfoDataSet.STATUS_DOWNLOADING:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                    holder.mProgressbarHint.setVisibility(View.GONE);
                                } else {

                                    RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                    holder1.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder1.downloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                    holder1.albumProgressBarHint.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;
                    case AppInfoDataSet.STATUS_COMPLETE:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.mMediaFrame.setVisibility(View.GONE);
                                } else {
                                    RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                    holder1.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder1.albumFrame.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;

                    case AppInfoDataSet.STATUS_PAUSED:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                    holder.mProgressbarHint.setVisibility(View.GONE);
                                } else {
                                    RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                    holder1.downloadBtn.setBackgroundResource(R.drawable.ic_play);
                                    holder1.albumProgressBarHint.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;
                    case AppInfoDataSet.STATUS_NOT_DOWNLOAD:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.mProgressbarHint.setVisibility(View.GONE);
                                } else {
                                    RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                    holder1.downloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder1.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder1.albumProgressBarHint.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;
                    case AppInfoDataSet.STATUS_DOWNLOAD_ERROR:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
                        appInfoDataSet.setDownloadPerSize("");
                        if (isCurrentListViewItemVisible(position)) {
                            CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                        }
                        break;
                    case AppInfoDataSet.STATUS_CANCEL:
                        try {
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            if (isCurrentListViewItemVisible(position)) {
                                CommunicationListAdapter.ViewHolder holder = getViewHolder(position);
                                if (mDownloadType.equals("post")) {
                                    holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder.mProgressBar.setProgress(0);
                                    holder.mProgressbarHint.setVisibility(View.GONE);
                                } else {
                                    RecyclerViewAdapter.AppViewHolder holder1 = (RecyclerViewAdapter.AppViewHolder) holder.mAlbumRecyclerView.findViewHolderForLayoutPosition(albumPosition);
                                    holder1.downloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder1.albumProgressBar.setProgress(0);
                                    holder1.albumProgressBarHint.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            LogWriter.write("Error--" + e.getMessage());
                        }
                        break;
                }
            }
        }
    }

    private CommunicationListAdapter.ViewHolder getViewHolder(int position) {
        return (CommunicationListAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        try {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (DialogInterface.OnClickListener) null);

            mProgressDialog.show();

            mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopDownload();
                    try {
                        mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                        mProgressDialog.setMessage("Cancelling...");
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }
            });
            mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

        } catch (Exception e) {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("DownloadProgressDialog exception : " + e.getMessage());
        }
    }

    /**
     * Stops currently running download
     */
    private void stopDownload() {
        //stop download if working...
        if (mDownload != null) {
            mDownload.setCancel(true);
        }
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }
}
