package com.edu.flinnt.adapter;

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
import com.edu.flinnt.protocol.Comment;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 23/2/17.
 */

public class CommunicationCommentsAdapter extends RecyclerView.Adapter<CommunicationCommentsAdapter.ViewHolder> {

    private ArrayList<Comment> mDataset;
    private ImageLoader mImageLoader;
    private OnItemLongClickListener mItemLongClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
    {
        // each data item is just a string in this case
        protected TextView mCommentUserNameTxt;
        protected TextView mCommentTimeTxt;
        protected TextView mCommentTxt;
        protected SelectableRoundedCourseImageView mUserPhoto;

        public ViewHolder(View v) {
            super(v);
            this.mCommentUserNameTxt = (TextView) v.findViewById(R.id.communication_comment_name_txt);
            this.mCommentTimeTxt = (TextView) v.findViewById(R.id.communication_comment_time_txt);
            this.mCommentTxt = (TextView) v.findViewById(R.id.communication_comment_txt);
            this.mUserPhoto = (SelectableRoundedCourseImageView) v.findViewById(R.id.communication_comment_photo);

            this.mUserPhoto.setDefaultImageResId(R.drawable.default_viewers_image);
            this.mUserPhoto.setOval(true);

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

    public void add(int position, Comment item) {
        mDataset.add(position, item);
        //notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<Comment> items) {
        mDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(Comment item) {
        int position = mDataset.indexOf(item);
        if ( position > -1 ) {
            mDataset.remove(position);
            notifyDataSetChanged();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommunicationCommentsAdapter(ArrayList<Comment> myDataset) {
        mDataset = myDataset;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public Comment getItem(int position) {
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
                R.layout.communication_comments_list_item, parent, false);

        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = mDataset.get(position);
        if( null != comment ) {
            String name = comment.getUserName();
            String time = comment.getCommentDate();
            String commentStr = comment.getCommentText();
            String url = comment.getUserPictureUrl() + Flinnt.PROFILE_MEDIUM + File.separator + comment.getUserPicture();

            holder.mCommentUserNameTxt.setText(name);
            holder.mCommentTimeTxt.setText(Helper.formateTimeMillis(Long.parseLong(time)));
            holder.mCommentTxt.setText(commentStr);
            holder.mUserPhoto.setImageUrl(url, mImageLoader);

            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write(position + " :: " + "userName : " + name + " , mUserPhotoUrl : " + url);
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
