package com.edu.flinnt.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.BrowseCourseCategoryMoreActivity;
import com.edu.flinnt.gui.BrowseCoursesListAdapter;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CategoryDataModel;
import com.edu.flinnt.util.Helper;

import java.util.ArrayList;
import java.util.List;

import static com.edu.flinnt.protocol.BrowsableCourse.BUNDLE_LIST_KEY;

/**
 * Created by flinnt-android-2 on 30/5/17.
 * Adapter to display course category wise contains Recycler view, Category Text and more button.
 */

public class BrowseCourseCategoryAdapter<T> extends RecyclerView.Adapter<BrowseCourseCategoryAdapter.ItemRowHolder> {

   // private ArrayList<CategoryDataModel> dataList;

    //08-01-2019 by vijay.
    private ArrayList<T> dataList;
    private Context mContext;
    private int type;

    public BrowseCourseCategoryAdapter(Context context, ArrayList<T> data,int type) {
        this.mContext = context;
        dataList = new ArrayList<>();
        dataList.addAll(data);
        this.type = type;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_course_horizontal_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(BrowseCourseCategoryAdapter.ItemRowHolder itemRowHolder, final int i) {
        //08-01-2019 by vijay

        if(type == 1){

            StoreModelResponse.Datum course  = (StoreModelResponse.Datum) dataList.get(i);
            final String categotyName = course.getStandardName();
            final String categoryId = String.valueOf(course.getStandardId());
            final List<StoreModelResponse.Course>courses = course.getCourses();
            itemRowHolder.titleTxt.setText(categotyName);

            // BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext, singleSectionItems);

            //08-01-2019 by vijay

            if(courses.size() > 4) {
                ArrayList<StoreModelResponse.Course> innerDataList = new ArrayList<>();

                for (int count = 0; count < 4; count++) {
                    innerDataList.add(courses.get(count));
                }
                BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext,innerDataList,type);
                itemRowHolder.categoryRecycler.setHasFixedSize(true);
                itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
            }else{
                BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext,courses,type);
                itemRowHolder.categoryRecycler.setHasFixedSize(true);
                itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
            }

            if (courses.size() > 2) {
                itemRowHolder.moreTxt.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.moreTxt.setVisibility(View.GONE);
            }

            itemRowHolder.moreTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isConnected()) {
                        Intent categoryIntent = new Intent(mContext,BrowseCourseCategoryMoreActivity.class);
                        categoryIntent.putExtra(BrowsableCourse.CATEGORY_ID_KEY,categoryId);
                        categoryIntent.putExtra(BrowsableCourse.CATEGORY_NAME_KEY,categotyName);
                        categoryIntent.putExtra("type",type);
                        categoryIntent.putParcelableArrayListExtra(BUNDLE_LIST_KEY,(ArrayList<? extends Parcelable>) courses);
                        mContext.startActivity(categoryIntent);
                    } else {
                        Helper.showNetworkAlertMessage(mContext);
                    }
                }
            });
        }else if(type == 2){

            StoreBookSetResponse.Datum course  = (StoreBookSetResponse.Datum) dataList.get(i);
            final String categotyName = course.getStandardName();
            final String categoryId = String.valueOf(course.getStandardId());
            final List<StoreBookSetResponse.Course>courses = course.getCourses();
            itemRowHolder.titleTxt.setText(categotyName);

            // BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext, singleSectionItems);

            //08-01-2019 by vijay

            if(courses.size() > 4) {
                ArrayList<StoreBookSetResponse.Course> innerDataList = new ArrayList<>();

                for (int count = 0; count < 4; count++) {
                    innerDataList.add(courses.get(count));
                }
                BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext,innerDataList,type);
                itemRowHolder.categoryRecycler.setHasFixedSize(true);
                itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
            }else{
                BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext,courses,type);
                itemRowHolder.categoryRecycler.setHasFixedSize(true);
                itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
            }

            if (courses.size() > 2) {
                itemRowHolder.moreTxt.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.moreTxt.setVisibility(View.GONE);
            }

            itemRowHolder.moreTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isConnected()) {
                        Intent categoryIntent = new Intent(mContext,BrowseCourseCategoryMoreActivity.class);
                        categoryIntent.putExtra(BrowsableCourse.CATEGORY_ID_KEY,categoryId);
                        categoryIntent.putExtra(BrowsableCourse.CATEGORY_NAME_KEY,categotyName);
                        categoryIntent.putExtra("type",type);
                        categoryIntent.putParcelableArrayListExtra(BUNDLE_LIST_KEY,(ArrayList<? extends Parcelable>) courses);
                        mContext.startActivity(categoryIntent);
                    } else {
                        Helper.showNetworkAlertMessage(mContext);
                    }
                }
            });
        }




    }

//    public void addItems(ArrayList<CategoryDataModel> items) {
//        int positionStart = dataList.size() + 1; // position after last course added
//        int size = items.size();
//        dataList.addAll(items);
//        notifyItemRangeInserted(positionStart, size);
//    }

    //08-01-2019 by vijay.
    public void addItems(ArrayList<T> items) {
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