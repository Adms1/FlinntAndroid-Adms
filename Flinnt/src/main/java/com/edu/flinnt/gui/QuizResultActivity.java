package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.QuizResultSummary;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.QuizResultSummaryResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;

public class QuizResultActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar mMainToolbar;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizName = "";
    private TextView mTxtTakenDate, mTxtTakenDuration;
    private Button mBtnViewResult, mBtnResultAnalysis;
    private TextView mTxtResultSummary, mTxtResultGrade , mTxtResultRank;
    private ImageView mImgEmojiResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_quiz_result);

        //Log.d("Contt","QuizResultActivity : onCreate()");

        getIntentData();
        initUI();
        setUpToolBar();
        getResultSummary();
    }

    private void initUI() {
        mTxtResultSummary = (TextView)findViewById(R.id.mark_obtained_tv);
        mTxtTakenDate = (TextView)findViewById(R.id.quiz_date_tv);
        mTxtTakenDuration = (TextView)findViewById(R.id.total_duration_tv);
        mTxtResultGrade = (TextView)findViewById(R.id.grade_tv);
        mTxtResultRank = (TextView)findViewById(R.id.rank_text_tv);
        mBtnViewResult = (Button)findViewById(R.id.view_result_button);
        mBtnViewResult.setOnClickListener(this);
        mBtnResultAnalysis = (Button)findViewById(R.id.result_analysis_button);
        mBtnResultAnalysis.setOnClickListener(this);
        mImgEmojiResult = (ImageView)findViewById(R.id.resut_emoji_iv);
    }

    private void getResultSummary() {
        startProgressDialog();
        QuizResultSummary summary = new QuizResultSummary(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what){
                    case Flinnt.SUCCESS:
                        showResult((QuizResultSummaryResponse)msg.obj);
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse)msg.obj);
                        break;
                }
                return false;
            }
        }),courseId,quizId,contentId);
        summary.sendQuizResultSummaryRequest();
    }

    private void handleError(BaseResponse obj) {
        if(obj.errorResponse != null && !TextUtils.isEmpty(obj.errorResponse.getMessage()))
            Helper.showAlertMessage(QuizResultActivity.this, "Error", obj.errorResponse.getMessage(), "Close");
        else
            Helper.showAlertMessage(QuizResultActivity.this, "Error", getString(R.string.default_error_message_quiz_result), "Close");

    }

    private void showResult(QuizResultSummaryResponse response) {
        QuizResultSummaryResponse.Result result = response.getData().getResult();
        mTxtTakenDate.setText(result.getAttemptDate());
        mTxtTakenDuration.setText(result.getDuration());
        mTxtResultSummary.setText(String.format("%s/%s(%d%%)", result.getMarksObtained(), result.getMarksMax(), result.getScorePercent()));
        mTxtResultGrade.setText(result.getGradeText());
        mTxtResultRank.setText(String.format("%d/%d", result.getRank(), result.getRankTotal()));
        updateResultImage(response);
    }

    private void updateResultImage(QuizResultSummaryResponse response) {
        float density = getResources().getDisplayMetrics().density;

        String quizEmojiUrl = "";
        if (density < 1.5) {
            quizEmojiUrl = response.getData().getResult().getGradePictureURL() + Flinnt.GRADE_PICTURE_MDPI + File.separator + response.getData().getResult().getGradePicture();
        } else if (density >= 1.5 && density < 2.0) {
            quizEmojiUrl = response.getData().getResult().getGradePictureURL() + Flinnt.GRADE_PICTURE_HDPI + File.separator + response.getData().getResult().getGradePicture();
        } else if (density >= 2.0 && density < 3.0) {
            quizEmojiUrl = response.getData().getResult().getGradePictureURL() + Flinnt.GRADE_PICTURE_XDPI + File.separator + response.getData().getResult().getGradePicture();
        } else if (density >= 3.0 && density < 4.0) {
            quizEmojiUrl = response.getData().getResult().getGradePictureURL() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + response.getData().getResult().getGradePicture();
        } else {
            quizEmojiUrl = response.getData().getResult().getGradePictureURL() + Flinnt.GRADE_PICTURE_XXXHDPI + File.separator + response.getData().getResult().getGradePicture();
        }


        if(quizEmojiUrl.contains(".gif")) {
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mImgEmojiResult);
            Glide.with(this).load(quizEmojiUrl).into(imageViewTarget);
        }else {
            Glide.with(QuizResultActivity.this)
                    .load(quizEmojiUrl)
// .placeholder(getRandomDrawbleColor())
                    .into(mImgEmojiResult);
        }
    }

    private void setUpToolBar() {
        mMainToolbar = (Toolbar)findViewById(R.id.toolbar) ;
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(quizName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData() {
        courseId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_COURSE_ID);
        quizId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_ID);
        contentId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_CONTENT_ID);
        quizName = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_NAME);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_result_button:
                viewResult();
                break;
            case R.id.result_analysis_button:
                viewResultAnalysis();
                break;

        }
    }

    private void viewResult() {

        if(!Helper.isConnected()){
            Helper.showNetworkAlertMessage(this);
            return;
        }

        Intent quizIntent = new Intent(this, QuizViewResultActivity.class);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
        startActivity(quizIntent);
    }

    private void viewResultAnalysis() {

        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }

        Intent quizIntent = new Intent(this, QuizResultAnalysisActivity.class);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
        startActivity(quizIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(QuizResultActivity.this, "activity="+Flinnt.QUIZ_RESULT+"&user="+ Config.getStringValue(Config.USER_ID)+"&course="+courseId+"&contentid="+contentId+"&quizid="+quizId);
            GoogleAnalytics.getInstance(QuizResultActivity.this).reportActivityStart(QuizResultActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(QuizResultActivity.this).reportActivityStop(QuizResultActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
}
