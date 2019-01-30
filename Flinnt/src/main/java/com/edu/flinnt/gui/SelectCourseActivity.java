package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.SelectCourse;
import com.edu.flinnt.gui.SelectCourseAdapter.OnItemClickListener;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.PollListResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostDetailsResponse.Options;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.SelectCourseResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DividerItemDecoration;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.edu.flinnt.Flinnt.POST_TYPE_BLOG;
import static com.edu.flinnt.Flinnt.POST_TYPE_POLL;

/**
 * GUI class to show course list to pick one
 * Display course list when user add post for any specific course
 */
public class SelectCourseActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    private SelectCourseAdapter mCourseSelectAdapter;
    //private ProgressBar progressBar;
    private ArrayList<CourseInfo> mCourseNameList = new ArrayList<CourseInfo>();
    LinearLayoutManager mLinearLayoutManager;
    private int mPostType;
    private String mAction;
    private String className = "";
    private String coursePictureUrl = "";

    //extra
    String mCourseID/*,courseNameStr*/, mPostID, postTitleStr, postDescStr, tagsNameStr, quizQueStr;
    String attachmentUrl, attachmentName;
    int postContentType;
    String postContentUrl = "";
    ArrayList<String> optionTexts, optionIDs, optionCorrection, attachmentNameArray;
    SearchView mSearchView = null;

    public static final String COURSE_ID_REPOST_KEY = "course_id_repost";
    private PollListResponse mPollListResponse;
    private String mPollResultHours;
    TextView noCourseFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.course_list);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mPostType = bundle.getInt(SelectCourseRequest.POST_TYPE_KEY);
            mAction = bundle.getString(SelectCourseRequest.ACTION_KEY);
            if (bundle.containsKey("class_name")) className = bundle.getString("class_name");
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            //if(bundle.containsKey(PostDetailsResponse.COURSE_NAME_KEY)) courseNameStr = bundle.getString(PostDetailsResponse.COURSE_NAME_KEY);
            if (bundle.containsKey(Post.POST_ID_KEY)) mPostID = bundle.getString(Post.POST_ID_KEY);
            if (bundle.containsKey(PostDetailsResponse.TITLE_KEY))
                postTitleStr = bundle.getString(PostDetailsResponse.TITLE_KEY);
            if (bundle.containsKey(PostDetailsResponse.DESCRIPTION_KEY))
                postDescStr = bundle.getString(PostDetailsResponse.DESCRIPTION_KEY);
            if (bundle.containsKey(PostDetailsResponse.TAG_NAME_KEY))
                tagsNameStr = bundle.getString(PostDetailsResponse.TAG_NAME_KEY);
            if (bundle.containsKey(PostDetailsResponse.QUESTION_KEY))
                quizQueStr = bundle.getString(PostDetailsResponse.QUESTION_KEY);
            if (bundle.containsKey(Options.OPTION_TEXT_ID))
                optionTexts = bundle.getStringArrayList(Options.OPTION_TEXT_ID);
            if (bundle.containsKey(Options.OPTION_ID_KEY))
                optionIDs = bundle.getStringArrayList(Options.OPTION_ID_KEY);
            if (bundle.containsKey(Options.OPTION_IS_CORRECT_KEY))
                optionCorrection = bundle.getStringArrayList(Options.OPTION_IS_CORRECT_KEY);

            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_TYPE_KEY))
                postContentType = bundle.getInt(PostDetailsResponse.POST_CONTENT_TYPE_KEY);
            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_URL_KEY))
                postContentUrl = bundle.getString(PostDetailsResponse.POST_CONTENT_URL_KEY);
            if (bundle.containsKey(PostDetailsResponse.ATTACHMENT_URL_KEY))
                attachmentUrl = bundle.getString(PostDetailsResponse.ATTACHMENT_URL_KEY);
            if (mPostType == Flinnt.POST_TYPE_ALBUM) {
                if (bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY))
                    attachmentNameArray = bundle.getStringArrayList(PostDetailsResponse.POST_ATTACHMENT_KEY);
            } else {
                if (bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY))
                    attachmentName = bundle.getString(PostDetailsResponse.POST_ATTACHMENT_KEY);
            }


            if (mPostType == Flinnt.POST_TYPE_POLL) {
                if (bundle.containsKey("PollOptions"))
                    mPollListResponse = (PollListResponse) bundle.getSerializable("PollOptions");
                if (bundle.containsKey(Post.POLL_RESULT_HOURS_KEY))
                    mPollResultHours = bundle.getString(Post.POLL_RESULT_HOURS_KEY);
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        noCourseFound = (TextView) findViewById(R.id.empty_text_courses);

        mRecyclerView = (RecyclerView) findViewById(R.id.course_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, Helper.getDip(0))); // here 0dp count from xml file

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof SelectCourseResponse) {
                            coursePictureUrl = ((SelectCourseResponse) message.obj).getCoursePictureUrl();
                            updateCourseNameList((SelectCourseResponse) message.obj, coursePictureUrl);
                        }


                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof SelectCourseResponse) {
                            SelectCourseResponse response = (SelectCourseResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(SelectCourseActivity.this, getString(R.string.error), response.errorResponse.getMessage(), getString(R.string.close_txt));
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

        ArrayList<Integer> postTypes = new ArrayList<Integer>();
        if (mPostType == Flinnt.POST_TYPE_ALL) {
            postTypes.add(Flinnt.POST_TYPE_BLOG);
//            postTypes.add(Flinnt.POST_TYPE_QUIZ);
            postTypes.add(Flinnt.POST_TYPE_MESSAGE);
            postTypes.add(Flinnt.POST_TYPE_ALBUM);
        } else {
            postTypes.add(mPostType);
        }


        SelectCourseRequest selectCourseRequest = new SelectCourseRequest();
        selectCourseRequest.setUserID(Config.getStringValue(Config.USER_ID));
        selectCourseRequest.setAction(mAction);
        selectCourseRequest.setPostType(postTypes);
        //selectCourseRequest.setExcludeIDs(excludeIDs);

        new SelectCourse(mHandler, selectCourseRequest).sendSelectCourseRequest();
        startProgressDialog();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mCourseSelectAdapter = new SelectCourseAdapter(mCourseNameList);
        mRecyclerView.setAdapter(mCourseSelectAdapter);
        mCourseSelectAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (TextUtils.isEmpty(className)) {
                    if (null != mCourseNameList.get(position)) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(CourseInfo.COURSE_ID_KEY, mCourseSelectAdapter.getItem(position).getCourseID());
                        resultIntent.putExtra(CourseInfo.COURSE_NAME_KEY, mCourseSelectAdapter.getItem(position).getCourseName());
                        resultIntent.putExtra(CourseInfo.COURSE_PICTURE, coursePictureUrl + Flinnt.COURSE_SMALL + File.separator + mCourseSelectAdapter.getItem(position).getCoursePicture());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }


                else if (className.equalsIgnoreCase("DetailActivity")) {
                    int mPostStat = Flinnt.INVALID;
                    Intent rIntent = null;

                    switch (mPostType) {
                        case Flinnt.POST_TYPE_ALBUM:
                            mPostStat = Flinnt.POST_ALBUM_REPOST;
                            rIntent = new Intent(SelectCourseActivity.this, AddCommunicationActivity.class);
                            rIntent.putExtra(Course.COURSE_ID_KEY, mCourseID);
                            rIntent.putExtra(Post.POST_ID_KEY, mPostID);
                            rIntent.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, postContentType);
                            rIntent.putExtra(PostDetailsResponse.DESCRIPTION_KEY, postDescStr);
                            rIntent.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, attachmentUrl);
                            rIntent.putStringArrayListExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, attachmentNameArray);
                            break;
                       /* case Flinnt.POST_TYPE_QUIZ:
                            mPostStat = Flinnt.POST_QUIZ_REPOST;
                            rIntent = new Intent(SelectCourseActivity.this, AddQuizActivity.class);
                            rIntent.putExtra(Course.COURSE_ID_KEY, mCourseID);
                            rIntent.putExtra(Post.POST_ID_KEY, mPostID);
                            rIntent.putExtra(PostDetailsResponse.TAG_NAME_KEY, tagsNameStr);
                            rIntent.putExtra(PostDetailsResponse.QUESTION_KEY, quizQueStr);
                            rIntent.putStringArrayListExtra(Options.OPTION_TEXT_ID, optionTexts);
                            rIntent.putStringArrayListExtra(Options.OPTION_ID_KEY, optionIDs);
                            rIntent.putStringArrayListExtra(Options.OPTION_IS_CORRECT_KEY, optionCorrection);

                            rIntent.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, postContentType);
                            rIntent.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, postContentUrl);
                            rIntent.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, attachmentUrl);
                            rIntent.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, attachmentName);
                            break;*/
                        case POST_TYPE_BLOG:

                            mPostStat = Flinnt.POST_BLOG_REPOST;
                            rIntent = new Intent(SelectCourseActivity.this, AddCommunicationActivity.class);
                            rIntent.putExtra(Course.COURSE_ID_KEY, mCourseID);
                            rIntent.putExtra(Post.POST_ID_KEY, mPostID);
                            rIntent.putExtra(PostDetailsResponse.TITLE_KEY, postTitleStr);
                            rIntent.putExtra(PostDetailsResponse.DESCRIPTION_KEY, postDescStr);
                            rIntent.putExtra(PostDetailsResponse.TAG_NAME_KEY, tagsNameStr);

                            rIntent.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, postContentType);
                            rIntent.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, postContentUrl);
                            rIntent.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, attachmentUrl);
                            if (!attachmentName.isEmpty() && attachmentName != null)
                                rIntent.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, attachmentName);

                            break;
                        case POST_TYPE_POLL:

                            mPostStat = Flinnt.POST_POLL_REPOST;
                            rIntent = new Intent(SelectCourseActivity.this, AddPollActivity.class);
                            rIntent.putExtra(Course.COURSE_ID_KEY, mCourseID);
                            rIntent.putExtra(Post.POST_ID_KEY, mPostID);
                            rIntent.putExtra(PostDetailsResponse.TITLE_KEY, postTitleStr);
                            rIntent.putExtra(PostDetailsResponse.DESCRIPTION_KEY, postDescStr);
                            rIntent.putExtra(PostDetailsResponse.TAG_NAME_KEY, tagsNameStr);
                            rIntent.putExtra("PollOptions", mPollListResponse);
                            rIntent.putExtra(Post.POLL_RESULT_HOURS_KEY, mPollResultHours);
                            rIntent.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, attachmentUrl);
                            if (!attachmentName.isEmpty() && attachmentName != null)
                                rIntent.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, attachmentName);

                        default:
                            break;
                    }

                    if (rIntent != null) {
                        rIntent.putExtra(Flinnt.POST_STATS_ACTION, mPostStat);
                        if (mCourseSelectAdapter.getItem(position) != null) {
                            rIntent.putExtra(COURSE_ID_REPOST_KEY, mCourseSelectAdapter.getItem(position).getCourseID());
                            rIntent.putExtra(CourseInfo.COURSE_NAME_KEY, mCourseSelectAdapter.getItem(position).getCourseName());
                            startActivity(rIntent);
                            finish();
                        }
                    }
                }

            }
        });

    }

    private void updateCourseNameList(SelectCourseResponse selectCourseResponse, String coursePicture) {
        mCourseSelectAdapter.addItems(selectCourseResponse.getCourseInfoList(), coursePicture);
    }


    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(SelectCourseActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(SelectCourseActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Select Course");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = null;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            // MenuItemCompat.getActionView(searchItem);
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {
                    noCourseFound.setVisibility((mCourseSelectAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onQueryTextChange query : " + query);
                    doSearch(query);
                    noCourseFound.setVisibility(View.GONE);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(new OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onClose searchView");
                    doSearch("");
                    noCourseFound.setVisibility((mCourseSelectAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                    return false;
                }
            });
        }
        if (mSearchView != null) {
            try {
                mSearchView.setSearchableInfo(searchManager
                        .getSearchableInfo(this.getComponentName()));

                //final int textViewID = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
                final EditText searchTextView = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchTextView, R.drawable.cursor_color); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                } catch (Exception e) {
                }

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
        return true;
    }

    public void doSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            mCourseSelectAdapter.removeFilter();
        } else {
            mCourseSelectAdapter.setFilter(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                onBackPressed();
                finish(); //onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}