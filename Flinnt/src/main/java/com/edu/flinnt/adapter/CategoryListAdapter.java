package com.edu.flinnt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.models.CategoryModel;


import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.SuggestionListViewHolder> {

    private List<CategoryModel> categoryModelList;
    private Context mContext;



    public class SuggestionListViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_category;
        public CheckBox category_chkbox;


        public SuggestionListViewHolder(View itemView) {
            super(itemView);
            txt_category = (TextView) itemView.findViewById(R.id.txt_category);
            category_chkbox = (CheckBox) itemView.findViewById(R.id.category_chkbox);
            category_chkbox.setClickable(false);
        }
    }

    public CategoryListAdapter(List<CategoryModel> categoryModelList, Context context) {
        this.categoryModelList = categoryModelList;
        this.mContext = context;
    }

    @Override
    public SuggestionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View new_suggestion_list_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list,parent,false);
        return new SuggestionListViewHolder(new_suggestion_list_view);
    }
    @Override
    public void onBindViewHolder(final SuggestionListViewHolder holder, final int position) {

        final CategoryModel categoryModel = categoryModelList.get(position);

        holder.txt_category.setText(categoryModel.getName());
        holder.category_chkbox.setChecked(categoryModelList.get(position).isSelected());
        holder.category_chkbox.setTag(position);
        //holder.category_chkbox.setChecked(categoryModel.isSelected());
        if (categoryModel.getSubscribed().equals("1")){
            Integer pos = (Integer) holder.category_chkbox.getTag();
            categoryModelList.get(pos).setSelected(true);
            holder.category_chkbox.setChecked(true);
        }

        /*holder.category_chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    categoryModel.setSelected(true);
                }else {
                    categoryModel.setSelected(false);
                }
            }
        });*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.category_chkbox.getTag();
                //Toast.makeText(mContext, categoryModelList.get(pos).getName() + " clicked!", Toast.LENGTH_SHORT).show();

                if (categoryModelList.get(pos).isSelected()) {
                    categoryModelList.get(pos).setSelected(false);
                    holder.category_chkbox.setChecked(false);
                } else {
                    categoryModelList.get(pos).setSelected(true);
                    holder.category_chkbox.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }
}