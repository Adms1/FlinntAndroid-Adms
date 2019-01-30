package com.edu.flinnt.gui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.CommunicationItemAdapter;
import com.edu.flinnt.core.AddPoll;
import com.edu.flinnt.core.EditPoll;
import com.edu.flinnt.core.ErrorCodes;
import com.edu.flinnt.core.RepostPoll;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.AddPollRequest;
import com.edu.flinnt.protocol.AddPollResponse;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.EditPollRequest;
import com.edu.flinnt.protocol.PollListResponse;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.RepostPollRequest;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.Validation;
import com.edu.flinnt.util.AttachmentValidation;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.CommunicationItem;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.SelectableRoundedImageView;
import com.edu.flinnt.util.UploadMediaFile;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddPollActivity extends AppCompatActivity implements View.OnClickListener, CommunicationItemAdapter.ItemListener {
    private final int COURSE_INFO_CALL_BACK = 99;
    private final int RESULT_FROM_STORAGE = 101;
    private final int SCHEDULE_CALL_BACK = 105;
    private final long MINIMUM_POLL_DURATION = 60 * 60 * 1000;
    private final long THRESHOLD_DURATION = 59 * 60 * 1000;
    private final long DEFAULT_POLL_DURATION = 24 * 60 * 60 * 1000;
    private LinearLayout mAttachemntListLinear, mAttachPhotosLinearLayout, mCourseTitleLinear, mAttachmentIconLinear;
    private EditText mAskAQuestionEdt, mOptionAEdt, mOptionBEdt, mOptionCEdt, mOptionDEdt;
    private ImageView mAttachmentImg, mScheduleImg, mSelectCourseImg;
    private Toolbar mToolbar;
    private int mPostStat = Flinnt.POST_POLL_ADD;
    private ProgressDialog mProgressDialog = null;
    private RecyclerView mAttachmentRecycler;
    private CommunicationItemAdapter mPollAttachmentAdapter;
    private ArrayList<CommunicationItem> mAttachmementItemsList = new ArrayList<>();
    private int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
            mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;
    private String uploadFilePathString, mAttachmentURL, mAttachmentName;
    private String comeFromActivity = "";
    private String mCourseID = "";
    private Calendar mCalendar = Calendar.getInstance();
    private LinearLayout mScheduleDateRelative, mScheduleTimeRelative;
    private TextView mScheduleDateTxt, mScheduleTimeTxt;
    private String changeCourseId = "", changeCourseName = "";

    //String from Intent
    private String mCourseNameStr, mQuestionDescription, mPollId, mRepostCourseId;
    private TextView mCourseNameTxt;
    private Date mPollEndDate = Calendar.getInstance().getTime(), mScheduleDate = null;
    private String mPublishStats = Flinnt.PUBLISH_NOW;
    private PollListResponse mPollOptions = null;
    private boolean isResourceChanged = false;
    private View mEmpatyView;
    Unregistrar mUnregistrar;
    private int mPosition = -1;
    private PermissionUtil.PermissionRequestObject mALLPermissionRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_add_poll);
        getData();
        setUpToolBar();
        initUI();
        sendRequestForResourceValidation();
        setEndDate();

        //@Nikhil 20062018
        AskPermition.getInstance(AddPollActivity.this).RequestAllPermission();

    }

    private void setEndDate() {
        if (mPostStat == Flinnt.POST_POLL_ADD || mPostStat == Flinnt.POST_POLL_REPOST) {
            mPollEndDate = Calendar.getInstance().getTime();
            mPollEndDate.setTime(System.currentTimeMillis() + DEFAULT_POLL_DURATION);
        }
        mScheduleDateTxt.setText(getDateInString(mPollEndDate));
        mScheduleTimeTxt.setText(getTimeInString(mPollEndDate));

    }

    private void sendRequestForResourceValidation() {
        ResourceValidation resourceValidation = new ResourceValidation(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        break;
                }
                return false;
            }
        }), ResourceValidation.RESOURCE_VALIDATION);
        resourceValidation.sendResourceValidationRequest();
        startProgressDialog();
    }

    private void setUpToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.add_poll));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mPostStat == Flinnt.POST_POLL_EDIT) {
            getSupportActionBar().setTitle(getString(R.string.edit_poll));
        }
        if (mPostStat == Flinnt.POST_POLL_REPOST) {
            getSupportActionBar().setTitle(getString(R.string.repost_poll));
        }
    }

    private void setViewSize(View view) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int viewHeight = metrics.heightPixels / 3;
        mEmpatyView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight + 50));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnregistrar.unregister();
    }

    private void initUI() {
        mAskAQuestionEdt = (EditText) findViewById(R.id.ask_a_question_edt);
        mOptionAEdt = (EditText) findViewById(R.id.option_a_edt);
        mOptionBEdt = (EditText) findViewById(R.id.option_b_edt);
        mOptionCEdt = (EditText) findViewById(R.id.option_c_edt);
        mOptionDEdt = (EditText) findViewById(R.id.option_d_edt);

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    mEmpatyView.setVisibility(View.VISIBLE);
                } else {
                    mEmpatyView.setVisibility(View.GONE);
                }
            }
        });

        mEmpatyView = (View) findViewById(R.id.empty_view);
        mAttachmentImg = (ImageView) findViewById(R.id.attachment_img);
        mScheduleImg = (ImageView) findViewById(R.id.schedule_img);

        mAttachmentIconLinear = (LinearLayout) findViewById(R.id.attachment_icon_linear);
        mAttachemntListLinear = (LinearLayout) findViewById(R.id.attachment_list_linear);
        mCourseTitleLinear = (LinearLayout) findViewById(R.id.course_title_linear);
        mAttachPhotosLinearLayout = (LinearLayout) findViewById(R.id.attach_file_linear);
        mAttachPhotosLinearLayout.removeAllViews();
        mAttachmentIconLinear.setOnClickListener(this);
        mAttachemntListLinear.setVisibility(View.VISIBLE);
        mAttachmentRecycler = (RecyclerView) findViewById(R.id.attachment_recycler);
        mAttachmentRecycler.setHasFixedSize(true);
        mAttachmentRecycler.setLayoutManager(new LinearLayoutManager(this));
        mPollAttachmentAdapter = new CommunicationItemAdapter(mAttachmementItemsList, this);
        mAttachmentRecycler.setAdapter(mPollAttachmentAdapter);
        createItems();

        mScheduleDateRelative = (LinearLayout) findViewById(R.id.schedule_date_relative);
        mScheduleTimeRelative = (LinearLayout) findViewById(R.id.schedule_time_relative);
        mScheduleDateRelative.setOnClickListener(this);
        mScheduleTimeRelative.setOnClickListener(this);
        mScheduleDateTxt = (TextView) findViewById(R.id.schedule_date_text);
        mScheduleTimeTxt = (TextView) findViewById(R.id.schedule_time_text);

        mCourseTitleLinear = (LinearLayout) findViewById(R.id.course_title_linear);
        mCourseTitleLinear.setOnClickListener(this);
        mCourseNameTxt = (TextView) findViewById(R.id.course_name_txt);
        mSelectCourseImg = (ImageView) findViewById(R.id.course_edit_img);
        if (TextUtils.isEmpty(mCourseNameStr)) {
            mSelectCourseImg.setBackgroundResource(R.drawable.ic_course_select);
        } else {
            mCourseNameTxt.setText(mCourseNameStr);
            mSelectCourseImg.setBackgroundResource(R.drawable.ic_course_edit);
        }

        if (mPostStat == Flinnt.POST_POLL_EDIT) {
            mCourseTitleLinear.setEnabled(false);
            mSelectCourseImg.setVisibility(View.GONE);
        }

        if (mPostStat == Flinnt.POST_POLL_REPOST || mPostStat == Flinnt.POST_POLL_EDIT) {
            mCourseNameTxt.setText(mCourseNameStr);
            mAskAQuestionEdt.setText(mQuestionDescription);

            if (mPollOptions != null) {
                if (mPollOptions.getData().getOptions().size() > 0) {
                    mOptionAEdt.setText(mPollOptions.getData().getOptions().get(0).getText());
                    mOptionAEdt.setTag(mPollOptions.getData().getOptions().get(0).getId());
                } else {
                    if (mPostStat == Flinnt.POST_POLL_EDIT)
                        mOptionAEdt.setVisibility(View.GONE);
                }
                if (mPollOptions.getData().getOptions().size() > 1) {
                    mOptionBEdt.setText(mPollOptions.getData().getOptions().get(1).getText());
                    mOptionBEdt.setTag(mPollOptions.getData().getOptions().get(1).getId());
                } else {
                    if (mPostStat == Flinnt.POST_POLL_EDIT)
                        mOptionBEdt.setVisibility(View.GONE);
                }
                if (mPollOptions.getData().getOptions().size() > 2) {
                    mOptionCEdt.setText(mPollOptions.getData().getOptions().get(2).getText());
                    mOptionCEdt.setTag(mPollOptions.getData().getOptions().get(2).getId());
                } else {
                    if (mPostStat == Flinnt.POST_POLL_EDIT)
                        mOptionCEdt.setVisibility(View.GONE);
                }
                if (mPollOptions.getData().getOptions().size() > 3) {
                    mOptionDEdt.setText(mPollOptions.getData().getOptions().get(3).getText());
                    mOptionDEdt.setTag(mPollOptions.getData().getOptions().get(3).getId());
                } else {
                    if (mPostStat == Flinnt.POST_POLL_EDIT)
                        mOptionDEdt.setVisibility(View.GONE);
                }
            } else {
                if (mPostStat == Flinnt.POST_POLL_EDIT)
                    mOptionAEdt.setVisibility(View.GONE);
            }


            if (mAttachmentName == null || TextUtils.isEmpty(mAttachmentName)) {
                return;
            }

            File file = new File(Helper.getFlinntImagePath() + File.separator + mAttachmentName);
            if (file.exists()) {
                uploadFilePathString = file.getAbsolutePath();
                AttachmentValidation attachmentValidation = new AttachmentValidation(this);
                Validation validation = attachmentValidation.getValidation(AttachmentValidation.FileType.image);
                uploadFilePathString = new Common(this).compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 1;
                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                            + ", Height : " + uploadBitmap.getHeight());
                if (uploadBitmap == null) {
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write("Invalid file");
                    Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.image), "CLOSE");
                    return;
                } else {
                    mAttachPhotosLinearLayout.removeAllViews();
                    addAttachedPhotoView(uploadBitmap, uploadFilePathString);
                    doShowAttachmentMenu(false);
                }
            } else {
                mAttachPhotosLinearLayout.removeAllViews();
                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_album), mAttachmentName);
                doShowAttachmentMenu(false);
            }
        }
    }

    private AddPollRequest getValidAddPollRequest() {
        AddPollRequest mAddPollRequest = new AddPollRequest();
        if (TextUtils.isEmpty(mCourseID)) {
            Helper.showAlertMessage(this, "Add Poll", "Select Course", "CLOSE");
            return null;
        } else {
            mAddPollRequest.setCourse_id(mCourseID);
        }

        if (mPublishStats.equals(Flinnt.PUBLISH_NOW) && mPollEndDate.getTime() < (System.currentTimeMillis() + THRESHOLD_DURATION)) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }


        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE) && (mPollEndDate.getTime() - mScheduleDate.getTime()) < MINIMUM_POLL_DURATION) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }

        if (TextUtils.isEmpty(mAskAQuestionEdt.getText().toString().trim())) {
            Helper.showAlertMessage(this, "Add Poll", "Add a question", "CLOSE");
            return null;
        } else {
            mAddPollRequest.setQuestion(mAskAQuestionEdt.getText().toString().trim());
        }
        mAddPollRequest.getOptions().clear();
        if (!TextUtils.isEmpty(mOptionAEdt.getText().toString().trim())) {
            mAddPollRequest.getOptions().add(mOptionAEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionBEdt.getText().toString().trim())) {
            mAddPollRequest.getOptions().add(mOptionBEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionCEdt.getText().toString().trim())) {
            mAddPollRequest.getOptions().add(mOptionCEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionDEdt.getText().toString().trim())) {
            mAddPollRequest.getOptions().add(mOptionDEdt.getText().toString().trim());
        }

        if (mAddPollRequest.getOptions().size() < 2) {
            //Show options required message
            Helper.showAlertMessage(this, "Add Poll", "Add at least two options", "CLOSE");
            return null;
        }

        mAddPollRequest.setPoll_end_year(1900 + mPollEndDate.getYear());
        mAddPollRequest.setPoll_end_month(mPollEndDate.getMonth() + 1);
        mAddPollRequest.setPoll_end_day(mPollEndDate.getDate());
        mAddPollRequest.setPoll_end_hour(mPollEndDate.getHours());
        mAddPollRequest.setPoll_end_minute(mPollEndDate.getMinutes());


        if (mScheduleYear != Flinnt.INVALID)
            mAddPollRequest.setPub_year(mScheduleYear);
        if (mScheduleMonth != Flinnt.INVALID)
            mAddPollRequest.setPub_month(mScheduleMonth);
        if (mScheduleDay != Flinnt.INVALID)
            mAddPollRequest.setPub_day(mScheduleDay);
        if (mScheduleHour != Flinnt.INVALID)
            mAddPollRequest.setPub_hour(mScheduleHour);
        if (mScheduleMinute != Flinnt.INVALID)
            mAddPollRequest.setPub_minute(mScheduleMinute);

        mAddPollRequest.setUser_id(Config.getStringValue(Config.USER_ID));
        return mAddPollRequest;
    }

    private RepostPollRequest getValidRepostPollRequest() {
        RepostPollRequest mRepostPollRequest = new RepostPollRequest();
        if (TextUtils.isEmpty(mCourseID)) {
            Helper.showAlertMessage(this, "Repost Poll", "Select Course", "CLOSE");
            return null;
        } else {
            mRepostPollRequest.setCourse_id(mCourseID);
        }

        if (TextUtils.isEmpty(mRepostCourseId)) {
            Helper.showAlertMessage(this, "Repost Poll", "Select Course", "CLOSE");
            return null;
        } else {
            mRepostPollRequest.setRepost_course(mRepostCourseId);
        }

        mRepostPollRequest.setPost_id(mPollId);

        if (mPublishStats.equals(Flinnt.PUBLISH_NOW) && mPollEndDate.getTime() < (System.currentTimeMillis() + THRESHOLD_DURATION)) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }


        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE) && (mPollEndDate.getTime() - mScheduleDate.getTime()) < MINIMUM_POLL_DURATION) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }

        if (TextUtils.isEmpty(mAskAQuestionEdt.getText().toString().trim())) {
            Helper.showAlertMessage(this, "Add Poll", "Add a question", "CLOSE");
            return null;
        } else {
            mRepostPollRequest.setQuestion(mAskAQuestionEdt.getText().toString().trim());
        }
        mRepostPollRequest.getOptions().clear();
        if (!TextUtils.isEmpty(mOptionAEdt.getText().toString().trim())) {
            mRepostPollRequest.getOptions().add(mOptionAEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionBEdt.getText().toString().trim())) {
            mRepostPollRequest.getOptions().add(mOptionBEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionCEdt.getText().toString().trim())) {
            mRepostPollRequest.getOptions().add(mOptionCEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionDEdt.getText().toString().trim())) {
            mRepostPollRequest.getOptions().add(mOptionDEdt.getText().toString().trim());
        }

        if (mRepostPollRequest.getOptions().size() < 2) {
            //Show options required message
            Helper.showAlertMessage(this, "Add Poll", "Add at least two options", "CLOSE");
            return null;
        }

        mRepostPollRequest.setPoll_end_year(1900 + mPollEndDate.getYear());
        mRepostPollRequest.setPoll_end_month(mPollEndDate.getMonth() + 1);
        mRepostPollRequest.setPoll_end_day(mPollEndDate.getDate());
        mRepostPollRequest.setPoll_end_hour(mPollEndDate.getHours());
        mRepostPollRequest.setPoll_end_minute(mPollEndDate.getMinutes());


        if (mScheduleYear != Flinnt.INVALID)
            mRepostPollRequest.setPub_year(mScheduleYear);
        if (mScheduleMonth != Flinnt.INVALID)
            mRepostPollRequest.setPub_month(mScheduleMonth);
        if (mScheduleDay != Flinnt.INVALID)
            mRepostPollRequest.setPub_day(mScheduleDay);
        if (mScheduleHour != Flinnt.INVALID)
            mRepostPollRequest.setPub_hour(mScheduleHour);
        if (mScheduleMinute != Flinnt.INVALID)
            mRepostPollRequest.setPub_minute(mScheduleMinute);

        mRepostPollRequest.setUser_id(Config.getStringValue(Config.USER_ID));
        mRepostPollRequest.setPost_id(mPollId);
        return mRepostPollRequest;
    }

    public void createItems() {
        mAttachmementItemsList.add(new CommunicationItem(R.drawable.add_poll_image, getResources().getString(R.string.bottom_add_image)));
        if (mPostStat != Flinnt.POST_POLL_EDIT)
            mAttachmementItemsList.add(new CommunicationItem(R.drawable.post_schedule, getResources().getString(R.string.bottom_schedule)));
        mAttachmentImg.setVisibility(View.VISIBLE);
        if (mPostStat != Flinnt.POST_POLL_EDIT)
            mScheduleImg.setVisibility(View.VISIBLE);
        else
            mScheduleImg.setVisibility(View.GONE);
        mPollAttachmentAdapter.notifyDataSetChanged();
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
        getMenuInflater().inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_done:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                    break;
                }
                if (mPostStat == Flinnt.POST_POLL_EDIT) {
                    editPollOnServer();
                    break;
                }
                if (mPostStat == Flinnt.POST_POLL_REPOST) {
                    repostPollOnServer();
                    break;
                }
                addPollOnServer();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void repostPollOnServer() {
        Helper.hideKeyboardFromWindow(this);
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }
        final RepostPollRequest mRepostPollRequest = getValidRepostPollRequest();
        if (mRepostPollRequest == null) {
            return;
        }
        if (!TextUtils.isEmpty(uploadFilePathString) && isResourceChanged) {
            new UploadMediaFile(this, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.what) {
                        case UploadMediaFile.UPLOAD_SUCCESS:
                            stopProgressDialog();
                            mRepostPollRequest.setResource_id(Long.parseLong(message.obj.toString()));
                            mRepostPollRequest.setResource_changed(1);
                            mRepostPollRequest.setPost_content_type(Flinnt.POST_CONTENT_GALLERY);
                            sendRepostPollRequest(mRepostPollRequest);
                            break;
                        case UploadMediaFile.UPLOAD_FAILURE:
                            stopProgressDialog();
                            Helper.showAlertMessage(AddPollActivity.this, "Failure", "Unable add attachment", "CLOSE");
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());
                            break;
                    }
                    return false;
                }
            }), uploadFilePathString, Flinnt.POST_CONTENT_GALLERY).execute();
            mProgressDialog = Helper.getProgressDialog(this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
            if (mProgressDialog != null) mProgressDialog.show();
        } else {
            if (!TextUtils.isEmpty(mAttachmentName) && isResourceChanged)
                mRepostPollRequest.setResource_changed(1);
            sendRepostPollRequest(mRepostPollRequest);
        }
    }

    private void sendRepostPollRequest(RepostPollRequest mRepostPollRequest) {
        startProgressDialog();
        RepostPoll mRepostPoll = new RepostPoll(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        Helper.showToast("Poll has been reposted successfully", Toast.LENGTH_LONG);
                        Intent intent = new Intent();
                        intent.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_POLL_REPOST);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        handleError(message);
                        break;
                }
                return false;
            }
        }), mRepostPollRequest);
        mRepostPoll.sendRepostPollRequest();
    }

    private void editPollOnServer() {
        Helper.hideKeyboardFromWindow(this);
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }
        final EditPollRequest mEditPollRequest = getValidEditPollRequest();
        if (mEditPollRequest == null) {
            return;
        }
        if (!TextUtils.isEmpty(uploadFilePathString) && isResourceChanged) {
            new UploadMediaFile(this, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.what) {
                        case UploadMediaFile.UPLOAD_SUCCESS:
                            stopProgressDialog();
                            mEditPollRequest.setResource_id(Long.parseLong(message.obj.toString()));
                            mEditPollRequest.setResource_changed(1);
                            mEditPollRequest.setPost_content_type(Flinnt.POST_CONTENT_GALLERY);
                            sendEditPollRequest(mEditPollRequest);
                            break;
                        case UploadMediaFile.UPLOAD_FAILURE:
                            stopProgressDialog();
                            Helper.showAlertMessage(AddPollActivity.this, "Failure", "Unable add attachment", "CLOSE");
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());
                            break;
                    }
                    return false;
                }
            }), uploadFilePathString, Flinnt.POST_CONTENT_GALLERY).execute();
            mProgressDialog = Helper.getProgressDialog(this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
            if (mProgressDialog != null) mProgressDialog.show();
        } else {
            if (!TextUtils.isEmpty(mAttachmentName) && isResourceChanged)
                mEditPollRequest.setResource_changed(1);
            sendEditPollRequest(mEditPollRequest);
        }
    }

    private EditPollRequest getValidEditPollRequest() {
        EditPollRequest mEditPollRequest = new EditPollRequest();
        if (TextUtils.isEmpty(mCourseID)) {
            Helper.showAlertMessage(this, "Add Poll", "Select Course", "CLOSE");
            return null;
        } else {
            mEditPollRequest.setCourse_id(mCourseID);
        }
        mEditPollRequest.setPost_id(mPollId);
        Log.i("Time", mPollEndDate.getTime() + "  " + (System.currentTimeMillis() + MINIMUM_POLL_DURATION));
        if (mPublishStats.equals(Flinnt.PUBLISH_NOW) && mPollEndDate.getTime() < (System.currentTimeMillis() + THRESHOLD_DURATION)) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }


        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE) && (mPollEndDate.getTime() - mScheduleDate.getTime()) < MINIMUM_POLL_DURATION) {
            Helper.showAlertMessage(this, "Poll ends on", "Poll end time must be higher than one hour.", "CLOSE");
            return null;
        }

        if (TextUtils.isEmpty(mAskAQuestionEdt.getText().toString().trim())) {
            Helper.showAlertMessage(this, "Add Poll", "Add a question", "CLOSE");
            return null;
        } else {
            mEditPollRequest.setQuestion(mAskAQuestionEdt.getText().toString().trim());
        }
        mEditPollRequest.getOptions().clear();
        mEditPollRequest.getOptions().clear();
        if (!TextUtils.isEmpty(mOptionAEdt.getText().toString().trim())) {
            mEditPollRequest.getOptions().add(mOptionAEdt.getText().toString().trim());
            mEditPollRequest.getAnswer_ids().add(mOptionAEdt.getTag().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionBEdt.getText().toString().trim())) {
            mEditPollRequest.getOptions().add(mOptionBEdt.getText().toString().trim());
            mEditPollRequest.getAnswer_ids().add(mOptionBEdt.getTag().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionCEdt.getText().toString().trim())) {
            mEditPollRequest.getOptions().add(mOptionCEdt.getText().toString().trim());
            mEditPollRequest.getAnswer_ids().add(mOptionCEdt.getTag().toString().trim());
        }

        if (!TextUtils.isEmpty(mOptionDEdt.getText().toString().trim())) {
            mEditPollRequest.getOptions().add(mOptionDEdt.getText().toString().trim());
            mEditPollRequest.getAnswer_ids().add(mOptionDEdt.getTag().toString().trim());
        }

        if (mEditPollRequest.getOptions().size() < mPollOptions.getData().getOptions().size()) {
            Helper.showAlertMessage(this, "Edit Poll", "You must specify all options", "CLOSE");
            return null;
        }

        if (mEditPollRequest.getOptions().size() < 2) {
            //Show options required message
            Helper.showAlertMessage(this, "Edit Poll", "Add at least two options", "CLOSE");
            return null;
        }

        mEditPollRequest.setPoll_end_year(1900 + mPollEndDate.getYear());
        mEditPollRequest.setPoll_end_month(mPollEndDate.getMonth() + 1);
        mEditPollRequest.setPoll_end_day(mPollEndDate.getDate());
        mEditPollRequest.setPoll_end_hour(mPollEndDate.getHours());
        mEditPollRequest.setPoll_end_minute(mPollEndDate.getMinutes());

        mEditPollRequest.setUser_id(Config.getStringValue(Config.USER_ID));
        return mEditPollRequest;
    }

    private void addPollOnServer() {
        Helper.hideKeyboardFromWindow(this);
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(this);
            return;
        }
        final AddPollRequest mAddPollRequest = getValidAddPollRequest();
        if (mAddPollRequest == null) {
            return;
        }

        if (!TextUtils.isEmpty(uploadFilePathString)) {
            new UploadMediaFile(this, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.what) {
                        case UploadMediaFile.UPLOAD_SUCCESS:
                            stopProgressDialog();
                            mAddPollRequest.setResource_id(Long.parseLong(message.obj.toString()));
                            mAddPollRequest.setPost_content_type(Flinnt.POST_CONTENT_GALLERY);
                            sendAddPollRequest(mAddPollRequest);
                            break;
                        case UploadMediaFile.UPLOAD_FAILURE:
                            stopProgressDialog();
                            Helper.showAlertMessage(AddPollActivity.this, "Failure", "Unable add attachment", "CLOSE");
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());
                            break;
                    }
                    return false;
                }
            }), uploadFilePathString, Flinnt.POST_CONTENT_GALLERY).execute();
            mProgressDialog = Helper.getProgressDialog(this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
            if (mProgressDialog != null) mProgressDialog.show();
        } else {
            sendAddPollRequest(mAddPollRequest);
        }
    }

    private void sendAddPollRequest(final AddPollRequest addPollRequest) {
        startProgressDialog();
        AddPoll mAddPoll = new AddPoll(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        Helper.showToast("Poll has been added successfully", Toast.LENGTH_LONG);
                        AddPostResponse addPostResponse = getAddPostResponse((AddPollResponse) message.obj);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(CourseDetailsActivity.ADDED_CONTENT_DATA, addPostResponse);
                        setResult(Activity.RESULT_OK, resultIntent);
                        AddPostResponse.COURSE_ID = addPollRequest.getCourse_id();
                        finish();
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        handleError(message);
                        break;
                }
                return false;
            }
        }), addPollRequest);
        mAddPoll.sendAddPollRequest();
    }

    private AddPostResponse getAddPostResponse(AddPollResponse addPollResponse) {
        AddPostResponse addPostResponse = new AddPostResponse();
        addPostResponse.setStatus(1);
        AddPostResponse.Data data = addPostResponse.getDataInstance();
        data.setIsAdded(1);
        data.setIsChange(0);
        data.setIsEdited(0);
        data.setIsRepost(0);
        AddPostResponse.Post post = addPostResponse.getPostInstance();
        post.setAlbumImages(addPollResponse.getData().getPost().getAttachments());
        post.setCanDeletePost(addPollResponse.getData().getPost().getCanDeletePost().intValue());
        post.setDescription(addPollResponse.getData().getPost().getDescription());
        post.setInserted(addPollResponse.getData().getPost().getInserted());
        post.setIsBookmark(addPollResponse.getData().getPost().getIsBookmark().intValue());
        post.setIsRead(addPollResponse.getData().getPost().getIsRead().intValue());
        post.setMessageToUsers(addPollResponse.getData().getPost().getMessageToUsers());
        post.setPostContentType(addPollResponse.getData().getPost().getPostContentType().intValue());
        post.setPostId(addPollResponse.getData().getPost().getPostId().intValue());
        post.setPublishDate(addPollResponse.getData().getPost().getPublishDate());
        post.setTitle(addPollResponse.getData().getPost().getDescription());
        post.setPostType(Flinnt.POST_TYPE_POLL);
        post.setTotalComments(addPollResponse.getData().getPost().getTotalComments().intValue());
        post.setTotalViews(addPollResponse.getData().getPost().getTotalViews().intValue());
        data.setPost(post);
        addPostResponse.setData(data);
        return addPostResponse;
    }

    private void sendEditPollRequest(EditPollRequest editPollRequest) {
        startProgressDialog();
        EditPoll mEditPoll = new EditPoll(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        Helper.showToast("Poll has been updated successfully", Toast.LENGTH_LONG);
                        Intent intent = new Intent();
                        intent.putExtra("position", mPosition);
                        intent.putExtra(Post.POST_ID_KEY, mPollId);
                        intent.putExtra(Flinnt.POST_STATS_ACTION, mPostStat);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        handleError(message);
                        break;
                }
                return false;
            }
        }), editPollRequest);
        mEditPoll.sendEditPollRequest();
    }

    private void handleError(Message message) {
        BaseResponse response = (BaseResponse) message.obj;
        if (response.errorResponse != null) {
            String errorMessage = response.errorResponse.getMessage();
            for (int i = 0; i < response.errorResponse.getErrorList().size(); i++) {
                com.edu.flinnt.protocol.Error error = response.errorResponse.getErrorList().get(i);
                if (error.getCode() == ErrorCodes.ERROR_CODE_8) {
                    showCourseDeletedDialog(errorMessage);
                    return;
                }
            }
            Helper.showAlertMessage(AddPollActivity.this, "Error", errorMessage, "CLOSE");
        } else {
            Helper.showAlertMessage(AddPollActivity.this, "Error", "Unexpected error occurred.", "CLOSE");
        }
    }

    /**
     * Course delete confirmation dialog
     */
    public void showCourseDeletedDialog(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set mResorceTitleTxt
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(this);
        // You Can Customise your Title here
        titleText.setText("Error");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(this.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage("You have been removed from course");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Delete Course from offline database
                CourseInterface.getInstance().deleteCourse(Config.getStringValue(Config.USER_ID), mCourseID);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("isCourseDelete", true);

                if (!Helper.isFinishingOrIsDestroyed(AddPollActivity.this)) {
                    finish();
                }
            }
        });
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }

    }


    @Override
    public void onItemClick(CommunicationItem item) {
        if (!TextUtils.isEmpty(mCourseID)) {
            switch (item.getDrawableResource()) {
                case R.drawable.add_poll_image:


                    if (AskPermition.getInstance(AddPollActivity.this).isPermitted()) {

                        Intent intent = new Intent(this, ImagePickerActivity.class);
                        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
                        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
                        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
                        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_TITLE, "Tap to select");
                        startActivityForResult(intent, RESULT_FROM_STORAGE);
                    }
                    break;
                case R.drawable.post_schedule:
                    Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                    scheduleIntent.putExtra("mScheduleStatus", mPublishStats);
                    scheduleIntent.putExtra(AddPostRequest.PUB_YEAR_KEY, Integer.toString(mScheduleYear));
                    scheduleIntent.putExtra(AddPostRequest.PUB_MONTH_KEY, Integer.toString(mScheduleMonth));
                    scheduleIntent.putExtra(AddPostRequest.PUB_DAY_KEY, Integer.toString(mScheduleDay));
                    scheduleIntent.putExtra(AddPostRequest.PUB_HOUR_KEY, Integer.toString(mScheduleHour));
                    scheduleIntent.putExtra(AddPostRequest.PUB_MINUTE_KEY, Integer.toString(mScheduleMinute));
                    startActivityForResult(scheduleIntent, SCHEDULE_CALL_BACK);
                    break;
                default:
                    break;

            }
        } else {
            Helper.showAlertMessage(this, "Add Poll", "Select Course", "CLOSE");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : image" + "\nData Uri : " + data);

        switch (requestCode) {
            case COURSE_INFO_CALL_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    updateCourseSelection(data);
                }
                break;
            case RESULT_FROM_STORAGE:
                if (resultCode == RESULT_OK) {
                    updateAttachment(data);
                }
                break;
            case SCHEDULE_CALL_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    mPublishStats = data.getStringExtra("mScheduleStatus");
                    if (mPublishStats.equals(Flinnt.PUBLISH_NOW)) {
                        mScheduleDate = null;
                        mPollEndDate.setTime(System.currentTimeMillis() + MINIMUM_POLL_DURATION);
                        mScheduleDateTxt.setText(getDateInString(mPollEndDate));
                        mScheduleTimeTxt.setText(getTimeInString(mPollEndDate));
                        mScheduleYear = mScheduleMinute = mScheduleMonth = mScheduleDay = mScheduleHour = Flinnt.INVALID;
                        break;
                    }
                    mScheduleYear = Integer.parseInt(data.getStringExtra("mScheduleYear"));
                    mScheduleMonth = Integer.parseInt(data.getStringExtra("mScheduleMonth"));
                    mScheduleDay = Integer.parseInt(data.getStringExtra("mScheduleDay"));
                    mScheduleHour = Integer.parseInt(data.getStringExtra("mScheduleHour"));
                    mScheduleMinute = Integer.parseInt(data.getStringExtra("mScheduleMinute"));

//                    mPollEndDate.setYear(mScheduleYear - 1900);
//                    mPollEndDate.setMonth(mScheduleMonth - 1);
//                    mPollEndDate.setDate(mScheduleDay);
//                    mPollEndDate.setHours(mScheduleHour);
//                    mPollEndDate.setMinutes(mScheduleMinute);
                    if (mScheduleDate == null)
                        mScheduleDate = new Date();
                    mScheduleDate.setYear(mScheduleYear - 1900);
                    mScheduleDate.setMonth(mScheduleMonth - 1);
                    mScheduleDate.setDate(mScheduleDay);
                    mScheduleDate.setHours(mScheduleHour);
                    mScheduleDate.setMinutes(mScheduleMinute);
//                    mScheduleDate.setTime(mPollEndDate.getTime());
//                    long time = mPollEndDate.getTime();
//                    mPollEndDate.setTime(time + MINIMUM_POLL_DURATION);
//                    mScheduleDateTxt.setText(getDateInString(mPollEndDate));
//                    mScheduleTimeTxt.setText(getTimeInString(mPollEndDate));
                }
                break;
        }

    }

    private void updateCourseSelection(Intent data) {
        if (mPostStat == Flinnt.POST_POLL_ADD)
            mCourseID = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
        else
            mRepostCourseId = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
        mSelectCourseImg.setBackgroundResource(R.drawable.ic_course_edit);
        mCourseNameTxt.setText(data.getStringExtra(CourseInfo.COURSE_NAME_KEY));
    }

    private void updateAttachment(Intent data) {
        AttachmentValidation attachmentValidation = new AttachmentValidation(this);
        try {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            uploadFilePathString = images.get(0).getPath();
            if (LogWriter.isValidLevel(Log.ERROR))
                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.image, false)) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("Valid Image File");

                Validation validation = attachmentValidation.getValidation(AttachmentValidation.FileType.image);
                uploadFilePathString = new Common(this).compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 1;
                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                            + ", Height : " + uploadBitmap.getHeight());
                if (uploadBitmap == null) {
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write("Invalid file");
                    Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.image), "CLOSE");
                    return;
                } else {
                    mAttachPhotosLinearLayout.removeAllViews();
                    isResourceChanged = true;
                    addAttachedPhotoView(uploadBitmap, uploadFilePathString);
                    doShowAttachmentMenu(false);
                }
            }
        } catch (Exception e) {
            Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.image), "CLOSE");
        }
    }

    private void doShowAttachmentMenu(boolean doShow) {
        if (doShow) {
            mAttachmentRecycler.setVisibility(View.VISIBLE);
            mAttachmentIconLinear.setVisibility(View.GONE);
        } else {
            mAttachmentRecycler.setVisibility(View.GONE);
            mAttachmentIconLinear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_date_relative:
                // open DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePicker,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                datePickerDialog.show();
                break;
            case R.id.schedule_time_relative:
                new TimePickerDialog(this, timePicker,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE), false).show();
                break;
            case R.id.attachment_icon_linear:
                doShowAttachmentMenu(true);
                break;
            case R.id.course_title_linear:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                    return;
                } else {
                    Intent selectCourse = new Intent(this, SelectCourseActivity.class);
                    if (comeFromActivity.equals("ViewersActivity")) {
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_POLL);
                    } else {
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_POLL);
                    }

                    if (mPostStat == Flinnt.POST_POLL_REPOST) {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_REPOST);
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_POLL);
                    } else {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_ADD);
                    }
                    startActivityForResult(selectCourse, COURSE_INFO_CALL_BACK);
                }
                break;
            default:
                break;
        }

    }


    public void getData() {
        mPostStat = Flinnt.POST_POLL_ADD;
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mPostStat = bundle.getInt(Flinnt.POST_STATS_ACTION);

            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            }
            if (bundle.containsKey(CourseInfo.COURSE_NAME_KEY)) {
                mCourseNameStr = bundle.getString(CourseInfo.COURSE_NAME_KEY);
            }
            if (bundle.containsKey(Post.POST_ID_KEY)) {
                mPollId = bundle.getString(Post.POST_ID_KEY);
            }
            if (bundle.containsKey(SelectCourseActivity.COURSE_ID_REPOST_KEY)) {
                mRepostCourseId = bundle.getString(SelectCourseActivity.COURSE_ID_REPOST_KEY);
            }
            if (bundle.containsKey(PostDetailsResponse.DESCRIPTION_KEY)) {
                mQuestionDescription = bundle.getString(PostDetailsResponse.DESCRIPTION_KEY);
            }
            if (bundle.containsKey("PollOptions")) {
                mPollOptions = (PollListResponse) bundle.getSerializable("PollOptions");
            }
            if (bundle.containsKey(PostDetailsResponse.ATTACHMENT_URL_KEY)) {
                mAttachmentURL = bundle.getString(PostDetailsResponse.ATTACHMENT_URL_KEY);
            }
            if (bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY)) {
                mAttachmentName = bundle.getString(PostDetailsResponse.POST_ATTACHMENT_KEY);
            }

            if (bundle.containsKey("position")) {
                mPosition = bundle.getInt("position");
            }
            if (bundle.containsKey(Post.POLL_RESULT_HOURS_KEY)) {
                Long endDateInMillis = Long.parseLong(bundle.getString(Post.POLL_RESULT_HOURS_KEY) + "000");
                if (mPollEndDate == null)
                    mPollEndDate = new Date();
                mPollEndDate.setTime(endDateInMillis);
            }

        }
    }

    /**
     * Show attached file on UI
     *
     * @param photoThumb thumbnail of attached photo
     * @param imagePath  path of attached file
     */
    private void addAttachedPhotoView(Bitmap photoThumb, final String imagePath) {
        LinearLayout inflatedView = (LinearLayout) View.inflate(this, R.layout.attached_photo, null);
        final RelativeLayout relativeLayout = (RelativeLayout) inflatedView.findViewById(R.id.attached_file_layout);
        ImageView removeImage = (ImageView) inflatedView.findViewById(R.id.attached_file_remove);
        final ImageView playImage = (ImageView) inflatedView.findViewById(R.id.media_open_imgbtn);
        playImage.setVisibility(View.GONE);
        final SelectableRoundedImageView addImage = (SelectableRoundedImageView) inflatedView.findViewById(R.id.attached_file_image);
        addImage.setImageBitmap(photoThumb);
        addImage.setVisibility(View.VISIBLE);
        addImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                            if (mPostStat == Flinnt.POST_POLL_ADD) {
                                                MediaHelper.showImage(imagePath, AddPollActivity.this);
                                                return;
                                            }

                                            if (imagePath.contains(File.separator)) {
                                                MediaHelper.showImage(imagePath, AddPollActivity.this);
                                                return;
                                            }

                                            if (!TextUtils.isEmpty(mAttachmentName)) {
                                                final File file = new File(Helper.getFlinntImagePath() + File.separator + mAttachmentName);
                                                if (file.exists()) {
                                                    MediaHelper.showImage(file.getAbsolutePath(), AddPollActivity.this);
                                                } else {
                                                    startProgressDialog();
                                                    DownloadMediaFile mDownload = new DownloadMediaFile(AddPollActivity.this, Helper.getFlinntImagePath(), mAttachmentName, Long.parseLong(mPollId), mAttachmentURL + Flinnt.GALLERY_MOBILE + File.separator + mAttachmentName, new Handler(new Handler.Callback() {
                                                        @Override
                                                        public boolean handleMessage(Message msg) {
                                                            stopProgressDialog();
                                                            switch (msg.what) {
                                                                case DownloadMediaFile.DOWNLOAD_COMPLETE:
                                                                    if (LogWriter.isValidLevel(Log.INFO))
                                                                        LogWriter.write("Download Complete");
                                                                    MediaHelper.showImage(file.getAbsolutePath(), AddPollActivity.this);
                                                                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                                                                    bitmapOptions.inSampleSize = 1;
                                                                    Bitmap uploadBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
                                                                    if (LogWriter.isValidLevel(Log.ERROR))
                                                                        LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                                                                + ", Height : " + uploadBitmap.getHeight());
                                                                    if (uploadBitmap != null) {
                                                                        addImage.setImageBitmap(uploadBitmap);
                                                                    }
                                                                    break;
                                                                case DownloadMediaFile.DOWNLOAD_FAIL:
                                                                    stopProgressDialog();
                                                                    if (LogWriter.isValidLevel(Log.INFO))
                                                                        LogWriter.write("Download Fail");

                                                                    if (!msg.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                                                                        Helper.showAlertMessage(AddPollActivity.this, "Download Fail", msg.obj.toString(), "CLOSE");
                                                                    }
                                                                    break;
                                                            }
                                                            return false;
                                                        }
                                                    }));
                                                    mDownload.execute();
                                                }
                                            }
                                        }
                                    }

        );

        removeImage.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View arg0) {
                                               if (mAttachmentName == null || TextUtils.isEmpty(mAttachmentName)) {
                                                   isResourceChanged = false;
                                               } else {
                                                   isResourceChanged = true;
                                               }
                                               relativeLayout.setVisibility(View.GONE);
                                               uploadFilePathString = "";
                                               doShowAttachmentMenu(true);
                                           }
                                       }

        );

        mAttachPhotosLinearLayout.addView(inflatedView);
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Attached Photos View added");
        //checkAttachedImageCount();
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mPollEndDate.setYear(year - 1900);
            mPollEndDate.setMonth(monthOfYear);
            mPollEndDate.setDate(dayOfMonth);
            mScheduleDateTxt.setText(getDateInString(mPollEndDate));
        }
    };

    TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            mPollEndDate.setHours(hourOfDay);
            mPollEndDate.setMinutes(minute);

            mScheduleTimeTxt.setText(getTimeInString(mPollEndDate));
        }
    };

    private String getDateInString(Date date) {
        String myFormatDate = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdfDate = new SimpleDateFormat(myFormatDate, Locale.US);
        return sdfDate.format(date);
    }

    private String getTimeInString(Date date) {
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(date);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setViewSize(mEmpatyView);
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.ADD_POLL + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID);
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

}
