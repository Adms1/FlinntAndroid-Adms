package com.edu.flinnt.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.BrowseCourseCategoryMoreActivity;
import com.edu.flinnt.gui.BrowseCoursesListAdapter;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CategoryDataModel;
import com.edu.flinnt.util.Helper;

import java.util.ArrayList;

/**
 * Created by flinnt-android-2 on 30/5/17.
 * Adapter to display course category wise contains Recycler view, Category Text and more button.
 */

public class BrowseCourseCategoryAdapter extends RecyclerView.Adapter<BrowseCourseCategoryAdapter.ItemRowHolder> {

    private ArrayList<CategoryDataModel> dataList;
    private Context mContext;

    public BrowseCourseCategoryAdapter(Context context, ArrayList<CategoryDataModel> data) {
        this.mContext = context;
        dataList = new ArrayList<>();
        dataList.addAll(data);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_course_horizontal_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {

        final String categotyName = dataList.get(i).getCategoryTitle();
        final String categoryId = dataList.get(i).getCategoryId();
        ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();
        itemRowHolder.titleTxt.setText(categotyName);
        BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext, singleSectionItems);
        itemRowHolder.categoryRecycler.setHasFixedSize(true);
        itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
        itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
        if (singleSectionItems.size() > 2) {
            itemRowHolder.moreTxt.setVisibility(View.VISIBLE);
        } else {
            itemRowHolder.moreTxt.setVisibility(View.GONE);
        }
        itemRowHolder.moreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    Intent categoryIntent = new Intent(mContext, BrowseCourseCategoryMoreActivity.class);
                    categoryIntent.putExtra(BrowsableCourse.CATEGORY_ID_KEY, categoryId);
                    categoryIntent.putExtra(BrowsableCourse.CATEGORY_NAME_KEY, categotyName);
                    mContext.startActivity(categoryIntent);
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }
            }
        });
    }

    public void addItems(ArrayList<CategoryDataModel> items) {
        int positionStart = dataList.size() + 1; // position after last course added
        int size = items.size();
        dataList.addAll(items);
        notifyItemRangeInserted(positionStart, size);
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView titleTxt;
        protected RecyclerView categoryRecycler;
        protected TextView moreTxt;

        public ItemRowHolder(View view) {
            super(view);
            this.titleTxt = (TextView) view.findViewById(R.id.category_txt);
            this.categoryRecycler = (RecyclerView) view.findViewById(R.id.category_courses_recycler);
            this.moreTxt = (TextView) view.findViewById(R.id.more_txt);

        }
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public boolean getSearchMode() {
        return false;
    }

}