package com.edu.flinnt.gui;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.flinnt.util.Tags;
import com.edu.flinnt.R;
import com.tokenautocomplete.FilteredArrayAdapter;



/**
 * Tag mContentsAdapter class
 */
public class TagAdapter extends FilteredArrayAdapter<Tags> {
	
	Tags[] mDataset; 

	public TagAdapter(Context context, int resource,
			Tags[] objects) {
		super(context, resource, objects);
		mDataset = objects;
	}
	/*
	@Override
	public void addAll(Tags... items) {
		// TODO Auto-generated method stub
		super.addAll(items);
		mDataset = items;
		notifyDataSetChanged();
	}
	*/
	
	public void addItems( Tags[] tags ){
		mDataset = tags;
		notifyDataSetChanged();
	}
	/*@Override
	public void add(Tags object) {
		// TODO Auto-generated method stub
		super.add(object);
		notifyDataSetChanged();
	}*/
	
	/*public Tags getItem(int position) {
		return mDataset[position];
	}*/
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = l.inflate(R.layout.tag_dropdown_layout, parent, false);
        }

        Tags p = getItem(position);
        if ( null != p ) {
        ((TextView)convertView.findViewById(R.id.name)).setText(p.getTagName());
        ((TextView)convertView.findViewById(R.id.email)).setText(p.getTagID());
	        return convertView;
        }
        else return null;

    }

	@Override
	protected boolean keepObject(Tags tag, String mask) {
		mask = mask.toLowerCase();
        return tag.getTagName().toLowerCase().startsWith(mask) || tag.getTagID().toLowerCase().startsWith(mask);
	}



}
