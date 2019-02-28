package com.edu.flinnt.customviews.store.expandableRecylerview.listner;

public interface ExpandCollapseListener {

    void onGroupExpanded(int positionStart, int itemCount);
    void onGroupCollapsed(int positionStart, int itemCount);

}
