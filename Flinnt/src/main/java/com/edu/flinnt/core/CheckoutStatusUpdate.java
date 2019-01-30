package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.CheckoutStatusUpdateRequest;
import com.edu.flinnt.protocol.CheckoutStatusUpdateResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyConfig;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.File;

/**
 * Created by flinnt-android-2 on 15/10/16.
 */
public class CheckoutStatusUpdate {


    public static final String TAG = CheckoutStatusUpdate.class.getSimpleName();
    public static CheckoutStatusUpdateResponse mCheckoutStatusUpdateResponse = null;
    public Handler mHandler = null;
    public int mTransectionID;
    public int mStatusCode;

    public CheckoutStatusUpdate(Handler handler, int transectionID, int statusCode) {
        mHandler = handler;
        mTransectionID = transectionID;
        mStatusCode= statusCode;
        getLastResponse();
    }

    public CheckoutStatusUpdateResponse getLastResponse() {
        if (mCheckoutStatusUpdateResponse == null) {
            mCheckoutStatusUpdateResponse = new CheckoutStatusUpdateResponse();
        }
        return mCheckoutStatusUpdateResponse;
    }

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_CHECKOUT_STATUS_UPDATE_KEY ;
    }

    public void sendCheckoutStatusUpdateRequest() {
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
        synchronized (FreeCheckout.class) {
            try {
                String url = buildURLString();
                CheckoutStatusUpdateRequest checkoutStatusUpdateRequest = new CheckoutStatusUpdateRequest();
                checkoutStatusUpdateRequest.setUserID(Config.getStringValue(Config.USER_ID));
                checkoutStatusUpdateRequest.setTransactionID(mTransectionID);
                checkoutStatusUpdateRequest.setStatusCode(mStatusCode);

                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CheckoutStatusUpdate Request :\nUrl : " + url + "\nData : " + checkoutStatusUpdateRequest.getJSONString());

                JSONObject jsonObject = checkoutStatusUpdateRequest.getJSONObject();

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
                if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("CheckoutStatusUpdate response :\n" + response.toString());

                if( null != mCheckoutStatusUpdateResponse && mCheckoutStatusUpdateResponse.isSuccessResponse(response)) {
                    String checkoutStatusUpdateResponse = new String(response.toString());
                    JSONObject jsonData = mCheckoutStatusUpdateResponse.getJSONData(response);
                    if (null != jsonData) {
                        Gson gson = new Gson();
                        mCheckoutStatusUpdateResponse = gson.fromJson(checkoutStatusUpdateResponse, CheckoutStatusUpdateResponse.class);
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("CheckoutStatusUpdate Error : " + error.getMessage());

                if( null != mCheckoutStatusUpdateResponse ) {
                    mCheckoutStatusUpdateResponse.parseErrorResponse(error);
                    sendMesssageToGUI(Flinnt.FAILURE);
                }
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);

        /** remove old unwanted files...*/
        Helper.deleteOldFiles(new File(MyConfig.FLINNT_FOLDER_PATH));
    }


    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if( null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mCheckoutStatusUpdateResponse;
            mHandler.sendMessage(msg);
        }
    }

}
