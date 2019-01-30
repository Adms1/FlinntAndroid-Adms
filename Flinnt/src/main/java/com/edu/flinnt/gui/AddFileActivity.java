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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddContent;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.protocol.AddContentRequest;
import com.edu.flinnt.protocol.AddContentResponse;
import com.edu.flinnt.protocol.ContentsShowHideDeleteRequest;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.Validation;
import com.edu.flinnt.util.AttachmentValidation;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.Recorder;
import com.edu.flinnt.util.SelectableRoundedImageView;
import com.edu.flinnt.util.UploadMediaFile;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.Func2;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;


/**
 * Created by flinnt-android-2 on 7/4/17.
 * For Add Content functionality
 */

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mTitleEdt;
    LinearLayout mPreviewLinear, mAttachmentLinear;
    HorizontalScrollView mAttachHorizontalscroll;
    CheckBox mPreviewChk;
    public static String FILE_NAME = "file_name";
    public static String IS_URL = "is_url";

    enum FileType {
        image, video, audio, pdf, link
    }

    FileType fileType = FileType.image;
    FileType defaultFileType = FileType.image;
    final int RESULT_FROM_STORAGE = 701;
    final int RESULT_FROM_RECORDERS = 702;
    public static String SECTION_POSTITION = "section_position";
    public static String CONTENT_POSTITION = "content_position";

    private File uploadFile;
    private String uploadFilePathString, attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;
    ResourceValidationResponse mResourceValidation;
    private int postContentTypeMedia = Flinnt.INVALID;
    int currentPostType = Flinnt.FALSE;
    private TextView galleryViewTxt;
    private TextView captureViewTxt;
    private EditText linkEdt;
    LinearLayout attachPhotosLinear;
    private String link;
    Common mCommon;
    private boolean isFromCustomRecorder = false;
    private Bitmap photoThumb;
    ProgressDialog mProgressDialog = null;
    public Handler mHandler = null;
    Toolbar toolbar;
    String mCourseID;
    String mSectionID;
    String mContentID;
    String mTitle;
    String mDescription;
    String mAttachmentUrl;
    String mAttachmentIsUrl;
    String mFileName;
    DownloadMediaFile mDownload;
    private int mContentStat = Flinnt.INVALID;
    int mCanPreview;
    private int isResourseChange = Flinnt.FALSE;
    String mPosition, mContentPosition;

    //*********** used for custom format tool bar
    private RTManager mRTManager;
    private RTEditText mDescriptionEdt;
    String message = null;

    private PermissionUtil.PermissionRequestObject mALLPermissionRequest;
    public void RequestAllPermission() {
        String[] allPermission = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.RECEIVE_SMS,};

        mALLPermissionRequest = PermissionUtil.with(this).request(allPermission).onResult(
                new Func2() {
                    @Override
                    protected void call(int requestCode, String[] permissions, int[] grantResults) {
                        for (int i = 0; i < permissions.length; i++) {

                        }
                    }
                }).onAllGranted(new Func() {
            @Override
            protected void call() {

            }
        }).ask(101);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_add_files);
/**
 * initialize rich text manager
 */
//@Nikhil 20062018
        RequestAllPermission();

        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        mRTManager = new RTManager(rtApi, savedInstanceState);

        //--------------------

        getBundleData();
        setUpToolBar();
        setUpFormatToolbar();
        initUI();
        sendRequestForResourceValidation();


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddContentResponse) {
                            AddContentResponse mAddCopntentResponse = (AddContentResponse) message.obj;
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(ContentEditActivity.CONTENT_DATA, mAddCopntentResponse.getData().getContent());
                            resultIntent.putExtra(AddFileActivity.SECTION_POSTITION, mPosition);
                            if (mContentStat == Flinnt.CONTENT_EDIT) {
                                resultIntent.putExtra(AddFileActivity.CONTENT_POSTITION, mContentPosition);
                            }
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                            if (mContentStat == Flinnt.CONTENT_EDIT) {
                                Helper.showToast(getResources().getString(R.string.content_update), Toast.LENGTH_LONG);
                            } else {
                                Helper.showToast(getResources().getString(R.string.content_add), Toast.LENGTH_LONG);
                            }

                        }
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddContentResponse) {
                            AddContentResponse response = (AddContentResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(AddFileActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }
                        break;
                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        if (mContentStat == Flinnt.CONTENT_EDIT) {
                            isResourseChange = Flinnt.TRUE;
                        }
                        if (!TextUtils.isEmpty(lastAttachedImagePath) && fileType != FileType.link) {
                            sendRequest(message.obj.toString());
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
                            attachFilePathString = Helper.getFlinntVideoPath() + mFileName;
                            MediaHelper.showVideo(attachFilePathString, AddFileActivity.this, mCommon);
                        } else if (fileType == FileType.audio) {
                            attachFilePathString = Helper.getFlinntAudioPath() + mFileName;
                            MediaHelper.showAudio(attachFilePathString, AddFileActivity.this, mCommon);
                        } else if (fileType == FileType.pdf) {
                            attachFilePathString = Helper.getFlinntDocumentPath() + mFileName;
                            MediaHelper.showDocument(attachFilePathString, AddFileActivity.this);
                        }
                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        mDownload = null;
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        if (!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                            Helper.showAlertMessage(AddFileActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
                        }
                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        final View activityRootView = findViewById(R.id.main_relative);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(AddFileActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    mAttachHorizontalscroll.setVisibility(View.GONE);
                } else {
                    mAttachHorizontalscroll.postDelayed(new Runnable() {
                        public void run() {
                            mAttachHorizontalscroll.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRTManager != null) {
            mRTManager.onDestroy(true);
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void getBundleData() {
        mContentStat = Flinnt.CONTENT_ADD;
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mContentStat = bundle.getInt(Flinnt.CONTENT_STATS_ACTION);
            if (bundle.containsKey(Course.COURSE_ID_KEY)) {
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            }
            if (bundle.containsKey(AddContentRequest.SECTION_ID_KEY)) {
                mSectionID = bundle.getString(AddContentRequest.SECTION_ID_KEY);
            }

            if (bundle.containsKey(ContentsShowHideDeleteRequest.CONTENT_ID)) {
                mContentID = bundle.getString(ContentsShowHideDeleteRequest.CONTENT_ID);
            }
            if (bundle.containsKey(AddContentRequest.TITLE_KEY)) {
                mTitle = bundle.getString(AddContentRequest.TITLE_KEY);
            }

            if (bundle.containsKey(AddContentRequest.DESCRIPTION_KEY)) {
                mDescription = bundle.getString(AddContentRequest.DESCRIPTION_KEY);
            }
            if (bundle.containsKey(AddContentRequest.CAN_PREVIEW_KEY)) {
                mCanPreview = parseInt(bundle.getString(AddContentRequest.CAN_PREVIEW_KEY));
            }

            if (bundle.containsKey(AddContentRequest.ATTACHMENT_TYPE_KEY)) {
                setPostContentTypeMedia(parseInt(bundle.getString(AddContentRequest.ATTACHMENT_TYPE_KEY)));
            }
            if (bundle.containsKey(AddContentRequest.ATTACHMENT_URL_KEY)) {
                mAttachmentUrl = bundle.getString(AddContentRequest.ATTACHMENT_URL_KEY);
            }

            if (bundle.containsKey(AddFileActivity.FILE_NAME)) {
                mFileName = bundle.getString(AddFileActivity.FILE_NAME);
            }
            if (bundle.containsKey(AddFileActivity.IS_URL)) {
                mAttachmentIsUrl = bundle.getString(AddFileActivity.IS_URL);
            }
            if (bundle.containsKey(AddFileActivity.SECTION_POSTITION)) {
                mPosition = bundle.getString(AddFileActivity.SECTION_POSTITION);
            }
            if (bundle.containsKey(AddFileActivity.CONTENT_POSTITION)) {
                mContentPosition = bundle.getString(AddFileActivity.CONTENT_POSTITION);
            }
        }
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.add_file));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Register toolbar and manage tool bar selection
     */
    private void setUpFormatToolbar() {

        final ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);

        // register toolbar 0 (if it exists)
        RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);
        if (rtToolbar != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar);
        }

        mDescriptionEdt = (RTEditText) findViewById(R.id.description_edt);
        mRTManager.registerEditor(mDescriptionEdt, true);
        if (message != null) {
            mDescriptionEdt.setRichTextEditing(true, message);
        }
        mDescriptionEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    toolbarContainer.setVisibility(View.VISIBLE);
                } else {
                    toolbarContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    /**
     * Initialize UI when in Add Mode
     */

    private void initUI() {
        mTitleEdt = (EditText) findViewById(R.id.titel_edt);
        // mDescriptionEdt = (EditText) findViewById(R.id.description_edt);
        mPreviewLinear = (LinearLayout) findViewById(R.id.preview_linear);
        mAttachmentLinear = (LinearLayout) findViewById(R.id.attachment_linear);
        mAttachmentLinear.setOnClickListener(this);
        mAttachHorizontalscroll = (HorizontalScrollView) findViewById(R.id.attach_horizontalscroll);
        mPreviewChk = (CheckBox) findViewById(R.id.preview_chk);
        attachPhotosLinear = (LinearLayout) findViewById(R.id.attach_file_linear);
        attachPhotosLinear.removeAllViews();

        mPreviewChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (arg0.isChecked()) {
                    mCanPreview = Flinnt.TRUE;
                } else {
                    mCanPreview = Flinnt.FALSE;
                }
            }
        });

        if (mContentStat == Flinnt.CONTENT_EDIT) {
            editUI();
        }
    }
    /**
     * Initialize UI when in Edit Mode
     */

    private void editUI() {
        currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
        mTitleEdt.setText(mTitle);
        mDescriptionEdt.setRichTextEditing(true, mDescription);
        if (mCanPreview == Flinnt.TRUE) {
            mPreviewChk.setChecked(true);
        } else {
            mPreviewChk.setChecked(false);
        }
        if (getPostContentTypeMedia() != Flinnt.INVALID) {
            if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO && mAttachmentIsUrl.equals(Flinnt.ENABLED)) {
                setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                fileType = AddFileActivity.FileType.link;
                defaultFileType = AddFileActivity.FileType.link;
                attachFilePathString = String.valueOf(mFileName);
                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), uploadFilePathString);
            } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_GALLERY) {
                fileType = AddFileActivity.FileType.image;
                defaultFileType = AddFileActivity.FileType.image;
                File file = new File(Helper.getFlinntImagePath() + File.separator + mFileName);
                if (file.exists()) {
                    attachFilePathString = file.getAbsolutePath();
                    photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), mFileName);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Bitmap : " + photoThumb);
                    if (photoThumb != null) {
                        addAttachedPhotoView(photoThumb, mFileName);
                        currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                    }
                } else {
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_album), mFileName);
                }

            } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_AUDIO) {
                fileType = AddFileActivity.FileType.audio;
                defaultFileType = AddFileActivity.FileType.audio;
                File file = new File(Helper.getFlinntAudioPath() + File.separator + mFileName);
                if (file.exists()) {
                    attachFilePathString = file.getAbsolutePath();
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_audio), mFileName);
                } else {
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_audio), mFileName);
                }
            } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_DOCUMENT) {

                File file = new File(Helper.getFlinntDocumentPath() + File.separator + mFileName);
                fileType = AddFileActivity.FileType.pdf;
                defaultFileType = AddFileActivity.FileType.pdf;
                if (file.exists()) {
                    attachFilePathString = file.getAbsolutePath();
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_pdf), mFileName);
                } else {
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_pdf), mFileName);
                }
            } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                attachFilePathString = mFileName;
                fileType = AddFileActivity.FileType.link;
                defaultFileType = AddFileActivity.FileType.link;
                addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), mFileName);
            } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO) {
                File file = new File(Helper.getFlinntVideoPath() + File.separator + mFileName);
                fileType = AddFileActivity.FileType.video;
                defaultFileType = AddFileActivity.FileType.video;
                if (file.exists()) {
                    attachFilePathString = file.getAbsolutePath();
                    photoThumb = ThumbnailUtils.createVideoThumbnail(attachFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                    if (photoThumb != null) {
                        addAttachedPhotoView(photoThumb, mFileName);
                    }
                } else {
                    photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_video);
                    addAttachedPhotoView(photoThumb, mFileName);
                }
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mCommon) {
            mCommon = new Common(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attachment_linear:
                openBottomSheet();
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
                Helper.hideKeyboardFromWindow(AddFileActivity.this);
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    if (validateContent()) {
                        if (!TextUtils.isEmpty(lastAttachedImagePath) && getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                            isResourseChange = Flinnt.TRUE;
                            sendRequest("");
                        } else if (!TextUtils.isEmpty(lastAttachedImagePath) && fileType != AddFileActivity.FileType.link) {
                            new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                            {
                                mProgressDialog = Helper.getProgressDialog(AddFileActivity.this, "", getString(R.string.upload_media), Helper.PROGRESS_DIALOG);
                                if (mProgressDialog != null) mProgressDialog.show();
                            }
                        } else {
                            sendRequest("");
                        }
                           /* try {
                                MyCommFun.sendTracker(this, "activity=" + Flinnt.ADD_COMMUNOCATION + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID);
                                GoogleAnalytics.getInstance(this).reportActivityStart(this);
                            } catch (Exception e) {
                                LogWriter.err(e);
                            }*/
                    }
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Checks for the validity
     *
     * @return true if valid, false otherwise
     */
    private boolean validateContent() {

        if (TextUtils.isEmpty(mTitleEdt.getText().toString()) || mTitleEdt.getText().toString().trim().length() < 2) {
            Helper.showAlertMessage(AddFileActivity.this, getString(R.string.add_file_content), getString(R.string.content_title_validation), getString(R.string.close_txt));
            return false;
        } else if (currentPostType == Flinnt.FALSE) {
            Helper.showAlertMessage(AddFileActivity.this, getString(R.string.add_file_content), getString(R.string.content_attachment_validation), getString(R.string.close_txt));
            return false;
        }
        return true;
    }


    private void sendRequest(String resourseID) {
        String titleStr = mTitleEdt.getText().toString();
        String desc = mDescriptionEdt.getText(RTFormat.HTML);

        AddContentRequest addContentRequest = new AddContentRequest();
        addContentRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addContentRequest.setCourseID(mCourseID);
        addContentRequest.setSectionID(mSectionID);
        addContentRequest.setTitle(titleStr);
        addContentRequest.setDescription(desc);
        addContentRequest.setCanPreview(String.valueOf(mCanPreview));

        if (mContentStat == Flinnt.CONTENT_EDIT) {
            addContentRequest.setContentID(mContentID);
            addContentRequest.setResourseChanged(isResourseChange);
            if (isResourseChange == Flinnt.TRUE) {
                addContentRequest.setAttachmentType(String.valueOf(getPostContentTypeMedia()));
            }
        } else {
            addContentRequest.setAttachmentType(String.valueOf(getPostContentTypeMedia()));
        }

        if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
            addContentRequest.setAttachmentUrl(uploadFilePathString);
        } else {
            addContentRequest.setResourceId(resourseID);
        }
        new AddContent(mHandler, addContentRequest, mContentStat).sendAddContentRequest();
        startProgressDialog();
    }


    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(AddFileActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddFileActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {
        new BottomSheet.Builder(AddFileActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title(R.string.attachment)
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

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
                }).show();
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
                storageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                storageIntent.setType(type + "/*");
                if (storageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                }
                break;

            case video:
                storageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
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

                    if (null != getValidation(AddFileActivity.FileType.video)) {
                        fileSize = getValidation(AddFileActivity.FileType.video).getMaxFileSizeLong();
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
                    Validation validation = getValidation(AddFileActivity.FileType.audio);
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
     * Dialog to display upload options
     *
     * @param title         dialog resorceTitleTxt
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

        galleryViewTxt = (TextView) dialog.findViewById(R.id.textview_gallery);
        captureViewTxt = (TextView) dialog.findViewById(R.id.textview_capture);

        galleryViewTxt.setText(textViewText2);
        captureViewTxt.setText(textViewText1);

        galleryViewTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
                chooseFromStorage(fileType.name());
            }
        });

        captureViewTxt.setOnClickListener(new View.OnClickListener() {

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
     * @param title dialog resorceTitleTxt
     */
    private void dialogContents(String title) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                link = linkEdt.getText().toString().trim();
                //descTxtview.setText(descTxtview.getText().toString() + "\nLink : " + link);
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    if (link.startsWith("http://") || link.startsWith("https://")) {
                        uploadFilePathString = link;
                    } else {
                        uploadFilePathString = "http://" + link;
                    }
                    attachPhotosLinear.removeAllViews();
                    addAttachedPhotoView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_link), uploadFilePathString);
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    currentPostType = Flinnt.POST_TYPE_ATTACHMENT;
                    lastAttachedImagePath = uploadFilePathString;
                    fileType = AddFileActivity.FileType.link;
                    defaultFileType = fileType;
                } else {
                    Helper.showAlertMessage(AddFileActivity.this, getString(R.string.invalid_lint), getString(R.string.add_valid_link), getString(R.string.close_txt));
                }

            }
        });
        builder.setView(R.layout.dialog_design_link);

        final AlertDialog dialog = builder.create();
        dialog.show();

        linkEdt = (EditText) dialog.findViewById(R.id.edittext_link_input);
        linkEdt.setSelection(linkEdt.getText().length());
    }

    /**
     * To get validation parameters
     *
     * @param type filetype
     * @return validation parameters of filetype
     */
    public Validation getValidation(AddFileActivity.FileType type) {
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
        final ImageView playImage = (ImageView) inflatedView.findViewById(R.id.media_open_imgbtn);
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
                                                if (!TextUtils.isEmpty(uploadFilePathString)) {
                                                    filePathString = uploadFilePathString;
                                                } else {
                                                    filePathString = attachFilePathString;
                                                }
                                            }
                                            if (null != filePathString && !filePathString.isEmpty()) {
                                                switch (fileType) {
                                                    case image:
                                                        MediaHelper.showImage(filePathString, AddFileActivity.this);
                                                        break;
                                                    case video:
                                                        MediaHelper.showVideo(filePathString, AddFileActivity.this, mCommon);
                                                        break;
                                                    case audio:
                                                        MediaHelper.showAudio(filePathString, AddFileActivity.this, mCommon);
                                                        break;
                                                    case pdf:
                                                        MediaHelper.showDocument(filePathString, AddFileActivity.this);
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
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), mAttachmentUrl)) {
                                                            String videourl = mAttachmentUrl + mFileName;
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("videourl : " + videourl);
                                                            mDownload = new DownloadMediaFile(AddFileActivity.this, Helper.getFlinntVideoPath(), mFileName, Long.parseLong(mContentID), videourl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();
                                                        }
                                                        break;

                                                    case audio:
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), mAttachmentUrl)) {
                                                            String audiourl = mAttachmentUrl + mFileName;
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("audiourl : " + audiourl);
                                                            mDownload = new DownloadMediaFile(AddFileActivity.this, Helper.getFlinntAudioPath(), mFileName, Long.parseLong(mContentID), audiourl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();

                                                        }
                                                        break;

                                                    case pdf:
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), mAttachmentUrl)) {
                                                            String docurl = mAttachmentUrl + mFileName;
                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                LogWriter.write("docurl : " + docurl);
                                                            mDownload = new DownloadMediaFile(AddFileActivity.this, Helper.getFlinntDocumentPath(), mFileName, Long.parseLong(mContentID), docurl, mHandler);
                                                            mDownload.execute();
                                                            setDownloadProgressDialog();

                                                        }
                                                        break;
                                                    case image:
                                                        final File file = new File(Helper.getFlinntImagePath() + File.separator + imagePath);
                                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), mAttachmentUrl)) {
                                                            startProgressDialog();
                                                            DownloadMediaFile mDownload = new DownloadMediaFile(AddFileActivity.this, Helper.getFlinntImagePath(), imagePath, Long.parseLong(mContentID), mAttachmentUrl + Flinnt.GALLERY_MOBILE + File.separator + imagePath, new Handler(new Handler.Callback() {
                                                                @Override
                                                                public boolean handleMessage(Message msg) {
                                                                    stopProgressDialog();
                                                                    switch (msg.what) {
                                                                        case DownloadMediaFile.DOWNLOAD_COMPLETE:
                                                                            if (LogWriter.isValidLevel(Log.INFO))
                                                                                LogWriter.write("Download Complete");
                                                                            MediaHelper.showImage(file.getAbsolutePath(), AddFileActivity.this);
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
                                                                                Helper.showAlertMessage(AddFileActivity.this, "Download Fail", msg.obj.toString(), "CLOSE");
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
                                               currentPostType = Flinnt.FALSE;
                                           }
                                       }

        );

        attachPhotosLinear.addView(inflatedView);
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Attached Photos View added");
        //checkAttachedImageCount();
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
                                Validation validation = getValidation(AddFileActivity.FileType.image);
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
                                    Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.image), "CLOSE");
                                    return;
                                } else {

                                }
                                if (uploadBitmap != null) {
                                    attachPhotosLinear.removeAllViews();
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
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.video, false)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                                if (photoThumb == null) {
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", attachmentValidation.wrongSizedFileMessage(AttachmentValidation.FileType.video), "CLOSE");
                                    return;
                                }
                                if (photoThumb != null) {
                                    attachPhotosLinear.removeAllViews();
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
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.audio, false)) {
                                attachPhotosLinear.removeAllViews();
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
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.pdf, false)) {
                                attachPhotosLinear.removeAllViews();
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
                } else if (requestCode == RESULT_FROM_RECORDERS) {
                    switch (fileType) {
                        case image:
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.image, true)) {
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("Valid Image File");
                                Validation validation = getValidation(AddFileActivity.FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                                bitmapOptions.inSampleSize = 1;
                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                            + ", Height : " + uploadBitmap.getHeight());

                                if (uploadBitmap != null) {
                                    attachPhotosLinear.removeAllViews();
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
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.video, true)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Video.Thumbnails.MICRO_KIND);
                                if (photoThumb != null) {
                                    attachPhotosLinear.removeAllViews();
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
                            if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.audio, true)) {
                                attachPhotosLinear.removeAllViews();
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

    private void driveURItoFile(final Uri contentUri) {
        final AttachmentValidation attachmentValidation = new AttachmentValidation(this);
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
                        if (attachmentValidation.isValidFile(uploadFilePathString, AttachmentValidation.FileType.pdf, false)) {
                            attachPhotosLinear.removeAllViews();
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
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        mProgressDialog = new ProgressDialog(AddFileActivity.this);
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
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.ADD_CONTENT + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseID);
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
