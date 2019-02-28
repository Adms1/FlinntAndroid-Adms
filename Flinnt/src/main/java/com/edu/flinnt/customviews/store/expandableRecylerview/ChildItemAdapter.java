package com.edu.flinnt.customviews.store.expandableRecylerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.edu.flinnt.R;
import com.edu.flinnt.helper.listner.store.FilterListener;
import com.edu.flinnt.models.store.FilterDataModel;
import com.edu.flinnt.util.store.StoreConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildItemAdapter<T> extends RecyclerView.Adapter<ChildItemAdapter.CustomViewHolder> {

    private List<T> childDataList = new ArrayList<T>();
    private Context context;
    private int viewType;

    private FilterListener filterListener;
    private HashMap<String,String> authorMap= new HashMap<>();
    private HashMap<String,String> langMap= new HashMap<>();
    private HashMap<String,String> boardMap= new HashMap<>();
    private HashMap<String,String> categoryMap= new HashMap<>();
    private HashMap<String,String> publisherMap= new HashMap<>();
    private HashMap<String,String> formatMap= new HashMap<>();


    public ChildItemAdapter(Context context,List<T> authorList,int viewType,FilterListener filterListener) {
        this.childDataList = authorList;
        this.viewType = viewType;
        this.filterListener = filterListener;
    }

    @Override
    public ChildItemAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        i = this.viewType;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_filter_checkable_item, null);
        ChildItemAdapter.CustomViewHolder viewHolder = new ChildItemAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChildItemAdapter.CustomViewHolder customViewHolder, int i) {

        switch (this.viewType){
            case 1:
                final FilterDataModel.Language language = (FilterDataModel.Language) childDataList.get(i);
                customViewHolder.cbNAme.setText(language.getLanguageName());
                customViewHolder.cbNAme.setTag(language.getLanguageId());
                customViewHolder.cbNAme.setChecked(language.isChecked());


                if(StoreConstants.Lang_ids.contains(String.valueOf(language.getLanguageId()))){
                    language.setChecked(true);
                    customViewHolder.cbNAme.setChecked(language.isChecked());

                }

                customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            langMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.LANG,langMap);
                            language.setChecked(true);

                        }else {
                            langMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.LANG,langMap);
                            language.setChecked(false);

                        }
                    }
                });

                break;
            case 2:
                final FilterDataModel.Author authorData = (FilterDataModel.Author) childDataList.get(i);
                customViewHolder.cbNAme.setText(authorData.getAuthorName());
                customViewHolder.cbNAme.setTag(authorData.getAuthorId());
                customViewHolder.cbNAme.setChecked(authorData.isChecked());

                if(StoreConstants.Author_ids.contains(String.valueOf(authorData.getAuthorId()))){
                    authorData.setChecked(true);
                    customViewHolder.cbNAme.setChecked(authorData.isChecked());
                }


                customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            authorMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.AUTHOR,authorMap);
                            authorData.setChecked(true);
                        }else {
                            authorMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.AUTHOR,authorMap);
                            authorData.setChecked(false);
                        }
                    }
                });

                break;
            case 3:
                final FilterDataModel.Board board = (FilterDataModel.Board) childDataList.get(i);
                customViewHolder.cbNAme.setText(board.getBoardName());
                customViewHolder.cbNAme.setTag(board.getBoardId());
                customViewHolder.cbNAme.setChecked(board.isChecked());


                if(StoreConstants.Board_ids.contains(String.valueOf(board.getBoardId()))){
                    board.setChecked(true);
                    customViewHolder.cbNAme.setChecked(board.isChecked());
                }


                customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            boardMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.BOARD,boardMap);
                            board.setChecked(true);
                        }else {
                            boardMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.BOARD,boardMap);
                            board.setChecked(false);

                        }
                    }
                });
                break;

                case 4:
                    final FilterDataModel.Category category = (FilterDataModel.Category) childDataList.get(i);
                    customViewHolder.cbNAme.setText(category.getCategoryName());
                    customViewHolder.cbNAme.setTag(category.getCategoryTreeId());
                    customViewHolder.cbNAme.setChecked(category.isChecked());

                    if(StoreConstants.Category_Tree_id.contains(String.valueOf(category.getCategoryTreeId()))){
                        category.setChecked(true);
                        customViewHolder.cbNAme.setChecked(category.isChecked());
                    }


                    customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                categoryMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                                filterListener.onFilterValuesChanged(FilterListener.FilterType.CATEGORY,boardMap);
                                category.setChecked(true);
                            }else {
                                categoryMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                                filterListener.onFilterValuesChanged(FilterListener.FilterType.CATEGORY,boardMap);
                                category.setChecked(false);

                            }
                        }
                    });

                break;

                case 5:
                    final FilterDataModel.Publisher publisher = (FilterDataModel.Publisher)childDataList.get(i);
                    customViewHolder.cbNAme.setText(publisher.getPublisherName());
                    customViewHolder.cbNAme.setTag(publisher.getPublisherId());
                    customViewHolder.cbNAme.setChecked(publisher.isChecked());

                    if(StoreConstants.Publisher_ids.contains(String.valueOf(publisher.getPublisherId()))){
                        publisher.setChecked(true);
                        customViewHolder.cbNAme.setChecked(publisher.isChecked());
                    }

                    customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                publisherMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                                filterListener.onFilterValuesChanged(FilterListener.FilterType.PUBLISHER,publisherMap);
                                publisher.setChecked(true);
                            }else {
                                publisherMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                                filterListener.onFilterValuesChanged(FilterListener.FilterType.PUBLISHER,publisherMap);
                                publisher.setChecked(false);


                            }
                        }
                    });


                    break;

            case 6:
                final FilterDataModel.BookFormatDataModel formatData = (FilterDataModel.BookFormatDataModel)childDataList.get(i);
                customViewHolder.cbNAme.setText(formatData.getValue());
                customViewHolder.cbNAme.setTag(formatData.getKey());
                customViewHolder.cbNAme.setChecked(formatData.isChecked());

                if(StoreConstants.Formats.contains(String.valueOf(formatData.getKey()))){
                    formatData.setChecked(true);
                    customViewHolder.cbNAme.setChecked(formatData.isChecked());
                }

                customViewHolder.cbNAme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            formatMap.put(String.valueOf(customViewHolder.cbNAme.getTag()),String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.FORMAT,formatMap);
                            formatData.setChecked(true);
                        }else {
                            formatMap.remove(String.valueOf(customViewHolder.cbNAme.getTag()));
                            filterListener.onFilterValuesChanged(FilterListener.FilterType.FORMAT,formatMap);
                            formatData.setChecked(false);
                        }
                    }
                });


                break;
        }


    }

    /**
     * Counts total number of alerts
     * @return total number of alerts
     */
    public int getItemCount() {
        return (null != childDataList ? childDataList.size() : 0);
    }

    public void addItems(ArrayList<T> items) {
        childDataList.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData(){
        childDataList.clear();
        notifyDataSetChanged();
    }



    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected CheckBox cbNAme;
        public CustomViewHolder(View itemView) {
            super(itemView);
            cbNAme = (CheckBox) itemView.findViewById(R.id.checkable_item);
        }

    }


}
