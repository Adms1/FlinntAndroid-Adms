package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.CommunicationCommentsAdapter;
import com.edu.flinnt.adapter.CommunicationCommentsAdapter.OnItemLongClickListener;
import com.edu.flinnt.core.AddComment;
import com.edu.flinnt.core.DeleteComment;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.PostComments;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.protocol.AddCommentResponse;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Comment;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostCommentsRequest;
import com.edu.flinnt.protocol.PostCommentsResponse;
import com.edu.flinnt.protocol.PostListMenuResponse;
import com.edu.flinnt.util.CommentEndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.InteractiveScrollView.OnBottomReachedListener;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;


/**
 * Created by flinnt-android-2 on 23/2/17.
 * When user click in comment section from the CommunicationListAdapter it will redirect to Communication comment activity.
 */

public class CommunicationCommentActivity extends AppCompatActivity implements View.OnClickListener, OnBottomReachedListener {
    LinearLayoutManager mylinearLayoutManager;
    Toolbar mToolbar;
    RecyclerView mCommentsRecycler;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    private PostComments mPostComments;
    private PostCommentsRequest mPostCommentsRequest;
    private CommunicationCommentsAdapter mCommunicationCommentAdapter;
    private CommentEndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListenerComments;
    private ArrayList<Comment> mCommentItems = new ArrayList<Comment>();
    private String courseID = "";
    private String postID = "", requestFocus = Flinnt.DISABLED;
    private boolean isNeedToGetComment = false;
    //private InteractiveScrollView mCommentScrollview;
    EditText mAddCommentEdt;
    ImageView mAddCommentSendImg;
    LinearLayout mAddCommentLinear;
    int commentSend = Flinnt.FALSE;
    int canComment = Flinnt.FALSE;
    String commentAddDelete = "";
    int commentCount = 0;
    TextView mEmptyTxt;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount, pastVisiblesItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_comment_communication);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                courseID = bundle.getString(Course.COURSE_ID_KEY);
            }
            if (bundle.containsKey(Post.POST_ID_KEY)) {
                postID = bundle.getString(Post.POST_ID_KEY);
            }
            if (bundle.containsKey("REQUEST_FOCUS")) {
                requestFocus = bundle.getString("REQUEST_FOCUS");
            }
            if (bundle.containsKey(PostListMenuResponse.CAN_COMMENT_KEY)) {
                canComment = bundle.getInt(PostListMenuResponse.CAN_COMMENT_KEY);
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCommentsRecycler = (RecyclerView) findViewById(R.id.comments_recycler);
        mCommentsRecycler.setNestedScrollingEnabled(false);
        mCommentsRecycler.setHasFixedSize(false);

        //@Nikhil 672018
//        mCommentScrollview = (InteractiveScrollView) findViewById(R.id.comments_scrollview);
//        mCommentScrollview.setOnBottomReachedListener(this);

        mAddCommentEdt = (EditText) findViewById(R.id.add_comment_edt);
        mAddCommentSendImg = (ImageView) findViewById(R.id.add_comment_send_img);
        mAddCommentSendImg.setOnClickListener(this);
        mAddCommentLinear = (LinearLayout) findViewById(R.id.add_comment_linear);

        mEmptyTxt = (TextView) findViewById(R.id.empty_txt);
        if (requestFocus.equals(Flinnt.ENABLED)) {
            mAddCommentEdt.requestFocus();
        }

        if (canComment == Flinnt.TRUE) {
            mAddCommentLinear.setVisibility(View.VISIBLE);
        } else {
            mAddCommentLinear.setVisibility(View.GONE);
        }


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("handleMessage : " + message.what);
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        if (message.obj instanceof PostCommentsResponse) {
                            updateCommentsList((PostCommentsResponse) message.obj);
                        } else if (message.obj instanceof AddCommentResponse) {
                            AddCommentResponse mAddCommentResponse = (AddCommentResponse) message.obj;
                            if (mAddCommentResponse.getIsAdded() == Flinnt.TRUE) {
                                mAddCommentEdt.setText("");
                                if (mAddCommentResponse.getShowComment() == Flinnt.TRUE) {
                                    showCommentToUser(mAddCommentResponse);
                                    commentSend = Flinnt.TRUE;
                                    commentAddDelete = "Add";
                                    commentCount = commentCount + 1;
                                } else {
                                    Helper.showAlertMessage(CommunicationCommentActivity.this, getString(R.string.success), getString(R.string.sent_for_approval), getString(R.string.close_txt));
                                }
                            }
                        }
                        break;

                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        BaseResponse response = ((BaseResponse) message.obj);
                        if (response.errorResponse != null) {
                            String errorMessage = response.errorResponse.getMessage();
                            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                if (error.getCode() == ErrorCodes.ERROR_CODE_5) {
                                    Helper.showToast(getString(R.string.delete_post_msg), Toast.LENGTH_SHORT);
                                    PostInterface.getInstance().deletePost(courseID, postID);
                                    onBackPressed();
                                    return;
                                }
                                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                    showCourseDeletedDialog(errorMessage);
                                    return;
                                }
                            }
                            Helper.showAlertMessage(CommunicationCommentActivity.this, getString(R.string.error), errorMessage, getString(R.string.close_txt));
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
        startProgressDialog();
        updateCommentsData();
    }


    /**
     * Display updated comments
     */
    private void updateCommentsData() {

        if (null == mPostComments) {
            mPostCommentsRequest = new PostCommentsRequest();

            mPostComments = new PostComments(mHandler, courseID, postID);
            mPostComments.sendPostCommentsRequest(mPostCommentsRequest);
        }

        mylinearLayoutManager = new LinearLayoutManager(CommunicationCommentActivity.this);
        mCommentsRecycler.setLayoutManager(mylinearLayoutManager);

        mCommunicationCommentAdapter = new CommunicationCommentsAdapter(mCommentItems);
        mCommentsRecycler.setAdapter(mCommunicationCommentAdapter);

        mEndlessRecyclerOnScrollListenerComments = new CommentEndlessRecyclerOnScrollListener(mylinearLayoutManager, false) {
            @Override
            public void onLoadMore() {


                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("onLoadMore : has_more : " + mPostComments.getLastResponse().getHasMore());

                if (mPostComments.getLastResponse().getHasMore() > 0) {
                    // Reset offset to new request - New offset = old offset + max
                    mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + mPostCommentsRequest.getMaxFetch());
                    mPostComments.sendPostCommentsRequest(mPostCommentsRequest);
                }
            }
        };
        mCommentsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mylinearLayoutManager.getChildCount();
                    totalItemCount = mylinearLayoutManager.getItemCount();
                    pastVisiblesItems = mylinearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("onLoadMore : has_more : " + mPostComments.getLastResponse().getHasMore());

                            if (mPostComments.getLastResponse().getHasMore() > 0) {
                                // Reset offset to new request - New offset = old offset + max
                                mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + mPostCommentsRequest.getMaxFetch());
                                mPostComments.sendPostCommentsRequest(mPostCommentsRequest);
                            }
                        }
                    }
                }
            }
        });
        mCommentsRecycler.addOnScrollListener(mEndlessRecyclerOnScrollListenerComments);

        mCommunicationCommentAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mCommunicationCommentAdapter.getItem(position) != null) {
                    if (mCommunicationCommentAdapter.getItem(position).getCanDelete() == Flinnt.TRUE) {
                        showDeleteCommentWarning(CommunicationCommentActivity.this,
                                mCommunicationCommentAdapter, mCommunicationCommentAdapter.getItem(position));
                    }

                }

            }
        });
    }

    /**
     * Shows confirmation dialog to delete comment
     *
     * @param context  current activity
     * @param mAdapter comments adapter
     * @param comment  comment object
     */
    public void showDeleteCommentWarning(final Context context, final CommunicationCommentsAdapter mAdapter, final Comment comment) {

        final Handler dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        mAdapter.remove(comment);
                        commentSend = Flinnt.TRUE;
                        commentCount = commentCount - 1;
                        if (mAdapter.getItemCount() > 0) {
                            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                if (mAdapter.getItem(i).getCommentUserID().equals(Config.getStringValue(Config.USER_ID))) {
                                    commentAddDelete = getString(R.string.exist);
                                    break;
                                } else {
                                    commentAddDelete = getString(R.string.not_exist);
                                }
                            }
                        } else {
                            mEmptyTxt.setVisibility(View.VISIBLE);
                            commentAddDelete = getString(R.string.not_exist);
                        }
                        Helper.showToast(getString(R.string.successfully_deleted), Toast.LENGTH_SHORT);
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        BaseResponse response = ((BaseResponse) message.obj);
                        if (response.errorResponse != null) {
                            String errorMessage = response.errorResponse.getMessage();
                            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                if (error.getCode() == ErrorCodes.ERROR_CODE_5) {
                                    Helper.showToast(getString(R.string.delete_post_msg), Toast.LENGTH_SHORT);
                                    PostInterface.getInstance().deletePost(courseID, postID);
                                    onBackPressed();
                                    return;
                                }
                                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                    showCourseDeletedDialog(errorMessage);
                                    return;
                                }
                            }
                            Helper.showAlertMessage(CommunicationCommentActivity.this, getString(R.string.error), errorMessage, getString(R.string.close_txt));
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        TextView titleText = new TextView(context);
        titleText.setText(R.string.delete_comment);
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton(getString(R.string.content_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    String commentID = comment.getPostCommentID();
                    new DeleteComment(dialogHandler, commentID).sendDeleteCommentRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(CommunicationCommentActivity.this);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * updates the current comments list
     *
     * @param postCommentsResponse
     */
    private void updateCommentsList(PostCommentsResponse postCommentsResponse) {
        mCommunicationCommentAdapter.addItems(postCommentsResponse.getCommentList());

        Log.d("totalll", String.valueOf(mCommunicationCommentAdapter.getItemCount()));
        isNeedToGetComment = false;
        loading = true;
        if (mCommunicationCommentAdapter.getItemCount() == 0) {
            mEmptyTxt.setVisibility(View.VISIBLE);
        } else {
            mEmptyTxt.setVisibility(View.GONE);
        }
    }

    /**
     * Show comment to user
     *
     * @param mAddCommentResponse new comment response
     */
    protected void showCommentToUser(AddCommentResponse mAddCommentResponse) {
        Comment comment = new Comment();
        comment.setPostCommentID(String.valueOf(mAddCommentResponse.getCommentID()));
        comment.setCommentText(String.valueOf(mAddCommentResponse.getCommentText()));
        comment.setUserName(String.valueOf(mAddCommentResponse.getUserName()));
        comment.setUserPicture(String.valueOf(mAddCommentResponse.getUserPicture()));
        comment.setUserPictureUrl(String.valueOf(mAddCommentResponse.getUserPictureURL()));
        comment.setCommentDate(String.valueOf(mAddCommentResponse.getCommentDate()));
        comment.setCanDelete(mAddCommentResponse.getCanDelete());
        comment.setCommentUserID(String.valueOf(mAddCommentResponse.getUserID()));

        mCommunicationCommentAdapter.add(0, comment);
        mCommentsRecycler.scrollToPosition(0/*mPostCommentsAdapter.getItemCount() - 1*/);
        mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + 1);
        if (mCommunicationCommentAdapter.getItemCount() == 0) {
            mEmptyTxt.setVisibility(View.VISIBLE);
        } else {
            mEmptyTxt.setVisibility(View.GONE);
        }
    }

    /**
     * Course delete confirmation dialog
     */
    public void showCourseDeletedDialog(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set resorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(this);
        // You Can Customise your Title here
        titleText.setText(getString(R.string.error));
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(this.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(getString(R.string.removed_course));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Delete Course from offline database
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), courseID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);

                if (!Helper.isFinishingOrIsDestroyed(CommunicationCommentActivity.this)) {
                    finish();
                }
            }
        });
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }

    }


    @Override
    public void onBottomReached() {
        // TODO Auto-generated method stub
        if (mEndlessRecyclerOnScrollListenerComments != null && !isNeedToGetComment) {
            isNeedToGetComment = true;
            mEndlessRecyclerOnScrollListenerComments.onLoadMore();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.COMMUNICATION_COMMENT + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseID + "&post=" + postID);
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


    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(CommunicationCommentActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(CommunicationCommentActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_comment_send_img) {
            String commentStr = mAddCommentEdt.getText().toString();
            if (!TextUtils.isEmpty(commentStr.trim())) {
                if (Helper.isConnected()) {
                    new AddComment(mHandler, courseID, postID, commentStr).sendAddCommentRequest();
                    startProgressDialog();
                    Helper.hideKeyboardFromWindow(CommunicationCommentActivity.this);
                } else {
                    Helper.showNetworkAlertMessage(CommunicationCommentActivity.this);
                }
            } else {
                Helper.showToast(getString(R.string.comment_blank), Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * Send data to previous activity so we will update comment count and flag
     */

    @Override
    public void onBackPressed() {
        if (commentSend == Flinnt.TRUE) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Comment", commentAddDelete);
            resultIntent.putExtra("CommentCount", "" + commentCount);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        super.onBackPressed();
        finish();
    }
}
