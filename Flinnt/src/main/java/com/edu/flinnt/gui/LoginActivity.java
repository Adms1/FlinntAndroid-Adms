package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Login;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.gui.SignUpActivity.validateErrorCodes;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.services.LocationService;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * GUI class to make login
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    Button loginBtn, signUpBtn;
    EditText emailMobileEdtTxt, passwordEdtTxt;
    TextView forgotPasswordTxt;
    Toolbar toolbar;
    public Handler mHandler = null;
    String emailMobile, password;
    Resources res = FlinntApplication.getContext().getResources();
    ProgressDialog mProgressDialog = null;
    public static Vector<Activity> v_context = new Vector<Activity>();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Common comm = new Common(this);

    int type;
    String userId;
    protected static final int NEW_ACCOUNT = 501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This device doesn't support Play services. So, This device is not supported.
        /*if( checkPlayServices() )*/
        {
            //GetUserDetailsLogin_l1();

            Bundle b = getIntent().getExtras();
            if (null != b) {
                type = b.getInt("TYPE", 0);
                if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("type :: " + type);
            }

            if (type == NEW_ACCOUNT) { // for add account operation
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                gologin();

                if (!TextUtils.isEmpty(Config.getStringValue(Config.USER_ID))) {
                    userId = Config.getStringValue(Config.USER_ID);
                    setMainIntent(null);
                } else {
                    ArrayList<String> userIds = UserInterface.getInstance().getUserIdList();
                    if (userIds.size() == 1) {
                        userId = userIds.get(0);
                        setMainIntent(null);
                    }
                }
            }

            // add activity context
            if (v_context != null) {
                v_context.removeAllElements();
            }
            v_context.add(LoginActivity.this);

            loginBtn = (Button) findViewById(R.id.login_button_login);
            signUpBtn = (Button) findViewById(R.id.sign_up_button_login);
            emailMobileEdtTxt = (EditText) findViewById(R.id.email_mobile_login);
            passwordEdtTxt = (EditText) findViewById(R.id.password_login);
            passwordEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
            passwordEdtTxt.setTypeface(Typeface.DEFAULT);
                    passwordEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        loginCall();
                    }
                    return false;
                }
            });
            forgotPasswordTxt = (TextView) findViewById(R.id.forgot_password_login);
            loginBtn.setOnClickListener(this);
            loginBtn.setLongClickable(false);
            signUpBtn.setOnClickListener(this);
            signUpBtn.setLongClickable(false);
            forgotPasswordTxt.setOnClickListener(this);

           /* if (type == NEW_ACCOUNT) { // for add account operation
                signUpBtn.setVisibility(View.GONE);
            }*/

            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    // Gets the task from the incoming Message object.
                    switch (message.what) {
                        case Flinnt.SUCCESS:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            LoginResponse mLoginResponse = (LoginResponse) message.obj;
                            insertUserInDatabase(mLoginResponse);
                            stopProgressDialog();
                            setMainIntent(mLoginResponse);
                            break;
                        case Flinnt.FAILURE:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                            stopProgressDialog();
                            LoginResponse response = (LoginResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(LoginActivity.this, "Login", response.errorResponse.getMessage(), "Close");
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Login");
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
        super.onResume();
    }


    /**
     * Set Intent after check ACCOUNT_VERIFIED and ACCOUNT_AUTH_MODE
     */
    private void setMainIntent(LoginResponse loginResponse) {
        if (!Config.getStringValue(Config.FCM_TOKEN).isEmpty()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.putExtra(LocationService.IS_NEW_USER_LOGGED_IN, true);
            startService(intent);
        }
        if (null == loginResponse) {
            User user = UserInterface.getInstance().getUserFromId(userId);
            String getVerified, getAuthMode;

            getVerified = null != user ? user.getAccVerified() : Config.getStringValue(Config.ACCOUNT_VERIFIED);
            getAuthMode = null != user ? user.getAccAuthMode() : Config.getStringValue(Config.ACCOUNT_AUTH_MODE);

            if (getVerified.equals(Flinnt.DISABLED)) {
                if (getAuthMode.equals("email")) {
                    startActivity(new Intent(LoginActivity.this, VerifyEmailActivity.class).putExtra(Config.USER_ID, userId));
                } else if (getAuthMode.equals("mobile")) {
                    startActivity(new Intent(LoginActivity.this, VerifyMobileActivity.class).putExtra(Config.USER_ID, userId));
                }
            } else {
                Bundle mBundle = new Bundle();
                mBundle.putInt("isFromNotification", Flinnt.FALSE);
                Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
                intent.putExtras(mBundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //startActivity(new Intent(LoginActivity.this, MyCoursesActivity.class));
            }
            finish();
        } else {
            if (loginResponse.getAccVerified().equals(Flinnt.DISABLED)) {
                if (loginResponse.getAccAuthMode().equals("email")) {
                    startActivityForResult(new Intent(LoginActivity.this, VerifyEmailActivity.class)
                                    .putExtra(Config.USER_ID, loginResponse.getUserID()).putExtra("isFromNotification", Flinnt.FALSE)
                            , MyCoursesActivity.LOGIN_CALLBACK);
                } else {
                    startActivityForResult(new Intent(LoginActivity.this, VerifyMobileActivity.class)
                                    .putExtra(Config.USER_ID, loginResponse.getUserID()).putExtra("isFromNotification", Flinnt.FALSE)
                            , MyCoursesActivity.LOGIN_CALLBACK);
                }
            } else {
                if (type == NEW_ACCOUNT) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    //startActivity(new Intent(LoginActivity.this, MyCoursesActivity.class));
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("isFromNotification", Flinnt.FALSE);
                    Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
                    intent.putExtras(mBundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("requestCode : " + requestCode + ", resultCode : " + resultCode);

        if (requestCode == MyCoursesActivity.LOGIN_CALLBACK) {
            Intent returnIntent = new Intent();
            setResult(resultCode, returnIntent);
            finish();
        }

        if (null != data && null != data.getExtras() && data.getExtras().containsKey("key") && data.getExtras().getInt("key") == SignUpActivity.SIGNUP_CALLBACK) {
            finish();
        }
    }

    private void insertUserInDatabase(LoginResponse mLoginResponse) {

        UserInterface userInterface = UserInterface.getInstance();

        if (null != mLoginResponse) {
            User user = new User();
            user.setUserID(mLoginResponse.getUserID());
            user.setUserLogin(mLoginResponse.getUserLogin());
            user.setFirstName(mLoginResponse.getFirstName());
            user.setLastName(mLoginResponse.getLastName());
            user.setIsActive(mLoginResponse.getIsActive());
            user.setUserPicture(mLoginResponse.getUserPicture());
            user.setAccVerified(mLoginResponse.getAccVerified());
            user.setAccAuthMode(mLoginResponse.getAccAuthMode());
            user.setCanAdd(mLoginResponse.getCanAdd());
            user.setUserPictureUrl(mLoginResponse.getUserPictureUrl());
            user.setCanBrowseCourse(mLoginResponse.getCanBrowseCourse());
            user.setTokenSentToServer(Flinnt.FALSE);
            user.setCurrentUser(Flinnt.FALSE);

            userInterface.insertOrUpdateUser(user.getUserID(), user);

            if (mLoginResponse.getAccVerified().equals(Flinnt.ENABLED)) {
                Helper.setCurrentUserConfig(user.getUserID());
            }
        }
    }

    /**
     * Check if the entered email or number is valid or not
     *
     * @return true if valid, false otherwise
     */
    private int isValidEmailorNumber() {
        int ret = validateErrorCodes.NOT_VALIDATE_EMAIL_MOBILE;
        if (emailMobile.matches(SignUpActivity.REGEX_NUMBER)) {
            if (emailMobile.length() == 10) {
                return validateErrorCodes.VALID_FIELD;
            } else {
                return validateErrorCodes.NOT_VALID_NUMBER;
            }
        }/*else if(emailMobile.matches(SignUpActivity.REGEX_EMAIL_ID)){
            return validateErrorCodes.VALID_FIELD;
		}*/ else if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailMobile).matches()) {
            return validateErrorCodes.VALID_FIELD;
        }
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); //onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.login_button_login) {
            loginCall();

        } else if (view.getId() == R.id.sign_up_button_login) {
            startActivityForResult(new Intent(this, SignUpActivity.class), SignUpActivity.SIGNUP_CALLBACK);
        } else if (view.getId() == R.id.forgot_password_login) {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        }
    }

    private void loginCall() {
        emailMobile = emailMobileEdtTxt.getText().toString().trim();
        password = passwordEdtTxt.getText().toString().trim();

        if (TextUtils.isEmpty(emailMobile) || TextUtils.isEmpty(password)) {
            validateEmptyField();
        } else if (isValidEmailorNumber() != validateErrorCodes.VALID_FIELD) {
            Helper.showAlertMessage(LoginActivity.this, "Login",res.getString(R.string.validate_email_mobile), "Close");
        } else {
            if (UserInterface.getInstance().isLogInIdExists(emailMobile)) {
                Helper.showAlertMessage(LoginActivity.this, "Login",getString(R.string.already_logged_in), "Close");
            } else {
                if (Helper.isConnected()) {
                    Config.setStringValue(Config.USER_LOGIN, emailMobile);
                    Config.setStringValue(Config.PASSWORD, password);
                    new Login(mHandler, Login.USER_LOGIN).sendLoginRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(this);
                }
            }
        }
    }

    /**
     * Checks which field is empty and display message
     */
    private void validateEmptyField() {

        if (TextUtils.isEmpty(emailMobile)) {
            Helper.showAlertMessage(LoginActivity.this, "Login", "Add email or mobile number", "CLOSE");
        } else if (TextUtils.isEmpty(password)) {
            Helper.showAlertMessage(LoginActivity.this, "Login", "Add Password", "CLOSE");
        }

    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(LoginActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(LoginActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(LoginActivity.this))
                mProgressDialog.show();
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
     * Make login and create offline database
     *
     * @return userID
     */
    private String gologin() {
        String uid = Config.getStringValue(Config.USER_ID);

        String OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/flinnt/" + "document" + "/";
        File myFile = new File(OutPutFile + "flinnt.db");
        if (myFile.exists()) {
            uid = comm.getUserID();
            //if(!TextUtils.isEmpty(uid)) Helper.showToast(uid, Toast.LENGTH_SHORT);
            Config.setStringValue(Config.USER_ID, uid);
        }
        return uid;
    }
}