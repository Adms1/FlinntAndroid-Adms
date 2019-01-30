package com.edu.flinnt.gui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.CourseInfo;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * mContentsAdapter class for courses
 */
public class SelectCourseAdapter extends RecyclerView.Adapter<SelectCourseAdapter.CustomViewHolder> {

	//private List<Course> courseList;
	private ArrayList<CourseInfo> mDataset;
	private ArrayList<CourseInfo> filteredDataset;
	OnItemClickListener mItemClickListener;
	private ImageLoader mImageLoader;
	String coursePictureUrl = "";
	
	public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	  	  
    	protected TextView textView;
		protected SelectableRoundedCourseImageView courseImg;

    	public CustomViewHolder(View itemView) {
    		super(itemView);
    		 
    		this.textView = (TextView) itemView.findViewById(R.id.title);
			this.courseImg = (SelectableRoundedCourseImageView) itemView.findViewById(R.id.course_image);
			this.courseImg .setDefaultImageResId(R.drawable.default_course_image_list);
    		itemView.setOnClickListener(this);
    	}
    	@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}

    }
	
	public void add(int position, CourseInfo item) {
		filteredDataset.add(position, item);
		notifyItemInserted(position);
	}

	public void addItems(ArrayList<CourseInfo> items, String coursePictureUrl) {
		this.coursePictureUrl= coursePictureUrl;
		filteredDataset.addAll(items);
		notifyDataSetChanged();
	}
		
	public void remove(CourseInfo item) {
		int position = filteredDataset.indexOf(item);
		if ( position > -1 ) {
			filteredDataset.remove(position);
			notifyItemRemoved(position);
		}
	}

    public SelectCourseAdapter(ArrayList<CourseInfo> myDataset) {
		mImageLoader = Requester.getInstance().getImageLoader();
      //  this.mContext = context;
        filteredDataset = myDataset;
		mDataset = filteredDataset;
    }
    
    public CourseInfo getItem(int position) {
    	if(position > Flinnt.INVALID && position < filteredDataset.size()){
    		return filteredDataset.get(position);
    	}
    	return null;
    }
	

    @Override
    public SelectCourseAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_select_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
    	CourseInfo course = filteredDataset.get(position);
        if( null != course ) {
        	//Setting course name
            customViewHolder.textView.setText(course.getCourseName());
			String url = coursePictureUrl + Flinnt.COURSE_SMALL + File.separator + course.getCoursePicture();
			customViewHolder.courseImg.setImageUrl(url, mImageLoader);
        }
    }
    
    public int getItemCount() {
        return filteredDataset.size();
    }

    /**
     * Set search results list and display it
     * @param queryText search query
     */
    public void setFilter(String queryText) {

        filteredDataset = new ArrayList<CourseInfo>();
        queryText = queryText.toString().toLowerCase();
        for(CourseInfo item: mDataset) {
                if( item.getCourseName().toLowerCase().contains(queryText) ) {                     
                	filteredDataset.add(item);
                }
        }
       notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter(){
    	filteredDataset = new ArrayList<CourseInfo>();
    	filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
	}
    
    public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
    
    public void setOnItemClickListener(
			final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

    
    
}
