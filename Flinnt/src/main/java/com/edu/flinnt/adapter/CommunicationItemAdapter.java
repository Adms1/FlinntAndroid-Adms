package com.edu.flinnt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.util.CommunicationItem;

import java.util.List;


/**
 * Created by flinnt-android-2 on 13/1/17.
 */

public class CommunicationItemAdapter extends RecyclerView.Adapter<CommunicationItemAdapter.ViewHolder> {

    private List<CommunicationItem> mItems;
    private CommunicationItemAdapter.ItemListener mListener;

    public CommunicationItemAdapter(List<CommunicationItem> items, CommunicationItemAdapter.ItemListener listener) {
        mItems = items;
        mListener = listener;
    }

    public void setListener(CommunicationItemAdapter.ItemListener listener) {
        mListener = listener;
    }

    @Override
    public CommunicationItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommunicationItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.communication_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(CommunicationItemAdapter.ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView;
        public CommunicationItem item;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void setData(CommunicationItem item) {
            this.item = item;
            imageView.setImageResource(item.getDrawableResource());
            textView.setText(item.getTitle());
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    public interface ItemListener {
        void onItemClick(CommunicationItem item);
    }
}