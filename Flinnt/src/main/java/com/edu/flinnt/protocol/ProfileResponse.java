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
public class ProfileResponse extends BaseResponse {

	public static final String PROFILE_KEY = "profile";
	public static final String MANDATORY_KEY = "mandatory";

	public static final String FIRST_NAME_KEY = "first_name";
	public static final String LAST_NAME_KEY = "last_name";
	public static final String INSTITUTE_NAME_KEY = "institute_name";
	public static final String GENDER_KEY = "gender";
	public static final String BIRTHDAY_KEY = "birth_day";
	public static final String BIRTH_MONTH_KEY = "birth_month";
	public static final String BIRTH_YEAR_KEY = "birth_year";
	public static final String CITY_KEY = "city";
	public static final String EMAIL_KEY = "email";
	public static final String MOBILE_KEY = "mobile";

	private int first_name 										= 0;
	private int last_name 										= 0;
	private int institute_name 									= 0;
	private int gender											= 0;
	private int birth_day 										= 0;
	private int birth_month										= 0;
	private int birth_year										= 0;
	private int city 											= 0;
	private int email 											= 0;
	private int mobile 											= 0;

	public int getFirst_name() {
		return first_name;
	}

	public void setFirst_name(int first_name) {
		this.first_name = first_name;
	}

	public int getLast_name() {
		return last_name;
	}

	public void setLast_name(int last_name) {
		this.last_name = last_name;
	}

	public int getInstitute_name() {
		return institute_name;
	}

	public void setInstitute_name(int institute_name) {
		this.institute_name = institute_name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getBirth_day() {
		return birth_day;
	}

	public void setBirth_day(int birth_day) {
		this.birth_day = birth_day;
	}

	public int getBirth_month() {
		return birth_month;
	}

	public void setBirth_month(int birth_month) {
		this.birth_month = birth_month;
	}

	public int getBirth_year() {
		return birth_year;
	}

	public void setBirth_year(int birth_year) {
		this.birth_year = birth_year;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public int getEmail() {
		return email;
	}

	public void setEmail(int email) {
		this.email = email;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	public final ArrayList<Profile> profileList = new ArrayList<Profile>();

	public ArrayList<Profile> getProfileList() {
		return profileList;
	}

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
			JSONArray profileProperties = jsonData.getJSONArray(PROFILE_KEY);
			for(int i = 0; i < profileProperties.length(); i++) {
				JSONObject jObject = profileProperties.getJSONObject(i);
				Profile profile = new Profile();	
				profile.parseJSONObject(jObject);
				if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Profile profile :: " + profile.toString() );
				profileList.add(profile);
				profile = null;
			}
			if (jsonData.has(MANDATORY_KEY)) {
				JSONObject mandatoryFields  = jsonData.getJSONObject(MANDATORY_KEY);

				if(mandatoryFields.has(FIRST_NAME_KEY)) setFirst_name(mandatoryFields.getInt(FIRST_NAME_KEY) );
				if(mandatoryFields.has(LAST_NAME_KEY)) setLast_name(mandatoryFields.getInt(LAST_NAME_KEY) );
				if(mandatoryFields.has(INSTITUTE_NAME_KEY)) setInstitute_name(mandatoryFields.getInt(INSTITUTE_NAME_KEY));
				if(mandatoryFields.has(GENDER_KEY)) setGender(mandatoryFields.getInt(GENDER_KEY));
				if(mandatoryFields.has(BIRTHDAY_KEY)) setBirth_day(mandatoryFields.getInt(BIRTHDAY_KEY) );
				if(mandatoryFields.has(BIRTH_MONTH_KEY)) setBirth_month(mandatoryFields.getInt(BIRTH_MONTH_KEY) );
				if(mandatoryFields.has(BIRTH_YEAR_KEY)) setBirth_year(mandatoryFields.getInt(BIRTH_YEAR_KEY) );
				if(mandatoryFields.has(CITY_KEY)) setCity(mandatoryFields.getInt(CITY_KEY) );
				if(mandatoryFields.has(EMAIL_KEY)) setEmail(mandatoryFields.getInt(EMAIL_KEY) );
				if(mandatoryFields.has(MOBILE_KEY)) setMobile(mandatoryFields.getInt(MOBILE_KEY) );
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}
	
	public int getCount() {
		return profileList.size();
	}
}