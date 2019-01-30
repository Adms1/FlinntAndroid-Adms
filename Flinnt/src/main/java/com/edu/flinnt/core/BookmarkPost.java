package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.BaseResponse;
import com.edu.flinnt.protocol.DrawerItemListRequest;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Send request and get response and pass it to GUI
 */
public class BookmarkPost {

	public static final String TAG = BookmarkPost.class.getSimpleName();
	public static BookmarkPostResponse mBookmarkPostResponse = null;
	public DrawerItemListRequest mBookmarkPostRequest = null;
	public Handler mHandler = null;

	public BookmarkPost(Handler handler) {
		mHandler = handler;
		getResponse();
	}

	static public BookmarkPostResponse getResponse() {
		mBookmarkPostResponse = new BookmarkPostResponse();
		return mBookmarkPostResponse;
	}

    /**
     * Generates appropriate URL string to make request
     * @return request URL
     */
    public String buildURLString() {
		return Flinnt.API_URL + Flinnt.URL_POST_LIST_BOOKMARK;
	}

	public void sendBookmarkPostRequest() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				Helper.lockCPU();
				try {
					sendRequest();
				} catch (Exception e) {
					LogWriter.err(e);
				} finally {
					Helper.unlockCPU();
				}
			}
		}.start();
	}

    /**
     * sends request along with parameters
     */
    public void sendRequest() {
		synchronized (BookmarkPost.class) {
			try {
				String url = buildURLString();
				if( null == mBookmarkPostRequest ) {
					mBookmarkPostRequest = new DrawerItemListRequest();
					mBookmarkPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
					mBookmarkPostRequest.setOffset(0);
					mBookmarkPostRequest.setMax(10);
					mBookmarkPostResponse.clearPostList();
				}
				else {
					// Reset offset to new request - New offset = old offset + max
					mBookmarkPostRequest.setOffset( mBookmarkPostRequest.getOffset() + mBookmarkPostRequest.getMax() );
				}


				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("AlertList Request :\nUrl : " + url + "\nData : " + mBookmarkPostRequest.getJSONString());

				JSONObject jsonObject = mBookmarkPostRequest.getJSONObject();

				sendJsonObjectRequest(url, jsonObject);

			} catch (Exception e) {
				LogWriter.err(e);
			}

		}
	}

	/**
	 * Method to send json object request.
	 */
	private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
				jsonObject, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d("bookmarkpost",response.toString());
				if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("BookmarkPost Response :\n" + response.toString());

				if (mBookmarkPostResponse.isSuccessResponse(response)) {

					JSONObject jsonData = mBookmarkPostResponse.getJSONData(response);
					if (null != jsonData) {
						mBookmarkPostResponse.parseJSONObject(jsonData);
						sendMesssageToGUI(Flinnt.SUCCESS);
						
						if( mBookmarkPostResponse.getHasMore() > 0 ) {
							sendRequest();
						}
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("BookmarkPost Error :\n" + error.getMessage());
				mBookmarkPostResponse.parseErrorResponse(error);
				sendMesssageToGUI(Flinnt.FAILURE);
			}
		});
		jsonObjReq.setShouldCache(false);

		// Adding request to request queue
		Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
	}

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mBookmarkPostResponse;
			mHandler.sendMessage(msg);
		}
	}

	public static class BookmarkPostResponse extends BaseResponse {
		public static final String POSTS_KEY 		= "posts";
		public static final String HAS_MORE_KEY 	= "has_more";

		private ArrayList<Post> postList	= new ArrayList<Post>();
		private int hasMore = Flinnt.INVALID;

        /**
         * Converts json string to json object
         * @param jsonData json string
         */
        synchronized public void parseJSONString(String jsonData) {

			if( TextUtils.isEmpty(jsonData) ) {
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				parseJSONObject(jsonObject); 
			} catch (Exception e) {
				LogWriter.err(e);
			}
		}

        /**
         * parse json object to suitable data types
         * @param jsonData json object
         */
        synchronized public void parseJSONObject(JSONObject jsonData) {
			
			try {
				setHasMore( jsonData.getInt(HAS_MORE_KEY) );
			} catch (Exception e) {
				LogWriter.err(e);
			}
			
			try {
				JSONArray courses = jsonData.getJSONArray(POSTS_KEY);
				clearPostList();
				for(int i = 0; i < courses.length(); i++) {
					JSONObject jObject = courses.getJSONObject(i);
					Post post = new Post();	
					post.parseJSONObject(jObject);
					// All posts are Bookmark so...
					post.setIsBookmark(Flinnt.ENABLED);
					if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "Post :: " + post.toString() );
					postList.add(post);
					post = null;
				}
				setPostList(postList);
			} catch (Exception e) {
				LogWriter.err(e);
			}

		}

		public int getHasMore() {
			return hasMore;
		}

		public void setHasMore(int hasMore) {
			this.hasMore = hasMore;
		}

		public ArrayList<Post> getPostList() {
			return postList;
		}

		public void setPostList(ArrayList<Post> postList) {
			this.postList = postList;
		}

		public void clearPostList() {
			this.postList.clear();
		}

	}
}
