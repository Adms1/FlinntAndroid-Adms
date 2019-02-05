package com.edu.flinnt.adapter.store;

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
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.store.BrowseBookSetDetailAcivity;
import com.edu.flinnt.gui.store.BrowseCourseDetailActivityNew;
import com.edu.flinnt.gui.store.BrowseCoursesFragmentNew;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * mContentsAdapter for course list t browse
 */
public class BrowseCoursesListAdapterNew<T> extends RecyclerView.Adapter<BrowseCoursesListAdapterNew.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<T> filteredDataset;
    private final ArrayList<T> mDataset;
    private boolean isSearchMode;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
    private String searchQuery = "";
    private Context mContext;
    private int type;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BrowseCoursesListAdapterNew(Context context, List<T> myDataset, int type) {
        this.mContext = context;
        this.type = type;
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);

    }



    public T getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BrowseCoursesListAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_course_category_item_new, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)

    //08-01-2019 by vijay
    @Override
    public void onBindViewHolder(final BrowseCoursesListAdapterNew.ViewHolder holder, int position) {

        if (type == 1) {

            final StoreModelResponse.Course course = (StoreModelResponse.Course)filteredDataset.get(position);

            holder.courseName.setText(course.getBookName());

            if (null != course) {

                //String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

                String url = course.getThumbnailPath();

                int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                holder.courseImage.setBackgroundColor(randomAndroidColor);
                holder.courseImage.setImageUrl(url, mImageLoader);


                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + String.valueOf(course.getListPrice()));
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + String.valueOf(course.getSalePrice()));

//            if (course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getListPrice());
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBuy());
//            } else {
//                holder.oldPriceTxt.setText("");
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
//            }
//            if (course.getDiscountApplicable().equalsIgnoreCase(Flinnt.ENABLED)) {
//                holder.oldPriceTxt.setVisibility(View.VISIBLE);
//            } else {
//                holder.oldPriceTxt.setVisibility(View.INVISIBLE);
//            }


            }
        }else if(type == 2){

            final StoreBookSetResponse.Course course = (StoreBookSetResponse.Course)filteredDataset.get(position);

            holder.courseName.setText(course.getBookSetName());

            if (null != course) {

                //String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

                String url = course.getThumbnailPath();

                int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                holder.courseImage.setBackgroundColor(randomAndroidColor);
                holder.courseImage.setImageUrl(url, mImageLoader);


                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + String.valueOf(course.getListPrice()));
                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + String.valueOf(course.getSalePrice()));

//            if (course.getIs_free().equalsIgnoreCase(Flinnt.DISABLED)) {
//                holder.oldPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getListPrice());
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.currency) + course.getPriceBuy());
//            } else {
//                holder.oldPriceTxt.setText("");
//                holder.newPriceTxt.setText(mContext.getResources().getString(R.string.free_course));
//            }
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

    /**
     * Set search results list and display it
     *
     * //@param queryText search query
     */
//    public void setFilter(String queryText) {
//        searchQuery = queryText;
//        isSearchMode = true;
//        filteredDataset = new ArrayList<>();
//        queryText = queryText.toLowerCase();
//
//
//        for (StoreModelResponse.Course item : ) {
////            if (item.getName().toLowerCase().contains(queryText) || item.getInstituteName().toLowerCase().contains(queryText)) {
////                filteredDataset.add(item);
////            }
//                if (item.getBookName().toLowerCase().contains(queryText)) {
//                    filteredDataset.add(item);
//                }
//
//        }
//        notifyDataSetChanged();
//    }

    public boolean getSearchMode() {
        return isSearchMode;
    }

    public void setSearchMode(boolean mode) {
        isSearchMode = mode;
        filteredDataset = new ArrayList<>();
    }

    public void addSearchedItems(ArrayList<T> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<T> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);

        if (isSearchMode && !searchQuery.isEmpty()) {
            positionStart = filteredDataset.size() + 1;
            String searchQueryLowerCase = searchQuery.toLowerCase();
            if(type == 1) {

                for(int count =0;count<items.size();count++){

                    StoreModelResponse.Course course = (StoreModelResponse.Course) items.get(count);

                    if (course.getBookName().toLowerCase().contains(searchQueryLowerCase)) {
                        filteredDataset.add(items.get(count));
                    }
                }


            }else if(type == 2){
                for(int count =0;count<items.size();count++){

                    StoreBookSetResponse.Course course = (StoreBookSetResponse.Course) items.get(count);

                    if (course.getBookSetName().toLowerCase().contains(searchQueryLowerCase)) {
                        filteredDataset.add(items.get(count));
                    }
                }
            }
            size = filteredDataset.size() - (positionStart - 1);
        } else {
            filteredDataset.addAll(items);
        }

        notifyItemRangeInserted(positionStart,size);
    }

//    public void remove(String courseID) {
//        for (int i = 0; i < mDataset.size(); i++) {
//            if (mDataset.get(i).getBookId().equals(courseID)) {
//                mDataset.remove(i);
//                break;
//            }
//        }
//        for (int i = 0; i < filteredDataset.size(); i++) {
//            if (filteredDataset.get(i).getBookId().equals(courseID)) {
//                filteredDataset.remove(i);
//                notifyItemRemoved(i);
//                break;
//            }
//        }
//    }

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

                    if(type == 1) {
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
                            courseDescriptionIntent.putExtra(BrowsableCourse.CART_COUNT,String.valueOf(BrowseCoursesFragmentNew.cart_count));
                            ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_STORE_CALLBACK);
                        } else {
                            Helper.showNetworkAlertMessage(mContext);
                        }
                    }else if(type == 2){
                        StoreBookSetResponse.Course courseItem = (StoreBookSetResponse.Course)filteredDataset.get(getAdapterPosition());
                        if (Helper.isConnected()) {
                            Intent courseDescriptionIntent = new Intent(mContext, BrowseBookSetDetailAcivity.class);
                            courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, String.valueOf(courseItem.getInstitutionBookSetVendorId()));
                            courseDescriptionIntent.putExtra(Flinnt.STANDARD_ID, String.valueOf(courseItem.getStandardId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getOriginalPath());
                            courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getBookSetName());
                            courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, String.valueOf(courseItem.getInstitutionId()));
                            courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, String.valueOf(3.5));
                            courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, String.valueOf(8));
                            courseDescriptionIntent.putExtra(BrowsableCourse.CART_COUNT,String.valueOf(BrowseCoursesFragmentNew.cart_count));
                            ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent,MyCoursesActivity.BROWSE_STORE_CALLBACK);
                        } else {
                            Helper.showNetworkAlertMessage(mContext);
                        }
                    }


                    //08-01-2019 by vijay.

//                    if (Helper.isConnected()) {
//                        Intent courseDescriptionIntent = new Intent(mContext, BrowseCourseDetailActivityNew.class);
//                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, courseItem.getInstitutionBookVendorId()+","+courseItem.getStandardId());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getThumbnailPath());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getBookName());
//                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, String.valueOf(courseItem.getInstitutionId()));
//                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, courseItem.get);
//                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, courseItem.getUserCount());
//                        ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
//                    } else {
//                        Helper.showNetworkAlertMessage(mContext);
//                    }
                }
            });
        }

    }

}