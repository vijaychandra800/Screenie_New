package com.app.interfaces;

import com.app.items.ItemWallpaper;

import java.util.ArrayList;

public interface WallListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListCat, int totalNumber);
}