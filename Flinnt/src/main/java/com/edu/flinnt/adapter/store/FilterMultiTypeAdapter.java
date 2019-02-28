package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.customviews.store.rangeseekbar.OnRangeChangedListener;
import com.edu.flinnt.customviews.store.rangeseekbar.RangeSeekBar;
import com.edu.flinnt.customviews.store.expandableRecylerview.ChildItemAdapter;
import com.edu.flinnt.customviews.store.expandableRecylerview.ExpandableGroup;
import com.edu.flinnt.customviews.store.expandableRecylerview.ExpandableListPosition;
import com.edu.flinnt.customviews.store.expandableRecylerview.MultiTypeExpandableRecyclerViewAdapter;
import com.edu.flinnt.customviews.store.expandableRecylerview.model.FilterModel;
import com.edu.flinnt.customviews.store.expandableRecylerview.model.GroupDataModel;
import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.ChildViewHolder;
import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.GroupViewHolder;
import com.edu.flinnt.helper.listner.store.FilterListener;
import com.edu.flinnt.protocol.BrowseCoursesRequest;

import java.util.HashMap;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class FilterMultiTypeAdapter extends MultiTypeExpandableRecyclerViewAdapter<FilterMultiTypeAdapter.ParentViewHolder,ChildViewHolder>{

    public static final int CHECK_ITEM_VIEW = 0;
    public static final int PRICE_ITEM_VIEW = 101;
    public static final int AVG_REVIEW_ITEM_VIEW = 303;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    private ChildItemAdapter childItemAdapter;
    private Context context;
    private FilterListener filterListener;
    private HashMap<String,String> priceValues = new HashMap<>();


    public FilterMultiTypeAdapter(Context context,List<FilterModel> groups,FilterListener filterListener) {
        super(groups);
        this.context  = context;
        this.filterListener = filterListener;
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_item, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent,int viewType) {
        switch (viewType) {
            case CHECK_ITEM_VIEW:
                View artist = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_group_item,parent, false);
                return new ChildViewHolder12(artist);
            case PRICE_ITEM_VIEW:
                View favorite = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_price, parent, false);
                return new ChildViewHolder13(favorite);
            case AVG_REVIEW_ITEM_VIEW:
                View review = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_reviews,parent, false);
                return new ChildViewHolder14(review);
            default:
                throw new IllegalArgumentException("Invalid viewType");
        }
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        int viewType = getItemViewType(flatPosition);
        final GroupDataModel checkableItem = (GroupDataModel) group.getItems().get(childIndex);

        switch (viewType) {
            case CHECK_ITEM_VIEW:

                if(checkableItem.getTitle().equalsIgnoreCase("Languages")){
                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),1,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);

                }else if(checkableItem.getTitle().equalsIgnoreCase("Author")){

                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),2,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);


                }else if(checkableItem.getTitle().equalsIgnoreCase("Board")){
                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),3,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);

                }else if(checkableItem.getTitle().equalsIgnoreCase("Categories")){

                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),4,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);


                }else if(checkableItem.getTitle().equalsIgnoreCase("Publisher")){

                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),5,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);


                }else if(checkableItem.getTitle().equalsIgnoreCase("Format")){
                    childItemAdapter = new ChildItemAdapter(context,checkableItem.getList(),6,filterListener);
                    ((ChildViewHolder12) holder).rvChildItems.setAdapter(childItemAdapter);

                } else if(checkableItem.getTitle().equalsIgnoreCase("Price")){



                }else if(checkableItem.getTitle().equalsIgnoreCase("Avg. Customers Review")){



                }
                break;

            case PRICE_ITEM_VIEW:
                ((ChildViewHolder13)holder).priceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                ((ChildViewHolder13)holder).rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
                    @Override
                    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                        try {

                            if (isFromUser) {
                                String minValue = String.valueOf((int) Math.floor(leftValue));
                                String maxValue = String.valueOf((int) Math.floor(rightValue));

                                priceValues.put(BrowseCoursesRequest.STORE_FILTER_PRICE_MIN_KEY,minValue);
                                priceValues.put(BrowseCoursesRequest.STORE_FILTER_PRICE_MAX_KEY,maxValue);

                                ((ChildViewHolder13)holder).tvMin.setText("Min ("+context.getString(R.string.currency)+minValue+")");
                                ((ChildViewHolder13)holder).tvMax.setText("Max ("+context.getString(R.string.currency)+maxValue+")");
                                filterListener.onFilterValuesChanged(FilterListener.FilterType.PRICE,priceValues);

                            }
                        }catch (Exception e){

                        }


                    }

                    @Override
                    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

                    }

                    @Override
                    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

                    }
                });
                break;

            case AVG_REVIEW_ITEM_VIEW:
                //do stuff

                break;
        }
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.filtertitle.setText(group.getTitle());
    }

    @Override
    public int getChildViewType(int position, ExpandableGroup group, int childIndex) {
        GroupDataModel filterModel = (GroupDataModel)group.getItems().get(childIndex);
        if(filterModel.getChildViewType() == CHECK_ITEM_VIEW){
            return CHECK_ITEM_VIEW;
        }else if(filterModel.getChildViewType() == PRICE_ITEM_VIEW){
            return  PRICE_ITEM_VIEW;
        }else if(filterModel.getChildViewType() == AVG_REVIEW_ITEM_VIEW){
            return AVG_REVIEW_ITEM_VIEW;
        }
        return CHECK_ITEM_VIEW;
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType == ExpandableListPosition.GROUP;
    }

    @Override
    public boolean isChild(int viewType) {
        return viewType == CHECK_ITEM_VIEW || viewType == PRICE_ITEM_VIEW || viewType == AVG_REVIEW_ITEM_VIEW;
    }


    public class ChildViewHolder12 extends ChildViewHolder {

        private RecyclerView rvChildItems;

        public ChildViewHolder12(View itemView) {
            super(itemView);
            rvChildItems = (RecyclerView) itemView.findViewById(R.id.rv_childlist);
        }

    }

    public class ChildViewHolder13 extends ChildViewHolder {

        private AppCompatSeekBar priceSeekbar;
        private RangeSeekBar rangeSeekBar;
        private TextView tvMin,tvMax;

        public ChildViewHolder13(View itemView) {
            super(itemView);
            priceSeekbar = (AppCompatSeekBar)itemView.findViewById(R.id.price_seekbar);
            rangeSeekBar = (RangeSeekBar)itemView.findViewById(R.id.seekbar3);
            tvMin = (TextView)itemView.findViewById(R.id.tv_minprice);
            tvMax = (TextView)itemView.findViewById(R.id.tv_maxprice);

        }

    }

    public class ChildViewHolder14 extends ChildViewHolder {

        public ChildViewHolder14(View itemView) {
            super(itemView);
        }

    }
    public class ParentViewHolder extends GroupViewHolder {

        private TextView filtertitle;
        private ImageView arrow;


        public ParentViewHolder(View itemView) {
            super(itemView);
            filtertitle = (TextView) itemView.findViewById(R.id.tv_filter_name);
            arrow = (ImageView)itemView.findViewById(R.id.iv_arrow);
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate = new RotateAnimation(360, -180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate = new RotateAnimation(-180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }
}
