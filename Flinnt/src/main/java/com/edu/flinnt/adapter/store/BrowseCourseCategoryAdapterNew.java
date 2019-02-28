package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.BrowseCourseCategoryMoreActivity;
import com.edu.flinnt.gui.BrowseCoursesListAdapter;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Helper;

import java.util.ArrayList;
import java.util.List;

import static com.edu.flinnt.protocol.BrowsableCourse.BUNDLE_LIST_KEY;

/**
 * Created by flinnt-android-2 on 30/5/17.
 * Adapter to display course category wise contains Recycler view, Category Text and more button.
 */

public class BrowseCourseCategoryAdapterNew<T> extends RecyclerView.Adapter<BrowseCourseCategoryAdapterNew.ItemRowHolder>  implements Filterable {

   // private ArrayList<CategoryDataModel> dataList;

    //08-01-2019 by vijay.
    private ArrayList<T> dataList  = new ArrayList<>();;
    private Context mContext;
    private int type;
    private ArrayList<T> filteredDataset;
    private BrowseCoursesListAdapterNew itemListDataAdapter;

    public BrowseCourseCategoryAdapterNew(Context context, ArrayList<T> data, int type) {
        this.mContext = context;
        dataList = data;
        filteredDataset = new ArrayList<T>();
        filteredDataset = dataList;
        this.type = type;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_course_horizontal_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(BrowseCourseCategoryAdapterNew.ItemRowHolder itemRowHolder, final int i) {
        //08-01-2019 by vijay

        try {
            if (filteredDataset.size() > 0) {
                if (type == 1) {

                    StoreModelResponse.Datum course = (StoreModelResponse.Datum) filteredDataset.get(i);
                    final String categotyName = course.getStandardName();
                    final String categoryId = String.valueOf(course.getStandardId());
                    final List<StoreModelResponse.Course> courses = course.getCourses();
                    itemRowHolder.titleTxt.setText(categotyName);

                    // BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext, singleSectionItems);

                    //08-01-2019 by vijay

                    if (courses.size() > 4) {
                        ArrayList<StoreModelResponse.Course> innerDataList = new ArrayList<>();

                        for (int count = 0; count < 4; count++) {
                            innerDataList.add(courses.get(count));
                        }
                        itemListDataAdapter = new BrowseCoursesListAdapterNew(mContext,innerDataList, type);
                        itemRowHolder.categoryRecycler.setHasFixedSize(true);
                        itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                        itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
                    } else {
                        itemListDataAdapter = new BrowseCoursesListAdapterNew(mContext,courses,type);
                        itemRowHolder.categoryRecycler.setHasFixedSize(true);
                        itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                        itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
                    }

                    if (courses.size() > 4) {
                        itemRowHolder.moreTxt.setVisibility(View.VISIBLE);
                    } else {
                        itemRowHolder.moreTxt.setVisibility(View.GONE);
                    }
                    //itemRowHolder.moreTxt.setText("More");
                    itemRowHolder.moreTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Helper.isConnected()) {
                                Intent categoryIntent = new Intent(mContext, BrowseCourseCategoryMoreActivity.class);
                                categoryIntent.putExtra(BrowsableCourse.CATEGORY_ID_KEY,categoryId);
                                categoryIntent.putExtra(BrowsableCourse.CATEGORY_NAME_KEY,categotyName);
                                categoryIntent.putExtra("type",type);
                                categoryIntent.putParcelableArrayListExtra(BUNDLE_LIST_KEY, (ArrayList<? extends Parcelable>) courses);
                                mContext.startActivity(categoryIntent);
                            } else {
                                Helper.showNetworkAlertMessage(mContext);
                            }
                        }
                    });
                } else if (type == 2) {

                    StoreBookSetResponse.Datum course = (StoreBookSetResponse.Datum) filteredDataset.get(i);
                    final String categotyName = course.getStandardName();
                    final String categoryId = String.valueOf(course.getStandardId());
                    final List<StoreBookSetResponse.Course> courses = course.getCourses();
                    itemRowHolder.titleTxt.setText(categotyName);

                    // BrowseCoursesListAdapter itemListDataAdapter = new BrowseCoursesListAdapter(mContext, singleSectionItems);

                    //08-01-2019 by vijay

                    if (courses.size() > 4) {
                        ArrayList<StoreBookSetResponse.Course> innerDataList = new ArrayList<>();

                        for (int count = 0; count < 4; count++) {
                            innerDataList.add(courses.get(count));
                        }
                        itemListDataAdapter = new BrowseCoursesListAdapterNew(mContext, innerDataList, type);
                        itemRowHolder.categoryRecycler.setHasFixedSize(true);
                        itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                        itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
                    } else {
                        itemListDataAdapter = new BrowseCoursesListAdapterNew(mContext,courses,type);
                        itemRowHolder.categoryRecycler.setHasFixedSize(true);
                        itemRowHolder.categoryRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        itemRowHolder.categoryRecycler.setAdapter(itemListDataAdapter);
                        itemRowHolder.categoryRecycler.setNestedScrollingEnabled(false);
                    }

                    if (courses.size() > 4) {
                        itemRowHolder.moreTxt.setVisibility(View.VISIBLE);
                    } else {
                        itemRowHolder.moreTxt.setVisibility(View.GONE);
                    }
                    //itemRowHolder.moreTxt.setText("More");
                    itemRowHolder.moreTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Helper.isConnected()) {
                                Intent categoryIntent = new Intent(mContext,BrowseCourseCategoryMoreActivity.class);
                                categoryIntent.putExtra(BrowsableCourse.CATEGORY_ID_KEY, categoryId);
                                categoryIntent.putExtra(BrowsableCourse.CATEGORY_NAME_KEY, categotyName);
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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void setFilter(String searchQuery) {
        ArrayList<T> filteredList;
        if (searchQuery.isEmpty()) {
            filteredDataset = dataList;
        }else {
            filteredList = new ArrayList<T>();
            if(type == 1) {
                for (int count = 0; count < dataList.size(); count++) {
                    StoreModelResponse.Datum data = (StoreModelResponse.Datum) dataList.get(count);
                    List<StoreModelResponse.Course> courseData = new ArrayList<StoreModelResponse.Course>();
                    courseData = data.getCourses();
                    List<StoreModelResponse.Course> tempData = new ArrayList<StoreModelResponse.Course>();

                    for (int subcount = 0; subcount < courseData.size(); subcount++) {
                        if (courseData.get(subcount).getBookName().equalsIgnoreCase(searchQuery)) {
                            tempData.add(courseData.get(subcount));
                            data.setCourses(tempData);
                            filteredList.add((T) data);
                        }
                    }
                }
                filteredDataset = filteredList;
                notifyDataSetChanged();
            }
        }
    }




    @Override
    public Filter getFilter() {
        return new Filter() {
            ArrayList<T> filteredList;

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredDataset = dataList;
                } else {
                    filteredList = new ArrayList<T>();
                    if(type == 1) {
                        for(int count = 0;count<dataList.size();count++){
                            StoreModelResponse.Datum data  = (StoreModelResponse.Datum)dataList.get(count);
                            List<StoreModelResponse.Course> courseData = new ArrayList<StoreModelResponse.Course>();
                            courseData = data.getCourses();
                            List<StoreModelResponse.Course> tempData  = new ArrayList<StoreModelResponse.Course>();
                            for(int subcount = 0;subcount<courseData.size();subcount++){
                                if(courseData.get(subcount).getBookName().equalsIgnoreCase(charString) || courseData.get(subcount).getBookName().contains(charString)){
                                    tempData.add(courseData.get(subcount));
                                    data.setCourses(tempData);
                                    filteredList.add((T)data);
                                }
                            }
                        }


                    }else if(type == 2){

                        for(int count = 0;count<dataList.size();count++){

                            StoreBookSetResponse.Course data  = (StoreBookSetResponse.Course)dataList.get(count);
                            if(data.getBookSetName().contains(charSequence)) {
                               //filteredList.add(dataList.get(count));
                            }
                        }
                    }
                    filteredDataset = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDataset;
                filterResults.count = filteredDataset.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                filteredDataset = filteredList;
//                notifyDataSetChanged();

                if(!TextUtils.isEmpty(charSequence)) {
                    filteredDataset = (ArrayList<T>) filterResults.values;
                    notifyDataSetChanged();
                }else{
                    notifyDataSetChanged();
                }
//                if(itemListDataAdapter != null){
//                    String charString = charSequence.toString();
//                    itemListDataAdapter.setFilter(charString);
//                }
            }
        };

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
        filteredDataset = dataList;
        notifyDataSetChanged();
        //notifyItemRangeInserted(positionStart, size);
    }
    @Override
    public int getItemCount() {
        return (null != filteredDataset ? filteredDataset.size() : 0);
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
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public boolean getSearchMode() {
        return false;
    }

}