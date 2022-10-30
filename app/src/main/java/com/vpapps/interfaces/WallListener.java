package com.vpapps.interfaces;

import com.vpapps.items.ItemWallpaper;

import java.util.ArrayList;

public interface WallListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListCat, int totalNumber);
}