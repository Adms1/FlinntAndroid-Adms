
package com.edu.flinnt.api.models.erpModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetERP {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetERP() {
    }

    /**
     * 
     * @param status
     * @param data
     */
    public GetERP(Integer status, Data data) {
        super();
        this.status = status;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
