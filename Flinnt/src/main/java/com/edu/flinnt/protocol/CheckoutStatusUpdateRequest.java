package com.edu.flinnt.protocol;
import com.edu.flinnt.util.LogWriter;
import org.json.JSONObject;

/**
 * Created by flinnt-android-2 on 15/10/16.
 */
public class CheckoutStatusUpdateRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String STATUS_CODE_KEY = "status_code";

    private String userID = "";
    private int transactionID;
    private int statusCode;

    /**
     * Converts the json object to string
     *
     * @return converted json string
     */
    public synchronized String getJSONString() {

        return getJSONObject().toString();
    }

    /**
     * creates json object
     *
     * @return created json object
     */
    public synchronized JSONObject getJSONObject() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY, userID);
            returnedJObject.put(TRANSACTION_ID_KEY, transactionID);
            returnedJObject.put(STATUS_CODE_KEY, statusCode);
        } catch (Exception e) {
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
