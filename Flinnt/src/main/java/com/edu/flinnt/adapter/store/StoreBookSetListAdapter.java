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
import com.edu.flinnt.adapter.BrowseCourseCategoryMoreAdapter;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.store.BrowseCourseDetailActivityNew;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StoreBookSetListAdapter extends RecyclerView.Adapter<StoreBookSetListAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private ArrayList<StoreModelResponse.Course> filteredDataset;
    private final ArrayList<StoreModelResponse.Course> mDataset;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public StoreBookSetListAdapter(Context context, ArrayList<StoreModelResponse.Course> myDataset) {
        this.mContext = context;
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
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

                    StoreModelResponse.Course courseItem = filteredDataset.get(getAdapterPosition());
                    if (Helper.isConnected()) {
                        Intent courseDescriptionIntent = new Intent(mContext,BrowseCourseDetailActivityNew.class);
                        courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY,String.valueOf(courseItem.getInstitutionBookVendorId()));
                        courseDescriptionIntent.putExtra(Flinnt.STANDARD_ID,String.valueOf(courseItem.getStandardId()));
                        courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, courseItem.getOriginalPath());
                        courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, courseItem.getBookName());
                        courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, String.valueOf(courseItem.getInstitutionId()));
                        courseDescriptionIntent.putExtra(BrowsableCourse.RATINGS_KEY, String.valueOf(3.5));
                        courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, String.valueOf(8));
                        ((AppCompatActivity) mContext).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                    } else {
                        Helper.showNetworkAlertMessage(mContext);
                    }
                }
            });
        }

    }

    public StoreModelResponse.Course getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StoreBookSetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_course_category_item_new, parent, false);

        return new StoreBookSetListAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final StoreBookSetListAdapter.ViewHolder holder, int position) {
        final StoreModelResponse.Course course = filteredDataset.get(position);

        if (null != course) {

            // String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getPicture();

            String url = course.getThumbnailPath();

            int[] androidColors = mContext.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            holder.courseImage.setBackgroundColor(randomAndroidColor);
            holder.courseImage.setImageUrl(url,mImageLoader);

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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }


    public void addItems(List<StoreModelResponse.Course> items) {
        int positionStart = mDataset.size() + 1; // position after last course added
        int size = items.size();
        mDataset.addAll(items);
        filteredDataset.addAll(items);
        notifyItemRangeInserted(positionStart, size);
    }
}
