package com.example.practiceapp.data;

public class DrawerItems {

    private int drawerImage;

    private String drawerTitle;

    public DrawerItems(){

    }

    public DrawerItems(int drawerImage, String drawerTitle) {
        this.drawerImage = drawerImage;
        this.drawerTitle = drawerTitle;
    }

    public int getDrawerImage() {
        return drawerImage;
    }

    public void setDrawerImage(int drawerImage) {
        this.drawerImage = drawerImage;
    }

    public String getDrawerTitle() {
        return drawerTitle;
    }

    public void setDrawerTitle(String drawerTitle) {
        this.drawerTitle = drawerTitle;
    }
}
