package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ChangePassword;
import com.edu.flinnt.core.ProfileGet;
import com.edu.flinnt.core.ProfileUpdate;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.gui.AddCommunicationActivity.FileType;
import com.edu.flinnt.gui.SignUpActivity.validateErrorCodes;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.ChangePasswordRequest;
import com.edu.flinnt.protocol.ChangePasswordResponse;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.protocol.Profile;
import com.edu.flinnt.protocol.ProfileResponse;
import com.edu.flinnt.protocol.ProfileUpdateRequest;
import com.edu.flinnt.protocol.ProfileUpdateResponse;
import com.edu.flinnt.protocol.ResendorVerifiedRequest;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.protocol.Validation;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.FlinntLocationCallBack;
import com.edu.flinnt.util.FusedLocationUtil;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyConfig;
import com.edu.flinnt.util.UploadMediaFile;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * GUI class to show/edit profile details
 */
public class ProfileActivity extends AppCompatActivity implements OnClickListener, FlinntLocationCallBack {

    private Toolbar toolbar;
    private ImageView profileImageview;
    private EditText changePasswordEdtTxt;
    private TextInputLayout firstNameEdtTxt, lastNameEdtTxt, instituteNameEdtTxt, cityEdtTxt, emailIDEdtTxt, mobileNumEdtTxt, genderEdtTxt, dobDateEdtTxt;
    private TextView userNameTxt, useremailTxt;
    private FloatingActionButton selectImage;

    final Calendar mCalendar = Calendar.getInstance();
    private Resources res = FlinntApplication.getContext().getResources();

    private String firstName, lastName, emailID, mobileNum, cityName = "", instituteName = "";
    private int dobYear = 0, dobMonth = 0, dobDay = 0;
    private Handler mHandler = null;
    private ImageLoader mImageLoader;
    private ProgressDialog mProgressDialog = null;
    private String profileImageUrl = "", profileImageName = "";

    private String MALE = "Male";
    private String FEMALE = "Female";
    private String genderStr = "";

    private ResendorVerifiedRequest mProfileGetRequest;
    private ProfileResponse mProfileResponse;

    private final int RESULT_FROM_STORAGE = 101;
    private final int RESULT_FROM_RECORDERS = 102;
    private final int RESULT_FROM_CROP_PIC = 103;

    private int postContentTypeMedia = Flinnt.POST_CONTENT_GALLERY; // Flinnt.INVALID;

    private File uploadFile;
    private String uploadFilePathString, attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;
    private Bitmap imageFile;
    private Bitmap photoThumb;
    private String link;

    private FileType fileType;
    private ResourceValidationResponse mResourceValidation;
    private Common mCommon;
    private String dobFormat = "dd/MM/yyyy"; //In which you need put here
    private FusedLocationUtil mFusedLocationUtil;
    private ImageButton mDetectLocationImgBtn;
    private PermissionUtil.PermissionRequestObject mALLPermissionRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.profile);
//@Nikhil 20062018
        AskPermition.getInstance(ProfileActivity.this).RequestAllPermission();
        toolbar = (Toolbar) findViewById(R.id.profile_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        TextView title = new TextView(this);
        title.setText(getString(R.string.edit_profile));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextSize(20);
        title.setSingleLine(true);
        //resorceTitleTxt.setEllipsize(TruncateAt.MARQUEE);
        //resorceTitleTxt.setMarqueeRepeatLimit(2);
        //resorceTitleTxt.setSelected(true);
        title.setTextAppearance(this, android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
        title.setTextColor(Color.WHITE);
        toolbar.addView(title);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            profileImageUrl = bundle.getString(LoginResponse.USER_PICTURE_URL_KEY);
            profileImageName = bundle.getString(LoginResponse.USER_PICTURE_KEY);
        }

        mImageLoader = Requester.getInstance().getImageLoader();

        userNameTxt = (TextView) findViewById(R.id.text_user_name);
        useremailTxt = (TextView) findViewById(R.id.text_user_email);

        userNameTxt.setText(Config.getStringValue(Config.FIRST_NAME) + " " + Config.getStringValue(Config.LAST_NAME));
        String userLogin = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID)).getUserLogin();
        if (userLogin != null)
            useremailTxt.setText(userLogin);

        profileImageview = (ImageView) findViewById(R.id.my_photo_profile);
        selectImage = (FloatingActionButton) findViewById(R.id.fab_take_image_profile);

        mDetectLocationImgBtn = (ImageButton) findViewById(R.id.detect_city_img_btn);
        mDetectLocationImgBtn.setOnClickListener(this);

        firstNameEdtTxt = (TextInputLayout) findViewById(R.id.first_name_text_input);
        lastNameEdtTxt = (TextInputLayout) findViewById(R.id.last_name_text_input);
        emailIDEdtTxt = (TextInputLayout) findViewById(R.id.email_id_text_input);
        mobileNumEdtTxt = (TextInputLayout) findViewById(R.id.mobile_number_text_input);
        instituteNameEdtTxt = (TextInputLayout) findViewById(R.id.institute_name_text_input);
        cityEdtTxt = (TextInputLayout) findViewById(R.id.city_text_input);
        dobDateEdtTxt = (TextInputLayout) findViewById(R.id.dob_text_input);
        genderEdtTxt = (TextInputLayout) findViewById(R.id.gender_text_input);
        genderEdtTxt.getEditText().setFocusable(false);
        genderEdtTxt.getEditText().setKeyListener(null);
        genderEdtTxt.getEditText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                creategenderSelectionDialog();
            }
        });
        dobDateEdtTxt.getEditText().setFocusable(false);
        dobDateEdtTxt.getEditText().setKeyListener(null);
        dobDateEdtTxt.getEditText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, datePicker,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        changePasswordEdtTxt = (EditText) findViewById(R.id.change_password_edit_text);
        changePasswordEdtTxt.setFocusable(false);
        changePasswordEdtTxt.setKeyListener(null);
        changePasswordEdtTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();
            }
        });
//        changePassword = (TextView) findViewById(R.id.change_password_title_profile);
//        changePassword.setOnClickListener(this);

        selectImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                openBottomSheet();
            }
        });
        profileImageview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        if (message.obj instanceof ProfileUpdateResponse) {
                            setIntent((ProfileUpdateResponse) message.obj);
                            Helper.showToast(getString(R.string.profile_update), Toast.LENGTH_LONG);
                        }
                        if (message.obj instanceof ChangePasswordResponse) {
                            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.change_password), getString(R.string.password_change_successfully), getString(R.string.close_txt));
                        } else if (message.obj instanceof ResourceValidationResponse) {
                            mResourceValidation = (ResourceValidationResponse) message.obj;
                        } else if (message.obj instanceof ProfileResponse) {
                            mProfileResponse = (ProfileResponse) message.obj;
                            fillData(mProfileResponse);
                        }

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());


                        if (message.obj instanceof ProfileUpdateResponse) {
                            if (((ProfileUpdateResponse) message.obj).errorResponse != null)
                                Helper.showAlertMessage(ProfileActivity.this, getString(R.string.error), ((ProfileUpdateResponse) message.obj).errorResponse.message, getString(R.string.close_txt));
                        }
                        if (message.obj instanceof ChangePasswordResponse) {
                            if (((ChangePasswordResponse) message.obj).errorResponse != null)
                                Helper.showAlertMessage(ProfileActivity.this, getString(R.string.error), ((ChangePasswordResponse) message.obj).errorResponse.message, getString(R.string.close_txt));
                        }

                        break;

                    case UploadMediaFile.UPLOAD_SUCCESS:

                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        sendRequest(message.obj.toString());

                        break;

                    case UploadMediaFile.UPLOAD_FAILURE:

                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    default:

                        super.handleMessage(message);
                }
            }
        };

        final String url = profileImageUrl + Flinnt.PROFILE_LARGE + File.separator + profileImageName;
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("profile image url : " + url);

        Bitmap profileBitmap = Helper.getBitmapFromSDcard(Helper.getFlinntUrlPath(url), profileImageName);
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("profile URL : " + url + " , profileBitmap : " + profileBitmap);
        if (profileBitmap == null) {
            mImageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("onResponse Bitmap : " + response.getBitmap());
                    if (response.getBitmap() != null) {
                        profileImageview.setImageBitmap(response.getBitmap());
                        Helper.saveBitmapToSDcard(response.getBitmap(), Helper.getFlinntUrlPath(url), profileImageName);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    profileImageview.setImageResource(R.drawable.default_user_profile_image);
                }
            });
        } else {
            profileImageview.setImageBitmap(profileBitmap);
        }

        mProfileGetRequest = new ResendorVerifiedRequest();
        new ProfileGet(mHandler, mProfileGetRequest, Config.getStringValue(Config.USER_ID)).sendProfileGetRequest();
        startProgressDialog();

        //mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_PROFILE_PICTURE);
        //if( null == mResourceValidation ) {
        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION_PROFILE_PICTURE);
        resourceValidation.sendResourceValidationRequest();
        //}

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
        dobDay = Integer.valueOf(profile.getBirthDay());
        dobMonth = Integer.valueOf(profile.getBirthMonth());
        dobYear = Integer.valueOf(profile.getBirthYear());
        cityName = profile.getCity();
        mobileNum = profile.getMobile();
        emailID = profile.getEmail();

        firstNameEdtTxt.getEditText().setText(firstName);
        lastNameEdtTxt.getEditText().setText(lastName);
        instituteNameEdtTxt.getEditText().setText(instituteName);

        //genderSpin.setSelection(genderStr.equalsIgnoreCase(MALE) ? 0 : 1);
        cityEdtTxt.getEditText().setText(cityName);
        emailIDEdtTxt.getEditText().setText(emailID);
        mobileNumEdtTxt.getEditText().setText(mobileNum);
        genderEdtTxt.getEditText().setText(genderStr);
        if (TextUtils.isEmpty(cityName)) {
            initializeLocationServiceAuto();
        }

        Log.d("Flinnt", "day : " + dobDay + " month : " + dobMonth + " year : " + dobYear);

        if (dobYear != 0 || dobDay != 0) {
            mCalendar.set(dobYear, dobMonth - 1, dobDay); // January starts from 0, not from 1
            SimpleDateFormat sdf = new SimpleDateFormat(dobFormat, Locale.US);
            dobDateEdtTxt.getEditText().setText(sdf.format(mCalendar.getTime()));
            Config.setStringValue(Config.DATE_OF_BIRTH, sdf.format(mCalendar.getTime()));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.PROFILE + "&user=" + Config.getStringValue(Config.USER_ID));
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



        if (null == mCommon) {
            mCommon = new Common(this);
        }
    }

    /**
     * Edit profile and verify email and mobile
     *
     * @param profileUpdateResponse
     */
    protected void setIntent(ProfileUpdateResponse profileUpdateResponse) {
        Intent intent = null;
        if (profileUpdateResponse.getUpdated() == Flinnt.TRUE) {

            Config.setStringValue(Config.FIRST_NAME, firstName);
            Config.setStringValue(Config.LAST_NAME, lastName);
            Config.setStringValue(Config.PROFILE_NAME, profileUpdateResponse.getUserPicture());
            Config.setStringValue(Config.PROFILE_URL, profileUpdateResponse.getUserPictureUrl());
            Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));

            User user = UserInterface.getInstance().getUserFromId(Config.getStringValue(Config.USER_ID));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUserPictureUrl(profileUpdateResponse.getUserPictureUrl());
            user.setUserPicture(profileUpdateResponse.getUserPicture());
            UserInterface.getInstance().insertOrUpdateUser(Config.getStringValue(Config.USER_ID), user);

            if (profileUpdateResponse.getVerifyEmail() == Flinnt.TRUE && profileUpdateResponse.getVerifyMobile() == Flinnt.TRUE) {
                intent = new Intent(ProfileActivity.this, ProfileVerifyMobileAndEmailActivity.class);
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.EMAIL_ID, profileUpdateResponse.getEmailID());
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.EMAIL_VERIFICATION_ID, profileUpdateResponse.getEmailVerificationID());
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.MOBILE_NUM, profileUpdateResponse.getMobileNo());
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.MOBILE_VERIFICATION_ID, profileUpdateResponse.getMobileVerificationID());
                //startActivity(intent);
                startActivityForResult(intent, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);
//				finish();
            } else if (profileUpdateResponse.getVerifyEmail() == Flinnt.TRUE) {
                intent = new Intent(ProfileActivity.this, ProfileVerifyEmailActivity.class);
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.EMAIL_ID, profileUpdateResponse.getEmailID());
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.EMAIL_VERIFICATION_ID, profileUpdateResponse.getEmailVerificationID());
                //startActivity(intent);
                startActivityForResult(intent, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);
                //finish();
            } else if (profileUpdateResponse.getVerifyMobile() == Flinnt.TRUE) {
                intent = new Intent(ProfileActivity.this, ProfileVerifyMobileActivity.class);
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.MOBILE_NUM, profileUpdateResponse.getMobileNo());
                intent.putExtra(ProfileVerifyMobileAndEmailActivity.MOBILE_VERIFICATION_ID, profileUpdateResponse.getMobileVerificationID());
                //startActivity(intent);
                startActivityForResult(intent, AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK);
//				finish();
            } else {
                //intent = new Intent(ProfileActivity.this, MyCoursesActivity.class);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }

			/*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
			finish();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); //onBackPressed();
                break;

            case R.id.action_done:

                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    if (valideDetails()) {
                        if (!TextUtils.isEmpty(lastAttachedImagePath) && fileType != FileType.link) {
                            //if(Helper.isConnected()){
                            new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                            {
                                mProgressDialog = Helper.getProgressDialog(ProfileActivity.this, "", getString(R.string.uploading_image), Helper.PROGRESS_DIALOG);
                                if (mProgressDialog != null) mProgressDialog.show();
                            }
                        /*}else{
							Helper.showNetworkAlertMessage(ProfileActivity.this);
						}*/

                        } else {
                            /** empty resourseId
                             * does not contain media file.*/
                            sendRequest("");
                        }
                    }
                }


                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Send request to update profile
     *
     * @param resourseID
     */
    private void sendRequest(String resourseID) {

        firstName = firstNameEdtTxt.getEditText().getText().toString().trim();
        lastName = lastNameEdtTxt.getEditText().getText().toString().trim();
        emailID = emailIDEdtTxt.getEditText().getText().toString().trim();
        mobileNum = mobileNumEdtTxt.getEditText().getText().toString().trim();
        cityName = cityEdtTxt.getEditText().getText().toString().trim();
        instituteName = instituteNameEdtTxt.getEditText().getText().toString().trim();
        genderStr = genderEdtTxt.getEditText().getText().toString().trim();
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setUserID(Config.getStringValue(Config.USER_ID));
        profileUpdateRequest.setFirstName(firstName);
        profileUpdateRequest.setLastName(lastName);
        profileUpdateRequest.setEmail(emailID);
        profileUpdateRequest.setMobileNo(mobileNum);
        profileUpdateRequest.setCity(cityName);
        profileUpdateRequest.setInstituteName(instituteName);
        profileUpdateRequest.setGender(genderStr);
        profileUpdateRequest.setDobDay(String.valueOf(dobDay));
        profileUpdateRequest.setDobMonth(String.valueOf(dobMonth));
        profileUpdateRequest.setDobYear(String.valueOf(dobYear));

        if (!TextUtils.isEmpty(resourseID)) {
            profileUpdateRequest.setResourseID(resourseID);
        }

        new ProfileUpdate(mHandler, profileUpdateRequest).sendProfileUpdateRequest();
        startProgressDialog();

    }

    /**
     * Checks the entered details
     *
     * @return true if valid, false otherwise
     */
    private boolean valideDetails() {

        firstName = firstNameEdtTxt.getEditText().getText().toString().trim();
        lastName = lastNameEdtTxt.getEditText().getText().toString().trim();
        instituteName = instituteNameEdtTxt.getEditText().getText().toString().trim();
        cityName = cityEdtTxt.getEditText().getText().toString().trim();
        emailID = emailIDEdtTxt.getEditText().getText().toString().trim();
        mobileNum = mobileNumEdtTxt.getEditText().getText().toString().trim();
        if (mProfileResponse != null) {
            if (mProfileResponse.getFirst_name() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(firstName)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_first), getString(R.string.close_txt));
                    return false;
                }
                if (firstName.length() < 2) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.minimum_first_name), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getLast_name() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(lastName)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_last), getString(R.string.close_txt));
                    return false;
                }
                if (lastName.length() < 2) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.minimum_last_name), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getInstitute_name() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(instituteName)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_institute), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getGender() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(genderStr)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_gender), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getBirth_day() == Flinnt.TRUE) {
                if (dobDay == Flinnt.FALSE || dobMonth == Flinnt.FALSE || dobYear == Flinnt.FALSE) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.date_birth), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getCity() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(cityName)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_city), getString(R.string.close_txt));
                    return false;
                }
                if (cityName.length() < 3) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.minimum_city), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getEmail() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(emailID)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.add_email), getString(R.string.close_txt));
                    return false;
                }
            }
            if (mProfileResponse.getMobile() == Flinnt.TRUE) {
                if (TextUtils.isEmpty(mobileNum)) {
                    Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.add_mobile), getString(R.string.close_txt));
                    return false;
                }
            }
        } else {
            return false;
        }
        if (isValideEmail() != validateErrorCodes.VALID_FIELD) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), res.getString(R.string.validate_email), getString(R.string.close_txt));
            return false;
        }
        if (isValideNumber() != validateErrorCodes.VALID_FIELD) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), res.getString(R.string.validate_mobile_number), getString(R.string.close_txt));
            return false;
        }

        return true;
    }

    /**
     * Check for empty fields
     */
    private void validateEmptyField() {

        if (TextUtils.isEmpty(firstName) && mProfileResponse.getFirst_name() == Flinnt.TRUE) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_first), getString(R.string.close_txt));
        } else if (TextUtils.isEmpty(lastName) && mProfileResponse.getLast_name() == Flinnt.TRUE) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.personal_add_last), getString(R.string.close_txt));
        } else if (TextUtils.isEmpty(emailID) && mProfileResponse.getEmail() == Flinnt.TRUE) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.add_email), getString(R.string.close_txt));
        } else if (TextUtils.isEmpty(mobileNum) && mProfileResponse.getMobile() == Flinnt.TRUE) {
            Helper.showAlertMessage(ProfileActivity.this, getString(R.string.profile), getString(R.string.add_mobile), getString(R.string.close_txt));
        }
    }

    /**
     * Checks if the entered email is in proper form
     *
     * @return code of validation
     */
    private int isValideEmail() {
        int ret = validateErrorCodes.NOT_VALID_EMAIL;
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailID).matches()) {
            return validateErrorCodes.VALID_FIELD;
        }
        return ret;
    }

    /**
     * Checks if entered number is valid or not
     *
     * @return code of validation
     */
    private int isValideNumber() {

        if (mobileNum.matches(SignUpActivity.REGEX_NUMBER) && mobileNum.length() == 10) {
            return validateErrorCodes.VALID_FIELD;
        } else {
            return validateErrorCodes.NOT_VALID_NUMBER;
        }
    }

    private void initializeLocationService() {
        if (mFusedLocationUtil != null) {
            mFusedLocationUtil.onPause();
            mFusedLocationUtil = null;
        }
        mFusedLocationUtil = new FusedLocationUtil(this, this);
        if (mFusedLocationUtil.isLocationFetchingAllowed()) {
            mFusedLocationUtil.setActivity(this);
            mFusedLocationUtil.startLocationUpdatesWithSettings();
        }
    }

    private void initializeLocationServiceAuto() {
        mFusedLocationUtil = new FusedLocationUtil(this, this);
        if (mFusedLocationUtil.isLocationFetchingAllowed()) {
            mFusedLocationUtil.setActivity(this);
            mFusedLocationUtil.startLocationUpdatesWithSettings();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder mGeoCoder = new Geocoder(this);
            List<Address> mAddresses = mGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (mAddresses.size() > 0 && mAddresses.get(0).getLocality() != null) {
                if (LogWriter.isApplicationDebug(this))
                    LogWriter.info("Address : " + mAddresses.get(0).toString());
                cityEdtTxt.getEditText().setText(mAddresses.get(0).getLocality());
                mDetectLocationImgBtn.setImageResource(R.drawable.my_location_enabled);
                mFusedLocationUtil.onPause();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.detect_city_img_btn:
                if (AskPermition.getInstance(ProfileActivity.this).isLocationAllowed()) {
                    initializeLocationService();
                }
                break;
        }
    }


    /**
     * Dialog to change password
     */
    private void changePasswordDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        // set view
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_password, null);
        alertDialogBuilder.setView(dialogView);

        // set resorceTitleTxt
        //alertDialogBuilder.setTitle("Change Password");
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Change Password");
        //resorceTitleTxt.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.ColorPrimary));
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(title);


        alertDialogBuilder.setCancelable(false);

        // set the custom dialog components - text, image and button
        final EditText currentPassword = (EditText) dialogView.findViewById(R.id.current_pass_changepass);
        final EditText newPassword = (EditText) dialogView.findViewById(R.id.new_pass_changepass);
        final EditText confirmPassword = (EditText) dialogView.findViewById(R.id.confirm_pass_changepass);

        currentPassword.setTypeface(Typeface.DEFAULT);
        newPassword.setTypeface(Typeface.DEFAULT);
        confirmPassword.setTypeface(Typeface.DEFAULT);

        alertDialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });
        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });

        try {
            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentPasswordStr = currentPassword.getText().toString();
                    String newPasswordStr = newPassword.getText().toString();
                    String confirmPasswordStr = confirmPassword.getText().toString();

				/*if(TextUtils.isEmpty(currentPasswordStr) || TextUtils.isEmpty(newPasswordStr)
						|| TextUtils.isEmpty(confirmPasswordStr)){
					Helper.showAlertMessage(ProfileActivity.this, "Change Password", getResources().getString(R.string.required_all_field), getString(R.string.close_txt));
				}*/


                    if (TextUtils.isEmpty(currentPasswordStr)) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Add current password", getString(R.string.close_txt));
                    } else if (TextUtils.isEmpty(newPasswordStr)) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Add new password", getString(R.string.close_txt));
                    } else if (TextUtils.isEmpty(confirmPasswordStr)) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Confirm new password", getString(R.string.close_txt));
                    } else if (newPasswordStr.length() < 6) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Use 6 character or more for password", getString(R.string.close_txt));
                    } else if (newPasswordStr.length() > 15) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Password must be 15 or less character", getString(R.string.close_txt));
                    } else if (currentPasswordStr.equals(newPasswordStr)) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Add different current and new password", getString(R.string.close_txt));
                    } else if (!confirmPasswordStr.equals(newPasswordStr)) {
                        Helper.showAlertMessage(ProfileActivity.this, "Change Password", "Use same new and confirm password", getString(R.string.close_txt));
                    } else {

                        if (Helper.isConnected()) {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
                            changePasswordRequest.setUserID(Config.getStringValue(Config.USER_ID));
                            changePasswordRequest.setCurrentPassword(currentPasswordStr);
                            changePasswordRequest.setNewPassword(newPasswordStr);

                            new ChangePassword(mHandler, changePasswordRequest).sendChangePasswordRequest();
                            startProgressDialog();

                        } else {
                            Helper.showNetworkAlertMessage(ProfileActivity.this);
                        }

                    }
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dobYear = year;
            dobMonth = (monthOfYear + 1);
            dobDay = dayOfMonth;

            SimpleDateFormat sdf = new SimpleDateFormat(dobFormat, Locale.US);
            dobDateEdtTxt.getEditText().setText(sdf.format(mCalendar.getTime()));
        }
    };

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ProfileActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ProfileActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(ProfileActivity.this))
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
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {

        new BottomSheet.Builder(ProfileActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title("Attachment")
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {


                        if (AskPermition.getInstance(ProfileActivity.this).isPermitted()) {
                            switch (position) {

                                case R.id.attach_bottom_album:
                                    //toast("Album");
                                    fileType = FileType.image;
                                    chooseFromStorage(fileType.name());
                                    break;

                                case R.id.attach_bottom_photo:
                                    //toast("Photo");
                                    fileType = FileType.image;
                                    captureFromRecorders();
                                    break;

                                default:
                            }
                        }
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("fileType : " + fileType);
                    }
                })
                .remove(R.id.attach_bottom_audio)
                .remove(R.id.attach_bottom_link)
                .remove(R.id.attach_bottom_video)
                .remove(R.id.attach_bottom_pdf)
                .show();

    }


    /**
     * Intent to pick the file from device's internal storage of SDCard
     *
     * @param type media file type
     */
    private void chooseFromStorage(String type) {

        Intent storageIntent;

        switch (fileType) {
            case image:
                storageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                storageIntent.setType(type + "/*");
			/*
					uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_GALLERY);
					uploadFilePathString = uploadFile.getAbsolutePath();
					uploadFileUri = Uri.fromFile(uploadFile);
					storageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
			 */
                if (storageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Intent to capture media from camera
     */
    private void captureFromRecorders() {

        Intent captureIntent;
        long fileSize;

        switch (fileType) {
            case image:
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uploadFile = Helper.getCropOutputFile();
                if (uploadFile != null) {
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
                    }
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Image file path : " + uploadFilePathString);
                }

                break;

            default:
                //toast("File type : " + fileType.name());
                break;
        }
    }

    //		Intent Results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : " + fileType + "\nData Uri : " + data);

        if (requestCode == FusedLocationUtil.REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mFusedLocationUtil.onResume();

            }
            return;
        }


        Uri contentUri;
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == AddCommunicationActivity.EDIT_SUCCESSFULL_CALL_BACK) {
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else if (requestCode == RESULT_FROM_STORAGE) {

                    switch (fileType) {

                        case image:
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            //imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            if (isValidFile(uploadFilePathString, FileType.image)) {
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Valid Image File");

                                Validation validation = getValidation(FileType.image);
                                String aspectRatio = String.format("%.2f", Double.parseDouble(validation.getAspectRatio()));
                                //String aspectRatio = String.format("%.2f", 1.77);
                                int aspectValueY = 100;
                                int aspectValueX = (int) (Double.parseDouble(aspectRatio) * aspectValueY);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("aspect ratio : " + aspectRatio + "\nX : " + aspectValueX + "\nY : " + aspectValueY);

                                uploadFile = Helper.getCropOutputFile();
                                if (uploadFile != null) {
                                    uploadFilePathString = uploadFile.getAbsolutePath();
                                    uploadFileUri = Uri.fromFile(uploadFile);

                                    Crop.of(contentUri, Uri.fromFile(new File(uploadFilePathString))).withAspect(aspectValueX, aspectValueY).start(this);

                                }
                            }
                            break;

                        default:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("Default ! " + photoThumb);
                            break;
                    }
                } else if (requestCode == RESULT_FROM_RECORDERS) {

                    switch (fileType) {

                        case image:
                            if (isValidFile(uploadFilePathString, FileType.image)) {

//							performCrop(Uri.fromFile(new File(uploadFilePathString)));

                                Validation validation = getValidation(FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                String aspectRatio = String.format("%.2f", Double.parseDouble(validation.getAspectRatio()));
                                //String aspectRatio = String.format("%.2f", 1.77);
                                int aspectValueY = 100;
                                int aspectValueX = (int) (Double.parseDouble(aspectRatio) * aspectValueY);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("aspect ratio : " + aspectRatio + "\nX : " + aspectValueX + "\nY : " + aspectValueY);

                                Crop.of(Uri.fromFile(new File(uploadFilePathString)), Uri.fromFile(new File(uploadFilePathString))).withAspect(aspectValueX, aspectValueY).start(this);

                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("Valid Image File");
                            }
                            break;

                        default:
                            break;
                    }
                } else if (requestCode == RESULT_FROM_CROP_PIC) {
                    if (LogWriter.isValidLevel(Log.DEBUG))
                        LogWriter.write("CROP_PIC data : " + data);

                    if (null != data) {
                        // get the returned data
                        Bundle extras = data.getExtras();
                        if (LogWriter.isValidLevel(Log.DEBUG))
                            LogWriter.write("CROP_PIC extras : " + extras);

                        Validation validation = getValidation(FileType.image);
                        uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 1;

                        {
                            Bitmap selectedImage = BitmapFactory.decodeFile(uploadFilePathString);
                            profileImageview.setImageBitmap(selectedImage);

                        }
                    }
                } else if (requestCode == Crop.REQUEST_CROP) {

                    Validation validation = getValidation(FileType.image);
                    uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 1;
                    {
                        Bitmap selectedImage = BitmapFactory.decodeFile(uploadFilePathString);
                        profileImageview.setImageBitmap(selectedImage);
                    }
                    lastAttachedImagePath = uploadFilePathString;
                }
            } else {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("You didn't pick any file ");
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Gets the type of attached media
     *
     * @return filetype code number
     */
    public int getPostContentTypeMedia() {
        return postContentTypeMedia;
    }

    /**
     * Sets the type of attached media
     *
     * @param postContentTypeMedia filetype code number
     */
    public void setPostContentTypeMedia(int postContentTypeMedia) {
        this.postContentTypeMedia = postContentTypeMedia;
    }

    /**
     * To get validation parameters
     *
     * @param type filetype
     * @return validation parameters of filetype
     */
    public Validation getValidation(FileType type) {
        Validation validation = null;
        if (null == mResourceValidation) {
            mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_PROFILE_PICTURE);
        }
        if (null != mResourceValidation) {
            switch (type) {
                case image:
                    validation = mResourceValidation.getImage();
                    break;

                default:
                    break;
            }
        }
        //if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("validation :: " + validation);
        return validation;
    }

    /**
     * Checks if the file is valid or not
     *
     * @param filePath selected file's path on storage
     * @param type     file type
     * @return true if valid, false otherwise
     */
    private boolean isValidFile(String filePath, FileType type) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("filePath :: " + filePath + ", FileType : " + type);
        boolean ret = true;
        Validation validation = getValidation(type);
        if (null != validation) {
            File file = new File(filePath);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("validation :: " + validation.toString());
            long length = file.length();
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File length : " + length);
            if (length >= validation.getMaxFileSizeLong()) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, getString(R.string.error), "Use 5 MB or fewer size image", getString(R.string.close_txt));
                return false;
            }

            switch (type) {
                case image:
                    ret = validateImage(file.getPath(), validation);
                    break;

                default:
                    break;
            }
        }
        return ret;
    }


    /**
     * Checks if attached file is valid to be uploaded or not
     *
     * @param ImagePath  file path on storage
     * @param validation validation parameter
     * @return true if valid, false otherwise
     */
    private boolean validateImage(String ImagePath, Validation validation) {

        uploadFileUri = Uri.parse(ImagePath);
        uploadFilePathString = ImagePath;
        String uploadOrigianlFilePath = ImagePath;

        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger images
            // UploadFileName=fileUri.getPath();
            //Flile lenth > 1MB ...then compress...
            if (new File(ImagePath).length() > (1 * 1024 * 1024)) {
                bitmapOptions = setBitmapFactoryOptions(bitmapOptions);
            } else {
                bitmapOptions.inSampleSize = 1;
            }

            final Bitmap originalBitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);

            if (originalBitmap == null) {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, getString(R.string.error), AddCommunicationActivity.wrongSizedFileMessage(FileType.image), getString(R.string.close_txt));
            }

            if (LogWriter.isValidLevel(Log.ERROR))
                LogWriter.write("originalBitmap : Height : " + originalBitmap.getHeight()
                        + ", Width : " + originalBitmap.getWidth());
            if (originalBitmap.getHeight() < validation.getMinHeight()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min300));
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File height is smaller then Minimum Height.");
                Helper.showAlertMessage(this, getString(R.string.error), "Add min. 50 x 50 px image", getString(R.string.close_txt));
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min200));
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, getString(R.string.error), "Add min. 50 x 50 px image", getString(R.string.close_txt));
                return false;
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return true;
    }

    private BitmapFactory.Options setBitmapFactoryOptions(BitmapFactory.Options bitmapOptions) {
        bitmapOptions.inScaled = false;
        bitmapOptions.inDither = false;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOptions.inSampleSize = 2;
        //bitmapOptions.inSampleSize = 1;
        return bitmapOptions;
    }

    public void creategenderSelectionDialog() {
        final String[] gender = {"Male", "Female"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_gender))
                .setCancelable(true)
                .setItems(gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        genderEdtTxt.getEditText().setText(gender[which]);
                    }
                });
        builder.create();
        builder.show();
    }

}
