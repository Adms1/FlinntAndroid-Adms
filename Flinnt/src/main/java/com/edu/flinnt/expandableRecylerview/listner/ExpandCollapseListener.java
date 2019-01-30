package com.edu.flinnt.expandableRecylerview.listner;

public interface ExpandCollapseListener {

    void onGroupExpanded(int positionStart, int itemCount);
    void onGroupCollapsed(int positionStart, int itemCount);

}
