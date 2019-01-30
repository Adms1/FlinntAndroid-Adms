package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
   "status": 1,
   "data":
   {
       "user_id": "1",
       "user_login": "urvish@synapsesoftech.com",
       "firstname": "Urvish",
       "lastname": "Patel",
       "user_is_active": "1",
       "user_picture": "1_1434439983.jpg",
       "user_acc_verified": "1",
       "user_acc_auth_mode": "email",
       "can_add": 1,
       "user_picture_url": "https://flinnt1.s3.amazonaws.com/profile_image/"
   }
}

*/

/**
 * class to parse response to object
 */
public class LoginResponse extends BaseResponse {

	public static final String USER_ID_KEY = "user_id";
	public static final String USER_LOGIN_KEY = "user_login";
	public static final String FIRST_NAME_KEY = "firstname";
	public static final String LAST_NAME_KEY = "lastname";
	public static final String USER_IS_ACTIVE_KEY = "user_is_active";
	public static final String USER_PICTURE_KEY = "user_picture";
	//public static final String IMAGE_PATH_KEY = "image_path";
	public static final String USER_ACC_VERIFIED_KEY = "user_acc_verified";
	public static final String USER_ACC_AUTH_MODE_KEY = "user_acc_auth_mode";
	public static final String CAN_ADD_KEY = "can_add";
	public static final String USER_PICTURE_URL_KEY = "user_picture_url";
    public static final String CAN_BROWSE_COURSE = "can_browse_course";
    public static final String CATEGORY_SUBCRIBED = "category_subscribed";

	public static final String USER_GENDER = "user_gender";
	public static final String USER_BIRTH_DAY = "birth_day";


	public String userID = "";
	public String userLogin = "";
	public String firstName = "";
	public String lastName = "";
	public String isActive = "";
	public String userPicture = "";
	//public String imagePath = "";
	public String accVerified = "";
	public String accAuthMode = "";
	public int canAdd = Flinnt.INVALID;
	public String userPictureUrl = "";
    public int canBrowseCourse = Flinnt.INVALID;
	public String userGender = "";
	public String userBirthDay = "";

	public int getCategorySubscribed() {
		return categorySubscribed;
	}

	public void setCategorySubscribed(int categorySubscribed) {
		this.categorySubscribed = categorySubscribed;
	}

	public int categorySubscribed = 0;


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
			setUserID(jsonData.getString(USER_ID_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setUserLogin(jsonData.getString(USER_LOGIN_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setFirstName(jsonData.getString(FIRST_NAME_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setLastName(jsonData.getString(LAST_NAME_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setIsActive(jsonData.getString(USER_IS_ACTIVE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(USER_PICTURE_KEY)) {
				setUserPicture(jsonData.getString(USER_PICTURE_KEY));
				//Config.setStringValue(Config.PROFILE_NAME, jsonData.getString(USER_PICTURE_KEY));
			}
			
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setAccVerified(jsonData.getString(USER_ACC_VERIFIED_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setAccAuthMode(jsonData.getString(USER_ACC_AUTH_MODE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(CAN_ADD_KEY)) setCanAdd(jsonData.getInt(CAN_ADD_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(USER_PICTURE_URL_KEY)) {
				setUserPictureUrl(jsonData.getString(USER_PICTURE_URL_KEY));
				//Config.setStringValue(Config.PROFILE_URL, jsonData.getString(USER_PICTURE_URL_KEY));
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

        try {
            if(jsonData.has(CAN_BROWSE_COURSE)) setCanBrowseCourse(jsonData.getInt(CAN_BROWSE_COURSE));
        } catch (Exception e) {
            LogWriter.err(e);
        }

		try {
			if(jsonData.has(USER_GENDER)) setUserGender(jsonData.getString(USER_GENDER));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(USER_BIRTH_DAY)) setUserBirthDay(jsonData.getString(USER_BIRTH_DAY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		try {
			if(jsonData.has(CATEGORY_SUBCRIBED)) setCategorySubscribed(jsonData.getInt(CATEGORY_SUBCRIBED));
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserBirthDay() {
		return userBirthDay;
	}

	public void setUserBirthDay(String userBirthDay) {
		this.userBirthDay = userBirthDay;
	}

	public String getAccVerified() {
		return accVerified;
	}

	public void setAccVerified(String accVerified) {
		this.accVerified = accVerified;
	}

	public String getAccAuthMode() {
		return accAuthMode;
	}

	public void setAccAuthMode(String accAuthMode) {
		this.accAuthMode = accAuthMode;
	}

	public int getCanAdd() {
		return canAdd;
	}

	public void setCanAdd(int canAdd) {
		this.canAdd = canAdd;
	}

	public String getUserPictureUrl() {
		return userPictureUrl;
	}

	public void setUserPictureUrl(String userPictureUrl) {
		this.userPictureUrl = userPictureUrl;
	}
    public int getCanBrowseCourse() {
        return canBrowseCourse;
    }

    public void setCanBrowseCourse(int canBrowseCourse) {
        this.canBrowseCourse = canBrowseCourse;
    }

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("userID : " + userID)
		.append(", userLogin : " + userLogin)
		.append(", firstName : " + firstName)
		.append(", lastName : " + lastName)
		.append(", isActive : " + isActive)
		.append(", userPicture : " + userPicture)
		//.append(", imagePath : " + imagePath)
		.append(", accVerified : " + accVerified)
		.append(", accAuthMode : " + accAuthMode)
		.append(", canAdd : " + canAdd)
				.append(", userGender : " + userGender)
				.append(", userBirthDay : " + userBirthDay)
                .append(", canBrowseCourse : " + canBrowseCourse)
                .append(", userPictureUrl : " + userPictureUrl);
		return strBuffer.toString();
	}
}
