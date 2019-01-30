package com.edu.flinnt.helper.listner;

/**
 * Created by Nikhil Prajapati on 11-07-2018.
 */
public interface APIResponse {
    void onSuccess(String res);
    void onFailer(String res);

}
