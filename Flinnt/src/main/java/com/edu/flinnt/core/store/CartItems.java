package com.edu.flinnt.core.store;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.CustomJsonObjectRequest;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.models.store.CartResponseModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.google.gson.Gson;

import org.json.JSONObject;

public class CartItems {

    public static final String TAG = CartItems.class.getSimpleName();
    public CartItemRequest cartItemRequest = null;
    public Handler mHandler = null;
    private String serverReponseMsg ="";
    private int type = Flinnt.INVALID;
    private String userid;
    private String id;
    private String name;
    private String bookid;
    private String price;
    private String qty;
    private String Strtype;
    private String rowId;

    private CartResponseModel cartResponseModel = new CartResponseModel();

    private CartListItemResponse cartListItemResponse;

    public CartItems(Handler handler, String userid, String id, String name, String bookid, String price, String qty, String type) {
        mHandler = handler;
        this.userid = userid;
        this.id = id;
        this.name = name;
        this.bookid = bookid;
        this.price = price;
        this.setQty(qty);
        this.Strtype = type;
        getCartListItemResponse();
    }

    public CartItems(Handler handler, String userid) {
        mHandler = handler;
        this.userid = userid;
        getCartListItemResponse();
    }

    public CartItems(Handler handler, String userid,String rowId) {
        mHandler = handler;
        this.userid = userid;
        this.setRowId(rowId);
        getCartListItemResponse();
    }

    public CartItems(Handler handler, String userid,String rowId,String qty) {
        mHandler = handler;
        this.userid = userid;
        this.setRowId(rowId);
        this.setQty(qty);
        getCartListItemResponse();
    }
    public CartListItemResponse getCartListItemResponse() {
        if (cartListItemResponse == null) {
            cartListItemResponse = new CartListItemResponse();
        }
        return cartListItemResponse;
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
//    public String buildURLString() {
//        String params = "?" + BrowseCoursesRequest.FIELDS_KEY + "="
//                + BrowsableCourse.ID_KEY + ","
//                + BrowsableCourse.NAME_KEY + ","
//                + BrowsableCourse.PICTURE_KEY + ","
//                + BrowsableCourse.INSTITUTE_NAME_KEY + ","
//                + BrowsableCourse.RATINGS_KEY + ","
//                + BrowsableCourse.IS_FREE_KEY + ","
//                + BrowsableCourse.DISCOUNT_APPLICABLE_BROWSE_KEY + ","
////                + BrowsableCourse.PRICE_BROWSE_KEY + ","
//                + BrowsableCourse.PRICE_BUY_BROWSE_KEY + ","
//                + BrowsableCourse.PRICE_KEY;
//
//
//        if (type == USERS_JOINED_COURSES)       return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_JOINED + params;
//        return Flinnt.API_URL + Flinnt.URL_COURSE_VIEW_ALSO_MORE + params;
//    }


    public String buildURLString() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.ADD_ITEM_TO_CART;
    }

    public String buildURLString1() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.CART_ITEM_LIST;
    }

    public String buildURLString2() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.CART_ITEM_LIST;
    }

    public String buildURLString3() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.REMOVE_CART_ITEM;
    }

    public String buildURLString4() {
        return Flinnt.LOCAL_API_URL_NEW+Flinnt.UPDATE_CART_ITEM;
    }

    public void sendAddCartRequest() {
        synchronized (CartItems.class) {
            try {
                String url = buildURLString();

                if (null == cartItemRequest) {
                    cartItemRequest = getCartItemsRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + cartItemRequest.getJSONString());

                JSONObject jsonObject = cartItemRequest.getJSONObject();
                sendJsonObjectRequest(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    public void sendAddCartRequest2() {
        synchronized (CartItems.class) {
            try {
                String url = buildURLString();

                cartItemRequest = getCartItemsRequestForBookset();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + cartItemRequest.getJSONString());

                JSONObject jsonObject = cartItemRequest.getJSONObjectForBookSet();
                sendJsonObjectRequest(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }





    public void sendListCartItemsRequest() {
        synchronized (CartItems.class) {
            try {
                String url = buildURLString1();

                if (null == cartItemRequest) {
                    cartItemRequest = getCartListItemsRequest();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " +cartItemRequest.getJSONString());

                JSONObject jsonObject = cartItemRequest.getJSONObjectOfCartListItem();
                sendJsonObjectRequest1(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }


    public void removeCartItemRequest() {
        synchronized (CartItems.class) {
            try {
                String url = buildURLString3();

                cartItemRequest = getCartItemsRequestForRemove();

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + cartItemRequest.getJSONString());

                JSONObject jsonObject = cartItemRequest.getJSONObjectOfRemoveCartListItem();
                sendJsonObjectRequestForRemoveItem(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    public void updateCartItemRequest() {
        synchronized (CartItems.class) {
            try {
                String url = buildURLString4();

                cartItemRequest = getCartItemsRequestForUpdate();


                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mSuggestedCoursesRequest :\nUrl : " + url + "\nData : " + cartItemRequest.getJSONString());

                JSONObject jsonObject = cartItemRequest.getJSONObjectOfUpdateCartListItem();
                sendJsonObjectRequestForUpdateItem(url,jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }

    private void sendJsonObjectRequest(String url,JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST, url,
                jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());
                try {
                    int status = response.optInt("status");


                    Gson gson = new Gson();

                    cartResponseModel = gson.fromJson(String.valueOf(response),CartResponseModel.class);

                    serverReponseMsg = response.optString("message").replace("_"," ");

                    if (status == 1) {
                        // sendListCartItemsRequest();
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }

    private void sendJsonObjectRequestForRemoveItem(String url,JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST,url, jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());
                try {
                    int status = response.optInt("status");

                    if (status == 1) {
                        // sendListCartItemsRequest();
                        sendMesssageToGUI(Flinnt.SUCCESS);
                    }else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }


    private void sendJsonObjectRequestForUpdateItem(String url,JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());
                try {
                    int status = response.optInt("status");

                    if (status == 1) {
                        // sendListCartItemsRequest();
                        Gson gson = new Gson();
                        cartResponseModel = gson.fromJson(String.valueOf(response),CartResponseModel.class);

                        sendMesssageToGUIForUpdate(Flinnt.SUCCESS);
                    }else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);
        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }

    private void sendJsonObjectRequest1(String url, final JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Request.Method.POST,url, jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("SuggestedCourses response :\n" + response.toString());

                try {

                    if(response != null){

                        Gson gson = new Gson();

                        cartListItemResponse = gson.fromJson(String.valueOf(response),CartListItemResponse.class);

                        if(cartListItemResponse != null){
                            if(cartListItemResponse.getStatus() == 1){
                                sendMesssageToGUI2(Flinnt.SUCCESS);
                            }else{
                                sendMesssageToGUI(Flinnt.FAILURE);
                            }
                        }
                    }


                }catch (Exception ex){
                    ex.printStackTrace();
                    sendMesssageToGUI(Flinnt.FAILURE);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("SuggestedCourses Error : " + error.getMessage());

                //mSuggestedCoursesResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Request.Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        Requester.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }


    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = cartResponseModel;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }
    public void sendMesssageToGUIForUpdate(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.arg1 = 2;
            msg.obj = cartResponseModel;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }

    public void sendMesssageToGUI2(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = cartListItemResponse;
            mHandler.sendMessage(msg);
        } else {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mHandler is null");
        }
    }



    public CartItemRequest getCartItemsRequest() {
        if (null == cartItemRequest) {
            cartItemRequest = new CartItemRequest();
            cartItemRequest.setUserId(Config.getStringValue(Config.USER_ID));
            cartItemRequest.setId(id);
            cartItemRequest.setName(name);
            cartItemRequest.setBookid(bookid);
            cartItemRequest.setPrice(price);
            cartItemRequest.setQty(getQty());
            cartItemRequest.setType(Strtype);

        }
        return cartItemRequest;
    }

    public CartItemRequest getCartItemsRequestForRemove() {
        cartItemRequest = new CartItemRequest();
        cartItemRequest.setUserId(Config.getStringValue(Config.USER_ID));
        cartItemRequest.setRowID(rowId);

        return cartItemRequest;
    }

    public CartItemRequest getCartItemsRequestForUpdate() {
        cartItemRequest = new CartItemRequest();
        cartItemRequest.setUserId(Config.getStringValue(Config.USER_ID));
        cartItemRequest.setRowID(rowId);
        cartItemRequest.setQty(qty);

        return cartItemRequest;
    }

    public CartItemRequest getCartItemsRequestForBookset() {
        if (null == cartItemRequest) {
            cartItemRequest = new CartItemRequest();
            cartItemRequest.setUserId(Config.getStringValue(Config.USER_ID));
            cartItemRequest.setId(id);
            cartItemRequest.setName(name);
            cartItemRequest.setBookid(bookid);
            cartItemRequest.setPrice(price);
            cartItemRequest.setQty(getQty());
            cartItemRequest.setBooksettype(Strtype);
        }
        return cartItemRequest;
    }



    public CartItemRequest getCartListItemsRequest() {
        if (null == cartItemRequest) {
            cartItemRequest = new CartItemRequest();
            cartItemRequest.setUserId(Config.getStringValue(Config.USER_ID));
        }
        return cartItemRequest;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
