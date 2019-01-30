package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentViewers;
import com.edu.flinnt.core.PostViewStatistics;
import com.edu.flinnt.protocol.ContentViewersRequest;
import com.edu.flinnt.protocol.ContentViewersResponse;
import com.edu.flinnt.protocol.PostViewersResponse;
import com.edu.flinnt.util.DividerItemDecoration;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

/**
 * GUI class to show post viewers
 */
public class ContentViewersActivity extends AppCompatActivity {

    public static final String TAG = ContentViewersActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView totalLikesTxt, totalViewersTxt, totalCommentsTxt;
    private ContentViewers mContentViewers;
    private ContentViewersRequest mContentViewersRequest;
    private ContentViewersAdapter mContentViewersAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    ArrayList<ContentViewersResponse.Viewer> mPostViewersItems = new ArrayList<ContentViewersResponse.Viewer>();
    public Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private String courseID = "", contentID = "", totalLikes = "", totalViewers = "", totalComments = "", viewerPicUrl = "";

    private LinearLayout layoutLikes, layoutComments;
    private View pipeView1, pipeView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.content_viewers_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            courseID = bundle.getString(ContentsDetailActivity.COURSE_ID_KEY);
            contentID = bundle.getString(ContentsDetailActivity.CONTENT_ID_KEY);
            totalViewers = bundle.getString(ContentsDetailActivity.TOTAL_VIEWERS_KEY);
            totalLikes = bundle.getString(ContentsDetailActivity.TOTAL_LIKES_KEY);
            totalComments = bundle.getString(ContentsDetailActivity.TOTAL_COMMENTS_KEY);
            viewerPicUrl = bundle.getString(ContentsDetailActivity.USER_PICTURE_URL_KEY);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.post_viewers_recycler_view);
        totalViewersTxt = (TextView) findViewById(R.id.totle_views_txt_viewers);
        totalLikesTxt = (TextView) findViewById(R.id.totle_likes_txt_viewers);
        totalCommentsTxt = (TextView) findViewById(R.id.totle_comments_txt_viewers);
        layoutLikes = (LinearLayout) findViewById(R.id.layout_likes);
        layoutComments = (LinearLayout) findViewById(R.id.layout_comments);
        pipeView1 = (View) findViewById(R.id.pipe_view1);
        pipeView2 = (View) findViewById(R.id.pipe_view2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, Helper.getDip(78))); // here 78dp count from xml file

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        updateViewersList((ContentViewersResponse) message.obj);

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof PostViewersResponse) {
                            PostViewersResponse response = (PostViewersResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(ContentViewersActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
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
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Post Viewers");
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
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        totalViewersTxt.setText(totalViewers + PostViewStatistics.VIEWERS);
        totalLikesTxt.setText(totalLikes + PostViewStatistics.LIKES);
        totalCommentsTxt.setText(totalComments + PostViewStatistics.COMMENTS);

        if (TextUtils.isEmpty(totalLikes)) {
            layoutLikes.setVisibility(View.GONE);
            layoutComments.setVisibility(View.GONE);
            //totalCommentsTxt.setText(totalComments + PostViewStatistics.REPLIES);

            pipeView1.setVisibility(View.GONE);
            pipeView2.setVisibility(View.GONE);
        }

        if (null == mContentViewers) {
            mContentViewersRequest = new ContentViewersRequest();

            mContentViewers = new ContentViewers(mHandler, courseID, contentID);
            mContentViewers.sendPostViewersRequest(mContentViewersRequest);
            startProgressDialog();
        }
        refreshView();
    }

    /**
     * Update post list view
     */
    public void refreshView() {
        // use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mContentViewersAdapter = new ContentViewersAdapter(mPostViewersItems, viewerPicUrl);
        mRecyclerView.setAdapter(mContentViewersAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager, false) {

            @Override
            public void onLoadMore(int current_page) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("onLoadMore : has_more : " + mContentViewers.getLastResponse().getData().getHasMore());
                if (mContentViewers.getLastResponse().getData().getHasMore() > 0) {
                    // Reset offset to new request - New offset = old offset + max
                    mContentViewersRequest.setOffSet(mContentViewersRequest.getOffSet() + mContentViewersRequest.getMaxFetch());
                    mContentViewers.sendPostViewersRequest(mContentViewersRequest);
                }
            }
        });
    }

    /**
     * Update the list of who visited the post
     *
     * @param contentViewersResponse post viewers response
     */
    private void updateViewersList(ContentViewersResponse contentViewersResponse) {
        mContentViewersAdapter.addItems(contentViewersResponse.getData().getViewers());
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ContentViewersActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ContentViewersActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); //onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}