package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesRequest;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 31/5/17.
 */

public class BrowseCourseCategory {

    public static final String TAG = BrowseCourseCategory.class.getSimpleName();
    public BrowseCoursesResponse mBrowseCoursesResponse = null;
    public BrowseCoursesRequest mBrowseCoursesRequest = null;
    public Handler mHandler = null;
    String searchString = "";
    String categoryId = "";

    int offset = 0;
    int max_count = 10;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public BrowseCourseCategory(Handler handler) {
        mHandler = handler;
        getLastBrowseCoursesResponse();
    }

    public BrowseCoursesResponse getLastBrowseCoursesResponse() {
        if (mBrowseCoursesResponse == null) {
            mBrowseCoursesResponse = new BrowseCoursesResponse();
        }
        return mBrowseCoursesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_BROWSE
                + "?" + BrowseCoursesRequest.FIELDS_KEY + "="
                + BrowsableCourse.ID_KEY + ","
                + BrowsableCourse.NAME_KEY + ","
                + BrowsableCourse.PICTURE_KEY + ","
                + BrowsableCourse.INSTITUTE_NAME_KEY + ","
                + BrowsableCourse.RATINGS_KEY + ","
                + BrowsableCourse.USER_COUNT_KEY + ","
                + BrowsableCourse.PUBLIC_TYPE_KEY + ","
                + BrowsableCourse.EVENT_DATETIME_KEY + ","
                + BrowsableCourse.IS_FREE_KEY + ","
                + BrowsableCourse.DISCOUNT_APPLICABLE_BROWSE_KEY + ","
                + BrowsableCourse.PRICE_BROWSE_KEY + ","
                + BrowsableCourse.PRICE_BUY_BROWSE_KEY + ","
                + BrowsableCourse.PRICE_KEY;
    }

    public void sendBrowseCoursesRequest() {
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
        synchronized (BrowseCourseCategory.class) {
            try {
                String url = buildURLString();

                if (null == mBrowseCoursesRequest) {
                    mBrowseCoursesRequest = getBrowseCoursesRequest();
                } else {
                    offset = offset + max_count;
                    mBrowseCoursesRequest.setOffset(offset);
                    mBrowseCoursesRequest.setMax(max_count);
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourseCategory Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());

                JSONObject jsonObject = mBrowseCoursesRequest.getJSONObject();

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

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourseCategory Response :\n" + response.toString());

                if (mBrowseCoursesResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mBrowseCoursesResponse.getJSONData(response);
                    if (null != jsonData) {
                        mBrowseCoursesResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        if (mBrowseCoursesResponse.getHasMore() > 0) {
                            sendRequest();
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
                    LogWriter.write("mBrowseCourseCategory Error : " + error.getMessage());

                mBrowseCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }


    /**
     * Sends response to handlers
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mBrowseCoursesResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public BrowseCoursesRequest getBrowseCoursesRequest() {
        if (null == mBrowseCoursesRequest) {
            mBrowseCoursesRequest = new BrowseCoursesRequest();
            mBrowseCoursesRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mBrowseCoursesRequest.setSearch(getSearchString());
            mBrowseCoursesRequest.setCategoryId(getCategoryId());
            mBrowseCoursesRequest.setOffset(offset);
            mBrowseCoursesRequest.setMax(max_count);
        }
        return mBrowseCoursesRequest;
    }
}