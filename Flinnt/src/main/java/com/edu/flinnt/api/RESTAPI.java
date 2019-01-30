package com.edu.flinnt.api;

import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.core.CustomJsonObjectRequest;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.helper.listner.APIResponse;

import org.json.JSONObject;

/**
 * Created by Nikhil Prajapati on 25-07-2018.
 */
public class RESTAPI {
    public static final String TAG = RESTAPI.class.getSimpleName();
    private static final RESTAPI ourInstance = new RESTAPI();
    APIResponse mapiResponse;
    private boolean isLogable = true;

    public static RESTAPI getInstance() {
        return ourInstance;
    }

    private RESTAPI() {
    }

    public void sendJsonObjectRequest(String url, JSONObject jsonObject, APIResponse apiResponse) {

        if (isLogable) {
            //Log.d("APIcall", url);
            //Log.d("APIcall", jsonObject.toString());
        }

        mapiResponse = apiResponse;
        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (isLogable) {
                    //Log.d("APIcall", response.toString());
                }
                mapiResponse.onSuccess(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mapiResponse.onFailer("on error " + error.getMessage());
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);

    }


}
