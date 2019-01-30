package com.edu.flinnt.gui;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostViewStatisticsResponse;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 30/9/16.
 */
public class PostViewersSendMessageActivity extends AppCompatActivity {
    public static final String TAG = PostViewersSendMessageActivity.class.getSimpleName();
    private Toolbar mToolbar;
    PagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    public static final int POST_VIEWRS_FRAGMENT_CALLBACK = 999;
    String courseID = "", postID = "", viewerPicUrl = "", courseName = "", comeFromPostOrContent = "";
    ArrayList<String> selectedViewersIDs;
    String mSelectedTab = "";

    private PostViewersFragment mPostViewersFragment = null;
    private PostViewersLikeFragment mPostViewersLikeFragment = null;
    private PostViewersCommentFragment mPostViewersCommentFragment = null;
    private CharSequence tabTitles[] = {"Viewers", "Likes", "Comments"};

    public static String COURSE_ID_KEY = "course_id";
    public static String COURSE_NAME_KEY = "course_name";
    public static String POST_ID_KEY = "post_id";
    public static String USER_PICTURE_URL_KEY = "user_picture_url";
    public static String POST_OR_CONTENT_KEY = "post_or_content";
    public static String CURRENT_TAB_KEY = "current_tab";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.post_viewers_message_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            courseID = bundle.getString(Course.COURSE_ID_KEY);
            courseName = bundle.getString(Course.COURSE_NAME_KEY);
            postID = bundle.getString(Post.POST_ID_KEY);
            viewerPicUrl = bundle.getString(PostViewStatisticsResponse.USER_PICTURE_URL_KEY);
            if (bundle.containsKey(getString(R.string.select_tab))) {
                mSelectedTab = bundle.getString(getString(R.string.select_tab));
            }
            if (bundle.containsKey("comeFrom"))
                comeFromPostOrContent = bundle.getString("comeFrom");
        }

        viewPager = (ViewPager) findViewById(R.id.post_tagslist_viewpager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.post_tagslist_tabs);


        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if (!mSelectedTab.equals("")) {
            if (mSelectedTab.equals(getString(R.string.likes))) {
                viewPager.setCurrentItem(1);
            } else if (mSelectedTab.equals(getString(R.string.comments))) {
                viewPager.setCurrentItem(2);
            } else {
                viewPager.setCurrentItem(0);
            }
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //  doMyUpdate();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // doMyUpdate();
            }
        });

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


    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                if (null == mPostViewersFragment) {
                    mPostViewersFragment = PostViewersFragment.newInstance(courseID, courseName, postID, viewerPicUrl, comeFromPostOrContent, position);
                }
                return mPostViewersFragment;
            } else if (position == 1) {
                if (null == mPostViewersLikeFragment) {
                    mPostViewersLikeFragment = PostViewersLikeFragment.newInstance(courseID, courseName, postID, viewerPicUrl, comeFromPostOrContent, position);
                }
                return mPostViewersLikeFragment;
            } else if (position == 2) {
                if (null == mPostViewersCommentFragment) {
                    mPostViewersCommentFragment = PostViewersCommentFragment.newInstance(courseID, courseName, postID, viewerPicUrl, comeFromPostOrContent, position);
                }
                return mPostViewersCommentFragment;
            } else return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PostViewersSendMessageActivity.POST_VIEWRS_FRAGMENT_CALLBACK) {
            if (resultCode == RESULT_OK) {
                selectedViewersIDs = new ArrayList<>();
                selectedViewersIDs = data.getStringArrayListExtra("userArray");
                mPostViewersFragment.mPostViewersAdapter.selectedUserId = selectedViewersIDs;
                viewPager.setCurrentItem(0);
                mPostViewersFragment.mPostViewersAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "PostViewersSendMessage");
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
}
