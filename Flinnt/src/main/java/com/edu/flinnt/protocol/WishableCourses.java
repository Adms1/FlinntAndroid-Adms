package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Created by flinntandr1 on 28/6/16.
 */
public class WishableCourses {

    public static final String ID_KEY                   = "id";
    public static final String NAME_KEY                 = "name";
    public static final String INSTITUTE_NAME_KEY       = "institute_name";
    public static final String PICTURE_KEY              = "picture";
    public static final String RATINGS_KEY              = "ratings";
    public static final String USER_COUNT_KEY           = "user_count";
    public static final String PUBLIC_TYPE_KEY          = "public_type";
    public static final String EVENT_DATETIME_KEY       = "event_datetime";
    public static final String DESCRIPTION_KEY          = "description";
    public static final String IS_SUBSCRIBED_KEY        = "is_subscribed";
    public static final String IS_WISHLIST_KEY          = "is_wishlist";
    public static final String IS_PUBLIC_KEY            = "is_public";
    public static final String SHARE_URL                = "share_url";

    public static final String USER_REVIEW_KEY          = "user_review";

    public static final String IS_FREE_KEY              = "is_free";
    public static final String DISCOUNT_APPLICABLE_KEY 		        = "discount_applicable";
    public static final String DISCOUNT_PERCENT_KEY 		        = "discount_percent";
    public static final String DISCOUNT_AMOUNT_KEY 		        = "discount_amount";
    public static final String PRICE_KEY 		        = "price";
    public static final String PRICE_BUY_KEY 		        = "price_buy";

    private String id = "";
    private String name = "";
    private String instituteName = "";
    private String picture = "";
    private String ratings = "";
    private String description = "";
    private int isSubscribed = Flinnt.INVALID;
    private int isWishList = Flinnt.INVALID;
    private int isPublic = Flinnt.INVALID;
    private String shareUrl = "";

    private int userCount = 0;
    private String publicType = "";
    private String eventDatetime = "";

    private UserReview userReview;

    private String isFree = "";
    private String discountApplicable = "";
    private String discountPercent = "";
    private String discountAmount = "";
    private String price = "";
    private String priceBuy = "";



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
            if (jObject.has(INSTITUTE_NAME_KEY)) setInstituteName(jObject.getString(INSTITUTE_NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PICTURE_KEY)) setPicture(jObject.getString(PICTURE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(RATINGS_KEY)) setRatings(jObject.getString(RATINGS_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(USER_COUNT_KEY)) setUserCount(jObject.getInt(USER_COUNT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PUBLIC_TYPE_KEY)) setPublicType(jObject.getString(PUBLIC_TYPE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(EVENT_DATETIME_KEY)) setEventDatetime(jObject.getString(EVENT_DATETIME_KEY));
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
            if (jObject.has(IS_PUBLIC_KEY)) setIsPublic(jObject.getInt(IS_PUBLIC_KEY));
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
            if (jObject.has(IS_FREE_KEY)) setIsFree(jObject.getString(IS_FREE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_APPLICABLE_KEY)) setDiscountApplicable(jObject.getString(DISCOUNT_APPLICABLE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_PERCENT_KEY)) setDiscountPercent(jObject.getString(DISCOUNT_PERCENT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(DISCOUNT_AMOUNT_KEY)) setDiscountAmount(jObject.getString(DISCOUNT_AMOUNT_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PRICE_KEY)) setPrice(jObject.getString(PRICE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
        try {
            if (jObject.has(PRICE_BUY_KEY)) setPriceBuy(jObject.getString(PRICE_BUY_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
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

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
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

    public UserReview getUserReview() {
        return userReview;
    }

    public void setUserReview(UserReview userReview) {
        this.userReview = userReview;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(String priceBuy) {
        this.priceBuy = priceBuy;
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
                .append(", isFree : " + isFree)
                .append(", discountApplicable : " + discountApplicable)
                .append(", discountPercent : " + discountPercent)
                .append(", discountAmount : " + discountAmount)
                .append(", price : " + price)
                .append(", priceBuy : " + priceBuy);
        return strBuffer.toString();
    }
}
