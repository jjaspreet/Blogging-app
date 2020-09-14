package com.example.practiceapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.practiceapp.data.DrawerItems;

import java.util.ArrayList;
import java.util.List;

public class DrawerItemsCustomAdapter extends ArrayAdapter<DrawerItems> {

   private Context mContext;
   private int layoutResourceId;
   private List<DrawerItems> drawerItems = new ArrayList<>();

    public DrawerItemsCustomAdapter(Context mContext, int layoutResourceId, List<DrawerItems> drawerItems) {

        super(mContext, layoutResourceId, drawerItems);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.drawerItems = drawerItems;
    }



}
