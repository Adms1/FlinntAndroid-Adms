package com.edu.flinnt;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;

import com.edu.flinnt.downloads.AppInfoDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains basic project information, request base URLs and operation codes which are used in whole project
 */

public class Flinnt {
    public static final String VERSION = "2.0.41.58";//"2.0.24.22"; // same as application version name in manifest
    //Test api
    //public static final String API_URL = "https://apidevtest.flinnt.com/mobile/v2.0/android/";// For testing
    //"https://apilivetest.flinnt.com/mobile/v2.0/android/";
    //"https://apiv2.flinnt.com/mobile/v2.0/android/";
    //"http://apiv1.flinnt.com/mobile/v2.0/android/";
    //	"https://beta-api.flinnt.com/";
    /*API_URL is use for calling API, when we want to release application, we have to assign live URL to API_URL, at the time of development we have to assign TEST_API_URL to API_URL.*/
    public static final String API_URL =  "https://api.flinnt.com/mobile/v2.0/android/"; //Live URL



    //08-01-2019 by vijay

    //public static final String LOCAL_API_URL_NEW =  "http://103.204.192.187:8080/flinnt/public/api/"; //LOCAL URL
   // public static final String LOCAL_API_URL_NEW =  "http://flinnt.admsonline.com/api/"; //LIVE URL
   //Local URL
    public static final String LOCAL_API_URL_NEW =  "http://192.168.1.20:8080/flinnt/public/api/";

    public static final String STORE_BOOK_LIST_API = "getBookList"; //Local URL
    public static final String STORE_BOOK_DETAIL_API = "getBookDetail"; //Local URL
    public static final String STORE_RELATED_BOOKS_API = "getBookListRelatedToBook"; //Local URL
    public static final String STORE_RELATED_VENDOR_API = "getBookListRelatedToBook"; //Local URL
    public static final String ADD_ITEM_TO_CART = "cartStore"; //Local URL
    public static final String CART_ITEM_LIST = "getCartList"; //Local URL
    public static final String REMOVE_CART_ITEM = "cartDestroy"; //Local URL
    public static final String BOOKSET_LIST = "getBooksetList"; //Local URL
    public static final String BOOKSET_LIST_DETAIL = "getBooksetDetail"; //Local URL
    public static final String UPDATE_CART_ITEM = "cartUpdate"; //Local URL
    public static final String STATE_DATA_API = "getStateList"; //Local URL
    public static final String ADD_USER_DATA_API = "addUserAddress"; //Local URL
    public static final String GET_USER_ADDRESS_LIST_API = "getUserAddressList"; //Local URL




     //Web API KEY Fields
    // 08-01-2019 by vijay
    public static final String INST_BOOK_VENDOR_ID  = "institution_book_vendor_id";
    public static final String STANDARD_ID = "standard_id";


    public static final String URL_TERM_AND_CONDITION = "https://www.flinnt.com/app/static/terms/";//"http://www.flinnt.com/static/terms/";
    public static final String URL_FAQ = "https://www.flinnt.com/app/static/faq/";//"http://www.flinnt.com/static/faq/";
    public static final String URL_CONTACT_US = "https://www.flinnt.com/app/contact/";//"http://www.flinnt.com/contact/";

    // Google Project Number
    public static final String GOOGLE_PROJ_ID = "855094780778";
    //	public static final String SECRETKEY_ENCRYPTION_DECRYPTION 		= "FlinntChy1wu0loghithy6";
    public static final String SECRETKEY_ENCRYPTION_DECRYPTION = "3737096688965235";
    public static final String FCM_SENDER_ID = "200667972425";


    public static final String URL_LOGIN = "account/login/";
    public static final String URL_AUTO_LOGIN = "account/autologin/";
    public static final String URL_SIGNUP = "account/signup/";
    public static final String URL_FORGOT_PASSWORD = "account/forgot_password/";
    public static final String URL_VERIFY_MOBILE = "account/verify/mobile/";
    public static final String URL_RESEND_CODE = "account/verify/resend/";
    public static final String URL_VERIFICATION_STATUS = "account/verify/status/";
    public static final String URL_SLIDER_MENU_STATISTICS = "account/statistics/";
    public static final String URL_ALERT_LIST = "account/alerts/list/";
    public static final String URL_ALERT_DETAIL = "account/alert/view/";
    public static final String URL_ALERT_DELETE = "account/alert/delete/";
    public static final String URL_HIGHLIGHT = "account/alert/latest/";
    public static final String URL_COURSE_INVITATION = "account/invitations/list/";
    public static final String URL_COURSE_INVITATION_ACCEPT = "account/invitation/accept/";
    public static final String URL_COURSE_INVITATION_REJECT = "account/invitation/reject/";
    public static final String URL_MY_COURSES = "course/my/";
    public static final String URL_COURSE_MENU_BANNER = "course/my/init/";
    public static final String URL_COURSE_LIST = "course/list/";
    public static final String URL_POST_LIST = "post/list/";
    public static final String URL_POST_LIST_MARGED = "post/list-view/merged/"; //"post/list/merged/";
    public static final String URL_POST_LIST_MENU = "post/list/menu/";
    public static final String URL_POST_LIST_UNREAD_STATISTICS = "post/list/unread/";
    public static final String URL_POST_DELETE = "post/delete/";
    public static final String URL_COMMENT_DELETE = "post/comment/delete/";
    public static final String URL_POST_TAGS_LIST = "post/tags/list/";
    public static final String URL_POST_LIST_BOOKMARK = "post/bookmarks/list/";
    public static final String URL_REGISTER_DEVICE = "device/register/push_notification/";
    public static final String URL_UNREGISTER_DEVICE = "device/unregister/push_notification/";
    public static final String URL_POST_VIEW = "post/view/";
    public static final String URL_POST_STATS = "post/view/statistics/";
    public static final String URL_POST_COMMENTS_LIST = "post/comments/list/";
    public static final String URL_POST_COMMENTS_ADD = "post/comment/add/";
    public static final String URL_POST_VIEWERS = "post/viewers/";
    public static final String URL_POST_BLOG_ADD = "post/blog/add/";
    public static final String URL_POST_BLOG_EDIT = "post/blog/edit/";
    public static final String URL_POST_BLOG_REPOST = "post/blog/repost/";
    public static final String URL_POST_QUIZ_ADD = "post/quiz/add/";
    public static final String URL_POST_QUIZ_EDIT = "post/quiz/edit/";
    public static final String URL_POST_QUIZ_REPOST = "post/quiz/repost/";
    public static final String URL_POST_MESSAGE_ADD = "post/message/add/";
    public static final String URL_POST_MESSAGE_EDIT = "post/message/edit/";
    public static final String URL_POST_ALBUM_ADD = "post/album/add/";
    public static final String URL_POST_ALBUM_EDIT = "post/album/edit/";
    public static final String URL_POST_ALBUM_REPOST = "post/album/repost/";
    public static final String URL_ALERT_ADD = "account/alert/add/";
    public static final String URL_COURSE_ADD = "course/add/";
    public static final String URL_COURSE_EDIT = "course/edit/";
    public static final String URL_QUIZ_ANSWER = "post/quiz/answer/";
    public static final String URL_JOIN_COURSE = "course/join/";
    public static final String URL_COURSE_MESSAGE_ROLE = "course/message/roles/";
    public static final String URL_COURSE_USERS_LIST = "course/users/list/";
    public static final String URL_BANNER_ADD = "account/banner/change/";
    public static final String URL_POLL_ADD = "post/communication/poll/add/";
    public static final String URL_POLL_EDIT = "post/communication/poll/edit/";
    public static final String URL_POLL_REPOST = "post/communication/poll/repost/";

    public static final String URL_RESOURCE_VALIDATION = "resource/validations/";
    public static final String URL_VALIDATION_COURSE_PICTURE = "resource/validations/course/picture/";
    public static final String URL_VALIDATION_PROFILE_PICTURE = "resource/validations/profile/picture/";
    public static final String URL_VALIDATION_BANNER_PICTURE = "resource/validations/account/banner/";

    public static final String URL_RESOURCE_UPLOAD = "resource/upload/";

    public static final String URL_NOTIFICATION_GET = "device/notifications/config/get/";
    public static final String URL_NOTIFICATION_SET = "device/notifications/config/set/";

    public static final String URL_COURSE_SETTING_GET = "course/settings/get/";
    public static final String URL_COURSE_SETTING_SET = "course/settings/set/";

    public static final String URL_PROFILE_UPDATE = "account/profile/update/";
    public static final String URL_PROFILE_MOBILE_VERIFY = "account/profile/mobile/verify/";
    public static final String URL_PROFILE_MOBILE_RESEND_CODE = "account/profile/mobile/resendcode/";
    public static final String URL_PROFILE_EMAIL_RESEND_LINK = "account/profile/email/resendlink/";
    public static final String URL_CHANGE_PASSWORD = "account/password/change/";

    public static final String URL_COURSE_COMMENT_ALLOW = "course/permissions/comment/allow/";
    public static final String URL_COURSE_COMMENT_DISALLOW = "course/permissions/comment/disallow/";
    public static final String URL_COURSE_MESSAGE_ALLOW = "course/permissions/message/allow/";
    public static final String URL_COURSE_MESSAGE_DISALLOW = "course/permissions/message/disallow/";
    public static final String URL_COURSE_REMOVE = "course/subscriptions/remove/";

    public static final String URL_COURSE_INVITATION_SEND = "course/invitation/send/";

    public static final String URL_POST_TEMPLATES_LIST = "post/templates/list/";

    public static final String URL_POST_BOOKMARK_ADD = "post/bookmark/add/";
    public static final String URL_POST_BOOKMARK_REMOVE = "post/bookmark/remove/";
    public static final String URL_POST_LIKE = "post/like/";
    public static final String URL_POST_DISLIKE = "post/dislike/";

    public static final String URL_ACCOUNT_PROFILE_GET = "account/profile/get/";
    public static final String URL_DEVICE_APP_UPDATE = "device/app/update/";

    public static final String URL_ACCOUNT_ALERT_EDIT = "account/alert/edit/";

    public static final String URL_COURSE_COMMUNITY_LIST = "course/community/list/";
    public static final String URL_COURSE_COMMUNITY_JOIN = "course/community/join/";

    public static final String URL_COURSE_BROWSE = "course/browse/";
    public static final String URL_PROMO_COURSE = "course/promotion/list/";
    public static final String URL_COURSE_VIEW = "course/view/";
    public static final String URL_COURSE_JOIN_PUBLIC = "course/join/public/";
    public static final String URL_COURSE_REVIEWS_LIST = "course/reviews/list/";
    public static final String URL_COURSE_REVIEW_WRITE = "course/review/write/";
    public static final String URL_COURSE_REVIEW_READ = "course/review/get/";
    public static final String URL_COURSE_VIEW_ALSO_JOINED = "course/view/also/joined/";
    public static final String URL_COURSE_VIEW_ALSO_MORE = "course/view/also/more/";
    public static final String URL_COURSE_SUBSCRIPTIONS_ROMOVE_SELF = "course/subscriptions/remove/self/";
    public static final String URL_COURSE_WISHLIST_ADD = "course/wishlist/add/";
    public static final String URL_COURSE_WISHLIST_REMOVE = "course/wishlist/remove/";
    public static final String URL_WISHLIST = "course/wishlist/list/";
    public static final String URL_CONTENTS_LIST = "lms/list/";
    public static final String URL_CONTENTS_EDIT_LIST = "lms/list/edit/";
    public static final String URL_CONTENTS_UPDATE_VISIBILITY = "lms/content/update/visibility/";
    public static final String URL_CONTENTS_DELETE = "lms/content/delete/";
    public static final String URL_SECTION_UPDATE_VISIBILITY = "lms/section/update/visibility/";
    public static final String URL_SECTION_DELETE = "lms/section/delete/";
    public static final String URL_CONTENTS_VIEW = "lms/content/view/";
    public static final String URL_CONTENTS_VIEW_STATISTICS = "lms/content/view/statistics/";
    public static final String URL_CONTENTS_COMMETNS = "lms/content/comments/list/";
    public static final String URL_CONTENTS_LIKE_UNLIKE = "lms/content/like/";
    public static final String URL_CONTENT_DELETE_COMMENT = "lms/content/comment/delete/";
    public static final String URL_CONTENT_VIEWERS = "lms/content/viewers/";
    public static final String URL_CONTENT_ADD_COMMENT = "lms/content/comment/add/";
    public static final String URL_CHECKOUT_COURSE = "checkout/course/single/";
    public static final String URL_COUPONSLIST_COURSE = "checkout/coupons/list/";
    public static final String URL_COUPON_REDEEM = "checkout/coupon/redeem/";
    public static final String URL_REMOVE_COUPON = "checkout/coupon/remove/";
    public static final String URL_GENERATE_KEY = "checkout/generate/key/";
    public static final String URL_FREE_CHECKOUT_KEY = "checkout/transaction/process/";
    public static final String URL_UPDATE_USER_LOCATION = "device/location/";
    public static final String URL_GENERATE_PUBLIC_KEY = "checkout/ccavenue/pubkey/";
    public static final String URL_CHECKOUT_STATUS_UPDATE_KEY = "checkout/status/update/";
    public static final String URL_INSTITUTION_LIST_KEY = "account/institution/list/";
    public static final String URL_INSTITUTION_COURSES_KEY = "account/institution/courses/";
    public static final String URL_INSTITUTION_COURSES_REQUEST_KEY = "account/institution/course/request/join/";
    public static final String URL_USER_PROFILE_SAVE_KEY = "course/user/profile/save/";
    public static final String URL_DEEPLINK_LIST = "device/deep_link/exclude/list/";
    public static final String URL_UPDATE_PROFILE_BIRTHDATE_KEY = "account/profile/birth_date/update/";

    public static final String URL_POST_COMMUNICATION_ADD = "post/communication/add/";
    public static final String URL_COMMUNICATION_OPTIONS = "post/communication/options/";
    public static final String URL_QUIZ_RESULT_ANALYSIS = "lms/quiz/result/analysis/";

    public static final String URL_QUIZ_STATUS = "lms/quiz/status/";
    public static final String URL_QUIZ_START = "lms/quiz/start/";
    public static final String URL_QUIZ_UPDATE_ANSWER = "lms/quiz/answer/update/";
    public static final String URL_QUIZ_VIEW_STATUS_UPDATE = "lms/quiz/question/update/view-time/";
    public static final String URL_GET_RESULT_SUMMARY = "lms/quiz/result/summary/";
    public static final String URL_VIEW_RESULT = "lms/quiz/result/detail/";
    public static final String URL_FINISH_QUIZ = "lms/quiz/finish/";
    public static final String URL_CATEGORY_LIST = "course/user/profile/category/list/";
    public static final String URL_USER_PROFILE_GET = "course/user/profile/get/";
    public static final String URL_COMMUNICATION_POLL_OPTIONS = "post/communication/poll/options/";
    public static final String URL_COMMUNICATION_POLL_VOTE = "post/communication/poll/vote/";
    public static final String URL_CONTENT_SEND_NOTIFICATION = "lms/content/notification/send/";
    public static final String URL_ADD_SECTION = "lms/section/add/";
    public static final String URL_COURSE_BUYER_LIST = "course/buyer/list/";
    public static final String URL_COURSE_REFUND_REASONS = "course/refund/reasons/";
    public static final String URL_COURSE_REFUND = "course/refund/";

    public static final String URL_CONTENTS_ADD = "lms/content/add/";
    public static final String URL_CONTENTS_EDIT = "lms/content/edit/";
    public static final String URL_SECTIONS_ADD = "lms/section/add/";
    public static final String URL_SECTIONS_EDIT = "lms/section/edit/";
    public static final String URL_CONTENTS_COPY = "lms/content/copy/";
    public static final String URL_COURSE_BROWSE_CATEGORY = "course/browse/category/";

    //@Nikhill 20062018
    public static final String URL_STORY_LIST = "story/list/";
    public static final String URL_GET_CATEGORY_SUBSCRIPTION = "story/category/subscription/list/";
    public static final String URL_UPDATE_CATEGORY_SUBSCRIPTION = "story/category/subscription/update/";
    public static final String URL_LIKE_DISLIKE_STORY = "story/like/";
    public static final String URL_POPULAR_STORY_LIST = "story/popular/list/";
    //@nikhil 25072018
    public static final String URL_ERP_STATUS = "service/erp/status/";

    public static int SCREEN_DENSITY = DisplayMetrics.DENSITY_HIGH;

    public static final int INVALID = -1;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    public static final int REFRESH_LIST = 2;

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final String ENABLED = "1";
    public static final String DISABLED = "0";
    public static final String SHOWMORE = "1";
    public static final String SHOWLESS = "2";

    public static final String MAX_UNREAD_COUNT = "99+";
    public static final int MAX_OFFLINE_COURSE_SIZE = 20;
    public static final int MAX_OFFLINE_POST_SIZE = 10;

    public static final String STATUS_KEY = "status";
    public static final String DATA_KEY = "data";

    // Course Image
    public static final String COURSE_XLARGE = "419x280";
    public static final String COURSE_LARGE = "280x187";
    public static final String COURSE_MEDIUM = "240x160";
    public static final String COURSE_SMALL = "70x70";

    // Course / User Profile Image
    public static String PROFILE_IMAGE = "75x75";
    public static final String PROFILE_LARGE = "x140";
    public static final String PROFILE_MEDIUM = "75x75";
    public static final String PROFILE_SMALL = "40x40";

    // Gallery Image
    public static final String GALLERY_XLARGE = "xlarge";
    public static final String GALLERY_LARGE = "300x200";
    public static final String GALLERY_SMALL = "75x75";
    public static final String GALLERY_MOBILE = "mobile";
    public static final String GALLERY_NOCROP = "ncrop";

    // Video / Audio
    public static final String NOCOMPRESS = "ncomp";

    // Banner
    public static final String USER_BANNER_LARGE = "600x400";


    // MenuStatistics Stats
    public static final String STAT_INVITATIONS = "S_INVITATIONS";
    public static final String STAT_ALERTS = "S_ALERTS";

    // invite users Stats (Select_Course)
    public static final String STAT_ADD = "A_ADD";
    public static final String STAT_REPOST = "A_REPOST";

    // Select user request - list source
    public static final String COURSE_USER_LIST_MESSAGE = "L_USER_LIST_MESSAGE";
    public static final String COURSE_USER_LIST_MYCOURSE = "L_USER_LIST_MYCOURSE";

    // Post Type
    public static final int POST_TYPE_BLOG = 1;
    public static final int POST_TYPE_POLL = 5;
    public static final int POST_TYPE_QUIZ = 6;
    public static final int POST_TYPE_MESSAGE = 14;
    public static final int POST_TYPE_ALBUM = 15;
    public static final int POST_TYPE_ATTACHMENT = 16;
    public static final int POST_TYPE_ALL = 17;
    public static final int POST_STORY_LINK = 18;
    public static final String POST_TYPE_CONTENT_QUIZ = "17";

    // Post Content Type
    public static final int POST_CONTENT_GALLERY = 2;
    public static final int POST_CONTENT_AUDIO = 3;
    public static final int POST_CONTENT_VIDEO = 4;
    public static final int POST_CONTENT_LINK = 9;
    public static final int POST_CONTENT_DOCUMENT = 8;
    public static final int POST_CONTENT_ALBUM = 15;

    public static final String ADD_COMMUNICATION_ACTION_ADD = "ADD";
    public static final String ADD_COMMUNICATION_ACTION_EDIT = "EDIT";
    public static final String ADD_COMMUNICATION_ACTION_REPOST = "REPOST";

    // Post schedule Stats
    public static final String PUBLISH_NOW = "publish_now";
    public static final String PUBLISH_SCHEDULE = "publish_schedule";

    //Constants for user course roles
    public static final int COURSE_ROLE_CREATOR = 1; //User is the course owner or creator
    public static final int COURSE_ROLE_TEACHER = 2; //User is a teacher or subscribe/invite as teacher
    public static final int COURSE_ROLE_LEARNER = 3; //User is a learner or subscribe/invite as learner
    public static final int COURSE_ROLE_VISITOR = 4; //User is a visitor and not subscribed to course

    // Add Post Type
    public static final String POST_STATS_ACTION = "post_stat_action";
    public static final String CONTENT_STATS_ACTION = "content_stat_action";
    public static final int POST_COMMUNICATION_ADD = 50;
    public static final int POST_BLOG_ADD = 51;
    public static final int POST_BLOG_EDIT = 52;
    public static final int POST_BLOG_REPOST = 53;
    public static final int POST_QUIZ_ADD = 54;
    public static final int POST_QUIZ_EDIT = 55;
    public static final int POST_QUIZ_REPOST = 56;
    public static final int POST_MESSAGE_ADD = 57;
    public static final int POST_MESSAGE_EDIT = 58;
    public static final int POST_ALERT_ADD = 59;
    public static final int POST_ALERT_EDIT = 60;
    public static final int POST_COURSE_ADD = 61;
    public static final int POST_COURSE_EDIT = 62;
    public static final int POST_ALBUM_ADD = 63;
    public static final int POST_ALBUM_EDIT = 64;
    public static final int POST_ALBUM_REPOST = 65;
    public static final int POST_BANNER_ADD = 66;

    public static final int POST_POLL_ADD = 67;
    public static final int POST_POLL_EDIT = 68;
    public static final int POST_POLL_REPOST = 69;

    public static final int CONTENT_ADD = 70;
    public static final int CONTENT_EDIT = 71;

    public static final int SECTION_ADD = 72;
    public static final int SECTION_EDIT = 73;
    // Checkout Transaction status
    public static final int CHECKOUT_TRANSECTION_STATUS = 2;

    public static List<AppInfoDataSet> appInfoDataSets = new ArrayList<>();

    public static final String DEVELOPER_KEY = "AIzaSyCCdqTdALNc_hVnCNdgzFH7ajdUV_AyVKY";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 2; // in dp

    // Course description display date and time if public type will be 2
    public static final String COURSE_TYPE_TIMEBOUND = "2"; // in dp


    // Quiz Emoji Image
    public static final String GRADE_PICTURE_MDPI = "mdpi";
    public static final String GRADE_PICTURE_HDPI = "hdpi";
    public static final String GRADE_PICTURE_XDPI = "xhdpi";
    public static final String GRADE_PICTURE_XXHDPI = "xxhdpi";
    public static final String GRADE_PICTURE_XXXHDPI = "xxxhdpi";

    // Browse Course Notifications Actions wise
    public static final String BROWSECOURSE_NOTIFICATION_TYPE = "NotificationType";
    public static final int BROWSECOURSE_NORMAL = 1;
    public static final int BROWSECOURSE_SHARE_HEIGHLIGHT = 2;
    public static final int BROWSECOURSE_RATTING_HEIGHLIGHT = 3;
    public static final String NOTIFICATION_TYPE_FCM = "FCM";

    // Activity ID to Identify the activity
    public static final String MY_COURSE = "901";
    public static final String BROWSE_COURSE = "902";
    public static final String COURSE_DESCRIPTION = "903";
    public static final String PROFILE = "904";
    public static final String MY_WISHLIST = "905";
    public static final String USER_REQUEST_OR_COURSE_INVITATION = "906";
    public static final String BOOKMARKS_POST = "907";
    public static final String ALERT_LIST = "908";
    public static final String SETTINGS = "909";
    public static final String COMMUNICATION_LIST = "910";
    public static final String CONTENT_LIST = "911";
    public static final String COURSE_SETTINGS = "912";
    public static final String POST_DETAIL = "913";
    public static final String MESSAGE_DETAIL = "914";
    public static final String ALBUM_DETAIL = "915";
    public static final String QUIZ_DETAIL = "916";
    public static final String ALERT_DETAIL = "917";
    public static final String CONTENT_DETAIL = "918";
    public static final String EDIT_CONTENT = "919";
    public static final String POST_VIEWERS = "920";
    public static final String QUIZ_VIEWERS = "921";
    public static final String MESSAGE_VIEWERS = "922";
    public static final String ALBUM_VIEWERS = "923";
    public static final String CONTENT_VIEWERS = "924";
    public static final String ADD_COMMUNOCATION = "925";
    public static final String PERSIONAL_INFORMATION = "926";
    public static final String QUIZ_QUESTIONS = "927";
    public static final String QUIZ_HELP = "928";
    public static final String QUIZ_RESULT = "929";
    public static final String QUIZ_VIEW_RESULT = "930";
    public static final String QUIZ_RESULT_ANALYSIS = "931";
    public static final String COMMUNICATION_COMMENT = "932";
    public static final String ADD_POLL = "933";
    public static final String COURSE_BUYERLIST = "934";
    public static final String NOTIFICATIONLIST = "935";
    public static final String REFUND_COURSE = "936";
    public static final String ADD_CONTENT = "937";
    public static final String BROWSECOURSE_CATEGORY_MORE = "938";
    public static final String BROWSECOURSE_SEARCH = "939";
    public static final String CHECKOUT = "940";
    public static final String NOTIFICATION_SCREENID = "screen_id";
    public static final String NEXT_SCREENID = "next_screen_id";


    public static ColorDrawable[] courseColorList = {
                    new ColorDrawable(Color.parseColor("#faedc4")),new ColorDrawable(Color.parseColor("#c0d5e5")),
                    new ColorDrawable(Color.parseColor("#f4d3dc")),new ColorDrawable(Color.parseColor("#e2eac9")),
                    new ColorDrawable(Color.parseColor("#c1b1c7")),new ColorDrawable(Color.parseColor("#d0edf4")),
                    new ColorDrawable(Color.parseColor("#b9cdda")),new ColorDrawable(Color.parseColor("#afd1c5")),
                    new ColorDrawable(Color.parseColor("#b5aaa2"))
            };


    public static final String KEY_INAPP_TITLE = "inapptitle";
    public static final String KEY_INAPP_URL = "inappurl";
    public static final String KEY_INAPP_BASE = "base";
}
