package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class UpdateBirthdateRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String DOB_DAY_KEY = "dob_day";
	public static final String DOB_MONTH_KEY = "dob_month";
	public static final String DOB_YEAR_KEY = "dob_year";

	private String userID = "";
	public int dobDay,dobMonth,dobYear;

	/**
	 * Converts the json object to string
	 * @return converted json string
	 */
	public synchronized String getJSONString() {

		return getJSONObject().toString();
	}

	/**
	 * creates json object
	 * @return created json object
	 */
	public synchronized JSONObject getJSONObject() {

		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(USER_ID_KEY, userID);
			returnedJObject.put(DOB_DAY_KEY, dobDay);
			returnedJObject.put(DOB_MONTH_KEY, dobMonth);
			returnedJObject.put(DOB_YEAR_KEY, dobYear);
		}
		catch(Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getDobDay() {
		return dobDay;
	}

	public void setDobDay(int dobDay) {
		this.dobDay = dobDay;
	}

	public int getDobMonth() {
		return dobMonth;
	}

	public void setDobMonth(int dobMonth) {
		this.dobMonth = dobMonth;
	}

	public int getDobYear() {
		return dobYear;
	}

	public void setDobYear(int dobYear) {
		this.dobYear = dobYear;
	}
}
