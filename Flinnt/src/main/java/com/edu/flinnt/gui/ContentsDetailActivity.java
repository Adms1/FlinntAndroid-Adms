package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentAddComment;
import com.edu.flinnt.core.ContentComments;
import com.edu.flinnt.core.ContentDeleteComment;
import com.edu.flinnt.core.ContentViewStatistics;
import com.edu.flinnt.core.ContentsDetails;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.LikeUnlikeContent;
import com.edu.flinnt.core.PostViewStatistics;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.ContentDetailsInterface;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.downloadsmultithread.DownloadInfo;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.ContentAddCommentResponse;
import com.edu.flinnt.protocol.ContentComment;
import com.edu.flinnt.protocol.ContentCommentsRequest;
import com.edu.flinnt.protocol.ContentCommentsResponse;
import com.edu.flinnt.protocol.ContentDeleteCommentResponse;
import com.edu.flinnt.protocol.ContentViewStatisticsResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.ErrorResponse;
import com.edu.flinnt.protocol.LikeUnlikeRequest;
import com.edu.flinnt.protocol.LikeUnlikeResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostViewStatisticsResponse;
import com.edu.flinnt.protocol.contentdetails.ContentDetailsResponse;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadFileManager;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.DownloadUtils;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyTagHandler;
import com.edu.flinnt.util.RoundedImageView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GUI class to show post details
 * Content Details Activity when user clicks on Content List item.
 */
public class ContentsDetailActivity extends AppCompatActivity implements OnClickListener, /*OnCancelListener,*/  YouTubePlayer.OnInitializedListener {

    private Toolbar toolbar;
    private TextView endDuration, contentDescriptionTxt, readMoreTxt, contentTotalViewsTxtview, contentTotalLikesTxtview, contentTotalCommentsTxtview, postLinkTextview, contentTitleTxt;
    private EditText commentTxt;
    private ImageButton mediaOpenImgbtn, audioPlayButton;
    private FloatingActionButton likeContentImgbtn;
    private ImageView sendComment, mediaThumnailImgview, contentViewMoreDetailsViewersImgview;
    private LinearLayout peoplePhotosLinearLayout, addCommentLayout, postMediaAudioLayout, contentDescriptionLinear;
    private RelativeLayout postMediaRelative;
    private ProgressBar audioProgressBar;
    private Thread audioThread;
    private Chronometer currentDuration;
    private long baseChronometer = 0;
    private RecyclerView contentCommentsRecycleview;
    private ContentComments mContentComments;
    private ContentCommentsRequest mContentCommentsRequest;
    private ContentCommentsResponse mContentCommentsResponse = null;
    private ContentCommentsAdapter mContentCommentsAdapter;
    private ArrayList<ContentComment> mCommentItems = new ArrayList<ContentComment>();
    //private CommentEndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListenerComments;
    public Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private ContentDetailsResponse mContentsDetailsResponse = null;
    private ContentViewStatisticsResponse mContentViewStatisticsResponse = ContentViewStatistics.getLastResponse();
    private LikeUnlikeRequest mLikeUnlikeRequest;
    private LikeUnlikeContent mLikeUnlikeContent;
    private ImageLoader mImageLoader;
    private String mCourseId = "", mContentID = "", courseName = "", coursePicture = "", coursePictureUrl = "", courseAllowedRole = "";
    private int isFromNotification = Flinnt.FALSE;
    Common mCommon;
    DownloadMediaFile mDownload;
    private ContentDetailsInterface mContentDetailsInterface;
    //private InteractiveScrollView mScrollView;
    private boolean isNeedToGetComment = false;
    private boolean isPostDeleted = false;
    private boolean isCourseRemoved = false;
    private boolean isCourseDeletedDialogShowing;

    public static final String COURSE_ID_KEY = "course_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String CONTENT_FROM_NOTIFICATION_KEY = "isFromNotification";

    public static final String USER_PICTURE_URL_KEY = "user_picture_url";
    public static final String TOTAL_LIKES_KEY = "total_likes";
    public static final String TOTAL_COMMENTS_KEY = "total_comments";
    public static final String TOTAL_VIEWERS_KEY = "total_viewers";

    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    LinearLayoutManager mylinearLayoutManager;
    /**
     * Content downloading declaration.
     */

    private DownloadReceiver mReceiver;
    private FrameLayout contentFrame;
    private FrameLayout youtubeFrame;
    private ImageView contentDownloadBtn;
    private com.github.lzyzsd.circleprogress.DonutProgress contentProgressBar;
    private ProgressBar contentProgressBarHint;
    private YouTubePlayerFragment contentYouTubePlayerFragment;
    private YouTubePlayer contentYouTubePlayer;
    private String contentYoutubeVedioID;
    private boolean contentFullScreen;

    //*****change 31
    String comeFrom = "";
    LinearLayout commentLinLayout, likeShareLinLayout;
    View dividerLine0, dividerLine1, dividerLine2;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    int firstVisibleItem, visibleItemCount, totalItemCount, pastVisiblesItems;
    NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.content_detailview);

        //Log.d("Contt","onCreate() ContentDetailActivity.java");

        mNestedScrollView = (NestedScrollView) findViewById(R.id.scrollview_detailview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            //Log.d("Contt","in bundle ");
            if (bundle.containsKey(COURSE_ID_KEY))
                mCourseId = bundle.getString(COURSE_ID_KEY);
            if (bundle.containsKey(CONTENT_ID_KEY))
                mContentID = bundle.getString(CONTENT_ID_KEY);
            if (bundle.containsKey(CONTENT_FROM_NOTIFICATION_KEY))
                isFromNotification = bundle.getInt(CONTENT_FROM_NOTIFICATION_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                courseName = bundle.getString(Course.COURSE_NAME_KEY);
            if (bundle.containsKey("comeFrom"))
                comeFrom = bundle.getString("comeFrom");

            if (isFromNotification == Flinnt.TRUE && bundle.containsKey(Config.USER_ID)) {
                String userId = bundle.getString(Config.USER_ID);

                coursePictureUrl = bundle.getString(Course.COURSE_PICTURE_URL_KEY);
                coursePicture = bundle.getString(Course.USER_PICTURE_KEY);
                courseName = bundle.getString(Course.COURSE_NAME_KEY);

                if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userId);
                }
            }
        }


        mImageLoader = Requester.getInstance().getImageLoader();
        contentTitleTxt = (TextView) findViewById(R.id.contentTitleTxt);
        contentDescriptionTxt = (TextView) findViewById(R.id.content_description_txt);
        readMoreTxt = (TextView) findViewById(R.id.readMoreTxt);
        readMoreTxt.setOnClickListener(this);

        contentTotalViewsTxtview = (TextView) findViewById(R.id.content_totle_views_txt_contentdetailview);
        contentTotalLikesTxtview = (TextView) findViewById(R.id.content_totle_likes_txt_contentdetailview);
        contentTotalCommentsTxtview = (TextView) findViewById(R.id.content_totle_comments_txt_contentdetailview);
        postLinkTextview = (TextView) findViewById(R.id.content_link_textview);

        commentTxt = (EditText) findViewById(R.id.content_add_comment_edittext_contentdetailview);
        sendComment = (ImageView) findViewById(R.id.content_send_comment_contentdetailview);
        sendComment.setOnClickListener(this);

        mediaOpenImgbtn = (ImageButton) findViewById(R.id.content_media_open_btn_contentdetailview);
        likeContentImgbtn = (FloatingActionButton) findViewById(R.id.content_like_contentdetailview);
        mediaOpenImgbtn.setOnClickListener(this);
        likeContentImgbtn.setOnClickListener(this);

        contentFrame = (FrameLayout) findViewById(R.id.contentFrame);
        youtubeFrame = (FrameLayout) findViewById(R.id.youtube_frame);
        contentDownloadBtn = (ImageView) findViewById(R.id.contentDownloadBtn);
        contentProgressBar = (com.github.lzyzsd.circleprogress.DonutProgress) findViewById(R.id.contentProgressBar);
        contentProgressBar.setTextColor(R.color.gray);
        contentProgressBar.setTextSize((float) 5.0);
        contentProgressBarHint = (ProgressBar) findViewById(R.id.contentProgressBarHint);
        contentFrame.setOnClickListener(this);

        mediaThumnailImgview = (ImageView) findViewById(R.id.content_media_thumbnail_contentdetailview);
        contentViewMoreDetailsViewersImgview = (ImageView) findViewById(R.id.content_people_view_details_contentdetailview);
        mediaThumnailImgview.setOnClickListener(this);
        contentViewMoreDetailsViewersImgview.setOnClickListener(this);

        commentLinLayout = (LinearLayout) findViewById(R.id.comment_linear_layout);
        likeShareLinLayout = (LinearLayout) findViewById(R.id.like_share_linear_layout);
        dividerLine0 = (View) findViewById(R.id.divider_0);
        dividerLine1 = (View) findViewById(R.id.divider_1);
        dividerLine2 = (View) findViewById(R.id.divider_2);
        peoplePhotosLinearLayout = (LinearLayout) findViewById(R.id.content_people_thumbnail_horizontalscroll_contentdetailview);
        addCommentLayout = (LinearLayout) findViewById(R.id.linear_add_comment_postdetailview);

        postMediaRelative = (RelativeLayout) findViewById(R.id.content_media_relative_contentdetailview);
        postMediaAudioLayout = (LinearLayout) findViewById(R.id.content_media_audio_view_contentdetailview);
        contentDescriptionLinear = (LinearLayout) findViewById(R.id.content_description_linear);
        audioPlayButton = (ImageButton) findViewById(R.id.content_audio_play_contentdetailview);
        audioPlayButton.setOnClickListener(this);
        audioProgressBar = (ProgressBar) findViewById(R.id.audio_progressBar);
        audioProgressBar.getProgressDrawable().setColorFilter(Color.GRAY, Mode.SRC_IN);
        currentDuration = (Chronometer) findViewById(R.id.audio_current_duration_text);
        endDuration = (TextView) findViewById(R.id.audio_end_duration_text);

        contentCommentsRecycleview = (RecyclerView) findViewById(R.id.content_people_comments_recycler_view);


        //mScrollView = (InteractiveScrollView) findViewById(R.id.scrollview_detailview);

        //mScrollView.setOnBottomReachedListener(this);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("handleMessage : " + message.what);

                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        //stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        //Log.d("Contt", "ContentsDetailActiity : " + "SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof ContentDetailsResponse) {

                            mContentsDetailsResponse = (ContentDetailsResponse) message.obj;
                            if (mContentsDetailsResponse != null) {
                                updatePostData();
                            }
                        } else if (message.obj instanceof ContentViewStatisticsResponse) {
                            mContentViewStatisticsResponse = (ContentViewStatisticsResponse) message.obj;
                            updateViewersData();
                        } else if (message.obj instanceof ContentCommentsResponse) {
                            loading = true;
                            mContentCommentsResponse = (ContentCommentsResponse) message.obj;
                            updateCommentsList((ContentCommentsResponse) message.obj);
                        } else if (message.obj instanceof ContentAddCommentResponse) {
                            ContentAddCommentResponse mContentAddCommentResponse = (ContentAddCommentResponse) message.obj;
                            if (mContentAddCommentResponse.getData().getAdded() == Flinnt.TRUE) {
                                commentTxt.setText("");
                                if (mContentAddCommentResponse.getData().getShowComment() == Flinnt.TRUE) {
                                    showCommentToUser(mContentAddCommentResponse);
                                } else {
                                    Helper.showAlertMessage(ContentsDetailActivity.this, getString(R.string.success), getString(R.string.comment_sent), getString(R.string.close_txt));
                                }
                            }
                        } else if (message.obj instanceof LikeUnlikeResponse) {
                            stopProgressDialog();
                            updateLikeCommentButtons((LikeUnlikeResponse) message.obj);
                        }

                        break;

                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());

                        //Log.d("Contt", "ContentsDetailActiity : FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        BaseResponse response = null;
                        if (message.obj instanceof ErrorResponse) {
                            response = new BaseResponse();
                            response.errorResponse = (ErrorResponse) message.obj;
                        } else {
                            response = ((BaseResponse) message.obj);
                        }
                        if (response.errorResponse != null) {
                            String errorMessage = response.errorResponse.getMessage();
                            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                if (error.getCode() == ErrorCodes.ERROR_CODE_5) {
                                    Helper.showToast(getResources().getString(R.string.content_delteted_message), Toast.LENGTH_SHORT);

                                    onBackPressed();
                                    return;
                                } else if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
//                                    isCourseRemoved = true;
                                    showContentDeletedDialog(errorMessage);
                                    return;
                                }
                            }
                            Helper.showAlertMessage(ContentsDetailActivity.this, "Error", errorMessage, getString(R.string.close_txt));
                        }
                        break;

                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        //stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");

                        //Log.d("Contt","ContentsDetailActivity : download complete");

                        if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_AUDIO) {
                            startAudioIntent();
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

        mContentDetailsInterface = ContentDetailsInterface.getInstance();
        mContentsDetailsResponse = mContentDetailsInterface.getContentDetailsData(mContentID, Config.getStringValue(Config.USER_ID));
        if (mContentsDetailsResponse != null) {
            updatePostData();
        }

        if (!Helper.isConnected())
            return;

        //*****change 31
        if (comeFrom.equals("BrowseCourseDescriptionActivity")) {
            commentLinLayout.setVisibility(View.GONE);
            likeShareLinLayout.setVisibility(View.GONE);
            dividerLine0.setVisibility(View.GONE);
            dividerLine1.setVisibility(View.GONE);
            dividerLine2.setVisibility(View.GONE);
            startProgressDialog();
            new ContentsDetails(mHandler, mCourseId, mContentID, "1").sendContentsDetailsRequest();
        } else {
            startProgressDialog();
            new ContentsDetails(mHandler, mCourseId, mContentID, "").sendContentsDetailsRequest();
            new ContentViewStatistics(mHandler, mCourseId, mContentID, 6).sendContentViewStatisticsRequest();
            updateCommentsData();
        }

    }

    public void showContentDeletedDialog(String errorMessage) {
        if (!isCourseDeletedDialogShowing) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
//                    onBackPressed();
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


    /**
     * Displays the number of user who visited
     */
    private void updateViewersData() {
        contentTotalViewsTxtview.setText(mContentViewStatisticsResponse.getData().getTotalViews() + PostViewStatistics.VIEWERS);
        contentTotalLikesTxtview.setText(mContentViewStatisticsResponse.getData().getTotalLikes() + PostViewStatistics.LIKES);
        contentTotalCommentsTxtview.setText(mContentViewStatisticsResponse.getData().getTotalComments() + PostViewStatistics.COMMENTS);

        ArrayList<ContentViewStatisticsResponse.Viewers> viewersArray = mContentViewStatisticsResponse.getData().getViewers();

        peoplePhotosLinearLayout.removeAllViews();
        for (int i = 0; i < viewersArray.size(); i++) {
            ContentViewStatisticsResponse.Viewers viewer = viewersArray.get(i);

            String url = mContentViewStatisticsResponse.getData().getUserPictureUrl() + Flinnt.PROFILE_MEDIUM + File.separator + viewer.getUserPicture();

            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams lp_ineer_ver = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(5, 5, 5, 5);
            linearLayout.setLayoutParams(lp_ineer_ver);

            final RoundedImageView addImage = new RoundedImageView(this);
            addImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isConnected()) {
                        Intent viewers = new Intent(ContentsDetailActivity.this, PostViewersSendMessageActivity.class);
                        viewers.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        viewers.putExtra(Post.POST_ID_KEY, mContentID);
                        viewers.putExtra(Course.COURSE_NAME_KEY, courseName);
                        viewers.putExtra("comeFrom", "Content");
                        viewers.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mContentViewStatisticsResponse.getData().getUserPictureUrl());
                        startActivity(viewers);
                    } else {
                        Helper.showNetworkAlertMessage(ContentsDetailActivity.this);
                    }
                }
            });
            LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
                    Helper.getDip(40),
                    Helper.getDip(40));
            addImage.setLayoutParams(lpImage);
            linearLayout.addView(addImage);

            peoplePhotosLinearLayout.addView(linearLayout);

            mImageLoader.get(url, ImageLoader.getImageListener(addImage, R.drawable.default_viewers_image, R.drawable.default_user_profile_image));

        }
    }

    /**
     * Updates the post data
     */
    private void updatePostData() {
        try {
            invalidateOptionsMenu();
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(mContentsDetailsResponse.getContent().getSectionTitle());
            contentTitleTxt.setText(mContentsDetailsResponse.getContent().getTitle());

            if (mContentsDetailsResponse.getContent().getLikeStatus() == Flinnt.TRUE) {
                likeContentImgbtn.setImageResource(R.drawable.ic_is_liked);
            } else {
                likeContentImgbtn.setImageResource(R.drawable.post_detail_like);
            }
            if (!mContentsDetailsResponse.getContent().getDescription().equalsIgnoreCase("")) {
//                contentDescriptionTxt.setText(Html.fromHtml(getStrippedString(mContentsDetailsResponse.getContent().getDescription())), null, new MyTagHandler()));
                contentDescriptionTxt.setText(Html.fromHtml(mContentsDetailsResponse.getContent().getDescription(), null, new MyTagHandler()));
                contentDescriptionLinear.setVisibility(View.VISIBLE);
            } else {
                contentDescriptionLinear.setVisibility(View.GONE);
            }
            contentDescriptionTxt.setMovementMethod(LinkMovementMethod.getInstance());

            if (!comeFrom.equals("BrowseCourseDescriptionActivity")) {
                contentDescriptionTxt.setMaxLines(4);
                contentDescriptionTxt.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = contentDescriptionTxt.getLineCount();
                        if (LogWriter.isValidLevel(Log.DEBUG))
                            LogWriter.write("lineCount : " + lineCount);
                        readMoreTxt.setVisibility(lineCount > 4 ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                readMoreTxt.setVisibility(View.GONE);
            }


            postMediaUpdate();


            if (mContentsDetailsResponse.getAllowComment() == 1) {
                addCommentLayout.setVisibility(View.VISIBLE);
            } else {
                addCommentLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }


        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.CONTENT_DETAIL + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseId + "&content=" + mContentID);
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    boolean userScrolled = false;


    private void updateCommentsData() {

        if (null == mContentComments) {
            mContentCommentsRequest = new ContentCommentsRequest();
            mContentComments = new ContentComments(mHandler, mCourseId, mContentID);
            mContentComments.sendContentCommentsRequest(mContentCommentsRequest);
        }

        mylinearLayoutManager = new LinearLayoutManager(this);
        contentCommentsRecycleview.setLayoutManager(mylinearLayoutManager);
        contentCommentsRecycleview.setNestedScrollingEnabled(false);
        contentCommentsRecycleview.setHasFixedSize(false);
        mContentCommentsAdapter = new ContentCommentsAdapter(mCommentItems);
        contentCommentsRecycleview.setAdapter(mContentCommentsAdapter);


//@Nikhil loadmore addded issue 7710
//        contentCommentsRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                if (dy > 0) //check for scroll down
//                {
//                    visibleItemCount = mylinearLayoutManager.getChildCount();
//                    totalItemCount = mylinearLayoutManager.getItemCount();
//                    pastVisiblesItems = mylinearLayoutManager.findFirstVisibleItemPosition();
//
//                    if (loading) {
//                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//
//                            Toast.makeText(getApplicationContext(), "load more", Toast.LENGTH_SHORT).show();
//                            Log.d("TAGload", String.valueOf(mContentCommentsAdapter.getItemCount()));
//                            loading = false;
//                            if (mContentCommentsResponse.getData().getHasMore() > 0) {
//                                mContentCommentsRequest.setOffSet(mContentCommentsRequest.getOffSet() + mContentCommentsRequest.getMaxFetch());
//                                mContentComments.sendContentCommentsRequest(mContentCommentsRequest);
//                            }
//                        }
//                    }
//                }
//            }
//        });


        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                        visibleItemCount = mylinearLayoutManager.getChildCount();
                        totalItemCount = mylinearLayoutManager.getItemCount();
                        pastVisiblesItems = mylinearLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                //Toast.makeText(getApplicationContext(), "load more", Toast.LENGTH_SHORT).show();
                                //Log.d("TAGload", String.valueOf(mContentCommentsAdapter.getItemCount()));

                                loading = false;
                                if (mContentCommentsResponse.getData().getHasMore() > 0) {
                                    mContentCommentsRequest.setOffSet(mContentCommentsRequest.getOffSet() + mContentCommentsRequest.getMaxFetch());
                                    mContentComments.sendContentCommentsRequest(mContentCommentsRequest);
                                }
                            }
                        }
                    }
                }
            }
        });


        // contentCommentsRecycleview.addOnScrollListener(mEndlessRecyclerOnScrollListenerComments);

        mContentCommentsAdapter.setOnItemLongClickListener(new ContentCommentsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mContentCommentsAdapter.getItem(position) != null) {
                    if (mContentCommentsAdapter.getItem(position).getCanDelete() == Flinnt.TRUE) {
                        showDeleteCommentWarning(ContentsDetailActivity.this,
                                mContentCommentsAdapter, mContentCommentsAdapter.getItem(position));
                    }

                }

            }
        });
    }

    /**
     * Shows confirmation dialog to delete comment
     *
     * @param context  current activity
     * @param mAdapter comments mContentsAdapter
     * @param comment  comment object
     */
    public void showDeleteCommentWarning(final Context context, final ContentCommentsAdapter mAdapter, final ContentComment comment) {

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
                        Helper.showToast("Successfully deleted", Toast.LENGTH_SHORT);
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((ContentDeleteCommentResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(ContentsDetailActivity.this, "Error", ((ContentDeleteCommentResponse) message.obj).errorResponse.getMessage(), "CLOSE");
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
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText("Delete Comment");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    String commentID = comment.getCommentId();
                    new ContentDeleteComment(dialogHandler, commentID).sendDeleteCommentRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(ContentsDetailActivity.this);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * updates the current comments list
     *
     * @param contentCommentsResponse
     */
    private void updateCommentsList(ContentCommentsResponse contentCommentsResponse) {
        mContentCommentsAdapter.addItems(contentCommentsResponse.getData().getComments());
        isNeedToGetComment = false;
    }

    /**
     * Show comment to user
     *
     * @param mContentAddCommentResponse new comment response
     */
    protected void showCommentToUser(ContentAddCommentResponse mContentAddCommentResponse) {
        contentTotalCommentsTxtview.setText(mContentAddCommentResponse.getData().getCount() + PostViewStatistics.COMMENTS);

        ContentComment comment = new ContentComment();
        comment.setCommentId(String.valueOf(mContentAddCommentResponse.getData().getComment().getCommentId()));
        comment.setCommentText(String.valueOf(mContentAddCommentResponse.getData().getComment().getCommentText()));
        comment.setUserName(String.valueOf(mContentAddCommentResponse.getData().getComment().getUserName()));
        comment.setUserPicture(String.valueOf(mContentAddCommentResponse.getData().getComment().getUserPicture()));
        comment.setUserPictureUrl(String.valueOf(mContentAddCommentResponse.getData().getComment().getUserPictureUrl()));
        comment.setCommentDate(String.valueOf(mContentAddCommentResponse.getData().getComment().getCommentDate()));
        comment.setCanDelete(String.valueOf(mContentAddCommentResponse.getData().getComment().getCanDelete()));
        comment.setCommentUserId(String.valueOf(mContentAddCommentResponse.getData().getComment().getCommentUserId()));


        mContentCommentsAdapter.add(0/*mContentCommentsAdapter.getItemCount()*/, comment);
        contentCommentsRecycleview.scrollToPosition(0/*mPostCommentsAdapter.getItemCount() - 1*/);
        mContentCommentsRequest.setOffSet(mContentCommentsRequest.getOffSet() + 1);
        mContentCommentsAdapter.notifyDataSetChanged();

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
     * Post media attachments update
     */
    private void postMediaUpdate() {
        mediaOpenImgbtn.setVisibility(View.GONE);
        contentFrame.setVisibility(View.GONE);
        youtubeFrame.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mContentsDetailsResponse.getAttachmentUrl()) &&
                TextUtils.isEmpty(mContentsDetailsResponse.getContent().getAttachment())) {
            postMediaRelative.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            postLinkTextview.setVisibility(View.GONE);
        } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_LINK) {
            postMediaRelative.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            postLinkTextview.setVisibility(View.VISIBLE);
            postLinkTextview.setText(mContentsDetailsResponse.getContent().getAttachment());
        } else {
            postMediaRelative.setVisibility(View.VISIBLE);
            postLinkTextview.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            String fileExtention = Helper.getExtension(mContentsDetailsResponse.getContent().getAttachment());
            int defaultDrawable = Helper.getDefaultPostImageFromType(mContentsDetailsResponse.getContent().getAttachmentType(), fileExtention);

            if ( /*mContentsDetailsResponse.getPostContentTypeInt() == Flinnt.POST_CONTENT_AUDIO ||*/
                    mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO) {
                mediaOpenImgbtn.setVisibility(View.VISIBLE);
            }

            if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_AUDIO) {
                postMediaRelative.setVisibility(View.GONE);
                postMediaAudioLayout.setVisibility(View.VISIBLE);
            }

            if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO
                    && mContentsDetailsResponse.getContent().getAttachmentIsUrl() == 1) {

                contentYoutubeVedioID = null;
                String youtubeUrl = mContentsDetailsResponse.getContent().getAttachment();

                String packageName = "com.google.android.youtube";
                boolean isYoutubeInstalled = isAppInstalled(packageName);

                String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
                Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = compiledPattern.matcher(youtubeUrl);
                while (matcher.find()) {
                    System.out.println(matcher.group());
                    contentYoutubeVedioID = matcher.group(1);
                }

                if (isYoutubeInstalled) {
                    contentYouTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, this);
                    contentFrame.setVisibility(View.GONE);
                    mediaOpenImgbtn.setVisibility(View.GONE);
                    youtubeFrame.setVisibility(View.VISIBLE);
                } else {
                    isAppNotInstalled();
                }

            } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO) {
                mediaOpenImgbtn.setVisibility(View.GONE);
                youtubeFrame.setVisibility(View.GONE);

                Flinnt.appInfoDataSets.clear();

                String videourl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mContentsDetailsResponse.getContent().getId(), mContentsDetailsResponse.getContent().getAttachment(), videourl, videourl, mContentsDetailsResponse.getContent().getAttachmentDoEncode() + "");
                appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                if ((mContentsDetailsResponse.getContent().getAttachmentDoEncode() + "").equals(Flinnt.ENABLED)) {
                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntHiddenVideoPath());
                } else {
                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                }

                Flinnt.appInfoDataSets.add(appInfoDataSet);

                for (AppInfoDataSet info : Flinnt.appInfoDataSets) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                    if (downloadInfo != null) {
                        info.setProgress(downloadInfo.getProgress());
                        info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }
                }

                /**
                 *   VideoEncrptionCode (Add if condition)
                 */

                if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), "en" + mContentsDetailsResponse.getContent().getAttachment())) {
                    contentFrame.setVisibility(View.GONE);
                } else if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), Flinnt.appInfoDataSets.get(0).getName()) && Flinnt.appInfoDataSets.get(0).getStatusText().equals("Not Download")) {
                    contentFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        contentFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }


                String videoPreviewUrl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachmentVideoThumb();
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("video Preview Url : " + videoPreviewUrl);
                mImageLoader.get(videoPreviewUrl, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.video_default, R.drawable.video_default));
            } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_GALLERY) {
                String urlNoCrop = mContentsDetailsResponse.getAttachmentUrl() + Flinnt.GALLERY_NOCROP + File.separator + mContentsDetailsResponse.getContent().getAttachment();

                String urlMobile = mContentsDetailsResponse.getAttachmentUrl() + Flinnt.GALLERY_MOBILE + File.separator + mContentsDetailsResponse.getContent().getAttachment();
                mImageLoader.get(urlMobile, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.album_default, R.drawable.album_default));


                Flinnt.appInfoDataSets.clear();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mContentsDetailsResponse.getContent().getId(), mContentsDetailsResponse.getContent().getAttachment(), urlMobile, urlNoCrop, Flinnt.DISABLED);
                appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                Flinnt.appInfoDataSets.add(appInfoDataSet);

                for (AppInfoDataSet info : Flinnt.appInfoDataSets) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                    if (downloadInfo != null) {
                        info.setProgress(downloadInfo.getProgress());
                        info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }
                }

                if (Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), Flinnt.appInfoDataSets.get(0).getName()) && Flinnt.appInfoDataSets.get(0).getStatusText().equals("Not Download")) {
                    contentFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        contentFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }

            } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_DOCUMENT) {
                mediaThumnailImgview.setImageDrawable(Helper.getDrawable(ContentsDetailActivity.this, defaultDrawable));
                Flinnt.appInfoDataSets.clear();

                String docUrl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mContentsDetailsResponse.getContent().getId(), mContentsDetailsResponse.getContent().getAttachment(), docUrl, docUrl, Flinnt.DISABLED);
                appInfoDataSet.setDownloadFilePath(Helper.getFlinntDocumentPath());
                Flinnt.appInfoDataSets.add(appInfoDataSet);


                for (AppInfoDataSet info : Flinnt.appInfoDataSets) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                    if (downloadInfo != null) {
                        info.setProgress(downloadInfo.getProgress());
                        info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }
                }

                if (Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), Flinnt.appInfoDataSets.get(0).getName()) && Flinnt.appInfoDataSets.get(0).getStatusText().equals("Not Download")) {
                    contentFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        contentFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        contentFrame.setVisibility(View.VISIBLE);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }
            } else {
                mediaThumnailImgview.setImageDrawable(Helper.getDrawable(ContentsDetailActivity.this, defaultDrawable));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (contentFullScreen) {
            contentYouTubePlayer.setFullscreen(false);
            //*****change 32
        } else if (comeFrom.equals("BrowseCourseDescriptionActivity")) {
            finish();
        } else {

            //Log.d("Contt","onBackPressed : "+isFromNotification);
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.putExtra(Course.COURSE_ID_KEY, mCourseId);
            upIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
            upIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
            upIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
            upIntent.putExtra(Course.ALLOWED_ROLES_KEY, courseAllowedRole);
            upIntent.putExtra("isFromNotification", isFromNotification);
            upIntent.putExtra("isPostDeleted", isPostDeleted);
            upIntent.putExtra("comeFrom", "ContentDetailActivity");

            upIntent.putExtra("isCourseRemoved", isCourseRemoved);

            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                //Log.d("Contt","shouldActivity if true : "+isFromNotification);
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                //Log.d("Contt","shouldActivity if else : "+isFromNotification);
                if (isFromNotification == Flinnt.TRUE) {  //@Chirag: 13/08/2018
                    Intent i = new Intent(ContentsDetailActivity.this,CourseDetailsActivity.class);
                    i.putExtra(Course.COURSE_ID_KEY, mCourseId);
                    i.putExtra(Course.COURSE_NAME_KEY, courseName);
                    i.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                    i.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
                    i.putExtra(Course.ALLOWED_ROLES_KEY, courseAllowedRole);
                    i.putExtra("isFromNotification", isFromNotification);
                    i.putExtra("isPostDeleted", isPostDeleted);
                    i.putExtra("comeFrom", "ContentDetailActivity");

                    i.putExtra("isCourseRemoved", isCourseRemoved);

                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//17

                    startActivity(i);
                    finish();

                }else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);

                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (AskPermition.getInstance(ContentsDetailActivity.this).isPermitted()) {
            if (v.getId() == R.id.readMoreTxt) {
                startActivity(new Intent(this, CourseDescriptionActivity.class)
                        .putExtra(NAME_KEY, mContentsDetailsResponse.getContent().getTitle())
                        .putExtra(DESCRIPTION_KEY, mContentsDetailsResponse.getContent().getDescription()));

            } else if (v.getId() == R.id.content_people_view_details_contentdetailview) {

                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                    return;
                }

                if (mContentViewStatisticsResponse == null || mContentViewStatisticsResponse.getData() == null) {
                    startProgressDialog();
                    new ContentViewStatistics(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case Flinnt.SUCCESS:
                                    if (msg.obj instanceof ContentViewStatisticsResponse) {
                                        stopProgressDialog();
                                        mContentViewStatisticsResponse = (ContentViewStatisticsResponse) msg.obj;
                                        Intent viewers = new Intent(ContentsDetailActivity.this, PostViewersSendMessageActivity.class);
                                        viewers.putExtra(Course.COURSE_ID_KEY, mCourseId);
                                        viewers.putExtra(Post.POST_ID_KEY, mContentID);
                                        viewers.putExtra(Course.COURSE_NAME_KEY, courseName);
                                        viewers.putExtra("comeFrom", "Content");
                                        viewers.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mContentViewStatisticsResponse.getData().getUserPictureUrl());
                                        startActivity(viewers);
                                        updateViewersData();
                                    }
                            }

                        }
                    }, mCourseId, mContentID, 6).sendContentViewStatisticsRequest();

                    return;
                }

                Intent viewers = new Intent(ContentsDetailActivity.this, PostViewersSendMessageActivity.class);
                viewers.putExtra(Course.COURSE_ID_KEY, mCourseId);
                viewers.putExtra(Post.POST_ID_KEY, mContentID);
                viewers.putExtra(Course.COURSE_NAME_KEY, courseName);
                viewers.putExtra("comeFrom", "Content");
                viewers.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mContentViewStatisticsResponse.getData().getUserPictureUrl());
                startActivity(viewers);
            } else if (v.getId() == R.id.content_media_open_btn_contentdetailview) {

                if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO && mContentsDetailsResponse.getContent().getAttachmentIsUrl() == 1) {
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(this);
                        return;
                    }
                    String youtubeUrl = mContentsDetailsResponse.getContent().getAttachment();
//				String lastIndxer = youtubeUrl.contains("=") ? "=" : "/";
//				String videoID = youtubeUrl.substring(mContentsDetailsResponse.getPostAttachment().lastIndexOf(lastIndxer) + 1);
                    Uri uri = Uri.parse(youtubeUrl);
                    String videoID = uri.getQueryParameter("v");
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + contentYoutubeVedioID));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + contentYoutubeVedioID));
                            startActivity(intent);
                        } catch (Exception e) {
                            Helper.showToast(getString(R.string.no_app_available), Toast.LENGTH_LONG);
                            LogWriter.err(e);
                        }
                    }
                } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_AUDIO) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mContentsDetailsResponse.getContent().getAttachment())) {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(this);
                            return;
                        }
                        String audiourl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("audiourl : " + audiourl);

                        setDownloadMediaFileandHandler();
//                    if (mDownload == null) {
                        mDownload = new DownloadMediaFile(ContentsDetailActivity.this, Helper.getFlinntAudioPath(), mContentsDetailsResponse.getContent().getAttachment(), Long.parseLong(mContentID), audiourl, mHandler);
                        mDownload.execute();
//                    }
                        setDownloadProgressDialog();


                    } else {
                        startAudioIntent();

                    }
                }
            } else if (v.getId() == R.id.content_media_thumbnail_contentdetailview) {
                if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_GALLERY) {
                    try {
                        String filename = Helper.getFlinntImagePath() + mContentsDetailsResponse.getContent().getAttachment();
                        if (new File(filename).exists()) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(filename)), "image/*");
                            startActivity(intent);
                        } else {
                            Helper.showAlertMessage(ContentsDetailActivity.this, "Image", "This image is not downloaded yet", "Ok");
                        }
                    } catch (Exception e) {
                    }
                } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO
                    /*&& mContentsDetailsResponse.getPostContentUrlInt() != 1*/) {

                    /*
                     *VideoEncrptionCode (if their is encrypt video file available then start video view)
                     */

                    if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), "en" + mContentsDetailsResponse.getContent().getAttachment())) {
                        String path = "en" + mContentsDetailsResponse.getContent().getAttachment();
                        Intent videoview = new Intent(ContentsDetailActivity.this, VideoViewActivity.class);
                        videoview.putExtra("path", path);
                        startActivity(videoview);

                    } else if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), mContentsDetailsResponse.getContent().getAttachment())) {

                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(this);
                            return;
                        }
                        String videourl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("videourl : " + videourl);

                        setDownloadMediaFileandHandler();
//                    if (mDownload == null) {
                        mDownload = new DownloadMediaFile(ContentsDetailActivity.this, Helper.getFlinntVideoPath(), mContentsDetailsResponse.getContent().getAttachment(), Long.parseLong(mContentID), videourl, mHandler);
                        mDownload.execute();
//                    }

                        setDownloadProgressDialog();

                    } else {
                        startVideoIntent();
                    }
                } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_DOCUMENT) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mContentsDetailsResponse.getContent().getAttachment())) {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(this);
                            return;
                        }
                        String docurl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("docurl : " + docurl);

                        setDownloadMediaFileandHandler();
//                    if (mDownload == null) {
                        mDownload = new DownloadMediaFile(ContentsDetailActivity.this, Helper.getFlinntDocumentPath(), mContentsDetailsResponse.getContent().getAttachment(), Long.parseLong(mContentID), docurl, mHandler);
                        mDownload.execute();
//                    }
                        setDownloadProgressDialog();

                    } else {
                        startDocumentIntent();
                    }
                }
            } else if (v.getId() == R.id.contentFrame) {
                if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_GALLERY) {
                    if (AskPermition.getInstance(ContentsDetailActivity.this).isPermitted()) {


                        if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), mContentsDetailsResponse.getContent().getAttachment())) {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            contentProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            } else {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            }

                        }
                    }
                } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_VIDEO) {
                    if (AskPermition.getInstance(ContentsDetailActivity.this).isPermitted()) {
                        String fileVideoPath;
                        if ((mContentsDetailsResponse.getContent().getAttachmentDoEncode() + "").equals(Flinnt.ENABLED)) {
                            fileVideoPath = Helper.getFlinntHiddenVideoPath();
                        } else {
                            fileVideoPath = Helper.getFlinntVideoPath();
                        }
                        if (!Helper.isFileExistsAtPath(fileVideoPath, mContentsDetailsResponse.getContent().getAttachment())) {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            contentProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            } else {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            }

                        }
                    }
                } else if (mContentsDetailsResponse.getContent().getAttachmentType() == Flinnt.POST_CONTENT_DOCUMENT) {
                    if (AskPermition.getInstance(ContentsDetailActivity.this).isPermitted()) {
                        if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mContentsDetailsResponse.getContent().getAttachment())) {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            contentProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                            if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            } else {
                                contentProgressBarHint.setVisibility(View.VISIBLE);
                                download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                            }
                        }
                    }
                }


            } else if (v.getId() == R.id.post_link_textview) {
                MediaHelper.ShowLink(mContentsDetailsResponse.getContent().getAttachment(), mCommon);
            } else if (v.getId() == R.id.content_send_comment_contentdetailview) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                    return;
                }
                String commentStr = commentTxt.getText().toString();
                //*****change 31
                if (!TextUtils.isEmpty(commentStr.trim())) {
                    if (Helper.isConnected()) {
                        new ContentAddComment(mHandler, mCourseId, mContentID, commentStr).sendAddCommentRequest();
                        startProgressDialog();
                        Helper.hideKeyboardFromWindow(ContentsDetailActivity.this);
                    } else {
                        Helper.showNetworkAlertMessage(ContentsDetailActivity.this);
                    }
                } else {
                    Helper.showToast(getString(R.string.comment_blank), Toast.LENGTH_SHORT);
                }
            } else if (v.getId() == R.id.content_like_contentdetailview) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                    return;
                }
                mLikeUnlikeRequest = new LikeUnlikeRequest();

                mLikeUnlikeRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mLikeUnlikeRequest.setCourseID(mCourseId);
                mLikeUnlikeRequest.setContentID(mContentID);

                mLikeUnlikeContent = new LikeUnlikeContent(mHandler);

                mLikeUnlikeContent.setLikeBookmarkRequest(mLikeUnlikeRequest);
                mLikeUnlikeContent.sendLikeBookmarkRequest();
                startProgressDialog();
            } else if (v.getId() == R.id.content_audio_play_contentdetailview) {


                if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mContentsDetailsResponse.getContent().getAttachment())) {

                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(ContentsDetailActivity.this);
                        return;
                    }

                    String audiourl = mContentsDetailsResponse.getAttachmentUrl() + mContentsDetailsResponse.getContent().getAttachment();
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("audiourl : " + audiourl);

                    setDownloadMediaFileandHandler();
//                if (mDownload == null) {
                    mDownload = new DownloadMediaFile(ContentsDetailActivity.this, Helper.getFlinntAudioPath(), mContentsDetailsResponse.getContent().getAttachment(), Long.parseLong(mContentID), audiourl, mHandler);
                    mDownload.execute();
//                }
                    setDownloadProgressDialog();

                } else if (null != mediaPlayer) {

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        baseChronometer = currentDuration.getBase() - SystemClock.elapsedRealtime();
                        currentDuration.stop();
                        audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white));
                    } else {
                        mediaPlayer.start();
                        currentDuration.setBase(SystemClock.elapsedRealtime() + baseChronometer);
                        currentDuration.start();
                        audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_arrow_white));
                    }
                } else {
                    startAudioIntent();
                }
            }
        }
    }

    /**
     * .Intent to get video
     */
    private void startVideoIntent() {
        try {
            String filename = Helper.getFlinntVideoPath() + mContentsDetailsResponse.getContent().getAttachment();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filename)), "video/*");
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    private void startAudioIntent() {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("startAudioIntent");
        mediaPlayer = new MediaPlayer();
        try {
            String audioFile = Helper.getFlinntAudioPath() + mContentsDetailsResponse.getContent().getAttachment();
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    performOnEndMedia();
                }

            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_arrow_white));

            currentDuration.setBase(SystemClock.elapsedRealtime());
            baseChronometer = 0;
            currentDuration.start();

            int secs = (int) (mediaPlayer.getDuration() / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            endDuration.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));

            audioProgressBar.setVisibility(ProgressBar.VISIBLE);
            audioProgressBar.setProgress(0);
            audioProgressBar.setMax(mediaPlayer.getDuration());
            audioThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    int currentPosition = 0;
                    int total = mediaPlayer.getDuration();
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("currentPosition : " + currentPosition + ", total : " + total);
                    while (mediaPlayer != null /*&& currentPosition <= total && (total-currentPosition) > 200*/) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("currentPosition : " + currentPosition + ", total : " + total);
                        try {
                            Thread.sleep(100);
                            if (mediaPlayer != null) {
                                currentPosition = mediaPlayer.getCurrentPosition();
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        audioProgressBar.setProgress(currentPosition);
                    }
                }
            });
            audioThread.start();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Reset the player after playing media ends
     */
    private void performOnEndMedia() {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("on complete media...");
        if (null != mediaPlayer) {
            audioProgressBar.setProgress(mediaPlayer.getDuration());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("currentDuration stop");
                    currentDuration.stop();
                    currentDuration.setBase(SystemClock.elapsedRealtime());
                    audioProgressBar.setProgress(0);
                    baseChronometer = 0;
                    mediaPlayer = null;
                    audioPlayButton.setImageDrawable(ContextCompat.getDrawable(ContentsDetailActivity.this, R.drawable.ic_play_arrow_white));
                }
            });
        }
    }

    private void startDocumentIntent() {
        try {
            String filename = Helper.getFlinntDocumentPath() + mContentsDetailsResponse.getContent().getAttachment();
            MediaHelper.showDocument(filename, ContentsDetailActivity.this);
        } catch (Exception e) {
        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ContentsDetailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ContentsDetailActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    /**
     * Stops currently running download
     */
    private void stopDownload() {
        //stop download if working...
        if (mDownload != null) {
            mDownload.setCancel(true);
        }
    }

    /**
     * Update like and bookmark buttons
     *
     * @param response like bookmark response
     */
    private void updateLikeCommentButtons(LikeUnlikeResponse response) {
        contentTotalLikesTxtview.setText(response.getData().getCount() + PostViewStatistics.LIKES);

        try {
            if (response.getData().getLike() == Flinnt.TRUE) {
                likeContentImgbtn.setImageResource(R.drawable.ic_is_liked);
            }
        } catch (Exception e) {

        }
        try {
            if (response.getData().getDislike() == Flinnt.TRUE) {
                likeContentImgbtn.setImageResource(R.drawable.post_detail_like);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            baseChronometer = currentDuration.getBase() - SystemClock.elapsedRealtime();
            currentDuration.stop();
            audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white));
        }

        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    // For Audio Controller
    private MediaPlayer mediaPlayer;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        try {
            mProgressDialog = new ProgressDialog(ContentsDetailActivity.this);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (DialogInterface.OnClickListener) null);

            mProgressDialog.show();

            mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopDownload();
                    try {
                        mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                        mProgressDialog.setMessage("Cancelling...");
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }
            });
            mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

        } catch (Exception e) {
            if (LogWriter.isValidLevel(Log.INFO)) ;
        }

    }


    /**
     * Set download handler
     */
    private void setDownloadMediaFileandHandler() {
        if (mDownload == null) {
            long contentID = Long.parseLong(mContentsDetailsResponse.getContent().getId());
            if (DownloadFileManager.isContainID(contentID)) {
                mDownload = DownloadFileManager.get(contentID);
                if (mDownload != null) {
                    mDownload.setHandler(mHandler);
                }
            }
        }
    }

    /**
     * Code for Post data image downloding Play/Pause
     */

    @Override
    public void onResume() {
        super.onResume();
        if (null == mCommon) {
            mCommon = new Common(this);
        }
        register();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegister();
    }

    private void register() {
        try {
            mReceiver = new DownloadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
            LocalBroadcastManager.getInstance(ContentsDetailActivity.this).registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            LogWriter.write("Exception in register : " + e.toString());
        }
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(ContentsDetailActivity.this).unregisterReceiver(mReceiver);
        }
    }


    private void download(int position, String tag, AppInfoDataSet info) {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            contentProgressBarHint.setVisibility(View.GONE);
            return;
        }
        DownloadService.intentDownload(ContentsDetailActivity.this, position, position, tag, info);
    }

    private void pause(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentPause(ContentsDetailActivity.this, position, position, tag, info);
    }


    private void cancel(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentCancel(ContentsDetailActivity.this, position, tag, info);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(contentYoutubeVedioID);
            contentYouTubePlayer = youTubePlayer;
            contentYouTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean _isFullScreen) {
                    contentFullScreen = _isFullScreen;
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        isAppNotInstalled();

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
        youtubeFrame.setVisibility(View.GONE);
        contentFrame.setVisibility(View.GONE);
        youtubeFrame.setVisibility(View.GONE);
        int defaultDrawable = R.drawable.youtube_video_fram_not_get;
        mediaOpenImgbtn.setVisibility(View.VISIBLE);
        String url = "http://img.youtube.com/vi/" + contentYoutubeVedioID + "/0.jpg";
        mImageLoader.get(url, ImageLoader.getImageListener(mediaThumnailImgview, defaultDrawable, defaultDrawable));
    }

    /*
     * Code for download rececive and update progress
     */
    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == null || !action.equals(DownloadService.ACTION_DOWNLOAD_BROAD_CAST)) {
                return;
            }
            final int position = intent.getIntExtra(DownloadService.EXTRA_POSITION, -1);
            final AppInfoDataSet tmpInfoDataSet = (AppInfoDataSet) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);

            if (mContentID.equals(tmpInfoDataSet.getId())) {
                if (tmpInfoDataSet == null || position == -1) {
                    return;
                }
                AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                final int status = tmpInfoDataSet.getStatus();
                switch (status) {
                    case AppInfoDataSet.STATUS_CONNECTING:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
                        if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(tmpInfoDataSet.getDownloadFilePath(), tmpInfoDataSet.getName())) {
                            cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                            contentProgressBarHint.setVisibility(View.GONE);
                        } else {
                        }

                        break;

                    case AppInfoDataSet.STATUS_DOWNLOADING:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
                        appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                        appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        contentProgressBarHint.setVisibility(View.GONE);

                        break;
                    case AppInfoDataSet.STATUS_COMPLETE:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
                        appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                        appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                        contentFrame.setVisibility(View.GONE);
                        break;

                    case AppInfoDataSet.STATUS_PAUSED:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        contentProgressBarHint.setVisibility(View.GONE);

                        break;
                    case AppInfoDataSet.STATUS_NOT_DOWNLOAD:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                        appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                        appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                        contentProgressBar.setProgress(appInfoDataSet.getProgress());
                        contentProgressBarHint.setVisibility(View.GONE);

                        break;
                    case AppInfoDataSet.STATUS_DOWNLOAD_ERROR:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
                        appInfoDataSet.setDownloadPerSize("");

                        break;
                    case AppInfoDataSet.STATUS_CANCEL:
                        appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                        appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                        appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                        contentDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                        contentProgressBar.setProgress(0);
                        contentProgressBarHint.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}