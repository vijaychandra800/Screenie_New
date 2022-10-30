package com.app.interfaces;

import com.app.items.ItemGIF;

import java.util.ArrayList;

public interface GIFListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGIF> arrayListCat, int totalNumber);
}