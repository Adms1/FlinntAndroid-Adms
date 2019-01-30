package com.edu.flinnt.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.NotificationDataSet;

import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 17/5/17.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private ImageLoader mImageLoader;
    private Activity mActivity;
    private ArrayList<NotificationDataSet> mDataset;
    private ArrayList<NotificationDataSet> filteredDataset;

    public NotificationsAdapter(Activity activity, ArrayList<NotificationDataSet> myDataset) {
        filteredDataset = new ArrayList<NotificationDataSet>();
        mDataset = new ArrayList<NotificationDataSet>();
        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public TextView mTitleTxt, mDescriptionTxt, mDateTxt;
        private LinearLayout mNotificationItemLinear;

        public ViewHolder(View v) {
            super(v);
            mTitleTxt = (TextView) v.findViewById(R.id.title_txt);
            mDescriptionTxt = (TextView) v.findViewById(R.id.description_txt);
            mDateTxt = (TextView) v.findViewById(R.id.date_txt);
            mNotificationItemLinear = (LinearLayout)v.findViewById(R.id.notification_item_linear);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return false;
        }
    }

    public void addItems(ArrayList<NotificationDataSet> notificationDataSets) {
        filteredDataset.addAll(notificationDataSets);
        mDataset.addAll(notificationDataSets);
        notifyDataSetChanged();
    }

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onCreateViewHolder viewType : " + viewType);

        // create a new view
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("Temp onBindViewHolder position : " + filteredDataset.get(position).getNotificationReadStatus());

        final ViewHolder holder = (ViewHolder) viewHolder;

        NotificationDataSet notificationDataSet = filteredDataset.get(position);
        if (null != notificationDataSet && filteredDataset.size() > 0) {
            holder.mTitleTxt.setText(notificationDataSet.getNotificationTitle());
            holder.mDescriptionTxt.setText(notificationDataSet.getNotificationDescription());
            holder.mDateTxt.setText(notificationDataSet.getNotificationTimestamp());
        }
        if(filteredDataset.get(position).getNotificationReadStatus().equalsIgnoreCase(Flinnt.DISABLED)){
            holder.mNotificationItemLinear.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.mTitleTxt.setTextColor(Color.parseColor("#000000"));
            holder.mTitleTxt.setTypeface(Typeface.DEFAULT_BOLD);
        }else {
            holder.mNotificationItemLinear.setBackgroundColor(Color.parseColor("#f6f6f6"));
            holder.mTitleTxt.setTextColor(Color.parseColor("#797979"));
            holder.mTitleTxt.setTypeface(Typeface.DEFAULT);
        }
    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(
            final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }
}