
package com.edu.flinnt.api.models.erpModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("erp")
    @Expose
    private Erp erp;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Data() {
    }

    /**
     * 
     * @param erp
     */
    public Data(Erp erp) {
        super();
        this.erp = erp;
    }

    public Erp getErp() {
        return erp;
    }

    public void setErp(Erp erp) {
        this.erp = erp;
    }

}
