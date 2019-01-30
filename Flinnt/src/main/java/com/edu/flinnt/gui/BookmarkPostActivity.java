package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PostListAdapter;
import com.edu.flinnt.adapter.PostListAdapter.OnItemClickListener;
import com.edu.flinnt.core.BookmarkPost;
import com.edu.flinnt.core.BookmarkPost.BookmarkPostResponse;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * GUI class to show bookmarks
 */
public class BookmarkPostActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog mProgressDialog = null;
    Handler mHandler = null;
    RecyclerView mRecyclerView;
    ArrayList<Post> mPosts;
    PostListAdapter mAdapter;
    SearchView mSearchView = null;
    RelativeLayout noBookmarksFoundLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.bookmark_post);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        noBookmarksFoundLayout = (RelativeLayout) findViewById(R.id.layout_no_bookmarks);

        mRecyclerView = (RecyclerView) findViewById(R.id.bookmark_post_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mPosts = new ArrayList<Post>();

        mAdapter = new PostListAdapter(BookmarkPostActivity.this, mPosts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Intent postDetailIntent = null;
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(BookmarkPostActivity.this);
                    } else {
                        if (null != mAdapter.getItem(position)) {
                            //@Nikhil 2662018
                            if (mAdapter.getItem(position).getPostTypeInt() == Flinnt.POST_STORY_LINK) {
                                if (Helper.isConnected()) {

                                    Intent inapp = new Intent(BookmarkPostActivity.this, InAppPreviewActivity.class);
                                    inapp.putExtra(Flinnt.KEY_INAPP_TITLE, mAdapter.getItem(position).getCourseName());
                                    inapp.putExtra(Flinnt.KEY_INAPP_URL, mAdapter.getItem(position).getAttachment());
                                    inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                                    inapp.putExtra(Flinnt.NOTIFICATION_SCREENID, 0);
                                    view.getContext().startActivity(inapp);
                                } else {
                                    Helper.showNetworkAlertMessage(BookmarkPostActivity.this.getApplicationContext());
                                }
                            } else {
                                postDetailIntent = new Intent(BookmarkPostActivity.this, PostDetailActivity.class);
                                postDetailIntent.putExtra(Post.COURSE_ID_KEY, mAdapter.getItem(position).getCourseID());
                                postDetailIntent.putExtra(Post.POST_ID_KEY, mAdapter.getItem(position).getPostID());
                                postDetailIntent.putExtra(Course.COURSE_CAN_COMMENT, mAdapter.getItem(position).getCanComment());
                                postDetailIntent.putExtra(Course.COURSE_NAME_KEY, mAdapter.getItem(position).getCourseName());
                                startActivity(postDetailIntent);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                swipeRefreshLayout.setRefreshing(false);
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        //Helper.showToast("Success");
                        stopProgressDialog();
                        updateBookmarkPostList((BookmarkPostResponse) message.obj);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        break;
                    case Flinnt.FAILURE:
                        //Helper.showToast("Failure");
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        noBookmarksFoundLayout.setVisibility(View.VISIBLE);
                        if (null != ((BookmarkPostResponse) message.obj).errorResponse) {
                            Helper.showAlertMessage(BookmarkPostActivity.this, "Error", ((BookmarkPostResponse) message.obj).errorResponse.getMessage(), "CLOSE");
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(BookmarkPostActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    invalidateOptionsMenu();
                    refreshList();
                }
            }
        });

        sendRequest();
        startProgressDialog();

    }

    /**
     * Sends a bookmark request
     */
    private void sendRequest() {
        new BookmarkPost(mHandler).sendBookmarkPostRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.BOOKMARKS_POST + "&user=" + Config.getStringValue(Config.USER_ID));
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
        super.onResume();
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
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onQueryTextSubmit query results : " + mAdapter.getItemCount());
                    noBookmarksFoundLayout.setVisibility(mAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onQueryTextChange query : " + query);
                    doSearch(query);
                    noBookmarksFoundLayout.setVisibility(View.GONE);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(new OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onClose searchView");
                    doSearch("");
                    noBookmarksFoundLayout.setVisibility(mAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
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

    /**
     * Search for bookmarks
     *
     * @param query search query
     */
    public void doSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.removeFilter();
        } else {
            mAdapter.setFilter(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BookmarkPostActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BookmarkPostActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    /**
     * Display bookmarks list
     *
     * @param bookmarkPostResponse bookmark list response
     */
    private void updateBookmarkPostList(BookmarkPostResponse bookmarkPostResponse) {
        mAdapter.addItems(bookmarkPostResponse.getPostList());
        if ((mAdapter.getItemCount() == 0)) {
            noBookmarksFoundLayout.setVisibility(View.VISIBLE);
        } else {
            noBookmarksFoundLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Clears the data and make new request
     */
    private void refreshList() {
        //if(Helper.isConnected()){
        Requester.getInstance().cancelPendingRequests(BookmarkPost.TAG);
        mAdapter.clearAllData();
        sendRequest();
		/*}else{
			Helper.showNetworkAlertMessage(this);
		}*/
    }
}
