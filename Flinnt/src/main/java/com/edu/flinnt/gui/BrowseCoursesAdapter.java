package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Course invites mContentsAdapter class
 */
public class BrowseCoursesAdapter extends RecyclerView.Adapter<BrowseCoursesAdapter.ViewHolder> {

    public static final String TAG = BrowseCoursesAdapter.class.getSimpleName();

    private ArrayList<Course> mDataset;
    private ArrayList<Course> filteredDataset;

    private boolean isSearchMode;

    private ImageLoader mImageLoader;
	private String mCoursePicUrl = "";
	OnItemClickListener mItemClickListener;


    public BrowseCoursesAdapter(ArrayList<Course> myDataset) {
        mImageLoader = Requester.getInstance().getImageLoader();

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public boolean isDataSetEmpty() {
        if ( mDataset.size() > 0 ) {
            return false;
        }
        else {
            return true;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public SelectableRoundedCourseImageView courseImage;
        public TextView courseName;
        public TextView communityName;
        public TextView totalPosts;


		public ViewHolder(View itemView) {
			super(itemView);

            courseImage = (SelectableRoundedCourseImageView) itemView.findViewById(R.id.browse_course_image);
            courseImage.setDefaultImageResId(R.drawable.default_course_image_list);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                courseImage.setCornerRadiiDP(4, 0, 4, 0);
            }


            courseName = (TextView) itemView.findViewById(R.id.browse_course_name_text);
            communityName = (TextView) itemView.findViewById(R.id.browse_course_community_name);
            totalPosts = (TextView) itemView.findViewById(R.id.browse_course_post_count_text);

            itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}

	}

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {

        final Course course = filteredDataset.get(i);

        holder.courseName.setText(course.getCourseName());
        holder.communityName.setText(course.getUserSchoolName());
        holder.totalPosts.setText(course.getTotalPosts());

        String url = getCoursePicUrl() + Flinnt.COURSE_MEDIUM + File.separator + course.getCoursePicture();

        holder.courseImage.setImageUrl(url, mImageLoader);
    }

	public void add(int position, Course item) {
		mDataset.add(position, item);
		notifyItemInserted(position);
	}

    public void update(int position, Course item) {

        if (isSearchMode) {
            int searchPosition = filteredDataset.indexOf(item);
            if ( searchPosition > -1 ) {
                filteredDataset.set( searchPosition, item );
            }
            notifyItemChanged(searchPosition);
        }

        int mainPosition = -1;
        for ( Course mCourse : mDataset) {
            if (item.getCourseID().equals(mCourse.getCourseID())) {
                mainPosition = mDataset.indexOf(mCourse);
                break;
            }
        }

        if (mainPosition > -1)	{
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ItemName : " + mDataset.get(mainPosition).getCourseName() + "  NewItem : " + item.getCourseName());
            mDataset.set(mainPosition, item);
        }
        notifyItemChanged(mainPosition);
    }

    public void remove(Course item) {
        int position = filteredDataset.indexOf(item);

        for ( int i=0; i < filteredDataset.size(); i++ ) {
            if ( filteredDataset.get(i).getCourseID().equals(item.getCourseID()) ) {
                filteredDataset.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
        for ( int i=0; i < mDataset.size(); i++ ) {
            if ( mDataset.get(i).getCourseID().equals(item.getCourseID()) ) {
                mDataset.remove(i);
                break;
            }
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("joinedCourseToRemovePosition :: " + position + "  of item : " + item.getCourseName() + ", mDataset size : " + mDataset.size());
    }

    public void addItems(ArrayList<Course> items) {
        final int positionStart = mDataset.size()+1;
        mDataset.addAll(items);
        notifyItemRangeInserted(positionStart, items.size());
        //notifyDataSetChanged();
    }

    public void addItem(Course item) {
        mDataset.add(item);
        notifyDataSetChanged();
    }

    public void clearData(){
        mDataset.clear();
        notifyDataSetChanged();
    }


	public Course getItem(int position) {
		if ( position >= 0 && position < filteredDataset.size() ) {
		return filteredDataset.get(position);
		}
		else return null;
	}

    public void updateItems(ArrayList<Course> items, ArrayList<String> offlineCourseIDs) {
        ArrayList<String> courseIDs = new ArrayList<String>();
        for( int i=0; i < items.size(); i++ ) {
            Course course = items.get(i);
            courseIDs.add(course.getCourseID());
            for( int j=0; j < mDataset.size(); j++ ) {
                if( course.getCourseID().equals( mDataset.get(j).getCourseID() ) ) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("index : " + j);
                    mDataset.set(j, course);
                    notifyItemChanged(j);
                    break;
                }
            }
        }
    }


	@SuppressLint("InflateParams")
	@Override
	public BrowseCoursesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_course_item, null);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_course_item, viewGroup, false);

		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}


	public int getItemCount() {
		return filteredDataset.size();
	}

	public String getCoursePicUrl() {
		return mCoursePicUrl;
	}

    /**
     * Set search results list and display it
     * @param queryText search query
     */
    public void setFilter(String queryText) {

        isSearchMode = true;
        filteredDataset = new ArrayList<Course>();
        queryText = queryText.toString().toLowerCase();
        for(Course item: mDataset) {
            if( item.getCourseName().toLowerCase().contains(queryText) ) {
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
        filteredDataset = new ArrayList<Course>();
    }

    public void addSearchedItems(ArrayList<Course> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        isSearchMode = false;
        filteredDataset = new ArrayList<Course>();
        filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }

	public void setCoursePicUrl(String mCoursePicUrl) {
		this.mCoursePicUrl = mCoursePicUrl;
	}

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
