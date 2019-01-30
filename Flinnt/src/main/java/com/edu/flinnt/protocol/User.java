package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;

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
public class User {

    public String userID = "";
	public String userLogin = "";
	public String firstName = "";
	public String lastName = "";
	public String isActive = "";
	public String userPicture = "";
	public String accVerified = "";
	public String accAuthMode = "";
	public int canAdd = Flinnt.INVALID;
	public String userPictureUrl = "";
    public int canBrowseCourse = Flinnt.INVALID;
    public int tokenSentToServer;
    public int currentUser = Flinnt.FALSE;

    public int getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }

    public int getTokenSentToServer() {
        return tokenSentToServer;
    }

    public void setTokenSentToServer(int tokenSentToServer) {
        this.tokenSentToServer = tokenSentToServer;
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

	/*
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	*/

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
                .append(", canBrowseCourse : " + canBrowseCourse)
                .append(", userPictureUrl : " + userPictureUrl)
        .append(", tokenSentToserver : " + tokenSentToServer);
		return strBuffer.toString();
	}
}
