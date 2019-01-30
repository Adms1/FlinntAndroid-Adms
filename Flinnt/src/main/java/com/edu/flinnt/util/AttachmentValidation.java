package com.edu.flinnt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.core.ResourceValidation;
import com.edu.flinnt.protocol.ResourceValidationResponse;
import com.edu.flinnt.protocol.Validation;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by flinnt-android-3 on 24/2/17.
 */
public class AttachmentValidation {


    private Context mContext;
    //Attachment
    public enum FileType {
        image, video, audio, pdf, link
    }
    private ResourceValidationResponse mResourceValidation;

    public AttachmentValidation(Context mContext){
        this.mContext = mContext;
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
    public boolean isValidFile(String filePath, FileType type ,boolean isFromCustomRecorder) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("filePath :: " + filePath + ", FileType : " + type);
        boolean ret = true;
        if (filePath == null) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Invalid file - null filepath");
            Helper.showAlertMessage(mContext, "Error", wrongSizedFileMessage(type), "CLOSE");
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
                Helper.showAlertMessage(mContext, "Error", wrongSizedFileMessage(type), "CLOSE");
                return false;
            }

            if (length >= validation.getMaxFileSizeLong()) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File size is larger then Max FileSize.");
                Helper.showAlertMessage(mContext, "Error", validateSizeMessage(type), "CLOSE");
                return false;
            }

            if (!TextUtils.isEmpty(validation.getFileTypes())) {
                String fileExtention = path.substring(path.lastIndexOf(".") + 1);
                if (type == FileType.pdf) {
                    fileExtention = fileExtention.toLowerCase();
                }
                ArrayList<String> extentions = new ArrayList<String>(Arrays.asList(validation.getFileTypes().split(",")));

                if (!extentions.contains(fileExtention) && !isFromCustomRecorder) {
                    filePath = "";
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write("This file type is not supported.");
                    Helper.showAlertMessage(mContext, "Error", validateSuppotedFileMessage(type), "CLOSE");
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

        Uri uploadFileUri = Uri.parse(ImagePath);
        String uploadFilePathString = ImagePath;
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
                Helper.showAlertMessage(mContext, "Error", validateResolutionMessage(FileType.image), "CLOSE");
                return false;
            }

            if (originalBitmap.getWidth() < validation.getMinWidth()) {
                uploadFilePathString = "";
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("File width is smaller then Minimum Width.");
                Helper.showAlertMessage(mContext, "Error", validateResolutionMessage(FileType.image), "CLOSE");
                return false;
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return true;
    }

    /**
     * Gets file size invalid message
     *
     * @param filetype type of file
     * @return validation message
     */
    private String validateSizeMessage(FileType filetype) {
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
