package com.edu.flinnt.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;

/**
 * class to handle recyclerview on scroll event for comments
 */
public abstract class CommentEndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = CommentEndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean isGrid;
    //private int current_page = 1;

    private LayoutManager mLayoutManager;

    public CommentEndlessRecyclerOnScrollListener(LayoutManager layoutManager, boolean isGrid) {
        this.mLayoutManager = layoutManager;
        this.isGrid = isGrid;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        if( isGrid ) {
        	firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }
        else {
        	firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }
        
        /*
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }*/
        
        if (!loading && 
        		(totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            //current_page++;
            onLoadMore(/*current_page*/);
            loading = true;
        }
    }

    public abstract void onLoadMore(/*int current_page*/);

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}
}