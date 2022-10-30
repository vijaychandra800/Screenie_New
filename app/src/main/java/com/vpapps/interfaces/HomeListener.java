package com.vpapps.interfaces;

import com.vpapps.items.ItemCat;
import com.vpapps.items.ItemColors;
import com.vpapps.items.ItemWallpaper;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemWallpaper> arrayListFeatured, ArrayList<ItemWallpaper> arrayListLatest, ArrayList<ItemWallpaper> arrayListPopular, ArrayList<ItemWallpaper> arrayListRecent, ArrayList<ItemCat> arrayListCat, ArrayList<ItemColors> arrayListColors);
}