package com.edu.flinnt.core.store;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.CustomJsonObjectRequest;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.models.store.FilterDataModel;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

public class FilterDataRequest {

    public static final String TAG = FilterDataRequest.class.getSimpleName();
    public FilterDataRequest filterDataRequest = null;
    public FilterDataModel storeModelResponse = null;

    public Handler mHandler = null;
    String searchString = "";
    public  FilterDataRequest(Handler handler) {
        mHandler = handler;
    }


    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.LOCAL_API_URL_NEW + Flinnt.FILTER_DATA_LIST;
    }


    public void sendFilterDataRequest() {
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
    private void sendRequest() {
        synchronized (BrowseCoursesNew.class) {
            try {
                String url = buildURLString();
                if (LogWriter.isValidLevel(Log.DEBUG)) {
                    //Log.d("Brr","mBrowseCourses Request :\nUrl : " + url + "\nData : " + mBrowseCoursesRequest.getJSONString());
                }
                sendJsonObjectRequestNew(url);
            } catch (Exception e) {
                //.d("Brr", "sendRequest() Failuar catch - msg : " + e.getMessage());
                LogWriter.err(e);
            }
        }
    }

    private void sendJsonObjectRequestNew(String url) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.GET, url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mBrowseCourses Response new--- :\n" + response.toString());
                //Log.d("Brr", "mBrowseCourses Response new--- : " + response.toString());

                int status =  response.optInt("status");

                if(status == 1){

                    try {
                        Gson gsonData = new Gson();
                        storeModelResponse = gsonData.fromJson(String.valueOf(response),FilterDataModel.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }catch (Exception ex){
                        ex.printStackTrace();
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }else{
                    sendMesssageToGUI(Flinnt.FAILURE);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("mBrowseCourses Error : " + error.getMessage());
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

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


}
