package com.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.screenie.R;
import com.app.items.ItemUser;

public class SharedPref {

    private Context context;
    private Methods methods;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String TAG_UID = "uid" ,TAG_USERNAME = "name", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_REMEMBER = "rem",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", TAG_WALL_TYPE = "wallType", TAG_LOGIN_TYPE = "loginType", TAG_AUTH_ID = "auth_id", TAG_NIGHT_MODE = "nightmode", TAG_LIGHT_MODE="lightmode";

    public SharedPref(Context context) {
        this.context = context;
        methods = new Methods(context);
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public Boolean getIsGIF() {
        return sharedPreferences.getBoolean("gif", true);
    }

    public void setIsGIF(Boolean gif) {
        editor.putBoolean("gif", gif);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password, String loginType) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, methods.encrypt(itemUser.getId()));
        editor.putString(TAG_USERNAME, methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, methods.encrypt(itemUser.getEmail()));
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, methods.encrypt(password));
        editor.putString(TAG_LOGIN_TYPE, methods.encrypt(loginType));
        editor.putString(TAG_AUTH_ID, methods.encrypt(itemUser.getAuthID()));
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Constant.itemUser = new ItemUser(methods.decrypt(sharedPreferences.getString(TAG_UID,"")), methods.decrypt(sharedPreferences.getString(TAG_USERNAME,"")), methods.decrypt(sharedPreferences.getString(TAG_EMAIL,"")), methods.decrypt(sharedPreferences.getString(TAG_MOBILE,"")), methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,"")), methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,"")));
    }

    public String getEmail() {
            return methods.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public String getPassword() {
        return methods.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean isRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public String getWallType() {
        String wallType = sharedPreferences.getString(TAG_WALL_TYPE,Constant.TAG_PORTRAIT);

        if(wallType.equals(context.getString(R.string.portrait)) && Constant.isPortrait) {
            return wallType;
        } else if(wallType.equals(context.getString(R.string.landscape)) && Constant.isLandscape) {
            return wallType;
        } else if(wallType.equals(context.getString(R.string.square)) && Constant.isSquare) {
            return wallType;
        } else {
            return context.getString(R.string.portrait);
        }
    }

    public void setWallType(String wallType) {
        editor.putString(TAG_WALL_TYPE, wallType);
        editor.apply();
    }

    public String getLoginType() {
        return methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,""));
    }

    public String getAuthID() {
        return methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,""));
    }

    public String getDarkMode() {
        return sharedPreferences.getString(TAG_NIGHT_MODE, Constant.DARK_MODE_OFF);
    }

    public void setDarkMode(String nightMode) {
        editor.putString(TAG_NIGHT_MODE, nightMode);
        editor.apply();
    }
}