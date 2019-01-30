package com.edu.flinnt.gui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Template;

import java.util.ArrayList;

/**
 * mContentsAdapter class for templates
 */
public class SelectTemplateAdapter extends RecyclerView.Adapter<SelectTemplateAdapter.CustomViewHolder> {

	private ArrayList<Template> templateDataset;
	private OnItemClickListener mItemClickListener;
	private int categorySize;
	private String categoryID = "";

	public SelectTemplateAdapter(ArrayList<Template> myDataset) {

		templateDataset = myDataset;
	}

	public class CustomViewHolder extends RecyclerView.ViewHolder implements
	View.OnClickListener {

		protected TextView textViewTemplateCategory;
		protected TextView textViewTemplateName;
		protected LinearLayout layoutCategory;

		public CustomViewHolder(View itemView) {
			super(itemView);

			this.textViewTemplateCategory = (TextView) itemView.findViewById(R.id.textview_template_category);
			this.textViewTemplateName = (TextView) itemView.findViewById(R.id.textview_template_name);
			this.layoutCategory = (LinearLayout) itemView.findViewById(R.id.layout_category);
			this.textViewTemplateName.setOnClickListener(this);
		}
		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}
	}

	public void addItems(ArrayList<Template> items) {
		templateDataset.addAll(items);
		notifyDataSetChanged();
	}

	public Template getItem(int position) {
		if ( position >= 0 && position < templateDataset.size() ) {
		return templateDataset.get(position);
		}
		else return null; 
	}


	@Override
	public SelectTemplateAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_templete_item, null);

		CustomViewHolder viewHolder = new CustomViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {

		Template template = getItem(position);

		if( null != template ) {
			
			if(template.getIsHeader() == Flinnt.INVALID) {
				if(!categoryID.equals(template.getCategoryId())) {
					customViewHolder.layoutCategory.setVisibility(View.VISIBLE);
					customViewHolder.textViewTemplateCategory.setText(template.getTemplateCategory());
					template.setIsHeader(Flinnt.TRUE);
				}
				else {
					customViewHolder.layoutCategory.setVisibility(View.GONE);
					template.setIsHeader(Flinnt.FALSE);
				}
			}
			else if(template.getIsHeader() == Flinnt.TRUE) {
				customViewHolder.layoutCategory.setVisibility(View.VISIBLE);
				customViewHolder.textViewTemplateCategory.setText(template.getTemplateCategory());
			}
			else {
				customViewHolder.layoutCategory.setVisibility(View.GONE);
			}
			
			customViewHolder.textViewTemplateName.setText(template.getTemplateName().trim());
			categoryID = template.getCategoryId();
		}
	}

	public int getItemCount() {
		return templateDataset.size();
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public void setOnItemClickListener(
			final com.edu.flinnt.gui.SelectTemplateAdapter.OnItemClickListener onItemClickListener) {
		this.mItemClickListener = (OnItemClickListener) onItemClickListener;
	}



}
