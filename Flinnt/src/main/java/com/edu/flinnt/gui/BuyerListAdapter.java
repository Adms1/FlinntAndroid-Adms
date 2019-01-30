package com.edu.flinnt.gui;

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
import com.edu.flinnt.protocol.BuyerListResponse;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by flinnt-android-2 on 18/4/17.
 */

public class BuyerListAdapter extends RecyclerView.Adapter<BuyerListAdapter.ViewHolder> {

    private ArrayList<BuyerListResponse.User> mDataset;
    private ImageLoader mImageLoader;
    String mUserPictureUrl;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView mUserNameTxt;
        protected TextView mUserInstituteTxt;
        protected SelectableRoundedCourseImageView mUserPhoto;

        public ViewHolder(View v) {
            super(v);
            this.mUserNameTxt = (TextView) v.findViewById(R.id.user_name_txt);
            this.mUserInstituteTxt = (TextView) v.findViewById(R.id.user_institute_txt);
            this.mUserPhoto = (SelectableRoundedCourseImageView) v.findViewById(R.id.user_photo);

            this.mUserPhoto.setDefaultImageResId(R.drawable.default_viewers_image);
            this.mUserPhoto.setOval(true);
        }
    }


    public void addItems(List<BuyerListResponse.User> items, String userPictureUrl) {
        mUserPictureUrl = userPictureUrl;
        mDataset.addAll(items);
        notifyDataSetChanged();
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public BuyerListAdapter(ArrayList<BuyerListResponse.User> myDataset) {
        mDataset = myDataset;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public BuyerListResponse.User getItem(int position) {
        if (position >= 0 && position < mDataset.size()) {
            return mDataset.get(position);
        } else return null;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public BuyerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.buyer_list_item, parent, false);
        return new BuyerListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BuyerListAdapter.ViewHolder holder, int position) {
        BuyerListResponse.User user = mDataset.get(position);
        if (null != user) {
            String name = user.getUserFirstname() + " " + user.getUserLastname();
            String institute = user.getInstituteName();
            String url = mUserPictureUrl + Flinnt.PROFILE_MEDIUM + File.separator + user.getUserPicture();

            holder.mUserNameTxt.setText(name);
            holder.mUserInstituteTxt.setText(institute);
            holder.mUserPhoto.setImageUrl(url, mImageLoader);

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write(position + " :: " + "userName : " + name + " , mUserPhotoUrl : " + url);
        }
    }
}