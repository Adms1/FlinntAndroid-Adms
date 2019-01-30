package com.edu.flinnt.gui.store;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.CustomJsonObjectRequest;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.SuggestedCourses;
import com.edu.flinnt.models.store.RelatedBookResponse;
import com.edu.flinnt.models.store.RelatedVendorsResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesRequest;
import com.edu.flinnt.protocol.SuggestedCoursesRequest;
import com.edu.flinnt.protocol.SuggestedCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

//10-01-2019 by vijay.

public class SuggestedBooks {

    public static final String TAG = SuggestedBooks.class.getSimpleName();
    public static final int USERS_JOINED_COURSES = 1;
    public static final int COURSES_FROM_INSTITUTE = 2;
    public RelatedBookResponse mSuggestedCoursesResponse = null;
    public RelatedVendorsResponse relatedVendorsResponse = null;

    public SuggestedCoursesRequest mSuggestedCoursesRequest = null;
    public Handler mHandler = null;
    private int type = Flinnt.INVALID;
    private String courseId,inst_vendor_id,standardid;

    public SuggestedBooks(Handler handler, String inst_vendor_id,String standardid) {
        mHandler = handler;
        this.inst_vendor_id = inst_vendor_id;
        this.standardid = standardid;
        getSuggestedCoursesResponse();
    }

    public RelatedBookResponse getSuggestedCoursesResponse() {
        if (mSuggestedCoursesResponse == null) {
            mSuggestedCoursesResponse = new RelatedBookResponse();
        }
        return mSuggestedCoursesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
//    public String buildURLString() {
//        String params = "?" + BrowseCoursesRequest.FIELDS_KEY + "="
//                + BrowsableCourse.ID_KEY + ","
//                + BrowsableCourse.NAME_KEY + ","
//                + BrowsableCourse.PICTURE_KEY + ","
//                + BrowsableCourse.INSTITUTE_NAME_KEY + ","
//                + BrowsableCourse.RATINGS_KEY + ","
//                + BrowsableCourse.IS_FREE_KEY + ","
//                + BrowsableCourse.DISCOUNT_APPLICABLE_BROWSE_KEY + ","
////                + BrowsableCourse.PRICE_BROWSE_KEY + ","
//                + BrowsableCourse.PRICE_BUY_BROWSE_KEY + ","
//                + BrowsableCourse.PRICE_KEY;
//
//
//        if (type == USERS_JOINED_COURSES)       return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_JOINED + params;
//        return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_MORE + params;
//    }


    public String buildURLStringNew() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.STORE_RELATED_BOOKS_API;
    }

    public String buildURLStringNew1() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.STORE_RELATED_VENDOR_API;
    }
    public void sendSuggestedBooksRequest() {
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

    public void sendSuggestedVendorsRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendRequest2();
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
        synchronized (SuggestedBooks.class) {
            try {
                String url = buildURLStringNew();

                if (null == mSuggestedCoursesRequest) {
                    mSuggestedCoursesRequest = getSuggestedCoursesRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + mSuggestedCoursesRequest.getJSONString());

                JSONObject jsonObject = mSuggestedCoursesRequest.getJSONObjectNew();

                sendJsonObjectRequest1(url,jsonObject);
            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    public void sendRequest2() {
        synchronized (SuggestedBooks.class) {
            try {
                String url = buildURLStringNew1();

                if (null == mSuggestedCoursesRequest) {
                    mSuggestedCoursesRequest = getSuggestedCoursesRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + mSuggestedCoursesRequest.getJSONString());

                JSONObject jsonObject = mSuggestedCoursesRequest.getJSONObjectNew1();

                sendJsonObjectRequest1(url,jsonObject);
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
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());


                Gson gson = new Gson();

                relatedVendorsResponse = gson.fromJson(String.valueOf(response),RelatedVendorsResponse.class);

                if (relatedVendorsResponse.getStatus() == 1) {

//                    if (null != jsonData) {


                       // mSuggestedCoursesResponse.parseJSONObject(jsonData);
                        sendMesssageToGUI(Flinnt.SUCCESS);
//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }else {
                    sendMesssageToGUI2(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    private void sendJsonObjectRequest1(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());


                Gson gson = new Gson();

                mSuggestedCoursesResponse = gson.fromJson(String.valueOf(response),RelatedBookResponse.class);


                if (mSuggestedCoursesResponse.getStatus() == 1) {

//                    if (null != jsonData) {


                    // mSuggestedCoursesResponse.parseJSONObject(jsonData);
                    sendMesssageToGUI(Flinnt.SUCCESS);
//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }else {
                    sendMesssageToGUI(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
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
            msg.obj = mSuggestedCoursesResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
    public void sendMesssageToGUI2(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = relatedVendorsResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }



    public SuggestedCoursesRequest getSuggestedCoursesRequest() {
        if (null == mSuggestedCoursesRequest) {
            mSuggestedCoursesRequest = new SuggestedCoursesRequest();
            mSuggestedCoursesRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mSuggestedCoursesRequest.setCourseId(courseId);
            mSuggestedCoursesRequest.setInst_book_vender_id(inst_vendor_id);
            mSuggestedCoursesRequest.setStandardid(standardid);
            mSuggestedCoursesRequest.setMax(10);
        }
        return mSuggestedCoursesRequest;
    }
}
