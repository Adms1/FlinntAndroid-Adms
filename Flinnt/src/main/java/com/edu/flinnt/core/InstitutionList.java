package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.InstitutionRequest;
import com.edu.flinnt.protocol.InstitutionResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class InstitutionList {

    public static final String TAG = InstitutionList.class.getSimpleName();
    public InstitutionRequest mInstitutionRequest;
    public InstitutionResponse mInstitutionResponse;
    public Handler mHandler = null;
    String searchString = "";

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int offset = 0;
    int max_count = 100;

    public InstitutionList(Handler handler , String searchString ) {
        mHandler = handler;
        this.searchString = searchString;
        this.offset = 0;
        getInstitutionResponse();
    }
    public InstitutionResponse getInstitutionResponse() {
        if (mInstitutionResponse == null) {
            mInstitutionResponse = new InstitutionResponse();
        }
        return mInstitutionResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_INSTITUTION_LIST_KEY;
    }


    public void sendInstitutionListRequest() {
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

                if (null == mInstitutionRequest) {
                    mInstitutionRequest=getInstitutionRequest();
                } else {
                    // Reset offset to new request - New offset = old offset + max
                    mInstitutionRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mInstitutionRequest.setOffset(offset);
                    mInstitutionRequest.setMax(max_count);
                    mInstitutionRequest.setSearch(searchString);
//                mContentsResponse.clearAllContentsList();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("InstitutionList Request :\nUrl : " + url + "\nData : " + mInstitutionRequest.getJSONString());

                JSONObject jsonObject = mInstitutionRequest.getJSONObject();

                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url,final JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("institute search response :\n" + response.toString());
                if (mInstitutionResponse.isSuccessResponse(response)) {

                    String institutionResponse = new String(response.toString());

                    JSONObject jsonData = mInstitutionResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mInstitutionResponse = gson.fromJson(institutionResponse, InstitutionResponse.class);
                        if(mInstitutionResponse.getData().getHasMore()==1){
                            offset = max_count + offset;
                            sendRequest();
                        }
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
                    LogWriter.write("InstitutionList Error : " + error.getMessage());

                mInstitutionResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    /**
     * Sends response to handler
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mInstitutionResponse;
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


    public InstitutionRequest getInstitutionRequest() {
        if (null == mInstitutionRequest) {
            mInstitutionRequest = new InstitutionRequest();
            mInstitutionRequest.setUserID(Config.getStringValue(Config.USER_ID));
        }
        mInstitutionRequest.setOffset(offset);
        mInstitutionRequest.setMax(max_count);
        mInstitutionRequest.setSearch(searchString);
        return mInstitutionRequest;
    }
}