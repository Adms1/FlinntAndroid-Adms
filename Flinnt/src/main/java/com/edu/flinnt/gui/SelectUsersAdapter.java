package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.SelectUserInfo;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter for selecting users
 */
public class SelectUsersAdapter extends RecyclerView.Adapter<SelectUsersAdapter.CustomViewHolder> implements Filterable

{
	private Context mContext;
	private ArrayList<SelectUserInfo> mDataset;
	private ArrayList<SelectUserInfo> filteredDataset;
	private ArrayList<SelectUserInfo> tempDataset = new ArrayList<>();
	ImageLoader mImageLoader;
	
	float x1 = 0, x2;
	final int MIN_DISTANCE = 150; 
	
	public SelectUsersAdapter(Context mContext, ArrayList<SelectUserInfo> myDataset) {
		filteredDataset = myDataset;
		mDataset = filteredDataset;
		this.mContext = mContext;
		mImageLoader = Requester.getInstance().getImageLoader();
	}
	

	public class CustomViewHolder extends RecyclerView.ViewHolder {

		protected SelectableRoundedCourseImageView imageViewUser;
		protected TextView textViewUserName;
		protected CheckBox checkBoxSingle;
		protected RelativeLayout itemLayout;

		public CustomViewHolder(View itemView) {
			super(itemView);

			this.imageViewUser = (SelectableRoundedCourseImageView) itemView.findViewById(R.id.imageViewUser);
			this.imageViewUser.setDefaultImageResId(R.drawable.default_user_profile_image);
			this.imageViewUser.setOval(true);
			this.textViewUserName = (TextView) itemView.findViewById(R.id.textViewUserName);
			this.checkBoxSingle = (CheckBox) itemView.findViewById(R.id.checkBoxSingle);
			this.itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public SelectUsersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_user_list_item, viewGroup, false);

		CustomViewHolder viewHolder = new CustomViewHolder(view);
		return viewHolder;
	}
	
	public void add(int position, SelectUserInfo item) {
		filteredDataset.add(position, item);
		notifyItemInserted(position);
	}

	public void addItems(ArrayList<SelectUserInfo> items) {
		filteredDataset.addAll(items);
		notifyDataSetChanged();
	}

	public void addTempItems(ArrayList<SelectUserInfo> items) {
		tempDataset.addAll(items);
		//notifyDataSetChanged();
	}
    /**
     * Remove search results and display all data
     */
    public void removeFilter(){
		if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("tempDataset : " + tempDataset.size());
		if(tempDataset != null && tempDataset.size()>0){
			mDataset.addAll(tempDataset);
			tempDataset.clear();
			notifyDataSetChanged();
		}

	}
		
	public void remove(SelectUserInfo item) {
		int position = filteredDataset.indexOf(item);
		if ( position > -1 ) {
			filteredDataset.remove(position);
			notifyItemRemoved(position);
		}
	}
	
	public SelectUserInfo getItem(int position) {
		if ( position >= 0 && position < filteredDataset.size() ) {
		return filteredDataset.get(position);
		}
		else return null;
	}

	public int getItemCount() {
		return filteredDataset.size();
	}

	public int getTotalItemCount() {
		return mDataset.size() + tempDataset.size();
	}

	@Override
	public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

		try {
			if ( i >= 0 && i < filteredDataset.size() ) {

				final SelectUserInfo user = filteredDataset.get(i);

				int indexOfUser = mDataset.indexOf(user);
				if(indexOfUser >= 0  && indexOfUser < mDataset.size()){

					final SelectUserInfo userFromall =  mDataset.get(indexOfUser);

					if(user != null){

						if(!SelectUsersActivity.checkboxState.containsKey(user.getUserID())) SelectUsersActivity.checkboxState.put(user.getUserID(), false);

						String url = SelectUsersActivity.userProfileUrl + Flinnt.PROFILE_MEDIUM  + File.separator + user.getUserPicture();

						customViewHolder.textViewUserName.setText(user.getUserName());

						customViewHolder.imageViewUser.setImageUrl(url, mImageLoader);

						customViewHolder.checkBoxSingle.setOnCheckedChangeListener(null);
						if(SelectUsersActivity.checkboxState.get(user.getUserID())){
							customViewHolder.checkBoxSingle.setChecked(true);
						}else{
							customViewHolder.checkBoxSingle.setChecked(false);
						}

						customViewHolder.checkBoxSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

								SelectUsersActivity.checkboxState.put(user.getUserID(), isChecked);

								for ( SelectUserInfo userInfo : SelectUsersActivity.mUsersListBoth ) {
									if (userInfo.getUserID().equals(user.getUserID())) {
										userInfo.setUserChecked(isChecked ? 1 : 0);
										if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("OnCheckedChange  userID : " + userInfo.getUserID() + " checked : " + userInfo.getUserChecked());
										break;
									}
								}

								if(SelectUsersActivity.checkboxState.get(user.getUserID())){
									userFromall.setUserChecked(1);


								}else{
									userFromall.setUserChecked(0);

								}
								( (SelectUsersActivity) mContext ).onChecked();
							}
						});

						customViewHolder.itemLayout.setOnTouchListener(new View.OnTouchListener() {
							@Override
							public boolean onTouch(View arg0, MotionEvent event) {

								switch (event.getAction()) {
									case MotionEvent.ACTION_DOWN:
										x1 = event.getX();
										break;

									case MotionEvent.ACTION_UP:
										x2 = event.getX();
										float deltaX = x2 - x1;

										if (Math.abs(deltaX) <= MIN_DISTANCE) {
											customViewHolder.checkBoxSingle.setChecked(!customViewHolder.checkBoxSingle.isChecked());
										}

										break;
								}
								return true;
							}
						});
					}
				}
			}
		}catch (Exception e){
			LogWriter.err(e);
		}

	}

	@SuppressLint("DefaultLocale")
	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() == 0) {
					results.values = mDataset;
					results.count = mDataset.size();
				}
				else {
					ArrayList<SelectUserInfo> filteredUserList = new ArrayList<SelectUserInfo>();

					for (SelectUserInfo user : mDataset) {
						if (user.getUserName().toLowerCase().contains(constraint.toString().toLowerCase())){
							filteredUserList.add(user);
						}
					}
					results.values = filteredUserList;
					results.count = filteredUserList.size();
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				filteredDataset = ((ArrayList<SelectUserInfo>) filterResults.values);
				notifyDataSetChanged();
			}
		};	} 
	
	
	public interface onCheckedListener {
		void onChecked();
	}
}
