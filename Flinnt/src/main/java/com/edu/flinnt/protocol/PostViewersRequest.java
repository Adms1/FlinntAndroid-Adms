package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 5/10/16.
 */
public class PostViewersRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String POST_ID_KEY = "post_id";
    public static final String CONTENT_ID_KEY = "content_id";
    public static final String OFFSET_KEY = "offset";
    public static final String MAX_KEY = "max";
    public static final String IS_LIKE_KEY = "is_like";
    public static final String IS_COMMENT_KEY = "is_comment";


    private String userID = "";
    private String courseID = "";
    private String postID = "";
    private String contentID = "";
    private String postOrContent = "";
    private int offSet = 0;
    private int maxFetch = 10;
    private int isLike = 0;
    private int isComment = 0;

    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COURSE_ID_KEY, courseID);

            returnedJObject.put(OFFSET_KEY, offSet);
            returnedJObject.put(MAX_KEY, maxFetch);
            if(postOrContent.equals("Content")){
                returnedJObject.put(CONTENT_ID_KEY, postID);
            }else {
                returnedJObject.put(POST_ID_KEY, postID);
            }
            if (isLike == Flinnt.TRUE) {
                returnedJObject.put(IS_LIKE_KEY, isLike);
            }
            if (isComment == Flinnt.TRUE) {
                returnedJObject.put(IS_COMMENT_KEY, isComment);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getMaxFetch() {
        return maxFetch;
    }

    public void setMaxFetch(int maxFetch) {
        this.maxFetch = maxFetch;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getIsComment() {
        return isComment;
    }

    public void setIsComment(int isComment) {
        this.isComment = isComment;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getPostOrContent() {
        return postOrContent;
    }

    public void setPostOrContent(String postOrContent) {
        this.postOrContent = postOrContent;
    }
}
