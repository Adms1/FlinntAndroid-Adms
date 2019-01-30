package com.edu.flinnt.protocol;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by flinnt-android-3 on 13/12/16.
 */
public class BaseRealmWSRequest {

    public ErrorResponse errorResponse = null;

    public void parseErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError /*|| error instanceof NoConnectionError*/) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("TimeoutError");
            Helper.showToast("Your current internet connection is slow. Pl. try after some time.", Toast.LENGTH_SHORT);
        } else if (error instanceof NoConnectionError) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("NoConnectionError");
            Helper.showToast("No network connection !!", Toast.LENGTH_SHORT);
        } else if (error instanceof AuthFailureError) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("AuthFailureError");
        } else if (error instanceof ServerError) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ServerError");
        } else if (error instanceof NetworkError) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("NetworkError");
        } else if (error instanceof ParseError) {
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("ParseError");
        }

        String responseString = "";
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {

            responseString = new String(response.data);
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Error Response : " + responseString);

            errorResponse = new ErrorResponse();
            boolean success = errorResponse.parse(responseString);
            if( success ) {
                ArrayList<Error> errorList = errorResponse.getErrorList();
                if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.write( "Error List size : " + errorList.size());
                for(int i = 0; i < errorList.size(); i++) {
                    if (LogWriter.isValidLevel(Log.ERROR))
                        LogWriter.write( "\nError code : " + errorList.get(i).getCode()
                                + "\nError Message : " + errorList.get(i).getMessage()
                                + "\nError File : " + errorList.get(i).getFile()
                                + "\nError Line : " + errorList.get(i).getLine()
                                + "\nError Trace : " + errorList.get(i).getTrace());

                    if (errorList.get(i).getCode() == 312) {
                        Requester.getInstance().cancelPendingRequests();
                        Intent intent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
                        intent.putExtra("doWhat", "deleteUser");
                        intent.putExtra("errMsg", errorList.get(i).getMessage());

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        FlinntApplication.getContext().startActivity(intent);
                    }
                }

            }
        }

    }

    public boolean isSuccessResponse(JSONObject response) {
        boolean ret = false;

        int status = 0;
        try {
            status = response.getInt(Flinnt.STATUS_KEY);
        }
        catch(Exception e){
            LogWriter.err(e);
        }

        if( Flinnt.SUCCESS == status ) {
            ret = true;
        }

        return ret;
    }
    public JSONObject getJSONData(JSONObject response) {

        JSONObject data = null;
        try {
            data = response.getJSONObject(Flinnt.DATA_KEY);
        }
        catch(Exception e){
            LogWriter.err(e);
        }

        return data;
    }
}
