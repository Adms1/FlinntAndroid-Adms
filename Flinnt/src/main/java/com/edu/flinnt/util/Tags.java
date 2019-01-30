package com.edu.flinnt.util;

/**
 * Tags object class
 */
public class Tags {
	
	public String tagName;
	public String tagID;
	public Boolean isContactChecked = false,isHeader=false;
	
	public Tags() {
    }
	
	public Tags(String name, String iD) {
		tagName = name;
		tagID = iD;
    }

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagID() {
		return tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

}
