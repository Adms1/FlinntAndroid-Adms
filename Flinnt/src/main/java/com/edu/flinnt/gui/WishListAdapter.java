package com.edu.flinnt.gui;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.WishableCourses;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;

import static com.edu.flinnt.FlinntApplication.mContext;

/**
 * mContentsAdapter for course list t browse
 */
public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<WishableCourses> filteredDataset;
    private final ArrayList<WishableCourses> mDataset;
    private boolean isSearchMode;
    private OnItemClickListener mItemClickListener = null;
    private OnItemLongClickListener mItemLongClickListener;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);

    private String searchQuery = "";

    public static final String TOTAL_USER = "Users: ";
    private Activity mActivity;


    // Provide a suitable constructor (depends on the kind of dataset)
    public WishListAdapter(Activity activity,ArrayList<WishableCourses> myDataset) {
        this.mActivity = activity;
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLongClickListener {
        // each data item is just a string in this case
        public ImageView courseImage;
        public TextView courseName;
        public TextView instituteName;
        public TextView ratings;
        public TextView totalUsers;
        public TextView totalUsersHeader;
        public LinearLayout ratingsLayout;
        public TextView oldPriceTxt,newPriceTxt;

        public ViewHolder(View v) {
            super(v);

            courseImage = (ImageView) v.findViewById(R.id.course_image);
            courseImage.setImageResource(R.drawable.default_course_image);

            courseName = (TextView) v.findViewById(R.id.wishlist_course_name_text);
            instituteName = (TextView) v.findViewById(R.id.wishlist_course_institute_name);
            ratings = (TextView) v.findViewById(R.id.wishlist_course_ratings);
            totalUsers = (TextView) v.findViewById(R.id.wishlist_course_no_of_users);
            totalUsersHeader = (TextView) v.findViewById(R.id.wishlist_course_users);
            oldPriceTxt = (TextView)v.findViewById(R.id.old_price_txt);
            oldPriceTxt.setPaintFlags(oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            newPriceTxt = (TextView)v.findViewById(R.id.newPriceTxt);
            ratingsLayout = (LinearLayout) v.findViewById(R.id.layout_ratings);

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

    public WishableCourses getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_list_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final WishableCourses wish = filteredDataset.get(position);

        if (null != wish) {

            String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + wish.getPicture();
            Glide.with(mActivity)
                    .load(url)
                    .bitmapTransform(new RoundedCornersTransformation(mActivity,0, 0))
                    .into(holder.courseImage);

            holder.courseName.setText(wish.getName());
            holder.instituteName.setText("by "+wish.getInstituteName());

            if(wish.getIsFree().equalsIgnoreCase(Flinnt.DISABLED)){
                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency)+wish.getPrice());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency)+wish.getPriceBuy());
            }else {
                holder.oldPriceTxt.setText("");
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
            }
            if(wish.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)){
                holder.oldPriceTxt.setVisibility(View.VISIBLE);
            }else {
                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
            }

            if (wish.getPublicType().equalsIgnoreCase(Flinnt.COURSE_TYPE_TIMEBOUND)) {
                holder.totalUsersHeader.setVisibility(View.GONE);
                holder.totalUsers.setText(wish.getEventDatetime()); // to display event date and time
                holder.totalUsers.setTextSize((float) 10.25);
                holder.ratings.setTextSize((float) 10.25);
            } else {
                holder.totalUsersHeader.setVisibility(View.VISIBLE);
                holder.totalUsersHeader.setText(TOTAL_USER);
                holder.totalUsers.setText(String.valueOf(wish.getUserCount()));
                holder.totalUsers.setTextSize(13);
                holder.totalUsersHeader.setTextSize(13);
                holder.ratings.setTextSize(13);
            }

            if (Float.parseFloat(wish.getRatings()) > 0) {
                holder.ratingsLayout.setVisibility(View.VISIBLE);
                holder.ratings.setText(wish.getRatings());
            } else {
                holder.ratingsLayout.setVisibility(View.GONE);
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */
    public void setFilter(String queryText) {
        searchQuery = queryText;
        isSearchMode = true;
        filteredDataset = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (WishableCourses item : mDataset) {
            if (item.getName().toLowerCase().contains(queryText)) {
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

    public void addSearchedItems(ArrayList<WishableCourses> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<WishableCourses> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);

        filteredDataset.addAll(items);

        notifyItemRangeInserted(positionStart, size);
    }

    public void remove(String courseID) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getId().equals(courseID)) {
                mDataset.remove(i);
                break;
            }
        }
        for (int i = 0; i < filteredDataset.size(); i++) {
            if (filteredDataset.get(i).getId().equals(courseID)) {
                filteredDataset.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clearData() {
        filteredDataset.clear();
        if (!isSearchMode) mDataset.clear();
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        searchQuery = "";
        isSearchMode = false;
        filteredDataset = new ArrayList<>();
        filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

}