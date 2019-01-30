package com.edu.flinnt.util;

/**
 * Created by flinnt-android-1 on 17/5/17.
 */

public class NotificationDataSet {
    private int autoID = 0;
    private String userID = "";
    private String notificationPayload = "";
    private String notificationTitle = "";
    private String notificationDescription = "";
    private String notificationTimestamp = "";
    private String notificationType = "";
    private String notificationReadStatus = "";

    public int getAutoID() {
        return autoID;
    }

    public void setAutoID(int autoID) {
        this.autoID = autoID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNotificationPayload() {
        return notificationPayload;
    }

    public void setNotificationPayload(String notificationPayload) {
        this.notificationPayload = notificationPayload;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(String notificationTimestamp) {
        this.notificationTimestamp = notificationTimestamp;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationReadStatus() {
        return notificationReadStatus;
    }

    public void setNotificationReadStatus(String notificationReadStatus) {
        this.notificationReadStatus = notificationReadStatus;
    }
}