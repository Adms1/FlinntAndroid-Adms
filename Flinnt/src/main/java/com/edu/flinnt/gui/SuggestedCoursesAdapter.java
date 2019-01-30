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
import com.edu.flinnt.models.store.RelatedBookResponse;
import com.edu.flinnt.models.store.RelatedVendorsResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.edu.flinnt.FlinntApplication.mContext;

/**
 * mContentsAdapter for course list t browse
 */
public class SuggestedCoursesAdapter<T> extends RecyclerView.Adapter<SuggestedCoursesAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<T> mDataset;
    private OnItemClickListener mItemClickListener = null;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
    private int type;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SuggestedCoursesAdapter(ArrayList<T> myDataset,int type) {
        mImageLoader = Requester.getInstance().getImageLoader();
        mDataset = myDataset;
        this.type = type;
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
    public SuggestedCoursesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_course_list_item, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final SuggestedCoursesAdapter.ViewHolder holder, int position) {

        if(type  == 0){
            final RelatedBookResponse.Datum course =(RelatedBookResponse.Datum) mDataset.get(position);

            if (null != course) {

                //String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();
                String url =  course.getOriginalPath();

                holder.courseImage.setImageUrl(url,mImageLoader);

                holder.courseName.setText(course.getBookName());
                holder.instituteName.setVisibility(View.GONE);
                // holder.instituteName.setText("by "+course.getVe);


//            if(course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)){
                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getListPrice());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getSalePrice());


//
//            if(course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)){
//                holder.oldPriceTxt.setVisibility(View.VISIBLE);
//            }else {
//                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
//            }

//            if (Float.parseFloat(course.getRatings()) > 0) {
//                holder.ratingsLayout.setVisibility(View.VISIBLE);
//                holder.ratings.setText(course.getRatings());
//            } else {
//                holder.ratingsLayout.setVisibility(View.INVISIBLE);
//            }

                holder.ratingsLayout.setVisibility(View.VISIBLE);
                holder.ratings.setText("3.5");


            }
        }else if(type  == 1){

            final RelatedVendorsResponse.Datum course =(RelatedVendorsResponse.Datum) mDataset.get(position);

            if (null != course) {

//                //String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();
//                String url =  course.getOriginalPath();
//
//                holder.courseImage.setImageUrl(url,mImageLoader);
//
//                holder.courseName.setText(course.getBookName());
//                holder.instituteName.setVisibility(View.GONE);
//                // holder.instituteName.setText("by "+course.getVe);
//
//
////            if(course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)){
//                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getListPrice());
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency)+course.getSalePrice());
//
//
////
////            if(course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)){
////                holder.oldPriceTxt.setVisibility(View.VISIBLE);
////            }else {
////                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
////            }
//
////            if (Float.parseFloat(course.getRatings()) > 0) {
////                holder.ratingsLayout.setVisibility(View.VISIBLE);
////                holder.ratings.setText(course.getRatings());
////            } else {
////                holder.ratingsLayout.setVisibility(View.INVISIBLE);
////            }
//
//                holder.ratingsLayout.setVisibility(View.VISIBLE);
//                holder.ratings.setText("3.5");


            }
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

//    public ArrayList<T> getItem(int position) {
//        return mDataset.get(position);
//    }

    public void addItems(ArrayList<T> items) {
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