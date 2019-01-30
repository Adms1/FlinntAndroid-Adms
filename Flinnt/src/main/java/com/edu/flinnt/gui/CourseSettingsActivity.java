package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CourseSetting;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseSettingRequest;
import com.edu.flinnt.protocol.CourseSettingResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

@SuppressLint("NewApi")
/**
 * GUI class to show course settings
 */
public class CourseSettingsActivity extends AppCompatActivity implements OnClickListener {

	ArrayList<CourseSetting> settingsList;
	CourseSetting mCourseSetting;

	TextView test;
	ProgressDialog mProgressDialog = null;

	CheckBox switchCommentOnPost;
	CheckBox switchDisplayCommentWithoutApproval;
	CheckBox switchDisplayTeacherName;
	CheckBox switchAddCourseInCommunity;
	CheckBox switchAllowRePost;
	CheckBox switchAllowTeacherMessage;

	Handler mHandler;

	EditText editTextMaximumSubscriptionDays;
	EditText editTextPostTeacherCanDelete;

	CourseSettingRequest mCourseSettingRequest = new CourseSettingRequest();
	CourseSettingResponse mCourseSettingResponse;

	String initialSetMax;

	private String mCourseID = "";

	private boolean isBackPressed = false;

	private RelativeLayout layoutCanComment ,layoutShowComment ,layoutShowTeacherName ,layoutCanAdd_message, layoutCanTeacherMessage ,layoutCanRepost;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.course_settings_activity);

		Bundle bundle = getIntent().getExtras();
		if( null != bundle ) {
			mCourseID = bundle.getString(Course.COURSE_ID_KEY);
		}

		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		getSupportActionBar().setTitle("Course Settings");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		test = (TextView) findViewById(R.id.textView1);

		widgetObjects();

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				stopProgressDialog();
				switch (message.what) {
				case Flinnt.SUCCESS:
					//Helper.showToast("Success");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					mCourseSettingResponse = (CourseSettingResponse) message.obj;
					if(mCourseSetting.getRequestType() == CourseSetting.SET_SETTING) {
						Helper.showToast("Settings updated succesfully", Toast.LENGTH_SHORT);
						if(isBackPressed) finish();
					}
					if(null != mCourseSettingResponse) 		setSettingRequest(mCourseSettingResponse);
					break;
				case Flinnt.FAILURE:
					//Helper.showToast("Failure");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					/*if( message.obj instanceof CourseSettingResponse ) {
						CourseSettingResponse response = (CourseSettingResponse) message.obj;
						if(response.errorResponse != null){
						   Helper.showAlertMessage(CourseSettingsActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
						}
					}*/
				if(mCourseSetting.getRequestType() == CourseSetting.SET_SETTING) {
						showAlertMessage(CourseSettingsActivity.this, "Course Settings", "Failed to update Settings", "Close");
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

		getSettingRequest();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "activity="+Flinnt.COURSE_SETTINGS+"&user="+Config.getStringValue(Config.USER_ID)+"&course="+mCourseID);
			GoogleAnalytics.getInstance(this).reportActivityStart(this);
		}catch (Exception e){
			LogWriter.err(e);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			GoogleAnalytics.getInstance(this).reportActivityStop(this);
		}catch (Exception e){
			LogWriter.err(e);
		}
	}

    /**
     * Make request to get settings from web-server
     */
	private void getSettingRequest() {
		mCourseSettingRequest.setUserId(Config.getStringValue(Config.USER_ID));
		mCourseSettingRequest.setCourseID(mCourseID);
		mCourseSetting = new CourseSetting(mHandler);
		mCourseSetting.setRequestType(CourseSetting.GET_SETTING);
		mCourseSetting.setCourseSettingRequest(mCourseSettingRequest);
		mCourseSetting.sendCourseSettingRequest();
		startProgressDialog();
	}

    /**
     * set up views and link with resource ids
     */
	private void widgetObjects() {
		switchCommentOnPost = (CheckBox) findViewById(R.id.switch_can_comment);
		switchCommentOnPost.setOnClickListener(this);
		layoutCanComment = (RelativeLayout) findViewById(R.id.layout_can_comment);
		layoutCanComment.setOnClickListener(this);

		switchDisplayCommentWithoutApproval = (CheckBox) findViewById(R.id.switch_show_comment);
		switchDisplayCommentWithoutApproval.setOnClickListener(this);
		layoutShowComment  = (RelativeLayout) findViewById(R.id.layout_show_comment);
		layoutShowComment.setOnClickListener(this);

		switchDisplayTeacherName= (CheckBox) findViewById(R.id.switch_show_teacher_name);
		switchDisplayTeacherName.setOnClickListener(this);
		layoutShowTeacherName  = (RelativeLayout) findViewById(R.id.layout_show_teacher_name);
		layoutShowTeacherName.setOnClickListener(this);

		editTextMaximumSubscriptionDays = (EditText) findViewById(R.id.edittext_subscription);

		switchAddCourseInCommunity = (CheckBox) findViewById(R.id.switch_can_add_message);
		switchAddCourseInCommunity.setOnClickListener(this);
		layoutCanAdd_message = (RelativeLayout) findViewById(R.id.layout_can_add_message);
		layoutCanAdd_message.setOnClickListener(this);

		switchAllowTeacherMessage = (CheckBox) findViewById(R.id.switch_can_teacher_message);
		switchAllowTeacherMessage.setOnClickListener(this);
		layoutCanTeacherMessage = (RelativeLayout) findViewById(R.id.layout_can_teacher_message);
		layoutCanTeacherMessage.setOnClickListener(this);

		switchAllowRePost = (CheckBox) findViewById(R.id.switch_can_repost);
		switchAllowRePost.setOnClickListener(this);
		layoutCanRepost= (RelativeLayout) findViewById(R.id.layout_can_repost);
		layoutCanRepost.setOnClickListener(this);

		editTextPostTeacherCanDelete = (EditText) findViewById(R.id.edittext_max_post_delete);
	}

    /**
     * Set setting on UI
     * @param setting settings response
     */
	private void setSettingRequest(CourseSettingResponse setting) {
		switchCommentOnPost.setChecked(setting.getCanComment() == Flinnt.TRUE ? true : false);
		switchDisplayCommentWithoutApproval.setChecked(setting.getShowComment() == Flinnt.TRUE ? true : false);
		switchDisplayTeacherName.setChecked(setting.getShowTeacherName() == Flinnt.TRUE ? true : false);
		switchAddCourseInCommunity.setChecked(setting.getCanAddMessage() == Flinnt.TRUE ? true : false);
		switchAllowRePost.setChecked(setting.getCanRepost() == Flinnt.TRUE ? true : false);
		switchAllowTeacherMessage.setChecked(setting.getCanTeacherMessage() == Flinnt.TRUE ? true : false);
		editTextPostTeacherCanDelete.setText(String.valueOf(setting.getMaxPostDelete())); 
		initialSetMax = String.valueOf(setting.getMaxPostDelete());
		//		editTextMaximumSubscriptionDays.setText();
		//startProgressDialog();

		layoutCanComment.setVisibility(setting.getV_can_comment() == Flinnt.TRUE   ? View.VISIBLE : View.GONE);
		layoutShowComment.setVisibility(setting.getV_show_comment_wt_approval()== Flinnt.TRUE   ? View.VISIBLE : View.GONE);
		layoutShowTeacherName.setVisibility(setting.getV_show_teacher_name_post()== Flinnt.TRUE   ? View.VISIBLE : View.GONE);
		layoutCanAdd_message.setVisibility(setting.getV_can_add_message()== Flinnt.TRUE   ? View.VISIBLE : View.GONE);
		layoutCanTeacherMessage.setVisibility(setting.getV_teacher_to_teacher_message()== Flinnt.TRUE   ? View.VISIBLE : View.GONE);
		layoutCanRepost.setVisibility(setting.getV_can_repost()== Flinnt.TRUE   ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onBackPressed() {
		if(editTextPostTeacherCanDelete.getText().toString().trim().equalsIgnoreCase(initialSetMax)
				|| TextUtils.isEmpty(editTextPostTeacherCanDelete.getText().toString().trim())) {
			finish();
		}
		else { 	
			mCourseSettingRequest.resetAllValues();
			mCourseSettingRequest.setUserId(Config.getStringValue(Config.USER_ID));
			mCourseSettingRequest.setCourseID(mCourseID);
			mCourseSetting.setRequestType(CourseSetting.SET_SETTING);
			mCourseSettingRequest.setMaxPostDelete(editTextPostTeacherCanDelete.getText().toString());
			mCourseSetting.setCourseSettingRequest(mCourseSettingRequest);
			mCourseSetting.sendCourseSettingRequest();
			isBackPressed = true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
            onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if ( !Helper.isConnected() ) {
			Helper.showNetworkAlertMessage(CourseSettingsActivity.this);
		}
		else {

		mCourseSettingRequest.resetAllValues();
		mCourseSettingRequest.setUserId(Config.getStringValue(Config.USER_ID));
		mCourseSettingRequest.setCourseID(mCourseID);
		mCourseSetting.setRequestType(CourseSetting.SET_SETTING);

		switch (v.getId()) {
		case R.id.layout_can_comment:
			mCourseSettingRequest.setCanComment(!switchCommentOnPost.isChecked() ? "1" : "0");
			switchCommentOnPost.setChecked(!switchCommentOnPost.isChecked());
			break;
		case R.id.switch_can_comment:
			mCourseSettingRequest.setCanComment(switchCommentOnPost.isChecked() ? "1" : "0");
			switchCommentOnPost.setChecked(!switchCommentOnPost.isChecked());
			break;

		case R.id.layout_show_comment:
			mCourseSettingRequest.setShowComment(!switchDisplayCommentWithoutApproval.isChecked() ? "1" : "0");
			switchDisplayCommentWithoutApproval.setChecked(!switchDisplayCommentWithoutApproval.isChecked());
			break;
		case R.id.switch_show_comment:
			mCourseSettingRequest.setShowComment(switchDisplayCommentWithoutApproval.isChecked() ? "1" : "0");
			switchDisplayCommentWithoutApproval.setChecked(!switchDisplayCommentWithoutApproval.isChecked());
			break;

		case R.id.layout_show_teacher_name:
			mCourseSettingRequest.setShowTeacherName(!switchDisplayTeacherName.isChecked() ? "1" : "0");
			switchDisplayTeacherName.setChecked(!switchDisplayTeacherName.isChecked());
			break;
		case R.id.switch_show_teacher_name:
			mCourseSettingRequest.setShowTeacherName(switchDisplayTeacherName.isChecked() ? "1" : "0");
			switchDisplayTeacherName.setChecked(!switchDisplayTeacherName.isChecked());
			break;

		case R.id.layout_can_add_message:
			mCourseSettingRequest.setCanAddMessage(!switchAddCourseInCommunity.isChecked() ? "1" : "0");
			switchAddCourseInCommunity.setChecked(!switchAddCourseInCommunity.isChecked());
			break;
		case R.id.switch_can_add_message:
			mCourseSettingRequest.setCanAddMessage(switchAddCourseInCommunity.isChecked() ? "1" : "0");
			switchAddCourseInCommunity.setChecked(!switchAddCourseInCommunity.isChecked());
			break;

		case R.id.layout_can_teacher_message:
			mCourseSettingRequest.setCanTeacherMessage(!switchAllowTeacherMessage.isChecked() ? "1" : "0");
			switchAllowTeacherMessage.setChecked(!switchAllowTeacherMessage.isChecked());
			break;
		case R.id.switch_can_teacher_message:
			mCourseSettingRequest.setCanTeacherMessage(switchAllowTeacherMessage.isChecked() ? "1" : "0");
			switchAllowTeacherMessage.setChecked(!switchAllowTeacherMessage.isChecked());
			break;

		case R.id.layout_can_repost:
			mCourseSettingRequest.setCanRepost(!switchAllowRePost.isChecked() ? "1" : "0");
			switchAllowRePost.setChecked(!switchAllowRePost.isChecked());
			break;
		case R.id.switch_can_repost:
			mCourseSettingRequest.setCanRepost(switchAllowRePost.isChecked() ? "1" : "0");
			switchAllowRePost.setChecked(!switchAllowRePost.isChecked());
			break;

		default:
			break;
		}
		mCourseSetting.setCourseSettingRequest(mCourseSettingRequest);
		mCourseSetting.sendCourseSettingRequest();
		startProgressDialog();
		}
	}
	public void showAlertMessage( final Context context, String title, String message, String dialogBtnText ) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set resorceTitleTxt
		//alertDialogBuilder.setTitle(resorceTitleTxt);
		//alertDialogBuilder.setTitle("Change Password");
		TextView titleText = new TextView(context);
		// You Can Customize your Title here 
		titleText.setText(title);
		titleText.setPadding(40, 40, 40, 0);
		titleText.setGravity(Gravity.CENTER_VERTICAL);
		titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
		titleText.setTextSize(20);
		titleText.setTypeface(Typeface.DEFAULT_BOLD);
		alertDialogBuilder.setCustomTitle(titleText);

		// set dialog message
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(dialogBtnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		 });
		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if(isBackPressed) finish();
                    alertDialog.dismiss();
                }
            });
        }

	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
        }
	}

    /**
     * Stops the circular progress dialog
     */
	private void stopProgressDialog(){
		try {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}
		catch (Exception e) {
			LogWriter.err(e);
		}
		finally {
			mProgressDialog = null;
		}
	}
}