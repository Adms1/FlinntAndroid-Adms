package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.BuyerList;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.BuyerListRequest;
import com.edu.flinnt.protocol.BuyerListResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;


/**
 * Created by flinnt-android-2 on 18/4/17.
 */

public class BuyerListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    ProgressDialog mProgressDialog = null;
    Handler mHandler;
    private String courseID = "";
    BuyerListAdapter mBuyerListAdapter;
    TextView mEmptyTxt;
    RecyclerView mRecyclerView;
    ArrayList<BuyerListResponse.User> mBuyerListItems = new ArrayList<BuyerListResponse.User>();
    BuyerList mBuyerList;
    BuyerListRequest mBuyerListRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_buyer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                courseID = bundle.getString(Course.COURSE_ID_KEY);
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
        mEmptyTxt = (TextView) findViewById(R.id.empty_txt);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof BuyerListResponse) {
                            updateBuyerList((BuyerListResponse) message.obj);
                        }


                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());

                        if (message.obj instanceof BuyerListResponse) {
                            BuyerListResponse response = (BuyerListResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(BuyerListActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }

                        BaseResponse response = ((BaseResponse) message.obj);
                        if (response.errorResponse != null) {
                            String errorMessage = response.errorResponse.getMessage();
                            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                                    showCourseDeletedDialog(errorMessage);
                                    return;
                                }
                            }
                            Helper.showAlertMessage(BuyerListActivity.this, "Error", errorMessage, "CLOSE");
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

        if (null == mBuyerList) {
            if (Helper.isConnected()) {
                mBuyerListRequest = new BuyerListRequest();
                mBuyerList = new BuyerList(mHandler, courseID);
                mBuyerList.sendBuyerListRequest(mBuyerListRequest);
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(BuyerListActivity.this);
            }
        }

        mBuyerListAdapter = new BuyerListAdapter(mBuyerListItems);
        mRecyclerView.setAdapter(mBuyerListAdapter);
        mRecyclerView.invalidate();
    }

    /**
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(BuyerListActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(BuyerListActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
     * Course delete confirmation dialog
     */
    public void showCourseDeletedDialog(String errorMessage) {

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
        alertDialogBuilder.setMessage("You have been removed from course");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Delete Course from offline database
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), courseID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);

                if (!Helper.isFinishingOrIsDestroyed(BuyerListActivity.this)) {
                    finish();
                }
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

    /**
     * Update the list of who purchase the course
     *
     * @param buyerListResponse post viewers response
     */

    private void updateBuyerList(BuyerListResponse buyerListResponse) {
        mBuyerListAdapter.addItems(buyerListResponse.getData().getUsers(), buyerListResponse.getData().getUserPictureUrl());
        if (mBuyerListAdapter.getItemCount() == 0) {
            mEmptyTxt.setVisibility(View.VISIBLE);
            mEmptyTxt.setText(getResources().getString(R.string.no_buyer_msg));
        } else {
            mEmptyTxt.setVisibility(View.GONE);
        }
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
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity="+Flinnt.COURSE_BUYERLIST+"&user="+Config.getStringValue(Config.USER_ID)+"&course="+courseID);
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
