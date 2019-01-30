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
import com.edu.flinnt.protocol.BuyerListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 18/4/17.
 */

public class CourseBuyerAdapter extends RecyclerView.Adapter<CourseBuyerAdapter.MyViewHolder> {
    private ArrayList<BuyerListResponse.User> mBuyerList;
    private String mBuyerPictureUrl;
    private ImageLoader mImageLoader;
    public OnItemClickListener mItemClickListener;

    public CourseBuyerAdapter(ArrayList<BuyerListResponse.User> data, String userPictureUrl) {
        this.mBuyerList = data;
        this.mBuyerPictureUrl = userPictureUrl;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_buyer_raw, parent, false);
        CourseBuyerAdapter.MyViewHolder viewHolder = new CourseBuyerAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String url = mBuyerPictureUrl + Flinnt.PROFILE_MEDIUM + File.separator + mBuyerList.get(position).getUserPicture();

        holder.buyerImg.setImageUrl(url, mImageLoader);
        String buyerName = mBuyerList.get(position).getUserFirstname();
        holder.mBuyerTxt.setText(buyerName);

    }

    public void addItems(ArrayList<BuyerListResponse.User> items) {
        final int positionStart = mBuyerList.size() + 1;
        this.mBuyerList.addAll(items);
        notifyItemRangeInserted(positionStart, items.size());
        //notifyDataSetChanged();
    }

    public void removeItems() {
        for(int i=0; i<mBuyerList.size(); i++){
            BuyerListResponse.User temp = mBuyerList.get(i);
            if(temp.getUserId().equals(Config.getStringValue(Config.USER_ID))){
                this.mBuyerList.remove(temp);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBuyerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SelectableRoundedCourseImageView buyerImg;
        private TextView mBuyerTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            buyerImg = (SelectableRoundedCourseImageView) itemView.findViewById(R.id.course_user_img);
            buyerImg.setOval(true);
            mBuyerTxt = (TextView)itemView.findViewById(R.id.buyer_name_txt);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}