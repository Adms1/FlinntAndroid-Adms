package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-3 on 8/2/17.
 */
public class QuizResultSummaryRequest {
    private final String USER_ID_KEY = "user_id";
    private final String COURSE_ID_KEY = "course_id";
    private final String QUIZ_ID_KEY = "quiz_id";
    private final String CONTENT_ID_KEY = "content_id";
    private String userID = "";
    private String courseId = "";
    private String quizId = "";
    private String contentId = "";
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
            returnedJObject.put(COURSE_ID_KEY, courseId);
            returnedJObject.put(QUIZ_ID_KEY, quizId);
            returnedJObject.put(CONTENT_ID_KEY, contentId);
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;

    }
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }



}
