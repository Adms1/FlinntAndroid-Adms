package com.edu.flinnt.core.store;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

public class CartItemRequest {

    public static final String USER_ID_KEY = "user_id";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String BOOKID_KEY = "book_id";

    public static final String PRICE_KEY = "price";
    public static final String QTY_KEY = "qty";
    public static final String TYPE_KEY = "type";
    public static final String ROWID_KEY = "rowId";

    private String userId;
    private String Id;
    private String name;
    private String bookid;
    private String price;
    private String qty;
    private String type;
    private String booksettype;
    private String rowID;

    //request string
    //  {"user_id":"1","id":"13","name":"Bharat: The Man Who Built a Nation","book_id":"13","price":"100","qty":"1","type":"book"}
    //  {"user_id":"1","id":"13","name":"Bharat: The Man Who Built a Nation","book_id":"13","price":"100","qty":"1","type":"bookset"}


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
            returnedJObject.put(USER_ID_KEY,getUserId());
            returnedJObject.put(ID_KEY,getId());
            returnedJObject.put(NAME_KEY,getName());
            returnedJObject.put(BOOKID_KEY,getBookid());
            returnedJObject.put(PRICE_KEY,getPrice());
            returnedJObject.put(QTY_KEY,getQty());
            returnedJObject.put(TYPE_KEY,getType());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectForBoksetCart() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
            returnedJObject.put(ID_KEY,getId());
            returnedJObject.put(NAME_KEY,getName());
            returnedJObject.put(BOOKID_KEY,getBookid());
            returnedJObject.put(PRICE_KEY,getPrice());
            returnedJObject.put(QTY_KEY,getQty());
            returnedJObject.put(TYPE_KEY,getBooksettype());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectForBookSet() {

        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
            returnedJObject.put(ID_KEY,getId());
            returnedJObject.put(NAME_KEY,getName());
            returnedJObject.put(BOOKID_KEY,getBookid());
            returnedJObject.put(PRICE_KEY,getPrice());
            returnedJObject.put(QTY_KEY,getQty());
            returnedJObject.put(TYPE_KEY,getBooksettype());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }


    public synchronized JSONObject getJSONObjectOfCartListItem() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public synchronized JSONObject getJSONObjectOfRemoveCartListItem() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
            returnedJObject.put(ROWID_KEY,getRowID());

        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }
    public synchronized JSONObject getJSONObjectOfUpdateCartListItem() {
        JSONObject returnedJObject = new JSONObject();
        try {
            returnedJObject.put(USER_ID_KEY,getUserId());
            returnedJObject.put(ROWID_KEY,getRowID());
            returnedJObject.put(QTY_KEY,getQty());
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return returnedJObject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBooksettype() {
        return booksettype;
    }

    public void setBooksettype(String booksettype) {
        this.booksettype = booksettype;
    }

    public String getRowID() {
        return rowID;
    }

    public void setRowID(String rowID) {
        this.rowID = rowID;
    }
}
