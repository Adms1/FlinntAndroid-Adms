package com.edu.flinnt.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;

/**
 * the class is custom recycler view listener to set to auto extending when new data comes
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    //private final int visibleThreshold = 5; //@Chirag:17/08/2018 commented // The minimum amount of items to have below your current scroll position before loading more.
    private final int visibleThreshold = 10; //@Chirag:17/08/2018// added // The minimum amount of items to have below your current scroll position before loading more.
    //private final int visibleThreshold = 20; //@Chirag:07/09/2018// added
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean isGrid;
    private int current_page = 0;

    private LayoutManager mLayoutManager;

    public EndlessRecyclerOnScrollListener(LayoutManager layoutManager, boolean isGrid) {
        this.mLayoutManager = layoutManager;
        this.isGrid = isGrid;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        if (isGrid) {
            firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

        }
        //Log.d("Scrr", "above if loading : " + loading);
        if (loading) {
            if (totalItemCount > previousTotal) {
                //Log.d("Scrr", "loading : " + loading);
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        /*Log.d("Scc", "loading : " + loading);
        Log.d("Scc", "totalItemcount : " + totalItemCount);
        Log.d("Scc", "firstvishible : " + firstVisibleItem);*/

        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;
            //Log.d("Scc","loadMore.. ");
            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);

    public int getPreviousTotal() {
        return previousTotal;
    }

    public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    public boolean isLoading() {
        //Log.d("Scrr", "isLoading : " + loading);
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }


}