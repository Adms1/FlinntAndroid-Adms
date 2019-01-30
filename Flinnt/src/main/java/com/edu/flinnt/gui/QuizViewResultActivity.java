package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Chronometer;
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
import com.edu.flinnt.core.QuizViewResult;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.downloadsmultithread.DownloadInfo;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.QuizViewResultResponse;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadFileManager;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.DownloadUtils;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.edu.flinnt.Flinnt.POST_CONTENT_AUDIO;
import static com.edu.flinnt.Flinnt.POST_CONTENT_VIDEO;


public class QuizViewResultActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {
    public static final String KEY_COURSE_ID = "course_id";
    public static final String KEY_QUIZ_ID = "quiz_id";
    public static final String KEY_CONTENT_ID = "content_id";
    public static final String KEY_QUIZ_NAME = "quiz_name";

    private TextView mQuestionScoreTextView, mPreviosTextView, mQuestionCounterTextView, mNextTextView, mQuestionDurationTextView, mQuizSectionNameTextView;
    private Toolbar mMainToolbar;
    private ImageView mInformationImageView;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizName = "";
    private RelativeLayout parentLaout;
    private NestedScrollView mNestedDetailScrollView;
    private QuizViewResultResponse mQuizViewResultResponse = null;
    private int currentQuestion = 0;

    //New changes
    private WebView mQuestionDetailTextView;
    private TextView mQuestionNumberTextView;
    private RecyclerView mOptionRecyclerView;
    private List<QuizViewResultResponse.Option> mOptionsList = new ArrayList<>();
    private QuizResultAnswerOptionAdapter mOptionsAdapter;
    private Spanned spannedValue;
    private CardView mExplanationCardView;
    private WebView mExplnationTextView;


    RelativeLayout mediaRelative;
    FrameLayout youtubeFrame, mediaFrame;
    LinearLayout audioLinear;
    private YouTubePlayerFragment quizTubePlayerFragment;
    private YouTubePlayer quizYouTubePlayer;
    private String quizYoutubeVedioID;
    private boolean quizFullScreen;
    private ImageButton mediaOpenImgbtn;
    private ImageLoader mImageLoader;
    private ImageView mediaThumnailImgview;


    private ImageView quizDownloadBtn;
    com.github.lzyzsd.circleprogress.DonutProgress quizProgressBar;
    private ProgressBar quizProgressBarHint;

    private DownloadReceiver mReceiver;
    private Common mCommon;
    QuizViewResultResponse.Result quizDownload = null;


    private DownloadMediaFile mDownload;
    private MediaPlayer mediaPlayer;
    private ProgressBar audioProgressBar;
    private Thread audioThread;
    private TextView endDuration;
    private Chronometer currentDuration;
    private ImageButton audioPlayButton;
    long baseChronometer = 0;

    public Handler mHandlerDownload = null;
    private int mPlayingPosition = -1;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_quiz_view_result);

        //Log.d("Contt","QuizViewResultActivity : onCreate()");

        getIntentData();
        initUI();
        setUpToolBar();
        getQuizResult();

        mHandlerDownload = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {


                //.d("mHandlerDownload",message.obj.toString());
                // Gets the task from the incoming Message object.
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("handleMessage : " + message.what);

                stopProgressDialog();
                switch (message.what) {
                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");
                        if (quizDownload.getAttachments().get(0).getFileName().equals(Integer.toString(Flinnt.POST_CONTENT_VIDEO))) {
                            startVideoIntent(quizDownload);
                        } else if (quizDownload.getAttachments().get(0).getFileName().equals(Integer.toString(Flinnt.POST_CONTENT_AUDIO))) {
                            startAudioIntent();
                        }

                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        mDownload = null;
                        if (!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                            Helper.showAlertMessage(QuizViewResultActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
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
    }

    private void setUpToolBar() {
        setSupportActionBar(mMainToolbar);
        mMainToolbar.setTitle("");
        mMainToolbar.setSubtitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(quizName);
    }

    private void getIntentData() {
        courseId = getIntent().getExtras().getString(KEY_COURSE_ID);
        quizId = getIntent().getExtras().getString(KEY_QUIZ_ID);
        contentId = getIntent().getExtras().getString(KEY_CONTENT_ID);
        contentId = getIntent().getExtras().getString(KEY_CONTENT_ID);
        quizName = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_NAME);
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    private void getQuizResult() {
        if (!Helper.isConnected()) {
            Helper.showToast(getString(R.string.no_internet_conn_message_dialog), Toast.LENGTH_LONG);
            finish();
            return;
        }
        startProgressDialog();
        QuizViewResult mQuizStart = new QuizViewResult(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {


                //Log.d("mHandlerDownload",msg.obj.toString());

                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        mQuestionCounterTextView.setVisibility(View.VISIBLE);
                        mNestedDetailScrollView.setVisibility(View.VISIBLE);
                        QuizViewResultResponse response = (QuizViewResultResponse) msg.obj;
                       // for(int i=0;i<response.getData().getResult().size();i++)
                        //Log.d("mHandlerDownload",response.getData().getResult().get(0).getText());

                        if (response != null) {
                            mQuizViewResultResponse = response;
                            currentQuestion = 0;
                            setData(mQuizViewResultResponse.getData().getResult().get(currentQuestion));
                            updateNavigationButton();
                        }
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId);
        mQuizStart.sendQuizViewResultRequest();
    }

    private void handleError(BaseResponse obj) {
        if (obj.errorResponse != null && !TextUtils.isEmpty(obj.errorResponse.getMessage()))
            Helper.showToast(obj.errorResponse.getMessage(), Toast.LENGTH_LONG);
        else
            Helper.showToast(getString(R.string.default_error_message_quiz_result), Toast.LENGTH_LONG);
        finish();
    }

    private void setUpQuestionsAdapter() {
//        QuizViewAnswerPagerAdapter questionsAdapter = new QuizViewAnswerPagerAdapter(getSupportFragmentManager(), mQuizViewResultResponse.getData().getResult());
//        mQuestionsViewPager.setAdapter(questionsAdapter);
//        mQuestionsViewPager.setOffscreenPageLimit(1);
    }

    private void initUI() {
        mQuestionDurationTextView = (TextView) findViewById(R.id.question_time_tv);
        mPreviosTextView = (TextView) findViewById(R.id.button_previous_questions);
        mNextTextView = (TextView) findViewById(R.id.button_next_questions);
        mQuestionScoreTextView = (TextView) findViewById(R.id.question_score_tv);
        mQuizSectionNameTextView = (TextView) findViewById(R.id.text_view_section_name);
//        mQuestionsViewPager = (LockableViewPager) findViewById(R.id.view_pager_quiz);
//        mQuestionsViewPager.setOffscreenPageLimit(1);
        mQuestionCounterTextView = (TextView) findViewById(R.id.text_view_question_counter);
        mMainToolbar = (Toolbar) findViewById(R.id.toolbar);
        mInformationImageView = (ImageView) findViewById(R.id.image_information);
        parentLaout = (RelativeLayout) findViewById(R.id.parent_layout);
        mPreviosTextView.setOnClickListener(this);
        mNextTextView.setOnClickListener(this);
        mInformationImageView.setOnClickListener(this);


        //
        mNestedDetailScrollView = (NestedScrollView) findViewById(R.id.question_detail_scrollView);
        mQuestionDetailTextView = (WebView) findViewById(R.id.view_question_webview);
        mQuestionNumberTextView = (TextView) findViewById(R.id.question_number_tv);
        mOptionRecyclerView = (RecyclerView) findViewById(R.id.view_answers_rv);
        mExplnationTextView = (WebView) findViewById(R.id.explanation_webview);
        mExplanationCardView = (CardView) findViewById(R.id.explanation_card_view);


        mediaRelative = (RelativeLayout) findViewById(R.id.media_relative);
        youtubeFrame = (FrameLayout) findViewById(R.id.youtube_frame);
        mediaFrame = (FrameLayout) findViewById(R.id.media_frame);
        mediaFrame.setOnClickListener(this);
        audioLinear = (LinearLayout) findViewById(R.id.audio_linear);
        quizTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        mediaOpenImgbtn = (ImageButton) findViewById(R.id.media_open_imgbtn);
        mediaOpenImgbtn.setOnClickListener(this);
        mediaThumnailImgview = (ImageView) findViewById(R.id.media_thumbnail_img);
        mediaThumnailImgview.setOnClickListener(this);
        quizDownloadBtn = (ImageView) findViewById(R.id.download_btn);

        quizProgressBar = (com.github.lzyzsd.circleprogress.DonutProgress) findViewById(R.id.progress_bar);
        quizProgressBar.setTextColor(R.color.gray);
        quizProgressBar.setTextSize((float) 5.0);
        quizProgressBarHint = (ProgressBar) findViewById(R.id.progress_barHint);


        audioPlayButton = (ImageButton) findViewById(R.id.audio_play_img);
        audioPlayButton.setOnClickListener(this);
        audioProgressBar = (ProgressBar) findViewById(R.id.audio_progressBar);
        audioProgressBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        currentDuration = (Chronometer) findViewById(R.id.audio_current_duration_txt);
        endDuration = (TextView) findViewById(R.id.audio_end_duration_txt);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next_questions:
                pauseAllControl();
                if (mNextTextView.getText().equals(getString(R.string.finish))) {
                    finish();
                    return;
                }
                showNextQuestion();
                break;
            case R.id.button_previous_questions:
                pauseAllControl();
                showPreviousQuestion();
                break;
            case R.id.image_information:
                showInformationDialog();
                break;
            case R.id.audio_play_img:   //for audio file

                //@Chirag add storage permissioon popup 06/08/2018
                if (AskPermition.getInstance(QuizViewResultActivity.this).isStoragePermitted()) {
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), quizDownload.getAttachments().get(0).getFileName())) {
                        String audiourl = quizDownload.getAttachments().get(0).getAttachmentUrl() + quizDownload.getAttachments().get(0).getFileName();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("audiourl : " + audiourl);

                        setDownloadMediaFileandHandler();
                        mDownload = new DownloadMediaFile(QuizViewResultActivity.this, Helper.getFlinntAudioPath(), quizDownload.getAttachments().get(0).getFileName(), Long.parseLong(quizDownload.getId()), audiourl, mHandlerDownload);
                        mDownload.execute();
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
                        LogWriter.write("*********audio**" + quizDownload.getAttachments().get(0).getFileName());
                        startAudioIntent();
                    }
                }
                break;
            case R.id.media_thumbnail_img:
            case R.id.media_frame: //for video file

                //@Chirag add storage permissioon popup 06/08/2018
                if (AskPermition.getInstance(QuizViewResultActivity.this).isStoragePermitted()) {

                    if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), quizDownload.getAttachments().get(0).getFileName())) {
                        String videourl = quizDownload.getAttachments().get(0).getAttachmentUrl() + quizDownload.getAttachments().get(0).getFileName();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("videourl : " + videourl);
                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(quizDownload.getId(), quizDownload.getAttachments().get(0).getFileName(), videourl, videourl, Flinnt.DISABLED);
                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                        quizProgressBarHint.setVisibility(View.VISIBLE);
                        download(0, videourl, quizDownload, appInfoDataSet);
                    } else {
                        startVideoIntent(quizDownload);
                    }
                }
                break;
            case R.id.media_open_imgbtn:
                startVideoIntent(quizDownload);
                break;
            default:
                break;
        }
    }

    private void pauseAllControl() {
        if (null != quizYouTubePlayer) {
            quizYouTubePlayer.release();
        }
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                baseChronometer = currentDuration.getBase() - SystemClock.elapsedRealtime();
                currentDuration.stop();
                audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white));
            }
        }
    }


    private void stopMediaControl() {
        if (mediaPlayer != null && mPlayingPosition != currentQuestion) {
            mediaPlayer.stop();
            mediaPlayer.release();
            baseChronometer = 0;
            audioPlayButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white));
            currentDuration.stop();
            currentDuration.setBase(SystemClock.elapsedRealtime());
            audioProgressBar.setProgress(0);
            audioThread.yield();
            mediaPlayer = null;
            endDuration.setText("" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
        }
    }

    private void showInformationDialog() {
        InformationDialog informationDialog = new InformationDialog();
        informationDialog.show(getSupportFragmentManager(), "Information dialog fragment");
    }

    private synchronized void showPreviousQuestion() {
        //currentQuestion = mQuestionsViewPager.getCurrentItem();
        currentQuestion = currentQuestion - 1;
        //mQuestionsViewPager.setCurrentItem(currentQuestion);
        setData(mQuizViewResultResponse.getData().getResult().get(currentQuestion));
        updateNavigationButton();
    }

    private synchronized void showNextQuestion() {
        //currentQuestion = mQuestionsViewPager.getCurrentItem();
        if (currentQuestion >= (mQuizViewResultResponse.getData().getResult().size() - 1)) {
            return;
        }
        currentQuestion = currentQuestion + 1;
        setData(mQuizViewResultResponse.getData().getResult().get(currentQuestion));
        updateNavigationButton();
    }


    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    private void setData(final QuizViewResultResponse.Result result) {
        mOptionsList.clear();
        quizDownload = result;
        if (result.getAttachments().size() > 0) {
            if (result.getAttachments().get(0).getAttachType().equals(Integer.toString(POST_CONTENT_VIDEO))) {
                mediaRelative.setVisibility(View.VISIBLE);
                audioLinear.setVisibility(View.GONE);
                if (result.getAttachments().get(0).getIsUrl().equals(Flinnt.ENABLED)) {

                    youtubeFrame.setVisibility(View.VISIBLE);
                    quizYoutubeVedioID = null;
                    String youtubeUrl = result.getAttachments().get(0).getFileName();

                    String packageName = "com.google.android.youtube";
                    boolean isYoutubeInstalled = isAppInstalled(packageName);

                    String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
                    Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = compiledPattern.matcher(youtubeUrl);
                    while (matcher.find()) {
                        System.out.println(matcher.group());
                        quizYoutubeVedioID = matcher.group(1);
                    }

                    if (isYoutubeInstalled) {
                        quizTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, QuizViewResultActivity.this);
                        mediaFrame.setVisibility(View.GONE);
                        mediaOpenImgbtn.setVisibility(View.GONE);
                        youtubeFrame.setVisibility(View.VISIBLE);
                    } else {
                        isAppNotInstalled();
                    }
                } else {
                    youtubeFrame.setVisibility(View.GONE);
                    mediaFrame.setVisibility(View.VISIBLE);


                    String videourl = result.getAttachments().get(0).getAttachmentUrl() + result.getAttachments().get(0).getFileName();
                    AppInfoDataSet appInfoDataSet = new AppInfoDataSet(result.getId(), result.getAttachments().get(0).getFileName(), videourl, videourl, Flinnt.DISABLED);
                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                    result.setAppInfoDataSets(appInfoDataSet);
                    //  quizDownload.setAppInfoDataSets(appInfoDataSet);


                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(result.getAppInfoDataSets().getUrl());
                    if (downloadInfo != null) {
                        result.getAppInfoDataSets().setProgress(downloadInfo.getProgress());
                        result.getAppInfoDataSets().setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        result.getAppInfoDataSets().setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }


                    if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), result.getAttachments().get(0).getFileName()) && appInfoDataSet.getStatusText().equals("Not Download")) {
                        mediaFrame.setVisibility(View.GONE);
                    } else {
                        if (appInfoDataSet.getStatusText().equals("Complete")) {
                            mediaFrame.setVisibility(View.GONE);
                        } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
                            mediaFrame.setVisibility(View.VISIBLE);
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                            quizProgressBar.setProgress(0);
                        } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                            mediaFrame.setVisibility(View.VISIBLE);
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                        } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                            mediaFrame.setVisibility(View.VISIBLE);
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                        } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                            mediaFrame.setVisibility(View.VISIBLE);
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                        }
                    }

                    String videoPreviewUrl = mQuizViewResultResponse.getData().getVideo_preview_url() + result.getAttachments().get(0).getAttachment_video_thumb();
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("video Preview Url : " + videoPreviewUrl);
                    mImageLoader.get(videoPreviewUrl, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.video_default, R.drawable.video_default));
                }
            } else if (result.getAttachments().get(0).getAttachType().equals(Integer.toString(POST_CONTENT_AUDIO))) {
                mediaRelative.setVisibility(View.GONE);
                audioLinear.setVisibility(View.VISIBLE);
                stopMediaControl();
            }
        }
        if (mOptionsAdapter != null)
            mOptionsAdapter.notifyDataSetChanged();
        mOptionsList.addAll(result.getOptions());
        mQuestionNumberTextView.setText(String.format("%d", currentQuestion + 1));
        mQuestionDetailTextView.loadData(result.getText(), "text/html", "UTF-8");
        mQuestionDetailTextView.setWebChromeClient(new WebChromeClient());
        mQuestionDetailTextView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            WebViewDialog newFragment = new WebViewDialog(result.getText(), "Question " + (currentQuestion + 1));
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                        }
                    }
                }
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mOptionRecyclerView.setLayoutManager(layoutManager);
        mOptionRecyclerView.getItemAnimator().setChangeDuration(0);
        if (mOptionsAdapter == null) {
            mOptionsAdapter = new QuizResultAnswerOptionAdapter(mOptionsList) {
                @Override
                public void onOptionClicked(String data, int position) {
                    int charAscii = 65 + position;
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    WebViewDialog newFragment = new WebViewDialog(data, "Option " + (char) charAscii);
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                }
            };
            mOptionRecyclerView.setAdapter(mOptionsAdapter);
        } else {
            mOptionsAdapter = new QuizResultAnswerOptionAdapter(mOptionsList) {
                @Override
                public void onOptionClicked(String data, int position) {
                    int charAscii = 65 + position;
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    WebViewDialog newFragment = new WebViewDialog(data, "Option " + (char) charAscii);
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                }
            };
            mOptionRecyclerView.swapAdapter(mOptionsAdapter, true);
        }
        if (result.getExplanation() != null && !TextUtils.isEmpty(result.getExplanation())) {
            mExplanationCardView.setVisibility(View.VISIBLE);
            mExplnationTextView.loadData(result.getExplanation(), "text/html", "UTF-8");
        } else {
            mExplanationCardView.setVisibility(View.GONE);
        }

        mExplnationTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            WebViewDialog newFragment = new WebViewDialog(result.getExplanation(), "Explanation");
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                        }
                    }
                }
                return true;

            }
        });

    }


    private synchronized void updateNavigationButton() {
        mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizViewResultResponse.getData().getResult().size()));
        QuizViewResultResponse.Result result = mQuizViewResultResponse.getData().getResult().get(currentQuestion);
        mQuestionScoreTextView.setText(result.getScore());
        mQuestionDurationTextView.setText(String.format("%s\tsec", result.getReactionTime()));
        mQuizSectionNameTextView.setText(Html.fromHtml(result.getSection()));
        if (currentQuestion <= 0) {
            mPreviosTextView.setVisibility(View.GONE);
            return;
        } else {
            mPreviosTextView.setVisibility(View.VISIBLE);
        }

        if (currentQuestion >= (mQuizViewResultResponse.getData().getResult().size() - 1)) {
            mNextTextView.setText(getString(R.string.finish));
        } else {
            mNextTextView.setText(getString(R.string.next));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pauseAllControl();
        if (quizFullScreen) {
            quizYouTubePlayer.setFullscreen(false);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    private ProgressDialog mProgressDialog = null;

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    @SuppressLint("ValidFragment")
    public static class InformationDialog extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dialog_answer_information, container,
                    false);
            TextView gotItTextView = (TextView) rootView.findViewById(R.id.text_view_got_it);
            gotItTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return rootView;
        }
    }

    @SuppressLint("ValidFragment")
    public static class WebViewDialog extends DialogFragment {

        String data = "";
        String title = "";
        Context context;

        public WebViewDialog(String data, String title) {
            this.data = data;
            this.title = title;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dialog_webview_content, container, false);
            context = rootView.getContext();
            WebView webView = (WebView) rootView.findViewById(R.id.web_view_content);
            TextView titleTextView = (TextView) rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(title);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadData(data, "text/html", "UTF-8");
            ImageView imgClose = (ImageView) rootView.findViewById(R.id.iv_close);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((AppCompatActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
                        dismiss();
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (((AppCompatActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
            }
            super.onDismiss(dialog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(QuizViewResultActivity.this, "activity=" + Flinnt.QUIZ_VIEW_RESULT + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseId + "&contentid=" + contentId + "&quizid=" + quizId);
            GoogleAnalytics.getInstance(QuizViewResultActivity.this).reportActivityStart(QuizViewResultActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(QuizViewResultActivity.this).reportActivityStop(QuizViewResultActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(quizYoutubeVedioID);
            quizYouTubePlayer = youTubePlayer;
            quizYouTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean _isFullScreen) {
                    quizFullScreen = _isFullScreen;
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        isAppNotInstalled();

    }

    private void isAppNotInstalled() {
        youtubeFrame.setVisibility(View.GONE);
        mediaFrame.setVisibility(View.GONE);
        int defaultDrawable = R.drawable.youtube_video_fram_not_get;
        mediaOpenImgbtn.setVisibility(View.VISIBLE);
        String url = "http://img.youtube.com/vi/" + quizYoutubeVedioID + "/0.jpg";
        mImageLoader.get(url, ImageLoader.getImageListener(mediaThumnailImgview, defaultDrawable, defaultDrawable));
    }

    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
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
                if (quizDownload.getId().equals(tmpInfoDataSet.getId())) {
                    if (tmpInfoDataSet == null || position == -1) {
                        return;
                    }

                    AppInfoDataSet appInfoDataSet;
                    appInfoDataSet = quizDownload.getAppInfoDataSets();
                    final int status = tmpInfoDataSet.getStatus();

                    switch (status) {
                        case AppInfoDataSet.STATUS_CONNECTING:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
                            if (tmpInfoDataSet.getProgress() > 1 && !Helper.isFileExistsAtPath(tmpInfoDataSet.getDownloadFilePath(), tmpInfoDataSet.getName())) {
                                cancel(position, tmpInfoDataSet.getUrl(), tmpInfoDataSet);
                                quizProgressBarHint.setVisibility(View.GONE);
                            } else {
                            }

                            break;

                        case AppInfoDataSet.STATUS_DOWNLOADING:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_pause);
                            quizProgressBarHint.setVisibility(View.GONE);

                            break;
                        case AppInfoDataSet.STATUS_COMPLETE:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                            mediaFrame.setVisibility(View.GONE);

                            break;

                        case AppInfoDataSet.STATUS_PAUSED:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_play);
                            quizProgressBarHint.setVisibility(View.GONE);

                            break;
                        case AppInfoDataSet.STATUS_NOT_DOWNLOAD:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                            quizProgressBar.setProgress(appInfoDataSet.getProgress());
                            quizProgressBarHint.setVisibility(View.GONE);

                            break;
                        case AppInfoDataSet.STATUS_DOWNLOAD_ERROR:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
                            appInfoDataSet.setDownloadPerSize("");

                            break;
                        case AppInfoDataSet.STATUS_CANCEL:
                            appInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
                            appInfoDataSet.setProgress(tmpInfoDataSet.getProgress());
                            appInfoDataSet.setDownloadPerSize(tmpInfoDataSet.getDownloadPerSize());
                            quizDownloadBtn.setBackgroundResource(R.drawable.ic_download);
                            quizProgressBar.setProgress(0);
                            quizProgressBarHint.setVisibility(View.GONE);
                            break;
                    }
                }
            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    private void stopDownload() {
        //stop download if working...
        if (mDownload != null) {
            mDownload.setCancel(true);
        }
    }


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
        mReceiver = new QuizViewResultActivity.DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(QuizViewResultActivity.this).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(QuizViewResultActivity.this).unregisterReceiver(mReceiver);
        }
    }

    private void download(int position, String tag, QuizViewResultResponse.Result mQuiz, AppInfoDataSet info) {
        quizDownload = mQuiz;
        DownloadService.intentDownload(QuizViewResultActivity.this, position, position, tag, info);
    }

    private void pause(int position, String tag, QuizViewResultResponse.Result mQuiz, AppInfoDataSet info) {
        quizDownload = mQuiz;
        DownloadService.intentPause(QuizViewResultActivity.this, position, position, tag, info);
    }

    private void cancel(int position, String tag, AppInfoDataSet info) {

        DownloadService.intentCancel(QuizViewResultActivity.this, position, tag, info);
    }

    /**
     * .Intent to get video
     *
     * @param quiz
     */
    private void startVideoIntent(QuizViewResultResponse.Result quiz) {
        try {
            String filename = Helper.getFlinntVideoPath() + quiz.getAttachments().get(0).getFileName();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filename)), "video/*");
            startActivity(intent);
        } catch (Exception e) {
            Helper.showToast(getString(R.string.no_app_available), Toast.LENGTH_LONG);
        }
    }

    /**
     * Set download handler
     */
    private void setDownloadMediaFileandHandler() {
        if (mDownload == null) {
            long postID = Long.parseLong(quizDownload.getId());
            if (DownloadFileManager.isContainID(postID)) {
                mDownload = DownloadFileManager.get(postID);
                if (mDownload != null) {
                    mDownload.setHandler(mHandler);
                }
            }
        }
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        try {
            mProgressDialog = new ProgressDialog(QuizViewResultActivity.this);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (DialogInterface.OnClickListener) null);

            mProgressDialog.show();

            mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
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

    private void startAudioIntent() {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("startAudioIntent");
        mediaPlayer = new MediaPlayer();
       /* if (mediaPlayer == null) {

            if (mPlayingPosition != -1) {
                int tempPosition = mPlayingPosition;
                mPlayingPosition = currentQuestion;
            }
            mPlayingPosition = currentQuestion;
        }*/
        try {
            String audioFile = Helper.getFlinntAudioPath() + quizDownload.getAttachments().get(0).getFileName();
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
            Helper.showToast(getString(R.string.no_app_available), Toast.LENGTH_LONG);
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
                    audioPlayButton.setImageDrawable(ContextCompat.getDrawable(QuizViewResultActivity.this, R.drawable.ic_play_arrow_white));
                }
            });
        }
    }
}
