package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;
import org.json.JSONObject;

/**
 * Builds json request
 */
public class GetRSARequest {

	public static final String TRANSECTION_ID_KEY = "transaction_id";

	private int transectionID;

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
			returnedJObject.put(TRANSECTION_ID_KEY, transectionID);
		}
		catch(Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public int getTransectionID() {
		return transectionID;
	}

	public void setTransectionID(int transectionID) {
		this.transectionID = transectionID;
	}
}