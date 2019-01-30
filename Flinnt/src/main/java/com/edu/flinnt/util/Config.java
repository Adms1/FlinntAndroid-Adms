package com.edu.flinnt.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.edu.flinnt.FlinntApplication;

/**
 * this class is for managing app configuration
 */
public class Config {
	
	public static String PREFERENCE_NAME 								= "FLINNT";
	
	public static final String UNKNOWN 									= "unknown";
	public static final String USER_ID 									= "userid";
	public static final String USER_LOGIN 								= "userlogin";
	public static final String FIRST_NAME					 			= "firstname";
	public static final String DATE_OF_BIRTH					 			= "dateofbirth";
	public static final String LAST_NAME 								= "lastname";
	public static final String PASSWORD 								= "password";
	public static final String PROFILE_NAME 								= "profile_name";
	public static final String PROFILE_URL 								= "profile_url";
	public static final String CAT_ID 									= "category_id";
	public static final String DATE 									= "date";
	public static final String ACCOUNT_VERIFIED 						= "acc_verified";
	public static final String ACCOUNT_AUTH_MODE 						= "acc_auth_mode";

	// Course Module 
	public static final String COURSE_PICTURE_URL 						= "course_picture_url";
	public static final String USER_PICTURE_URL 						= "user_picture_url";
	public static final String COURSE_USER_PICTURE_URL 					= "course_user_picture_url";
	
	// Post Module
	public static final String ALBUM_URL	 							= "album_url";
	public static final String GALLERY_URL	 							= "gallery_url";
	public static final String AUDIO_URL	 							= "audio_url";
	public static final String VIDEO_URL	 							= "video_url";
	public static final String DOC_URL	 								= "doc_url";
	
	// for GCM
	public static final String GCM_TOKEN								= "gcmToken";
    public static final String TOKEN_SENT_TO_SERVER 					= "TokenSentToServer";
	// for FCM
	public static final String FCM_TOKEN								= "fcmToken";
	public static final String FCM_TOKEN_SENT_TO_SERVER 				= "FCMTokenSentToServer";
	//User location
	public static final String USER_LOCATION_LATITUDE					="latitude";
	public static final String USER_LOCATION_LONGITUDE					="longitude";
	public static final String DISTANCE_VARIATION_THRESHOLD             = "distance_variation_threshold";

	//Response
	public static final String LAST_LOGIN_RESPONSE 						= "LastLoginRasponse";
	public static final String LAST_SIGNUP_RESPONSE 					= "LastSignUpRasponse";
	public static final String LAST_VERIFYMOBILE_RESPONSE 				= "LastverifymobileRasponse";
	public static final String LAST_MENU_BANNER_POST_RESPONSE 			= "LastMenuBannerPostRasponse";
	//public static final String LAST_HIGHLIGHTS_RESPONSE 				= "LastHighlightsRasponse";
	public static final String LAST_APP_UPDATE_RESPONSE 		= "LastAppUpdateResponse";
	
	//extra
	public static final String LAST_APP_UPDATE_REQUEST_SEND_TIME 		= "LastAppUpdateRequestSendTime";
	public static final String FLINNT_VERSION_NAME 		= "flinntVersionName";
	public static final String FLINNT_VERSION_CODE 		= "flinntVersionCode";
	public static final String FLINNT_FEATURES_STATS 		= "flinntFeaturesStatus";
	public static final String FLINNT_TOOLTIP_MACC_STATS 		= "flinntTooltipMAccStatus:";
	public static final String FLINNT_TOOLTIP_BCOURSE_STATS 		= "flinntTooltipBCourseStatus:";
	public static final String FLINNT_TOOLTIP_EDIT_CONTENT     = "flinntTooltipEditContent:";
	public static final String FLINNT_SNACKBAR_GENDER_DOB 		= "flinntsGenderDobStatus:";
	public static final String GALLERY_SCAN 					= "gallery_scan";
	public static final String IS_BROWSECOURSE 					= "open_browsecourse";
	public static final String EDIT_CONTENT_PROMPT_DIALOG					= "open_prompt";
	
	// Validation
	public static final String LAST_RESOURCE_VALIDATION_RESPONSE 				= "LastValidationResponse";
	public static final String LAST_RESOURCE_VALIDATION_COURCE_RESPONSE 		= "LastValidationCourceResponse";
	public static final String LAST_RESOURCE_VALIDATION_PROFILE_RESPONSE 		= "LastValidationProfileResponse";
	public static final String LAST_RESOURCE_VALIDATION_BANNER_RESPONSE 		= "LastValidationBannerResponse";

	// Institution list
	public static final String INSTITUTE_USER_PICTURE_URL 						= "user_picture_url";
	
	//public static final String LAST_ACCOUNT_MUTE_SETTING_RESPONSE 				= "LastAccountMuteSettingResponse";
	//public static final String LAST_ACCOUNT_COURSE_SETTING_RESPONSE 			= "LastAccountCourseSettingResponse";

	public static final String IS_ALLOWED_TO_CLEAR_WEB_VIEW_SESSION = "isAllowedToClearWebViewSession";//@Chirag:20/08/2018
	
	public static boolean getBoolValue(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		//Log.v("Get ==>", key +":" +preferences.getBoolean(key, false));
		return preferences.getBoolean(key, Boolean.parseBoolean(ProfileMap.getDefaultValue(key)));
	}
	
	public static boolean getBoolValue(String key, boolean def)
	{
		boolean ret = def;
		try {
			SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
			//Log.v("Get ==>", key +":" +preferences.getBoolean(key, def));
			ret =  preferences.getBoolean(key, def);
		} catch (Exception e) {
			ret = def;
		}
		return ret;
	}
	
	public static void setBoolValue(String key, boolean value)
	{		
		try{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		//Log.v("Set ==>", key +":" +value);
		preferences.edit().putBoolean(key, value).commit();
		}catch(Exception e)
		{
			if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.err(e);
		}
	}
	
	public static String getStringValue(String key, String defaultValue)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		//if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.debug(key +":" +preferences.getString(key, defaultValue));
		return preferences.getString(key, ProfileMap.getDefaultValue(key));
	}
	
	public static String getStringValue(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		//if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.debug(key +":" +preferences.getString(key, ""));
		return preferences.getString(key, ProfileMap.getDefaultValue(key));
	}
	
	public static int getIntValue(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getInt(key, Integer.parseInt(ProfileMap.getDefaultValue(key)));
	}
	
	public static void setIntValue(String key, int value)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putInt(key, value).commit();
	}
	
	public static long getLongValue(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getLong(key, Long.parseLong(ProfileMap.getDefaultValue(key)));
	}
	
	public static void setLongValue(String key, long value)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putLong(key, value).commit();
	}
	
	public static void setStringValue(String key, String value)
	{
		//if(LogWriter.isValidLevel(Log.INFO)) LogWriter.info("Set Value:Key:" + key + "[" + value+"]" );
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	public static void setToolTipValue(String key, String value){
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	public static String getToolTipValue(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, ProfileMap.getDefaultValue(key));
	}

	public static void setGenderOrDobSnakbar(String key, String value){
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	public static String getGenderOrDobSnakbar(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, ProfileMap.getDefaultValue(key));
	}

	//Chirag:20/08/2018 For handle session of webview //********start
	public static void setIsAllowedToClearSession(String key, boolean value){
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(key, value).commit();
	}

	public static boolean isAllowedToClearSession(String key)
	{
		SharedPreferences preferences =  FlinntApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}
	//*********end for chirag
}
