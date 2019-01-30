package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class RedeemCouponRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String TRANSACTIOM_ID_KEY = "transaction_id";
	public static final String COUPON_CODE_KEY = "coupon_code";

	private String userID = "";
	private int transactionID;
	private String couponCode = "";

	/**
	 * Converts the json object to string
	 * @return converted json string
	 */
	public synchronized String getJSONString() {

		return getJSONObject().toString();
	}

	/**
	 * creates json object
	 * @return created json object
	 */
	public synchronized JSONObject getJSONObject() {

		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(USER_ID_KEY, userID);
			returnedJObject.put(TRANSACTIOM_ID_KEY, transactionID);
			returnedJObject.put(COUPON_CODE_KEY, couponCode);
		} catch(Exception e) {
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

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
}
