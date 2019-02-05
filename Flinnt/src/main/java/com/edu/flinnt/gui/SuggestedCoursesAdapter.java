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
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

import static com.edu.flinnt.FlinntApplication.mContext;

/**
 * mContentsAdapter for course list t browse
 */
public class SuggestedCoursesAdapter extends RecyclerView.Adapter<SuggestedCoursesAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<BrowsableCourse> mDataset;
    private OnItemClickListener mItemClickListener = null;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);

    // Provide a suitable constructor (depends on the kind of dataset)
    public SuggestedCoursesAdapter(ArrayList<BrowsableCourse> myDataset) {
        mImageLoader = Requester.getInstance().getImageLoader();
        mDataset = myDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SelectableRoundedCourseImageView courseImage;
        public TextView courseName;
        public TextView instituteName;
        public TextView oldPriceTxt,newPriceTxt;
        public TextView ratings;

        public LinearLayout ratingsLayout;

        public ViewHolder(View v) {
            super(v);

            courseImage = (SelectableRoundedCourseImageView) v.findViewById(R.id.course_image);
            courseImage.setDefaultImageResId(R.drawable.default_course_image);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                courseImage.setCornerRadiiDP(4, 4, 0, 0);
            }

            courseName = (TextView) v.findViewById(R.id.course_name_text);
            instituteName = (TextView) v.findViewById(R.id.browse_course_institute_name);
            ratings = (TextView) v.findViewById(R.id.browse_course_ratings);
            oldPriceTxt = (TextView)v.findViewById(R.id.old_price_txt);
            oldPriceTxt.setPaintFlags(oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            newPriceTxt = (TextView)v.findViewById(R.id.newPriceTxt);
            ratingsLayout = (LinearLayout) v.findViewById(R.id.layout_ratings);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_course_list_item, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BrowsableCourse course = mDataset.get(position);

        if (null != course) {

            String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

            holder.courseImage.setImageUrl(url, mImageLoader);

            holder.courseName.setText(course.getName());
            holder.instituteName.setText("by "+course.getInstituteName());


            if(course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)){
                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getPriceBrowse());
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

            if (Float.parseFloat(course.getRatings()) > 0) {
                holder.ratingsLayout.setVisibility(View.VISIBLE);
                holder.ratings.setText(course.getRatings());
            } else {
                holder.ratingsLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public BrowsableCourse getItem(int position) {
        return mDataset.get(position);
    }

    public void addItems(ArrayList<BrowsableCourse> items) {
        mDataset = new ArrayList<>();
        mDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}