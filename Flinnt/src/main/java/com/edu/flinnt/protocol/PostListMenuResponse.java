package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class PostListMenuResponse extends BaseResponse {

    public static final String CAN_FILTER_KEY = "can_filter";
    public static final String CAN_INVITE_KEY = "can_invite";
    public static final String CAN_SEE_USER_LIST_KEY = "can_see_user_list";
    public static final String CAN_EDIT_COURSE_KEY = "can_edit_course";
    public static final String CAN_MUTE_KEY = "can_mute";
    public static final String CAN_CHANGE_SETTINGS_KEY = "can_change_settings";
    public static final String CAN_RATE_COURSE_KEY = "can_rate_course";
    public static final String CAN_UNSUBSCRIBE_KEY = "can_unsubscribe";
    public static final String CAN_VIEW_COURSE_INFO_KEY = "can_view_course_info";

    public static final String CAN_COMMENT_KEY = "can_comment";
    public static final String SHOW_POSTED_COMMENT_KEY = "show_posted_comment";


    private int canFilter = Flinnt.INVALID;
    private int canInvite = Flinnt.INVALID;
    private int canSeeUserList = Flinnt.INVALID;
    private int canEditCourse = Flinnt.INVALID;
    private int canMute = Flinnt.INVALID;

    private int canRateCourse = Flinnt.INVALID;
    private int canUnsubscribe = Flinnt.INVALID;
    private int canViewCourseInfo = Flinnt.INVALID;
    private int canChangeSettings = Flinnt.INVALID;

    private int canComment = Flinnt.INVALID;
    private int showPostedComment = Flinnt.INVALID;


    /**
     * Converts json string to json object
     *
     * @param jsonData json string
     */
    synchronized public void parseJSONString(String jsonData) {

        if (TextUtils.isEmpty(jsonData)) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            parseJSONObject(jsonObject);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * parse json object to suitable data types
     *
     * @param jsonData json object
     */
    synchronized public void parseJSONObject(JSONObject jsonData) {

        try {
            setCanFilter(jsonData.getInt(CAN_FILTER_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanInvite(jsonData.getInt(CAN_INVITE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanSeeUserList(jsonData.getInt(CAN_SEE_USER_LIST_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanEditCourse(jsonData.getInt(CAN_EDIT_COURSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanMute(jsonData.getInt(CAN_MUTE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanRateCourse(jsonData.getInt(CAN_RATE_COURSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanUnsubscribe(jsonData.getInt(CAN_UNSUBSCRIBE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            setCanViewCourseInfo(jsonData.getInt(CAN_VIEW_COURSE_INFO_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanChangeSettings(jsonData.getInt(CAN_CHANGE_SETTINGS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setCanComment(jsonData.getInt(CAN_COMMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            setShowPostedComment(jsonData.getInt(SHOW_POSTED_COMMENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }


    public int getCanFilter() {
        return canFilter;
    }

    public void setCanFilter(int canFilter) {
        this.canFilter = canFilter;
    }

    public int getCanInvite() {
        return canInvite;
    }

    public void setCanInvite(int canInvite) {
        this.canInvite = canInvite;
    }

    public int getCanSeeUserList() {
        return canSeeUserList;
    }

    public void setCanSeeUserList(int canSeeUserList) {
        this.canSeeUserList = canSeeUserList;
    }

    public int getCanEditCourse() {
        return canEditCourse;
    }

    public void setCanEditCourse(int canEditCourse) {
        this.canEditCourse = canEditCourse;
    }

    public int getCanMute() {
        return canMute;
    }

    public void setCanMute(int canMute) {
        this.canMute = canMute;
    }

    public int getCanRateCourse() {
        return canRateCourse;
    }

    public void setCanRateCourse(int canRateCourse) {
        this.canRateCourse = canRateCourse;
    }

    public int getCanUnsubscribe() {
        return canUnsubscribe;
    }

    public void setCanUnsubscribe(int canUnsubscribe) {
        this.canUnsubscribe = canUnsubscribe;
    }

    public int getCanViewCourseInfo() {
        return canViewCourseInfo;
    }

    public void setCanViewCourseInfo(int canViewCourseInfo) {
        this.canViewCourseInfo = canViewCourseInfo;
    }

    public int getCanChangeSettings() {
        return canChangeSettings;
    }

    public void setCanChangeSettings(int canChangeSettings) {
        this.canChangeSettings = canChangeSettings;
    }

    public static String getCanFilterKey() {
        return CAN_FILTER_KEY;
    }

    public int getCanComment() {
        return canComment;
    }

    public void setCanComment(int canComment) {
        this.canComment = canComment;
    }

    public int getShowPostedComment() {
        return showPostedComment;
    }

    public void setShowPostedComment(int showPostedComment) {
        this.showPostedComment = showPostedComment;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("canFilter : " + canFilter)
                .append(", canInvite : " + canInvite)
                .append(", canSeeUserList : " + canSeeUserList)
                .append(", canEdit : " + canEditCourse)
                .append(", canMute : " + canMute)
                .append(", canChangeSettings : " + canChangeSettings)
                .append(", canComment : " + canComment)
                .append(", showPostedComment : " + showPostedComment);

        return strBuffer.toString();
    }
}
