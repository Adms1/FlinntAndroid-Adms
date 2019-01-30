package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;
import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 26/11/16.
 */

public class PersonalInformationRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String COURSE_ID_KEY = "course_id";
    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String INSTITUTE_NAME_KEY = "institute_name";
    public static final String EDUCATION_KEY = "education";
    public static final String GENDER_KEY = "gender";
    public static final String AGE_KEY = "age";
    public static final String MOBILE_KEY = "mobile_no";
    public static final String EMAIL_KEY = "email_id";
    public static final String CITY_KEY = "city";
    public static final String CATEGORY_KEY = "category_id";


    private String userID = "";
    private String courseID = "";
    private String firstName = "";
    private String lastName = "";
    private String instituteName = "";
    private String gender = "";    //Male/Female
    private String education = "";
    private String age = "";
    private String mobileNo = "";
    private String emailId = "";
    private String city = "";
    private String activityKey = "";
    private String categoryID = "";


    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(COURSE_ID_KEY, courseID);
            returnedJObject.put(FIRST_NAME_KEY, firstName);
            returnedJObject.put(LAST_NAME_KEY, lastName);
            if (activityKey.equals("contact")) {
                returnedJObject.put(MOBILE_KEY, mobileNo);
                returnedJObject.put(EMAIL_KEY, emailId);
                returnedJObject.put(CITY_KEY, city);
                if(!categoryID.equalsIgnoreCase("")){
                    returnedJObject.put(CATEGORY_KEY, categoryID);
                }
            } else {
                returnedJObject.put(INSTITUTE_NAME_KEY, instituteName);
                returnedJObject.put(GENDER_KEY, gender);
                returnedJObject.put(EDUCATION_KEY, education);
                returnedJObject.put(AGE_KEY, age);
            }
        } catch (Exception e) {
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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
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

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}