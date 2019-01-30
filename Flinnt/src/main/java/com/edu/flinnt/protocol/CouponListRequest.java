package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Builds json request
 */
public class CouponListRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String TRANSACTIOM_ID_KEY = "transaction_id";

	private String userID = "";
	private int transactionID;

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
		}
		catch(Exception e) {
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
}
