package com.edu.flinnt.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by flinnt-android-2 on 31/5/17.
 * Adapter display search item which is search from BrowseCourseFragment. Adapter of BrowseCourseSearchActivity.
 */

public class BrowseCourseSearchAdapter extends RecyclerView.Adapter<BrowseCourseSearchAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<BrowsableCourse> filteredDataset;
    private final ArrayList<BrowsableCourse> mDataset;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
    private Context mContext;
    private boolean isSearchMode;
    private String searchQuery = "";
    private boolean isOnlineMode = false;

    public BrowseCourseSearchAdapter(Context context, ArrayList<BrowsableCourse> myDataset) {
        this.mContext = context;
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public ArrayList<BrowsableCourse> getBrowsableCourseList() {
        return mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public SelectableRoundedCourseImageView courseImage;
        public TextView courseName;
        public TextView oldPriceTxt, newPriceTxt;

        public ViewHolder(View v) {
            super(v);

            courseImage = (SelectableRoundedCourseImageView) v.findViewById(R.id.course_image);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                courseImage.setCornerRadiiDP(4, 4, 0, 0);
            }

            courseName = (TextView) v.findViewById(R.id.course_name_text);
            oldPriceTxt = (TextView) v.findViewById(R.id.old_price_txt);
            oldPriceTxt.setPaintFlags(oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            newPriceTxt = (TextView) v.findViewById(R.id.newPriceTxt);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowsableCourse courseItem = filteredDataset.get(getAdapterPosition());
                    if (Helper.isConnected()) {
                        Intent courseDescriptionIntent = new Intent(mContext, BrowseCourseDetailActivity.class);
                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseItem.getId());
                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getPicture());
                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, courseItem.getInstituteName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, courseItem.getRatings());
                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, courseItem.getUserCount());
                        ((Activity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                    } else {
                        Helper.showNetworkAlertMessage(mContext);
                    }
                }
            });
        }

    }

    public BrowsableCourse getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BrowseCourseSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_course_category_item, parent, false);

        return new BrowseCourseSearchAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final BrowseCourseSearchAdapter.ViewHolder holder, int position) {
        final BrowsableCourse course = filteredDataset.get(position);

        if (null != course) {

            String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();
            int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            holder.courseImage.setBackgroundColor(randomAndroidColor);
            holder.courseImage.setImageUrl(url, mImageLoader);

            holder.courseName.setText(course.getName());

            if (course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBrowse());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBuy());
            } else {
                holder.oldPriceTxt.setText("");
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
            }
            if (course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)) {
                holder.oldPriceTxt.setVisibility(View.VISIBLE);
            } else {
                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

    public void addItems(ArrayList<BrowsableCourse> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);

        if (isSearchMode && !searchQuery.isEmpty()) {
            positionStart = filteredDataset.size() + 1;
            String searchQueryLowerCase = searchQuery.toLowerCase();
            for (BrowsableCourse course : items) {
                if (course.getName().toLowerCase().contains(searchQueryLowerCase)) {
                    filteredDataset.add(course);
                }
            }
            size = filteredDataset.size() - (positionStart - 1);
        } else {
            filteredDataset.addAll(items);
        }

        notifyItemRangeInserted(positionStart, size);

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
        for (BrowsableCourse item : mDataset) {
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

    public void addSearchedItems(ArrayList<BrowsableCourse> items) {
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

    public void setSearchSubmitted(boolean isSearchMode){
        isOnlineMode = isSearchMode;
    }

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredDataset.clear();
        mDataset.clear();
        notifyDataSetChanged();
    }
}