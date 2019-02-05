package com.edu.flinnt.gui.store;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.AuthorListAdapter;
import com.edu.flinnt.adapter.store.ProductDetailListAdapter;
import com.edu.flinnt.core.store.CourseDescriptionNew;
import com.edu.flinnt.core.store.BrowseCoursesNew;
import com.edu.flinnt.core.BuyerList;
import com.edu.flinnt.core.CheckoutCourse;
import com.edu.flinnt.core.ContentsList;
import com.edu.flinnt.core.CourseRequest;
import com.edu.flinnt.core.CourseReviewWrite;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.store.CartItems;
import com.edu.flinnt.gui.AddCommunicationActivity;
import com.edu.flinnt.gui.BuyerListActivity;
import com.edu.flinnt.gui.ContentsDisplayAdapter;
import com.edu.flinnt.gui.CourseBuyerAdapter;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.ProfileActivity;
import com.edu.flinnt.models.store.RelatedBookResponse;
import com.edu.flinnt.models.store.RelatedVendorsResponse;
import com.edu.flinnt.models.store.StoreBookDetailResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BuyerListRequest;
import com.edu.flinnt.protocol.BuyerListResponse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseSendResponse;
import com.edu.flinnt.protocol.CourseViewResponse;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.protocol.UnsubscribeCourseResponse;
import com.edu.flinnt.protocol.UserReview;
import com.edu.flinnt.protocol.WishListResponse;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.CustomNestedScrollView;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyTagHandler;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmList;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.edu.flinnt.R.id.review_base_linear;



public class BrowseCourseDetailActivityNew extends AppCompatActivity implements View.OnClickListener, AnimationListener, YouTubePlayer.OnInitializedListener {

    Toolbar toolbar;
    ImageLoader mImageLoader;
    TextView mTotalRattingsTxt, mTotalContentsTxt, mCourseNameTxt, mInstituteNameTxt, mReadMoreTxt, mViewMoreTxt, mAllReviewsTxt, mDatetimeTxt, mAddressTxt, mMoreCoursesFromInstituteTxt, mBuyTxt, mUnableToJoinTxt, mSpaceTxt, mCourseDescriptionTxt;
    Button mSubscriptionBtn;
    LinearLayout mMainContentLayout, mContainsLinear, mRatingLinear, mReviewCourseLinear, mReviewsBaseLinear,
            mJoinedCoursesLinear, mInstituteCoursesLinear, mDateTimeAddressLinear, mDescriptionLinear, mBottomSheetLinear, mReadMoreLinear;
    AppCompatRatingBar mCourseRatingBar;
    NetworkImageView mCoursePictureImg;
    private ProgressDialog mProgressDialog;
    Handler mHandler = null;
    private String couserHash, courseId, courseName, baseUserPictureUrl, shareUrl,standardId = "";
    private RecyclerView mJoinedCoursesRecycler, mInstituteCoursesRecycler;
    ArrayList<BrowsableCourse> mJoinedCoursesList, mInstituteCourseList;
    ArrayList<RelatedBookResponse.Datum> mRelatedBookList;
    ArrayList<RelatedVendorsResponse.Datum> mRelatedVendorList;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private SuggestedCoursesAdapterNew mJoinedCoursesAdapter, mInstituteCoursesAdapter;
    private HashMap<Course, Boolean> addedAndRemovedCourses = new HashMap<>();  // true if added course, false if removed course
   // private BrowsableCourse mBrowsableCourse;
    private StoreBookDetailResponse.Data mBrowsableCourse2;
    private Course joinedCourse;
    private CustomNestedScrollView mCustomNestedScrollView;
    private View mDeviderView;
    private RecyclerView mBuyerRecycler;
    private CourseBuyerAdapter courseBuyerAdapter = null;
    private Animation animFadein, animFadeOut; // Animation
    public static String CHECKOUT_RESPONSE = "CheckoutResponse";
    private int lineCount;
    private TextView mOldPriceTxt, mFinalPriceTxt, mFPriceTxt, mOPriceTxt, mTempTxt;
    public static final int CHECKOUT_CALLBACK = 121;
    private boolean isCourseDeletedDialogShowing;
    private boolean isDeleteCourse = false;
    private String coursePictureUrl = "";
    private int isFromNotification = Flinnt.FALSE;
    //String userId = ""; //@Chirag 13/08/2018
    private String userId;  //@Chirag 13/08/2018   //for issue 7745
    public static final String COURSE_PICTURE_URL = "coursePictureUrl";
    private FrameLayout mYoutubeFrame;
    private YouTubePlayerFragment postYouTubePlayerFragment;
    private String postYoutubeVedioID;
    private ImageButton mMediaOpenImgbtn;
    private ImageView mMediaThumnailImg;
    private YouTubePlayer postYouTubePlayer;
    private boolean postFullScreen;
    private RelativeLayout mPostMediaRelative;
    private RecyclerView mContentRecycle,mAuthorRvList,mRVProductAttributeList;
    private ContentsDisplayAdapter mContentsDisplayAdapter;
    private RealmList<Sections> mSectionList = new RealmList<Sections>();
    private ContentsList mContentsList;
    private ContentsResponse mContentsResponse;
    private BuyerListResponse mBuyerListResponse;
    public static String CONTENT_ONLY = "ContentOnly";
    public static String BROWSABLE_COURSE = "browsablecourse";
    private WishListResponse mWishListResponse = null;
    private boolean isWishlist = false;
    private Button mShareAnimationBtn;
    private LinearLayout mUsersCourseLinear;
    private BuyerList mBuyerList;
    private BuyerListRequest mBuyerListRequest;
    private View mRattingsView;
    private int seekTime = 0;
    public static final int BROWSE_COURSE_DETAILS_CALLBACK = 125;
    private View mContentLineTxt;
    private TextView mRefunPolicyTxt;
    public static final int REFUND_CALLBACK = 521;
    private RelatedBookResponse relatedBookResponse;
    private RelatedVendorsResponse relatedVendorsResponse;
    private AuthorListAdapter authorListAdapter;
    private ProductDetailListAdapter productDetailListAdapter;
    private Button btnAddCart;
    public static StoreBookDetailResponse.Data browsableCourse;
    private TextView textCartItemCount;
    private int mCartItemCount;
    private String cartCount;
    private FrameLayout fl_badge_containers;
    private TextView tv_title;
    private BrowseCoursesNew mBrowseCourses;
    private StoreModelResponse storeModelResponse;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.browse_course_detail_activity_new);

        toolbar = (Toolbar) findViewById(R.id.profile_main_toolbar);
        tv_title = (TextView)findViewById(R.id.tv_title);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findAndInitializeViews();

        final String baseCoursePictureUrl, coursePictureName;
        baseCoursePictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL) + Flinnt.COURSE_LARGE + File.separator;
        baseUserPictureUrl = Config.getStringValue(Config.USER_PICTURE_URL) + Flinnt.PROFILE_MEDIUM + File.separator;

        Bundle bundle = getIntent().getExtras();

        if (null != bundle) {
            if (bundle.containsKey("couserHash"))
                couserHash = bundle.getString("couserHash");

            if (bundle.containsKey(BrowsableCourse.ID_KEY))
                courseId = bundle.getString(BrowsableCourse.ID_KEY);

            if (bundle.containsKey(BrowsableCourse.PICTURE_KEY)) {
                coursePictureName = bundle.getString(BrowsableCourse.PICTURE_KEY);
//                coursePictureUrl = baseCoursePictureUrl + coursePictureName;
//                mCoursePictureImg.setImageUrl(coursePictureUrl, mImageLoader);
            }

            if (bundle.containsKey(Flinnt.STANDARD_ID))
                standardId = bundle.getString(Flinnt.STANDARD_ID);

            if (bundle.containsKey(BrowsableCourse.NAME_KEY))
                courseName = bundle.getString(BrowsableCourse.NAME_KEY);
                mCourseNameTxt.setText(courseName);

            if (bundle.containsKey(BrowsableCourse.INSTITUTE_NAME_KEY))
                mInstituteNameTxt.setText(bundle.getString(BrowsableCourse.INSTITUTE_NAME_KEY));

            if (bundle.containsKey(BrowsableCourse.RATINGS_KEY))
                mCourseRatingBar.setRating(Float.parseFloat(bundle.getString(BrowsableCourse.RATINGS_KEY)));

            if (bundle.containsKey(JoinCourseResponse.JOINED_KEY))
                addedAndRemovedCourses = (HashMap<Course, Boolean>) bundle.getSerializable(JoinCourseResponse.JOINED_KEY);

            if (bundle.containsKey(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE))
                isFromNotification = bundle.getInt(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE);

            if(bundle.containsKey(BrowsableCourse.CART_COUNT)){
                cartCount = bundle.getString(BrowsableCourse.CART_COUNT);
                mCartItemCount = Integer.parseInt(cartCount);
                setupBadge();
            }

            if (isFromNotification > Flinnt.FALSE && bundle.containsKey(Config.USER_ID))
                userId = bundle.getString(Config.USER_ID);

            if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                Helper.setCurrentUserConfig(userId);
            }

        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            try{
                                if(msg.obj != null) {
                                    LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());
                                }
                            }catch (Exception ex){
                              ex.printStackTrace();
                            }

                        if (msg.obj instanceof StoreBookDetailResponse) {
                            StoreBookDetailResponse mCourseViewResponse = (StoreBookDetailResponse) msg.obj;
                            mBrowsableCourse2 = mCourseViewResponse.getData();
                            //courseId = String.valueOf(mBrowsableCourse2.getBookId());
//                            refreshView();
//                            if (mBrowsableCourse.getIsPublic().equals("0")) {
//                                mReviewsBaseLinear.setVisibility(View.GONE);
//                                mDescriptionLinear.setVisibility(View.GONE);
//                            } else {
//                                mReviewsBaseLinear.setVisibility(View.VISIBLE);
//                            }
                            mReviewsBaseLinear.setVisibility(View.GONE);
                            showHideReviewSection();
                            tv_title.setText(mBrowsableCourse2.getBookName());
                            //getSupportActionBar().setTitle(mBrowsableCourse2.getBookName());
                            updateCourseDetailsNew(mBrowsableCourse2);
//                            if (!TextUtils.isEmpty(couserHash) || coursePictureUrl.equalsIgnoreCase("")) {
//                                CourseReviews mCourseReviews = new CourseReviews(mHandler, courseId, 2); // needs two reviews only
//                                mCourseReviews.sendCourseReviewListRequest();
//                                mCoursePictureImg.setImageUrl(baseCoursePictureUrl + mBrowsableCourse.getPicture(), mImageLoader);
//                                coursePictureUrl = baseCoursePictureUrl + mBrowsableCourse.getPicture();
//                            }
                            // Requests for suggested courses list
                            new SuggestedBooks(mHandler,courseId,standardId).sendSuggestedBooksRequest();
//                            new SuggestedCourses(mHandler, courseId,SuggestedCourses.COURSES_FROM_INSTITUTE).sendSuggestedCoursesRequest();

                        }

                        if (msg.obj instanceof RelatedBookResponse) {
                            relatedBookResponse = new RelatedBookResponse();
                            relatedBookResponse =(RelatedBookResponse)msg.obj;
                            fillSuggestedCourses(relatedBookResponse);

                           // new SuggestedBooks(mHandler,courseId,standardId).sendSuggestedVendorsRequest();
                        }
                        if (msg.obj instanceof RelatedVendorsResponse) {
                            relatedVendorsResponse = new RelatedVendorsResponse();
                            relatedVendorsResponse =(RelatedVendorsResponse) msg.obj;
                           // fillSuggestedCourses(relatedBookResponse);
                        }

                        if(msg.arg1 == 1) {
                            startProgressDialog();

                            if(msg.obj instanceof  String){
                                String message = (String)msg.obj;
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getColor(R.color.white))
                                        .setAction("View Cart",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(BrowseCourseDetailActivityNew.this,ShoppingCartActivity.class));
                                            }
                                        }).show();
                            }


                            mBrowseCourses = new BrowseCoursesNew(mHandler);
                            mBrowseCourses.setSearchString("");
                            mBrowseCourses.sendBrowseCoursesRequest();
                        }
                        if(msg.obj instanceof StoreModelResponse){
                            stopProgressDialog();
                            btnAddCart.setEnabled(true);
                            btnAddCart.setAlpha(1);
                            storeModelResponse = (StoreModelResponse)msg.obj;
                            updateCartCount(storeModelResponse);

                        }


                        break;

                    case Flinnt.FAILURE:

                        btnAddCart.setEnabled(true);
                        btnAddCart.setAlpha(1);
                        // TODO: 13/6/16 handle error codes
                        try {
                            mSubscriptionBtn.setEnabled(true);
                            if (msg.obj instanceof CourseViewResponse) {
                                CourseViewResponse mCourseViewResponse = (CourseViewResponse) msg.obj;
                                String errorMessage = mCourseViewResponse.errorResponse.getMessage();

                                for (int i = 0; i < mCourseViewResponse.errorResponse.getErrorList().size(); i++) {
                                    com.edu.flinnt.protocol.Error error = mCourseViewResponse.errorResponse.getErrorList().get(i);
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_507 || error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        return;
                                    }
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        return;
                                    }
                                }
                                Helper.showAlertMessage(BrowseCourseDetailActivityNew.this, "Error", errorMessage, "Close");
                            }

                            if (msg.obj instanceof UnsubscribeCourseResponse) {
                                UnsubscribeCourseResponse mUnsubscribeCourseResponse = (UnsubscribeCourseResponse) msg.obj;
                                String errorMessage = mUnsubscribeCourseResponse.errorResponse.getMessage();

                                for (int i = 0; i < mUnsubscribeCourseResponse.errorResponse.getErrorList().size(); i++) {
                                    com.edu.flinnt.protocol.Error error = mUnsubscribeCourseResponse.errorResponse.getErrorList().get(i);
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_507 || error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        return;
                                    }
                                    if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                        showCourseDeletedDialog(errorMessage);
                                        return;
                                    }
                                }
                                Helper.showAlertMessage(BrowseCourseDetailActivityNew.this, "Error", errorMessage, "Close");
                            }
                            if (msg.obj instanceof JoinCourseResponse) {
                                JoinCourseResponse mJoinCourseResponse = (JoinCourseResponse) msg.obj;
                                Helper.showAlertMessage(BrowseCourseDetailActivityNew.this, "Join Course", mJoinCourseResponse.errorResponse.getMessage(), "Close");
                            }
                            if (msg.obj instanceof CheckoutResponse) {
                                CheckoutResponse mCheckoutResponse = (CheckoutResponse) msg.obj;
                                Helper.showAlertMessage(BrowseCourseDetailActivityNew.this, "Join Course", mCheckoutResponse.errorResponse.getMessage(), "Close");
                            }
                            if (msg.obj instanceof CourseSendResponse) {
                                CourseSendResponse mCourseSendResponse = (CourseSendResponse) msg.obj;
                                showSendRequestError("Send Request", mCourseSendResponse.errorResponse.getMessage());
                            }
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + msg.obj.toString());
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };

        CourseDescriptionNew mCourseDescription = new CourseDescriptionNew(mHandler, courseId, couserHash);
        mCourseDescription.sendCourseDescriptionRequest();
//        if (TextUtils.isEmpty(couserHash)) {
//            CourseReviews mCourseReviews = new CourseReviews(mHandler, courseId, 2); // needs two reviews only
//            mCourseReviews.sendCourseReviewListRequest();
//        }

        startProgressDialog();

        resetListsAndAdapters();

//        mJoinedCoursesAdapter.setOnItemClickListener(new SuggestedCoursesAdapterNew.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                startNewActivity(mJoinedCoursesAdapter.getItem(position));
//            }
//        });
//        mInstituteCoursesAdapter.setOnItemClickListener(new SuggestedCoursesAdapterNew.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                startNewActivity(mInstituteCoursesAdapter.getItem(position));
//            }
//        });


        refreshView();

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    btnAddCart.setEnabled(false);
                    btnAddCart.setAlpha(0.5f);
                    new CartItems(mHandler,Config.getStringValue(Config.USER_ID),String.valueOf(courseId),mBrowsableCourse2.getBookName(),String.valueOf(mBrowsableCourse2.getBookId()),String.valueOf(mBrowsableCourse2.getSalePrice()),"1","book").sendAddCartRequest();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });


        mCustomNestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                mCustomNestedScrollView.scrollTo(0, mMainContentLayout.getTop());
            }
        });

        mShareAnimationBtn = (Button) findViewById(R.id.show_btn);
        mShareAnimationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflowPrompt(v);
            }
        });
    }




    private void setupBadge() {
        try {

            mCartItemCount = BrowseCoursesFragmentNew.cart_count;
            if (textCartItemCount != null) {
                if (mCartItemCount <= 0) {
                    textCartItemCount.setVisibility(View.GONE);
                    fl_badge_containers.setVisibility(View.GONE);

                } else {
                    textCartItemCount.setText(String.valueOf(mCartItemCount));
                    textCartItemCount.setVisibility(View.VISIBLE);
                    fl_badge_containers.setVisibility(View.VISIBLE);

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void fillSuggestedCourses(RelatedBookResponse relatedBookResponse) {
        if (null != relatedBookResponse) {

              mRelatedBookList = relatedBookResponse.getData();

                if (null != mRelatedBookList && mRelatedBookList.size() > 0) {
                    mJoinedCoursesLinear.setVisibility(View.VISIBLE);
                    mJoinedCoursesAdapter.addItems(mRelatedBookList);
                } else {
                    mJoinedCoursesLinear.setVisibility(View.GONE);
                }
            }
//                if (null != mInstituteCourseList && mInstituteCourseList.size() > 0) {
//                    mInstituteCoursesLinear.setVisibility(View.VISIBLE);
//                    mInstituteCoursesAdapter.addItems(mInstituteCourseList);
//                } else {
//                    mInstituteCoursesLinear.setVisibility(View.GONE);
//                }

            // don't consume space of recommended courses if there is no course available
            findViewById(R.id.recommended_courses_linear).setVisibility(mJoinedCoursesLinear.getVisibility() == View.VISIBLE || mInstituteCoursesLinear.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

    }

    private void fillSuggestedVendorsProducts(RelatedBookResponse relatedBookResponse) {
        if (null != relatedBookResponse) {

            mRelatedBookList = relatedBookResponse.getData();

            if (null != mRelatedBookList && mRelatedBookList.size() > 0) {
                mJoinedCoursesLinear.setVisibility(View.VISIBLE);
                mJoinedCoursesAdapter.addItems(mRelatedBookList);
            } else {
                mJoinedCoursesLinear.setVisibility(View.GONE);
            }
        }
//                if (null != mInstituteCourseList && mInstituteCourseList.size() > 0) {
//                    mInstituteCoursesLinear.setVisibility(View.VISIBLE);
//                    mInstituteCoursesAdapter.addItems(mInstituteCourseList);
//                } else {
//                    mInstituteCoursesLinear.setVisibility(View.GONE);
//                }

        // don't consume space of recommended courses if there is no course available
        findViewById(R.id.recommended_courses_linear).setVisibility(mJoinedCoursesLinear.getVisibility() == View.VISIBLE || mInstituteCoursesLinear.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

    }

    private void updateBuyerList(BuyerListResponse buyerListResponse, String pictureUrl) {
        ArrayList<BuyerListResponse.User> buyers = new ArrayList<>();
        if (courseBuyerAdapter != null) {
            buyers = (ArrayList<BuyerListResponse.User>) buyerListResponse.getData().getUsers();
            courseBuyerAdapter.addItems(buyers);
            if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
                mUsersCourseLinear.setVisibility(View.GONE);
            } else {
                mUsersCourseLinear.setVisibility(View.VISIBLE);
            }
        } else {
            if (buyerListResponse.getData().getUsers().size() > Flinnt.FALSE) {
                buyers = (ArrayList<BuyerListResponse.User>) buyerListResponse.getData().getUsers();
                courseBuyerAdapter = new CourseBuyerAdapter(buyers, pictureUrl);
                courseBuyerAdapter.setOnItemClickListener(new CourseBuyerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(BrowseCourseDetailActivityNew.this,BuyerListActivity.class);
                        intent.putExtra(Course.COURSE_ID_KEY,courseId);
                        startActivity(intent);
                    }
                });
                mBuyerRecycler.setAdapter(courseBuyerAdapter);

                if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
                    mUsersCourseLinear.setVisibility(View.GONE);
                } else {
                    mUsersCourseLinear.setVisibility(View.VISIBLE);
                }

            } else {
                mUsersCourseLinear.setVisibility(View.GONE);
            }
        }
    }

    public void showCourseDeletedDialog(String errorMessage) {
        if (!isCourseDeletedDialogShowing) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // set mResorceTitleTxt
            //alertDialogBuilder.setTitle("Delete Post");
            TextView titleText = new TextView(this);
            // You Can Customise your Title here
            titleText.setText("Error");

            titleText.setPadding(40, 40, 40, 0);
            titleText.setGravity(Gravity.CENTER_VERTICAL);
            titleText.setTextColor(this.getResources().getColor(R.color.ColorPrimary));
            titleText.setTextSize(20);
            titleText.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(titleText);
            // set dialog message
            alertDialogBuilder.setMessage(errorMessage);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Delete Course from offline database
                    isCourseDeletedDialogShowing = false;
                    isDeleteCourse = true;
                    onBackPressed();
                }
            });
            alertDialogBuilder.setCancelable(false);
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            if (!Helper.isFinishingOrIsDestroyed(this)) {
                alertDialog.show();
                isCourseDeletedDialogShowing = true;
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rating_linear) {
            mCustomNestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mCustomNestedScrollView.scrollTo(0, mReviewsBaseLinear.getTop());
                }
            });
        } else {
            if (!Helper.isConnected()) {
                Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
            } else {
                switch (v.getId()) {
                    case R.id.read_more_txt:

//                        Intent intentBCD = new Intent(this, BrowseCourseDescriptionActivity.class);
//                        intentBCD.putExtra(CONTENT_ONLY, false);
//                        if (mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE && mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                            intentBCD.putExtra(BROWSABLE_COURSE, mBrowsableCourse);
//                        }
//                        intentBCD.putExtra(BrowsableCourse.ID_KEY, mBrowsableCourse.getId());
//                        intentBCD.putExtra(BrowsableCourse.NAME_KEY, mBrowsableCourse.getName());
//                        intentBCD.putExtra(BrowsableCourse.IS_PUBLIC_KEY, mBrowsableCourse.getIsPublic());
//                        intentBCD.putExtra(BrowsableCourse.DESCRIPTION_KEY, mBrowsableCourse.getDescription());
//                        startActivityForResult(intentBCD, BROWSE_COURSE_DETAILS_CALLBACK);

                        break;
                    case R.id.view_more_txt:
//                        Intent intentBCC = new Intent(this, BrowseCourseDescriptionActivity.class);
//                        intentBCC.putExtra(CONTENT_ONLY, true);
//                        intentBCC.putExtra(BrowsableCourse.ID_KEY, mBrowsableCourse.getId());
//                        intentBCC.putExtra(BrowsableCourse.NAME_KEY, mBrowsableCourse.getName());
//                        intentBCC.putExtra(BrowsableCourse.IS_PUBLIC_KEY, mBrowsableCourse.getIsPublic());
//                        if (mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE && mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                            intentBCC.putExtra(BROWSABLE_COURSE, mBrowsableCourse);
//                        }
//                        startActivityForResult(intentBCC, BROWSE_COURSE_DETAILS_CALLBACK);
                        break;

                    case R.id.join_remove_course_btn:
                        try {
//                            if (mBrowsableCourse != null) {
////                                    mSubscriptionBtn.setEnabled(false);
//                                if (mBrowsableCourse.getIsSubscribed() == Flinnt.TRUE && mBrowsableCourse.getCanSubscribe() == Flinnt.FALSE) {
//
//                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseCourseDetailActivityNew.this);
//                                    TextView titleText = new TextView(BrowseCourseDetailActivityNew.this);
//                                    titleText.setText(getResources().getString(R.string.un_subscribe));
//                                    titleText.setPadding(40, 40, 40, 0);
//                                    titleText.setGravity(Gravity.CENTER_VERTICAL);
//                                    titleText.setTextColor(BrowseCourseDetailActivityNew.this.getResources().getColor(R.color.ColorPrimary));
//                                    titleText.setTextSize(20);
//                                    titleText.setTypeface(Typeface.DEFAULT_BOLD);
//                                    alertDialogBuilder.setCustomTitle(titleText);
//                                    alertDialogBuilder.setMessage("Are you sure?");
//                                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.unsubscribe_button), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//
//                                            startProgressDialog();
//                                            new UnsubsribeCourse(mHandler, courseId).sendUnsubribeCourseRequest();
//                                        }
//                                    });
//                                    alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
////                                                mSubscriptionBtn.setEnabled(true);
//                                            dialog.cancel();
//                                        }
//                                    });
//                                    AlertDialog alertDialog = alertDialogBuilder.create();
//
//                                    if (!Helper.isFinishingOrIsDestroyed(BrowseCourseDetailActivityNew.this)) {
//                                        alertDialog.show();
//                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BrowseCourseDetailActivityNew.this.getResources().getColor(R.color.ColorPrimary));
//                                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(BrowseCourseDetailActivityNew.this.getResources().getColor(R.color.ColorPrimary));
//                                    }
//                                } else if (mBrowsableCourse.getCanSubscribe() == Flinnt.TRUE && mBrowsableCourse.getIsPublic().equalsIgnoreCase(Flinnt.DISABLED)) {
//                                    sendRequestDialog();
//                                } else if (mBrowsableCourse.getRequestContactInfo() == Flinnt.TRUE) {
//                                    startActivity(new Intent(this, PersonalInformationContactActivity.class)
//                                            .putExtra(PersonalInformationActivity.BUTTON_JOIN_BUY_TEXT, mSubscriptionBtn.getText())
//                                            .putExtra(PersonalInformationActivity.PERSONAL_BROWSE_COURSE, mBrowsableCourse)
//                                            .putExtra(PersonalInformationActivity.COURSE_PICTURE_URL, coursePictureUrl));
//                                } else if (mBrowsableCourse.getRequestPersonalInfo() == Flinnt.TRUE) {
//                                    startActivity(new Intent(this, PersonalInformationActivity.class)
//                                            .putExtra(PersonalInformationActivity.BUTTON_JOIN_BUY_TEXT, mSubscriptionBtn.getText())
//                                            .putExtra(PersonalInformationActivity.PERSONAL_BROWSE_COURSE, mBrowsableCourse)
//                                            .putExtra(PersonalInformationActivity.COURSE_PICTURE_URL, coursePictureUrl));
//                                } else if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.ENABLED)) {
//                                    startProgressDialog();
//                                    new JoinCommunity(mHandler, courseId, true).sendJoinCommunityRequest();
//                                } else if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                                    try {
//                                        MyCommFun.sendTracker(this, "BuyNow/Start/" + courseId);
//                                        GoogleAnalytics.getInstance(this).reportActivityStart(this);
//                                    } catch (Exception e) {
//                                        LogWriter.err(e);
//                                    }
//                                    startProgressDialog();
//                                    new CheckoutCourse(mHandler, courseId).sendCheckoutRequest();
//                                }
//                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;

                    case R.id.layout_rate_course:
                        showRateReviewDialog("SUBMIT");
                        break;
                    case R.id.layout_rating_0:
                        showRateReviewDialog("SUBMIT");
                        break;
                    case R.id.layout_rating_1:
                    case R.id.layout_rating_2:
                    case R.id.all_reviews_txt:
//                        if (mBrowsableCourse != null) {
//                            startActivity(new Intent(this, CourseReviewsActivity.class)
//                                    .putExtra(BrowsableCourse.ID_KEY, courseId)
//                                    .putExtra(BrowsableCourse.NAME_KEY, mBrowsableCourse.getName()));
//                        }
                        break;
                    case R.id.buy_txt:
                        startProgressDialog();
                        new CheckoutCourse(mHandler, courseId).sendCheckoutRequest();
                        break;
                    case R.id.post_media_open_btn_postdetailview:
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + postYoutubeVedioID));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + postYoutubeVedioID));
                                startActivity(intent);
                            } catch (Exception e) {
                                Helper.showToast(getString(R.string.no_app_available), Toast.LENGTH_LONG);
                                LogWriter.err(e);
                            }
                        }
                        break;
                    case R.id.refund_policy_txt:
                        if (Helper.isConnected()) {
//                            if (mBrowsableCourse.getCanRefund() == Flinnt.TRUE) {
//                                Intent refundIntent = new Intent(BrowseCourseDetailActivityNew.this, RefundActivity.class);
//                                refundIntent.putExtra(Course.COURSE_ID_KEY, courseId);
//                                startActivityForResult(refundIntent, REFUND_CALLBACK);
//                            }
                        } else {
                            Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
                        }
                        break;
                }
            }
        }
    }

    private void showHideReviewSection() {
        try {
            if (mRattingsView == null) {
                if ((mReviewCourseLinear.getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_0).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_1).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_2).getVisibility() == View.VISIBLE)) {
                    mReviewsBaseLinear.setVisibility(View.VISIBLE);
                } else {
                    mReviewsBaseLinear.setVisibility(View.GONE);
                }
            } else if ((/*mReviewCourseLinear.getVisibility() == View.VISIBLE || */findViewById(R.id.layout_rating_0).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_1).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_2).getVisibility() == View.VISIBLE || mRattingsView.getVisibility() == View.VISIBLE)) {
                mReviewsBaseLinear.setVisibility(View.VISIBLE);
            } else {
                mReviewsBaseLinear.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void showHideReviewSection(BrowsableCourse mBrowsableCourse) {

        try {
            if (mBrowsableCourse.getIsPublic().equals("0")) {
                mReviewsBaseLinear.setVisibility(View.GONE);
            } else {
                mReviewsBaseLinear.setVisibility(View.VISIBLE);
            }
            if ((mReviewCourseLinear.getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_0).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_1).getVisibility() == View.VISIBLE || findViewById(R.id.layout_rating_2).getVisibility() == View.VISIBLE || mRattingsView.getVisibility() == View.VISIBLE) && !mBrowsableCourse.getIsPublic().equals("0")) {
                mReviewsBaseLinear.setVisibility(View.VISIBLE);
            } else {
                mReviewsBaseLinear.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data + ", Course id : " + courseId);
        try {

            if (resultCode == RESULT_OK) {
                switch (requestCode) {

                    case MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK:
                        addedAndRemovedCourses = (HashMap<Course, Boolean>) data.getSerializableExtra(JoinCourseResponse.JOINED_KEY);
                        if (addedAndRemovedCourses.size() > 0) {
                            onBackPressed();
                        }
                        break;

                    case BrowseCourseDetailActivityNew.CHECKOUT_CALLBACK:
                        CourseDescriptionNew mCourseDescription = new CourseDescriptionNew(mHandler, courseId);
                        mCourseDescription.sendCourseDescriptionRequest();
                        startProgressDialog();
                        break;

                    case BrowseCourseDetailActivityNew.REFUND_CALLBACK:
                        courseBuyerAdapter.removeItems();
                        if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
                            mUsersCourseLinear.setVisibility(View.GONE);
                        }

                        boolean isCourseAddedAndRemovedInCurrentSession = false;
                        if (null != joinedCourse) {
                            for (Course course : addedAndRemovedCourses.keySet()) {
                                if (course.getCourseID().equals(joinedCourse.getCourseID())) {
                                    addedAndRemovedCourses.remove(course);
                                    isCourseAddedAndRemovedInCurrentSession = true;
                                    break;
                                }
                            }
                        }
                        if (!isCourseAddedAndRemovedInCurrentSession) {
                            if (null == joinedCourse) {
                                joinedCourse = new Course();
                               // joinedCourse.setCourseID(mBrowsableCourse.getId()); // to remove a course, only id is needed
                            }
                            addedAndRemovedCourses.put(joinedCourse, false);
                        }
                        CourseDescriptionNew mCourseDesc = new CourseDescriptionNew(mHandler, courseId);
                        mCourseDesc.sendCourseDescriptionRequest();
//                        mReviewsBaseLinear.removeAllViews();

                        mReviewCourseLinear.setVisibility(View.GONE);
                        findViewById(R.id.layout_rating_0).setVisibility(View.GONE);
                        showHideReviewSection();

                        break;

                    case 101:
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("whichTab",2);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                        break;
                }
            } else {
                CourseDescriptionNew mCourseDescription = new CourseDescriptionNew(mHandler, courseId);
                mCourseDescription.sendCourseDescriptionRequest();
                startProgressDialog();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    //Chirag : 14/08/2018
    @Override
    public void onBackPressed() {

        if (postFullScreen) {
            postYouTubePlayer.setFullscreen(false);
        } else if (isFromNotification >= Flinnt.BROWSECOURSE_NORMAL) { //For 90301: change == with >=
            //Log.d("Broo", "isFromNotification : " + isFromNotification);
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            //NavUtils.navigateUpTo(this, upIntent);
            Intent resultIntent = new Intent(this, MyCoursesActivity.class);
            resultIntent.putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses);
            resultIntent.putExtra("isFromNotification",Flinnt.TRUE);//@chirag: 14/08/2018 for course tab
            resultIntent.putExtra(Config.USER_ID, userId);////@chirag: 14/08/2018
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //@Chirag:16/08/2018 added
            if (isDeleteCourse) {
                resultIntent.putExtra(BrowsableCourse.ID_KEY, courseId);
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("addedAndRemovedCourses : " + addedAndRemovedCourses.toString());
//            }
            //setResult(Activity.RESULT_OK, resultIntent);////@chirag: 14/08/2018
            startActivity(resultIntent);
            finish();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            //NavUtils.navigateUpTo(this, upIntent);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses);
            if (isDeleteCourse) {
                resultIntent.putExtra(BrowsableCourse.ID_KEY, courseId);
            }
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("addedAndRemovedCourses : " + addedAndRemovedCourses.toString());
//            }
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }


    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (MyCoursesActivity.class.getName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * Update the UI based on user's subscription of the course
     *
     * @param mUnsubscribeCourseResponse course unsubscribe response
     */
    private void switchSubscription(UnsubscribeCourseResponse mUnsubscribeCourseResponse) {
        if (courseBuyerAdapter != null) {
            courseBuyerAdapter.removeItems();
        }

      //  mBrowsableCourse.setCanSubscribe(mUnsubscribeCourseResponse.getCanSubscribe());
//        if (mBrowsableCourse.getCanSubscribe() != Flinnt.TRUE && mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE) {
//            mSubscriptionBtn.setVisibility(View.GONE);
//            mUnableToJoinTxt.setVisibility(View.VISIBLE);
//            mUnableToJoinTxt.setText(mBrowsableCourse.getMessage_text());
//        } else if (mBrowsableCourse.getCanSubscribe() == Flinnt.TRUE && mBrowsableCourse.getIsPublic().equalsIgnoreCase(Flinnt.DISABLED)) {
//            mSubscriptionBtn.setText(R.string.send_request);
//        } else if (mBrowsableCourse.getCanSubscribe() == Flinnt.TRUE && mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.ENABLED)) {
//            mSubscriptionBtn.setText(R.string.join_now);
//        } else if (mUnsubscribeCourseResponse.getIsRemoved() == Flinnt.TRUE) {
//            mBrowsableCourse.setIsSubscribed(Flinnt.FALSE);
//            mBrowsableCourse.setCan_unsubscribe(Flinnt.FALSE);
//            mReviewCourseLinear.setVisibility(View.GONE);
//            showHideReviewSection();
//            isWishlist = true;
//            invalidateOptionsMenu();
//            findViewById(R.id.layout_rating_0).setVisibility(View.GONE);
//            if (mBrowsableCourse.getCan_unsubscribe() != Flinnt.TRUE && mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                mSubscriptionBtn.setText(R.string.buy_now_button);
//                if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                    mCustomNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//
//                        @Override
//                        public void onScrollChanged() {
//                            Rect scrollBounds = new Rect();
//                            mCustomNestedScrollView.getDrawingRect(scrollBounds);
//
//                            float top = mDeviderView.getY() + getResources().getDimension(R.dimen.margin_100dp);
//                            float bottom = top + mDeviderView.getHeight();
//
//                            if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
//                                if (mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.GONE);
//                                    mBottomSheetLinear.setVisibility(View.GONE);
//                                    mBottomSheetLinear.startAnimation(animFadeOut);
//                                }
//                            } else {
//                                if (!mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.startAnimation(animFadein);
//                                }
//                            }
//                        }
//                    });
//                }
//
//            }
//
//            if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                LogWriter.write("Price After unsubscribe : " + mBrowsableCourse.getPrice().getBuy());
//                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBuy());
//                mFinalPriceTxt.setVisibility(View.VISIBLE);
//                mFPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBuy());
//                mFPriceTxt.setVisibility(View.VISIBLE);
//                if (mBrowsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
//                    mOldPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBase());
//                    mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    mOldPriceTxt.setVisibility(View.VISIBLE);
//                    mOPriceTxt.setText(getResources().getString(R.string.currency) + mBrowsableCourse.getPrice().getBase());
//                    mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    mOPriceTxt.setVisibility(View.VISIBLE);
//                } else {
//                    mOldPriceTxt.setVisibility(View.GONE);
//                    mOPriceTxt.setVisibility(View.GONE);
//                }
//            }
//        }
//        if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//            if (courseBuyerAdapter != null) {
//                if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
//                    mUsersCourseLinear.setVisibility(View.GONE);
//                } else {
//                    mUsersCourseLinear.setVisibility(View.VISIBLE);
//                }
//            }
//            if (null == mBuyerList) {
//                if (Helper.isConnected()) {
//                    mBuyerListRequest = new BuyerListRequest();
//                    mBuyerList = new BuyerList(mHandler, courseId);
//                    mBuyerList.sendBuyerListRequest(mBuyerListRequest);
//                } else {
//                    Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
//                }
//            }
//        }

 /*       // To dosplay refund policty message/link.
        if (mBrowsableCourse.getRefundApplicable() == Flinnt.TRUE) {
            if (mBrowsableCourse.getCanRefund() == Flinnt.TRUE) {
                if (mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
                    mRefunPolicyTxt.setTextColor(Color.parseColor("#3b6d95"));
                    SpannableString content = new SpannableString(getResources().getString(R.string.refund_course_txt));
                    content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.refund_course_txt).length(), 0);
                    mRefunPolicyTxt.setText(content);
                } else {
                    mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
                }
            } else {
                if (!mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
                    mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
                } else {
                    mRefunPolicyTxt.setVisibility(View.GONE);
                }
            }
        } else {
            mRefunPolicyTxt.setVisibility(View.GONE);
        }*/

        if (Helper.isConnected()) {
            CourseDescriptionNew mCourseDesc = new CourseDescriptionNew(mHandler, courseId);
            mCourseDesc.sendCourseDescriptionRequest();
            startProgressDialog();
        } else {
            Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
        }

    }

    /**
     * Switch the wish-list icon according to current status
     *
     * @param mWishListResponse wish-list response
     */
    private void switchWishStatus(WishListResponse mWishListResponse) {
       // mBrowsableCourse.setIsWishList(mWishListResponse.getIsWishList());
    }

    /**
     * Manages the visibility of suggested courses list and updates UI
     *
     * @param mSuggestedCoursesResponse response of suggested courses
     */
//    private void fillSuggestedCourses(SuggestedCoursesResponse mSuggestedCoursesResponse) {
//        if (null != mSuggestedCoursesResponse) {
//            if (mSuggestedCoursesResponse.getType() == SuggestedCourses.USERS_JOINED_COURSES) {
//                mJoinedCoursesList = mSuggestedCoursesResponse.getBrowsableCourses();
//                if (null != mJoinedCoursesList && mJoinedCoursesList.size() > 0) {
//                    mJoinedCoursesLinear.setVisibility(View.VISIBLE);
//                    mJoinedCoursesAdapter.addItems(mJoinedCoursesList);
//                } else {
//                    mJoinedCoursesLinear.setVisibility(View.GONE);
//                }
//            } else if (mSuggestedCoursesResponse.getType() == SuggestedCourses.COURSES_FROM_INSTITUTE) {
//                mInstituteCourseList = mSuggestedCoursesResponse.getBrowsableCourses();
//                if (null != mInstituteCourseList && mInstituteCourseList.size() > 0) {
//                    mInstituteCoursesLinear.setVisibility(View.VISIBLE);
//                    mInstituteCoursesAdapter.addItems(mInstituteCourseList);
//                } else {
//                    mInstituteCoursesLinear.setVisibility(View.GONE);
//                }
//            }
//            // don't consume space of recommended courses if there is no course available
//            findViewById(R.id.recommended_courses_linear)
//                    .setVisibility(mJoinedCoursesLinear.getVisibility() == View.VISIBLE
//                            || mInstituteCoursesLinear.getVisibility() == View.VISIBLE
//                            ? View.VISIBLE : View.GONE);
//        }
//    }

    /**
     * Manages the visibility of views and fill and display reviews
     *
     * @param positionOfLayout position of layout in where to display the review
     * @param userReview       user review model
     */
    private void fillReviewAtPosition(int positionOfLayout, final UserReview userReview) {
        mRattingsView = findViewById(R.id.layout_rating_0);
        if (positionOfLayout == 1) {
            mRattingsView = findViewById(R.id.layout_rating_1);
        } else if (positionOfLayout == 2) {
            mRattingsView = findViewById(R.id.layout_rating_2);
        } else {
            mReviewCourseLinear.setVisibility(View.GONE);
            mRattingsView.setVisibility(View.VISIBLE);
            mRattingsView.findViewById(R.id.layout_edit_review).setVisibility(View.VISIBLE);
        }

        mRattingsView.setOnClickListener(this);


        final TextView reviewMain = (TextView) mRattingsView.findViewById(R.id.tv_review_0);
        reviewMain.setMaxLines(3); // TODO: 13/6/16 : change this to 2 if implementing separate TextView for fade
        final TextView reviewLastLine = (TextView) mRattingsView.findViewById(R.id.tv_review_1);
        reviewLastLine.setSelected(true);
        TextView time = (TextView) mRattingsView.findViewById(R.id.tv_review_time);
        TextView userName = (TextView) mRattingsView.findViewById(R.id.tv_review_username);
        TextView youText = (TextView) mRattingsView.findViewById(R.id.tv_youtax);
        AppCompatRatingBar courseRating = (AppCompatRatingBar) mRattingsView.findViewById(R.id.rb_review);
        SelectableRoundedCourseImageView userPicture = (SelectableRoundedCourseImageView) mRattingsView.findViewById(R.id.iv_review_user);
        userPicture.setDefaultImageResId(R.drawable.default_viewers_image);
        userPicture.setOval(true);

        mRattingsView.setVisibility(View.VISIBLE);
        String userImageUrl = baseUserPictureUrl + userReview.getReviewUserPicture();
        userPicture.setImageUrl(userImageUrl, mImageLoader);
        time.setText(Helper.formateTimeMillis(Long.parseLong(userReview.getReviewTimeStamp())));
        courseRating.setRating(Float.parseFloat(userReview.getReviewRating()));
        reviewMain.setText(userReview.getReviewText());

//      userName.setText(String.format(positionOfLayout == 0 ? "%s (You)" : "%s", userReview.getReviewUserName()));
        userName.setText(String.format(userReview.getReviewUserName()));
        if (positionOfLayout == 0) {
            youText.setText(" (You)");
            youText.setVisibility(View.VISIBLE);
        } else {
            youText.setVisibility(View.GONE);
        }


    }

    /**
     * Fill the UI with course details
     *
     * @param browsableCourse course from response
     */
    private void updateCourseDetails(BrowsableCourse browsableCourse) {
        if (null != browsableCourse) {
            if (null == mContentsList && browsableCourse.getIsPublic().equals(Flinnt.ENABLED)) {
                mContentsList = new ContentsList(mHandler, courseId);
                mContentsList.setSearchString("");
                mContentsList.sendContentsListRequest();
                startProgressDialog();
            }
            mTotalRattingsTxt.setText(browsableCourse.getTotalNoRating() + " " + getResources().getString(R.string.ratting_total_text));
            if (!browsableCourse.getVideoUrl().equalsIgnoreCase("")) {
                // start for youtube video
                mPostMediaRelative.setVisibility(View.VISIBLE);
                mCoursePictureImg.setVisibility(View.GONE);
                postYoutubeVedioID = null;
                String youtubeUrl = browsableCourse.getVideoUrl();//"https://www.youtube.com/watch?v=4HRC6c5-2lQ&list=PLcm_sbf1ZgNkYF1fIF-pJB7MSWgX_ygnD";//mPost.getAttachments();

                String packageName = "com.google.android.youtube";
                boolean isYoutubeInstalled = isAppInstalled(packageName);

                String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
                Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = compiledPattern.matcher(youtubeUrl);
                while (matcher.find()) {
                    System.out.println(matcher.group());
                    postYoutubeVedioID = matcher.group(1);
                }

                if (isYoutubeInstalled) {
                    postYouTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, BrowseCourseDetailActivityNew.this);
                    mMediaOpenImgbtn.setVisibility(View.GONE);
                    mYoutubeFrame.setVisibility(View.VISIBLE);
                } else {
                    isAppNotInstalled();
                }
                // end for youtube
            } else {
                mCoursePictureImg.setVisibility(View.VISIBLE);
                mPostMediaRelative.setVisibility(View.GONE);
            }

            mCourseNameTxt.setText(browsableCourse.getName());
            mInstituteNameTxt.setText(browsableCourse.getInstituteName());
            mMoreCoursesFromInstituteTxt.setText(String.format("%s %s", getString(R.string.more_courses_from), browsableCourse.getInstituteName()));
            //*****change 32
            if (!browsableCourse.getDescription().equalsIgnoreCase("")) {
                mCourseDescriptionTxt.setText(Html.fromHtml(browsableCourse.getDescription(), null, new MyTagHandler()));
//                mCourseDescriptionTxt.setText(getStrippedString(browsableCourse.getDescription()));
                mDescriptionLinear.setVisibility(View.VISIBLE);
            } else {
                mDescriptionLinear.setVisibility(View.GONE);
            }
            mCourseDescriptionTxt.setMovementMethod(LinkMovementMethod.getInstance());

            mCourseDescriptionTxt.post(new Runnable() {
                @Override
                public void run() {
                    lineCount = mCourseDescriptionTxt.getLineCount();
                    if (LogWriter.isValidLevel(Log.DEBUG))
                        LogWriter.write("lineCount : " + lineCount);
                    mReadMoreLinear.setVisibility(lineCount >= 10 ? View.VISIBLE : View.GONE);
                }
            });
            if (browsableCourse.getPublicType().equalsIgnoreCase(Flinnt.COURSE_TYPE_TIMEBOUND)) {
                mDateTimeAddressLinear.setVisibility(View.VISIBLE);
                mDatetimeTxt.setText(getStrippedString(browsableCourse.getEventDatetime()));
                mAddressTxt.setText(getStrippedString(browsableCourse.getEventLocation()));
                mDatetimeTxt.setVisibility(!getStrippedString(browsableCourse.getEventDatetime()).equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
                mAddressTxt.setVisibility(!getStrippedString(browsableCourse.getEventLocation()).equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
            } else {
                mDateTimeAddressLinear.setVisibility(View.GONE);
            }
            mCourseRatingBar.setRating(Float.parseFloat(browsableCourse.getRatings()));
            shareUrl = browsableCourse.getShareUrl();
            mSubscriptionBtn.setVisibility(View.VISIBLE);
            LogWriter.write("Is free : " + browsableCourse.getIs_free() + " , unsubscribe : " + browsableCourse.getCanSubscribe());

//            if (browsableCourse.getCanSubscribe() != Flinnt.TRUE && browsableCourse.getCan_unsubscribe() != Flinnt.TRUE) {
//                mSubscriptionBtn.setVisibility(View.GONE);
//                mUnableToJoinTxt.setVisibility(View.VISIBLE);
//                mUnableToJoinTxt.setText(mBrowsableCourse.getMessage_text());
//                if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                    mFinalPriceTxt.setVisibility(View.VISIBLE);
//                    mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
//                    mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
//                    if (browsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
//                        mOldPriceTxt.setVisibility(View.VISIBLE);
//                        mOPriceTxt.setVisibility(View.VISIBLE);
//                        mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
//                        mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//                        mOPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
//                        mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    } else {
//                        mOldPriceTxt.setVisibility(View.GONE);
//                        mOPriceTxt.setVisibility(View.GONE);
//                    }
//                } else {
//                    mTempTxt.setVisibility(View.GONE);
//                }
            } else if (browsableCourse.getCanSubscribe() == Flinnt.TRUE && browsableCourse.getIsPublic().equalsIgnoreCase(Flinnt.DISABLED)) {
                mSubscriptionBtn.setText(R.string.send_request);
            } else if (browsableCourse.getCanSubscribe() == Flinnt.TRUE && browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.ENABLED)) {
                mSubscriptionBtn.setText(R.string.join_now);
            } else if (browsableCourse.getCan_unsubscribe() == Flinnt.TRUE) {
                mSubscriptionBtn.setText(R.string.un_subscribe);
                mFinalPriceTxt.setVisibility(View.GONE);
                mFPriceTxt.setVisibility(View.GONE);
                mOldPriceTxt.setVisibility(View.GONE);
                mOPriceTxt.setVisibility(View.GONE);
            } else if (browsableCourse.getCan_unsubscribe() != Flinnt.TRUE && browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
                mSubscriptionBtn.setText(R.string.buy_now_button);

//                if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                    mCustomNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//
//                        @Override
//                        public void onScrollChanged() {
//                            Rect scrollBounds = new Rect();
//                            mCustomNestedScrollView.getDrawingRect(scrollBounds);
//
//                            float top = mDeviderView.getY() + getResources().getDimension(R.dimen.margin_100dp);
//                            float bottom = top + mDeviderView.getHeight();
//
//                            if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
//                                if (mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.GONE);
//                                    mBottomSheetLinear.setVisibility(View.GONE);
//                                    mBottomSheetLinear.startAnimation(animFadeOut);
//                                }
//                            } else {
//                                if (!mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.startAnimation(animFadein);
//                                }
//                            }
//                        }
//                    });
//                }

            }
            if (browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
                mFinalPriceTxt.setVisibility(View.VISIBLE);
                mFPriceTxt.setVisibility(View.VISIBLE);
                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
                mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
                if (browsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
                    mOldPriceTxt.setVisibility(View.VISIBLE);
                    mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
                    mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    mOPriceTxt.setVisibility(View.VISIBLE);
                    mOPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
                    mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                } else {
                    mOldPriceTxt.setVisibility(View.GONE);
                    mOPriceTxt.setVisibility(View.GONE);
                }
            }
//            if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                if (courseBuyerAdapter != null) {
//                    if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
//                        mUsersCourseLinear.setVisibility(View.GONE);
//                    } else {
//                        mUsersCourseLinear.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                if (null == mBuyerList) {
//                    if (Helper.isConnected()) {
//                        mBuyerListRequest = new BuyerListRequest();
//                        mBuyerList = new BuyerList(mHandler, courseId);
//                        mBuyerList.sendBuyerListRequest(mBuyerListRequest);
//                    } else {
//                        Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
//                    }
//                }
         //  }

            if (browsableCourse.getIsSubscribed() == Flinnt.TRUE) {
                isWishlist = false;
                mReviewCourseLinear.setVisibility(View.VISIBLE);
                showHideReviewSection(browsableCourse);
            }

            if (browsableCourse.getShowWishlist() == Flinnt.TRUE) {
                isWishlist = true;
            } else {
                isWishlist = false;
            }

            if (null != browsableCourse.getUserReview()) {
                fillReviewAtPosition(0, browsableCourse.getUserReview());
            }
            invalidateOptionsMenu();
            if (isFromNotification == Flinnt.BROWSECOURSE_SHARE_HEIGHLIGHT) {
                mShareAnimationBtn.performClick();
            }
            if (isFromNotification == Flinnt.BROWSECOURSE_RATTING_HEIGHLIGHT) {
                showRateReviewDialog("SUBMIT");
            }


            // To dosplay refund policty message/link.
//            if (mBrowsableCourse.getRefundApplicable() == Flinnt.TRUE) {
//                if (mBrowsableCourse.getCanRefund() == Flinnt.TRUE) {
//                    if (mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
//                        mRefunPolicyTxt.setTextColor(Color.parseColor("#3b6d95"));
//                        SpannableString content = new SpannableString(getResources().getString(R.string.refund_course_txt));
//                        content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.refund_course_txt).length(), 0);
//                        mRefunPolicyTxt.setText(content);
//                    } else {
//                        mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
//                    }
//                } else {
//                    if (!mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
//                        mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
//                    } else {
//                        mRefunPolicyTxt.setVisibility(View.GONE);
//                    }
//                }
//            } else {
//                mRefunPolicyTxt.setVisibility(View.GONE);
//            }

        //}
    }

    //09-01-2019 by  vijay
    private void updateCourseDetailsNew(final StoreBookDetailResponse.Data browsableCourse) {
        if (null != browsableCourse) {
//            if (null == mContentsList && browsableCourse.getIsPublic().equals(Flinnt.ENABLED)) {
//                mContentsList = new ContentsList(mHandler, courseId);
//                mContentsList.setSearchString("");
//                mContentsList.sendContentsListRequest();
//                startProgressDialog();
//            }
            //mTotalRattingsTxt.setText(browsableCourse.getTotalNoRating() + " " + getResources().getString(R.string.ratting_total_text));
//            if (!browsableCourse.getVideoUrl().equalsIgnoreCase("")) {
//                // start for youtube video
//                mPostMediaRelative.setVisibility(View.VISIBLE);
//                mCoursePictureImg.setVisibility(View.GONE);
//                postYoutubeVedioID = null;
//                String youtubeUrl = browsableCourse.getVideoUrl();//"https://www.youtube.com/watch?v=4HRC6c5-2lQ&list=PLcm_sbf1ZgNkYF1fIF-pJB7MSWgX_ygnD";//mPost.getAttachments();
//
//                String packageName = "com.google.android.youtube";
//                boolean isYoutubeInstalled = isAppInstalled(packageName);
//
//                String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
//                Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
//                Matcher matcher = compiledPattern.matcher(youtubeUrl);
//                while (matcher.find()) {
//                    System.out.println(matcher.group());
//                    postYoutubeVedioID = matcher.group(1);
//                }
//
//                if (isYoutubeInstalled) {
//                    postYouTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, BrowseCourseDetailActivityNew.this);
//                    mMediaOpenImgbtn.setVisibility(View.GONE);
//                    mYoutubeFrame.setVisibility(View.VISIBLE);
//                } else {
//                    isAppNotInstalled();
//                }
//                // end for youtube
//            } else {
//                mCoursePictureImg.setVisibility(View.VISIBLE);
//                mPostMediaRelative.setVisibility(View.GONE);
//            }
            this.browsableCourse = browsableCourse;
            mCoursePictureImg.setVisibility(View.VISIBLE);
            mPostMediaRelative.setVisibility(View.GONE);

            btnAddCart.setEnabled(true);
            btnAddCart.setAlpha(1);

            mCourseNameTxt.setText(browsableCourse.getBookName());
            mInstituteNameTxt.setText(browsableCourse.getVendorName());
            //BrowseCoursesFragmentNew.cart_count = browsableCourse.getCart_total();

            authorListAdapter = new AuthorListAdapter(BrowseCourseDetailActivityNew.this,browsableCourse.getAuthors());
            mAuthorRvList.setLayoutManager(new LinearLayoutManager(BrowseCourseDetailActivityNew.this));
            mAuthorRvList.setAdapter(authorListAdapter);


            try {
                Gson jsonData =  new Gson();
                String jsonStringData  = jsonData.toJson(browsableCourse.getAttribute());

                JSONObject dataObject = new JSONObject(jsonStringData);

                if(dataObject!= null) {

                    Iterator<String> iterator = dataObject.keys();

                    ArrayList<String> keys = new ArrayList<String>();
                    ArrayList<String> values = new ArrayList<String>();


                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        keys.add(key);
                        values.add(dataObject.optString(key));
                    }

                    productDetailListAdapter = new ProductDetailListAdapter(BrowseCourseDetailActivityNew.this, keys, values);
                    mRVProductAttributeList.setLayoutManager(new LinearLayoutManager(BrowseCourseDetailActivityNew.this));
                    mRVProductAttributeList.setAdapter(productDetailListAdapter);

                }

            }catch (Exception ex){
                ex.printStackTrace();
            }

            //mInstituteNameTxt.setText(browsableCourse.getInstituteName());
            //mMoreCoursesFromInstituteTxt.setText(String.format("%s %s", getString(R.string.more_courses_from), browsableCourse.getInstituteName()));
            //*****change 32
            //&& mBrowsableCourse.getIsPublic().equals("1")



            if (!browsableCourse.getDescriptions().get(0).getDescription().equalsIgnoreCase("")) {

                mCourseDescriptionTxt.setText(Html.fromHtml(browsableCourse.getDescriptions().get(1).getDescription(), null, new MyTagHandler()));

                mRefunPolicyTxt.setText(browsableCourse.getDescriptions().get(0).getDescription());

//                mCourseDescriptionTxt.setText(getStrippedString(browsableCourse.getDescription()));
                mDescriptionLinear.setVisibility(View.VISIBLE);
            } else {
                mDescriptionLinear.setVisibility(View.GONE);
            }
            mCourseDescriptionTxt.setMovementMethod(LinkMovementMethod.getInstance());
            coursePictureUrl = browsableCourse.getImages().get(0).getOriginalPath();
            mCoursePictureImg.setImageUrl(coursePictureUrl,mImageLoader);
            mCourseDescriptionTxt.post(new Runnable() {
                @Override
                public void run() {
                    lineCount = mCourseDescriptionTxt.getLineCount();
                    if (LogWriter.isValidLevel(Log.DEBUG))
                        LogWriter.write("lineCount : " + lineCount);
                    mReadMoreLinear.setVisibility(lineCount >= 10 ? View.VISIBLE : View.GONE);
                }
            });
//            if (browsableCourse.getPublicType().equalsIgnoreCase(Flinnt.COURSE_TYPE_TIMEBOUND)) {
//                mDateTimeAddressLinear.setVisibility(View.VISIBLE);
//                mDatetimeTxt.setText(getStrippedString(browsableCourse.getEventDatetime()));
//                mAddressTxt.setText(getStrippedString(browsableCourse.getEventLocation()));
//                mDatetimeTxt.setVisibility(!getStrippedString(browsableCourse.getEventDatetime()).equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
//                mAddressTxt.setVisibility(!getStrippedString(browsableCourse.getEventLocation()).equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
//            } else {
//                mDateTimeAddressLinear.setVisibility(View.GONE);
//            }
            //mCourseRatingBar.setRating(Float.parseFloat(browsableCourse.getRatings()));
            mCourseRatingBar.setRating(Float.parseFloat("3.5"));

            //shareUrl = browsableCourse.getShareUrl();
            mSubscriptionBtn.setVisibility(View.VISIBLE);

            mOldPriceTxt.setVisibility(View.VISIBLE);
            mOPriceTxt.setVisibility(View.VISIBLE);
            mFinalPriceTxt.setVisibility(View.VISIBLE);
            mFPriceTxt.setVisibility(View.VISIBLE);


            mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getListPrice());
            mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getSalePrice());
               mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getSalePrice());

            //LogWriter.write("Is free : " + browsableCourse.getIs_free() + " , unsubscribe : " + browsableCourse.getCanSubscribe());

//            if (browsableCourse.getCanSubscribe() != Flinnt.TRUE && browsableCourse.getCan_unsubscribe() != Flinnt.TRUE) {
//            mSubscriptionBtn.setVisibility(View.GONE);
//            mUnableToJoinTxt.setVisibility(View.VISIBLE);
           // mUnableToJoinTxt.setText(mBrowsableCourse.getMessage_text());

           // mUnableToJoinTxt.setText("Unable to Join");

//            if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                mFinalPriceTxt.setVisibility(View.VISIBLE);
////                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
////                mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
//
//                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getSalePrice());
//                mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getSalePrice());
//
////                    if (browsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
////                        mOldPriceTxt.setVisibility(View.VISIBLE);
////                        mOPriceTxt.setVisibility(View.VISIBLE);
////                        mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
////                        mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
////
////                        mOPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
////                        mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
////                    }
////
////                    else {
////                        mOldPriceTxt.setVisibility(View.GONE);
////                        mOPriceTxt.setVisibility(View.GONE);
////                    }
//
//                mOldPriceTxt.setVisibility(View.VISIBLE);
//                mOPriceTxt.setVisibility(View.VISIBLE);
//                mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getListPrice());
//                mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//            } else {
//                mTempTxt.setVisibility(View.GONE);
//            }
        }
//            } else if (browsableCourse.getCanSubscribe() == Flinnt.TRUE && browsableCourse.getIsPublic().equalsIgnoreCase(Flinnt.DISABLED)) {
//                mSubscriptionBtn.setText(R.string.send_request);
//            } else if (browsableCourse.getCanSubscribe() == Flinnt.TRUE && browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.ENABLED)) {
//                mSubscriptionBtn.setText(R.string.join_now);
//            } else if (browsableCourse.getCan_unsubscribe() == Flinnt.TRUE) {
//                mSubscriptionBtn.setText(R.string.un_subscribe);
//                mFinalPriceTxt.setVisibility(View.GONE);
//                mFPriceTxt.setVisibility(View.GONE);
//                mOldPriceTxt.setVisibility(View.GONE);
//                mOPriceTxt.setVisibility(View.GONE);
//        }
//            } else if (browsableCourse.getCan_unsubscribe() != Flinnt.TRUE && browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                mSubscriptionBtn.setText(R.string.buy_now_button);

//                if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                    mCustomNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//
//                        @Override
//                        public void onScrollChanged() {
//                            Rect scrollBounds = new Rect();
//                            mCustomNestedScrollView.getDrawingRect(scrollBounds);
//
//                            float top = mDeviderView.getY() + getResources().getDimension(R.dimen.margin_100dp);
//                            float bottom = top + mDeviderView.getHeight();
//
//                            if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
//                                if (mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.GONE);
//                                    mBottomSheetLinear.setVisibility(View.GONE);
//                                    mBottomSheetLinear.startAnimation(animFadeOut);
//                                }
//                            } else {
//                                if (!mBottomSheetLinear.isShown()) {
//                                    mSpaceTxt.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.setVisibility(View.VISIBLE);
//                                    mBottomSheetLinear.startAnimation(animFadein);
//                                }
//                            }
//                        }
//                    });
//                }


//            if (browsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                mFinalPriceTxt.setVisibility(View.VISIBLE);
//                mFPriceTxt.setVisibility(View.VISIBLE);
//                mFinalPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
//                mFPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBuy());
//                if (browsableCourse.getPrice().getDiscount_applicable() >= Flinnt.TRUE) {
//                    mOldPriceTxt.setVisibility(View.VISIBLE);
//                    mOldPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
//                    mOldPriceTxt.setPaintFlags(mOldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    mOPriceTxt.setVisibility(View.VISIBLE);
//                    mOPriceTxt.setText(getResources().getString(R.string.currency) + browsableCourse.getPrice().getBase());
//                    mOPriceTxt.setPaintFlags(mOPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//                } else {
//                    mOldPriceTxt.setVisibility(View.GONE);
//                    mOPriceTxt.setVisibility(View.GONE);
//                }
//            }

//            if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                if (courseBuyerAdapter != null) {
//                    if (courseBuyerAdapter.getItemCount() < Flinnt.TRUE) {
//                        mUsersCourseLinear.setVisibility(View.GONE);
//                    } else {
//                        mUsersCourseLinear.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                if (null == mBuyerList) {
//                    if (Helper.isConnected()) {
//                        mBuyerListRequest = new BuyerListRequest();
//                        mBuyerList = new BuyerList(mHandler, courseId);
//                        mBuyerList.sendBuyerListRequest(mBuyerListRequest);
//                    } else {
//                        Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
//                    }
//                }
//            }

//            if (browsableCourse.getIsSubscribed() == Flinnt.TRUE) {
//                isWishlist = false;
//                mReviewCourseLinear.setVisibility(View.VISIBLE);
//                showHideReviewSection(browsableCourse);
//            }

//            if (browsableCourse.getShowWishlist() == Flinnt.TRUE) {
//                isWishlist = true;
//            } else {
//                isWishlist = false;
//            }

//            if (null != browsableCourse.getUserReview()) {
//                fillReviewAtPosition(0, browsableCourse.getUserReview());
//            }
            invalidateOptionsMenu();
            if (isFromNotification == Flinnt.BROWSECOURSE_SHARE_HEIGHLIGHT) {
                mShareAnimationBtn.performClick();
            }
            if (isFromNotification == Flinnt.BROWSECOURSE_RATTING_HEIGHLIGHT) {
                showRateReviewDialog("SUBMIT");
            }


            // To dosplay refund policty message/link.
//            if (mBrowsableCourse.getRefundApplicable() == Flinnt.TRUE) {
//                if (mBrowsableCourse.getCanRefund() == Flinnt.TRUE) {
//                    if (mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
//                        mRefunPolicyTxt.setTextColor(Color.parseColor("#3b6d95"));
//                        SpannableString content = new SpannableString(getResources().getString(R.string.refund_course_txt));
//                        content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.refund_course_txt).length(), 0);
//                        mRefunPolicyTxt.setText(content);
//                    } else {
//                        mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
//                    }
//                } else {
//                    if (!mBrowsableCourse.getRefundMessage().equalsIgnoreCase("")) {
//                        mRefunPolicyTxt.setText(Html.fromHtml(mBrowsableCourse.getRefundMessage()));
//                    } else {
//                        mRefunPolicyTxt.setVisibility(View.GONE);
//                    }
//                }
//            } else {
//                mRefunPolicyTxt.setVisibility(View.GONE);
//            }


    }

    /**
     * Update and display course list
     *
     * @param mContentsResponse cource course list response
     */
    public void updateContentsList(ContentsResponse mContentsResponse) {
        try {
            if (mContentsResponse.getData().getList().size() > 0) {
                LogWriter.write("Current Count: " + mContentsResponse.getData().getList().size());
                mContentsDisplayAdapter.addItems(mContentsResponse.getData().getList(), mContentsResponse, 3, 2);

                if (mContentsResponse.getData().getList().size() > 0) {
                    mContentLineTxt.setVisibility(View.VISIBLE);
                    mTotalContentsTxt.setText(mContentsResponse.getData().getSectionCount() + " " + getResources().getString(R.string.topics_text) + " " + mContentsResponse.getData().getContentCount() + " " + getResources().getString(R.string.content_text));
                    mTotalContentsTxt.setVisibility(View.VISIBLE);
                    mContainsLinear.setVisibility(View.VISIBLE);
                    mViewMoreTxt.setVisibility(View.VISIBLE);
                } else {
                    mContentLineTxt.setVisibility(View.GONE);
                    mTotalContentsTxt.setVisibility(View.GONE);
                    mContainsLinear.setVisibility(View.GONE);
                    mViewMoreTxt.setVisibility(View.GONE);
                }

            } else if (mContentsDisplayAdapter.getItemCount() < Flinnt.TRUE) {
                mContainsLinear.setVisibility(View.GONE);
                mViewMoreTxt.setVisibility(View.GONE);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mContentsDisplayAdapter.getItemCount());

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Strip HTML tags to readable string while keeping new lines.
     *
     * @param htmlDesc HTML input string
     * @return stripped string
     */
    private String getStrippedString(String htmlDesc) {
        String newLined = htmlDesc.replaceAll("<br\\s*[\\/]?>", "\n"); // converts html new lines to java new lines
        String stripped = newLined.replaceAll("<[^>]*>", ""); // removes all html tags
        return stripped.trim();
    }

    /**
     * Displays the rate/review dialog
     *
     * @param positiveButtonText Operation button text
     */
    private void showRateReviewDialog(String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("Review by " + Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME))
                .setPositiveButton(positiveButtonText, null)
                .setView(R.layout.rating_dialog_design);
        final AlertDialog inviteDialog = builder.create();
        inviteDialog.show();

        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) inviteDialog.findViewById(R.id.rb_rate);
        final EditText etReview = (EditText) inviteDialog.findViewById(R.id.et_review);
        Button btnSubmit = inviteDialog.getButton(DialogInterface.BUTTON_POSITIVE);

//        if (null != mBrowsableCourse.getUserReview()) {
//            ratingBar.setRating(Float.parseFloat(mBrowsableCourse.getUserReview().getReviewRating()));
//            etReview.setText(mBrowsableCourse.getUserReview().getReviewText());
//        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
                } else {
                    if (ratingBar.getRating() == 0) {
                        Helper.showToast(getString(R.string.give_your_rating), Toast.LENGTH_LONG);
                    } else {
                        CourseReviewWrite mCourseReviewWrite = new CourseReviewWrite(mHandler, courseId);
                        mCourseReviewWrite.setRatings(String.valueOf(ratingBar.getRating()));
                        mCourseReviewWrite.setReview(etReview.getText().toString());
                        mCourseReviewWrite.sendCourseWriteRequest();
                        inviteDialog.dismiss();
                        startProgressDialog();
                    }
                }
            }
        });
    }

    /**
     * Starts a new Activity for selected course
     *
     * @param courseItem selected course
     */
    private void startNewActivity(BrowsableCourse courseItem) {
        if (Helper.isConnected()) {
            Intent courseDescriptionIntent = new Intent(BrowseCourseDetailActivityNew.this, BrowseCourseDetailActivityNew.class);
            courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseItem.getId());
            courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getPicture());
            courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getName());
            courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, courseItem.getInstituteName());
            courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, courseItem.getRatings());
            courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, courseItem.getUserCount());
            courseDescriptionIntent.putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses);
            startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
        } else {
            Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
        }
    }

    /**
     * Initialize UI views and their visibility
     */
    private void findAndInitializeViews() {
        mUsersCourseLinear = (LinearLayout) findViewById(R.id.course_buyer_linear);

        btnAddCart = (Button)findViewById(R.id.btn_add_cart);
        btnAddCart.setEnabled(false);
        btnAddCart.setAlpha(0.5f);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBuyerRecycler = (RecyclerView) findViewById(R.id.course_buyer_recycler);
        mBuyerRecycler.setLayoutManager(layoutManager);

        mRVProductAttributeList = (RecyclerView)findViewById(R.id.rv_product_detail_list);

        mTotalRattingsTxt = (TextView) findViewById(R.id.total_rattings_txt);
        mAuthorRvList = (RecyclerView)findViewById(R.id.rv_authorlist);
        mTotalContentsTxt = (TextView) findViewById(R.id.total_content_txt);
        mContentRecycle = (RecyclerView) findViewById(R.id.content_recycle);
        mContentRecycle.setNestedScrollingEnabled(false);
        mYoutubeFrame = (FrameLayout) findViewById(R.id.youtube_frame);
        postYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        mMediaOpenImgbtn = (ImageButton) findViewById(R.id.post_media_open_btn_postdetailview);
        mMediaOpenImgbtn.setOnClickListener(this);
        mMediaThumnailImg = (ImageView) findViewById(R.id.post_media_thumbnail_postdetailview);
        mMediaThumnailImg.setOnClickListener(this);
        mPostMediaRelative = (RelativeLayout) findViewById(R.id.post_media_relative_postdetailview);
        mCoursePictureImg = (NetworkImageView) findViewById(R.id.iv_course_picture);
        mCourseNameTxt = (TextView) findViewById(R.id.tv_course_name);
        mInstituteNameTxt = (TextView) findViewById(R.id.tv_institute_name);
        mDatetimeTxt = (TextView) findViewById(R.id.date_time_txt);
        mAddressTxt = (TextView) findViewById(R.id.address_txt);
        mCourseDescriptionTxt = (TextView) findViewById(R.id.course_description_txt);
        mMoreCoursesFromInstituteTxt = (TextView) findViewById(R.id.courses_from_institute_txt);
        mBuyTxt = (TextView) findViewById(R.id.buy_txt);
        mBuyTxt.setOnClickListener(this);
        mUnableToJoinTxt = (TextView) findViewById(R.id.unabletojoin_txt);
        mReadMoreLinear = (LinearLayout) findViewById(R.id.read_more_linear);

        mBottomSheetLinear = (LinearLayout) findViewById(R.id.bottom_sheet_linear);

        mDeviderView = (View) findViewById(R.id.deviderView);

        mReadMoreTxt = (TextView) findViewById(R.id.read_more_txt);
        mReadMoreTxt.setOnClickListener(this);
        mViewMoreTxt = (TextView) findViewById(R.id.view_more_txt);
        mViewMoreTxt.setOnClickListener(this);
        mContentLineTxt = (View) findViewById(R.id.content_lint_txt);

        mAllReviewsTxt = (TextView) findViewById(R.id.all_reviews_txt);
        mAllReviewsTxt.setOnClickListener(this);


        mSubscriptionBtn = (Button) findViewById(R.id.join_remove_course_btn);
        mSubscriptionBtn.setOnClickListener(this);
        mRefunPolicyTxt = (TextView) findViewById(R.id.refund_policy_txt);
        mRefunPolicyTxt.setOnClickListener(this);
        mSubscriptionBtn.setLongClickable(false);
        mMainContentLayout = (LinearLayout) findViewById(R.id.layout_main_content);
        mContainsLinear = (LinearLayout) findViewById(R.id.contains_linear);
        mRatingLinear = (LinearLayout) findViewById(R.id.rating_linear);
        mRatingLinear.setOnClickListener(this);
        mReviewCourseLinear = (LinearLayout) findViewById(R.id.layout_rate_course);
        mReviewCourseLinear.setOnClickListener(this);
        mReviewsBaseLinear = (LinearLayout) findViewById(review_base_linear);
        mReviewsBaseLinear.setVisibility(View.GONE);
        // mReviewsBaseLinear.setOnClickListener(this);
        mJoinedCoursesLinear = (LinearLayout) findViewById(R.id.also_joined_courses_linear);
        mInstituteCoursesLinear = (LinearLayout) findViewById(R.id.institute_courses_linear);
        mDateTimeAddressLinear = (LinearLayout) findViewById(R.id.datetime_address_linear);
        mDescriptionLinear = (LinearLayout) findViewById(R.id.description_linear);
        mSpaceTxt = (TextView) findViewById(R.id.spaceTxt);

        findViewById(R.id.layout_rating_0).setVisibility(View.GONE);
        findViewById(R.id.layout_rating_1).setVisibility(View.GONE);
        findViewById(R.id.layout_rating_2).setVisibility(View.GONE);

        mJoinedCoursesRecycler = (RecyclerView) findViewById(R.id.rv_recommended_courses);
        mJoinedCoursesRecycler.setHasFixedSize(true);
        mJoinedCoursesRecycler.setNestedScrollingEnabled(false);
        mInstituteCoursesRecycler = (RecyclerView) findViewById(R.id.institute_courses_recycler);
        mInstituteCoursesRecycler.setHasFixedSize(true);
        mInstituteCoursesRecycler.setNestedScrollingEnabled(false);

        mCustomNestedScrollView = (CustomNestedScrollView) findViewById(R.id.customNestedScrollView);
        final Rect scrollBounds = new Rect();
        mCustomNestedScrollView.getHitRect(scrollBounds);

        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);

        // set animation listener
        animFadein.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);

        mCourseRatingBar = (AppCompatRatingBar) findViewById(R.id.course_rating);
        mCourseRatingBar.setOnClickListener(this);
        mImageLoader = Requester.getInstance().getImageLoader();

        mFinalPriceTxt = (TextView) findViewById(R.id.final_price_txt);
        mOldPriceTxt = (TextView) findViewById(R.id.old_price_txt);

        mFPriceTxt = (TextView) findViewById(R.id.fPriceTxt);
        mOPriceTxt = (TextView) findViewById(R.id.oPriceTxt);

        mTempTxt = (TextView) findViewById(R.id.temp_txt);


        fl_badge_containers = (FrameLayout)findViewById(R.id.fl_badge_containers);
        textCartItemCount = (TextView)findViewById(R.id.totalBadgeTxt1);


        if(BrowseCoursesFragmentNew.cart_count > 0){
            textCartItemCount.setVisibility(View.VISIBLE);
            textCartItemCount.setText(String.valueOf(BrowseCoursesFragmentNew.cart_count));
        }else{
            textCartItemCount.setVisibility(View.GONE);
            textCartItemCount.setText(String.valueOf(BrowseCoursesFragmentNew.cart_count));
        }

        findViewById(R.id.cart_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(BrowseCourseDetailActivityNew.this,ShoppingCartActivity.class);
                startActivityForResult(i, 101);
               // onOptionsItemSelected(menuItem);
            }
        });
        setupBadge();
    }

    private void refreshView() {
        try {
            mContentsDisplayAdapter = new ContentsDisplayAdapter(BrowseCourseDetailActivityNew.this, courseId, courseName, mSectionList);
            GridLayoutManager manager = new GridLayoutManager(BrowseCourseDetailActivityNew.this, 1);
            mContentRecycle.setLayoutManager(manager);
            mContentsDisplayAdapter.setLayoutManager(manager);
            mContentRecycle.setAdapter(mContentsDisplayAdapter);

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private boolean isViewVisible(View view) {
        return false;
    }

    /**
     * Initialize lists and their adapters
     */
    private void resetListsAndAdapters() {
       // mJoinedCoursesList = new ArrayList<>();
        mRelatedBookList = new ArrayList<>();
        mJoinedCoursesAdapter = new SuggestedCoursesAdapterNew(mRelatedBookList,0);

        mJoinedCoursesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mJoinedCoursesRecycler.setAdapter(mJoinedCoursesAdapter);

        mRelatedVendorList = new ArrayList<>();
        mInstituteCoursesAdapter = new SuggestedCoursesAdapterNew(mInstituteCourseList,1);
        mInstituteCoursesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mInstituteCoursesRecycler.setAdapter(mInstituteCoursesAdapter);
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//
//            case R.id.wishlist_menu:
////                if (mBrowsableCourse != null) {
////                    new WishList(mHandler, courseId, mBrowsableCourse.getIsWishList()).sendWishListRequest();
////                    startProgressDialog();
////                }
//                return true;
//
//            case R.id.share_menu:
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl/* + "\n\n" + Html.fromHtml(mBrowsableCourse.getDescription())*/);
//                shareIntent.setType("text/plain");
//                startActivity(shareIntent);
//                return true;
////            case R.id.cart_menu:
////
////                break;
//
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.COURSE_DESCRIPTION + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseId);
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
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(this))
                mProgressDialog.show();
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
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * Display dialog to join course by course code
     */
    private void sendRequestDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseCourseDetailActivityNew.this);
            // set view
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_send_request, null);
            alertDialogBuilder.setView(dialogView);

            TextView mTitleTxt = new TextView(this);
            mTitleTxt.setText(R.string.send_request_title);
            mTitleTxt.setPadding(40, 40, 40, 40);
            mTitleTxt.setGravity(Gravity.CENTER_VERTICAL);
            mTitleTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
            mTitleTxt.setTextSize(20);
            mTitleTxt.setTypeface(Typeface.DEFAULT_BOLD);
            alertDialogBuilder.setCustomTitle(mTitleTxt);

            TextView mDialogNoteTxt = (TextView) dialogView.findViewById(R.id.dialog_note_text);
            final String noteStr = "<font color=#ff0000><b> Important:</b> Incomplete profile may lead to your request being rejected</font> <font color=#007DCD><u>Click here</u></font> <font color=#ff0000>to update your profile</font>";
            final EditText mRequestEdt = (EditText) dialogView.findViewById(R.id.request_edit);

            mRequestEdt.setTypeface(Typeface.DEFAULT);
            alertDialogBuilder.setCancelable(false);

            mDialogNoteTxt.setText(Html.fromHtml(noteStr));


            alertDialogBuilder.setPositiveButton("SEND REQUEST", new DialogInterface.OnClickListener() {
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
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            mDialogNoteTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    Intent profileIntent = new Intent(BrowseCourseDetailActivityNew.this, ProfileActivity.class)
                            .putExtra(LoginResponse.USER_PICTURE_URL_KEY,Config.getStringValue(Config.PROFILE_URL))
                            .putExtra(LoginResponse.USER_PICTURE_KEY,Config.getStringValue(Config.PROFILE_NAME));
                    startActivityForResult(profileIntent, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);
                }
            });
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String notesStr = mRequestEdt.getText().toString();
                    if (TextUtils.isEmpty(notesStr)) {
                        Helper.showAlertMessage(BrowseCourseDetailActivityNew.this, "Course Request", getResources().getString(R.string.course_request_invalide), "CLOSE");
                    } else {
                        if (Helper.isConnected()) {
                            alertDialog.dismiss();
                            new CourseRequest(mHandler, courseId, notesStr).sendCourseSendRequest();
                            startProgressDialog();
                        } else {
                            Helper.showNetworkAlertMessage(BrowseCourseDetailActivityNew.this);
                        }
                    }
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
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

    /*
    * Request Already sent Error dialog
    */
    public void showSendRequestError(String title, String errorMsg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set mResorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView mTitleTxt = new TextView(this);
        // You Can Customise your Title here
        mTitleTxt.setText(title);
        mTitleTxt.setPadding(40, 40, 40, 0);
        mTitleTxt.setGravity(Gravity.CENTER_VERTICAL);
        mTitleTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
        mTitleTxt.setTextSize(20);
        mTitleTxt.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(mTitleTxt);
        // set dialog message
        alertDialogBuilder.setMessage(errorMsg);
        alertDialogBuilder.setPositiveButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
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

    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    private void isAppNotInstalled() {
        mYoutubeFrame.setVisibility(View.GONE);
        mYoutubeFrame.setVisibility(View.GONE);
        int defaultDrawable = R.drawable.youtube_video_fram_not_get;
        mMediaOpenImgbtn.setVisibility(View.VISIBLE);
        String url = "http://img.youtube.com/vi/" + postYoutubeVedioID + "/0.jpg";
        mImageLoader.get(url,ImageLoader.getImageListener(mMediaThumnailImg, defaultDrawable, defaultDrawable));
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!b) {
            postYouTubePlayer = youTubePlayer;
            if (seekTime > 0) {
                postYouTubePlayer.cueVideo(postYoutubeVedioID, seekTime);
            } else {
                postYouTubePlayer.cueVideo(postYoutubeVedioID);
                postYouTubePlayer.loadVideo(postYoutubeVedioID);
            }

            postYouTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean _isFullScreen) {
                    postFullScreen = _isFullScreen;
                    if (!postFullScreen) {
                        mCustomNestedScrollView.scrollTo(0, 0);
                    }
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        isAppNotInstalled();
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
            toolbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPlaying() {
            toolbar.setVisibility(View.GONE);
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
            toolbar.setVisibility(View.VISIBLE);
        }

    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//      //  getMenuInflater().inflate(R.menu.browse_course_details_menu, menu);
//      //  updatePostMenu(menu);
//       //final MenuItem menuItem = menu.findItem(R.id.cart_menu);
//        //SearchView actionView = (SearchView) menuItem.getActionView();
//        //View actionView = MenuItemCompat.getActionView(menuItem);
//
//        return true;
//    }

//    private void updatePostMenu(Menu menu) {
//        MenuItem mWishlistItem = menu.findItem(R.id.wishlist_menu);
//        MenuItem mShareItem = menu.findItem(R.id.share_menu);
//        if (mWishListResponse != null) {
//            if (mWishListResponse.getIsWishList() == Flinnt.TRUE) {
//                mWishlistItem.setTitle(getResources().getString(R.string.wishlisted_text));
//            }
//        }
//        if (mBrowsableCourse != null) {
//            if (mBrowsableCourse.getIsWishList() == Flinnt.TRUE) {
//                mWishlistItem.setTitle(getResources().getString(R.string.wishlisted_text));
//            }
//        }
//        if (isWishlist) {
//            mWishlistItem.setVisible(true);
//        } else {
//            mWishlistItem.setVisible(false);
//        }
//    }

    public void showOverflowPrompt(View view) {
        final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText(getResources().getString(R.string.share_title_msg))
//                .setSecondaryText("overflow_prompt_description")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.tap_target_menu_max_width)
                .setIcon(R.drawable.moreblack);
        final Toolbar tb = (Toolbar) this.findViewById(R.id.profile_main_toolbar);
        final View child = tb.getChildAt(2);
        if (child instanceof ActionMenuView) {
            final ActionMenuView actionMenuView = ((ActionMenuView) child);
            tapTargetPromptBuilder.setTarget(actionMenuView.getChildAt(actionMenuView.getChildCount() - 1));
        }
        tapTargetPromptBuilder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postYoutubeVedioID != null) {
            postYouTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, BrowseCourseDetailActivityNew.this);
        }


        mBrowseCourses = new BrowseCoursesNew(mHandler);
        mBrowseCourses.setSearchString("");
        mBrowseCourses.sendBrowseCoursesRequest();


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (postYoutubeVedioID != null) {
                seekTime = postYouTubePlayer.getCurrentTimeMillis() + 100;
                postYouTubePlayer.release();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }


    public void updateCartCount(StoreModelResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            BrowseCoursesFragmentNew.cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
            setupBadge();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

}