package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ProfileVerify;
import com.edu.flinnt.protocol.ProfileVerifyRequest;
import com.edu.flinnt.protocol.ProfileVerifyResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressLint("NewApi")
/**
 * GUI class to show verification status of email
 */
public class ProfileVerifyEmailActivity extends AppCompatActivity implements OnClickListener {

	TextView sentVerificationLinkTxt, emailNotReceivedTxt, termCondition;
	Toolbar toolbar;
	ProgressDialog mProgressDialog = null;
	public Handler mHandler = null;
	private int mReqType = ProfileVerify.PROFILE_MOBILE_VERIFY;
	private String emailID;
	private int emailVerificationID = Flinnt.INVALID;
	private int isEmailVerified = Flinnt.INVALID;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}

		setContentView(R.layout.profile_verify_email);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Verify Email");
		setSupportActionBar(toolbar);

		sentVerificationLinkTxt = (TextView) findViewById(R.id.sent_verification_link_txt_verifyemail);
		emailNotReceivedTxt = (TextView) findViewById(R.id.email_not_received_txt_verifyemail);

		emailNotReceivedTxt.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			emailID = bundle.getString(ProfileVerifyMobileAndEmailActivity.EMAIL_ID);
			emailVerificationID = bundle.getInt(ProfileVerifyMobileAndEmailActivity.EMAIL_VERIFICATION_ID);
		}

		sentVerificationLinkTxt.setText(Html.fromHtml( getResources().getString(R.string.sent_verification_link_on_email)
				.replace("xxxxxxxxxx", (null != emailID ? emailID : "xxxxxxxxxx")) ));

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				switch (message.what) {
				case Flinnt.SUCCESS:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if(mReqType == ProfileVerify.PROFILE_EMAIL_RESEND_LINK){
						if( ((ProfileVerifyResponse)message.obj).getIsVerified() == Flinnt.TRUE ){
							Helper.showToast("Email ID already verified", Toast.LENGTH_LONG);
							isEmailVerified = Flinnt.TRUE;
							setMainIntent();
						}
						else if( ((ProfileVerifyResponse)message.obj).getIsExpired() == Flinnt.TRUE ){
							Helper.showToast("Verification link is expired", Toast.LENGTH_LONG);
							isEmailVerified = Flinnt.TRUE;
							setMainIntent();

						}else if( ((ProfileVerifyResponse)message.obj).getIsSent() == Flinnt.TRUE ){
							Helper.showToast("Verification link sent", Toast.LENGTH_LONG);
						}
					}

					break;
				case Flinnt.FAILURE:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if( message.obj instanceof ProfileVerifyResponse) {
						ProfileVerifyResponse response = (ProfileVerifyResponse) message.obj;
						if(response.errorResponse != null){
						   Helper.showAlertMessage(ProfileVerifyEmailActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
						}
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Profile Verify - Email");
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
     *
     */
	private void setMainIntent() {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	@Override
	public void onClick(View view) {

		if( view.getId() == R.id.email_not_received_txt_verifyemail ){
			if(Helper.isConnected()){
				mReqType = ProfileVerify.PROFILE_EMAIL_RESEND_LINK;

				ProfileVerifyRequest profileVerifyRequest = new ProfileVerifyRequest();
				profileVerifyRequest.setUserID(Config.getStringValue(Config.USER_ID));
				profileVerifyRequest.setVerificationID(emailVerificationID);

				new ProfileVerify(mHandler, profileVerifyRequest, mReqType).sendProfileVerifyRequest();

				startProgressDialog();
			}else{
				Helper.showNetworkAlertMessage(this);
			}
		}
	}

    /**
     *  Starts a circular progress dialog
     */
    private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(ProfileVerifyEmailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ProfileVerifyEmailActivity.this, "", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.close, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
            finish(); //onBackPressed();
            break;
		case R.id.action_close:
			setMainIntent();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
