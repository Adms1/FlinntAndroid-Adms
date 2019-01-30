package com.edu.flinnt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.NotificationDataSet;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manages offline Courses database operations
 */

public class NotificationInterface {

    private static NotificationInterface mNotificationInterface;

    DatabaseHelper mDBHelper;


    public NotificationInterface() {
        if (null == mDBHelper) {
            mDBHelper = new DatabaseHelper();
        }
    }

    public static NotificationInterface getInstance() {
        if (null == mNotificationInterface) {
            mNotificationInterface = new NotificationInterface();
        }
        return mNotificationInterface;
    }

    /**
     * Add a course to database
     *
     * @param  notificationData
     * @param  notificationType
     * @param  timeStamp
     * @return row id
     */
    public long insertNotification(String notificationData,String notificationType,String timeStamp) {
        long rowID = Flinnt.INVALID;
        try {
            ContentValues cv = getNotificationValuesFromCourse(notificationData,notificationType,timeStamp);
            rowID = mDBHelper.insert(DatabaseHelper.NotificationTable.TABLE_NAME, cv);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        // printCourse( Course );
        return rowID;
    }

    /**
     * Gets content values from course object
     *
     * @param notificationData
     * @param notificationType
     * @param timeStamp
     * @return content values
     */
    private ContentValues getNotificationValuesFromCourse(String notificationData,String notificationType,String timeStamp) {
        ContentValues cv = new ContentValues();
        try {

            JSONObject obj = new JSONObject(notificationData);

            cv.put(DatabaseHelper.NotificationTable.USER_ID_KEY,obj.getString("user_id"));
            cv.put(DatabaseHelper.NotificationTable.NOTIFICATION_PAYLOAD_KEY,notificationData);
            cv.put(DatabaseHelper.NotificationTable.NOTIFICATION_TYPE_KEY,notificationType);
            cv.put(DatabaseHelper.NotificationTable.NOTIFICATION_TIMESTAMP,timeStamp);
            cv.put(DatabaseHelper.NotificationTable.NOTIFICATION_READ_STATUS_KEY,Flinnt.DISABLED);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return cv;
    }


    /**
     * Gets all courses stored in offline database
     *
     * @return offline course list
     */
    public ArrayList<NotificationDataSet> getNotificationsByUser() {
        ArrayList<NotificationDataSet> notificationList = new ArrayList<NotificationDataSet>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getNotificationsByUser");
        Cursor cursor = null;
        try {
          SQLiteDatabase db = mDBHelper.getWritableDatabase();
          cursor = db.rawQuery("SELECT * FROM "+DatabaseHelper.NotificationTable.TABLE_NAME+" WHERE " + DatabaseHelper.NotificationTable.USER_ID_KEY + " = " + Config.getStringValue(Config.USER_ID)+" ORDER BY "+DatabaseHelper.NotificationTable.NOTIFICATION_TIMESTAMP + " DESC", null);

            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("cursor : " + cursor);
            if (null != cursor && cursor.moveToFirst()) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("count : " + cursor.getCount());
                do {
                    try {
                        NotificationDataSet notificationDataSet = getNotificationFromCursor(cursor);
                        notificationList.add(notificationDataSet);
                    } catch (Exception e) {
                        LogWriter.err(e);
                    }
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }

        return notificationList;
    }


    /**
     * Generate course object from database
     *
     * @param c cursor
     * @return course object
     */
    private NotificationDataSet getNotificationFromCursor(Cursor c) {
        NotificationDataSet notificationDataSet = new NotificationDataSet();
        try {
            String jsonStr = c.getString(c.getColumnIndex(DatabaseHelper.NotificationTable.NOTIFICATION_PAYLOAD_KEY)).toString();
            LogWriter.write("Notification Payload : "+jsonStr);
            JSONObject obj = new JSONObject(jsonStr);

            notificationDataSet.setAutoID(c.getInt(c.getColumnIndex(DatabaseHelper.NotificationTable.AUTO_ID_KEY)));
            notificationDataSet.setNotificationPayload(c.getString(c.getColumnIndex(DatabaseHelper.NotificationTable.NOTIFICATION_PAYLOAD_KEY)));
            notificationDataSet.setNotificationTitle(""+obj.getString("title"));
            notificationDataSet.setUserID(""+obj.getString(DatabaseHelper.NotificationTable.USER_ID_KEY));

            notificationDataSet.setNotificationDescription(""+obj.getString("msg"));
            long notificationDate = 0;
            if(!obj.getString("notify_dt").equalsIgnoreCase("") && obj.getString("notify_dt") != null){
                notificationDataSet.setNotificationTimestamp(Helper.formateDateTime(Long.parseLong(obj.getString("notify_dt"))));
            }

            notificationDataSet.setNotificationType(""+c.getString(c.getColumnIndex(DatabaseHelper.NotificationTable.NOTIFICATION_TYPE_KEY)));
            notificationDataSet.setNotificationReadStatus(""+c.getString(c.getColumnIndex(DatabaseHelper.NotificationTable.NOTIFICATION_READ_STATUS_KEY)));
        } catch (Exception e) {
            LogWriter.write("Exception get notification time : "+e.toString());
        }
        return notificationDataSet;
    }

    /**
     * deletes all notification from database
     *
     * @return number of deleted notification
     */
    public int deleteNotificationForUser(int autoId) {
        int rowsAffected = 0;
        try {
            rowsAffected = mDBHelper.delete(DatabaseHelper.NotificationTable.TABLE_NAME, DatabaseHelper.NotificationTable.AUTO_ID_KEY + "='"+autoId+"' and " + DatabaseHelper.NotificationTable.USER_ID_KEY + "=" + Config.getStringValue(Config.USER_ID), null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * deletes all notification from database
     *
     * @return number of deleted notification
     */
    public void updateNotificationReadingStatus(int autoId) {
        int rowsAffected = 0;
        try {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(DatabaseHelper.NotificationTable.NOTIFICATION_READ_STATUS_KEY, Flinnt.ENABLED);
            String updateQery = "UPDATE "+DatabaseHelper.NotificationTable.TABLE_NAME+" SET "+DatabaseHelper.NotificationTable.NOTIFICATION_READ_STATUS_KEY+" = "+Flinnt.ENABLED+" WHERE "+DatabaseHelper.NotificationTable.USER_ID_KEY+" = "+Config.getStringValue(Config.USER_ID)+" AND "+DatabaseHelper.NotificationTable.AUTO_ID_KEY+" = '"+autoId+"'";
            db.execSQL(updateQery);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    /**
     * deletes all notification from database
     *
     * @return number of deleted notification
     */
    public int deleteOlderNotificationForUser(Long timeStamp) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldCourse timeStamp : " + timeStamp);
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.NotificationTable.NOTIFICATION_TIMESTAMP + "<?";
            String[] whereArgs = new String[]{String.valueOf(timeStamp)};
            rowsAffected = mDBHelper.delete(DatabaseHelper.NotificationTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * deletes all users from database
     *
     * @return number of deleted users
     */
    public int deleteAllNotifications() {
        int rowsAffected = 0;
        try {
            rowsAffected = mDBHelper.delete(DatabaseHelper.NotificationTable.TABLE_NAME, null, null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * Gets all courses stored in offline database
     *
     * @return offline course list
     */
    public int getUnreadNotification() {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getNotificationsByUser");
        Cursor cursor = null;
        int totalUnread = 0;

        try {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM "+DatabaseHelper.NotificationTable.TABLE_NAME+" WHERE " + DatabaseHelper.NotificationTable.USER_ID_KEY + " = " + Config.getStringValue(Config.USER_ID) +" AND "+DatabaseHelper.NotificationTable.NOTIFICATION_READ_STATUS_KEY +" = " + Flinnt.DISABLED, null);
            totalUnread = cursor.getCount();
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }

        return totalUnread;
    }

    public void printCourse(Course course) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write(course.toString());
    }
}