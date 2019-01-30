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
import com.edu.flinnt.models.store.StoreBookSetDetailModel;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.CourseViewRequest;
import com.edu.flinnt.protocol.CourseViewResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Send request and get response and pass it to GUI
 */
public class CourseDescription {

    public static final String TAG = CourseDescription.class.getSimpleName();
    public CourseViewRequest mCourseViewRequest = null;
    public CourseViewResponse mCourseViewResponse = null;
    private StoreBookDetailResponse storeBookDetailResponse;
    private StoreModelResponse storeModelResponse;
    private StoreBookSetResponse storeBookSetResponse;
    private StoreBookSetDetailModel storeBookSetDetailModel;
    public Handler mHandler = null;
    private String courseId;
    private String inst_book_vendor_id = "";
    private String inst_book_set_vendor_id = "";

    private String courseHash;

    public CourseDescription(Handler handler, String id) {
        mHandler = handler;
        courseId = id;
        inst_book_vendor_id = id;
        inst_book_set_vendor_id = id;
        if(null == mCourseViewResponse) mCourseViewResponse = new CourseViewResponse();
        if(null == storeBookDetailResponse) storeBookDetailResponse = new StoreBookDetailResponse();
    }

    public CourseDescription(Handler handler,String id,String hash) {
        mHandler = handler;
        courseId = id;
        inst_book_vendor_id = id;
        inst_book_set_vendor_id = id;
        courseHash = hash;
        if (null == mCourseViewResponse) mCourseViewResponse = new CourseViewResponse();
        if (null == storeBookDetailResponse) storeBookDetailResponse = new StoreBookDetailResponse();
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW;
    }
    public String buildURLStringNew() {

        return Flinnt.LOCAL_API_URL_NEW +Flinnt.STORE_BOOK_DETAIL_API;

        //return Flinnt.LOCAL_API_URL_NEW + Flinnt.BOOKSET_LIST_DETAIL;
    }

    public String buildURLStringOfBookSetList() {

        return Flinnt.LOCAL_API_URL_NEW +Flinnt.BOOKSET_LIST;

        //return Flinnt.LOCAL_API_URL_NEW + Flinnt.BOOKSET_LIST_DETAIL;
    }

    public String buildURLStringOfBookSetDetail() {

        return Flinnt.LOCAL_API_URL_NEW +Flinnt.BOOKSET_LIST_DETAIL;

        //return Flinnt.LOCAL_API_URL_NEW + Flinnt.BOOKSET_LIST_DETAIL;
    }

    public void sendCourseDescriptionRequest(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    //sendRequest();
                    sendRequestNew();

                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }


    public void sendBookSetListRequest(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    //sendRequest();
                   // sendRequestNew();
                    callBookSetListRequest();

                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }

    public void sendBookSetDetailRequest(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    //sendRequest();
                    // sendRequestNew();
                    callBookSetDetailRequest();

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
        synchronized (CourseDescription.class) {
            try {
                String url = buildURLString();
                if (null == mCourseViewRequest) {
                    mCourseViewRequest = getCourseViewRequest();
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription Request :\nUrl : " + url + "\nData : " + mCourseViewRequest.getJSONString());

                JSONObject jsonObject = mCourseViewRequest.getJSONObject();

                sendJsonObjectRequest(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    public void sendRequestNew() {
        synchronized (CourseDescription.class) {
            try {
                String url = buildURLStringNew();
                if (null == mCourseViewRequest) {
                    mCourseViewRequest = getCourseViewRequest();
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription Request :\nUrl : " + url + "\nData : " +mCourseViewRequest.getJSONString());


                JSONObject jsonObject = mCourseViewRequest.getJSONObjectNew();
                sendJsonObjectRequestNew(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }


    public void callBookSetListRequest() {
        synchronized (CourseDescription.class) {
            try {
                String url = buildURLStringOfBookSetList();
                if (null == mCourseViewRequest) {
                    mCourseViewRequest = getCourseViewRequest();
                }
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription Request :\nUrl : " + url + "\nData : " +mCourseViewRequest.getJSONString());


                JSONObject jsonObject = mCourseViewRequest.getJSONObjectOfBooksetList();
                sendJsonObjectRequestOfBooksetList(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    public void callBookSetDetailRequest() {
        synchronized (CourseDescription.class) {
            try {
                String url = buildURLStringOfBookSetDetail();

                mCourseViewRequest = getCourseViewRequestOfBookSetDetail();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription Request :\nUrl : " + url + "\nData : " +mCourseViewRequest.getJSONString());


                JSONObject jsonObject = mCourseViewRequest.getJSONObjectOfBookSetDetail();
                sendJsonObjectRequestOfBooksetDetail(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription response :\n" + response.toString());

                if (mCourseViewResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseViewResponse.getJSONData(response);
                    if (null != jsonData) {
                        mCourseViewResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);
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

                mCourseViewResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }


    private void sendJsonObjectRequestOfBooksetList(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription response :\n" + response.toString());

                if (mCourseViewResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseViewResponse.getJSONData(response);
//                    if (null != jsonData) {
                    mCourseViewResponse.parseJSONObject(jsonData);

                    Gson gson = new Gson();

                    storeBookSetResponse = gson.fromJson(String.valueOf(response),StoreBookSetResponse.class);
                    //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());

                    sendMesssageToGUI2(Flinnt.SUCCESS);
//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }else{
                    sendMesssageToGUI(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("MyCourses Error : " + error.getMessage());

                mCourseViewResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }

    private void sendJsonObjectRequestOfBooksetDetail(String url,JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription response :\n" + response.toString());

                if (mCourseViewResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseViewResponse.getJSONData(response);
//                    if (null != jsonData) {
                    mCourseViewResponse.parseJSONObject(jsonData);

                    Gson gson = new Gson();

                    storeBookSetDetailModel = gson.fromJson(String.valueOf(response),StoreBookSetDetailModel.class);
                    //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());

                    sendMesssageToGUI3(Flinnt.SUCCESS);
//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }else{
                    sendMesssageToGUI(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("MyCourses Error : " + error.getMessage());

                mCourseViewResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }



    //08-01-2019 by vijay
    private void sendJsonObjectRequestNew(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("CourseDescription response :\n" + response.toString());

                if (mCourseViewResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mCourseViewResponse.getJSONData(response);
//                    if (null != jsonData) {
                        mCourseViewResponse.parseJSONObject(jsonData);

                        Gson gson = new Gson();

                        storeBookDetailResponse = gson.fromJson(String.valueOf(response),StoreBookDetailResponse.class);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());

                        sendMesssageToGUI(Flinnt.SUCCESS);
//                    } else {
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }else{
                    sendMesssageToGUI(Flinnt.FAILURE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("MyCourses Error : " + error.getMessage());

                mCourseViewResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
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
            Message msg = new Message();
            msg.what = messageID;
           // msg.obj = mCourseViewResponse;
            msg.obj = storeBookDetailResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public void sendMesssageToGUI2(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            // msg.obj = mCourseViewResponse;
            msg.obj = storeBookSetResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
    public void sendMesssageToGUI3(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            // msg.obj = mCourseViewResponse;
            msg.obj = storeBookSetDetailModel;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public CourseViewRequest getCourseViewRequest() {
        if (null == mCourseViewRequest) {
            mCourseViewRequest = new CourseViewRequest();
            mCourseViewRequest.setUserId(Config.getStringValue(Config.USER_ID));
            mCourseViewRequest.setCourseId(courseId);
            mCourseViewRequest.setCouserHash(courseHash);
            mCourseViewRequest.setPaidPromoContact(Flinnt.ENABLED);
            mCourseViewRequest.setInst_book_vendor_id(inst_book_vendor_id);
            mCourseViewRequest.setInst_book_set_vendor_id(inst_book_set_vendor_id);
        }
        return mCourseViewRequest;
    }

    public CourseViewRequest getCourseViewRequestOfBookSetDetail() {
        mCourseViewRequest = new CourseViewRequest();
        mCourseViewRequest.setUserId(Config.getStringValue(Config.USER_ID));
        mCourseViewRequest.setCourseId(courseId);
        mCourseViewRequest.setCouserHash(courseHash);
        mCourseViewRequest.setPaidPromoContact(Flinnt.ENABLED);
        mCourseViewRequest.setInst_book_vendor_id(inst_book_vendor_id);
        mCourseViewRequest.setInst_book_set_vendor_id(inst_book_set_vendor_id);

        return mCourseViewRequest;
    }
}
