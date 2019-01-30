package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.models.store.StoreBookDetailResponse;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesRequest;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class BrowseCourses {

    public static final String TAG = BrowseCourses.class.getSimpleName();
    public BrowseCoursesResponse mBrowseCoursesResponse = null;

    public StoreModelResponse storeModelResponse = null;

    public BrowseCoursesRequest mBrowseCoursesRequest = null;
    public Handler mHandler = null;
    String searchString = "";

    int offset = 0;
    int max_count = 5;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public BrowseCourses(Handler handler) {
        mHandler = handler;
        getLastBrowseCoursesResponse();
        getLastStoreCoursesResponse();
    }

    public BrowseCoursesResponse getLastBrowseCoursesResponse() {
        if (mBrowseCoursesResponse == null) {
            mBrowseCoursesResponse = new BrowseCoursesResponse();
        }
        return mBrowseCoursesResponse;
    }

    public StoreModelResponse getLastStoreCoursesResponse() {
        if (storeModelResponse == null) {
            storeModelResponse = new StoreModelResponse();
        }
        return storeModelResponse;
    }
    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_BROWSE_CATEGORY
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
                + BrowsableCourse.PRICE_KEY + ","
                + BrowsableCourse.CATEGORY_ID_KEY + ","
                + BrowsableCourse.CATEGORY_NAME_KEY;

    }


    //08-01-2019 by vijay
    public String buildURLStringNew() {
       // return Flinnt.LOCAL_API_URL_NEW +Flinnt.STORE_BOOK_LIST_API;
        return Flinnt.LOCAL_API_URL_NEW +Flinnt.STORE_BOOK_LIST_API;



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
        synchronized (BrowseCourses.class) {
            try {

               // String url = buildURLString();

                //08-01-2019 by vijay

                String url  = buildURLStringNew();
                //Log.d("Brr", "sendRequest() in try - url : " + url);

//                if (null == mBrowseCoursesRequest) {
//                    mBrowseCoursesRequest = getBrowseCoursesRequest();
//                } else {
//                    offset = offset + max_count;
//                    mBrowseCoursesRequest.setOffset(offset);
//                    mBrowseCoursesRequest.setMax(max_count);
//                }
//                if (LogWriter.isValidLevel(Log.DEBUG)) {
//                    LogWriter.write("mBrowseCourses Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());
//                    //Log.d("Brr","mBrowseCourses Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());
//                }


                if (null == mBrowseCoursesRequest) {
                    mBrowseCoursesRequest = getBrowseCoursesRequest();
                } else {
                    offset = offset + max_count;
                    mBrowseCoursesRequest.setOffset(offset);
                    mBrowseCoursesRequest.setMax(max_count);
                }
                if (LogWriter.isValidLevel(Log.DEBUG)) {
                    LogWriter.write("mBrowseCourses Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());
                    //Log.d("Brr","mBrowseCourses Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());
                }

                JSONObject jsonObject = mBrowseCoursesRequest.getJSONObject();
                jsonObject.put("user_id",Config.getStringValue(Config.USER_ID));

               // sendJsonObjectRequest(url, jsonObject);
                sendJsonObjectRequestNew(url,jsonObject);
            } catch (Exception e) {
                //.d("Brr", "sendRequest() Failuar catch - msg : " + e.getMessage());
                LogWriter.err(e);
            }
        }
    }

    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourses Response new--- :\n" + response.toString());
                //Log.d("Brr", "mBrowseCourses Response new--- : " + response.toString());
                if (mBrowseCoursesResponse.isSuccessResponse(response)) {

                    //Log.d("Brr", "onResuponse() response suceess: " + response.toString());

                    JSONObject jsonData = mBrowseCoursesResponse.getJSONData(response);
                    if (null != jsonData) {

                        //Log.d("Brr", "onresp() jsonData : " + jsonData.toString());

                        mBrowseCoursesResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        if (mBrowseCoursesResponse.getHasMore() > 0) {
                            sendRequest();
                        }

                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                } else {
                    //Log.d("Brr", "onresp() not success : ");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("mBrowseCourses Error : " + error.getMessage());

                //Log.d("Brr", "onErrorResuponse() msg : " + error.getMessage());

                mBrowseCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }

    private void sendJsonObjectRequestNew(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourses Response new--- :\n" + response.toString());
                //Log.d("Brr", "mBrowseCourses Response new--- : " + response.toString());
                if (mBrowseCoursesResponse.isSuccessResponse(response)) {

                    //Log.d("Brr", "onResuponse() response suceess: " + response.toString());

                    JSONObject jsonData = mBrowseCoursesResponse.getJSONData(response);
//                    if (null != jsonData) {

                        //Log.d("Brr", "onresp() jsonData : " + jsonData.toString());


                       // mBrowseCoursesResponse.parseJSONObject(jsonData);
                        try {
                            Gson gsonData = new Gson();
                            storeModelResponse = gsonData.fromJson(String.valueOf(response),StoreModelResponse.class);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }


                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        try {
                            if (mBrowseCoursesResponse.getHasMore() > 0) {
                                sendRequest();
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                } else {
                    //Log.d("Brr", "onresp() not success : ");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("mBrowseCourses Error : " + error.getMessage());

                //Log.d("Brr", "onErrorResuponse() msg : " + error.getMessage());

                mBrowseCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });
        jsonObjReq.setPriority(Priority.HIGH);
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
            //Log.d("Brr", "sendMesgToGUI() msgID : " + String.valueOf(messageID));
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = storeModelResponse;
            mHandler.sendMessage(msg);
        } else {
            //Log.d("Brr", "sendMesgToGUI() handler is null : " + String.valueOf(messageID));
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public BrowseCoursesRequest getBrowseCoursesRequest() {
        if (null == mBrowseCoursesRequest) {
            mBrowseCoursesRequest = new BrowseCoursesRequest();
            mBrowseCoursesRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mBrowseCoursesRequest.setSearch(getSearchString());
            mBrowseCoursesRequest.setOffset(offset);
            mBrowseCoursesRequest.setMax(max_count);
        }
        return mBrowseCoursesRequest;
    }
}
