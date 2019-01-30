package com.edu.flinnt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

/**
 * Manages offline Courses database operations
 */

public class CourseInterface {

    private static CourseInterface mCourseInterface;

    DatabaseHelper mDBHelper;


    public CourseInterface() {
        if (null == mDBHelper) {
            mDBHelper = new DatabaseHelper();
        }
    }


    public static CourseInterface getInstance() {
        if (null == mCourseInterface) {
            mCourseInterface = new CourseInterface();
        }
        return mCourseInterface;
    }

    /**
     * Gets all courses stored in offline database
     *
     * @return offline course list
     */
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courseList = new ArrayList<Course>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getAllCourses");
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID)};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("cursor : " + cursor);
            if (null != cursor && cursor.moveToFirst()) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("count : " + cursor.getCount());
                do {
                    try {
                        Course course = getCourseFromCursor(cursor);
                        courseList.add(course);
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

        return courseList;
    }

    /**
     * Get all cources ids stored in offline database
     *
     * @return offline course ids list
     */
    public ArrayList<String> getAllCourseIDs() {


        ArrayList<String> courseIDList = new ArrayList<String>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getAllCourseIDs");
        String[] columns = new String[]{DatabaseHelper.CourseTable.COURSE_ID_KEY};
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID)};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    try {
                        String id = cursor.getString(0);
                        courseIDList.add(id);
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
        //Log.d("courseidsall",courseIDList.toString());
        return courseIDList;
    }

    /**
     * Checks how many courses are currently stored in offline database
     *
     * @return number of courses stored offline
     */
    public int getOfflineCourseSize() {
        int count = 0;
        String[] columns = new String[]{DatabaseHelper.CourseTable.COURSE_ID_KEY};
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID)};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getOfflineCourseSize : " + count);
        return count;
    }

    /**
     * Apply changes to current database
     */
    public void updateDatabase() {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("updateDatabase");
        String[] columns = new String[]{DatabaseHelper.CourseTable.COURSE_ID_KEY};
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID)};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                int count = cursor.getCount();
                if (count > Flinnt.MAX_OFFLINE_COURSE_SIZE) {
                    if (cursor.moveToPosition(Flinnt.MAX_OFFLINE_COURSE_SIZE)) {
                        do {
                            try {
                                String id = cursor.getString(0);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("updateDatabase deleteCourse id  : " + id);
                                deleteCourse(id);
                            } catch (Exception e) {
                                LogWriter.err(e);
                            }
                        }
                        while (cursor.moveToNext());
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }
    }

    /**
     * Get course by id
     *
     * @param id course id
     * @return course
     */
    public Course getCourseFromID(String id) {
        Course Course = new Course();
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=? and " + DatabaseHelper.CourseTable.COURSE_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), id};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                Course = getCourseFromCursor(cursor);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }

        // printCourse( Course );
        return Course;
    }

    /**
     * checks for existence of course
     *
     * @param id course id
     * @return true if exist, false otherwise
     */
    public boolean isCourseExist(String id) {
        boolean ret = false;
        String selection = DatabaseHelper.CourseTable.USER_ID_KEY + "=? and " + DatabaseHelper.CourseTable.COURSE_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), id};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.CourseTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
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
            LogWriter.write("isCourseExist : " + ret + ", courseID : " + id);
        return ret;
    }

    /**
     * Add a course to database
     *
     * @param Course course
     * @return row id
     */
    public long insertCourse(Course Course) {
        long rowID = Flinnt.INVALID;

        try {
            ContentValues cv = getContentValuesFromCourse(Course);
            rowID = mDBHelper.insert(DatabaseHelper.CourseTable.TABLE_NAME, cv);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        // printCourse( Course );
        return rowID;
    }

    /**
     * Add or Edit course on database
     *
     * @param course course
     * @return row id
     */
    public long insertOrUpdateCourse(Course course) {
        //Log.d("mycoursereq", "insertOrUpdateCourse");
        long rowID = Flinnt.INVALID;

        try {
            if (isCourseExist(course.getCourseID())) {
                rowID = updateCourse(course);
                //Log.d("mycoursereq", "update");
            } else {
                ContentValues cv = getContentValuesFromCourse(course);
                rowID = mDBHelper.insert(DatabaseHelper.CourseTable.TABLE_NAME, cv);
                //Log.d("mycoursereq", "insert");
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        //printCourse( course );
        return rowID;
    }

    /**
     * Make changes to currently stored course
     *
     * @param course course
     * @return number of courses changed
     */
    public int updateCourse(Course course) {
        //Log.d("mycoursereq", "updateCourse");
        int rowsAffected = 0;

        try {
            String whereClause = DatabaseHelper.CourseTable.USER_ID_KEY + "=? and " + DatabaseHelper.CourseTable.COURSE_ID_KEY + "=?";
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID), course.getCourseID()};
            ContentValues cv = getContentValuesFromCourse(course);
            rowsAffected = mDBHelper.update(DatabaseHelper.CourseTable.TABLE_NAME, cv, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * Deletes course from database
     *
     * @param userID   user id
     * @param courseID course id
     * @return number of courses deleted
     */
    public int deleteCourse(String userID, String courseID) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.USER_ID_KEY + "=? and " + DatabaseHelper.CourseTable.COURSE_ID_KEY + "=?";
            String[] whereArgs = new String[]{userID, courseID};
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Deletes course from database
     *
     * @param courseID course id
     * @return number of courses deleted
     */
    public int deleteCourse(String courseID) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.USER_ID_KEY + "=? and " + DatabaseHelper.CourseTable.COURSE_ID_KEY + "=?";
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Delete all courses from database
     *
     * @return number of deleted courses
     */
    public int deleteAllCourseForUser(String userId) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.USER_ID_KEY + "=?";
            String[] whereArgs = new String[]{ userId };
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Delete all courses from database
     *
     * @return number of deleted courses
     */
    public int deleteAllCourse() {
        int rowsAffected = 0;
        try {
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, null, null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Delete courses stored from long time ago
     *
     * @param timeStamp before when the course is stored
     * @return number of deleted courses
     */
    public int deleteOldCourse(Long timeStamp) {
        //Log.d("mycoursereq", "deleteOldCourse");
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldCourse timeStamp : " + timeStamp);
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.TIMESTAMP_KEY + "<?";
            String[] whereArgs = new String[]{String.valueOf(timeStamp)};
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, whereClause, whereArgs);
            //Log.d("mycoursereq", "rowsAffected => "+rowsAffected);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldCourse rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Delete and unsubscribe course
     *
     * @param courseIDs course id
     * @return number of courses
     */
    public int deleteUnsubscribedCourses(ArrayList<String> courseIDs) {
        //Log.d("mycoursereq","deleteUnsubscribedCourses");
        //Log.d("mycoursereq",courseIDs.toString());
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.USER_ID_KEY + "=? AND " + DatabaseHelper.CourseTable.COURSE_ID_KEY + " NOT IN " + getInString(courseIDs);
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID)};
            rowsAffected = mDBHelper.delete(DatabaseHelper.CourseTable.TABLE_NAME, whereClause, whereArgs);
            //Log.d("mycoursereq","rowsAffected =>" +rowsAffected);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteUnsubscribedCourses rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Properly formed list of course id to be used in database query
     *
     * @param courseIDs course ids
     * @return formed list of course ids
     */
    public String getInString(ArrayList<String> courseIDs) {
        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (String item : courseIDs) {
            if (first) {
                first = false;
                inQuery.append("'").append(item).append("'");
            } else {
                inQuery.append(", '").append(item).append("'");
            }
        }
        inQuery.append(")");

        return inQuery.toString();
    }

    /**
     * Generate course object from database
     *
     * @param c cursor
     * @return course object
     */
    private Course getCourseFromCursor(Cursor c) {
        Course Course = new Course();
        try {
            Course.setCourseID(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_ID_KEY)));
            Course.setCourseName(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_NAME_KEY)));
            Course.setCoursePicture(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_PICTURE_KEY)));
            Course.setUserPicture(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.USER_PICTURE_KEY)));
            Course.setCoursePrice(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_PRICE_KEY)));
            Course.setCourseOwnerName(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_OWNER_NAME_KEY)));
            Course.setCourseIsFree(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_IS_FREE_KEY)));
            Course.setCourseStatus(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_STATUS_KEY)));
            Course.setUserModRoleID(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.USER_MOD_ROLE_ID_KEY)));
            Course.setCourseUserPicture(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_USER_PICTURE_KEY)));
            Course.setCoursePlanExpired(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_PLAN_EXPIRED_KEY)));
            Course.setTotalPosts(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.STAT_TOTAL_POSTS_KEY)));
            Course.setTotalView(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.STAT_TOTAL_VIEW_KEY)));
            Course.setTotalUsers(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.TOTAL_USERS_KEY)));
            Course.setCourseCommmunity(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.COURSE_COMMUNITY_KEY)));
            Course.setUserSchoolName(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.USER_SCHOOL_NAME_KEY)));
            Course.setUnreadPost(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.UNREAD_POST_KEY)));
            Course.setAllowMute(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOW_MUTE_KEY)));
            Course.setAllowInviteUsers(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOW_INVITE_USERS_KEY)));
            Course.setAllowChangeSettings(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOW_CHANGE_SETTINGS_KEY)));
            Course.setAllowRateCourse(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOW_RATE_COURSE_KEY)));
            Course.setAllowedRoles(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOWED_ROLES_KEY)));
            Course.setAllowCourseInfo(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.ALLOWED_COURSE_INFO_KEY)));
            Course.setPublicType(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.PUBLIC_TYPE_KEY)));
            Course.setEventDateTime(c.getString(c.getColumnIndex(DatabaseHelper.CourseTable.EVENT_DATETIME_KEY)));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return Course;
    }

    /**
     * Gets content values from course object
     *
     * @param Course course object
     * @return content values
     */
    private ContentValues getContentValuesFromCourse(Course Course) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(DatabaseHelper.CourseTable.USER_ID_KEY, Config.getStringValue(Config.USER_ID));
            cv.put(DatabaseHelper.CourseTable.COURSE_ID_KEY, Course.getCourseID());
            cv.put(DatabaseHelper.CourseTable.COURSE_NAME_KEY, Course.getCourseName());
            cv.put(DatabaseHelper.CourseTable.COURSE_PICTURE_KEY, Course.getCoursePicture());
            cv.put(DatabaseHelper.CourseTable.USER_PICTURE_KEY, Course.getUserPicture());
            cv.put(DatabaseHelper.CourseTable.COURSE_PRICE_KEY, Course.getCoursePrice());
            cv.put(DatabaseHelper.CourseTable.COURSE_OWNER_NAME_KEY, Course.getCourseOwnerName());
            cv.put(DatabaseHelper.CourseTable.COURSE_IS_FREE_KEY, Course.getCourseIsFree());
            cv.put(DatabaseHelper.CourseTable.COURSE_STATUS_KEY, Course.getCourseStatus());
            cv.put(DatabaseHelper.CourseTable.USER_MOD_ROLE_ID_KEY, Course.getUserModRoleID());
            cv.put(DatabaseHelper.CourseTable.COURSE_USER_PICTURE_KEY, Course.getCourseUserPicture());
            cv.put(DatabaseHelper.CourseTable.COURSE_PLAN_EXPIRED_KEY, Course.getCoursePlanExpired());
            cv.put(DatabaseHelper.CourseTable.STAT_TOTAL_POSTS_KEY, Course.getTotalPosts());
            cv.put(DatabaseHelper.CourseTable.STAT_TOTAL_VIEW_KEY, Course.getTotalView());
            cv.put(DatabaseHelper.CourseTable.TOTAL_USERS_KEY, Course.getTotalUsers());
            cv.put(DatabaseHelper.CourseTable.COURSE_COMMUNITY_KEY, Course.getCourseCommmunity());
            cv.put(DatabaseHelper.CourseTable.USER_SCHOOL_NAME_KEY, Course.getUserSchoolName());
            cv.put(DatabaseHelper.CourseTable.UNREAD_POST_KEY, Course.getUnreadPost());
            cv.put(DatabaseHelper.CourseTable.ALLOW_MUTE_KEY, Course.getAllowMute());
            cv.put(DatabaseHelper.CourseTable.ALLOW_INVITE_USERS_KEY, Course.getAllowInviteUsers());
            cv.put(DatabaseHelper.CourseTable.ALLOW_CHANGE_SETTINGS_KEY, Course.getAllowChangeSettings());
            cv.put(DatabaseHelper.CourseTable.ALLOW_RATE_COURSE_KEY, Course.getAllowRateCourse());
            cv.put(DatabaseHelper.CourseTable.ALLOWED_ROLES_KEY, Course.getAllowedRoles());
            cv.put(DatabaseHelper.CourseTable.ALLOWED_COURSE_INFO_KEY, Course.getAllowCourseInfo());
            cv.put(DatabaseHelper.CourseTable.PUBLIC_TYPE_KEY, Course.getPublicType());
            cv.put(DatabaseHelper.CourseTable.EVENT_DATETIME_KEY, Course.getEventDateTime());
            cv.put(DatabaseHelper.CourseTable.TIMESTAMP_KEY, System.currentTimeMillis());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return cv;
    }


    public void printCourse(Course course) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write(course.toString());
    }


}

