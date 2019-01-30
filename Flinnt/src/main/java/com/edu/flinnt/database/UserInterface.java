package com.edu.flinnt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.User;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

/**
 * Manages offline Users database operations
 */
public class UserInterface {

    private static UserInterface mUserInterface;

    DatabaseHelper mDBHelper;

    public UserInterface() {
        if (null == mDBHelper) {
            mDBHelper = new DatabaseHelper();
        }
    }

    public static UserInterface getInstance() {
        if (null == mUserInterface) {
            mUserInterface = new UserInterface();
        }
        return mUserInterface;
    }

    public ArrayList<User> getUserList() {
        ArrayList<User> accountList = new ArrayList<User>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("accountList");
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.UserTable.TABLE_NAME, null, null, null, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    try {
                        User mUser = getUserFromCursor(cursor);
                        accountList.add(mUser);
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

        return accountList;
    }

    public ArrayList<String> getUserIdList() {
        ArrayList<String> userIdList = new ArrayList<String>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getUserIdList");
        String[] columns = new String[]{DatabaseHelper.UserTable.USER_ID_KEY};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.UserTable.TABLE_NAME, columns, null, null, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    try {
                        String id = cursor.getString(0);
                        userIdList.add(id);
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

        return userIdList;
    }

    public boolean setToken(String userId, int isTokenSent) {
        try {
            String selection = DatabaseHelper.UserTable.USER_ID_KEY + "=?";
            String[] selectionArgs = new String[]{userId};

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.UserTable.TOKEN_SENT_TO_SERVER, isTokenSent);

            mDBHelper.update(DatabaseHelper.UserTable.TABLE_NAME, values, selection, selectionArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return false;
    }

    public void resetTokenSent() {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.UserTable.TOKEN_SENT_TO_SERVER, Flinnt.FALSE);
            mDBHelper.update(DatabaseHelper.UserTable.TABLE_NAME, values, null, null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public User getUserFromId(String id) {
        User user = null;
        String selection = DatabaseHelper.UserTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{id};
        try {
            Cursor cursor = mDBHelper.query(DatabaseHelper.UserTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                user = getUserFromCursor(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return user;
    }

    public int getTokenValueFromId(String id) {
        try {
            User user = getUserFromId(id);
            return user.getTokenSentToServer();
        } catch (Exception e) {
            LogWriter.err(e);
            return 0;
        }
    }

    public boolean isUserExist(String userId) {
        boolean ret = false;
        String selection = DatabaseHelper.UserTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{userId};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.UserTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.getCount() > 0) {
                ret = true;
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }

        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("isUserExist : " + ret + ", userId : " + userId);

        return ret;
    }

    public boolean isLogInIdExists(String loginId) {
        boolean ret = false;
        String selection = DatabaseHelper.UserTable.USER_LOGIN_KEY + "=?";
        String[] selectionArgs = new String[]{loginId};
        try {
            Cursor cursor = mDBHelper.query(DatabaseHelper.UserTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.getCount() > 0) {
                ret = true;
                cursor.close();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return ret;
    }

    public long insertUser(User user) {
        long rowId = Flinnt.INVALID;

        try {
            ContentValues cv = getContentValuesFromUser(user);
            rowId = mDBHelper.insert(DatabaseHelper.UserTable.TABLE_NAME, cv);
        } catch (Exception e) {
            LogWriter.err(e);
        }

        return rowId;
    }

    public long insertOrUpdateUser(String userId, User user) {
        long rowID = Flinnt.INVALID;

        try {
            if (isUserExist(userId)) {
                rowID = updateUser(userId, user);
            } else {
                ContentValues cv = getContentValuesFromUser(user);
                rowID = mDBHelper.insert(DatabaseHelper.UserTable.TABLE_NAME, cv);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowID;
    }

    public int updateUser(String userId, User user) {
        int rowsAffected = 0;

        try {
            String whereClause = DatabaseHelper.UserTable.USER_ID_KEY + "=?";
            String[] whereArgs = new String[]{userId};
            ContentValues cv = getContentValuesFromUser(user);
            rowsAffected = mDBHelper.update(DatabaseHelper.UserTable.TABLE_NAME, cv, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("updateUser rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * deletes all users from database
     *
     * @return number of deleted users
     */
    public int deleteAllUsers() {
        int rowsAffected = 0;
        try {
            rowsAffected = mDBHelper.delete(DatabaseHelper.UserTable.TABLE_NAME, null, null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    public int deleteUser(String userId) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.UserTable.USER_ID_KEY + "=?";
            String[] whereArgs = new String[]{userId};
            mDBHelper.delete(DatabaseHelper.UserTable.TABLE_NAME, whereClause, whereArgs);
            rowsAffected = mDBHelper.delete(DatabaseHelper.UserTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    private User getUserFromCursor(Cursor c) {
        User user = new User();
        try {
            user.setUserID(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_ID_KEY)));
            user.setUserLogin(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_LOGIN_KEY)));
            user.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.FIRST_NAME_KEY)));
            user.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.LAST_NAME_KEY)));
            user.setIsActive(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_IS_ACTIVE_KEY)));
            user.setUserPicture(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_PICTURE_KEY)));
            user.setAccVerified(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_ACC_VERIFIED_KEY)));
            user.setAccAuthMode(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_ACC_AUTH_MODE_KEY)));
            user.setCanAdd(c.getInt(c.getColumnIndex(DatabaseHelper.UserTable.CAN_ADD_KEY)));
            user.setUserPictureUrl(c.getString(c.getColumnIndex(DatabaseHelper.UserTable.USER_PICTURE_URL_KEY)));
            user.setCanBrowseCourse(c.getInt(c.getColumnIndex(DatabaseHelper.UserTable.CAN_BROWSE_COURSE)));
            user.setTokenSentToServer(c.getInt(c.getColumnIndex(DatabaseHelper.UserTable.TOKEN_SENT_TO_SERVER)));
            user.setCurrentUser(c.getInt(c.getColumnIndex(DatabaseHelper.UserTable.IS_CURRENT_USER)));

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return user;
    }


    private ContentValues getContentValuesFromUser(User user) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(DatabaseHelper.UserTable.USER_ID_KEY, user.getUserID());
            cv.put(DatabaseHelper.UserTable.USER_LOGIN_KEY, user.getUserLogin());
            cv.put(DatabaseHelper.UserTable.FIRST_NAME_KEY, user.getFirstName());
            cv.put(DatabaseHelper.UserTable.LAST_NAME_KEY, user.getLastName());
            cv.put(DatabaseHelper.UserTable.USER_IS_ACTIVE_KEY, user.getIsActive());
            cv.put(DatabaseHelper.UserTable.USER_PICTURE_KEY, user.getUserPicture());
            cv.put(DatabaseHelper.UserTable.USER_ACC_VERIFIED_KEY, user.getAccVerified());
            cv.put(DatabaseHelper.UserTable.USER_ACC_AUTH_MODE_KEY, user.getAccAuthMode());
            cv.put(DatabaseHelper.UserTable.CAN_ADD_KEY, user.getCanAdd());
            cv.put(DatabaseHelper.UserTable.USER_PICTURE_URL_KEY, user.getUserPictureUrl());
            cv.put(DatabaseHelper.UserTable.CAN_BROWSE_COURSE, user.getCanBrowseCourse());
            cv.put(DatabaseHelper.UserTable.TOKEN_SENT_TO_SERVER, user.getTokenSentToServer());
            cv.put(DatabaseHelper.UserTable.IS_CURRENT_USER, user.getCurrentUser());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return cv;
    }

}

