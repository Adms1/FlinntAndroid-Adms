package com.edu.flinnt.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A common class containing some important methods
 */
public class Common {

    private final Context con;
    ProgressDialog progressDialog;
    String url1;
    String Ret = "";
    String LastLineExec = "";
    String res;
    StringBuilder builder = new StringBuilder();
    //HttpClient client = new DefaultHttpClient();
    //HttpGet httpGet;
    JSONObject object;
    String readTwitterFeed = "";
    String[] MyAllVal = new String[30];
    String s = "";
    String MyClassName = "Flinnt";
    //flinnt_app F;
    String Message_Local = "";
    String readTwitterFeed_Error = "";
    ProgressDialog progressBar;
    Bitmap scaledBitmap = null;
    public static boolean IS_NETWORK_TOAST = true;

    // flinnt_app F = (flinnt_app) getApplication();
    public Common(Context context) {

        con = context;
        //F = (flinnt_app) context.getApplicationContext();

    }

    /**
     * Get user id from text file store in flinnt folder
     */
    public String getUserID() {
        String txt = this.ReadMessage("flinnt.db", "document");

        if (txt.matches("")) {
        } else {
            txt = MyCommFun.EncryptDecrypt("D", txt);
        }
        String t1[];
        t1 = txt.split("~");

        // delete file
        String OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/flinnt/" + "document" + "/";
        File myFile = new File(OutPutFile + "flinnt.db");
        if (myFile.exists()) {
            myFile.delete();
        }

        if (t1.length > 2) {
            return t1[2];
        } else {
            return "";
        }

    }


    /**
     * Show Message on UI on page
     *
     * @param activity current ui name
     * @param Msg      message to show on screen
     */
    public void ShowMessage(Activity activity, String Msg) {
        try {
            //MyCommFun.LOG1(0, this.MyClassName + "ShowMessage alert", Msg);
            Builder builder = new Builder(activity);
            builder.setTitle("Flinnt");

            // builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(Msg);
            builder.setPositiveButton("Close", null);
            final AlertDialog alert = builder.create();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alert.show();
                }
            });

        } catch (OutOfMemoryError e) {
            //LogWriter.err(e);
            String stackTrace = Log.getStackTraceString(e);
            //this.SendErrorOnServer("common->ShowMessage->OutOfMemoryError", this.LastLineExec, stackTrace, "", "", "");

        } catch (NullPointerException e) {
            String stackTrace = Log.getStackTraceString(e);
            //this.SendErrorOnServer("common->ShowMessage->NullPointerException", this.LastLineExec, stackTrace, "", "", "");
        } catch (ClassCastException e) {
            String stackTrace = Log.getStackTraceString(e);
            //this.SendErrorOnServer("common->ShowMessage->ClassCastException", this.LastLineExec, stackTrace, "", "", "");

        } catch (RuntimeException e) {
            String stackTrace = Log.getStackTraceString(e);
            //this.SendErrorOnServer("slidemenu_courselist->OnCreate->RuntimeException", this.LastLineExec, stackTrace, "", "", "");

        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            //this.SendErrorOnServer("common->ShowMessage->Exception", this.LastLineExec, stackTrace, "", "", "");
            LogWriter.err(e);
        }

    }

    public void LOG(String Head, String Txt) {
        this.LOG1(10, Head, Txt);
    }

    private void LOG1(int Level, String Head, String Txt) {
        if (Level == 0 || Level > MyConfig.LogLevel) {
            //LOG1(1,Head, Txt);
            if (Txt != null) {
                if (Txt.matches("")) {
                    // Log.e(Head, "a");
                } else {
                    // Log.e(Head, Txt);
                }
            } else {
                // Log.e(Head, "Txt is null");
            }
        }
    }


    public void LOG_Err(String Head, String Txt) {
        this.LOG1(1, Head, Txt);
    }

    /**
     * Opens url in browser
     *
     * @param url url
     */
    public void OpenLink(String url) // link
    {
        try {
            Uri uri1 = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
            this.con.startActivity(intent);
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }

    /**
     * To get intent to play audio file
     *
     * @param Audiourl audio file path
     * @param activity current activity
     * @return audio player intent
     */
    public Intent PlayAudio(String Audiourl, Context activity) {
        // LTDCommonData ltdCommonData,
        /*
		 * String extension = MimeTypeMap.getFileExtensionFromUrl(videourl);
		 * String mimeType =
		 * MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		 * Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
		 * mediaIntent.setDataAndType(Uri.parse(videourl), mimeType); return
		 * mediaIntent;
		 */
        // startActivity(mediaIntent);
        this.LOG1(1, this.MyClassName + "Com.PlayAudio", Audiourl);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        try {
            File file = new File(Audiourl);
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            myIntent.setDataAndType(Uri.fromFile(file), mimetype);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App available to show this file", Toast.LENGTH_LONG).show();
            // String stackTrace = Log.getStackTraceString(e);
            // comm.SendErrorOnServer(MyAllVal,
            // "Post_details->img_Pict_click->ActivityNotFoundException->3",LastLineExec,stackTrace,v.getTag().toString(),"","");

        } catch (Exception e) {
            // String data = e.getMessage();
        }
        return myIntent;
    }

    /**
     * Creates a text file with text messsage to a folder
     *
     * @param txt        message to be written on file
     * @param FileName   containing filename
     * @param FolderType type of folder
     */
    public void WriteMessage(String txt, String FileName, String FolderType) {

        String OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/flinnt/" + FolderType + "/";

		/*
		 * Calendar cal = Calendar.getInstance(); Calendar now = cal; int year =
		 * cal.get(Calendar.YEAR); int month = cal.get(Calendar.MONTH); int day
		 * = cal.get(Calendar.DAY_OF_MONTH); int hour = cal.get(Calendar.HOUR);
		 * int minute = cal.get(Calendar.MINUTE);
		 */
        // cal.clear();
        // cal.set(year, month, day, hour , minute);
        File file = new File(OutPutFile, FileName);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);

            // txt=txt + "  " + String.valueOf(year) + ":" +
            // String.valueOf(month) + ":" + String.valueOf(day) + ":" +
            // String.valueOf(hour) + ":" + String.valueOf(minute);
			/*
			 * os.write(txt.getBytes()); os.close();
			 */

            // FileOutputStream fOut = openFileOutput(OutPutFile+FileName, );
            OutputStreamWriter osw = new OutputStreamWriter(os);

            // Write the string to the file
            // LOG1(1,"flinnt txt",txt);
            osw.write(txt);

			/*
			 * ensure that everything is really written out and close
			 */
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            LogWriter.err(e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * Gets texts from stored text file
     *
     * @param FileName   name of file to be read
     * @param FolderName folder in which file exists
     * @return texts from file
     */
    public String ReadMessage(String FileName, String FolderName) {
        String RetVal = "";
        try {

            String OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/flinnt/" + FolderName + "/";

            // cal.clear();
            // cal.set(year, month, day, hour , minute);

            File myFile = new File(OutPutFile + FileName);
            if (myFile.exists() == true) {
                try {
                    FileInputStream fIn = new FileInputStream(myFile);
                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                    // String aDataRow = "";
                    // String aBuffer = "";

					/*
					 * while ((aDataRow = myReader.readLine()) != null) {
					 * aBuffer += aDataRow + "\n"; }
					 */

                    RetVal = myReader.readLine();
                    if (RetVal == null) {
                        RetVal = "";
                    }
                    if (RetVal.matches("")) {
                        //this.LOG1(1, this.MyClassName + "flinnt UserID VER", RetVal);
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("flinnt UserID VER : " + RetVal);
                    }

                    myReader.close();
                } catch (IOException e) {
                    LogWriter.err(e);
                }
            } else {
                //this.LOG1(1, this.MyClassName + "File not found", OutPutFile + FileName);
                if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("File not found");
                RetVal = "";
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            this.LOG_Err(this.MyClassName + "ReadMessage", e1.getMessage().toString());

        }
        return RetVal;
    }

    /**
     * Compress and scale image in order to consume less memory
     *
     * @param imageUri       original image uri
     * @param minHeight      to be converted in at least this size of height
     * @param minWidth       to be converted in at least this size of width
     * @param scaleMaxHeight conversion height size should not be exceed to this
     * @param scaleMaxWidth  conversion width size should not be exceed to this
     * @return lower sized compressed image's name
     */
    public String compressImage(String imageUri, float minHeight, float minWidth, int scaleMaxHeight, int scaleMaxWidth) {

        String filePath = "";
        String filename = "";
        try {
            filePath = this.getRealPathFromURI(imageUri);
            float maxHeight = 1600.0f;
            float maxWidth = 1066.0f;
            // this.LOG1(1, this.MyClassName + "Compress imageUri", imageUri.toString());
            //this.LOG1(1, this.MyClassName + "Compress filePath", filePath);
            Options options = new Options();

            // by setting this field as true, the actual bitmap pixels are not
            // loaded in the memory. Just the bounds are loaded. If
            // you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("actualHeight :: " + actualHeight + ", actualWidth :: " + actualWidth);

            float actualHeight1 = options.outHeight;
            float actualWidth1 = options.outWidth;

            // OutOfMemoryError solution, scaling larger images to maximum scale limit
            if (scaleMaxHeight != 0 || scaleMaxWidth != 0) {  // For AddAlbumActivity only which adds multiple images
                float scaleMaxHeightFloat = scaleMaxHeight;
                float scaleMaxWidthFloat = scaleMaxWidth;

                if (actualHeight > scaleMaxHeight || actualWidth > scaleMaxWidth) {
                    float heightRatio = actualHeight1 / scaleMaxHeightFloat;
                    float widthRatio = actualWidth1 / scaleMaxWidthFloat;

                    if (heightRatio > widthRatio) {
                        actualHeight1 = actualHeight1 / heightRatio;
                        actualWidth1 = actualWidth1 / heightRatio;
                    } else {
                        actualHeight1 = actualHeight1 / widthRatio;
                        actualWidth1 = actualWidth1 / widthRatio;
                    }

                    // new height and width to be scaled for larger image size
                    actualHeight = (int) actualHeight1;
                    actualWidth = (int) actualWidth1;
                }

                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("new actualHeight :: " + actualHeight + ", new actualWidth :: " + actualWidth);
            }

            if (actualHeight > actualWidth) {
                // height is grater than width
                maxHeight = 1600.0f;
                maxWidth = 1066.0f;
            } else {
                maxHeight = 1066.0f;
                maxWidth = 1600.0f;
            }

            /*ExifInterface exif1;
            try {
                exif1 = new ExifInterface(filePath);

                int orientation1 = exif1.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.e("orientation","Value=" + orientation1  );
            }
            catch(Exception e)
            {

            }*/

			/*
			 * if (actualHeight1>minHeight && actualWidth1 >minWidth) {
			 *
			 * } else { return ""; }
			 */

            //this.LOG1(1, this.MyClassName + "Compress actualHeight=", actualHeight + " actualWidth=" + actualWidth);
            // max Height and width values of the compressed image is taken as
            // 816x612


            DecimalFormat d = new DecimalFormat("#.##");
            Double d1 = (double) 0;
            // 2448/3264

            d1 = (double) (actualWidth1 / actualHeight1);

            Double imgRatio = Double.valueOf(d.format(d1));// Math.round
            // ((actualWidth /
            // actualHeight));

            // 4000/3000

            float maxRatio = maxWidth / maxHeight;
            //this.LOG1(1, this.MyClassName + "Compress imgRatio=", imgRatio + "  maxRatio=" + maxRatio + "   d1=" + d1 + "  actualWidth1/actualHeight1=" + actualWidth1 / actualHeight1);

            // width and height values are set maintaining the aspect ratio of
            // the
            // image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Compress imgRatio < maxRatio,  " + imgRatio + " < " + maxRatio);
                    imgRatio = (double) (maxHeight / actualHeight);
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Compress imgRatio > maxRatio,  " + imgRatio + " > " + maxRatio);
                    imgRatio = (double) (maxWidth / actualWidth);
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("Compress imgRatio == maxRatio,  " + imgRatio + " == " + maxRatio);
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

            // setting inSampleSize value allows to load a scaled down version
            // of
            // the original image
            try {
                options.inSampleSize = this.calculateInSampleSize(options, actualWidth, actualHeight);

                // inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false;
                // this options allow android to claim the bitmap memory if it
                // runs
                // low
                // on memory
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];

                // load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);

            } catch (Exception e) {
                LogWriter.err(e);
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
            } catch (Exception e) {
                LogWriter.err(e);
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            bmp = null;

            // check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("orientation : " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("orientation : " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("orientation : " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("orientation : " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("scaledBitmap Width : " + scaledBitmap.getWidth() + ", Height : " + scaledBitmap.getHeight());
            } catch (Exception e) {
                LogWriter.err(e);
            }

            FileOutputStream out = null;
            filename = this.getFilename();
            try {
                out = new FileOutputStream(filename);

                // write the compressed bitmap at the destination specified by
                // filename.
                scaledBitmap.compress(CompressFormat.JPEG, 70, out);
                out.close();

            } catch (Exception e) {
                LogWriter.err(e);
            }

        } catch (Exception e) {
            LogWriter.err(e);
        }

        return filename;

    }

    /**
     * Creates folder and get file path
     *
     * @return filepath
     */
    public String getFilename() {
        File file = new File(MyConfig.FLINNT_FOLDER_PATH, MyConfig.UPLOAD);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/new_" + System.currentTimeMillis() + ".jpg";

    }

    /**
     * Gets original file content path
     *
     * @param contentURI content uri
     * @return original file path
     */
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
		/*
		 * Cursor cursor = getContentResolver().query(contentUri, null, null,
		 * null, null); if (cursor == null) { return contentUri.getPath(); }
		 * else { cursor.moveToFirst(); int index =
		 * cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); return
		 * cursor.getString(index); }
		 */
        return contentUri.getPath();
    }

    /**
     * Calculate the sample size in what the image is going to be converted
     *
     * @param options   conversion options
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return sample size
     */
    public int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        float totalPixels = width * height;
        float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * Gets all folders containing pdf files
     *
     * @param FolderPath base folder path
     * @return list of folders
     */
    public ArrayList<String> getFolderPaths(String FolderPath) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //this.FileType = FileType1;
        //File directory = new File(android.os.Environment.getExternalStorageDirectory()+ File.separator + AppConstant.PHOTO_ALBUM);
        //File directory = new File(android.os.Environment.getExternalStorageDirectory(),"");
        //File directory = new File(Environment.getExternalStorageDirectory()+"/flinnt/" + MyConfig.GALLERY + "/" ,"");
        File directory = new File(FolderPath, "");

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count

            //Toast.makeText(_context, String.valueOf(listFiles.length), Toast.LENGTH_LONG).show();


            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();

                    // check for supported file extension
                    if (this.IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                        //Log.e("filePath", listFiles[i].getAbsolutePath());
                    }
                }
            } else {
                // image directory is empty
                //Toast.makeText(this._context, AppConstant.PHOTO_ALBUM + " is empty. Please load some images in it !", Toast.LENGTH_LONG).show();
            }

        } else {
        	/*
            Builder alert = new Builder(this._context);
            alert.setTitle("Error!");
            alert.setMessage(AppConstant.PHOTO_ALBUM + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("Close", null);
            alert.show();
            */
        }

        return filePaths;
    }

    /**
     * Checks for the pdf file
     *
     * @param filePath location of file
     * @return true if pdf file, false otherwise
     */
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        if (ext.toLowerCase().equals("pdf"))
            return true;
        else
            return false;
    }
}
