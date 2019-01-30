package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentsList;
import com.edu.flinnt.core.CourseReviewRead;
import com.edu.flinnt.core.CourseReviewWrite;
import com.edu.flinnt.core.InviteUsers;
import com.edu.flinnt.core.MenuBanner;
import com.edu.flinnt.core.MuteSetting;
import com.edu.flinnt.core.PostListMenu;
import com.edu.flinnt.core.PostStatistics;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.gui.SignUpActivity.validateErrorCodes;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.CourseReviewReadRequest;
import com.edu.flinnt.protocol.CourseReviewReadResponse;
import com.edu.flinnt.protocol.CourseReviewWriteResponse;
import com.edu.flinnt.protocol.InviteUsersRequest;
import com.edu.flinnt.protocol.InviteUsersResponse;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.MenuBannerResponse;
import com.edu.flinnt.protocol.MuteSettingRequest;
import com.edu.flinnt.protocol.MuteSettingResponse;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostListMenuResponse;
import com.edu.flinnt.protocol.PostStatisticsResponse;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.OnUpdateMenuListner;
import com.edu.flinnt.util.RippleBackground;
import com.edu.flinnt.util.RoundedCornersTransformation;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * GUI class to show course details
 */
public class CourseDetailsActivity extends AppCompatActivity implements OnClickListener, AppBarLayout.OnOffsetChangedListener, OnUpdateMenuListner {

    public static final String TAG = "CourseDetailsActivity";
    public static final int POST_COMMENT_CALL_BACK = 161;
    public static final int ADD_NEW_CONTENT = 21;//request code to add new post , alert , album , quiz
    public static final String ADDED_CONTENT_DATA = "data";//key for payload of added new post , alert , album , quiz
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private CourseDeatilsPagerAdapter mCourseDeatilsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabs;
    private CommunicationFragment mCommunicationFragment;
    private ContentsFragment mContentsFragment;
    private ImageView courseDetailImg;
    private FloatingActionsMenu mAddFloatingAction;
    private FloatingActionButton mFabAlert, mFabMessage, mFabAlbum, mFabQuiz, mFabPost, mFabCourse, mFabCommunication, mFabPoll;
    public FloatingActionButton mContentEditFloatingAction;
    private RelativeLayout fabBackground;
    private Resources res = FlinntApplication.getContext().getResources();
    private Handler mHandler;
    private String courseID = "", coursePictureUrl = "", coursePicture = "", courseName = "", courseAllowedRoles = "";
    public PostListMenuResponse mPostListMenuResponse = null;
    private MenuBannerResponse mMenuBannerResponse;
    private SearchView mSearchView = null;
    private String filterBy = null;
    private int filterPostContentType = Flinnt.INVALID;
    private int filterPostType = Flinnt.INVALID;

    // For Invite User Dialog
    private RadioButton radioLearners = null;
    private RadioButton radioTeachers = null;
    private EditText editEmailNumber = null;
    private Button buttonDone = null;
    private Boolean isLearnerChecked = true;
    private Boolean isTeacherChecked = false;
    private String emailNumber = null;
    private InviteUsersRequest mInviteUsersRequest = new InviteUsersRequest();
    private InviteUsers mInviteUsers;
    private int inviteRole = Flinnt.COURSE_ROLE_LEARNER;
    public static final String REGEX_NUMBER = "^[0-9]*$";
    private String invited_email_mob = "";
    private ProgressDialog mProgressDialog;
    private boolean isCourseUpdate = false;
    private int muteSettingRequestType = MuteSetting.GET_SETTING;
    private AppBarLayout appBarLayout;
    private boolean isTeacher = false;
    private int currentPage = 0;

    int contentItemCount = 0;
    RippleBackground rippleBackground, rippleBackgroundFabButton;
    private boolean isJoined = false;
    private String comeFromActivity = "";
    public static final int CONTENT_UPDATE_CALL_BACK = 122;
    private int nextScreenId = 0;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.IS_NETWORK_TOAST = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.course_details_activity);

        //Log.d(TAG,"CourseDetailsActivity.java");

        Bundle bundle = getIntent().getExtras();

        if (null != bundle) {

            //Log.d(TAG,"in bundle");
            if (bundle.containsKey(MyCoursesActivity.IS_JOIN))
                isJoined = bundle.getBoolean(MyCoursesActivity.IS_JOIN);
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                courseID = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(Course.COURSE_PICTURE_KEY))
                coursePicture = bundle.getString(Course.COURSE_PICTURE_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                courseName = bundle.getString(Course.COURSE_NAME_KEY);
            if (bundle.containsKey(Course.ALLOWED_ROLES_KEY))
                courseAllowedRoles = bundle.getString(Course.ALLOWED_ROLES_KEY);
            if (bundle.containsKey(FilterByPostActivity.FILTER_BY))
                filterBy = bundle.getString(FilterByPostActivity.FILTER_BY);
            if (bundle.containsKey(FilterByPostActivity.FILTER_POST_CONTENT_TYPE))
                filterPostContentType = bundle.getInt(FilterByPostActivity.FILTER_POST_CONTENT_TYPE);
            if (bundle.containsKey(FilterByPostActivity.FILTER_POST_TYPE))
                filterPostType = bundle.getInt(FilterByPostActivity.FILTER_POST_TYPE);

            //@Chirag: 10/08/2018 5:06*******
            if (bundle.containsKey(Flinnt.NEXT_SCREENID)) {
                nextScreenId = bundle.getInt(Flinnt.NEXT_SCREENID);
            }

            //@Chirag: 13/08/2018 for LCC Notification back
            if (bundle.containsKey("isFromNotification")) {
                nextScreenId = bundle.getInt("isFromNotification");
                //Log.d(TAG,"contain is from notifiation : " + nextScreenId);
            }

            if (bundle.containsKey(Config.USER_ID)) {
                userId = bundle.getString(Config.USER_ID);

                if (!TextUtils.isEmpty(userId) && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userId);

                }
            }//******

        }

        //Log.d(TAG, "isFromNoti : " + bundle.getInt("isFromNotification"));

        mToolbar = (Toolbar) findViewById(R.id.courses_details_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        setTitleToolbar();

        courseDetailImg = (ImageView) findViewById(R.id.course_details_image);
        courseDetailImg.setColorFilter(getResources().getColor(R.color.course_image_transperent_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
        // Setup FloatingActionMenu
        mAddFloatingAction = (FloatingActionsMenu) findViewById(R.id.course_details_fab_menu);

        //mAddFloatingAction.setVisibility(View.GONE);

        mContentEditFloatingAction = (FloatingActionButton) findViewById(R.id.fab_content);
        mContentEditFloatingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {
                    Intent intent = new Intent(CourseDetailsActivity.this, ContentEditActivity.class);
                    intent.putExtra(Course.COURSE_NAME_KEY, courseName);
                    intent.putExtra(Course.COURSE_ID_KEY, courseID);
                    startActivityForResult(intent, CONTENT_UPDATE_CALL_BACK);
                }
            }
        });
        rippleBackgroundFabButton = (RippleBackground) findViewById(R.id.fab_content_ripple);

        if (!TextUtils.isEmpty(coursePicture)) {
            //Log.d(TAG, "in if.");
            updateCoursePicture();
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.course_details_appbar);
        mTabs = (TabLayout) findViewById(R.id.course_deatils_tabs);

// Create the mContentsAdapter that will return a fragment for each of the three
// primary sections of the activity.
        mCourseDeatilsPagerAdapter = new CourseDeatilsPagerAdapter(getSupportFragmentManager());

// Set up the ViewPager with the sections mContentsAdapter.
        mViewPager = (ViewPager) findViewById(R.id.course_deatils_viewpager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Set up the ViewPager with the sections mContentsAdapter.
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mCourseDeatilsPagerAdapter != null)
                    mCourseDeatilsPagerAdapter.updateTabTitle(position);
                doSearch("", false);
                if (mSearchView != null && !mSearchView.isIconified()) {
                    mSearchView.setIconified(true);
                    mSearchView.clearFocus();
                    invalidateOptionsMenu();
                }
                currentPage = mViewPager.getCurrentItem();
                if (comeFromActivity.equalsIgnoreCase("ContentDetailActivity") || comeFromActivity.equalsIgnoreCase("QuizHelpActivity")) {
                    comeFromActivity = "";
                } else {
                    if (Helper.isConnected()) {
                        if (currentPage == 1) {
                            //Log.d(TAG,"current page : 1 ");
                            mAddFloatingAction.setVisibility(View.GONE);
                            updateMenuBanner(mMenuBannerResponse);
                            invalidateOptionsMenu();
                            rippleBackground.stopRippleAnimation();
                        } else {
                            mContentEditFloatingAction.setVisibility(View.GONE);
                            updateMenuBanner(mMenuBannerResponse);
                            invalidateOptionsMenu();
                        }
                    } else {
                        if (currentPage == 1) {
                            mAddFloatingAction.setVisibility(View.GONE);
                            mContentEditFloatingAction.setVisibility(View.GONE);
                        } else {
                            mContentEditFloatingAction.setVisibility(View.GONE);
                            mAddFloatingAction.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mCourseDeatilsPagerAdapter);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setupWithViewPager(mViewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < mTabs.getTabCount(); i++) {
            TabLayout.Tab tab = mTabs.getTabAt(i);
            tab.setCustomView(mCourseDeatilsPagerAdapter.getTabView(i));
        }
        // First tab color set White
        mCourseDeatilsPagerAdapter.updateTabTitle(0);
        //Log.d("Couu","viewpager seted.");

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS: {
                        if (message.obj instanceof PostStatisticsResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE PostStatisticsResponse : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE PostStatisticsResponse : " + message.obj.toString());

                            updateTabLayout((PostStatisticsResponse) message.obj);
                        } else if (message.obj instanceof MenuBannerResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE MenuBannerResponse : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE MenuBannerResponse : " + message.obj.toString());

                            mMenuBannerResponse = (MenuBannerResponse) message.obj;
                            updateMenuBanner((MenuBannerResponse) message.obj);
                        } else if (message.obj instanceof PostListMenuResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE PostListMenuResponse : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE PostListMenuResponse : " + message.obj.toString());

                            mPostListMenuResponse = (PostListMenuResponse) message.obj;
                            invalidateOptionsMenu();
                        } else if (message.obj instanceof MuteSettingResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE MuteSettingResponse : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE MuteSettingResponse : " + message.obj.toString());

                            if (muteSettingRequestType != MuteSetting.SET_SETTING) {
                                showMuteDialog((MuteSettingResponse) message.obj);
                            }

                        } else if (message.obj instanceof InviteUsersResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE InviteUsers : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE InviteUsers : " + message.obj.toString());

                            if (((InviteUsersResponse) message.obj).getSent() == Flinnt.TRUE) {
                                Helper.showToast("Email-Id/Number : " + invited_email_mob + " is invited", Toast.LENGTH_SHORT);
                                invited_email_mob = "";
                            }
                        } else if (message.obj instanceof CourseReviewReadResponse) {
                            LogWriter.write("SUCCESS_RESPONSE Rating : " + message.obj.toString());

                            //Log.d(TAG, "SUCCESS_RESPONSE Rating : " + message.obj.toString());

                            String rating = "5.0";
                            String reviewText = "";
                            if (((CourseReviewReadResponse) message.obj).getData().getReviewed().equals("1")) {
                                rating = ((CourseReviewReadResponse) message.obj).getData().getReview().getRating();
                                reviewText = ((CourseReviewReadResponse) message.obj).getData().getReview().getText();
                                showRateReviewDialog(rating, reviewText);
                            } else {
                                showRateReviewDialog(rating, reviewText);
                            }
                        } else if (message.obj instanceof CourseReviewWriteResponse) {
                            //UserReview userReview = ((CourseReviewWriteResponse) message.obj).getUserReview();

                            //  Helper.showToast("Successfully Joined", Toast.LENGTH_LONG);
                        }
                    }
                    break;
                    case Flinnt.FAILURE: {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());

                        //Log.d(TAG, "FAILURE_RESPONSE : " + message.obj.toString());

                        if (message.obj instanceof InviteUsersResponse) {
                            InviteUsersResponse failResponse = (InviteUsersResponse) message.obj;
                            invited_email_mob = "";
                            if (failResponse.errorResponse != null) {
                                Helper.showAlertMessage(CourseDetailsActivity.this, "Invite User", failResponse.errorResponse.getMessage(), "Close");

                            }
                        } else if (message.obj instanceof MenuBannerResponse) {
                            updateMenuBanner((MenuBannerResponse) message.obj);
                        }
                    }
                    break;
                    default:
                        super.handleMessage(message);
                        break;
                }
            }
        };

        if (!TextUtils.isEmpty(courseID)) {
            //Log.d(TAG, "courseId is not empty ..");
            Common.IS_NETWORK_TOAST = false;
            //send MenuBanner request
            new MenuBanner(mHandler, courseID).sendMenuBannerRequest();

            startProgressDialog();
            //send PostListMenu request
            new PostListMenu(mHandler, courseID).sendPostListMenuRequest();
        }


    }

    private void setTitleToolbar() {
        boolean needToAddView = false;

        if (mToolbarTitle == null) {
            mToolbarTitle = new TextView(this);
            needToAddView = true;
        }

        mToolbarTitle.setText(courseName);
        mToolbarTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mToolbarTitle.setTextSize(20);
        mToolbarTitle.setSingleLine(true);
        mToolbarTitle.setTextAppearance(this, android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
        mToolbarTitle.setTextColor(Color.WHITE);

        if (needToAddView) {
            mToolbar.addView(mToolbarTitle);
        }

    }

    /**
     * Save and update course picture
     */
    private void updateCoursePicture() {
        if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("updateCoursePicture");
        final String imgUrl = Config.getStringValue(Config.COURSE_PICTURE_URL) + Flinnt.COURSE_XLARGE + File.separator + coursePicture;

        displayCourseImage(imgUrl);

    }

    private void displayCourseImage(String imgUrl) {
        LogWriter.write("Course Image : " + imgUrl);
        Glide.with(CourseDetailsActivity.this)
                .load(imgUrl)
                .placeholder(R.drawable.default_course_image)
                .bitmapTransform(new RoundedCornersTransformation(CourseDetailsActivity.this, 0, 0))
                .into(courseDetailImg);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Log.d(TAG,"CourseDetailsActivity onNewIntent()");
        Bundle bundle = intent.getExtras();
        if (null != bundle) {

            if (LogWriter.isValidLevel(Log.DEBUG))
                LogWriter.write("contains, isCourseRemoved : " + bundle.containsKey("isCourseRemoved") + ", " + bundle.getBoolean("isCourseRemoved"));

            if (bundle.containsKey("isCourseRemoved") && bundle.getBoolean("isCourseRemoved")) {
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), courseID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            boolean isPostDeleted = bundle.getBoolean("isPostDeleted");
            int isFromNotification = bundle.getInt("isFromNotification");
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("isPostDeleted : " + isPostDeleted + " , isFromNotification : " + isFromNotification);


            if (isFromNotification == Flinnt.TRUE) {
                nextScreenId = isFromNotification;
                if (!isPostDeleted) {
                    filterBy = null;
                    courseID = bundle.getString(Course.COURSE_ID_KEY);
                    coursePicture = bundle.getString(Course.COURSE_PICTURE_KEY);
                    courseName = bundle.getString(Course.COURSE_NAME_KEY);
                    courseAllowedRoles = bundle.getString(Course.ALLOWED_ROLES_KEY);
                    comeFromActivity = bundle.getString("comeFrom");

                    //Log.d(TAG, "comeFrom : " + comeFromActivity);

                    if (comeFromActivity.equalsIgnoreCase("ContentDetailActivity") || comeFromActivity.equalsIgnoreCase("QuizHelpActivity")) {
                        try {


                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {

                                    mAddFloatingAction.setVisibility(View.GONE);//@Chirag:20/08/2018
                                    mViewPager.setCurrentItem(1);

                                }
                            });
                        } catch (Exception e) {
                            //Log.d(TAG, "catch Exception : " + e.getMessage());
                            LogWriter.write("Exception :: " + e.getMessage());
                        }
                    }
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onNewIntent :: courseID : " + courseID + " , mCourseNameTxt : " + courseName + " , coursePicture : " + coursePicture + ", courseAllowedRoles : " + courseAllowedRoles);

                    updateCoursePicture();
                    setTitleToolbar();
                    //send MenuBanner request
                    new MenuBanner(mHandler, courseID).sendMenuBannerRequest();

                    //send PostListMenu request
                    new PostListMenu(mHandler, courseID).sendPostListMenuRequest();
                } else {
                    finish();
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Common.IS_NETWORK_TOAST = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //.d(TAG,"CourseDetailsActivity onResume");
        //send PostStatistics request
        appBarLayout.addOnOffsetChangedListener(this);
        if (filterBy == null) {
            new PostStatistics(mHandler, null, courseID).sendPostStatisticsRequest();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    /**
     * Displays the rate/review dialog
     */
    private void showRateReviewDialog(String rating, String reviewText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseDetailsActivity.this);
        // set view
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog_design, null);
        alertDialogBuilder.setView(dialogView);

        TextView title = new TextView(this);
        title.setText("Review by " + Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.ColorPrimary));
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(title);
        alertDialogBuilder.setPositiveButton("SUBMIT", null);

        final AlertDialog inviteDialog = alertDialogBuilder.create();
        inviteDialog.show();

        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) inviteDialog.findViewById(R.id.rb_rate);
        final EditText etReview = (EditText) inviteDialog.findViewById(R.id.et_review);
        Button mSubmitBtn = inviteDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        ratingBar.setRating(Float.parseFloat(rating));
        etReview.setText(reviewText);
        etReview.setSelection(etReview.getText().length());

        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {
                    if (ratingBar.getRating() == 0) {
                        Helper.showToast(getString(R.string.give_your_rating), Toast.LENGTH_LONG);
                    } else {
                        if (courseID != null) {
                            CourseReviewWrite mCourseReviewWrite = new CourseReviewWrite(mHandler, courseID);
                            mCourseReviewWrite.setRatings(String.valueOf(ratingBar.getRating()));
                            mCourseReviewWrite.setReview(etReview.getText().toString());
                            mCourseReviewWrite.sendCourseWriteRequest();
                            inviteDialog.dismiss();
                            startProgressDialog();
                        }
                    }
                }
            }
        });
    }

    /**
     * Show mute settings dialog which lets user to enable or disable course notifications
     *
     * @param muteSettingResponse mutesettings response
     */
    protected void showMuteDialog(final MuteSettingResponse muteSettingResponse) {
        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseDetailsActivity.this);
            // set view
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.mute_dialog_design, null);
            alertDialogBuilder.setView(dialogView);

            TextView title = new TextView(this);
            title.setText("Mute");
            title.setPadding(40, 40, 40, 40);
            title.setGravity(Gravity.CENTER_VERTICAL);
            title.setTextColor(getResources().getColor(R.color.ColorPrimary));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(title);
            alertDialogBuilder.setPositiveButton("DONE", null);


            final AlertDialog muteDialog;
            muteDialog = alertDialogBuilder.create();
            muteDialog.show();

            final CheckBox switchCommentReply = (CheckBox) muteDialog.findViewById(R.id.course_comment_reply_cb);
            switchCommentReply.setChecked(muteSettingResponse.getCourseMuteComment().equals(Flinnt.ENABLED) ? true : false);
            if (muteSettingResponse.getAccountMuteComment().equals(Flinnt.ENABLED)) {
                switchCommentReply.setEnabled(false);
                switchCommentReply.setButtonDrawable(R.drawable.checked_mute);
            }

            final CheckBox switchSound = (CheckBox) muteDialog.findViewById(R.id.course_sound_cb);
            switchSound.setChecked(muteSettingResponse.getCourseMuteSound().equals(Flinnt.ENABLED) ? true : false);
            if (muteSettingResponse.getAccountMuteSound().equals(Flinnt.ENABLED)) {
                switchSound.setEnabled(false);
                switchSound.setButtonDrawable(R.drawable.checked_mute);
            }

            final TextView muteMessage = (TextView) muteDialog.findViewById(R.id.course_mute_message);
            if (!switchCommentReply.isEnabled() && !switchSound.isEnabled()) {
                muteMessage.setVisibility(View.VISIBLE);
            } else {
                muteMessage.setVisibility(View.GONE);
            }

            muteDialog.findViewById(R.id.layout_comment_reply).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switchCommentReply.isEnabled()) {
                        switchCommentReply.setChecked(!switchCommentReply.isChecked());
                    }
                }
            });
            muteDialog.findViewById(R.id.layout_mute_all).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switchSound.isEnabled()) {
                        switchSound.setChecked(!switchSound.isChecked());
                    }
                }
            });

            Button buttonMuteDialogDone = muteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonMuteDialogDone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                    } else {

                        if (muteMessage.getVisibility() == View.GONE) {
                            MuteSettingRequest mMuteSettingRequest = new MuteSettingRequest();
                            mMuteSettingRequest.setCourseID(muteSettingResponse.getCourseID());
                            mMuteSettingRequest.setMuteComment(switchCommentReply.isChecked() ? Flinnt.ENABLED : Flinnt.DISABLED);
                            mMuteSettingRequest.setMuteSound(switchSound.isChecked() ? Flinnt.ENABLED : Flinnt.DISABLED);
                            MuteSetting mMuteSetting = new MuteSetting(mHandler, mMuteSettingRequest, MuteSetting.COURSE_SETTING);
                            muteSettingRequestType = MuteSetting.SET_SETTING;
                            mMuteSetting.setRequestType(MuteSetting.SET_SETTING);
                            mMuteSetting.sendMuteSettingRequest();

                            startProgressDialog();
                        }
                        //muteDialog.hide();
                        muteDialog.cancel();
                    }
                }
            });
            muteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void updateTabLayout(PostStatisticsResponse postStatisticsResponse) {
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < mTabs.getTabCount(); i++) {
            mCourseDeatilsPagerAdapter.updateTabview(i, postStatisticsResponse);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_details_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = null;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            // MenuItemCompat.getActionView(searchItem);
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (Helper.isConnected()) {
                        doSearch(query, true);
                    } else {
                        doSearch(query, false);
                    }
                    mSearchView.clearFocus();
                    Helper.hideKeyboardFromWindow(CourseDetailsActivity.this);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onQueryTextChange query : " + query);
                    doSearch(query, false);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(new OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onClose searchView");
                    doSearch("", false);
                    return false;
                }
            });
        }
        if (mSearchView != null) {
            try {
                mSearchView.setSearchableInfo(searchManager
                        .getSearchableInfo(this.getComponentName()));

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
        updatePostMenu(menu);
        return true;
    }

    /**
     * Search and display results
     *
     * @param query    search text
     * @param isSubmit if search query is submitted or not
     */
    private void doSearch(String query, Boolean isSubmit) {
        Fragment fragment = mCourseDeatilsPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment instanceof CommunicationFragment) {
            ((CommunicationFragment) fragment).onSearch(query, isSubmit);
        } else if (fragment instanceof ContentsFragment) {
            ((ContentsFragment) fragment).onSearch(query, isSubmit);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        Fragment fragment = mCourseDeatilsPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment instanceof CommunicationFragment) {
            ((CommunicationFragment) fragment).onOffsetChanged(i);
        } else if (fragment instanceof ContentsFragment) {
            ((ContentsFragment) fragment).onOffsetChanged(i);
        }
    }

    public interface appBarLayoutOnOffsetChanged {
        public void onOffsetChanged(int i);
    }

    public interface onSearchListener {
        public void onSearch(String query, Boolean isSubmit);
    }

    public interface onQueryChangeListener {
        public void onQueryChange(String query);
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }*/

    @Override
    public void onBackPressed() {

        //Log.d(TAG, "onBackPressed(). screenId : " + nextScreenId);
        Requester.getInstance().cancelPendingRequests(ContentsList.TAG);
        //super.onBackPressed();
        Requester.getInstance().cancelPendingRequests(ContentsList.TAG);
        //Log.d(TAG, "onBackPressed().. ");
        if (isJoined) {
            Intent intent = new Intent(CourseDetailsActivity.this, MyCoursesActivity.class);
            intent.putExtra("isFromNotification", Flinnt.TRUE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //Log.d(TAG, "isJoined true.. : ");
            finish();
        }

        if (mSearchView != null && !mSearchView.isIconified()) {

            mSearchView.setIconified(true);
            mSearchView.clearFocus();
            invalidateOptionsMenu();
        } else if (currentPage == 1 && mContentsFragment.isBrowseCourse) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isBrowseCours", mContentsFragment.isBrowseCourse);
            resultIntent.putExtra("isCourseUpdate", isCourseUpdate);
            resultIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
            resultIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);

            setResult(Activity.RESULT_OK, resultIntent);

            finish(); //super.onBackPressed();
        } else {
            if (!TextUtils.isEmpty(filterBy) || filterPostContentType != Flinnt.INVALID || filterPostType != Flinnt.INVALID) {

                Intent resultIntent = new Intent();

                resultIntent.putExtra("isCourseUpdate", isCourseUpdate);
                resultIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                resultIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);

                setResult(Activity.RESULT_OK, resultIntent);

                finish(); //super.onBackPressed();
            } else if (nextScreenId >= Flinnt.TRUE) {   //open CoursesTab if this activity opened from inappPreviewActivity
                //@Chirag if condition added 07/08/2018
                //Log.d(TAG, "in nextScreenId_ : " + nextScreenId);
                Bundle mBundle = new Bundle();
                mBundle.putInt("isFromNotification", Flinnt.TRUE);
                mBundle.putString("user_id", userId); //@Chirag 10/08/2018 :  For switch user account by userid
                Intent intent = new Intent(this, MyCoursesActivity.class);
                intent.putExtras(mBundle);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//@Chirag:14/08/2018
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //@Chirag:16/08/2018 added
                startActivity(intent);
                finish();
            } else {
                //Log.d(TAG, "onBackPressed()....isNotification : " + nextScreenId);
                NavUtils.navigateUpFromSameTask(this);
            }

        }
    }

    private void updatePostMenu(Menu menu) {

        MenuItem filterItem = menu.findItem(R.id.course_details_menu_filter_by);
        MenuItem inviteUserItem = menu.findItem(R.id.course_details_menu_invite_users);
        MenuItem usersItem = menu.findItem(R.id.course_details_menu_users);
//        MenuItem courseInfoItem = menu.findItem(R.id.course_details_menu_course_info);
        MenuItem muteItem = menu.findItem(R.id.course_details_menu_mute);
        MenuItem giveRatingItem = menu.findItem(R.id.course_details_menu_give_rating);
        MenuItem courseInfoItem = menu.findItem(R.id.course_card_menu_course_info);
        MenuItem settingItem = menu.findItem(R.id.course_details_menu_settings);

        if (mPostListMenuResponse != null) {

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanFilter() && currentPage == 0) {
                filterItem.setVisible(true);
            } else {
                filterItem.setVisible(false);
            }

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanInvite()) {
                inviteUserItem.setVisible(true);
            } else {
                inviteUserItem.setVisible(false);
            }

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanSeeUserList()) {
                usersItem.setVisible(true);
            } else {
                usersItem.setVisible(false);
            }

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanViewCourseInfo()) {
                courseInfoItem.setVisible(true);
            } else {
                courseInfoItem.setVisible(false);
            }

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanMute()) {
                muteItem.setVisible(true);
            } else {
                muteItem.setVisible(false);
            }
            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanRateCourse()) {
                giveRatingItem.setVisible(true);
            } else {
                giveRatingItem.setVisible(false);
            }

            if (Flinnt.SUCCESS == mPostListMenuResponse.getCanChangeSettings()) {
                settingItem.setVisible(true);
            } else {
                settingItem.setVisible(false);
            }

        } else {
            filterItem.setVisible(true);
            inviteUserItem.setVisible(false);
            usersItem.setVisible(false);
            courseInfoItem.setVisible(false);
            muteItem.setVisible(false);
            settingItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;

            case R.id.course_details_menu_filter_by:
                startActivity(new Intent(CourseDetailsActivity.this, FilterByPostActivity.class)
                        .putExtra(Course.COURSE_ID_KEY, courseID)
                        .putExtra(Course.COURSE_PICTURE_KEY, coursePicture)
                        .putExtra(Course.COURSE_NAME_KEY, courseName)
                        .putExtra(Course.ALLOWED_ROLES_KEY, courseAllowedRoles));
                break;

            case R.id.course_details_menu_mute:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {

                    MuteSettingRequest mMuteSettingRequest = new MuteSettingRequest();
                    mMuteSettingRequest.setCourseID(courseID);
                    mMuteSettingRequest.setMuteComment("");
                    mMuteSettingRequest.setMuteSound("");
                    MuteSetting mMuteSetting = new MuteSetting(mHandler, mMuteSettingRequest, MuteSetting.COURSE_SETTING);
                    muteSettingRequestType = MuteSetting.GET_SETTING;
                    mMuteSetting.setRequestType(MuteSetting.GET_SETTING);
                    mMuteSetting.sendMuteSettingRequest();
                    startProgressDialog();
                }

                break;

            case R.id.course_details_menu_settings:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {
                    Intent courseSettingIntent = new Intent(this, CourseSettingsActivity.class);
                    courseSettingIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                    startActivity(courseSettingIntent);
                }
                break;

            case R.id.course_details_menu_give_rating:
                if (Helper.isConnected()) {

                    MyCoursesActivity.RATING_COURSE_ID = courseID;

                    CourseReviewReadRequest mCourseReviewReadRequest = new CourseReviewReadRequest();
                    mCourseReviewReadRequest.setCourseId(courseID);
                    mCourseReviewReadRequest.setUserId(Config.USER_ID);

                    CourseReviewRead mCourseReviewRead = new CourseReviewRead(mHandler, courseID);
                    mCourseReviewRead.sendCourseReadRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                }
                break;
            case R.id.course_card_menu_course_info:
                if (Helper.isConnected()) {
                    Intent courseDescriptionIntent = new Intent(CourseDetailsActivity.this, BrowseCourseDetailActivity.class);
                    courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseID);
                    courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, coursePicture);
                    courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseName);
                    startActivityForResult(courseDescriptionIntent, MyCoursesActivity.JOIN_COMMUNITY_CALLBACK);
                } else {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                }
                break;

            case R.id.course_details_menu_users:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {
                    if (!TextUtils.isEmpty(courseAllowedRoles)) {
                        CharSequence tabTitle[];
                        List<String> tabItems = new ArrayList<String>();
                        String[] items = courseAllowedRoles.split(",");
                        for (int i = 0; i < items.length; i++) {
                            int role = Integer.parseInt(items[i]);
                            if (role == Flinnt.COURSE_ROLE_TEACHER) {
                                tabItems.add("Teachers");
                            } else if (role == Flinnt.COURSE_ROLE_LEARNER) {
                                tabItems.add("Learners");
                            }
                        }
                        tabTitle = tabItems.toArray(new CharSequence[tabItems.size()]);

                        Intent userIntent = new Intent(this, UsersActivity.class);
                        userIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                        userIntent.putExtra(SelectUsersActivity.TAB_TITLE, tabTitle);
                        startActivity(userIntent);
                    }

                }
                break;

            case R.id.course_details_menu_invite_users:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                } else {
                    if (!TextUtils.isEmpty(courseAllowedRoles)) {

                        List<String> radioItems = new ArrayList<String>();
                        String[] rItems = courseAllowedRoles.split(",");
                        for (int i = 0; i < rItems.length; i++) {
                            int role = Integer.parseInt(rItems[i]);
                            if (role == Flinnt.COURSE_ROLE_TEACHER) {
                                radioItems.add("Teacher");
                            } else if (role == Flinnt.COURSE_ROLE_LEARNER) {
                                radioItems.add("Learner");
                            }
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseDetailsActivity.this);
                        // set view
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.invite_dialog_design, null);
                        alertDialogBuilder.setView(dialogView);

                        TextView title = new TextView(this);
                        title.setText("Invite User");
                        title.setPadding(40, 40, 40, 40);
                        title.setGravity(Gravity.CENTER_VERTICAL);
                        title.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        title.setTextSize(20);
                        title.setTypeface(Typeface.DEFAULT_BOLD);
                        alertDialogBuilder.setCustomTitle(title);
                        alertDialogBuilder.setPositiveButton("INVITE", null);


//                        AlertDialog.Builder builder = new AlertDialog.Builder(CourseDetailsActivity.this, R.style.MyAlertDialogStyle)
//                                .setTitle("Invite User")
//                                .setPositiveButton("INVITE", null)
//                                .setView(R.layout.invite_dialog_design);


                        final AlertDialog inviteDialog = alertDialogBuilder.create();
                        inviteDialog.show();

                        RelativeLayout radioLearnerLayout = (RelativeLayout) inviteDialog.findViewById(R.id.radio_learner_layout);
                        RelativeLayout radioTeacherLayout = (RelativeLayout) inviteDialog.findViewById(R.id.radio_teacher_layout);

                        radioLearners = (RadioButton) inviteDialog.findViewById(R.id.radio_learners);
                        radioTeachers = (RadioButton) inviteDialog.findViewById(R.id.radio_teachers);

                        if (radioItems.contains("Teacher")) {
                            radioTeacherLayout.setVisibility(View.VISIBLE);
                            radioTeachers.setChecked(isTeacherChecked);
                        } else {
                            radioTeacherLayout.setVisibility(View.GONE);
                        }

                        if (radioItems.contains("Learner")) {
                            radioLearnerLayout.setVisibility(View.VISIBLE);
                            radioLearners.setChecked(isLearnerChecked);
                        } else {
                            radioLearnerLayout.setVisibility(View.GONE);
                        }

                        editEmailNumber = (EditText) inviteDialog.findViewById(R.id.et_review);
                        editEmailNumber.setTextColor(Color.DKGRAY);

                        changeColor();

                        radioLearners.setChecked(isLearnerChecked);
                        radioTeachers.setChecked(isTeacherChecked);

                        radioLearners.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                                radioLearners.setChecked(isChecked);
                                radioTeachers.setChecked(!isChecked);
                                inviteRole = isChecked ? Flinnt.COURSE_ROLE_LEARNER : Flinnt.COURSE_ROLE_TEACHER;
                                changeColor();
                            }
                        });
                        radioTeachers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                                radioTeachers.setChecked(isChecked);
                                radioLearners.setChecked(!isChecked);
                                inviteRole = isChecked ? Flinnt.COURSE_ROLE_TEACHER : Flinnt.COURSE_ROLE_LEARNER;
                                changeColor();
                            }
                        });

                        buttonDone = inviteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        buttonDone.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                isLearnerChecked = radioLearners.isChecked();
                                isTeacherChecked = radioTeachers.isChecked();
                                emailNumber = editEmailNumber.getText().toString();

                                if (isValideEmailorNumber(emailNumber) == validateErrorCodes.VALID_FIELD) {

                                    invited_email_mob = emailNumber;

                                    mInviteUsersRequest.setUserId(Config.getStringValue(Config.USER_ID));
                                    mInviteUsersRequest.setCourseID(courseID);
                                    mInviteUsersRequest.setInviteRole(inviteRole);
                                    mInviteUsersRequest.setSendTo(emailNumber);

                                    mInviteUsers = new InviteUsers(mHandler);
                                    mInviteUsers.setInviteUsersRequest(mInviteUsersRequest);
                                    mInviteUsers.sendInviteUsersRequest();
                                    inviteDialog.dismiss();
                                    startProgressDialog();
                                } else {
                                    Helper.showAlertMessage(CourseDetailsActivity.this, "Invite", res.getString(R.string.validate_email_mobile), "Close");
                                }
                            }
                        });
                        inviteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
                    }

                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check and uncheck radiobuttons
     */
    protected void changeColor() {
        final int blue = Color.parseColor("#007DCD");

        if (radioLearners.isChecked()) {
            radioLearners.setTextColor(blue);
            radioTeachers.setTextColor(Color.BLACK);
        }
        if (radioTeachers.isChecked()) {
            radioTeachers.setTextColor(blue);
            radioLearners.setTextColor(Color.BLACK);
        }
    }

    /**
     * A {@link CourseDeatilsPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class CourseDeatilsPagerAdapter extends FragmentStatePagerAdapter {

        private String tabTitles[] = new String[]{"COMMUNICATION", "CONTENTS"/*, "ALBUMS"*/};
        private ArrayList<View> viewArry = new ArrayList<View>();

        public CourseDeatilsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(CourseDetailsActivity.this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) v.findViewById(R.id.tab_title_customtab);
            title.setText(tabTitles[position]);
            if (position == 1) {
                rippleBackground = (RippleBackground) v.findViewById(R.id.content);
            }
            viewArry.add(v);

            return v;
        }

        /**
         * @param position position of tabs
         */
        public void updateTabTitle(int position) {

            for (int i = 0; i < viewArry.size(); i++) {
                View v = viewArry.get(i);
                TextView title = (TextView) v.findViewById(R.id.tab_title_customtab);
                if (i == position) {
                    title.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    title.setTextColor(Color.parseColor("#99ffffff"));
                }
            }


        }

        /**
         * Sets tabview according to response
         *
         * @param position               tab postion
         * @param postStatisticsResponse response
         */
        public void updateTabview(int position, PostStatisticsResponse postStatisticsResponse) {
            View v = viewArry.get(position);
            TextView unreadCount = (TextView) v.findViewById(R.id.tab_unread_count_customtab);
            if (postStatisticsResponse != null) {
                switch (position) {
                    case 0:
                        int unreadPost = 0;
                        int unreadMsg = 0;
                        int unreadAlbum = 0;
                        int unreadTotalCommunication = 0;
                        String unreadTotalCommunicationStr = "";


                        if (!postStatisticsResponse.getUnreadPosts().equalsIgnoreCase("") && postStatisticsResponse.getUnreadPosts() != null) {
                            unreadPost = Integer.parseInt(postStatisticsResponse.getUnreadPosts());
                        }

                        if (!postStatisticsResponse.getUnreadMessages().equalsIgnoreCase("") && postStatisticsResponse.getUnreadMessages() != null) {
                            unreadMsg = Integer.parseInt(postStatisticsResponse.getUnreadMessages());
                        }

                        if (!postStatisticsResponse.getUnreadAlbums().equalsIgnoreCase("") && postStatisticsResponse.getUnreadAlbums() != null) {
                            unreadAlbum = Integer.parseInt(postStatisticsResponse.getUnreadAlbums());
                        }

                        unreadTotalCommunication = unreadPost + unreadMsg + unreadAlbum;
                        if (unreadTotalCommunication > 99) {
                            unreadTotalCommunicationStr = "99+";
                        } else {
                            unreadTotalCommunicationStr = String.valueOf(unreadTotalCommunication);
                        }

                        unreadCount.setVisibility(unreadTotalCommunicationStr.equals(Flinnt.DISABLED) ? View.GONE : View.VISIBLE);
                        unreadCount.setText(unreadTotalCommunicationStr);
                        break;
                    case 1:

                        String unreadContentsStr = "";
                        if (!postStatisticsResponse.getUnreadContents().equalsIgnoreCase("") && postStatisticsResponse.getUnreadContents() != null) {
                            unreadContentsStr = postStatisticsResponse.getUnreadContents();
                        }

                        if (Integer.parseInt(unreadContentsStr) > 99) {
                            unreadContentsStr = "99+";
                        }

                        unreadCount.setVisibility(unreadContentsStr.equals(Flinnt.DISABLED) ? View.GONE : View.VISIBLE);
                        unreadCount.setText(unreadContentsStr);

                        break;
                    case 2:
//						mUnreadCountTxt.setVisibility( postStatisticsResponse.getUnreadAlbums().equals(Flinnt.DISABLED) ? View.GONE : View.VISIBLE);
//						mUnreadCountTxt.setText( postStatisticsResponse.getUnreadAlbums() );
                        break;
                    default:
                        break;
                }
            } else {
                unreadCount.setVisibility(View.GONE);
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            if (LogWriter.isValidLevel(Log.ERROR))
                LogWriter.write("getItem : position :: " + position + " , " + courseID);
            switch (position) {
                case 0:
                    if (null == mCommunicationFragment) {
                        mCommunicationFragment = CommunicationFragment.newInstance(courseID, filterBy, filterPostContentType, filterPostType, courseName, coursePicture, courseAllowedRoles, isTeacher);
                    }
                    return mCommunicationFragment;
                case 1:
                    if (null == mContentsFragment) {
                        mContentsFragment = ContentsFragment.newInstance(courseID, courseName);
                    }
                    return mContentsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.communication_page_title);
                case 1:
                    return getResources().getString(R.string.content_page_title);
            /*case 2:
                return "ALBUMS";	*/
            }
            return tabTitles[position];
        }
    }


    /**
     * Bug found in some when toolbar is half-way collapsed and a touch is made on image (some phones only)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
        } else {

            switch (v.getId()) {
                case R.id.course_details_fab_alert:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddAlertActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_ALERT_ADD), ADD_NEW_CONTENT);
                    break;
              /*  case R.id.course_details_fab_message:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddMessageActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_MESSAGE_ADD)
                            .putExtra(Course.COURSE_ID_KEY, courseID)
                            .putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, mCourseNameTxt), ADD_NEW_CONTENT);
                    break;*/
             /*   case R.id.course_details_fab_album:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddAlbumActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_ALBUM_ADD)
                            .putExtra(Course.COURSE_ID_KEY, courseID)
                            .putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, mCourseNameTxt), ADD_NEW_CONTENT);
                    break;*/
              /*  case R.id.course_details_fab_quiz:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddQuizActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_QUIZ_ADD)
                            .putExtra(Course.COURSE_ID_KEY, courseID)
                            .putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, mCourseNameTxt), ADD_NEW_CONTENT);
                    break;*/
               /* case R.id.course_details_fab_post:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddPostActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_BLOG_ADD)
                            .putExtra(Course.COURSE_ID_KEY, courseID)
                            .putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, mCourseNameTxt), ADD_NEW_CONTENT);
                    break;*/
                case R.id.course_details_fab_communication:
                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddCommunicationActivity.class)
                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_COMMUNICATION_ADD)
                            .putExtra(Course.COURSE_ID_KEY, courseID)
                            .putExtra(PostDetailsResponse.POST_COURSE_NAME_KEY, courseName), ADD_NEW_CONTENT);
                    break;
                case R.id.course_details_fab_course:
//                    startActivityForResult(new Intent(CourseDetailsActivity.this, AddCourseActivity.class)
//                            .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_COURSE_ADD),ADD_NEW_CONTENT);
                    break;
                case R.id.course_details_fab_poll:
                    Intent intent = new Intent(this, AddPollActivity.class);
                    intent.putExtra(Course.COURSE_ID_KEY, courseID);
                    intent.putExtra(CourseInfo.COURSE_NAME_KEY, courseName);
                    intent.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_POLL_ADD);
                    startActivityForResult(intent, ADD_NEW_CONTENT);
                default:
                    break;
            }
            mAddFloatingAction.collapse();
        }
    }

    /**
     * Gets course menu picture and add
     */
    private void updateMenuBanner(MenuBannerResponse menuBannerResponse) {
        try {
            boolean isVisible = false;
            fabBackground = (RelativeLayout) findViewById(R.id.fab_background);


            mAddFloatingAction.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(CourseDetailsActivity.this);
                        mAddFloatingAction.collapse();
                    } else {
                        if (fabBackground.getVisibility() == View.INVISIBLE)
                            fabBackground.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onMenuCollapsed() {
                    if (fabBackground.getVisibility() == View.VISIBLE)
                        fabBackground.setVisibility(View.INVISIBLE);
                }
            });

            fabBackground.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (fabBackground.getVisibility() == View.VISIBLE) {
                        mAddFloatingAction.collapse();
                        return true;
                    }
                    return false;
                }
            });

            mFabAlert = (FloatingActionButton) findViewById(R.id.course_details_fab_alert);
            mFabMessage = (FloatingActionButton) findViewById(R.id.course_details_fab_message);
            mFabAlbum = (FloatingActionButton) findViewById(R.id.course_details_fab_album);
            mFabQuiz = (FloatingActionButton) findViewById(R.id.course_details_fab_quiz);
            mFabPost = (FloatingActionButton) findViewById(R.id.course_details_fab_post);
            mFabCourse = (FloatingActionButton) findViewById(R.id.course_details_fab_course);
            mFabCommunication = (FloatingActionButton) findViewById(R.id.course_details_fab_communication);
            mFabPoll = (FloatingActionButton) findViewById(R.id.course_details_fab_poll);


            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddAlert()) {
                mFabAlert.setOnClickListener(this);
                mFabAlert.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabAlert.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddMessage()) {
                mFabMessage.setOnClickListener(this);
                mFabMessage.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabMessage.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddAlbum()) {
                mFabAlbum.setOnClickListener(this);
                mFabAlbum.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabAlbum.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddQuiz()) {
                mFabQuiz.setOnClickListener(this);
                mFabQuiz.setVisibility(View.VISIBLE);
                isVisible = true;
                isTeacher = true;
            } else {
                mFabQuiz.setVisibility(View.GONE);
                isTeacher = false;
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddPost()) {
                mFabPost.setOnClickListener(this);
                mFabPost.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabPost.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddCommunication()) {
                mFabCommunication.setOnClickListener(this);
                mFabCommunication.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabCommunication.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddPoll()) {
                mFabPoll.setOnClickListener(this);
                mFabPoll.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabPoll.setVisibility(View.GONE);
            }


            if (Flinnt.SUCCESS == menuBannerResponse.getCanCreateCourse()) {
                mFabCourse.setOnClickListener(this);
                mFabCourse.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                mFabCourse.setVisibility(View.GONE);
            }

            if (currentPage == 1 && Flinnt.SUCCESS == menuBannerResponse.getCanEditContent()) {
                mContentEditFloatingAction.setVisibility(View.VISIBLE);
                rippleBackgroundFabButton.startRippleAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackgroundFabButton.stopRippleAnimation();
                    }
                }, 4000);
            } else {
                rippleBackgroundFabButton.stopRippleAnimation();
            }

            if (isVisible) {
                //******* Pull Change
                if (currentPage == 1) {
                    mAddFloatingAction.setVisibility(View.GONE);
                } else {
                    mAddFloatingAction.setVisibility(View.VISIBLE);
                }
            }
            if (mViewPager.getCurrentItem() == 0 && mCommunicationFragment != null) {
                mCommunicationFragment.isTeacher = isTeacher;
                if (isTeacher) {
                    mCommunicationFragment.mEmptyView.setText(getResources().getString(R.string.no_post_teacher));
                } else {
                    mCommunicationFragment.mEmptyView.setText(getResources().getString(R.string.no_post_learner));
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Check entered email or mobile number
     *
     * @param emailMobile email or mobile number
     * @return true if valid, false otherwise
     */
    private int isValideEmailorNumber(String emailMobile) {
        int ret = validateErrorCodes.NOT_VALIDATE_EMAIL_MOBILE;
        if (emailMobile.matches(REGEX_NUMBER)) {
            if (emailMobile.length() == 10) {
                return validateErrorCodes.VALID_FIELD;
            } else {
                return validateErrorCodes.NOT_VALID_NUMBER;
            }
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailMobile).matches()) {
            return validateErrorCodes.VALID_FIELD;
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        if (TextUtils.isEmpty(courseName)) {
            courseName = data.getStringExtra(Course.COURSE_NAME_KEY);
        }
        if (requestCode == ADD_NEW_CONTENT || requestCode == CourseDetailsActivity.POST_COMMENT_CALL_BACK || requestCode == AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK) {
            mCommunicationFragment.onActivityResult(requestCode, resultCode, data);
            return;
        }
        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK:

                        //update course picture
                        coursePictureUrl = data.getStringExtra(Course.COURSE_PICTURE_URL_KEY);
                        coursePicture = data.getStringExtra(Course.COURSE_PICTURE_KEY);
                        courseName = data.getStringExtra(Course.COURSE_NAME_KEY);

                        displayCourseImage(coursePicture);
                        if (LogWriter.isValidLevel(Log.ERROR))
                            LogWriter.write("coursePicture : " + coursePicture + " , mCourseNameTxt : " + courseName);

                        if (!TextUtils.isEmpty(courseName)) {
                            isCourseUpdate = true;
                            updateCoursePicture();
                            setTitleToolbar();
                        }
                        break;

                    case MyCoursesActivity.JOIN_COMMUNITY_CALLBACK:

                        HashMap<Course, Boolean> addedAndRemovedCourses = (HashMap<Course, Boolean>) data.getSerializableExtra(JoinCourseResponse.JOINED_KEY);
                        if (addedAndRemovedCourses.size() > 0) {
                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses);
                            //resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//@Chirag: 14/08/2018
                            setResult(Activity.RESULT_OK, resultIntent);
//                            finish();

                            startActivity(new Intent(CourseDetailsActivity.this, MyCoursesActivity.class)
                                    .putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));//@Chirag:14/08/2018
                            finish();
                        }
                        break;
                    case CONTENT_UPDATE_CALL_BACK:
//                        Fragment fragment = mCourseDeatilsPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        ((ContentsFragment) fragment).refreshListFromActivity();
                        if (Helper.isConnected()) {
                            mContentsFragment.refreshListFromActivity();
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

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(CourseDetailsActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(CourseDetailsActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) {
                //Log.d(TAG, "startProgressDialog()..");
                mProgressDialog.show();
            }
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

    public void onUpdateMenuBanner() {
        new MenuBanner(mHandler, courseID).sendMenuBannerRequest();
    }

    //******* Pull Change
    public void rippleAnimationShowHide() {
        if (contentItemCount > 0) {
            rippleBackground.startRippleAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rippleBackground.stopRippleAnimation();
                }
            }, 6000);
        }
    }
}