package com.edu.flinnt.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class UserProfileResponse extends BaseResponse{

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

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


    public class Data {

        @SerializedName("show_category")
        @Expose
        private Integer showCategory;
        @SerializedName("profile")
        @Expose
        private List<Profile> profile = null;
        @SerializedName("categories")
        @Expose
        private ArrayList<Category> categories = null;

        public Integer getShowCategory() {
            return showCategory;
        }

        public void setShowCategory(Integer showCategory) {
            this.showCategory = showCategory;
        }

        public List<Profile> getProfile() {
            return profile;
        }

        public void setProfile(List<Profile> profile) {
            this.profile = profile;
        }

        public ArrayList<Category> getCategories() {
            return categories;
        }

        public void setCategories(ArrayList<Category> categories) {
            this.categories = categories;
        }

        public class Profile {

            @SerializedName("first_name")
            @Expose
            private String firstName;
            @SerializedName("last_name")
            @Expose
            private String lastName;
            @SerializedName("institute_name")
            @Expose
            private String instituteName;
            @SerializedName("gender")
            @Expose
            private String gender;
            @SerializedName("birth_day")
            @Expose
            private String birthDay;
            @SerializedName("birth_month")
            @Expose
            private String birthMonth;
            @SerializedName("birth_year")
            @Expose
            private String birthYear;
            @SerializedName("city")
            @Expose
            private String city;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("mobile")
            @Expose
            private String mobile;

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getInstituteName() {
                return instituteName;
            }

            public void setInstituteName(String instituteName) {
                this.instituteName = instituteName;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getBirthDay() {
                return birthDay;
            }

            public void setBirthDay(String birthDay) {
                this.birthDay = birthDay;
            }

            public String getBirthMonth() {
                return birthMonth;
            }

            public void setBirthMonth(String birthMonth) {
                this.birthMonth = birthMonth;
            }

            public String getBirthYear() {
                return birthYear;
            }

            public void setBirthYear(String birthYear) {
                this.birthYear = birthYear;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

        }

        public class Category {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }
    }

}