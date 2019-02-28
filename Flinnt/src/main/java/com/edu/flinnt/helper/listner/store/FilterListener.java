package com.edu.flinnt.helper.listner.store;

import java.util.HashMap;
import java.util.List;

public interface FilterListener {
    public static enum FilterType {
        AUTHOR,
        LANG,
        FORMAT,
        BOARD,
        PRICE,
        CATEGORY,
        PUBLISHER};

    void onFilterValuesChanged(FilterType type,HashMap<String,String> data);
}
