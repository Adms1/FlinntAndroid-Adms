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
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class AddressResponse {

    public static final String USER_ID_KEY = "user_id";
    private String userId;
    public static final String TAG = AddressResponse.class.getSimpleName();

    private ShippingAdressModel shippingAdressModel;
    private Handler mHandler;


    public AddressResponse(Handler handler, String userid){
        mHandler = handler;
        this.userId = userid;

    }
    public synchronized String getJSONString() {
        return getJSONObject().toString();
    }

    public synchronized JSONObject getJSONObject() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public String buildURLString() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.GET_USER_ADDRESS_LIST_API;
    }


    public void getAddressListRequest() {
        synchronized (AddressResponse.class) {
            try {
                String url = buildURLString();
                JSONObject jsonObject = getJSONObject();
                sendJsonObjectRequest(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    private void sendJsonObjectRequest(String url,JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());
                try {
                    int status = response.optInt("status");

//                    if (status == 1) {
                        // sendListCartItemsRequest();
                        Gson gson = new Gson();
                        shippingAdressModel = gson.fromJson(String.valueOf(response),ShippingAdressModel.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
//                    }else if(status == 0){
//
//
//                        sendMesssageToGUI(Flinnt.FAILURE);
//                    }
                }catch (Exception ex){
                    ex.printStackTrace();
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
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = shippingAdressModel;
            msg.arg1 = 1;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }


}
