package com.edu.flinnt.gui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.CommunicationItemAdapter;
import com.edu.flinnt.core.AddPost;
import com.edu.flinnt.core.PostAllowOptions;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.core.SelectUserTab;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostAllowOptionsResponse;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.PostDetailsResponse.MessageUsers;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.SelectUserTabResponse;
import com.edu.flinnt.protocol.Template;
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
import com.edu.flinnt.util.MyConfig;
import com.edu.flinnt.util.Recorder;
import com.edu.flinnt.util.SelectableRoundedImageView;
import com.edu.flinnt.util.UploadMediaFile;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.Func2;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by flinnt-android-2 on 20/12/16.
 * Add Communiction Functionality
 */

public class AddCommunicationActivity extends AppCompatActivity implements View.OnClickListener, CommunicationItemAdapter.ItemListener {


    LinearLayout attachment_list_linear, attachmentIconLinear, courseTitleLinear;
    Animation animSlideUp;//, animFadeIn;
    EditText descriptionEditTxt;
    Toolbar toolbar;
    private CommunicationItemAdapter mAdapter;
    ImageView selectCourseImg;
    ProgressDialog mProgressDialog = null;
    public Handler mHandler = null;
    String comeFromActivity = "";
    DownloadMediaFile mDownload;
    int currentPostType = Flinnt.FALSE;
    private String mCourseID = "";
    protected ImageView courseImg;
    private ImageLoader mImageLoader;
    private String postContentUrl = "";
    String courseNameStr, attachmentUrl, descriptionStr, mPostID = "", mRepostCourseId;
    private int mPosition = -1;

    //Attachment
    enum FileType {
        image, video, audio, pdf, link
    }

    FileType fileType = FileType.image;
    FileType defaultFileType = FileType.image;
    private int mPostStat = Flinnt.INVALID;
    private String mPublishStats = Flinnt.PUBLISH_NOW;
    public int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
            mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;

    // Dialog widgets
    private TextView textViewGallery;
    private TextView textViewCapture;
    private EditText editTextLink;
    private ImageView attachedUsersRemoveImg;
    private RelativeLayout attachedUserRelative;

    private File uploadFile;
    private int isResourseChange = Flinnt.FALSE;
    private String uploadFilePathString, attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;

    private String link;
    final int RESULT_FROM_STORAGE = 101;
    final int RESULT_FROM_RECORDERS = 102;
    final int RESULT_FROM_ALBUM_STORAGE = 103;

    ResourceValidationResponse mResourceValidation;
    private int postContentTypeMedia = Flinnt.INVALID;

    //users
    private final int COURSE_INFO_CALL_BACK = 99;
    private final int TEMPLATES_CALL_BACK = 100;
    private final int SCHEDULE_CALL_BACK = 155;
    private final int USER_INFO_CALL_BACK = 104;
    public static final int EDIT_SUCCESSFULL_CALL_BACK = 105;
    private CharSequence tabTitle[];
    private JSONArray postSelectedUserIDsJsonArray = null;
    private JSONArray postTeacherIDsJsonArray = new JSONArray();
    private JSONArray postStudentIDsJsonArray = new JSONArray();
    private int teachersCount = 0;
    private int studentsCount = 0;
    private String teachersIdsString = "";
    private String studentsIdsString = "";


    private String TEACHERS = "Teachers: ";
    private String LEARNERS = "Learners: ";
    ArrayList<String> selectedViewersIDs;
    String comeFromViewerActivityFirst = Flinnt.DISABLED;


    //Album
    private ArrayList<String> attachmentName = new ArrayList<String>();
    ArrayList<Image> selectedImages = null;
    LinearLayout attachPhotosLinearLayout;
    private ArrayList<String> uploadFilePathStringArray = new ArrayList<String>();
    private ArrayList<String> resourseIDsArr = new ArrayList<String>();

    //Template
    String templateId = "", templateName, templateTitle, templateDescription;
    Common mCommon;
    private boolean isFromCustomRecorder = false;
    private Bitmap photoThumb;
    TextView courseNameTextview, selectedTeacherTxt, selectedLearnerTxt, attachmentTxt;
    HorizontalScrollView attachHorizontalscroll;

    ImageView attachmentImg, albumImg, templateImg, usersImg, scheduleImg;
    private int attachmentNameArrSize = 0;


    ArrayList<CommunicationItem> itemsShowHide = new ArrayList<>();
    RecyclerView recyclerView;
    PostAllowOptionsResponse mPostAllowOptionsResponse;
    String changeCourseId = "", changeCourseName = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_add_communication);
        //@Nikhil 20062018
        AskPermition.getInstance(AddCommunicationActivity.this).RequestAllPermission();
        mPostStat = Flinnt.POST_COMMUNICATION_ADD;
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mPostStat = bundle.getInt(Flinnt.POST_STATS_ACTION);
            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
                changeCourseId = mCourseID;
            }
            if (bundle.containsKey(CourseInfo.COURSE_NAME_KEY)) {
                courseNameStr = bundle.getString(CourseInfo.COURSE_NAME_KEY);
                changeCourseName = courseNameStr;
            }
            if (bundle.containsKey(Post.POST_ID_KEY)) {
                mPostID = bundle.getString(Post.POST_ID_KEY);
            }
            if (bundle.containsKey(SelectCourseActivity.COURSE_ID_REPOST_KEY)) {
                mRepostCourseId = bundle.getString(SelectCourseActivity.COURSE_ID_REPOST_KEY);
            }
            if (bundle.containsKey(PostDetailsResponse.DESCRIPTION_KEY)) {
                descriptionStr = bundle.getString(PostDetailsResponse.DESCRIPTION_KEY);
            }
            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_TYPE_KEY)) {
                setPostContentTypeMedia(bundle.getInt(PostDetailsResponse.POST_CONTENT_TYPE_KEY));
            }
            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_URL_KEY)) {
                postContentUrl = bundle.getString(PostDetailsResponse.POST_CONTENT_URL_KEY);
            }
            if (bundle.containsKey(PostDetailsResponse.ATTACHMENT_URL_KEY)) {
                attachmentUrl = bundle.getString(PostDetailsResponse.ATTACHMENT_URL_KEY);
            }
            if (bundle.containsKey(SelectUsersActivity.SELECTED_TEACHERS_COUNT)) {
                teachersCount = bundle.getInt(SelectUsersActivity.SELECTED_TEACHERS_COUNT);
            }
            if (bundle.containsKey(SelectUsersActivity.SELECTED_STUDENTS_COUNT)) {
                studentsCount = bundle.getInt(SelectUsersActivity.SELECTED_STUDENTS_COUNT);
            }
            if (getIntent().getExtras().containsKey(MessageUsers.MESSAGE_USER_TEACHER)) {
                teachersIdsString = getIntent().getExtras().getString(MessageUsers.MESSAGE_USER_TEACHER);
            }
            if (getIntent().getExtras().containsKey(MessageUsers.MESSAGE_USER_LEARNER)) {
                studentsIdsString = getIntent().getExtras().getString(MessageUsers.MESSAGE_USER_LEARNER);
            }
            if (getIntent().getExtras().containsKey("comeFrom")) {
                comeFromActivity = getIntent().getExtras().getString("comeFrom");
                comeFromViewerActivityFirst = Flinnt.ENABLED;
            }
            if (bundle.containsKey("position")) {
                mPosition = bundle.getInt("position");
            }
            if (bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY)) {
                if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
                    attachmentName.add(bundle.getString(PostDetailsResponse.POST_ATTACHMENT_KEY));
                    attachmentNameArrSize = attachmentName.size();
                } else {
                    attachmentName = bundle.getStringArrayList(PostDetailsResponse.POST_ATTACHMENT_KEY);
                    attachmentNameArrSize = attachmentName.size();

                }
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.add_post));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_post));
        }

        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
            isResourseChange = Flinnt.FALSE;
        }
        mImageLoader = Requester.getInstance().getImageLoader();

        attachment_list_linear = (LinearLayout) findViewById(R.id.attachment_list_linear);
        attachmentIconLinear = (LinearLayout) findViewById(R.id.attachment_icon_linear);
        courseTitleLinear = (LinearLayout) findViewById(R.id.course_title_linear);


        courseNameTextview = (TextView) findViewById(R.id.course_name_text);
        descriptionEditTxt = (EditText) findViewById(R.id.description_edit);
        selectCourseImg = (ImageView) findViewById(R.id.course_edit_img);
        attachHorizontalscroll = (HorizontalScrollView) findViewById(R.id.attach_horizontalscroll);

        attachedUserRelative = (RelativeLayout) findViewById(R.id.attached_user_relative);
        selectedTeacherTxt = (TextView) findViewById(R.id.selected_teacher_text);
        selectedLearnerTxt = (TextView) findViewById(R.id.selected_learner_text);
        attachmentTxt = (TextView) findViewById(R.id.attachment_text);
        attachedUsersRemoveImg = (ImageView) findViewById(R.id.attached_user_remove_image);
        courseImg = (ImageView) findViewById(R.id.course_photo_image);

        attachmentImg = (ImageView) findViewById(R.id.attachment_image);
        albumImg = (ImageView) findViewById(R.id.album_image);
        templateImg = (ImageView) findViewById(R.id.template_image);
        usersImg = (ImageView) findViewById(R.id.users_image);
        scheduleImg = (ImageView) findViewById(R.id.schedule_image);

        attachedUserRelative.setOnClickListener(this);
        attachment_list_linear.setOnClickListener(this);
        attachmentIconLinear.setOnClickListener(this);
        attachedUsersRemoveImg.setOnClickListener(this);
        courseTitleLinear.setOnClickListener(this);

        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        attachPhotosLinearLayout = (LinearLayout) findViewById(R.id.attach_file_linear);
        attachPhotosLinearLayout.removeAllViews();


        descriptionEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    attachment_list_linear.setVisibility(View.GONE);
                    attachmentIconLinear.setVisibility(View.VISIBLE);
                    showHorizontalView(Flinnt.DISABLED);
                    attachHorizontalscroll.setVisibility(View.GONE);
                    attachedUserRelative.setVisibility(View.GONE);
                } else {
                    attachment_list_linear.setVisibility(View.VISIBLE);
                    attachmentIconLinear.setVisibility(View.GONE);
                    attachHorizontalscroll.setVisibility(View.GONE);
                    if (getUserIDsJsonArray().length() > 0) {
                        attachedUserRelative.setVisibility(View.VISIBLE);
                    }
                    hideSoftKeyboard(AddCommunicationActivity.this, v);
                    showHorizontalView(Flinnt.ENABLED);
                }
            }
        });

        attachment_list_linear.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.section_options_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddPostResponse) {
                            if (((AddPostResponse) message.obj).getData().getIsAdded() == Flinnt.TRUE && comeFromActivity.equals("ViewersActivity") ||
                                    ((AddPostResponse) message.obj).getData().getIsRepost() == Flinnt.TRUE) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("userArray", selectedViewersIDs);
                                setResult(Activity.RESULT_OK, resultIntent);
                                Helper.showToast(getResources().getString(R.string.communication_add), Toast.LENGTH_LONG);
                                finish();
                            } else if (((AddPostResponse) message.obj).getData().getIsAdded() == Flinnt.TRUE ||
                                    ((AddPostResponse) message.obj).getData().getIsRepost() == Flinnt.TRUE) {
                                Helper.showToast(getResources().getString(R.string.communication_add), Toast.LENGTH_LONG);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(CourseDetailsActivity.ADDED_CONTENT_DATA, (AddPostResponse) message.obj);
                                setResult(Activity.RESULT_OK, resultIntent);
                                AddPostResponse.COURSE_ID = mCourseID;
                                finish();
                            } else if (((AddPostResponse) message.obj).getData().getIsEdited() == Flinnt.TRUE) {
                                Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("position", mPosition);
                                resultIntent.putExtra(Post.POST_ID_KEY, mPostID);
                                setResult(RESULT_OK, resultIntent);
                                Helper.showToast(getResources().getString(R.string.communication_edit), Toast.LENGTH_LONG);
                                finish();
                            }
                        } else if (message.obj instanceof SelectUserTabResponse) {
                            List<String> tabItems = new ArrayList<String>();
                            for (int i = 0; i < ((SelectUserTabResponse) message.obj).getAllowedRoles().size(); i++) {
                                if (((SelectUserTabResponse) message.obj).getAllowedRoles().get(i) == Flinnt.COURSE_ROLE_TEACHER) {
                                    tabItems.add("Teachers");
                                } else if (((SelectUserTabResponse) message.obj).getAllowedRoles().get(i) == Flinnt.COURSE_ROLE_LEARNER) {
                                    tabItems.add("Learners");
                                }
                            }
                            tabTitle = tabItems.toArray(new CharSequence[tabItems.size()]);
                        } else if (message.obj instanceof PostAllowOptionsResponse) {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE PostAllowOptionsResponse : " + message.obj.toString());
                            if (!TextUtils.isEmpty(mCourseID)) {
                                if (Flinnt.SUCCESS == ((PostAllowOptionsResponse) message.obj).getData().getOptions().getCanAddAlbum()) {
                                    if (getUserIDsJsonArray().length() > 0) {
                                        messageCourseChangeDialog((PostAllowOptionsResponse) message.obj, getResources().getString(R.string.user_romove_message));
                                    } else {
                                        AddCommunicationShowHide((PostAllowOptionsResponse) message.obj);
                                    }
                                } else {
                                    if (getUserIDsJsonArray().length() > 0 && comeFromViewerActivityFirst.equals(Flinnt.DISABLED) && mPostStat != Flinnt.POST_MESSAGE_EDIT) {
                                        messageCourseChangeDialog((PostAllowOptionsResponse) message.obj, getResources().getString(R.string.user_romove_message));
                                    } else {
                                        if (comeFromActivity.equals("ViewersActivity") || mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST || mPostStat == Flinnt.POST_BLOG_REPOST || !(uploadFilePathStringArray.size() > 0)) {
                                            AddCommunicationShowHide((PostAllowOptionsResponse) message.obj);
                                        } else {
                                            messageCourseChangeDialog((PostAllowOptionsResponse) message.obj, getResources().getString(R.string.attachment_romove_message));
                                        }
                                        comeFromViewerActivityFirst = Flinnt.DISABLED;
                                    }
                                }
                            } else {
                                AddCommunicationShowHide((PostAllowOptionsResponse) message.obj);
                            }
                        } else if (message.obj instanceof ResourceValidationResponse) {
                            mResourceValidation = (ResourceValidationResponse) message.obj;
                        }

                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddPostResponse) {
                            AddPostResponse response = (AddPostResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(AddCommunicationActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }

                        break;
                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
                            isResourseChange = Flinnt.TRUE;
                        }
                        if (!TextUtils.isEmpty(lastAttachedImagePath) && fileType != FileType.link) {
                            sendRequest(message.obj.toString());
                        } else {
                            resourseIDsArr.add(message.obj.toString());
                            if (resourseIDsArr.size() == (uploadFilePathStringArray.size() - attachmentName.size())) {
                                stopProgressDialog();
                                sendRequest(getJSONArrayOfResourseIDs(resourseIDsArr));
                            }
                        }

                        break;
                    case UploadMediaFile.UPLOAD_FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");
                        if (fileType == FileType.video) {
                            attachFilePathString = Helper.getFlinntVideoPath() + attachmentName.get(0);
                            MediaHelper.showVideo(attachFilePathString, AddCommunicationActivity.this, mCommon);
                        } else if (fileType == FileType.audio) {
                            attachFilePathString = Helper.getFlinntAudioPath() + attachmentName.get(0);
                            MediaHelper.showAudio(attachFilePathString, AddCommunicationActivity.this, mCommon);
                        } else if (fileType == FileType.pdf) {
                            attachFilePathString = Helper.getFlinntDocumentPath() + attachmentName.get(0);
                            MediaHelper.showDocument(attachFilePathString, AddCommunicationActivity.this);
                        }
                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        mDownload = null;
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        if (!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                            Helper.showAlertMessage(AddCommunicationActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
                        }
                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        if (TextUtils.isEmpty(courseNameStr)) {
            selectCourseImg.setBackgroundResource(R.drawable.ic_course_select);
        } else {
            courseNameTextview.setText(courseNameStr);
            selectCourseImg.setBackgroundResource(R.drawable.ic_course_edit);
        }

        if (mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            selectCourseImg.setBackgroundResource(R.drawable.ic_course_edit);
        }

        if (!TextUtils.isEmpty(mCourseID)) {
            sendSelectUserTabRequest();
            updateCommunicationView();
        } else {
            createItems();
        }

        mAdapter = new CommunicationItemAdapter(itemsShowHide, AddCommunicationActivity.this);
        recyclerView.setAdapter(mAdapter);

        JSONArray usersIDsArray = new JSONArray();
        JSONArray teacherArray = new JSONArray();
        JSONArray studentArray = new JSONArray();
        selectedViewersIDs = new ArrayList<>();
        String[] teacherUserIDs = teachersIdsString.split(",");
        String[] studentUserIDs = studentsIdsString.split(",");

        for (int i = 0; i < teacherUserIDs.length; i++) {
            if (!TextUtils.isEmpty(teacherUserIDs[i])) {
                usersIDsArray.put(teacherUserIDs[i]);
                teacherArray.put(teacherUserIDs[i]);
                selectedViewersIDs.add(teacherUserIDs[i]);
            }
        }
        for (int i = 0; i < studentUserIDs.length; i++) {
            if (!TextUtils.isEmpty(studentUserIDs[i])) {
                usersIDsArray.put(studentUserIDs[i]);
                studentArray.put(studentUserIDs[i]);
                selectedViewersIDs.add(studentUserIDs[i]);
            }
        }
        setUserIDsJsonArray(usersIDsArray);
        setTeacherIDsJsonArray(teacherArray);
        setStudentIDsJsonArray(studentArray);


        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_MESSAGE_ADD) {
            if (getPostContentTypeMedia() != Flinnt.INVALID) {
                if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO && postContentUrl.equals("1")) {
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    fileType = FileType.link;
                    defaultFileType = FileType.link;
                    attachFilePathString = attachmentName.get(0);
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), attachmentName.get(0));
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_GALLERY) {
                    fileType = FileType.image;
                    defaultFileType = FileType.image;
                    File file = new File(Helper.getFlinntImagePath() + File.separator + attachmentName.get(0));
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), attachmentName.get(0));
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("Bitmap : " + photoThumb);
                        if (photoThumb != null) {
                            addAttachedPhotoView(photoThumb, attachmentName.get(0));
                            currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                        }
                    } else {
                        addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_album), attachmentName.get(0));
                    }

                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_AUDIO) {
                    fileType = FileType.audio;
                    defaultFileType = FileType.audio;
                    File file = new File(Helper.getFlinntAudioPath() + File.separator + attachmentName.get(0));
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_audio), attachmentName.get(0));
                    } else {
                        addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.youtube_video_fram_not_get), attachmentName.get(0));
                    }
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_DOCUMENT) {

                    File file = new File(Helper.getFlinntDocumentPath() + File.separator + attachmentName.get(0));
                    fileType = FileType.pdf;
                    defaultFileType = FileType.pdf;
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_pdf), attachmentName.get(0));
                    } else {
                        addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.youtube_video_fram_not_get), attachmentName.get(0));
                    }
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                    attachFilePathString = attachmentName.get(0);
                    fileType = FileType.link;
                    defaultFileType = FileType.link;
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), attachmentName.get(0));
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO) {
                    File file = new File(Helper.getFlinntVideoPath() + File.separator + attachmentName.get(0));
                    fileType = FileType.video;
                    defaultFileType = FileType.video;

                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = ThumbnailUtils.createVideoThumbnail(attachFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                        if (photoThumb != null) {
                            addAttachedPhotoView(photoThumb, attachmentName.get(0));
                        }
                    } else {
                        photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_video);
                        addAttachedPhotoView(photoThumb, attachmentName.get(0));
                    }
                }
            }
            reFilledData();
        } else if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_ALBUM) {
                fileType = FileType.image;
                defaultFileType = FileType.image;
                for (int i = 0; i < attachmentName.size(); i++) {
                    String attachmentFileName = attachmentName.get(i);

                    File file = new File(Helper.getFlinntImagePath() + File.separator + attachmentFileName);
                    Bitmap photoThumb;
                    if (file.exists()) {
                        photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), attachmentFileName);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("Bitmap : " + photoThumb);
                        if (photoThumb != null) {
                            addAttachedPhotoView(photoThumb, attachmentFileName);
                        }
                    } else {
                        photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_album);
                        addAttachedPhotoView(photoThumb, attachmentFileName);
                    }
                }
            }
            reFilledData();
        }

        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION);
        resourceValidation.sendResourceValidationRequest();

        final View activityRootView = findViewById(R.id.coordinatorLayout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(AddCommunicationActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    attachHorizontalscroll.setVisibility(View.GONE);
                    attachedUserRelative.setVisibility(View.GONE);
                } else {
                    attachHorizontalscroll.postDelayed(new Runnable() {
                        public void run() {
                            attachHorizontalscroll.setVisibility(View.VISIBLE);
                            if (getUserIDsJsonArray().length() > 0) {
                                attachedUserRelative.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 500);
                }
            }
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    /**
     * In edit and repost mode, original details to be filled in again to UI
     */
    private void reFilledData() {
        courseNameTextview.setText(courseNameStr);
        descriptionEditTxt.setText(descriptionStr);
        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
            selectCourseImg.setVisibility(View.GONE);
            courseTitleLinear.setClickable(false);
        }
        if (mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_MESSAGE_ADD) {
            showHorizontalView(Flinnt.ENABLED);
            attachedUserRelative.setVisibility(View.VISIBLE);
            attachedUsersRemoveImg.setVisibility(View.GONE);
            selectedTeacherTxt.setText(TEACHERS + teachersCount);
            selectedLearnerTxt.setText(LEARNERS + studentsCount);
        }
        if (mPostStat == Flinnt.POST_ALBUM_EDIT) {
            selectCourseImg.setVisibility(View.GONE);
            courseTitleLinear.setClickable(false);
        }
    }


    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attachment_list_linear:
                break;
            case R.id.attachment_icon_linear:
                hideSoftKeyboard(AddCommunicationActivity.this, v);
                descriptionEditTxt.clearFocus();
                attachmentIconLinear.setVisibility(View.GONE);
                attachment_list_linear.setVisibility(View.VISIBLE);
                attachment_list_linear.startAnimation(animSlideUp);
                showHorizontalView(Flinnt.ENABLED);
                break;
            case R.id.course_title_linear:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    Intent selectCourse = new Intent(AddCommunicationActivity.this, SelectCourseActivity.class);
                    if (comeFromActivity.equals("ViewersActivity")) {
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_MESSAGE);
                    } else {
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_ALL);
                    }

                    if (mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_ALBUM_REPOST) {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_REPOST);
                        selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_BLOG);
                    } else {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_ADD);
                    }
                    startActivityForResult(selectCourse, COURSE_INFO_CALL_BACK);
                }
                break;
            case R.id.attached_user_relative:
                selectionUsers();
                break;
            case R.id.attached_user_remove_image:
                attachedUserRelative.setVisibility(View.GONE);
                resetSelectUsers();
                if (uploadFilePathStringArray.size() > 0 && attachmentName.size() > 0) {
                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                } else {
                    currentPostType = Flinnt.FALSE;
                }
                break;

            default:
                break;
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
                Helper.hideKeyboardFromWindow(AddCommunicationActivity.this);
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    if (TextUtils.isEmpty(mCourseID)) {
                        Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.select_course), getString(R.string.close_txt));
                        return false;
                    }
                    if (mPostAllowOptionsResponse != null) {
                        if (validatePost()) {
                            if (!TextUtils.isEmpty(lastAttachedImagePath) && getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                                isResourseChange = Flinnt.TRUE;
                                sendRequest("");
                            } else if (!TextUtils.isEmpty(lastAttachedImagePath) && fileType != FileType.link) {
                                new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                                {
                                    mProgressDialog = Helper.getProgressDialog(AddCommunicationActivity.this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
                                    if (mProgressDialog != null) mProgressDialog.show();
                                }
                            } else if (uploadFilePathStringArray.size() > 0) {
                                mProgressDialog = Helper.getProgressDialog(AddCommunicationActivity.this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
                                if (mProgressDialog != null) mProgressDialog.show();
                                for (int i = 0; i < uploadFilePathStringArray.size(); i++) {
                                    if (uploadFilePathStringArray.get(i).contains(".upload")) {
                                        new UploadMediaFile(this, mHandler, uploadFilePathStringArray.get(i), getPostContentTypeMedia()).execute();
                                    }
                                }
                            } else {
                                sendRequest("");
                            }

                            try {
                                MyCommFun.sendTracker(this, "activity=" + Flinnt.ADD_COMMUNOCATION + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID);
                                GoogleAnalytics.getInstance(this).reportActivityStart(this);
                            } catch (Exception e) {
                                LogWriter.err(e);
                            }
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
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(AddCommunicationActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddCommunicationActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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


    /**
     * Sends a request to send a message
     *
     * @param resourseID IDs of attached media resource that are going to sent along with message
     */
    private void sendRequest(String resourseID) {

        String descStr = descriptionEditTxt.getText().toString();
        AddPostRequest addPostRequest = new AddPostRequest();
        addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addPostRequest.setCourseID(mCourseID);
        if (mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_ALBUM_REPOST)
            addPostRequest.setRepostCourseID(mRepostCourseId);

        addPostRequest.setDescription(descStr);
        addPostRequest.setToUsers(getUserIDsJsonArray());

        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            addPostRequest.setPostID(mPostID);
            if (attachmentNameArrSize != attachmentName.size()) {
                isResourseChange = Flinnt.TRUE;
            }
            addPostRequest.setResourseChanged(isResourseChange);
        }


        if (!TextUtils.isEmpty(templateId)) {
            addPostRequest.setPostTemplateID(templateId);
        }
        if (getPostContentTypeMedia() != Flinnt.INVALID && currentPostType != Flinnt.POST_TYPE_ALBUM && !TextUtils.isEmpty(lastAttachedImagePath)) {
            addPostRequest.setPostContantType(getPostContentTypeMedia());
            if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                addPostRequest.setPostUrl(uploadFilePathString);
            }
        }

        if (currentPostType == Flinnt.POST_TYPE_ATTACHMENT || currentPostType == Flinnt.POST_TYPE_MESSAGE) {
            if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                addPostRequest.setPostUrl(uploadFilePathString);
            } else {
                if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
                    if (isResourseChange == Flinnt.TRUE)
                        addPostRequest.setResourseID(resourseID);
                } else {
                    addPostRequest.setResourseID(resourseID);
                }
            }
        }

        if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            addPostRequest.setAlbumImages(getJSONArrayOfAlbumImages(attachmentName));
        } else {
            addPostRequest.setResourseID(resourseID);
        }

        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
            addPostRequest.setPubYear(mScheduleYear);
            addPostRequest.setPubMonth(mScheduleMonth);
            addPostRequest.setPubDay(mScheduleDay);
            addPostRequest.setPubHour(mScheduleHour);
            addPostRequest.setPubMinute(mScheduleMinute);
        }

        if (mPostStat != Flinnt.INVALID) {
            new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
            startProgressDialog();
        }
    }

    private void sendRequest(JSONArray resourseIDs) {

        String descStr = descriptionEditTxt.getText().toString();
        AddPostRequest addPostRequest = new AddPostRequest();
        addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addPostRequest.setCourseID(mCourseID);
        addPostRequest.setDescription(descStr);
        addPostRequest.setToUsers(getUserIDsJsonArray());

        if (mPostStat == Flinnt.POST_ALBUM_REPOST || mPostStat == Flinnt.POST_BLOG_REPOST) {
            addPostRequest.setRepostCourseID(mRepostCourseId);
        }

        if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            addPostRequest.setPostID(mPostID);
            if ((resourseIDs != null && resourseIDs.length() > 0) || attachmentNameArrSize != attachmentName.size()) {
                isResourseChange = Flinnt.TRUE;
            } else {
                isResourseChange = Flinnt.FALSE;
            }
            addPostRequest.setResourseChanged(isResourseChange);
        }

        if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
            if (resourseIDs != null && resourseIDs.length() > 0) {
                addPostRequest.setResourseIDs(resourseIDs);
            }
            addPostRequest.setAlbumImages(getJSONArrayOfAlbumImages(attachmentName));
        }

        if (!TextUtils.isEmpty(templateId)) {
            addPostRequest.setPostTemplateID(templateId);
        }
        if (getPostContentTypeMedia() != Flinnt.INVALID && currentPostType != Flinnt.POST_TYPE_ALBUM) {
            addPostRequest.setPostContantType(getPostContentTypeMedia());
        }

        if (currentPostType == Flinnt.POST_TYPE_ALBUM) {
            addPostRequest.setAlbumResources(resourseIDs);
        }
        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
            addPostRequest.setPubYear(mScheduleYear);
            addPostRequest.setPubMonth(mScheduleMonth);
            addPostRequest.setPubDay(mScheduleDay);
            addPostRequest.setPubHour(mScheduleHour);
            addPostRequest.setPubMinute(mScheduleMinute);
        }

        if (mPostStat != Flinnt.INVALID) {
            new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
            startProgressDialog();
        }
    }


    /**
     * Checks for the validity of the message to be sent
     *
     * @return true if valid, false otherwise
     */
    private boolean validatePost() {
        if (TextUtils.isEmpty(mCourseID)) {
            Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.select_course), getString(R.string.close_txt));
            return false;
        } else if (TextUtils.isEmpty(descriptionEditTxt.getText().toString())) {
            Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.add_description), getString(R.string.close_txt));
            return false;
        } else if (Flinnt.FAILURE == mPostAllowOptionsResponse.getData().getOptions().getCanAddAlbum()) {
            if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_MESSAGE_EDIT || mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST || mPostStat == Flinnt.POST_BLOG_REPOST) {
            } else {
                if (getUserIDsJsonArray() == null || getUserIDsJsonArray().length() < 1) {
                    Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.select_user_message), getString(R.string.close_txt));
                    return false;
                }
            }
        } else if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
            if (mScheduleYear == Flinnt.INVALID) {
                Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.select_date), getString(R.string.close_txt));
                return false;
            } else if (mScheduleHour == Flinnt.INVALID) {
                Helper.showAlertMessage(AddCommunicationActivity.this, getResources().getString(R.string.add_post), getString(R.string.select_time), getString(R.string.close_txt));
                return false;
            }
        }
        return true;
    }

    /**
     * Add item to the bottom sheet
     */
    public void createItems() {

        if (mPostStat == Flinnt.POST_COMMUNICATION_ADD) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_attachment, getResources().getString(R.string.bottom_attachment)));
            itemsShowHide.add(new CommunicationItem(R.drawable.post_album, getResources().getString(R.string.bottom_album)));
            itemsShowHide.add(new CommunicationItem(R.drawable.post_template, getResources().getString(R.string.bottom_template)));
            itemsShowHide.add(new CommunicationItem(R.drawable.post_users, getResources().getString(R.string.bottom_users)));
            itemsShowHide.add(new CommunicationItem(R.drawable.post_schedule, getResources().getString(R.string.bottom_schedule)));

            attachmentImg.setVisibility(View.VISIBLE);
            albumImg.setVisibility(View.VISIBLE);
            templateImg.setVisibility(View.VISIBLE);
            usersImg.setVisibility(View.VISIBLE);
            scheduleImg.setVisibility(View.VISIBLE);

        }
    }

    /**
     * Send Action based on post status
     */

    public void updateCommunicationView() {
        startProgressDialog();
        PostAllowOptions postAllowOptions = null;
        if (mPostStat == Flinnt.POST_BLOG_REPOST) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_BLOG, Flinnt.ADD_COMMUNICATION_ACTION_REPOST);
        } else if (mPostStat == Flinnt.POST_ALBUM_REPOST) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_ALBUM, Flinnt.ADD_COMMUNICATION_ACTION_REPOST);
        } else if (mPostStat == Flinnt.POST_ALBUM_EDIT) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_ALBUM, Flinnt.ADD_COMMUNICATION_ACTION_EDIT);
        } else if (mPostStat == Flinnt.POST_BLOG_EDIT) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_BLOG, Flinnt.ADD_COMMUNICATION_ACTION_EDIT);
        } else if (mPostStat == Flinnt.POST_MESSAGE_EDIT) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_MESSAGE, Flinnt.ADD_COMMUNICATION_ACTION_EDIT);
        } else if (mPostStat == Flinnt.POST_MESSAGE_ADD) {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.POST_TYPE_MESSAGE, Flinnt.ADD_COMMUNICATION_ACTION_ADD);
        } else {
            postAllowOptions = new PostAllowOptions(mHandler, changeCourseId, Flinnt.FALSE, Flinnt.ADD_COMMUNICATION_ACTION_ADD);
        }
        postAllowOptions.sendPostAllowOptionsRequest();
    }

    /**
     * show/hide bottom sheet item based on condition
     */

    public void AddCommunicationShowHide(PostAllowOptionsResponse postAllowOptionsResponse) {
        mPostAllowOptionsResponse = postAllowOptionsResponse;
        itemsShowHide.clear();
        if (Flinnt.SUCCESS == postAllowOptionsResponse.getData().getOptions().getCanAddAttachment()) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_attachment, getResources().getString(R.string.bottom_attachment)));
            attachmentImg.setVisibility(View.VISIBLE);
        } else {
        }

        if (Flinnt.SUCCESS == postAllowOptionsResponse.getData().getOptions().getCanAddAlbum()) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_album, getResources().getString(R.string.bottom_album)));
            albumImg.setVisibility(View.VISIBLE);
            descriptionEditTxt.setHint(getResources().getString(R.string.attachment_description_hint));
            courseNameTextview.setText(changeCourseName);
            mCourseID = changeCourseId;
        } else {
            if (selectedImages != null) {
                selectedImages.clear();
                uploadFilePathStringArray.clear();
                attachPhotosLinearLayout.removeAllViews();
                showHideBottomSheet();
            }
            if (!TextUtils.isEmpty(templateId)) {
                descriptionEditTxt.setText("");
            }
            descriptionEditTxt.setHint(getResources().getString(R.string.attachment_description_hint_learner));
            currentPostType = Flinnt.FALSE;
            courseNameTextview.setText(changeCourseName);
            mCourseID = changeCourseId;
            templateId = "";
        }
        if (Flinnt.SUCCESS == postAllowOptionsResponse.getData().getOptions().getCanSelectTemplate()) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_template, getResources().getString(R.string.bottom_template)));
            templateImg.setVisibility(View.VISIBLE);
        } else {

        }
        if (Flinnt.SUCCESS == postAllowOptionsResponse.getData().getOptions().getCanSelectUsers()) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_users, getResources().getString(R.string.bottom_users)));
            usersImg.setVisibility(View.VISIBLE);
        } else {

        }
        if (Flinnt.SUCCESS == postAllowOptionsResponse.getData().getOptions().getCanSchedule()) {
            itemsShowHide.add(new CommunicationItem(R.drawable.post_schedule, getResources().getString(R.string.bottom_schedule)));
            scheduleImg.setVisibility(View.VISIBLE);
        } else {

        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(CommunicationItem item) {
        if (!TextUtils.isEmpty(mCourseID)) {



            switch (item.getDrawableResource()) {
                case R.drawable.post_attachment:
                    if (currentPostType == Flinnt.POST_TYPE_ALBUM) {
                        messageDialog(getResources().getString(R.string.switch_to_attachment), Flinnt.DISABLED);
                    } else {
                        openBottomSheet();
                        showHideBottomSheet();
                    }
                    break;
                case R.drawable.post_album:
                    if (currentPostType == Flinnt.POST_TYPE_MESSAGE) {
                        messageDialog(getResources().getString(R.string.switch_to_album), Flinnt.DISABLED);
                    } else if (currentPostType == Flinnt.POST_TYPE_ATTACHMENT) {
                        messageDialog(getResources().getString(R.string.switch_to_attachment), Flinnt.DISABLED);
                    } else {

                        if (AskPermition.getInstance(AddCommunicationActivity.this).isPermitted()) {
                            selectAlbum();
                            showHideBottomSheet();
                        }

                    }
                    break;
                case R.drawable.post_template:
                    Intent templateIntent = new Intent(this, SelectTempleteActivity.class);
                    startActivityForResult(templateIntent, TEMPLATES_CALL_BACK);
                    showHideBottomSheet();
                    break;
                case R.drawable.post_users:
                    if (currentPostType == Flinnt.POST_TYPE_ALBUM) {
                        messageDialog(getResources().getString(R.string.switch_to_album), Flinnt.ENABLED);
                    } else {
                        selectionUsers();
                    }
                    break;
                case R.drawable.post_schedule:
                    Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
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
            Helper.showAlertMessage(AddCommunicationActivity.this, getString(R.string.add_post), getString(R.string.select_course), getString(R.string.close_txt));
        }
    }


    public void showHideBottomSheet() {
        attachment_list_linear.setVisibility(View.GONE);
        attachmentIconLinear.setVisibility(View.VISIBLE);
        showHorizontalView(Flinnt.DISABLED);
    }

    public void showHorizontalView(String below) {

        attachHorizontalscroll.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams pram = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams pramUser = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.users_layout_width), (int) getResources().getDimension(R.dimen.users_layout_height));
        pramUser.addRule(RelativeLayout.RIGHT_OF, R.id.attach_horizontalscroll);
        pramUser.setMargins(15, 0, 0, 15);
        if (below.equals(Flinnt.ENABLED)) {
            pram.addRule(RelativeLayout.ABOVE, R.id.attachment_list_linear);
            pramUser.addRule(RelativeLayout.ABOVE, R.id.attachment_list_linear);
        } else {
            pram.addRule(RelativeLayout.ABOVE, R.id.attachment_icon_linear);
            pramUser.addRule(RelativeLayout.ABOVE, R.id.attachment_icon_linear);
        }
        attachHorizontalscroll.setLayoutParams(pram);
        attachedUserRelative.setLayoutParams(pramUser);
    }





/**
 * Attachment bottom sheet code
 */

    /**
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {

        new BottomSheet.Builder(AddCommunicationActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title("Attachment")
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {


                        if (AskPermition.getInstance(AddCommunicationActivity.this).isPermitted()) {
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

                                case R.id.attach_bottom_video:
                                    //toast("Video");
                                    fileType = FileType.video;
                                    dialogContents(getString(R.string.choose_action), getString(R.string.record_with_flinnt), getString(R.string.choose_video_track));
                                    break;

                                case R.id.attach_bottom_audio:
                                    //toast("Audio");
                                    fileType = FileType.audio;
                                    dialogContents(getString(R.string.choose_action), getString(R.string.record_with_flinnt), getString(R.string.choose_video_track));
                                    break;

                                case R.id.attach_bottom_link:
                                    //toast("Link");

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
                    }
                }).show();
    }


    /**
     * Dialog to display upload options
     *
     * @param title         dialog mResorceTitleTxt
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
     *
     * @param title dialog mResorceTitleTxt
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
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    if (link.startsWith("http://") || link.startsWith("https://")) {
                        uploadFilePathString = link;
                    } else {
                        uploadFilePathString = "http://" + link;
                    }
                    attachPhotosLinearLayout.removeAllViews();
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), uploadFilePathString);
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                    lastAttachedImagePath = uploadFilePathString;
                    fileType = FileType.link;
                    defaultFileType = fileType;
                } else {
                    Helper.showAlertMessage(AddCommunicationActivity.this, "Invalid Link", "Add valid link", getString(R.string.close_txt));
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
                if (storageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                break;

            case video:
                storageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                storageIntent.setType(type + "/*");
                if (storageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                break;

            case audio:
                storageIntent = new Intent();

                List<Intent> targetShareIntents = new ArrayList<Intent>();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_GET_CONTENT);
                shareIntent.setType("audio/*");
                List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
                if (!resInfos.isEmpty()) {
                    for (ResolveInfo resInfo : resInfos) {
                        String packageName = resInfo.activityInfo.packageName;
                        Log.i("Package Name", packageName);
                        // com.google.android.apps.docs - Drive // com.dropbox.android - DropBox
                        if (!packageName.contains("google") && !packageName.contains("dropbox")) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("audio/*");
                            intent.setPackage(packageName);
                            targetShareIntents.add(intent);
                        }
                    }
                    if (!targetShareIntents.isEmpty()) {
                        Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose file from");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                        startActivityForResult(chooserIntent, RESULT_FROM_STORAGE);
                    }
                }
                break;

            case pdf:

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    storageIntent = new Intent(this, PickDocFolderActivity.class);
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                } else {
                    List<Intent> targetShareIntents1 = new ArrayList<Intent>();
                    Intent shareIntent1 = new Intent();
                    shareIntent1.setAction(Intent.ACTION_GET_CONTENT);
                    shareIntent1.setType("application/pdf");
                    List<ResolveInfo> resInfos1 = getPackageManager().queryIntentActivities(shareIntent1, 0);

                    if (!resInfos1.isEmpty()) {
                        for (ResolveInfo resInfo : resInfos1) {
                            String packageName = resInfo.activityInfo.packageName;
                            Log.i("Package Name", packageName);
                            // com.google.android.apps.docs - Drive // com.dropbox.android - DropBox
                            if (!packageName.contains("google") && !packageName.contains("dropbox")) {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                intent.setPackage(packageName);
                                targetShareIntents1.add(intent);
                            }
                        }
                        if (!targetShareIntents1.isEmpty()) {
                            Intent chooserIntent = Intent.createChooser(targetShareIntents1.remove(0), "Choose file from");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents1.toArray(new Parcelable[]{}));
                            startActivityForResult(chooserIntent, RESULT_FROM_STORAGE);
                        } else {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("Add Message : targetShareIntentsl empty...");
                            storageIntent = new Intent(this, PickDocFolderActivity.class);
                            startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                        }
                    } else {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("Add Message : resInfos1 empty...");
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

        switch (fileType) {
            case image:
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_GALLERY);
                if (null != uploadFile) {
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

                    captureIntent.putExtra("video_widths", 480);
                    captureIntent.putExtra("video_heights", 640);

                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
                    }
                }
                break;

            case audio:
                try {
                    uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);

                    Intent recordAudio = new Intent(this, Recorder.class);
                    recordAudio.putExtra("FilePath", uploadFilePathString);
                    Validation validation = getValidation(FileType.audio);
                    if (null != validation) {
                        recordAudio.putExtra(Validation.MAX_FILE_SIZE_KEY, validation.getMaxFileSizeLong());
                        recordAudio.putExtra(Validation.FILE_TYPES_KEY, validation.getFileTypes());
                    }
                    if (recordAudio.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(recordAudio, RESULT_FROM_RECORDERS);
                    }
                } catch (Exception e) {
                    LogWriter.err(e);
                }
                break;
            default:
                //toast("File type : " + fileType.name());
                break;
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
     * Converts image attachments name list to JSONArray
     *
     * @param attachmentName Attached image names
     * @return Converted JsonArray of attached image names
     */
    private JSONArray getJSONArrayOfAlbumImages(ArrayList<String> attachmentName) {
        JSONArray albumImageJson = new JSONArray();
        for (int i = 0; i < attachmentName.size(); i++) {
            albumImageJson.put(attachmentName.get(i));
        }
        return albumImageJson;
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
            if (new File(ImagePath).length() > (1 * 1024 * 1024)) {
                bitmapOptions = setBitmapFactoryOptions(bitmapOptions);
            } else {
                bitmapOptions.inSampleSize = 1;
            }

            final Bitmap originalBitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);

            if (LogWriter.isValidLevel(Log.ERROR))
                LogWriter.write("originalBitmap : Height : " + originalBitmap.getHeight()
                        + ", Width : " + originalBitmap.getWidth());
            if (originalBitmap.getHeight() < validation.getMinHeight()) {
                uploadFilePathString = "";
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File height is smaller then Minimum Height.");
                Helper.showAlertMessage(this, "Error", validateResolutionMessage(FileType.image), getString(R.string.close_txt));
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, "Error", validateResolutionMessage(FileType.image), getString(R.string.close_txt));
                return false;
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return true;
    }

    private BitmapFactory.Options setBitmapFactoryOptions(BitmapFactory.Options bitmapOptions) {
        // TODO Auto-generated method stub
        bitmapOptions.inScaled = false;
        bitmapOptions.inDither = false;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOptions.inSampleSize = 2;
        //bitmapOptions.inSampleSize = 1;
        return bitmapOptions;
    }

    /**
     * User Selection Dharmendra
     */

    public void selectionUsers() {
        if (TextUtils.isEmpty(mCourseID)) {
            Helper.showAlertMessage(AddCommunicationActivity.this, getString(R.string.add_post), getString(R.string.select_course), getString(R.string.close_txt));
        } else {
            String teachersIdsStr = "";
            String studentsIdsStr = "";

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("getUserIDsJsonArray() : " + getUserIDsJsonArray());
            if (getUserIDsJsonArray() != null) {
                for (int i = 0; i < getTeacherIDsJsonArray().length(); i++) {
                    try {
                        teachersIdsStr = teachersIdsStr + getTeacherIDsJsonArray().get(i) + ",";
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }

                }

                for (int i = 0; i < getStudentIDsJsonArray().length(); i++) {
                    try {
                        studentsIdsStr = studentsIdsStr + getStudentIDsJsonArray().get(i) + ",";
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }

            } else {
                if (getIntent().getExtras().containsKey(MessageUsers.MESSAGE_USER_TEACHER))
                    teachersIdsStr = getIntent().getExtras().getString(MessageUsers.MESSAGE_USER_TEACHER);

                if (getIntent().getExtras().containsKey(MessageUsers.MESSAGE_USER_LEARNER))
                    studentsIdsStr = getIntent().getExtras().getString(MessageUsers.MESSAGE_USER_LEARNER);

            }


            Intent selectUser = new Intent(AddCommunicationActivity.this, SelectUsersActivity.class);
            selectUser.putExtra(Course.COURSE_ID_KEY, mCourseID);
            selectUser.putExtra(SelectUsersActivity.TAB_TITLE, tabTitle);
            if (!TextUtils.isEmpty(studentsIdsStr))
                selectUser.putExtra(MessageUsers.MESSAGE_USER_LEARNER, studentsIdsStr);
            if (!TextUtils.isEmpty(teachersIdsStr))
                selectUser.putExtra(MessageUsers.MESSAGE_USER_TEACHER, teachersIdsStr);
            startActivityForResult(selectUser, USER_INFO_CALL_BACK);
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

        JSONArray usersIDsArray = new JSONArray();
        JSONArray teacherArray = new JSONArray();
        JSONArray studentArray = new JSONArray();

        setUserIDsJsonArray(usersIDsArray);
        setTeacherIDsJsonArray(teacherArray);
        setStudentIDsJsonArray(studentArray);

    }

    /**
     * Converts the media resource IDs' String ArrayList to JsonArray
     *
     * @param resIds Attached media resource Ids' String ArrayList
     * @return Converted JsonArray of attached media resource Ids' String ArrayList
     */
    private JSONArray getJSONArrayOfResourseIDs(ArrayList<String> resIds) {
        JSONArray resIDsJson = new JSONArray();
        for (int i = 0; i < resIds.size(); i++) {
            resIDsJson.put(resIds.get(i));
        }
        return resIDsJson;
    }

    public JSONArray getStudentIDsJsonArray() {
        return postStudentIDsJsonArray;
    }

    public void setStudentIDsJsonArray(JSONArray studentIDsJsonArray) {
        this.postStudentIDsJsonArray = studentIDsJsonArray;
    }

    public JSONArray getTeacherIDsJsonArray() {
        return postTeacherIDsJsonArray;
    }

    public void setTeacherIDsJsonArray(JSONArray teacherIDsJsonArray) {
        this.postTeacherIDsJsonArray = teacherIDsJsonArray;
    }

    /**
     * Sends request to get the list of roles to be added to the tabs
     */
    private void sendSelectUserTabRequest() {
        new SelectUserTab(mHandler, Config.getStringValue(Config.USER_ID), changeCourseId).sendSelectUserTabRequest();
    }

    /**
     * @return selected userIDs JSONAArray
     */
    public JSONArray getUserIDsJsonArray() {
        return postSelectedUserIDsJsonArray;
    }

    /**
     * Set User Ids to Json Array locally
     *
     * @param userIDsJsonArray user ids json array
     */
    public void setUserIDsJsonArray(JSONArray userIDsJsonArray) {
        this.postSelectedUserIDsJsonArray = userIDsJsonArray;
    }


    /**
     * Select Album Dharmendra
     */

    public void selectAlbum() {

        // openBottomSheet();
        int selection = 10;
        if (attachmentName != null) {
            selection = 10 - attachmentName.size();
        }
        Intent intent = new Intent(AddCommunicationActivity.this, ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, selection);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_TITLE, "Tap to select");
        startActivityForResult(intent, RESULT_FROM_ALBUM_STORAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mCommon) {
            mCommon = new Common(this);
        }
    }

    //	Intent Results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : " + fileType + "\nData Uri : " + data);

        Uri contentUri;
        try {
            if (resultCode == RESULT_OK) {
                AttachmentValidation attachmentValidation = new AttachmentValidation(this);
                if (requestCode == RESULT_FROM_STORAGE) {
                    switch (fileType) {
                        case image:

                            contentUri = data.getData();
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("contentUri : " + contentUri);

                            String fileTest = MediaHelper.getPath(this, contentUri);
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmapOptions.inSampleSize = 1;
                            Bitmap uploadBitmap = BitmapFactory.decodeFile(fileTest, bitmapOptions);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                        + ", Height : " + uploadBitmap.getHeight());


                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + fileTest);
                            if (attachmentValidation.isValidFile(fileTest, AttachmentValidation.FileType.image, false)) {
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Valid Image File");
                                uploadFilePathString = MediaHelper.getPath(this, contentUri);
                                Validation validation = getValidation(AddCommunicationActivity.FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                             /*   BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                                bitmapOptions.inSampleSize = 1;
                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                            + ", Height : " + uploadBitmap.getHeight());*/
                                if (uploadBitmap == null) {
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.image), getString(R.string.close_txt));
                                    return;
                                } else {

                                }
                                if (uploadBitmap != null) {
                                    attachPhotosLinearLayout.removeAllViews();
                                    addAttachedPhotoView(uploadBitmap, uploadFilePathString);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;
                                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                    defaultFileType = fileType;
                                }
                            } else {
                                fileType = defaultFileType;
                            }
                            break;

                        case video:
                            //uploadFilePathString = getFilePathFromContentURI(data.getData());
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                                if (photoThumb == null) {
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", AddCommunicationActivity.wrongSizedFileMessage(FileType.video), getString(R.string.close_txt));
                                    return;
                                }
                                if (photoThumb != null) {
                                    attachPhotosLinearLayout.removeAllViews();
                                    addAttachedPhotoView(photoThumb, uploadFilePathString);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                    lastAttachedImagePath = uploadFilePathString;
                                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                    defaultFileType = fileType;
                                }
                            } else {
                                fileType = defaultFileType;
                            }
                            break;
                        case audio:
                            contentUri = data.getData();
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("contentUri : " + contentUri);
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, FileType.audio)) {
                                attachPhotosLinearLayout.removeAllViews();
                                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_audio), uploadFilePathString);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                lastAttachedImagePath = uploadFilePathString;
                                currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                defaultFileType = fileType;
                            } else {
                                fileType = defaultFileType;
                            }
                            break;
                        case pdf:
                            contentUri = data.getData();
                            if (null == contentUri || Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                uploadFilePathString = data.getStringExtra("result");
                            } else {
                                uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            }
                            if (uploadFilePathString == null) {
                                driveURItoFile(contentUri);
                                break;
                            }
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, FileType.pdf)) {
                                attachPhotosLinearLayout.removeAllViews();
                                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_pdf), uploadFilePathString);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_DOCUMENT);
                                lastAttachedImagePath = uploadFilePathString;
                                currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                defaultFileType = fileType;
                            } else {
                                fileType = defaultFileType;
                            }
                            break;
                        default:
                            break;
                    }
                } else if (requestCode == RESULT_FROM_ALBUM_STORAGE) {
                    if (selectedImages != null) {
                        selectedImages.clear();
                        uploadFilePathStringArray.clear();
                    } else {
                        selectedImages = new ArrayList<>();
                    }
                    attachPhotosLinearLayout.removeAllViews();
                    if (attachmentName != null && attachmentName.size() > 0) {

                        for (int i = 0; i < attachmentName.size(); i++) {
                            String attachmentFileName = attachmentName.get(i);

                            File file = new File(Helper.getFlinntImagePath() + File.separator + attachmentFileName);
                            Bitmap photoThumb;
                            if (file.exists()) {

                                photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), attachmentFileName);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("Bitmap : " + photoThumb);
                                if (photoThumb != null) {
                                    addAttachedPhotoView(photoThumb, attachmentFileName);
                                }
                            } else {
                                photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_album);
                                addAttachedPhotoView(photoThumb, attachmentFileName);
                            }
                            uploadFilePathStringArray.add(attachmentName.get(i));
                        }
                    }
                    ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    for (int i = 0; i < images.size(); i++) {
                        String filePathString = images.get(i).getPath();
                        if (LogWriter.isValidLevel(Log.ERROR))
                            LogWriter.write("uploadFilePathStringArray :: " + uploadFilePathStringArray);
                        if (filePathString != null && isValidFile(filePathString, FileType.image)) {
                            selectedImages.add(images.get(i));
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("Valid Image File");

                            Validation validation = getValidation(FileType.image);
                            String compressFilePathString = mCommon.compressImage(filePathString, validation.getMinHeight(), validation.getMinWidth(), validation.getScaleMaxHeight(), validation.getScaleMaxWidth());
                            uploadFilePathStringArray.add(compressFilePathString);
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmapOptions.inSampleSize = 1;

                            Bitmap uploadBitmap = BitmapFactory.decodeFile(compressFilePathString, bitmapOptions);
                            if (uploadBitmap == null) {
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Invalid file");
                                Helper.showAlertMessage(this, "Error", AddCommunicationActivity.wrongSizedFileMessage(FileType.image), getString(R.string.close_txt));
                                //  return;
                            }
                            if (uploadBitmap != null) {
                                addAttachedPhotoView(uploadBitmap, compressFilePathString);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                lastAttachedImagePath = "";
                                currentPostType = Flinnt.POST_TYPE_ALBUM;
                                defaultFileType = fileType;
                            }
                        } else {
                            fileType = defaultFileType;
                        }
                    }
                } else if (requestCode == RESULT_FROM_RECORDERS) {
                    switch (fileType) {
                        case image:
                            if (isValidFile(uploadFilePathString, FileType.image)) {
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("Valid Image File");
                                Validation validation = getValidation(FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                                bitmapOptions.inSampleSize = 1;
                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                            + ", Height : " + uploadBitmap.getHeight());

                                if (uploadBitmap != null) {
                                    attachPhotosLinearLayout.removeAllViews();
                                    addAttachedPhotoView(uploadBitmap, uploadFilePathString);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;
                                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                    defaultFileType = fileType;
                                }
                            } else {
                                fileType = defaultFileType;
                            }
                            break;

                        case video:
                            if (isValidFile(uploadFilePathString, FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                                if (photoThumb != null) {
                                    attachPhotosLinearLayout.removeAllViews();
                                    addAttachedPhotoView(photoThumb, uploadFilePathString);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                    lastAttachedImagePath = uploadFilePathString;
                                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                    defaultFileType = fileType;
                                }
                            } else {
                                fileType = defaultFileType;
                            }
                            break;

                        case audio:
                            isFromCustomRecorder = true;
                            if (isValidFile(uploadFilePathString, FileType.audio)) {
                                attachPhotosLinearLayout.removeAllViews();
                                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_audio), uploadFilePathString);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                lastAttachedImagePath = uploadFilePathString;
                                currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                                defaultFileType = fileType;
                            } else {
                                fileType = defaultFileType;
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    switch (requestCode) {
                        case COURSE_INFO_CALL_BACK:
                            if (resultCode == Activity.RESULT_OK) {
                                changeCourseName = data.getStringExtra(CourseInfo.COURSE_NAME_KEY);
                                if (mPostStat == Flinnt.POST_ALBUM_REPOST || mPostStat == Flinnt.POST_BLOG_REPOST) {
                                    mRepostCourseId = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
                                } else {
                                    changeCourseId = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
                                }
                                sendSelectUserTabRequest();
                                updateCommunicationView();
                                selectCourseImg.setBackgroundResource(R.drawable.ic_course_edit);
                            }
                            break;
                        case TEMPLATES_CALL_BACK:
                            if (resultCode == Activity.RESULT_OK) {
                                templateId = data.getStringExtra(Template.POST_TEMPLATE_ID_KEY);
                                templateName = data.getStringExtra(Template.POST_TEMPLATE_NAME_KEY);
                                templateTitle = data.getStringExtra(Template.POST_TEMPLATE_TITLE_KEY);
                                templateDescription = data.getStringExtra(Template.POST_TEMPLATE_DESCRIPTION_KEY);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("templateTitle : " + templateTitle);
                                descriptionEditTxt.setText(templateTitle + "\n\n" + templateDescription);

                            }
                            break;
                        case USER_INFO_CALL_BACK:
                            selectedViewersIDs = new ArrayList<>();
                            ArrayList<String> teacherUserIDs = data.getStringArrayListExtra(SelectUsersActivity.SELECTED_TEACHERS_IDS);
                            ArrayList<String> studentUserIDs = data.getStringArrayListExtra(SelectUsersActivity.SELECTED_STUDENTS_IDS);
                            teachersCount = data.getIntExtra(SelectUsersActivity.SELECTED_TEACHERS_COUNT, 0);
                            studentsCount = data.getIntExtra(SelectUsersActivity.SELECTED_STUDENTS_COUNT, 0);

                            attachedUserRelative.setVisibility(View.VISIBLE);

                            selectedTeacherTxt.setText(TEACHERS + teachersCount);
                            selectedLearnerTxt.setText(LEARNERS + studentsCount);

                            JSONArray usersIDsArray = new JSONArray();
                            JSONArray teacherArray = new JSONArray();
                            JSONArray studentArray = new JSONArray();
                            for (int i = 0; i < teacherUserIDs.size(); i++) {
                                usersIDsArray.put(teacherUserIDs.get(i));
                                teacherArray.put(teacherUserIDs.get(i));
                                selectedViewersIDs.add(teacherUserIDs.get(i));

                            }
                            for (int i = 0; i < studentUserIDs.size(); i++) {
                                usersIDsArray.put(studentUserIDs.get(i));
                                studentArray.put(studentUserIDs.get(i));
                                selectedViewersIDs.add(studentUserIDs.get(i));
                            }
                            setUserIDsJsonArray(usersIDsArray);
                            setTeacherIDsJsonArray(teacherArray);
                            setStudentIDsJsonArray(studentArray);
                            currentPostType = Flinnt.POST_TYPE_MESSAGE;

                            break;
                        case SCHEDULE_CALL_BACK:
                            if (resultCode == Activity.RESULT_OK) {
                                mPublishStats = Flinnt.PUBLISH_SCHEDULE;
                                mScheduleYear = Integer.parseInt(data.getStringExtra("mScheduleYear"));
                                mScheduleMonth = Integer.parseInt(data.getStringExtra("mScheduleMonth"));
                                mScheduleDay = Integer.parseInt(data.getStringExtra("mScheduleDay"));
                                mScheduleHour = Integer.parseInt(data.getStringExtra("mScheduleHour"));
                                mScheduleMinute = Integer.parseInt(data.getStringExtra("mScheduleMinute"));
                            }

                            break;
                        default:
                            break;
                    }
                }
            } else {
                fileType = defaultFileType;
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("You didn't pick any file ");
            }
        } catch (Exception e) {
            fileType = defaultFileType;
            LogWriter.err(e);
        }
    }

    /**
     * upload document from drive
     */

    private void driveURItoFile(final Uri contentUri) {
        startProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFilePathString = createLocalFile(contentUri);
                Looper.prepare();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.ERROR))
                            LogWriter.write("uploadFilePathString :: " + uploadFilePathString);


                        if (isValidFile(uploadFilePathString, AddCommunicationActivity.FileType.pdf)) {
                            attachPhotosLinearLayout.removeAllViews();
                            addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_pdf), uploadFilePathString);
                            setPostContentTypeMedia(Flinnt.POST_CONTENT_DOCUMENT);
                            lastAttachedImagePath = uploadFilePathString;
                            currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                            defaultFileType = fileType;
                        } else {
                            fileType = defaultFileType;
                        }
                    }
                });
            }
        }).start();


    }

    private String createLocalFile(Uri contentUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String filePath = null;
        try {
            String mimeType = getContentResolver().getType(contentUri);
            Cursor returnCursor =
                    getContentResolver().query(contentUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            inputStream = getContentResolver().openInputStream(contentUri);

            // write the inputStream to a FileOutputStream
            outputStream =
                    new FileOutputStream(new File(Helper.getFlinntDocumentPath() + returnCursor.getString(nameIndex)));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            filePath = Helper.getFlinntDocumentPath() + returnCursor.getString(nameIndex);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return filePath;
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
            mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION);
        }
        if (null != mResourceValidation) {
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
        if (filePath == null) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file - null filepath");
            Helper.showAlertMessage(this, "Error", wrongSizedFileMessage(type), getString(R.string.close_txt));
            return false;
        }

        Validation validation = getValidation(type);
        if (null != validation) {
            File file = new File(filePath);
            String path = file.getPath();
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("validation :: " + validation.toString());
            long length = file.length();
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("File length : " + length);

            if (length <= 0) {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", wrongSizedFileMessage(type), getString(R.string.close_txt));
                return false;
            }

            if (length >= validation.getMaxFileSizeLong()) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, "Error", validateSizeMessage(type), getString(R.string.close_txt));
                return false;
            }

            if (!TextUtils.isEmpty(validation.getFileTypes())) {
                String fileExtention = path.substring(path.lastIndexOf(".") + 1);
                if (fileType == FileType.pdf) {
                    fileExtention = fileExtention.toLowerCase();
                }
                ArrayList<String> extentions = new ArrayList<String>(Arrays.asList(validation.getFileTypes().split(",")));

                if (!extentions.contains(fileExtention) && !isFromCustomRecorder) {
                    uploadFilePathString = "";
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write("This file type is not supported.");
                    Helper.showAlertMessage(this, "Error", validateSuppotedFileMessage(type), getString(R.string.close_txt));
                    return false;
                }
            }

            switch (type) {
                case image:
                    ret = validateImage(file.getPath(), validation);
                    break;
                case audio:
                    //ret = validateAudio(file.getPath(), validation);
                    break;
                case video:
                    //ret = validateVideo(file.getPath(), validation);
                    break;
                case pdf:
                    //ret = validateDocument(file.getPath(), validation);
                    break;

                default:
                    break;
            }
        }
        return ret;
    }

    /**
     * Gets file size invalid message
     *
     * @param filetype type of file
     * @return validation message
     */
    public static String validateSizeMessage(FileType filetype) {
        String validationMsg = "";

        switch (filetype) {
            case image:
                validationMsg = "Use 5 MB or fewer size image";
                break;
            case audio:
                validationMsg = "Use 10 MB or fewer size audio";
                break;
            case video:
                validationMsg = "Use 10 MB or fewer size video";
                break;
            case pdf:
                validationMsg = "Use 5 MB or fewer size pdf";
                break;
            default:
                validationMsg = "Use fewer size file";
                break;
        }
        return validationMsg;
    }

    /**
     * Gets file type invalid message
     *
     * @param filetype type of file
     * @return validation message
     */
    public static String validateSuppotedFileMessage(FileType filetype) {
        String validationMsg = "This file type is not supported";

        switch (filetype) {
            case image:
                validationMsg = "This file type is not supported";
                break;
            case audio:
                validationMsg = "Select mp3 files only";
                break;
            case video:
                validationMsg = "Select mp4/3gp files only";
                break;
            case pdf:
                validationMsg = "Select pdf files only";
                break;
            default:
                validationMsg = "This file type is not supported";
                break;
        }

        return validationMsg;
    }

    /**
     * Gets file size invalid message
     *
     * @param filetype type of file
     * @return validation message
     */

    public static String wrongSizedFileMessage(FileType filetype) {
        String validationMsg;

        switch (filetype) {
            case image:
                validationMsg = "Wrong size image";
                break;
            case audio:
                validationMsg = "Wrong size audio";
                break;
            case video:
                validationMsg = "Wrong size video";
                break;
            case pdf:
                validationMsg = "Wrong size document";
                break;
            default:
                validationMsg = "Wrong size file";
                break;
        }

        return validationMsg;
    }

    /**
     * Gets image resolution validation message
     *
     * @param filetype type of file
     * @return resolution validation message
     */
    public static String validateResolutionMessage(FileType filetype) {
        String validationMsg = "";

        switch (filetype) {
            case image:
                validationMsg = "Add min. 300 x 200 px image";
                break;
            default:
                validationMsg = "Add min. 300 x 200 px image";
                break;
        }
        return validationMsg;
    }

    LinearLayout inflatedView;

    /**
     * Show attached file on UI
     *
     * @param photoThumb thumbnail of attached photo
     * @param imagePath  path of attached file
     */
    private void addAttachedPhotoView(Bitmap photoThumb, final String imagePath) {
        inflatedView = (LinearLayout) View.inflate(this, R.layout.attached_photo, null);
        final RelativeLayout relativeLayout = (RelativeLayout) inflatedView.findViewById(R.id.attached_file_layout);
        ImageView removeImage = (ImageView) inflatedView.findViewById(R.id.attached_file_remove);
        ImageView playImage = (ImageView) inflatedView.findViewById(R.id.media_open_imgbtn);
        if (fileType == FileType.video) {
            playImage.setVisibility(View.VISIBLE);
        } else {
            playImage.setVisibility(View.GONE);
        }
        final SelectableRoundedImageView addImage = (SelectableRoundedImageView) inflatedView.findViewById(R.id.attached_file_image);
        addImage.setImageBitmap(photoThumb);
        addImage.setVisibility(View.VISIBLE);
        addImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                            String filePathString = "";
                                            if (!TextUtils.isEmpty(imagePath)) {
                                                if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
                                                    File file = new File(imagePath);
                                                    if (file.exists()) {
                                                        filePathString = imagePath;
                                                    }
                                                    /*else {
                                                        File filenew = new File(Helper.getFlinntImagePath() + File.separator + imagePath);
                                                        filePathString = filenew.getAbsolutePath();
                                                    }*/
                                                } else if (mPostStat == Flinnt.POST_ALBUM_ADD || mPostStat == Flinnt.POST_COMMUNICATION_ADD) {
                                                    filePathString = imagePath;
                                                } else if (!TextUtils.isEmpty(uploadFilePathString)) {
                                                    filePathString = uploadFilePathString;
                                                } else {
                                                    filePathString = attachFilePathString;
                                                }
                                            }
                                            if (null != filePathString && !filePathString.isEmpty()) {
                                                switch (fileType) {
                                                    case image:
                                                        MediaHelper.showImage(filePathString, AddCommunicationActivity.this);
                                                        break;
                                                    case video:
                                                        MediaHelper.showVideo(filePathString, AddCommunicationActivity.this, mCommon);
                                                        break;
                                                    case audio:
                                                        MediaHelper.showAudio(filePathString, AddCommunicationActivity.this, mCommon);
                                                        break;
                                                    case pdf:
                                                        MediaHelper.showDocument(filePathString, AddCommunicationActivity.this);
                                                        break;
                                                    case link:
                                                        MediaHelper.ShowLink(filePathString, mCommon);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            } else {
                                                switch (fileType) {
                                                    case video:
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), attachmentUrl)) {
                                                            String videourl = attachmentUrl + attachmentName.get(0);
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("videourl : " + videourl);
                                                            mDownload = new DownloadMediaFile(AddCommunicationActivity.this, Helper.getFlinntVideoPath(), attachmentName.get(0), Long.parseLong(mPostID), videourl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();
                                                        }
                                                        break;

                                                    case audio:
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), attachmentUrl)) {
                                                            String audiourl = attachmentUrl + attachmentName.get(0);
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("audiourl : " + audiourl);
                                                            mDownload = new DownloadMediaFile(AddCommunicationActivity.this, Helper.getFlinntAudioPath(), attachmentName.get(0), Long.parseLong(mPostID), audiourl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();

                                                        }
                                                        break;

                                                    case pdf:
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), attachmentUrl)) {
                                                            String docurl = attachmentUrl + attachmentName.get(0);
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("docurl : " + docurl);
                                                            mDownload = new DownloadMediaFile(AddCommunicationActivity.this, Helper.getFlinntDocumentPath(), attachmentName.get(0), Long.parseLong(mPostID), docurl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();

                                                        }
                                                        break;
                                                    case image:
                                                        final File file = new File(Helper.getFlinntImagePath() + File.separator + imagePath);
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), attachmentUrl)) {
                                                            startProgressDialog();
                                                            DownloadMediaFile mDownload = new DownloadMediaFile(AddCommunicationActivity.this, Helper.getFlinntImagePath(), imagePath, Long.parseLong(mPostID), attachmentUrl + Flinnt.GALLERY_MOBILE + File.separator + imagePath, new Handler(new Handler.Callback() {
                                                                @Override
                                                                public boolean handleMessage(Message msg) {
                                                                    stopProgressDialog();
                                                                    switch (msg.what) {
                                                                        case DownloadMediaFile.DOWNLOAD_COMPLETE:
                                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                                LogWriter.write("Download Complete");
                                                                            MediaHelper.showImage(file.getAbsolutePath(), AddCommunicationActivity.this);
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
                                                                                Helper.showAlertMessage(AddCommunicationActivity.this, "Download Fail", msg.obj.toString(), getString(R.string.close_txt));
                                                                            }
                                                                            break;
                                                                    }
                                                                    return false;
                                                                }
                                                            }));
                                                            mDownload.execute();
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }

        );

        removeImage.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View arg0) {
                                               relativeLayout.setVisibility(View.GONE);
                                               int position = uploadFilePathStringArray.indexOf(imagePath);
                                               if (selectedImages != null) {
                                                   if (mPostStat == Flinnt.POST_ALBUM_EDIT || mPostStat == Flinnt.POST_ALBUM_REPOST) {
                                                       if (position >= attachmentName.size()) {
                                                           selectedImages.remove(position - attachmentName.size());
                                                       }
                                                   } else if (position != -1 && selectedImages.size() > position) {
                                                       selectedImages.remove(position - attachmentName.size());
                                                   }
                                               }
                                               if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST || mPostStat == Flinnt.POST_MESSAGE_EDIT) {
                                                   isResourseChange = Flinnt.TRUE;
                                               }
                                               if (currentPostType == Flinnt.POST_TYPE_ATTACHMENT || currentPostType == Flinnt.POST_TYPE_MESSAGE) {
                                                   lastAttachedImagePath = "";
                                               }
                                               if (uploadFilePathStringArray.contains(imagePath)) {
                                                   uploadFilePathStringArray.remove(imagePath);
                                               }

                                               if (attachmentName.contains(imagePath)) {
                                                   attachmentName.remove(imagePath);
                                               }
                                               if (uploadFilePathStringArray.size() == 0 && attachmentName.size() == 0 && getUserIDsJsonArray().length() == 0) {
                                                   currentPostType = Flinnt.FALSE;
                                               }
                                           }
                                       }

        );

        attachPhotosLinearLayout.addView(inflatedView);
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Attached Photos View added");
        //checkAttachedImageCount();
    }

    public void messageDialog(String message, final String disabled) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCommunicationActivity.this);
        TextView titleText = new TextView(AddCommunicationActivity.this);
        titleText.setText("Alert");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (currentPostType == Flinnt.POST_TYPE_ATTACHMENT || currentPostType == Flinnt.POST_TYPE_MESSAGE) {
                    attachPhotosLinearLayout.removeAllViews();
                    attachedUserRelative.setVisibility(View.GONE);
                    resetSelectUsers();
                    selectAlbum();
                    showHideBottomSheet();
                    currentPostType = Flinnt.FALSE;
                } else if (currentPostType == Flinnt.POST_TYPE_ALBUM) {
                    selectedImages.clear();
                    uploadFilePathStringArray.clear();
                    if (disabled.equals(Flinnt.ENABLED)) {
                        selectionUsers();
                    } else {
                        openBottomSheet();
                    }
                    attachPhotosLinearLayout.removeAllViews();
                    showHideBottomSheet();
                    currentPostType = Flinnt.FALSE;
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (!Helper.isFinishingOrIsDestroyed(AddCommunicationActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }
    }


    //------New Change
    public void messageCourseChangeDialog(final PostAllowOptionsResponse mPostAllowResponse, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCommunicationActivity.this);
        TextView titleText = new TextView(AddCommunicationActivity.this);
        titleText.setText("Alert");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        // alertDialogBuilder.setMessage("Oops! You cannot add Album to selected course as a Learner. Do you wish to remove Album?");
        alertDialogBuilder.setMessage(message);
        //   Oops! Selected users will be removed. Are your sure?
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetSelectUsers();
                attachedUserRelative.setVisibility(View.GONE);
                mPostAllowOptionsResponse = mPostAllowResponse;
                AddCommunicationShowHide(mPostAllowOptionsResponse);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (!Helper.isFinishingOrIsDestroyed(AddCommunicationActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        mProgressDialog = new ProgressDialog(AddCommunicationActivity.this);
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

    /**
     * Stops currently running download
     */
    private void stopDownload() {
        if (mDownload != null) {
            mDownload.setCancel(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(AddCommunicationActivity.this).reportActivityStop(AddCommunicationActivity.this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
}
