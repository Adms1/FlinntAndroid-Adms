package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.edu.flinnt.gui.AddMessageActivity.FileType;
import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddPost;
import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Course;
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
import com.google.android.gms.analytics.GoogleAnalytics;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class AddCourseActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    EditText courseNameTxt;
    RelativeLayout attachedFileRelative;
    ImageButton attachFile;
    ImageView attachedMediaThumb, attachedMediaRemove;

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

    private String mCourseID, courseNameStr, attachmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.add_course);

        Bundle bundle = getIntent().getExtras();
        if( null != bundle ) {
            mPostStat = bundle.getInt( Flinnt.POST_STATS_ACTION );
            if(bundle.containsKey(Course.COURSE_ID_KEY)) mCourseID = bundle.getString(Course.COURSE_ID_KEY);
            if(bundle.containsKey(Course.COURSE_NAME_KEY)) courseNameStr = bundle.getString(Course.COURSE_NAME_KEY);
            if(bundle.containsKey(Course.COURSE_PICTURE_KEY)) attachmentName = bundle.getString(Course.COURSE_PICTURE_KEY);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mPostStat == Flinnt.POST_COURSE_EDIT){
            getSupportActionBar().setTitle("Edit Course");
        }

        courseNameTxt = (EditText) findViewById(R.id.course_title_addcourse);
        attachedFileRelative = (RelativeLayout) findViewById(R.id.attached_file_layout);
        attachedMediaThumb = (ImageView) findViewById(R.id.attached_file_image);
        attachedMediaRemove = (ImageView) findViewById(R.id.attached_file_remove);

        attachedMediaThumb.setOnClickListener(this);
        attachedMediaRemove.setOnClickListener(this);

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
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if( message.obj instanceof AddPostResponse) {

                            Helper.deleteDirectory(new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD));
                            AddPostResponse response = ((AddPostResponse) message.obj );
                            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Add : " + response.getIsAdded() + " , Edit : " + response.getIsEdited() + " , Repost : " + response.getIsRepost());

                            if( response.getIsAdded() == Flinnt.TRUE )
                            {
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Added success");
                                //MyCoursesActivity.coursePictureURLstatic = response.getCoursePictureUrl();
                                Helper.showToast("Course has been added", Toast.LENGTH_LONG);

                                Intent resultIntent = new Intent();
                                // course
                                Bundle mBundle = new Bundle();
                                mBundle.putParcelable(AddPostResponse.COURSE_KEY,response.getNewCourse());
                                resultIntent.putExtras(mBundle);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                            else if(response.getIsEdited() == Flinnt.TRUE){
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Edited success");
                                Helper.showToast("Course has been updated", Toast.LENGTH_LONG);

                                MyCoursesActivity.coursePictureURLstatic = response.getCoursePictureUrl();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(Course.COURSE_PICTURE_KEY, response.getCoursePictureName());
                                resultIntent.putExtra(Course.COURSE_NAME_KEY, courseNameStr);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                            else if(response.getIsRepost() == Flinnt.TRUE){
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Repost success");
                                Helper.showToast("Course has been added", Toast.LENGTH_LONG);
                                finish();
                            }
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
                                Helper.showAlertMessage(AddCourseActivity.this, "Add Course", message.obj.toString(), "CLOSE");
                            }
                        }

                        break;

                    case UploadMediaFile.UPLOAD_SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UPLOAD_SUCCESS resource ID : " + message.obj.toString());
                        sendRequest(message.obj.toString());

                        break;

                    case UploadMediaFile.UPLOAD_FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UPLOAD_FAILURE resource ID : " + message.obj.toString());

                        break;

                    default:
                        stopProgressDialog();
                        super.handleMessage(message);
                }
            }
        };

        if(mPostStat == Flinnt.POST_COURSE_EDIT){

            String imgUrl = MyCoursesActivity.coursePictureURLstatic + Flinnt.COURSE_XLARGE  + File.separator + attachmentName;
            if(getPostContentTypeMedia() == Flinnt.POST_CONTENT_GALLERY){
                File file = new File(Helper.getFlinntUrlPath(imgUrl) + File.separator + attachmentName);
                if(file.exists()) {
                    attachedFileRelative.setVisibility(View.VISIBLE);
                    attachFilePathString = file.getAbsolutePath();
                    photoThumb = Helper.getBitmapFromSDcard(Helper.getFlinntUrlPath(imgUrl), attachmentName);
                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Bitmap : " + photoThumb);
                    if( photoThumb != null ){
                        attachedMediaThumb.setImageBitmap(photoThumb);
                    }
                }
                else{
                    attachedFileRelative.setVisibility(View.GONE);
                    lastAttachedImagePath = "";
					/*photoThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attachment_album);
					attachedMediaThumb.setImageBitmap(photoThumb);*/
					/*Requester.getInstance().getImageLoader().get(imgUrl,
			 		   ImageLoader.getImageListener(attachedMediaThumb, R.drawable.default_course_image, R.drawable.default_course_image));*/

                }

            }


            reFilledData();
        }

        //mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_COURSE_PICTURE);
        //if( null == mResourceValidation ) {
        ResourceValidation resourceValidation = new ResourceValidation(mHandler, ResourceValidation.RESOURCE_VALIDATION_COURSE_PICTURE);
        resourceValidation.sendResourceValidationRequest();
        //}
    }

    /** In edit and repost mode, original details to be filled in again to UI */
    private void reFilledData() {

        courseNameTxt.setText(courseNameStr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Add Course");
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
                Helper.hideKeyboardFromWindow(AddCourseActivity.this);
                if ( !Helper.isConnected() ) {
                    Helper.showNetworkAlertMessage(this);
                }
                else {
                    if( validateCourse() ) {
                        if(!TextUtils.isEmpty(lastAttachedImagePath)) {
                            //if(Helper.isConnected()){
                            new UploadMediaFile(this, mHandler, lastAttachedImagePath, getPostContentTypeMedia()).execute();
                            {
                                mProgressDialog = Helper.getProgressDialog(AddCourseActivity.this, "", "Uploading Image...", Helper.PROGRESS_DIALOG);
                                if( mProgressDialog != null ) mProgressDialog.show();
                            }
					/*}else{
						Helper.showNetworkAlertMessage(AddCourseActivity.this);
					}*/


                        }else {
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
     * Sends a request to add new course
     */
    private void sendRequest(String resourseID) {

        String courseName = courseNameTxt.getText().toString();
        courseNameStr = courseName;

        AddPostRequest addPostRequest = new AddPostRequest();
        addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
        addPostRequest.setCourseName(courseName);
        if(mPostStat == Flinnt.POST_COURSE_EDIT){
            addPostRequest.setCourseID(mCourseID);
        }
        if(!TextUtils.isEmpty(resourseID)){
            addPostRequest.setResourseID(resourseID);
        }

        if(mPostStat != Flinnt.INVALID){
            new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
            startProgressDialog();
        }
    }

    /**
     * Checks for the entered course name's validation
     * @return true if valid name, false otherwise
     */
    private boolean validateCourse() {

        if(TextUtils.isEmpty(courseNameTxt.getText().toString())){
            Helper.showAlertMessage(AddCourseActivity.this, "Course", "Add course name", "CLOSE");
            return false;
        }
        else if(courseNameTxt.getText().toString().length() > CHARACTER_LIMIT_COURSE_NAME ){
            Helper.showAlertMessage(AddCourseActivity.this, "Course", "Course name can not be longer than " +  CHARACTER_LIMIT_COURSE_NAME + " characters", "CLOSE");
            return false;
        }
        return true;
    }

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
                if(!TextUtils.isEmpty(uploadFilePathString)){
                    filePathString = uploadFilePathString;
                }else{
                    filePathString = attachFilePathString;
                }
                switch (fileType) {
                    case image:
                        MediaHelper.showImage(filePathString, this);
                        break;

                    default:
                        break;
                }
                break;

        }
    }

    /**
     *  Starts a circular progress dialog
     */
    private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(AddCourseActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddCourseActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null ) mProgressDialog.show();
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

    /**
     * Opens the bottom slider to select which type of media to upload
     */
    protected void openBottomSheet() {

        new BottomSheet.Builder(AddCourseActivity.this, R.style.BottomSheet_Dialog)
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

                            default:
                                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Adjust the IDK constant value : " + position);
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

        switch(fileType) {
            case image:
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uploadFile = Helper.getCropOutputFile();
                if(uploadFile != null){
                    uploadFilePathString = uploadFile.getAbsolutePath();
                    uploadFileUri = Uri.fromFile(uploadFile);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadFileUri);
                    if (captureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureIntent, RESULT_FROM_RECORDERS);
                    }
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Image file path : " +  uploadFilePathString);
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
        if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + ", File type : "  + fileType	+ "\nData Uri : " + data);

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
                            if(uploadFilePathString != null && isValidFile(uploadFilePathString, FileType.image)) {
                                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Valid Image File");

//							performCrop(Uri.fromFile(new File(uploadFilePathString)));

                                Validation validation = getValidation(FileType.image);
                                String aspectRatio = String.format("%.2f", Double.parseDouble(validation.getAspectRatio()));
                                int aspectValueY = 100;
                                int aspectValueX = (int) (Double.parseDouble(aspectRatio) * aspectValueY);
                                if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("aspect ratio : " + aspectRatio + "\nX : " + aspectValueX + "\nY : " + aspectValueY);

                                uploadFile = Helper.getCropOutputFile();
                                if(uploadFile != null){
                                    uploadFilePathString = uploadFile.getAbsolutePath();
                                    uploadFileUri = Uri.fromFile(uploadFile);

                                    Crop.of(data.getData(), Uri.fromFile(new File(uploadFilePathString))).withAspect( aspectValueX , aspectValueY ).start(this);

                                }

							/*
									photoThumb = Bitmap.createScaledBitmap(imageFile, Helper.getDip(80), Helper.getDip(80), true);
									attachedImage.setImageBitmap(photoThumb);
							 */
							/*Validation validation = getValidation(FileType.image);
							uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth());
							BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();*/

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();
                                //bitmapOptions.inSampleSize = 1;

							/*Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
							if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
									+ ", Height : " + uploadBitmap.getHeight());
							if(uploadBitmap != null){
								attachedMediaThumb.setImageBitmap(uploadBitmap);
								attachedFileRelative.setVisibility(View.VISIBLE);
								setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
							}*/
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

                        default:

                            break;
                    }
                }

                else if(requestCode == RESULT_FROM_RECORDERS) {

                    switch (fileType) {

                        case image:
                            if(uploadFilePathString != null && isValidFile(uploadFilePathString, FileType.image)) {

//							performCrop(Uri.fromFile(new File(uploadFilePathString)));


                                Validation validation = getValidation(FileType.image);
                                uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);

                                String aspectRatio = String.format("%.2f", Double.parseDouble(validation.getAspectRatio()));
                                //String aspectRatio = String.format("%.2f", 1.77);
                                int aspectValueY = 100;
                                int aspectValueX = (int) (Double.parseDouble(aspectRatio) * aspectValueY);
                                if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("aspect ratio : " + aspectRatio + "\nX : " + aspectValueX + "\nY : " + aspectValueY);

                                Crop.of(Uri.fromFile(new File(uploadFilePathString)), Uri.fromFile(new File(uploadFilePathString))).withAspect( aspectValueX , aspectValueY ).start(this);


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
                                //					Validation validation = getValidation(FileType.image);
                                //					uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth());

                                //					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger images
                                // UploadFileName=fileUri.getPath();
                                //					bitmapOptions.inSampleSize = 1;

                                //					Bitmap uploadBitmap = BitmapFactory.decodeFile(uploadFilePathString, bitmapOptions);
                                //					if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("uploadBitmap Width : " + uploadBitmap.getWidth()
                                //							+ ", Height : " + uploadBitmap.getHeight());

                                //					if(uploadBitmap != null){
                                //						attachedMediaThumb.setImageBitmap(uploadBitmap);
                                //						attachedFileRelative.setVisibility(View.VISIBLE);
                                //						setPostContentTypeMedia(Flinnt.POST_CONTENT_GALLERY);
                                //					}
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

                        default:
                            break;
                    }
                }

                else if(requestCode == RESULT_FROM_CROP_PIC) {
                    if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CROP_PIC data : " + data);



                    if( null != data ) {
                        //Create an instance of bundle and get the returned data
                        Bundle extras = data.getExtras();
                        if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CROP_PIC extras : " + extras);

                        Validation validation = getValidation(FileType.image);
                        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getValidation :: " + validation);
                        uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 1;

                        {
                            Bitmap selectedImage =  BitmapFactory.decodeFile(uploadFilePathString);
                            attachedMediaThumb.setImageBitmap(selectedImage);
                            attachedFileRelative.setVisibility(View.VISIBLE);

                        }
                    }
                }

                else if ( requestCode == Crop.REQUEST_CROP ) {

                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("uploadFilePathString result : " + uploadFilePathString);

                    Validation validation = getValidation(FileType.image);
                    uploadFilePathString = mCommon.compressImage(uploadFilePathString, validation.getMinHeight(), validation.getMinWidth(), 0, 0);
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 1;
                    {
                        Bitmap selectedImage =  BitmapFactory.decodeFile(uploadFilePathString);
                        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("selectedImage : " + selectedImage);
                        attachedMediaThumb.setImageBitmap(selectedImage);
                        attachedFileRelative.setVisibility(View.VISIBLE);
                    }
                    lastAttachedImagePath = uploadFilePathString;
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

    /**
     * To get validation parameters
     * @param type filetype
     * @return validation parameters of filetype
     */
    public Validation getValidation(FileType type) {
        Validation validation = null;
        if( null == mResourceValidation ) {
            mResourceValidation = ResourceValidation.getLastResourceValidationResponse(ResourceValidation.RESOURCE_VALIDATION_COURSE_PICTURE);
        }

        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mResourceValidation :: " + mResourceValidation);
        if( null != mResourceValidation ) {
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
     * @param filePath selected file's path on storage
     * @param type file type
     * @return true if valid, false otherwise
     */
    private boolean isValidFile(String filePath, FileType type) {
        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("filePath :: " + filePath + ", FileType : " + type);
        boolean ret = true;
        Validation validation = getValidation(type);
        if( null != validation ) {
            File file = new File(filePath);
            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("validation :: " + validation.toString());
            long length = file.length();
            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File length : " + length );
            if ( length <= 0 ) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(type) , "CLOSE");
                return false;
            }
            if(length >= validation.getMaxFileSizeLong()) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(this, "Error", "Use 5 MB or fewer size image", "CLOSE");
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
            if( originalBitmap == null ){
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file");
                Helper.showAlertMessage(this, "Error", AddPostActivity.wrongSizedFileMessage(FileType.image) , "CLOSE");
            }
            if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("originalBitmap : Height : " + originalBitmap.getHeight()
                    + ", Width : " + originalBitmap.getWidth());
            if (originalBitmap.getHeight() < validation.getMinHeight()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min300));
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File height is smaller then Minimum Height.");
                Helper.showAlertMessage(this, "Error", "Add min. 70 x 70 px image", "CLOSE");
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                //img_close.setVisibility(View.GONE);
                //comm.ShowMessage(activity, getResources().getString(R.string.msg_min200));
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(this, "Error", "Add min. 70 x 70 px image", "CLOSE");
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

}
