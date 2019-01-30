package com.edu.flinnt.util;

import android.os.Environment;

/**
 * Configuration class for media files
 */
public interface MyConfig {
    /*
     * dipal 264915543676157 70009cc959aea02ab72396cc2bc5b1ad
	 */
    // CONSTANTS

    String UserID = "";

    String GOOGLE_SENDER_ID = "855094780778";// "530120136543"; //
    int ListItemLength = 15;
    /**
     * Tag used on log messages.
     */
    String TAG = "GCM Android Example";
    String TrackingId = "UA-46829088-2";
    String DISPLAY_MESSAGE_ACTION = "com.edu.flinnt.DISPLAY_MESSAGE";

    String EXTRA_MESSAGE = "msg";
    int LogLevel = 90;

/*    String PHPVER = "1.0.3";
    String domainname = "http://www.flinnt.com";
    String subdomainname = "/mobile_v3/";
    String  flinnt_imagepath="http://www.flinnt.com/resources/";
*/

    String PHPVER = "1.0.3";
    String domainname = "http://www.flinnt.com";
    String subdomainname = "/mobile_v3/";
    //String  flinnt_imagepath="http://www.flinnt.com/resources/";

    String flinnt_imagepath = "http://flinnt.s3.amazonaws.com/";
    // String subdomainname = "https://flinnt1.s3.amazonaws.com";
   /*

    String PHPVER = "1.0.4";
    String domainname = "http://devtest.flinnt.com";
    String subdomainname = "/mobile_v3/";
    String  flinnt_imagepath="http://flinnt1.s3.amazonaws.com/";
*/


    String mtype = "1";
    String format = "format";

    public static String FLINNT_FOLDER_PATH = Environment.getExternalStorageDirectory()+"/Flinnt/Media/";
    public static String FLINNT_FOLDER_HIDDEN_PATH = Environment.getExternalStorageDirectory()+"/Flinnt/.shared/";
    public static String FLINNT_FOLDER = "/Flinnt/Media/";
  //  String IMAGE_EXTRA_CHAR1 = "IMG";
    String IMAGE_FULL = "Flinnt Images";
    String SMALLGALLERY = ".gallery";
    String AUDIO = "Flinnt Audio";
    String VIDEO = "Flinnt Video";
    String DOCUMENT = "Flinnt Docs";
    String UPLOAD = ".upload";
    String NOMEDIA = ".temp";
    String IMAGE_CROPPED = ".cropped";

    String ErrIntMsg = "No Connection ";


    int POSTDETAILS_VIEW = 1;
    int POSTDETAILS_COMMENT = 2;
    int POSTDETAILS_LIKE = 3;
    int POSTDETAILS = 0;

    int const_mycourse = 1;
    int const_popularcourse = 222;
    int const_joincourse = 2;
    int const_request = 3;
    int const_FAQ = 4;
    //public final int const_DeleteMedia = 5;
    int const_ContactUs = 5;

    //public final int const_refresh = 7;

    int const_Logout = 6;

    // CCAvenue
    String MERCHANT_ID = "6645";

    // CCAvenue Testing mode
    String ACCESS_CODE = "AVTJ00DI52AS11JTSA";
    String RSA_KEY_URL = "https://devtest.flinnt.com/ccAvenue/GetRSA.php";
    String REDIRECT_URL = "https://devtest.flinnt.com/app/payack/?android";
    String CANCEL_URL = "https://devtest.flinnt.com/app/payack/?android";
    String TRANS_URL = "https://test.ccavenue.com/transaction/initTrans";

    // CCAvenue Live mode
    /*String ACCESS_CODE = "AVTI66DI87AO77ITOA";
    String RSA_KEY_URL = "https://devtest.flinnt.com/ccAvenuelive/GetRSA.php";
    String REDIRECT_URL = "https://devtest.flinnt.com/app/payack/?android";
    String CANCEL_URL = "https://devtest.flinnt.com/app/payack/?android";
    String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";*/
}
