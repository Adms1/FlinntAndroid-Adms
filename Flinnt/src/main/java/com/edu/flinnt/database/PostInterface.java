package com.edu.flinnt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

/**
 * Manages offline Posts database operations
 */
public class PostInterface {

    private static PostInterface mPostInterface;

    DatabaseHelper mDBHelper;

    public static final int POST_TYPE_BLOG_QUIZ = 101;
    public static final int POST_TYPE_MESSAGE = 102;
    public static final int POST_TYPE_ALBUM = 103;

    public interface ValueType {
        int INTEGER = 1;
        int STRING = 2;
        int FLOAT = 3;
        int LONG = 4;
        int DOUBLE = 5;
    }


    public PostInterface() {
        if (null == mDBHelper) {
            mDBHelper = new DatabaseHelper();
        }
    }

    public static PostInterface getInstance() {
        if (null == mPostInterface) {
            mPostInterface = new PostInterface();
        }
        return mPostInterface;
    }

    /**
     * Gets all offline posts from course
     *
     * @param courseID course id
     * @return list of posts
     */
    public ArrayList<Post> getAllPosts(String courseID) {
        ArrayList<Post> PostList = new ArrayList<Post>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getAllPosts");
        String selection = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND " + DatabaseHelper.PostTable.COURSE_ID_KEY + "=?"/* + getPostTypeString(postType)*/;
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
        String orderBy = DatabaseHelper.PostTable.PUBLISH_DATE_KEY + " DESC";
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.PostTable.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy, null);
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    try {
                        Post Post = getPostFromCursor(cursor);
                        PostList.add(Post);
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

        return PostList;
    }

    /**
     * Gets all offline posts ids from course
     *
     * @param courseID course id
     * @return list of post ids
     */
    public ArrayList<String> getAllPostIDs(String courseID) {
        ArrayList<String> postIDList = new ArrayList<String>();
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getAllPostIDs");
        String[] columns = new String[]{DatabaseHelper.PostTable.POST_ID_KEY};
        String selection = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND " + DatabaseHelper.PostTable.COURSE_ID_KEY + "=?"/* + getPostTypeString(postType)*/;
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
        String orderBy = DatabaseHelper.PostTable.PUBLISH_DATE_KEY + " DESC";
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.PostTable.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy, null);
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    try {
                        String id = cursor.getString(0);
                        postIDList.add(id);
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

        return postIDList;
    }

    /**
     * Gets offline posts size in course
     *
     * @param courseID course id
     * @param postType post type
     * @return number of offline posts
     */
    public int getOfflinePostSize(String courseID, int postType) {
        int count = 0;
        String[] columns = new String[]{DatabaseHelper.PostTable.POST_ID_KEY};
        String selection = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND " + DatabaseHelper.PostTable.COURSE_ID_KEY + "=?"/* + getPostTypeString(postType)*/;
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.PostTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
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
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("getOfflinePostSize : " + count);
        return count;
    }

    /**
     * Update posts in database
     *
     * @param courseID course id
     * @param postType post type
     */
    public void updateDatabase(String courseID, int postType) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("updateDatabase");
        String[] columns = new String[]{DatabaseHelper.PostTable.COURSE_ID_KEY, DatabaseHelper.PostTable.POST_ID_KEY};
        String selection = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND " + DatabaseHelper.PostTable.COURSE_ID_KEY + "=?"/* + getPostTypeString(postType)*/;
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
        String orderBy = DatabaseHelper.PostTable.PUBLISH_DATE_KEY + " DESC";
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.PostTable.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy, null);
            if (null != cursor && cursor.moveToFirst()) {
                int count = cursor.getCount();
                if (count > Flinnt.MAX_OFFLINE_COURSE_SIZE) {
                    if (cursor.moveToPosition(Flinnt.MAX_OFFLINE_COURSE_SIZE)) {
                        do {
                            try {
                                String coourseID = cursor.getString(0);
                                String postID = cursor.getString(1);
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("updateDatabase delete postID : " + postID + ", coourseID : " + coourseID);
                                deletePost(coourseID, postID);
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
     * Checks for the existence of a post
     *
     * @param courseID course id
     * @param postID   post id
     * @return true if exists, false otherwise
     */
    public boolean isPostExist(String courseID, String postID) {
        boolean ret = false;
        //String[] columns = new String[] { DatabaseHelper.PostTable.COURSE_ID_KEY, DatabaseHelper.PostTable.POST_ID_KEY };
        String selection = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND "
                + DatabaseHelper.PostTable.COURSE_ID_KEY + "=? AND "
                + DatabaseHelper.PostTable.POST_ID_KEY + "=?";
        String[] selectionArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID, postID};
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(DatabaseHelper.PostTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
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
            LogWriter.write("isPostExist : " + ret + ", courseID : " + courseID + ", postID : " + postID);
        return ret;
    }

    /**
     * Add or edit a post in database
     *
     * @param courseID course id
     * @param post     post object
     * @return number of affected datatable rows
     */
    public long insertOrUpdatePost(String courseID, Post post) {
        long rowID = Flinnt.INVALID;

        try {
            if (isPostExist(courseID, post.getPostID())) {
                rowID = updatePost(courseID, post);
            } else {
                ContentValues cv = getContentValuesFromPost(courseID, post);
                rowID = mDBHelper.insert(DatabaseHelper.PostTable.TABLE_NAME, cv);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        //printCourse( course );
        return rowID;
    }

    /**
     * edit a post on database
     *
     * @param courseID course id in which post lies
     * @param post     post object
     * @return number of edited posts
     */
    public int updatePost(String courseID, Post post) {
        int rowsAffected = 0;

        try {
            String whereClause = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND "
                    + DatabaseHelper.PostTable.COURSE_ID_KEY + "=? AND "
                    + DatabaseHelper.PostTable.POST_ID_KEY + "=?";
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID, post.getPostID()};
            ContentValues cv = getContentValuesFromPost(courseID, post);
            rowsAffected = mDBHelper.update(DatabaseHelper.PostTable.TABLE_NAME, cv, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("updatePost rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

/*	
    public int updatePost( String courseID, Post post, String column, String value ) {
		int rowsAffected = 0;
		try {
			String whereClause = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND "
					+ DatabaseHelper.PostTable.COURSE_ID_KEY + "=? AND " 
					+ DatabaseHelper.PostTable.POST_ID_KEY + "=?";
			String[] whereArgs = new String[] { Config.getStringValue(Config.USER_ID), courseID, post.getPostID() };
			ContentValues cv = new ContentValues();
			cv.put( column, value );
			rowsAffected = mDBHelper.update( DatabaseHelper.PostTable.TABLE_NAME, cv, whereClause, whereArgs );
		}
		catch( Exception e ) {
			LogWriter.err(e);
		}
		return rowsAffected;
	}
*/

    /**
     * deletes all posts from database
     *
     * @return number of deleted posts
     */
    public int deleteAllPostForUser(String userId) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.PostTable.USER_ID_KEY + "=?";
            String[] whereArgs = new String[]{userId};
            rowsAffected = mDBHelper.delete(DatabaseHelper.PostTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * deletes all posts from database
     *
     * @return number of deleted posts
     */
    public int deleteAllPost() {
        int rowsAffected = 0;
        try {
            rowsAffected = mDBHelper.delete(DatabaseHelper.PostTable.TABLE_NAME, null, null);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * deletes a post from database
     *
     * @param courseID course id in which post exists
     * @param postID   post id
     * @return number of deleted posts
     */
    public int deletePost(String courseID, String postID) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND "
                    + DatabaseHelper.PostTable.COURSE_ID_KEY + "=? AND "
                    + DatabaseHelper.PostTable.POST_ID_KEY + "=?";
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID, postID};
            rowsAffected = mDBHelper.delete(DatabaseHelper.PostTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return rowsAffected;
    }

    /**
     * deletes a post from database which is removed from server
     *
     * @param courseID course id in which post exits
     * @param postIDs  list of post ids
     * @param postType type of post
     * @return number of deleted posts
     */
    public int deleteRemovedPost(String courseID, ArrayList<String> postIDs, int postType) {
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.PostTable.USER_ID_KEY + "=? AND "
                    + DatabaseHelper.PostTable.COURSE_ID_KEY + "=?"
                    + getPostTypeString(postType) + " AND "
                    + DatabaseHelper.PostTable.POST_ID_KEY + " NOT IN " + getInString(postIDs);
            String[] whereArgs = new String[]{Config.getStringValue(Config.USER_ID), courseID};
            rowsAffected = mDBHelper.delete(DatabaseHelper.PostTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteRemovedPost rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Deletes posts stored before long time
     *
     * @param timeStamp time stamp before what the post is created
     * @return number of posts deleted
     */
    public int deleteOldPost(Long timeStamp) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldPost timeStamp : " + timeStamp);
        int rowsAffected = 0;
        try {
            String whereClause = DatabaseHelper.CourseTable.TIMESTAMP_KEY + "<?";
            String[] whereArgs = new String[]{String.valueOf(timeStamp)};
            rowsAffected = mDBHelper.delete(DatabaseHelper.PostTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("deleteOldPost rowsAffected : " + rowsAffected);
        return rowsAffected;
    }

    /**
     * create post ids list to a suitable form to be passed in query
     *
     * @param postIDs list of course ids
     * @return query formed list of posts
     */
    public String getInString(ArrayList<String> postIDs) {
        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (String item : postIDs) {
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
     * Gets post object from database cursor
     *
     * @param c cursor
     * @return post object
     */
    private Post getPostFromCursor(Cursor c) {
        Post post = new Post();
        try {
            post.setPostID(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POST_ID_KEY)));
            post.setCourseID(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.COURSE_ID_KEY)));
            post.setTitle(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.TITLE_KEY)));
            post.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.DESCRIPTION_KEY)));
            post.setPublishDate(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.PUBLISH_DATE_KEY)));
            post.setPostType(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POST_TYPE_KEY)));
            post.setPostContentType(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POST_CONTENT_TYPE_KEY)));
            post.setTotalLikes(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.TOTAL_LIKES_KEY)));
            post.setTotalComments(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.TOTAL_COMMENTS_KEY)));
            post.setIsRead(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.IS_READ_KEY)));
            post.setIsBookmark(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.IS_BOOKMARK_KEY)));
            post.setCanDeletePost(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.CAN_DELETE_POST_KEY)));
            post.setTotalViews(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.TOTAL_VIEWS_KEY)));
            post.setInserted(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.INSERTED_KEY)));
            post.setMessageToUsers(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.MESSAGE_TO_USERS_KEY)));
            post.setAlbumImages(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.ALBUM_IMAGES_KEY)));


            post.setAttachments(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POST_ATTACHMENT_KEY)));
            post.setVideoPreview(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.VIDEO_PREVIEW_KEY)));
            post.setAttachmentsIsUrl(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POST_ATTACHMENT_URL_KEY)));
            post.setCanEdit(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.CAN_EDIT_KEY)));
            post.setAllowRepost(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.ALLOW_REPOST_KEY)));
            post.setAuthor(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.AUTHOR_KEY)));
            post.setAuthorPicture(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.AUTHOR_PICTURE_KEY)));
            post.setAuthorPictureUrl(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.AUTHOR_PICTURE_URL_KEY)));
            post.setAttachmentUrl(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.ATTACHMENT_URL_KEY)));
            post.setVideoThumbUrl(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.VIDEO_THUMB_URL_KEY)));
            post.setIsLike(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.IS_LIKE_KEY)));
            post.setIsComment(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.IS_COMMENT_KEY)));
            post.setPollResultHours(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POLL_RESULT_HOURS_KEY)));
            post.setPollListResponse(c.getString(c.getColumnIndex(DatabaseHelper.PostTable.POLL_OPTIONS_KEY)));


        } catch (Exception e) {
            LogWriter.err(e);
        }
        return post;
    }

    /**
     * Gets content values from post object
     *
     * @param coursID course id
     * @param post    post object
     * @return content values
     */
    private ContentValues getContentValuesFromPost(String coursID, Post post) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(DatabaseHelper.PostTable.USER_ID_KEY, Config.getStringValue(Config.USER_ID));
            cv.put(DatabaseHelper.PostTable.POST_ID_KEY, post.getPostID());
            cv.put(DatabaseHelper.PostTable.COURSE_ID_KEY, coursID);
            cv.put(DatabaseHelper.PostTable.TITLE_KEY, post.getTitle());
            cv.put(DatabaseHelper.PostTable.DESCRIPTION_KEY, post.getDescription());
            cv.put(DatabaseHelper.PostTable.PUBLISH_DATE_KEY, post.getPublishDate());
            cv.put(DatabaseHelper.PostTable.POST_TYPE_KEY, post.getPostType());
            cv.put(DatabaseHelper.PostTable.POST_CONTENT_TYPE_KEY, post.getPostContentType());
            cv.put(DatabaseHelper.PostTable.TOTAL_LIKES_KEY, post.getTotalLikes());
            cv.put(DatabaseHelper.PostTable.TOTAL_COMMENTS_KEY, post.getTotalComments());
            cv.put(DatabaseHelper.PostTable.IS_READ_KEY, post.getIsRead());
            cv.put(DatabaseHelper.PostTable.IS_BOOKMARK_KEY, post.getIsBookmark());
            cv.put(DatabaseHelper.PostTable.CAN_DELETE_POST_KEY, post.getCanDeletePost());
            cv.put(DatabaseHelper.PostTable.TOTAL_VIEWS_KEY, post.getTotalViews());
            cv.put(DatabaseHelper.PostTable.INSERTED_KEY, post.getInserted());
            cv.put(DatabaseHelper.PostTable.MESSAGE_TO_USERS_KEY, post.getMessageToUsers());
            cv.put(DatabaseHelper.PostTable.ALBUM_IMAGES_KEY, post.getAlbumImages());
            cv.put(DatabaseHelper.PostTable.TIMESTAMP_KEY, System.currentTimeMillis());

            cv.put(DatabaseHelper.PostTable.POST_ATTACHMENT_KEY, post.getAttachments());
            cv.put(DatabaseHelper.PostTable.VIDEO_PREVIEW_KEY, post.getVideoPreview());
            cv.put(DatabaseHelper.PostTable.POST_ATTACHMENT_URL_KEY, post.getAttachmentsIsUrl());
            cv.put(DatabaseHelper.PostTable.CAN_EDIT_KEY, post.getCanEdit());
            cv.put(DatabaseHelper.PostTable.ALLOW_REPOST_KEY, post.getAllowRepost());
            cv.put(DatabaseHelper.PostTable.AUTHOR_KEY, post.getAuthor());
            cv.put(DatabaseHelper.PostTable.AUTHOR_PICTURE_KEY, post.getAuthorPicture());
            cv.put(DatabaseHelper.PostTable.AUTHOR_PICTURE_URL_KEY, post.getAuthorPictureUrl());
            cv.put(DatabaseHelper.PostTable.ATTACHMENT_URL_KEY, post.getAttachmentUrl());
            cv.put(DatabaseHelper.PostTable.VIDEO_THUMB_URL_KEY, post.getVideoThumbUrl());
            cv.put(DatabaseHelper.PostTable.IS_LIKE_KEY, post.getIsLike());
            cv.put(DatabaseHelper.PostTable.IS_COMMENT_KEY, post.getIsComment());
            cv.put(DatabaseHelper.PostTable.POLL_RESULT_HOURS_KEY, post.getPollResultHours());
            cv.put(DatabaseHelper.PostTable.POLL_OPTIONS_KEY, post.getPollListResponse());

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return cv;
    }


    public void printPost(Post post) {
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write(post.toString());
    }

    /**
     * Formed string according to post type to be queried
     *
     * @param postType post type
     * @return query formed string
     */
    public String getPostTypeString(int postType) {
        String retString = "";
        switch (postType) {
            case POST_TYPE_BLOG_QUIZ:
                retString = " AND (" + DatabaseHelper.PostTable.POST_TYPE_KEY + "=" + Flinnt.POST_TYPE_BLOG + ") ";
                break;
            case POST_TYPE_MESSAGE:
                retString = " AND " + DatabaseHelper.PostTable.POST_TYPE_KEY + "=" + Flinnt.POST_TYPE_MESSAGE;
                break;
            case POST_TYPE_ALBUM:
                retString = " AND " + DatabaseHelper.PostTable.POST_TYPE_KEY + "=" + Flinnt.POST_TYPE_ALBUM;
                break;
            default:
                break;
        }
        return retString;
    }

}

