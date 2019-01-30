package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.daimajia.swipe.SwipeLayout;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AllowDisallow;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.AllowDisallowRequest;
import com.edu.flinnt.protocol.SelectUserInfo;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter class of users
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> implements Filterable

{
	AllowDisallowRequest mAllowDisallowRequest;
	AllowDisallow mAllowDisallow;
	
	ArrayList<String> mSelectedUsers;

	private Context mContext;
	private ArrayList<SelectUserInfo> mDataset;
	private ArrayList<SelectUserInfo> filteredDataset;
	private ArrayList<SelectUserInfo> tempDataset = new ArrayList<>();
	ImageLoader mImageLoader;
	
	private boolean isTeachersTab = false;
	
	private String canManageMessage = Flinnt.DISABLED;
	private int canRemoveSubscription = Flinnt.FALSE;
	public void setCanManageMessage(String canManageMessage) {
		this.canManageMessage = canManageMessage;
	}

	public void setCanManageComment(String canManageComment) {
		this.canManageComment = canManageComment;
	}

	public int getCanRemoveSubscription() {
		return canRemoveSubscription;
	}

	public void setCanRemoveSubscription(int canRemoveSubscription) {
		this.canRemoveSubscription = canRemoveSubscription;
	}

	private String canManageComment = Flinnt.DISABLED;
	
	float x1 = 0, x2;
	final int MIN_DISTANCE = 150; 
	
	public UsersAdapter(Context mContext, ArrayList<SelectUserInfo> userList, boolean isTeacher) {
		this.mDataset = userList;
		this.filteredDataset = this.mDataset;
		this.mContext = mContext;
		mImageLoader = Requester.getInstance().getImageLoader();
        isTeachersTab = isTeacher;
	}

	@SuppressLint("InflateParams")
	@Override
	public UsersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_item, null);

		CustomViewHolder viewHolder = new CustomViewHolder(view);
		return viewHolder;
	}

	public void add(int position, SelectUserInfo item) {
		filteredDataset.add(position, item);
		notifyItemInserted(position);
	}

	public void addItems(ArrayList<SelectUserInfo> items) {
		final int positionStart = filteredDataset.size()+1;
		filteredDataset.addAll(items);
		notifyItemRangeInserted(positionStart, items.size());
	}

	public void remove(SelectUserInfo item) {
		int position = filteredDataset.indexOf(item);
		if ( position > -1 ) {
			filteredDataset.remove(position);
			notifyItemRemoved(position);
		}
	}

	public void addTempItems(ArrayList<SelectUserInfo> items) {
		tempDataset.addAll(items);
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
	
	public SelectUserInfo getItem(int position) {
		if ( position >= 0 && position < filteredDataset.size()) {
		return filteredDataset.get(position);
	}
		else return null;
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

						if(!UsersActivity.checkboxState.containsKey(user.getUserID())) UsersActivity.checkboxState.put(user.getUserID(), false);

						String url = UsersActivity.userProfileUrl + Flinnt.PROFILE_MEDIUM  + File.separator + user.getUserPicture();
						customViewHolder.imageViewUser.setImageUrl(url, mImageLoader);

						mAllowDisallowRequest = new AllowDisallowRequest();

						mAllowDisallowRequest.setCourseID(UsersActivity.mCourseID);

						@SuppressWarnings("unused")
						String baseImageUrl = null;
						@SuppressWarnings("unused")
						Uri userImageUrl = null;

						customViewHolder.textViewUserName.setText(user.getUserName());

						customViewHolder.checkBoxSingle.setOnCheckedChangeListener(null);
						if(UsersActivity.checkboxState.get(user.getUserID())){
							customViewHolder.checkBoxSingle.setChecked(true);
						}else{
							customViewHolder.checkBoxSingle.setChecked(false);
						}

						customViewHolder.checkBoxSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

								UsersActivity.checkboxState.put( user.getUserID(), isChecked );

								if(UsersActivity.checkboxState.get(user.getUserID())){
									userFromall.setUserChecked(1);
								}else{
									userFromall.setUserChecked(0);
								}
								( (UsersActivity) mContext ).onChecked() ;
							}
						});

						customViewHolder.itemLayout.setOnTouchListener(new View.OnTouchListener() {
							@Override
							public boolean onTouch(View arg0, MotionEvent event) {

								switch(event.getAction())
								{
									case MotionEvent.ACTION_DOWN:
										x1 = event.getX();
										break;

									case MotionEvent.ACTION_UP:
										x2 = event.getX();
										float deltaX = x2 - x1;

										if (Math.abs(deltaX) > MIN_DISTANCE) {
											if (x2 > x1) {
												// Left to Right Swipe
												customViewHolder.swipeLayout.close(true);
											} else {
												// Right to Left Swipe
												customViewHolder.swipeLayout.open(true);
											}
										} else {
											// Minor swipe considered as Touch
											customViewHolder.checkBoxSingle.setChecked(!customViewHolder.checkBoxSingle.isChecked());
										}

										break;
								}
								return true;
							}
						});


						if(user.getCanAddMessage().equals(Flinnt.ENABLED)) {
							customViewHolder.swipeLayout.close(true);
							customViewHolder.buttonMessage.setBackgroundResource(R.drawable.message_allowed);
						}
						else{
							customViewHolder.swipeLayout.close(true);
							customViewHolder.buttonMessage.setBackgroundResource(R.drawable.message_disallowed);
						}
						if(user.getCanComment().equals(Flinnt.ENABLED)) {
							customViewHolder.swipeLayout.close(true);
							customViewHolder.buttonComment.setBackgroundResource(R.drawable.comment_allowed);
						}
						else {
							customViewHolder.swipeLayout.close(true);
							customViewHolder.buttonComment.setBackgroundResource(R.drawable.comment_disallowed);
						}

						customViewHolder.buttonComment.setVisibility(canManageComment.equalsIgnoreCase(Flinnt.ENABLED) ? View.VISIBLE : View.GONE);
						customViewHolder.buttonMessage.setVisibility(canManageMessage.equalsIgnoreCase(Flinnt.ENABLED) ? View.VISIBLE : View.GONE);
						customViewHolder.buttonDelete.setVisibility(canRemoveSubscription == Flinnt.TRUE ? View.VISIBLE : View.GONE);

						customViewHolder.buttonMessage.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if ( !Helper.isConnected() ) {
									Helper.showNetworkAlertMessage(mContext);
								}
								else {
									mSelectedUsers = new ArrayList<String>();
									mSelectedUsers.add(user.getUserID());
									mAllowDisallowRequest.setUsers(mSelectedUsers);

									if(user.getCanAddMessage().equals(Flinnt.DISABLED)) {
										mAllowDisallow = new AllowDisallow(StudentsFragmentUsers.mHandler, AllowDisallow.ALLOW_MESSAGE, mAllowDisallowRequest);
										user.setCanAddMessage(Flinnt.ENABLED);
									}
									else if(user.getCanAddMessage().equals(Flinnt.ENABLED)) {
										mAllowDisallow = new AllowDisallow(StudentsFragmentUsers.mHandler, AllowDisallow.DISALLOW_MESSAGE, mAllowDisallowRequest);
										user.setCanAddMessage(Flinnt.DISABLED);
									}
									mAllowDisallow.sendAllowDisallowRequest();
									StudentsFragmentUsers.startProgressDialog();
								}
							}
						});
						customViewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if ( !Helper.isConnected() ) {
									Helper.showNetworkAlertMessage(mContext);
								}
								else {
									mSelectedUsers = new ArrayList<String>();
									mSelectedUsers.add(user.getUserID());
									mAllowDisallowRequest.setUsers(mSelectedUsers);

									if(user.getCanComment().equals(Flinnt.DISABLED)) {
										mAllowDisallow = new AllowDisallow(StudentsFragmentUsers.mHandler, AllowDisallow.ALLOW_COMMENT, mAllowDisallowRequest);
										user.setCanComment(Flinnt.ENABLED);
									}
									else if(user.getCanComment().equals(Flinnt.ENABLED)) {
										mAllowDisallow = new AllowDisallow(StudentsFragmentUsers.mHandler, AllowDisallow.DISALLOW_COMMENT, mAllowDisallowRequest);
										user.setCanComment(Flinnt.DISABLED);
									}
									mAllowDisallow.sendAllowDisallowRequest();
									StudentsFragmentUsers.startProgressDialog();
								}
							}
						});
						customViewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {

								if ( !Helper.isConnected() ) {
									Helper.showNetworkAlertMessage(mContext);
								}
								else {

									mSelectedUsers = new ArrayList<String>();
									mSelectedUsers.add(user.getUserID());
									mAllowDisallowRequest.setUsers(mSelectedUsers);
									if(isTeachersTab) {
										mAllowDisallow = new AllowDisallow(TeachersFragmentUsers.mHandler, AllowDisallow.REMOVE_FROM_COURSE, mAllowDisallowRequest);
										TeachersFragmentUsers.startProgressDialog();
									}
									else {
										mAllowDisallow = new AllowDisallow(StudentsFragmentUsers.mHandler, AllowDisallow.REMOVE_FROM_COURSE, mAllowDisallowRequest);
										StudentsFragmentUsers.startProgressDialog();
									}
									mAllowDisallow.sendAllowDisallowRequest();
									//remove(user);
								}
							}
						});

					}
				}
			}

		}catch (Exception e){
			LogWriter.err(e);
		}

	}


	public int getItemCount() {
		return (null != filteredDataset ? filteredDataset.size() : 0);
	}

	public int getTotalItemCount() {
		return mDataset.size() + tempDataset.size();
	}

	public class CustomViewHolder extends RecyclerView.ViewHolder {

		protected SelectableRoundedCourseImageView imageViewUser;
		protected TextView textViewUserName;
		protected CheckBox checkBoxSingle;
		protected SwipeLayout swipeLayout;
		protected Button buttonMessage;
		protected Button buttonComment;
		protected Button buttonDelete;
		protected RelativeLayout itemLayout;

		public CustomViewHolder(View itemView) {
			super(itemView);

			this.imageViewUser = (SelectableRoundedCourseImageView) itemView.findViewById(R.id.imageViewUser);
			this.imageViewUser.setDefaultImageResId(R.drawable.default_user_profile_image);
			this.imageViewUser.setOval(true);
			this.textViewUserName = (TextView) itemView.findViewById(R.id.textViewUserName);
			this.checkBoxSingle = (CheckBox) itemView.findViewById(R.id.checkBoxSingle);
			this.swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeLayout);
			this.buttonMessage = (Button) itemView.findViewById(R.id.buttonMessage);
			this.buttonComment = (Button) itemView.findViewById(R.id.buttonComment);
			this.buttonDelete = (Button) itemView.findViewById(R.id.buttonDelete);
			this.itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);

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

	public interface OnCheckedListener {
		void onChecked();
	}

}
