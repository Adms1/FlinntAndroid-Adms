package com.edu.flinnt.protocol.contentlist;

import io.realm.RealmObject;

/**
 * Created by flinnt-android-1 on 11/11/16.
 */

public class Statistics extends RealmObject {

    public String likes = "";
    public String comments = "";

    /**
     *
     * @return
     *     The likes
     */
    public String getLikes() {
        return likes;
    }

    /**
     *
     * @param likes
     *     The likes
     */
    public void setLikes(String likes) {
        this.likes = likes;
    }

    /**
     *
     * @return
     *     The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     *     The comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

}