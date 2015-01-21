package com.joost.navigationdrawer;

/**
 * Created by Joost on 04/01/2015.
 */
public class DrawerItem {
    private int iconId;
    private String title;
    private int viewType;


    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
