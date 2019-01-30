package com.edu.flinnt.adapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.expandableRecylerview.ExpandableGroup;
import com.edu.flinnt.expandableRecylerview.ExpandableRecyclerViewAdapter;
import com.edu.flinnt.expandableRecylerview.viewholders.ChildViewHolder;
import com.edu.flinnt.expandableRecylerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class FilterExpandAdapter extends ExpandableRecyclerViewAdapter<FilterExpandAdapter.ParentViewHolder, FilterExpandAdapter.ChildViewHolder12> {

    public FilterExpandAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public FilterExpandAdapter.ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_item, parent, false);
        return new FilterExpandAdapter.ParentViewHolder(view);
    }

    @Override
    public FilterExpandAdapter.ChildViewHolder12 onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_checkable_item, parent, false);
        return new FilterExpandAdapter.ChildViewHolder12(view);
    }

    @Override
    public void onBindChildViewHolder(FilterExpandAdapter.ChildViewHolder12 holder, int flatPosition, ExpandableGroup group,int childIndex) {
        final String checkableItem  = (String) group.getItems().get(childIndex);
        holder.childTextView.setChecked(false);
        holder.childTextView.setTag(childIndex);
        holder.childTextView.setText(checkableItem);
    }

    @Override
    public void onBindGroupViewHolder(FilterExpandAdapter.ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.filtertitle.setText(group.getTitle());
    }


    public class ChildViewHolder12 extends ChildViewHolder {

        private AppCompatCheckBox childTextView;

        public ChildViewHolder12(View itemView) {
            super(itemView);
            childTextView = (AppCompatCheckBox)itemView.findViewById(R.id.checkable_item);
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
            RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }
}
