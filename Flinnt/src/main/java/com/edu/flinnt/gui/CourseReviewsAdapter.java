package com.edu.flinnt.gui;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Comment;
import com.edu.flinnt.protocol.UserReview;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter for course reviews
 */
public class CourseReviewsAdapter extends RecyclerView.Adapter<CourseReviewsAdapter.ViewHolder> {

	private ArrayList<UserReview> mDataset;
	private ImageLoader mImageLoader;
	private OnItemLongClickListener mItemLongClickListener;

	public class ViewHolder extends RecyclerView.ViewHolder implements OnLongClickListener
	{
		// each data item is just a string in this case
		protected TextView userName;
        protected TextView time;
        protected TextView review;
        protected LinearLayout layoutEdit;
        protected SelectableRoundedCourseImageView userPhoto;
        protected AppCompatRatingBar rbReview;

		public ViewHolder(View v) {
			super(v);
            this.userName = (TextView) v.findViewById(R.id.tv_review_username);
            this.time = (TextView) v.findViewById(R.id.tv_review_time);
            this.review = (TextView) v.findViewById(R.id.tv_review_0);
			this.userPhoto = (SelectableRoundedCourseImageView) v.findViewById(R.id.iv_review_user);
            this.userPhoto.setDefaultImageResId(R.drawable.default_viewers_image);
            this.userPhoto.setOval(true);

            this.layoutEdit = (LinearLayout) v.findViewById(R.id.layout_edit_review);
            this.rbReview = (AppCompatRatingBar) v.findViewById(R.id.rb_review);
            /*LayerDrawable stars = (LayerDrawable) rbReview.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(FlinntApplication.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);*/

			v.setOnLongClickListener(this);
		}

		@Override
		public boolean onLongClick(View v) {
			if (mItemLongClickListener != null) {
				mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
			}
			return true;
		}

	}

	public void add(int position, UserReview item) {
		mDataset.add(position, item);
		//notifyItemInserted(position);
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<UserReview> items) {
		mDataset.addAll(items);
		notifyDataSetChanged();
	}

	public void remove(Comment item) {
		int position = mDataset.indexOf(item);
		if ( position > -1 ) {
			mDataset.remove(position);
			//notifyItemRemoved(position);
			notifyDataSetChanged();
		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public CourseReviewsAdapter(ArrayList<UserReview> myDataset) {
		mDataset = myDataset;
		mImageLoader = Requester.getInstance().getImageLoader();
	}

	public UserReview getItem(int position) {
		if ( position >= 0 && position < mDataset.size() ) {
		return mDataset.get(position);
		}
		else return null;
	}
	
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.course_review_item, parent, false);

		// set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		UserReview userReview = mDataset.get(position);
		if( null != userReview ) {
			String name = userReview.getReviewUserName();
			String time = userReview.getReviewTimeStamp();
			String reviewText = userReview.getReviewText();
			String url = Config.getStringValue(Config.USER_PICTURE_URL) + Flinnt.PROFILE_MEDIUM + File.separator + userReview.getReviewUserPicture();
            Float ratings = Float.parseFloat(userReview.getReviewRating());

            holder.userPhoto.setImageUrl(url, mImageLoader);
            holder.userName.setText(name);
            holder.time.setText(Helper.formateTimeMillis(Long.parseLong(time)));
            holder.review.setText(reviewText);
            holder.rbReview.setRating(ratings);
        }
	}
	
	public interface OnItemLongClickListener {
		void onItemLongClick(View view, int position);
	}
	
	public void setOnItemLongClickListener(
			final OnItemLongClickListener mItemLongClickListener) {
		this.mItemLongClickListener = mItemLongClickListener;
	}

}
