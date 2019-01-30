package com.edu.flinnt.gui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddPost;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.gui.AddCommunicationActivity.FileType;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.MenuBannerResponse;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.Validation;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MediaHelper;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyConfig;
import com.edu.flinnt.util.UploadMediaFile;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.Func2;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * GUI class to add/update new banner
 */
public class AddBannerActivity extends AppCompatActivity implements OnClickListener {

    Toolbar toolbar;
    RelativeLayout attachedFileRelative;
    ImageButton attachFile;
    ImageView attachedMediaThumb, attachedMediaRemove;
    ImageLoader mImageLoader;

    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;

    private int mPostStat = Flinnt.INVALID;
    private int postContentTypeMedia = Flinnt.POST_CONTENT_GALLERY;

    Resources res = FlinntApplication.getContext().getResources();

    private int CHARACTER_LIMIT_COURSE_NAME = 100;
    private final int RESULT_FROM_STORAGE = 101;
    private final int RESULT_FROM_RECORDERS = 102;
    private final int RESULT_FROM_CROP_PIC = 103;

    File uploadFile;
    private String uploadFilePathString, attachFilePathString, lastAttachedImagePath = "";
    private Uri uploadFileUri;
    private Bitmap imageFile;
    private Bitmap photoThumb;
    String link;

    FileType fileType = FileType.image;
    ResourceValidationResponse mResourceValidation;
    Common mCommon;

    private String bannerPath = "", bannerName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.add_banner);
        //@Nikhil 20062018
        AskPermition.getInstance(AddBannerActivity.this).RequestAllPermission();
        mImageLoader = Requester.getInstance().getImageLoader();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mPostStat = bundle.getInt(Flinnt.POST_STATS_ACTION);
            if (bundle.containsKey(MenuBannerResponse.BANNERS_PATH_KEY) &&
                    bundle.containsKey(MenuBannerResponse.BANNERS_KEY)) {
                bannerPath = bundle.getString(MenuBannerResponse.BANNERS_PATH_KEY);
                bannerName = bundle.getString(MenuBannerResponse.BANNERS_KEY);
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //courseNameTxt = (EditText) findViewById(R.id.course_title_addcourse);
        attachedFileRelative = (RelativeLayout) findViewById(R.id.attached_file_layout);
        attachedMediaThumb = (ImageView) findViewById(R.id.attached_file_image);
        attachedMediaRemove = (ImageView) findViewById(R.id.attached_file_remove);

        attachedMediaThumb.setOnClickListener(this);
        attachedMediaRemove.setOnClickListener(this);

        final String bannerThumbUrl = bannerPath + Flinnt.USER_BANNER_LARGE + File.separator + bannerName;
        mImageLoader.get(bannerThumbUrl,
                ImageLoader.getImageListener(attachedMediaThumb, R.drawable.default_course_image_list, R.drawable.default_course_image_list));
        attachFilePathString = Helper.getFlinntUrlPath(bannerThumbUrl) + bannerName;

        Bitmap bitmap = Helper.getBitmapFromSDcard(Helper.getFlinntUrlPath(bannerThumbUrl), bannerName);
        if (bitmap == null) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Image url : " + bannerThumbUrl);
            //mImageLoader.get(url, ImageLoader.getImageListener(holder.courseImage, R.drawable.default_course_image, R.drawable.default_course_image));
            mImageLoader.get(bannerThumbUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("onResponse Bitmap : " + response.getBitmap());
                        attachedMediaThumb.setImageBitmap(response.getBitmap());
                        Helper.saveBitmapToSDcard(response.getBitmap(), Helper.getFlinntUrlPath(bannerThumbUrl),
                                bannerName);
                    } else {
                        attachedMediaThumb.setImageResource(R.drawable.default_course_image_list);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    attachedMediaThumb.setImageResource(R.drawable.default_course_image_list);
                }
            });
        } else {
            attachedMediaThumb.setImageBitmap(bitmap);
        }

        attachFile = (ImageButton) findViewById(R.id.attach_file_addpost);

        attachFile.setOnClickListener(new OnClickListener() {

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
                            if (((AddPostResponse) message.obj).getData().getIsChange() == Flinnt.TRUE) {
                                Helper.showToast("Banner has been updated", Toast.LENGTH_LONG);

                                Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
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
                                Helper.showAlertMessage(AddBannerActivity.this, "Add Course", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }

                        break;

                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        sendRequest(message.obj.toString());

                        break;

                    case UploadMediaFile.UPLOAD_FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        //mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_BANNER_PICTURE);
        //if( null == mResourceValidation ) {
        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION_BANNER_PICTURE);
        resourceValidation.sendResourceValidationRequest();
        //}
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Add Banner");
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
                //if( validateCourse() ) {

                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {

                    if (!TextUtils.isEmpty(lastAttachedImagePath)) {
                        //if(Helper.isConnected()){
                        new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                        {
                            mProgressDialog = Helper.getProgressDialog(AddBannerActivity.this, "", "Uploading Image...", Helper.PROGRESS_DIALOG);
                            if (mProgressDialog != null) mProgressDialog.show();
                        }
					/*}else{
						Helper.showNetworkAlertMessage(AddBannerActivity.this);
					}*/


                    } else {
                        /** empty resourseId
                         * does not contain media file.*/
                        //sendRequest("");
                        Helper.showAlertMessage(AddBannerActivity.this, "Banner", "Add Banner image", "CLOSE");
                    }

                }
                //}
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends request to add new banner
     *
     * @param resourseID banner image resource id
     */
    private void sendRequest(String resourseID) {

        if (!TextUtils.isEmpty(resourseID)) {
            AddPostRequest addPostRequest = new AddPostRequest();
            addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
            addPostRequest.setResourseID(resourseID);

            if (mPostStat != Flinnt.INVALID) {
                new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
                startProgressDialog();
            }
        } else {
            Helper.showAlertMessage(AddBannerActivity.this, "Banner", "Add banner image", "CLOSE");
        }
    }

	/*private boolean validateCourse() {

		if(TextUtils.isEmpty(courseNameTxt.getText().toString())){
			Helper.showAlertMessage(AddBannerActivity.this, "Course", "Add course name", "CLOSE");
			return false;
		}
		else if(courseNameTxt.getText().toString().length() > CHARACTER_LIMIT_COURSE_NAME ){
			Helper.showAlertMessage(AddBannerActivity.this, "Course", "Course name can not be longer than " +  CHARACTER_LIMIT_COURSE_NAME + " characters", "CLOSE");
			return false;
		}
		return true;
	}*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.attached_file_remove:
                attachedFileRelative.setVisibility(View.GONE);
                uploadFilePathString = "";
                lastAttachedImagePath = uploadFilePathString;
                setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);

                break;

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

                        default:
                            break;
                    }
                } else {
                    Helper.showAlertMessage(AddBannerActivity.this, "Banner", "This image is not downloaded yet", "CLOSE");
                }

                break;

        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(AddBannerActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddBannerActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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

        new BottomSheet.Builder(AddBannerActivity.this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.attach_bottom_menu)
                .title("Attachment")
                .grid()
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {


                        if (AskPermition.getInstance(AddBannerActivity.this).isPermitted()) {
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
                                    if (LogWriter.isValidLevel(Log.ERROR))
                                        LogWriter.write("Adjust the IDK constant value : " + position);
                            }
                        }


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

        Uri contentUri;
        try {
            if (resultCode == RESULT_OK) {

                if (requestCode == RESULT_FROM_STORAGE) {

                    switch (fileType) {

                        case image:
                            contentUri = data.getData();
                            uploadFilePathString = MediaHelper.getPath(this, contentUri);
                            if (LogWriter.isValidLevel(Log.ERROR))
                                LogWriter.write("uploadFilePathString :: " + uploadFilePathString);
                            //imageFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            if (uploadFilePathString != null && isValidFile(uploadFilePathString, FileType.image)) {
                                if (LogWriter.isValidLevel(Log.ERROR))
                                    LogWriter.write("Valid Image File");

//							performCrop(Uri.fromFile(new File(uploadFilePathString)));


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

                                    Crop.of(data.getData(), Uri.fromFile(new File(uploadFilePathString))).withAspect(aspectValueX, aspectValueY).start(this);
                                }

                            }
                            break;

                        default:

                            break;
                    }
                } else if (requestCode == RESULT_FROM_RECORDERS) {

                    switch (fileType) {

                        case image:
                            if (uploadFilePathString != null && isValidFile(uploadFilePathString, FileType.image)) {

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
                        //Create an instance of bundle and get the returned data
                        Bundle extras = data.getExtras();
                        if (LogWriter.isValidLevel(Log.DEBUG))
                            LogWriter.write("CROP_PIC extras : " + extras);

                        Validation validation = getValidation(FileType.image);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("getValidation :: " + validation);
                        uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 1;

                        {
                            Bitmap selectedImage = BitmapFactory.decodeFile(uploadFilePathString);
                            attachedMediaThumb.setImageBitmap(selectedImage);
                            attachedFileRelative.setVisibility(View.VISIBLE);

                        }
                    }
                } else if (requestCode == Crop.REQUEST_CROP) {
                    Validation validation = getValidation(FileType.image);
                    uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 1;
                    {
                        Bitmap selectedImage = BitmapFactory.decodeFile(uploadFilePathString);
                        attachedMediaThumb.setImageBitmap(selectedImage);
                        attachedFileRelative.setVisibility(View.VISIBLE);
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
            mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_COURSE_PICTURE);
        }

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("mResourceValidation :: " + mResourceValidation);
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
            if (length <= 0) {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", AddCommunicationActivity.wrongSizedFileMessage(type), "CLOSE");
                return false;
            }
            if (length >= validation.getMaxFileSizeLong()) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, "Error", "Use 5 MB or fewer size image.", "CLOSE");
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
            //bitmapOptions.inSampleSize = 1;
            //Flile lenth > 1MB ...then compress...
            if (new File(ImagePath).length() > (1 * 1024 * 1024)) {
                bitmapOptions = setBitmapFactoryOptions(bitmapOptions);
            } else {
                bitmapOptions.inSampleSize = 1;
            }

            final Bitmap originalBitmap = BitmapFactory.decodeFile(uploadOrigianlFilePath, bitmapOptions);
            if (originalBitmap == null) {
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", AddCommunicationActivity.wrongSizedFileMessage(FileType.image), "CLOSE");
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
                Helper.showAlertMessage(this, "Error", "Add min. 600 x 400 px image", "CLOSE");
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min200));
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, "Error", "Add min. 600 x 400 px image", "CLOSE");
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

}
