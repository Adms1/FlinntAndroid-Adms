package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.ContentsRequest;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.contentlist.Contents;
import com.edu.flinnt.protocol.contentlist.Data;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by flinnt-android-1 on 19/8/16.
 */
public class ContentsList {

    public static final String TAG = ContentsList.class.getSimpleName();
    public ContentsRequest mContentsRequest;
    public ContentsResponse mContentsResponse;
    public Handler mHandler = null;
    String searchString = "";
    String mCourseId;
    private Realm mRealm = null;

    public ContentsList(Handler handler, String courseId) {
        mHandler = handler;
        mCourseId = courseId;
        getContentsListResponse();
    }

    public ContentsResponse getContentsListResponse() {
        if (mContentsResponse == null) {
            mContentsResponse = new ContentsResponse();
        }
        return mContentsResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CONTENTS_LIST;
    }


    public void sendContentsListRequest() {
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

                if (null == mContentsRequest) {
                    mContentsRequest=getContentsRequest();
                } else {
                    // Reset offset to new request - New offset = old offset + max
                    mContentsRequest.setCourse_id(mCourseId);
                    mContentsRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mContentsRequest.setMultiple_attachment(Flinnt.ENABLED);
                    mContentsRequest.setOffset(mContentsRequest.getOffset() + mContentsRequest.getMax());
                    mContentsRequest.setMax(10);
//                mContentsResponse.clearAllContentsList();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("ContentsList Request :\nUrl : " + url + "\nData : " + mContentsRequest.getJSONString());

                //Log.d("ConnRes","ContentsList Request :\nUrl : " + url + "\nData : " + mContentsRequest.getJSONString());

                JSONObject jsonObject = mContentsRequest.getJSONObject();

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
                    LogWriter.write("ContentsList Response :\n" + response.toString());

                //Log.d("ConnRes","ContentsList Response :\n" + response.toString());

                if (mContentsResponse.isSuccessResponse(response)) {

                    //Log.d("ConnRes","isSucces");
                    final String contentsResponse = new String(response.toString());

                    final JSONObject jsonData = mContentsResponse.getJSONData(response);

                    if (null != jsonData) {

                        if(mRealm == null){

                            mRealm = Realm.getInstance(Helper.createRealmObj());//Realm.getDefaultInstance();
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        RealmResults<Data> rows = realm.where(Data.class).equalTo("courseID", mCourseId).equalTo("userID", Config.getStringValue(Config.USER_ID)).findAll();
                                        for (int i = 0; i < rows.size(); i++) {
                                            RealmList<Sections> sectionList = rows.get(i).getList();
                                            for (int j = 0; j < sectionList.size(); j++) {
                                                RealmList<Contents> contentList = sectionList.get(j).getContents();
                                                for (int k = 0; k < contentList.size(); k++) {
                                                    if (k < contentList.size()) {
                                                        contentList.get(k).getStatistics().deleteFromRealm();
                                                        contentList.get(k).getAttachments().deleteAllFromRealm();
                                                    }
                                                }
                                                contentList.deleteAllFromRealm();
                                            }
                                            sectionList.deleteAllFromRealm();
//                                            courseContent.deleteFromRealm();

                                        }
                                        rows.deleteAllFromRealm();

                                    } catch (Exception e) {
                                        LogWriter.err(e);
                                    }
                                }
                            });
                        }

                        // To delete records of perticular course

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                boolean isExist = false;
                                Data mCourseContent = null;

                                Gson gson = new Gson();
                                mContentsResponse = gson.fromJson(contentsResponse, ContentsResponse.class);

                                try {
                                    mCourseContent = mRealm.where(Data.class).equalTo("courseID", mCourseId).equalTo("userID", Config.getStringValue(Config.USER_ID)).findFirst();

                                    int offSet = mContentsRequest.getOffset();
                                    if(offSet == -1){
                                        offSet = 0;
                                    }

                                    if(mCourseContent.getOffset() == offSet && mCourseContent.getCourseID().equalsIgnoreCase(mCourseId)){
//                                        if (mCourseContent != null)
                                        isExist = true;
                                    }
                                } catch (Exception e) {
                                    LogWriter.err(e);
                                }

                                try {
                                    // To add records of perticular course.
                                    if(!isExist){
                                        mContentsResponse.getData().setUserID(Config.getStringValue(Config.USER_ID));
                                        mContentsResponse.getData().setCourseID(mCourseId);
                                        mContentsResponse.getData().setOffset(mContentsRequest.getOffset());
                                        mRealm.copyToRealm(mContentsResponse.getData());
                                    }
                                }catch (Exception e){
                                    LogWriter.err(e);
                                }

                                if(mContentsRequest.getOffset() == 0){
                                    sendMesssageToGUI(Flinnt.REFRESH_LIST);
                                }

                                sendMesssageToGUI(Flinnt.SUCCESS);

                                if (mContentsResponse.getData().getHasMore() > 0) {
                                    sendRequest();
                                }
                            }
                        });

                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("ContentsList Error : " + error.getMessage());

                mContentsResponse.parseErrorResponse(error);

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
            msg.obj = mContentsResponse;
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


    public ContentsRequest getContentsRequest() {
        if (null == mContentsRequest) {
            mContentsRequest = new ContentsRequest();
            mContentsRequest.setCourse_id(mCourseId);
            mContentsRequest.setUserID(Config.getStringValue(Config.USER_ID));
            mContentsRequest.setMultiple_attachment(Flinnt.ENABLED);
            mContentsRequest.setSearch(getSearchString());
            mContentsRequest.setOffset(0);
            mContentsRequest.setMax(0);
        }
        return mContentsRequest;
    }
}