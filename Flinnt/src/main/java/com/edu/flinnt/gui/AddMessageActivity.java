package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddPost;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.core.SelectUserTab;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.SelectUserTabResponse;
import com.edu.flinnt.protocol.Validation;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyConfig;
import com.edu.flinnt.util.Recorder;
import com.edu.flinnt.util.UploadMediaFile;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.json.JSONArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddMessageActivity  extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnCancelListener {

    Toolbar toolbar;
    ScrollView mScrollView;
    LinearLayout schedulePublishPostLineatLayout;
    RelativeLayout scheduleDateRelative, scheduleTimeRelative, publishRelative, attachedFileRelative;

    TextView scheduleDateTxtview, scheduleTimeTxtview, courseNameTextview, teacherCountTextview, studentCountTextview;
    ImageButton attachFile;
    ImageView attachedMediaThumb, attachedMediaRemove;
    ImageView selectCourse, selectUsers;
    EditText descTxtview;

    private String mCourseID = "", mPostID = "";

    private String mPublishStats = Flinnt.PUBLISH_NOW;
    private int mPostStat = Flinnt.INVALID;
    private int postContentTypeMedia = Flinnt.INVALID;
    private int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
            mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;

    Resources res = FlinntApplication.getContext().getResources();
    final Calendar mCalendar = Calendar.getInstance();
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    private static final int COURSE_INFO_CALL_BACK = 99;
    private static final int USER_INFO_CALL_BACK = 100;
    public static final int EDIT_SUCCESSFULL_CALL_BACK = 105;

    private String courseNameStr, postDescStr, attachmentUrl, attachmentName;
    private int postContentUrl = Flinnt.INVALID;

    private CharSequence tabTitle[];
    private JSONArray postSelectedUserIDsJsonArray = null;
    private JSONArray postTeacherIDsJsonArray = new JSONArray();
    private JSONArray postStudentIDsJsonArray = new JSONArray();

    // Dialog widgets
    TextView textViewGallery;
    TextView textViewCapture;
    EditText editTextLink;

    File uploadFile;

    private String uploadFilePathString , attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;
    private Bitmap imageFile;
    private Bitmap photoThumb;
    String link;

    final int RESULT_FROM_STORAGE = 101;
    final int RESULT_FROM_RECORDERS = 102;

    enum FileType {
        image, video, audio, pdf, link
    }

    FileType fileType = FileType.image;
    ResourceValidationResponse mResourceValidation;
    Common mCommon;

    private int teachersCount = 0;
    private int studentsCount = 0;
    private String teachersIdsString = "";
    private String studentsIdsString = "";
    //private ArrayList<String> messageUserIDs = new ArrayList<String>();

    private String TEACHERS = "Teachers: ";
    private String LEARNERS = "Learners: ";

    private int isResourseChange = Flinnt.FALSE;
    DownloadMediaFile mDownload ;
    ImageButton playButton;
    private boolean isFromCustomRecorder = false;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.add_message);

        Bundle bundle = getIntent().getExtras();
        if( null != bundle ) {
            mPostStat = bundle.getInt( Flinnt.POST_STATS_ACTION );

            if(bundle.containsKey(Course.COURSE_ID_KEY)) mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            if(bundle.containsKey(CourseInfo.COURSE_NAME_KEY)) courseNameStr = bundle.getString(CourseInfo.COURSE_NAME_KEY);
            if(bundle.containsKey(Post.POST_ID_KEY)) mPostID = bundle.getString(Post.POST_ID_KEY);
            if(bundle.containsKey(PostDetailsResponse.DESCRIPTION_KEY)) postDescStr = bundle.getString(PostDetailsResponse.DESCRIPTION_KEY);

            if(bundle.containsKey(PostDetailsResponse.POST_CONTENT_TYPE_KEY)) setPostContentTypeMedia(bundle.getInt(PostDetailsResponse.POST_CONTENT_TYPE_KEY));
            if(bundle.containsKey(PostDetailsResponse.POST_CONTENT_URL_KEY)) postContentUrl = bundle.getInt(PostDetailsResponse.POST_CONTENT_URL_KEY);
            if(bundle.containsKey(PostDetailsResponse.ATTACHMENT_URL_KEY)) attachmentUrl = bundle.getString(PostDetailsResponse.ATTACHMENT_URL_KEY);
            if(bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY)) attachmentName = bundle.getString(PostDetailsResponse.POST_ATTACHMENT_KEY);

            if(bundle.containsKey(SelectUsersActivity.SELECTED_TEACHERS_COUNT)) teachersCount = bundle.getInt(SelectUsersActivity.SELECTED_TEACHERS_COUNT);
            if(bundle.containsKey(SelectUsersActivity.SELECTED_STUDENTS_COUNT)) studentsCount = bundle.getInt(SelectUsersActivity.SELECTED_STUDENTS_COUNT);
            if(getIntent().getExtras().containsKey(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER))
                teachersIdsString = getIntent().getExtras().getString(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER);
            if(getIntent().getExtras().containsKey(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER))
                studentsIdsString = getIntent().getExtras().getString(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
            getSupportActionBar().setTitle("Edit Message");
        }

        if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
            isResourseChange = Flinnt.FALSE;
        }else{
            isResourseChange = Flinnt.TRUE;
        }


        schedulePublishPostLineatLayout = (LinearLayout) findViewById(R.id.publish_linear_date_time_select_addmessage);
        scheduleDateRelative = (RelativeLayout) findViewById(R.id.schedule_date_relative_addmessage);
        scheduleTimeRelative = (RelativeLayout) findViewById(R.id.schedule_time_relative_addmessage);
        publishRelative = (RelativeLayout) findViewById(R.id.publish_relative_main);
        attachedFileRelative = (RelativeLayout) findViewById(R.id.attached_file_layout);

        mScrollView = (ScrollView) findViewById(R.id.scroll_addmessage);
        scheduleDateTxtview = (TextView) findViewById(R.id.schedule_date_text_addmessage);
        scheduleTimeTxtview = (TextView) findViewById(R.id.schedule_time_text_addmessage);
        courseNameTextview = (TextView) findViewById(R.id.course_desc_addmessage);
        teacherCountTextview = (TextView) findViewById(R.id.teachers_users_addmessage);
        studentCountTextview = (TextView) findViewById(R.id.students_users_addmessage);

        descTxtview = (EditText) findViewById(R.id.message_description_addmessage);
        descTxtview.setMaxLines(Integer.MAX_VALUE);
        selectCourse = (ImageView) findViewById(R.id.course_edit_addmessage);
        selectUsers = (ImageView) findViewById(R.id.message_select_user_addmessage);
        attachedMediaThumb = (ImageView) findViewById(R.id.attached_file_image);
        attachedMediaThumb.setOnClickListener(this);
        attachedMediaRemove = (ImageView) findViewById(R.id.attached_file_remove);

        attachedMediaRemove.setOnClickListener(this);
        selectUsers.setOnClickListener(this);

        scheduleDateRelative.setOnClickListener(this);
        scheduleTimeRelative.setOnClickListener(this);
        selectCourse.setOnClickListener(this);

        playButton = (ImageButton) findViewById(R.id.imagebutton_preview_media);
        playButton.setOnClickListener(this);

        if(!TextUtils.isEmpty(mCourseID)){
            courseNameTextview.setText(courseNameStr);
        }
        if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
            if(getPostContentTypeMedia() != Flinnt.INVALID){

                if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO && postContentUrl == 1){
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    fileType = FileType.link;
                    attachFilePathString = attachmentName;
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }

                else if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_GALLERY){
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);

                    fileType = FileType.image;
                    File file = new File(Helper.getFlinntImagePath() + File.separator + attachmentName);
                    if(file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), attachmentName);
                        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Bitmap : " + photoThumb);
                        if( photoThumb != null ){
                            attachedMediaThumb.setImageBitmap(photoThumb);
                        }
                    }
                    else{
                        attachedMediaThumb.setImageResource(R.drawable.ic_attached_album);
                    }

                }
                else if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_AUDIO){
                    playButton.setVisibility(View.GONE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = FileType.audio;
                    File file = new File(Helper.getFlinntAudioPath() + File.separator + attachmentName);
                    if(file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                    }
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                }

                else if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_DOCUMENT){
                    playButton.setVisibility(View.GONE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = FileType.pdf;
                    File file = new File(Helper.getFlinntDocumentPath() + File.separator + attachmentName);
                    if(file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                    }
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_pdf);
                }

                else if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK){
                    playButton.setVisibility(View.GONE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    attachFilePathString = attachmentName;
                    fileType = FileType.link;

                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                }

                else if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO){
                    playButton.setVisibility(View.VISIBLE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = FileType.video;
                    File file = new File(Helper.getFlinntVideoPath() + File.separator + attachmentName);
                    if(file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = ThumbnailUtils.createVideoThumbnail(attachFilePathString,MediaStore.Images.Thumbnails.MICRO_KIND);
                        if(photoThumb != null){
                            attachedMediaThumb.setImageBitmap(photoThumb);
                        }
                    }
                    else{
                        photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_video);
                        attachedMediaThumb.setImageBitmap(photoThumb);
                    }
                }
            }

            reFilledData();
        }

        attachFile = (ImageButton) findViewById(R.id.attach_file_addmessage);

        attachFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                openBottomSheet();
            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if( message.obj instanceof AddPostResponse) {
                            if( ((AddPostResponse) message.obj ).getIsAdded() == Flinnt.TRUE ||
                                    ((AddPostResponse) message.obj ).getIsRepost() == Flinnt.TRUE){
                                Helper.showToast("Message has been sent", Toast.LENGTH_LONG);
                                finish();
                            }
                            else if( ((AddPostResponse) message.obj ).getIsEdited() == Flinnt.TRUE )
                            {
                                Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);
                                Helper.showToast("Message has been updated", Toast.LENGTH_LONG);
                                finish();
                            }
                        }
                        else if( message.obj instanceof SelectUserTabResponse) {
                            List<String> tabItems = new ArrayList<String>();
                            for (int i = 0; i <  ((SelectUserTabResponse) message.obj ).getAllowedRoles().size() ; i++) {
                                if(((SelectUserTabResponse) message.obj ).getAllowedRoles().get(i) == Flinnt.COURSE_ROLE_TEACHER){
                                    tabItems.add("Teachers");
                                }else if(((SelectUserTabResponse) message.obj ).getAllowedRoles().get(i) == Flinnt.COURSE_ROLE_LEARNER){
                                    tabItems.add("Learners");
                                }
                            }
                            tabTitle = tabItems.toArray(new CharSequence[tabItems.size()]);
                        }
                        else if( message.obj instanceof ResourceValidationResponse) {
                            mResourceValidation = (ResourceValidationResponse) message.obj;
                        }

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if( message.obj instanceof AddPostResponse) {
                            AddPostResponse response = (AddPostResponse) message.obj;
                            if(response != null){
                                Helper.showAlertMessage(AddMessageActivity.this, "Error",message.obj.toString(), "CLOSE");
                            }
                        }

                        break;
                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        isResourseChange = Flinnt.TRUE;
                        sendRequest(message.obj.toString());

                        break;
                    case UploadMediaFile.UPLOAD_FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");
                        if(fileType == FileType.video){
                            attachFilePathString = Helper.getFlinntVideoPath() + attachmentName;
                            MediaHelper.showVideo(attachFilePathString, AddMessageActivity.this, mCommon);
                        }
                        else if(fileType == FileType.audio){
                            attachFilePathString = Helper.getFlinntAudioPath() + attachmentName;
                            MediaHelper.showAudio(attachFilePathString, AddMessageActivity.this, mCommon);
                        }
                        else if(fileType == FileType.pdf){
                            attachFilePathString = Helper.getFlinntDocumentPath() + attachmentName;
                            MediaHelper.showDocument(attachFilePathString, AddMessageActivity.this);
                        }

                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        mDownload = null;
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        if(!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)){
                            Helper.showAlertMessage(AddMessageActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
                        }

                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        {// generate json array in edit mode
            JSONArray usersIDsArray = new JSONArray();
            JSONArray teacherArray = new JSONArray();
            JSONArray studentArray = new JSONArray();
            String[] teacherUserIDs = teachersIdsString.split(",");
            String[] studentUserIDs = studentsIdsString.split(",");

            for (int i = 0; i < teacherUserIDs.length; i++) {
                if(!TextUtils.isEmpty(teacherUserIDs[i])){
                    usersIDsArray.put(teacherUserIDs[i]);
                    teacherArray.put(teacherUserIDs[i]);
                }

            }
            for (int i = 0; i < studentUserIDs.length; i++) {
                if(!TextUtils.isEmpty(studentUserIDs[i])){
                    usersIDsArray.put(studentUserIDs[i]);
                    studentArray.put(studentUserIDs[i]);
                }
            }
            setUserIDsJsonArray(usersIDsArray);
            setTeacherIDsJsonArray(teacherArray);
            setStudentIDsJsonArray(studentArray);
        }

        //mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION);
        //if( null == mResourceValidation ) {
        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION);
        resourceValidation.sendResourceValidationRequest();
        //}

        if(!TextUtils.isEmpty(mCourseID)){
            sendSelectUserTabRequest();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Add Message");
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
        super.onResume();
        if( null == mCommon ) {
            mCommon = new Common(this);
        }
    }

    /** In edit and repost mode, original details to be filled in again to UI */
    private void reFilledData(){
        selectCourse.setVisibility(View.GONE);
        publishRelative.setVisibility(View.GONE);
        descTxtview.setText(postDescStr);
        teacherCountTextview.setText(TEACHERS + teachersCount);
        studentCountTextview.setText(LEARNERS + studentsCount);
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
                finish(); // onBackPressed();
                break;

            case R.id.action_done:
                Helper.hideKeyboardFromWindow(AddMessageActivity.this);
                if ( !Helper.isConnected() ) {
                    Helper.showNetworkAlertMessage(this);
                }
                else {

                    if( validatePost() ) {
                        if(!TextUtils.isEmpty(uploadFilePathString) && fileType == FileType.link ){
                            isResourseChange = Flinnt.TRUE;
                        }

                        if(!TextUtils.isEmpty(lastAttachedImagePath) && fileType != FileType.link){
                            //if(Helper.isConnected()){
                            new UploadMediaFile(AddMessageActivity.this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                            {
                                mProgressDialog = Helper.getProgressDialog(AddMessageActivity.this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
                                if( mProgressDialog != null ) mProgressDialog.show();
                            }
						/*}else{
							Helper.showNetworkAlertMessage(AddMessageActivity.this);
						}*/


                        }else{
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
     * Sends a request to send a message
     * @param resourseID IDs of attached media resource that are going to sent along with message
     */
    private void sendRequest(String resourseID) {

        String descStr = descTxtview.getText().toString();

        AddPostRequest addPostRequest = new AddPostRequest();
        addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addPostRequest.setCourseID(mCourseID);
        addPostRequest.setDescription(descStr);

        if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
            addPostRequest.setPostID(mPostID);
            addPostRequest.setResourseChanged(isResourseChange);
        }

        if(getPostContentTypeMedia() != Flinnt.INVALID){
            if(mPostStat != Flinnt.POST_MESSAGE_EDIT){
                addPostRequest.setPostContantType(getPostContentTypeMedia());
            }

            if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK){
                addPostRequest.setPostUrl(uploadFilePathString);
                addPostRequest.setPostContantType(getPostContentTypeMedia());
            }
            else{
                //if(!TextUtils.isEmpty(resourseID))
                if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
                    if(isResourseChange == Flinnt.TRUE){
                        addPostRequest.setResourseID(resourseID);
                        addPostRequest.setPostContantType(getPostContentTypeMedia());
                    }

                }else{
                    addPostRequest.setResourseID(resourseID);
                }

            }
        }

        addPostRequest.setToUsers(getUserIDsJsonArray());
        if(mPostStat != Flinnt.POST_MESSAGE_EDIT && mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)){
            addPostRequest.setPubYear(mScheduleYear);
            addPostRequest.setPubMonth(mScheduleMonth);
            addPostRequest.setPubDay(mScheduleDay);
            addPostRequest.setPubHour(mScheduleHour);
            addPostRequest.setPubMinute(mScheduleMinute);
        }
        if(mPostStat != Flinnt.INVALID){
            new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
            startProgressDialog();
        }
    }

    /**
     * Checks for the validity of the message to be sent
     * @return true if valid, false otherwise
     */
    private boolean validatePost() {

        if(TextUtils.isEmpty(mCourseID)){
            Helper.showAlertMessage(AddMessageActivity.this, "Add Message", "Select Course", "CLOSE");
            return false;
        }
        else if(TextUtils.isEmpty(descTxtview.getText().toString())){
            Helper.showAlertMessage(AddMessageActivity.this, "Add Message", "Add Description", "CLOSE");
            return false;
        }
        else if(getUserIDsJsonArray() == null || getUserIDsJsonArray().length() < 1){
            Helper.showAlertMessage(AddMessageActivity.this, "Message", "Select users to send message", "CLOSE");
            return false;
        }
        else if(mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)){
            if(mScheduleYear == Flinnt.INVALID){
                Helper.showAlertMessage(AddMessageActivity.this, "Add Message", "Select Date", "CLOSE");
                return false;
            }
            else if(mScheduleHour == Flinnt.INVALID){
                Helper.showAlertMessage(AddMessageActivity.this, "Add Message", "Select Time", "CLOSE");
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Checks if to publish now or schedule
     * @param view publish now/schedule radio button
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.publish_post_now_addmessage:
                if (checked)
                    mPublishStats = Flinnt.PUBLISH_NOW;
                schedulePublishPostLineatLayout.setVisibility(View.GONE);
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                scheduleDateTxtview.setText(res.getString(R.string.select_date));
                scheduleTimeTxtview.setText(res.getString(R.string.select_time));
                break;

            case R.id.publish_post_schedule_addmessage:
                if (checked) {
                    mPublishStats = Flinnt.PUBLISH_SCHEDULE;
                    mScheduleYear = Flinnt.INVALID;
                    mScheduleHour = Flinnt.INVALID;
                }
                schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);
                focusOnView();
                break;
        }
    }

    /**
     * Scroll to the bottom
     */
    private final void focusOnView(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, mScrollView.getBottom());
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.schedule_date_relative_addmessage:
                // open DatePickerDialog
                new DatePickerDialog(AddMessageActivity.this, datePicker,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.schedule_time_relative_addmessage:
                new TimePickerDialog(AddMessageActivity.this, timePicker,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE), false).show();
                break;
            case R.id.course_edit_addmessage:
                if ( !Helper.isConnected() ) {
                    Helper.showNetworkAlertMessage(this);
                }
                else {

                    Intent selectCourse = new Intent(AddMessageActivity.this, SelectCourseActivity.class);
                    selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_MESSAGE);
                    selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_ADD);
                    startActivityForResult( selectCourse, COURSE_INFO_CALL_BACK );

                }

                break;
            case R.id.message_select_user_addmessage:
                if(TextUtils.isEmpty(mCourseID)){
                    Helper.showAlertMessage(AddMessageActivity.this, "Add Message", "Select Course", "CLOSE");
                }
                else if( tabTitle == null || tabTitle.length < 1){

                }else{
                    String teachersIdsStr = "";
                    String studentsIdsStr = "";

                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getUserIDsJsonArray() : " + getUserIDsJsonArray() );
                    if(getUserIDsJsonArray() != null){
                        for (int i = 0; i < getTeacherIDsJsonArray().length(); i++) {
                            try {
                                teachersIdsStr = teachersIdsStr +  getTeacherIDsJsonArray().get(i) + ",";
                            } catch (Exception e) {	LogWriter.err(e);}

                        }

                        for (int i = 0; i < getStudentIDsJsonArray().length(); i++) {
                            try {
                                studentsIdsStr = studentsIdsStr +  getStudentIDsJsonArray().get(i) + ",";
                            } catch (Exception e) {	LogWriter.err(e);}
                        }

                    }else{
                        if(getIntent().getExtras().containsKey(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER))
                            teachersIdsStr = getIntent().getExtras().getString(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER);

                        if(getIntent().getExtras().containsKey(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER))
                            studentsIdsStr = getIntent().getExtras().getString(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER);

                    }

                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("studentsIdsStr : " + studentsIdsStr + " || teachersIdsStr : "  + teachersIdsStr);

                    Intent selectUser = new Intent(AddMessageActivity.this, SelectUsersActivity.class);
                    selectUser.putExtra(Course.COURSE_ID_KEY, mCourseID);
                    selectUser.putExtra(SelectUsersActivity.TAB_TITLE, tabTitle);
                    if(!TextUtils.isEmpty(studentsIdsStr))
                        selectUser.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_LEARNER, studentsIdsStr);
                    if(!TextUtils.isEmpty(teachersIdsStr))
                        selectUser.putExtra(PostDetailsResponse.MessageUsers.MESSAGE_USER_TEACHER, teachersIdsStr);
                    startActivityForResult( selectUser, USER_INFO_CALL_BACK );
                }
                break;
            case R.id.attached_file_remove:
                attachedFileRelative.setVisibility(View.GONE);
                uploadFilePathString = "";
                lastAttachedImagePath = uploadFilePathString;
                setPostContentTypeMedia(Flinnt.INVALID);
                if(mPostStat == Flinnt.POST_MESSAGE_EDIT){
                    isResourseChange = Flinnt.TRUE;
                }
                break;

            case R.id.imagebutton_preview_media:

            case R.id.attached_file_image:
                String filePathString = "";
                if(!TextUtils.isEmpty(uploadFilePathString)){
                    filePathString = uploadFilePathString;
                }else{
                    filePathString = attachFilePathString;
                }

                if(null != filePathString && !filePathString.isEmpty()){
                    switch (fileType) {
                        case image:
                            MediaHelper.showImage(filePathString, this);
                            break;
                        case video:
                            MediaHelper.showVideo(filePathString, this, mCommon);
                            break;
                        case audio:
                            MediaHelper.showAudio(filePathString, this, mCommon);
                            break;
                        case pdf:
                            MediaHelper.showDocument(filePathString, this);
                            break;
                        case link:
                            MediaHelper.ShowLink(filePathString, mCommon);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    switch (fileType) {
				/*case image:
						MediaHelper.showImage(filePathString, this);
						break;*/
                        case video:

                            playButton.setVisibility(View.VISIBLE);
                            if(!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), attachmentUrl)){
                                String videourl = attachmentUrl + attachmentName;
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("videourl : " + videourl);
                                mDownload = new DownloadMediaFile(AddMessageActivity.this, Helper.getFlinntVideoPath(), attachmentName, Long.parseLong(mPostID), videourl, mHandler);
                                mDownload.execute();
                                setDownloadProgressDialog();
                            }
                            break;

                        case audio:

                            playButton.setVisibility(View.GONE);
                            if(!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), attachmentUrl)){
                                String audiourl = attachmentUrl + attachmentName;
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("audiourl : " + audiourl);
                                mDownload = new DownloadMediaFile(AddMessageActivity.this, Helper.getFlinntAudioPath(), attachmentName, Long.parseLong(mPostID), audiourl, mHandler);
                                mDownload.execute();
                                setDownloadProgressDialog();

                            }
                            break;

                        case pdf:

                            if(!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), attachmentUrl)){
                                String docurl = attachmentUrl + attachmentName;
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("docurl : " + docurl);
                                mDownload = new DownloadMediaFile(AddMessageActivity.this, Helper.getFlinntDocumentPath(), attachmentName, Long.parseLong(mPostID), docurl, mHandler);
                                mDownload.execute();
                                setDownloadProgressDialog();

                            }
                            break;

					/*case link:
						MediaHelper.ShowLink(filePathString, mCommon);
						break;*/
                        default:
                            break;
                    }
                }

                break;
            default:
                break;
        }


    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mScheduleYear = year;
            mScheduleMonth = (monthOfYear+1);
            mScheduleDay = dayOfMonth;

            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            scheduleDateTxtview.setText(sdf.format(mCalendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            mScheduleHour = hourOfDay;
            mScheduleMinute = minute;

            String myFormat = "hh:mm a"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            scheduleTimeTxtview.setText(sdf.format(mCalendar.getTime()));
        }
    };

    /**
     *  Starts a circular progress dialog
     */
    private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(AddMessageActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddMessageActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if( mProgressDialog != null) mProgressDialog.show();
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
    public void onCancel(DialogInterface arg0) {
        stopDownload();
    }

    /**
     * Stops currently running download
     */
    private void stopDownload(){
        //stop download if working...
        if( mDownload != null ){
            mDownload.setCancel(true);
        }
    }

    /**
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {

        new BottomSheet.Builder(AddMessageActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title("Attachment")
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch(position) {

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

                            case R.id.attach_bottom_video:
                                //toast("Video");
                                fileType = FileType.video;
                                dialogContents("Choose an Action" , "Record with flinnt", "Choose video track");
                                break;

                            case R.id.attach_bottom_audio:
                                //toast("Audio");
                                fileType = FileType.audio;
                                dialogContents("Choose an Action" , "Record with flinnt", "Choose music track");
                                break;

                            case R.id.attach_bottom_link:
                                //toast("Link");
                                fileType = FileType.link;
                                dialogContents("Paste/Type Link");
                                break;

                            case R.id.attach_bottom_pdf:
                                //toast("PDF");
                                fileType = FileType.pdf;
                                chooseFromStorage(fileType.name());
                                break;

                            default:
                        }
                    }
                }).show();
    }


    /**
     * Dialog to display upload options
     * @param title dialog title
     * @param textViewText1 option 1
     * @param textViewText2 option 2
     */
    private void dialogContents(String title, String textViewText1, String textViewText2) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setView(R.layout.dialog_design_upload);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        textViewGallery = (TextView) dialog.findViewById(R.id.textview_gallery);
        textViewCapture = (TextView) dialog.findViewById(R.id.textview_capture);

        textViewGallery.setText(textViewText2);
        textViewCapture.setText(textViewText1);

        textViewGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
                chooseFromStorage(fileType.name());
            }
        });

        textViewCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
                captureFromRecorders();
            }
        });

    }

    /**
     * Dialog to take and validate weblink entered by user
     * @param title dialog title
     */
    private void dialogContents(String title) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                link = editTextLink.getText().toString().trim();
                //descTxtview.setText(descTxtview.getText().toString() + "\nLink : " + link);
                if(Patterns.WEB_URL.matcher(link).matches()){
                    if( link.startsWith("http://") || link.startsWith("https://")){
                        uploadFilePathString = link;
                    }else{
                        uploadFilePathString = "http://" + link;
                    }
                    playButton.setVisibility(View.GONE);
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    lastAttachedImagePath = uploadFilePathString;
                }
                else{
                    Helper.showAlertMessage(AddMessageActivity.this, "Invalid Link", "Add valid link", "CLOSE");
                }

            }
        });
        builder.setView(R.layout.dialog_design_link);

        final AlertDialog dialog = builder.create();
        dialog.show();

        editTextLink = (EditText) dialog.findViewById(R.id.edittext_link_input);
        editTextLink.setSelection(editTextLink.getText().length());

    }


    /**
     * Intent to pick the file from device's internal storage of SDCard
     * @param type media file type
     */
    private void chooseFromStorage(String type) {

        Intent storageIntent;

        switch (fileType) {
            case image:
                storageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                storageIntent.setType( type + "/*");
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

            case video:
                storageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                storageIntent.setType( type + "/*");
			/*
				uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_VIDEO);
				uploadFilePathString = uploadFile.getAbsolutePath();
				uploadFileUri = Uri.fromFile(uploadFile);
				storageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
			 */
                if (storageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                break;

            case audio:
                storageIntent = new Intent();
			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					storageIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
				}
				else {
					storageIntent.setAction(Intent.ACTION_GET_CONTENT);
				}
				storageIntent.addCategory(Intent.CATEGORY_OPENABLE);
				storageIntent.setType( type + "/*");
				if (storageIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
				}*/

                List<Intent> targetShareIntents=new ArrayList<Intent>();
                Intent shareIntent=new Intent();
                shareIntent.setAction(Intent.ACTION_GET_CONTENT);
                shareIntent.setType("audio/*");
                List<ResolveInfo> resInfos=getPackageManager().queryIntentActivities(shareIntent, 0);
                if(!resInfos.isEmpty()){
                    for(ResolveInfo resInfo : resInfos){
                        String packageName=resInfo.activityInfo.packageName;
                        Log.i("Package Name", packageName);
                        // com.google.android.apps.docs - Drive // com.dropbox.android - DropBox
                        if ( !packageName.contains("google") && !packageName.contains("dropbox") ) {
                            Intent intent=new Intent();
                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("audio/*");
                            intent.setPackage(packageName);
                            targetShareIntents.add(intent);
                        }
                    }
                    if(!targetShareIntents.isEmpty()){
                        Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose file from");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                        startActivityForResult(chooserIntent, RESULT_FROM_STORAGE);
                    }
                }
                break;

            case pdf:
                //			storageIntent = new Intent();
			/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				storageIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
			}
			else {
				storageIntent.setAction(Intent.ACTION_GET_CONTENT);
			}
			storageIntent.addCategory(Intent.CATEGORY_OPENABLE);
			storageIntent.setType( "application/" + fileType.name());
				uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_DOCUMENT);
				uploadFilePathString = uploadFile.getAbsolutePath();
				uploadFileUri = Uri.fromFile(uploadFile);
			    storageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
			// Verify that the intent will resolve to an activity
			if (storageIntent.resolveActivity(getPackageManager()) != null) {
			    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);*/
                //			}
                //			startActivityForResult(storageIntent, RESULT_FROM_STORAGE);

                //			if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {

                if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
                    storageIntent = new Intent(this, PickDocFolderActivity.class);
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                else {
                    List<Intent> targetShareIntents1=new ArrayList<Intent>();
                    Intent shareIntent1=new Intent();
                    shareIntent1.setAction(Intent.ACTION_GET_CONTENT);
                    shareIntent1.setType("application/pdf");
                    List<ResolveInfo> resInfos1=getPackageManager().queryIntentActivities(shareIntent1, 0);

                    if(!resInfos1.isEmpty()){
                        for(ResolveInfo resInfo : resInfos1){
                            String packageName=resInfo.activityInfo.packageName;
                            Log.i("Package Name", packageName);
                            // com.google.android.apps.docs - Drive // com.dropbox.android - DropBox
                            if ( !packageName.contains("google") && !packageName.contains("dropbox") ) {
                                Intent intent=new Intent();
                                intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                intent.setPackage(packageName);
                                targetShareIntents1.add(intent);
                            }
                        }
                        if(!targetShareIntents1.isEmpty()){
                            Intent chooserIntent=Intent.createChooser(targetShareIntents1.remove(0), "Choose file from");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents1.toArray(new Parcelable[]{}));
                            startActivityForResult(chooserIntent, RESULT_FROM_STORAGE);
                        }
                        else {
                            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Add Message : targetShareIntentsl empty...");
                            storageIntent = new Intent(this, PickDocFolderActivity.class);
                            startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                        }
                    }
                    else{
                        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Add Message : resInfos1 empty...");
                        storageIntent = new Intent(this, PickDocFolderActivity.class);
                        startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                    }
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

        switch(fileType) {
            case image:
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_GALLERY);
                if ( null != uploadFile ) {
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
                    }
                }
                break;

            case video:
                captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_VIDEO);
                if (null != uploadFile) {
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);
                    // set the video file name
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);


                    // set video quality
                    captureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // value 0 means low quality. 1 is High

                    if (null != getValidation(FileType.video)) {
                        fileSize = getValidation(FileType.video).getMaxFileSizeLong();
                    } else {
                        fileSize = 10 * 1048 * 1048;
                    }

                    if (fileSize == Flinnt.INVALID) {
                        fileSize = 10 * 1048 * 1048;
                    }
                    captureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, fileSize);// 10*1048*1048=10MB
                    captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 1500);

                    // recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                    captureIntent.putExtra("video_widths", 480);
                    captureIntent.putExtra("video_heights", 640);
                    // intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, );

                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
                    }
                }
                break;

            case audio:
			/*
				captureIntent = new Intent(
				MediaStore.Audio.Media.RECORD_SOUND_ACTION);
				uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
				uploadFilePathString = uploadFile.getAbsolutePath();
				uploadFileUri = Uri.fromFile(uploadFile);
				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
				fileSize = getValidation(FileType.video).getMaxFileSizeLong();
		        if ( fileSize == Flinnt.INVALID ) {
		        	fileSize = 10 * 1048 * 1048;
		        }
		        captureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, fileSize);// 10*1048*1048=10MB
				startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
			 */
                try{
                    uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);

                    Intent recordAudio = new Intent(this, Recorder.class);
                    recordAudio.putExtra("FilePath", uploadFilePathString);
                    Validation validation = getValidation(FileType.audio);
                    if( null != validation ) {
                        recordAudio.putExtra(Validation.MAX_FILE_SIZE_KEY, validation.getMaxFileSizeLong());
                        recordAudio.putExtra(Validation.FILE_TYPES_KEY, validation.getFileTypes());
                    }
                    if (recordAudio.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(recordAudio, RESULT_FROM_RECORDERS);
                    }
                }
                catch(Exception e)
                {
                    LogWriter.err(e);
                }
                break;
            default:
                //toast("File type : " + fileType.name());
                break;
        }
    }


    //	Intent Results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : "  + fileType + "\nData Uri : " + data);

        Uri contentUri;
        try {
            if(resultCode == RESULT_OK) {

                if(requestCode == RESULT_FROM_STORAGE) {

                    switch (fileType) {

                        case image:
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            //imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            if(isValidFile(uploadFilePathString, FileType.image)) {
                                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Valid Image File");
							/*
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
							 */
                                Validation validation = getValidation(FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();
                                bitmapOptions.inSampleSize = 1;

                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if( uploadBitmap == null ){
                                    if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(FileType.image) , "CLOSE");
                                    return;
                                }
                                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                        + ", Height : " + uploadBitmap.getHeight());
                                if(uploadBitmap != null){
                                    attachedMediaThumb.setImageBitmap(uploadBitmap);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.GONE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;
                                }
                                //Picasso.with(con).load(new File(UploadFileName)).into(imagebutton);
							/*
					            if (imagebutton == null) {
					                img_close.setVisibility(View.GONE);
					                return;
					            }
					            img_close.setVisibility(View.VISIBLE);
					            imagebutton.setVisibility(View.VISIBLE);
					            ViewGroup.LayoutParams params1 = imagebutton.getLayoutParams();
					            // params1.height = Math.round(Integer.valueOf(MyAllVal[3])*
					            // bitmap.getHeight() / bitmap.getWidth());
					            //if (imageHeight != 0 && imageWidth!= 0 )
					            int newheight = Math.round(Integer.valueOf(Integer.parseInt(F.getScreenWidth("0")) - 40) * originalBitmap.getHeight() / originalBitmap.getWidth());
					            params1.height = 400;
					            int screenHeight = Integer.valueOf(F.getScreenHeight());
					            if (newheight < screenHeight / 4) {
					                params1.height = newheight;
					            } else {
					                params1.height = screenHeight / 4;
					            }
					            params1.width = Integer.valueOf(F.getScreenWidth());
					            imagebutton.setLayoutParams(params1);
					            imagebutton.setScaleType(ImageView.ScaleType.CENTER_CROP);
					            // imgPreview.setImageBitmap(bitmap);
							 */
                            }
                            break;

                        case video:
                            //uploadFilePathString = getFilePathFromContentURI(data.getData());
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if(isValidFile(uploadFilePathString, FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Images.Thumbnails.MICRO_KIND);
                                if(photoThumb != null){
                                    attachedMediaThumb.setImageBitmap(photoThumb);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.VISIBLE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                    lastAttachedImagePath = uploadFilePathString;


                                }
                                else {
                                    if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(FileType.video) , "CLOSE");
                                }
                            }
                            break;

                        case audio:
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if(isValidFile(uploadFilePathString, FileType.audio)) {
                                attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                                attachedFileRelative.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.GONE);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                lastAttachedImagePath = uploadFilePathString;

                            }
                            break;

                        case pdf:
                            contentUri = data.getData();
                            if ( null == contentUri || Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                uploadFilePathString = data.getStringExtra("result");
                            }
                            else {
                                uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            }
                            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if(isValidFile(uploadFilePathString, FileType.pdf)) {

                                attachedMediaThumb.setImageResource(R.drawable.ic_attached_pdf);
                                attachedFileRelative.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.GONE);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_DOCUMENT);
                                lastAttachedImagePath = uploadFilePathString;

                            }
                            break;

                        default:

                            break;
                    }
                }

                else if(requestCode == RESULT_FROM_RECORDERS) {

                    switch (fileType) {

                        case image:
                            if(isValidFile(uploadFilePathString, FileType.image)) {
							/*
								imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uploadFileUri);
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
							 */
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Valid Image File");
							/*
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
							 */
                                Validation validation = getValidation(FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();
                                bitmapOptions.inSampleSize = 1;

                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                        + ", Height : " + uploadBitmap.getHeight());

                                if(uploadBitmap != null){
                                    attachedMediaThumb.setImageBitmap(uploadBitmap);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.GONE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;

                                }
                                //Picasso.with(con).load(new File(UploadFileName)).into(imagebutton);
							/*
					            if (imagebutton == null) {
					                img_close.setVisibility(View.GONE);
					                return;
					            }
					            img_close.setVisibility(View.VISIBLE);
					            imagebutton.setVisibility(View.VISIBLE);
					            ViewGroup.LayoutParams params1 = imagebutton.getLayoutParams();
					            // params1.height = Math.round(Integer.valueOf(MyAllVal[3])*
					            // bitmap.getHeight() / bitmap.getWidth());
					            //if (imageHeight != 0 && imageWidth!= 0 )
					            int newheight = Math.round(Integer.valueOf(Integer.parseInt(F.getScreenWidth("0")) - 40) * originalBitmap.getHeight() / originalBitmap.getWidth());
					            params1.height = 400;
					            int screenHeight = Integer.valueOf(F.getScreenHeight());
					            if (newheight < screenHeight / 4) {
					                params1.height = newheight;
					            } else {
					                params1.height = screenHeight / 4;
					            }
					            params1.width = Integer.valueOf(F.getScreenWidth());
					            imagebutton.setLayoutParams(params1);
					            imagebutton.setScaleType(ImageView.ScaleType.CENTER_CROP);
					            // imgPreview.setImageBitmap(bitmap);
							 */
                            }
                            break;

                        case video:
                            if(isValidFile(uploadFilePathString, FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString,MediaStore.Images.Thumbnails.MICRO_KIND);
                                if(photoThumb != null){
                                    attachedMediaThumb.setImageBitmap(photoThumb);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.VISIBLE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                    lastAttachedImagePath = uploadFilePathString;

                                }
                            }
                            break;

                        case audio:
                            isFromCustomRecorder = true;
                            if(isValidFile(uploadFilePathString, FileType.audio)) {
                                attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                                attachedFileRelative.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.GONE);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                lastAttachedImagePath = uploadFilePathString;

                            }
                            break;

                        default:
                            playButton.setVisibility(View.GONE);
                            break;
                    }
                }
                else {
                    switch (requestCode) {
                        case COURSE_INFO_CALL_BACK:
                            String courseID = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
                            String courseName = data.getStringExtra(CourseInfo.COURSE_NAME_KEY);
                            if ( !courseName.equalsIgnoreCase(courseNameTextview.getText().toString()) ) {

                                resetSelectUsers();
                            }

                            courseNameTextview.setText(courseName);
                            mCourseID = courseID;

                            if(!TextUtils.isEmpty(mCourseID)){
                                sendSelectUserTabRequest();
                            }
                            break;

                        case USER_INFO_CALL_BACK:
                            ArrayList<String> teacherUserIDs = data.getStringArrayListExtra(SelectUsersActivity.SELECTED_TEACHERS_IDS);
                            ArrayList<String> studentUserIDs = data.getStringArrayListExtra(SelectUsersActivity.SELECTED_STUDENTS_IDS);
                            teachersCount = data.getIntExtra(SelectUsersActivity.SELECTED_TEACHERS_COUNT, 0);
                            studentsCount = data.getIntExtra(SelectUsersActivity.SELECTED_STUDENTS_COUNT, 0);

                            teacherCountTextview.setText(TEACHERS + teachersCount);
                            studentCountTextview.setText(LEARNERS + studentsCount);

                            JSONArray usersIDsArray = new JSONArray();
                            JSONArray teacherArray = new JSONArray();
                            JSONArray studentArray = new JSONArray();
                            for (int i = 0; i < teacherUserIDs.size(); i++) {
                                usersIDsArray.put(teacherUserIDs.get(i));
                                teacherArray.put(teacherUserIDs.get(i));
                            }
                            for (int i = 0; i < studentUserIDs.size(); i++) {
                                usersIDsArray.put(studentUserIDs.get(i));
                                studentArray.put(studentUserIDs.get(i));
                            }
                            setUserIDsJsonArray(usersIDsArray);
                            setTeacherIDsJsonArray(teacherArray);
                            setStudentIDsJsonArray(studentArray);

                            break;

                        default:
                            break;
                    }
                }
            }
            else {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("You didn't pick any file ");
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Resets the list of users with none checked
     */
    private void resetSelectUsers() {

        ArrayList<String> teacherUserIDs = new ArrayList<String>();
        ArrayList<String> studentUserIDs = new ArrayList<String>();

        teachersCount = 0;
        studentsCount = 0;

        teacherCountTextview.setText(TEACHERS + teachersCount);
        studentCountTextview.setText(LEARNERS + studentsCount);

        JSONArray usersIDsArray = new JSONArray();
        JSONArray teacherArray = new JSONArray();
        JSONArray studentArray = new JSONArray();

        //		for (int i = 0; i < teacherUserIDs.size(); i++) {
        //			usersIDsArray.put(teacherUserIDs.get(i));
        //			teacherArray.put(teacherUserIDs.get(i));
        //		}
        //		for (int i = 0; i < studentUserIDs.size(); i++) {
        //			usersIDsArray.put(studentUserIDs.get(i));
        //			studentArray.put(studentUserIDs.get(i));
        //		}

        setUserIDsJsonArray(usersIDsArray);
        setTeacherIDsJsonArray(teacherArray);
        setStudentIDsJsonArray(studentArray);

    }

    /**
     * Sends request to get the list of roles to be added to the tabs
     */
    private void sendSelectUserTabRequest() {
        new SelectUserTab(mHandler, Config.getStringValue(Config.USER_ID), mCourseID).sendSelectUserTabRequest();
    }

    /**
     * @return selected userIDs JSONAArray
     */
    public JSONArray getUserIDsJsonArray() {
        return postSelectedUserIDsJsonArray;
    }

    /**
     * Set User Ids to Json Array locally
     * @param userIDsJsonArray user ids json array
     */
    public void setUserIDsJsonArray(JSONArray userIDsJsonArray) {
        this.postSelectedUserIDsJsonArray = userIDsJsonArray;
    }

    public JSONArray getTeacherIDsJsonArray() {
        return postTeacherIDsJsonArray;
    }
    public void setTeacherIDsJsonArray(JSONArray teacherIDsJsonArray) {
        this.postTeacherIDsJsonArray = teacherIDsJsonArray;
    }
    public JSONArray getStudentIDsJsonArray() {
        return postStudentIDsJsonArray;
    }
    public void setStudentIDsJsonArray(JSONArray studentIDsJsonArray) {
        this.postStudentIDsJsonArray = studentIDsJsonArray;
    }

    /**
     * Gets the type of attached media
     * @return filetype code number
     */
    public int getPostContentTypeMedia() {
        return postContentTypeMedia;
    }

    /**
     * Sets the type of attached media
     * @param postContentTypeMedia filetype code number
     */
    public void setPostContentTypeMedia(int postContentTypeMedia) {
        this.postContentTypeMedia = postContentTypeMedia;
    }

	/*
	private void  toast(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	 */
	/*
	public String getFilePathFromContentURI(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Video.Media.DATA};
            @SuppressWarnings("deprecation")
			Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }
	 */

    /**
     * To get validation parameters
     * @param type filetype
     * @return validation parameters of filetype
     */
    public Validation getValidation(FileType type) {
        Validation validation = null;
        if( null == mResourceValidation ) {
            mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION);
        }
        if( null != mResourceValidation ) {
            switch (type) {
                case image:
                    validation = mResourceValidation.getImage();
                    break;
                case audio:
                    validation = mResourceValidation.getAudio();
                    break;
                case video:
                    validation = mResourceValidation.getVideo();
                    break;
                case pdf:
                    validation = mResourceValidation.getDoc();
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
     * @param filePath selected file's path on storage
     * @param type file type
     * @return true if valid, false otherwise
     */
    private boolean isValidFile(String filePath, FileType type) {
        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("filePath : " + filePath + ", FileType : " + type);
        boolean ret = true;
        if ( filePath == null ) {
            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file - null filepath");
            Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(type) , "CLOSE");
            return false;
        }
        Validation validation = getValidation(type);
        if( null != validation ) {
            File file = new File(filePath);
            String path = file.getPath();
            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("validation : " + validation.toString());
            long length = file.length();
            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File length : " + length );
            //	length <= 0 when file cannot be decoded
            if ( length <= 0 ) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(type) , "CLOSE");
                return false;
            }
            if(length >= validation.getMaxFileSizeLong()) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, "Error", AddPostActivity.validateSizeMessage(type), "CLOSE");
                return false;
            }

            if(!TextUtils.isEmpty(validation.getFileTypes())){
                String fileExtention = path.substring(path.lastIndexOf(".")+1);
                if(fileType == FileType.pdf){
                    fileExtention = fileExtention.toLowerCase();
                }
                ArrayList<String> extentions = new ArrayList<String>(Arrays.asList(validation.getFileTypes().split(",")));
                if (!extentions.contains(fileExtention) && !isFromCustomRecorder) {
                    uploadFilePathString = "";
                    if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("This file type is not supported.");
                    Helper.showAlertMessage(this, "Error", AddPostActivity.validateSuppotedFileMessage(type), "CLOSE");
                    return false;
                }
            }

            switch (type) {
                case image:
                    ret = validateImage(file.getPath(), validation);
                    break;
                case audio:

                    break;
                case video:

                    break;
                case pdf:

                    break;

                default:
                    break;
            }
        }
        return ret;
    }


    /**
     * Checks if attached file is valid to be uploaded or not
     * @param ImagePath file path on storage
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
            //bitmapOptions.inSampleSize = 1;
            //Flile lenth > 1MB ...then compress...
            if(new File(ImagePath).length() > (1*1024*1024))	{
                bitmapOptions = setBitmapFactoryOptions(bitmapOptions);
            }else{
                bitmapOptions.inSampleSize = 1;
            }

            final Bitmap originalBitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);
            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("originalBitmap : Height : " + originalBitmap.getHeight()
                    + ", Width : " + originalBitmap.getWidth());
            if (originalBitmap.getHeight() < validation.getMinHeight()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min300));
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File height is smaller then Minimum Height.");
                Helper.showAlertMessage(this, "Error", AddPostActivity.validateResolutionMessage(FileType.image), "CLOSE");
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min200));
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, "Error", AddPostActivity.validateResolutionMessage(FileType.image), "CLOSE");
                return false;
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return true;
    }

    private BitmapFactory.Options setBitmapFactoryOptions(BitmapFactory.Options bitmapOptions ) {
        // TODO Auto-generated method stub
        bitmapOptions.inScaled = false;
        bitmapOptions.inDither = false;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOptions.inSampleSize = 2;
        //bitmapOptions.inSampleSize = 1;
        return bitmapOptions;
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        mProgressDialog = new ProgressDialog(AddMessageActivity.this);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (DialogInterface.OnClickListener) null);

        mProgressDialog.show();

        mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDownload();
                try {
                    mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                    mProgressDialog.setMessage("Cancelling...");
                } catch (Exception e) {
                    LogWriter.err(e);
                }
            }
        });

        mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

    }
}
