package com.edu.flinnt.firebase.notification;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Invitation;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.InvitationResponse;
import com.edu.flinnt.protocol.LoginResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
/**
 * Created by flinnt-android-3 on 24/3/17.
 */

/**
 * Receives notification calls
 */
//@Nikhil please check below link for deprection
//https://stackoverflow.com/questions/47217345/wakefulbroadcastreceiver-is-deprecated

public class FlinntNotificationReceiver extends WakefulBroadcastReceiver {

    public static String ACCEPT_ACTION = "com.edu.flinnt.action.ACCEPT";
    public static String REJECT_ACTION = "com.edu.flinnt.action.REJECT";

    public static String INVITATION_ID = "invitation_id";
    public static String NOTIFICATION_ID = "notification_id";

    private String invitationID = "";
    private String userID = Config.getStringValue(Config.USER_ID);
    private int notificationID = Flinnt.INVALID;

    private Handler mHandler;
    private Context mContext;
    int notificationId = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("notification intent parameter : " + intent);

        if (intent != null) {

            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                invitationID = bundle.getString(INVITATION_ID);
                notificationID = bundle.getInt(NOTIFICATION_ID);
                if (bundle.containsKey(Config.USER_ID)) {
                    userID = bundle.getString(Config.USER_ID);
                }
            }

            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    closeNotificationDrawer();

                    switch (message.what) {
                        case Flinnt.SUCCESS:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                            // cancel notification on success response
                            if (FlinntFirebaseMessagingService.inviteNotifications.containsKey(invitationID)) {
                                notificationId = FlinntFirebaseMessagingService.inviteNotifications.get(invitationID);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("notificationId : " + notificationId);
                                Helper.cancelNotification(notificationId);
                                FlinntFirebaseMessagingService.inviteNotifications.remove(invitationID);
                            }

                            if (message.obj instanceof InvitationResponse) {
                                InvitationResponse invitationResponse = ((InvitationResponse) message.obj);
                                //Log.i("Course Invites", "\nSubscribed : " + invitationResponse.getSubscribed() + "\nRejected : " + invitationResponse.getRejected());
                                if (invitationResponse.getSubscribed() == Flinnt.TRUE && invitationResponse.getRejected() == Flinnt.INVALID) {
                                    Helper.showToast("Accepted", Toast.LENGTH_SHORT);
                                    invitationResponse.setSubscribed(Flinnt.INVALID);

                                    Bundle mBundle = new Bundle();
                                    mBundle.putParcelable(AddPostResponse.COURSE_KEY, invitationResponse.getAcceptedCourse());
                                    mBundle.putString(LoginResponse.USER_ID_KEY, userID);
                                    mBundle.putInt("isFromNotification", Flinnt.TRUE);

                                    //Log.d("NotiRecc","invitationResponse");

                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("InvitationResponse userID : " + userID + ", Config userID : " + Config.getStringValue(Config.USER_ID));

                                    //Log.d("NotiRecc","InvitationResponse userID : " + userID + ", Config userID : " + Config.getStringValue(Config.USER_ID));
                                    Intent intent = new Intent(mContext, MyCoursesActivity.class);
                                    intent.putExtras(mBundle);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//@Chirag:14/08/2018 added this line  //Old was two line with cleartop&newtask
                                    mContext.startActivity(intent);

                                } else if (invitationResponse.getRejected() == Flinnt.TRUE && invitationResponse.getSubscribed() == Flinnt.INVALID) {
                                    Helper.showToast("Rejected", Toast.LENGTH_SHORT);
                                    invitationResponse.setRejected(Flinnt.INVALID);
                                }
                            }

                            break;
                        case Flinnt.FAILURE:
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof InvitationResponse) {
                                InvitationResponse response = (InvitationResponse) message.obj;
                                if (response.errorResponse != null) {
                                    //*******Change 2.0.27
                                    //Config.setStringValue(Config.USER_ID, userID);
                                    Helper.showToast(response.errorResponse.getMessage(),Toast.LENGTH_SHORT);
                                    //Helper.showAlertMessage(mContext, "Error", response.errorResponse.getMessage(), "CLOSE");
                                }
                            }

                            break;
                        default:
                            super.handleMessage(message);
                    }
                }
            };

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("invitationID : " + invitationID + " , action : " + action);

            //Log.d("NotiRece","invitationId : " + invitationID + " , action : " + action);

            if (Helper.isConnected()) {
                if (ACCEPT_ACTION.equals(action)) {
                    new Invitation(mHandler, invitationID, Invitation.ACCEPT, userID).sendInvitationRequest();
                } else if (REJECT_ACTION.equals(action)) {
                    new Invitation(mHandler, invitationID, Invitation.REJECT, userID).sendInvitationRequest();
                }
                closeNotificationDrawer();

                if (FlinntFirebaseMessagingService.inviteNotifications.containsKey(invitationID)) {
                    notificationId = FlinntFirebaseMessagingService.inviteNotifications.get(invitationID);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("notificationId : " + notificationId);

                    Helper.cancelNotification(notificationId);

                    FlinntFirebaseMessagingService.inviteNotifications.remove(invitationID);
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("invitationID : " + invitationID + " , notificationId : " + notificationId);
                }

            }else {
                Helper.showToast(mContext.getString(R.string.no_internet_conn_title_dialog), Toast.LENGTH_SHORT);
            }



        }

    }

    /**
     * close the notification drawer
     */
    protected void closeNotificationDrawer() {
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.sendBroadcast(it);
    }

}