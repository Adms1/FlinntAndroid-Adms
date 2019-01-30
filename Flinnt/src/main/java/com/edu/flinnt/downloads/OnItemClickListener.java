package com.edu.flinnt.downloads;

import android.view.View;


public interface OnItemClickListener<T> {
    void onItemClick(View v, int position, T t);
}
