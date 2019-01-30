package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * GUI class to show email and mobile verification status
 */
public class ProfileVerifyMobileAndEmailActivity extends AppCompatActivity implements OnClickListener {

	Button verifyBtn;
	EditText varificationCodeEdtTxt;
	TextView sentVerificationCodeTxt, smsNotReceivedTxt;
	TextView sentVerificationLinkTxt, emailNotReceivedTxt, termCondition;
	LinearLayout emailVerifyLayout, mobileVerifyLayout;
	Toolbar toolbar;
	ProgressDialog mProgressDialog = null;
	public Handler mHandler = null;
	String varificationCode;
	private int mReqType = ProfileVerify.PROFILE_MOBILE_VERIFY;

	public static String EMAIL_ID = "email_id";
	public static String EMAIL_VERIFICATION_ID = "email_verification_id";
	public static String MOBILE_NUM = "mobile_num";
	public static String MOBILE_VERIFICATION_ID = "mobile_verification_id";

	private String emailID, mobileNum;
	private int emailVerificationID = Flinnt.INVALID, mobileVerificationID = Flinnt.INVALID;
	private int isEmailVerified = Flinnt.INVALID, isMobileVerified = Flinnt.INVALID;
	SMSReceiver1 smsReceiver1;

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

		setContentView(R.layout.profile_verify_mobile_and_email);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Verify Mobile & Email");
		setSupportActionBar(toolbar);

		emailVerifyLayout = (LinearLayout) findViewById(R.id.profile_emailverify_layout);
		mobileVerifyLayout = (LinearLayout) findViewById(R.id.profile_mobileverify_layout);

		verifyBtn = (Button) findViewById(R.id.verify_button_verifymobile);
		varificationCodeEdtTxt = (EditText) findViewById(R.id.varification_code_verifymobile);
		sentVerificationCodeTxt = (TextView) findViewById(R.id.sent_verification_code_txt_verifymobile);
		smsNotReceivedTxt = (TextView) findViewById(R.id.sms_not_received_txt_verifymobile);
		sentVerificationLinkTxt = (TextView) findViewById(R.id.sent_verification_link_txt_verifyemail);
		emailNotReceivedTxt = (TextView) findViewById(R.id.email_not_received_txt_verifyemail);

		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			emailID = bundle.getString(EMAIL_ID);
			mobileNum = bundle.getString(MOBILE_NUM);
			emailVerificationID = bundle.getInt(EMAIL_VERIFICATION_ID);
			mobileVerificationID = bundle.getInt(MOBILE_VERIFICATION_ID);
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("emailID : " + emailID + " , mobileNum : " + mobileNum
					+ " , emailVerificationID : " + emailVerificationID + " , mobileVerificationID : " + mobileVerificationID);
		}

		verifyBtn.setOnClickListener(this);
		verifyBtn.setLongClickable(false);
		smsNotReceivedTxt.setOnClickListener(this);
		emailNotReceivedTxt.setOnClickListener(this);

		sentVerificationLinkTxt.setText(Html.fromHtml( getResources().getString(R.string.sent_verification_link_on_email)
				.replace("xxxxxxxxxx", emailID) ));

		sentVerificationCodeTxt.setText(Html.fromHtml( getResources().getString(R.string.sent_verification_code_on_mobile)
				.replace("xxxxxxxxxx", mobileNum) ));

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				switch (message.what) {
				case Flinnt.SUCCESS:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if(mReqType == ProfileVerify.PROFILE_MOBILE_VERIFY){
						if( ((ProfileVerifyResponse)message.obj).getIsVerified() == Flinnt.TRUE ){
							Helper.showToast("Mobile no. verified", Toast.LENGTH_LONG);
							isMobileVerified = Flinnt.TRUE;
							if(isEmailVerified == Flinnt.TRUE){	setMainIntent();}
							else{ setVisibility();}
						}
					}else if(mReqType == ProfileVerify.PROFILE_MOBILE_RESEND_CODE){
						if( ((ProfileVerifyResponse)message.obj).getIsVerified() == Flinnt.TRUE ){
							Helper.showToast("Mobile no. already verified", Toast.LENGTH_LONG);
							isMobileVerified = Flinnt.TRUE;
							if(isEmailVerified == Flinnt.TRUE){	setMainIntent();}
							else{ setVisibility();}
						}
						else if( ((ProfileVerifyResponse)message.obj).getIsExpired() == Flinnt.TRUE ){
							Helper.showToast("Verification code is expired", Toast.LENGTH_LONG);
							isMobileVerified = Flinnt.TRUE;
							if(isEmailVerified == Flinnt.TRUE){	setMainIntent();}
							else{ setVisibility();}

						}else if( ((ProfileVerifyResponse)message.obj).getIsSent() == Flinnt.TRUE ){
							Helper.showToast("Verification code sent", Toast.LENGTH_LONG);
						}

					}else if(mReqType == ProfileVerify.PROFILE_EMAIL_RESEND_LINK){
						if( ((ProfileVerifyResponse)message.obj).getIsVerified() == Flinnt.TRUE ){
							Helper.showToast("Email ID already verified", Toast.LENGTH_LONG);
							isEmailVerified = Flinnt.TRUE;
							if(isMobileVerified == Flinnt.TRUE){ setMainIntent();}
							else{ setVisibility();}
						}
						else if( ((ProfileVerifyResponse)message.obj).getIsExpired() == Flinnt.TRUE ){
							Helper.showToast("Verification link is expired", Toast.LENGTH_LONG);
							isEmailVerified = Flinnt.TRUE;
							if(isMobileVerified == Flinnt.TRUE){ setMainIntent();}
							else{ setVisibility();}

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
							Helper.showAlertMessage(ProfileVerifyMobileAndEmailActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
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
			MyCommFun.sendTracker(this, "Profile Verify - Email & Mobile");
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerSMSReceiver();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unRegisterSMSReceiver();
	}


    /**
     * Set visibility of mobile and email view
     */
	protected void setVisibility() {
		if(isMobileVerified == Flinnt.TRUE){
			mobileVerifyLayout.setVisibility(View.GONE);
		}else if(isEmailVerified == Flinnt.TRUE){
			emailVerifyLayout.setVisibility(View.GONE);
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
		else if( view.getId() == R.id.verify_button_verifymobile ){
			if(Helper.isConnected()){
				String verificationCode = varificationCodeEdtTxt.getText().toString();

				if(TextUtils.isEmpty(verificationCode) ){
					Helper.showAlertMessage(ProfileVerifyMobileAndEmailActivity.this, "Verify", "Enter verification code", "CLOSE");
				}
				else if(verificationCode.length() < 5 ){
					Helper.showAlertMessage(ProfileVerifyMobileAndEmailActivity.this, "Verify", "Add valid code", "CLOSE");
				}else{

					mReqType = ProfileVerify.PROFILE_MOBILE_VERIFY;

					ProfileVerifyRequest profileVerifyRequest = new ProfileVerifyRequest();
					profileVerifyRequest.setUserID(Config.getStringValue(Config.USER_ID));
					profileVerifyRequest.setVerificationCode(verificationCode);

					new ProfileVerify(mHandler, profileVerifyRequest, mReqType).sendProfileVerifyRequest();

					startProgressDialog(); 
				}

			}else{
				Helper.showNetworkAlertMessage(this);
			}
		}

		else if( view.getId() == R.id.sms_not_received_txt_verifymobile ){
			if(Helper.isConnected()){
				mReqType = ProfileVerify.PROFILE_MOBILE_RESEND_CODE;

				ProfileVerifyRequest profileVerifyRequest = new ProfileVerifyRequest();
				profileVerifyRequest.setUserID(Config.getStringValue(Config.USER_ID));
				profileVerifyRequest.setVerificationID(mobileVerificationID);

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
        if (!Helper.isFinishingOrIsDestroyed(ProfileVerifyMobileAndEmailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ProfileVerifyMobileAndEmailActivity.this, "", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
	
	class SMSReceiver1 extends BroadcastReceiver
 	{

 		@Override
 		public void onReceive(Context context, Intent intent) {
 			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("SMSReceiver :: onReceive()");
 			 Bundle bundle = intent.getExtras();

             if (bundle != null)
             {
                 Object[] pdus = (Object[]) bundle.get("pdus");
                 SmsMessage[] messages = new SmsMessage[pdus.length];
                 for (int i = 0; i < pdus.length; i++)
                 {
                     messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                 }
                 
                 for (SmsMessage message : messages)
                 {
                 	if(message == null)
                 		break;
                     String strFrom = "";
                     try{
                     strFrom = message.getOriginatingAddress();
                     }catch(Exception e)
                     {
                     	e.printStackTrace();
                     }
                     String strMsg = "";
                     try{
                     strMsg = message.getDisplayMessageBody();
                     }catch(Exception e)
                     {
                     	e.printStackTrace();
                     }
                     if(TextUtils.isEmpty(strFrom))
                     	break;
                     if(TextUtils.isEmpty(strMsg))
                     	strMsg = "";
                     if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("SMS From:" + strFrom + ", msg:" + strMsg);
                     System.out.println("contain  code : " + strMsg.contains("flinnt verification code") + " ," +
                     		" contain flinnt : " + strFrom.contains("FLINNT"));
                     if(strMsg.contains("flinnt verification code") && ( strFrom.contains("FLINNT") || strFrom.contains("flinnt") ))
                     {
                    	 String varificationCode = Helper.extractDigits(strMsg, 5);
                    	 System.out.println("varificationCode : " + varificationCode);
                    	 varificationCodeEdtTxt.setText(varificationCode);
                     }
                 }
             }
 		
 		}
 	}

    /**
     * register sms receiver to get verification code
     */
	private void registerSMSReceiver(){
		try {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("registerSMSReceiver to get varification code");
			smsReceiver1 = new SMSReceiver1();
			IntentFilter filter = new IntentFilter();
		    filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		    filter.setPriority(2147483647);
		    registerReceiver(smsReceiver1, filter);
		} catch (Exception e) {
		}
		
	}

    /**
     * unregister sms receiver
     */
	private void unRegisterSMSReceiver(){
		try {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("unRegisterSMSReceiver...");
			unregisterReceiver(smsReceiver1);
		} catch (Exception e) {
		}
		
	}

}
