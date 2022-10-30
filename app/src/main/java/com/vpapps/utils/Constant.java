package com.vpapps.utils;

import android.net.Uri;

import com.vpapps.hdwallpaper.BuildConfig;
import com.vpapps.items.ItemAbout;
import com.vpapps.items.ItemColors;
import com.vpapps.items.ItemGIF;
import com.vpapps.items.ItemUser;
import com.vpapps.items.ItemWallpaper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    //server url
    public static String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String METHOD_HOME = "get_home";
    public static final String METHOD_CAT = "get_category";
    public static final String METHOD_ABOUT = "get_app_details";

    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTRATION = "user_register";
    public static final String METHOD_FORGOT_PASSWORD = "forgot_pass";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_EDIT_PROFILE = "edit_profile";

    public static final String METHOD_LATEST_WALL = "get_latest";
    public static final String METHOD_FAV_WALL = "get_favorite_post";
    public static final String METHOD_MOST_VIEWED = "get_wallpaper_most_viewed";
    public static final String METHOD_MOST_RATED = "get_wallpaper_most_rated";
    public static final String METHOD_WALLPAPER_BY_CAT = "get_wallpaper";
    public static final String METHOD_WALL_SEARCH = "search_wallpaper";
    public static final String METHOD_CHECK_FAVORITE = "get_check_favorite";
    public static final String METHOD_WALL_DOWNLOAD = "download_wallpaper";
    public static final String METHOD_WALL_SINGLE = "get_single_wallpaper";
    public static final String METHOD_WALL_RATING = "wallpaper_rate";
    public static final String METHOD_WALL_GET_RATING = "get_wallpaper_rate";
    public static final String METHOD_REPORT = "user_report";
    public static final String METHOD_DO_FAV = "favorite_post";
    public static final String METHOD_RECENT = "get_recent_post";

    public static final String METHOD_LATEST_GIF = "get_latest_gif";
    public static final String METHOD_MOST_VIEWED_GIF = "get_gif_wallpaper_most_viewed";
    public static final String METHOD_MOST_RATED_GIF = "get_gif_wallpaper_most_rated";
    public static final String METHOD_GIF_SEARCH = "search_gif";
    public static final String METHOD_GIF_DOWNLOAD = "download_gif";
    public static final String METHOD_GIF_SINGLE = "get_single_gif";
    public static final String METHOD_GIF_RATING = "gif_rate";
    public static final String METHOD_GIF_GET_RATING = "get_gif_rate";

    public static final String URL_ABOUT_US_LOGO = SERVER_URL.replace("api.php","") + "images/";

    public static final String TAG_ROOT = "HD_WALLPAPER";
    public static final String TAG_MSG = "MSG";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_LATEST_WALL = "latest_wallpaper";
    public static final String TAG_FEATURED_WALL = "featured_wallpaper";
    public static final String TAG_POPULAR_WALL = "popular_wallpaper";
    public static final String TAG_RECENT_WALL = "recent_wallpapers";
    public static final String TAG_WALL_CAT = "wallpaper_category";
    public static final String TAG_WALLPAPER_COLORS = "wallpaper_colors";
    public static final String TAG_RESOLUTION = "resolution";
    public static final String TAG_SIZE = "size";

    public static final String TAG_PORTRAIT = "Portrait";
    public static final String TAG_LANDSCAPE = "Landscape";
    public static final String TAG_SQUARE = "Square";

    public static final String TAG_COLOR_ID = "color_id";
    public static final String TAG_COLOR_NAME = "color_name";
    public static final String TAG_COLOR_CODE = "color_code";

    public static final String TAG_CAT_ID = "cid";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_CAT_IMAGE_THUMB = "category_image_thumb";
    public static final String TAG_TOTAL_WALL = "category_total_wall";

    public static final String TAG_WALL_ID = "id";
    public static final String TAG_WALL_IMAGE = "wallpaper_image";
    public static final String TAG_WALL_IMAGE_THUMB = "wallpaper_image_thumb";

    public static final String TAG_GIF_ID = "id";
    public static final String TAG_GIF_IMAGE = "gif_image";
    public static final String TAG_GIF_TAGS = "gif_tags";
    public static final String TAG_GIF_VIEWS = "total_views";
    public static final String TAG_GIF_TOTAL_RATE = "total_rate";
    public static final String TAG_GIF_AVG_RATE = "rate_avg";

    public static final String TAG_WALL_VIEWS = "total_views";
    public static final String TAG_WALL_AVG_RATE = "rate_avg";
    public static final String TAG_WALL_TOTAL_RATE = "total_rate";
    public static final String TAG_WALL_DOWNLOADS = "total_download";
    public static final String TAG_WALL_TAGS = "wall_tags";
    public static final String TAG_WALL_TYPE = "wallpaper_type";
    public static final String TAG_WALL_COLORS = "wall_colors";
    public static final String TAG_IS_FAV = "is_favorite";

    public static final String LOGIN_TYPE_NORMAL = "normal";
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FB = "facebook";

    public static final String DARK_MODE_ON = "on";
    public static final String DARK_MODE_OFF = "off";
    public static final String DARK_MODE_SYSTEM = "system";

    public static final String AD_TYPE_ADMOB = "admob";
    public static final String AD_TYPE_FACEBOOK = "facebook";
    public static final String AD_TYPE_STARTAPP = "startapp";
    public static final String AD_TYPE_APPLOVIN = "applovins";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 2;

    // Gridview image padding
    public static final int GRID_PADDING = 3; // in dp

    public static ArrayList<ItemWallpaper> arrayList = new ArrayList<>();
    public static ArrayList<ItemGIF> arrayListGIF = new ArrayList<>();
    public static ArrayList<ItemColors> arrayListColors = new ArrayList<>();
    public static ItemAbout itemAbout;
    public static int columnWidth = 0;
    public static int columnHeight = 0;

    public static Boolean isFav = false;
    public static String packageName = "", search_item = "", gifPath = "";
    public static Uri uri_set;

    public static Boolean isUpdate = false, isBannerAd = true, isInterAd = true, isNativeAd = false, isGIFEnabled = true,
            isPortrait = true, isLandscape = true, isSquare = true, showUpdateDialog = true, appUpdateCancel = false;
    public static String bannerAdType = "admob", interstitialAdType = "admob", nativeAdType = "admob", ad_publisher_id = "", ad_banner_id = "",
            ad_inter_id = "", ad_native_id = "", startapp_id = "", appVersion="1", appUpdateMsg = "", appUpdateURL = "";

    public static int adInterstitialShow = 5;
    public static int adCount = 0;

    public static int adNativeShow = 12;

    public static Boolean isColorOn = true;
}