package com.edu.flinnt.gui;

import android.app.ProgressDialog;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ForgotPassword;
import com.edu.flinnt.gui.SignUpActivity.validateErrorCodes;
import com.edu.flinnt.protocol.ForgotPasswordResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to show forgot password help
 */
public class ForgotPasswordActivity extends AppCompatActivity implements OnClickListener {

	private Button submitBtn;
	private EditText emailMobileEdtTxt;
	private Toolbar toolbar;
	private Handler mHandler;
	private ProgressDialog mProgressDialog = null;
	private String emailMobile;
	private boolean isEmail = false;
	
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}

		setContentView(R.layout.forgot_password);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		submitBtn = (Button) findViewById(R.id.submit_button_forgotpassword);
		emailMobileEdtTxt = (EditText) findViewById(R.id.email_mobile_forgotpassword);
		emailMobileEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					forgotPasswordCall();
				}
				return false;
			}
		});
		submitBtn.setOnClickListener(this);
		submitBtn.setLongClickable(false);
		
		mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                    	//Helper.showToast("Success");
                    	stopProgressDialog();
                    	String successMessage = "Reset password link has been sent to ";
                    	if(isEmail){
                    		successMessage = successMessage + "email id '" + Config.getStringValue(Config.USER_LOGIN) + "'";
                    	}else{
                    		successMessage = successMessage + "mobile number '" + Config.getStringValue(Config.USER_LOGIN) + "'";
                    	}
                    	showAlertMessage("Forgot Password", successMessage, "Close");
                    	if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                    	//finish();
                    break;
                    case Flinnt.FAILURE:
                    	//Helper.showToast("Failure");
                    	stopProgressDialog();
                    	ForgotPasswordResponse response = (ForgotPasswordResponse) message.obj;
                    	if(response.errorResponse != null){
                    		Helper.showAlertMessage(ForgotPasswordActivity.this, "Forgot Password", response.errorResponse.getMessage(), "Close");
                    	}
                    	if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
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

	private void forgotPasswordCall(){
		emailMobile = emailMobileEdtTxt.getText().toString().trim();
		if( TextUtils.isEmpty(emailMobile) ){
			Helper.showAlertMessage(ForgotPasswordActivity.this, "Forgot Password", "Add email or mobile number", "Close");
		}
		else if( isValidEmailorNumber() != validateErrorCodes.VALID_FIELD) {
			Helper.showAlertMessage(ForgotPasswordActivity.this, "Forgot Password", getResources().getString(R.string.validate_email_mobile), "Close");
		}
		else{
			if(Helper.isConnected()){
				Config.setStringValue(Config.USER_LOGIN, emailMobile );
				new ForgotPassword(mHandler).sendForgotPasswordRequest();
				startProgressDialog();
			}else{
				Helper.showNetworkAlertMessage(this);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Forgot Password");
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
	public void onClick(View view) {

		if( view.getId() == R.id.submit_button_forgotpassword ){
			forgotPasswordCall();
		}
	}

    /**
     * Check if the entered email or number is valid or not
     * @return true if valid, false otherwise
     */
	private int isValidEmailorNumber(){
		int ret = validateErrorCodes.NOT_VALIDATE_EMAIL_MOBILE;
		if( emailMobile.matches(SignUpActivity.REGEX_NUMBER) ){
			if(emailMobile.length() == 10){
				isEmail = false;
				return validateErrorCodes.VALID_FIELD;
			}else{
				return validateErrorCodes.NOT_VALID_NUMBER;
			}
		}/*else if(emailMobile.matches(SignUpActivity.REGEX_EMAIL_ID)){
			isEmail = true;
			return validateErrorCodes.VALID_FIELD;
		}*/
		else if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailMobile).matches()){
			isEmail = true;
			return validateErrorCodes.VALID_FIELD;
		}
		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish(); //onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(ForgotPasswordActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ForgotPasswordActivity.this, "", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    /**
     * Display a dialog
     * @param title dialog resorceTitleTxt
     * @param message dialog message which appears in body
     * @param dialogBtnText dialog option button text
     */
	private void showAlertMessage( String title, String message, String dialogBtnText ) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set resorceTitleTxt
		TextView titleText = new TextView(this);
		// You Can Customise your Title here 
		titleText.setText(title);
		titleText.setPadding(40, 40, 40, 0);
		titleText.setGravity(Gravity.CENTER_VERTICAL);
		titleText.setTextColor(this.getResources().getColor(R.color.ColorPrimary));
		titleText.setTextSize(20);
		titleText.setTypeface(Typeface.DEFAULT_BOLD);
		alertDialogBuilder.setCustomTitle(titleText);

		// set dialog message
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(dialogBtnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				finish();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.ColorPrimary));
        }
	}
}
