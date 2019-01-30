package com.edu.flinnt.protocol.contentlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/*@Created by Chirag Prajapati   25/07/2018 5:30 pm*/
public class Service extends RealmObject implements Serializable {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("message")
@Expose
private String message;

/**
* No args constructor for use in serialization
* 
*/
public Service() {
}

/**
* 
* @param message
* @param status
*/
public Service(Integer status, String message) {
super();
this.status = status;
this.message = message;
}

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

}