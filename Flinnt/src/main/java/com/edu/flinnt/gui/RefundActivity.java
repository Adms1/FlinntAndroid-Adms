package com.edu.flinnt.gui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Refund;
import com.edu.flinnt.core.RefundReasonOption;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.RefundReasonOptionRequest;
import com.edu.flinnt.protocol.RefundReasonOptionResponse;
import com.edu.flinnt.protocol.RefundRequest;
import com.edu.flinnt.protocol.RefundResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;


/**
 * Created by flinnt-android-2 on 22/5/17.
 * Refund Activity call from course details activity when user click on Refund button.
 */

public class RefundActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String mCourseID = "";
    RadioGroup refundRadGrp;
    Button submitBtn;
    TextView title1Txt, title2Txt;
    public Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    String courseName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_refund);
        getData();
        setUpToolBar();
        initUI();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof RefundReasonOptionResponse) {
                            updateUI((RefundReasonOptionResponse) message.obj);
                        } else if (message.obj instanceof RefundResponse) {
                            showConfirmationDialog();
                        }

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof RefundReasonOptionResponse) {
                            RefundReasonOptionResponse response = (RefundReasonOptionResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(RefundActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }
                        if (message.obj instanceof RefundResponse) {
                            RefundResponse response = (RefundResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(RefundActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }

                        break;
                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        sendOptionRequest();
    }


    private void setUpToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.refund_course));

    }

    public void getData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            }
        }
    }

    private void sendOptionRequest() {
        startProgressDialog();
        RefundReasonOptionRequest mRefundReasonOptionRequest = new RefundReasonOptionRequest();
        mRefundReasonOptionRequest.setUserID(Config.getStringValue(Config.USER_ID));
        mRefundReasonOptionRequest.setCourseID(mCourseID);
        new RefundReasonOption(mHandler, mRefundReasonOptionRequest).sendRefundReasonOptionRequest();
    }


    private void initUI() {
        refundRadGrp = (RadioGroup) findViewById(R.id.refund_radiogrp);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        title1Txt = (TextView) findViewById(R.id.title1_txt);
        title2Txt = (TextView) findViewById(R.id.title2_txt);
    }

    private void updateUI(RefundReasonOptionResponse obj) {
        courseName = obj.getData().getCourseName();
        title1Txt.setText(Html.fromHtml(getString(R.string.sorry_You_are) + " <b>" + obj.getData().getCourseName() + "</b>" + getString(R.string.like_a_refund)));
        title2Txt.setText(R.string.help_us_improve);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        for (int i = 0; i < obj.getData().getRefundReasons().size(); i++) {
            final RadioButton radioButton = new RadioButton(this);
            refundRadGrp.addView(radioButton); //the RadioButtons are added to the radioGroup instead of the layout

            radioButton.setLayoutParams(params);
            radioButton.setId(Integer.parseInt(obj.getData().getRefundReasons().get(i).getId()));
            radioButton.setText(obj.getData().getRefundReasons().get(i).getText());
            radioButton.setTextColor(getResources().getColor(R.color.gray));
            radioButton.setTextSize(16);
            radioButton.setButtonDrawable(getResources().getDrawable(R.drawable.radio_button));

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                radioButton.setPadding(20, 20, 0, 20);
            }
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refundRadGrp.getCheckedRadioButtonId() != -1) {
                    int selectedId = refundRadGrp.getCheckedRadioButtonId();
                    startProgressDialog();
                    RefundRequest mRefundRequest = new RefundRequest();
                    mRefundRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mRefundRequest.setCourseID(mCourseID);
                    mRefundRequest.setReason(String.valueOf(selectedId));
                    new Refund(mHandler, mRefundRequest).sendRefundRequest();
                }else{
                    Helper.showToast(getString(R.string.select_reason), Toast.LENGTH_LONG);
                }
            }
        });
    }


    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(Html.fromHtml(getString(R.string.refund_dialog_message_title) + " <b>" + courseName + "</b>" + "<br/><br/>" + getString(R.string.refund_dialog_message_one) + "<br/><br/>" + getString(R.string.refund_dialog_message_two) + "<br/><br/>" + getString(R.string.refund_dialog_message_three)));
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
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
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.REFUND_COURSE + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID);
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
}