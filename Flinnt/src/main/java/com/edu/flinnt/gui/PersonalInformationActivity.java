package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CheckoutCourse;
import com.edu.flinnt.core.JoinCommunity;
import com.edu.flinnt.core.PersonalInformation;
import com.edu.flinnt.core.ProfileGet;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.PersonalInformationRequest;
import com.edu.flinnt.protocol.PersonalInformationResponse;
import com.edu.flinnt.protocol.Profile;
import com.edu.flinnt.protocol.ProfileResponse;
import com.edu.flinnt.protocol.ResendorVerifiedRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.FlinntLocationCallBack;
import com.edu.flinnt.util.FusedLocationUtil;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.Calendar;
import java.util.List;

import static com.edu.flinnt.gui.BrowseCourseDetailActivity.CHECKOUT_RESPONSE;


/**
 * Created by flinnt-android-2 on 25/11/16.
 */

public class PersonalInformationActivity extends AppCompatActivity implements View.OnClickListener , FlinntLocationCallBack {

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ImageLoader mImageLoader;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    public static final String PERSONAL_BROWSE_COURSE = "browsableCourse";
    public static final String COURSE_PICTURE_URL = "coursePictureUrl";
    public static final String BUTTON_JOIN_BUY_TEXT = "courseJoinBuy";
    private ResendorVerifiedRequest mProfileGetRequest;
    private ProfileResponse mProfileResponse;
    private PersonalInformationResponse mPersonalInformationResponse;
    private String firstName, lastName, instituteName = "", education = "", ageStr = "" , cityName = "";
    private int mDobYear = 0, mDobMonth = 0, mDobDay = 0, mAge = 0;
    private String mMale = "Male";
    private String mFemale = "Female";
    private String genderStr = "";
    private EditText firstNameEdtTxt, lastNameEdtTxt, instituteNameEdtTxt, educationEdtTxt, ageEdtTxt ,cityEdtTxt;
    private TextView courseNameTxt;
    private Spinner genderSpin;
    String userId = "", courseId = "", courseName = "", coursePictureUrl = "";
    NetworkImageView coursePhotoImg;

    private BrowsableCourse mBrowsableCourse;
    TextView yearTxt;
    Button joinBuyBtn;
    String joinBuyBtnString = "";
    private FusedLocationUtil mFusedLocationUtil;
    private ImageButton mDetectLocationImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_personal_information);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.personal_information_title);


        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(PersonalInformationActivity.PERSONAL_BROWSE_COURSE))
                mBrowsableCourse = (BrowsableCourse) bundle.getSerializable(PersonalInformationActivity.PERSONAL_BROWSE_COURSE);
            if (bundle.containsKey(PersonalInformationActivity.COURSE_PICTURE_URL))
                coursePictureUrl = bundle.getString(PersonalInformationActivity.COURSE_PICTURE_URL);
            if (bundle.containsKey(PersonalInformationActivity.BUTTON_JOIN_BUY_TEXT))
                joinBuyBtnString = bundle.getString(PersonalInformationActivity.BUTTON_JOIN_BUY_TEXT);
        }

        courseId = mBrowsableCourse.getId();
        courseName = mBrowsableCourse.getName();
        mImageLoader = Requester.getInstance().getImageLoader();

        firstNameEdtTxt = (EditText) findViewById(R.id.first_name_edit);
        lastNameEdtTxt = (EditText) findViewById(R.id.last_name_edit);
        instituteNameEdtTxt = (EditText) findViewById(R.id.institute_name_edit);
        educationEdtTxt = (EditText) findViewById(R.id.education_edit);
        ageEdtTxt = (EditText) findViewById(R.id.age_edit);
        coursePhotoImg = (NetworkImageView) findViewById(R.id.course_photo_img);
        courseNameTxt = (TextView) findViewById(R.id.course_name_text);
        yearTxt = (TextView) findViewById(R.id.year_text);
        cityEdtTxt = (EditText) findViewById(R.id.city_edit);
        joinBuyBtn = (Button) findViewById(R.id.join_buy_course_btn);
        joinBuyBtn.setText(joinBuyBtnString);

        courseNameTxt.setText(courseName);
        coursePhotoImg.setImageUrl(coursePictureUrl, mImageLoader);

        mDetectLocationImgBtn = (ImageButton)findViewById(R.id.detect_city_img_btn);
        mDetectLocationImgBtn.setOnClickListener(this);

        genderSpin = (Spinner) findViewById(R.id.spinner_gender_profile);
        genderSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (position == 0) {
                    genderStr = "";
                } else if (position == 1) {
                    genderStr = mMale;
                } else if (position == 2) {
                    genderStr = mFemale;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof ProfileResponse) {
                            mProfileResponse = (ProfileResponse) message.obj;
                            fillData(mProfileResponse);
                        }
                        if (message.obj instanceof PersonalInformationResponse) {
                            mPersonalInformationResponse = (PersonalInformationResponse) message.obj;
                            LogWriter.write("SUCCESS_RESPONSE PERSONAL : " + message.obj.toString());
                            requestSelection();
                        }

                        if (message.obj instanceof JoinCourseResponse) {
                            JoinCourseResponse mJoinCourseResponse = (JoinCourseResponse) message.obj;
                            if (mJoinCourseResponse.getJoined().equals(Flinnt.ENABLED)) {
                                Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Course.COURSE_ID_KEY, courseId);
                                intent.putExtra(Course.COURSE_NAME_KEY, courseName);
                                intent.putExtra(MyCoursesActivity.IS_JOIN, true);
                                startActivity(intent);
                                finish();
                            }
                        }

                        if (message.obj instanceof CheckoutResponse) {
                            CheckoutResponse mCheckoutResponse = (CheckoutResponse) message.obj;
                            if (mCheckoutResponse.getData().getTransactionId() != 0) {
                                BrowsableCourse.Price price = mBrowsableCourse.getPrice();
                                Intent intent = new Intent(PersonalInformationActivity.this, CheckoutActivity.class);
                                intent.putExtra(BrowsableCourse.NAME_KEY, mBrowsableCourse.getName());
                                intent.putExtra(CHECKOUT_RESPONSE, mCheckoutResponse);
                                intent.putExtra(BrowsableCourse.PRICE_KEY, price);
                                intent.putExtra(BrowsableCourse.ID_KEY, courseId);
                                startActivityForResult(intent, BrowseCourseDetailActivity.CHECKOUT_CALLBACK);
                            }
                        }

                        break;
                    case Flinnt.FAILURE:
                        if (message.obj instanceof PersonalInformationResponse) {
                            PersonalInformationResponse mPersonalInformationResponse = (PersonalInformationResponse) message.obj;
                            String errorMessage = mPersonalInformationResponse.errorResponse.getMessage();
                            stopProgressDialog();
                            Helper.showAlertMessage(PersonalInformationActivity.this, "Error", errorMessage, "Close");
                        }
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        break;
                    default:
                        /*
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(message);
                }
            }
        };

        mProfileGetRequest = new ResendorVerifiedRequest();
        new ProfileGet(mHandler, mProfileGetRequest, Config.getStringValue(Config.USER_ID)).sendProfileGetRequest();
        startProgressDialog();

        joinBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(PersonalInformationActivity.this);
                } else {
                    if (valideDetails()) {
                        PersonalInformationRequest mPersonalInformationRequest = new PersonalInformationRequest();
                        mPersonalInformationRequest.setUserID(Config.getStringValue(Config.USER_ID));
                        mPersonalInformationRequest.setCourseID(courseId);
                        mPersonalInformationRequest.setFirstName(firstName);
                        mPersonalInformationRequest.setLastName(lastName);
                        mPersonalInformationRequest.setInstituteName(instituteName);
                        mPersonalInformationRequest.setGender(genderStr);
                        mPersonalInformationRequest.setEducation(education);
                        mPersonalInformationRequest.setAge(ageStr);
                        mPersonalInformationRequest.setCity(cityName);

                        new PersonalInformation(mHandler, mPersonalInformationRequest).sendPersonalInformationRequest();
                        startProgressDialog();
                    }
                }
            }
        });
    }

    private void initializeLocationService() {
        if(mFusedLocationUtil!=null){
            mFusedLocationUtil.onPause();
            mFusedLocationUtil = null;
        }
        mFusedLocationUtil = new FusedLocationUtil(this,this);
        if(mFusedLocationUtil.isLocationFetchingAllowed()){
            mFusedLocationUtil.setActivity(this);
            mFusedLocationUtil.startLocationUpdatesWithSettings();
        }
    }

    private void initializeLocationServiceAuto() {
        mFusedLocationUtil = new FusedLocationUtil(this,this);
        if(mFusedLocationUtil.isLocationFetchingAllowed()){
            mFusedLocationUtil.setActivity(this);
            mFusedLocationUtil.startLocationUpdatesWithSettings();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder mGeoCoder = new Geocoder(this);
            List<Address> mAddresses = mGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(mAddresses.size()>0 && mAddresses.get(0).getLocality()!= null) {
                if(LogWriter.isApplicationDebug(this))
                    LogWriter.info("Address : " + mAddresses.get(0).toString());
                cityEdtTxt.setText(mAddresses.get(0).getLocality());
                mDetectLocationImgBtn.setImageResource(R.drawable.my_location_enabled);
                mFusedLocationUtil.onPause();
            }
        } catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(PersonalInformationActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(PersonalInformationActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
    public void onBackPressed() {
        finish();
    }

    /**
     * Fill data in views
     *
     * @param profileResponse profile response
     */
    protected void fillData(ProfileResponse profileResponse) {
        Profile profile = profileResponse.getProfileList().get(profileResponse.getCount() - 1);

        firstName = profile.getFirstName();
        lastName = profile.getLastName();
        instituteName = profile.getInstituteName();
        genderStr = profile.getGender();
        mDobDay = Integer.valueOf(profile.getBirthDay());
        mDobMonth = Integer.valueOf(profile.getBirthMonth());
        mDobYear = Integer.valueOf(profile.getBirthYear());
        cityName = profile.getCity();

        firstNameEdtTxt.setText(firstName);
        lastNameEdtTxt.setText(lastName);
        instituteNameEdtTxt.setText(instituteName);
        cityEdtTxt.setText(cityName);
        if(TextUtils.isEmpty(cityName)){
            initializeLocationServiceAuto();
        }
        if (genderStr.equalsIgnoreCase(mMale)) {
            genderSpin.setSelection(1);
        } else if (genderStr.equalsIgnoreCase(mFemale)) {
            genderSpin.setSelection(2);
        } else {
            genderSpin.setSelection(0);
        }
        //genderSpin.setSelection(genderStr.equalsIgnoreCase(mMale) ? 0 : 1);

        Calendar c = Calendar.getInstance();


        LogWriter.write("day : " + mDobDay + " month : " + mDobMonth + " year : " + mDobYear);

        if (mDobYear != 0 || mDobDay != 0) {
//            mCalendar.set(mDobYear, mDobMonth - 1, mDobDay); // January starts from 0, not from 1
//            SimpleDateFormat sdf = new SimpleDateFormat(dobFormat, Locale.US);

            //  int year = c.get(Calendar.YEAR) - mDobYear;
            int diff = c.get(Calendar.YEAR) - mDobYear;
            if (mDobMonth > c.get(Calendar.MONTH) + 1 || (mDobMonth == c.get(Calendar.MONTH) + 1 && mDobDay > c.get(Calendar.DATE))) {
                diff--;
            }
            mAge = diff;
            ageEdtTxt.setText("" + mAge);
        }
        if (ageEdtTxt.getText().length() > 0) {
            yearTxt.setVisibility(View.VISIBLE);
        }


        ageEdtTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (ageEdtTxt.getText().length() > 0) {
                    yearTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ageEdtTxt.getText().length() > 0) {
                    yearTxt.setVisibility(View.VISIBLE);
                } else {
                    yearTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Checks the entered details
     *
     * @return true if valid, false otherwise
     */
    private boolean valideDetails() {

        firstName = firstNameEdtTxt.getText().toString().trim();
        lastName = lastNameEdtTxt.getText().toString().trim();
        instituteName = instituteNameEdtTxt.getText().toString().trim();
        education = educationEdtTxt.getText().toString().trim();
        ageStr = ageEdtTxt.getText().toString().trim();
        cityName = cityEdtTxt.getText().toString().trim();
        if (TextUtils.isEmpty(firstName)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_first), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_last), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(instituteName)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_institute), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(genderStr)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_gender), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(education)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_education), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(ageStr)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_age), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(cityName)) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_city), getString(R.string.personal_close_button));
            return false;
        }

        if (firstName.length() < 2) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_first_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (lastName.length() < 2) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_last_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (instituteName.length() < 2) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_institute_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (Integer.parseInt(ageStr) < 2) {
            Helper.showAlertMessage(PersonalInformationActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_age_minimum), getString(R.string.personal_close_button));
            return false;
        }


        return true;
    }

    private void requestSelection() {
        if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.ENABLED)) {
            startProgressDialog();
            new JoinCommunity(mHandler, courseId, true).sendJoinCommunityRequest();
        } else if (mBrowsableCourse.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
            startProgressDialog();
            new CheckoutCourse(mHandler, courseId).sendCheckoutRequest();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(PersonalInformationActivity.this, "activity="+Flinnt.PERSIONAL_INFORMATION);
            GoogleAnalytics.getInstance(PersonalInformationActivity.this).reportActivityStart(PersonalInformationActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(PersonalInformationActivity.this).reportActivityStop(PersonalInformationActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case FusedLocationUtil.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mFusedLocationUtil.onResume();
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detect_city_img_btn:
                initializeLocationService();
                break;
        }
    }
}