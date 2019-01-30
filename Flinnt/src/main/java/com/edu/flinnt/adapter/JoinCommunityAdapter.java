package com.edu.flinnt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.JoinCommunityCardViewItem;

import java.util.ArrayList;

/**
 * Adapter class for join community list
 */
public class JoinCommunityAdapter extends RecyclerView.Adapter<JoinCommunityAdapter.ViewHolder> {

	private ArrayList<JoinCommunityCardViewItem> mDataset;
	//OnItemClickListener mItemClickListener;

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		// each data item is just a string in this case
		public TextView courseName;
		public TextView communityName;
		public TextView totalUsers;

		public ViewHolder(View v) {
			super(v);
			courseName = (TextView) v.findViewById(R.id.course_name_community_text);
			communityName = (TextView) v.findViewById(R.id.community_name_text);
			totalUsers = (TextView) v.findViewById(R.id.community_users_text);
			//v.setOnClickListener(this);
		}

	}

	public void add(int position, JoinCommunityCardViewItem item) {
		mDataset.add(position, item);
		notifyItemInserted(position);
	}

	public void remove(JoinCommunityCardViewItem item) {
		int position = mDataset.indexOf(item);
		if ( position > -1 ) {
			mDataset.remove(position);
			notifyItemRemoved(position);
		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public JoinCommunityAdapter(ArrayList<JoinCommunityCardViewItem> myDataset) {
		mDataset = myDataset;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder parent, int viewType) {

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.join_community_list_item, parent, false);

		// set the view's size, margins, paddings and layout parameters
		ViewHolder vh = new ViewHolder(v);

		return vh;
	}
}
