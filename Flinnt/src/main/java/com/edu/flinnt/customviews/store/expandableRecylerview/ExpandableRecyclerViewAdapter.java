package com.edu.flinnt.customviews.store.expandableRecylerview;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.edu.flinnt.customviews.store.expandableRecylerview.listner.ExpandCollapseListener;
import com.edu.flinnt.customviews.store.expandableRecylerview.listner.GroupExpandCollapseListener;
import com.edu.flinnt.customviews.store.expandableRecylerview.listner.OnGroupClickListener;
import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.ChildViewHolder;
import com.edu.flinnt.customviews.store.expandableRecylerview.viewholders.GroupViewHolder;

import java.util.List;

public abstract class ExpandableRecyclerViewAdapter<GVH extends GroupViewHolder,CVH extends ChildViewHolder> extends RecyclerView.Adapter implements ExpandCollapseListener,OnGroupClickListener {

private static final String EXPAND_STATE_MAP = "expandable_recyclerview_adapter_expand_state_map";

protected ExpandableList expandableList;
private ExpandCollapseController expandCollapseController;

private OnGroupClickListener groupClickListener;
private GroupExpandCollapseListener expandCollapseListener;

 public ExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
         this.expandableList = new ExpandableList(groups);
         this.expandCollapseController = new ExpandCollapseController(expandableList, this);
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                case ExpandableListPosition.GROUP:
                GVH gvh = onCreateGroupViewHolder(parent, viewType);
                gvh.setOnGroupClickListener((OnGroupClickListener) this);
                return gvh;
                case ExpandableListPosition.CHILD:
                CVH cvh = onCreateChildViewHolder(parent, viewType);
                        return cvh;
                default:
                        throw new IllegalArgumentException("viewType is not valid");
                        }
                }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
                ExpandableGroup group = expandableList.getExpandableGroup(listPos);
                switch (listPos.type) {
                case ExpandableListPosition.GROUP:
                onBindGroupViewHolder((GVH) holder, position, group);

                if (isGroupExpanded(group)) {
                ((GVH) holder).expand();
                } else {
                ((GVH) holder).collapse();
                }
                break;
                case ExpandableListPosition.CHILD:
                onBindChildViewHolder((CVH) holder, position, group, listPos.childPos);
                break;
                }
                }


        @Override
        public int getItemCount() {
                return expandableList.getVisibleItemCount();
        }


        @Override
        public int getItemViewType(int position) {
                return expandableList.getUnflattenedPosition(position).type;
        }


        @Override
        public void onGroupExpanded(int positionStart, int itemCount) {
                //update header
                int headerPosition = positionStart - 1;
                notifyItemChanged(headerPosition);

                // only insert if there items to insert
                if (itemCount > 0) {
                        notifyItemRangeInserted(positionStart, itemCount);
                        if (expandCollapseListener != null) {
                        int groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos;
                        expandCollapseListener.onGroupExpanded(getGroups().get(groupIndex));
                        }
                }
        }


        @Override
        public void onGroupCollapsed(int positionStart, int itemCount) {
                //update header
                int headerPosition = positionStart - 1;
                notifyItemChanged(headerPosition);

                // only remote if there items to remove
                if (itemCount > 0) {
                notifyItemRangeRemoved(positionStart, itemCount);
                if (expandCollapseListener != null) {
                //minus one to return the position of the header, not first child
                int groupIndex = expandableList.getUnflattenedPosition(positionStart - 1).groupPos;
                expandCollapseListener.onGroupCollapsed(getGroups().get(groupIndex));
                }
                }
        }


@Override
public boolean onGroupClick(int flatPos) {
        if (groupClickListener != null) {
        groupClickListener.onGroupClick(flatPos);
        }
        return expandCollapseController.toggleGroup(flatPos);
        }

/**
 * @param flatPos The flat list position of the group
 * @return true if the group is expanded, *after* the toggle, false if the group is now collapsed
 */
public boolean toggleGroup(int flatPos) {
        return expandCollapseController.toggleGroup(flatPos);
        }

/**
 * @param group the {@link ExpandableGroup} being toggled
 * @return true if the group is expanded, *after* the toggle, false if the group is now collapsed
 */
public boolean toggleGroup(ExpandableGroup group) {
        return expandCollapseController.toggleGroup(group);
        }

/**
 * @param flatPos the flattened position of an item in the list
 * @return true if {@code group} is expanded, false if it is collapsed
 */
        public boolean isGroupExpanded(int flatPos) {
                return expandCollapseController.isGroupExpanded(flatPos);
                }
        public boolean isGroupExpanded(ExpandableGroup group) {
                return expandCollapseController.isGroupExpanded(group);
        }


        public void onSaveInstanceState(Bundle savedInstanceState) {
                savedInstanceState.putBooleanArray(EXPAND_STATE_MAP, expandableList.expandedGroupIndexes);
                }


        public void onRestoreInstanceState(Bundle savedInstanceState) {
                if (savedInstanceState == null || !savedInstanceState.containsKey(EXPAND_STATE_MAP)) {
                return;
                }
                expandableList.expandedGroupIndexes = savedInstanceState.getBooleanArray(EXPAND_STATE_MAP);
                notifyDataSetChanged();
                }

        public void setOnGroupClickListener(OnGroupClickListener listener) {
                groupClickListener = listener;
                }

        public void setOnGroupExpandCollapseListener(GroupExpandCollapseListener listener) {
                expandCollapseListener = listener;
                }

/**
 * The full list of {@link ExpandableGroup} backing this RecyclerView
 *
 * @return the list of {@link ExpandableGroup} that this object was instantiated with
 */
        public List<? extends ExpandableGroup> getGroups() {
                return expandableList.groups;
                }

/**
 * Called from {@link #onCreateViewHolder(ViewGroup, int)} when  the list item created is a group
 *
 * @param viewType an int returned by {@link ExpandableRecyclerViewAdapter#getItemViewType(int)}
 * @param parent the {@link ViewGroup} in the list for which a {@link GVH}  is being created
 * @return A {@link GVH} corresponding to the group list item with the  {@code ViewGroup} parent
 */
public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);

/**
 * Called from {@link #onCreateViewHolder(ViewGroup, int)} when the list item created is a child
 *
 * @param viewType an int returned by {@link ExpandableRecyclerViewAdapter#getItemViewType(int)}
 * @param parent the {@link ViewGroup} in the list for which a {@link CVH}  is being created
 * @return A {@link CVH} corresponding to child list item with the  {@code ViewGroup} parent
 */
public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);

/**
 * Called from onBindViewHolder(RecyclerView.ViewHolder, int) when the list item
 * bound to is a  child.
 * <p>
 * Bind data to the {@link CVH} here.
 *
 * @param holder The {@code CVH} to bind data to
 * @param flatPosition the flat position (raw index) in the list at which to bind the child
 * @param group The {@link ExpandableGroup} that the the child list item belongs to
 * @param childIndex the index of this child within it's {@link ExpandableGroup}
 */
public abstract void onBindChildViewHolder(CVH holder, int flatPosition, ExpandableGroup group,
        int childIndex);

/**
 * Called from onBindViewHolder(RecyclerView.ViewHolder, int) when the list item bound to is a
 * group
 * <p>
 * Bind data to the {@link GVH} here.
 *
 * @param holder The {@code GVH} to bind data to
 * @param flatPosition the flat position (raw index) in the list at which to bind the group
 * @param group The {@link ExpandableGroup} to be used to bind data to this {@link GVH}
 */
public abstract void onBindGroupViewHolder(GVH holder, int flatPosition, ExpandableGroup group);
}
