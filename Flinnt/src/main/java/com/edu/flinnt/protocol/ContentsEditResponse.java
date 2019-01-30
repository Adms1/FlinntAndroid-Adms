package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by flinnt-android-2 on 16/11/16.
 */

public class ContentsEditResponse extends BaseResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("has_more")
        @Expose
        private Integer hasMore;
        @SerializedName("list")
        @Expose
        private ArrayList<Sections> list = new ArrayList<Sections>();

        /**
         * @return The hasMore
         */
        public Integer getHasMore() {
            return hasMore;
        }

        /**
         * @param hasMore The has_more
         */
        public void setHasMore(Integer hasMore) {
            this.hasMore = hasMore;
        }

        /**
         * @return The list
         */
        public ArrayList<Sections> getList() {
            return list;
        }

        public void setList(ArrayList<Sections> list) {
            this.list = list;
        }
    }
}