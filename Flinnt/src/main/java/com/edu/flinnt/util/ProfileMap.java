package com.edu.flinnt.util;

import android.util.Log;

import java.util.HashMap;

/**
 * Configuration Structure class for getting profile configurations
 */
public class ProfileMap {
	static ProfileMap profilemap = new ProfileMap();
	HashMap<String, ConfigStruct> hmProfileList = new HashMap<String, ConfigStruct>();
	
	public class ConfigStruct {
		public String name;
		public String defaultValue;
		public boolean restartRequired;
		
		public ConfigStruct(String name, String defaultValue, boolean restartRequired) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.restartRequired = restartRequired;
		}
	}
	
	static public ProfileMap getObj() {
		return profilemap;
	}
	
	private ProfileMap() {
		hmProfileList.put(Config.UNKNOWN,new ConfigStruct(Config.UNKNOWN,"",false));
		hmProfileList.put(Config.USER_ID,new ConfigStruct(Config.USER_ID,"",false));
		hmProfileList.put(Config.USER_LOGIN,new ConfigStruct(Config.USER_LOGIN,"",false));
		hmProfileList.put(Config.FIRST_NAME,new ConfigStruct(Config.FIRST_NAME,"",false));
		hmProfileList.put(Config.LAST_NAME,new ConfigStruct(Config.LAST_NAME,"",false));
		hmProfileList.put(Config.PASSWORD,new ConfigStruct(Config.PASSWORD,"",false));
		hmProfileList.put(Config.PROFILE_NAME,new ConfigStruct(Config.PROFILE_NAME,"",false));
		hmProfileList.put(Config.PROFILE_URL,new ConfigStruct(Config.PROFILE_URL,"",false));
		hmProfileList.put(Config.ACCOUNT_VERIFIED,new ConfigStruct(Config.ACCOUNT_VERIFIED,"",false));
		hmProfileList.put(Config.ACCOUNT_AUTH_MODE,new ConfigStruct(Config.ACCOUNT_AUTH_MODE,"",false));
//		hmProfileList.put(Config.GCM_TOKEN,new ConfigStruct(Config.GCM_TOKEN,"",false));
		hmProfileList.put(Config.FCM_TOKEN,new ConfigStruct(Config.FCM_TOKEN,"",false));
		hmProfileList.put(Config.TOKEN_SENT_TO_SERVER,new ConfigStruct(Config.TOKEN_SENT_TO_SERVER,"false",false));
		hmProfileList.put(Config.COURSE_PICTURE_URL,new ConfigStruct(Config.COURSE_PICTURE_URL,"",false));
		hmProfileList.put(Config.USER_PICTURE_URL,new ConfigStruct(Config.USER_PICTURE_URL,"",false));
		hmProfileList.put(Config.ALBUM_URL,new ConfigStruct(Config.ALBUM_URL,"",false));
		hmProfileList.put(Config.GALLERY_URL,new ConfigStruct(Config.GALLERY_URL,"",false));
		hmProfileList.put(Config.AUDIO_URL,new ConfigStruct(Config.AUDIO_URL,"",false));
		hmProfileList.put(Config.VIDEO_URL,new ConfigStruct(Config.VIDEO_URL,"",false));
		hmProfileList.put(Config.DOC_URL,new ConfigStruct(Config.DOC_URL,"",false));
		hmProfileList.put(Config.LAST_APP_UPDATE_REQUEST_SEND_TIME,new ConfigStruct(Config.LAST_APP_UPDATE_REQUEST_SEND_TIME,"0",false));
		hmProfileList.put(Config.FLINNT_VERSION_NAME,new ConfigStruct(Config.FLINNT_VERSION_NAME,"",false));
		hmProfileList.put(Config.FLINNT_FEATURES_STATS,new ConfigStruct(Config.FLINNT_FEATURES_STATS,"0",false));
		hmProfileList.put(Config.FLINNT_TOOLTIP_MACC_STATS,new ConfigStruct(Config.FLINNT_TOOLTIP_MACC_STATS,"0",false));
		hmProfileList.put(Config.FLINNT_TOOLTIP_BCOURSE_STATS,new ConfigStruct(Config.FLINNT_TOOLTIP_BCOURSE_STATS,"0",false));
		hmProfileList.put(Config.FLINNT_SNACKBAR_GENDER_DOB,new ConfigStruct(Config.FLINNT_SNACKBAR_GENDER_DOB,"",false));
		hmProfileList.put(Config.GALLERY_SCAN,new ConfigStruct(Config.GALLERY_SCAN,"false",false));

		// For User Location
		hmProfileList.put(Config.USER_LOCATION_LATITUDE,new ConfigStruct(Config.USER_LOCATION_LATITUDE,null,false));
		hmProfileList.put(Config.USER_LOCATION_LONGITUDE,new ConfigStruct(Config.USER_LOCATION_LONGITUDE,null,false));
		hmProfileList.put(Config.DISTANCE_VARIATION_THRESHOLD,new ConfigStruct(Config.DISTANCE_VARIATION_THRESHOLD,"500",false));

		// Response
		hmProfileList.put( Config.LAST_LOGIN_RESPONSE,new ConfigStruct(Config.LAST_LOGIN_RESPONSE, "", true) );
		hmProfileList.put(Config.LAST_SIGNUP_RESPONSE,new ConfigStruct(Config.LAST_SIGNUP_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_VERIFYMOBILE_RESPONSE,new ConfigStruct(Config.LAST_VERIFYMOBILE_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_MENU_BANNER_POST_RESPONSE,new ConfigStruct(Config.LAST_MENU_BANNER_POST_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_APP_UPDATE_RESPONSE,new ConfigStruct(Config.LAST_APP_UPDATE_RESPONSE,"",true));
		//hmProfileList.put(Config.LAST_HIGHLIGHTS_RESPONSE,new ConfigStruct(Config.LAST_HIGHLIGHTS_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_RESOURCE_VALIDATION_RESPONSE,new ConfigStruct(Config.LAST_RESOURCE_VALIDATION_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_RESOURCE_VALIDATION_COURCE_RESPONSE,new ConfigStruct(Config.LAST_RESOURCE_VALIDATION_COURCE_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE,new ConfigStruct(Config.LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE,"",true));
		hmProfileList.put(Config.LAST_RESOURCE_VALIDATION_BANNER_RESPONSE,new ConfigStruct(Config.LAST_RESOURCE_VALIDATION_BANNER_RESPONSE,"",true));
		//hmProfileList.put(Config.LAST_ACCOUNT_MUTE_SETTING_RESPONSE,new ConfigStruct(Config.LAST_ACCOUNT_MUTE_SETTING_RESPONSE,"",true));
		//hmProfileList.put(Config.LAST_ACCOUNT_COURSE_SETTING_RESPONSE,new ConfigStruct(Config.LAST_ACCOUNT_COURSE_SETTING_RESPONSE,"",true));
	}
	
	public static ConfigStruct getConfig(String key) {
		ConfigStruct configStruct = (ConfigStruct) profilemap.hmProfileList.get(key);
		if(configStruct == null) {
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.info("key " + key + " Not found in ProfileMap. So returning unknown parameter.");
			configStruct = (ConfigStruct) profilemap.hmProfileList.get(Config.UNKNOWN);			
		}
		return configStruct;
	}
		
	public static String getDefaultValue(String key) {
		return getConfig(key).defaultValue;
	}
}
