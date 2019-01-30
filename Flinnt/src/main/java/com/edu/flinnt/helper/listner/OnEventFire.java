package com.edu.flinnt.helper.listner;

/**
 * Created by Nikhil Prajapati on 11-07-2018.
 */
public interface OnEventFire {
    void onEvent(int er);
    void onUnattempted();
    void onUnanswered();
    void onPendingReview();
    void onAttempted();

}
