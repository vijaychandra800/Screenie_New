package com.app.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.items.ItemAbout;
import com.app.items.ItemCat;
import com.app.items.ItemColors;
import com.app.items.ItemGIF;
import com.app.items.ItemWallpaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private EncryptData encryptData;
    private static String DB_NAME = "hdwall.db";
    private SQLiteDatabase db;
    private final Context context;

    private static String TAG_ID = "id";

    private static final String TABLE_ABOUT = "about";
    private static final String TABLE_CAT = "cat";
    public static final String TABLE_WALL_BY_CAT = "catlist";
    public static final String TABLE_WALL_BY_FAV = "fav";
    public static final String TABLE_WALL_BY_LATEST = "latest";
    public static final String TABLE_WALL_BY_RECENT = "recent";
    public static final String TABLE_WALL_BY_FEATURED = "featured";
    private static final String TABLE_GIF = "gif";
    private static final String TABLE_COLOR = "colors";

    private static final String TAG_IMAGE_BIG = "img";
    private static final String TAG_IMAGE_SMALL = "img_thumb";

    private static final String TAG_PID = "pid";
    private static final String TAG_GID = "gid";
    private static final String TAG_CID = "cid";
    private static final String TAG_HEX = "hex";
    private static final String TAG_VIEWS = "views";
    private static final String TAG_TOTAL_RATE = "total_rate";
    private static final String TAG_AVG_RATE = "avg_rate";
    private static final String TAG_TOTAL_DWONLOAD = "total_download";
    private static final String TAG_TAGS = "tags";
    private static final String TAG_TYPE = "type";
    private static final String TAG_RES = "res";
    private static final String TAG_SIZE = "size";
    private static final String TAG_WALLURL = "wallUrl";
    private static final String TAG_APP1URL = "app1Url";
    private static final String TAG_APP2NAME = "app2Name";
    private static final String TAG_APP2URL = "app2Url";
    private static final String TAG_APP3NAME = "app3Name";
    private static final String TAG_APP3URL = "app3Url";
    private static final String TAG_APP4NAME = "app4Name";
    private static final String TAG_APP4URL = "app4Url";
    private static final String TAG_APP5NAME = "app5Name";
    private static final String TAG_APP5URL = "app5Url";


    private static final String TAG_IMAGE = "image";
    private static final String TAG_COLORS = "colors";

    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_CAT_TOT_WALL = "tot_wall";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "desc";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_IS_PORTRAIT = "isportrait";
    private static final String TAG_ABOUT_IS_LANDSCAPE = "islandscape";
    private static final String TAG_ABOUT_IS_SQUARE = "issquare";
    private static final String TAG_ABOUT_CLICK = "click";

    private String[] columns_wall = new String[]{TAG_ID, TAG_PID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_SMALL, TAG_IMAGE_BIG, TAG_VIEWS,
            TAG_TOTAL_RATE, TAG_AVG_RATE, TAG_TOTAL_DWONLOAD, TAG_TAGS, TAG_TYPE, TAG_RES, TAG_SIZE, TAG_COLORS};

    private String[] columns_cat = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_SMALL, TAG_IMAGE_BIG, TAG_CAT_TOT_WALL};

    private String[] columns_colors = new String[]{TAG_ID, TAG_CID, TAG_HEX};

    private String[] columns_gif = new String[]{TAG_ID, TAG_GID, TAG_IMAGE, TAG_VIEWS, TAG_TOTAL_RATE, TAG_AVG_RATE, TAG_TOTAL_DWONLOAD,
            TAG_TAGS, TAG_RES, TAG_SIZE};

    private String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR, TAG_ABOUT_CONTACT,
            TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED, TAG_ABOUT_PRIVACY, TAG_ABOUT_PUB_ID,
            TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK, TAG_ABOUT_IS_PORTRAIT, TAG_ABOUT_IS_LANDSCAPE, TAG_ABOUT_IS_SQUARE};


    // Creating table about
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_DEVELOPED + " TEXT, " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " + TAG_ABOUT_IS_PORTRAIT + " TEXT, " + TAG_ABOUT_IS_LANDSCAPE + " TEXT, " + TAG_ABOUT_IS_SQUARE + " TEXT, " + TAG_ABOUT_CLICK + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CAT = "create table " + TABLE_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_CAT_TOT_WALL + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_COLORS = "create table " + TABLE_COLOR + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CID + " TEXT," +
            TAG_HEX + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_CAT = "create table " + TABLE_WALL_BY_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT," +
            TAG_COLORS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_FAV = "create table " + TABLE_WALL_BY_FAV + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT," +
            TAG_COLORS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_LATEST = "create table " + TABLE_WALL_BY_LATEST + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT," +
            TAG_COLORS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_RECENT = "create table " + TABLE_WALL_BY_RECENT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT," +
            TAG_COLORS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_FEATURED = "create table " + TABLE_WALL_BY_FEATURED + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT," +
            TAG_COLORS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_GIF = "create table " + TABLE_GIF + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_GID + " TEXT UNIQUE," +
            TAG_IMAGE + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_TOTAL_RATE + " TEXT," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DWONLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_RES + " TEXT," +
            TAG_SIZE + " TEXT);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
        encryptData = new EncryptData(context);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_ABOUT);
            db.execSQL(CREATE_TABLE_CAT);
            db.execSQL(CREATE_TABLE_GIF);
            db.execSQL(CREATE_TABLE_COLORS);
            db.execSQL(CREATE_TABLE_WALL_BY_CAT);
            db.execSQL(CREATE_TABLE_WALL_BY_FAV);
            db.execSQL(CREATE_TABLE_WALL_BY_LATEST);
            db.execSQL(CREATE_TABLE_WALL_BY_RECENT);
            db.execSQL(CREATE_TABLE_WALL_BY_FEATURED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public ArrayList<ItemWallpaper> getWallpapers(String table, String filter, String wallType, String color_id) {
        ArrayList<ItemWallpaper> arrayList = new ArrayList<>();

        String query = "";
        switch (filter) {
            case "":
                query = null;
                break;
            case "id":
                query = TAG_ID + " DESC";
                break;
            case "views":
                query = TAG_VIEWS + " DESC";
                break;
            case "rate":
                query = TAG_AVG_RATE + " DESC";
                break;
        }

        Cursor cursor = db.query(table, columns_wall, TAG_TYPE + "='" + wallType + "'", null, null, null, query);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_PID));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                String img_thumb = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String totalrate = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_RATE));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DWONLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                String res = cursor.getString(cursor.getColumnIndex(TAG_RES));
                String size = cursor.getString(cursor.getColumnIndex(TAG_SIZE));

                String wallUrl = cursor.getString(cursor.getColumnIndex(TAG_WALLURL));
                String app1Url = cursor.getString(cursor.getColumnIndex(TAG_APP1URL));
                String app2Name = cursor.getString(cursor.getColumnIndex(TAG_APP2NAME));
                String app2Url = cursor.getString(cursor.getColumnIndex(TAG_APP2URL));
                String app3Name = cursor.getString(cursor.getColumnIndex(TAG_APP3NAME));
                String app3Url = cursor.getString(cursor.getColumnIndex(TAG_APP3URL));
                String app4Name = cursor.getString(cursor.getColumnIndex(TAG_APP4NAME));
                String app4Url = cursor.getString(cursor.getColumnIndex(TAG_APP4URL));
                String app5Name = cursor.getString(cursor.getColumnIndex(TAG_APP5NAME));
                String app5Url = cursor.getString(cursor.getColumnIndex(TAG_APP5URL));

                String colors = cursor.getString(cursor.getColumnIndex(TAG_COLORS)).replace(",", ",,");

                List<String> colors_array_wall = new ArrayList<>(Arrays.asList(colors.split(",")));
                colors_array_wall.remove("");

                List<String> colors_array_selected = new ArrayList<>(Arrays.asList(color_id.split(",")));
                colors_array_selected.remove("");

                if (colors_array_selected.size() > 0) {
                    if (colors_array_wall.size() > 0) {
                        for (String aColors_array_selected : colors_array_selected) {
                            if (colors_array_wall.contains(aColors_array_selected)) {
                                ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url, false);
                                itemWallpaper.setResolution(res);
                                itemWallpaper.setSize(size);
                                arrayList.add(itemWallpaper);
                                break;
                            }
                        }
                    }
                } else {
                    ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url,false);
                    itemWallpaper.setResolution(res);
                    itemWallpaper.setSize(size);
                    arrayList.add(itemWallpaper);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public String getFavWallpapersID(String wallType, String color_id) {
        Cursor cursor = db.query(TABLE_WALL_BY_FAV, columns_wall, TAG_TYPE + "='" + wallType + "'", null, null, null, null);
        String id = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(TAG_PID));
            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++) {
                id = id + "," + cursor.getString(cursor.getColumnIndex(TAG_PID));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return id;
    }

    @SuppressLint("Range")
    public String getRecentWallpapersID(String wallType, String color_id) {
        Cursor cursor = db.query(TABLE_WALL_BY_RECENT, columns_wall, TAG_TYPE + "='" + wallType + "'", null, null, null, null);
        String id = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(TAG_PID));
            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++) {
                id = id + "," + cursor.getString(cursor.getColumnIndex(TAG_PID));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return id;
    }

    @SuppressLint("Range")
    public ArrayList<ItemWallpaper> getRecentWallpapers(String wallType, String color_id, String limit) {
        ArrayList<ItemWallpaper> arrayList = new ArrayList<>();

        String query = TAG_ID + " DESC";

        Cursor cursor = db.query(TABLE_WALL_BY_RECENT, columns_wall, TAG_TYPE + "='" + wallType + "'", null, null, null, query, limit);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_PID));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                String img_thumb = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String totalrate = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_RATE));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DWONLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                String res = cursor.getString(cursor.getColumnIndex(TAG_RES));
                String size = cursor.getString(cursor.getColumnIndex(TAG_SIZE));

                String wallUrl = cursor.getString(cursor.getColumnIndex(TAG_WALLURL));
                String app1Url = cursor.getString(cursor.getColumnIndex(TAG_APP1URL));
                String app2Name = cursor.getString(cursor.getColumnIndex(TAG_APP2NAME));
                String app2Url = cursor.getString(cursor.getColumnIndex(TAG_APP2URL));
                String app3Name = cursor.getString(cursor.getColumnIndex(TAG_APP3NAME));
                String app3Url = cursor.getString(cursor.getColumnIndex(TAG_APP3URL));
                String app4Name = cursor.getString(cursor.getColumnIndex(TAG_APP4NAME));
                String app4Url = cursor.getString(cursor.getColumnIndex(TAG_APP4URL));
                String app5Name = cursor.getString(cursor.getColumnIndex(TAG_APP5NAME));
                String app5Url = cursor.getString(cursor.getColumnIndex(TAG_APP5URL));

                String colors = cursor.getString(cursor.getColumnIndex(TAG_COLORS)).replace(",", ",,");

                List<String> colors_array_wall = new ArrayList<>(Arrays.asList(colors.split(",")));
                colors_array_wall.remove("");

                List<String> colors_array_selected = new ArrayList<>(Arrays.asList(color_id.split(",")));
                colors_array_selected.remove("");

                if (colors_array_selected.size() > 0) {
                    if (colors_array_wall.size() > 0) {
                        for (String aColors_array_selected : colors_array_selected) {
                            if (colors_array_wall.contains(aColors_array_selected)) {
                                ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url, false);
                                itemWallpaper.setResolution(res);
                                itemWallpaper.setSize(size);
                                arrayList.add(itemWallpaper);
                                break;
                            }
                        }
                    }
                } else {
                    ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url, false);
                    itemWallpaper.setResolution(res);
                    itemWallpaper.setSize(size);
                    arrayList.add(itemWallpaper);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<ItemWallpaper> getWallByCat(String id, String wallType, String color_id) {
        ArrayList<ItemWallpaper> arrayList = new ArrayList<>();

        String where = TAG_CAT_ID + "=? AND " + TAG_TYPE + "=?";
        String[] args = {id, wallType};

        Cursor cursor = db.query(TABLE_WALL_BY_CAT, columns_wall, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_PID));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                String img_thumb = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String totalrate = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_RATE));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DWONLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                String res = cursor.getString(cursor.getColumnIndex(TAG_RES));
                String size = cursor.getString(cursor.getColumnIndex(TAG_SIZE));

                String wallUrl = cursor.getString(cursor.getColumnIndex(TAG_WALLURL));
                String app1Url = cursor.getString(cursor.getColumnIndex(TAG_APP1URL));
                String app2Name = cursor.getString(cursor.getColumnIndex(TAG_APP2NAME));
                String app2Url = cursor.getString(cursor.getColumnIndex(TAG_APP2URL));
                String app3Name = cursor.getString(cursor.getColumnIndex(TAG_APP3NAME));
                String app3Url = cursor.getString(cursor.getColumnIndex(TAG_APP3URL));
                String app4Name = cursor.getString(cursor.getColumnIndex(TAG_APP4NAME));
                String app4Url = cursor.getString(cursor.getColumnIndex(TAG_APP4URL));
                String app5Name = cursor.getString(cursor.getColumnIndex(TAG_APP5NAME));
                String app5Url = cursor.getString(cursor.getColumnIndex(TAG_APP5URL));


                String colors = cursor.getString(cursor.getColumnIndex(TAG_COLORS)).replace(",", ",,");

                List<String> colors_array_wall = new ArrayList<>(Arrays.asList(colors.split(",")));
                colors_array_wall.remove("");

                List<String> colors_array_selected = new ArrayList<>(Arrays.asList(color_id.split(",")));
                colors_array_selected.remove("");

                if (colors_array_selected.size() > 0) {
                    if (colors_array_wall.size() > 0) {
                        for (String aColors_array_selected : colors_array_selected) {
                            if (colors_array_wall.contains(aColors_array_selected)) {
                                ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url,  false);
                                itemWallpaper.setResolution(res);
                                itemWallpaper.setSize(size);
                                arrayList.add(itemWallpaper);
                                break;
                            }
                        }
                    }
                } else {
                    ItemWallpaper itemWallpaper = new ItemWallpaper(pid, cid, cname, img, img_thumb, colors, views, totalrate, averagerate, download, tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url, false);
                    itemWallpaper.setResolution(res);
                    itemWallpaper.setSize(size);
                    arrayList.add(itemWallpaper);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<ItemGIF> getGIFs() {
        ArrayList<ItemGIF> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_GIF, columns_gif, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                String gid = cursor.getString(cursor.getColumnIndex(TAG_GID));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE)));

                String views = cursor.getString(cursor.getColumnIndex(TAG_VIEWS));
                String total_rate = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_RATE));
                String avg_rate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DWONLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String res = cursor.getString(cursor.getColumnIndex(TAG_RES));
                String size = cursor.getString(cursor.getColumnIndex(TAG_SIZE));

                ItemGIF itemGIF = new ItemGIF(gid, img, views, total_rate, avg_rate, download, tags, false);
                itemGIF.setResolution(res);
                itemGIF.setSize(size);
                arrayList.add(itemGIF);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public String getFavGifsID() {
        String ids = "";

        Cursor cursor = db.query(TABLE_GIF, columns_gif, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            ids = cursor.getString(cursor.getColumnIndex(TAG_GID));

            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++) {
                ids = ids + "," + cursor.getString(cursor.getColumnIndex(TAG_GID));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return ids;
    }

    @SuppressLint("Range")
    public ArrayList<ItemCat> getCat() {
        ArrayList<ItemCat> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_CAT, columns_cat, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String total_wall = cursor.getString(cursor.getColumnIndex(TAG_CAT_TOT_WALL));

                ItemCat itemCat = new ItemCat(cid, cname, img, img, total_wall);
                arrayList.add(itemCat);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<ItemColors> getColors() {
        ArrayList<ItemColors> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_COLOR, columns_colors, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CID));
                String hex = cursor.getString(cursor.getColumnIndex(TAG_HEX));

                ItemColors itemColors = new ItemColors(cid, "", hex);
                arrayList.add(itemColors);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    public Boolean isFav(String id) {

        String where = TAG_PID + "=?";
        String[] args = {id};

        Cursor cursor = db.query(TABLE_WALL_BY_FAV, columns_wall, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public Boolean isFavGIF(String id) {

        String where = TAG_GID + "=?";
        String[] args = {id};

        Cursor cursor = db.query(TABLE_GIF, columns_gif, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void addtoFavorite(ItemWallpaper itemWallpaper) {
        String imageBig = encryptData.encrypt(itemWallpaper.getImage().replace(" ", "%20"));
        String imageSmall = encryptData.encrypt(itemWallpaper.getImageThumb().replace(" ", "%20"));

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_PID, itemWallpaper.getId());
        contentValues.put(TAG_CAT_ID, itemWallpaper.getCId());
        contentValues.put(TAG_CAT_NAME, itemWallpaper.getCName());
        contentValues.put(TAG_IMAGE_BIG, imageBig);
        contentValues.put(TAG_IMAGE_SMALL, imageSmall);
        contentValues.put(TAG_VIEWS, Integer.parseInt(itemWallpaper.getTotalViews()));
        contentValues.put(TAG_TOTAL_RATE, itemWallpaper.getTotalRate());
        contentValues.put(TAG_AVG_RATE, itemWallpaper.getAverageRate());
        contentValues.put(TAG_TOTAL_DWONLOAD, itemWallpaper.getTotalDownloads());
        contentValues.put(TAG_TAGS, itemWallpaper.getTags());
        contentValues.put(TAG_TYPE, itemWallpaper.getType());

        contentValues.put(TAG_WALLURL, itemWallpaper.getWallUrl());
        contentValues.put(TAG_APP1URL, itemWallpaper.getApp1Url());
        contentValues.put(TAG_APP2NAME, itemWallpaper.getApp2Name());
        contentValues.put(TAG_APP2URL, itemWallpaper.getApp2Url());
        contentValues.put(TAG_APP3NAME, itemWallpaper.getApp3Name());
        contentValues.put(TAG_APP3URL, itemWallpaper.getApp3Url());
        contentValues.put(TAG_APP4NAME, itemWallpaper.getApp4Name());
        contentValues.put(TAG_APP4URL, itemWallpaper.getApp4Url());
        contentValues.put(TAG_APP5NAME, itemWallpaper.getApp5Name());
        contentValues.put(TAG_APP5URL, itemWallpaper.getApp5Url());

        contentValues.put(TAG_RES, itemWallpaper.getResolution());
        contentValues.put(TAG_SIZE, itemWallpaper.getSize());
        contentValues.put(TAG_COLORS, itemWallpaper.getWallColors());

        db.insert(TABLE_WALL_BY_FAV, null, contentValues);
    }

    public void removeFav(String id) {
        db.delete(TABLE_WALL_BY_FAV, TAG_PID + "=" + id, null);
    }

    public void addWallpaper(ItemWallpaper itemWallpaper, String table) {
        if (!checkWallpaperAdded(itemWallpaper.getId(), table)) {
            String imageBig = encryptData.encrypt(itemWallpaper.getImage().replace(" ", "%20"));
            String imageSmall = encryptData.encrypt(itemWallpaper.getImageThumb().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_PID, itemWallpaper.getId());
            contentValues.put(TAG_CAT_ID, itemWallpaper.getCId());
            contentValues.put(TAG_CAT_NAME, itemWallpaper.getCName());
            contentValues.put(TAG_IMAGE_BIG, imageBig);
            contentValues.put(TAG_IMAGE_SMALL, imageSmall);
            contentValues.put(TAG_VIEWS, Integer.parseInt(itemWallpaper.getTotalViews()));
            contentValues.put(TAG_TOTAL_RATE, itemWallpaper.getTotalRate());
            contentValues.put(TAG_AVG_RATE, itemWallpaper.getAverageRate());
            contentValues.put(TAG_TOTAL_DWONLOAD, itemWallpaper.getTotalDownloads());
            contentValues.put(TAG_TAGS, itemWallpaper.getTags());
            contentValues.put(TAG_TYPE, itemWallpaper.getType());

            contentValues.put(TAG_WALLURL, itemWallpaper.getWallUrl());
            contentValues.put(TAG_APP1URL, itemWallpaper.getApp1Url());
            contentValues.put(TAG_APP2NAME, itemWallpaper.getApp2Name());
            contentValues.put(TAG_APP2URL, itemWallpaper.getApp2Url());
            contentValues.put(TAG_APP3NAME, itemWallpaper.getApp3Name());
            contentValues.put(TAG_APP3URL, itemWallpaper.getApp3Url());
            contentValues.put(TAG_APP4NAME, itemWallpaper.getApp4Name());
            contentValues.put(TAG_APP4URL, itemWallpaper.getApp4Url());
            contentValues.put(TAG_APP5NAME, itemWallpaper.getApp5Name());
            contentValues.put(TAG_APP5URL, itemWallpaper.getApp5Url());

            contentValues.put(TAG_RES, itemWallpaper.getResolution());
            contentValues.put(TAG_SIZE, itemWallpaper.getSize());
            contentValues.put(TAG_COLORS, itemWallpaper.getWallColors());

            db.insert(table, null, contentValues);
        }
    }

    private Boolean checkRecent(String id) {
        Cursor cursor = db.query(TABLE_WALL_BY_RECENT, columns_wall, TAG_PID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    private Boolean checkWallpaperAdded(String id, String table) {
        Cursor cursor = db.query(table, columns_wall, TAG_PID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    @SuppressLint("Range")
    public void addToRecent(ItemWallpaper itemWallpaper) {
        Cursor cursor_delete = db.query(TABLE_WALL_BY_RECENT, columns_wall, null, null, null, null, null);
        if (cursor_delete != null && cursor_delete.getCount() > 20) {
            cursor_delete.moveToFirst();
            db.delete(TABLE_WALL_BY_RECENT, TAG_PID + "=" + cursor_delete.getString(cursor_delete.getColumnIndex(TAG_PID)), null);
        }
        cursor_delete.close();

        if (checkRecent(itemWallpaper.getId())) {
            db.delete(TABLE_WALL_BY_RECENT, TAG_PID + "='" + itemWallpaper.getId() + "'", null);
        }

        String imageBig = encryptData.encrypt(itemWallpaper.getImage().replace(" ", "%20"));
        String imageSmall = encryptData.encrypt(itemWallpaper.getImageThumb().replace(" ", "%20"));

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_PID, itemWallpaper.getId());
        contentValues.put(TAG_CAT_ID, itemWallpaper.getCId());
        contentValues.put(TAG_CAT_NAME, itemWallpaper.getCName());
        contentValues.put(TAG_IMAGE_BIG, imageBig);
        contentValues.put(TAG_IMAGE_SMALL, imageSmall);
        contentValues.put(TAG_VIEWS, Integer.parseInt(itemWallpaper.getTotalViews()));
        contentValues.put(TAG_TOTAL_RATE, itemWallpaper.getTotalRate());
        contentValues.put(TAG_AVG_RATE, itemWallpaper.getAverageRate());
        contentValues.put(TAG_TOTAL_DWONLOAD, itemWallpaper.getTotalDownloads());
        contentValues.put(TAG_TAGS, itemWallpaper.getTags());

        contentValues.put(TAG_WALLURL, itemWallpaper.getWallUrl());
        contentValues.put(TAG_APP1URL, itemWallpaper.getApp1Url());
        contentValues.put(TAG_APP2NAME, itemWallpaper.getApp2Name());
        contentValues.put(TAG_APP2URL, itemWallpaper.getApp2Url());
        contentValues.put(TAG_APP3NAME, itemWallpaper.getApp3Name());
        contentValues.put(TAG_APP3URL, itemWallpaper.getApp3Url());
        contentValues.put(TAG_APP4NAME, itemWallpaper.getApp4Name());
        contentValues.put(TAG_APP4URL, itemWallpaper.getApp4Url());
        contentValues.put(TAG_APP5NAME, itemWallpaper.getApp5Name());
        contentValues.put(TAG_APP5URL, itemWallpaper.getApp5Url());

        contentValues.put(TAG_TYPE, itemWallpaper.getType());
        contentValues.put(TAG_RES, itemWallpaper.getResolution());
        contentValues.put(TAG_SIZE, itemWallpaper.getSize());
        contentValues.put(TAG_COLORS, itemWallpaper.getWallColors());

        db.insert(TABLE_WALL_BY_RECENT, null, contentValues);
    }

    public void removeAllWallpaper(String table) {
        try {
            db.delete(table, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeWallByCat(String table, String id) {
        try {
            db.delete(table, TAG_CAT_ID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addtoFavoriteGIF(ItemGIF itemGIF) {

        String image = encryptData.encrypt(itemGIF.getImage().replace(" ", "%20"));

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_GID, itemGIF.getId());
        contentValues.put(TAG_IMAGE, image);
        contentValues.put(TAG_VIEWS, itemGIF.getTotalViews());
        contentValues.put(TAG_TOTAL_RATE, itemGIF.getTotalRate());
        contentValues.put(TAG_AVG_RATE, itemGIF.getAveargeRate());
        contentValues.put(TAG_TOTAL_DWONLOAD, itemGIF.getTotalDownload());
        contentValues.put(TAG_TAGS, itemGIF.getTags());
        contentValues.put(TAG_RES, itemGIF.getResolution());
        contentValues.put(TAG_SIZE, itemGIF.getSize());

        db.insert(TABLE_GIF, null, contentValues);
    }

    public void removeFavGIF(String id) {
        try {
            db.delete(TABLE_GIF, TAG_GID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeColors() {
        try {
            db.delete(TABLE_COLOR, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addtoCatList(ItemCat itemCategory) {
        String image = encryptData.encrypt(itemCategory.getImage().replace(" ", "%20"));

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_CAT_ID, itemCategory.getId());
        contentValues.put(TAG_CAT_NAME, itemCategory.getName());
        contentValues.put(TAG_IMAGE_BIG, image);
        contentValues.put(TAG_CAT_TOT_WALL, itemCategory.getTotalWallpaper());

        db.insert(TABLE_CAT, null, contentValues);
    }

    public void addtoColorList(ItemColors itemColors) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_CID, itemColors.getId());
        contentValues.put(TAG_HEX, itemColors.getColorHex());

        db.insert(TABLE_COLOR, null, contentValues);
    }

    public void removeAllCat() {
        db.delete(TABLE_CAT, null, null);
    }

    public void updateView(String id, String totview, String download, String reso, String size) {

        ContentValues values = new ContentValues();
        values.put(TAG_VIEWS, totview);
        values.put(TAG_TOTAL_DWONLOAD, download);
        values.put(TAG_RES, reso);
        values.put(TAG_SIZE, size);

        String where = TAG_PID + "=?";
        String[] args = {id};

        db.update(TABLE_WALL_BY_FAV, values, where, args);
        db.update(TABLE_WALL_BY_CAT, values, where, args);
        db.update(TABLE_WALL_BY_LATEST, values, where, args);
        db.update(TABLE_WALL_BY_RECENT, values, where, args);
    }

    public void updateViewGIF(String id, String totview, String download, String reso, String size) {
        int views = Integer.parseInt(totview) + 1;

        ContentValues values = new ContentValues();
        values.put(TAG_VIEWS, views);
        values.put(TAG_TOTAL_DWONLOAD, download);
        values.put(TAG_RES, reso);
        values.put(TAG_SIZE, size);

        String where = TAG_GID + "=?";
        String[] args = {id};

        db.update(TABLE_GIF, values, where, args);
    }

    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_NAME, Constant.itemAbout.getAppName());
            contentValues.put(TAG_ABOUT_LOGO, Constant.itemAbout.getAppLogo());
            contentValues.put(TAG_ABOUT_VERSION, Constant.itemAbout.getAppVersion());
            contentValues.put(TAG_ABOUT_AUTHOR, Constant.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, Constant.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_EMAIL, Constant.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_WEBSITE, Constant.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, Constant.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_DEVELOPED, Constant.itemAbout.getDevelopedby());
            contentValues.put(TAG_ABOUT_PRIVACY, Constant.itemAbout.getPrivacy());
            contentValues.put(TAG_ABOUT_PUB_ID, Constant.ad_publisher_id);
            contentValues.put(TAG_ABOUT_BANNER_ID, Constant.ad_banner_id);
            contentValues.put(TAG_ABOUT_INTER_ID, Constant.ad_inter_id);
            contentValues.put(TAG_ABOUT_IS_BANNER, Constant.isBannerAd.toString());
            contentValues.put(TAG_ABOUT_IS_INTER, Constant.isInterAd.toString());
            contentValues.put(TAG_ABOUT_IS_PORTRAIT, Constant.isPortrait.toString());
            contentValues.put(TAG_ABOUT_IS_LANDSCAPE, Constant.isLandscape.toString());
            contentValues.put(TAG_ABOUT_IS_SQUARE, Constant.isSquare.toString());
            contentValues.put(TAG_ABOUT_CLICK, Constant.adInterstitialShow);

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public Boolean getAbout() {

        Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                String privacy = c.getString(c.getColumnIndex(TAG_ABOUT_PRIVACY));
                String developedby = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                Constant.ad_banner_id = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                Constant.ad_inter_id = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                Constant.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                Constant.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                Constant.ad_publisher_id = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                Constant.adInterstitialShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));
                Constant.isPortrait = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_PORTRAIT)));
                Constant.isLandscape = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_LANDSCAPE)));
                Constant.isSquare = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_SQUARE)));

                Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
            }
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.e("aaa", "upgrade");
//        Log.e("aaa -oldVersion", "" + oldVersion);
//        Log.e("aaa -newVersion", "" + newVersion);
        try {

            String myPath = "/data/data/" + context.getPackageName() + "/" + "databases/" + DB_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            switch (oldVersion) {
                case 1:
                case 2:
                    db.execSQL("ALTER TABLE gif ADD 'total_rate' TEXT");
                    db.execSQL("ALTER TABLE gif ADD 'avg_rate' TEXT");
                    db.execSQL("ALTER TABLE gif ADD 'total_download' TEXT");
                    db.execSQL("ALTER TABLE gif ADD 'tags' TEXT");

                    db.execSQL("ALTER TABLE latest ADD 'total_rate' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'avg_rate' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'total_download' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'tags' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'wallUrl' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app1Url' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app2Name' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app2Url' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app3Name' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app3Url' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app4Name' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app4Url' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app5Name' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'app5Url' TEXT");

                    db.execSQL("ALTER TABLE fav ADD 'total_rate' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'avg_rate' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'total_download' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'tags' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'wallUrl' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app1Url' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app2Name' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app2Url' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app3Name' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app3Url' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app4Name' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app4Url' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app5Name' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'app5Url' TEXT");

                    db.execSQL("ALTER TABLE catlist ADD 'total_rate' TEXT");
                    db.execSQL("ALTER TABLE catlist ADD 'avg_rate' TEXT");
                    db.execSQL("ALTER TABLE catlist ADD 'total_download' TEXT");
                    db.execSQL("ALTER TABLE catlist ADD 'tags' TEXT");

                    db.execSQL("ALTER TABLE about ADD 'ad_pub' TEXT");
                    db.execSQL("ALTER TABLE about ADD 'ad_banner' TEXT");
                    db.execSQL("ALTER TABLE about ADD 'ad_inter' TEXT");
                    db.execSQL("ALTER TABLE about ADD 'isbanner' TEXT");
                    db.execSQL("ALTER TABLE about ADD 'isinter' TEXT");
                    db.execSQL("ALTER TABLE about ADD 'click' TEXT");
                case 3:

                    db.execSQL("ALTER TABLE latest ADD 'type' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'res' TEXT");
                    db.execSQL("ALTER TABLE latest ADD 'size' TEXT");

                    db.execSQL("ALTER TABLE fav ADD 'type' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'res' TEXT");
                    db.execSQL("ALTER TABLE fav ADD 'size' TEXT");

                    db.execSQL("ALTER TABLE catlist ADD 'type' TEXT");
                    db.execSQL("ALTER TABLE catlist ADD 'res' TEXT");
                    db.execSQL("ALTER TABLE catlist ADD 'size' TEXT");

                    db.execSQL("ALTER TABLE gif ADD 'res' TEXT");
                    db.execSQL("ALTER TABLE gif ADD 'size' TEXT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}