package com.edu.flinnt.customviews.store.expandableRecylerview.listner;

import com.edu.flinnt.customviews.store.expandableRecylerview.ExpandableGroup;

public interface GroupExpandCollapseListener {

    void onGroupExpanded(ExpandableGroup group);

    void onGroupCollapsed(ExpandableGroup group);
}
