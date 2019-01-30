package com.edu.flinnt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.util.LogWriter;

/**
 * Helper class to create, manage and structure database
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Flinnt.db";
    private static final int DATABASE_VERSION = 3;


    public interface UserTable {
        public static final String TABLE_NAME = "USER";
        public static final String AUTO_ID_KEY = "auto_id";
        public static final String USER_ID_KEY = "user_id";
        public static final String USER_LOGIN_KEY = "user_login";
        public static final String FIRST_NAME_KEY = "firstname";
        public static final String LAST_NAME_KEY = "lastname";
        public static final String USER_IS_ACTIVE_KEY = "user_is_active";
        public static final String USER_PICTURE_KEY = "user_picture";
        //public static final String IMAGE_PATH_KEY = "image_path";
        public static final String USER_ACC_VERIFIED_KEY = "user_acc_verified";
        public static final String USER_ACC_AUTH_MODE_KEY = "user_acc_auth_mode";
        public static final String CAN_ADD_KEY = "can_add";
        public static final String USER_PICTURE_URL_KEY = "user_picture_url";
        public static final String CAN_BROWSE_COURSE = "can_browse_course";
        public static final String TOKEN_SENT_TO_SERVER = "TokenSentToServer";
        public static final String IS_CURRENT_USER = "is_current_user";
    }

    public interface CourseTable {
        public static final String TABLE_NAME = "COURSE";
        public static final String AUTO_ID_KEY = "auto_id";
        public static final String USER_ID_KEY = "user_id";
        public static final String COURSE_ID_KEY = "course_id";
        public static final String COURSE_NAME_KEY = "course_name";
        public static final String COURSE_PICTURE_KEY = "course_picture";
        public static final String USER_PICTURE_KEY = "user_picture";
        public static final String COURSE_PRICE_KEY = "course_price";
        public static final String COURSE_OWNER_NAME_KEY = "course_owner_name";
        public static final String COURSE_IS_FREE_KEY = "course_is_free";
        public static final String COURSE_STATUS_KEY = "course_status";
        public static final String USER_MOD_ROLE_ID_KEY = "user_mod_role_id";
        public static final String COURSE_USER_PICTURE_KEY = "course_user_picture";
        public static final String COURSE_PLAN_EXPIRED_KEY = "course_plan_expired";
        public static final String STAT_TOTAL_POSTS_KEY = "stat_total_posts";
        public static final String STAT_TOTAL_VIEW_KEY = "stat_total_view";
        public static final String TOTAL_USERS_KEY = "total_users";
        public static final String COURSE_COMMUNITY_KEY = "course_community";
        public static final String USER_SCHOOL_NAME_KEY = "user_school_name";
        public static final String UNREAD_POST_KEY = "unread_post";
        public static final String ALLOW_MUTE_KEY = "allow_mute";
        public static final String ALLOW_INVITE_USERS_KEY = "allow_invite_users";
        public static final String ALLOW_CHANGE_SETTINGS_KEY = "allow_change_settings";
        public static final String ALLOW_RATE_COURSE_KEY = "allow_rate_course";
        public static final String ALLOWED_ROLES_KEY = "allowed_roles";
        public static final String ALLOWED_COURSE_INFO_KEY = "allow_course_info";
        public static final String PUBLIC_TYPE_KEY = "public_type";
        public static final String EVENT_DATETIME_KEY = "event_datetime";
        public static final String TIMESTAMP_KEY = "timestamp";
        /*
		public static final String COURSE_PICTURE_URL_KEY 						= "course_picture_url";
		public static final String USER_PICTURE_URL_KEY 						= "user_picture_url";
		public static final String COURSE_USER_PICTURE_URL_KEY 					= "course_user_picture_url";
		*/
    }

    public interface PostTable {
        public static final String TABLE_NAME = "POST";
        public static final String AUTO_ID_KEY = "auto_id";
        public static final String USER_ID_KEY = "user_id";
        public static final String POST_ID_KEY = "post_id";
        public static final String COURSE_ID_KEY = "course_id";
        public static final String TITLE_KEY = "title";
        public static final String DESCRIPTION_KEY = "description";
        public static final String PUBLISH_DATE_KEY = "publish_date";
        public static final String POST_TYPE_KEY = "post_type";
        public static final String POST_CONTENT_TYPE_KEY = "post_content_type";
        public static final String TOTAL_LIKES_KEY = "total_likes";
        public static final String TOTAL_COMMENTS_KEY = "total_comments";
        public static final String IS_READ_KEY = "is_read";
        public static final String IS_BOOKMARK_KEY = "is_bookmark";
        public static final String CAN_DELETE_POST_KEY = "can_delete_post";
        public static final String TOTAL_VIEWS_KEY = "total_views";
        public static final String INSERTED_KEY = "inserted";
        public static final String MESSAGE_TO_USERS_KEY = "message_to_users";
        public static final String ALBUM_IMAGES_KEY = "album_images";
        public static final String TIMESTAMP_KEY = "timestamp";


        public static final String POST_ATTACHMENT_KEY = "attachments";
        public static final String VIDEO_PREVIEW_KEY = "video_preview";
        public static final String POST_ATTACHMENT_URL_KEY = "attachment_is_url";
        public static final String CAN_EDIT_KEY = "can_edit";
        public static final String ALLOW_REPOST_KEY = "allow_repost";
        public static final String AUTHOR_KEY = "author";
        public static final String AUTHOR_PICTURE_KEY = "author_picture";
        public static final String AUTHOR_PICTURE_URL_KEY = "author_picture_url";
        public static final String ATTACHMENT_URL_KEY = "attachment_url";
        public static final String VIDEO_THUMB_URL_KEY = "video_thumb_url";
        public static final String IS_LIKE_KEY = "is_like";
        public static final String IS_COMMENT_KEY = "is_comment";
        public static final String POLL_RESULT_HOURS_KEY = "poll_result_hours";
        public static final String POLL_OPTIONS_KEY = "poll_options";
    }

    public interface NotificationTable {
        public static final String TABLE_NAME = "NOTIFICATION";
        public static final String AUTO_ID_KEY = "auto_id";
        public static final String USER_ID_KEY = "user_id";
        public static final String NOTIFICATION_PAYLOAD_KEY = "notification_payload";
        public static final String NOTIFICATION_TIMESTAMP = "notification_timestamp";
        public static final String NOTIFICATION_TYPE_KEY = "notification_type";
        public static final String NOTIFICATION_READ_STATUS_KEY = "notification_read_status";
    }


    // Table Create Statements
    private final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE_NAME + "("
            + UserTable.AUTO_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT   , "
            + UserTable.USER_ID_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.USER_LOGIN_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.FIRST_NAME_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.LAST_NAME_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.USER_IS_ACTIVE_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.USER_PICTURE_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.USER_ACC_VERIFIED_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.USER_ACC_AUTH_MODE_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.CAN_ADD_KEY + " INTEGER DEFAULT                 '0' , "
            + UserTable.USER_PICTURE_URL_KEY + " TEXT DEFAULT                    ''  , "
            + UserTable.CAN_BROWSE_COURSE + " INTEGER DEFAULT                 '0' , "
            + UserTable.TOKEN_SENT_TO_SERVER + " INTEGER DEFAULT                 '0'  , "
            + UserTable.IS_CURRENT_USER + " INTEGER DEFAULT                 '0'   "
            + ")";

    // Table Create Statements
    private final String CREATE_TABLE_COURSE = "CREATE TABLE IF NOT EXISTS " + CourseTable.TABLE_NAME + "("
            + CourseTable.AUTO_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CourseTable.USER_ID_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_ID_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_NAME_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_PICTURE_KEY + " TEXT DEFAULT '', "
            + CourseTable.USER_PICTURE_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_PRICE_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_OWNER_NAME_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_IS_FREE_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_STATUS_KEY + " TEXT DEFAULT '', "
            + CourseTable.USER_MOD_ROLE_ID_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_USER_PICTURE_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_PLAN_EXPIRED_KEY + " TEXT DEFAULT '', "
            + CourseTable.STAT_TOTAL_POSTS_KEY + " TEXT DEFAULT '', "
            + CourseTable.STAT_TOTAL_VIEW_KEY + " TEXT DEFAULT '', "
            + CourseTable.TOTAL_USERS_KEY + " TEXT DEFAULT '', "
            + CourseTable.COURSE_COMMUNITY_KEY + " TEXT DEFAULT '', "
            + CourseTable.USER_SCHOOL_NAME_KEY + " TEXT DEFAULT '', "
            + CourseTable.UNREAD_POST_KEY + " TEXT DEFAULT '0', "
            + CourseTable.ALLOW_MUTE_KEY + " TEXT DEFAULT '', "
            + CourseTable.ALLOW_INVITE_USERS_KEY + " TEXT DEFAULT '', "
            + CourseTable.ALLOW_CHANGE_SETTINGS_KEY + " TEXT DEFAULT '', "
            + CourseTable.ALLOW_RATE_COURSE_KEY + " TEXT DEFAULT '', "
            + CourseTable.ALLOWED_ROLES_KEY + " TEXT DEFAULT '', "
            + CourseTable.ALLOWED_COURSE_INFO_KEY + " TEXT DEFAULT '', "
            + CourseTable.PUBLIC_TYPE_KEY + " TEXT DEFAULT '', "
            + CourseTable.EVENT_DATETIME_KEY + " TEXT DEFAULT '', "
            + CourseTable.TIMESTAMP_KEY + " INTEGER DEFAULT '0'"
        		/*        		
	 			+ CourseTable.COURSE_PICTURE_URL_KEY + " TEXT DEFAULT '', "
        		+ CourseTable.USER_PICTURE_URL_KEY + " TEXT DEFAULT '', "
        		+ CourseTable.COURSE_USER_PICTURE_URL_KEY + " TEXT DEFAULT '' "
        		*/
            + ")";


    private final String CREATE_TABLE_POST = "CREATE TABLE IF NOT EXISTS " + PostTable.TABLE_NAME + "("
            + PostTable.AUTO_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PostTable.USER_ID_KEY + " TEXT DEFAULT '', "
            + PostTable.POST_ID_KEY + " TEXT DEFAULT '', "
            + PostTable.COURSE_ID_KEY + " TEXT DEFAULT '', "
            + PostTable.TITLE_KEY + " TEXT DEFAULT '', "
            + PostTable.DESCRIPTION_KEY + " TEXT DEFAULT '', "
            + PostTable.PUBLISH_DATE_KEY + " TEXT DEFAULT '', "
            + PostTable.POST_TYPE_KEY + " TEXT DEFAULT '', "
            + PostTable.POST_CONTENT_TYPE_KEY + " TEXT DEFAULT '', "
            + PostTable.TOTAL_LIKES_KEY + " TEXT DEFAULT '', "
            + PostTable.TOTAL_COMMENTS_KEY + " TEXT DEFAULT '', "
            + PostTable.IS_READ_KEY + " TEXT DEFAULT '', "
            + PostTable.IS_BOOKMARK_KEY + " TEXT DEFAULT '', "
            + PostTable.CAN_DELETE_POST_KEY + " TEXT DEFAULT '', "
            + PostTable.TOTAL_VIEWS_KEY + " TEXT DEFAULT '', "
            + PostTable.INSERTED_KEY + " TEXT DEFAULT '', "
            + PostTable.MESSAGE_TO_USERS_KEY + " TEXT DEFAULT '', "
            + PostTable.ALBUM_IMAGES_KEY + " TEXT DEFAULT '', "
            + PostTable.TIMESTAMP_KEY + " INTEGER DEFAULT '0', "

            + PostTable.POST_ATTACHMENT_KEY + " TEXT DEFAULT '', "
            + PostTable.VIDEO_PREVIEW_KEY + " TEXT DEFAULT '', "
            + PostTable.POST_ATTACHMENT_URL_KEY + " TEXT DEFAULT '', "
            + PostTable.CAN_EDIT_KEY + " TEXT DEFAULT '', "
            + PostTable.ALLOW_REPOST_KEY + " TEXT DEFAULT '', "
            + PostTable.AUTHOR_KEY + " TEXT DEFAULT '', "
            + PostTable.AUTHOR_PICTURE_KEY + " TEXT DEFAULT '', "
            + PostTable.AUTHOR_PICTURE_URL_KEY + " TEXT DEFAULT '', "
            + PostTable.ATTACHMENT_URL_KEY + " TEXT DEFAULT '', "
            + PostTable.VIDEO_THUMB_URL_KEY + " TEXT DEFAULT '', "
            + PostTable.IS_LIKE_KEY + " TEXT DEFAULT '', "
            + PostTable.IS_COMMENT_KEY + " TEXT DEFAULT '', "
            + PostTable.POLL_RESULT_HOURS_KEY + " TEXT DEFAULT '', "
            + PostTable.POLL_OPTIONS_KEY + " TEXT DEFAULT '', "
            + "FOREIGN KEY(" + PostTable.POST_ID_KEY + ") REFERENCES "
            + CourseTable.TABLE_NAME + "(" + CourseTable.COURSE_ID_KEY + ") ON DELETE CASCADE"
            + ")";

// Table Notification
    private final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + NotificationTable.TABLE_NAME + "("
            + NotificationTable.AUTO_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NotificationTable.USER_ID_KEY + " TEXT DEFAULT '', "
            + NotificationTable.NOTIFICATION_PAYLOAD_KEY + " TEXT DEFAULT '', "
            + NotificationTable.NOTIFICATION_TIMESTAMP + " TEXT DEFAULT '', "
        + NotificationTable.NOTIFICATION_TYPE_KEY + " TEXT DEFAULT '', "
            + NotificationTable.NOTIFICATION_READ_STATUS_KEY + " TEXT DEFAULT '')";


    public DatabaseHelper() {
        super(FlinntApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Creating Database");

        db.execSQL(CREATE_TABLE_COURSE);
        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // create tables if not exists
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onUpgrade oldVersion : " + oldVersion + ", newVersion : " + newVersion);

        if (newVersion > oldVersion) {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_NOTIFICATION);
        }
    }
	
	/*
	private void addColumnInTable( SQLiteDatabase db, String tableName, String columnName, String columntype ) {
		String alterQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columntype;
		try {
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Add Column In Table :: " + alterQuery );
			db.execSQL( alterQuery );
		}
		catch( Exception e ) {
			LogWriter.err(e);
		}
	}
	*/

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return c;
    }


    public long insert(String table, ContentValues values) {
        long rowID = Flinnt.INVALID;
        SQLiteDatabase db = this.getWritableDatabase();
        rowID = db.insert(table, null, values);
        return rowID;
    }


    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int rowsAffected = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowsAffected = db.update(table, values, whereClause, whereArgs);
        return rowsAffected;
    }


    public int delete(String table, String whereClause, String[] whereArgs) {
        int rowsAffected = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowsAffected = db.delete(table, whereClause, whereArgs);
        return rowsAffected;
    }
}


