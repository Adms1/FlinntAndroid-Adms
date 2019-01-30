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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.edu.flinnt.core.PostTagsList;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostDetailsResponse;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.SelectCourseRequest;
import com.edu.flinnt.protocol.Template;
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
import com.edu.flinnt.util.Tags;
import com.edu.flinnt.util.TagsCompletionView;
import com.edu.flinnt.util.UploadMediaFile;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.tokenautocomplete.TokenCompleteTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener, TokenCompleteTextView.TokenListener<Tags>, DialogInterface.OnCancelListener {

    Toolbar toolbar;
    ScrollView mScrollView;
    LinearLayout schedulePublishPostLineatLayout;
    RelativeLayout scheduleDateRelative, scheduleTimeRelative, publishRelative, attachedFileRelative, templateRelative;

    TextView scheduleDateTxtview, scheduleTimeTxtview, courseNameTextview;
    ImageButton attachFile;
    ImageView selectCourse, attachedMediaThumb, attachedMediaRemove;
    EditText titleTxtview, descTxtview;
    private TagsCompletionView postTagsText;
    //RadioGroup publishPostGroup;

    Button buttonTemplate;
    String templateId, templateName, templateTitle, templateTags, templateDescription;

    private String mCourseID = "", mRepostCourseID = "", mPostID = "";

    private String mPublishStats = Flinnt.PUBLISH_NOW;
    private int mPostStat = Flinnt.INVALID;
    private int postContentTypeMedia = Flinnt.INVALID;

    private int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
            mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;
    private String dateFormat = "dd/MM/yy"; //In which you need put here
    private String timeFormat = "hh:mm a"; //In which you need put here

    Resources res = FlinntApplication.getContext().getResources();
    final Calendar mCalendar = Calendar.getInstance();
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;

    private int CHARACTER_LIMIT_POST_TITLE = 255;

    private String courseNameStr, postTitleStr, postDescStr, tagsNameStr, attachmentUrl, attachmentName, publishDateStr;
    private int postContentUrl = Flinnt.INVALID;

    // Dialog widgets
    TextView textViewGallery;
    TextView textViewCapture;
    EditText editTextLink;

    File uploadFile;

    private String uploadFilePathString, attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;
    private Bitmap imageFile;
    private Bitmap photoThumb;
    String link;

    private final int COURSE_INFO_CALL_BACK = 99;
    private final int TEMPLATES_CALL_BACK = 100;
    private final int RESULT_FROM_STORAGE = 101;
    private final int RESULT_FROM_RECORDERS = 102;

    AddMessageActivity.FileType fileType;
    ResourceValidationResponse mResourceValidation;
    Common mCommon;

    private int isResourseChange = Flinnt.FALSE;
    public static ArrayList<String> tagsListStatic = new ArrayList<String>();
    TagAdapter tagAdapter;
    ImageButton playButton;
    DownloadMediaFile mDownload;
    private boolean isFromCustomRecorder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.add_post);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mPostStat = bundle.getInt(Flinnt.POST_STATS_ACTION);
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(SelectCourseActivity.COURSE_ID_REPOST_KEY))
                mRepostCourseID = bundle.getString(SelectCourseActivity.COURSE_ID_REPOST_KEY);
            if (bundle.containsKey(CourseInfo.COURSE_NAME_KEY))
                courseNameStr = bundle.getString(CourseInfo.COURSE_NAME_KEY);
            if (bundle.containsKey(Post.POST_ID_KEY)) mPostID = bundle.getString(Post.POST_ID_KEY);
            if (bundle.containsKey(PostDetailsResponse.TITLE_KEY))
                postTitleStr = bundle.getString(PostDetailsResponse.TITLE_KEY);
            if (bundle.containsKey(PostDetailsResponse.DESCRIPTION_KEY))
                postDescStr = bundle.getString(PostDetailsResponse.DESCRIPTION_KEY);
            if (bundle.containsKey(PostDetailsResponse.TAG_NAME_KEY))
                tagsNameStr = bundle.getString(PostDetailsResponse.TAG_NAME_KEY);

            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_TYPE_KEY))
                setPostContentTypeMedia(bundle.getInt(PostDetailsResponse.POST_CONTENT_TYPE_KEY));
            if (bundle.containsKey(PostDetailsResponse.POST_CONTENT_URL_KEY))
                postContentUrl = bundle.getInt(PostDetailsResponse.POST_CONTENT_URL_KEY);
            if (bundle.containsKey(PostDetailsResponse.ATTACHMENT_URL_KEY))
                attachmentUrl = bundle.getString(PostDetailsResponse.ATTACHMENT_URL_KEY);
            if (bundle.containsKey(PostDetailsResponse.POST_ATTACHMENT_KEY))
                attachmentName = bundle.getString(PostDetailsResponse.POST_ATTACHMENT_KEY);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mPostStat == Flinnt.POST_BLOG_EDIT) {
            getSupportActionBar().setTitle("Edit Post");
        }

        Tags[] noTag = new Tags[]{new Tags("", "")};
        tagAdapter = new TagAdapter(this, R.layout.tag_dropdown_layout, noTag);
        tagsListStatic = new ArrayList<String>();

        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST) {
            isResourseChange = Flinnt.FALSE;
        }

        //attachPhotosLinearLayout = (LinearLayout) findViewById(R.id.attach_file_horizontalscroll_addpost);
        schedulePublishPostLineatLayout = (LinearLayout) findViewById(R.id.publish_linear_date_time_select_addpost);
        scheduleDateRelative = (RelativeLayout) findViewById(R.id.schedule_date_relative_addpost);
        scheduleTimeRelative = (RelativeLayout) findViewById(R.id.schedule_time_relative_addpost);
        publishRelative = (RelativeLayout) findViewById(R.id.publish_relative_main);
        attachedFileRelative = (RelativeLayout) findViewById(R.id.attached_file_layout);
        templateRelative = (RelativeLayout) findViewById(R.id.template_layout);

        mScrollView = (ScrollView) findViewById(R.id.scroll_addpost);
        scheduleDateTxtview = (TextView) findViewById(R.id.schedule_date_text_addpost);
        scheduleTimeTxtview = (TextView) findViewById(R.id.schedule_time_text_addpost);
        courseNameTextview = (TextView) findViewById(R.id.course_desc_addpost);
        titleTxtview = (EditText) findViewById(R.id.post_title_addpost);
        descTxtview = (EditText) findViewById(R.id.post_decription_addpost);
        descTxtview.setMaxLines(Integer.MAX_VALUE);

        postTagsText = (TagsCompletionView) findViewById(R.id.add_tags_addpost);
        postTagsText.performBestGuess(false);
        postTagsText.allowCollapse(false);
        postTagsText.allowDuplicates(false);
        postTagsText.setThreshold(1);
        postTagsText.setAdapter(tagAdapter);
        postTagsText.setTokenListener(this);
        postTagsText.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

        selectCourse = (ImageView) findViewById(R.id.course_edit_addpost);
        buttonTemplate = (Button) findViewById(R.id.button_choose_template);
        buttonTemplate.setOnClickListener(this);

        attachedMediaThumb = (ImageView) findViewById(R.id.attached_file_image);
        attachedMediaRemove = (ImageView) findViewById(R.id.attached_file_remove);

        attachedMediaThumb.setOnClickListener(this);
        attachedMediaRemove.setOnClickListener(this);
        //publishPostGroup = (RadioGroup) findViewById(R.id.publish_post_radiogroup_addpost);

        scheduleDateRelative.setOnClickListener(this);
        scheduleTimeRelative.setOnClickListener(this);
        selectCourse.setOnClickListener(this);

        playButton = (ImageButton) findViewById(R.id.imagebutton_preview_media);
        playButton.setOnClickListener(this);

        descTxtview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.post_decription_addpost) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        attachFile = (ImageButton) findViewById(R.id.attach_file_addpost);

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
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddPostResponse) {
                            Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("isEdited : " + ((AddPostResponse) message.obj).getIsEdited() + "  isRepost : " + ((AddPostResponse) message.obj).getIsRepost() + "  isAdded : " + ((AddPostResponse) message.obj).getIsAdded());
                            if (((AddPostResponse) message.obj).getIsAdded() == Flinnt.TRUE ||
                                    ((AddPostResponse) message.obj).getIsRepost() == Flinnt.TRUE) {
                                Helper.showToast("Post has been sent", Toast.LENGTH_LONG);
                                finish();
                            } else if (((AddPostResponse) message.obj).getIsEdited() == Flinnt.TRUE) {
                                Helper.showToast("Post has been updated", Toast.LENGTH_LONG);
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        } else if (message.obj instanceof ResourceValidationResponse) {
                            mResourceValidation = (ResourceValidationResponse) message.obj;
                        } else if (message.obj instanceof PostTagsList.PostTagsListResponse) {
                            updatePostTagsList((PostTagsList.PostTagsListResponse) message.obj);
                            // afer tag succes response
                        /*TagPickerAdapter tagPickerAdapter = new TagPickerAdapter(AddPostActivity.this,
								android.R.layout.simple_list_item_1, TagUtil.getTags(
									AddPostActivity.this, false));
						postTagsText.setAdapter(tagPickerAdapter);*/
                            String[] tagAr = ((PostTagsList.PostTagsListResponse) message.obj).getTags().split(",");

                            Tags[] tags = new Tags[tagAr.length];

                            for (int i = 0; i < tagAr.length; i++) {
                                tags[i] = new Tags(tagAr[i], tagAr[i]);
                            }

                            //tagAdapter.addItems(tags);
                            tagAdapter = new TagAdapter(AddPostActivity.this, R.layout.tag_dropdown_layout, tags);
                            postTagsText.setAdapter(tagAdapter);
                        }


                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof AddPostResponse) {
                            AddPostResponse response = (AddPostResponse) message.obj;
                            if (response != null) {
                                Helper.showAlertMessage(AddPostActivity.this, "Error", message.obj.toString(), "CLOSE");
                            }
                        }

                        break;

                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST) {
                            isResourseChange = Flinnt.TRUE;
                        }
                        sendRequest(message.obj.toString());

                        break;
                    case UploadMediaFile.UPLOAD_FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    case DownloadMediaFile.DOWNLOAD_COMPLETE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Complete");
                        if (fileType == AddMessageActivity.FileType.video) {
                            attachFilePathString = Helper.getFlinntVideoPath() + attachmentName;
                            MediaHelper.showVideo(attachFilePathString, AddPostActivity.this, mCommon);
                        } else if (fileType == AddMessageActivity.FileType.audio) {
                            attachFilePathString = Helper.getFlinntAudioPath() + attachmentName;
                            MediaHelper.showAudio(attachFilePathString, AddPostActivity.this, mCommon);
                        } else if (fileType == AddMessageActivity.FileType.pdf) {
                            attachFilePathString = Helper.getFlinntDocumentPath() + attachmentName;
                            MediaHelper.showDocument(attachFilePathString, AddPostActivity.this);
                        }

                        break;

                    case DownloadMediaFile.DOWNLOAD_FAIL:
                        mDownload = null;
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Download Fail");
                        if (!message.obj.toString().equals(DownloadMediaFile.download_cancel_str)) {
                            Helper.showAlertMessage(AddPostActivity.this, "Download Fail", message.obj.toString(), "CLOSE");
                        }


                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        if (!TextUtils.isEmpty(mCourseID)) {
            new PostTagsList(mHandler, mCourseID).sendPostTagsListRequest();
            //selectCourse.setVisibility(View.GONE);
            courseNameTextview.setText(courseNameStr);
        }

        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST) {
            // User can not select Template while editing post and repost.
            templateRelative.setVisibility(View.GONE);

            if (getPostContentTypeMedia() != Flinnt.INVALID) {

                if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO && postContentUrl == 1) {
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    fileType = AddMessageActivity.FileType.link;
                    attachFilePathString = attachmentName;

                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_GALLERY) {
                    playButton.setVisibility(View.GONE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = AddMessageActivity.FileType.image;
                    File file = new File(Helper.getFlinntImagePath() + File.separator + attachmentName);
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), attachmentName);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("Bitmap : " + photoThumb);
                        if (photoThumb != null) {
                            attachedMediaThumb.setImageBitmap(photoThumb);
                        }
                    } else {
                        photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_album);
                        attachedMediaThumb.setImageBitmap(photoThumb);
                    }

                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_AUDIO) {
                    playButton.setVisibility(View.GONE);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = AddMessageActivity.FileType.audio;
                    File file = new File(Helper.getFlinntAudioPath() + File.separator + attachmentName);
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                    }
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_DOCUMENT) {
                    playButton.setVisibility(View.GONE);
                    File file = new File(Helper.getFlinntDocumentPath() + File.separator + attachmentName);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = AddMessageActivity.FileType.pdf;
                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        attachedMediaThumb.setImageResource(R.drawable.ic_attached_pdf);
                    }
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                    playButton.setVisibility(View.GONE);
                    attachFilePathString = attachmentName;
                    fileType = AddMessageActivity.FileType.link;
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                } else if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_VIDEO) {
                    playButton.setVisibility(View.VISIBLE);
                    File file = new File(Helper.getFlinntVideoPath() + File.separator + attachmentName);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    fileType = AddMessageActivity.FileType.video;

                    if (file.exists()) {
                        attachFilePathString = file.getAbsolutePath();
                        photoThumb = ThumbnailUtils.createVideoThumbnail(attachFilePathString, MediaStore.Images.Thumbnails.MICRO_KIND);
                        if (photoThumb != null) {
                            attachedMediaThumb.setImageBitmap(photoThumb);
                        }
                    } else {
                        photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attached_video);
                        attachedMediaThumb.setImageBitmap(photoThumb);
                    }

                }

                if (mPostStat == Flinnt.POST_BLOG_REPOST) {
                    uploadFilePathString = attachFilePathString;
                }


            }

            reFilledData();
        }

        //mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION);
        //if( null == mResourceValidation ) {
        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION);
        resourceValidation.sendResourceValidationRequest();
        //}

    }

    /**
     * Gets tags list locally from response
     *
     * @param postTagsListResponse
     */
    private void updatePostTagsList(PostTagsList.PostTagsListResponse postTagsListResponse) {
        String[] tags = postTagsListResponse.getTags().split(",");
        tagsListStatic = new ArrayList<String>(Arrays.asList(tags));
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Add Post");
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
     * In edit and repost mode, original details to be filled in again to UI
     */
    private void reFilledData() {

        if (mPostStat == Flinnt.POST_BLOG_EDIT) {
            selectCourse.setVisibility(View.GONE);
            publishRelative.setVisibility(View.GONE);
        }
		/*else if(mPostStat == Flinnt.POST_BLOG_REPOST) {
			titleTxtview.setFocusable(false);
			descTxtview.setFocusable(false);
		}*/

        courseNameTextview.setText(courseNameStr);
        titleTxtview.setText(postTitleStr);
        descTxtview.setText(postDescStr);
        //postTagsText.setText(tagsNameStr);

        if (!TextUtils.isEmpty(tagsNameStr)) {
            String[] tagAr = tagsNameStr.split(",");

            Tags[] tags = new Tags[tagAr.length];

            for (int i = 0; i < tagAr.length; i++) {
                if (!TextUtils.isEmpty(tagAr[i])) {
                    tags[i] = new Tags(tagAr[i], tagAr[i]);
                    postTagsText.addObject(tags[i]);
                }
            }
        }


		/*long pubTime = Long.parseLong(publishDateStr)*1000;
		if(pubTime > Helper.getCurrentTimeMillis()){
			mCalendar.setTimeInMillis(pubTime);
			mPublishStats = Flinnt.PUBLISH_SCHEDULE;
			publishPostGroup.check(R.id.publish_post_schedule_addpost);
			schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);
			SimpleDateFormat sdfdate = new SimpleDateFormat(dateFormat, Locale.US);
			SimpleDateFormat sdftime = new SimpleDateFormat(timeFormat, Locale.US);
			scheduleDateTxtview.setText(sdfdate.format(pubTime));
			scheduleTimeTxtview.setText(sdftime.format(pubTime));
		}*/

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

                if (postTagsText != null) postTagsText.clearFocus();
                Helper.hideKeyboardFromWindow(AddPostActivity.this);
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {

                    if (validatePost()) {
                        if (!TextUtils.isEmpty(lastAttachedImagePath) && getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                            isResourseChange = Flinnt.TRUE;
                        }

                        if (!TextUtils.isEmpty(lastAttachedImagePath) && getPostContentTypeMedia() != Flinnt.POST_CONTENT_LINK) {
                            //if(Helper.isConnected()){
                            new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                            {
                                mProgressDialog = Helper.getProgressDialog(AddPostActivity.this, "", "Uploading media...", Helper.PROGRESS_DIALOG);
                                if (mProgressDialog != null) mProgressDialog.show();
                            }
					/*}else{
						Helper.showNetworkAlertMessage(AddPostActivity.this);
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
     * Sends a request to add post
     *
     * @param resourseID ID of attached media resource that are going to be uploaded in the post
     */
    private void sendRequest(String resourseID) {
        String titleStr = titleTxtview.getText().toString();
        String descStr = descTxtview.getText().toString();
        String postTagsStr = "";
        List<Tags> tagsList = postTagsText.getObjects();
        ArrayList<String> tempTagNames = new ArrayList<String>();
        for (int i = 0; i < tagsList.size(); i++) {
            String tempTagName = tagsList.get(i).getTagName().trim();
            if (!tempTagName.isEmpty() && !tempTagNames.contains(tempTagName)) {
                postTagsStr = postTagsStr + tempTagName + ",";
                tempTagNames.add(tempTagName);
            }
        }
        //postTagsStr = postTagsStr.substring(0, postTagsStr.length()-2);
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("postTagsStr : " + postTagsStr);

        AddPostRequest addPostRequest = new AddPostRequest();
        addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addPostRequest.setCourseID(mCourseID);
        if (mPostStat == Flinnt.POST_BLOG_REPOST) {
            addPostRequest.setRepostCourseID(mRepostCourseID);
        }
        if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST) {
            addPostRequest.setPostID(mPostID);
            addPostRequest.setResourseChanged(isResourseChange);
        }
        if (getPostContentTypeMedia() != Flinnt.INVALID) {
            addPostRequest.setPostContantType(getPostContentTypeMedia());

            if (getPostContentTypeMedia() == Flinnt.POST_CONTENT_LINK) {
                addPostRequest.setPostUrl(uploadFilePathString);
            } else {
                //if(!TextUtils.isEmpty(resourseID))
                if (mPostStat == Flinnt.POST_BLOG_EDIT) {
                    if (isResourseChange == Flinnt.TRUE)
                        addPostRequest.setResourseID(resourseID);
                } else {
                    addPostRequest.setResourseID(resourseID);
                }
            }
        }
        if (TextUtils.isEmpty(templateId)) {
            addPostRequest.setPostTemplateID(templateId);
        }
        addPostRequest.setTitle(titleStr);
        addPostRequest.setDescription(descStr);
        addPostRequest.setPostTags(postTagsStr);
        if (mPostStat != Flinnt.POST_BLOG_EDIT && mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
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
            Helper.showAlertMessage(AddPostActivity.this, "Post", "Select Course", "CLOSE");
            return false;
        }
        if (mPostStat == Flinnt.POST_BLOG_REPOST && TextUtils.isEmpty(mRepostCourseID)) {
            Helper.showAlertMessage(AddPostActivity.this, "Post", "Select Course", "CLOSE");
            return false;
        } else if (mPostStat == Flinnt.POST_BLOG_EDIT &&
                TextUtils.isEmpty(titleTxtview.getText().toString())) {
            Helper.showAlertMessage(AddPostActivity.this, "Post", "Add Title", "CLOSE");
            return false;
        } else if (TextUtils.isEmpty(descTxtview.getText().toString())) {
            Helper.showAlertMessage(AddPostActivity.this, "Post", "Add Description", "CLOSE");
            return false;
        } else if (titleTxtview.getText().toString().length() > CHARACTER_LIMIT_POST_TITLE) {
            Helper.showAlertMessage(AddPostActivity.this, "Post", "Title can not be longer than " + CHARACTER_LIMIT_POST_TITLE + " characters", "CLOSE");
            return false;
        } else if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
            if (mScheduleYear == Flinnt.INVALID) {
                Helper.showAlertMessage(AddPostActivity.this, "Post", "Select Date", "CLOSE");
                return false;
            } else if (mScheduleHour == Flinnt.INVALID) {
                Helper.showAlertMessage(AddPostActivity.this, "Post", "Select Time", "CLOSE");
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Checks if to publish now or schedule
     *
     * @param view publish now/schedule radio button
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {

            case R.id.publish_post_now_addpost:
                if (checked)
                    mPublishStats = Flinnt.PUBLISH_NOW;
                schedulePublishPostLineatLayout.setVisibility(View.GONE);
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                scheduleDateTxtview.setText(res.getString(R.string.select_date));
                scheduleTimeTxtview.setText(res.getString(R.string.select_time));
                break;

            case R.id.publish_post_schedule_addpost:
                if (checked) {
                    mPublishStats = Flinnt.PUBLISH_SCHEDULE;
                    mScheduleYear = Flinnt.INVALID;
                    mScheduleHour = Flinnt.INVALID;
                }


                mScheduleYear = Flinnt.INVALID;
                mScheduleHour = Flinnt.INVALID;
                schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);
                focusOnView();
                break;
        }
    }

    /**
     * Scroll to the bottom
     */
    private final void focusOnView() {
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
            case R.id.schedule_date_relative_addpost:
                // open DatePickerDialog
                new DatePickerDialog(AddPostActivity.this, datePicker,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.schedule_time_relative_addpost:
                new TimePickerDialog(AddPostActivity.this, timePicker,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE), false).show();
                break;

            case R.id.course_edit_addpost:
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {

                    Intent selectCourse = new Intent(AddPostActivity.this, SelectCourseActivity.class);
                    selectCourse.putExtra(SelectCourseRequest.POST_TYPE_KEY, Flinnt.POST_TYPE_BLOG);
                    if (mPostStat == Flinnt.POST_BLOG_REPOST) {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_REPOST);
                    } else {
                        selectCourse.putExtra(SelectCourseRequest.ACTION_KEY, Flinnt.STAT_ADD);
                    }

                    startActivityForResult(selectCourse, COURSE_INFO_CALL_BACK);
                }
                break;

            case R.id.button_choose_template:
                Intent templateIntent = new Intent(this, SelectTempleteActivity.class);
                startActivityForResult(templateIntent, TEMPLATES_CALL_BACK);
                break;

            case R.id.attached_file_remove:
                attachedFileRelative.setVisibility(View.GONE);
                uploadFilePathString = "";
                lastAttachedImagePath = uploadFilePathString;
                setPostContentTypeMedia(Flinnt.INVALID);
                if (mPostStat == Flinnt.POST_BLOG_EDIT || mPostStat == Flinnt.POST_BLOG_REPOST) {
                    isResourseChange = Flinnt.TRUE;
                }
                break;

            case R.id.imagebutton_preview_media:

            case R.id.attached_file_image:
                String filePathString = "";
                if (!TextUtils.isEmpty(uploadFilePathString)) {
                    filePathString = uploadFilePathString;
                } else {
                    filePathString = attachFilePathString;
                }

                if (null != filePathString && !filePathString.isEmpty()) {
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
                } else {
                    switch (fileType) {
				/*case image:
					MediaHelper.showImage(filePathString, this);
					break;*/
                        case video:

                            if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), attachmentUrl)) {
                                String videourl = attachmentUrl + attachmentName;
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("videourl : " + videourl);
                                mDownload = new DownloadMediaFile(AddPostActivity.this, Helper.getFlinntVideoPath(), attachmentName, Long.parseLong(mPostID), videourl, mHandler);
                                mDownload.execute();
                                setDownloadProgressDialog();
                            }
                            break;

                        case audio:

                            if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), attachmentUrl)) {
                                String audiourl = attachmentUrl + attachmentName;
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("audiourl : " + audiourl);
                                mDownload = new DownloadMediaFile(AddPostActivity.this, Helper.getFlinntAudioPath(), attachmentName, Long.parseLong(mPostID), audiourl, mHandler);
                                mDownload.execute();
                                setDownloadProgressDialog();

                            }
                            break;

                        case pdf:

                            if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), attachmentUrl)) {
                                String docurl = attachmentUrl + attachmentName;
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("docurl : " + docurl);
                                mDownload = new DownloadMediaFile(AddPostActivity.this, Helper.getFlinntDocumentPath(), attachmentName, Long.parseLong(mPostID), docurl, mHandler);
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
            mScheduleMonth = (monthOfYear + 1);
            mScheduleDay = dayOfMonth;

            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
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

            SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);
            scheduleTimeTxtview.setText(sdf.format(mCalendar.getTime()));
        }
    };

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(AddPostActivity.this)) {
            try {
                mProgressDialog = Helper.getProgressDialog(AddPostActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
                if (mProgressDialog != null) mProgressDialog.show();
            } catch (Exception e) {
                LogWriter.err(e);
            }
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
    public void onCancel(DialogInterface arg0) {
        stopDownload();
    }

    /**
     * Stops currently running download
     */
    private void stopDownload() {
        if (mDownload != null) {
            mDownload.setCancel(true);
        }
    }

    /**
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {

        new BottomSheet.Builder(AddPostActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title("Attachment")
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch (position) {

                            case R.id.attach_bottom_album:
                                //toast("Album");
                                fileType = AddMessageActivity.FileType.image;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                chooseFromStorage(fileType.name());
                                break;

                            case R.id.attach_bottom_photo:
                                //toast("Photo");
                                fileType = AddMessageActivity.FileType.image;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                captureFromRecorders();
                                break;

                            case R.id.attach_bottom_video:
                                //toast("Video");
                                fileType = AddMessageActivity.FileType.video;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                dialogContents("Choose an Action", "Record with flinnt", "Choose video track");
                                break;

                            case R.id.attach_bottom_audio:
                                //toast("Audio");
                                fileType = AddMessageActivity.FileType.audio;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                dialogContents("Choose an Action", "Record with flinnt", "Choose music track");
                                break;

                            case R.id.attach_bottom_link:
                                //toast("Link");
                                fileType = AddMessageActivity.FileType.link;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                                dialogContents("Paste/Type Link");
                                break;

                            case R.id.attach_bottom_pdf:
                                //toast("PDF");
                                fileType = AddMessageActivity.FileType.pdf;
//					setPostContentTypeMedia(Flinnt.POST_CONTENT_DOCUMENT);
                                chooseFromStorage(fileType.name());
                                break;

                            default:
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Adjust the IDK constant value : " + position);
                        }
                    }
                }).show();
    }

    /**
     * Dialog to display upload options
     *
     * @param title         dialog title
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
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    if (link.startsWith("http://") || link.startsWith("https://")) {
                        uploadFilePathString = link;
                    } else {
                        uploadFilePathString = "http://" + link;
                    }
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Link : " + uploadFilePathString);
                    playButton.setVisibility(View.GONE);
                    attachedMediaThumb.setImageResource(R.drawable.ic_attached_link);
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    setPostContentTypeMedia(Flinnt.POST_CONTENT_LINK);
                    lastAttachedImagePath = uploadFilePathString;
                } else {
                    Helper.showAlertMessage(AddPostActivity.this, "Invalid Link", "Add valid link", "CLOSE");
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
                storageIntent.setType(type + "/*");
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
                List<Intent> targetShareIntents = new ArrayList<Intent>();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_GET_CONTENT);
                shareIntent.setType("audio/*");
                List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
                if (!resInfos.isEmpty()) {
                    for (ResolveInfo resInfo : resInfos) {
                        String packageName = resInfo.activityInfo.packageName;
//			            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Intent packageName : " + packageName);
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
//				storageIntent = new Intent();
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
//				}
//				startActivityForResult(storageIntent, RESULT_FROM_STORAGE);

//				if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {

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
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("Intent packageName : " + packageName);
                            if (!packageName.contains("drive") && !packageName.contains("dropbox")) {
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
                                LogWriter.write("Add Post : targetShareIntentsl empty...");
                            storageIntent = new Intent(this, PickDocFolderActivity.class);
                            startActivityForResult(storageIntent, RESULT_FROM_STORAGE);
                        }
                    } else {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("Add Post : resInfos1 empty...");
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
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Image file path : " + uploadFilePathString);
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
                    if (null != getValidation(AddMessageActivity.FileType.video)) {
                        fileSize = getValidation(AddMessageActivity.FileType.video).getMaxFileSizeLong();
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
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Video file path : " + uploadFilePathString);
                }
                break;

            case audio:
/*				captureIntent = new Intent(
				MediaStore.Audio.Media.RECORD_SOUND_ACTION);
				uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
//					uploadFilePathString = uploadFile.getAbsolutePath();
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

                try {
                    uploadFile = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);

                    Intent recordAudio = new Intent(this, Recorder.class);
                    recordAudio.putExtra("FilePath", uploadFilePathString);
                    Validation validation = getValidation(AddMessageActivity.FileType.audio);
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

    //	Intent Results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : " + fileType + "\nData Uri : " + data);

        Uri contentUri;
        try {
            if (resultCode == RESULT_OK) {

                if (requestCode == RESULT_FROM_STORAGE) {

                    switch (fileType) {

                        case image:
                            contentUri = data.getData();
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("contentUri : " + contentUri);
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            //imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.image)) {
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Valid Image File");
								/*
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
								*/
                                Validation validation = getValidation(AddMessageActivity.FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();

                                bitmapOptions.inSampleSize = 1;
                                // setBitmapFactoryOptions(bitmapOptions);

                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                            + ", Height : " + uploadBitmap.getHeight());
                                if (uploadBitmap == null) {
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(AddMessageActivity.FileType.image), "CLOSE");
                                    return;
                                }
                                if (uploadBitmap != null) {
                                    attachedMediaThumb.setImageBitmap(uploadBitmap);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;
                                }
                                playButton.setVisibility(View.GONE);
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
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Images.Thumbnails.MICRO_KIND);
                                if (photoThumb == null) {
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Invalid file");
                                    Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(AddMessageActivity.FileType.video), "CLOSE");
                                    return;
                                }
                                if (photoThumb != null) {
                                    attachedMediaThumb.setImageBitmap(photoThumb);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.VISIBLE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_VIDEO);
                                    lastAttachedImagePath = uploadFilePathString;
                                }
                            }
                            break;

                        case audio:
                            contentUri = data.getData();
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("contentUri : " + contentUri);
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.audio)) {
                                attachedFileRelative.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.GONE);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                                lastAttachedImagePath = uploadFilePathString;
                            }
                            break;

                        case pdf:
                            contentUri = data.getData();
                            if (null == contentUri || Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                uploadFilePathString = data.getStringExtra("result");
                            } else {
                                uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            }
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.pdf)) {
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
                } else if (requestCode == RESULT_FROM_RECORDERS) {

                    switch (fileType) {

                        case image:
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.image)) {
								/*
								imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uploadFileUri);
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
								*/
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("Valid Image File");
								/*
								photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
								attachedImage.setImageBitmap(photoThumb);
								*/
                                Validation validation = getValidation(AddMessageActivity.FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();

                                bitmapOptions.inSampleSize = 1;
                                //setBitmapFactoryOptions(bitmapOptions);

                                Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                            + ", Height : " + uploadBitmap.getHeight());

                                if (uploadBitmap != null) {
                                    attachedMediaThumb.setImageBitmap(uploadBitmap);
                                    attachedFileRelative.setVisibility(View.VISIBLE);
                                    setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                    lastAttachedImagePath = uploadFilePathString;
                                }
                                playButton.setVisibility(View.GONE);
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
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.video)) {
                                photoThumb = ThumbnailUtils.createVideoThumbnail(uploadFilePathString, MediaStore.Images.Thumbnails.MICRO_KIND);
                                if (photoThumb != null) {
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
                            if (isValidFile(uploadFilePathString, AddMessageActivity.FileType.audio)) {
                                attachedMediaThumb.setImageResource(R.drawable.ic_attached_audio);
                                attachedFileRelative.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.GONE);
                                setPostContentTypeMedia(Flinnt.POST_CONTENT_AUDIO);
                                lastAttachedImagePath = uploadFilePathString;
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    switch (requestCode) {
                        case COURSE_INFO_CALL_BACK:
                            if (resultCode == Activity.RESULT_OK) {
                                String courseID = data.getStringExtra(CourseInfo.COURSE_ID_KEY);
                                String courseName = data.getStringExtra(CourseInfo.COURSE_NAME_KEY);
                                courseNameTextview.setText(courseName);
                                if (mPostStat == Flinnt.POST_BLOG_REPOST) {
                                    mRepostCourseID = courseID;
                                } else {
                                    mCourseID = courseID;
                                    new PostTagsList(mHandler, mCourseID).sendPostTagsListRequest();
                                }
                            }
                            break;
                        case TEMPLATES_CALL_BACK:
                            if (resultCode == Activity.RESULT_OK) {
                                templateId = data.getStringExtra(Template.POST_TEMPLATE_ID_KEY);
                                templateName = data.getStringExtra(Template.POST_TEMPLATE_NAME_KEY);
                                templateTitle = data.getStringExtra(Template.POST_TEMPLATE_TITLE_KEY);
                                templateTags = data.getStringExtra(Template.POST_TEMPLATE_TAGS_KEY);
                                templateDescription = data.getStringExtra(Template.POST_TEMPLATE_DESCRIPTION_KEY);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("templateTitle : " + templateTitle);
                                titleTxtview.setText(templateTitle);
                                descTxtview.setText(templateDescription);


							/*List<Tags> goingToRemove = postTagsText.getObjects();
							for (int i = 0; i < goingToRemove.size(); i++) {
								postTagsText.removeObject(goingToRemove.get(i));
							};*/
                                postTagsText.clear();
                                //postTagsText.setText("");


                                if (!TextUtils.isEmpty(templateTags)) {
                                    String[] tagAr = templateTags.split(",");
                                    Tags[] tags = new Tags[tagAr.length];

                                    for (int i = 0; i < tagAr.length; i++) {
                                        if (!TextUtils.isEmpty(tagAr[i])) {
                                            tags[i] = new Tags(tagAr[i], tagAr[i]);
                                            postTagsText.addObject(tags[i]);
                                        }
                                    }
                                }


                            }
                            break;

                        default:
                            break;
                    }
                }
            } else {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("You didn't pick any file ");
//                uploadFilePathString = "";
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
    public Validation getValidation(AddMessageActivity.FileType type) {
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
    private boolean isValidFile(String filePath, AddMessageActivity.FileType type) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("filePath :: " + filePath + ", FileType : " + type);
        boolean ret = true;
        if (filePath == null) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file - null filepath");
            Helper.showAlertMessage(this, "Error", wrongSizedFileMessage(type), "CLOSE");
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
                Helper.showAlertMessage(this, "Error", wrongSizedFileMessage(type), "CLOSE");
                return false;
            }

            if (length >= validation.getMaxFileSizeLong()) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, "Error", validateSizeMessage(type), "CLOSE");
                return false;
            }

            if (!TextUtils.isEmpty(validation.getFileTypes())) {
                String fileExtention = path.substring(path.lastIndexOf(".") + 1);
                if (fileType == AddMessageActivity.FileType.pdf) {
                    fileExtention = fileExtention.toLowerCase();
                }
                ArrayList<String> extentions = new ArrayList<String>(Arrays.asList(validation.getFileTypes().split(",")));

                if (!extentions.contains(fileExtention) && !isFromCustomRecorder) {
                    uploadFilePathString = "";
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write("This file type is not supported.");
                    Helper.showAlertMessage(this, "Error", validateSuppotedFileMessage(type), "CLOSE");
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
            //bitmapOptions.inSampleSize = 1;

            //Flile lenth > 1MB ...then compress...
            if (new File(ImagePath).length() > (1 * 1024 * 1024)) {
                bitmapOptions = setBitmapFactoryOptions(bitmapOptions);
            } else {
                bitmapOptions.inSampleSize = 1;
            }


            // Bitmap bitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);

            // Calculate inSampleSize
            //bitmapOptions.inSampleSize = Helper.calculateInSampleSize(bitmapOptions, validation.getMinWidth(), validation.getMinHeight());
            // Decode bitmap with inSampleSize set
            // bitmapOptions.inJustDecodeBounds = false;

            final Bitmap originalBitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);

            if (LogWriter.isValidLevel(Log.ERROR))
                LogWriter.write("originalBitmap : Height : " + originalBitmap.getHeight()
                        + ", Width : " + originalBitmap.getWidth());
            if (originalBitmap.getHeight() < validation.getMinHeight()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min300));
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File height is smaller then Minimum Height.");
                Helper.showAlertMessage(this, "Error", validateResolutionMessage(AddMessageActivity.FileType.image), "CLOSE");
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min200));
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, "Error", validateResolutionMessage(AddMessageActivity.FileType.image), "CLOSE");
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
     * Gets file size invalid message
     *
     * @param filetype type of file
     * @return validation message
     */
    public static String validateSizeMessage(AddMessageActivity.FileType filetype) {
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
    public static String validateSuppotedFileMessage(AddMessageActivity.FileType filetype) {
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

    public static String wrongSizedFileMessage(AddMessageActivity.FileType filetype) {
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
    public static String validateResolutionMessage(AddMessageActivity.FileType filetype) {
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

    /**
     * Updates the list of post tags
     */
    private void updateTokenConfirmation() {
        StringBuilder sb = new StringBuilder("Current tokens:\n");
        for (Object token : postTagsText.getObjects()) {
            sb.append(token.toString());
            sb.append("\n");
        }

        //((TextView)findViewById(R.id.tokens)).setText(sb);
    }

    @Override
    public void onTokenAdded(Tags token) {
        // TODO Auto-generated method stub
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Tags token) {
        // TODO Auto-generated method stub
        updateTokenConfirmation();
    }

    /**
     * Create and display downloading progress dialog
     */
    public void setDownloadProgressDialog() {

        mProgressDialog = new ProgressDialog(AddPostActivity.this);
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
