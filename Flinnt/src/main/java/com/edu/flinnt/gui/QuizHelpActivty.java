package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.QuizStatus;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.QuizStatusResponse;
import com.edu.flinnt.util.CirclePageIndicator;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by flinnt-android-3 on 8/2/17.
 */
public class QuizHelpActivty extends AppCompatActivity {

    int[] imageResourseArray = {  // total 5 images
            R.drawable.quiz_tutorial_one,
            R.drawable.quiz_tutorial_two,
            R.drawable.quiz_tutorial_three,
            R.drawable.quiz_tutorial_four,
    };

    private static final int REQUEST_CODE = 101;

    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
    private String quizName = "";
    private int status = 0;
    private Toolbar mMainToolbar;
    private String courseName, coursePictureUrl, coursePicture;
    private int isFromNotification = Flinnt.FALSE;
    QuizHelpViewPagerAdapter mHelpViewPagerAdapter;
    ViewPager pager;
    CirclePageIndicator mIndicator;

    //	Set number of pages here
    private final int PAGE_COUNT = 4;

    Button mBtnStartQuiz;
    TextView mTxtInstruction;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.quiz_help_activity);

        //Log.d("Qhelp", "onCreate() QuizeHelpActivity.java");

        getIntentData();
        mMainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolBar();
        mBtnStartQuiz = (Button) findViewById(R.id.button_quiz_start);
        mBtnStartQuiz.setLongClickable(false);
        mBtnStartQuiz.setAllCaps(false);
        mBtnStartQuiz.setVisibility(View.INVISIBLE);
        mTxtInstruction = (TextView) findViewById(R.id.instruction_label_tv);
        mTxtInstruction.setVisibility(View.INVISIBLE);
        if (status == QuizStatusResponse.QUIZ_IS_NOT_STARTED) {
            mBtnStartQuiz.setText(getString(R.string.start_quiz));
        } else if (status == QuizStatusResponse.QUIZ_IS_FINISHED) {
            mBtnStartQuiz.setText(getString(R.string.view_result));
        } else {
            mBtnStartQuiz.setText(getString(R.string.resume_quiz));
        }

        mBtnStartQuiz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(QuizHelpActivty.this);
                    return;
                }
                if (status != QuizStatusResponse.QUIZ_IS_FINISHED) {
                    Intent quizIntent = new Intent(QuizHelpActivty.this, QuizQuestionsActivity.class);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, status);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
                    QuizHelpActivty.this.startActivityForResult(quizIntent, REQUEST_CODE);
                } else {
                    Intent quizIntent = new Intent(QuizHelpActivty.this, QuizResultActivity.class);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_STATUS, status);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
                    QuizHelpActivty.this.startActivityForResult(quizIntent, REQUEST_CODE);
                }
            }
        });

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        pager = (ViewPager) findViewById(R.id.pager_help);
        pager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        pager.setPadding(180, 0, 180, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        pager.setPageMargin(20);
        mHelpViewPagerAdapter = new QuizHelpViewPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(mHelpViewPagerAdapter);
        mIndicator.setViewPager(pager);
        getQuizStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            onBackPressed();
        }
    }

    private void getQuizStatus() {

        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }
        startProgressDialog();
        QuizStatus mQuizStatus = new QuizStatus(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        QuizStatusResponse response = (QuizStatusResponse) msg.obj;
                        mBtnStartQuiz.setVisibility(View.VISIBLE);
                        status = response.getData().getStatus();
                        if (status == QuizStatusResponse.QUIZ_IS_NOT_STARTED) {
                            mBtnStartQuiz.setText(getString(R.string.start_quiz));
                            mTxtInstruction.setVisibility(View.VISIBLE);
                        } else if (status == QuizStatusResponse.QUIZ_IS_FINISHED) {
                            mBtnStartQuiz.setText(getString(R.string.view_result));
                        } else {
                            mBtnStartQuiz.setText(getString(R.string.resume_quiz));
                        }
                        break;
                    case Flinnt.FAILURE:
                        handleError((BaseResponse) msg.obj);
                        break;
                }
                return false;
            }
        }), courseId, quizId, contentId);
        mQuizStatus.sendQuizStatusRequest();
        return;

    }

    private void handleError(BaseResponse obj) {
        if (!TextUtils.isEmpty(obj.errorResponse.getMessage()))
            Helper.showAlertMessage(this, "Error", obj.errorResponse.getMessage(), "Close");
        else
            Helper.showAlertMessage(this, "Error", getString(R.string.default_error_message_quiz_details), "Close");
    }

    private void setUpToolBar() {
        setSupportActionBar(mMainToolbar);
        mMainToolbar.setTitle("");
        mMainToolbar.setSubtitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(quizName);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        //Log.d("Qhelp", "above bundle");
        if (bundle != null) {
            //Log.d("Qhelp", "bundle is not null");
            if (bundle.containsKey(QuizQuestionsActivity.KEY_COURSE_ID))
                courseId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_COURSE_ID);
            if (bundle.containsKey(QuizQuestionsActivity.KEY_QUIZ_ID))
                quizId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_ID);
            if (bundle.containsKey(QuizQuestionsActivity.KEY_CONTENT_ID))
                contentId = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_CONTENT_ID);
            if (bundle.containsKey(QuizQuestionsActivity.KEY_QUIZ_NAME))
                quizName = getIntent().getExtras().getString(QuizQuestionsActivity.KEY_QUIZ_NAME);

            if (bundle.containsKey("isFromNotification"))
                isFromNotification = bundle.getInt("isFromNotification");


            if (isFromNotification == Flinnt.TRUE && bundle.containsKey(Config.USER_ID)) {
                String userId = bundle.getString(Config.USER_ID);

                coursePictureUrl = bundle.getString(Course.COURSE_PICTURE_URL_KEY);
                coursePicture = bundle.getString(Course.USER_PICTURE_KEY);
                courseName = bundle.getString(Course.COURSE_NAME_KEY);

                if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userId);
                }
            }
        } else {
            //Log.d("Qhelp", "bundle is null ");
        }
        //Log.d("Qhelp", "isNotificationFrom : " + isFromNotification);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(QuizHelpActivty.this, "activity=" + Flinnt.QUIZ_HELP + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseId + "&contentid=" + contentId + "&quizid=" + quizId);
            GoogleAnalytics.getInstance(QuizHelpActivty.this).reportActivityStart(QuizHelpActivty.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(QuizHelpActivty.this).reportActivityStop(QuizHelpActivty.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Log.d("Qhelp", "onResume : isFromNotification : " + isFromNotification);
    }

    public class QuizHelpViewPagerAdapter extends FragmentStatePagerAdapter {

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public QuizHelpViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            QuizHelpFragment tab = QuizHelpFragment.newInstance(imageResourseArray[0], null);
            switch (position) {
                case 0:
                    tab = QuizHelpFragment.newInstance(imageResourseArray[0], null);
                    break;
                case 1:
                    tab = QuizHelpFragment.newInstance(imageResourseArray[1], null);
                    break;
                case 2:
                    tab = QuizHelpFragment.newInstance(imageResourseArray[2], null);
                    break;
                case 3:
                    tab = QuizHelpFragment.newInstance(imageResourseArray[3], null);
                    break;

                default:
                    break;
            }
            // Help Screen Screenshot image Resource

            return tab;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();  //@Chirag:16/08/2018 commented 4:18

        //Log.d("Qhelp", "onBackPressed - isFromNotification : " + isFromNotification);
        try {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.putExtra("course_id", courseId);
            upIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
            upIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
            upIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
            upIntent.putExtra("isFromNotification", isFromNotification);
            upIntent.putExtra("comeFrom", "QuizHelpActivity");


            //upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  //@Chirag:16/08/2018 4:48  commented
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  //@Chirag:16/08/2018 4:48 //asus

            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                //Log.d("Qhelp","shouldUpReTask : true ");
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                //Log.d("Qhelp","shouldUpReTask : false ");
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
        } catch (Exception e) {
            LogWriter.write("Exception : " + e.toString());
            //.d("Qhelp","Exception : " + e.toString());
        }

    }
}