package com.edu.flinnt.gui;

import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.InstitutionCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.edu.flinnt.FlinntApplication.mContext;

/**
 * Created by flinnt-android-2 on 19/10/16.
 */
public class InstituteCourseAdapter extends RecyclerView.Adapter<InstituteCourseAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<InstitutionCoursesResponse.Course> filteredDataset;
    private final ArrayList<InstitutionCoursesResponse.Course> mDataset;
    //private boolean isSearchMode;
    private OnItemClickListener mItemClickListener = null;
    private OnItemLongClickListener mItemLongClickListener;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);

    private String searchQuery = "";

    public static final String TOTAL_USER = "Users: ";


    // Provide a suitable constructor (depends on the kind of dataset)
    public InstituteCourseAdapter(ArrayList<InstitutionCoursesResponse.Course> myDataset) {
        mImageLoader = Requester.getInstance().getImageLoader();
        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();
        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public Collection<? extends InstitutionCoursesResponse.Course> getCourseList() {
        return mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        public SelectableRoundedCourseImageView courseImage;
        //        public ImageView courseCommunityImage;
        public TextView courseName;
        public TextView instituteName;
        public TextView ratings;
        public TextView totalUsers;
        public TextView totalUsersHeader;
        public LinearLayout ratingsLayout;
        public TextView oldPriceTxt,newPriceTxt;

        public ViewHolder(View v) {
            super(v);

            courseImage = (SelectableRoundedCourseImageView) v.findViewById(R.id.course_image);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                courseImage.setCornerRadiiDP(4, 4, 0, 0);
            }

            courseName = (TextView) v.findViewById(R.id.institute_course_name_text);
            instituteName = (TextView) v.findViewById(R.id.institute_course_institute_name);
            ratings = (TextView) v.findViewById(R.id.institute_course_ratings);
            totalUsers = (TextView) v.findViewById(R.id.institute_course_no_of_users);
            totalUsersHeader = (TextView) v.findViewById(R.id.institute_course_users);
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

    public InstitutionCoursesResponse.Course getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InstituteCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.institute_course_list_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final InstitutionCoursesResponse.Course course = filteredDataset.get(position);

        if (null != course) {

            String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getCoursePicture();
            int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            holder.courseImage.setBackgroundColor(randomAndroidColor);
            holder.courseImage.setImageUrl(url, mImageLoader);

            holder.courseName.setText(course.getCourseName());
            holder.instituteName.setText("by "+course.getCourseOwnerName());
            if(course.getCoursePublic().equalsIgnoreCase(Flinnt.DISABLED)){
                holder.oldPriceTxt.setText("");
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.private_course));
            }else if(course.getCourseIsFree().equalsIgnoreCase(Flinnt.DISABLED)){
                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getPrice());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getPriceBuy());
            }else {
                holder.oldPriceTxt.setText("");
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
            }
            if(course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)){
                holder.oldPriceTxt.setVisibility(View.VISIBLE);
            }else {
                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
            }

            if(course.getCoursePublicTypeId() != null){
                if (course.getCoursePublicTypeId().equalsIgnoreCase(Flinnt.COURSE_TYPE_TIMEBOUND)) {
                    holder.totalUsersHeader.setVisibility(View.GONE);
                    holder.totalUsers.setText(course.getEventDatetime()); // to display event date and time
                    holder.totalUsers.setTextSize((float) 10.25);
                    holder.ratings.setTextSize((float) 10.25);
                } else {
                    holder.totalUsersHeader.setVisibility(View.VISIBLE);
                    holder.totalUsersHeader.setText(TOTAL_USER);
                    holder.totalUsers.setText(String.valueOf(course.getTotalUsers()));
                    holder.totalUsers.setTextSize(13);
                    holder.totalUsersHeader.setTextSize(13);
                    holder.ratings.setTextSize(13);
                }
            }

            if (Float.parseFloat(course.getStatTotalRating()) > 0) {
                holder.ratingsLayout.setVisibility(View.VISIBLE);
                holder.ratings.setText(course.getStatTotalRating());
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
        filteredDataset = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (InstitutionCoursesResponse.Course item : mDataset) {
            if (item.getCourseName().toLowerCase().contains(queryText)) {
                filteredDataset.add(item);
            }
        }

        searchQuery = queryText;
        notifyDataSetChanged();
    }

//    public boolean getSearchMode() {
//        return isSearchMode;
//    }
//
//    public void setSearchMode(boolean mode) {
//        //isSearchMode = mode;
//        filteredDataset = new ArrayList<>();
//    }

    public void addSearchedItems(ArrayList<InstitutionCoursesResponse.Course> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<InstitutionCoursesResponse.Course> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);
        filteredDataset.addAll(items);
       notifyDataSetChanged();
    }

    public void remove(String courseID) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getCourseId().equals(courseID)) {
                mDataset.remove(i);
//                if(!isSearchMode) notifyItemRemoved(i);
                break;
            }
        }
        for (int i = 0; i < filteredDataset.size(); i++) {
            if (filteredDataset.get(i).getCourseId().equals(courseID)) {
                filteredDataset.remove(i);
               /* if(isSearchMode) */
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clearData() {
        filteredDataset.clear();
        mDataset.clear();
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        searchQuery = "";
        //isSearchMode = false;
        if(filteredDataset!=null)
            filteredDataset.clear();
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

    public void setOnItemLongClickListener(
            final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

}