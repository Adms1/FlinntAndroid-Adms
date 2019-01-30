package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;
/*
 * 
 Request
 	user_id:

 Response 
{
  "status": 1,
  "data": {
    "profile": [
      {
        "first_name": "Mehul",
        "last_name": "Domadiya",
        "institute_name": "",
        "gender": "Male",
        "birth_day": "7",
        "birth_month": "7",
        "birth_year": "1992",
        "city": "Ahmedabad",
        "email": "flinntme@mailinator.com",
        "mobile": "1234567890"
      }
    ]
  }
}
account/profile/get/

*/

public class Profile {
	
	public static final String FIRST_NAME_KEY 						= "first_name";
	public static final String LAST_NAME_KEY						= "last_name";
	public static final String INSTITUTE_NAME_KEY 					= "institute_name";
	public static final String GENDER_KEY							= "gender";
	public static final String BIRTH_DAY_KEY 						= "birth_day";
	public static final String BIRTH_MONTH_KEY 						= "birth_month";
	public static final String BIRTH_YEAR_KEY 						= "birth_year";
	public static final String CITY_KEY 							= "city";
	public static final String EMAIL_KEY 							= "email";
	public static final String MOBILE_KEY 							= "mobile";
	
	private String firstName 										= "" ;
	private String lastName 										= "" ;
	private String instituteName 									= "" ;
	private String gender											= "" ;
	private String birthDay 										= "" ;
	private String birthMonth										= "" ;
	private String birthYear										= "" ;
	private String city 											= "" ;
	private String email 											= "" ;
	private String mobile 											= "" ;

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			if(jObject.has(FIRST_NAME_KEY)) setFirstName( jObject.getString(FIRST_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

		try {
			if(jObject.has(LAST_NAME_KEY)) setLastName( jObject.getString(LAST_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(INSTITUTE_NAME_KEY)) setInstituteName( jObject.getString(INSTITUTE_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(GENDER_KEY)) setGender( jObject.getString(GENDER_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(BIRTH_DAY_KEY)) setBirthDay( jObject.getString(BIRTH_DAY_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(BIRTH_MONTH_KEY)) setBirthMonth( jObject.getString(BIRTH_MONTH_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(BIRTH_YEAR_KEY)) setBirthYear( jObject.getString(BIRTH_YEAR_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(CITY_KEY)) setCity( jObject.getString(CITY_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(EMAIL_KEY)) setEmail( jObject.getString(EMAIL_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		
		try {
			if(jObject.has(MOBILE_KEY)) setMobile( jObject.getString(MOBILE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
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


	public String getBirthDay() {
		return birthDay;
	}


	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}


	public String getBirthMonth() {
		return birthMonth;
	}


	public void setBirthMonth(String birthMonth) {
		this.birthMonth = birthMonth;
	}


	public String getBirthYear() {
		return birthYear;
	}


	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
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


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("firstName : " + firstName)
		.append(" , lastName : " + lastName)
		.append(" , instituteName : " + instituteName)
		.append(" , gender : " + gender)
		.append(" , birthDay : " + birthDay)
		.append(" , birthMonth : " + birthMonth)
		.append(" , birthDay : " + birthDay)
		.append(" , city : " + city)
		.append(" , email : " + email)
		.append(" , mobile : " + mobile);
		return strBuffer.toString();
	}
}