package com.edu.flinnt.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
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
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.LikeBookmark;
import com.edu.flinnt.core.PollList;
import com.edu.flinnt.core.PollVote;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.RecyclerViewAdapter;
import com.edu.flinnt.downloadsmultithread.DownloadInfo;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.gui.AddCommunicationActivity;
import com.edu.flinnt.gui.AddPollActivity;
import com.edu.flinnt.gui.CommunicationCommentActivity;
import com.edu.flinnt.gui.CommunicationFragment;
import com.edu.flinnt.gui.CourseDetailsActivity;
import com.edu.flinnt.gui.InAppPreviewActivity;
import com.edu.flinnt.gui.PostViewersSendMessageActivity;
import com.edu.flinnt.gui.SelectCourseActivity;
import com.edu.flinnt.gui.SelectUsersActivity;
import com.edu.flinnt.gui.YoutubeCustomLightboxActivity;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.LikeBookmarkRequest;
import com.edu.flinnt.protocol.PollListRequest;
import com.edu.flinnt.protocol.PollListResponse;
import com.edu.flinnt.protocol.PollVoteRequest;
import com.edu.flinnt.protocol.PollVoteResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostListMenuResponse;
import com.edu.flinnt.protocol.PostViewStatisticsResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadFileManager;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.DownloadUtils;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import com.edu.flinnt.util.UpdateReadMoreListner;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by flinnt-android-2 on 20/2/17.
 * Adapter of Communication fragment as per new design user can perform multiple option over here regarding post.(Downloading/Play)
 */

public class CommunicationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Post> mDataset;
    public ArrayList<Post> filteredDataset;
    static OnItemClickListener mItemClickListener;
    static OnItemLongClickListener mItemLongClickListener;
    private boolean isSearchMode;

    private boolean isWithFooter;
    private boolean isOfflineSearch;
    private ImageLoader mImageLoader;
    private static Activity mActivity;
    private RecyclerViewAdapter mAdapter;
    private static LikeBookmarkRequest mLikeBookmarkRequest;
    private static LikeBookmark mLikeBookmark;
    String mCourseId;
    String mCourseName;
    int deviceWidth;
    static CommunicationFragment mCommunicationFragment;
    public PollVoteResponse mPollVoteResponse;
    long baseChronometer = 0;
    private Thread audioThread;

    ViewHolder audioViewholder;
    int audioPosition;
    private UpdateReadMoreListner mUpdateReadMoreListner;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public CommunicationListAdapter(Activity activity, ArrayList<Post> myDataset, String courseID, String courseName, CommunicationFragment communicationFragment) {
        LogWriter.write("Dataset" + myDataset);

        filteredDataset = new ArrayList<Post>();
        mDataset = new ArrayList<Post>();
        try {
            this.mUpdateReadMoreListner = ((UpdateReadMoreListner) communicationFragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.mCourseId = courseID;
        this.mCourseName = courseName;
        this.mActivity = activity;
        this.mCommunicationFragment = communicationFragment;
        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);


        mImageLoader = Requester.getInstance().getImageLoader();
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        deviceWidth = size.x;
    }


    public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, Observer {
        public SelectableRoundedCourseImageView mUserImg;
        public TextView mEndDuration, mLinkTxt, mNameTxt, mDateTxt, mMessageRecipientsTxt, mDescriptionTxt, mLikeTxt, mCommentTxt, mViewsTxt, mPollEnddateTxt, mPollStartdateTxt, mPollTxt, mLikesTxt, mCommunicationCommentTxt, mMessageViewMoreTxt, mDescriptionReadMoreTxt;
        public Button mPollSubmitBtn, btn_viewinapp;
        public RelativeLayout mMediaRelLay, mAlbumMediaRelLay;
        public LinearLayout mAudioLinLay, mAddCommentLinear, mLikeLinear, mCommentLinear, mLikeViewCommentLinear, mLikeCommentLinear, mPollLinear;
        public ImageButton mMediaOpenImgbtn;
        public Toolbar mEditToolbar;
        public ImageView mAudioPlayButtonImg, mMediaThumbnailImg, mDownloadBtn, mCommentImg, mLikeImg, mRepostImg;
        public SelectableRoundedCourseImageView mUserCommentImg;
        public com.github.lzyzsd.circleprogress.DonutProgress mProgressBar;
        public RecyclerView mAlbumRecyclerView;
        public FrameLayout mMediaFrame;
        public ProgressBar mProgressbarHint, mPollProgress, mAudioProgressBar;
        public RadioGroup mPollOptionRadiogrp;
        public PollListResponse mPollListResponse;
        private int postion;
        public Chronometer mCurrentDuration;
        public View mSepratorView;
        public MediaPlayer mediaPlayer;
        public Thread audioThread;

        public ViewHolder(View v, final int position) {
            super(v);
            this.postion = position;

            mUserImg = (SelectableRoundedCourseImageView) v.findViewById(R.id.post_by_img);
            mUserImg.setDefaultImageResId(R.drawable.default_user_profile_image);
            mUserImg.setBorderColor(mActivity.getResources().getColor(R.color.userpic_border_color));
            mUserImg.setBorderWidthDP(1);
//            mUserImg.setOval(true);
            mUserCommentImg = (SelectableRoundedCourseImageView) v.findViewById(R.id.user_comment_img);
            mUserCommentImg.setDefaultImageResId(R.drawable.default_user_profile_image);
            mUserCommentImg.setBorderColor(mActivity.getResources().getColor(R.color.userpic_border_color));
            mUserCommentImg.setBorderWidthDP(1);

            mNameTxt = (TextView) v.findViewById(R.id.name_txt);
            mDateTxt = (TextView) v.findViewById(R.id.date_txt);
            mMessageRecipientsTxt = (TextView) v.findViewById(R.id.message_recipients);
            mDescriptionTxt = (TextView) v.findViewById(R.id.description_txt);
            mLikeTxt = (TextView) v.findViewById(R.id.likes_txt);
            mCommentTxt = (TextView) v.findViewById(R.id.comments_txt);
            mViewsTxt = (TextView) v.findViewById(R.id.views_txt);
            mLinkTxt = (TextView) v.findViewById(R.id.link_txt);
            mPollTxt = (TextView) v.findViewById(R.id.poll_txt);
            mLikesTxt = (TextView) v.findViewById(R.id.total_likes_txt);
            mCommunicationCommentTxt = (TextView) v.findViewById(R.id.total_comments_txt);
            mMessageViewMoreTxt = (TextView) v.findViewById(R.id.message_recipients_view_more);
            mDescriptionReadMoreTxt = (TextView) v.findViewById(R.id.description_read_more_txt);
            mMediaOpenImgbtn = (ImageButton) v.findViewById(R.id.media_open_imgbtn);
            mDownloadBtn = (ImageView) v.findViewById(R.id.download_btn);
            mMediaThumbnailImg = (ImageView) v.findViewById(R.id.media_thumbnail_img);
            mCommentImg = (ImageView) v.findViewById(R.id.comment_img);
            mLikeImg = (ImageView) v.findViewById(R.id.like_img);

            mRepostImg = (ImageView) v.findViewById(R.id.repost_img);
            mProgressBar = (com.github.lzyzsd.circleprogress.DonutProgress) v.findViewById(R.id.progress_bar);
            mProgressBar.setTextColor(R.color.gray);
            mProgressBar.setTextSize((float) 5.0);
            mAlbumRecyclerView = (RecyclerView) v.findViewById(R.id.album_recyclerview);
            mAudioLinLay = (LinearLayout) v.findViewById(R.id.audio_linear);
            mLikeLinear = (LinearLayout) v.findViewById(R.id.like_linear);
            mCommentLinear = (LinearLayout) v.findViewById(R.id.comment_linear);
            mAddCommentLinear = (LinearLayout) v.findViewById(R.id.add_comment_linear);
            mLikeViewCommentLinear = (LinearLayout) v.findViewById(R.id.like_view_comment_linear);
            mLikeCommentLinear = (LinearLayout) v.findViewById(R.id.like_comment_linear);
            mPollLinear = (LinearLayout) v.findViewById(R.id.poll_linear);
            mMediaRelLay = (RelativeLayout) v.findViewById(R.id.media_relative);
            mAlbumMediaRelLay = (RelativeLayout) v.findViewById(R.id.album_media_relative);
            mMediaFrame = (FrameLayout) v.findViewById(R.id.media_frame);
            mEditToolbar = (Toolbar) v.findViewById(R.id.edit_toolbar);
            mEditToolbar.inflateMenu(R.menu.communication_toolbar_menu);
            mProgressbarHint = (ProgressBar) v.findViewById(R.id.progress_barHint);
            mPollOptionRadiogrp = (RadioGroup) v.findViewById(R.id.poll_option_radiogrp);
            mPollEnddateTxt = (TextView) v.findViewById(R.id.poll_enddate_txt);
            mPollStartdateTxt = (TextView) v.findViewById(R.id.poll_startdate_txt);
            mPollSubmitBtn = (Button) v.findViewById(R.id.poll_submit_btn);
            btn_viewinapp = (Button) v.findViewById(R.id.btn_viewinapp);
            mPollProgress = (ProgressBar) v.findViewById(R.id.poll_progress);

            mAudioPlayButtonImg = (ImageButton) v.findViewById(R.id.audio_play_img);
            mAudioProgressBar = (ProgressBar) v.findViewById(R.id.audio_progressBar);
            mAudioProgressBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            mCurrentDuration = (Chronometer) v.findViewById(R.id.audio_current_duration_txt);
            mEndDuration = (TextView) v.findViewById(R.id.audio_end_duration_txt);
            mSepratorView = (View) v.findViewById(R.id.seprator_view);

            mAudioPlayButtonImg.setOnClickListener(this);
            mLikeLinear.setOnClickListener(this);
            mAddCommentLinear.setOnClickListener(this);
            mMediaFrame.setOnClickListener(this);
            mDescriptionReadMoreTxt.setOnClickListener(this);
            mCommentLinear.setOnClickListener(this);
            mMediaThumbnailImg.setOnClickListener(this);
            mMediaOpenImgbtn.setOnClickListener(this);
            mRepostImg.setOnClickListener(this);
            mLikeTxt.setOnClickListener(this);
            mCommentTxt.setOnClickListener(this);
            mViewsTxt.setOnClickListener(this);
            mMessageViewMoreTxt.setOnClickListener(this);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

            Pattern p = Pattern.compile("<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>");
            Linkify.addLinks(mDescriptionTxt, p, "http://");
            mDescriptionTxt.setMovementMethod(LinkMovementMethod.getInstance());
            mDescriptionTxt.setAutoLinkMask(Linkify.WEB_URLS);

            if (filteredDataset.get(position).getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                sendOptionRequest(position);
            }
        }

        /**
         * Mehtod for received data for poll asynchronously
         */

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void onPolldataReceived(final int position) {
            if (mPollListResponse == null && Helper.isConnected()) {
                LogWriter.write("data has not been received");
                sendOptionRequest(postion);
                return;
            }
            try {
                if (Helper.isConnected()) {
                    Post tempPost = filteredDataset.get(position);
                    Gson gson = new Gson();
                    tempPost.setPollListResponse(gson.toJson(mPollListResponse));
                    PostInterface.getInstance().insertOrUpdatePost(mCourseId, tempPost);
                } else {
                    Gson gson = new Gson();
                    mPollListResponse = gson.fromJson(filteredDataset.get(position).getPollListResponse(), PollListResponse.class);
                    if (mPollListResponse == null) {
                        return;
                    }
                }
            } catch (Exception e) {
                LogWriter.err(e);
            }

            if (mPollListResponse.getData().getPollFinished() == Flinnt.TRUE) {
                mPollLinear.removeAllViews();
                mPollLinear.addView(mPollTxt);
                mPollTxt.setText(mActivity.getResources().getString(R.string.poll_result));
                int maxIndex = 0;
                int totalVote = 0;
                for (int i = 1; i < mPollListResponse.getData().getOptions().size(); i++) {
                    float newnumber = Float.parseFloat(mPollListResponse.getData().getOptions().get(i).getVotesPercent());
                    if ((newnumber > Float.parseFloat(mPollListResponse.getData().getOptions().get(maxIndex).getVotesPercent()))) {
                        maxIndex = i;
                    }
                }
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
                for (int i = 0; i < mPollListResponse.getData().getOptions().size(); i++) {
                    View someView = inflater.inflate(R.layout.poll_result_item, mPollLinear, false);
                    TextView pollBackgroundView = (TextView) someView.findViewById(R.id.poll_background_view);
                    TextView pollOptionTxt = (TextView) someView.findViewById(R.id.poll_option_txt);
                    TextView pollOptionPercentTxt = (TextView) someView.findViewById(R.id.poll_option_percent_txt);
                    //  TextView pollOptionVoteTxt = (TextView) someView.findViewById(R.id.poll_option_vote_txt);

                    float tempPercent = Float.parseFloat(mPollListResponse.getData().getOptions().get(i).getVotesPercent());
                    int layWidth;
                    if (tempPercent > 0) {
                        layWidth = deviceWidth - (mActivity.getResources().getDimensionPixelSize(R.dimen.margin_25dp));
                    } else {
                        layWidth = deviceWidth;
                    }
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (layWidth * tempPercent) / 100, FrameLayout.LayoutParams.MATCH_PARENT);
                    pollBackgroundView.setLayoutParams(lp);

                    pollOptionTxt.setText(mPollListResponse.getData().getOptions().get(i).getText());
                    pollOptionPercentTxt.setText(mPollListResponse.getData().getOptions().get(i).getVotesPercent() + "%");
                    // pollOptionVoteTxt.setText("(" + mPollListResponse.getData().getOptions().get(i).getVotesReceived() + " Votes)");
                    totalVote += Integer.parseInt(mPollListResponse.getData().getOptions().get(i).getVotesReceived());

                    if (maxIndex == i && totalVote > 0) {
                        pollOptionTxt.setTypeface(Typeface.DEFAULT_BOLD);
                        pollOptionPercentTxt.setTypeface(Typeface.DEFAULT_BOLD);
                        pollBackgroundView.setBackground(mActivity.getResources().getDrawable(R.drawable.poll_round_blue));
                    } else {
                        pollOptionTxt.setTypeface(Typeface.DEFAULT);
                        pollOptionPercentTxt.setTypeface(Typeface.DEFAULT);
                        pollBackgroundView.setBackground(mActivity.getResources().getDrawable(R.drawable.poll_round_grey));
                    }

                    mPollLinear.addView(someView);
                }
                mPollLinear.addView(mPollEnddateTxt);
                mPollEnddateTxt.setPadding(0, 25, 50, 25);
                mPollEnddateTxt.setText(mActivity.getString(R.string.total_votes) + totalVote);
                mPollEnddateTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.comment_layout_background));
                mPollEnddateTxt.setTypeface(Typeface.DEFAULT_BOLD);
                mSepratorView.setVisibility(View.GONE);
                mPollEnddateTxt.setTextColor(Color.BLACK);
            } else {
                mPollTxt.setText(mActivity.getResources().getString(R.string.give_your_opinion));
                mPollEnddateTxt.setText(mActivity.getString(R.string.poll_result_on) + Helper.formateTimeMillis(filteredDataset.get(position).getPollResultHoursLong(), "HH:mm | dd MMM yyyy"));
                mPollEnddateTxt.setTypeface(Typeface.DEFAULT_BOLD);
                mPollEnddateTxt.setBackgroundColor(Color.WHITE);
                mPollOptionRadiogrp.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 0, 8);
                for (int i = 0; i < mPollListResponse.getData().getOptions().size(); i++) {
                    final RadioButton radioButton = new RadioButton(mActivity);
                    mPollOptionRadiogrp.addView(radioButton); //the RadioButtons are added to the radioGroup instead of the layout

                    radioButton.setLayoutParams(params);
                    radioButton.setId(Integer.parseInt(mPollListResponse.getData().getOptions().get(i).getId()));
                    radioButton.setText(mPollListResponse.getData().getOptions().get(i).getText());
                    radioButton.setTextSize(17);
                    radioButton.setButtonDrawable(mActivity.getResources().getDrawable(R.drawable.radio_button));

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        radioButton.setPadding(20, 20, 0, 20);
                    }
                }
                if ((Long.parseLong(filteredDataset.get(position).getPublishDate())) > (System.currentTimeMillis() / 1000)) {
                    mPollLinear.removeView(mPollSubmitBtn);
                    mPollStartdateTxt.setText("Poll starts at: " + Helper.formateTimeMillis(filteredDataset.get(position).getPublishDateLong(), "HH:mm | dd MMM yyyy"));
                    mPollStartdateTxt.setTypeface(Typeface.DEFAULT_BOLD);
                    mPollStartdateTxt.setBackgroundColor(Color.WHITE);
                } else {
                    mPollLinear.removeView(mPollStartdateTxt);
                }


                mPollOptionRadiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        for (int i = 0; i < mPollOptionRadiogrp.getChildCount(); i++) {
                            RadioButton radiobtn = (RadioButton) mPollOptionRadiogrp.getChildAt(i);
                            if (radiobtn.isChecked()) {
                                radiobtn.setTextColor(mActivity.getResources().getColor(R.color.ColorPrimary));
                            } else {
                                radiobtn.setTextColor(Color.BLACK);
                            }
                        }

                    }
                });
                if (mPollListResponse.getData().getPollVoted() == 1) {
                    mPollTxt.setText(R.string.your_opinion);
                    mPollLinear.removeView(mPollSubmitBtn);
                    for (int i = 0; i < mPollOptionRadiogrp.getChildCount(); i++) {
                        RadioButton radiobtn = (RadioButton) mPollOptionRadiogrp.getChildAt(i);
                        if (radiobtn.getId() == Integer.parseInt(mPollListResponse.getData().getVotedOption())) {
                            radiobtn.setButtonDrawable(mActivity.getResources().getDrawable(R.drawable.ic_radio_on));
                            radiobtn.setTextColor(Color.parseColor("#0c67c2"));
                            radiobtn.setEnabled(false);
                        } else {
                            radiobtn.setEnabled(false);
                            radiobtn.setTextColor(Color.BLACK);
                        }
                    }
                }
                mPollSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPollOptionRadiogrp.getCheckedRadioButtonId() != -1) {
                            final int selectedId = mPollOptionRadiogrp.getCheckedRadioButtonId();
                            if (Helper.isConnected()) {
                                final PollVoteRequest mPollVoteRequest = new PollVoteRequest();
                                mPollVoteRequest.setCourseID(mCourseId);
                                mPollVoteRequest.setPostID(filteredDataset.get(position).getPostID());
                                mPollVoteRequest.setUserID(Config.getStringValue(Config.USER_ID));
                                mPollVoteRequest.setOptionID(String.valueOf(selectedId));

                                PollVote pollVote = new PollVote(new Handler(new Handler.Callback() {
                                    @Override
                                    public boolean handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case Flinnt.SUCCESS:
                                                LogWriter.write("data has been received");
                                                mPollVoteResponse = (PollVoteResponse) msg.obj;
                                                if (mPollVoteResponse.getData().getVoted() == 1) {
                                                    Helper.showToast(mActivity.getResources().getString(R.string.poll_vote_sent), Toast.LENGTH_LONG);
                                                    mPollListResponse.getData().setPollVoted(Flinnt.TRUE);
                                                    mPollListResponse.getData().setVotedOption(String.valueOf(selectedId));
                                                    notifyItemChanged(position);
                                                }
                                                break;
                                            case Flinnt.FAILURE:
                                                mPollVoteResponse = (PollVoteResponse) msg.obj;
                                                if (mPollVoteResponse.errorResponse != null) {
                                                    String errorMessage = mPollVoteResponse.errorResponse.getMessage();
                                                    for (int i = 0; i < mPollVoteResponse.errorResponse.getErrorList().size(); i++) {
                                                        com.edu.flinnt.protocol.Error error = mPollVoteResponse.errorResponse.getErrorList().get(i);
                                                        if (error.getCode() == ErrorCodes.ERROR_CODE_400 || error.getCode() == ErrorCodes.ERROR_CODE_401 || error.getCode() == ErrorCodes.ERROR_CODE_409) {
                                                            Helper.showToast(errorMessage, Toast.LENGTH_SHORT);
                                                        }
                                                    }
                                                    Helper.showAlertMessage(mActivity, mActivity.getString(R.string.error), errorMessage, mActivity.getString(R.string.close_txt));
                                                }
                                                break;
                                        }
                                        return false;
                                    }
                                }), mPollVoteRequest);
                                pollVote.sendPollVoteRequest();

                            } else {
                                Helper.showNetworkAlertMessage(mActivity);
                            }
                        } else {
                            Helper.showToast(mActivity.getResources().getString(R.string.select_poll_option), Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        }

        /**
         * Send request for getting Poll Data
         */
        private void sendOptionRequest(final int position) {
            if (Helper.isConnected()) {
                PollListRequest mPollListRequest = new PollListRequest();
                mPollListRequest.setCourseID(mCourseId);
                mPollListRequest.setPostID(filteredDataset.get(position).getPostID());
                mPollListRequest.setUserID(Config.getStringValue(Config.USER_ID));
                mPollProgress.setVisibility(View.VISIBLE);
                PollList pollList = new PollList(new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        switch (msg.what) {
                            case Flinnt.SUCCESS:
                                LogWriter.write("data has been received");
                                mPollListResponse = (PollListResponse) msg.obj;
                                if (filteredDataset.size() > position) {
                                    onPolldataReceived(position);
                                }
                                mPollProgress.setVisibility(View.GONE);
                                break;
                            case Flinnt.FAILURE:
                                break;
                        }
                        return false;
                    }
                }), mPollListRequest);
                pollList.sendPollListRequest();
            }
        }


        public void onViewHolderBound(int position) {
            onPolldataReceived(position);
        }

        @Override
        public void onClick(View v) {
            Post mPost = null;
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mPost = filteredDataset.get(getAdapterPosition());
            }

            if (mPost == null) return;

            switch (v.getId()) {
                case R.id.description_read_more_txt:
                    if (mPost.getShowMoreLess().equals(Flinnt.SHOWMORE)) {
                        mPost.setShowMoreLess(Flinnt.SHOWLESS);
                        mDescriptionTxt.setMaxLines(Integer.MAX_VALUE);
                        mDescriptionReadMoreTxt.setText(mActivity.getResources().getString(R.string.read_less));
                    } else if (mPost.getShowMoreLess().equals(Flinnt.SHOWLESS)) {
                        mPost.setShowMoreLess(Flinnt.SHOWMORE);
                        mDescriptionTxt.setMaxLines(8);
                        mDescriptionReadMoreTxt.setText(mActivity.getResources().getString(R.string.read_more));
                    }
                    break;
                case R.id.message_recipients_view_more:
                    if (mPost.getViewMoreLess().equals(Flinnt.SHOWMORE)) {
                        mPost.setViewMoreLess(Flinnt.SHOWLESS);
                        mMessageRecipientsTxt.setMaxLines(Integer.MAX_VALUE);
                        mMessageViewMoreTxt.setText(mActivity.getResources().getString(R.string.view_less));
                    } else if (mPost.getViewMoreLess().equals(Flinnt.SHOWLESS)) {
                        mPost.setViewMoreLess(Flinnt.SHOWMORE);
                        mMessageRecipientsTxt.setMaxLines(2);
                        mMessageViewMoreTxt.setText(mActivity.getResources().getString(R.string.view_all));
                    }
                    break;
                case R.id.views_txt:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        Intent mViewsIntext = new Intent(mActivity, PostViewersSendMessageActivity.class);
                        mViewsIntext.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        mViewsIntext.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                        mViewsIntext.putExtra(Course.COURSE_NAME_KEY, mCourseName);
                        mViewsIntext.putExtra("SELECTED_TAB", "viewers");
                        mViewsIntext.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                        mActivity.startActivity(mViewsIntext);
                    }
                    break;
                case R.id.likes_txt:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        Intent mLikesIntext = new Intent(mActivity, PostViewersSendMessageActivity.class);
                        mLikesIntext.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        mLikesIntext.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                        mLikesIntext.putExtra(Course.COURSE_NAME_KEY, mCourseName);
                        mLikesIntext.putExtra("SELECTED_TAB", "likes");
                        mLikesIntext.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                        mActivity.startActivity(mLikesIntext);
                    }
                    break;
                case R.id.comments_txt:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        Intent mCommentIntent = new Intent(mActivity, PostViewersSendMessageActivity.class);
                        mCommentIntent.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        mCommentIntent.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                        mCommentIntent.putExtra(Course.COURSE_NAME_KEY, mCourseName);
                        mCommentIntent.putExtra("SELECTED_TAB", "comments");
                        mCommentIntent.putExtra(PostViewStatisticsResponse.USER_PICTURE_URL_KEY, mPost.getAuthorPictureUrl());
                        mActivity.startActivity(mCommentIntent);
                    }
                    break;
                case R.id.add_comment_linear:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(v, getAdapterPosition());
                        }
                        Intent commentActivity = new Intent(mActivity, CommunicationCommentActivity.class);
                        commentActivity.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        commentActivity.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                        commentActivity.putExtra(PostListMenuResponse.CAN_COMMENT_KEY, ((CourseDetailsActivity) mActivity).mPostListMenuResponse.getCanComment());
                        commentActivity.putExtra("REQUEST_FOCUS", Flinnt.ENABLED);
                        mActivity.startActivityForResult(commentActivity, CourseDetailsActivity.POST_COMMENT_CALL_BACK);
                    }
                    break;
                case R.id.like_linear:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(v, getAdapterPosition());
                        }
                        mLikeBookmarkRequest = new LikeBookmarkRequest();
                        mLikeBookmarkRequest.setUserID(Config.getStringValue(Config.USER_ID));
                        mLikeBookmarkRequest.setPostID(mPost.getPostID());
                        if (mPost.getIsLike().equals(Flinnt.ENABLED)) {
                            mLikeBookmark = new LikeBookmark(mCommunicationFragment.mHandler, LikeBookmark.POST_DISLIKE);
                        } else {
                            mLikeBookmark = new LikeBookmark(mCommunicationFragment.mHandler, LikeBookmark.POST_LIKE);
                        }
                        mLikeBookmark.setLikeBookmarkRequest(mLikeBookmarkRequest);
                        mLikeBookmark.sendLikeBookmarkRequest();
                        mCommunicationFragment.startProgressDialog();
                    }
                    break;
                case R.id.comment_linear:
                    try {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(mActivity);
                        } else {
                            if (mItemClickListener != null) {
                                mItemClickListener.onItemClick(v, getAdapterPosition());
                            }
                            Intent cmtActivity = new Intent(mActivity, CommunicationCommentActivity.class);
                            cmtActivity.putExtra(Course.COURSE_ID_KEY, mCourseId);
                            cmtActivity.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                            cmtActivity.putExtra(PostListMenuResponse.CAN_COMMENT_KEY, ((CourseDetailsActivity) mActivity).mPostListMenuResponse.getCanComment());
                            cmtActivity.putExtra("REQUEST_FOCUS", Flinnt.DISABLED);
                            mActivity.startActivityForResult(cmtActivity, CourseDetailsActivity.POST_COMMENT_CALL_BACK);
                        }
                    } catch (Exception e) {
                        LogWriter.write(e.getMessage());
                    }

                    break;
                case R.id.repost_img:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        Intent postRepost = new Intent(mActivity, SelectCourseActivity.class);
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
                        postRepost.putExtra(Course.COURSE_ID_KEY, mCourseId);
                        postRepost.putExtra(Post.POST_ID_KEY, mPost.getPostID());
                        postRepost.putExtra(CourseInfo.COURSE_NAME_KEY, mCourseName);
                        postRepost.putExtra(PostDetailsResponse.TITLE_KEY, mPost.getTitle());
                        postRepost.putExtra(PostDetailsResponse.DESCRIPTION_KEY, mPost.getDescription());
                        postRepost.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, mPost.getPostContentTypeInt());
                        postRepost.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, mPost.getAttachmentsIsUrl());
                        postRepost.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, mPost.getAttachmentUrl());

                        mActivity.startActivity(postRepost);
                    }
                    break;
                case R.id.media_thumbnail_img:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        if (AskPermition.getInstance(mActivity).isPermitted())
                            playOrDownloadMedia(mPost);
                    }
                    break;
                case R.id.media_open_imgbtn:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        if (AskPermition.getInstance(mActivity).isPermitted())
                            playOrDownloadMedia(mPost);
                    }
                    break;
                case R.id.media_frame:
                    try {
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(mActivity);
                        } else {
                            if (AskPermition.getInstance(mActivity).isPermitted()) {
                                AppInfoDataSet appInfoDataSet = mPost.appInfoDataSets.get(0);
                                mProgressbarHint.setVisibility(View.VISIBLE);
                                if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                                    mCommunicationFragment.pause(getAdapterPosition(), appInfoDataSet.getUrl(), mPost, mActivity.getString(R.string.post_txt), getAdapterPosition());
                                } else {
                                    mCommunicationFragment.download(getAdapterPosition(), appInfoDataSet.getUrl(), mPost, mActivity.getString(R.string.post_txt), getAdapterPosition());
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogWriter.err(e.getMessage());
                    }
                    break;
                case R.id.audio_play_img:
                    if (!Helper.isConnected()) {
                        Helper.showNetworkAlertMessage(mActivity);
                    } else {
                        if (AskPermition.getInstance(mActivity).isPermitted()) {
                            if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mPost.getAttachments())) {
                                String audiourl = mPost.getAttachmentUrl() + mPost.getAttachments();
                                setDownloadMediaFileandHandler(mPost);
                                mCommunicationFragment.mDownload = new DownloadMediaFile(mActivity, Helper.getFlinntAudioPath(), mPost.getAttachments(), Long.parseLong(mPost.getPostID()), audiourl, mCommunicationFragment.mHandler);
                                mCommunicationFragment.mDownload.execute();
                                mCommunicationFragment.setDownloadProgressDialog();
                            } else if (null != mediaPlayer) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    baseChronometer = mCurrentDuration.getBase() - SystemClock.elapsedRealtime();
                                    mCurrentDuration.stop();
                                    mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_arrow_white));
                                } else {
                                    mediaPlayer.start();
                                    mCurrentDuration.setBase(SystemClock.elapsedRealtime() + baseChronometer);
                                    mCurrentDuration.start();
                                    mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_pause_arrow_white));
                                }
                            } else {
                                startAudioIntent(postion, mPost, this);
                            }
                        }
                    }
                    break;
                default:
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }

        @Override
        public void update(Observable observable, Object data) {

        }
    }


    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Button readMore;

        public FooterViewHolder(View v) {
            super(v);
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FooterViewHolder : ");
            readMore = (Button) v.findViewById(R.id.read_more_button);
            readMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
                LogWriter.write("********************");
            }
        }
    }


    public void add(int position, Post item) {
        mDataset.add(position, item);
        filteredDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(List<Post> items) {
        int count = filteredDataset.size();
        filteredDataset.addAll(items);
        mDataset.addAll(items);
        Collections.sort(filteredDataset, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getPublishDateLong() > post2.getPublishDateLong() ? -1 :
                        post1.getPublishDateLong() < post2.getPublishDateLong() ? 1 : 0;
            }
        });
        notifyItemRangeInserted(count, items.size());
    }

    public void clearData() {
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    /**
     * Update offline post items
     *
     * @param items          items list
     * @param offlinePostIDs item ids list
     */
    public void updateItems(List<Post> items, ArrayList<String> offlinePostIDs) {
        ArrayList<String> postIDs = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            Post post = items.get(i);
            postIDs.add(post.getPostID());
            for (int j = 0; j < mDataset.size(); j++) {
                if (post.getPostID().equals(mDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("index : " + j);
                    mDataset.set(j, post);
                    break;
                }
            }
            for (int j = 0; j < filteredDataset.size(); j++) {
                if (post.getPostID().equals(filteredDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("filteredDataset index : " + j);
                    filteredDataset.set(j, post);
                    break;
                }
            }
        }

        Collection<String> offlinePostList = offlinePostIDs;
        offlinePostList.removeAll(postIDs);
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("Deleted offlinePostList size : " + offlinePostList.size());
        for (int i = 0; i < offlinePostIDs.size(); i++) {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("Deleted offline Post ID : " + offlinePostIDs.get(i));
            for (int j = 0; j < mDataset.size(); j++) {
                if (offlinePostIDs.get(i).equals(mDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("index : " + j);
                    mDataset.remove(j);
                    break;
                }
            }
            for (int j = 0; j < filteredDataset.size(); j++) {
                if (offlinePostIDs.get(i).equals(filteredDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("filteredDataset index : " + j);
                    filteredDataset.remove(j);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Update list item when it is edited.
     */
    public void updateItem(RecyclerView mRecyclerview, Post item, int position) {
        if (mDataset.size() > position) {
            mDataset.remove(position);
            filteredDataset.remove(position);
            notifyItemRemoved(position);
            mDataset.add(position, item);
            filteredDataset.add(position, item);
            notifyItemInserted(position);
            mRecyclerview.scrollToPosition(position);
        }
    }

    public void remove(Post item) {
        int position = filteredDataset.indexOf(item);
        if (position > -1) {
            filteredDataset.remove(position);
            mDataset.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    public Post getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        }
        return null;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int itemCount = filteredDataset.size();

        if (isOfflineSearch) {
            return itemCount;
        }

        /*if (isWithFooter) {
            itemCount++;
        }*/
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemCount itemCount : " + itemCount + ", isWithFooter : " + isWithFooter);
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemViewType position : " + position);
        return position;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View v;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onCreateViewHolder viewType : " + position);


        if (isWithFooter && isPositionFooter(position)) {
//            if(mUpdateReadMoreListner == null){
//                mUpdateReadMoreListner = (UpdateReadMoreListner) mActivity;
//            }
            mUpdateReadMoreListner.onUpdateReadMoreButton(true);
        } else {
//            if(mUpdateReadMoreListner == null){
//                mUpdateReadMoreListner = (UpdateReadMoreListner) mActivity;
//            }
            mUpdateReadMoreListner.onUpdateReadMoreButton(false);
        }


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_communication_item, parent, false);
        return new ViewHolder(v, position);
    }

    /**
     * Display item in list view based on Post type (Audio,Video,Doc)
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (LogWriter.isValidLevel(Log.INFO))
//            LogWriter.write("onBindViewHolder position : " + position);

            if (viewHolder instanceof ViewHolder) {
                try {
                    final ViewHolder holder = (ViewHolder) viewHolder;
                    final Post post = filteredDataset.get(position);

                    //Log.d("PostType", String.valueOf(post.getPostTypeInt()));
                    //Log.d("PostType", String.valueOf(post.getPostContentTypeInt()));


                    if (null != post) {
                        LogWriter.write("onBindViewHolder position : " + post.getPostID());
                        String authourPicUrl = post.getAuthorPictureUrl() + Flinnt.PROFILE_MEDIUM + File.separator + post.getAuthorPicture();
                        holder.mUserImg.setImageUrl(authourPicUrl, mImageLoader);
                        String userPicUrl = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID)).getUserPictureUrl();
                        String userPic = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID)).getUserPicture();
                        holder.mUserCommentImg.setImageUrl(userPicUrl + Flinnt.PROFILE_LARGE + File.separator + userPic, mImageLoader);
                        holder.mNameTxt.setText(post.getAuthor());

                        holder.mLikeViewCommentLinear.setVisibility(View.VISIBLE);
                        holder.mLikeCommentLinear.setVisibility(View.VISIBLE);
                        holder.mMessageRecipientsTxt.setVisibility(View.GONE);
                        holder.mMediaOpenImgbtn.setVisibility(View.GONE);
                        holder.mLinkTxt.setVisibility(View.GONE);
                        holder.mAlbumMediaRelLay.setVisibility(View.GONE);
                        holder.mMediaRelLay.setVisibility(View.GONE);
                        holder.mAudioLinLay.setVisibility(View.GONE);
                        holder.mPollLinear.setVisibility(View.GONE);
                        holder.mDescriptionReadMoreTxt.setVisibility(View.GONE);
                        holder.mMessageViewMoreTxt.setVisibility(View.GONE);
                        holder.mRepostImg.setVisibility(View.GONE);

                        if (holder.mediaPlayer != null && mPlayingPosition != position) {
                            holder.mediaPlayer.stop();
                            holder.mediaPlayer.release();
                            baseChronometer = 0;
                            holder.mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_arrow_white));
                            holder.mCurrentDuration.stop();
                            holder.mCurrentDuration.setBase(SystemClock.elapsedRealtime());
                            holder.mAudioProgressBar.setProgress(0);
                            holder.audioThread.yield();
                            holder.mediaPlayer = null;

                        }
                        String title = post.getDescription();

                        if (post.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                            holder.mDescriptionTxt.setText(title);
                            holder.mViewsTxt.setText(post.getTotalViews() + " Views");
                            holder.mLikeTxt.setText(post.getTotalLikes() + " Likes");
                            holder.mCommentTxt.setText(post.getTotalComments() + " Comments");
                            String photos = post.getAttachments();


                            String[] photosArr = photos.split(",");
                            if (photosArr.length > 0) {
                                holder.mAlbumMediaRelLay.setVisibility(View.VISIBLE);
                                post.appInfoDataSets.clear();
                                for (int i = 0; i < photosArr.length; i++) {
                                    final String imageFileName = photosArr[i].trim();
                                    if (!TextUtils.isEmpty(imageFileName)) {
                                        String urlNoCrop = post.getAttachmentUrl() + Flinnt.GALLERY_NOCROP + File.separator + imageFileName;
                                        String urlMobile = post.getAttachmentUrl() + Flinnt.GALLERY_MOBILE + File.separator + imageFileName;
                                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(post.getPostID(), imageFileName, urlMobile, urlNoCrop, Flinnt.DISABLED);
                                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                                        post.appInfoDataSets.add(appInfoDataSet);
                                    }
                                }
                            }

                            for (AppInfoDataSet info : post.appInfoDataSets) {
                                DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                                if (downloadInfo != null) {
                                    info.setProgress(downloadInfo.getProgress());
                                    info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                                    info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                }
                            }

                            mAdapter = new RecyclerViewAdapter(mActivity, mCommunicationFragment, post, position);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                            holder.mAlbumRecyclerView.setLayoutManager(layoutManager);
                            holder.mAlbumRecyclerView.setAdapter(mAdapter);
                            mAdapter.setData(post.appInfoDataSets);

                        } else {

                            holder.mViewsTxt.setText(post.getTotalViews() + " Views");
                            holder.mCommentTxt.setText(post.getTotalComments() + " Comments");

                            if (post.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
                                String msgToUser = "";
                                List<String> items = Arrays.asList(post.getMessageToUsers().split("\\s*,\\s*"));
                                for (int i = 0; i < items.size(); i++) {
                                    String temp = items.get(i);
                                    String[] valueRoleId = temp.split("\\|");
                                    msgToUser = msgToUser + valueRoleId[0] + ",";
                                }
                                msgToUser = msgToUser.substring(0, msgToUser.length() - 1);
                                holder.mMessageRecipientsTxt.setVisibility(View.VISIBLE);
                                holder.mMessageRecipientsTxt.setText("To: " + msgToUser);
                                holder.mDescriptionTxt.setText(title);
                            } else if (post.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                                holder.mDescriptionTxt.setText("Poll: " + title);
                                holder.mLikeViewCommentLinear.setVisibility(View.GONE);
                                holder.mLikeCommentLinear.setVisibility(View.GONE);
                                holder.mAddCommentLinear.setVisibility(View.GONE);
                                holder.mSepratorView.setVisibility(View.GONE);
                                holder.mPollLinear.setVisibility(View.VISIBLE);
                                holder.onViewHolderBound(position);
                            } else {

                                //@Nikhil 2162018
                                if (post.getPostTypeInt() == Flinnt.POST_STORY_LINK && post.getPostContentTypeInt() == Flinnt.POST_CONTENT_LINK) {
                                    holder.mLinkTxt.setVisibility(View.GONE);
                                    holder.btn_viewinapp.setVisibility(View.VISIBLE);
                                    holder.btn_viewinapp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //redirect ot in-app preview
                                            //InAppPreviewActivity
                                            if (Helper.isConnected()) {
                                                Intent inapp = new Intent(mActivity, InAppPreviewActivity.class);
                                                inapp.putExtra(Flinnt.KEY_INAPP_TITLE, mCourseName);
                                                inapp.putExtra(Flinnt.KEY_INAPP_URL, post.getAttachments());
                                                inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                                                inapp.putExtra(Flinnt.NOTIFICATION_SCREENID, 0);
                                                view.getContext().startActivity(inapp);
                                            } else {
                                                Helper.showNetworkAlertMessage(mActivity);
                                            }
                                        }
                                    });
                                } else {
                                    holder.btn_viewinapp.setVisibility(View.GONE);
                                }
                                holder.mDescriptionTxt.setText(title);
                                holder.mLikeTxt.setText(post.getTotalLikes() + " Likes");
                            }


                            switch (post.getPostContentTypeInt()) {
                                case Flinnt.POST_CONTENT_GALLERY:
                                    holder.mMediaRelLay.setVisibility(View.VISIBLE);
                                    String urlNoCrop = post.getAttachmentUrl() + Flinnt.GALLERY_NOCROP + File.separator + post.getAttachments();
                                    String urlMobile = post.getAttachmentUrl() + Flinnt.GALLERY_MOBILE + File.separator + post.getAttachments();
                                    mImageLoader.get(urlMobile, ImageLoader.getImageListener(holder.mMediaThumbnailImg, R.drawable.album_default, R.drawable.album_default));


                                    AppInfoDataSet appInfoDataSet = new AppInfoDataSet(post.getPostID(), post.getAttachments(), urlMobile, urlNoCrop, Flinnt.DISABLED);
                                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                                    post.appInfoDataSets.add(appInfoDataSet);

                                    for (AppInfoDataSet info : post.appInfoDataSets) {
                                        DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                                        if (downloadInfo != null) {
                                            info.setProgress(downloadInfo.getProgress());
                                            info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                                            info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                        }
                                    }

                                    if (Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), post.getAttachments()) && appInfoDataSet.getStatusText().equals("Not Download")) {
                                        holder.mMediaFrame.setVisibility(View.GONE);
                                    } else {
                                        holder.mMediaFrame.setVisibility(View.VISIBLE);
                                        if (appInfoDataSet.getStatusText().equals("Complete")) {
                                            holder.mMediaFrame.setVisibility(View.GONE);
                                        } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                        } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                            holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                        } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                            holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                        } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                            holder.mProgressBar.setProgress(appInfoDataSet.getProgress());
                                        }
                                    }

                                    break;
                                case Flinnt.POST_CONTENT_AUDIO:
                                    holder.mAudioLinLay.setVisibility(View.VISIBLE);
                                    break;
                                case Flinnt.POST_CONTENT_VIDEO:
                                    holder.mMediaRelLay.setVisibility(View.VISIBLE);
                                    holder.mMediaOpenImgbtn.setVisibility(View.VISIBLE);
                                    if (post.getAttachmentsIsUrl() != null && post.getAttachmentsIsUrl().equals(Flinnt.ENABLED)) {
                                        holder.mMediaFrame.setVisibility(View.GONE);
                                        String postYoutubeVedioID = getYouTubeVideoId(post);
                                        String url = "http://img.youtube.com/vi/" + postYoutubeVedioID + "/0.jpg";
                                        mImageLoader.get(url, ImageLoader.getImageListener(holder.mMediaThumbnailImg, R.drawable.youtube_video_fram_not_get, R.drawable.youtube_video_fram_not_get));
                                    } else {

                                        holder.mMediaFrame.setVisibility(View.VISIBLE);
                                        String videoPreviewUrl = post.getVideoThumbUrl() + post.getVideoPreview();
                                        String videoUrl = post.getAttachmentUrl() + post.getAttachments();
                                        mImageLoader.get(videoPreviewUrl, ImageLoader.getImageListener(holder.mMediaThumbnailImg, R.drawable.video_default, R.drawable.video_default));

                                        AppInfoDataSet appInfoDataSetVideo = new AppInfoDataSet(post.getPostID(), post.getAttachments(), videoUrl, videoUrl, Flinnt.DISABLED);
                                        appInfoDataSetVideo.setDownloadFilePath(Helper.getFlinntVideoPath());
                                        post.appInfoDataSets.add(appInfoDataSetVideo);

                                        for (AppInfoDataSet info : post.appInfoDataSets) {
                                            DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                                            if (downloadInfo != null) {
                                                info.setProgress(downloadInfo.getProgress());
                                                info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                                                info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                            }
                                        }

                                        if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), post.getAttachments()) && appInfoDataSetVideo.getStatusText().equals("Not Download")) {
                                            holder.mMediaFrame.setVisibility(View.GONE);
                                        } else {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            if (appInfoDataSetVideo.getStatusText().equals("Complete")) {
                                                holder.mMediaFrame.setVisibility(View.GONE);
                                            } else if (appInfoDataSetVideo.getStatusText().equals("Not Download")) {
                                                holder.mMediaFrame.setVisibility(View.VISIBLE);
                                                holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                            } else if (appInfoDataSetVideo.getStatusText().equals("Downloading")) {
                                                holder.mMediaFrame.setVisibility(View.VISIBLE);
                                                holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                                holder.mProgressBar.setProgress(appInfoDataSetVideo.getProgress());
                                            } else if (appInfoDataSetVideo.getStatusText().equals("Pause")) {
                                                holder.mMediaFrame.setVisibility(View.VISIBLE);
                                                holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                                holder.mProgressBar.setProgress(appInfoDataSetVideo.getProgress());
                                            } else if (appInfoDataSetVideo.getStatusText().equals("Resume")) {
                                                holder.mMediaFrame.setVisibility(View.VISIBLE);
                                                holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                                holder.mProgressBar.setProgress(appInfoDataSetVideo.getProgress());
                                            }
                                        }
                                    }
                                    break;
                                case Flinnt.POST_CONTENT_LINK:
                                    //@Nikhil 2162018 updated  472018
                                    if (post.getPostTypeInt() != Flinnt.POST_STORY_LINK) {
                                        holder.mLinkTxt.setVisibility(View.VISIBLE);
                                        holder.mLinkTxt.setText(post.getAttachments());
                                    } else {
                                        holder.mLinkTxt.setVisibility(View.GONE);
                                        holder.mLinkTxt.setText(post.getAttachments());
                                    }

                                    break;
                                case Flinnt.POST_CONTENT_DOCUMENT:
                                    holder.mMediaRelLay.setVisibility(View.VISIBLE);

                                    String fileExtention = Helper.getExtension(post.getAttachments());
                                    int defaultDrawable = Helper.getDefaultPostImageFromType(Flinnt.POST_CONTENT_DOCUMENT, fileExtention);
                                    holder.mMediaThumbnailImg.setImageDrawable(Helper.getDrawable(mActivity, defaultDrawable));
                                    String docUrl = post.getAttachmentUrl() + post.getAttachments();

                                    AppInfoDataSet appInfoDataSetDoc = new AppInfoDataSet(post.getPostID(), post.getAttachments(), docUrl, docUrl, Flinnt.DISABLED);
                                    appInfoDataSetDoc.setDownloadFilePath(Helper.getFlinntDocumentPath());
                                    post.appInfoDataSets.add(appInfoDataSetDoc);

                                    for (AppInfoDataSet info : post.appInfoDataSets) {
                                        DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(info.getUrl());
                                        if (downloadInfo != null) {
                                            info.setProgress(downloadInfo.getProgress());
                                            info.setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                                            info.setStatus(AppInfoDataSet.STATUS_PAUSED);
                                        }
                                    }

                                    if (Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), post.getAttachments())) {
                                        holder.mMediaFrame.setVisibility(View.GONE);
                                    } else {
                                        holder.mMediaFrame.setVisibility(View.VISIBLE);
                                        holder.mMediaFrame.setVisibility(View.VISIBLE);
                                        if (appInfoDataSetDoc.getStatusText().equals("Complete")) {
                                            holder.mMediaFrame.setVisibility(View.GONE);
                                        } else if (appInfoDataSetDoc.getStatusText().equals("Not Download")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                                        } else if (appInfoDataSetDoc.getStatusText().equals("Downloading")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                                            holder.mProgressBar.setProgress(appInfoDataSetDoc.getProgress());
                                        } else if (appInfoDataSetDoc.getStatusText().equals("Pause")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                            holder.mProgressBar.setProgress(appInfoDataSetDoc.getProgress());
                                        } else if (appInfoDataSetDoc.getStatusText().equals("Resume")) {
                                            holder.mMediaFrame.setVisibility(View.VISIBLE);
                                            holder.mDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                                            holder.mProgressBar.setProgress(appInfoDataSetDoc.getProgress());
                                        }
                                    }
                                default:
                                    break;

                            }
                        }

                        if (post.getIsRead().equals(Flinnt.DISABLED)) {
                            holder.mDescriptionTxt.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            holder.mDescriptionTxt.setTypeface(Typeface.DEFAULT);
                        }

                        if (post.getIsLike() != null && post.getIsLike().equals(Flinnt.ENABLED)) {
                            holder.mLikeImg.setImageResource(R.drawable.ic_like_blue);
                            holder.mLikesTxt.setTextColor(mActivity.getResources().getColor(R.color.ColorPrimary));
                        } else {
                            holder.mLikeImg.setImageResource(R.drawable.ic_like_grey);
                            holder.mLikesTxt.setTextColor(mActivity.getResources().getColor(R.color.timeline_text_color));
                        }

                        if (post.getIsComment() != null && post.getIsComment().equals(Flinnt.ENABLED)) {
                            holder.mCommentImg.setImageResource(R.drawable.ic_comment_blue);
                            holder.mCommunicationCommentTxt.setTextColor(mActivity.getResources().getColor(R.color.ColorPrimary));
                        } else {
                            holder.mCommentImg.setImageResource(R.drawable.ic_comment_grey);
                            holder.mCommunicationCommentTxt.setTextColor(mActivity.getResources().getColor(R.color.timeline_text_color));
                        }


                        holder.mDescriptionTxt.post(new Runnable() {
                            @Override
                            public void run() {
                                int moreLines = holder.mDescriptionTxt.getLineCount();
                                if (moreLines > 8) {
                                    post.setShowMoreLess(Flinnt.SHOWMORE);
                                    holder.mDescriptionTxt.setMaxLines(8);
                                    holder.mDescriptionReadMoreTxt.setVisibility(View.VISIBLE);
                                    holder.mDescriptionReadMoreTxt.setText(mActivity.getResources().getString(R.string.read_more));
                                } else {
                                    holder.mDescriptionReadMoreTxt.setVisibility(View.GONE);
                                }
                            }
                        });


                        if (post.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
                            holder.mLikeLinear.setVisibility(View.GONE);
                            holder.mLikeTxt.setVisibility(View.GONE);
                            holder.mMessageRecipientsTxt.post(new Runnable() {
                                @Override
                                public void run() {
                                    int moreLine = holder.mMessageRecipientsTxt.getLineCount();
                                    if (moreLine > 2) {
                                        post.setViewMoreLess(Flinnt.SHOWMORE);
                                        holder.mMessageRecipientsTxt.setMaxLines(2);
                                        holder.mMessageViewMoreTxt.setVisibility(View.VISIBLE);
                                        holder.mMessageViewMoreTxt.setText(mActivity.getResources().getString(R.string.view_more));
                                    } else {
                                        holder.mMessageViewMoreTxt.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }


                        if (post.getAllowRepost() != null && post.getAllowRepost().equals(Flinnt.ENABLED)) {
                            holder.mRepostImg.setVisibility(View.VISIBLE);
                        } else {
                            holder.mRepostImg.setVisibility(View.GONE);
                        }

                        try {
                            if ((Long.parseLong(post.getPublishDate())) > (System.currentTimeMillis() / 1000) + 20) {
                                holder.mDateTxt.setText("not yet");
                            } else
                                holder.mDateTxt.setText(Helper.formateDate(Long.parseLong(post.getPublishDate())));
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        try {
                            if (((CourseDetailsActivity) mActivity).mPostListMenuResponse.getCanComment() == Flinnt.TRUE && post.getPostTypeInt() != Flinnt.POST_TYPE_POLL) {
                                holder.mAddCommentLinear.setVisibility(View.VISIBLE);
                            } else {
                                holder.mAddCommentLinear.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        updateCommunicationMenu(holder.mEditToolbar.getMenu(), post);


                        if (holder.mEditToolbar != null) {
                            holder.mEditToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.commu_edit:
                                            if (Helper.isConnected()) {
                                                Intent postEdit;
                                                if (post.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                                                    postEdit = new Intent(mActivity, AddPollActivity.class);
                                                    postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_POLL_EDIT);
                                                    postEdit.putExtra("PollOptions", holder.mPollListResponse);
                                                    postEdit.putExtra(Post.POLL_RESULT_HOURS_KEY, post.getPollResultHours());
                                                    postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, post.getAttachments());
                                                } else {
                                                    postEdit = new Intent(mActivity, AddCommunicationActivity.class);
                                                }
                                                postEdit.putExtra("position", position);
                                                postEdit.putExtra(Course.COURSE_ID_KEY, mCourseId);
                                                postEdit.putExtra(Post.POST_ID_KEY, post.getPostID());
                                                postEdit.putExtra(CourseInfo.COURSE_NAME_KEY, mCourseName);
                                                postEdit.putExtra(PostDetailsResponse.TITLE_KEY, post.getTitle());
                                                postEdit.putExtra(PostDetailsResponse.DESCRIPTION_KEY, post.getDescription());
                                                postEdit.putExtra(PostDetailsResponse.ATTACHMENT_URL_KEY, post.getAttachmentUrl());
                                                postEdit.putExtra(PostDetailsResponse.POST_CONTENT_TYPE_KEY, post.getPostContentTypeInt());
                                                postEdit.putExtra(PostDetailsResponse.POST_CONTENT_URL_KEY, post.getAttachmentsIsUrl());


                                                if (post.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
                                                    String teachersIDs = "";
                                                    String studentsIDs = "";
                                                    int teachersCount = 0;
                                                    int studentsCount = 0;
                                                    if (post.getMessageToUsers().contains("|")) {
                                                        List<String> items = Arrays.asList(post.getMessageToUsers().split("\\s*,\\s*"));
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
                                                    postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, post.getAttachments());
                                                    postEdit.putExtra(SelectUsersActivity.SELECTED_TEACHERS_COUNT, teachersCount);
                                                    postEdit.putExtra(SelectUsersActivity.SELECTED_STUDENTS_COUNT, studentsCount);
                                                    postEdit.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER, studentsIDs);
                                                    postEdit.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER, teachersIDs);
                                                } else if (post.getPostTypeInt() == Flinnt.POST_TYPE_BLOG) {
                                                    postEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_BLOG_EDIT);
                                                    postEdit.putExtra(PostDetailsResponse.POST_ATTACHMENT_KEY, post.getAttachments());
                                                } else if (post.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                                                    String photos = post.getAttachments();
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

                                                mActivity.startActivityForResult(postEdit, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);

                                            } else {
                                                Helper.showNetworkAlertMessage(mActivity);
                                            }
                                            break;
                                        case R.id.commu_bookmark:
                                            if (Helper.isConnected()) {
                                                mCommunicationFragment.adapterPosition = position;
                                                mLikeBookmarkRequest = new LikeBookmarkRequest();

                                                mLikeBookmarkRequest.setUserID(Config.getStringValue(Config.USER_ID));
                                                mLikeBookmarkRequest.setPostID(post.getPostID());
                                                if (post.getIsBookmark().equals(Flinnt.ENABLED)) {
                                                    mLikeBookmark = new LikeBookmark(mCommunicationFragment.mHandler, LikeBookmark.BOOKMARK_REMOVE);
                                                } else {
                                                    mLikeBookmark = new LikeBookmark(mCommunicationFragment.mHandler, LikeBookmark.BOOKMARK_ADD);
                                                }
                                                mLikeBookmark.setLikeBookmarkRequest(mLikeBookmarkRequest);
                                                mLikeBookmark.sendLikeBookmarkRequest();
                                                mCommunicationFragment.startProgressDialog();

                                            } else {
                                                Helper.showNetworkAlertMessage(mActivity);
                                            }
                                            break;

                                        case R.id.commu_delete:
                                            if (Helper.isConnected()) {
                                                if ((post.getCanDeletePost()).equals(Flinnt.ENABLED)) {
                                                    mCommunicationFragment.showDeletePostWarning(mActivity, mCourseId, post);
                                                }
                                            } else {
                                                Helper.showNetworkAlertMessage(mActivity);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    LogWriter.err(e.getMessage());
                }
            } else if (viewHolder instanceof FooterViewHolder) {
                FooterViewHolder footerHolder = (FooterViewHolder) viewHolder;
                if (isOfflineSearch) {
                    footerHolder.readMore.setVisibility(View.GONE);
                } else {
                    footerHolder.readMore.setVisibility(isWithFooter ? View.VISIBLE : View.GONE);
                }

                if ((position + 1) == (filteredDataset.size())) {
                    footerHolder.readMore.setVisibility(isWithFooter ? View.VISIBLE : View.GONE);
                } else {
                    footerHolder.readMore.setVisibility(View.GONE);
                }


//            footerHolder.readMore.setVisibility(isWithFooter ? View.VISIBLE : View.GONE);
            }
    }

    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */

    public void setFilter(String queryText) {
        filteredDataset = new ArrayList<Post>();
        isOfflineSearch = true;
        queryText = queryText.toString().toLowerCase();
        for (Post item : mDataset) {
            if (item.getDescription().toLowerCase().contains(queryText)) {
                filteredDataset.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public boolean getSearchMode() {
        return isSearchMode;
    }

    public void setSearchMode(boolean mode) {
        isSearchMode = mode;
        isOfflineSearch = !isSearchMode;
        filteredDataset = new ArrayList<Post>();
    }


    public void addSearchedItems(List<Post> posts) {
        filteredDataset.addAll(posts);
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        isSearchMode = false;
        isOfflineSearch = false;
        filteredDataset = new ArrayList<Post>();
        filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(
            final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }


    public boolean isWithFooter() {
        return isWithFooter;
    }

    public void setWithFooter(boolean withFooter) {
        this.isWithFooter = withFooter;
    }

    /**
     * Update communication menu item based on Flag
     */

    private void updateCommunicationMenu(Menu menu, Post mPost) {

        MenuItem editPostItem = menu.findItem(R.id.commu_edit);
        MenuItem bookmarkItem = menu.findItem(R.id.commu_bookmark);
        MenuItem deleteItem = menu.findItem(R.id.commu_delete);

        if (mPost.getCanEdit().equals(Flinnt.ENABLED)) {
            editPostItem.setVisible(true);
            if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL) {
                editPostItem.setTitle(mActivity.getResources().getString(R.string.edit_poll));
            } else {
                editPostItem.setTitle(mActivity.getResources().getString(R.string.edit_post));
            }
        } else {
            editPostItem.setVisible(false);
        }


        if (mPost.getPostTypeInt() == Flinnt.POST_TYPE_POLL || mPost.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE || mPost.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
            bookmarkItem.setVisible(false);
        } else {
            if (mPost.getIsBookmark().equals(Flinnt.ENABLED)) {
                bookmarkItem.setTitle(mActivity.getResources().getString(R.string.unbookmark_txt));
            } else {
                bookmarkItem.setTitle(mActivity.getResources().getString(R.string.bookmark_txt));
            }
        }


        if (mPost.getCanDeletePost().equals(Flinnt.ENABLED)) {
            deleteItem.setVisible(true);
        } else {
            deleteItem.setVisible(false);
        }
    }

    /**
     * .Intent to get video
     */
    private void startVideoIntent(Post mPost) {
        try {
            String filename = Helper.getFlinntVideoPath() + mPost.getAttachments();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filename)), "video/*");
            mActivity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * .Intent to get document
     */
    private void startDocumentIntent(Post mPost) {
        try {
            String filename = Helper.getFlinntDocumentPath() + mPost.getAttachments();
            MediaHelper.showDocument(filename, mActivity);
        } catch (Exception e) {
        }
    }

    private String getYouTubeVideoId(Post mPost) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
        Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(mPost.getAttachments());
        while (matcher.find()) {
            System.out.println(matcher.group());
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Set download handler
     */
    private void setDownloadMediaFileandHandler(Post mPost) {
        if (mCommunicationFragment.mDownload == null) {
            long postID = Long.parseLong(mPost.getPostID());
            if (DownloadFileManager.isContainID(postID)) {
                mCommunicationFragment.mDownload = DownloadFileManager.get(postID);
                if (mCommunicationFragment.mDownload != null) {
                    // mDownload.setHandler(mHandler);
                }
            }
        }
    }


    private void playOrDownloadMedia(Post mPost) {
        switch (mPost.getPostContentTypeInt()) {
            case Flinnt.POST_CONTENT_GALLERY:
                try {
                    String filename = Helper.getFlinntImagePath() + mPost.getAttachments();
                    if (new File(filename).exists()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(filename)), "image/*");
                        mActivity.startActivity(intent);
                    } else {
                        Helper.showAlertMessage(mActivity, mActivity.getString(R.string.image), mActivity.getString(R.string.image_not_download), mActivity.getString(R.string.ok));
                    }
                } catch (Exception e) {
                }
                break;
            case Flinnt.POST_CONTENT_VIDEO:
                if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO && mPost.getAttachmentsIsUrl().equals(Flinnt.DISABLED)) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), mPost.getAttachments())) {

                    } else {
                        startVideoIntent(mPost);
                    }
                } else if (mPost.getPostContentTypeInt() == Flinnt.POST_CONTENT_VIDEO && mPost.getAttachmentsIsUrl().equals(Flinnt.ENABLED)) {
                    String videoId = getYouTubeVideoId(mPost);
                    final Intent lightboxIntent = new Intent(mActivity, YoutubeCustomLightboxActivity.class);
                    lightboxIntent.putExtra(YoutubeCustomLightboxActivity.KEY_VIDEO_ID, videoId);
                    mActivity.startActivity(lightboxIntent);
                }
                break;
            case Flinnt.POST_CONTENT_DOCUMENT:
                if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mPost.getAttachments())) {
                    String docurl = mPost.getAttachmentUrl() + mPost.getAttachments();

                } else {
                    startDocumentIntent(mPost);
                }
                break;
            default:
                break;
        }
    }

    private void startAudioIntent(int position, Post mPost, final ViewHolder holder) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("startAudioIntent");
        if (holder.mediaPlayer == null) {
            holder.mediaPlayer = new MediaPlayer();
            if (mPlayingPosition != -1) {
                int tempPosition = mPlayingPosition;
                mPlayingPosition = position;
                CommunicationListAdapter.this.notifyItemChanged(tempPosition);
            }
            mPlayingPosition = position;
        }

        try {
            String audioFile = Helper.getFlinntAudioPath() + mPost.getAttachments();
            holder.mediaPlayer.setDataSource(audioFile);
            holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    performOnEndMedia(holder.mediaPlayer, holder);
                }

            });
            holder.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LogWriter.info(what + " " + extra);
                    return false;
                }
            });
            holder.mediaPlayer.prepare();
            holder.mediaPlayer.start();
            audioViewholder = holder;
            audioPosition = position;
            holder.mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_pause_arrow_white));
            holder.mCurrentDuration.setBase(SystemClock.elapsedRealtime());
            baseChronometer = 0;
            holder.mCurrentDuration.start();
            int secs = (holder.mediaPlayer.getDuration() / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            holder.mEndDuration.setText(String.format("%s:%s", String.format("%02d", mins), String.format("%02d", secs)));

            holder.mAudioProgressBar.setVisibility(ProgressBar.VISIBLE);
            holder.mAudioProgressBar.setProgress(0);
            holder.mAudioProgressBar.setMax(holder.mediaPlayer.getDuration());
            holder.audioThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    int currentPosition = 0;
                    int total = holder.mediaPlayer.getDuration();
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("currentPosition : " + currentPosition + ", total : " + total);
                    while (holder.mediaPlayer != null && currentPosition <= total && (total - currentPosition) > 200) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("currentPosition : " + currentPosition + ", total : " + total);
                        try {
                            Thread.sleep(100);
                            if (holder.mediaPlayer != null) {
                                currentPosition = holder.mediaPlayer.getCurrentPosition();
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        holder.mAudioProgressBar.setProgress(currentPosition);
                    }
                }
            });
            holder.audioThread.start();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    /**
     * Reset the player after playing media ends
     *
     * @param mediaPlayer
     */
    private void performOnEndMedia(MediaPlayer mediaPlayer, final ViewHolder holder) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("on complete media...");
        if (null != mediaPlayer) {
            holder.mAudioProgressBar.setProgress(mediaPlayer.getDuration());
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("currentDuration stop");
                    holder.mCurrentDuration.stop();
                    holder.mCurrentDuration.setBase(SystemClock.elapsedRealtime());
                    holder.mAudioProgressBar.setProgress(0);
                    baseChronometer = 0;
                    holder.mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_arrow_white));
                }
            });
        }
    }

    private int mPlayingPosition = -1;

    public void stopMediaControl() {
        try {
            if (audioViewholder.mediaPlayer != null) {

                audioViewholder.mediaPlayer.stop();
                audioViewholder.mediaPlayer.release();
                baseChronometer = 0;
                audioViewholder.mAudioPlayButtonImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_arrow_white));
                audioViewholder.mCurrentDuration.stop();
                audioViewholder.mCurrentDuration.setBase(SystemClock.elapsedRealtime());
                audioViewholder.mAudioProgressBar.setProgress(0);
                audioViewholder.audioThread.yield();
                audioViewholder.mediaPlayer = null;

            }
        } catch (Exception e) {
            LogWriter.err(e.getMessage());
        }
    }

}