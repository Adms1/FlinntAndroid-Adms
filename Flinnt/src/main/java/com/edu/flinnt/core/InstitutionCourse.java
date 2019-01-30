package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.InstitutionCoursesRequest;
import com.edu.flinnt.protocol.InstitutionCoursesResponse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 19/10/16.
 */
public class InstitutionCourse {

    public static final String TAG = PostViewers.class.getSimpleName();
    public static InstitutionCoursesResponse mInstitutionCoursesResponse = null;
    public InstitutionCoursesRequest mInstitutionCoursesRequest = null;
    public Handler mHandler = null;
    private String mUserId = "";
    private String mInstituteId = "";
    private String search = "";
    private int offest = 0;

    public InstitutionCourse(Handler handler, String userId, String instituteId) {
        mHandler = handler;
        mUserId = userId;
        mInstituteId = instituteId;
        getLastResponse();
    }

    public InstitutionCoursesResponse getLastResponse() {
        if (mInstitutionCoursesResponse == null) {
            mInstitutionCoursesResponse = new InstitutionCoursesResponse();
        }
        return mInstitutionCoursesResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_INSTITUTION_COURSES_KEY;
    }

    public void sendInstitutionCourseRequest(InstitutionCoursesRequest institutionCoursesRequest) {
        mInstitutionCoursesRequest = institutionCoursesRequest;
        sendInstitutionCourseRequest();
    }

    public void sendInstitutionCourseRequest() {
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
        synchronized (PostViewers.class) {
            try {
                String url = buildURLString();

                if (null == mInstitutionCoursesRequest) {
                    mInstitutionCoursesRequest = new InstitutionCoursesRequest();
                    //  mInstitutionCoursesResponse.clearViewersList();
                }
                mInstitutionCoursesRequest.setUserId(mUserId);
                mInstitutionCoursesRequest.setInstituteId(mInstituteId);
                mInstitutionCoursesRequest.setSearch(search);
                mInstitutionCoursesRequest.setOffset(offest);

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("InstitutionCourses Request  :\nUrl : " + url + "\nData : " + mInstitutionCoursesRequest.getJSONString());

                JSONObject jsonObject = mInstitutionCoursesRequest.getJSONObject();

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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("InstitutionCourses Response :\n" + response.toString());

                if (mInstitutionCoursesResponse.isSuccessResponse(response)) {
                    String InstitutionResponse = new String(response.toString());
                    JSONObject jsonData = mInstitutionCoursesResponse.getJSONData(response);

                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mInstitutionCoursesResponse = gson.fromJson(InstitutionResponse, InstitutionCoursesResponse.class);
                        if(mInstitutionCoursesResponse.getData().getHasMore()==1){
                            offest = offest + mInstitutionCoursesRequest.getMax();
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
                    LogWriter.write("InstitutionCourses Error : " + error.getMessage());

                mInstitutionCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);
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
            msg.obj = mInstitutionCoursesResponse;
            mHandler.sendMessage(msg);
        }
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getOffest() {
        return offest;
    }

    public void setOffest(int offest) {
        this.offest = offest;
    }


}
