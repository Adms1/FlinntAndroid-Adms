package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.WishRequest;
import com.edu.flinnt.protocol.WishResponse;
import com.edu.flinnt.protocol.WishableCourses;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinntandr1 on 28/6/16.
 */
public class WishCourses {
    public static final String TAG = WishCourses.class.getSimpleName();
    public Handler mHandler = null;
    String searchString = "";
    public WishResponse mWishResponse = null;
    public WishRequest mWishRequest = null;

    public WishCourses(Handler handler) {
        mHandler = handler;
        getWishResponse();
    }

    public WishResponse getWishResponse() {
        if (mWishResponse == null) {
            mWishResponse = new WishResponse();
        }
        return mWishResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_WISHLIST
                + "?" + WishRequest.FIELDS_KEY + "="
                + WishableCourses.ID_KEY + ","
                + WishableCourses.NAME_KEY + ","
                + WishableCourses.PICTURE_KEY + ","
                + WishableCourses.INSTITUTE_NAME_KEY + ","
                + WishableCourses.RATINGS_KEY + ","
                + WishableCourses.USER_COUNT_KEY + ","
                + WishableCourses.PUBLIC_TYPE_KEY + ","
                + WishableCourses.EVENT_DATETIME_KEY + ","
                + WishableCourses.IS_FREE_KEY + ","
                + WishableCourses.DISCOUNT_APPLICABLE_KEY + ","
                + WishableCourses.DISCOUNT_PERCENT_KEY + ","
                + WishableCourses.DISCOUNT_AMOUNT_KEY + ","
                + WishableCourses.PRICE_BUY_KEY + ","
                + WishableCourses.PRICE_KEY;
    }

    public void sendWishlistCoursesRequest() {
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
        synchronized (WishCourses.class) {
            try {
                String url = buildURLString();

                if (null == mWishRequest) {
                    mWishRequest = getWishRequest();
                } else {
                    // Reset offset to new request - New offset = old offset + max
                    mWishRequest.setOffset(mWishRequest.getOffset() + mWishRequest.getMax());
                    mWishRequest.setMax(10);
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mWishCoursesRequest :\nUrl : " + url + "\nData : " + mWishRequest.getJSONString());

                JSONObject jsonObject = mWishRequest.getJSONObject();

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
                    LogWriter.write("mWishCoursesResponse :\n" + response.toString());

                if (mWishResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mWishResponse.getJSONData(response);
                    if (null != jsonData) {
                        mWishResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        if (mWishResponse.getHasMore() > 0) {
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
                    LogWriter.write("Wishlist Error : " + error.getMessage());

                mWishResponse.parseErrorResponse(error);
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
            msg.obj = mWishResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public WishRequest getWishRequest() {
        if (null == mWishRequest) {
            mWishRequest = new WishRequest();
            mWishRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mWishRequest.setSearch(searchString);
            mWishRequest.setOffset(0);
            mWishRequest.setMax(0);
        }
        return mWishRequest;
    }
}