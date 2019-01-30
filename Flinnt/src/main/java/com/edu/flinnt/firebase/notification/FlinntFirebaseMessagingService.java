package com.edu.flinnt.firebase.notification;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.PostList;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.database.NotificationInterface;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.downloads.AppInfoDataSet;
import com.edu.flinnt.downloads.DownloadService;
import com.edu.flinnt.gui.BrowseCourseDetailActivity;
import com.edu.flinnt.gui.ContentsDetailActivity;
import com.edu.flinnt.gui.CourseInvitesActivity;
import com.edu.flinnt.gui.InAppPreviewActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.PostDetailActivity;
import com.edu.flinnt.gui.QuizHelpActivty;
import com.edu.flinnt.gui.QuizQuestionsActivity;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.protocol.PostListRequest;
import com.edu.flinnt.protocol.PostListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.DownloadMediaFile;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flinnt-android-1 on 15/3/17.
 */

public class FlinntFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessaging";
    private static NotificationManager mNotificationManager = (NotificationManager) FlinntApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    final String ADMIN_CHANNEL_ID = "100";
    // Notification Type
    public static final String NOTIFICATION_TYPE_P = "P";   // New Quiz, Blog, Message , Album , Poll added OR Poll Result
    public static final String NOTIFICATION_TYPE_PC = "PC"; // New comment posted on blog, quiz, message or album
    public static final String NOTIFICATION_TYPE_CI = "CI"; // course invitation
    public static final String NOTIFICATION_TYPE_CU = "CU"; // course Un-subscribe
    public static final String NOTIFICATION_TYPE_CC = "CC"; // Custom Notification

    public static final String NOTIFICATION_TYPE_LCC = "LCC"; // New comment posted on lms content

    public static final String NOTIFICATION_TYPE_TA = "TA"; // Targeted Activity

    public static final String NOTIFICATION_MYCOURSE_TA = "901"; // To open MyCourse screen.
    public static final String NOTIFICATION_BROWSECOURSE_TA = "902"; // To open BrowseCourse screen.
    public static final String NOTIFICATION_BROWSECOURSE_DETAIL_TA = "903"; // To open browse course detail screen.
    public static final String NOTIFICATION_CONTENT_DETAIL_TA = "918"; //
    public static final String NOTIFICATION_BROWSECOURSE_DETAIL_SHARE_TA = "90301"; // To open browse course detail screen and heighlight share option.
    public static final String NOTIFICATION_BROWSECOURSE_DETAIL_RATTING_TA = "90302"; // To open browse course detail screen and heighlight share option.

    public static final String NOTIFICATION_QUIZ_HELP_TA = "928"; // To open browse course detail screen and heighlight share option.
    public static final String NOTIFICATION_STORY_TA_933 = "933"; // To open browse course detail screen and heighlight share option.
    public static final String NOTIFICATION_STORY_TA_932 = "932";

    public static HashMap<String, Integer> inviteNotifications = new HashMap<>();
    private PostList mPostList = null;
    private PostListRequest mPostListRequest;
    private PostListResponse mPostListResponse = null;


    @SuppressLint("WrongConstant")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        try {
            String senderID = remoteMessage.getFrom();
            if (!senderID.equalsIgnoreCase(Flinnt.FCM_SENDER_ID)) {
                return;
            }


            String userID = remoteMessage.getData().get("user_id");
            String type = remoteMessage.getData().get("type");
            //Log.d(TAG + "data", remoteMessage.getData().toString());

            if (remoteMessage.getData().get("title") == null) {
                remoteMessage.getData().put("title", remoteMessage.getNotification().getTitle());
            }
            if (remoteMessage.getData().get("msg") == null) {
                remoteMessage.getData().put("msg", remoteMessage.getNotification().getBody());
            }

            if (!TextUtils.isEmpty(type)) {

                if (!TextUtils.isEmpty(userID) && UserInterface.getInstance().isUserExist(userID)) {
                    if (type.equalsIgnoreCase(NOTIFICATION_TYPE_P)) {
                        //Log.d(TAG, "onNewPostReceived()");
                        onNewPostReceived(remoteMessage.getData());
                    } else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_PC)) {
                        //Log.d(TAG, "onNewCommentReceived()");
                        onNewCommentReceived(remoteMessage.getData());
                    } else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_CI)) {
                        //Log.d(TAG, "onCourseInvitationReceived()");
                        onCourseInvitationReceived(remoteMessage.getData());
                    } else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_CC)) {
                        //Log.d(TAG, "onCustomNotificationReceived()");
                        onCustomNotificationReceived(remoteMessage.getData());
                    } else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_LCC)) {
                        //Log.d(TAG, "onNewCommentReceivedLms()");
                        onNewCommentReceivedLms(remoteMessage.getData());
                    } else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_TA)) {
                        String activityID = remoteMessage.getData().get("activity_id");
                        if (activityID.equalsIgnoreCase(NOTIFICATION_MYCOURSE_TA)) {
                            //.d(TAG, "onTA901Received()");
                            onTA901Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_TA)) {
                            //.d(TAG, "onTA902Received()");
                            onTA902Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_TA)) {
                           // Log.d(TAG, "onTA903Received()");
                            onTA903Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_SHARE_TA)) {
                            //Log.d(TAG, "onTA90301Received()");
                            onTA90301Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_RATTING_TA)) {
                            //Log.d(TAG, "onTA90302Received()");
                            onTA90302Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_QUIZ_HELP_TA)) {
                            //Log.d(TAG, "onTA928Received()");
                            onTA928Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_STORY_TA_933)) {
                            //@Nikhil 2662018
                            //Log.d(TAG, "onTA933Received()");
                            onTA933Received(remoteMessage.getData());
                        } else if (activityID.equalsIgnoreCase(NOTIFICATION_STORY_TA_932)) {
                            //@Nikhil 2762018
                            //Log.d(TAG, "onTA932Received()");
                            onTA932Received(remoteMessage.getData());
                        }
                    }
                }
                /* // some times notification comes with userID doesn't exist in database
                else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_CC)) {
                    onCustomNotificationReceived(data);
                }
                */
                else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_CU)) {
                    onCourseUnsubscribed(remoteMessage.getData());
                }
            }

        } catch (Exception e) {
            LogWriter.err(e);
            //Log.d(TAG, e.getMessage());
        }


    }

    private void onCustomNotificationReceived(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onCustomNotificationReceived");

        try {
            String type = data.get("post_type");
            JSONObject object = new JSONObject(data);
            String jsonData = object.toString();
            //Log.d("JSON_OBJECT", jsonData);

            if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                NotificationInterface.getInstance().insertNotification(jsonData, type, data.get("notify_dt"));
            }
        } catch (Exception e) {
            LogWriter.write("Error : " + e.toString());
            LogWriter.err(e);
        }

        try {
            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");
            String userId = "";
            if (data.containsKey("user_id")) userId = data.get("user_id");
            Intent notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
            notificationIntent.putExtra("user_id", userId);
            setNotification(notificationIntent, title, msg, notify);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    private void onCourseUnsubscribed(Map<String, String> data) {
        LogWriter.write("onCourseUnsubscribed");

        try {
            String userID = data.get("user_id");
            String courseID = data.get("course_id");
            LogWriter.write("userID : " + userID + ", courseID : " + courseID);
            CourseInterface.getInstance().deleteCourse(userID, courseID);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * When new post is added to subscribes course create notification
     *
     * @param data Data bundle containing message data as key/value pairs.      *             For Set of keys use data.keySet(). data bundle
     */
    private void onNewPostReceived(Map<String, String> data) {

        try {
            String userID = data.get("user_id");
            String courseID = data.get("course_id");
            String courseTitle = data.get("title");
//            courseTitle = courseTitle.substring(0, courseTitle.length() - 1);
            String postID = data.get("post_id");
            String type = data.get("type");
            String postTypeStr = data.get("post_type");
            String coursePictureUrl = data.get("course_picture_url");
            String coursePicture = data.get("course_picture");
            String courseName = data.get("course_name");
            String canComment = data.get(Course.COURSE_CAN_COMMENT);
            String allowedRole = data.get(Course.ALLOWED_ROLES_KEY);


            try {
                JSONObject object = new JSONObject(data);
                String jsonData = object.toString();
                //Log.e("JSON_OBJECT", jsonData);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsonData, type, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", courseID : " + courseID + ", postID : " + postID);
            Intent notificationIntent = null;
            int postType = Helper.getIntegerValue(postTypeStr);

            //Log.d(TAG, "onNewPostRecieved() : postType : " + postType);

            if (postType == 18) {
                //for story type
                notificationIntent = new Intent(FlinntApplication.getContext(), InAppPreviewActivity.class);
                if (notificationIntent != null) {
                    /*******This is old code
                     JSONObject object = new JSONObject(data);
                     notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                     notificationIntent.putExtra(Config.USER_ID, userID);
                     notificationIntent.putExtra(Flinnt.KEY_INAPP_URL, object.getString("story_url"));
                     notificationIntent.putExtra(Flinnt.KEY_INAPP_TITLE, object.getString("course_name"));
                     //notificationIntent.putExtra(Flinnt.NOTIFICATION_SCREENID, 1);    //old*/

                    JSONObject object = new JSONObject(data);
                    notificationIntent.putExtra(Flinnt.KEY_INAPP_URL, object.getString("story_url"));
                    notificationIntent.putExtra(Flinnt.KEY_INAPP_TITLE, object.getString("course_name"));
                    //This is new code @chirag 07/08/2018
                    notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                    notificationIntent.putExtra(Config.USER_ID, userID);
                    notificationIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                    notificationIntent.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
                    notificationIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                    notificationIntent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRole);
                    notificationIntent.putExtra(Flinnt.NOTIFICATION_SCREENID, 2);

                    //For Issue : 7741
                    //Pass One more extraValue for specify that navigation of backbutton in InAppPreviewActivity.java
                    //then provide condition on that extraValue for back button navigation to CourseDetail For
                    //Communication list.
                }
            } else {

                //Log.d(TAG,"onNewPostReceived()..for postDetailActivity");
                notificationIntent = new Intent(FlinntApplication.getContext(), PostDetailActivity.class);

                if (notificationIntent != null) {
                    notificationIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                    notificationIntent.putExtra(Post.POST_ID_KEY, postID);
                    notificationIntent.putExtra(Config.USER_ID, userID);
                    notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                    notificationIntent.putExtra(Course.POST_TYPE, postType);
                    notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                    notificationIntent.putExtra(Course.USER_PICTURE_KEY, coursePicture);
                    notificationIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                    notificationIntent.putExtra(Course.COURSE_CAN_COMMENT, canComment);
                    notificationIntent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRole);
                }
            }


            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");
            String autoDownload = data.get("auto_download");

            setNotification(notificationIntent, title, msg, notify);

            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("autoDownload : " + autoDownload);
            if (!TextUtils.isEmpty(autoDownload) && autoDownload.equals(Flinnt.ENABLED)) {
                ArrayList<String> postIdList = new ArrayList<>();
                postIdList.add(postID);

                mPostList = new PostList(mHandler, courseID, PostInterface.POST_TYPE_BLOG_QUIZ, postIdList, true);
                mPostList.sendPostListRequest(mPostListRequest);
            }
        } catch (Exception e) {
            LogWriter.write("Error : " + e.toString());
            LogWriter.err(e);
        }
    }

    /**
     * When new comment is posted create notification
     *
     * @param data Data bundle containing message data as key/value pairs.      *             For Set of keys use data.keySet(). data bundle
     */
    private void onNewCommentReceived(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewCommentReceived");
        try {
            String userID = data.get("user_id");
            String courseID = data.get("course_id");
            String postID = data.get("post_id");
            String type = data.get("post_type");
            String coursePictureUrl = data.get("course_picture_url");
            String coursePicture = data.get("course_picture");
            String courseName = data.get("course_name");
            String canComment = data.get(Course.COURSE_CAN_COMMENT);
            String allowedRole = data.get(Course.ALLOWED_ROLES_KEY);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", courseID : " + courseID + ", postID : " + postID);
            Intent notificationIntent = null;
            int postType = Helper.getIntegerValue(type);

            try {
                JSONObject object = new JSONObject(data);
                String jsonData = object.toString();
                //Log.e("JSON_OBJECT", jsonData);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsonData, type, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            notificationIntent = new Intent(FlinntApplication.getContext(), PostDetailActivity.class);
            if (notificationIntent != null) {
                notificationIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                notificationIntent.putExtra(Post.POST_ID_KEY, postID);
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                notificationIntent.putExtra(Course.USER_PICTURE_KEY, coursePicture);
                notificationIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
                notificationIntent.putExtra(Course.POST_TYPE, postType);
                notificationIntent.putExtra(Course.COURSE_CAN_COMMENT, canComment);
                notificationIntent.putExtra(Course.ALLOWED_ROLES_KEY, allowedRole);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * When course invitation is received creates notification
     *
     * @param data Data bundle containing message data as key/value pairs.      *             For Set of keys use data.keySet(). data bundle
     */
    private void onCourseInvitationReceived(Map<String, String> data) {

        //Log.d("onCourseInvitation", data.toString());

        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onCourseInvitationReceived");
        try {
            String type = data.get("post_type");
            JSONObject object = new JSONObject(data);
            String jsonData = object.toString();
            //Log.d("JSON_OBJECT", jsonData);

            if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                NotificationInterface.getInstance().insertNotification(jsonData, type, data.get("notify_dt"));
            }
        } catch (Exception e) {
            LogWriter.write("Error : " + e.toString());
            LogWriter.err(e);
        }
        try {
            new generatePictureStyleNotification(this, data).execute();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        String userID;
        String courseID;
        String postID;
        String invitationID;
        int notificationID;
        String title;
        String msg;
        String notify;
        String pictureUrl;
        String picture;
        String isRequest;
        String notificationUrl;


        public generatePictureStyleNotification(Context context, Map<String, String> data) {
            super();
            this.mContext = context;
            if (data.containsKey("user_id"))
                userID = data.get("user_id");
            if (data.containsKey("course_id"))
                courseID = data.get("course_id");
            if (data.containsKey("course_name"))
                postID = data.get("course_name");
            if (data.containsKey("invitation_id"))
                invitationID = data.get("invitation_id");
            notificationID = (int) (System.currentTimeMillis() / 1000) + Integer.parseInt(userID);
            if (data.containsKey("title"))
                title = data.get("title");
            if (data.containsKey("msg"))
                msg = data.get("msg");
            if (data.containsKey("notify"))
                notify = data.get("notify");
            if (data.containsKey("picture_url"))
                pictureUrl = data.get("picture_url");
            if (data.containsKey("picture"))
                picture = data.get("picture");
            if (data.containsKey("is_request"))
                isRequest = data.get("is_request");
            if (isRequest.equals(Flinnt.ENABLED)) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    notificationUrl = pictureUrl + Flinnt.PROFILE_LARGE + File.separator + picture;
                } else {
                    notificationUrl = pictureUrl + Flinnt.PROFILE_MEDIUM + File.separator + picture;
                }

            } else {
                notificationUrl = pictureUrl + Flinnt.PROFILE_LARGE + File.separator + picture;
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(notificationUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("WrongConstant")
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            try {
                Intent notificationIntent = new Intent(FlinntApplication.getContext(), CourseInvitesActivity.class);
                notificationIntent.putExtra(Course.COURSE_ID_KEY, courseID);
                notificationIntent.putExtra(Post.POST_ID_KEY, postID);
                notificationIntent.putExtra(Config.USER_ID, userID);

                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("setNotification title : " + title + ", message : " + msg + ", notify : " + notify);

                NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(FlinntApplication.getContext(), ADMIN_CHANNEL_ID);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence adminChannelName = getString(R.string.app_name);
                    String adminChannelDescription = getString(R.string.app_name);
                    NotificationChannel adminChannel;
                    adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
                    adminChannel.setDescription(adminChannelDescription);
                    adminChannel.enableLights(true);
                    adminChannel.setLightColor(Color.RED);
                    adminChannel.enableVibration(true);

                    adminChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notiBuilder.setChannelId(ADMIN_CHANNEL_ID);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(adminChannel);
                    }
                }


                if (isRequest.equals(Flinnt.ENABLED)) {
                    notiBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                            .setColor(getResources().getColor(R.color.ColorPrimary))
                            .setTicker(title)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setAutoCancel(true);
                    Bitmap bitmap = getCircleBitmap(result);
                    if (bitmap != null) {
                        notiBuilder.setLargeIcon(bitmap);
                    }

                } else {
                    notiBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                            .setColor(getResources().getColor(R.color.ColorPrimary))
                            .setTicker(title)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setAutoCancel(true);
                    if (result != null) {
                        notiBuilder.setLargeIcon(result);
                    }
                }


                if (notify != null) {
                    if (notify.equals(Flinnt.ENABLED)) {
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        notiBuilder.setSound(defaultSoundUri);
                    }
                }

                if (null != notificationIntent) {

                    PendingIntent contentIntent = TaskStackBuilder.create(FlinntApplication.getContext())
                            .addNextIntentWithParentStack(notificationIntent)
                            .getPendingIntent((int) (System.currentTimeMillis() / 1000), PendingIntent.FLAG_CANCEL_CURRENT); //PendingIntent.FLAG_UPDATE_CURRENT);
                    notiBuilder.setContentIntent(contentIntent);
                }

                Bundle bundle = new Bundle();
                bundle.putString(FlinntNotificationReceiver.INVITATION_ID, invitationID);
                bundle.putInt(FlinntNotificationReceiver.NOTIFICATION_ID, notificationID);
                bundle.putString(Config.USER_ID, userID);

                //accept intent
                Intent acceptReceive = new Intent();
                acceptReceive.setAction(FlinntNotificationReceiver.ACCEPT_ACTION);
                acceptReceive.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    acceptReceive.setClass(FlinntFirebaseMessagingService.this, FlinntNotificationReceiver.class);

                PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(FlinntFirebaseMessagingService.this, notificationID, acceptReceive, PendingIntent.FLAG_CANCEL_CURRENT);
                if (Build.VERSION.SDK_INT <= 19) {
                    notiBuilder.addAction(R.drawable.ic_course_invites_accept_white, "ACCEPT", pendingIntentAccept);
                } else {
                    notiBuilder.addAction(R.drawable.ic_course_invites_accept, "ACCEPT", pendingIntentAccept);
                }


                //reject intent
                Intent rejectReceive = new Intent();
                rejectReceive.setAction(FlinntNotificationReceiver.REJECT_ACTION);
                rejectReceive.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    rejectReceive.setClass(FlinntFirebaseMessagingService.this, FlinntNotificationReceiver.class);
                PendingIntent pendingIntentReject = PendingIntent.getBroadcast(FlinntFirebaseMessagingService.this, notificationID, rejectReceive, PendingIntent.FLAG_CANCEL_CURRENT);
                if (Build.VERSION.SDK_INT <= 19) { // 19 = Build.VERSION_CODES.KITKAT
                    notiBuilder.addAction(R.drawable.ic_course_invites_reject_white, "REJECT", pendingIntentReject);
                } else {
                    notiBuilder.addAction(R.drawable.ic_course_invites_reject, "REJECT", pendingIntentReject);
                }

                inviteNotifications.put(invitationID, notificationID);
                mNotificationManager.notify(notificationID, notiBuilder.build());
            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.BLUE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        //   final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
//    canvas.drawOval(rectF, paint);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    /**
     * build notification
     *
     * @param notificationIntent intent to be started
     * @param title              notification title
     * @param message            notification message
     * @param notify             notification time
     */
    @SuppressLint("WrongConstant")
    private void setNotification(Intent notificationIntent, String title, String message, String notify) {

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(FlinntApplication.getContext(), ADMIN_CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence adminChannelName = getString(R.string.app_name);
            String adminChannelDescription = getString(R.string.app_name);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            adminChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notiBuilder.setChannelId(ADMIN_CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(adminChannel);
            }
        }

        notiBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getResources().getColor(R.color.ColorPrimary))
                .setTicker(title)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);
        if (notify.equals(Flinnt.ENABLED)) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notiBuilder.setSound(defaultSoundUri);
        }

        if (null != notificationIntent) {
            int code = (int) (System.currentTimeMillis() % 100000000L);
            PendingIntent contentIntent = TaskStackBuilder.create(FlinntApplication.getContext())
                    .addNextIntentWithParentStack(notificationIntent)
                    .getPendingIntent(code, PendingIntent.FLAG_ONE_SHOT); //PendingIntent.FLAG_UPDATE_CURRENT);

            notiBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(code, notiBuilder.build());
        }

    }


    @SuppressLint("WrongConstant")
    private void setNotification(Intent notificationIntent, String title, String message, String notify, String bannerUrl) {
        LogWriter.write("setNotification title : " + title + ", message : " + message + ", notify : " + notify + ", bannerUrl : " + bannerUrl);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(FlinntApplication.getContext(), ADMIN_CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence adminChannelName = getString(R.string.app_name);
            String adminChannelDescription = getString(R.string.app_name);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            adminChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notiBuilder.setChannelId(ADMIN_CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(adminChannel);
            }
        }


        Bitmap image = null;
        try {
            URL url = new URL(bannerUrl);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(title);
        notiStyle.setSummaryText(message);
        notiStyle.bigPicture(image);


        if (bannerUrl == null) {
            notiBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                    .setColor(getResources().getColor(R.color.ColorPrimary))
                    .setTicker(title)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true);
        } else {
            notiBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                    .setColor(getResources().getColor(R.color.ColorPrimary))
                    .setTicker(title)
                    .setContentTitle(title)
                    .setStyle(notiStyle)
                    .setAutoCancel(true);
        }


        if (notify.equals(Flinnt.ENABLED)) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notiBuilder.setSound(defaultSoundUri);
        }

        if (null != notificationIntent) {
            int code = (int) (System.currentTimeMillis() % 100000000L);
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("reqCode : " + code);

            PendingIntent contentIntent = TaskStackBuilder.create(FlinntApplication.getContext())
                    .addNextIntentWithParentStack(notificationIntent)
                    .getPendingIntent(code, PendingIntent.FLAG_ONE_SHOT); //PendingIntent.FLAG_UPDATE_CURRENT);

            notiBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(code, notiBuilder.build());
        }

    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Flinnt.SUCCESS:
                    //stopProgressDialog();
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                    if (message.obj instanceof PostListResponse) {
                        mPostListResponse = (PostListResponse) message.obj;
                        if (mPostListResponse != null) {
                            startAutoDownload(mPostListResponse);
                        }
                    }
                    break;

                case Flinnt.FAILURE:
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                    break;

                case DownloadMediaFile.DOWNLOAD_COMPLETE:
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("DOWNLOAD_COMPLETE");
                    break;

                case DownloadMediaFile.DOWNLOAD_FAIL:
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("DOWNLOAD_FAIL");
                    break;
            }
        }
    };

    /**
     * starts downloading media if users has set auto download on
     */
    public void startAutoDownload(PostListResponse mPostListResponse) {
        String attachment = null;
        String attachmentURL = null;
        String postID = null;
        int contentType = 0;


        if (mPostListResponse != null) {
            if (mPostListResponse.getPostList().size() > 0) {
                attachment = mPostListResponse.getPostList().get(0).getAttachments();
                attachmentURL = mPostListResponse.getPostList().get(0).getAttachmentUrl();
                postID = mPostListResponse.getPostList().get(0).getPostID();
                contentType = Integer.parseInt(mPostListResponse.getPostList().get(0).getPostContentType());
            }
        }

        if (LogWriter.isValidLevel(Log.INFO))
            switch (contentType) {
                case Flinnt.POST_CONTENT_AUDIO:
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), attachment)) {
                        String audiourl = attachmentURL + attachment;
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("audio url : " + audiourl);
                        DownloadMediaFile downloadMediaFile = new DownloadMediaFile(FlinntApplication.getContext(), Helper.getFlinntAudioPath(), attachment, Long.valueOf(postID), audiourl, mHandler);
                        downloadMediaFile.execute();
                    }
                    break;

                case Flinnt.POST_CONTENT_VIDEO:
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), attachment)) {
                        String videourl = attachmentURL + attachment;
                        Flinnt.appInfoDataSets.clear();
                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(postID, attachment, videourl, videourl, Flinnt.DISABLED);
                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntVideoPath());
                        Flinnt.appInfoDataSets.add(appInfoDataSet);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);

                    }
                    break;

                case Flinnt.POST_CONTENT_DOCUMENT:
                    if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), attachment)) {
                        String docurl = attachmentURL + attachment;

                        Flinnt.appInfoDataSets.clear();
                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(postID, attachment, docurl, docurl, Flinnt.DISABLED);
                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntDocumentPath());
                        Flinnt.appInfoDataSets.add(appInfoDataSet);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);

                    }
                    break;

                case Flinnt.POST_CONTENT_GALLERY:
                    Flinnt.appInfoDataSets.clear();
                    String urlNoCrop = attachmentURL + Flinnt.GALLERY_NOCROP + File.separator + attachment;
                    LogWriter.write("audio url : " + urlNoCrop);
                    AppInfoDataSet appInfoDataSet = new AppInfoDataSet(postID, attachment, urlNoCrop, urlNoCrop, Flinnt.DISABLED);
                    appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                    Flinnt.appInfoDataSets.add(appInfoDataSet);

                    download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                    break;

                case Flinnt.POST_CONTENT_ALBUM:
                    String photos = attachment;
                    String[] photosArr = photos.split(",");
                    Flinnt.appInfoDataSets.clear();
                    if (photosArr.length > 0) {
                        for (int i = 0; i < photosArr.length; i++) {
                            final String imageFileName = photosArr[i].trim();
                            if (!TextUtils.isEmpty(imageFileName)) {
                                String albumUrl = attachmentURL + Flinnt.GALLERY_NOCROP + File.separator + imageFileName;
                                AppInfoDataSet appInfoDataSetalbum = new AppInfoDataSet(postID, imageFileName, albumUrl, albumUrl, Flinnt.DISABLED);
                                appInfoDataSetalbum.setDownloadFilePath(Helper.getFlinntImagePath());
                                Flinnt.appInfoDataSets.add(appInfoDataSetalbum);
                                Bitmap albumbitmap = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), imageFileName);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("album photo url " + i + " : " + albumUrl + " , Bitmap : " + albumbitmap);
                                if (albumbitmap == null) {
                                    if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), imageFileName)) {
                                        download(i, appInfoDataSetalbum.getUrl(), Flinnt.appInfoDataSets.get(i));
                                    } else {
                                        if (LogWriter.isValidLevel(Log.INFO))
                                            LogWriter.write("album bitmap -----> notification skiped : " + imageFileName);
                                    }
                                }
                            }
                        }
                    }
                    break;

                default:
                    break;

            }
    }

    /**
     * When new comment is posted create notification
     *
     * @param data Data bundle containing message data as key/value pairs.      *             For Set of keys use data.keySet(). data bundle
     */
    private void onNewCommentReceivedLms(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewCommentReceived");
        try {
            String type = data.get("type");
            String userID = data.get("user_id");
            String contentType = data.get("content_type");
            String contentID = data.get("content_id");
            String courseId = data.get("course_id");
            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");
            String autoDownload = data.get("auto_download");
            String attachType = data.get("attach_type");
            String attachFile = data.get("attach_file");
            String attachUrl = data.get("attach_url");

            String coursePictureUrl = data.get("course_picture_url");
            String coursePicture = data.get("course_picture");
            String courseName = data.get("course_name");
            String attachmentDoEncode = data.get("attachment_do_encode");


            try {
                JSONObject object = new JSONObject(data);
                String jsonData = object.toString();
                //Log.d("JSON_OBJECT", jsonData);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsonData, type, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", contentID : " + contentID + ", title : " + title);
            Intent notificationIntent = null;
            int postType = Helper.getIntegerValue(type);

            notificationIntent = new Intent(FlinntApplication.getContext(), ContentsDetailActivity.class);

            //Log.d(TAG, "onNewCommentt : contentId" + contentID);

            if (notificationIntent != null) {
                notificationIntent.putExtra("course_id", courseId);
                notificationIntent.putExtra("content_id", contentID);
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, coursePictureUrl);
                notificationIntent.putExtra(Course.USER_PICTURE_KEY, coursePicture);
                notificationIntent.putExtra(Course.COURSE_NAME_KEY, courseName);
            }

            setNotification(notificationIntent, title, msg, notify);

            if (!TextUtils.isEmpty(autoDownload) && autoDownload.equals(Flinnt.ENABLED)) {
                switch (Integer.parseInt(attachType)) {
                    case Flinnt.POST_CONTENT_AUDIO:
                        if (!Helper.isFileExistsAtPath(Helper.getFlinntAudioPath(), attachFile)) {
                            String audiourl = attachUrl + attachFile;
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("audio url : " + audiourl);
                            DownloadMediaFile downloadMediaFile = new DownloadMediaFile(FlinntApplication.getContext(), Helper.getFlinntAudioPath(), attachFile, Long.parseLong(contentID), audiourl, mHandler);
                            downloadMediaFile.execute();
                        }
                        break;
                    case Flinnt.POST_CONTENT_VIDEO:
                        if (!Helper.isFileExistsAtPath(Helper.getFlinntVideoPath(), attachFile)) {
                            String videourl = attachUrl + attachFile;
                            Flinnt.appInfoDataSets.clear();
                            AppInfoDataSet appInfoDataSet = new AppInfoDataSet(contentID, attachFile, videourl, videourl, attachmentDoEncode);
                            String fileVideoPath;
                            if (attachmentDoEncode.equals(Flinnt.ENABLED)) {
                                fileVideoPath = Helper.getFlinntHiddenVideoPath();
                            } else {
                                fileVideoPath = Helper.getFlinntVideoPath();
                            }
                            appInfoDataSet.setDownloadFilePath(fileVideoPath);
                            Flinnt.appInfoDataSets.add(appInfoDataSet);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);

                        }
                        break;
                    case Flinnt.POST_CONTENT_DOCUMENT:
                        if (!Helper.isFileExistsAtPath(Helper.getFlinntDocumentPath(), attachFile)) {
                            String docurl = attachUrl + attachFile;

                            Flinnt.appInfoDataSets.clear();
                            AppInfoDataSet appInfoDataSet = new AppInfoDataSet(contentID, attachFile, docurl, docurl, attachmentDoEncode);
                            appInfoDataSet.setDownloadFilePath(Helper.getFlinntDocumentPath());
                            Flinnt.appInfoDataSets.add(appInfoDataSet);
                            download(0, appInfoDataSet.getUrl(), appInfoDataSet);

                        }
                        break;
                    case Flinnt.POST_CONTENT_GALLERY:
                        Flinnt.appInfoDataSets.clear();
                        String urlNoCrop = attachUrl + Flinnt.GALLERY_NOCROP + File.separator + attachFile;

                        AppInfoDataSet appInfoDataSet = new AppInfoDataSet(contentID, attachFile, urlNoCrop, urlNoCrop, attachmentDoEncode);
                        appInfoDataSet.setDownloadFilePath(Helper.getFlinntImagePath());
                        Flinnt.appInfoDataSets.add(appInfoDataSet);
                        download(0, appInfoDataSet.getUrl(), appInfoDataSet);
                        break;

                    case Flinnt.POST_CONTENT_ALBUM:

                        String photos = attachFile;
                        String[] photosArr = photos.split(",");
                        Flinnt.appInfoDataSets.clear();
                        if (photosArr.length > 0) {
                            for (int i = 0; i < photosArr.length; i++) {
                                final String imageFileName = photosArr[i].trim();
                                if (!TextUtils.isEmpty(imageFileName)) {
                                    String albumUrl = attachUrl + Flinnt.GALLERY_NOCROP + File.separator + imageFileName;
                                    AppInfoDataSet appInfoDataSetalbum = new AppInfoDataSet(contentID, imageFileName, albumUrl, albumUrl, attachmentDoEncode);
                                    appInfoDataSetalbum.setDownloadFilePath(Helper.getFlinntImagePath());
                                    Flinnt.appInfoDataSets.add(appInfoDataSetalbum);
                                    Bitmap albumbitmap = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), imageFileName);
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("album photo url " + i + " : " + albumUrl + " , Bitmap : " + albumbitmap);
                                    if (albumbitmap == null) {
                                        if (!Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), imageFileName)) {
                                            download(i, appInfoDataSetalbum.getUrl(), Flinnt.appInfoDataSets.get(i));
                                        } else {
                                            if (LogWriter.isValidLevel(Log.INFO))
                                                LogWriter.write("album bitmap -----> notification skiped : " + imageFileName);
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    private void onTA901Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {
            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            // To get Big/Banner image in notification

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }

            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsonData = object.toString();
                //Log.e("JSON_OBJECT", jsonData);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsonData, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + " , " + bannerUrl + " , " + coursePictureUrl + " , " + coursePicture);
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void onTA902Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {
            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }


            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsonData = object.toString();
                //Log.e("JSON_OBJECT", jsonData);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsonData, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID);
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(Config.IS_BROWSECOURSE, true);
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void onTA903Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {
            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            String message = data.get("data");

            JSONObject jsonData = new JSONObject(message);    // create JSON obj from string

            String courseID = jsonData.getString("course_id");

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }

            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsondata = object.toString();
                //.e("JSON_OBJECT", jsondata);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsondata, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", courseID : " + courseID);
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);

            //Log.d(TAG, "from notification 903 : userID : " + userID);
            if (notificationIntent != null) {
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(BrowsableCourse.ID_KEY, courseID);
                notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.BROWSECOURSE_NORMAL);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void onTA90301Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {

            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            String message = data.get("data");

            JSONObject jsonData = new JSONObject(message);    // create JSON obj from string

            String courseID = jsonData.getString("course_id");

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }

            //Log.d(TAG, "from notification 90301 : userID : " + userID);
            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsondata = object.toString();
                //Log.e("JSON_OBJECT", jsondata);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsondata, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", bannerUrl : " + bannerUrl);
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(BrowsableCourse.ID_KEY, courseID);
                notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.BROWSECOURSE_SHARE_HEIGHLIGHT);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void onTA90302Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {
            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            String message = data.get("data");

            JSONObject jsonData = new JSONObject(message);    // create JSON obj from string

            String courseID = jsonData.getString("course_id");

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }

            //Log.d(TAG, "from notification 90302 : userID : " + userID);

            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsondata = object.toString();
                //Log.e("JSON_OBJECT", jsondata);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsondata, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", bannerUrl : " + bannerUrl);
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(BrowsableCourse.ID_KEY, courseID);
                notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.BROWSECOURSE_RATTING_HEIGHLIGHT);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    //@Nikhil
    private void onTA933Received(Map<String, String> data) {

        LogWriter.write("onStoryReceived");
        //Log.d(TAG, "onTA933Received()");

        try {
            String bannerUrl = null;
            String userID = data.get("user_id");
            String message = data.get("data");

            JSONObject jsonData = new JSONObject(message);    // create JSON obj from string
            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsondata = object.toString();

                //Log.d("JSON_OBJECT", jsondata);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsondata, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", courseID : ");
            Intent notificationIntent = null;

            notificationIntent = new Intent(FlinntApplication.getContext(), InAppPreviewActivity.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(Flinnt.KEY_INAPP_URL, jsonData.getString("story_url"));
                notificationIntent.putExtra(Flinnt.KEY_INAPP_TITLE, jsonData.getString("course_name"));
                notificationIntent.putExtra(Flinnt.NOTIFICATION_SCREENID, 1);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    //@Nikhil
    private void onTA932Received(Map<String, String> data) {
        try {
            String bannerUrl = "";
            String userID = data.get("user_id");
            Intent notificationIntent = null;
            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
            if (notificationIntent != null) {
                notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra("user_id", userID);//@Chirag : 10/08/2018 For switch account
                notificationIntent.putExtra(Flinnt.NOTIFICATION_SCREENID, 0);
            }
            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");
            setNotification(notificationIntent, title, msg, notify);


        } catch (Exception e) {
            LogWriter.err(e);
            //Log.d(TAG, e.getMessage());
        }
    }

    private void onTA928Received(Map<String, String> data) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onNewPostReceived");
        try {
            String bannerUrl = null, coursePictureUrl = null, coursePicture = null;
            String userID = data.get("user_id");

            String message = data.get("data");

            JSONObject jsonData = new JSONObject(message);    // create JSON obj from string

            String courseID = jsonData.getString("course_id");
            String quizID = jsonData.getString("quiz_id");
            String contentID = jsonData.getString("content_id");
            String quizName = jsonData.getString("quiz_name");

            String hasBanner = data.get("has_banner");
            if (hasBanner.equalsIgnoreCase(Flinnt.ENABLED)) {
                String banner = data.get("banner");
                JSONObject jsonDataBanner = new JSONObject(banner);
                int isExternal = jsonDataBanner.getInt("is_external");
                if (isExternal == Flinnt.TRUE) {
                    bannerUrl = jsonDataBanner.getString("banner_url");
                } else {
                    coursePictureUrl = jsonDataBanner.getString("course_picture_url");
                    coursePicture = jsonDataBanner.getString("course_picture");
                    bannerUrl = coursePictureUrl + Flinnt.COURSE_MEDIUM + File.separator + coursePicture;
                }
            }

            try {
                String activityID = data.get("activity_id");
                JSONObject object = new JSONObject(data);
                String jsondata = object.toString();
                Log.e("JSON_OBJECT", jsondata);

                if (data.get("offline").equalsIgnoreCase(Flinnt.ENABLED)) {
                    NotificationInterface.getInstance().insertNotification(jsondata, activityID, data.get("notify_dt"));
                }
            } catch (Exception e) {
                LogWriter.write("Error : " + e.toString());
                LogWriter.err(e);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("userID : " + userID + ", courseID : " + courseID);
            Intent notificationIntent = null;

            //Log.d(TAG,"onTA928Received()_ _");

            notificationIntent = new Intent(FlinntApplication.getContext(), QuizHelpActivty.class);

            if (notificationIntent != null) {
                notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                notificationIntent.putExtra(Config.USER_ID, userID);
                notificationIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, courseID);
                notificationIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, quizID);
                notificationIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, contentID);
                notificationIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, quizName);
            }

            String title = data.get("title");
            String msg = data.get("msg");
            String notify = data.get("notify");

            setNotification(notificationIntent, title, msg, notify, bannerUrl);


        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    private void download(int position, String tag, AppInfoDataSet info) {
        DownloadService.intentDownload(getApplicationContext(), position, position, tag, info);
    }

}