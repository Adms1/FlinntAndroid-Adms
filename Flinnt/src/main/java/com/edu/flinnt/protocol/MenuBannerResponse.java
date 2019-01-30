package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * class to parse response to object
 */
public class MenuBannerResponse extends BaseResponse {

	public static final String ADD_MENU_KEY = "add_menu";
	public static final String CAN_CREATE_COURSE_KEY = "can_create_course";
	public static final String CAN_ADD_POST_KEY = "can_add_post";
	public static final String CAN_ADD_QUIZ_KEY = "can_add_quiz";
	public static final String CAN_ADD_ALBUM_KEY = "can_add_album";
	public static final String CAN_ADD_MESSAGE_KEY = "can_add_message";
	public static final String CAN_ADD_ALERT_KEY = "can_add_alert";
	public static final String CAN_EDIT_CONTENT_KEY = "can_edit_content";
	public static final String CAN_COMMUNICATION_KEY = "can_add_communication";
	public static final String CAN_CHANGE_BANNER_KEY = "can_change_banner";
	public static final String CAN_SEND_JOIN_KEY = "can_send_join_request";
	public static final String CAN_ADD_POLL = "can_add_poll";

	public static final String BANNERS_KEY = "banners";
	public static final String BANNERS_PATH_KEY = "banner_path";
	public static final String ACCOUNT_BANNER_KEY = "account_banner";

    public static final String CAN_BROWSE_COURSE_KEY = "can_browse_course";


    public int canCreateCourse = Flinnt.INVALID;
	public int canAddPost = Flinnt.INVALID;
	public int canAddQuiz = Flinnt.INVALID;
	public int canAddAlbum = Flinnt.INVALID;
	public int canAddMessage = Flinnt.INVALID;
	public int canAddAlert = Flinnt.INVALID;
	public int canEditContent = Flinnt.INVALID;
	public int canAddCommunication = Flinnt.INVALID;
	public int canChangeBanner = Flinnt.INVALID;
	public int canSendJoinRequest = Flinnt.INVALID;
    public int canBrowseCourse = Flinnt.INVALID;
	public int canAddPoll = Flinnt.INVALID;

    public String bannerPath = "";
	public String accountBanner = "";
	public ArrayList<String> bannersList = new ArrayList<String>();

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    synchronized public void parseJSONString(String jsonData) {

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
    synchronized public void parseJSONObject(JSONObject jsonData) {

		try {
			
			JSONObject jsonAddMenu = jsonData.getJSONObject(ADD_MENU_KEY);

			try {
				setCanCreateCourse(jsonAddMenu.getInt(CAN_CREATE_COURSE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddAlbum(jsonAddMenu.getInt(CAN_ADD_ALBUM_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddAlert(jsonAddMenu.getInt(CAN_ADD_ALERT_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanEditContent(jsonAddMenu.getInt(CAN_EDIT_CONTENT_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddMessage(jsonAddMenu.getInt(CAN_ADD_MESSAGE_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddCommunication(jsonAddMenu.getInt(CAN_COMMUNICATION_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}
			try {
				setCanAddPost(jsonAddMenu.getInt(CAN_ADD_POST_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddQuiz(jsonAddMenu.getInt(CAN_ADD_QUIZ_KEY));
			} catch (Exception e) {
				LogWriter.err(e);
			}

			try {
				setCanAddPoll(jsonAddMenu.getInt(CAN_ADD_POLL));
			} catch (Exception e) {
				LogWriter.err(e);
			}


		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			JSONArray bannersJsonArray = jsonData.getJSONArray(BANNERS_KEY);
			clearBannersList();
			for (int i = 0; i < bannersJsonArray.length(); i++) {
				bannersList.add( bannersJsonArray.getString(i) );
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setBannerPath(jsonData.getString(BANNERS_PATH_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setAccountBanner(jsonData.getString(ACCOUNT_BANNER_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			setCanChangeBanner(jsonData.getInt(CAN_CHANGE_BANNER_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			setCanSendJoinRequest(jsonData.getInt(CAN_SEND_JOIN_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}


        try {
            if ( jsonData.has(CAN_BROWSE_COURSE_KEY)) setCanBrowseCourse(jsonData.getInt(CAN_BROWSE_COURSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
	}

	public int getCanCreateCourse() {
		return canCreateCourse;
	}

	public void setCanCreateCourse(int canCreateCourse) {
		this.canCreateCourse = canCreateCourse;
	}

	public int getCanAddPost() {
		return canAddPost;
	}

	public void setCanAddPost(int canAddPost) {
		this.canAddPost = canAddPost;
	}

	public int getCanAddQuiz() {
		return canAddQuiz;
	}

	public void setCanAddQuiz(int canAddQuiz) {
		this.canAddQuiz = canAddQuiz;
	}

	public int getCanAddAlbum() {
		return canAddAlbum;
	}

	public void setCanAddAlbum(int canAddAlbum) {
		this.canAddAlbum = canAddAlbum;
	}

	public int getCanAddMessage() {
		return canAddMessage;
	}

	public void setCanAddMessage(int canAddMessage) {
		this.canAddMessage = canAddMessage;
	}

	public int getCanAddAlert() {
		return canAddAlert;
	}

	public void setCanAddAlert(int canAddAlert) {
		this.canAddAlert = canAddAlert;
	}

	public int getCanEditContent() {
		return canEditContent;
	}

	public void setCanEditContent(int canEditContent) {
		this.canEditContent = canEditContent;
	}

	public String getBannerPath() {
		return bannerPath;
	}

	public void setBannerPath(String bannerPath) {
		this.bannerPath = bannerPath;
	}

	public String getAccountBanner() {
		return accountBanner;
	}

	public void setAccountBanner(String accountBanner) {
		this.accountBanner = accountBanner;
	}

	public ArrayList<String> getBannersList() {
		return bannersList;
	}

	public void setBannersList(ArrayList<String> bannersList) {
		this.bannersList = bannersList;
	}
	
	public void clearBannersList() {
		this.bannersList.clear();
	}
	
	public int getCanChangeBanner() {
		return canChangeBanner;
	}

	public void setCanChangeBanner(int canChangeBanner) {
		this.canChangeBanner = canChangeBanner;
	}

	public int getCanSendJoinRequest() {
		return canSendJoinRequest;
	}

	public void setCanSendJoinRequest(int canSendJoinRequest) {
		this.canSendJoinRequest = canSendJoinRequest;
	}

	public int getCanBrowseCourse() {
        return canBrowseCourse;
    }

    public void setCanBrowseCourse(int canBrowseCourse) {
        this.canBrowseCourse = canBrowseCourse;
    }

	public int getCanAddCommunication() {
		return canAddCommunication;
	}

	public void setCanAddCommunication(int canAddCommunication) {
		this.canAddCommunication = canAddCommunication;
	}

	public int getCanAddPoll() {
		return canAddPoll;
	}

	public void setCanAddPoll(int canAddPoll) {
		this.canAddPoll = canAddPoll;
	}


	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("canCreateCourse : " + canCreateCourse)
		.append(", canAddPost : " + canAddPost)
		.append(", canAddQuiz : " + canAddQuiz)
		.append(", canAddAlbum : " + canAddAlbum)
		.append(", canAddMessage : " + canAddMessage)
		.append(", canAddAlert : " + canAddAlert)
		.append(", canEditContent : " + canEditContent)
		.append(", canAddCommunication : " + canAddCommunication)
		.append(", bannerPath : " + bannerPath)
		.append(", accountBanner : " + accountBanner)
		.append(", canBrowseCourse : " + canBrowseCourse)
        .append(", banners : " + bannersList);
		return strBuffer.toString();
	}

}
