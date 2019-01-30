package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
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
import com.edu.flinnt.core.ResendorVerified;
import com.edu.flinnt.core.VerifyMobile;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.ErrorResponse;
import com.edu.flinnt.protocol.ResendorVerifiedResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.protocol.VerifyMobileResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to show Mobile verification details
 */
public class VerifyMobileActivity extends AppCompatActivity implements OnClickListener {

    Button verifyBtn, alreadyVerifiedBtn, logoutBtn;
    EditText varificationCodeEdtTxt;
    TextView sentVerificationCodeTxt, smsNotReceivedTxt, termCondition;
    Toolbar toolbar;
    public Handler mHandler = null;
    String varificationCode;
    private ProgressDialog mProgressDialog = null;
    private int mClassType = ResendorVerified.ALREADY_VERIFIED;

    SMSReceiver1 smsReceiver1;
    boolean isSwapOperation = false;
    User mUser;
    String mobNumber = Config.getStringValue(Config.USER_LOGIN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.verify_mobile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyBtn = (Button) findViewById(R.id.verify_button_verifymobile);
        alreadyVerifiedBtn = (Button) findViewById(R.id.already_verified_button_verifymobile);
        logoutBtn = (Button) findViewById(R.id.logout_button_verifymobile);
        varificationCodeEdtTxt = (EditText) findViewById(R.id.varification_code_verifymobile);
        varificationCodeEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    verifyMobileCall();
                }
                return false;
            }
        });
        sentVerificationCodeTxt = (TextView) findViewById(R.id.sent_verification_code_txt_verifymobile);
        smsNotReceivedTxt = (TextView) findViewById(R.id.sms_not_received_txt_verifymobile);

        termCondition = (TextView) findViewById(R.id.terms_conditions_verifymobile);
        termCondition.setOnClickListener(this);

        verifyBtn.setOnClickListener(this);
        verifyBtn.setLongClickable(false);
        alreadyVerifiedBtn.setOnClickListener(this);
        alreadyVerifiedBtn.setLongClickable(false);
        logoutBtn.setOnClickListener(this);
        logoutBtn.setLongClickable(false);
        smsNotReceivedTxt.setOnClickListener(this);

        mUser = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID));

        isSwapOperation = !TextUtils.isEmpty(Config.getStringValue(Config.USER_ID));

        Bundle b = getIntent().getExtras();
        if (null != b) {
            String userId = b.getString(Config.USER_ID, "");
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("userId :: " + userId);

            if (!TextUtils.isEmpty(userId)) {
                mUser = UserInterface.getInstance().getUserFromId(userId);
                if(mUser != null){
                    mobNumber = mUser.getUserLogin();
                }
            }
            if (b.containsKey("TYPE") && b.getString("TYPE").equals(MyCoursesActivity.SWAP_ACCOUNT_OPERATION)) {
                isSwapOperation = true;
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(isSwapOperation);

        sentVerificationCodeTxt.setText(Html.fromHtml(getResources().getString(R.string.sent_verification_code_on_mobile)
                .replace("xxxxxxxxxx", mobNumber)));

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();

                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof VerifyMobileResponse) {
                            VerifyMobileResponse mVerifyMobileResponse = (VerifyMobileResponse) message.obj;
                            if (mVerifyMobileResponse.getAccVerified().equals(Flinnt.ENABLED)) {
                                mUser.setAccVerified(Flinnt.ENABLED);
                                mUser.setAccAuthMode(mVerifyMobileResponse.getAccAuthMode());
                                UserInterface.getInstance().insertOrUpdateUser(mUser.getUserID(), mUser);
                                Helper.setCurrentUserConfig(mUser.getUserID());
                            }
                        }
                        if (message.obj instanceof ResendorVerifiedResponse) {
                            ResendorVerifiedResponse mResendorVerifiedResponse = (ResendorVerifiedResponse) message.obj;
                            if (mResendorVerifiedResponse.getAccVerified().equals(Flinnt.ENABLED)) {
                                mUser.setAccVerified(Flinnt.ENABLED);
                                UserInterface.getInstance().insertOrUpdateUser(mUser.getUserID(), mUser);
                                Helper.setCurrentUserConfig(mUser.getUserID());
                            }
                        }
                        setMainIntent();
                        break;

                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        ErrorResponse errorRes;
                        if (mClassType == ResendorVerified.ALREADY_VERIFIED || mClassType == ResendorVerified.RESEND_CODE) {
                            ResendorVerifiedResponse response = (ResendorVerifiedResponse) message.obj;
                            errorRes = response.errorResponse;
                        } else {
                            VerifyMobileResponse response = (VerifyMobileResponse) message.obj;
                            errorRes = response.errorResponse;
                        }
                        if (errorRes != null) {
                            Helper.showAlertMessage(VerifyMobileActivity.this, "Verify Account", errorRes.getMessage(), "Close");
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
            MyCommFun.sendTracker(this, "SignUp Verify - Mobile");
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
     * Set Intent after check ACCOUNT_VERIFIED
     */
    private void setMainIntent() {
        if (mUser.getAccVerified().equals(Flinnt.ENABLED)) {
            showAlertMessage(VerifyMobileActivity.this, "Verify Account", getResources().getString(R.string.account_verified), "Close");
        } else {
            if (mClassType == ResendorVerified.ALREADY_VERIFIED) {
                Helper.showAlertMessage(VerifyMobileActivity.this, "Verify Account", getResources().getString(R.string.account_not_verified), "Close");
            } else {
                Helper.showAlertMessage(VerifyMobileActivity.this, "Verify Account",
                        "You will shortly receive verification code on your mobile number '" + mobNumber + "'.", "Close");
            }
        }
    }

    /**
     * Display message dialog
     *
     * @param context       activity context
     * @param title         dialog resorceTitleTxt
     * @param message       dialog message
     * @param dialogBtnText dialog button text
     */
    private void showAlertMessage(final Context context, String title, String message, String dialogBtnText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set resorceTitleTxt
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
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
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (isSwapOperation) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    startActivity(new Intent(VerifyMobileActivity.this, MyCoursesActivity.class));
                }
                finish();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    private void verifyMobileCall(){
        mClassType = -1;
        varificationCode = varificationCodeEdtTxt.getText().toString().trim();
        if (TextUtils.isEmpty(varificationCode)) {
            Helper.showAlertMessage(VerifyMobileActivity.this, "Verify Account",
                    "Please enter code we sent on your mobile number '" + mobNumber + "'.", "Close");
        } else {
            if (Helper.isConnected()) {
                new VerifyMobile(mHandler, varificationCode, mUser.getUserID()).sendVerifyMobileRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.terms_conditions_verifymobile) {
            startActivity(new Intent(VerifyMobileActivity.this, TermsAndConditionsActivity.class));
        } else if (view.getId() == R.id.verify_button_verifymobile) {
            verifyMobileCall();
        } else if (view.getId() == R.id.already_verified_button_verifymobile) {
            if (Helper.isConnected()) {
                mClassType = ResendorVerified.ALREADY_VERIFIED;
                new ResendorVerified(mHandler, mClassType, mUser.getUserID()).sendResendorVerifiedRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }
        } else if (view.getId() == R.id.sms_not_received_txt_verifymobile) {
            if (Helper.isConnected()) {
                mClassType = ResendorVerified.RESEND_CODE;
                new ResendorVerified(mHandler, mClassType, mUser.getUserID()).sendResendorVerifiedRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }
        } else if (view.getId() == R.id.logout_button_verifymobile) {
            Helper.resetConfigValuesAndClearData(null);
            if (isSwapOperation) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
            } else {
                startActivity(new Intent(VerifyMobileActivity.this, LoginActivity.class));
            }
            finish();
        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(VerifyMobileActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(VerifyMobileActivity.this, "", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

    class SMSReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("SMSReceiver :: onReceive()");
            // TODO Auto-generated method stub
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                for (SmsMessage message : messages) {
                    if (message == null)
                        break;
                    String strFrom = "";
                    try {
                        strFrom = message.getOriginatingAddress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String strMsg = "";
                    try {
                        strMsg = message.getDisplayMessageBody();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(strFrom))
                        break;
                    if (TextUtils.isEmpty(strMsg))
                        strMsg = "";
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.info("SMS From:" + strFrom + ", msg:" + strMsg);
                    System.out.println("contain  code : " + strMsg.contains("flinnt verification code") + " ," +
                            " contain flinnt : " + strFrom.contains("FLINNT"));
                    if (strMsg.contains("flinnt verification code") && (strFrom.contains("FLINNT") || strFrom.contains("flinnt"))) {
                        String varificationCode = Helper.extractDigits(strMsg, 6);
                        System.out.println("varificationCode : " + varificationCode);
                        varificationCodeEdtTxt.setText(varificationCode);
                    }
                }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); //onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * register sms receiver to get verification code
     */
    private void registerSMSReceiver() {
        try {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.info("registerSMSReceiver to get varification code");
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
    private void unRegisterSMSReceiver() {
        try {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("unRegisterSMSReceiver...");
            unregisterReceiver(smsReceiver1);
        } catch (Exception e) {
        }

    }
}