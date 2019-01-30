package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Browsable course response object class
 */

/*"courses": [
        {
        "id": "709",
        "name": "The Economic Blog",
        "institute_name": "Aditya Iyer",
        "picture": "709_1403429460.jpg",
        "ratings": "2.0"
        “user_count”: 10
        },
        {
        "id": "6562",
        "name": "samir153 c12",
        "institute_name": "Samira153 Flinnt Testing Institute",
        "picture": "",
        "ratings": "0.0"
        “user_count”: 100
        },*/

public class BrowsableCourse implements Serializable {

    public static final String ID_KEY = "id";

    public static final String NAME_KEY = "name";
    public static final String INSTITUTE_NAME_KEY = "institute_name";
    public static final String PICTURE_KEY = "picture";
    public static final String VIDEO_URL_KEY = "video_url";
    public static final String RATINGS_KEY = "ratings";
    public static final String USER_COUNT_KEY = "user_count";
    public static final String CART_COUNT = "cart_total";

    public static final String DESCRIPTION_KEY = "description";
    public static final String IS_SUBSCRIBED_KEY = "is_subscribed";
    public static final String IS_WISHLIST_KEY = "is_wishlist";
    public static final String IS_PUBLIC_KEY = "is_public";
    public static final String SHARE_URL = "share_url";
    public static final String PUBLIC_TYPE_KEY = "public_type";
    public static final String EVENT_DATETIME_KEY = "event_datetime";
    public static final String EVENT_LOCATION_KEY = "event_location";
    public static final String CAN_SUBSCRIBE_KEY = "can_subscribe";
    public static final String CAN_UNSUBSCRIBE_KEY = "can_unsubscribe";
    public static final String USER_REVIEW_KEY = "user_review";
    public static final String IS_FREE_KEY = "is_free";
    public static final String MESSAGE_TEXT_KEY = "message_text";

    public static final String DISCOUNT_APPLICABLE_BROWSE_KEY = "discount_applicable";
    public static final String DISCOUNT_PERCENT_BROWSE_KEY = "discount_percent";
    public static final String DISCOUNT_AMOUNT_BROWSE_KEY = "discount_amount";
    public static final String PRICE_BROWSE_KEY = "price";
    public static final String PRICE_BUY_BROWSE_KEY = "price_buy";

    public static final String PRICE_KEY = "price";
    public static final String SHOW_WISHLIST_KEY = "show_wishlist";
    public static final String REQUEST_PERSONAL_INFO_KEY = "req_personal_info";
    public static final String REQUEST_CONTACT_INFO_KEY = "req_contact_info";
    public static final String TOTAL_NO_RATING_KEY = "total_no_rating";
    public static final String REFUND_APPLICABLE_KEY = "refund_applicable";
    public static final String CAN_REFUND_KEY = "can_refund";
    public static final String REFUND_MESSAGE_KEY = "refund_message";
    public static final String CATEGORY_ID_KEY = "category_id";
    public static final String CATEGORY_NAME_KEY = "category_name";

    //08-01-19 by vijay
    public static final String BUNDLE_LIST_KEY = "bundle_list";

    private String id = "";
    private String name = "";
    private String instituteName = "";
    private String picture = "";
    private String videoUrl = "";
    private String ratings = "";
    private String description = "";
    private int isSubscribed = Flinnt.INVALID;
    private int isWishList = Flinnt.INVALID;
    private String isPublic = "";
    private String publicType = "";
    private String eventDatetime = "";
    private String eventLocation = "";
    private int canSubscribe = 0;
    private int can_unsubscribe = 0;
    private String shareUrl = "";
    private String userCount = "";

    private UserReview userReview;
    private String is_free = "";
    private String message_text = "";
    private String discountApplicable = "";
    private String discountPercent = "";
    private String discountAmount = "";
    private String priceBrowse = "";
    private String priceBuy = "";
    private int showWishlist = 0;
    private int requestPersonalInfo = 0;
    private int requestContactInfo = 0;
    private int totalNoRating = 0;
    private int refundApplicable = 0;
    private int canRefund = 0;
    private String refundMessage = "";
    private String categoryId = "";
    private String categoryName = "";

    private Price price;

    public synchronized void parseJSONObject(JSONObject jObject) {

        try {
            if (jObject.has(ID_KEY)) setId(jObject.getString(ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(NAME_KEY)) setName(jObject.getString(NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(INSTITUTE_NAME_KEY))
                setInstituteName(jObject.getString(INSTITUTE_NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PICTURE_KEY)) setPicture(jObject.getString(PICTURE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(VIDEO_URL_KEY)) setVideoUrl(jObject.getString(VIDEO_URL_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(RATINGS_KEY)) setRatings(jObject.getString(RATINGS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(USER_COUNT_KEY))
                setUserCount(String.valueOf(jObject.getInt(USER_COUNT_KEY)));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DESCRIPTION_KEY)) setDescription(jObject.getString(DESCRIPTION_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(SHARE_URL)) setShareUrl(jObject.getString(SHARE_URL));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(IS_SUBSCRIBED_KEY)) setIsSubscribed(jObject.getInt(IS_SUBSCRIBED_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(IS_WISHLIST_KEY)) setIsWishList(jObject.getInt(IS_WISHLIST_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(IS_PUBLIC_KEY)) setIsPublic(jObject.getString(IS_PUBLIC_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PUBLIC_TYPE_KEY)) setPublicType(jObject.getString(PUBLIC_TYPE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(EVENT_DATETIME_KEY))
                setEventDatetime(jObject.getString(EVENT_DATETIME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(EVENT_LOCATION_KEY))
                setEventLocation(jObject.getString(EVENT_LOCATION_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(CAN_SUBSCRIBE_KEY)) setCanSubscribe(jObject.getInt(CAN_SUBSCRIBE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(CAN_UNSUBSCRIBE_KEY))
                setCan_unsubscribe(jObject.getInt(CAN_UNSUBSCRIBE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(USER_REVIEW_KEY)) {
                JSONObject userReviewsJObject = jObject.getJSONObject(USER_REVIEW_KEY);
                userReview = new UserReview();
                userReview.parseJSONObject(userReviewsJObject);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(IS_FREE_KEY)) setIs_free(jObject.getString(IS_FREE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(MESSAGE_TEXT_KEY)) setMessage_text(jObject.getString(MESSAGE_TEXT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_APPLICABLE_BROWSE_KEY))
                setDiscountApplicable(jObject.getString(DISCOUNT_APPLICABLE_BROWSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_PERCENT_BROWSE_KEY))
                setDiscountPercent(jObject.getString(DISCOUNT_PERCENT_BROWSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_AMOUNT_BROWSE_KEY))
                setDiscountAmount(jObject.getString(DISCOUNT_AMOUNT_BROWSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(PRICE_BROWSE_KEY)) setPriceBrowse(jObject.getString(PRICE_BROWSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PRICE_BUY_BROWSE_KEY))
                setPriceBuy(jObject.getString(PRICE_BUY_BROWSE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(SHOW_WISHLIST_KEY)) setShowWishlist(jObject.getInt(SHOW_WISHLIST_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(REQUEST_PERSONAL_INFO_KEY))
                setRequestPersonalInfo(jObject.getInt(REQUEST_PERSONAL_INFO_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(REQUEST_CONTACT_INFO_KEY))
                setRequestContactInfo(jObject.getInt(REQUEST_CONTACT_INFO_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(TOTAL_NO_RATING_KEY))
                setTotalNoRating(jObject.getInt(TOTAL_NO_RATING_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(REFUND_APPLICABLE_KEY))
                setRefundApplicable(jObject.getInt(REFUND_APPLICABLE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(CAN_REFUND_KEY))
                setCanRefund(jObject.getInt(CAN_REFUND_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(REFUND_MESSAGE_KEY))
                setRefundMessage(jObject.getString(REFUND_MESSAGE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(PRICE_KEY)) {
                JSONObject courseJSONObject = jObject.getJSONObject(PRICE_KEY);
                price = new Price();
                price.parseJSONObject(courseJSONObject);
                setPrice(price);
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(CAN_REFUND_KEY))
                setCanRefund(jObject.getInt(CAN_REFUND_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(REFUND_MESSAGE_KEY))
                setRefundMessage(jObject.getString(REFUND_MESSAGE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }


        try {
            if (jObject.has(CATEGORY_ID_KEY))
                setCategoryId(jObject.getString(CATEGORY_ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(CATEGORY_NAME_KEY))
                setCategoryName(jObject.getString(CATEGORY_NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }






    public String getUserCount() {
        if (getTotalUsersCount() >= 10000) {
            userCount = String.format("%.1f", (double) getTotalUsersCount() / 1000) + "k";
        } else if (getTotalUsersCount() >= 1000) {
            userCount = String.format("%.2f", (double) getTotalUsersCount() / 1000) + "k";
        }
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public int getTotalUsersCount() {
        int count = Flinnt.INVALID;
        try {
            count = Integer.parseInt(userCount);
        } catch (Exception e) {
        }
        return count;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(int isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public int getIsWishList() {
        return isWishList;
    }

    public void setIsWishList(int isWishList) {
        this.isWishList = isWishList;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getPublicType() {
        return publicType;
    }

    public void setPublicType(String publicType) {
        this.publicType = publicType;
    }

    public String getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(String eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getCanSubscribe() {
        return canSubscribe;
    }

    public void setCanSubscribe(int canSubscribe) {
        this.canSubscribe = canSubscribe;
    }

    public int getCan_unsubscribe() {
        return can_unsubscribe;
    }

    public void setCan_unsubscribe(int can_unsubscribe) {
        this.can_unsubscribe = can_unsubscribe;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public UserReview getUserReview() {
        return userReview;
    }

    public void setUserReview(UserReview userReview) {
        this.userReview = userReview;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getDiscountApplicable() {
        return discountApplicable;
    }

    public void setDiscountApplicable(String discountApplicable) {
        this.discountApplicable = discountApplicable;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPriceBrowse() {
        return priceBrowse;
    }

    public void setPriceBrowse(String priceBrowse) {
        this.priceBrowse = priceBrowse;
    }

    public String getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(String priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public int getShowWishlist() {
        return showWishlist;
    }

    public void setShowWishlist(int showWishlist) {
        this.showWishlist = showWishlist;
    }

    public int getRequestPersonalInfo() {
        return requestPersonalInfo;
    }

    public void setRequestPersonalInfo(int requestPersonalInfo) {
        this.requestPersonalInfo = requestPersonalInfo;
    }

    public int getRequestContactInfo() {
        return requestContactInfo;
    }

    public void setRequestContactInfo(int requestContactInfo) {
        this.requestContactInfo = requestContactInfo;
    }

    public int getTotalNoRating() {
        return totalNoRating;
    }

    public void setTotalNoRating(int totalNoRating) {
        this.totalNoRating = totalNoRating;
    }

    public int getRefundApplicable() {
        return refundApplicable;
    }

    public void setRefundApplicable(int refundApplicable) {
        this.refundApplicable = refundApplicable;
    }

    public int getCanRefund() {
        return canRefund;
    }

    public void setCanRefund(int canRefund) {
        this.canRefund = canRefund;
    }

    public String getRefundMessage() {
        return refundMessage;
    }

    public void setRefundMessage(String refundMessage) {
        this.refundMessage = refundMessage;
    }

    public static String getIdKey() {
        return ID_KEY;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("id : " + id)
                .append(", name : " + name)
                .append(", instituteName : " + instituteName)
                .append(", picture : " + picture)
                .append(", ratings : " + ratings)
                .append(", description : " + description)
                .append(", userCount : " + userCount)
                .append(", isSubscribed : " + isSubscribed)
                .append(", isWishList : " + isWishList)
                .append(", isPublic : " + isPublic)
                .append(", shareUrl : " + shareUrl)
                .append(", showWishlist : " + showWishlist)
                .append(", requestPersonalInfo : " + requestPersonalInfo)
                .append(", requestContactInfo : " + requestContactInfo)
                .append(", categoryId : " + categoryId)
                .append(", categoryName : " + categoryName);
        return strBuffer.toString();
    }

    public class Price implements Serializable {

        public static final String BASE_KEY = "base";
        public static final String DISCOUNT_APPLICABLE_KEY = "discount_applicable";
        public static final String DISCOUNT_PERCENT_KEY = "discount_percent";
        public static final String DISCOUNT_AMOUNT = "discount_amount";
        public static final String BUY_KEY = "buy";

        private String base = "";
        private int discount_applicable = 0;
        private String discount_percent = "";
        private String discount_amount = "";
        private String buy = "";

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public int getDiscount_applicable() {
            return discount_applicable;
        }

        public void setDiscount_applicable(int discount_applicable) {
            this.discount_applicable = discount_applicable;
        }

        public String getDiscount_percent() {
            return discount_percent;
        }

        public void setDiscount_percent(String discount_percent) {
            this.discount_percent = discount_percent;
        }

        public String getDiscount_amount() {
            return discount_amount;
        }

        public void setDiscount_amount(String discount_amount) {
            this.discount_amount = discount_amount;
        }

        public String getBuy() {
            return buy;
        }

        public void setBuy(String buy) {
            this.buy = buy;
        }

        public synchronized void parseJSONObject(JSONObject jObject) {
            try {
                if (jObject.has(BASE_KEY)) setBase(jObject.getString(BASE_KEY));
            } catch (Exception e) {
                LogWriter.err(e);
            }

            try {
                if (jObject.has(DISCOUNT_APPLICABLE_KEY))
                    setDiscount_applicable(jObject.getInt(DISCOUNT_APPLICABLE_KEY));
            } catch (Exception e) {
                LogWriter.err(e);
            }

            try {
                if (jObject.has(DISCOUNT_PERCENT_KEY))
                    setDiscount_percent(jObject.getString(DISCOUNT_PERCENT_KEY));
            } catch (Exception e) {
                LogWriter.err(e);
            }

            try {
                if (jObject.has(DISCOUNT_AMOUNT))
                    setDiscount_amount(jObject.getString(DISCOUNT_AMOUNT));
            } catch (Exception e) {
                LogWriter.err(e);
            }

            try {
                if (jObject.has(BUY_KEY)) setBuy(jObject.getString(BUY_KEY));
            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

}
