package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ResendorVerified;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.ResendorVerifiedResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to email verification details
 */
public class VerifyEmailActivity extends AppCompatActivity implements OnClickListener {

    private Button alreadyVerifiedBtn, logoutBtn;
    private TextView sentVerificationLinkTxt, emailNotReceivedTxt, termCondition;
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog = null;
    public Handler mHandler = null;
    private int mClassType = ResendorVerified.ALREADY_VERIFIED;
    private User mUser;
    private boolean isSwapOperation;

    private String emailID = Config.getStringValue(Config.USER_LOGIN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.verify_email);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        alreadyVerifiedBtn = (Button) findViewById(R.id.already_verified_button_verifyemail);
        logoutBtn = (Button) findViewById(R.id.logout_button_verifyemail);
        sentVerificationLinkTxt = (TextView) findViewById(R.id.sent_verification_link_txt_verifyemail);
        emailNotReceivedTxt = (TextView) findViewById(R.id.email_not_received_txt_verifyemail);

        termCondition = (TextView) findViewById(R.id.terms_conditions_verifyemail);
        termCondition.setOnClickListener(this);

        alreadyVerifiedBtn.setOnClickListener(this);
        alreadyVerifiedBtn.setLongClickable(false);
        logoutBtn.setOnClickListener(this);
        logoutBtn.setLongClickable(false);
        emailNotReceivedTxt.setOnClickListener(this);

        mUser = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID));

        isSwapOperation = !TextUtils.isEmpty(Config.getStringValue(Config.USER_ID));

        Bundle b = getIntent().getExtras();
        if (null != b) {
            String userId = b.getString(Config.USER_ID, "");
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("userId :: " + userId);

            if (!TextUtils.isEmpty(userId)) {
                mUser = UserInterface.getInstance().getUserFromId(userId);
                if(mUser != null){
                    emailID = mUser.getUserLogin();
                }
            }
            if (b.containsKey("TYPE") && b.getString("TYPE").equals(MyCoursesActivity.SWAP_ACCOUNT_OPERATION)) {
                isSwapOperation = true;
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(isSwapOperation);

        sentVerificationLinkTxt.setText(Html.fromHtml(getResources().getString(R.string.sent_verification_link_on_email)
                .replace("xxxxxxxxxx", emailID)));

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
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
                        stopProgressDialog();
                        ResendorVerifiedResponse response = (ResendorVerifiedResponse) message.obj;
                        if (response.errorResponse != null) {
                            Helper.showToast(response.errorResponse.getMessage(), Toast.LENGTH_LONG);
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
            MyCommFun.sendTracker(this, "SignUp Verify - Email");
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

    /**
     * Set Intent after check ACCOUNT_VERIFIED
     */
    private void setMainIntent() {
        if (mUser.getAccVerified().equals(Flinnt.ENABLED)) {
            showAlertMessage(VerifyEmailActivity.this, "Verify Account", getResources().getString(R.string.account_verified), "Close");
        } else {
            if (mClassType == ResendorVerified.ALREADY_VERIFIED) {
                Helper.showAlertMessage(VerifyEmailActivity.this, "Verify Account", getResources().getString(R.string.account_not_verified), "Close");
            } else {
                Helper.showAlertMessage(VerifyEmailActivity.this, "Verify Account",
                        "You will shortly receive verification link on your email address '" + emailID + "'.", "Close");
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
        //alertDialogBuilder.setTitle(resorceTitleTxt);
        //alertDialogBuilder.setTitle("Change Password");
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
                    startActivity(new Intent(VerifyEmailActivity.this, MyCoursesActivity.class));
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

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.terms_conditions_verifyemail) {
            startActivity(new Intent(VerifyEmailActivity.this, TermsAndConditionsActivity.class));
        } else if (view.getId() == R.id.already_verified_button_verifyemail) {
            if (Helper.isConnected()) {
                mClassType = ResendorVerified.ALREADY_VERIFIED;
                new ResendorVerified(mHandler, mClassType, mUser.getUserID()).sendResendorVerifiedRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }

        } else if (view.getId() == R.id.email_not_received_txt_verifyemail) {
            if (Helper.isConnected()) {
                mClassType = ResendorVerified.RESEND_CODE;
                new ResendorVerified(mHandler, mClassType, mUser.getUserID()).sendResendorVerifiedRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }

        } else if (view.getId() == R.id.logout_button_verifyemail) {
            Helper.resetConfigValuesAndClearData(null);
            if (isSwapOperation) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
            } else {
                startActivity(new Intent(VerifyEmailActivity.this, LoginActivity.class));
            }
            finish();
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
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(VerifyEmailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(VerifyEmailActivity.this, "", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
}
