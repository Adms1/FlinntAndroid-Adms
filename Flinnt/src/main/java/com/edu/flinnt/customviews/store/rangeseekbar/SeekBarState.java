package com.edu.flinnt.customviews.store.rangeseekbar;

public class SeekBarState {

    public String indicatorText;
    public float value; //now progress value
    public boolean isMin;
    public boolean isMax;

    @Override
    public String toString() {
        return "indicatorText: " + indicatorText + " ,isMin: " + isMin + " ,isMax: " + isMax;
    }
}
