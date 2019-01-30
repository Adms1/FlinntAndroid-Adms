package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class ErrorResponse {

	public static final String MESSAGE_KEY = "message";
	public static final String ERRORS_KEY = "errors";

	public int status = Flinnt.INVALID;
	public String message = "";
	public ArrayList<Error> errorList = new ArrayList<Error>();

	synchronized public boolean parse(String jsonData) {

		errorList.clear();
		
		boolean ret = false;
		if(TextUtils.isEmpty(jsonData))
		{
			//if(LogWriter.isValidLevel(Log.INFO)) LogWriter.info("blank data to parse...");
			return ret;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(jsonData);

			try {
				setMessage( jsonObject.getString(MESSAGE_KEY) );
			}
			catch(Exception e){
				LogWriter.err(e);
			}
			
			if(jsonObject.has(ERRORS_KEY)){
				JSONArray errors = jsonObject.getJSONArray(ERRORS_KEY);
				
				for(int i = 0; i < errors.length(); i++) {
					JSONObject jObject = errors.getJSONObject(i);
					Error error = new Error();	
					
					try {
						error.setCode( jObject.getInt(error.CODE_KEY) );
					}
					catch(Exception e){
						LogWriter.err(e);
					}

					try {
						error.setMessage( jObject.getString(error.MESSAGE_KEY) );
					}
					catch(Exception e){
						LogWriter.err(e);
					}

					try {
						error.setFile( jObject.getString(error.FILE_KEY) );
					}
					catch(Exception e){
						LogWriter.err(e);
					}

					try {
						error.setLine( jObject.getInt(error.LINE_KEY) );
					}
					catch(Exception e){
						LogWriter.err(e);
					}

					try {
						error.setTrace( jObject.getString(error.TRACE_KEY) );
					}
					catch(Exception e){
						LogWriter.err(e);
					}
					errorList.add(error);
					error = null;
				}
			}

			ret = true;
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return ret;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<Error> getErrorList() {
		return errorList;
	}

	public void setErrorList(ArrayList<Error> errorList) {
		this.errorList = errorList;
	}

}
