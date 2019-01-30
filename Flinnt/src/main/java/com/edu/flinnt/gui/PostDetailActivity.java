package com.edu.flinnt.gui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
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
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PostCommentsAdapter;
import com.edu.flinnt.adapter.PostCommentsAdapter.OnItemLongClickListener;
import com.edu.flinnt.core.AddComment;
import com.edu.flinnt.core.DeleteComment;
import com.edu.flinnt.core.DeletePost;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.LikeBookmark;
import com.edu.flinnt.core.PollList;
import com.edu.flinnt.core.PollVote;
import com.edu.flinnt.core.PostComments;
import com.edu.flinnt.core.PostList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.downloads.OnItemClickListener;
import com.edu.flinnt.downloads.PostDetailRecyclerViewAdapter;
import com.edu.flinnt.downloadsmultithread.DownloadInfo;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.AddCommentResponse;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Comment;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.DeletePostResponse;
import com.edu.flinnt.protocol.LikeBookmarkRequest;
import com.edu.flinnt.protocol.LikeBookmarkResponse;
import com.edu.flinnt.protocol.PollListRequest;
import com.edu.flinnt.protocol.PollListResponse;
import com.edu.flinnt.protocol.PollVoteRequest;
import com.edu.flinnt.protocol.PollVoteResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostCommentsRequest;
import com.edu.flinnt.protocol.PostCommentsResponse;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostListMenuResponse;
import com.edu.flinnt.protocol.PostListRequest;
import com.edu.flinnt.protocol.PostListResponse;
import com.edu.flinnt.protocol.PostViewStatisticsResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadFileManager;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.DownloadUtils;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.JellyBeanSpanFixTextView;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.edu.flinnt.R.id.communication_date_txt;
import static com.edu.flinnt.R.id.like_view_comment_linear;
import static com.edu.flinnt.R.id.message_linear;
import static com.edu.flinnt.R.id.post_by_img;

/**
 * GUI class to show post details
 */
public class PostDetailActivity extends AppCompatActivity implements OnClickListener, /*OnCancelListener,*/  YouTubePlayer.OnInitializedListener, OnItemClickListener<AppInfoDataSet> {

    private Toolbar toolbar;
    private TextView postTitleTxtview,
            postLinkTextview, communicationNameTxt, communicationDateTxt;

    private JellyBeanSpanFixTextView postDescriptionTxtview;
    private RadioGroup mCommunicationPollOptionRadiogrp;
    private EditText commentTxt;
    private ImageView sendComment, likePostImg, commentPostImg, repostImg;
    private SelectableRoundedCourseImageView postByImg;
    private ImageButton mediaOpenImgbtn, audioPlayButton;
    private ImageView mediaThumnailImgview;
    private LinearLayout addCommentLayout, commentListLayout, postMediaAudioLayout, mCommunicationPollLinear, mLikeCommentLinear, mCommentsLinear;
    private RelativeLayout postMediaRelative;
    private ProgressBar audioProgressBar;
    private Thread audioThread;
    private TextView endDuration;
    private Chronometer currentDuration;
    long baseChronometer = 0;
    private Button mPollSubmitBtn;
    private RecyclerView postCommentsRecycleview;
    private PostComments mPostComments;
    private PostCommentsRequest mPostCommentsRequest;
    private PostCommentsAdapter mPostCommentsAdapter;
    private ArrayList<Comment> mCommentItems = new ArrayList<Comment>();
    //private CommentEndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListenerComments;


    public Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private PostListResponse mPostListResponse;
    private Post mPost;

    private LikeBookmarkRequest mLikeBookmarkRequest;
    private LikeBookmark mLikeBookmark;

    private ImageLoader mImageLoader;
    private PollVoteResponse mPollVoteResponse;
    private PollVoteRequest mPollVoteRequest = new PollVoteRequest();
    private String courseID = "", courseName = "", coursePicture = "", coursePictureUrl = "", postID = "", allowedRole = "";
    private int postType = 0;
    private int isFromNotification = Flinnt.FALSE;
    private Common mCommon;
    private DownloadMediaFile mDownload;

    private NestedScrollView mScrollView;
    private boolean isNeedToGetComment = false, isPostDeleted = false, isCourseRemoved = false, isCourseDeletedDialogShowing, isLiked;
    private RelativeLayout albumMediaRelative;
    private PostDetailRecyclerViewAdapter mAlbumAdapter;
    private RecyclerView mAlbumRecycler;
    private ProgressBar mCommunicationPollProgress;
    private PollListResponse mPollListResponse = null;
    /**
     * Album downloading declaration.
     */

    private DownloadReceiver mReceiver;
    private FrameLayout postFrame;
    private FrameLayout youtubeFrame;
    private ImageView postDownloadBtn;
    com.github.lzyzsd.circleprogress.DonutProgress postProgressBar;
    private ProgressBar postProgressBarHint;
    private YouTubePlayerFragment postYouTubePlayerFragment;
    private YouTubePlayer postYouTubePlayer;
    private String postYoutubeVedioID;
    private boolean postFullScreen;
    String bookMarkStatus = "";
    private ArrayList<String> imagesName = new ArrayList<String>();
    private TextView mCommunicationPollTxt;
    private PostList mPostList = null;
    private PostListRequest mPostListRequest;
    private int deviceWidth;
    private Toolbar mPostDetailToolbar;
    MenuItem bookmarkItem, editItem, deleteItem;
    private String canComment = "0";
    String selectedID = "";
    public static final int POST_DETAIL_COMMENT_CALL_BACK = 123;
    public static final int SELECT_COURSE_CALL_BACK = 124;

    LinearLayout likeLinear, commentLinear, messageLinear, mLikeViewCommentLinear;
    TextView messageTxt, messageViewMoreTxt, mPollEnddateTxt;
    TextView mLikeTxt, mCommentTxt, mViewsTxt;
    TextView mPostLikeTxt, mPostCommentTxt;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    int firstVisibleItem, visibleItemCount, totalItemCount, pastVisiblesItems;
    LinearLayoutManager mylinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.post_detailview);
        AskPermition.getInstance(PostDetailActivity.this).RequestAllPermission();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        postYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Log.d("Postt", "onCreate()_PostDetailActivity.java");

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            //Log.d("Postt", "bundle is not null");
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                courseID = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(Post.POST_ID_KEY)) postID = bundle.getString(Post.POST_ID_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                courseName = bundle.getString(Course.COURSE_NAME_KEY);
            if (bundle.containsKey(Course.COURSE_PICTURE_KEY))
                coursePicture = bundle.getString(Course.COURSE_PICTURE_KEY);
            if (bundle.containsKey("isFromNotification"))
                isFromNotification = bundle.getInt("isFromNotification");
            if (bundle.containsKey(Course.POST_TYPE))
                postType = bundle.getInt(Course.POST_TYPE);

            if (bundle.containsKey(Course.COURSE_CAN_COMMENT))
                canComment = bundle.getString(Course.COURSE_CAN_COMMENT);

            if (isFromNotification == Flinnt.TRUE && bundle.containsKey(Config.USER_ID)) {
                String userId = bundle.getString(Config.USER_ID);

                coursePictureUrl = bundle.getString(Course.COURSE_PICTURE_URL_KEY);
                coursePicture = bundle.getString(Course.USER_PICTURE_KEY);
                courseName = bundle.getString(Course.COURSE_NAME_KEY);
                allowedRole = bundle.getString(Course.ALLOWED_ROLES_KEY);

                if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userId);
                }
            }
        } else { //@Chirag: 16/08/2018 added else part for testing
            //Log.d("Postt", "bundle is null");
        }
        deviceWidth();
        mImageLoader = Requester.getInstance().getImageLoader();

        postTitleTxtview = (TextView) findViewById(R.id.postdetail_title_postdetailview);
        postDescriptionTxtview = (JellyBeanSpanFixTextView) findViewById(R.id.post_content_description_postdetailview);

        postLinkTextview = (TextView) findViewById(R.id.post_link_textview);

        communicationNameTxt = (TextView) findViewById(R.id.communication_name_txt);
        communicationDateTxt = (TextView) findViewById(communication_date_txt);
        postByImg = (SelectableRoundedCourseImageView) findViewById(post_by_img);
//        postByImg.setOval(true);
        postByImg.setBorderColor(getResources().getColor(R.color.userpic_border_color));
        postByImg.setBorderWidthDP(1);
        postByImg.setDefaultImageResId(R.drawable.default_user_profile_image);

        commentTxt = (EditText) findViewById(R.id.post_add_comment_edittext_postdetailview);
        sendComment = (ImageView) findViewById(R.id.post_send_comment_postdetailview);
        sendComment.setOnClickListener(this);
        messageTxt = (TextView) findViewById(R.id.message_recipients);
        messageViewMoreTxt = (TextView) findViewById(R.id.message_recipients_view_more);
        messageViewMoreTxt.setOnClickListener(this);
        mPollEnddateTxt = (TextView) findViewById(R.id.communication_poll_enddate_txt);

        mediaOpenImgbtn = (ImageButton) findViewById(R.id.post_media_open_btn_postdetailview);
        likePostImg = (ImageView) findViewById(R.id.post_like_img);
        commentPostImg = (ImageView) findViewById(R.id.post_comment_img);
        repostImg = (ImageView) findViewById(R.id.repost_img);
        likeLinear = (LinearLayout) findViewById(R.id.like_linear);
        commentLinear = (LinearLayout) findViewById(R.id.comment_linear);
        messageLinear = (LinearLayout) findViewById(message_linear);
        mLikeViewCommentLinear = (LinearLayout) findViewById(like_view_comment_linear);
        mLikeTxt = (TextView) findViewById(R.id.likes_txt);
        mCommentTxt = (TextView) findViewById(R.id.comments_txt);
        mViewsTxt = (TextView) findViewById(R.id.views_txt);

        mPostLikeTxt = (TextView) findViewById(R.id.post_like_txt);
        mPostCommentTxt = (TextView) findViewById(R.id.post_comment_txt);

        mLikeTxt.setOnClickListener(this);
        mCommentTxt.setOnClickListener(this);
        mViewsTxt.setOnClickListener(this);
        likeLinear.setOnClickListener(this);
        commentLinear.setOnClickListener(this);
        mediaOpenImgbtn.setOnClickListener(this);
        repostImg.setOnClickListener(this);

        postFrame = (FrameLayout) findViewById(R.id.postFrame);
        youtubeFrame = (FrameLayout) findViewById(R.id.youtube_frame);
        postDownloadBtn = (ImageView) findViewById(R.id.postDownloadBtn);
        postProgressBar = (com.github.lzyzsd.circleprogress.DonutProgress) findViewById(R.id.postProgressBar);
        postProgressBar.setTextColor(R.color.gray);
        postProgressBar.setTextSize((float) 5.0);
        postProgressBarHint = (ProgressBar) findViewById(R.id.postProgressBarHint);

        mPostDetailToolbar = (Toolbar) findViewById(R.id.communication_toolbar);
        mPostDetailToolbar.inflateMenu(R.menu.communication_toolbar_menu);
        mPostDetailToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.commu_bookmark:
                        if (Helper.isConnected()) {
                            mLikeBookmarkRequest = new LikeBookmarkRequest();

                            mLikeBookmarkRequest.setUserID(Config.getStringValue(Config.USER_ID));
                            mLikeBookmarkRequest.setPostID(postID);
                            if (mPost.getIsBookmarkInt() == Flinnt.TRUE) {
                                mLikeBookmark = new LikeBookmark(mHandler, LikeBookmark.BOOKMARK_REMOVE);
                            } else {
                                mLikeBookmark = new LikeBookmark(mHandler, LikeBookmark.BOOKMARK_ADD);
                            }
                            mLikeBookmark.setLikeBookmarkRequest(mLikeBookmarkRequest);
                            mLikeBookmark.sendLikeBookmarkRequest();
                            startProgressDialog();
                        } else {
                            Helper.showNetworkAlertMessage(PostDetailActivity.this);
                        }
                        return true;

                    case R.id.commu_delete:
                        showDeletePostWarning(PostDetailActivity.this, courseID, mPost);
                        return true;

                    case R.id.commu_edit:

                        if (Helper.isConnected()) {
                            Intent postEdit;
                            if (postType == Flinnt.POST_TYPE_POLL) {
                                postEdit = new Intent(PostDetailActivity.this, AddPollActivity.class);
                                postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_POLL_EDIT);
                                postEdit.putExtra("PollOptions", mPollListResponse);
                                postEdit.putExtra(Post.POLL_RESULT_HOURS_KEY, mPost.getPollResultHours());
                                postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, mPost.getAttachments());
                            } else {
                                postEdit = new Intent(PostDetailActivity.this, AddCommunicationActivity.class);
                            }
                            postEdit.putExtra(Course.COURSE_ID_KEY, courseID);
                            postEdit.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                            postEdit.putExtra(CourseInfo.COURSE_NAME_KEY, courseName);
                            postEdit.putExtra(PostDetailsResponse.TITLE_KEY, mPost.getTitle());
                            postEdit.putExtra(PostDetailsResponse.DESCRIPTION_KEY, mPost.getDescription());
                            postEdit.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, mPost.getAttachmentUrl());
                            postEdit.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, mPost.getPostContentTypeInt());
                            postEdit.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, mPost.getAttachmentsIsUrl());


                            if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
                                String teachersIDs = "";
                                String studentsIDs = "";
                                int teachersCount = 0;
                                int studentsCount = 0;
                                if (mPost.getMessageToUsers().contains("|")) {
                                    List<String> items = Arrays.asList(mPost.getMessageToUsers().split("\\s*,\\s*"));
                                    for (int i = 0; i < items.size(); i++) {
                                        String temp = items.get(i);
                                        String[] valueRoleId = temp.split("\\|");
                                        if (Integer.parseInt(valueRoleId[1]) == Flinnt.COURSE_ROLE_TEACHER) {
                                            teachersIDs = teachersIDs + valueRoleId[2] + ",";
                                            teachersCount++;
                                        } else if (Integer.parseInt(valueRoleId[1]) == Flinnt.COURSE_ROLE_LEARNER) {
                                            studentsIDs = studentsIDs + valueRoleId[2] + ",";
                                            studentsCount++;
                                        }
                                    }
                                }

                                postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_MESSAGE_EDIT);
                                postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, mPost.getAttachments());
                                postEdit.putExtra(SelectUsersActivity.SELECTED_TEACHERS_COUNT, teachersCount);
                                postEdit.putExtra(SelectUsersActivity.SELECTED_STUDENTS_COUNT, studentsCount);
                                postEdit.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER, studentsIDs);
                                postEdit.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER, teachersIDs);
                            } else if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_BLOG) {
                                postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_BLOG_EDIT);
                                postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, mPost.getAttachments());
                            } else if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                                String photos = mPost.getAttachments();
                                ArrayList<String> imagesName = new ArrayList<String>();
                                String[] photosArr = photos.split(",");
                                if (photosArr.length > 0) {
                                    for (int i = 0; i < photosArr.length; i++) {
                                        final String imageFileName = photosArr[i].trim();
                                        if (!TextUtils.isEmpty(imageFileName)) {
                                            imagesName.add(imageFileName);
                                        }
                                    }
                                    postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_ALBUM_EDIT);
                                    postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, imagesName);
                                }
                            }

                            startActivityForResult(postEdit, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);

                        } else {
                            Helper.showNetworkAlertMessage(PostDetailActivity.this);
                        }

                        return true;

                    default:
                        break;
                }
                return false;
            }
        });

        bookmarkItem = mPostDetailToolbar.getMenu().findItem(R.id.commu_bookmark);
        editItem = mPostDetailToolbar.getMenu().findItem(R.id.commu_edit);
        deleteItem = mPostDetailToolbar.getMenu().findItem(R.id.commu_delete);

        mediaThumnailImgview = (ImageView) findViewById(R.id.post_media_thumbnail_postdetailview);
        postFrame.setOnClickListener(this);
        mediaThumnailImgview.setOnClickListener(this);

        addCommentLayout = (LinearLayout) findViewById(R.id.linear_add_comment_postdetailview);
        commentListLayout = (LinearLayout) findViewById(R.id.comments_list_layout);

        postMediaRelative = (RelativeLayout) findViewById(R.id.post_media_relative_postdetailview);
        postMediaAudioLayout = (LinearLayout) findViewById(R.id.post_media_audio_view_postdetailview);
        audioPlayButton = (ImageButton) findViewById(R.id.post_audio_play_postdetailview);
        audioPlayButton.setOnClickListener(this);
        audioProgressBar = (ProgressBar) findViewById(R.id.audio_progressBar);
        audioProgressBar.getProgressDrawable().setColorFilter(Color.GRAY, Mode.SRC_IN);
        currentDuration = (Chronometer) findViewById(R.id.audio_current_duration_text);
        endDuration = (TextView) findViewById(R.id.audio_end_duration_text);

        postCommentsRecycleview = (RecyclerView) findViewById(R.id.post_people_comments_recycler_view);
        postCommentsRecycleview.setNestedScrollingEnabled(false);
        postCommentsRecycleview.setHasFixedSize(false);

        mPollSubmitBtn = (Button) findViewById(R.id.communication_poll_submit_btn);
        mPollSubmitBtn.setOnClickListener(this);

        mScrollView = (NestedScrollView) findViewById(R.id.scrollview_detailview);

        mCommunicationPollOptionRadiogrp = (RadioGroup) findViewById(R.id.communication_poll_option_radiogrp);
        mCommunicationPollLinear = (LinearLayout) findViewById(R.id.communication_poll_linear);
        mCommunicationPollTxt = (TextView) findViewById(R.id.communication_poll_txt);
        mLikeCommentLinear = (LinearLayout) findViewById(R.id.like_comment_layout);
        mCommentsLinear = (LinearLayout) findViewById(R.id.comments_linear);


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

                        if (message.obj instanceof PostListResponse) {
                            mPostListResponse = (PostListResponse) message.obj;
                            if (mPostListResponse != null && mPostListResponse.getPostList().size() > 0) {
                                mPost = mPostListResponse.getPostList().get(mPostListResponse.getPostList().size() - 1);
                                if (postType == Flinnt.POST_TYPE_ALBUM) {
                                    mLikeCommentLinear.setVisibility(View.VISIBLE);
                                    albumMediaRelative.setVisibility(View.VISIBLE);
                                    albumMediaUpdate();
                                    updateAlbumData();
                                } else if (postType == Flinnt.POST_TYPE_POLL) {
                                    mCommunicationPollLinear.setVisibility(View.VISIBLE);
                                    mLikeCommentLinear.setVisibility(View.GONE);
                                    mCommentsLinear.setVisibility(View.GONE);
                                    pollOptionRequest();

                                    if (mPostListResponse != null) {
                                        updatePostData();
                                    }
                                } else {
                                    mLikeCommentLinear.setVisibility(View.VISIBLE);
                                    albumMediaRelative.setVisibility(View.GONE);

                                    if (mPostListResponse != null) {
                                        updatePostData();
                                    }
                                }
                                invalidateOptionsMenu();
                            }

                        } else if (message.obj instanceof PostCommentsResponse) {
                            loading = true;
                            updateCommentsList((PostCommentsResponse) message.obj);
                            updateCommentButton();
                        } else if (message.obj instanceof AddCommentResponse) {
                            AddCommentResponse mAddCommentResponse = (AddCommentResponse) message.obj;
                            if (mAddCommentResponse.getIsAdded() == Flinnt.TRUE) {
                                commentPostImg.setImageResource(R.drawable.ic_comment_blue);
                                mPostCommentTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                                commentTxt.setText("");
                                if (mAddCommentResponse.getShowComment() == Flinnt.TRUE) {
                                    showCommentToUser(mAddCommentResponse);
                                } else {
                                    Helper.showAlertMessage(PostDetailActivity.this, "SUCCESS", "Comment is sent for approval.", "CLOSE");
                                }
                            }
                            commentCountFunction(Flinnt.TRUE);
                        } else if (message.obj instanceof LikeBookmarkResponse) {
                            stopProgressDialog();
                            updateLikeCommentButtons((LikeBookmarkResponse) message.obj);
                        } else if (message.obj instanceof PollVoteResponse) {
                            mPollVoteResponse = (PollVoteResponse) message.obj;
                            if (mPollVoteResponse.getData().getVoted() == Flinnt.TRUE) {
                                if (mPollVoteResponse.getData().getVoted() == 1) {
                                    Helper.showToast(getResources().getString(R.string.poll_vote_sent), Toast.LENGTH_LONG);
                                    mPollListResponse.getData().setPollVoted(Flinnt.TRUE);
                                    mPollListResponse.getData().setVotedOption(selectedID);
                                }
                                displayPollOption();
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
                                    if (postType == Flinnt.POST_TYPE_POLL) {
                                        Helper.showToast(getResources().getString(R.string.delete_poll_msg), Toast.LENGTH_SHORT);
                                    } else {
                                        Helper.showToast(getResources().getString(R.string.delete_post_msg), Toast.LENGTH_SHORT);
                                    }
                                    onBackPressed();
                                    return;
                                } else if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
//                                    isCourseRemoved = true;
                                    showCourseDeletedDialog(errorMessage);
                                    return;
                                }
                            }
                            Helper.showAlertMessage(PostDetailActivity.this, "Error", errorMessage, "CLOSE");
                        }

                        break;

                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");
                        if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO) {
                            startVideoIntent();
                        } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_AUDIO) {
                            startAudioIntent();
                        } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_DOCUMENT) {
                            startDocumentIntent();
                        }

                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        mDownload = null;
                        if (!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                            Helper.showAlertMessage(PostDetailActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
                        }

                        break;

                    default:
                        /*commentCountFunction(Flinnt.TRUE);
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(message);
                }
            }
        };


        if (Helper.isConnected()) {

            startProgressDialog();
            ArrayList<String> postIdList = new ArrayList<>();
            postIdList.add(postID);

            mPostList = new PostList(mHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ, postIdList, true);
            mPostList.sendPostListRequest(mPostListRequest);
        } else {
            Helper.showNetworkAlertMessage(PostDetailActivity.this);
        }


        mAlbumRecycler = (RecyclerView) findViewById(R.id.section_options_recycler);
        mAlbumAdapter = new PostDetailRecyclerViewAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAlbumRecycler.setLayoutManager(layoutManager);
        mAlbumAdapter.setOnItemClickListener(PostDetailActivity.this);
        albumMediaRelative = (RelativeLayout) findViewById(R.id.album_media_relative_quizdetailview);

        mCommunicationPollProgress = (ProgressBar) findViewById(R.id.communication_poll_progress);

        updateCommentsData();

        Pattern p = Pattern.compile("<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>");
        Linkify.addLinks(postDescriptionTxtview, p, "http://");
        postDescriptionTxtview.setMovementMethod(LinkMovementMethod.getInstance());
        postDescriptionTxtview.setAutoLinkMask(Linkify.WEB_URLS);
    }

    private void pollOptionRequest() {
        if (Helper.isConnected()) {
            PollListRequest mPollListRequest = new PollListRequest();
            mPollListRequest.setCourseID(courseID);
            mPollListRequest.setPostID(mPost.getPostID());
            mPollListRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mCommunicationPollProgress.setVisibility(View.VISIBLE);
            PollList pollList = new PollList(new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case Flinnt.SUCCESS:
                            LogWriter.write("data has been received");
                            mPollListResponse = (PollListResponse) msg.obj;
                            mCommunicationPollProgress.setVisibility(View.GONE);
                            displayPollOption();
                            break;
                        case Flinnt.FAILURE:
                            break;
                    }
                    return false;
                }
            }), mPollListRequest);
            pollList.sendPollListRequest();

        } else {
            Helper.showNetworkAlertMessage(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void displayPollOption() {

        if (mPollListResponse.getData().getPollFinished() == Flinnt.TRUE) {
            mCommunicationPollLinear.removeAllViews();
            mCommunicationPollLinear.addView(mCommunicationPollTxt);
            mCommunicationPollTxt.setText("Poll Result");
            int maxIndex = 0;
            int totalVote = 0;
            for (int i = 1; i < mPollListResponse.getData().getOptions().size(); i++) {
                float newnumber = Float.parseFloat(mPollListResponse.getData().getOptions().get(i).getVotesPercent());
                if ((newnumber > Float.parseFloat(mPollListResponse.getData().getOptions().get(maxIndex).getVotesPercent()))) {
                    maxIndex = i;
                }
            }
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < mPollListResponse.getData().getOptions().size(); i++) {
                View someView = inflater.inflate(R.layout.poll_result_item, mCommunicationPollLinear, false);
                TextView pollBackgroundView = (TextView) someView.findViewById(R.id.poll_background_view);
                TextView pollOptionTxt = (TextView) someView.findViewById(R.id.poll_option_txt);
                TextView pollOptionPercentTxt = (TextView) someView.findViewById(R.id.poll_option_percent_txt);


                float tempPercent = Float.parseFloat(mPollListResponse.getData().getOptions().get(i).getVotesPercent());
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (deviceWidth * tempPercent / 100),
                        FrameLayout.LayoutParams.MATCH_PARENT);
                pollBackgroundView.setLayoutParams(lp);

                pollOptionTxt.setText(mPollListResponse.getData().getOptions().get(i).getText());
                pollOptionPercentTxt.setText(mPollListResponse.getData().getOptions().get(i).getVotesPercent() + "%");

                totalVote += Integer.parseInt(mPollListResponse.getData().getOptions().get(i).getVotesReceived());

                if (maxIndex == i && totalVote > 0) {
                    pollOptionTxt.setTypeface(Typeface.DEFAULT_BOLD);
                    pollOptionPercentTxt.setTypeface(Typeface.DEFAULT_BOLD);
                    pollBackgroundView.setBackground(getResources().getDrawable(R.drawable.poll_round_blue));
                } else {
                    pollOptionTxt.setTypeface(Typeface.DEFAULT);
                    pollOptionPercentTxt.setTypeface(Typeface.DEFAULT);
                    pollBackgroundView.setBackground(getResources().getDrawable(R.drawable.poll_round_grey));
                }

                mCommunicationPollLinear.addView(someView);

            }
            mCommunicationPollLinear.addView(mPollEnddateTxt);
            mPollEnddateTxt.setPadding(0, 15, 15, 15);
            mPollEnddateTxt.setText("Total Votes: " + totalVote);
            mPollEnddateTxt.setBackgroundColor(Color.TRANSPARENT);
            mPollEnddateTxt.setTypeface(Typeface.DEFAULT_BOLD);
            mPollEnddateTxt.setTextColor(Color.BLACK);

        } else {
            mPollEnddateTxt.setPadding(0, 15, 15, 15);
            mPollEnddateTxt.setText("Poll result on: " + Helper.formateTimeMillis(mPost.getPollResultHoursLong(), "HH:mm | dd MMM yyyy"));
            mPollEnddateTxt.setTypeface(Typeface.DEFAULT_BOLD);
            mPollEnddateTxt.setBackgroundColor(Color.TRANSPARENT);
            mCommunicationPollOptionRadiogrp.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 8, 0, 8);
            for (int i = 0; i < mPollListResponse.getData().getOptions().size(); i++) {
                final RadioButton radioButton = new RadioButton(this);
                mCommunicationPollOptionRadiogrp.addView(radioButton); //the RadioButtons are added to the radioGroup instead of the layout

                radioButton.setLayoutParams(params);
                radioButton.setId(Integer.parseInt(mPollListResponse.getData().getOptions().get(i).getId()));
                radioButton.setText(mPollListResponse.getData().getOptions().get(i).getText());
                radioButton.setTextSize(17);
                radioButton.setButtonDrawable(getResources().getDrawable(R.drawable.radio_button));

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    radioButton.setPadding(20, 20, 20, 20);
                }
            }

            mCommunicationPollOptionRadiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for (int i = 0; i < mCommunicationPollOptionRadiogrp.getChildCount(); i++) {
                        RadioButton radiobtn = (RadioButton) mCommunicationPollOptionRadiogrp.getChildAt(i);
                        if (radiobtn.isChecked()) {
                            radiobtn.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        } else {
                            radiobtn.setTextColor(Color.BLACK);
                        }
                    }

                }
            });

            if (mPollListResponse.getData().getPollVoted() == 1) {
                mCommunicationPollTxt.setText(getResources().getString(R.string.your_opinion));
                mCommunicationPollLinear.removeView(mPollSubmitBtn);
                for (int i = 0; i < mCommunicationPollOptionRadiogrp.getChildCount(); i++) {
                    RadioButton radiobtn = (RadioButton) mCommunicationPollOptionRadiogrp.getChildAt(i);
                    if (radiobtn.getId() == Integer.parseInt(mPollListResponse.getData().getVotedOption())) {
                        radiobtn.setButtonDrawable(getResources().getDrawable(R.drawable.ic_radio_on));
                        radiobtn.setTextColor(Color.parseColor("#0c67c2"));
                        radiobtn.setEnabled(false);
                    } else {
                        radiobtn.setEnabled(false);
                        radiobtn.setTextColor(Color.BLACK);
                    }
                }
            }
        }
    }

    /**
     * Display author picture, update time, description and comments visibility
     */
    private void updateAlbumData() {
        invalidateOptionsMenu();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mPost.getAuthor());

            String authourPicUrl = mPost.getAuthorPictureUrl() + Flinnt.PROFILE_MEDIUM + File.separator + mPost.getAuthorPicture();
            postByImg.setImageUrl(authourPicUrl, mImageLoader);
        }
        communicationNameTxt.setText(mPost.getAuthor());
        if (!TextUtils.isEmpty(mPost.getPublishDate())) {
            if ((Long.parseLong(mPost.getPublishDate())) > (System.currentTimeMillis() / 1000)) {
                communicationDateTxt.setText("not yet");
            } else
                communicationDateTxt.setText(Helper.formateDate(Long.parseLong(mPost.getPublishDate())));
        }

        postDescriptionTxtview.setText(mPost.getDescription());
        showHideButtons();
    }

    /**
     * Updates attached photos view and display it
     */
    private void albumMediaUpdate() {
        boolean isExist = false;
        String photos = mPost.getAttachments();

        String[] photosArr = photos.split(",");
        if (photosArr.length > 0) {
            albumMediaRelative.setVisibility(View.VISIBLE);

            imagesName.clear();
            if (isExist) {
            } else {
                Flinnt.appInfoDataSets.clear();
                for (int i = 0; i < photosArr.length; i++) {
                    final String imageFileName = photosArr[i].trim();
                    if (!TextUtils.isEmpty(imageFileName)) {
                        imagesName.add(imageFileName);
                        String urlNoCrop = mPost.getAttachmentUrl() + Flinnt.GALLERY_NOCROP + File.separator + imageFileName;
                        final String urlMobile = mPost.getAttachmentUrl() + Flinnt.GALLERY_MOBILE + File.separator + imageFileName;
                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mPost.getPostID(), imageFileName, urlMobile, urlNoCrop, Flinnt.DISABLED);
                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                        Flinnt.appInfoDataSets.add(appInfoDataSet);
                    }
                }
            }
        } else {
            albumMediaRelative.setVisibility(View.GONE);
        }
        /**
         * Set album data to recyclerview adapter.
         */

        for (AppInfoDataSet info : Flinnt.appInfoDataSets) {
            DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
            if (downloadInfo != null) {
                info.setProgress(downloadInfo.getProgress());
                info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                info.setStatus(AppInfoDataSet.STATUS_PAUSED);
            }
        }

        mAlbumRecycler.setAdapter(mAlbumAdapter);
        mAlbumAdapter.setData(Flinnt.appInfoDataSets);
    }


    public void showCourseDeletedDialog(String errorMessage) {
        if (!isCourseDeletedDialogShowing) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
     * Updates the post data
     */
    private void updatePostData() {
        try {
            invalidateOptionsMenu();

            if (mPost.getAllowRepost().equalsIgnoreCase(Flinnt.DISABLED)) {
                repostImg.setVisibility(View.INVISIBLE);
            } else {
                repostImg.setVisibility(View.VISIBLE);
            }


            if (null != getSupportActionBar()) {
                getSupportActionBar().setTitle(courseName);
            }

            communicationNameTxt.setText(mPost.getAuthor());

            String authourPicUrl = mPost.getAuthorPictureUrl() + Flinnt.PROFILE_MEDIUM + File.separator + mPost.getAuthorPicture();
            postByImg.setImageUrl(authourPicUrl, mImageLoader);

            if (mPost.getIsLike().equalsIgnoreCase(Flinnt.ENABLED)) {
                likePostImg.setImageResource(R.drawable.ic_like_blue);
                mPostLikeTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                isLiked = true;
            } else {
                likePostImg.setImageResource(R.drawable.ic_like_grey);
                mPostLikeTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                isLiked = false;
            }

            postTitleTxtview.setText(mPost.getTitle());
            if (!TextUtils.isEmpty(mPost.getPublishDate())) {
                if ((Long.parseLong(mPost.getPublishDate())) > (System.currentTimeMillis() / 1000)) {
                    communicationDateTxt.setText("not yet");
                } else
                    communicationDateTxt.setText(Helper.formateTimeMillis(Long.parseLong(mPost.getPublishDate())));
            }
            if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                postDescriptionTxtview.setText("Poll: " + mPost.getDescription());
            } else {
                postDescriptionTxtview.setText(mPost.getDescription());
            }

            postMediaUpdate();

            showHideButtons();

        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.POST_DETAIL + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseID + "&post=" + mPost.getPostID());
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void showHideButtons() {
        if (canComment.equalsIgnoreCase(Flinnt.ENABLED) && postType != Flinnt.POST_TYPE_POLL) {
            addCommentLayout.setVisibility(View.VISIBLE);
        } else {
            addCommentLayout.setVisibility(View.GONE);
        }

        if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL || mPost.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE || mPost.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
            bookmarkItem.setVisible(false);
        } else {
            if (mPost.isBookmark.equalsIgnoreCase(Flinnt.ENABLED)) {
                bookmarkItem.setTitle(getResources().getString(R.string.unbookmark_txt));
            } else {
                bookmarkItem.setTitle(getResources().getString(R.string.bookmark_txt));
            }
        }
        if (mPost.canEdit.equalsIgnoreCase(Flinnt.ENABLED)) {
            editItem.setVisible(true);
        } else {
            editItem.setVisible(false);
        }

        if (mPost.canDeletePost.equalsIgnoreCase(Flinnt.ENABLED)) {
            deleteItem.setVisible(true);
        } else {
            deleteItem.setVisible(false);
        }
        if (mPost.getAllowRepost().equalsIgnoreCase(Flinnt.DISABLED)) {
            repostImg.setVisibility(View.INVISIBLE);
        } else {
            repostImg.setVisibility(View.VISIBLE);
        }

        if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
            messageLinear.setVisibility(View.VISIBLE);
            likeLinear.setVisibility(View.GONE);
            String msgToUser = "";
            List<String> items = Arrays.asList(mPost.getMessageToUsers().split("\\s*,\\s*"));
            for (int i = 0; i < items.size(); i++) {
                String temp = items.get(i);
                String[] valueRoleId = temp.split("\\|");
                msgToUser = msgToUser + valueRoleId[0] + ",";
            }
            msgToUser = msgToUser.substring(0, msgToUser.length() - 1);
            messageTxt.setVisibility(View.VISIBLE);
            messageTxt.setText("To: " + msgToUser);
            messageTxt.post(new Runnable() {
                @Override
                public void run() {
                    int moreLine = messageTxt.getLineCount();
                    if (moreLine > 2) {
                        mPost.setViewMoreLess(Flinnt.SHOWMORE);
                        messageTxt.setMaxLines(2);
                        messageTxt.setVisibility(View.VISIBLE);
                        messageViewMoreTxt.setText(getResources().getString(R.string.view_more));
                    } else {
                        messageViewMoreTxt.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            messageLinear.setVisibility(View.GONE);
        }

        if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
            mLikeViewCommentLinear.setVisibility(View.GONE);
        } else if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
            mLikeViewCommentLinear.setVisibility(View.VISIBLE);
            mViewsTxt.setText(mPost.getTotalViews() + " Views");
            mLikeTxt.setVisibility(View.GONE);
            mCommentTxt.setText(mPost.getTotalComments() + " Comments");
        } else {
            mLikeViewCommentLinear.setVisibility(View.VISIBLE);
            mViewsTxt.setText(mPost.getTotalViews() + " Views");
            mLikeTxt.setText(mPost.getTotalLikes() + " Likes");
            mCommentTxt.setText(mPost.getTotalComments() + " Comments");
        }
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

        mylinearLayoutManager = new LinearLayoutManager(this);
        postCommentsRecycleview.setLayoutManager(mylinearLayoutManager);

        mPostCommentsAdapter = new PostCommentsAdapter(mCommentItems);
        postCommentsRecycleview.setAdapter(mPostCommentsAdapter);

//        mEndlessRecyclerOnScrollListenerComments = new CommentEndlessRecyclerOnScrollListener(mylinearLayoutManager, false) {
//            @Override
//            public void onLoadMore() {
//                if (LogWriter.isValidLevel(Log.INFO))
//                    LogWriter.write("onLoadMore : has_more : " + mPostComments.getLastResponse().getHasMore());
//                if (mPostComments.getLastResponse().getHasMore() > 0) {
//                    // Reset offset to new request - New offset = old offset + max
//                    mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + mPostCommentsRequest.getMaxFetch());
//                    mPostComments.sendPostCommentsRequest(mPostCommentsRequest);
//                }
//            }
//        };


        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
                                //Log.d("TAGload", String.valueOf(mPostCommentsAdapter.getItemCount()));

                                loading = false;
                                if (mPostComments.getLastResponse().getHasMore() > 0) {
                                    // Reset offset to new request - New offset = old offset + max
                                    mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + mPostCommentsRequest.getMaxFetch());
                                    mPostComments.sendPostCommentsRequest(mPostCommentsRequest);
                                }
                            }
                        }
                    }
                }
            }
        });

        // postCommentsRecycleview.addOnScrollListener(mEndlessRecyclerOnScrollListenerComments);

        mPostCommentsAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mPostCommentsAdapter.getItem(position) != null) {
                    if (mPostCommentsAdapter.getItem(position).getCanDelete() == Flinnt.TRUE) {
                        showDeleteCommentWarning(PostDetailActivity.this,
                                mPostCommentsAdapter, mPostCommentsAdapter.getItem(position));
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
    public void showDeleteCommentWarning(final Context context, final PostCommentsAdapter mAdapter, final Comment comment) {

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
                        commentPostImg.setImageResource(R.drawable.ic_comment_grey);
                        mPostCommentTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                        Helper.showToast("Successfully deleted", Toast.LENGTH_SHORT);
                        updateCommentButton();
                        commentCountFunction(Flinnt.INVALID);
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((DeletePostResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(PostDetailActivity.this, "Error", ((DeletePostResponse) message.obj).errorResponse.getMessage(), "CLOSE");
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
                    String commentID = comment.getPostCommentID();
                    new DeleteComment(dialogHandler, commentID).sendDeleteCommentRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(PostDetailActivity.this);
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
     * @param postCommentsResponse
     */
    private void updateCommentsList(PostCommentsResponse postCommentsResponse) {
        mPostCommentsAdapter.addItems(postCommentsResponse.getCommentList());
        isNeedToGetComment = false;
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

        mPostCommentsAdapter.add(0/*mPostCommentsAdapter.getItemCount()*/, comment);
        postCommentsRecycleview.scrollToPosition(0/*mPostCommentsAdapter.getItemCount() - 1*/);
        mPostCommentsRequest.setOffSet(mPostCommentsRequest.getOffSet() + 1);
    }

    /**
     * Post media attachments update
     */
    private void postMediaUpdate() {
        mediaOpenImgbtn.setVisibility(View.GONE);
        postFrame.setVisibility(View.GONE);
        youtubeFrame.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mPost.getAttachmentUrl()) &&
                TextUtils.isEmpty(mPost.getAttachments())) {
            postMediaRelative.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            postLinkTextview.setVisibility(View.GONE);
        } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_LINK) {
            postMediaRelative.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            postLinkTextview.setVisibility(View.VISIBLE);
            postLinkTextview.setText(mPost.getAttachments());
        } else {
            postMediaRelative.setVisibility(View.VISIBLE);
            postLinkTextview.setVisibility(View.GONE);
            postMediaAudioLayout.setVisibility(View.GONE);
            String fileExtention = Helper.getExtension(mPost.getAttachments());
            int defaultDrawable = Helper.getDefaultPostImageFromType(mPost.getPostContentTypeInt(), fileExtention);

            if ( /*mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_AUDIO ||*/
                    mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO) {
                mediaOpenImgbtn.setVisibility(View.VISIBLE);
            }

            if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_AUDIO) {
                postMediaRelative.setVisibility(View.GONE);
                postMediaAudioLayout.setVisibility(View.VISIBLE);
            }

            if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO && mPost.getAttachmentsIsUrl().equalsIgnoreCase(Flinnt.ENABLED)) {
                postYoutubeVedioID = null;
                String youtubeUrl = mPost.getAttachments();

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
                    postYouTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, this);
                    postFrame.setVisibility(View.GONE);
                    mediaOpenImgbtn.setVisibility(View.GONE);
                    youtubeFrame.setVisibility(View.VISIBLE);
                } else {
                    isAppNotInstalled();
                }

            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO) {

                mediaOpenImgbtn.setVisibility(View.GONE);
                youtubeFrame.setVisibility(View.GONE);

                Flinnt.appInfoDataSets.clear();
                String videourl = mPost.getAttachmentUrl() + mPost.getAttachments();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mPost.getPostID(), mPost.getAttachments(), videourl, videourl, Flinnt.DISABLED);
                appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                Flinnt.appInfoDataSets.add(appInfoDataSet);

                for (AppInfoDataSet info : Flinnt.appInfoDataSets) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                    if (downloadInfo != null) {
                        info.setProgress(downloadInfo.getProgress());
                        info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }
                }

                if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), Flinnt.appInfoDataSets.get(0).getName()) && Flinnt.appInfoDataSets.get(0).getStatusText().equals("Not Download")) {
                    postFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        postFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }

                //******************************************************
                String videoPreviewUrl = mPost.getVideoPreview() + mPost.getVideoPreview();
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("video Preview Url : " + videoPreviewUrl);
                mImageLoader.get(videoPreviewUrl, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.video_default, R.drawable.video_default));
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_GALLERY) {
                String urlNoCrop = mPost.getAttachmentUrl() + Flinnt.GALLERY_NOCROP + File.separator + mPost.getAttachments();
                String urlMobile = mPost.getAttachmentUrl() + Flinnt.GALLERY_MOBILE + File.separator + mPost.getAttachments();
                mImageLoader.get(urlMobile, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.album_default, R.drawable.album_default));

                Flinnt.appInfoDataSets.clear();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mPost.getPostID(), mPost.getAttachments(), urlMobile, urlNoCrop, Flinnt.DISABLED);
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
                    postFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        postFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }

            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_DOCUMENT) {
                mediaThumnailImgview.setImageDrawable(Helper.getDrawable(PostDetailActivity.this, defaultDrawable));

                Flinnt.appInfoDataSets.clear();
                String docUrl = mPost.getAttachmentUrl() + mPost.getAttachments();
                AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mPost.getPostID(), mPost.getAttachments(), docUrl, docUrl, Flinnt.DISABLED);
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
                    postFrame.setVisibility(View.GONE);
                } else {
                    if (appInfoDataSet.getStatusText().equals("Complete")) {
                        postFrame.setVisibility(View.GONE);
                    } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                    } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                        postFrame.setVisibility(View.VISIBLE);
                        postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                        postProgressBar.setProgress(appInfoDataSet.getProgress());
                    }
                }
            } else {
                mediaThumnailImgview.setImageDrawable(Helper.getDrawable(PostDetailActivity.this, defaultDrawable));
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        //Log.d("Postt", "onBackPresed isNotifricaton : " + isFromNotification);
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            //Log.d("Postt", "onBackPresed mediaPlayer : " + isFromNotification);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (postFullScreen) {
            //Log.d("Postt", "onBackPresed postFullScreen : " + isFromNotification);
            postYouTubePlayer.setFullscreen(false);
        } else {

            //Log.d("Postt", "onBackPresed else : " + isFromNotification);

            if (isFromNotification >= Flinnt.TRUE) {//@Chirag:16/08/2018 added 1:04

                //********@Chirag: 16/08/2018
                Intent intent = new Intent(this, CourseDetailsActivity.class);
                intent.putExtra(Course.COURSE_ID_KEY, courseID);
                intent.putExtra(Course.COURSE_NAME_KEY, courseName);
                intent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                intent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
                intent.putExtra(Flinnt.NEXT_SCREENID, isFromNotification);//@Chirag: 16/08/2018
                intent.putExtra("isFromNotification", isFromNotification);
                intent.putExtra("isPostDeleted", isPostDeleted);
                intent.putExtra("comeFrom", "PostDetailActivity");
                intent.putExtra("isCourseRemoved", isCourseRemoved);
                intent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRole);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//
                startActivity(intent);
                finish();
            } else {
                //Log.d("Postt", "isFromNotification is false : " + isFromNotification);
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                upIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                upIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                upIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
                upIntent.putExtra("isFromNotification", isFromNotification);
                upIntent.putExtra("isPostDeleted", isPostDeleted);
                upIntent.putExtra("comeFrom", "PostDetailActivity");
                upIntent.putExtra("isCourseRemoved", isCourseRemoved);
                upIntent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRole);

                upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //@Chirag: 16/08/2018
                //upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//@Chirag:16/08/2018 added
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    //Log.d("Postt", "onBackPresed shouldUpReCreate true : " + isFromNotification);
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    //Log.d("Postt", "onBackPresed shouldUpReCreate else : " + isFromNotification);
                    //***********change v2.0.27

                    if (!bookMarkStatus.equals("")) {
                        //Log.d("Postt", "onBackPresed shouldUpReCreate bookmark true : " + isFromNotification);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("BookMark", bookMarkStatus);
                        setResult(Activity.RESULT_OK, resultIntent);
                    }

                    //Log.d("Postt", "onBackPresed last in else : " + isFromNotification);
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);

                    //Log.d("Postt", "onBackPresed last in else for my test : " + isFromNotification);

                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.message_recipients_view_more) {
            if (mPost.getViewMoreLess().equals(Flinnt.SHOWMORE)) {
                mPost.setViewMoreLess(Flinnt.SHOWLESS);
                messageTxt.setMaxLines(Integer.MAX_VALUE);
                messageViewMoreTxt.setText(getResources().getString(R.string.view_less));
            } else if (mPost.getViewMoreLess().equals(Flinnt.SHOWLESS)) {
                mPost.setViewMoreLess(Flinnt.SHOWMORE);
                messageTxt.setMaxLines(2);
                messageViewMoreTxt.setText(getResources().getString(R.string.view_more));
            }
        } else if (v.getId() == R.id.post_media_open_btn_postdetailview) {
            if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO && mPost.getAttachmentsIsUrl() == Flinnt.ENABLED) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + postYoutubeVedioID));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + postYoutubeVedioID));
                        startActivity(intent);
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_AUDIO) {
                if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mPost.getAttachments())) {
                    String audiourl = mPost.getAttachmentUrl() + mPost.getAttachments();
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("audiourl : " + audiourl);
                    if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                        setDownloadMediaFileandHandler();
                        mDownload = new DownloadMediaFile(PostDetailActivity.this, Helper.getFlinntAudioPath(), mPost.getAttachments(), Long.parseLong(postID), audiourl, mHandler);
                        mDownload.execute();
                        setDownloadProgressDialog();
                    }
                } else {
                    startAudioIntent();

                }
            }
        } else if (v.getId() == R.id.post_media_thumbnail_postdetailview) {
            if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_GALLERY) {
                try {

                    if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                        String filename = Helper.getFlinntImagePath() + mPost.getAttachments();
                        if (new File(filename).exists()) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(filename)), "image/*");
                            startActivity(intent);
                        } else {
                            Helper.showAlertMessage(PostDetailActivity.this, "Image", "This image is not downloaded yet", "Ok");
                        }
                    }
                } catch (Exception e) {
                }
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO && mPost.getAttachmentsIsUrl() != Flinnt.ENABLED) {
                if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), mPost.getAttachments())) {
                        String videourl = mPost.getAttachmentUrl() + mPost.getAttachments();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("videourl : " + videourl);
                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(mPost.getPostID(), mPost.getAttachments(), videourl, videourl, Flinnt.DISABLED);
                        download(0, videourl, appInfoDataSet);
                    } else {
                        startVideoIntent();
                    }
                }
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_DOCUMENT) {
                if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mPost.getAttachments())) {
                        String docurl = mPost.getAttachmentUrl() + mPost.getAttachments();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("docurl : " + docurl);

                        setDownloadMediaFileandHandler();
                        mDownload = new DownloadMediaFile(PostDetailActivity.this, Helper.getFlinntDocumentPath(), mPost.getAttachments(), Long.parseLong(postID), docurl, mHandler);
                        mDownload.execute();
//                    }
                        setDownloadProgressDialog();

                    } else {
                        startDocumentIntent();
                    }
                }
            }
        } else if (v.getId() == R.id.postFrame) {
            if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_GALLERY) {
                if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), mPost.getAttachments())) {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        postProgressBarHint.setVisibility(View.VISIBLE);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                    } else {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        }

                    }
                }
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO) {
                if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), mPost.getAttachments())) {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        postProgressBarHint.setVisibility(View.VISIBLE);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                    } else {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        }

                    }
                }
            } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_DOCUMENT) {
                if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mPost.getAttachments())) {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        postProgressBarHint.setVisibility(View.VISIBLE);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                    } else {
                        AppInfoDataSet appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                        if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            pause(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        } else {
                            postProgressBarHint.setVisibility(View.VISIBLE);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        }
                    }
                }
            }
        } else if (v.getId() == R.id.post_link_textview) {
            MediaHelper.ShowLink(mPost.getAttachments(), mCommon);
        } else if (v.getId() == R.id.post_send_comment_postdetailview) {
            String commentStr = commentTxt.getText().toString();
            //*****change 31
            if (!TextUtils.isEmpty(commentStr.trim())) {
                if (Helper.isConnected()) {
                    new AddComment(mHandler, courseID, postID, commentStr).sendAddCommentRequest();
                    startProgressDialog();
                    Helper.hideKeyboardFromWindow(PostDetailActivity.this);
                } else {
                    Helper.showNetworkAlertMessage(PostDetailActivity.this);
                }
            } else {
                Helper.showToast(getString(R.string.comment_blank), Toast.LENGTH_SHORT);
            }
        } else if (v.getId() == R.id.like_linear) {
            if (Helper.isConnected()) {
                mLikeBookmarkRequest = new LikeBookmarkRequest();

                mLikeBookmarkRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mLikeBookmarkRequest.setPostID(postID);

                if (isLiked) {
                    mLikeBookmark = new LikeBookmark(mHandler, LikeBookmark.POST_DISLIKE);
                    likePostImg.setImageResource(R.drawable.ic_like_blue);
                    mPostLikeTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    isLiked = false;
                } else {
                    mLikeBookmark = new LikeBookmark(mHandler, LikeBookmark.POST_LIKE);
                    likePostImg.setImageResource(R.drawable.ic_like_grey);
                    mPostLikeTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                    isLiked = true;
                }

                mLikeBookmark.setLikeBookmarkRequest(mLikeBookmarkRequest);
                mLikeBookmark.sendLikeBookmarkRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(PostDetailActivity.this);
            }
        } else if (v.getId() == R.id.comment_linear) {
            if (Helper.isConnected()) {
                Intent commentActivity = new Intent(PostDetailActivity.this, CommunicationCommentActivity.class);
                commentActivity.putExtra(Course.COURSE_ID_KEY, courseID);
                commentActivity.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                commentActivity.putExtra("REQUEST_FOCUS", Flinnt.ENABLED);
                commentActivity.putExtra(PostListMenuResponse.CAN_COMMENT_KEY, Integer.parseInt(canComment));
                startActivityForResult(commentActivity, POST_DETAIL_COMMENT_CALL_BACK);
            } else {
                Helper.showNetworkAlertMessage(PostDetailActivity.this);
            }
        } else if (v.getId() == R.id.post_audio_play_postdetailview) {
            if (AskPermition.getInstance(PostDetailActivity.this).isPermitted()) {
                if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mPost.getAttachments())) {
                    String audiourl = mPost.getAttachmentUrl() + mPost.getAttachments();
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("audiourl : " + audiourl);

                    setDownloadMediaFileandHandler();
//                if (mDownload == null) {
                    mDownload = new DownloadMediaFile(PostDetailActivity.this, Helper.getFlinntAudioPath(), mPost.getAttachments(), Long.parseLong(postID), audiourl, mHandler);
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
        } else if (v.getId() == R.id.repost_img) {
            if (Helper.isConnected()) {
                Intent postRepost = new Intent(PostDetailActivity.this, SelectCourseActivity.class);

                if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                    postRepost.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_POLL);
                    postRepost.putExtra("PollOptions", mPollListResponse);
                    postRepost.putExtra(Post.POLL_RESULT_HOURS_KEY, mPost.getPollResultHours());
                    postRepost.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, mPost.getAttachments());

                } else {

                    postRepost.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, mPost.getPostContentTypeInt());
                    postRepost.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, mPost.getAttachmentsIsUrl());
                    if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_BLOG) {
                        postRepost.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_BLOG);
                        postRepost.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, mPost.getAttachments());
                    } else if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                        String photos = mPost.getAttachments();
                        ArrayList<String> imagesName = new ArrayList<String>();
                        String[] photosArr = photos.split(",");
                        if (photosArr.length > 0) {
                            for (int i = 0; i < photosArr.length; i++) {
                                final String imageFileName = photosArr[i].trim();
                                if (!TextUtils.isEmpty(imageFileName)) {
                                    imagesName.add(imageFileName);
                                }
                            }
                        }
                        postRepost.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_ALBUM);
                        postRepost.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, imagesName);
                    }
                }

                postRepost.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_REPOST);
                postRepost.putExtra("class_name", "DetailActivity");
                postRepost.putExtra(Course.COURSE_ID_KEY, courseID);
                postRepost.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                postRepost.putExtra(CourseInfo.COURSE_NAME_KEY, courseName);
                postRepost.putExtra(PostDetailsResponse.TITLE_KEY, mPost.getTitle());
                postRepost.putExtra(PostDetailsResponse.DESCRIPTION_KEY, mPost.getDescription());
                postRepost.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, mPost.getPostContentTypeInt());
                postRepost.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, mPost.getAttachmentsIsUrl());
                postRepost.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, mPost.getAttachmentUrl());

                startActivityForResult(postRepost, SELECT_COURSE_CALL_BACK);
            } else {
                Helper.showNetworkAlertMessage(PostDetailActivity.this);
            }


        } else if (v.getId() == R.id.communication_poll_submit_btn) {
            if (mCommunicationPollOptionRadiogrp.getCheckedRadioButtonId() != -1) {
                selectedID = String.valueOf(mCommunicationPollOptionRadiogrp.getCheckedRadioButtonId());

                mPollVoteRequest.setOptionID(selectedID);
                mPollVoteRequest.setCourseID(courseID);
                mPollVoteRequest.setPostID(postID);
                mPollVoteRequest.setUserID(Config.getStringValue(Config.USER_ID));
                PollVote pollVote = new PollVote(mHandler, mPollVoteRequest);
                pollVote.sendPollVoteRequest();
            } else {
                Helper.showToast(getResources().getString(R.string.select_poll_option), Toast.LENGTH_LONG);
            }
        } else if (v.getId() == R.id.views_txt) {
            if (!Helper.isConnected()) {
                Helper.showNetworkAlertMessage(this);
            } else {
                Intent mViewsIntext = new Intent(PostDetailActivity.this, PostViewersSendMessageActivity.class);
                mViewsIntext.putExtra(Course.COURSE_ID_KEY, courseID);
                mViewsIntext.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                mViewsIntext.putExtra(Course.COURSE_NAME_KEY, courseName);
                mViewsIntext.putExtra("SELECTED_TAB", "viewers");
                mViewsIntext.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                startActivity(mViewsIntext);
            }
        } else if (v.getId() == R.id.likes_txt) {
            if (!Helper.isConnected()) {
                Helper.showNetworkAlertMessage(this);
            } else {
                Intent mLikesIntext = new Intent(PostDetailActivity.this, PostViewersSendMessageActivity.class);
                mLikesIntext.putExtra(Course.COURSE_ID_KEY, courseID);
                mLikesIntext.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                mLikesIntext.putExtra(Course.COURSE_NAME_KEY, courseName);
                mLikesIntext.putExtra("SELECTED_TAB", "likes");
                mLikesIntext.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                startActivity(mLikesIntext);
            }
        } else if (v.getId() == R.id.comments_txt) {

            if (!Helper.isConnected()) {
                Helper.showNetworkAlertMessage(this);
            } else {
                Intent mCommentIntent = new Intent(PostDetailActivity.this, PostViewersSendMessageActivity.class);
                mCommentIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                mCommentIntent.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                mCommentIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                mCommentIntent.putExtra("SELECTED_TAB", "comments");
                mCommentIntent.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                startActivity(mCommentIntent);
            }
        }
    }

    /**
     * .Intent to get video
     */
    private void startVideoIntent() {
        try {
            String filename = Helper.getFlinntVideoPath() + mPost.getAttachments();
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
            String audioFile = Helper.getFlinntAudioPath() + mPost.getAttachments();
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
                    audioPlayButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_play_arrow_white));
                }
            });
        }
    }

    private void startDocumentIntent() {
        try {
            String filename = Helper.getFlinntDocumentPath() + mPost.getAttachments();
            MediaHelper.showDocument(filename, PostDetailActivity.this);
        } catch (Exception e) {
        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(PostDetailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(PostDetailActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    private void updateLikeCommentButtons(LikeBookmarkResponse response) {

        if (isLiked) {
            if (response.getIsLiked() == Flinnt.TRUE) {
                int likeCount = Integer.parseInt(mPost.getTotalLikes()) + 1;
                mPost.setTotalLikes("" + likeCount);
                mLikeTxt.setText(mPost.getTotalLikes() + " Likes");
                likePostImg.setImageResource(R.drawable.ic_like_blue);
                mPostLikeTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                isLiked = true;
            }
        } else {
            if (response.getIsDisliked() == Flinnt.TRUE) {
                int likeCount = Integer.parseInt(mPost.getTotalLikes()) - 1;
                mPost.setTotalLikes("" + likeCount);
                mLikeTxt.setText(mPost.getTotalLikes() + " Likes");
                likePostImg.setImageResource(R.drawable.ic_like_grey);
                mPostLikeTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                isLiked = false;
            }
        }

        if (response.getIsBookmarked() == Flinnt.TRUE) {
            bookmarkItem.setTitle(getResources().getString(R.string.unbookmark_txt));
            mPost.setIsBookmark(Flinnt.ENABLED);
        } else if (response.getIsBookmarked() == Flinnt.FALSE) {
            bookmarkItem.setTitle(getResources().getString(R.string.bookmark_txt));
            mPost.setIsBookmark(Flinnt.DISABLED);
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
            LogWriter.write("Request code Postdetails : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case SELECT_COURSE_CALL_BACK:
                        onBackPressed();
                        break;

                    case AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK:
                        startProgressDialog();
                        ArrayList<String> postIdList = new ArrayList<>();
                        postIdList.add(postID);
                        mPostList = new PostList(mHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ, postIdList, true);
                        mPostList.sendPostListRequest(mPostListRequest);
                        break;

                    case POST_DETAIL_COMMENT_CALL_BACK:
                        int commentCount = Integer.parseInt(data.getStringExtra("CommentCount"));
                        if (data.getStringExtra("Comment").equals("not exist")) {
                            commentPostImg.setImageResource(R.drawable.ic_comment_grey);
                            mPostCommentTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                        } else {
                            commentPostImg.setImageResource(R.drawable.ic_comment_blue);
                            mPostCommentTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        }
                        mPostComments = null;
                        mCommentItems.clear();
                        updateCommentsData();
                        commentCountFunction(commentCount);

                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void commentCountFunction(int commentCount) {
        int listCommentCount = (Integer.parseInt(mPost.getTotalComments()));
        if (commentCount > 0) {
            mPost.setTotalComments("" + (listCommentCount + commentCount));
        } else {
            mPost.setTotalComments("" + (listCommentCount - Math.abs(commentCount)));
        }
        mCommentTxt.setText(mPost.getTotalComments() + " Comments");
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        try {
            mProgressDialog = new ProgressDialog(PostDetailActivity.this);
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
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("DownloadProgressDialog exception : " + e.getMessage());
        }

    }

//    @Override
//    public void onBottomReached() {
//        // TODO Auto-generated method stub
//        if (mEndlessRecyclerOnScrollListenerComments != null && !isNeedToGetComment) {
//            isNeedToGetComment = true;
//            mEndlessRecyclerOnScrollListenerComments.onLoadMore();
//        }
//    }

    /**
     * Set download handler
     */
    private void setDownloadMediaFileandHandler() {
        if (mDownload == null) {
            long postID = Long.parseLong(mPost.getPostID());
            if (DownloadFileManager.isContainID(postID)) {
                mDownload = DownloadFileManager.get(postID);
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
        mReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(PostDetailActivity.this).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(PostDetailActivity.this).unregisterReceiver(mReceiver);
        }
    }

    private void download(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentDownload(PostDetailActivity.this, position, position, tag, info);
    }

    private void pause(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentPause(PostDetailActivity.this, position, position, tag, info);
    }

    private void cancel(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentCancel(PostDetailActivity.this, position, tag, info);
    }


    private void pauseAll(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentPauseAll(PostDetailActivity.this, position, tag, info);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(postYoutubeVedioID);
            postYouTubePlayer = youTubePlayer;
            postYouTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean _isFullScreen) {
                    postFullScreen = _isFullScreen;
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
        postFrame.setVisibility(View.GONE);
        youtubeFrame.setVisibility(View.GONE);
        int defaultDrawable = R.drawable.youtube_video_fram_not_get;
        mediaOpenImgbtn.setVisibility(View.VISIBLE);
        String url = "http://img.youtube.com/vi/" + postYoutubeVedioID + "/0.jpg";
        mImageLoader.get(url, ImageLoader.getImageListener(mediaThumnailImgview, defaultDrawable, defaultDrawable));
    }

    @Override
    public void onItemClick(View v, final int position, final AppInfoDataSet appInfoDataSet) {
        if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
            pause(position, appInfoDataSet.getUrl(), appInfoDataSet);
        } else if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_COMPLETE) {

        } else if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CANCEL) {

        } else {
            download(position, appInfoDataSet.getUrl(), appInfoDataSet);
        }
    }

    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String action = intent.getAction();
                if (action == null || !action.equals(DownloadService.ACTION_DOWNLOAD_BROAD_CAST)) {
                    return;
                }
                final int position = intent.getIntExtra(DownloadService.EXTRA_POSITION, -1);
                final AppInfoDataSet tmpInfoDataSet = (AppInfoDataSet) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);
                if (mPost.getPostID().equals(tmpInfoDataSet.getId())) {
                    if (tmpInfoDataSet == null || position == -1) {
                        return;
                    }

                    AppInfoDataSet appInfoDataSet;

                    if (postType == Flinnt.POST_TYPE_ALBUM) {
                        appInfoDataSet = Flinnt.appInfoDataSets.get(position);
                    } else {
                        appInfoDataSet = Flinnt.appInfoDataSets.get(0);
                    }
                    final int status = tmpInfoDataSet.getStatus();
                    if (postType == Flinnt.POST_TYPE_ALBUM) {

                        switch (status) {
                            case AppInfoDataSet.STATUS_CONNECTING:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);

                                    if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), tmpInfoDataSet.getName())) {
                                        cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                                        holder.albumProgressBarHint.setVisibility(View.GONE);
                                        mAlbumAdapter.notifyDataSetChanged();
                                    } else {
                                    }
                                }
                                break;

                            case AppInfoDataSet.STATUS_DOWNLOADING:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                    holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.downloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                    holder.albumProgressBarHint.setVisibility(View.GONE);
                                }
                                break;
                            case AppInfoDataSet.STATUS_COMPLETE:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());

                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                    holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.albumFrame.setVisibility(View.GONE);
                                }
                                break;

                            case AppInfoDataSet.STATUS_PAUSED:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                    holder.downloadBtn.setBackgroundResource(R.drawable.ic_play);
                                    holder.albumProgressBarHint.setVisibility(View.GONE);
                                }
                                break;
                            case AppInfoDataSet.STATUS_NOT_DOWNLOAD:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                    holder.downloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
                                    holder.albumProgressBarHint.setVisibility(View.GONE);
                                }
                                break;
                            case AppInfoDataSet.STATUS_DOWNLOAD_ERROR:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
                                appInfoDataSet.setDownloadPerSize("");
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                }
                                break;
                            case AppInfoDataSet.STATUS_CANCEL:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                if (isCurrentListViewItemVisible(position)) {
                                    PostDetailRecyclerViewAdapter.AppViewHolder holder = getViewHolder(position);
                                    holder.downloadBtn.setBackgroundResource(R.drawable.ic_download);
                                    holder.albumProgressBar.setProgress(0);
                                    holder.albumProgressBarHint.setVisibility(View.GONE);
                                }
                                break;
                        }
                    } else {
                        switch (status) {
                            case AppInfoDataSet.STATUS_CONNECTING:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
                                if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(tmpInfoDataSet.getDownloadFilePath(), tmpInfoDataSet.getName())) {
                                    cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                                    postProgressBarHint.setVisibility(View.GONE);
                                } else {
                                }

                                break;

                            case AppInfoDataSet.STATUS_DOWNLOADING:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                postProgressBar.setProgress(appInfoDataSet.getProgress());
                                postDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                postProgressBarHint.setVisibility(View.GONE);

                                break;
                            case AppInfoDataSet.STATUS_COMPLETE:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                postProgressBar.setProgress(appInfoDataSet.getProgress());
                                postFrame.setVisibility(View.GONE);

                                break;

                            case AppInfoDataSet.STATUS_PAUSED:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                postDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                postProgressBarHint.setVisibility(View.GONE);

                                break;
                            case AppInfoDataSet.STATUS_NOT_DOWNLOAD:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                postDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                postProgressBar.setProgress(appInfoDataSet.getProgress());
                                postProgressBarHint.setVisibility(View.GONE);

                                break;
                            case AppInfoDataSet.STATUS_DOWNLOAD_ERROR:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
                                appInfoDataSet.setDownloadPerSize("");

                                break;
                            case AppInfoDataSet.STATUS_CANCEL:
                                appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                                appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                                appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                                postDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                postProgressBar.setProgress(0);
                                postProgressBarHint.setVisibility(View.GONE);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void deviceWidth() {
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        deviceWidth = size.x;
    }

    private PostDetailRecyclerViewAdapter.AppViewHolder getViewHolder(int position) {
        return (PostDetailRecyclerViewAdapter.AppViewHolder) mAlbumRecycler.findViewHolderForLayoutPosition(position);
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mAlbumRecycler.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }


    public void showDeletePostWarning(final Context context, final String courseID, final Post post) {

        final Handler dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        Helper.showToast(context.getString(R.string.successfully_deleted), Toast.LENGTH_SHORT);
                        onBackPressed();
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((DeletePostResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(PostDetailActivity.this, "Error", ((DeletePostResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set mResorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText(getResources().getString(R.string.delete_post_title));
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.delete_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    startProgressDialog();
                    new DeletePost(dialogHandler, courseID, post.getPostID()).sendDeletePostRequest();
                } else {
                    Helper.showNetworkAlertMessage(PostDetailActivity.this);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(PostDetailActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCommentButton() {
        if (mPostCommentsAdapter.getItemCount() > 0) {
            for (int i = 0; i < mPostCommentsAdapter.getItemCount(); i++) {
                if (mPostCommentsAdapter.getItem(i).getCommentUserID().equals(Config.getStringValue(Config.USER_ID))) {
                    commentPostImg.setImageResource(R.drawable.ic_comment_blue);
                    mPostCommentTxt.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    break;
                } else {
                    commentPostImg.setImageResource(R.drawable.ic_comment_grey);
                    mPostCommentTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));
                }
            }
        } else {
            commentPostImg.setImageResource(R.drawable.ic_comment_grey);
            mPostCommentTxt.setTextColor(getResources().getColor(R.color.timeline_text_color));

        }
    }
}