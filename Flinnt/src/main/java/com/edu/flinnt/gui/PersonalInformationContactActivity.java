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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.UserProfileGet;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.PersonalInformationRequest;
import com.edu.flinnt.protocol.PersonalInformationResponse;
import com.edu.flinnt.protocol.UserProfileRequest;
import com.edu.flinnt.protocol.UserProfileResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.FlinntLocationCallBack;
import com.edu.flinnt.util.FusedLocationUtil;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

import static com.edu.flinnt.gui.BrowseCourseDetailActivity.CHECKOUT_RESPONSE;

/**
 * Created by flinnt-android-2 on 11/2/17.
 */

public class PersonalInformationContactActivity extends AppCompatActivity implements View.OnClickListener , FlinntLocationCallBack {

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ImageLoader mImageLoader;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    private UserProfileRequest mUserProfileRequest;
    private UserProfileResponse mUserProfileResponse;
    private PersonalInformationResponse mPersonalInformationResponse;
    private String firstName, lastName, mobileNumber = "", email = "", city = "", categoryID = "";
    private EditText firstNameEdtTxt, lastNameEdtTxt, mobileEdtTxt, emailEdtTxt, cityEdtTxt;
    private TextView courseNameTxt;
    String userId = "", courseId = "", courseName = "", coursePictureUrl = "";
    NetworkImageView coursePhotoImg;
    Button joinBuyBtn;
    String joinBuyBtnString = "";
    private ArrayList<UserProfileResponse.Data.Category> mCategoryList = new ArrayList<UserProfileResponse.Data.Category>();
    private BrowsableCourse mBrowsableCourse;
    private Course joinedCourse;
    private Spinner categorySpn;
    private ArrayList<String> categoriesList;
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
        setContentView(R.layout.activity_personal_information_contact);

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
        mobileEdtTxt = (EditText) findViewById(R.id.mobile_number_edit);
        emailEdtTxt = (EditText) findViewById(R.id.email_edit);
        cityEdtTxt = (EditText) findViewById(R.id.city_edit);
        coursePhotoImg = (NetworkImageView) findViewById(R.id.course_photo_img);
        courseNameTxt = (TextView) findViewById(R.id.course_name_text);
        categorySpn = (Spinner) findViewById(R.id.category_spn);

        courseNameTxt.setText(courseName);
        coursePhotoImg.setImageUrl(coursePictureUrl, mImageLoader);
        joinBuyBtn = (Button) findViewById(R.id.join_buy_course_btn);
        joinBuyBtn.setText(joinBuyBtnString);
        mDetectLocationImgBtn = (ImageButton)findViewById(R.id.detect_city_img_btn);
        mDetectLocationImgBtn.setOnClickListener(this);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof UserProfileResponse) {
                            mUserProfileResponse = (UserProfileResponse) message.obj;
                            fillData(mUserProfileResponse);
                        }
                        if (message.obj instanceof PersonalInformationResponse) {
                            mPersonalInformationResponse = (PersonalInformationResponse) message.obj;
                            LogWriter.write("SUCCESS_RESPONSE PERSONAL : " + message.obj.toString());
                            requestSelection();
                        }

                        if (message.obj instanceof JoinCourseResponse) {
                            JoinCourseResponse mJoinCourseResponse = (JoinCourseResponse) message.obj;
                            if (mJoinCourseResponse.getJoined().equals(Flinnt.ENABLED)) {
                                joinedCourse = mJoinCourseResponse.getJoinedCourse();
//                              onBackPressed(); // for navigate to MyCourse screen
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
                                Intent intent = new Intent(PersonalInformationContactActivity.this, CheckoutActivity.class);
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
                            Helper.showAlertMessage(PersonalInformationContactActivity.this, "Error", errorMessage, "Close");
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
        startProgressDialog();
        mUserProfileRequest = new UserProfileRequest();
        new UserProfileGet(mHandler, mUserProfileRequest, Config.getStringValue(Config.USER_ID),courseId).sendUserProfileGetRequest();

        joinBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(PersonalInformationContactActivity.this);
                } else {
                    if (valideDetails()) {
                        PersonalInformationRequest mPersonalInformationRequest = new PersonalInformationRequest();
                        mPersonalInformationRequest.setUserID(Config.getStringValue(Config.USER_ID));
                        mPersonalInformationRequest.setCourseID(courseId);
                        mPersonalInformationRequest.setFirstName(firstName);
                        mPersonalInformationRequest.setLastName(lastName);
                        mPersonalInformationRequest.setMobileNo(mobileNumber);
                        mPersonalInformationRequest.setEmailId(email);
                        mPersonalInformationRequest.setCity(city);
                        mPersonalInformationRequest.setCategoryID(categoryID);

                        mPersonalInformationRequest.setActivityKey("contact");

                        new PersonalInformation(mHandler, mPersonalInformationRequest).sendPersonalInformationRequest();
                        startProgressDialog();
                        //   requestSelection();
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

    private void updateCategories(ArrayList<String> categoriesList){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpn.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Personal Information");
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
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(PersonalInformationContactActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(PersonalInformationContactActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
    protected void onDestroy() {
        if(mFusedLocationUtil != null){
            mFusedLocationUtil.onPause();
        }
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Fill data in views
     *
     */
    protected void fillData(UserProfileResponse userProfileResponse) {
        if(userProfileResponse.getData().getShowCategory() == Flinnt.TRUE){
            categorySpn.setVisibility(View.VISIBLE);
        }else {
            categorySpn.setVisibility(View.GONE);
        }
        UserProfileResponse.Data.Profile profile = userProfileResponse.getData().getProfile().get(userProfileResponse.getData().getProfile().size() - 1);

        firstName = profile.getFirstName();
        lastName = profile.getLastName();
        mobileNumber = profile.getMobile();
        email = profile.getEmail();
        city = profile.getCity();

        firstNameEdtTxt.setText(firstName);
        lastNameEdtTxt.setText(lastName);
        mobileEdtTxt.setText(mobileNumber);
        emailEdtTxt.setText(email);
        cityEdtTxt.setText(city);
        if(TextUtils.isEmpty(city)){
            initializeLocationServiceAuto();
        }


        mCategoryList = userProfileResponse.getData().getCategories();
        categoriesList = new ArrayList<>();
        categoriesList.add(getResources().getString(R.string.select_category_txt));
        for (int i = 0; i < mCategoryList.size(); i++) {
            categoriesList.add(mCategoryList.get(i).getName());
        }
        updateCategories(categoriesList);
    }

    /**
     * Checks the entered details
     *
     * @return true if valid, false otherwise
     */
    private boolean valideDetails() {

        firstName = firstNameEdtTxt.getText().toString().trim();
        lastName = lastNameEdtTxt.getText().toString().trim();
        mobileNumber = mobileEdtTxt.getText().toString().trim();
        email = emailEdtTxt.getText().toString().trim();
        city = cityEdtTxt.getText().toString().trim();
//      if(mCategoryList.get(i).getName())

        if(categoriesList.contains(categorySpn.getSelectedItem().toString())){
            int categoryid;
            categoryid = categoriesList.indexOf(categorySpn.getSelectedItem().toString());
            if(categoryid != 0){
                categoryID = mCategoryList.get(categoryid - 1).getId();
            }else {
                Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.choose_your_interest), getString(R.string.personal_close_button));
                return false;
            }

        }


        if (TextUtils.isEmpty(firstName)) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_first), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_last), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(mobileNumber)) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_mobile), getString(R.string.personal_close_button));
            return false;
        }

        if (TextUtils.isEmpty(city)) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_add_city), getString(R.string.personal_close_button));
            return false;
        }

        if (firstName.length() < 2) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_first_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (lastName.length() < 2) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_last_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (city.length() < 3) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_city_minimum), getString(R.string.personal_close_button));
            return false;
        }

        if (mobileNumber.length() != 10) {
            Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.personal_mobile_minimun), getString(R.string.personal_close_button));
            return false;
        }
        if (!TextUtils.isEmpty(email))
            if (isValideEmail() != SignUpActivity.validateErrorCodes.VALID_FIELD) {
                Helper.showAlertMessage(PersonalInformationContactActivity.this, getString(R.string.personal_dialog_title), getString(R.string.validate_email), getString(R.string.personal_close_button));
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

    /**
     * Checks if the entered email is in proper form
     *
     * @return code of validation
     */
    private int isValideEmail() {
        int ret = SignUpActivity.validateErrorCodes.NOT_VALID_EMAIL;
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return SignUpActivity.validateErrorCodes.VALID_FIELD;
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detect_city_img_btn:
                if(AskPermition.getInstance(PersonalInformationContactActivity.this).isLocationAllowed())
                initializeLocationService();
                break;
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
}