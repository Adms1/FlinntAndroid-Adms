package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PostViewersAdapter;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.PostViewersLikes;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostViewersRequest;
import com.edu.flinnt.protocol.PostViewersResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import static com.edu.flinnt.FlinntApplication.mContext;

/**
 * Created by flinnt-android-2 on 6/10/16.
 */
public class PostViewersLikeFragment extends Fragment {

    RecyclerView mRecyclerView;
    Handler mHandler;
    ProgressDialog mProgressDialog = null;
    PostViewersRequest mPostViewersRequest;
    public PostViewersAdapter mPostViewersAdapter;
    PostViewersLikes mPostViewersLikes;
    ArrayList<PostViewersResponse.PostViewersItems> mPostViewersItems = new ArrayList<PostViewersResponse.PostViewersItems>();
    String courseID = "", postID = "", viewerPicUrl = "", courseName = "",postOrContent = "";
    int currentTab;
    TextView mEmptyTxt;

    int visibleItemCount, totalItemCount, lastVisibleItem;
    private int visibleThreshold = 2;
    private int hasMore = 0;
    private int offset = 0;
    private int max = 20;


    public static PostViewersLikeFragment newInstance(String courseID, String courseName, String postID, String viewerPicUrl,String comeFrom, int position) {
        PostViewersLikeFragment fragment = new PostViewersLikeFragment(/*courseID, coursePicture, mCourseNameTxt, courseAllowedRoles*/);

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(PostViewersSendMessageActivity.COURSE_ID_KEY, courseID);
        args.putString(PostViewersSendMessageActivity.COURSE_NAME_KEY, courseName);
        args.putString(PostViewersSendMessageActivity.POST_ID_KEY, postID);
        args.putString(PostViewersSendMessageActivity.USER_PICTURE_URL_KEY, viewerPicUrl);
        args.putString(PostViewersSendMessageActivity.POST_OR_CONTENT_KEY,comeFrom);
        args.putInt(PostViewersSendMessageActivity.CURRENT_TAB_KEY, position);
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
            courseID = getArguments().getString(PostViewersSendMessageActivity.COURSE_ID_KEY);
            courseName = getArguments().getString(PostViewersSendMessageActivity.COURSE_NAME_KEY);
            postID = getArguments().getString(PostViewersSendMessageActivity.POST_ID_KEY);
            viewerPicUrl = getArguments().getString(PostViewersSendMessageActivity.USER_PICTURE_URL_KEY);
            postOrContent = getArguments().getString(PostViewersSendMessageActivity.POST_OR_CONTENT_KEY);
            currentTab = getArguments().getInt(PostViewersSendMessageActivity.CURRENT_TAB_KEY);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onCreate courseID : " + courseID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.post_viewers_fragment, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.post_tags_recyclerView);
        mEmptyTxt = (TextView)  rootView.findViewById(R.id.empty_text_posttagfragment);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof PostViewersResponse) {
                            updateViewersList((PostViewersResponse) message.obj,currentTab);
                            hasMore = ((PostViewersResponse) message.obj).getHasMore();

                        }

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof PostViewersResponse) {
                            PostViewersResponse response = (PostViewersResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(getActivity(), "Error", response.errorResponse.getMessage(), "CLOSE");
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
                            }
                            Helper.showAlertMessage(getActivity(), "Error", errorMessage, "CLOSE");
                        }
                        break;
                    default:
                        /*
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(message);
                }
            }
        };
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
        // set resorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(mContext);
        // You Can Customise your Title here
        titleText.setText("Error");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(mContext.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage("You have been removed from course");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == mPostViewersLikes) {
            mPostViewersRequest = new PostViewersRequest();
            mPostViewersLikes = new PostViewersLikes(mHandler, courseID, postID, postOrContent,currentTab, offset);
            mPostViewersLikes.sendPostViewersLikesRequest(mPostViewersRequest);
            startProgressDialog();
        }
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mPostViewersAdapter = new PostViewersAdapter(mPostViewersItems, viewerPicUrl, courseID, courseName);
        mRecyclerView.setAdapter(mPostViewersAdapter);
        mRecyclerView.invalidate();
        mPostViewersAdapter.setOnItemClickListener(new PostViewersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Helper.isConnected()) {
                    try {
                        PostViewersResponse.PostViewersItems viewer = mPostViewersItems.get(position);
                        String teachersIDs = "";
                        String studentsIDs = "";
                        int teachersCount = 0;
                        int studentsCount = 0;

                        Intent addMessage = new Intent(getActivity(), AddCommunicationActivity.class);
                        addMessage.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_MESSAGE_ADD);
                        addMessage.putExtra(Course.COURSE_ID_KEY, courseID);
                        addMessage.putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, courseName);
                        if (Integer.parseInt(viewer.getUserRole()) == Flinnt.COURSE_ROLE_TEACHER) {
                            teachersIDs = viewer.getViewerID();
                            teachersCount++;
                        } else if (Integer.parseInt(viewer.getUserRole()) == Flinnt.COURSE_ROLE_LEARNER) {
                            studentsIDs = viewer.getViewerID();
                            studentsCount++;
                        }
                        addMessage.putExtra(SelectUsersActivity.SELECTED_TEACHERS_COUNT, teachersCount);
                        addMessage.putExtra(SelectUsersActivity.SELECTED_STUDENTS_COUNT, studentsCount);
                        addMessage.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER, studentsIDs);
                        addMessage.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER, teachersIDs);
                        addMessage.putExtra("comeFrom", "ViewersActivity");
                        getActivity().startActivityForResult(addMessage, PostViewersSendMessageActivity.POST_VIEWRS_FRAGMENT_CALLBACK);
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                } else {
                    Helper.showNetworkAlertMessage(getActivity());
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (hasMore == 1) {
                        offset = max + offset;
                        mPostViewersRequest = new PostViewersRequest();
                        mPostViewersLikes = new PostViewersLikes(mHandler, courseID, postID, postOrContent, currentTab, offset);
                        mPostViewersLikes.sendPostViewersLikesRequest(mPostViewersRequest);
                        hasMore = 0;
                    }
                }
            }
        });
    }


    /**
     * Update the list of who visited the post
     *
     * @param postViewersResponse post viewers response
     */
    private void updateViewersList(PostViewersResponse postViewersResponse, int currentTab) {
        mPostViewersAdapter.addItems(postViewersResponse.getViewersList(), currentTab);
        if (mPostViewersAdapter.getItemCount() == 0) {
            mEmptyTxt.setVisibility(View.VISIBLE);
            mEmptyTxt.setText(getResources().getString(R.string.no_likes_msg));
        }else {
            mEmptyTxt.setVisibility(View.GONE);
        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(getActivity(), "PostViewersLike");
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