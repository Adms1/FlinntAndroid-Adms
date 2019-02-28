package com.edu.flinnt.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AppUpdate;
import com.edu.flinnt.core.Highlights;
import com.edu.flinnt.core.Login;
import com.edu.flinnt.core.MenuBanner;
import com.edu.flinnt.core.RegisterDevice;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.SignUp;
import com.edu.flinnt.core.VerifyMobile;
import com.edu.flinnt.database.ContentDetailsInterface;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.database.NotificationInterface;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.MenuBannerResponse;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.protocol.contentlist.Contents;
import com.edu.flinnt.protocol.contentlist.Data;
import com.edu.flinnt.protocol.contentlist.Sections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * this class contains frequently used important methods that can be used in any class of project
 */
public class Helper {

    public static final String TAG = Helper.class.getSimpleName();
    public static PowerManager mPM = null;
    public static WakeLock mWL = null;
    static public int WAKELOCK_COUNTER = 0;

    static public int PROGRESS_DIALOG = 1;
    static public int PROGRESS_DIALOG_TRANSPERENT = 2;
    public static Realm mRealm = null;


    /**
     * Creates directory for file about to be uploaded
     *
     * @return directory path with separator
     */
    public static String getFileUploadDirectory() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.UPLOAD);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }

    /**
     * Gets the location of directory in which image file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntImagePath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.IMAGE_FULL);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }

    /**
     * Gets the location of directory in which audio file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntAudioPath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.AUDIO);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + "/";
    }

    /**
     * Gets the location of directory in which video file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntVideoPath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.VIDEO);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }


    /**
     * Gets the location of directory in which video file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntHiddenVideoPath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_HIDDEN_PATH);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }


    /**
     * Gets the location of directory in which document file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntDocumentPath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.DOCUMENT);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }

    /**
     * Gets the location of directory in which cropped image file getting stored
     *
     * @return directory path with separator
     */
    public static String getFlinntCropImagePath() {
        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.IMAGE_CROPPED);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + File.separator;
    }

    /**
     * Checks for file's existence at specific path
     *
     * @param directoryPath directory to be checked
     * @param fileName      file name
     * @return true if exists, false otherwise
     */
    public static boolean isFileExistsAtPath(String directoryPath, String fileName) {
        File file = new File(directoryPath, fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * deletes directory from storage
     *
     * @param path directory path
     * @return true if deleted, false otherwise
     */
    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * delete files stored long time ago
     *
     * @param path path of file/directory
     */
    public static void deleteOldFiles(File path) {

        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                System.out.println("file is directory : " + files[i].isDirectory());
                if (files[i].isDirectory()) {

                    if (!files[i].getName().equals(MyConfig.IMAGE_FULL)
                            && !files[i].getName().equals(MyConfig.AUDIO)
                            && !files[i].getName().equals(MyConfig.VIDEO)
                            && !files[i].getName().equals(MyConfig.DOCUMENT)
                            ) {
                        deleteOldFiles(files[i]);
                    }

                } else {
                    long lastModTime = files[i].lastModified();

                    if (System.currentTimeMillis() - lastModTime > 15 * 24 * 60 * 60 * 1000) {    // 15 day
                        /** remove more than 15 day old unwanted files...*/
                        files[i].delete();
                    }

                }
            }
        }
    }

    /**
     * Gets default url path of application API in string format
     *
     * @param url app url
     * @return url path
     */
    public static String getFlinntUrlPath(String url) {
        String[] pathArry = url.split("/");
        String directoryPath = "";
        // for example : https://flinnt1.s3.amazonaws.com/courses/240x160/496_1442048951.jpg
        // here exclude pathArry[0] http, pathArry[1] blank, pathArry[2] url
        for (int i = 3; i < pathArry.length - 1; i++) {
            if (i < (pathArry.length - 2)) {
                directoryPath = directoryPath + pathArry[i] + "/";
            } else {
                directoryPath = directoryPath + pathArry[i];
            }
        }

        File tmp = new File(MyConfig.FLINNT_FOLDER_PATH + MyConfig.NOMEDIA + File.separator + directoryPath);
        if (!tmp.exists()) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("directory path : create ");
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + "/";
    }

    /**
     * Return integer value in dip.
     *
     * @param pixel
     */
    public static int getDip(int pixel) {
        float scale = FlinntApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (pixel * scale + 0.5f);
    }

    /**
     * prevents resources from being used concurrently
     */
    public static synchronized void lockCPU() {
        WAKELOCK_COUNTER++;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("lockCPU : counter[" + WAKELOCK_COUNTER + "]");
        if (mPM == null)
            mPM = (PowerManager) FlinntApplication.getContext()
                    .getSystemService(Context.POWER_SERVICE);
        if (mWL == null)
            mWL = mPM
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Flinnt.lock");

        if (mWL.isHeld()) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("lockCPU: already locked");
        } else {
            mWL.acquire();
        }
    }

    /**
     * let resources being used concurrently
     */
    public static synchronized void unlockCPU() {
        WAKELOCK_COUNTER--;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("unlockCPU : counter[" + WAKELOCK_COUNTER + "]");

        if (mWL != null && mWL.isHeld() && WAKELOCK_COUNTER <= 0) {
            mWL.release();
            mWL = null;
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("unlockCPU: already unlocked");
        }
    }

    /**
     * return version name of application
     */
    public static String getAppVersionName(Context context) {
        String latestAppVersionName = Flinnt.VERSION;
        try {
            latestAppVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            String configAppVersionName = Config.getStringValue(Config.FLINNT_VERSION_NAME);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("latestAppVersionName : " + latestAppVersionName + " , configAppVersionName : " + configAppVersionName);
            if (!configAppVersionName.equals(latestAppVersionName)) {
                Config.setStringValue(Config.LAST_APP_UPDATE_RESPONSE, "");
                AppUpdate.mAppUpdateResponse = null;
                Config.setStringValue(Config.LAST_APP_UPDATE_REQUEST_SEND_TIME, "0");
            }
            Config.setStringValue(Config.FLINNT_VERSION_NAME, latestAppVersionName);
        } catch (NameNotFoundException e) {
            LogWriter.err(e);
        }
        return latestAppVersionName;
    }

    /**
     * return version code of application
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = Flinnt.INVALID;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            LogWriter.err(e);
        }
        return versionCode;
    }

    /**
     * Hide keyboard
     */
    public static void hideKeyboardFromWindow(Context context) {
        // Check if no view has focus:
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /**
     * return Drawable from Drawable ID.
     */
    public static Drawable getDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        return drawable;
    }

    /**
     * return Bitmap from Drawable ID.
     */
    public static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                drawableId);
        return bitmap;
    }

    /**
     * return Drawable from Bitmap.
     */
    public static Drawable getDrawableFromBitmap(Context context, Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;
    }

    public static RealmConfiguration createRealmObj() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("contentlist.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        return realmConfiguration;
    }

    /**
     * resetConfigValuesAndClearData function .... empty config values.
     */
    public static void resetConfigValuesAndClearData(final String id) {
        mRealm = Realm.getInstance(createRealmObj());

        // Cancel all pending request before exit
        Requester.getInstance().cancelPendingRequests();

        if (null == id) {
            for (String userId : UserInterface.getInstance().getUserIdList()) {
                new RegisterDevice(null, RegisterDevice.UNREGISTER_DEVICE, userId).sendRegisterDeviceRequest();
            }
            PostInterface.getInstance().deleteAllPost();
            CourseInterface.getInstance().deleteAllCourse();
            UserInterface.getInstance().deleteAllUsers();
            NotificationInterface.getInstance().deleteAllNotifications();

            Config.setStringValue(Config.USER_ID, "");
            Config.setStringValue(Config.USER_LOGIN, "");
            Config.setStringValue(Config.PASSWORD, "");
            Config.setStringValue(Config.FIRST_NAME, "");
            Config.setStringValue(Config.LAST_NAME, "");
            Config.setStringValue(Config.PROFILE_NAME, "");
            Config.setStringValue(Config.PROFILE_URL, "");
            Config.setStringValue(Config.ACCOUNT_VERIFIED, "");
            Config.setStringValue(Config.ACCOUNT_AUTH_MODE, "");

            Config.setStringValue(Config.GCM_TOKEN, "");
            Config.setStringValue(Config.FCM_TOKEN, "");
            Config.setBoolValue(Config.TOKEN_SENT_TO_SERVER, false);

            Config.setStringValue(Config.LAST_LOGIN_RESPONSE, "");
            Config.setStringValue(Config.LAST_SIGNUP_RESPONSE, "");
            Config.setStringValue(Config.LAST_VERIFYMOBILE_RESPONSE, "");
            Config.setStringValue(Config.LAST_MENU_BANNER_POST_RESPONSE, "");
            //Config.setStringValue(Config.LAST_HIGHLIGHTS_RESPONSE, "");

            Login.mLoginResponse = null;
            SignUp.mSignUpResponse = null;
            VerifyMobile.mVerifyMobileResponse = null;
            MenuBanner.mMenuBannerResponse = new MenuBannerResponse();
            Highlights.mHighlightResponse = null;
            AppUpdate.mAppUpdateResponse = null;


            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
            ContentDetailsInterface.getInstance().deleteAll();
        } else {
            new RegisterDevice(null, RegisterDevice.UNREGISTER_DEVICE, id).sendRegisterDeviceRequest();

            PostInterface.getInstance().deleteAllPostForUser(id);
            CourseInterface.getInstance().deleteAllCourseForUser(id);
            UserInterface.getInstance().deleteUser(id);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        RealmResults<Data> rows = realm.where(Data.class).equalTo("userID", id).findAll();
//                                        LogWriter.write("In side of insert execution : "+rows.size());
//                                        rows.deleteAllFromRealm();

//                                        Data courseContent = realm.where(Data.class).equalTo("courseID", mCourseId).findFirst();
                        for (int i = 0; i < rows.size(); i++) {
                            RealmList<Sections> sectionList = rows.get(i).getList();
                            for (int j = 0; j < sectionList.size(); j++) {
                                RealmList<Contents> contentList = sectionList.get(j).getContents();
                                for (int k = 0; k < contentList.size(); k++) {
                                    if (k < contentList.size()) {
                                        contentList.get(k).getStatistics().deleteFromRealm();
                                        contentList.get(k).getAttachments().deleteAllFromRealm();
                                    }
                                }
                                contentList.deleteAllFromRealm();
                            }
                            sectionList.deleteAllFromRealm();
//                                            courseContent.deleteFromRealm();

                        }
                        rows.deleteAllFromRealm();
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }
            });
            ContentDetailsInterface.getInstance().deleteAllForUser(id);
        }
        // Clear all notification
        cancelNotification(Flinnt.INVALID);
    }


    public static void cancelNotification(int notificationId) {
        NotificationManager nMgr = (NotificationManager)FlinntApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationId != Flinnt.INVALID) {
            nMgr.cancel(notificationId);
        } else {
            nMgr.cancelAll();
        }
    }


    /**
     * Check if connected to internet or not
     * return true if enable
     */
    public static boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) FlinntApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            // Toast.makeText(con, "No Internet connection!",
            // Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }

		/* if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
		    }
		    return false;*/
        //return mobileDataEnabled;
    }

    /**
     * Check if wifi enable of not
     * return true if enable
     */
    public static boolean isConnectedOverWifi() {
        boolean wifiEnabled = false; // Assume disabled
        ConnectivityManager connManager = (ConnectivityManager) FlinntApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            wifiEnabled = mWifi.isConnected();
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return wifiEnabled;
    }

    /**
     * Builds and display dialog
     *
     * @param context       activity context
     * @param title         dialog title
     * @param message       dialog message
     * @param dialogBtnText dailog operation button text
     */
    public static void showAlertMessage(final Context context, String title, String message, String dialogBtnText) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        //alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle("Change Password");
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText(title);
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);

        // set dialog message
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(dialogBtnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!isFinishingOrIsDestroyed((Activity) context)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * Displays message of network inavailibility
     * network availability
     *
     * @param context
     */
    public static void showNetworkAlertMessage(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        //alertDialogBuilder.setTitle(R.string.no_internet_conn_title_dialog);
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText(R.string.no_internet_conn_title_dialog);
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.no_internet_conn_message_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.action_settings,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            LogWriter.err(e);
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        if (!isFinishingOrIsDestroyed((Activity) context)) {

            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * Display a toast bubble message
     *
     * @param text        message
     * @param toastLength time to stay on display
     */
    public static void showToast(String text, int toastLength) {
        Toast.makeText(FlinntApplication.getContext(), text, toastLength).show();
    }

    /**
     * theme should be 'Helper.PROGRESS_DIALOG' or 'Helper.PROGRESS_DIALOG_TRANSPERENT'
     */
    public static ProgressDialog getProgressDialog(Context context, String title, String message, int theme) {
        ProgressDialog progressDialog = null;
        if (theme == PROGRESS_DIALOG) {
            progressDialog = new ProgressDialog(context);
            if (!TextUtils.isEmpty(title)) progressDialog.setTitle(title);
            if (!TextUtils.isEmpty(message)) progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
        } else {
            progressDialog = new ProgressDialog(context, R.style.MyDialogTheme);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        }
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    /**
     * Returns the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Returns the start time of day from calender time and sets calender to today
     */
    public static long getCurrentDateStartMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long millis = 0;
        mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        millis = mCalendar.getTimeInMillis();
        //LogWriter.write("current Date Millis : " + millis);

        return millis;
    }

    /**
     * Returns the end time of day from calender time and sets calender to today
     */
    public static long getCurrentDateEndMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long millis = 0;
        mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        millis = mCalendar.getTimeInMillis();
        //LogWriter.write("current Date Millis : " + millis);

        return millis;
    }

    /**
     * Returns the start time of year from calender time and sets calender to current year
     */
    public static long getCurrentYearMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long millis = 0;
        mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        mCalendar.set(Calendar.MONTH, 0);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        millis = mCalendar.getTimeInMillis();
        //LogWriter.write("current Year Millis : " + millis);

        return millis;
    }

    /**
     * Returns the end time of year from calender time and sets calender to today
     */
    public static long getCurrentYearEndMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long millis = 0;
        mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        mCalendar.set(Calendar.MONTH, 11);
        mCalendar.set(Calendar.DAY_OF_MONTH, 31);
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        millis = mCalendar.getTimeInMillis();
        //LogWriter.write("current Year Millis : " + millis);

        return millis;
    }

    /**
     * Format seconds time to dd/MM/yyyy
     *
     * @param seconds seconds
     * @return time in dd/MM/yyyy
     */
    public static String formateTimeMillis(long seconds) {

        Long millis = seconds * 1000;
        String myFormat = "dd MMM yyyy"; // "dd/MM/yyyy"; In which you need put here

        if (millis > getCurrentDateStartMillis() && millis < getCurrentDateEndMillis()) {
            myFormat = "HH:mm";
        } else if (millis > getCurrentYearMillis() && millis < getCurrentYearEndMillis()) {
            myFormat = "dd MMM";
        }
        /*SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		String formatedStr = sdf.format(millis).toUpperCase();*/

        return formateTimeMillis(seconds, myFormat);
    }

    /**
     * Format seconds time to dd/MM/yyyy
     *
     * @param seconds seconds
     * @return time in dd/MM/yyyy
     */
    public static String formateDate(long seconds) {

        Long millis = seconds * 1000;
        //String myFormat = "MMMM DD,yyyy"; // "dd/MM/yyyy"; In which you need put here
        String myFormat = "dd MMM yyyy"; // "dd/MM/yyyy"; In which you need put here

        if (millis > getCurrentDateStartMillis() && millis < getCurrentDateEndMillis()) {
            myFormat = "HH:mm";
        } else if (millis > getCurrentYearMillis() && millis < getCurrentYearEndMillis()) {
            myFormat = "dd MMM yyyy";
        }
		/*SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		String formatedStr = sdf.format(millis).toUpperCase();*/

        return formateTimeMillis(seconds, myFormat);
    }

    public static String formateDateDetails(long seconds) {
        String myFormat = "MMMM dd,yyyy"; // "dd/MM/yyyy"; In which you need put here
        return formateTimeMillis(seconds, myFormat).toLowerCase();
    }

    /**
     * Format seconds time to dd/MM/yyyy
     *
     * @param seconds seconds
     * @return time in dd/MM/yyyy
     */
    public static String formateDateTime(long seconds) {
        Long millis = seconds * 1000;
        String myFormat = "dd MMM yyyy, HH:mm";

        if (millis > getCurrentDateStartMillis() && millis < getCurrentDateEndMillis()) {
            myFormat = "HH:mm";
        } else if (millis > getCurrentYearMillis() && millis < getCurrentYearEndMillis()) {
            myFormat = "dd MMM yyyy, HH:mm";
        }
		/*SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		String formatedStr = sdf.format(millis).toUpperCase();*/

        return formateTimeMillis(seconds, myFormat);
    }

    /**
     * Format seconds time
     *
     * @param seconds seconds
     * @param format  format to convert (e.g DD MM YYYY HH:MM)
     * @return time in given format
     */
    public static String formateTimeMillis(long seconds, String format) {
        Long millis = seconds * 1000;
        String myFormat = format; // "DD MMM YYYY HH:MM"; In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formatedStr = sdf.format(millis).toUpperCase();
        return formatedStr;
    }

    /**
     * Set resource ids according to file type
     *
     * @param type file type
     * @return file type image code
     */
    public static int getDrawableIdFromType(int type) {
        int drawable = Flinnt.INVALID;
        switch (type) {
            case Flinnt.POST_CONTENT_GALLERY:
                drawable = R.drawable.ic_card_type_gallery;
                break;
            case Flinnt.POST_CONTENT_AUDIO:
                drawable = R.drawable.ic_card_type_audio;
                break;
            case Flinnt.POST_CONTENT_VIDEO:
                drawable = R.drawable.ic_card_type_video;
                break;
            case Flinnt.POST_CONTENT_LINK:
                drawable = R.drawable.ic_card_type_attache;
                break;
            case Flinnt.POST_CONTENT_DOCUMENT:
                drawable = R.drawable.ic_card_type_document;
                break;
            case Flinnt.POST_CONTENT_ALBUM:
                drawable = R.drawable.ic_attachment_album;
                break;

            default:
                break;
        }
        return drawable;
    }

    /**
     * Sets default image according to file type
     *
     * @param type          attachment type
     * @param fileExtention file extension
     * @return image resource id
     */
    public static int getDefaultPostImageFromType(int type, String fileExtention) {
        int defaultDrawable = R.drawable.album_default;

        switch (type) {
            case Flinnt.POST_CONTENT_GALLERY:
                defaultDrawable = R.drawable.ic_attachment_photo;
                break;
            case Flinnt.POST_CONTENT_AUDIO:
                defaultDrawable = R.drawable.ic_attachment_audio;
                break;
            case Flinnt.POST_CONTENT_VIDEO:
                defaultDrawable = R.drawable.ic_attachment_video;
                break;
            case Flinnt.POST_CONTENT_LINK:
                defaultDrawable = R.drawable.ic_attachment_link;
                break;
            case Flinnt.POST_CONTENT_DOCUMENT:
                defaultDrawable = R.drawable.ic_postview_document_pdf;
                switch (fileExtention) {
                    case "pdf":
                        defaultDrawable = R.drawable.ic_postview_document_pdf;
                        break;

                    case "doc":
                    case "docx":
                    case "odt":
                        defaultDrawable = R.drawable.ic_postview_document_doc;
                        break;

                    case "xls":
                    case "xlsx":
                    case "ods":
                        defaultDrawable = R.drawable.ic_postview_document_excel;
                        break;

                    case "ppt":
                    case "pptx":
                        defaultDrawable = R.drawable.ic_postview_document_ppt;
                        break;

                    case "txt":
                        defaultDrawable = R.drawable.ic_postview_document_txt;
                        break;

                    default:
                        defaultDrawable = R.drawable.ic_postview_document_pdf;
                        break;
                }
                break;
            case Flinnt.POST_CONTENT_ALBUM:
                defaultDrawable = R.drawable.ic_attachment_album;
                break;

            default:
                break;
        }
        return defaultDrawable;
    }

    /**
     * Gets content type code by content name
     *
     * @param name content name
     * @return content type code
     */
    public static int getPostContentTypeFromName(String name) {
        int postContentType = Flinnt.POST_CONTENT_GALLERY;
        switch (name.toUpperCase()) {
            case "IMAGE":
                postContentType = Flinnt.POST_CONTENT_GALLERY;
                break;
            case "AUDIO":
                postContentType = Flinnt.POST_CONTENT_AUDIO;
                break;
            case "VIDEO":
                postContentType = Flinnt.POST_CONTENT_VIDEO;
                break;
            case "LINK":
                postContentType = Flinnt.POST_CONTENT_LINK;
                break;
            case "DOCUMENT":
                postContentType = Flinnt.POST_CONTENT_DOCUMENT;
                break;
            case "ALBUM":
                postContentType = Flinnt.POST_CONTENT_ALBUM;
                break;

            default:
                break;
        }
        return postContentType;
    }

    /**
     * Returns parsed string to integer
     */
    public static int getIntegerValue(String str) {
        int val = Flinnt.INVALID;
        try {
            val = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return val;
    }

    /**
     * creates image rounded at corner
     *
     * @param bitmap     image
     * @param color      color
     * @param cornerDips corner pixles
     * @param borderDips border pixles
     * @param context    activity context
     * @return rounded corner image
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_user_profile_image);
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }

    /**
     * Gets image from storage
     *
     * @param dirPath  storage directory path
     * @param fileName file name
     * @return image
     */
    public static Bitmap getBitmapFromSDcard(String dirPath, String fileName) {
        Bitmap bitmap = null;
        try {
            String photoPath = dirPath + File.separator + fileName;

            if (new File(photoPath).exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(photoPath, options);
            }

        } catch (Exception e) {
        }

        return bitmap;
    }


    /**
     * Stores image to SD card
     *
     * @param bitmap   image
     * @param dirPath  directory path
     * @param fileName file name
     */
    public static void saveBitmapToSDcard(Bitmap bitmap, String dirPath, String fileName) {
		
		/*if(TextUtils.isEmpty(fileName) || bitmap == null){
			return;
		}*/

        File mediafile = new File(dirPath, fileName);
        String strMyMediaPath = mediafile.getAbsolutePath();

        if (!mediafile.exists() || mediafile.length() <= 0) {
            LogWriter.write("Media Path to create : " + strMyMediaPath);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mediafile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                LogWriter.err(e);
            } catch (Exception e) {
                LogWriter.err(e);
            }
        } else {
            LogWriter.write("Media already exist : " + strMyMediaPath);
        }

        new SingleMediaScanner(FlinntApplication.getContext(), mediafile);
        //updateGallery();
    }

    /**
     * scan media folder
     */
    public static void updateGallery() {
        // scan directory...
//		19 is Kitkat // causes permission issues even under 19
//        if (android.os.Build.VERSION.SDK_INT < 19) {
//            FlinntApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        } else {
        scanDirectory(new File(MyConfig.FLINNT_FOLDER_PATH));
//        }

    }

    /**
     * Scans directory
     *
     * @param path directory path
     */
    public static void scanDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {

                        if (files[i].getName().equals(MyConfig.IMAGE_FULL)
                                || files[i].getName().equals(MyConfig.VIDEO)
                                ) {
                            scanDirectory(files[i]);
                        }

                    } else {
                        new SingleMediaScanner(FlinntApplication.getContext(), files[i]);
                    }
                }
            }
        }
    }

    /**
     * Get extension from filename
     *
     * @param name filename
     * @return extension (e.g. jpeg, pdf)
     */
    public static String getExtension(String name) {
        String ext = "";
        try {
            int i = name.lastIndexOf('.');

            if (i > 0 && i < name.length() - 1) {
                ext = name.substring(i + 1).toLowerCase();
            }
        } catch (Exception e) {
            ext = "";
        }
        return ext;
    }

    /**
     * Returns string from input stream
     */
    public static String InputStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LogWriter.err(e);
        }
        return sb.toString();
    }

    /**
     * Creates an output media file according to file type
     *
     * @param type file type
     * @return media file
     */
    public static File getOutputMediaFile(int type) {

        // External sdcard location
  /*
   * File mediaStorageDir = new File( Environment
   * .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
   * IMAGE_DIRECTORY_NAME);
   */
        File mediaStorageDir = new File(getFileUploadDirectory());
        // Create the storage directory if it does not exist

//      if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.exists() && !mediaStorageDir.isDirectory()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        String UploadFileName;
        if (type == Flinnt.POST_CONTENT_GALLERY) {

            UploadFileName = "IMG_" + timeStamp + ".jpg";

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + UploadFileName);

        } else if (type == Flinnt.POST_CONTENT_VIDEO) {
            UploadFileName = "VID_" + timeStamp + ".mp4";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + UploadFileName);
        } else if (type == Flinnt.POST_CONTENT_AUDIO) {
            UploadFileName = "AUD_" + timeStamp + ".mp4";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + UploadFileName);
        } else if (type == Flinnt.POST_CONTENT_DOCUMENT) {
            UploadFileName = "DOC_" + timeStamp + ".pdf";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + UploadFileName);
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Returns cropped output media file
     */
    public static File getCropOutputFile() {

        File mediaStorageDir = new File(getFlinntCropImagePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        String UploadFileName;

        UploadFileName = "IMG_" + timeStamp + ".jpg";

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + UploadFileName);

        return mediaFile;
    }

    /**
     * for 6 digit varification code only...
     * bleow regexpr will by default find the first 6 digit match
     */

    public static String extractDigits(final String in, int digitCount) {
        final Pattern p = Pattern.compile("(\\d{" + digitCount + "})");
        final Matcher m = p.matcher(in);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public static boolean isFinishingOrIsDestroyed(Activity activity) {
        if (null == activity) return true;
        if (android.os.Build.VERSION.SDK_INT >= 17) { // isDestroyed() introduced in API 17
            return activity.isDestroyed() || activity.isFinishing();
        } else {
            return activity.isFinishing();
        }
    }

    public static void setCurrentUserConfig(String userId) {
        User user = UserInterface.getInstance().getUserFromId(userId);
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("setCurrentUserConfig userID : " + userId + ", user : " + user);
        Log.d("Helpp", "setCurrentUserConfig userID : " + userId + ", user : " + user.toString());
        if (null != user) {

            //Chirag:20/08/2018 //For handle session of webview****start
            if (!userId.equals(Config.getStringValue(Config.USER_ID))) {
                Log.d("Helpp", "current user is different userID : " + userId + ", user : " + user.toString());
                Config.setIsAllowedToClearSession(Config.IS_ALLOWED_TO_CLEAR_WEB_VIEW_SESSION, true);
            }
            //*end for chirag

            Config.setStringValue(Config.USER_ID, user.getUserID());
            Config.setStringValue(Config.USER_LOGIN, user.getUserLogin());
            Config.setStringValue(Config.ACCOUNT_VERIFIED, user.getAccVerified());
            Config.setStringValue(Config.ACCOUNT_AUTH_MODE, user.getAccAuthMode());
            Config.setStringValue(Config.FIRST_NAME, user.getFirstName());
            Config.setStringValue(Config.LAST_NAME, user.getLastName());
            Config.setStringValue(Config.PROFILE_NAME, user.getUserPicture());
            Config.setStringValue(Config.PROFILE_URL, user.getUserPictureUrl());

        }
    }

}
