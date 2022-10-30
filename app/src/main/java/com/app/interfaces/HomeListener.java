package com.app.interfaces;

import com.app.items.ItemCat;
import com.app.items.ItemColors;
import com.app.items.ItemWallpaper;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemWallpaper> arrayListFeatured, ArrayList<ItemWallpaper> arrayListLatest, ArrayList<ItemWallpaper> arrayListPopular, ArrayList<ItemWallpaper> arrayListRecent, ArrayList<ItemCat> arrayListCat, ArrayList<ItemColors> arrayListColors);
}