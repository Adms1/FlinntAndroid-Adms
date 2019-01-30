package com.edu.flinnt.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.BrowseCourseDetailActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.store.BrowseBookSetDetailAcivity;
import com.edu.flinnt.gui.store.BrowseCourseDetailActivityNew;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by flinnt-android-2 on 31/5/17.
 * Adapter for display courses in for of category wise
 */

public class BrowseCourseCategoryMoreAdapter<T> extends RecyclerView.Adapter<BrowseCourseCategoryMoreAdapter.ViewHolder1> {

    private final ImageLoader mImageLoader;
    private ArrayList<T> filteredDataset;
    private final ArrayList<T> mDataset;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
    private Context mContext;
    private int type;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BrowseCourseCategoryMoreAdapter(Context context, ArrayList<T> myDataset,int type) {
        this.mContext = context;
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        this.type = type;
        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public SelectableRoundedCourseImageView courseImage;
        public TextView courseName;
        public TextView oldPriceTxt, newPriceTxt;

        public ViewHolder1(View v) {
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
//                    StoreModelResponse.Course courseItem = filteredDataset.get(getAdapterPosition());
//                    if (Helper.isConnected()) {
//                        Intent courseDescriptionIntent = new Intent(mContext, BrowseCourseDetailActivityNew.class);
//                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseItem.getId());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getPicture());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getName());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, courseItem.getInstituteName());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, courseItem.getRatings());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, courseItem.getUserCount());
//                        ((Activity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
//                    } else {
//                        Helper.showNetworkAlertMessage(mContext);
//                    }


                    if (type == 1) {
                        StoreModelResponse.Course courseItem = (StoreModelResponse.Course) filteredDataset.get(getAdapterPosition());
                        if (Helper.isConnected()) {
                            Intent courseDescriptionIntent = new Intent(mContext, BrowseCourseDetailActivityNew.class);
                            courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, String.valueOf(courseItem.getInstitutionBookVendorId()));
                            courseDescriptionIntent.putExtra(Flinnt.STANDARD_ID, String.valueOf(courseItem.getStandardId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getOriginalPath());
                            courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getBookName());
                            courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, String.valueOf(courseItem.getInstitutionId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, String.valueOf(3.5));
                            courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, String.valueOf(8));
                            ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                        } else {
                            Helper.showNetworkAlertMessage(mContext);
                        }
                    } else if (type == 2) {
                        StoreBookSetResponse.Course courseItem = (StoreBookSetResponse.Course) filteredDataset.get(getAdapterPosition());
                        if (Helper.isConnected()) {
                            Intent courseDescriptionIntent = new Intent(mContext, BrowseBookSetDetailAcivity.class);
                            courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, String.valueOf(courseItem.getInstitutionBookSetVendorId()));
                            courseDescriptionIntent.putExtra(Flinnt.STANDARD_ID, String.valueOf(courseItem.getStandardId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getOriginalPath());
                            courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getBookSetName());
                            courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, String.valueOf(courseItem.getInstitutionId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, String.valueOf(3.5));
                            courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, String.valueOf(8));
                            ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                        } else {
                            Helper.showNetworkAlertMessage(mContext);
                        }
                    }
                }
            });


        }
    }

    public T getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BrowseCourseCategoryMoreAdapter.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_course_category_item_new, parent, false);

        return new BrowseCourseCategoryMoreAdapter.ViewHolder1(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final BrowseCourseCategoryMoreAdapter.ViewHolder1 holder, int position) {
        if (type == 1) {


            final StoreModelResponse.Course course = (StoreModelResponse.Course) filteredDataset.get(position);

            if (null != course) {

                // String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

                String url = course.getOriginalPath();

                int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                holder.courseImage.setBackgroundColor(randomAndroidColor);
                holder.courseImage.setImageUrl(url, mImageLoader);

                holder.courseName.setText(course.getBookName());

//            if (course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBrowse());
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBuy());
//            } else {
//                holder.oldPriceTxt.setText("");
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
//            }

                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getListPrice());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getSalePrice());
//            if (course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)) {
//                holder.oldPriceTxt.setVisibility(View.VISIBLE);
//            } else {
//                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
//            }
            }
        } else if (type == 2) {
            final StoreBookSetResponse.Course course = (StoreBookSetResponse.Course) filteredDataset.get(position);

            if (null != course) {

                // String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

                String url = course.getOriginalPath();

                int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                holder.courseImage.setBackgroundColor(randomAndroidColor);
                holder.courseImage.setImageUrl(url, mImageLoader);

                holder.courseName.setText(course.getBookSetName());

//            if (course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBrowse());
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBuy());
//            } else {
//                holder.oldPriceTxt.setText("");
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
//            }

                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getListPrice());
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getSalePrice());
//            if (course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)) {
//                holder.oldPriceTxt.setVisibility(View.VISIBLE);
//            } else {
//                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
//            }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }


    public void addItems(List<T> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);
        filteredDataset.addAll(items);
        notifyItemRangeInserted(positionStart, size);
    }


}