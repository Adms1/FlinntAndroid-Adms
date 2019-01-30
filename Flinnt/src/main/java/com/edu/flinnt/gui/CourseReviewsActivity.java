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
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CourseReviews;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CourseReviewListRequest;
import com.edu.flinnt.protocol.CourseReviewListResponse;
import com.edu.flinnt.protocol.UserReview;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

/**
 * GUI class to show all ratings for a course
 */
public class CourseReviewsActivity extends AppCompatActivity {

    public static final String TAG = CourseReviewsActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    CourseReviewsAdapter mCourseReviewsAdapter;
    private ProgressDialog mProgressDialog = null;
    private String courseId;
    private ArrayList<UserReview> mCourseReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.course_ratings_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if (null != bundle) {
            courseId = bundle.getString(BrowsableCourse.ID_KEY);
            getSupportActionBar().setTitle(bundle.getString(BrowsableCourse.NAME_KEY));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_course_ratings);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
//		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, Helper.getDip(78))); // here 78dp count from xml file

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof CourseReviewListResponse) {
                            CourseReviewListResponse mCourseReviewListResponse = (CourseReviewListResponse)message.obj;
                            fillReviews(mCourseReviewListResponse.getUserReviews());
                        }
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        break;
                    default:
                        /*
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(message);
                }
            }
        };

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mCourseReviewsList = new ArrayList<>();
        mCourseReviewsAdapter = new CourseReviewsAdapter(mCourseReviewsList);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mCourseReviewsAdapter);

        CourseReviews mCourseReviews = null;
        CourseReviewListRequest mCourseReviewListRequest = null;

        mCourseReviewListRequest = new CourseReviewListRequest();
        mCourseReviews = new CourseReviews(mHandler, courseId, mCourseReviewListRequest.getMax());
        mCourseReviews.sendCourseReviewListRequest();
        startProgressDialog();

        final CourseReviews finalMCourseReviews = mCourseReviews;
        final CourseReviewListRequest finalMCourseReviewListRequest = mCourseReviewListRequest;
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager, false) {
            @Override
            public void onLoadMore(int current_page) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("onLoadMore : has_more : " + finalMCourseReviews.getLastCourseReviewListResponse().getHasMore());
                if (finalMCourseReviews.getLastCourseReviewListResponse().getHasMore() > 0) {
                    // Reset offset to new request - New offset = old offset + max
                    finalMCourseReviewListRequest.setOffset(finalMCourseReviewListRequest.getOffset() + finalMCourseReviewListRequest.getMax());
                    finalMCourseReviews.sendCourseReviewListRequest();
                }
            }
        });
    }

    /**
     * Display reviews on UI
     * @param userReviews list of user reviews to display
     */
    private void fillReviews(ArrayList<UserReview> userReviews) {
        if (null != userReviews) {
            mCourseReviewsAdapter.addItems(userReviews);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); //onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Course Reviews");
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
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(CourseReviewsActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(CourseReviewsActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
}
