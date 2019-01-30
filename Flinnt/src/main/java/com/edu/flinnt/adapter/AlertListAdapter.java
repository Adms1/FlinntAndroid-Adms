package com.edu.flinnt.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import java.util.ArrayList;

/**
 * Alert list mContentsAdapter class
 */
public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.CustomViewHolder> {

	private ArrayList<Alert> alertList;
    OnItemClickListener mItemClickListener;
	OnItemLongClickListener mItemLongClickListener;
    
    public AlertListAdapter() {
        this.alertList = new ArrayList<Alert>();
    }
    
	@Override
    public AlertListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alert_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
     
    	Alert alert = alertList.get(i);

    	customViewHolder.textViewText.setText( alert.getAlertText() );
    	if( alert.getIsRead().equals(Flinnt.ENABLED) ) {
    		customViewHolder.textViewText.setTypeface(Typeface.DEFAULT);
    		customViewHolder.imageViewRead.setVisibility(View.INVISIBLE); 
    	}
    	else {
    		customViewHolder.textViewText.setTypeface(Typeface.DEFAULT_BOLD);
    		customViewHolder.imageViewRead.setVisibility(View.VISIBLE);
    	}
    	
        customViewHolder.textViewUSer.setText("by " + alert.getAlertByUser());
        try {
        	
			if ( (alert.getAlertDateLong()) > (System.currentTimeMillis()/1000) ) {
				customViewHolder.textViewDt.setText( "not yet" );
			}
			else customViewHolder.textViewDt.setText( Helper.formateTimeMillis( alert.getAlertDateLong() ) );
			
		} catch (Exception e) { LogWriter.err(e);}
    }

    /**
     * Counts total number of alerts
     * @return total number of alerts
     */
    public int getItemCount() {
        return (null != alertList ? alertList.size() : 0);
    }
    
	public void addItems(ArrayList<Alert> items) {
		alertList.addAll(items);
		notifyDataSetChanged();
	}

	public void clearData(){
		alertList.clear();
		notifyDataSetChanged();
	}
    
	public Alert getItem(int position) {
		if ( position >= 0 && position < alertList.size() ) {
		return alertList.get(position);
		}
		else return null;
	}
	public void remove(String alertID) {
		for (int i = 0; i < alertList.size(); i++) {
			if (alertList.get(i).getAlertID().equals(alertID)) {
				alertList.remove(i);
//                if(!isSearchMode) notifyItemRemoved(i);
				notifyItemRemoved(i);
				break;
			}
		}
	}

    public class CustomViewHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
  	  
    	protected TextView textViewText;
    	protected TextView textViewUSer;
    	protected TextView textViewDt;
    	protected ImageView imageViewRead;
    	

    	public CustomViewHolder(View itemView) {
    		super(itemView);
    		 
    		this.textViewText = (TextView) itemView.findViewById(R.id.textViewText);
    		this.textViewUSer = (TextView) itemView.findViewById(R.id.textViewUser);
    		this.textViewDt = (TextView) itemView.findViewById(R.id.textViewDt);
    		this.imageViewRead = (ImageView) itemView.findViewById(R.id.imageViewRead);
    		
    		itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
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
			return true;
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
}
