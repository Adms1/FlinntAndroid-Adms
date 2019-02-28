package com.edu.flinnt.customviews.store.expandableRecylerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.ChildViewHolder;
import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.GroupViewHolder;

import java.util.List;

public abstract  class MultiTypeExpandableRecyclerViewAdapter <GVH extends GroupViewHolder, CVH extends ChildViewHolder>
        extends ExpandableRecyclerViewAdapter<GVH, CVH> {

    public MultiTypeExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isGroup(viewType)) {
            GVH gvh = onCreateGroupViewHolder(parent, viewType);
            gvh.setOnGroupClickListener(this);
            return gvh;
        } else if (isChild(viewType)) {
            CVH cvh = onCreateChildViewHolder(parent, viewType);
            return cvh;
        }
        throw new IllegalArgumentException("viewType is not valid");
    }

    /**
     * Implementation of Adapter.onBindViewHolder(RecyclerView.ViewHolder, int)
     * that determines if the list item is a group or a child and calls through
     * to the appropriate implementation of either {@link #onBindGroupViewHolder(GroupViewHolder,
     * int,
     * ExpandableGroup)}
     * or {@link #onBindChildViewHolder(ChildViewHolder, int, ExpandableGroup, int)}.
     *
     * @param holder Either the GroupViewHolder or the ChildViewHolder to bind data to
     * @param position The flat position (or index in the list of {@link
     * ExpandableList#getVisibleItemCount()} in the list at which to bind
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPos);
        if (isGroup(getItemViewType(position))) {
            onBindGroupViewHolder((GVH) holder, position, group);

            if (isGroupExpanded(group)) {
                ((GVH) holder).expand();
            } else {
                ((GVH) holder).collapse();
            }
        } else if (isChild(getItemViewType(position))) {
            onBindChildViewHolder((CVH) holder, position, group, listPos.childPos);
        }
    }

    /**
     * Gets the view type of the item at the given position.
     *
     * @param position The flat position in the list to get the view type of
     * @return if the flat position corresponds to a child item, this will return the value returned
     * by {@code getChildViewType}. if the flat position refers to a group item this will return the
     * value returned by {@code getGroupViewType}
     */
    @Override
    public int getItemViewType(int position) {
        ExpandableListPosition listPosition = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPosition);

        int viewType = listPosition.type;
        switch (viewType) {
            case ExpandableListPosition.GROUP:
                return getGroupViewType(position, group);
            case ExpandableListPosition.CHILD:
                return getChildViewType(position, group, listPosition.childPos);
            default:
                return viewType;
        }
    }


    public int getChildViewType(int position, ExpandableGroup group, int childIndex) {
        return super.getItemViewType(position);
    }


    public int getGroupViewType(int position, ExpandableGroup group) {
        return super.getItemViewType(position);
    }

    /**
     * @param viewType the int corresponding to the viewType of a {@code ExpandableGroup}
     * @return if a subclasses has *NOT* overridden {@code getGroupViewType} than the viewType for
     * the group is defaulted to {@link ExpandableListPosition#GROUP}
     */
    public boolean isGroup(int viewType) {
        return viewType == ExpandableListPosition.GROUP;
    }

    /**
     * @param viewType the int corresponding to the viewType of a child of a {@code ExpandableGroup}
     * @return if a subclasses has *NOT* overridden {@code getChildViewType} than the viewType for
     * the child is defaulted to {@link ExpandableListPosition#CHILD}
     */
    public boolean isChild(int viewType) {
        return viewType == ExpandableListPosition.CHILD;
    }
}
