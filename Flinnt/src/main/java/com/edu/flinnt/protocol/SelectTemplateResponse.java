package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class SelectTemplateResponse extends BaseResponse {

	public static final String TEMPLATES_KEY = "templates";

	private ArrayList<Template> templateList = new ArrayList<Template>();

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {

		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			parseJSONObject(jsonObject); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {

		try {
			JSONArray templates = jsonData.getJSONArray(TEMPLATES_KEY);
			clearTemplateList();
			for(int i = 0; i < templates.length(); i++) {
				JSONObject jObject = templates.getJSONObject(i);
				Template template = new Template();	
				template.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "template :: " + template.toString() );
				templateList.add(template);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Template name : " + template.getTemplateName());
				template = null;
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

	}

	public ArrayList<Template> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList<Template> templateList) {
		this.templateList = templateList;
	}

	public void clearTemplateList() {
		this.templateList.clear();
	}

}
