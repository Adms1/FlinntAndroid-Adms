package com.edu.flinnt.protocol;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by flinnt-android-2 on 26/11/16.
 */

public class PersonalInformationResponse extends BaseResponse {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * @return The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("profile_id")
        @Expose
        private Integer profileId;

        /**
         * @return The profileId
         */
        public Integer getProfileId() {
            return profileId;
        }

        /**
         * @param profileId The profile_id
         */
        public void setProfileId(Integer profileId) {
            this.profileId = profileId;
        }

    }
}
