package com.edu.flinnt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.PostViewersResponse.PostViewersItems;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * mContentsAdapter class for post viewers
 */
public class PostViewersAdapter extends RecyclerView.Adapter<PostViewersAdapter.ViewHolder> {

    private ArrayList<PostViewersItems> mDataset;
    private String viewerPictureUrl;
    private int currentTab;
    private ImageLoader mImageLoader;
    String courseID = "", courseName = "";
    OnItemClickListener mItemClickListener;
    public ArrayList<String> selectedUserId = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView userName;
        public TextView lastSeen;
        public ImageView commentBtn;
        public SelectableRoundedCourseImageView userPhoto;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.viewers_name);
            lastSeen = (TextView) v.findViewById(R.id.viewers_last_seen);
            commentBtn = (ImageView) v.findViewById(R.id.viewers_comments);
            userPhoto = (SelectableRoundedCourseImageView) v.findViewById(R.id.viewers_photo);
            userPhoto.setDefaultImageResId(R.drawable.default_user_profile_image);
            userPhoto.setOval(true);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                if (mDataset.get(getAdapterPosition()).getCanMessage().equals("1")) {
                    mItemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public void add(int position, PostViewersItems item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<PostViewersItems> items, int currenttab) {
        currentTab = currenttab;
        mDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(PostViewersItems item) {
        int position = mDataset.indexOf(item);
        if (position > -1) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PostViewersAdapter(ArrayList<PostViewersItems> myDataset, String viewerPicUrl, String courseId, String coursName) {
        mDataset = myDataset;
        viewerPictureUrl = viewerPicUrl;
        courseID = courseId;
        courseName = coursName;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public PostViewersItems getItem(int position) {
        if (position >= 0 && position < mDataset.size()) {
            return mDataset.get(position);
        } else return null;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mDataset.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.post_detail_viewers_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PostViewersItems viewer = mDataset.get(position);
        if (null != viewer) {
            String name = viewer.getViewerName();
            String time = viewer.getViewDate();
            String canMessage = viewer.getCanMessage();
            String url = viewerPictureUrl + Flinnt.PROFILE_LARGE + File.separator + viewer.getViewerPicture();

            holder.userName.setText(name);
            if (Long.parseLong(time) * 1000 > Helper.getCurrentDateStartMillis()) {
                holder.lastSeen.setText(Helper.formateTimeMillis(Long.parseLong(time), "HH:mm"));
            } else {
                holder.lastSeen.setText(Helper.formateTimeMillis(Long.parseLong(time), "dd MMM yyyy, HH:mm"));
            }

            if (canMessage.equals("1")) {
                holder.commentBtn.setVisibility(View.VISIBLE);
                if (currentTab == 0) {
                    if (selectedUserId.size() > 0) {
                        if (selectedUserId.contains(mDataset.get(position).getViewerID())) {
                            holder.commentBtn.setImageResource(R.drawable.ic_message_check);
                        }
                    }
                }
            } else {
                holder.commentBtn.setVisibility(View.GONE);
            }
            holder.userPhoto.setImageUrl(url, mImageLoader);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}