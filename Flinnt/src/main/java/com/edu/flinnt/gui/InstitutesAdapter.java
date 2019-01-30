package com.edu.flinnt.gui;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.InstitutionResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 17/10/16.
 */

public class InstitutesAdapter extends RecyclerView.Adapter<InstitutesAdapter.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private ArrayList<InstitutionResponse.Institution> mDataset;
    private ArrayList<InstitutionResponse.Institution> filteredDataset;
    InstitutesAdapter.OnItemClickListener mItemClickListener;
    InstitutesAdapter.OnItemLongClickListener mItemLongClickListener;
    private boolean isWithFooter;
    private ImageLoader mImageLoader;
    private String viewerPictureUrl = Config.getStringValue(Config.INSTITUTE_USER_PICTURE_URL);
    private String searchQuery = "";
    private boolean isSearchMode;

    // Provide a suitable constructor (depends on the kind of dataset)
    public InstitutesAdapter(ArrayList<InstitutionResponse.Institution> myDataset) {
        LogWriter.write("Dataset"+myDataset);
        filteredDataset = new ArrayList<InstitutionResponse.Institution>();
        mDataset = new ArrayList<InstitutionResponse.Institution>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public ArrayList<InstitutionResponse.Institution> getInstituteList() {
        return mDataset;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        public TextView institutesNameTxt;
        public SelectableRoundedCourseImageView institutePicImg;

        public ViewHolder(View v) {
            super(v);

            institutePicImg = (SelectableRoundedCourseImageView) v.findViewById(R.id.institute_photo_img);
            institutesNameTxt = (TextView) v.findViewById(R.id.institute_name_txt);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }



    public void add(int position, InstitutionResponse.Institution item) {
        filteredDataset.add(position, item);
        notifyItemInserted(position);
    }

    private boolean isOnlineMode = false;
    public void setSearchSubmitted(boolean isSearchMode){
        isOnlineMode = isSearchMode;
    }

    public void addItems(ArrayList<InstitutionResponse.Institution> items) {
        if(searchQuery.isEmpty())
            filteredDataset.addAll(items);
        if(isOnlineMode)
            filteredDataset.addAll(items);
        mDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredDataset.clear();
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void prependItems(ArrayList<InstitutionResponse.Institution> items) {
        for (int i = 0; i < items.size(); i++) {
            filteredDataset.add(i, items.get(i));
        }
        notifyDataSetChanged();
    }


    /**
     * Update offline post items
     *
     * @param items          items list
     * @param offlinePostIDs item ids list
     */
    public void updateItems(ArrayList<InstitutionResponse.Institution> items, ArrayList<String> offlinePostIDs) {

        notifyDataSetChanged();
    }

    public void remove(InstitutionResponse.Institution item) {
        int position = filteredDataset.indexOf(item);
        if (position > -1) {
            filteredDataset.remove(position);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public InstitutionResponse.Institution getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        }
        return null;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int itemCount = filteredDataset.size();
        if (isWithFooter) {
            itemCount++;
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemCount itemCount : " + itemCount + ", isWithFooter : " + isWithFooter);
        return itemCount;
    }


    @Override
    public int getItemViewType(int position) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemViewType position : " + position);
        if (isWithFooter && isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InstitutesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.institutes_list_item, parent, false);
        return new InstitutesAdapter.ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final InstitutesAdapter.ViewHolder holder, int position) {
        final InstitutionResponse.Institution institution = filteredDataset.get(position);
        if (null != institution) {

            holder.institutesNameTxt.setText(institution.getUserSchoolName());

            String url = viewerPictureUrl + Flinnt.PROFILE_LARGE + File.separator + institution.getUserPicture();
            holder.institutePicImg.setImageUrl(url, mImageLoader);
        }
    }

    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */

    public void setSearchQuery(String queryText){
        searchQuery = queryText;
    }

    public void setFilter(String queryText) {
        searchQuery = queryText;
        isSearchMode = true;
        filteredDataset = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (InstitutionResponse.Institution item : mDataset) {
            if (item.getUserSchoolName().toLowerCase().contains(queryText)) {
                filteredDataset.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public boolean getSearchMode() {
        return isSearchMode;
    }

    public void setSearchMode(boolean mode) {
        isSearchMode = mode;
        filteredDataset = new ArrayList<>();
    }

    public void addSearchedItems(ArrayList<InstitutionResponse.Institution> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter(String instituteId) {
        isOnlineMode = false;
        filteredDataset.clear();
        filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(
            final InstitutesAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(
            final InstitutesAdapter.OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }


    public boolean isWithFooter() {
        return isWithFooter;
    }

    public void setWithFooter(boolean withFooter) {
        this.isWithFooter = withFooter;
    }
}