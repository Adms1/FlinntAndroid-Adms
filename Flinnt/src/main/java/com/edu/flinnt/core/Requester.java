package com.edu.flinnt.core;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.util.LogWriter;

/**
 * class to manage and queue requests
 */
public class Requester {

	public static final String TAG = Requester.class.getSimpleName();
	// Default maximum disk usage in bytes
	private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;
	// Default cache folder name
	private static final String DEFAULT_CACHE_DIR = "photos";
	private static Requester mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static final int DEFAULT_TIMEOUT_MS = 20000;
	
	public Requester() {
		getRequestQueue();
		mImageLoader = new ImageLoader(mRequestQueue,new BitmapLruCache(BitmapLruCache.getDefaultLruCacheSize()));
	}
	
		
    public static synchronized Requester getInstance() {
        if (mInstance == null) {
            mInstance = new Requester();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
        	// Always pass application context
            // mRequestQueue = Volley.newRequestQueue(FlinntApplication.getContext());
        	mRequestQueue = newRequestQueue(FlinntApplication.getContext());
        }
        return mRequestQueue;
    }
    
    
    // Most code copied from "Volley.newRequestQueue(..)", we only changed cache directory
    private static RequestQueue newRequestQueue(Context context) {
        // define cache folder
        File rootCache = null;
        try {
            rootCache = context.getExternalCacheDir();
        } catch (Exception e) {
            LogWriter.err(e);
        }
        if (rootCache == null) {
        	if(LogWriter.isValidLevel(Log.WARN)) LogWriter.write("Can't find External Cache Dir, "
                    + "switching to application specific cache directory");
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache,DEFAULT_CACHE_DIR);
        cacheDir.mkdirs();

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
        RequestQueue queue = new RequestQueue(diskBasedCache,network);
        queue.start();

        return queue;
    }
    
    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     * 
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
    	req.setRetryPolicy(new DefaultRetryPolicy(
    			DEFAULT_TIMEOUT_MS, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    /**
     * Adds the specified request to the global queue using the Default TAG.
     * 
     * @param req
     *
     */
    public <T> void addToRequestQueue(Request<T> req) {
    	req.setRetryPolicy(new DefaultRetryPolicy(
    			DEFAULT_TIMEOUT_MS, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    	req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     * 
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
        	try {
        		tag = TextUtils.isEmpty((String)tag) ? TAG : tag;
        	}
        	catch(Exception e) { }
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Cancels all pending requests by the default TAG.
     */
    public void cancelPendingRequests() {
    	mRequestQueue.cancelAll(TAG);
    }
    
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
