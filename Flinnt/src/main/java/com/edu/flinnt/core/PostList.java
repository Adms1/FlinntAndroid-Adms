package com.edu.flinnt.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.database.PostInterface;
import com.edu.flinnt.protocol.PostListRequest;
import com.edu.flinnt.protocol.PostListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Send request and get response and pass it to GUI
 */
public class PostList {

    public static final String TAG = PostList.class.getSimpleName();
    private PostListResponse mPostListResponse = null;
    private PostListRequest mPostListRequest = null;
    private Handler mHandler = null;
    private String mCourseID = "";

    int mPostType = Flinnt.INVALID;

    // For offline support
    private boolean isUpdateDB = false, isSearch = false;
    int mOfflinePostSize = Flinnt.INVALID;
    ArrayList<String> mOfflinePostIDs = new ArrayList<String>();

    private ArrayList<String> mPostIDs = new ArrayList<>();


    public PostList(Handler handler, String courseID, int postType) {
        mHandler = handler;
        mCourseID = courseID;
        mPostType = postType;
        mOfflinePostSize = PostInterface.getInstance().getOfflinePostSize(mCourseID, mPostType);
        getPostListResponse();
    }

    public PostList(Handler handler, String courseID, int postType, ArrayList<String> postIds, boolean isUpdatedb) {
        mHandler = handler;
        mCourseID = courseID;
        mPostType = postType;
        mPostIDs = postIds;
        isUpdateDB = isUpdatedb;
        mOfflinePostSize = PostInterface.getInstance().getOfflinePostSize(mCourseID, mPostType);
        getPostListResponse();
    }

    public PostListResponse getPostListResponse() {
        if (mPostListResponse == null) {
            mPostListResponse = new PostListResponse();
            //mMyCoursesResponse.parseJSONString( Config.getStringValue(Config.LAST_MY_COURSES_RESPONSE) );
        }
        //if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("response : " + mPostListResponse.toString() );
        return mPostListResponse;
    }

    public void setPostListResponse(PostListResponse postListResponse) {
        mPostListResponse = new PostListResponse();
        mPostListResponse.copyResponse(postListResponse);
    }

    /**
     * Generates appropriate URL string to make request
     *
     * @return request URL
     */
    public String buildURLString() {
        return Flinnt.API_URL + Flinnt.URL_POST_LIST_MARGED;
    }


    public void sendPostListRequest(PostListRequest postListRequest) {
        mPostListRequest = postListRequest;
        sendPostListRequest();
    }

    public void sendPostListRequest() {
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
        synchronized (PostList.class) {
            try {
                String url = buildURLString();
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("mPostListRequest : " + mPostListRequest);

                //Log.d("CommRes","mPostListRequest : " + mPostListRequest);
                if (null == mPostListRequest) {
                    mPostListRequest = new PostListRequest();
                    mPostListResponse.clearPostList();
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("isUpdateDB : " + isUpdateDB);

                //Log.d("CommRes","isUpdateDB : " + isUpdateDB);
                if (isUpdateDB) {
                    mPostListRequest.setUserID(Config.getStringValue(Config.USER_ID));
                    mPostListRequest.setCourseID(mCourseID);
                    mPostListRequest.setUpdateOffline(Flinnt.TRUE);
                    if (mPostIDs != null && mPostIDs.size() > 0) {
                        mPostListRequest.setOfflinePosts(mPostIDs);

                    } else {
                        mPostListRequest.setOfflinePosts(PostInterface.getInstance().getAllPostIDs(mCourseID));
                    }

                    //mPostListRequest.setPostTypes(getPostTypes());
                    //mPostListRequest.setOfflinePosts(getOfflinePostIDs());
                    mPostListResponse.clearPostList();
                }

				/*
                else {
					// Reset offset to new request - New offset = old offset + max
					mPostListRequest.setOffset( mPostListRequest.getOffset() + mPostListRequest.getMax() );
				}
				*/

                if (mPostListRequest.getNewOnly().equals(Flinnt.ENABLED)) {
                    mPostListRequest.setPubDate(Flinnt.DISABLED);
                }

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostList Request :\nUrl : " + url + "\nData : " + mPostListRequest.getJSONString());

                //Log.d("CommRes","PostList Request :\nUrl : " + url + "\nData : " + mPostListRequest.getJSONString());

                JSONObject jsonObject = mPostListRequest.getJSONObject();

                sendJsonObjectRequest(url, jsonObject);

            } catch (Exception e) {
                LogWriter.err(e);
            }
        }
    }


    private ArrayList<Integer> getPostTypes() {
        ArrayList<Integer> postTypes = new ArrayList<Integer>();
        if (mPostType == PostInterface.POST_TYPE_BLOG_QUIZ) {
            postTypes.add(Flinnt.POST_TYPE_BLOG);
//            postTypes.add(Flinnt.POST_TYPE_QUIZ);
        } else if (mPostType == PostInterface.POST_TYPE_MESSAGE) {
            postTypes.add(Flinnt.POST_TYPE_MESSAGE);
        } else if (mPostType == PostInterface.POST_TYPE_ALBUM) {
            postTypes.add(Flinnt.POST_TYPE_ALBUM);
        }
        return postTypes;
    }


    /**
     * Method to send json object request.
     */
    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        CustomJsonObjectRequest jsonObjReq = new CustomJsonObjectRequest(Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("PostList Response :\n" + response.toString());

                //Log.d("CommRes","PostList Response : " + response.toString());
                if (mPostListResponse.isSuccessResponse(response)) {

                    JSONObject jsonData = mPostListResponse.getJSONData(response);
                    if (null != jsonData) {
                        mPostListResponse.parseJSONObject(jsonData);
                        //Config.setStringValue(Config.LAST_MY_COURSES_RESPONSE, jsonData.toString());
                        sendMesssageToGUI(Flinnt.SUCCESS);

                        if (!isSearch()) {

                            //Log.d("CommRes","isSearch is false");
                            // Add or update course in database
                            try {
                                if (LogWriter.isValidLevel(Log.INFO))
                                    LogWriter.write("isUpdateDB : " + isUpdateDB);

                                //Log.d("CommRes","isUpdateDB : " + isUpdateDB);
                                if (isUpdateDB) {
                                    Config.setStringValue(Config.GALLERY_URL, mPostListResponse.getGalleryUrl());
                                    Config.setStringValue(Config.ALBUM_URL, mPostListResponse.getAlbumUrl());
                                    Config.setStringValue(Config.AUDIO_URL, mPostListResponse.getAudioUrl());
                                    Config.setStringValue(Config.VIDEO_URL, mPostListResponse.getVideoUrl());
                                    Config.setStringValue(Config.DOC_URL, mPostListResponse.getDocUrl());

                                    ArrayList<String> postIDs = new ArrayList<String>();
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("PostList size : " + mPostListResponse.getPostList().size());
                                    for (int i = 0; i < mPostListResponse.getPostList().size(); i++) {
                                        postIDs.add(mPostListResponse.getPostList().get(i).getPostID());
                                        PostInterface.getInstance().insertOrUpdatePost(mCourseID, mPostListResponse.getPostList().get(i));
                                    }
                                    PostInterface.getInstance().deleteRemovedPost(mCourseID, postIDs, mPostType);
                                    // Time stamp before 30 days
                                    long oldTimeStamp = System.currentTimeMillis() - 2592000000L; // (60*60*24*30*1000) 30 days millis
                                    PostInterface.getInstance().deleteOldPost(oldTimeStamp);
                                    mOfflinePostSize = PostInterface.getInstance().getOfflinePostSize(mCourseID, mPostType);
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("mOfflinePostSize : " + mOfflinePostSize);

                                } else if (mPostListRequest.getNewOnly().equals(Flinnt.ENABLED)) {
                                    //mOfflinePostSize = PostInterface.getInstance().getOfflinePostSize(mCourseID);
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("Insert New Post mOfflinePostSize : " + mOfflinePostSize + ", PostList size : " + mPostListResponse.getPostList().size());

                                    Config.setStringValue(Config.GALLERY_URL, mPostListResponse.getGalleryUrl());
                                    Config.setStringValue(Config.ALBUM_URL, mPostListResponse.getAlbumUrl());
                                    Config.setStringValue(Config.AUDIO_URL, mPostListResponse.getAudioUrl());
                                    Config.setStringValue(Config.VIDEO_URL, mPostListResponse.getVideoUrl());
                                    Config.setStringValue(Config.DOC_URL, mPostListResponse.getDocUrl());

                                    for (int i = 0; i < mPostListResponse.getPostList().size(); i++) {
                                        // Don't insert future post in DB. // Inserted again
//											if( mPostListResponse.getPostList().get(i).getPublishDateLong() < (System.currentTimeMillis()/1000) ) {
                                        PostInterface.getInstance().insertOrUpdatePost(mCourseID, mPostListResponse.getPostList().get(i));
                                        mOfflinePostSize++;
//											}
                                    }
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("mOfflinePostSize : " + mOfflinePostSize + ", PostList size : " + mPostListResponse.getPostList().size());
                                } else if (mOfflinePostSize < Flinnt.MAX_OFFLINE_POST_SIZE) {

                                    //mOfflinePostSize = PostInterface.getInstance().getOfflinePostSize(mCourseID);
                                    if (LogWriter.isValidLevel(Log.INFO))
                                        LogWriter.write("mOfflinePostSize : " + mOfflinePostSize + ", PostList size : " + mPostListResponse.getPostList().size());

                                    Config.setStringValue(Config.GALLERY_URL, mPostListResponse.getGalleryUrl());
                                    Config.setStringValue(Config.ALBUM_URL, mPostListResponse.getAlbumUrl());
                                    Config.setStringValue(Config.AUDIO_URL, mPostListResponse.getAudioUrl());
                                    Config.setStringValue(Config.VIDEO_URL, mPostListResponse.getVideoUrl());
                                    Config.setStringValue(Config.DOC_URL, mPostListResponse.getDocUrl());

                                    for (int i = 0; i < mPostListResponse.getPostList().size(); i++) {
                                        // Don't insert feature post in DB.
//											if( mPostListResponse.getPostList().get(i).getPublishDateLong() < (System.currentTimeMillis()/1000) ) {
                                        PostInterface.getInstance().insertOrUpdatePost(mCourseID, mPostListResponse.getPostList().get(i));
                                        mOfflinePostSize++;
//											}
                                        if (mOfflinePostSize == Flinnt.MAX_OFFLINE_POST_SIZE) {
                                            break;
                                        }
                                    }
                                }

                                if (mOfflinePostSize > Flinnt.MAX_OFFLINE_POST_SIZE) {
                                    PostInterface.getInstance().updateDatabase(mCourseID, mPostType);
                                }
                            } catch (Exception e) {
                                LogWriter.err(e);
                            }
                        }
                    } else {
                        sendMesssageToGUI(Flinnt.FAILURE);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (LogWriter.isValidLevel(Log.ERROR))
                    LogWriter.write("PostList Error : " + error.getMessage());

                //Log.d("CommRes","PostList Error : " + error.getMessage());

                mPostListResponse.parseErrorResponse(error);
                sendMesssageToGUI(Flinnt.FAILURE);
            }
        }
        );
        jsonObjReq.setPriority(Priority.HIGH);
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }


    /**
     * Sends response to handler
     *
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
        if (null != mHandler) {
            Message msg = new Message();
            msg.what = messageID;
            msg.obj = mPostListResponse;
            mHandler.sendMessage(msg);
        }
    }

    public boolean isUpdateDB() {
        return isUpdateDB;
    }

    public void setUpdateDB(boolean isUpdateDB) {
        this.isUpdateDB = isUpdateDB;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public ArrayList<String> getOfflinePostIDs() {
        return mOfflinePostIDs;
    }

    public void setOfflinePostIDs(ArrayList<String> offlinePostIDs) {
        this.mOfflinePostIDs = offlinePostIDs;
    }
}
