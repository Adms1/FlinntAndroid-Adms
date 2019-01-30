package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.SignUp;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.SignUpResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to show sign user up
 */
public class SignUpActivity extends AppCompatActivity implements OnClickListener {

    Button submitBtn;
    EditText firstNameEdtTxt, lastNameEdtTxt, emailMobileEdtTxt, passwordEdtTxt, confirmPasswordEdtTxt;
    TextView termCondition;
    String firstName, lastName, emailMobile, password, confirmPassword;
    Toolbar toolbar;
    public Handler mHandler = null;
    public static final String REGEX_EMAIL_ID = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String REGEX_NUMBER = "^[0-9]*$";
    Resources res = FlinntApplication.getContext().getResources();
    ProgressDialog mProgressDialog = null;

    public static final int SIGNUP_CALLBACK = 119;

    Intent returnIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.sign_up);
//        getEmailID();
//        String number = getMyPhoneNO();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        submitBtn = (Button) findViewById(R.id.submit_button_signup);
        firstNameEdtTxt = (EditText) findViewById(R.id.first_name_signup);
        lastNameEdtTxt = (EditText) findViewById(R.id.last_name_signup);
        emailMobileEdtTxt = (EditText) findViewById(R.id.email_mobile_signup);
        passwordEdtTxt = (EditText) findViewById(R.id.password_signup);
        confirmPasswordEdtTxt = (EditText) findViewById(R.id.confirm_password_signup);
        confirmPasswordEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    signUpCall();
                }
                return false;
            }
        });
        passwordEdtTxt.setTypeface(Typeface.DEFAULT);
        confirmPasswordEdtTxt.setTypeface(Typeface.DEFAULT);

        termCondition = (TextView) findViewById(R.id.terms_conditions_signup);
        termCondition.setOnClickListener(this);

        submitBtn.setOnClickListener(this);
        submitBtn.setLongClickable(false);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof SignUpResponse) {
                            SignUpResponse mSignUpResponse = (SignUpResponse) message.obj;

                            User user = new User();
                            user.setUserID(mSignUpResponse.getUserID());
                            user.setUserLogin(mSignUpResponse.getUserLogin());
                            user.setAccVerified(mSignUpResponse.getAccVerified());
                            user.setAccAuthMode(mSignUpResponse.getAccAuthMode());
                            user.setFirstName(firstName);
                            user.setLastName(lastName);

                            UserInterface.getInstance().insertOrUpdateUser(user.getUserID(), user);

                            if (mSignUpResponse.getAccVerified().equals(Flinnt.ENABLED)) {
                                Helper.setCurrentUserConfig(user.getUserID());
                            }

                            returnIntent.putExtra("key", SIGNUP_CALLBACK);

                            setUpIntent(mSignUpResponse);
                        }

                        break;

                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        SignUpResponse response = (SignUpResponse) message.obj;
                        if (response.errorResponse != null) {
                            Helper.showAlertMessage(SignUpActivity.this, "SignUp", response.errorResponse.getMessage(), "CLOSE");
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

    private void setUpIntent(SignUpResponse response) {

        if (response.getAccAuthMode().equals("email")) {
            startActivityForResult(new Intent(SignUpActivity.this, VerifyEmailActivity.class)
                            .putExtra(Config.USER_ID, response.getUserID())
                    , MyCoursesActivity.LOGIN_CALLBACK);
        } else {
            startActivityForResult(new Intent(SignUpActivity.this, VerifyMobileActivity.class)
                            .putExtra(Config.USER_ID, response.getUserID())
                    , MyCoursesActivity.LOGIN_CALLBACK);
        }

//        finish();

       /* // also finish LoginActivity
        for (int i = 0; i < LoginActivity.v_context.size(); i++) {
            (LoginActivity.v_context.elementAt(i)).finish();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("requestCode : " + requestCode + ", resultCode : " + resultCode);

        if (requestCode == MyCoursesActivity.LOGIN_CALLBACK) {
//            Intent returnIntent = new Intent();
            setResult(resultCode, returnIntent);
            finish();
        }
    }

    // To get mobile number
    private String getMyPhoneNO() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    private void getEmailID() {
        // Get EmailID
        /*Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                LogWriter.write("Email id : "+possibleEmail);
            }
        }*/
        // Get Mobile Number
        Toast.makeText(SignUpActivity.this, "Mobile No. : " + ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getLine1Number(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "SignUp");
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
    public void onClick(View view) {

        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(SignUpActivity.this);
        } else {
            if (view.getId() == R.id.terms_conditions_signup) {
                startActivity(new Intent(SignUpActivity.this, TermsAndConditionsActivity.class));
            } else if (view.getId() == R.id.submit_button_signup) {
                signUpCall();
            }
        }
    }

    private void signUpCall() {
        firstName = firstNameEdtTxt.getText().toString().trim();
        lastName = lastNameEdtTxt.getText().toString().trim();
        emailMobile = emailMobileEdtTxt.getText().toString().trim();
        password = passwordEdtTxt.getText().toString().trim();
        confirmPassword = confirmPasswordEdtTxt.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(emailMobile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            validateEmptyField();
        } else if (firstName.length() < 2) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.minimum_first_name), "CLOSE");
        } else if (lastName.length() < 2) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.minimum_last_name), "CLOSE");
        } else if (isValidEmailorNumber() != validateErrorCodes.VALID_FIELD) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.validate_email_mobile), "CLOSE");
        } else if (password.length() < 6) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.validate_pass), "CLOSE");
        } else if (password.length() > 15) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.pass_max_number), "CLOSE");
        } else if (!confirmPassword.equals(password)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", res.getString(R.string.confirmpass_not_match), "CLOSE");
        } else {
            if (Helper.isConnected()) {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUserLogin(emailMobile);

                new SignUp(mHandler, firstName, lastName, emailMobile, password).sendSignUpRequest();
                startProgressDialog();
            } else {
                Helper.showNetworkAlertMessage(this);
            }
        }
    }

    /**
     * Checks for empty fields
     */
    private void validateEmptyField() {

        if (TextUtils.isEmpty(firstName)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", "Add First Name", "CLOSE");
        } else if (TextUtils.isEmpty(lastName)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", "Add Last Name", "CLOSE");
        } else if (TextUtils.isEmpty(emailMobile)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", "Add email or mobile number", "CLOSE");
        } else if (TextUtils.isEmpty(password)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", "Add Password", "CLOSE");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Helper.showAlertMessage(SignUpActivity.this, "SignUp", "Add Confirm Password", "CLOSE");
        }

    }

    public static interface validateErrorCodes {
        int VALID_FIELD = 0;
        int NOT_VALIDATE_EMAIL_MOBILE = 1;
        int NOT_VALID_NUMBER = 2;
        int NOT_VALID_EMAIL = 3;
    }

    /**
     * Check if the entered email or number is valid or not
     *
     * @return true if valid, false otherwise
     */
    private int isValidEmailorNumber() {

        int ret = validateErrorCodes.NOT_VALIDATE_EMAIL_MOBILE;
        if (emailMobile.matches(REGEX_NUMBER)) {
            if (emailMobile.length() == 10) {
                return validateErrorCodes.VALID_FIELD;
            } else {
                return validateErrorCodes.NOT_VALID_NUMBER;
            }
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailMobile).matches()) {
            return validateErrorCodes.VALID_FIELD;
        }
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
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
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(SignUpActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(SignUpActivity.this, "SignUp", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
