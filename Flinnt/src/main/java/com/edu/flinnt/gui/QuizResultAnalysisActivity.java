package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.QuizResultAnalysis;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.QuizResultAnalysisResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.RoundedCornersTransformation;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;
import java.util.ArrayList;

import static com.edu.flinnt.core.QuizResultAnalysis.mQuizResultAnalysisResponse;

/**
 * Created by flinnt-android-1 on 9/2/17.
 */

public class QuizResultAnalysisActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mCourseNameTxt, mQuizDateTxt;
    private RecyclerView mChapterRv, mSummaryRv;
    private QuizChapterAdapter mQuizChapterAdapter;
    private QuizSummaryAdapter mQuizSummaryAdapter;
    private NestedScrollView analysisNestedscroll;
    Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private QuizResultAnalysis mQuizResultAnalysis;
    private ArrayList<QuizResultAnalysisResponse.Result> resultlist;
    private TextView gradeTxt,gradeTextTxt,gradesTxt,totalMarksTxt;
    private ImageView gradeIv;
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.quiz_result_analysis);
        Intent quizIntent = new Intent(this, QuizViewResultActivity.class);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
        quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if (bundle.containsKey(QuizQuestionsActivity.KEY_COURSE_ID))
                courseId = bundle.getString(QuizQuestionsActivity.KEY_COURSE_ID);

            if (bundle.containsKey(QuizQuestionsActivity.KEY_CONTENT_ID))
                contentId = bundle.getString(QuizQuestionsActivity.KEY_CONTENT_ID);

            if (bundle.containsKey(QuizQuestionsActivity.KEY_QUIZ_ID))
                quizId = bundle.getString(QuizQuestionsActivity.KEY_QUIZ_ID);

            if (bundle.containsKey(QuizQuestionsActivity.KEY_QUIZ_NAME))
                quizName = bundle.getString(QuizQuestionsActivity.KEY_QUIZ_NAME);

        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(null);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.close);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(quizName);

        initilize();


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());
                        if (msg.obj instanceof QuizResultAnalysisResponse) {
                            mQuizResultAnalysisResponse = (QuizResultAnalysisResponse) msg.obj;
                            resultlist = mQuizResultAnalysisResponse.getData().getResult();
                            LogWriter.write("Test name : " + mQuizResultAnalysisResponse.getData().getTestName());
                            updateData(mQuizResultAnalysisResponse);
                        }
                        break;
                    case Flinnt.FAILURE:
                        try {

                            if (msg.obj instanceof QuizResultAnalysisResponse) {
                                QuizResultAnalysisResponse mQuizResultAnalysisResponse = (QuizResultAnalysisResponse) msg.obj;
                                if (mQuizResultAnalysisResponse.errorResponse != null) {
                                    String errorMessage = mQuizResultAnalysisResponse.errorResponse.getMessage();
                                    for (int i = 0; i < mQuizResultAnalysisResponse.errorResponse.getErrorList().size(); i++) {
                                        com.edu.flinnt.protocol.Error error = mQuizResultAnalysisResponse.errorResponse.getErrorList().get(i);
                                        if (error.getCode() == ErrorCodes.ERROR_CODE_400 || error.getCode() == ErrorCodes.ERROR_CODE_401 || error.getCode() == ErrorCodes.ERROR_CODE_557) {
                                            showErrorDialog(errorMessage);
                                            return;
                                        } else {
                                            Helper.showToast(((BaseResponse) msg.obj).errorResponse.getMessage(), Toast.LENGTH_LONG);
                                            onBackPressed();
                                            return;
                                        }
                                    }

                                }else {
                                    onBackPressed();
                                }
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

        if (null == mQuizResultAnalysis) {
            mQuizResultAnalysis = new QuizResultAnalysis(mHandler, courseId, contentId, quizId);
            mQuizResultAnalysis.sendQuizResultAnalysisRequest();
            startProgressDialog();
        }
    }



    public void showErrorDialog(String errorMsg) {

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
        alertDialogBuilder.setMessage(errorMsg);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Delete Course from offline database
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(QuizResultAnalysisActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(QuizResultAnalysisActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    private void initilize() {
        mCourseNameTxt = (TextView) findViewById(R.id.course_name_txt);

        mQuizDateTxt = (TextView) findViewById(R.id.quiz_date_txt);

        mChapterRv = (RecyclerView) findViewById(R.id.chapter_rv);
        LinearLayoutManager layoutManagerChapter = new LinearLayoutManager(QuizResultAnalysisActivity.this);
        layoutManagerChapter.setOrientation(LinearLayoutManager.VERTICAL);
        mChapterRv.setLayoutManager(layoutManagerChapter);


        mSummaryRv = (RecyclerView) findViewById(R.id.summary_rv);
        LinearLayoutManager layoutManagerSummary = new LinearLayoutManager(QuizResultAnalysisActivity.this);
        layoutManagerSummary.setOrientation(LinearLayoutManager.VERTICAL);
        mSummaryRv.setLayoutManager(layoutManagerSummary);


        analysisNestedscroll = (NestedScrollView) findViewById(R.id.analysis_nestedscroll);
        analysisNestedscroll.scrollTo(0, analysisNestedscroll.getBottom());
        gradeTxt = (TextView)findViewById(R.id.grade_tv);
        totalMarksTxt = (TextView)findViewById(R.id.total_marks_tv);
        gradeTextTxt = (TextView)findViewById(R.id.grade_text_tv);
        gradesTxt = (TextView)findViewById(R.id.grades_tv);
        gradeIv = (ImageView)findViewById(R.id.grade_iv);
    }

    private void updateData(QuizResultAnalysisResponse mQuizResultAnalysisResponse) {
        mToolbar.setTitle(mQuizResultAnalysisResponse.getData().getTestName());
        mCourseNameTxt.setText(mQuizResultAnalysisResponse.getData().getCourseName());
        mQuizDateTxt.setText(getResources().getString(R.string.quiz_test_date_txt)+" "+mQuizResultAnalysisResponse.getData().getTestDate());
        gradeTxt.setText(getResources().getString(R.string.quiz_grade_txt)+" "+mQuizResultAnalysisResponse.getData().getGrade());
        totalMarksTxt.setText(getResources().getString(R.string.quiz_total_marks_txt)+" "+mQuizResultAnalysisResponse.getData().getTotalMarks());
        gradeTextTxt.setText(mQuizResultAnalysisResponse.getData().getGradeText());


        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        String quizEmojiUrl = "";
        float density = getResources().getDisplayMetrics().density;
        if (density < 1.5) {
            quizEmojiUrl = mQuizResultAnalysisResponse.getData().getGradePictureUrl() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + mQuizResultAnalysisResponse.getData().getGradePicture();
        } else if (density >= 1.5 && density < 2.0) {
            quizEmojiUrl = mQuizResultAnalysisResponse.getData().getGradePictureUrl() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + mQuizResultAnalysisResponse.getData().getGradePicture();
        } else if (density >= 2.0 && density < 3.0) {
            quizEmojiUrl = mQuizResultAnalysisResponse.getData().getGradePictureUrl() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + mQuizResultAnalysisResponse.getData().getGradePicture();
        } else if (density >= 3.0 && density < 4.0) {
            quizEmojiUrl = mQuizResultAnalysisResponse.getData().getGradePictureUrl() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + mQuizResultAnalysisResponse.getData().getGradePicture();
        } else {
            quizEmojiUrl = mQuizResultAnalysisResponse.getData().getGradePictureUrl() + Flinnt.GRADE_PICTURE_XXHDPI + File.separator + mQuizResultAnalysisResponse.getData().getGradePicture();
        }




        if(quizEmojiUrl.contains(".gif")) {
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gradeIv);
            Glide.with(this).load(quizEmojiUrl).into(imageViewTarget);
        }else {
            Glide.with(QuizResultAnalysisActivity.this)
                    .load(quizEmojiUrl)
//                .placeholder(getRandomDrawbleColor())
                    .bitmapTransform(new RoundedCornersTransformation(QuizResultAnalysisActivity.this, 0, 0))
                    .into(gradeIv);
        }

        String gradesStr = "";
        for (int i = 0; i < mQuizResultAnalysisResponse.getData().getGrades().size(); i++) {
            if(gradesStr.equalsIgnoreCase("")){
                gradesStr = mQuizResultAnalysisResponse.getData().getGrades().get(i).getGrade()+" : "+mQuizResultAnalysisResponse.getData().getGrades().get(i).getRange()+" | ";
            }else {
                gradesStr = gradesStr+""+mQuizResultAnalysisResponse.getData().getGrades().get(i).getGrade()+" : "+mQuizResultAnalysisResponse.getData().getGrades().get(i).getRange()+" | ";
            }
        }

        gradesTxt.setText(gradesStr);

        mQuizChapterAdapter = new QuizChapterAdapter(mQuizResultAnalysisResponse.getData().getResult());
        mChapterRv.setAdapter(mQuizChapterAdapter);

        mQuizSummaryAdapter = new QuizSummaryAdapter(mQuizResultAnalysisResponse.getData().getSummary());
        mSummaryRv.setAdapter(mQuizSummaryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(QuizResultAnalysisActivity.this, "activity="+Flinnt.QUIZ_RESULT_ANALYSIS+"&user="+ Config.getStringValue(Config.USER_ID)+"&course="+courseId+"&contentid="+contentId+"&quizid="+quizId);
            GoogleAnalytics.getInstance(QuizResultAnalysisActivity.this).reportActivityStart(QuizResultAnalysisActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(QuizResultAnalysisActivity.this).reportActivityStop(QuizResultAnalysisActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
}