
package com.edu.flinnt.gui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PromoCoursePagerAdapter;
import com.edu.flinnt.core.AppUpdate;
import com.edu.flinnt.core.CourseReviewWrite;
import com.edu.flinnt.core.Highlights;
import com.edu.flinnt.core.JoinCourse;
import com.edu.flinnt.core.Login;
import com.edu.flinnt.core.MenuBanner;
import com.edu.flinnt.core.MenuStatistics;
import com.edu.flinnt.core.MuteSetting;
import com.edu.flinnt.core.MyCourses;
import com.edu.flinnt.core.RegisterDevice;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.UpdateBirthdate;
import com.edu.flinnt.customviews.CustomViewPager;
import com.edu.flinnt.customviews.store.CustomAutoCompleteTextView;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.database.NotificationInterface;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.fragments.MyERPFragment;
import com.edu.flinnt.fragments.store.BrowseCoursesFragmentNew;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.helper.listner.AppBarStateChangeListener;
import com.edu.flinnt.models.PopularStoryDataModel;
import com.edu.flinnt.models.PopularStoryList;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.protocol.AppUpdateResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseReviewReadResponse;
import com.edu.flinnt.protocol.CourseReviewWriteResponse;
import com.edu.flinnt.protocol.HighlightsResponse;
import com.edu.flinnt.protocol.InviteUsersResponse;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.protocol.MenuBannerResponse;
import com.edu.flinnt.protocol.MenuStatisticsResponse;
import com.edu.flinnt.protocol.MuteSettingRequest;
import com.edu.flinnt.protocol.MuteSettingResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.UpdateBirthdateResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.services.LocationService;
import com.edu.flinnt.slidemenu.MenuAdapter;
import com.edu.flinnt.slidemenu.SlideMenuItem;
import com.edu.flinnt.util.BlurBuilder;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyConfig;
import com.edu.flinnt.util.PagerContainer;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import com.edu.flinnt.util.SelectableRoundedImageView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.Func2;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;

// comment update version dialog by antra 21-01-2019

public class MyCoursesActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, OnClickListener {
    public Toolbar mMainToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private MenuAdapter mPrimaryMenuAdapter, mSecondaryMenuAdapter;
    private Resources res = FlinntApplication.getContext().getResources();
    private final String[] mMenuTitles = {res.getString(R.string.my_courses), res.getString(R.string.join_course), res.getString(R.string.browse_courses), res.getString(R.string.wishlist_title), res.getString(R.string.user_requests), /*res.getString(R.string.join_community), */
            res.getString(R.string.select_institute), res.getString(R.string.bookmarks), res.getString(R.string.notifications), res.getString(R.string.alerts), res.getString(R.string.settings), res.getString(R.string.contact_us), res.getString(R.string.help), res.getString(R.string.faq), res.getString(R.string.logout)};
    private final int[] mMenuIconId = {R.drawable.ic_drawer_my_courses, R.drawable.ic_drawer_join_course, R.drawable.ic_drawer_browse_courses, R.drawable.ic_drawer_wishlist, R.drawable.ic_drawer_course_invites, /*R.drawable.ic_drawer_join_community,*/
            R.drawable.ic_select_institute, R.drawable.ic_drawer_bookmarks, R.drawable.ic_drawer_notification, R.drawable.ic_drawer_alert, R.drawable.ic_drawer_settings, R.drawable.ic_drawer_about_us, R.drawable.ic_drawer_help, R.drawable.ic_drawer_faq, R.drawable.ic_drawer_logout};
    private ArrayList<SlideMenuItem> primaryDrawerItemList = new ArrayList<SlideMenuItem>();
    final Handler mImageHandler = new Handler();
    private Runnable runnable;
    public Handler mHandler = null;
    private FloatingActionsMenu mFloatingActionMenu;
    private FloatingActionButton fab_category;
    private FloatingActionButton mFabAlert, mFabMessage, mFabAlbum, mFabQuiz, mFabPost, mFabCourse, mFabCommunication, mFabPoll;
    private RelativeLayout fabBackground;
    private ArrayList<String> mOfflineCouseIDs = new ArrayList<String>();
    private MyCourses mMyCourses = null;
    private TextView mHighlightText, mHighlightDate, mToolbarTitle, mTotalBadgeTxt, mDateTxt;
    private String highlightID = "0";
    ProgressDialog mProgressDialog = null;
    ImageLoader mImageLoader;
    private boolean isNavHeaderExecute = false;
    private String profileImageUrl = "", profileImageName = "", bannerPath = "", accountBanner = "";
    public String invited_email_mob = "";
    private int canChangeBanner = Flinnt.FALSE;
    private int canSendJoinRequest = Flinnt.FALSE;
    private ArrayList<String> bannerList = new ArrayList<String>();
    public static final int CHANGE_BANNER_SUCCESSFULL_CALL_BACK = 106;
    public static final int MENU_STATISTICS_CALL_BACK = 107;
    public static final int ADD_COURSE_CALL_BACK = 108;
    public static final int COURSE_UNSUBSCRIBED_CALL_BACK = 116;
    public static final int JOIN_COMMUNITY_CALLBACK = 117;
    public static final int LOGIN_CALLBACK = 118;
    public static final int BROWSE_COURSE_SUBSCRIBE_CALLBACK = 119;
    public static final int BROWSE_STORE_CALLBACK = 125;

    public static final int WISHLIST_BROWSE_COURSE_CALLBACK = 120;
    public static final int NOTIFICATIONLIST_CALLBACK = 121;
    public static String RATING_COURSE_ID = null;
    public static final String SWAP_ACCOUNT_OPERATION = "swap";
    protected static int coursePositionOnItemClick = Flinnt.INVALID;
    public int muteSettingRequestType = MuteSetting.GET_SETTING;
    //	Image change after 5 seconds
    private static final int BANNER_CHANGE_TIMEOUT = 5000;
    // Fade effect lasts for 1 second, increase this to slower fade, decrease to faster
    protected static final int BANNER_FADE_EFFECT_TIMEOUT = 1000;
    private TransitionDrawable transition;
    private Drawable[] layers = new Drawable[2];
    private ArrayList<Drawable> allLayers = new ArrayList<Drawable>();
    private ActionBar mActionBar;
    private int currentBannerPosition = 0;
    private boolean bannerZeroZero = true;
    private boolean isFreshBannerShow = false;
    private AppBarLayout appBarLayout;
    public int canBrowseCourse = Flinnt.INVALID;
    private boolean isPrimaryMenuShowing = true;
    private ImageView mSwitchUserIconImg, mMyCourseImg;
    private boolean isDeleteDialogShowing;
    private MyCoursesFragment myCoursesFragment = null;
    private MyStoriesFragment myStoryFragment = null;
    private BrowseCoursesFragmentNew browseCoursesFragment = null;
    private MyERPFragment myERPFragment = null;
    private boolean canShowFab;
    private LinearLayout mHighlightLayout, mTooltipBrowseCourseLinear, mTooltipMultipleAccLinear;
    public static int currentPage = 0;

    public void setCanBrowseCourse(int canBrowseCourse) {
        this.canBrowseCourse = canBrowseCourse;
    }

    private CharSequence tabTitles[] = {"STORIES", "COURSES", "STORE", "ERP"};
    private CustomViewPager mViewPager;
    private CoursesViewPagerAdapter coursesViewPagerAdapter;
    private SearchView searchView = null;
    private View mHeaderView;
    private TabLayout mTabLayout;
    public static String IS_JOIN = "";
    public MenuItem notificationItem, scanQRCodeItem, searchItem, settingItem;
    private AlertDialog alertDialog;
    final Calendar mCalendar = Calendar.getInstance();
    private int dobYear = 0, dobMonth = 0, dobDay = 0;
    private String dobFormat = "dd/MM/yyyy"; //In which you need put here
    private AlertDialog updateDOBDialog;
    private boolean isBrowseCourseTA = false;
    private int isFromNotification = Flinnt.TRUE;

    private Timer promoCourseTimer = null;
    PromoCoursePagerAdapter mMyPromoCourseAdapter;
    PagerContainer pagerContainer;
    private ViewPager mDisplayCourserRecyclerView;
    //@Nikhill 20062018
    String TAG = "MyCoursesActivity";
    private ArrayList<PopularStoryDataModel.Story> storyDataModelArrayList = new ArrayList<PopularStoryDataModel.Story>();
    private PopularStoryDataModel popularStoryDataModel = new PopularStoryDataModel();

    public static boolean isSwipable = true;
    boolean doubleBackToExitPressedOnce = false;
    public static String coursePictureURLstatic = "https://flinnt1.s3.amazonaws.com/courses/";

    private  ProgressBar filterProgressBar;

//*********

    public static ValueCallback<Uri> mUploadMessage;//@Chirag:20/08/2018
    public static ValueCallback<Uri[]> uploadMessage;//@Chirag:20/08/2018
    public static final int REQUEST_SELECT_FILE = 100;//@Chirag:20/08/2018
    public final static int FILECHOOSER_RESULTCODE = 1;//@Chirag:20/08/2018
    public static Uri mCapturedImageURI = null;
    private CustomAutoCompleteTextView storeSearchBox; //@vijay 07/02/2019


    //********

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (null != getIntent()) {
            //Log.d(TAG, "Intent is not null");
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                //Log.d(TAG, "bundle is not null");
                String userID = null;
                if (bundle.containsKey("user_id")) {
                    userID = bundle.getString("user_id");
                    //Log.d(TAG, "bundle userId found : " + userID);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onCreate userID : " + userID + ", Config userID : " + Config.getStringValue(Config.USER_ID));


                    if (!TextUtils.isEmpty(userID) && !userID.equals(Config.getStringValue(Config.USER_ID))) {
                        Helper.setCurrentUserConfig(userID);
                        //Log.d(TAG, "setCurrentUserConfig : notificationUserId : " + userID);
                    }
                }

                if (bundle.containsKey("isFromNotification")) {
                    isFromNotification = bundle.getInt("isFromNotification");
                    //Log.d(TAG, "isFromNotification found : " + isFromNotification);
                }

                if (isFromNotification == Flinnt.TRUE && bundle.containsKey(Config.USER_ID))
                    userID = bundle.getString(Config.USER_ID);

                if (null != userID && !userID.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userID);
                    //Log.d(TAG, "setCurrentUserConfig 2 : notificationUserId : " + userID);
                }
                isBrowseCourseTA = bundle.getBoolean(Config.IS_BROWSECOURSE);
            } else {
                //Log.d(TAG, "bundle is null");
                isFromNotification = Flinnt.FALSE;
            }
        } else {
            //Log.d(TAG, "Intent is null");
            isFromNotification = Flinnt.FALSE;
        }

        setContentView(R.layout.my_courses_activity);


        mTooltipBrowseCourseLinear = (LinearLayout) findViewById(R.id.tooltipBrowseCourseLinear);
        //@Nikhil 21062018
        fab_category = (FloatingActionButton) findViewById(R.id.fab_category);
        fab_category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isConnected()) {
                    startActivity(new Intent(MyCoursesActivity.this, CategorylistActivity.class));
                } else {
                    Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                }
            }
        });






        mTooltipBrowseCourseLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToBrowseCourses();
                mTooltipBrowseCourseLinear.setVisibility(View.GONE);
                Config.setToolTipValue(Config.FLINNT_TOOLTIP_BCOURSE_STATS + Config.getStringValue(Config.USER_ID), "1");
            }
        });

        storeSearchBox = (CustomAutoCompleteTextView)findViewById(R.id.edt_search_store_box);
        storeSearchBox.setVisibility(View.GONE);

        mTooltipMultipleAccLinear = (LinearLayout) findViewById(R.id.tooltipMultipleAccLinear);
        mTooltipMultipleAccLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                mTooltipMultipleAccLinear.setVisibility(View.GONE);
                Config.setToolTipValue(Config.FLINNT_TOOLTIP_MACC_STATS + Config.getStringValue(Config.USER_ID), "1");
            }
        });


        if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_BCOURSE_STATS + Config.getStringValue(Config.USER_ID)).equals("1")) {
            mTooltipBrowseCourseLinear.setVisibility(View.GONE);
        } else {
            mTooltipBrowseCourseLinear.setVisibility(View.VISIBLE);
        }

        if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_MACC_STATS + Config.getStringValue(Config.USER_ID)).equals("1")) {
            mTooltipMultipleAccLinear.setVisibility(View.GONE);
        } else {
            mTooltipMultipleAccLinear.setVisibility(View.VISIBLE);
        }


        logUser();
        mMainToolbar = (Toolbar) findViewById(R.id.my_courses_main_toolbar);
        setSupportActionBar(mMainToolbar);
        mMainToolbar.setTitle("");
        mMainToolbar.setSubtitle("");
        setTitleToolbar();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });

        mImageLoader = Requester.getInstance().getImageLoader();

        // Setup Toolbar/Actionbar
        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);

        // Setup Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_courses_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.my_courses_nav_view);

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mMainToolbar,
                R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                // getActionBar().setTitle(mTitle);
//				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                if (!isPrimaryMenuShowing) toggleDrawerMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle(mDrawerTitle);
//				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                Helper.hideKeyboardFromWindow(MyCoursesActivity.this);
                mTooltipMultipleAccLinear.setVisibility(View.GONE);
                Config.setToolTipValue(Config.FLINNT_TOOLTIP_MACC_STATS + Config.getStringValue(Config.USER_ID), "1");
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());

        mHeaderView = mNavigationView.getHeaderView(0);
        RelativeLayout layoutHeaderRel = (RelativeLayout) mHeaderView.findViewById(R.id.layout_header);
        layoutHeaderRel.setOnClickListener(this);

        mHeaderView.findViewById(R.id.layout_switch).setOnClickListener(this);


        appBarLayout = (AppBarLayout) findViewById(R.id.my_courses_appbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView

        mSwitchUserIconImg = (ImageView) mHeaderView.findViewById(R.id.iv_switch_user);

        mMyCourseImg = (ImageView) findViewById(R.id.my_courses_image);
        mMyCourseImg.setColorFilter(getResources().getColor(R.color.course_image_transperent_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
        mHighlightLayout = (LinearLayout) findViewById(R.id.my_courses_highlights_layout);
        mHighlightLayout.setOnClickListener(this);
        mHighlightText = (TextView) findViewById(R.id.my_courses_highlight_text);
        mHighlightDate = (TextView) findViewById(R.id.my_courses_highlight_date);
        mTotalBadgeTxt = (TextView) findViewById(R.id.totalBadgeTxt);

        isFreshBannerShow = true;

        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message message) {

                stopProgressDialog();

                switch (message.what) {
                    case Flinnt.SUCCESS:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                            //Log.d("Menuu", "handleMessage : " + message.obj.toString());

                            if (message.obj instanceof MenuBannerResponse) {
                                if (canBrowseCourse == Flinnt.INVALID) {
                                    setCanBrowseCourse(((MenuBannerResponse) message.obj).getCanBrowseCourse());
                                }
                                canSendJoinRequest = ((MenuBannerResponse) message.obj).getCanSendJoinRequest();
                                updateDrawerItem(null);
                                updateMenu((MenuBannerResponse) message.obj);
                                updateBanner((MenuBannerResponse) message.obj);
                                invalidateOptionsMenu();
                            } else if (message.obj instanceof HighlightsResponse) {
                                updateHighlights((HighlightsResponse) message.obj);
                            } else if (message.obj instanceof MenuStatisticsResponse) {
                                updateDrawerItem((MenuStatisticsResponse) message.obj);
                                //******* pull change
                                MenuStatisticsResponse menuStatisticsResponse = (MenuStatisticsResponse) message.obj;
                                int invitationCount = 0, alertsCount = 0, totalCount;
                                String totalCountStr;
                                if (!menuStatisticsResponse.getInvitations().equalsIgnoreCase("") && menuStatisticsResponse.getInvitations() != null) {
                                    invitationCount = Integer.parseInt(menuStatisticsResponse.getInvitations());
                                }

                                if (!menuStatisticsResponse.getAlerts().equalsIgnoreCase("") && menuStatisticsResponse.getAlerts() != null) {
                                    alertsCount = Integer.parseInt(menuStatisticsResponse.getAlerts());
                                }

                                totalCount = invitationCount + alertsCount;
                                if (totalCount > 99) {
                                    totalCountStr = "99+";
                                    mTotalBadgeTxt.setTextSize(10);
                                } else {
                                    totalCountStr = String.valueOf(totalCount);
                                    mTotalBadgeTxt.setTextSize(12);
                                }

                                mTotalBadgeTxt.setVisibility(totalCountStr.equals(Flinnt.DISABLED) ? View.GONE : View.VISIBLE);
                                mTotalBadgeTxt.setText(totalCountStr);

                            } else if (message.obj instanceof JoinCourseResponse) {
                                alertDialog.dismiss();
                                JoinCourseResponse joinCourseResponse = (JoinCourseResponse) message.obj;
                                if (joinCourseResponse.getJoined().equals(Flinnt.ENABLED)) {
                                    try {
                                        Helper.showToast(getResources().getString(R.string.successfully_joined_message), Toast.LENGTH_LONG);
                                        Course joinedCourse = joinCourseResponse.getJoinedCourse();
                                        if (LogWriter.isValidLevel(Log.INFO))
                                            LogWriter.write("Joined Course : " + joinedCourse.toString());
                                        if (!CourseInterface.getInstance().isCourseExist(joinedCourse.getCourseID())) {
                                            myCoursesFragment.mMyCoursesAdapter.addItem(joinedCourse);
                                            CourseInterface.getInstance().insertCourse(joinedCourse);
                                            mOfflineCouseIDs.add(joinedCourse.getCourseID());
                                        }

                                        if (myCoursesFragment.mMyCoursesAdapter.getItemCount() > 0) {
                                            myCoursesFragment.mEmptyTextView.setVisibility(View.GONE);
                                            myCoursesFragment.mEmptyButton.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e) {
                                        LogWriter.err(e);
                                    }
                                }
                                //refreshView(true);
                            } else if (message.obj instanceof MuteSettingResponse) {
                                if (muteSettingRequestType != MuteSetting.SET_SETTING) {
                                    showMuteDialog((MuteSettingResponse) message.obj);
                                }
                            } else if (message.obj instanceof InviteUsersResponse) {
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("SUCCESS_RESPONSE InviteUsers : " + message.obj.toString());
                                if (((InviteUsersResponse) message.obj).getSent() == Flinnt.TRUE) {
                                    Helper.showToast("Email-Id/Number : " + invited_email_mob + " is invited", Toast.LENGTH_SHORT);
                                    invited_email_mob = "";
                                }
                            } else if (message.obj instanceof LoginResponse) {
                                // Check for account verified or not
                                if (canBrowseCourse == Flinnt.INVALID) {
                                    setCanBrowseCourse(((LoginResponse) message.obj).getCanBrowseCourse());
                                    updateDrawerItem(null);
                                }
                                updateAccountsList((LoginResponse) message.obj);
                                updateProfileData();
                                genderOrDobValidation((LoginResponse) message.obj);
                                setMainIntentIfNeed(((LoginResponse) message.obj));

                                //TODO category_subscribed=0 then open once in day
                                if (((LoginResponse) message.obj).getCategorySubscribed() == 0) {
                                    manageCategoryPrompt();
                                }

                            } else if (message.obj instanceof AppUpdateResponse) {
                                Config.setStringValue(Config.LAST_APP_UPDATE_REQUEST_SEND_TIME, String.valueOf(System.currentTimeMillis()));
//                                setupAppUpdateDialog((AppUpdateResponse) message.obj);
                            } else if (message.obj instanceof CourseReviewReadResponse) {
                                LogWriter.write("SUCCESS_RESPONSE Rating : " + message.obj.toString());
                                //******* v2.0.27

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
                            } else if (message.obj instanceof UpdateBirthdateResponse) {
                                UpdateBirthdateResponse mUpdateBirthdateResponse = (UpdateBirthdateResponse) message.obj;
                                if (mUpdateBirthdateResponse.getStatus() == Flinnt.TRUE) {
                                    Helper.showToast(getString(R.string.birthdate_update_message), Toast.LENGTH_LONG);
                                    Config.setGenderOrDobSnakbar(Config.FLINNT_SNACKBAR_GENDER_DOB + Config.getStringValue(Config.USER_ID), "1");
                                    updateDOBDialog.dismiss();
                                }
                            }


                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof MenuBannerResponse) {
                                updateMenu((MenuBannerResponse) message.obj);
                                updateBanner((MenuBannerResponse) message.obj);
                                invalidateOptionsMenu();
                            } else if (message.obj instanceof JoinCourseResponse) {
                                //( (JoinCourseResponse) message.obj )
                                if (null != ((JoinCourseResponse) message.obj).errorResponse.getMessage()) {
                                    Helper.showAlertMessage(MyCoursesActivity.this, "Error", ((JoinCourseResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                                }
                            } else if (message.obj instanceof MuteSettingResponse) {
                                //( (JoinCourseResponse) message.obj )
                                if (null != ((MuteSettingResponse) message.obj).errorResponse.getMessage()) {
                                    Helper.showAlertMessage(MyCoursesActivity.this, "Error", ((MuteSettingResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                                }
                            } else if (message.obj instanceof InviteUsersResponse) {
                                InviteUsersResponse failResponse = (InviteUsersResponse) message.obj;
                                invited_email_mob = "";
                                if (failResponse.errorResponse != null) {
                                    Helper.showAlertMessage(MyCoursesActivity.this, "Error", failResponse.errorResponse.getMessage(), "Close");
                                }
                            } else if (message.obj instanceof LoginResponse) {
                                LoginResponse failureResponse = (LoginResponse) message.obj;
                                if (failureResponse.errorResponse != null) {
                                    // show error dialog & on click OK -> logout & redirect to manual login page
//                                    showUserDeletedDialog(failureResponse.errorResponse.getMessage());
                                }
                            } else if (message.obj instanceof UpdateBirthdateResponse) {
                                if (((UpdateBirthdateResponse) message.obj).errorResponse != null)
                                    Helper.showAlertMessage(MyCoursesActivity.this, "Error", ((UpdateBirthdateResponse) message.obj).errorResponse.message, "CLOSE");
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }

        };
        sendAllRequests();
        setMarqueeRecyclerView();
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_courses);

        mFloatingActionMenu = (FloatingActionsMenu) findViewById(R.id.my_course_fab_menu);

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager_courses);

        coursesViewPagerAdapter = new CoursesViewPagerAdapter(getSupportFragmentManager());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                currentPage = position;

                if (position == 3) {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(false);
                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem()); //@Chirag:23/08/2018
                    ((MyERPFragment) fragment).onOffsetChanged(MyERPFragment.WEBVIEW_CALL_API_THEN_LOAD_URL); //@Chirag:23/08/2018
                    storeSearchBox.setVisibility(View.GONE);
                    //rlFilterBottomContainer.setVisibility(View.GONE);


                }

                else if (position == 2) {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(false);
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                    storeSearchBox.setVisibility(View.VISIBLE);
                    //rlFilterBottomContainer.setVisibility(View.VISIBLE);


                    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                        @Override
                        public void onStateChanged(AppBarLayout appBarLayout, State state) {
                            LogWriter.write("Appbar state:-"+state.name());
                            switch (state){
                                case IDLE:
                                    //do nothing.
                                    break;
                                case EXPANDED:
                                    storeSearchBox.setVisibility(View.GONE);
                                    break;
                                case COLLAPSED:
                                    storeSearchBox.setVisibility(View.VISIBLE);
                                    break;
                            }

                        }
                    });



//                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem()); //@Chirag:23/08/2018
//                    ((BrowseCoursesFragmentNew) fragment).onOffsetChanged(BrowseCoursesFragmentNew.WEBVIEW_CALL_API_THEN_LOAD_URL); //@Chirag:23/08/2018

                }

                else {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(true);

                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                    behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                        @Override
                        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                            return true;
                        }
                    });

                    storeSearchBox.setVisibility(View.GONE);
                    //rlFilterBottomContainer.setVisibility(View.GONE);


                }

                doSearch("", false);
                if (searchView != null && !searchView.isIconified()) {
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    invalidateOptionsMenu();
                }
                updateFabVisibility();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

        });

        mViewPager.setAdapter(coursesViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

//                if (tab.getPosition() == 3) {
//////
//////                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//////                    ((MyERPFragment) fragment).onOffsetChanged(MyERPFragment.WEBVIEW_REFRESH_RECEIVED_URL);
//////
//////                }
//////
//////                if (tab.getPosition() == 2) {
//////
////////                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem());
////////                    ((BrowseCoursesFragmentNew) fragment).onOffsetChanged(BrowseCoursesFragmentNew.WEBVIEW_REFRESH_RECEIVED_URL);
//////                    storeSearchBox.setVisibility(View.VISIBLE);
//////
//////                }else{
//////                    storeSearchBox.setVisibility(View.GONE);
//////
//////                }
                currentPage = tab.getPosition();

                if (tab.getPosition() == 3) {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(false);
                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem()); //@Chirag:23/08/2018
                    ((MyERPFragment) fragment).onOffsetChanged(MyERPFragment.WEBVIEW_CALL_API_THEN_LOAD_URL); //@Chirag:23/08/2018
                    storeSearchBox.setVisibility(View.GONE);
                    //rlFilterBottomContainer.setVisibility(View.GONE);


                }

                else if (tab.getPosition() == 2) {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(false);
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                    storeSearchBox.setVisibility(View.VISIBLE);
                    //rlFilterBottomContainer.setVisibility(View.VISIBLE);


                    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                        @Override
                        public void onStateChanged(AppBarLayout appBarLayout, State state) {
                            LogWriter.write("Appbar state:-"+state.name());
                            switch (state){
                                case IDLE:
                                    //do nothing.
                                    break;
                                case EXPANDED:
                                    storeSearchBox.setVisibility(View.GONE);
                                    break;
                                case COLLAPSED:
                                    storeSearchBox.setVisibility(View.VISIBLE);
                                    break;
                            }

                        }
                    });



//                    Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem()); //@Chirag:23/08/2018
//                    ((BrowseCoursesFragmentNew) fragment).onOffsetChanged(BrowseCoursesFragmentNew.WEBVIEW_CALL_API_THEN_LOAD_URL); //@Chirag:23/08/2018

                }

                else {
                    mViewPager.setPagingEnabled(false);
                    appBarLayout.setExpanded(true);

                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                    behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                        @Override
                        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                            return true;
                        }
                    });

                    storeSearchBox.setVisibility(View.GONE);
                    //rlFilterBottomContainer.setVisibility(View.GONE);
                }

                doSearch("", false);
                if (searchView != null && !searchView.isIconified()) {
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    invalidateOptionsMenu();
                }
                updateFabVisibility();
            }
        });

        try {
            Boolean needToSendRequestAppUpdate = true;
            Long preSendRequestAppUpdateTime = Long.parseLong(Config.getStringValue(Config.LAST_APP_UPDATE_REQUEST_SEND_TIME));
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("preSendRequestAppUpdateTime : " + preSendRequestAppUpdateTime);
            if ((System.currentTimeMillis() - preSendRequestAppUpdateTime) < 24 * 60 * 60 * 1000) {
                needToSendRequestAppUpdate = false;
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("no need to send App update request --->");
            } else {
                try {
                    long oldTimeStamp = System.currentTimeMillis() - 604800000L;
                    NotificationInterface.getInstance().deleteOlderNotificationForUser(oldTimeStamp);
                } catch (Exception e) {
                    LogWriter.err(e.getMessage());
                }
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("need to send App update request ---> it's more then a day");
            }

            String versionName = Config.getStringValue(Config.FLINNT_VERSION_NAME);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("Flinnt versionName : " + versionName);
            if (needToSendRequestAppUpdate) {
                if (!TextUtils.isEmpty(versionName)) {
                    versionName = Helper.getAppVersionName(MyCoursesActivity.this);
                }
                new AppUpdate(mHandler,versionName).sendAppUpdateRequest();

            } else if (AppUpdate.getLastResponse().getHardUpdate() == Flinnt.TRUE
                    && AppUpdate.getLastResponse().getGracePeriod() == Flinnt.FALSE) {
//                setupAppUpdateDialog(AppUpdate.getLastResponse());
            }

            LogWriter.write("Temp Notificaiton delete code execution  : ");

        } catch (Exception e) {
            LogWriter.err(e);
        }

        boolean galleryScan = Config.getBoolValue(Config.GALLERY_SCAN);
        if (!galleryScan) {
            new UpdateGallery().execute();
            Config.setBoolValue(Config.GALLERY_SCAN, true);
        }
        //@Nikhil 20062018
        RequestAllPermission();


        //Log.d(TAG, "isFromNotification : " + isFromNotification);
        if (isBrowseCourseTA) {
            mViewPager.setCurrentItem(2, true);
            isBrowseCourseTA = false;
        } else {
            //@Nikhil TODO testing for notification
            if (isFromNotification == Flinnt.TRUE) {

                mViewPager.setCurrentItem(1);
            }

            if (isFromNotification == Flinnt.FALSE) {
                //Log.d(TAG, "Last isNotification is false .currentUserId : " + Config.getStringValue(Config.USER_ID));
                mViewPager.setCurrentItem(0);
                //mViewPager.getAdapter().notifyDataSetChanged(); //@Chirag 03/08/18
            }

        }

    }

    private void manageCategoryPrompt() {

        if (Config.getStringValue(Config.getStringValue(Config.USER_ID) + Config.DATE).equals(new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()))) {

        } else {
            Intent intent = new Intent(MyCoursesActivity.this, CategorylistActivity.class);
            startActivity(intent);
            Config.setStringValue(Config.getStringValue(Config.USER_ID) + Config.DATE, new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()));
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
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("user_id", Config.getStringValue(Config.USER_ID));
        StringEntity entity = new StringEntity(jsonParams.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        try {
            client.setConnectTimeout(50000);
            client.post(getApplicationContext(), Flinnt.API_URL + Flinnt.URL_POPULAR_STORY_LIST, entity, "application/json", new BaseJsonHttpResponseHandler<PopularStoryList>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final PopularStoryList response) {
                    //progressDialog.dismiss();
                    //Log.d(TAG, "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], rawJsonResponse = [" + rawJsonResponse + "], response = [" + response + "]");
                    try {
                        if (response.getStatus().equals(1)) {
                            //storyDataModelArrayList
                            /*storyDataModelArrayList.clear();
                            storyDataModelArrayList.addAll(response.getData().getStories());
                            mMyPromoCourseAdapter.updateDataSource(storyDataModelArrayList);
                            mDisplayCourserRecyclerView.setAdapter(mMyPromoCourseAdapter);*/ //@chirag;31/08/2018 commented

                            storyDataModelArrayList.clear();
                            mMyPromoCourseAdapter.setGalleryUrl(response.getData().getGalleryUrl());//@chirag:31/08/2018 added
                            storyDataModelArrayList.addAll(response.getData().getStories());
                            mMyPromoCourseAdapter.updateDataSource(storyDataModelArrayList);
                            mDisplayCourserRecyclerView.setAdapter(mMyPromoCourseAdapter);
//
//                            storyDataModelArrayList.addAll(response.getData().getStories());
//                            myCustomPagerAdapter = new MyCustomPagerAdapter(MyCoursesActivity.this, storyDataModelArrayList);
                            //mDisplayCourserRecyclerView.setAdapter(myCustomPagerAdapter);
                            if (promoCourseTimer != null) {
                                promoCourseTimer.cancel();
                                promoCourseTimer.purge();
                                promoCourseTimer = null;
                            }
                            promoCourseTimer = new Timer();
                            promoCourseTimer.schedule(new RemindTask(mDisplayCourserRecyclerView), 5000, 5000);
                            if (storyDataModelArrayList.size() > 0)
                                pagerContainer.setVisibility(View.VISIBLE);
                            else
                                pagerContainer.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, PopularStoryList errorResponse) {
                    //progressDialog.dismiss();
                    // Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    //Log.d("TAG", "onFailure() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], throwable = [" + throwable + "], rawJsonData = [" + rawJsonData + "], errorResponse = [" + errorResponse + "]");
                }

                @Override
                protected PopularStoryList parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    //Log.e("TAG", "parseResponse() called with: rawJsonData = [" + rawJsonData + "], isFailure = [" + isFailure + "]");
                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return new Gson().fromJson(rawJsonData, PopularStoryList.class);
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

    private PermissionUtil.PermissionRequestObject mALLPermissionRequest;

    public void RequestAllPermission() {
        String[] allPermission = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.RECEIVE_SMS,};

        mALLPermissionRequest = PermissionUtil.with(this).request(allPermission).onResult(
                new Func2() {
                    @Override
                    protected void call(int requestCode, String[] permissions, int[] grantResults) {
                        for (int i = 0; i < permissions.length; i++) {

                        }
                    }
                }).onAllGranted(new Func() {
            @Override
            protected void call() {

            }
        }).ask(101);
    }

    private void setMarqueeRecyclerView() {
        pagerContainer = (PagerContainer) findViewById(R.id.course_display_pager_container);
        mDisplayCourserRecyclerView = pagerContainer.getViewPager();
        // Disable clip to padding
        mDisplayCourserRecyclerView.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        mDisplayCourserRecyclerView.setPadding(40, 0, 40, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mDisplayCourserRecyclerView.setPageMargin(20);            //mDisplayCourserRecyclerView.setLayoutManager(promoLayoutManager);

        //@Nikhill 20062018
        mMyPromoCourseAdapter = new PromoCoursePagerAdapter(this, new ArrayList<PopularStoryDataModel.Story>());
        mDisplayCourserRecyclerView.setAdapter(mMyPromoCourseAdapter);
        if (Helper.isConnected()) {
            //@Nikhill 20062018
            try {
                WebService_StoryList();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void setTitleToolbar() {
        boolean needToAddView = false;
        if (mToolbarTitle == null) {
            mToolbarTitle = new TextView(this);
            needToAddView = true;
        }
        mToolbarTitle.setText(getResources().getString(R.string.toolbar_title));
        mToolbarTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mToolbarTitle.setTextSize(20);
        mToolbarTitle.setSingleLine(true);
        mToolbarTitle.setPadding(20, 0, 0, 0);
        mToolbarTitle.setTextAppearance(this, android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
        mToolbarTitle.setTextColor(Color.WHITE);
        if (needToAddView) {
            mMainToolbar.addView(mToolbarTitle);
        }
    }

//    private void requestPromoCourses() {
//        PromoCourse mMyCoursePromoCourse = new PromoCourse(new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case Flinnt.SUCCESS:
//                        PromoCourseResponse response = (PromoCourseResponse) msg.obj;
//                        myCoursePromoList.clear();
//                        mMyPromoCourseAdapter.setmCoursePictureUrl(response.getData().getCoursePictureUrl());
//                        myCoursePromoList.addAll(response.getData().getCourses());
//                        if (mViewPager.getCurrentItem() == 0) {
//                            if (promoCourseTimer != null) {
//                                promoCourseTimer.cancel();
//                                promoCourseTimer.purge();
//                                promoCourseTimer = null;
//                            }
//                            mMyPromoCourseAdapter.updateDataSource(myCoursePromoList);
//                            mDisplayCourserRecyclerView.setAdapter(mMyPromoCourseAdapter);
//
//                            promoCourseTimer = new Timer();
//                            promoCourseTimer.schedule(new RemindTask(mDisplayCourserRecyclerView), 5000, 5000);
//                            if (myCoursePromoList.size() > 0)
//                                pagerContainer.setVisibility(View.VISIBLE);
//                            else
//                                pagerContainer.setVisibility(View.GONE);
//                        }
//                        break;
//                    case Flinnt.FAILURE:
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        });
//        mMyCoursePromoCourse.setSource(PromoCourseRequest.PROMOTE_COURSE_MOBILE_MY_COURSE);
//        mMyCoursePromoCourse.sendPromoCourseRequest();
//
//        PromoCourse mBrowseCoursePromoCourse = new PromoCourse(new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case Flinnt.SUCCESS:
//                        PromoCourseResponse response = (PromoCourseResponse) msg.obj;
//                        browseCoursePromoList.clear();
//                        browseCoursePromoList.addAll(response.getData().getCourses());
//                        mBrowsePromoCOurseAdapter.setmCoursePictureUrl(response.getData().getCoursePictureUrl());
//
//                        if (mViewPager.getCurrentItem() == 1) {
//                            if (promoCourseTimer != null) {
//                                promoCourseTimer.cancel();
//                                promoCourseTimer.purge();
//                                promoCourseTimer = null;
//                            }
//                            mBrowsePromoCOurseAdapter.updateDataSource(browseCoursePromoList);
//                            mDisplayCourserRecyclerView.setAdapter(mBrowsePromoCOurseAdapter);
//                            promoCourseTimer = new Timer();
//                            promoCourseTimer.schedule(new RemindTask(mDisplayCourserRecyclerView), 5000, 5000);
//                            if (browseCoursePromoList.size() > 0)
//                                pagerContainer.setVisibility(View.VISIBLE);
//                            else
//                                pagerContainer.setVisibility(View.GONE);
//                        }
//                        break;
//                    case Flinnt.FAILURE:
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        });
//        mBrowseCoursePromoCourse.setSource(PromoCourseRequest.PROMOTE_COURSE_MOBILE_BROWSE_COURSE);
//        mBrowseCoursePromoCourse.sendPromoCourseRequest();
//    }

    private void doSearch(String query, Boolean isSubmit) {

        Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment instanceof MyCoursesFragment) {
            ((MyCoursesFragment) fragment).onSearch(query, isSubmit);
        } else if (fragment instanceof BrowseCoursesFragmentNew) {
            ((BrowseCoursesFragmentNew) fragment).onSearch(query, isSubmit);
        } else if (fragment instanceof MyStoriesFragment) {
            ((MyStoriesFragment) fragment).onSearch(query, isSubmit);
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

//        Fragment fragment = coursesViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//        if (fragment instanceof MyCoursesFragment) {
//            ((MyCoursesFragment) fragment).onOffsetChanged(i);
//        } else if (fragment instanceof BrowseCoursesFragmentNew) {
//            mTooltipBrowseCourseLinear.setVisibility(View.GONE);
//            Config.setToolTipValue(Config.FLINNT_TOOLTIP_BCOURSE_STATS + Config.getStringValue(Config.USER_ID), "1");
//            ((BrowseCoursesFragmentNew) fragment).onOffsetChanged(i);
//        }
    }

    public void updateFabVisibility() {

        if (mViewPager.getCurrentItem() == 0) {
            mFloatingActionMenu.setVisibility(View.GONE);
            fab_category.setVisibility(View.VISIBLE);
        } else if (mViewPager.getCurrentItem() == 1) {

            boolean hasCourses = false;


            if (null != myCoursesFragment.mMyCoursesAdapter) {
                hasCourses = myCoursesFragment.mMyCoursesAdapter.getItemCount() > 0;

            }

            mFloatingActionMenu.setVisibility((hasCourses && canShowFab) ? View.VISIBLE : View.GONE);


            fab_category.setVisibility(View.GONE);
            //TODO make sure this field is conditinaly true or flase before submitting

        } else {
            mFloatingActionMenu.setVisibility(View.GONE);
            fab_category.setVisibility(View.GONE);
        }
    }

    public class CoursesViewPagerAdapter extends FragmentStatePagerAdapter {

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public CoursesViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (null == myStoryFragment) {
                    myStoryFragment = MyStoriesFragment.newInstance();
                }
                return myStoryFragment;
            } else if (position == 1) {
                if (null == myCoursesFragment) {
                    myCoursesFragment = MyCoursesFragment.newInstance();
                }
                return myCoursesFragment;
            } else if (position == 2) {
                if (null == browseCoursesFragment) {
                    browseCoursesFragment = BrowseCoursesFragmentNew.newInstance();
                }
                return browseCoursesFragment;
            } else if (position == 3) {
                if (null == myERPFragment) {
                    myERPFragment = MyERPFragment.newInstance();
                }
                return myERPFragment;
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

        //@Chirag 03/08/2018 added
        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }
    }


    private void sendAllRequests() {
        User user = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID));
        boolean isTokenSentToServer = null != user && user.getTokenSentToServer() == Flinnt.TRUE;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("isTokenSentToServer : " + isTokenSentToServer);
        if (!isTokenSentToServer) {
            // Start IntentService to register this application with GCM.
            if (TextUtils.isEmpty(Config.getStringValue(Config.FCM_TOKEN))) {
                String token = FirebaseInstanceId.getInstance().getToken();
                Config.setStringValue(Config.FCM_TOKEN, token);
                new RegisterDevice(null, RegisterDevice.REGISTER_DEVICE, Config.getStringValue(Config.USER_ID)).sendRegisterDeviceRequest();
            } else {
                new RegisterDevice(null, RegisterDevice.REGISTER_DEVICE, Config.getStringValue(Config.USER_ID)).sendRegisterDeviceRequest();
            }
        }

        setCanBrowseCourse(Flinnt.INVALID);

        // auto login response
        new Login(mHandler, Login.AUTO_LOGIN).sendLoginRequest();
        //if( TextUtils.isEmpty(Config.getStringValue(Config.LAST_MENU_BANNER_POST_RESPONSE)) ) {
        // send menu banner request
        new MenuBanner(mHandler, null).sendMenuBannerRequest();

        new MenuStatistics(mHandler, "").sendMenuStatisticsRequest();

        new Highlights(mHandler).sendHighlightRequest();
    }

    /**
     * Update offline gallery
     */
    private class UpdateGallery extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            /** delete .nomedia directory if exist ).... because gallery can not scan other images if this folder exist...
             * so we change foler name to .tempmedia
             * code creation date : 19th Nov 2015
             * version : 2.0.8 */
            Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + ".nomedia"));
            Helper.updateGallery();
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewIntent --> ");

            if (bundle.containsKey("isFromNotification")) {
                isFromNotification = bundle.getInt("isFromNotification");
                //Log.d(TAG,"onNewIntent : " + isFromNotification);
                if (isFromNotification == 1) mViewPager.setCurrentItem(1);
            }

            if (bundle.containsKey("doWhat") && bundle.getString("doWhat").equals("deleteUser")) {
                showUserDeletedDialog(bundle.getString("errMsg"));
            } else {
                boolean isNeedToAddCourse = false;
                if (bundle.containsKey(LoginResponse.USER_ID_KEY)) {
                    String userID = bundle.getString(LoginResponse.USER_ID_KEY);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onNewIntent userID : " + userID + ", Config userID : " + Config.getStringValue(Config.USER_ID));
                    if (!TextUtils.isEmpty(userID) && !userID.equals(Config.getStringValue(Config.USER_ID))) {
                        swapAccountAndReload(userID);
                    } else {
                        isNeedToAddCourse = true;
                    }
                }

                if (isNeedToAddCourse && bundle.containsKey(AddPostResponse.COURSE_KEY)) {
                    Course acceptedCourse = bundle.getParcelable(AddPostResponse.COURSE_KEY);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("acceptedCourse : " + acceptedCourse.toString());
                    if (!CourseInterface.getInstance().isCourseExist(acceptedCourse.getCourseID())) {
                        myCoursesFragment.mMyCoursesAdapter.addItem(acceptedCourse);
                        CourseInterface.getInstance().insertOrUpdateCourse(acceptedCourse);
                        mOfflineCouseIDs.add(acceptedCourse.getCourseID());

                        if (myCoursesFragment.mMyCoursesAdapter.getItemCount() < 1) {
                            myCoursesFragment.mEmptyTextView.setVisibility(View.VISIBLE);
                            myCoursesFragment.mEmptyButton.setVisibility(View.VISIBLE);
                        } else {
                            myCoursesFragment.mEmptyTextView.setVisibility(View.GONE);
                            myCoursesFragment.mEmptyButton.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isNavHeaderExecute) {
            updateProfileData();
            isNavHeaderExecute = true;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Updates profile data
     */
    private void updateProfileData() {

        try {
            final RelativeLayout navHeader = (RelativeLayout) findViewById(R.id.nav_header_layout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                navHeader.setPadding(Helper.getDip(16), Helper.getDip(42), Helper.getDip(16), Helper.getDip(16));
            } else {
                navHeader.setPadding(Helper.getDip(16), Helper.getDip(16), Helper.getDip(16), Helper.getDip(16));
            }
            final PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.parseColor("#615F60"), android.graphics.PorterDuff.Mode.MULTIPLY);
            navHeader.getBackground().setColorFilter(greyFilter);

            TextView drawerText = (TextView) mHeaderView.findViewById(R.id.text_name_drawer);
            drawerText.setText(Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));

            TextView drawerEmail = (TextView) mHeaderView.findViewById(R.id.text_email_drawer);
            drawerEmail.setText(Config.getStringValue(Config.USER_LOGIN).toLowerCase());

            final SelectableRoundedImageView drawerImage = (SelectableRoundedImageView) mHeaderView.findViewById(R.id.round_image_drawer);

            profileImageUrl = Config.getStringValue(Config.PROFILE_URL);
            profileImageName = Config.getStringValue(Config.PROFILE_NAME);

            final String profileUrl = profileImageUrl + Flinnt.PROFILE_LARGE + File.separator + profileImageName;

            Bitmap profileBitmap = Helper.getBitmapFromSDcard(Helper.getFlinntUrlPath(profileUrl), profileImageName);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("profile URL : " + profileUrl + " , profileBitmap : " + profileBitmap + " , " + Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));
            if (profileBitmap == null) {
                mImageLoader.get(profileUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("onResponse Bitmap : " + response.getBitmap());
                            if (response.getBitmap() != null) {
                                drawerImage.setImageBitmap(response.getBitmap());
                                Drawable blurImage = Helper.getDrawableFromBitmap(FlinntApplication.getContext(), BlurBuilder.fastblur(response.getBitmap(), 5));
                                if (Build.VERSION.SDK_INT >= 16) {
                                    navHeader.setBackground(blurImage);
                                } else {
                                    navHeader.setBackgroundDrawable(blurImage);
                                }
                                //PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.parseColor("#615F60"), android.graphics.PorterDuff.Mode.MULTIPLY);
                                navHeader.getBackground().setColorFilter(greyFilter);

                                Helper.saveBitmapToSDcard(response.getBitmap(), Helper.getFlinntUrlPath(profileUrl), profileImageName);

                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("VolleyError message : " + error.getMessage());
                        drawerImage.setImageResource(R.drawable.default_user_profile_image);
                        navHeader.setBackgroundResource(R.drawable.blurry_user_profile_image);
                        navHeader.getBackground().setColorFilter(greyFilter);
                    }
                });
            } else {
                try {
                    drawerImage.setImageBitmap(profileBitmap);
                    Drawable blurImage = Helper.getDrawableFromBitmap(FlinntApplication.getContext(), BlurBuilder.fastblur(profileBitmap, 5));
                    if (Build.VERSION.SDK_INT >= 16) {
                        navHeader.setBackground(blurImage);
                    } else {
                        navHeader.setBackgroundDrawable(blurImage);
                    }
                    navHeader.getBackground().setColorFilter(greyFilter);
                } catch (Exception e) {
                    LogWriter.err(e);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        //Log.d(TAG, "onResume()");
        try {
            appBarLayout.addOnOffsetChangedListener(this);
            new Highlights(mHandler).sendHighlightRequest();

            updateDrawerItem(null);
            updateAccountsList(null);
            updateBanner(MenuBanner.getLastResponse());

        } catch (Exception e) {
            LogWriter.err(e);
        }

        //Log.d(TAG, "onResume() //End");

    }

    /**
     * Create and run banners fade show
     */
    private void bannerFadeShow() {

        try {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("\nbannerFadeShowRunnable : " + runnable
                        + "\nallLayers.size() = " + allLayers.size());

            if (allLayers.size() > 0) {
//                if (null == runnable) {

                runnable = new Runnable() {

                    @Override
                    public void run() {
                        if (allLayers.size() > currentBannerPosition) {
                            layers[0] = allLayers.get(currentBannerPosition);

                            if (bannerZeroZero) {
                                layers[1] = allLayers.get(0);
                            } else {
                                if (currentBannerPosition == allLayers.size() - 1) {
                                    layers[1] = allLayers.get(0);
                                } else {
                                    if (currentBannerPosition < allLayers.size()) {
                                        layers[1] = allLayers.get(currentBannerPosition + 1);
                                    }
                                }
                            }

                            // ( TODO: Test this )
                            // Don't animate if there is exactly one Banner
                            if (allLayers.size() == 1) {
                                mMyCourseImg.setImageDrawable(layers[0]);
                                bannerZeroZero = false;
                            } else {
                                if (bannerZeroZero) {
                                    mMyCourseImg.setImageDrawable(layers[0]);
                                } else {
                                    transition = new TransitionDrawable(layers);
                                    transition.setCrossFadeEnabled(true);
                                    mMyCourseImg.setImageDrawable(transition);
                                    transition.startTransition(BANNER_FADE_EFFECT_TIMEOUT);
                                }
                                currentBannerPosition++;

                                if (currentBannerPosition == allLayers.size() || bannerZeroZero) {
                                    currentBannerPosition = 0;
                                    bannerZeroZero = false;
                                }
                            }
                        }
                        mImageHandler.postDelayed(runnable, BANNER_CHANGE_TIMEOUT);
                    }
                };
//                }

                if (isFreshBannerShow == true) {   // FadeShow starts for the first time when app just started
                    mImageHandler.postDelayed(runnable, 0);
                    isFreshBannerShow = false;
                } else {
                    mImageHandler.removeCallbacks(runnable);
                    currentBannerPosition = 0;
                    mImageHandler.postDelayed(runnable, BANNER_CHANGE_TIMEOUT);
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            appBarLayout.removeOnOffsetChangedListener(this);
            mImageHandler.removeCallbacks(runnable);
            if (searchView != null && !searchView.isIconified()) {
                searchView.setIconified(true);
                searchView.clearFocus();
                invalidateOptionsMenu();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            invalidateOptionsMenu();
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawers();
            } else {
                if (searchView != null && !searchView.isIconified()) {
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    invalidateOptionsMenu();
                } else {

                    //@Chirag  - this whole block added by chirag 03/08/2018
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        // Cancel all pending request before exit
                        Requester.getInstance().cancelPendingRequests();
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 3000);

                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    /**
     * Show mute settings dialog which lets user to enable or disable course notifications
     *
     * @param muteSettingResponse mutesettings response
     */
    protected void showMuteDialog(final MuteSettingResponse muteSettingResponse) {
        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
            // set view
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.mute_dialog_design, null);
            alertDialogBuilder.setView(dialogView);

            TextView title = new TextView(this);
            title.setText(getResources().getString(R.string.mute_text));
            title.setPadding(40, 40, 40, 40);
            title.setGravity(Gravity.CENTER_VERTICAL);
            title.setTextColor(getResources().getColor(R.color.ColorPrimary));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(title);
            alertDialogBuilder.setPositiveButton("DONE", null);


//            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
//            builder.setTitle("Mute");
//            builder.setPositiveButton("DONE", null);
//            builder.setView(R.layout.mute_dialog_design);

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
            switchSound.setChecked(muteSettingResponse.getCourseMuteSound().equals(Flinnt.ENABLED));
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
                        Helper.showNetworkAlertMessage(MyCoursesActivity.this);
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

    String invitationCount = "0", alertsCount = "0";

    /**
     * Update Navigation sliding drawer items
     *
     * @param menuStatisticsResponse menu statistics response
     */
    public void updateDrawerItem(MenuStatisticsResponse menuStatisticsResponse) {
        try {
            int courseInvitePosition = 3, alertsPosition = 6;
            if (canSendJoinRequest == Flinnt.FALSE) {
                alertsPosition = 6;
            } else {
                alertsPosition = 7;
            }

            primaryDrawerItemList.clear();
            for (int i = 0; i < mMenuTitles.length; i++) {
                SlideMenuItem item = new SlideMenuItem();
                item.setTitle(mMenuTitles[i]);
                item.setIconID(mMenuIconId[i]);

                LogWriter.write("canBrowseCourse :: " + canBrowseCourse);

                if (mMenuIconId[i] == R.drawable.ic_drawer_browse_courses) {
                    /*if (canBrowseCourse == Flinnt.TRUE) {
                        primaryDrawerItemList.add(item);
                        courseInvitePosition++;
                        alertsPosition++;
                    }*/
                } else {
                    if (mMenuIconId[i] == R.drawable.ic_select_institute) {
                        if (canSendJoinRequest == Flinnt.TRUE) {
                            primaryDrawerItemList.add(item);
                        }
                    } else {
                        primaryDrawerItemList.add(item);
                    }
                }
            }
//            primaryDrawerItemList.get(courseInvitePosition).setUnread(menuStatisticsResponse.getInvitations());
            if (null != menuStatisticsResponse) {
                invitationCount = menuStatisticsResponse.getInvitations();
                alertsCount = menuStatisticsResponse.getAlerts();
                if (Integer.parseInt(invitationCount) > 99) {
                    invitationCount = "99+";
                }
                if (Integer.parseInt(alertsCount) > 99) {
                    alertsCount = "99+";
                }

            }

            primaryDrawerItemList.get(courseInvitePosition).setUnread(invitationCount);
            primaryDrawerItemList.get(alertsPosition).setUnread(alertsCount);

            mPrimaryMenuAdapter = new MenuAdapter(MyCoursesActivity.this, R.layout.drawer_list_item, primaryDrawerItemList, true);

            if (isPrimaryMenuShowing) mDrawerList.setAdapter(mPrimaryMenuAdapter);

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Update and set accounts list
     *
     * @param mLoginResponse
     */
    private void updateAccountsList(LoginResponse mLoginResponse) {
        ArrayList<SlideMenuItem> secondaryDrawerItemList = new ArrayList<SlideMenuItem>();
        SlideMenuItem item = new SlideMenuItem();
        item.setTitle(getString(R.string.add_account));
        item.setIconID(R.drawable.ic_add_account);
        secondaryDrawerItemList.add(item);

        if (null != mLoginResponse) {
            User user = new User();
            user.setUserID(mLoginResponse.getUserID());
            user.setUserLogin(mLoginResponse.getUserLogin());
            user.setFirstName(mLoginResponse.getFirstName());
            user.setLastName(mLoginResponse.getLastName());
            user.setIsActive(mLoginResponse.getIsActive());
            user.setUserPicture(mLoginResponse.getUserPicture());
            user.setAccVerified(mLoginResponse.getAccVerified());
            user.setAccAuthMode(mLoginResponse.getAccAuthMode());
            user.setCanAdd(mLoginResponse.getCanAdd());
            user.setUserPictureUrl(mLoginResponse.getUserPictureUrl());
            user.setCanBrowseCourse(mLoginResponse.getCanBrowseCourse());
            user.setTokenSentToServer(UserInterface.getInstance().getTokenValueFromId(mLoginResponse.getUserID()));
            user.setCurrentUser(Flinnt.FALSE);

            UserInterface.getInstance().insertOrUpdateUser(user.getUserID(), user);
            Helper.setCurrentUserConfig(mLoginResponse.getUserID());
        }

        for (User usr : UserInterface.getInstance().getUserList()) {
            item = new SlideMenuItem();
            item.setTitle(usr.getFirstName() + " " + usr.getLastName());
            item.setFirstName(usr.getFirstName());
            item.setIsSelected(usr.getUserID().equals(Config.getStringValue(Config.USER_ID)));
            item.setIconID(Integer.parseInt(usr.getUserID()));
            secondaryDrawerItemList.add(item);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("item.getTitle : " + item.getTitle() + ", userID : " + usr.getUserID());
        }

        mSecondaryMenuAdapter = new MenuAdapter(MyCoursesActivity.this, R.layout.drawer_list_item, secondaryDrawerItemList, false);
        if (!isPrimaryMenuShowing) mDrawerList.setAdapter(mSecondaryMenuAdapter);
    }

    private void toggleDrawerMenu() {
        mSwitchUserIconImg.setImageResource(isPrimaryMenuShowing ? R.drawable.up_w : R.drawable.down_w);
        mDrawerList.setAdapter(isPrimaryMenuShowing ? mSecondaryMenuAdapter : mPrimaryMenuAdapter);
        isPrimaryMenuShowing = !isPrimaryMenuShowing;
    }

    /**
     * Update menu according to users privileges
     *
     * @param menuBannerResponse menu banner response
     */
    private void updateMenu(MenuBannerResponse menuBannerResponse) {


        try {
            canShowFab = false;
            fabBackground = (RelativeLayout) findViewById(R.id.fab_background);
            // Setup FloatingActionMenu


            mFloatingActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {
                    if (fabBackground.getVisibility() == View.INVISIBLE)
                        fabBackground.setVisibility(View.VISIBLE);
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
                        mFloatingActionMenu.collapse();
                        return true;
                    }
                    return false;
                }
            });

            mFabAlert = (FloatingActionButton) findViewById(R.id.my_course_fab_alert);
            mFabMessage = (FloatingActionButton) findViewById(R.id.my_course_fab_message);
            mFabAlbum = (FloatingActionButton) findViewById(R.id.my_course_fab_album);
            mFabQuiz = (FloatingActionButton) findViewById(R.id.my_course_fab_quiz);
            mFabPost = (FloatingActionButton) findViewById(R.id.my_course_fab_post);
            mFabCourse = (FloatingActionButton) findViewById(R.id.my_course_fab_course);
            mFabCommunication = (FloatingActionButton) findViewById(R.id.my_course_fab_communication);
            mFabPoll = (FloatingActionButton) findViewById(R.id.my_course_fab_poll);

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddAlert()) {
                mFabAlert.setOnClickListener(this);
                mFabAlert.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabAlert.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddMessage()) {
                mFabMessage.setOnClickListener(this);
                mFabMessage.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabMessage.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddAlbum()) {
                mFabAlbum.setOnClickListener(this);
                mFabAlbum.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabAlbum.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddQuiz()) {
                mFabQuiz.setOnClickListener(this);
                mFabQuiz.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabQuiz.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddPost()) {
                mFabPost.setOnClickListener(this);
                mFabPost.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabPost.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddCommunication()) {
                mFabCommunication.setOnClickListener(this);
                mFabCommunication.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabCommunication.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanAddPoll()) {
                mFabPoll.setOnClickListener(this);
                mFabPoll.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabPoll.setVisibility(View.GONE);
            }

            if (Flinnt.SUCCESS == menuBannerResponse.getCanCreateCourse()) {
                mFabCourse.setOnClickListener(this);
                mFabCourse.setVisibility(View.VISIBLE);
                canShowFab = true;
            } else {
                mFabCourse.setVisibility(View.GONE);
            }

            updateFabVisibility();

        } catch (Exception e) {
            LogWriter.err(e);

        }

    }

    /**
     * Gets list of banners from response
     *
     * @param menuBannerResponse menu banner response
     */
    private void updateBanner(MenuBannerResponse menuBannerResponse) {

        if (null != menuBannerResponse) {

            try {
                canChangeBanner = menuBannerResponse.getCanChangeBanner();
                canSendJoinRequest = menuBannerResponse.getCanSendJoinRequest();
                bannerList = menuBannerResponse.getBannersList();
                bannerPath = menuBannerResponse.getBannerPath();
                accountBanner = menuBannerResponse.getAccountBanner();

                ArrayList<String> tempList = new ArrayList<String>(bannerList);
                allLayers = new ArrayList<Drawable>();

                for (String banner : tempList) {
                    if (banner.equalsIgnoreCase(accountBanner)) {
                        Collections.swap(bannerList, 0, bannerList.indexOf(banner));
                        break;
                    }
                }

                for (int i = 0; i < bannerList.size(); i++) {
                    //if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Download banner from url");
                    final int position = i;
                    //https://flinnt1.s3.amazonaws.com/banners/user/600x400/7107_1444288296.jpg
                    final String fileName = bannerList.get(position);
                    String url = bannerPath + Flinnt.USER_BANNER_LARGE + File.separator + fileName;

                    Bitmap bannerBitmap = Helper.getBitmapFromSDcard(Helper.getFlinntUrlPath(url), fileName);
                    if (bannerBitmap == null) {
                        mImageLoader.get(url, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response.getBitmap() != null) {
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("onResponse Bitmap : " + response.getBitmap());
                                    Helper.saveBitmapToSDcard(response.getBitmap(), Helper.getFlinntUrlPath(bannerPath + Flinnt.USER_BANNER_LARGE + File.separator + fileName),
                                            fileName);

                                    if (position == 0) {
                                        allLayers.add(0, new BitmapDrawable(getResources(), response.getBitmap()));
                                        mImageHandler.removeCallbacks(runnable);
                                        currentBannerPosition = 0;
                                        bannerZeroZero = true;
                                        bannerFadeShow();
                                    } else {
                                        allLayers.add(new BitmapDrawable(getResources(), response.getBitmap()));
                                    }
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                    } else {
                        allLayers.add(new BitmapDrawable(getResources(), bannerBitmap));
                    }
                }
                bannerFadeShow();

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    /**
     * Gets the latest highlighted alert and display it
     *
     * @param highlightsResponse highlight response
     */
    private void updateHighlights(HighlightsResponse highlightsResponse) {
        try {
            String userID = Config.getStringValue(Config.USER_ID);
            int time = 10 * 1000;
            if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_BCOURSE_STATS + userID).equals("1")) {

                if (highlightsResponse.getAlertID().equals("0")) {
                    return;
                }
            } else {

                time = 2 * 1000;

            }
            mHighlightText.setText(highlightsResponse.getAlertText());
            long millis = highlightsResponse.getAlertID().equals(Flinnt.DISABLED) ? (System.currentTimeMillis() / 1000) : highlightsResponse.getAlertDateLong();
            mHighlightDate.setText(Helper.formateTimeMillis(millis));
            highlightID = highlightsResponse.getAlertID();
            mHighlightLayout.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mHighlightLayout.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(runnable, time);
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.layout_switch) {
                toggleDrawerMenu();
            } else {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                } else {
                    switch (v.getId()) {
                        case R.id.layout_header:
                            Intent profileIntent = new Intent(MyCoursesActivity.this, ProfileActivity.class)
                                    .putExtra(LoginResponse.USER_PICTURE_URL_KEY, profileImageUrl)
                                    .putExtra(LoginResponse.USER_PICTURE_KEY, profileImageName);
                            startActivityForResult(profileIntent, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);
                            break;
                        case R.id.my_course_fab_alert:
                            startActivity(new Intent(MyCoursesActivity.this, AddAlertActivity.class)
                                    .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_ALERT_ADD));
                            break;
                        case R.id.my_course_fab_message:
                            sendSelectCourseRequest(Flinnt.POST_TYPE_MESSAGE);
                            break;
                        case R.id.my_course_fab_album:
                            sendSelectCourseRequest(Flinnt.POST_TYPE_ALBUM);
                            break;
                        case R.id.my_course_fab_quiz:
                            sendSelectCourseRequest(Flinnt.POST_TYPE_QUIZ);
                            break;
                        case R.id.my_course_fab_post:
                            sendSelectCourseRequest(Flinnt.POST_TYPE_BLOG);
//                            startActivity(new Intent(MyCoursesActivity.this, AddCommunicationActivity.class));
                            break;
                        case R.id.my_course_fab_communication:
                            startActivity(new Intent(MyCoursesActivity.this, AddCommunicationActivity.class));
                            break;
                        case R.id.my_course_fab_poll:
                            startActivity(new Intent(MyCoursesActivity.this, AddPollActivity.class));
                            break;
                        case R.id.my_course_fab_course:
//                            Intent courseIntent = new Intent(MyCoursesActivity.this, AddCourseActivity.class)
//                                    .putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_COURSE_ADD);
//
//                            startActivityForResult(courseIntent, ADD_COURSE_CALL_BACK);
                            break;
                        case R.id.my_courses_highlights_layout:
                            if (highlightID.equals(Flinnt.DISABLED)) {
                                startActivity(new Intent(MyCoursesActivity.this, AlertListActivity.class));
                            } else {
                                Intent intent = new Intent(MyCoursesActivity.this, AlertDetailActivity.class);
                                intent.putExtra(Alert.ALERT_ID_KEY, highlightID);
                                startActivity(intent);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
            if (null != mFloatingActionMenu) mFloatingActionMenu.collapse();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        private static final int MAX_ACCOUNTS = 5;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean toCloseDrawer = true;
            try {
                SlideMenuItem item = (SlideMenuItem) parent.getItemAtPosition(position);

                switch (item.getIconId()) {
                    case R.drawable.ic_drawer_my_courses: {
                        //@Nikhil 20062018
                        mViewPager.setCurrentItem(1);
                        //startActivity(new Intent(MyCoursesActivity.this, AddEntry.class));
                    }
                    break;
                    case R.drawable.ic_drawer_join_course: {
                        joinCourseDialog();
                    }
                    break;
                    case R.drawable.ic_drawer_browse_courses: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            //startActivity(new Intent(MyCoursesActivity.this, CourseInvitesActivity.class));
                            startActivityForResult(new Intent(MyCoursesActivity.this, BrowseCoursesActivity.class),
                                    JOIN_COMMUNITY_CALLBACK);
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_wishlist: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            startActivityForResult(new Intent(MyCoursesActivity.this, WishlistActivity.class),
                                    WISHLIST_BROWSE_COURSE_CALLBACK);
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_course_invites: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            //startActivity(new Intent(MyCoursesActivity.this, CourseInvitesActivity.class));
                            startActivityForResult(new Intent(MyCoursesActivity.this, CourseInvitesActivity.class),
                                    MENU_STATISTICS_CALL_BACK);
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_join_community: {
                        //startActivity(new Intent(MyCoursesActivity.this, JoinCommunityActivity.class));
                    }
                    break;
                    case R.drawable.ic_select_institute: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            //startActivity(new Intent(MyCoursesActivity.this, CourseInvitesActivity.class));
                            startActivity(new Intent(MyCoursesActivity.this, InstitutesActivity.class));
                        }
                    }
                    break;

                    case R.drawable.ic_drawer_bookmarks: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            startActivity(new Intent(MyCoursesActivity.this, BookmarkPostActivity.class));
                        }
                    }
                    break;

                    case R.drawable.ic_drawer_notification: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            Intent intent = new Intent(MyCoursesActivity.this, NotificationListActivity.class);
                            startActivityForResult(intent, NOTIFICATIONLIST_CALLBACK);
                        }
                    }
                    break;

                    case R.drawable.ic_drawer_alert: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            //startActivity(new Intent(MyCoursesActivity.this, AlertListActivity.class));
                            startActivityForResult(new Intent(MyCoursesActivity.this, AlertListActivity.class),
                                    MENU_STATISTICS_CALL_BACK);
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_settings: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            startActivity(new Intent(MyCoursesActivity.this, AccountSettingsActivity.class));
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_about_us: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            startActivity(new Intent(MyCoursesActivity.this, ContactUsActivity.class));
                        }
                        //	Helper.showAlertMessage(MyCoursesActivity.this, "Flinnt", "Version : " + Flinnt.VERSION, "Ok");
                    }
                    break;
                    case R.drawable.ic_drawer_help: {
                        startActivity(new Intent(MyCoursesActivity.this, HelpActivity.class).putExtra("TYPE", "help"));
                    }
                    break;
                    case R.drawable.ic_drawer_faq: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            startActivity(new Intent(MyCoursesActivity.this, FaqActivity.class));
                        }
                    }
                    break;
                    case R.drawable.ic_drawer_logout: {
                        //Common comm = new Common(MyCoursesActivity.this);
                        //comm.Logout(MyCoursesActivity.this, "", comm);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
                        TextView titleText = new TextView(MyCoursesActivity.this);
                        titleText.setText(getResources().getString(R.string.logout_text));
                        titleText.setPadding(40, 40, 40, 0);
                        titleText.setGravity(Gravity.CENTER_VERTICAL);
                        titleText.setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                        titleText.setTextSize(20);
                        titleText.setTypeface(Typeface.DEFAULT_BOLD);
                        alertDialogBuilder.setCustomTitle(titleText);

                        alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_text));
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.logout_text), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Helper.resetConfigValuesAndClearData(null);
                                Intent intent = new Intent(MyCoursesActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        if (!Helper.isFinishingOrIsDestroyed(MyCoursesActivity.this)) {
                            alertDialog.show();
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                        }

                    }
                    break;

                    case R.drawable.ic_add_account: {
                        if (UserInterface.getInstance().getUserIdList().size() < MAX_ACCOUNTS) {
                            if (!Helper.isConnected()) {
                                Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                            } else {
                                startActivityForResult(
                                        new Intent(MyCoursesActivity.this, LoginActivity.class)
                                                .putExtra("TYPE", LoginActivity.NEW_ACCOUNT)
                                        , LOGIN_CALLBACK);
                            }
                        } else {
                            toCloseDrawer = false;
                            Helper.showAlertMessage(MyCoursesActivity.this, getString(R.string.add_account), getString(R.string.no_more_accounts_than_5), "OK");
                        }
                        break;
                    }

                    default: {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        } else {
                            String userId = String.valueOf(item.getIconId());
                            if (!userId.equals(Config.getStringValue(Config.USER_ID))) {
                                User user = UserInterface.getInstance().getUserFromId(userId);
                                if (!user.getAccVerified().equals(Flinnt.ENABLED)) {
                                    if (user.getAccAuthMode().equals("email")) {
                                        startActivityForResult(
                                                new Intent(MyCoursesActivity.this, VerifyEmailActivity.class)
                                                        .putExtra(Config.USER_ID, userId).putExtra("TYPE", SWAP_ACCOUNT_OPERATION), LOGIN_CALLBACK);
                                    } else {
                                        startActivityForResult(
                                                new Intent(MyCoursesActivity.this, VerifyMobileActivity.class)
                                                        .putExtra(Config.USER_ID, userId).putExtra("TYPE", SWAP_ACCOUNT_OPERATION), LOGIN_CALLBACK);
                                    }
                                } else {
                                    swapAccountAndReload(userId);
                                }
                            } else {
                                toCloseDrawer = false;
                            }
                        }// TODO: close this
                    }
                    break;
                }
            } catch (Exception e) {
                LogWriter.err(e);
            }
            if (toCloseDrawer) mDrawerLayout.closeDrawers();
        }
    }

    private class DrawerItemLongClickListener implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            try {
                final SlideMenuItem item = (SlideMenuItem) parent.getItemAtPosition(position);
                final String userId = String.valueOf(item.getIconId());

                if (!userId.equals(Config.getStringValue(Config.USER_ID))) {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                    } else {
                        if (UserInterface.getInstance().getUserIdList().contains(userId)) {
                            mDrawerLayout.closeDrawers();

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
                            TextView titleText = new TextView(MyCoursesActivity.this);
                            titleText.setText("Remove Account");
                            titleText.setPadding(40, 40, 40, 0);
                            titleText.setGravity(Gravity.CENTER_VERTICAL);
                            titleText.setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                            titleText.setTextSize(20);
                            titleText.setTypeface(Typeface.DEFAULT_BOLD);
                            alertDialogBuilder.setCustomTitle(titleText);

                            alertDialogBuilder.setMessage("Want to remove " + item.getFirstName() + "'s account ?");
                            alertDialogBuilder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Helper.resetConfigValuesAndClearData(userId);
                                    updateAccountsList(null);
                                }
                            });
                            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            if (!Helper.isFinishingOrIsDestroyed(MyCoursesActivity.this)) {
                                alertDialog.show();
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MyCoursesActivity.this.getResources().getColor(R.color.ColorPrimary));
                            }
                        }
                    }
                } else {
                    Helper.showAlertMessage(MyCoursesActivity.this, "Remove Account", "You can't remove current user.", "OK");
                    return false;
                }
            } catch (Exception e) {
                LogWriter.err(e);
            }
            return true;
        }
    }


    private void swapAccountAndReload(String userId) {
//        if (snackbar != null && snackbar.isShown()) {
//            snackbar.dismiss();
//        }

        //Log.d(TAG, "swapAccount : " + userId);
        //startProgressDialog();  //@Chirag : For remove second progress bar when switching the user | 10/08/2018

        Requester.getInstance().cancelPendingRequests();
        Requester.getInstance().cancelPendingRequests(MyCourses.TAG);

        canSendJoinRequest = Flinnt.FALSE;
        updateDrawerItem(null);
        String currentUserId;
        if (userId != null) {
            currentUserId = userId;
        } else {
            currentUserId = Config.getStringValue(Config.USER_ID);
        }
        if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_BCOURSE_STATS + currentUserId).equals("1")) {
            mTooltipBrowseCourseLinear.setVisibility(View.GONE);
        } else {
            mTooltipBrowseCourseLinear.setVisibility(View.VISIBLE);
        }

        if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_MACC_STATS + currentUserId).equals("1")) {
            mTooltipMultipleAccLinear.setVisibility(View.GONE);
        } else {
            mTooltipMultipleAccLinear.setVisibility(View.VISIBLE);
        }

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("swapAccountAndReload :: userId : " + userId);

        if (null != userId) {
            Helper.setCurrentUserConfig(userId);
        }

        mMyCourses = null;
        updateAccountsList(null);
        updateProfileData();
        sendAllRequests();

        canShowFab = false;
        dobDay = 0;
        dobMonth = 0;
        dobYear = 0;
        myCoursesFragment = null;
        browseCoursesFragment = null;
        coursesViewPagerAdapter = new CoursesViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(coursesViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //@Nikhil 2062018
        //pagerContainer.setVisibility(View.GONE);
        setMarqueeRecyclerView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        try {
            searchItem = menu.findItem(R.id.action_search);
            settingItem = menu.findItem(R.id.action_add_banner);
            scanQRCodeItem = menu.findItem(R.id.action_scan_qr_code);
            notificationItem = menu.findItem(R.id.action_notificationlist);

            if (canChangeBanner == Flinnt.TRUE) {
                settingItem.setVisible(true);
            } else {
                settingItem.setVisible(false);
            }
            scanQRCodeItem.setVisible(true);
            notificationItem.setVisible(true);

            if (NotificationInterface.getInstance().getUnreadNotification() > 0) {
                notificationItem.setIcon(R.drawable.notification_red_toolbar);
            } else {
                notificationItem.setIcon(R.drawable.notification_toolbar);
            }

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) searchItem.getActionView();
            if (searchItem != null) {

                searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search..." + "</font>"));

                searchView.setOnQueryTextListener(new OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (Helper.isConnected()) {
                            doSearch(query, true);
                        } else {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        }
                        searchView.clearFocus();
                        Helper.hideKeyboardFromWindow(MyCoursesActivity.this);
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
                searchView.setOnCloseListener(new OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("onClose searchView");

                        doSearch("", false);

                        return false;
                    }
                });
            }
            if (searchView != null) {
                try {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

                    final EditText searchTextView = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                    try {
                        Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                        mCursorDrawableRes.setAccessible(true);
                        mCursorDrawableRes.set(searchTextView, R.drawable.cursor_color); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    LogWriter.err(e);
                }

            }

        } catch (Exception e) {
            LogWriter.err(e);
        }


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (canChangeBanner == Flinnt.TRUE) {
            menu.findItem(R.id.action_add_banner).setVisible(true);
        } else {
            menu.findItem(R.id.action_add_banner).setVisible(false);
        }
        menu.findItem(R.id.action_scan_qr_code).setVisible(true);
        menu.findItem(R.id.action_notificationlist).setVisible(true);
        return true;
    }

    public interface AppBarLayoutSwitchListener {
        public void onOffsetChanged(int i);
    }

    public interface OnSearchListener {
        public void onSearch(String query, Boolean isSubmit);
    }

    public void switchToBrowseCourses() {
        mViewPager.setCurrentItem(2);
    }

    /**
     * Logs user out
     *//*
    public void logoutFromAccouunt() {
        try {
            Helper.resetConfigValuesAndClearData(null);
            //Common comm = new Common(MyCoursesActivity.this);
            //comm.Logout(MyCoursesActivity.this, "", comm);
            // not in use now
            userIdList = UserInterface.getInstance().getUserIdList();
            if (userIdList.size() > 0) {
                for (String userId : userIdList) {
                    swapAccountAndReload(userId); // Pass userId you want to switch to when one user logs out. Here, switching to first user in database
                    break;
                }
            } else {
                startActivity(new Intent(MyCoursesActivity.this, LoginActivity.class));
                finish();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        try {
            int id = item.getItemId();
            if (id == R.id.action_add_banner) {
                Intent bannerAdd = new Intent(MyCoursesActivity.this, AddBannerActivity.class);
                bannerAdd.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_BANNER_ADD);
                if (!bannerPath.isEmpty() && bannerList.size() > 0) {
                    bannerAdd.putExtra(MenuBannerResponse.BANNERS_PATH_KEY, bannerPath);
                    bannerAdd.putExtra(MenuBannerResponse.BANNERS_KEY, accountBanner);
                }
                //startActivity(bannerAdd);
                startActivityForResult(bannerAdd, CHANGE_BANNER_SUCCESSFULL_CALL_BACK);
                return true;
            }
            if (id == R.id.action_scan_qr_code) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    if (AskPermition.getInstance(MyCoursesActivity.this).isPermitted()) {
                        startActivity(new Intent(this, QRCodeScannerActivity.class));
                    }
                }
            }
            if (id == R.id.action_notificationlist) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    startActivity(new Intent(this, NotificationListActivity.class));
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Display dialog to join course by course code
     */
    private void joinCourseDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
            // set view
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.join_course, null);
            alertDialogBuilder.setView(dialogView);

            //alertDialogBuilder.setTitle("Change Password");
            TextView title = new TextView(this);
            // You Can Customise your Title here
            title.setText("Join a Course");
            title.setPadding(40, 40, 40, 40);
            title.setGravity(Gravity.CENTER_VERTICAL);
            title.setTextColor(getResources().getColor(R.color.ColorPrimary));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(title);


            alertDialogBuilder.setCancelable(false);

            // set the custom dialog components - text, image and button
            final EditText courseCodeTxt = (EditText) dialogView.findViewById(R.id.course_code_text);

            courseCodeTxt.setTypeface(Typeface.DEFAULT);
            //courseCodeTxt.getBackground().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.SRC_ATOP);

            alertDialogBuilder.setPositiveButton("JOIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing here because we override this button later to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                }
            });
            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Same as above button
                }
            });

            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseCodeStr = courseCodeTxt.getText().toString();

                    if (TextUtils.isEmpty(courseCodeStr) || courseCodeStr.length() != 6) {
                        Helper.showAlertMessage(MyCoursesActivity.this, "Join Course", getResources().getString(R.string.course_code_invalide), "CLOSE");
                    } else {
                        if (Helper.isConnected()) {

                            new JoinCourse(mHandler, courseCodeStr).sendJoinCourseRequest();
                            startProgressDialog();
                        } else {
                            Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                        }
                    }
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    /**
     * Opens course select activity
     *
     * @param postType
     */
    private void sendSelectCourseRequest(int postType) {
        try {
            Intent selectCourse = new Intent(MyCoursesActivity.this, SelectCourseActivity.class);
            selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, postType);
            selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_ADD);
            selectCourse.putExtra("class_name", MyCoursesActivity.class.getSimpleName());
            startActivity(selectCourse);
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        //Log.d(TAG, "Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {

                    case COURSE_UNSUBSCRIBED_CALL_BACK:
                        boolean isCourseUpdate = false;
                        boolean isCourseDelete = false;
                        boolean isBrowseCourse = false;
                        isCourseUpdate = data.getBooleanExtra("isCourseUpdate", false);
                        isCourseDelete = data.getBooleanExtra("isCourseDelete", false);
                        isBrowseCourse = data.getBooleanExtra("isBrowseCours", false);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("coursePositionOnItemClick : " + coursePositionOnItemClick + " , isCourseUpdate : " + isCourseUpdate + " , isCourseDelete : " + isCourseDelete + " , isBrowseCourse : " + isBrowseCourse);

                        if (coursePositionOnItemClick != Flinnt.INVALID) {
                            if (isCourseUpdate) {
                                //Update post from mContentsAdapter
                                Course updateCourse = myCoursesFragment.mMyCoursesAdapter.getItem(coursePositionOnItemClick);
                                String ucName = data.getStringExtra(Course.COURSE_NAME_KEY);
                                String ucPicture = data.getStringExtra(Course.COURSE_PICTURE_KEY);
                                updateCourse.setCourseName(ucName);
                                updateCourse.setCoursePicture(ucPicture);

                                myCoursesFragment.mMyCoursesAdapter.update(coursePositionOnItemClick, updateCourse);
                                CourseInterface.getInstance().updateCourse(updateCourse);

                            } else if (isCourseDelete) {
                                //Delete post from mContentsAdapter
                                myCoursesFragment.mMyCoursesAdapter.remove(myCoursesFragment.mMyCoursesAdapter.getItem(coursePositionOnItemClick));
                            }
                            coursePositionOnItemClick = Flinnt.INVALID;
                            if (myCoursesFragment.mMyCoursesAdapter.getItemCount() < 1) {
                                myCoursesFragment.mEmptyTextView.setVisibility(View.VISIBLE);
                                myCoursesFragment.mEmptyButton.setVisibility(View.VISIBLE);
                            } else {
                                myCoursesFragment.mEmptyTextView.setVisibility(View.GONE);
                                myCoursesFragment.mEmptyButton.setVisibility(View.GONE);
                            }
                            if (isBrowseCourse) {
                                mViewPager.setCurrentItem(2);
                            }
                        }


                        break;

                    case AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK:
                        updateAccountsList(null);
                        updateProfileData();
                        break;

                    case CHANGE_BANNER_SUCCESSFULL_CALL_BACK:
                        new MenuBanner(mHandler, null).sendMenuBannerRequest();
                        //					super.onResume();
                        break;

                    case MENU_STATISTICS_CALL_BACK:
                        if (data.hasExtra(AddPostResponse.COURSE_KEY)) {
                            ArrayList<Course> acceptedCoursesList = data.getParcelableArrayListExtra(AddPostResponse.COURSE_KEY);
                            for (int i = 0; i < acceptedCoursesList.size(); i++) {
                                Course acceptedCourse = acceptedCoursesList.get(i);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("accepted Course " + i + " : " + acceptedCourse.toString());
                                if (!CourseInterface.getInstance().isCourseExist(acceptedCourse.getCourseID())) {
                                    myCoursesFragment.mMyCoursesAdapter.addItem(acceptedCourse);
                                    CourseInterface.getInstance().insertCourse(acceptedCourse);
                                    mOfflineCouseIDs.add(acceptedCourse.getCourseID());
                                }
                                if (myCoursesFragment.mMyCoursesAdapter.getItemCount() > 0) {
                                    myCoursesFragment.mEmptyTextView.setVisibility(View.GONE);
                                    myCoursesFragment.mEmptyButton.setVisibility(View.GONE);
                                }
                            }
                        }

                        new MenuStatistics(mHandler, "").sendMenuStatisticsRequest();

                        break;

                    case ADD_COURSE_CALL_BACK:
                        Course newCourse = (Course) data.getParcelableExtra(AddPostResponse.COURSE_KEY);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("New Course : " + newCourse.toString());
                        myCoursesFragment.mMyCoursesAdapter.addItem(newCourse);
                        CourseInterface.getInstance().insertCourse(newCourse);
                        mOfflineCouseIDs.add(newCourse.getCourseID());
                        break;

                    case JOIN_COMMUNITY_CALLBACK:
                        ArrayList<Course> joinedCourseList = data.getParcelableArrayListExtra(JoinCourseResponse.JOINED_KEY);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("joinedCourseListSize :: " + joinedCourseList.size() + ", joinedCourseList : " + joinedCourseList.toString());
                        for (Course joinedCourse : joinedCourseList) {
                            if (!CourseInterface.getInstance().isCourseExist(joinedCourse.getCourseID())) {
                                CourseInterface.getInstance().insertCourse(joinedCourse);
                                myCoursesFragment.mMyCoursesAdapter.addItem(joinedCourse);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("mOfflineCouseIDs :: " + mOfflineCouseIDs.toString());
                                mOfflineCouseIDs.add(joinedCourse.getCourseID());
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("mOfflineCouseIDs :: " + mOfflineCouseIDs.toString());
                            }
                        }
                        break;

                    case BROWSE_COURSE_SUBSCRIBE_CALLBACK:
                        HashMap<Course, Boolean> addedAndRemovedCourses = (HashMap<Course, Boolean>) data.getSerializableExtra(JoinCourseResponse.JOINED_KEY);
                        boolean isAnyCourseAddedOrRemoved = false;
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("BROWSE_COURSE_SUBSCRIBE_CALLBACK :: " + addedAndRemovedCourses.toString());
                        //Log.d("Broo", "MyCoursesActivity : " + addedAndRemovedCourses.toString());
                        for (Course course : addedAndRemovedCourses.keySet()) {
                            if (addedAndRemovedCourses.get(course)) { // added courses
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("joinedCourse :: " + course.toString());
                                //Log.d("Broo", "MyCoursesActivity : if true.." + addedAndRemovedCourses.toString());
                                if (!CourseInterface.getInstance().isCourseExist(course.getCourseID())) {
                                    CourseInterface.getInstance().insertCourse(course);
                                    myCoursesFragment.mMyCoursesAdapter.addItem(course);
                                    // browseCoursesFragment.mBrowseCoursesAdapter.remove(course.getCourseID());
                                }
                            } else { // removed courses
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("removedCourse :: " + course.toString());
                                //Log.d("Broo", "MyCoursesActivity : if else.." + addedAndRemovedCourses.toString());
                                if (CourseInterface.getInstance().isCourseExist(course.getCourseID())) {
                                    CourseInterface.getInstance().deleteCourse(course.getCourseID());
                                }
                                myCoursesFragment.mMyCoursesAdapter.remove(course.getCourseID());
                            }
                            isAnyCourseAddedOrRemoved = true;
                        }

                        if (isAnyCourseAddedOrRemoved && mViewPager.getCurrentItem() == 2) {
                            mViewPager.setCurrentItem(1); // swapping to My Courses Tab from Browse Course Tab
                        }
                        break;

                    case LOGIN_CALLBACK: // New account successful login
                        swapAccountAndReload(null);
                        break;
                    case WISHLIST_BROWSE_COURSE_CALLBACK: // New account successful logi
                        try {
                            if (data != null) {
                                String previouseScreen = data.getStringExtra("ComeFrom");
                                if (previouseScreen.equalsIgnoreCase("WishlistActivity")) {
                                    mViewPager.setCurrentItem(2);
                                } else if (previouseScreen.equalsIgnoreCase("WishlistActivityResult")) {
                                    mViewPager.setCurrentItem(1);
                                }
                            }
                        } catch (Exception e) {

                        }
                        break;
                    //@Chirag:20/08/2018******start
                    case FILECHOOSER_RESULTCODE:

                        //Log.d(TAG, "file chooser result : ");
                        /*if (null == mUploadMessage) //
                            return;
                        // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
                        // Use RESULT_OK only if you're implementing WebView inside an Activity
                        Uri result = data == null || resultCode != MyCoursesActivity.RESULT_OK ? null : data.getData();
                        mUploadMessage.onReceiveValue(result);
                        mUploadMessage = null;*/

                        //***********
                        if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                            super.onActivityResult(requestCode, resultCode, data);
                            return;
                        }
                        if (requestCode == FILECHOOSER_RESULTCODE) {
                            if (null == this.mUploadMessage) {
                                return;
                            }
                            Uri result = null;
                            try {
                                if (resultCode != RESULT_OK) {
                                    result = null;
                                } else {
                                    // retrieve from the private variable if the intent is null
                                    result = data == null ? mCapturedImageURI : data.getData();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "activity :" + e,Toast.LENGTH_LONG).show();
                            }
                            mUploadMessage.onReceiveValue(result);
                            mUploadMessage = null;
                        }
                        //Log.d(TAG, "file chooser result : end of else");

                        break;
                    case REQUEST_SELECT_FILE:
                        //Log.d(TAG, "file chooser result : request_select_file");
                        if (uploadMessage == null)
                            return;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        }
                        uploadMessage = null;
                        //Log.d(TAG, "file chooser result : last at request_select_file");
                        //********end for chirag
                        break;

                    case MyCoursesActivity.BROWSE_STORE_CALLBACK:
                        TabLayout.Tab tab = mTabLayout.getTabAt(2);
                        tab.select();
                    default:
                        break;
                }

                updateFabVisibility();

            } else {
                if (requestCode == LOGIN_CALLBACK) {
                    startActivity(new Intent(MyCoursesActivity.this, LoginActivity.class));
                    finish();
                }
            }
            invalidateOptionsMenu();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Set Intent after check ACCOUNT_VERIFIED and ACCOUNT_AUTH_MODE
     */
    private void setMainIntentIfNeed(LoginResponse loginResponse) {
        if (loginResponse.getAccVerified().equals(Flinnt.DISABLED)) {
            if (loginResponse.getAccVerified().equals("email")) {
                startActivity(new Intent(MyCoursesActivity.this, VerifyEmailActivity.class));
            } else {
                startActivity(new Intent(MyCoursesActivity.this, VerifyMobileActivity.class));
            }
            finish();
        }
    }

    /**
     * Shows delete confirmation dialog
     *
     * @param message dialog confirmation message
     */
    public void showUserDeletedDialog(String message) {

        try {
            if (!isDeleteDialogShowing) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
                //alertDialogBuilder.setTitle("Delete Post");
                TextView titleText = new TextView(MyCoursesActivity.this);
                // You Can Customise your Title here
                titleText.setText("Error");
                titleText.setPadding(40, 40, 40, 0);
                titleText.setGravity(Gravity.CENTER_VERTICAL);
                titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
                titleText.setTextSize(20);
                titleText.setTypeface(Typeface.DEFAULT_BOLD);
                alertDialogBuilder.setCustomTitle(titleText);
                // set dialog message
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            ArrayList<String> userIdList = UserInterface.getInstance().getUserIdList();
                            if (userIdList.size() > 1) {
                                Helper.resetConfigValuesAndClearData(Config.getStringValue(Config.USER_ID));
                                for (String userId : UserInterface.getInstance().getUserIdList()) {
                                    swapAccountAndReload(userId); // Pass userId you want to switch to when one user logs out. Here, switching to first user in database
                                    break;
                                }
                            } else {
                                Helper.resetConfigValuesAndClearData(null);
                                startActivity(new Intent(MyCoursesActivity.this, LoginActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        isDeleteDialogShowing = false;
                    }
                });
                alertDialogBuilder.setCancelable(false);
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                isDeleteDialogShowing = true;

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    /**
     * Displays the rate/review dialog
     */
    private void showRateReviewDialog(String rating, String reviewText) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
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

//        AlertDialog.Builder builder = new AlertDialog.Builder(MyCoursesActivity.this)
//                .setTitle("Review by " + Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME))
//                .setPositiveButton("SUBMIT", null)
//                .setView(R.layout.rating_dialog_design);

        final AlertDialog inviteDialog = alertDialogBuilder.create();
        inviteDialog.show();

        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) inviteDialog.findViewById(R.id.rb_rate);
        final EditText etReview = (EditText) inviteDialog.findViewById(R.id.et_review);
        Button btnSubmit = inviteDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        ratingBar.setRating(Float.parseFloat(rating));
        etReview.setText(reviewText);
        etReview.setSelection(etReview.getText().length());

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(MyCoursesActivity.this);
                } else {
                    if (ratingBar.getRating() == 0) {
                        Helper.showToast(getString(R.string.give_your_rating),Toast.LENGTH_LONG);
                    } else {
                        if (RATING_COURSE_ID != null) {
                            CourseReviewWrite mCourseReviewWrite = new CourseReviewWrite(mHandler, RATING_COURSE_ID);
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
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        //Log.d("Myy", "startProgressDialog()");
        if (!Helper.isFinishingOrIsDestroyed(MyCoursesActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(MyCoursesActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {

        //Log.d("Myy", "stopProgressDialog()");
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
     * Checks if a newer version of app is available or not
     *
     * @param appUpdateResponse app update response
     * @return true if newer version is available, false otherwise
     */
    private boolean needToShowDialog(AppUpdateResponse appUpdateResponse) {
        boolean needToShow = false;
        if (appUpdateResponse.getUpdate() == Flinnt.TRUE) {
            needToShow = true;
        }
        return needToShow;
    }

    /**
     * create the app update dialog
     *
     * @param appUpdateResponse app update response
     */
    protected void setupAppUpdateDialog(AppUpdateResponse appUpdateResponse) {

        if (needToShowDialog(appUpdateResponse)) {
            boolean isHardUpdate = false;
            boolean isShowGracePerios = false;

            if (appUpdateResponse.getHardUpdate() == Flinnt.TRUE) {
                isHardUpdate = true;
            }
            if (appUpdateResponse.getGracePeriod() == Flinnt.TRUE) {
                isShowGracePerios = true;
            }

            showAppUpdateDialog(isHardUpdate, isShowGracePerios, appUpdateResponse);
        }

    }


    private void callFilterData(){


    }



    private void showAppUpdateDialog(final boolean isHardUpdate,final boolean isShowGracePerios, AppUpdateResponse appUpdateResponse) {
        try {
            if (LogWriter.isValidLevel(Log.DEBUG))
                LogWriter.write("isHardUpdate : " + isHardUpdate + " , isShowGracePerios : " + isShowGracePerios);
            String titleStr = "Update";
            String messageStr = appUpdateResponse.getMessage();

            String positiveBtnStr = "Update";

            String negativeBtnStr = "Skip";
            if (isHardUpdate && !isShowGracePerios) {
                negativeBtnStr = "Exit";
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
            //alertDialogBuilder.setTitle("Delete Post");
            TextView titleText = new TextView(MyCoursesActivity.this);
            // You Can Customise your Title here
            titleText.setText(titleStr);
            titleText.setPadding(40, 40, 40, 0);
            titleText.setGravity(Gravity.CENTER_VERTICAL);
            titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
            titleText.setTextSize(20);
            titleText.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(titleText);
            // set dialog message
            alertDialogBuilder.setMessage(messageStr);
            alertDialogBuilder.setPositiveButton(positiveBtnStr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Go to playstore and start download");
                    openAppInPlaystore();
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnStr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (isHardUpdate && !isShowGracePerios) {
                        finish();
                    } else {
                        dialog.dismiss();
                    }
                }
            });

            alertDialogBuilder.setCancelable(false);
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    /**
     * Open the apps in playstore
     */
    private void openAppInPlaystore() {
        //getPackageName(); // getPackageName() from Context or Activity object
        final String appPackageName = "com.edu.flinnt";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName)));
            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(Config.getStringValue(Config.USER_ID));
        Crashlytics.setUserEmail(Config.getStringValue(Config.USER_LOGIN));
        Crashlytics.setUserName(Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));
    }


    private void genderOrDobValidation(LoginResponse mLoginResponse) {
        if (/*mLoginResponse.getUserGender() != null && */mLoginResponse.getUserBirthDay() != null) {
            if (/*mLoginResponse.getUserGender().equals("") || */mLoginResponse.getUserBirthDay().equals("0")) {
                if (!Config.getGenderOrDobSnakbar(Config.FLINNT_SNACKBAR_GENDER_DOB + Config.getStringValue(Config.USER_ID)).trim().equals("1")) {
//                   displaySnackBar();
                    profileUpdateDialog();
                }
            }
        }
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    // Permision to get location
    public boolean isLocationFetchingAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(MyCoursesActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    private void fetchLocation() {
        if (!isLocationFetchingAllowed()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Config.getStringValue(Config.FCM_TOKEN) != null && !TextUtils.isEmpty(Config.getStringValue(Config.FCM_TOKEN))) {
                    Intent intent = new Intent(this, LocationService.class);
                    startService(intent);
                }
            }
        }
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dobYear = year;
            dobMonth = (monthOfYear + 1);
            dobDay = dayOfMonth;

            SimpleDateFormat sdf = new SimpleDateFormat(dobFormat, Locale.US);
            mDateTxt.setText(sdf.format(mCalendar.getTime()));
        }
    };


    /**
     * Display dialog for profile update
     */
    private void profileUpdateDialog() {
        try {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
            // set view
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_profile_update, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(true);

            // create alert dialog
            updateDOBDialog = alertDialogBuilder.create();
            updateDOBDialog.show();

            TextView drawerText = (TextView) dialogView.findViewById(R.id.profile_name_text);
            drawerText.setText(Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));

            TextView drawerEmail = (TextView) dialogView.findViewById(R.id.profile_email_text);
            drawerEmail.setText(Config.getStringValue(Config.USER_LOGIN).toLowerCase());

            TextView updateBirthdayTxt = (TextView) dialogView.findViewById(R.id.update_birthday_text);
            updateBirthdayTxt.setText("Hey " + Config.getStringValue(Config.FIRST_NAME) + "!\n" + getResources().getString(R.string.update_birthday));

            mDateTxt = (TextView) dialogView.findViewById(R.id.date_txt);
            mDateTxt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(MyCoursesActivity.this, datePicker,
                            mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            Button btnCancel = (Button) dialogView.findViewById(R.id.profile_cancel_btn);
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.setGenderOrDobSnakbar(Config.FLINNT_SNACKBAR_GENDER_DOB + Config.getStringValue(Config.USER_ID), "1");
                    updateDOBDialog.dismiss();
//                    Config.setGenderOrDobSnakbar(Config.FLINNT_SNACKBAR_GENDER_DOB + Config.getStringValue(Config.USER_ID), getCurrentDate());
                }
            });

            Button btnUpdate = (Button) dialogView.findViewById(R.id.profile_update_btn);
            btnUpdate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dobYear > 0) {
                        startProgressDialog();
                        new UpdateBirthdate(mHandler, dobDay, dobMonth, dobYear).sendUpdateBirthdateRequest();
                    } else {
                        Helper.showAlertMessage(MyCoursesActivity.this, "Error", getResources().getString(R.string.select_birthday), "CLOSE");
                    }

                }
            });

            SelectableRoundedCourseImageView profileImg = (SelectableRoundedCourseImageView) dialogView.findViewById(R.id.profile_round_image);
            profileImg.setDefaultImageResId(R.drawable.default_user_profile_image);
            String profileUrl = profileImageUrl + Flinnt.PROFILE_LARGE + File.separator + profileImageName;
            profileImg.setImageUrl(profileUrl, mImageLoader);

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    class RemindTask extends TimerTask {

        private ViewPager mViewPager;

        public RemindTask(ViewPager mViewPager) {
            this.mViewPager = mViewPager;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (promoCourseTimer != null) {
            promoCourseTimer.purge();
            promoCourseTimer.cancel();
            promoCourseTimer = null;
        }
    }

}