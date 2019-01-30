package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.database.CourseInterface;
import com.edu.flinnt.protocol.MyCoursesRequest;
import com.edu.flinnt.protocol.MyCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Send request and get response and pass it to GUI
 */
public class MyCourses {

    public static String TAG = MyCourses.class.getSimpleName();
    public MyCoursesResponse mMyCoursesResponse = null;
    public MyCoursesRequest mMyCoursesRequest = null;
    public Handler mHandler = null;
    public int type;
    String searchString = "";
    //private static String TAG = "MyCoursess";

    public static final int TYPE_MY_COURSE = 67;
    public static final int TYPE_BROWSE_COURSE = 68;


    // For offline support
    private boolean isUpdateDB = false;
    int mOfflineCourseSize = CourseInterface.getInstance().getOfflineCourseSize();

    ArrayList<String> mOfflineCourseIDs = new ArrayList<String>();

    int offset = 0;
    int max_count = 1000;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public MyCourses(Handler handler, int fromWhere) {
        mHandler = handler;
        type = fromWhere;
        getLastMyCoursesResponse();
    }

    public MyCoursesResponse getLastMyCoursesResponse() {
        if (mMyCoursesResponse == null) {
            mMyCoursesResponse = new MyCoursesResponse();
            //mMyCoursesResponse.parseJSONString( Config.getStringValue(Config.LAST_MY_COURSES_RESPONSE) );
        }
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("response : " + mMyCoursesResponse.toString() );
        return mMyCoursesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        if (type == TYPE_MY_COURSE) {
            return Flinnt.API_URL + Flinnt.URL_MY_COURSES;
        } else {
            return Flinnt.API_URL + Flinnt.URL_COURSE_COMMUNITY_LIST;
        }
    }

    public void sendMyCoursesRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendRequest();
                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }

    /**
     * sends request along with parameters
     */
    public void sendRequest() {

        synchronized (MyCourses.class) {
            try {
                String url = buildURLString();

                if (isUpdateDB) {
                    //if( null == mMyCoursesRequest ) {
                    mMyCoursesRequest = new MyCoursesRequest();
                    mMyCoursesRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mMyCoursesRequest.setUpdateOffline(Flinnt.TRUE);
                    mMyCoursesRequest.setOfflineCourses(CourseInterface.getInstance().getAllCourseIDs());
                    mMyCoursesResponse.clearAllCourseList();
                    //}
                } else {
                    if (null == mMyCoursesRequest) {
                        mMyCoursesRequest = getMyCoursesRequest();
                    } else {
                        // Reset offset to new request - New offset = old offset + max
                        offset = offset + 10;
                        mMyCoursesRequest.setOffset(offset);
                        mMyCoursesRequest.setMax(max_count);
                    }
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("MyCourses Request :\nUrl : " + url + "\nData : " + mMyCoursesRequest.getJSONString() + " , " + isUpdateDB);

                JSONObject jsonObject = mMyCoursesRequest.getJSONObject();

                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }

    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {
        //Log.d(TAG, "req sent" + jsonObject.toString());

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Log.d(TAG, "onResponse().. ");

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("MyCourses response :\n" + response.toString());

                if (mMyCoursesResponse.isSuccessResponse(response)) {

                    //Log.d(TAG, "onResponse() Success.response :  " + response.toString());

                    JSONObject jsonData = mMyCoursesResponse.getJSONData(response);
                    if (null != jsonData) {
                        mMyCoursesResponse.parseJSONObject(jsonData);
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        // Add or update course in database

                        if (type == TYPE_MY_COURSE) {
                            try {
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("isUpdateDB : " + isUpdateDB);

                                ///Log.d(TAG, "onResponse()_ isUpdateDB : " + isUpdateDB);
                                if (isUpdateDB) {


                                    Config.setStringValue(Config.COURSE_PICTURE_URL, mMyCoursesResponse.getCoursePictureUrl());
                                    Config.setStringValue(Config.COURSE_USER_PICTURE_URL, mMyCoursesResponse.getCourseUserPictureUrl());
                                    Config.setStringValue(Config.USER_PICTURE_URL, mMyCoursesResponse.getUserPictureUrl());

                                    ArrayList<String> courseIDs = new ArrayList<>();
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("CourseList size : " + mMyCoursesResponse.getCourseList().size());
                                    for (int i = 0; i < mMyCoursesResponse.getCourseList().size(); i++) {
                                        courseIDs.add(mMyCoursesResponse.getCourseList().get(i).getCourseID());
                                        CourseInterface.getInstance().insertOrUpdateCourse(mMyCoursesResponse.getCourseList().get(i));
                                    }
                                    CourseInterface.getInstance().deleteUnsubscribedCourses(courseIDs);
                                    // Time stamp before 30 days
                                    long oldTimeStamp = System.currentTimeMillis() - 2592000000L; // (60*60*24*30*1000) 30 days millis
                                    CourseInterface.getInstance().deleteOldCourse(oldTimeStamp);
                                    mOfflineCourseSize = CourseInterface.getInstance().getOfflineCourseSize();
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("mOfflineCourseSize : " + mOfflineCourseSize);
                                    if (mOfflineCourseSize > Flinnt.MAX_OFFLINE_COURSE_SIZE) {
                                        CourseInterface.getInstance().updateDatabase();
                                    }
                                } else if (mOfflineCourseSize < Flinnt.MAX_OFFLINE_COURSE_SIZE) {

                                    mOfflineCourseSize = CourseInterface.getInstance().getOfflineCourseSize();
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("mOfflineCourseSize : " + mOfflineCourseSize + ", AllCourseList size : " + mMyCoursesResponse.getCourseList().size());

                                    Config.setStringValue(Config.COURSE_PICTURE_URL, mMyCoursesResponse.getCoursePictureUrl());
                                    Config.setStringValue(Config.COURSE_USER_PICTURE_URL, mMyCoursesResponse.getCourseUserPictureUrl());
                                    Config.setStringValue(Config.USER_PICTURE_URL, mMyCoursesResponse.getUserPictureUrl());

                                    for (int i = 0; i < mMyCoursesResponse.getCourseList().size(); i++) {
                                        CourseInterface.getInstance().insertOrUpdateCourse(mMyCoursesResponse.getCourseList().get(i));
                                        mOfflineCourseSize++;
                                        if (mOfflineCourseSize == Flinnt.MAX_OFFLINE_COURSE_SIZE) {
                                            break;
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                LogWriter.err(e);
                            }
                            if (mMyCoursesResponse.getHasMore() > 0) {
                                sendRequest();
                            }
                        }
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("MyCourses Error : " + error.getMessage());

                mMyCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);

//		if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "isUpdateDB : " + isUpdateDB );
        // Adding request to request queue
//		if( isUpdateDB ) {
//			Requester.getInstance().addToRequestQueue(jsonObjReq, System.currentTimeMillis()+"");
//		}
//		else {
        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
//		}
    }


    /**
     * Sends response to handler
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {

            //Log.d(TAG, "onMessageToGUI().. msgId : " + messageID);
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mMyCoursesResponse;
            mHandler.sendMessage(msg);
        } else {
            //Log.d(TAG, "onMessageToGUI().. mHandler is null ");
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public MyCoursesRequest getMyCoursesRequest() {
        if (null == mMyCoursesRequest) {
            mMyCoursesRequest = new MyCoursesRequest();
            mMyCoursesRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mMyCoursesRequest.setSearch(getSearchString());
            mMyCoursesRequest.setOffset(offset);
            mMyCoursesRequest.setMax(max_count);
            if (mOfflineCourseIDs.size() > 0 && getSearchString().equals("")) {
                mMyCoursesRequest.setExcludeCourses(mOfflineCourseIDs);
            }
            mMyCoursesResponse.clearAllCourseList();
        }
        return mMyCoursesRequest;
    }

    public boolean isUpdateDB() {
        return isUpdateDB;
    }

    public void setUpdateDB(boolean isUpdateDB) {
        this.isUpdateDB = isUpdateDB;
    }

    public ArrayList<String> getOfflineCourseIDs() {
        return mOfflineCourseIDs;
    }

    public void setOfflineCourseIDs(ArrayList<String> mOfflineCourseIDs) {
        this.mOfflineCourseIDs = mOfflineCourseIDs;
    }


}