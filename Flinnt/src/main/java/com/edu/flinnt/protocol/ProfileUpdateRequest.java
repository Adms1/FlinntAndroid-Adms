package com.edu.flinnt.protocol;

import android.text.TextUtils;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class ProfileUpdateRequest {

	public static final String USER_ID_KEY 	=  "user_id";
	public static final String FIRST_NAME_KEY 	=  "first_name";
	public static final String LAST_NAME_KEY 	=  "last_name";
	public static final String INSTITUTE_NAME_KEY 	=  "institute_name";
	public static final String GENDER_KEY 	=  "gender";
	public static final String DOB_DAY_KEY 	=  "dob_day";
	public static final String DOB_MONTH_KEY 	=  "dob_month";
	public static final String DOB_YEAR_KEY 	=  "dob_year";
	public static final String CITY_KEY 	=  "city";
	public static final String EMAIL_KEY 	=  "email";
	public static final String MOBILE_NUMBER_KEY 	=  "mobile_no";
	public static final String RESOURSE_ID_KEY 	=  "resource_id";
	
	private String userID 		= "";
	private String firstName 		= "";
	private String lastName 		= "";
	private String instituteName 		= "";
	private String gender 		= "Male";	//Male/Female
	private String dobDay 		= "0";
	private String dobMonth 	= "0";
	private String dobYear 		= "0";
	private String city 		= "";
	private String email 		= "";
	private String mobileNo 		= "";
	private String resourceID		= "";
	//private String password 		= "";

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
			returnedJObject.put(FIRST_NAME_KEY, firstName); 
			returnedJObject.put(LAST_NAME_KEY, lastName); 
			returnedJObject.put(INSTITUTE_NAME_KEY, instituteName); 
			returnedJObject.put(GENDER_KEY, gender); 
			returnedJObject.put(DOB_DAY_KEY, dobDay); 
			returnedJObject.put(DOB_MONTH_KEY, dobMonth); 
			returnedJObject.put(DOB_YEAR_KEY, dobYear); 
			returnedJObject.put(CITY_KEY, city); 
			returnedJObject.put(EMAIL_KEY, email); 
			returnedJObject.put(MOBILE_NUMBER_KEY, mobileNo); 
			if(!TextUtils.isEmpty(resourceID)){
				returnedJObject.put(RESOURSE_ID_KEY, resourceID);
			}
			/*if(!TextUtils.isEmpty(password)){
				returnedJObject.put(PASSWORD_KEY, password);
			}*/
			
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(String instituteName) {
		this.instituteName = instituteName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDobDay() {
		return dobDay;
	}

	public void setDobDay(String dobDay) {
		this.dobDay = dobDay;
	}

	public String getDobMonth() {
		return dobMonth;
	}

	public void setDobMonth(String dobMonth) {
		this.dobMonth = dobMonth;
	}

	public String getDobYear() {
		return dobYear;
	}

	public void setDobYear(String dobYear) {
		this.dobYear = dobYear;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourseID(String resourceID) {
		this.resourceID = resourceID;
	}

	/*public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}*/
	
}
