package com.edu.flinnt.gui;

/**
 * Object class for join community card item
 */
public class JoinCommunityCardViewItem {

	private String courseName;
	private String communityName;
	private String totalUsers;
	
	public JoinCommunityCardViewItem() {
		courseName = "6TH-Std Maths Advance";
		communityName = "Community Name";
		totalUsers = "145";
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(String totalUsers) {
		this.totalUsers = totalUsers;
	}
}
