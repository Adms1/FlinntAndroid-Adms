package com.edu.flinnt.gui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.PromoCourseResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;

/**
 * mContentsAdapter for course list
 */
public class PromoCourseAdapter extends RecyclerView.Adapter<PromoCourseAdapter.ViewHolder> {
    Activity activity;
    ArrayList<PromoCourseResponse.Course> courseArrayList;
    private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);

    public PromoCourseAdapter(Activity activity ,ArrayList<PromoCourseResponse.Course> courseArrayList){
        this.activity = activity;
        this.courseArrayList = courseArrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
//		public SelectableRoundedCourseImageView courseImage;
        private ImageView courseImage;
        private TextView txtCourseName , txtInstituteName;
        public ViewHolder(View v) {
            super(v);
            courseImage = (ImageView)v.findViewById(R.id.course_image);
            txtCourseName = (TextView)v.findViewById(R.id.text_course_name);
            txtInstituteName = (TextView)v.findViewById(R.id.text_institute_name);
//			String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getCoursePicture();
//			holder.courseImage.setImageUrl(url, mImageLoader);

        }

    }

    public synchronized void updateDataSource(ArrayList<PromoCourseResponse.Course> promoCourses){
        courseArrayList.clear();
        this.notifyDataSetChanged();
        courseArrayList.addAll(promoCourses);
        this.notifyDataSetChanged();
    }

    public PromoCourseResponse.Course getItem(int position) {

        position = position % courseArrayList.size();
        return courseArrayList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.card_promo_course, parent, false);
              return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        position = position % courseArrayList.size();
        final PromoCourseResponse.Course course = courseArrayList.get(position);
        String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getCoursePicture();
        if(activity.isDestroyed()){
            return;
        }
        Glide.with(activity)
                .load(url)
                .placeholder(R.drawable.default_course_banner)
                .bitmapTransform(new RoundedCornersTransformation(activity,0, 0))
                .into(holder.courseImage);
        holder.txtCourseName.setText(course.getCourseName());
        holder.txtInstituteName.setText(course.getUserSchoolName());
        holder.courseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent courseDescriptionIntent = new Intent(activity, BrowseCourseDetailActivity.class);
                courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, course.getCourseId());
                courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY,course.getCoursePicture());
                courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, course.getCourseName());
                activity.startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(courseArrayList.size() > 2)
            return Integer.MAX_VALUE;
        else
            return courseArrayList.size();
    }




}