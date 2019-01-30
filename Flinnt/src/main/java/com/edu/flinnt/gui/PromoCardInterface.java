package com.edu.flinnt.gui;

import android.support.v7.widget.CardView;

/**
 * Created by flinnt-android-3 on 13/1/17.
 */
public interface PromoCardInterface {

        int MAX_ELEVATION_FACTOR = 8;
        float getBaseElevation();
        CardView getCardViewAt(int position);
        int getCount();
}
