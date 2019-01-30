package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
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
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.QuizFinished;
import com.edu.flinnt.core.QuizStart;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.UpdateAnswer;
import com.edu.flinnt.core.UpdateQuestionViewStatus;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.downloadsmultithread.DownloadInfo;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.helper.listner.OnEventFire;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.QuizFinishResponse;
import com.edu.flinnt.protocol.QuizStartResponse;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.edu.flinnt.Flinnt.POST_CONTENT_AUDIO;
import static com.edu.flinnt.Flinnt.POST_CONTENT_VIDEO;


/**
 * Created by flinnt-android-3 on 31/1/17.
 */
public class QuizQuestionsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, YouTubePlayer.OnInitializedListener {

    public static final String KEY_COURSE_ID = "course_id";
    public static final String KEY_QUIZ_ID = "quiz_id";
    public static final String KEY_CONTENT_ID = "content_id";
    public static final String KEY_QUIZ_NAME = "quiz_name";
    public static final String KEY_STATUS = "status";
    private TextView mExamTimerTextView, mFilterTypeTextView, mPreviosTextView, mNextTextView, mQuestionCounterTextView, mFilterTextView, mResetFilterTextView;
    private CheckBox mMarkAsReviewCheckBox;
    //private LockableViewPager mQuestionsViewPager;
    private Toolbar mMainToolbar;
    private RelativeLayout mFilterResetLayout;
    private NestedScrollView mNestedDetailScrollView;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizName = "";
    public static QuizStartResponse mQuizStartResponse = null;
    private int currentQuestion = 0;
    private long reactionTimeStart = 0;
    private String currentAnswerId;
    public static ArrayList<Integer> mReviewPendinArrayList = new ArrayList<>();
    public static ArrayList<Integer> mUnAttemptedArrayList = new ArrayList<>();
    public static ArrayList<Integer> mAttemptedArrayList = new ArrayList<>();
    public static ArrayList<Integer> mUnAnsweredArrayList = new ArrayList<>();
    private static RelativeLayout parentLaout;

    boolean isAttemptedFilter, isUnAttemptedFilter, isUnAsweredFilter, isPendingReviewFilter;

    public void setCurrentQuestion(int pos) {
        currentQuestion = pos;
    }

    //Question details
    private WebView mQuestionDetailWebView;
    private TextView mQuestionNumberTextView;
    private RecyclerView mOptionRecyclerView;
    private ArrayList<QuizStartResponse.Option> mOptionsList = new ArrayList<>();
    private QuizQuestionOptionAdapter mOptionsAdapter;

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
    QuizStartResponse.Quiz quizDownload = null;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_content_quiz_detail);

        //Log.d("Contt", "QuizQuestionsActivity : onCreate()");

        getIntentData();
        initUI();
        setUpToolBar();
        startQuiz();


        mHandlerDownload = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("handleMessage : " + message.what);

                //Log.d("Contt", "QuizQuestionsActivity : handleMessage : " + message.what);

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
                            Helper.showAlertMessage(QuizQuestionsActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
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
        quizName = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_NAME);
        mImageLoader = Requester.getInstance().getImageLoader();

    }


    private void startQuiz() {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }
        startProgressDialog();
        QuizStart mQuizStart = new QuizStart(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        mQuestionCounterTextView.setVisibility(View.VISIBLE);
                        QuizStartResponse response = (QuizStartResponse) msg.obj;
                        if (response != null) {
                            mQuizStartResponse = response;
                            for (int i = 0; i < mQuizStartResponse.getData().getQuiz().size(); i++) {
                                QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(i);
                                quiz.setRelativePosition(i + 1);
                                if (!quiz.getMarkedForReview().equals("0"))
                                    mReviewPendinArrayList.add(Integer.valueOf(i));
                                if (quiz.getAnswered() == 1) {
                                    mAttemptedArrayList.add(Integer.valueOf(i));
                                } else if (quiz.getAnswered() == 0 && quiz.getViewed() == 0) {
                                    mUnAttemptedArrayList.add(Integer.valueOf(i));
                                } else if (quiz.getAnswered() == 0 && quiz.getViewed() == 1) {
                                    mUnAnsweredArrayList.add(Integer.valueOf(i));
                                }
                            }
                            if (response.getData().getTest_start_timestamp() < 0) {
                                startCountDownTimer(Math.abs(response.getData().getTest_start_timestamp()));
                            } else {
                                durationSeconds = response.getData().getTest_start_timestamp();
                                startTimer();
                            }
                            mNestedDetailScrollView.setVisibility(View.VISIBLE);
                            reactionTimeStart = System.currentTimeMillis();
                            mQuestionCounterTextView.setText((currentQuestion + 1) + "/" + response.getData().getQuiz().size());
                            setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                            updateNavigationButton();
                        }
                        break;
                    case Flinnt.FAILURE:
                        if (((BaseResponse) msg.obj).errorResponse != null) {
                            if (!isErrorCodeHandled((BaseResponse) msg.obj))
                                Helper.showToast(((BaseResponse) msg.obj).errorResponse.getMessage(), Toast.LENGTH_LONG);
                        } else
                            Helper.showAlertMessage(QuizQuestionsActivity.this, "Error", getString(R.string.default_error_message_quiz_details), "Close");
                        finish();
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId);
        mQuizStart.sendQuizStartRequest();
    }

    private void handleError(BaseResponse obj) {
        if (obj.errorResponse != null && !TextUtils.isEmpty(obj.errorResponse.getMessage())) {
            if (isErrorCodeHandled(obj)) {
                return;
            }
            Helper.showAlertMessage(this, "Error", obj.errorResponse.getMessage(), "Close");
        } else
            Helper.showAlertMessage(this, "Error", getString(R.string.default_error_message_quiz_details), "Close");
    }

    private boolean isErrorCodeHandled(BaseResponse obj) {
        for (int i = 0; i < obj.errorResponse.getErrorList().size(); i++) {
            com.edu.flinnt.protocol.Error error = obj.errorResponse.getErrorList().get(i);
            if (error.getCode() == ErrorCodes.ERROR_CODE_702) {
                Intent quizIntent = new Intent(QuizQuestionsActivity.this, QuizResultActivity.class);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, 4);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
                QuizQuestionsActivity.this.startActivity(quizIntent);
                finish();
                return true;
            }

            if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                // TO:DO if user is not authorized
                showQuizDeletedDialog(error.getMessage());
                return true;
            }
        }
        return false;
    }


    public void showQuizDeletedDialog(String errorMsg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set resorceTitleTxt
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
        alertDialogBuilder.setMessage(errorMsg);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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

    private void initUI() {
        mExamTimerTextView = (TextView) findViewById(R.id.exam_timer_tv);
        mPreviosTextView = (TextView) findViewById(R.id.button_previous_questions);
        mNextTextView = (TextView) findViewById(R.id.button_next_questions);
        mQuestionCounterTextView = (TextView) findViewById(R.id.text_view_question_counter);
        mMarkAsReviewCheckBox = (CheckBox) findViewById(R.id.checkbox_mark_review);
        //mQuestionsViewPager = (LockableViewPager) findViewById(R.id.view_pager_quiz);
        mMainToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFilterResetLayout = (RelativeLayout) findViewById(R.id.layout_filter_display);
        mFilterTypeTextView = (TextView) findViewById(R.id.selected_filter_tv);
        mFilterTextView = (TextView) findViewById(R.id.selected_filter_tv);
        mResetFilterTextView = (TextView) findViewById(R.id.reset_filter_tv);
        parentLaout = (RelativeLayout) findViewById(R.id.parent_layout);
        mPreviosTextView.setOnClickListener(this);
        mNextTextView.setOnClickListener(this);
        mMarkAsReviewCheckBox.setOnCheckedChangeListener(this);
        mResetFilterTextView.setOnClickListener(this);


        //Question details
        mNestedDetailScrollView = (NestedScrollView) findViewById(R.id.question_detail_scrollView);
        mQuestionDetailWebView = (WebView) findViewById(R.id.view_question_webview);
        mQuestionNumberTextView = (TextView) findViewById(R.id.question_number_tv);
        mOptionRecyclerView = (RecyclerView) findViewById(R.id.view_answers_rv);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content_quiz, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.action_question_list:
                pauseAllControl();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                QuizDialog qFragment = new QuizDialog(courseId, quizId, contentId, quizName, reactionTimeStart);
                qFragment.setQUiz(currentQuestion, mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                qFragment.setQuizz(mQuizStartResponse.getData().getQuiz());
                qFragment.setOnEventFire(new OnEventFire() {
                    @Override
                    public void onEvent(int position) {
                        setCurrentQuestion(position);
                        setData(mQuizStartResponse.getData().getQuiz().get(position));
                        updateViewStatus(position);
                        resetFilters();
                    }

                    @Override
                    public void onUnattempted() {
                        isUnAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onUnanswered() {
                        isUnAsweredFilter = true;
                        isUnAttemptedFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAnsweredFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onPendingReview() {
                        isPendingReviewFilter = true;
                        isUnAsweredFilter = isUnAttemptedFilter = isAttemptedFilter = false;
                        setPendingReviewFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onAttempted() {
                        isAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isUnAttemptedFilter = false;
                        setAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }
                });
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, qFragment).addToBackStack(null).commit();

                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideVIew() {
        parentLaout.setVisibility(View.GONE);
    }

    public void showVIew() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        parentLaout.setVisibility(View.VISIBLE);
    }


    //Exam counter
    private Timer timer;
    private TimerTask timerTask;
    private Handler mHandler = new Handler();
    private long durationSeconds = 0;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private CountDownTimer countDownTimer;

    private void startCountDownTimer(long time) {
        durationSeconds = time;
        countDownTimer = new CountDownTimer(durationSeconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mHandler.post(new Runnable() {
                    public void run() {
                        durationSeconds = durationSeconds - 1;
                        mExamTimerTextView.setText(String.format(" - %s", String.format("%02d:%02d:%02d", durationSeconds / 3600,
                                (durationSeconds % 3600) / 60, (durationSeconds % 60))));
                    }
                });                //here you can have your logic to set text to edittext
            }

            public void onFinish() {

                if (!mQuizStartResponse.getData().getAuto_terminate().equals("1")) {
                    durationSeconds = 0;
                    startTimer();
                    return;
                }

                UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        stopProgressDialog();
                        switch (msg.what) {
                            case Flinnt.SUCCESS:
                                QuizFinished quizFinished = new QuizFinished(new Handler(new Handler.Callback() {
                                    @Override
                                    public boolean handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case Flinnt.SUCCESS:
                                                onTimeFinished();
                                                break;
                                            case Flinnt.FAILURE:
                                                handleError((BaseResponse) msg.obj);
                                                break;

                                        }
                                        return false;
                                    }
                                }), courseId, quizId, contentId);
                                quizFinished.sendQuizFinishRequest();
                                break;
                            case Flinnt.FAILURE:
                                handleError((BaseResponse) msg.obj);
                                break;
                        }

                        return false;
                    }
                }), courseId, quizId, contentId, mQuizStartResponse.getData().getQuiz().get(currentQuestion).getQuizQueId(), mQuizStartResponse.getData().getQuiz().get(currentQuestion).getAnswerId(), System.currentTimeMillis() - reactionTimeStart + "", mQuizStartResponse.getData().getQuiz().get(currentQuestion).getMarkedForReview());
                request.sendUpdateAnswerRequest();
            }

        };
        countDownTimer.start();
    }

    private void onTimeFinished() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.finish_quiz_message));
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent quizIntent = new Intent(QuizQuestionsActivity.this, QuizResultActivity.class);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, 4);
                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
                QuizQuestionsActivity.this.startActivity(quizIntent);
                finish();
            }
        });
        dialog.show();
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                mHandler.post(new Runnable() {
                    public void run() {
                        durationSeconds = durationSeconds + 1;
                        mExamTimerTextView.setText(String.format(" %s", String.format("%02d:%02d:%02d", durationSeconds / 3600,
                                (durationSeconds % 3600) / 60, (durationSeconds % 60))));
                    }
                });
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (timer != null) {
            timer.purge();
            timer.cancel();
        }

        if (timerTask != null) {
            timerTask.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next_questions:
                pauseAllControl();
                showNextQuestion();
                break;
            case R.id.button_previous_questions:
                pauseAllControl();
                showPreviousQuestion();
                break;
            case R.id.reset_filter_tv:
                resetFilters();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.audio_play_img: //for audio file

                //@Chirag add here permission check code for storage
                if (AskPermition.getInstance(QuizQuestionsActivity.this).isStoragePermitted()) {

                    if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), quizDownload.getAttachments().get(0).getFileName())) {
                        String audiourl = quizDownload.getAttachments().get(0).getAttachmentUrl() + quizDownload.getAttachments().get(0).getFileName();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("audiourl : " + audiourl);

                        setDownloadMediaFileandHandler();
                        mDownload = new DownloadMediaFile(QuizQuestionsActivity.this, Helper.getFlinntAudioPath(), quizDownload.getAttachments().get(0).getFileName(), Long.parseLong(quizDownload.getId()), audiourl, mHandlerDownload);
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
                        startAudioIntent();
                    }
                }
                break;
            case R.id.media_thumbnail_img:
            case R.id.media_frame: //for video file

                //@Chirag add storage permissioon popup 06/08/2018
                if (AskPermition.getInstance(QuizQuestionsActivity.this).isStoragePermitted()) {
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


    private void resetFilters() {
        if (mFilterResetLayout.getVisibility() == View.VISIBLE) {
            mFilterResetLayout.setVisibility(View.GONE);
        }
        reactionTimeStart = System.currentTimeMillis();
        updateNavigationButton();
        isAttemptedFilter = isUnAttemptedFilter = isUnAsweredFilter = isPendingReviewFilter = false;
        mQuestionCounterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_text_view_reset));
        mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizStartResponse.getData().getQuiz().size()));
    }

    @Override
    public void onBackPressed() {
        //Log.d("Qqu","onBackPressed.");
        if (quizFullScreen) {
            quizYouTubePlayer.setFullscreen(false);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            parentLaout.setVisibility(View.VISIBLE);
        } else {
            showExitConfirmationDialog();
        }
    }

    private void showExitConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.exit_quiz_message));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pauseAllControl();
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void showPreviousQuestion() {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }

        if (isUnAsweredFilter || isUnAttemptedFilter || isPendingReviewFilter || isAttemptedFilter) {
            showPreviousFilteredQuestion();
            return;
        }

        QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(currentQuestion);
        if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {
            reactionTimeStart = System.currentTimeMillis();
            currentQuestion = currentQuestion - 1;
            setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
            mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizStartResponse.getData().getQuiz().size()));
            updateNavigationButton();
            return;
        }
        startProgressDialog();
        UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        reactionTimeStart = System.currentTimeMillis();
                        currentQuestion = currentQuestion - 1;
                        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                        mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizStartResponse.getData().getQuiz().size()));
                        updateNavigationButton();
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }

                return false;
            }
        }), courseId, quizId, contentId, quiz.getQuizQueId(), quiz.getAnswerId(), System.currentTimeMillis() - reactionTimeStart + "", quiz.getMarkedForReview());

        if (quiz.getAnswerId() == null || quiz.getAnswerId().equals("0")) {
            if (!mUnAnsweredArrayList.contains(Integer.valueOf(currentQuestion)))
                mUnAnsweredArrayList.add(Integer.valueOf(currentQuestion));
        } else {
            if (!mAttemptedArrayList.contains(Integer.valueOf(currentQuestion)))
                mAttemptedArrayList.add(Integer.valueOf(currentQuestion));
        }

        request.sendUpdateAnswerRequest();
    }

    private void showNextQuestion() {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }

        if (isUnAsweredFilter || isUnAttemptedFilter || isPendingReviewFilter || isAttemptedFilter) {
            showNexFilteredQuestion();
            return;
        }

        QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(currentQuestion);
        if (currentQuestion >= (mQuizStartResponse.getData().getQuiz().size() - 1)) {
            if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                QuizDialog newFragment = new QuizDialog(courseId, quizId, contentId, quizName, reactionTimeStart);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                newFragment.setQUiz(currentQuestion, quiz);
                newFragment.setQuizz(mQuizStartResponse.getData().getQuiz());
                newFragment.setOnEventFire(new OnEventFire() {
                    @Override
                    public void onEvent(int position) {
                        setCurrentQuestion(position);
                        setData(mQuizStartResponse.getData().getQuiz().get(position));
                        updateViewStatus(position);
                        resetFilters();
                    }

                    @Override
                    public void onUnattempted() {
                        isUnAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onUnanswered() {
                        isUnAsweredFilter = true;
                        isUnAttemptedFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAnsweredFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onPendingReview() {
                        isPendingReviewFilter = true;
                        isUnAsweredFilter = isUnAttemptedFilter = isAttemptedFilter = false;
                        setPendingReviewFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onAttempted() {
                        isAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isUnAttemptedFilter = false;
                        setAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }
                });
                return;
            }
        }
        if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {
            reactionTimeStart = System.currentTimeMillis();
            currentQuestion = currentQuestion + 1;
            setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
            mQuestionCounterTextView.setText((currentQuestion + 1) + "/" + mQuizStartResponse.getData().getQuiz().size());
            updateNavigationButton();
            return;
        }
        startProgressDialog();
        UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (mNextTextView.getText().equals(getString(R.string.finish))) {
                            //Log.d("quizresponseses", "this is from finish textbox");
                            //@Nikhil 672108
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            QuizDialog qFragment = new QuizDialog(courseId, quizId, contentId, quizName, reactionTimeStart);
                            qFragment.setQuizz(mQuizStartResponse.getData().getQuiz());
                            qFragment.setQUiz(currentQuestion, mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, qFragment).addToBackStack(null).commit();
                            qFragment.setOnEventFire(new OnEventFire() {
                                @Override
                                public void onEvent(int position) {
                                    setCurrentQuestion(position);
                                    setData(mQuizStartResponse.getData().getQuiz().get(position));
                                    updateViewStatus(position);
                                    resetFilters();
                                }

                                @Override
                                public void onUnattempted() {
                                    isUnAttemptedFilter = true;
                                    isUnAsweredFilter = isPendingReviewFilter = isAttemptedFilter = false;
                                    setUnAttemptedFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onUnanswered() {
                                    isUnAsweredFilter = true;
                                    isUnAttemptedFilter = isPendingReviewFilter = isAttemptedFilter = false;
                                    setUnAnsweredFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onPendingReview() {
                                    isPendingReviewFilter = true;
                                    isUnAsweredFilter = isUnAttemptedFilter = isAttemptedFilter = false;
                                    setPendingReviewFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onAttempted() {
                                    isAttemptedFilter = true;
                                    isUnAsweredFilter = isPendingReviewFilter = isUnAttemptedFilter = false;
                                    setAttemptedFilter();
                                    updateNavigationButtonFiltered();
                                }
                            });
                            break;
                        }
                        reactionTimeStart = System.currentTimeMillis();
                        currentQuestion = currentQuestion + 1;
                        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                        mQuestionCounterTextView.setText((currentQuestion + 1) + "/" + mQuizStartResponse.getData().getQuiz().size());
                        updateNavigationButton();
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId, quiz.getQuizQueId(), quiz.getAnswerId(), System.currentTimeMillis() - reactionTimeStart + "", quiz.getMarkedForReview());
        if (quiz.getAnswerId() == null || quiz.getAnswerId().equals("0")) {
            if (!mUnAnsweredArrayList.contains(Integer.valueOf(currentQuestion)))
                mUnAnsweredArrayList.add(Integer.valueOf(currentQuestion));
        } else {
            if (!mAttemptedArrayList.contains(Integer.valueOf(currentQuestion)))
                mAttemptedArrayList.add(Integer.valueOf(currentQuestion));
        }
        request.sendUpdateAnswerRequest();
    }

    private void showNexFilteredQuestion() {

        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }

        final QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(currentQuestion);
        if (mNextTextView.getText().equals(getString(R.string.finish))) {
            if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                QuizDialog newFragment = new QuizDialog(courseId, quizId, contentId, quizName, reactionTimeStart);
                newFragment.setQuizz(mQuizStartResponse.getData().getQuiz());
                newFragment.setQUiz(currentQuestion, quiz);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                newFragment.setOnEventFire(new OnEventFire() {
                    @Override
                    public void onEvent(int position) {
                        setCurrentQuestion(position);
                        setData(mQuizStartResponse.getData().getQuiz().get(position));
                        updateViewStatus(position);
                        resetFilters();
                    }

                    @Override
                    public void onUnattempted() {
                        isUnAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onUnanswered() {
                        isUnAsweredFilter = true;
                        isUnAttemptedFilter = isPendingReviewFilter = isAttemptedFilter = false;
                        setUnAnsweredFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onPendingReview() {
                        isPendingReviewFilter = true;
                        isUnAsweredFilter = isUnAttemptedFilter = isAttemptedFilter = false;
                        setPendingReviewFilter();
                        updateNavigationButtonFiltered();
                    }

                    @Override
                    public void onAttempted() {
                        isAttemptedFilter = true;
                        isUnAsweredFilter = isPendingReviewFilter = isUnAttemptedFilter = false;
                        setAttemptedFilter();
                        updateNavigationButtonFiltered();
                    }
                });

                return;
            }
        }
        if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {
            reactionTimeStart = System.currentTimeMillis();
            currentQuestion = getNextFilteredQuestion(currentQuestion);
            setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
            mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizStartResponse.getData().getQuiz().size()));
            updateNavigationButtonFiltered();
            return;
        }

        startProgressDialog();
        UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (mNextTextView.getText().equals(getString(R.string.finish))) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            QuizDialog newFragment = new QuizDialog();
                            newFragment.setQUiz(currentQuestion, quiz);
                            newFragment.setQuizz(mQuizStartResponse.getData().getQuiz());
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                            newFragment.setOnEventFire(new OnEventFire() {
                                @Override
                                public void onEvent(int position) {
                                    setCurrentQuestion(position);
                                    setData(mQuizStartResponse.getData().getQuiz().get(position));
                                    updateViewStatus(position);
                                    resetFilters();
                                }

                                @Override
                                public void onUnattempted() {
                                    isUnAttemptedFilter = true;
                                    isUnAsweredFilter = isPendingReviewFilter = isAttemptedFilter = false;
                                    setUnAttemptedFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onUnanswered() {
                                    isUnAsweredFilter = true;
                                    isUnAttemptedFilter = isPendingReviewFilter = isAttemptedFilter = false;
                                    setUnAnsweredFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onPendingReview() {
                                    isPendingReviewFilter = true;
                                    isUnAsweredFilter = isUnAttemptedFilter = isAttemptedFilter = false;
                                    setPendingReviewFilter();
                                    updateNavigationButtonFiltered();
                                }

                                @Override
                                public void onAttempted() {
                                    isAttemptedFilter = true;
                                    isUnAsweredFilter = isPendingReviewFilter = isUnAttemptedFilter = false;
                                    setAttemptedFilter();
                                    updateNavigationButtonFiltered();
                                }
                            });
                            break;
                        }
                        reactionTimeStart = System.currentTimeMillis();
                        currentQuestion = getNextFilteredQuestion(currentQuestion);
                        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                        updateNavigationButtonFiltered();
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId, quiz.getQuizQueId(), quiz.getAnswerId(), System.currentTimeMillis() - reactionTimeStart + "", quiz.getMarkedForReview());
        request.sendUpdateAnswerRequest();

    }

    private int getNextFilteredQuestion(int currentQuestion) {
        if (isAttemptedFilter) {
            int position = mAttemptedArrayList.indexOf(currentQuestion);
            if (position == (mAttemptedArrayList.size() - 1))
                return -1;
            return mAttemptedArrayList.get(position + 1);
        }
        if (isUnAsweredFilter) {
            int position = mUnAnsweredArrayList.indexOf(currentQuestion);
            if (position == (mUnAnsweredArrayList.size() - 1))
                return -1;
            return mUnAnsweredArrayList.get(position + 1);
        }
        if (isPendingReviewFilter) {
            int position = mReviewPendinArrayList.indexOf(currentQuestion);
            if (position == (mReviewPendinArrayList.size() - 1))
                return -1;
            return mReviewPendinArrayList.get(position + 1);
        }
        if (isUnAttemptedFilter) {
            int position = mUnAttemptedArrayList.indexOf(currentQuestion);
            if (position == (mUnAttemptedArrayList.size() - 1))
                return -1;
            return mUnAttemptedArrayList.get(position + 1);
        }
        return -1;
    }

    private void showPreviousFilteredQuestion() {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }

        QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(currentQuestion);
        if (currentAnswerId != null && currentAnswerId.equals(quiz.getAnswerId())) {
            reactionTimeStart = System.currentTimeMillis();
            currentQuestion = getPreviousFilteredQuestion(currentQuestion);
            setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
            mQuestionCounterTextView.setText(String.format("%d/%d", currentQuestion + 1, mQuizStartResponse.getData().getQuiz().size()));
            updateNavigationButtonFiltered();
            return;
        }

        startProgressDialog();
        UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        reactionTimeStart = System.currentTimeMillis();
                        currentQuestion = getPreviousFilteredQuestion(currentQuestion);
                        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
                        updateNavigationButtonFiltered();
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId, quiz.getQuizQueId(), quiz.getAnswerId(), System.currentTimeMillis() - reactionTimeStart + "", quiz.getMarkedForReview());
        request.sendUpdateAnswerRequest();
    }


    private int getPreviousFilteredQuestion(int currentQuestion) {
        if (isAttemptedFilter) {
            int position = mAttemptedArrayList.indexOf(currentQuestion);
            if (position <= 0)
                return -1;
            return mAttemptedArrayList.get(position - 1);
        }
        if (isUnAsweredFilter) {
            int position = mUnAnsweredArrayList.indexOf(currentQuestion);
            if (position <= 0)
                return -1;
            return mUnAnsweredArrayList.get(position - 1);
        }
        if (isPendingReviewFilter) {
            int position = mReviewPendinArrayList.indexOf(currentQuestion);
            if (position <= 0)
                return -1;
            return mReviewPendinArrayList.get(position - 1);
        }
        if (isUnAttemptedFilter) {
            int position = mUnAttemptedArrayList.indexOf(currentQuestion);
            if (position <= 0)
                return -1;
            return mUnAttemptedArrayList.get(position - 1);
        }
        return -1;
    }

    private void updateNavigationButtonFiltered() {
        updateViewStatus(currentQuestion);
        if (mQuizStartResponse.getData().getQuiz().get(currentQuestion).getMarkedForReview().equals("0")) {
            mMarkAsReviewCheckBox.setChecked(false);
        } else {
            mMarkAsReviewCheckBox.setChecked(true);
        }

        currentAnswerId = mQuizStartResponse.getData().getQuiz().get(currentQuestion).getAnswerId();


        if (!hasNextFilteredQuestion(currentQuestion)) {
            mNextTextView.setText(getString(R.string.finish));
        } else {
            mNextTextView.setText(getString(R.string.next));
        }

        if (hasPreviousFilteredQuestion(currentQuestion)) {
            mPreviosTextView.setVisibility(View.GONE);
        } else {
            mPreviosTextView.setVisibility(View.VISIBLE);
        }

        if (!hasNextFilteredQuestion(currentQuestion)) {
            mNextTextView.setText(getString(R.string.finish));
        } else {
            mNextTextView.setText(getString(R.string.next));
        }
        setFilterQuestionCounter();
    }

    private void setFilterQuestionCounter() {
        if (isAttemptedFilter) {
            int position = mAttemptedArrayList.indexOf(currentQuestion);
            mQuestionCounterTextView.setText(String.format("%d/%d", position + 1, mAttemptedArrayList.size()));
        }
        if (isUnAsweredFilter) {
            int position = mUnAnsweredArrayList.indexOf(currentQuestion);
            mQuestionCounterTextView.setText(String.format("%d/%d", position + 1, mUnAnsweredArrayList.size()));
        }
        if (isPendingReviewFilter) {
            int position = mReviewPendinArrayList.indexOf(currentQuestion);
            mQuestionCounterTextView.setText(String.format("%d/%d", position + 1, mReviewPendinArrayList.size()));
        }
        if (isUnAttemptedFilter) {
            int position = mUnAttemptedArrayList.indexOf(currentQuestion);
            mQuestionCounterTextView.setText(String.format("%d/%d", position + 1, mUnAttemptedArrayList.size()));
        }
    }

    private boolean hasNextFilteredQuestion(int currentQuestion) {
        if (isAttemptedFilter) {
            int position = mAttemptedArrayList.indexOf(currentQuestion);
            if (position == (mAttemptedArrayList.size() - 1))
                return false;
            return true;
        }
        if (isUnAsweredFilter) {
            int position = mUnAnsweredArrayList.indexOf(currentQuestion);
            if (position == (mUnAnsweredArrayList.size() - 1))
                return false;
            return true;
        }
        if (isPendingReviewFilter) {
            int position = mReviewPendinArrayList.indexOf(currentQuestion);
            if (position == (mReviewPendinArrayList.size() - 1))
                return false;
            return true;
        }
        if (isUnAttemptedFilter) {
            int position = mUnAttemptedArrayList.indexOf(currentQuestion);
            if (position == (mUnAttemptedArrayList.size() - 1))
                return false;
            return true;
        }
        return false;
    }

    private boolean hasPreviousFilteredQuestion(int currentQuestion) {
        if (isAttemptedFilter) {
            int position = mAttemptedArrayList.indexOf(currentQuestion);
            if (position == 0)
                return true;
            return false;
        }
        if (isUnAsweredFilter) {
            int position = mUnAnsweredArrayList.indexOf(currentQuestion);
            if (position == 0)
                return true;
            return false;
        }
        if (isPendingReviewFilter) {
            int position = mReviewPendinArrayList.indexOf(currentQuestion);
            if (position == 0)
                return true;
            return false;
        }
        if (isUnAttemptedFilter) {
            int position = mUnAttemptedArrayList.indexOf(currentQuestion);
            if (position == 0)
                return true;
            return false;
        }
        return false;
    }

    private void updateNavigationButton() {
        updateViewStatus(currentQuestion);
        if (mQuizStartResponse.getData().getQuiz().get(currentQuestion).getMarkedForReview().equals("0")) {
            mMarkAsReviewCheckBox.setChecked(false);
        } else {
            mMarkAsReviewCheckBox.setChecked(true);
        }

        currentAnswerId = mQuizStartResponse.getData().getQuiz().get(currentQuestion).getAnswerId();

        if (currentQuestion >= (mQuizStartResponse.getData().getQuiz().size() - 1)) {
            mNextTextView.setText(getString(R.string.finish));
        } else {
            mNextTextView.setText(getString(R.string.next));
        }

        if (currentQuestion <= 0) {
            mPreviosTextView.setVisibility(View.GONE);
            return;
        } else {
            mPreviosTextView.setVisibility(View.VISIBLE);
        }

        if (currentQuestion >= (mQuizStartResponse.getData().getQuiz().size() - 1)) {
            mNextTextView.setText(getString(R.string.finish));
        } else {
            mNextTextView.setText(getString(R.string.next));
        }
    }

    private void updateViewStatus(final int currentQuestion) {

        UpdateQuestionViewStatus staus = new UpdateQuestionViewStatus(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        mQuizStartResponse.getData().getQuiz().get(currentQuestion).setViewed(1);
                        mUnAttemptedArrayList.remove(Integer.valueOf(currentQuestion));
                        break;
                    case Flinnt.FAILURE:
                        if (((BaseResponse) msg.obj).errorResponse != null)
                            if (!isErrorCodeHandled((BaseResponse) msg.obj))
                                Helper.showToast(((BaseResponse) msg.obj).errorResponse.getMessage(), Toast.LENGTH_LONG);
                            else
                                Helper.showToast(getString(R.string.default_error_message_quiz_details), Toast.LENGTH_LONG);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId, mQuizStartResponse.getData().getQuiz().get(currentQuestion).getQuizQueId());
        staus.sendUpdateViewStatusRequest();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int currentItem = currentQuestion;
        if (isChecked) {
            mQuizStartResponse.getData().getQuiz().get(currentItem).setMarkedForReview("1");
            if (!mReviewPendinArrayList.contains(Integer.valueOf(currentItem)))
                mReviewPendinArrayList.add(Integer.valueOf(currentItem));
        } else {
            mQuizStartResponse.getData().getQuiz().get(currentItem).setMarkedForReview("0");
            if (mReviewPendinArrayList.contains(Integer.valueOf(currentItem)))
                mReviewPendinArrayList.remove(Integer.valueOf(currentItem));
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


    @SuppressLint("ValidFragment")
    public static class QuizDialog extends DialogFragment {
        QuizDialog fragment;
        private static final String TAG = "QuizDialog";
        private TextView txtUnattempted, txtAttempted, txtPendingReview, txtUnanswered, txtFinishExam;
        private TextView txtUnattemptedCount, txtAttemptedCount, txtPendingReviewCount, txtUnansweredCount, txtFinishExamCount;

        private boolean isUnAttempted = false, isAttempted = false, isPendingReview = false, isUnAnswered = false;
        List<QuizStartResponse.Quiz> mquizList = new ArrayList<>();
        Context context;
        private String mcourseId, mquizId, mcontentId;
        private String mquizName = "";
        private long mreactionTimeStart = 0;
        QuizFilterAdapter mMyCoursesAdapter;
        OnEventFire onEventFire;

        public void setOnEventFire(OnEventFire onEventFire) {
            this.onEventFire = onEventFire;
        }

        public QuizDialog(String courseId, String quizId, String contentId, String quizName, long reactionTimeStart) {
            mcourseId = courseId;
            mquizId = quizId;
            mcontentId = contentId;
            mquizName = quizName;
            mreactionTimeStart = reactionTimeStart;


        }

        public QuizDialog() {
        }

        public void setQuizz(List<QuizStartResponse.Quiz> quizList) {
            mquizList.addAll(quizList);
            if (mMyCoursesAdapter != null)
                mMyCoursesAdapter.notifyDataSetChanged();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.quiz_filter_dialog, container, false);
            context = rootView.getContext();
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.quiz_filter_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mRecyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 5));
            ListSpacingDecoration itemDecoration = new ListSpacingDecoration(rootView.getContext(), R.dimen.item_offset);
            mRecyclerView.addItemDecoration(itemDecoration);

            mMyCoursesAdapter = new QuizFilterAdapter((Activity) context, mquizList) {
                @Override
                public void onItemClick(int position) {
                    onEventFire.onEvent(position);
                    dismiss();
                }
            };
            mRecyclerView.setAdapter(mMyCoursesAdapter);

            txtUnanswered = (TextView) rootView.findViewById(R.id.text_view_unanswered);
            txtUnattempted = (TextView) rootView.findViewById(R.id.text_view_unattempted);
            txtPendingReview = (TextView) rootView.findViewById(R.id.text_view_review_pending);
            txtAttempted = (TextView) rootView.findViewById(R.id.text_view_attempted);
            txtFinishExam = (TextView) rootView.findViewById(R.id.text_view_finish);
            txtUnansweredCount = (TextView) rootView.findViewById(R.id.text_view_unanswered_count);
            txtUnattemptedCount = (TextView) rootView.findViewById(R.id.text_view_unattempted_count);
            txtPendingReviewCount = (TextView) rootView.findViewById(R.id.text_view_review_pending_count);
            txtAttemptedCount = (TextView) rootView.findViewById(R.id.text_view_attempted_count);
            mReviewPendinArrayList.clear();
            mAttemptedArrayList.clear();
            mUnAttemptedArrayList.clear();
            mUnAnsweredArrayList.clear();
            for (int i = 0; i < mQuizStartResponse.getData().getQuiz().size(); i++) {

                QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(i);
                quiz.setRelativePosition(i + 1);
                if (!quiz.getMarkedForReview().equals("0"))
                    mReviewPendinArrayList.add(Integer.valueOf(i));
                if (quiz.getAnswered() == 1) {
                    mAttemptedArrayList.add(Integer.valueOf(i));
                } else if (quiz.getAnswered() == 0 && quiz.getViewed() == 0) {
                    mUnAttemptedArrayList.add(Integer.valueOf(i));
                } else if (quiz.getAnswered() == 0 && quiz.getViewed() == 1) {
                    mUnAnsweredArrayList.add(Integer.valueOf(i));
                }
            }
            if (mAttemptedArrayList.size() > 0)
                txtAttemptedCount.setText(String.format("%d", mAttemptedArrayList.size()));
            else
                txtAttemptedCount.setVisibility(View.INVISIBLE);
            if (mUnAttemptedArrayList.size() > 0)
                txtUnattemptedCount.setText(String.format("%d", mUnAttemptedArrayList.size()));
            else
                txtUnattemptedCount.setVisibility(View.INVISIBLE);
            if (mUnAnsweredArrayList.size() > 0)
                txtUnansweredCount.setText(String.format("%d", mUnAnsweredArrayList.size()));
            else
                txtUnansweredCount.setVisibility(View.INVISIBLE);
            if (mReviewPendinArrayList.size() > 0)
                txtPendingReviewCount.setText(String.format("%d", mReviewPendinArrayList.size()));
            else
                txtPendingReviewCount.setVisibility(View.INVISIBLE);
            ((ImageView) rootView.findViewById(R.id.close_iv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //resetFilters();
                    dismiss();
                }
            });
            txtUnattempted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast("txtUnattempted clicked");
                    if (mUnAttemptedArrayList.size() == 0) {
                        return;
                    }
                    onEventFire.onUnattempted();
                    dismiss();
                }
            });
//
            txtUnanswered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast("txtUnanswered clicked");
                    if (mUnAnsweredArrayList.size() == 0) {
                        return;
                    }
                    onEventFire.onUnanswered();
                    dismiss();
                }
            });

            txtPendingReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReviewPendinArrayList.size() == 0) {
                        return;
                    }
                    onEventFire.onPendingReview();
                    dismiss();
                }
            });

            txtAttempted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAttemptedArrayList.size() == 0) {
                        return;
                    }
                    onEventFire.onAttempted();
                    dismiss();
                }
            });

            txtFinishExam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UpdateAnswer request = new UpdateAnswer(new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case Flinnt.SUCCESS:
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showResult();
                                        }
                                    });
                                    break;
                                case Flinnt.FAILURE:
                                    //@Nikhil
                                    // TODO please seee this code leter

                                    if (((BaseResponse) msg.obj).errorResponse != null && !TextUtils.isEmpty(((BaseResponse) msg.obj).errorResponse.getMessage())) {
                                        //TODO this below code is IMP please make note here
                                        if (isErrorCodeHandled2(((BaseResponse) msg.obj))) {
                                        } else {
                                            Helper.showAlertMessage(context, "Error", ((BaseResponse) msg.obj).errorResponse.getMessage(), "Close");
                                        }
                                    } else
                                        Helper.showAlertMessage(context, "Error", getString(R.string.default_error_message_quiz_details), "Close");


                                    break;
                            }
                            return false;
                        }
                    }), mcourseId, mquizId, mcontentId, mquiz.getQuizQueId(), mquiz.getAnswerId(), System.currentTimeMillis() - mreactionTimeStart + "", mquiz.getMarkedForReview());
                    request.sendUpdateAnswerRequest();
                }
            });


            return rootView;
        }

        private void showToast(String msg) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }

        private boolean isErrorCodeHandled2(BaseResponse obj) {
            for (int i = 0; i < obj.errorResponse.getErrorList().size(); i++) {
                com.edu.flinnt.protocol.Error error = obj.errorResponse.getErrorList().get(i);
                if (error.getCode() == ErrorCodes.ERROR_CODE_702) {
                    Intent quizIntent = new Intent(context, QuizResultActivity.class);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, mcourseId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, mcontentId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, mquizId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, 4);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, mquizName);
                    ((AppCompatActivity) context).startActivity(quizIntent);
                    ((AppCompatActivity) context).finish();
                    return true;
                }

                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                    // TO:DO if user is not authorized
                    //  showQuizDeletedDialog(error.getMessage());
                    return true;
                }
            }
            return false;
        }

        public void showResult() {

            QuizFinished quizFinished = new QuizFinished(new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case Flinnt.SUCCESS:
                            QuizFinishResponse response = (QuizFinishResponse) msg.obj;
                            if (response.getData().getFinished() == 1) {
                                Intent quizIntent = new Intent(context, QuizResultActivity.class);
                                quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, mcourseId);
                                quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, mcontentId);
                                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, mquizId);
                                quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, 4);
                                quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, mquizName);
                                ((AppCompatActivity) (context)).startActivity(quizIntent);
                                ((AppCompatActivity) (context)).finish();
                            }
                            break;

                    }
                    return false;
                }
            }), mcourseId, mquizId, mcontentId);
            quizFinished.sendQuizFinishRequest();


        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            parentLaout.setVisibility(View.GONE);
        }

        @Override
        public void dismiss() {
            parentLaout.setVisibility(View.VISIBLE);
            super.dismiss();
            if (((AppCompatActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0)
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }

        int currentQuestion = 0;
        QuizStartResponse.Quiz mquiz;

        public void setQUiz(int currentQuestion, QuizStartResponse.Quiz quiz) {
            this.currentQuestion = currentQuestion;
            this.mquiz = quiz;
        }
    }

    private void setUnAnsweredFilter() {
        mFilterResetLayout.setVisibility(View.VISIBLE);
        mQuestionCounterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_text_view_unanswered));
        mQuestionCounterTextView.setText(String.format("%d/%d", 1, mUnAnsweredArrayList.size()));
        mFilterTypeTextView.setText(getString(R.string.quiz_unanswered_txt) + " questions only");
        currentQuestion = mUnAnsweredArrayList.get(0);
        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
    }

    private void setUnAttemptedFilter() {
        mFilterResetLayout.setVisibility(View.VISIBLE);
        mQuestionCounterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_text_view_unattempted));
        mQuestionCounterTextView.setText(String.format("%d/%d", 1, mUnAttemptedArrayList.size()));
        currentQuestion = mUnAttemptedArrayList.get(0);
        mFilterTypeTextView.setText(getString(R.string.quiz_unattamped_txt) + " questions only");
        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
    }

    private void setPendingReviewFilter() {
        mFilterResetLayout.setVisibility(View.VISIBLE);
        mQuestionCounterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_text_view_marked));
        mQuestionCounterTextView.setText(String.format("%d/%d", 1, mReviewPendinArrayList.size()));
        currentQuestion = mReviewPendinArrayList.get(0);
        mFilterTypeTextView.setText(getString(R.string.quiz_reviewpending_txt) + " questions only");
        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
    }

    private void setAttemptedFilter() {
        mFilterResetLayout.setVisibility(View.VISIBLE);
        mQuestionCounterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_text_view_attempted));
        mQuestionCounterTextView.setText(String.format("%d/%d", 1, mAttemptedArrayList.size()));
        currentQuestion = mAttemptedArrayList.get(0);
        mFilterTypeTextView.setText(getString(R.string.quiz_attamped_txt) + " questions only");
        setData(mQuizStartResponse.getData().getQuiz().get(currentQuestion));
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

    public static class ListSpacingDecoration extends RecyclerView.ItemDecoration {

        private static final int VERTICAL = OrientationHelper.VERTICAL;

        private int orientation = -1;
        private int spanCount = -1;
        private int spacing;
        private int halfSpacing;


        public ListSpacingDecoration(Context context, @DimenRes int spacingDimen) {

            spacing = context.getResources().getDimensionPixelSize(spacingDimen);
            halfSpacing = spacing / 2;
        }

        public ListSpacingDecoration(int spacingPx) {

            spacing = spacingPx;
            halfSpacing = spacing / 2;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            super.getItemOffsets(outRect, view, parent, state);

            if (orientation == -1) {
                orientation = getOrientation(parent);
            }

            if (spanCount == -1) {
                spanCount = getTotalSpan(parent);
            }

            int childCount = parent.getLayoutManager().getItemCount();
            int childIndex = parent.getChildAdapterPosition(view);

            int itemSpanSize = getItemSpanSize(parent, childIndex);
            int spanIndex = getItemSpanIndex(parent, childIndex);

            /* INVALID SPAN */
            if (spanCount < 1) return;

            setSpacings(outRect, parent, childCount, childIndex, itemSpanSize, spanIndex);
        }

        protected void setSpacings(Rect outRect, RecyclerView parent, int childCount, int childIndex, int itemSpanSize, int spanIndex) {

            outRect.top = halfSpacing;
            outRect.bottom = halfSpacing;
            outRect.left = halfSpacing;
            outRect.right = halfSpacing;

            if (isTopEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
                outRect.top = spacing;
            }

            if (isLeftEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
                outRect.left = spacing;
            }

            if (isRightEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
                outRect.right = spacing;
            }

            if (isBottomEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
                outRect.bottom = spacing;
            }
        }

        @SuppressWarnings("all")
        protected int getTotalSpan(RecyclerView parent) {

            RecyclerView.LayoutManager mgr = parent.getLayoutManager();
            if (mgr instanceof GridLayoutManager) {
                return ((GridLayoutManager) mgr).getSpanCount();
            } else if (mgr instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) mgr).getSpanCount();
            } else if (mgr instanceof LinearLayoutManager) {
                return 1;
            }

            return -1;
        }

        @SuppressWarnings("all")
        protected int getItemSpanSize(RecyclerView parent, int childIndex) {

            RecyclerView.LayoutManager mgr = parent.getLayoutManager();
            if (mgr instanceof GridLayoutManager) {
                return ((GridLayoutManager) mgr).getSpanSizeLookup().getSpanSize(childIndex);
            } else if (mgr instanceof StaggeredGridLayoutManager) {
                return 1;
            } else if (mgr instanceof LinearLayoutManager) {
                return 1;
            }

            return -1;
        }

        @SuppressWarnings("all")
        protected int getItemSpanIndex(RecyclerView parent, int childIndex) {

            RecyclerView.LayoutManager mgr = parent.getLayoutManager();
            if (mgr instanceof GridLayoutManager) {
                return ((GridLayoutManager) mgr).getSpanSizeLookup().getSpanIndex(childIndex, spanCount);
            } else if (mgr instanceof StaggeredGridLayoutManager) {
                return childIndex % spanCount;
            } else if (mgr instanceof LinearLayoutManager) {
                return 0;
            }

            return -1;
        }

        @SuppressWarnings("all")
        protected int getOrientation(RecyclerView parent) {

            RecyclerView.LayoutManager mgr = parent.getLayoutManager();
            if (mgr instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) mgr).getOrientation();
            } else if (mgr instanceof GridLayoutManager) {
                return ((GridLayoutManager) mgr).getOrientation();
            } else if (mgr instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) mgr).getOrientation();
            }

            return VERTICAL;
        }

        protected boolean isLeftEdge(RecyclerView parent, int childCount, int childIndex, int itemSpanSize, int spanIndex) {

            if (orientation == VERTICAL) {

                return spanIndex == 0;

            } else {

                return (childIndex == 0) || isFirstItemEdgeValid((childIndex < spanCount), parent, childIndex);
            }
        }

        protected boolean isRightEdge(RecyclerView parent, int childCount, int childIndex, int itemSpanSize, int spanIndex) {

            if (orientation == VERTICAL) {

                return (spanIndex + itemSpanSize) == spanCount;

            } else {

                return isLastItemEdgeValid((childIndex >= childCount - spanCount), parent, childCount, childIndex, spanIndex);
            }
        }

        protected boolean isTopEdge(RecyclerView parent, int childCount, int childIndex, int itemSpanSize, int spanIndex) {

            if (orientation == VERTICAL) {

                return (childIndex == 0) || isFirstItemEdgeValid((childIndex < spanCount), parent, childIndex);

            } else {

                return spanIndex == 0;
            }
        }

        protected boolean isBottomEdge(RecyclerView parent, int childCount, int childIndex, int itemSpanSize, int spanIndex) {

            if (orientation == VERTICAL) {

                return isLastItemEdgeValid((childIndex >= childCount - spanCount), parent, childCount, childIndex, spanIndex);

            } else {

                return (spanIndex + itemSpanSize) == spanCount;
            }
        }

        protected boolean isFirstItemEdgeValid(boolean isOneOfFirstItems, RecyclerView parent, int childIndex) {

            int totalSpanArea = 0;
            if (isOneOfFirstItems) {
                for (int i = childIndex; i >= 0; i--) {
                    totalSpanArea = totalSpanArea + getItemSpanSize(parent, i);
                }
            }

            return isOneOfFirstItems && totalSpanArea <= spanCount;
        }

        protected boolean isLastItemEdgeValid(boolean isOneOfLastItems, RecyclerView parent, int childCount, int childIndex, int spanIndex) {

            int totalSpanRemaining = 0;
            if (isOneOfLastItems) {
                for (int i = childIndex; i < childCount; i++) {
                    totalSpanRemaining = totalSpanRemaining + getItemSpanSize(parent, i);
                }
            }

            return isOneOfLastItems && (totalSpanRemaining <= spanCount - spanIndex);
        }
    }

    @SuppressLint("ValidFragment")
    public static class WebViewDialog extends DialogFragment {
        Context context;
        String data = "";
        String title = "";

        public WebViewDialog(String data, String title) {
            this.data = data;
            this.title = title;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dialog_webview_content, container,
                    false);
            context = rootView.getContext();
            WebView webView = (WebView) rootView.findViewById(R.id.web_view_content);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadData(data, "text/html", "UTF-8");
            TextView titleTextView = (TextView) rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(title);
            ImageView imgClose = (ImageView) rootView.findViewById(R.id.iv_close);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    ((QuizQuestionsActivity) (getActivity())).showVIew();
                }
            });
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ((QuizQuestionsActivity) (getActivity())).hideVIew();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (((AppCompatActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0)
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    private void setData(final QuizStartResponse.Quiz quiz) {
        mOptionsList.clear();
        mediaRelative.setVisibility(View.GONE);
        audioLinear.setVisibility(View.GONE);
        quizDownload = quiz;
        if (mOptionsAdapter != null)
            mOptionsAdapter.notifyDataSetChanged();
        mOptionsList.addAll(quiz.getOptions());
        mQuestionNumberTextView.setText(quiz.getRelativePosition() + "");
        mQuestionDetailWebView.setMinimumHeight(130);
        mQuestionDetailWebView.loadData(quiz.getText(), "text/html", "UTF-8");
        mQuestionDetailWebView.setOnTouchListener(new View.OnTouchListener() {

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
                            WebViewDialog newFragment = new WebViewDialog(quiz.getText(), "Question " + quiz.getRelativePosition());
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                        }
                    }
                }
                return true;
            }
        });

        //mQuestionDetailWebView.setText(new HtmlSpanner(getActivity()).fromHtml(quiz.getText()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mOptionRecyclerView.setLayoutManager(layoutManager);
        mOptionRecyclerView.getItemAnimator().setChangeDuration(0);
        if (mOptionsAdapter == null) {
            mOptionsAdapter = new QuizQuestionOptionAdapter(this, mOptionsList, mOnAnswerChecked) {
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
            mOptionsAdapter = new QuizQuestionOptionAdapter(this, mOptionsList, mOnAnswerChecked) {
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

        if (quiz.getAttachments().size() > 0) {
            if (quiz.getAttachments().get(0).getAttachType().equals(Integer.toString(POST_CONTENT_VIDEO))) {
                mediaRelative.setVisibility(View.VISIBLE);
                audioLinear.setVisibility(View.GONE);
                if (quiz.getAttachments().get(0).getIsUrl().equals(Flinnt.ENABLED)) {

                    youtubeFrame.setVisibility(View.VISIBLE);
                    quizYoutubeVedioID = null;
                    String youtubeUrl = quiz.getAttachments().get(0).getFileName();

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
                        quizTubePlayerFragment.initialize(Flinnt.DEVELOPER_KEY, QuizQuestionsActivity.this);
                        mediaFrame.setVisibility(View.GONE);
                        mediaOpenImgbtn.setVisibility(View.GONE);
                        youtubeFrame.setVisibility(View.VISIBLE);
                    } else {
                        isAppNotInstalled();
                    }
                } else {
                    youtubeFrame.setVisibility(View.GONE);
                    mediaFrame.setVisibility(View.VISIBLE);


                    String videourl = quiz.getAttachments().get(0).getAttachmentUrl() + quiz.getAttachments().get(0).getFileName();
                    AppInfoDataSet appInfoDataSet = new AppInfoDataSet(quiz.getId(), quiz.getAttachments().get(0).getFileName(), videourl, videourl, Flinnt.DISABLED);
                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                    quiz.setAppInfoDataSets(appInfoDataSet);
                    //  quizDownload.setAppInfoDataSets(appInfoDataSet);


                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadProgress(quiz.getAppInfoDataSets().getUrl());
                    if (downloadInfo != null) {
                        quiz.getAppInfoDataSets().setProgress(downloadInfo.getProgress());
                        quiz.getAppInfoDataSets().setDownloadPerSize(DownloadUtils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                        quiz.getAppInfoDataSets().setStatus(AppInfoDataSet.STATUS_PAUSED);
                    }


                    if (Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), quiz.getAttachments().get(0).getFileName()) && appInfoDataSet.getStatusText().equals("Not Download")) {
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

                    String videoPreviewUrl = mQuizStartResponse.getData().getVideo_preview_url() + quiz.getAttachments().get(0).getAttachment_video_thumb();
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("video Preview Url : " + videoPreviewUrl);
                    mImageLoader.get(videoPreviewUrl, ImageLoader.getImageListener(mediaThumnailImgview, R.drawable.video_default, R.drawable.video_default));
                }
            } else if (quiz.getAttachments().get(0).getAttachType().equals(Integer.toString(POST_CONTENT_AUDIO))) {
                mediaRelative.setVisibility(View.GONE);
                audioLinear.setVisibility(View.VISIBLE);
                stopMediaControl();
            }
        }
    }

    public interface OnAnswerChecked {
        void onAnswerCheckChanged(int position, boolean isChecked, boolean shouldRefresh);
    }

    OnAnswerChecked mOnAnswerChecked = new OnAnswerChecked() {
        @Override
        public void onAnswerCheckChanged(int position, boolean isChecked, boolean shouldRefresh) {
            boolean isAnswerSelected = false;
            QuizStartResponse.Quiz quiz = mQuizStartResponse.getData().getQuiz().get(currentQuestion);
            for (int i = 0; i < mOptionsList.size(); i++) {
                if (i == position && isChecked) {
                    isAnswerSelected = true;
                    mOptionsList.get(i).setSelected(1);
                    quiz.setAnswerId(mOptionsList.get(i).getId());
                    quiz.setAnswered(1);
                } else {
                    mOptionsList.get(i).setSelected(0);
                }
            }
            if (!isAnswerSelected) {
                quiz.setAnswerId("0");
                quiz.setAnswered(0);
            }

            if (!shouldRefresh)
                return;
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    mOptionsAdapter.notifyDataSetChanged();
                }
            };
            handler.post(r);
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(QuizQuestionsActivity.this, "activity=" + Flinnt.QUIZ_QUESTIONS + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseId + "&contentid=" + contentId + "&quizid=" + quizId);
            GoogleAnalytics.getInstance(QuizQuestionsActivity.this).reportActivityStart(QuizQuestionsActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(QuizQuestionsActivity.this).reportActivityStop(QuizQuestionsActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
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
        mReceiver = new QuizQuestionsActivity.DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(QuizQuestionsActivity.this).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(QuizQuestionsActivity.this).unregisterReceiver(mReceiver);
        }
    }

    private void download(int position, String tag, QuizStartResponse.Quiz mQuiz, AppInfoDataSet info) {
        quizDownload = mQuiz;
        DownloadService.intentDownload(QuizQuestionsActivity.this, position, position, tag, info);
    }

    private void pause(int position, String tag, QuizStartResponse.Quiz mQuiz, AppInfoDataSet info) {
        quizDownload = mQuiz;
        DownloadService.intentPause(QuizQuestionsActivity.this, position, position, tag, info);
    }

    private void cancel(int position, String tag, AppInfoDataSet info) {

        DownloadService.intentCancel(QuizQuestionsActivity.this, position, tag, info);
    }

    /**
     * .Intent to get video
     *
     * @param quiz
     */
    private void startVideoIntent(QuizStartResponse.Quiz quiz) {
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
            mProgressDialog = new ProgressDialog(QuizQuestionsActivity.this);
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
                    audioPlayButton.setImageDrawable(ContextCompat.getDrawable(QuizQuestionsActivity.this, R.drawable.ic_play_arrow_white));
                }
            });
        }
    }


}