package com.edu.flinnt.slidemenu;

import android.graphics.Bitmap;

import com.edu.flinnt.Flinnt;

/**
 * Navigation drawer menu item object class
 */
public class SlideMenuItem {
	
	private Bitmap icon;
	private String title;
	private String unread;
    private String firstName = "";
	private int iconID;
    private boolean isSelected;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public int getIconId() {
		return iconID;
	}

	public void setIconID(int icon) {
		this.iconID = icon;
	}

	public String getUnread() {
		return unread;
	}

	public int getUnreadCount() {
		int count = Flinnt.INVALID;
		try {
			if( null != unread ) {
				count = Integer.parseInt(unread);
			}
		} catch (Exception e) {
		}
		return count;
	}
	
	public void setUnread(String unread) {
		this.unread = unread;
	}

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
