package com.edu.flinnt.gui;

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
import com.edu.flinnt.protocol.ContentViewersResponse;
import com.edu.flinnt.protocol.PostViewersResponse.PostViewersItems;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * mContentsAdapter class for post viewers
 */
public class ContentViewersAdapter extends RecyclerView.Adapter<ContentViewersAdapter.ViewHolder> {

    private ArrayList<ContentViewersResponse.Viewer> mDataset;
    private String viewerPictureUrl;
    private ImageLoader mImageLoader;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView userName;
        public TextView lastSeen;
        public ImageView likeBtn;
        public ImageView commentBtn;
        public SelectableRoundedCourseImageView userPhoto;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.viewers_name);
            lastSeen = (TextView) v.findViewById(R.id.viewers_last_seen);
            likeBtn = (ImageView) v.findViewById(R.id.viewers_like);
            commentBtn = (ImageView) v.findViewById(R.id.viewers_comments);
            userPhoto = (SelectableRoundedCourseImageView) v.findViewById(R.id.viewers_photo);
            userPhoto.setDefaultImageResId(R.drawable.default_user_profile_image);
            userPhoto.setOval(true);
        }
    }

    public void add(int position, ContentViewersResponse.Viewer item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<ContentViewersResponse.Viewer> items) {
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

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContentViewersAdapter(ArrayList<ContentViewersResponse.Viewer> myDataset, String viewerPicUrl) {
        mDataset = myDataset;
        viewerPictureUrl = viewerPicUrl;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public ContentViewersResponse.Viewer getItem(int position) {
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
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_detail_viewers_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContentViewersResponse.Viewer viewer = mDataset.get(position);
        if (null != viewer) {
            String name = viewer.getUserFirstname();
            String time = viewer.getViewDate();
            String isComment = viewer.getIsCommented();
            String isLike = viewer.getIsLiked();
            String url = viewerPictureUrl + Flinnt.PROFILE_LARGE + File.separator + viewer.getUserPicture();

            holder.userName.setText(name);
            if (Long.parseLong(time) * 1000 > Helper.getCurrentDateStartMillis()) {
                holder.lastSeen.setText(Helper.formateTimeMillis(Long.parseLong(time), "HH:mm"));
            } else {
                holder.lastSeen.setText(Helper.formateTimeMillis(Long.parseLong(time), "dd MMM yyyy, HH:mm"));
            }

            holder.commentBtn.setVisibility((isComment.equals(Flinnt.ENABLED)) ? View.VISIBLE : View.GONE);
            holder.likeBtn.setVisibility((isLike.equals(Flinnt.ENABLED)) ? View.VISIBLE : View.GONE);

			/*mImageLoader.get(url,  
                    ImageLoader.getImageListener(holder.userPhoto, R.drawable.default_user_profile_image, R.drawable.default_user_profile_image));*/
            holder.userPhoto.setImageUrl(url, mImageLoader);
        }
    }

}
