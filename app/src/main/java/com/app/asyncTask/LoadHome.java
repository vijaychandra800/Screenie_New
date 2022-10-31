package com.app.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.app.interfaces.HomeListener;
import com.app.items.ItemCat;
import com.app.items.ItemColors;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadHome extends AsyncTask<String, String, Boolean> {

    private RequestBody requestBody;
    private HomeListener homeListener;
    private DBHelper dbHelper;
    private ArrayList<ItemColors> arrayListColors;
    private ArrayList<ItemCat> arrayListCat;
    private ArrayList<ItemWallpaper> arrayListFeatured, arrayListLatest, arrayListPopular, arrayListRecent;

    public LoadHome(Context context, HomeListener homeListener, RequestBody requestBody) {
        dbHelper = new DBHelper(context);
        this.homeListener = homeListener;
        this.requestBody = requestBody;
        arrayListFeatured = new ArrayList<>();
        arrayListPopular = new ArrayList<>();
        arrayListLatest = new ArrayList<>();
        arrayListCat = new ArrayList<>();
        arrayListColors = new ArrayList<>();
        arrayListRecent = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllWallpaper(DBHelper.TABLE_WALL_BY_LATEST);
        dbHelper.removeAllWallpaper(DBHelper.TABLE_WALL_BY_FEATURED);
        dbHelper.removeColors();
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONObject jsonObj = jOb.getJSONObject(Constant.TAG_ROOT);

            JSONArray jsonArray_featured = jsonObj.getJSONArray(Constant.TAG_FEATURED_WALL);
            for (int i = 0; i < jsonArray_featured.length(); i++) {
                JSONObject objJson = jsonArray_featured.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_WALL_ID);
                String cid = objJson.getString(Constant.TAG_CAT_ID);
                String cat_name = objJson.getString(Constant.TAG_CAT_NAME);
                String img = objJson.getString(Constant.TAG_WALL_IMAGE).replace(" ", "%20");
                String img_thumb = objJson.getString(Constant.TAG_WALL_IMAGE_THUMB).replace(" ", "%20");
                String totalviews = objJson.getString(Constant.TAG_WALL_VIEWS);
                String totalrate = objJson.getString(Constant.TAG_WALL_TOTAL_RATE);
                String averagerate = objJson.getString(Constant.TAG_WALL_AVG_RATE);
                String tags = objJson.getString(Constant.TAG_WALL_TAGS);
                String wallUrl = objJson.getString(Constant.TAG_WALL_URL);
                String app1Url = objJson.getString(Constant.TAG_APP1_URL);
                String app2Name = objJson.getString(Constant.TAG_APP2_NAME);
                String app2Url = objJson.getString(Constant.TAG_APP2_URL);
                String app3Name = objJson.getString(Constant.TAG_APP3_NAME);
                String app3Url = objJson.getString(Constant.TAG_APP3_URL);
                String app4Name = objJson.getString(Constant.TAG_APP4_NAME);
                String app4Url = objJson.getString(Constant.TAG_APP4_URL);
                String app5Name = objJson.getString(Constant.TAG_APP5_NAME);
                String app5Url = objJson.getString(Constant.TAG_APP5_URL);

                String type = objJson.getString(Constant.TAG_WALL_TYPE);
                String colors = objJson.getString(Constant.TAG_WALL_COLORS);
                boolean isFav = objJson.getBoolean(Constant.TAG_IS_FAV);

                ItemWallpaper itemWallpaper = new ItemWallpaper(id, cid, cat_name, img, img_thumb, colors, totalviews, totalrate, averagerate, "", tags, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url, app4Name,app4Url,app5Name,app5Url,type, isFav);
                arrayListFeatured.add(itemWallpaper);
                dbHelper.addWallpaper(itemWallpaper, DBHelper.TABLE_WALL_BY_FEATURED);
            }

            JSONArray jsonArray_potrait = jsonObj.getJSONArray(Constant.TAG_LATEST_WALL);
            for (int i = 0; i < jsonArray_potrait.length(); i++) {
                JSONObject objJson = jsonArray_potrait.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_WALL_ID);
                String cid = objJson.getString(Constant.TAG_CAT_ID);
                String cat_name = objJson.getString(Constant.TAG_CAT_NAME);
                String img = objJson.getString(Constant.TAG_WALL_IMAGE).replace(" ", "%20");
                String img_thumb = objJson.getString(Constant.TAG_WALL_IMAGE_THUMB).replace(" ", "%20");
                String totalviews = objJson.getString(Constant.TAG_WALL_VIEWS);
                String totalrate = objJson.getString(Constant.TAG_WALL_TOTAL_RATE);
                String averagerate = objJson.getString(Constant.TAG_WALL_AVG_RATE);
                String tags = objJson.getString(Constant.TAG_WALL_TAGS);
                String type = objJson.getString(Constant.TAG_WALL_TYPE);
                String wallUrl = objJson.getString(Constant.TAG_WALL_URL);
                String app1Url = objJson.getString(Constant.TAG_APP1_URL);
                String app2Name = objJson.getString(Constant.TAG_APP2_NAME);
                String app2Url = objJson.getString(Constant.TAG_APP2_URL);
                String app3Name = objJson.getString(Constant.TAG_APP3_NAME);
                String app3Url = objJson.getString(Constant.TAG_APP3_URL);
                String app4Name = objJson.getString(Constant.TAG_APP4_NAME);
                String app4Url = objJson.getString(Constant.TAG_APP4_URL);
                String app5Name = objJson.getString(Constant.TAG_APP5_NAME);
                String app5Url = objJson.getString(Constant.TAG_APP5_URL);

                String colors = objJson.getString(Constant.TAG_WALL_COLORS);
                boolean isFav = objJson.getBoolean(Constant.TAG_IS_FAV);

                ItemWallpaper itemWallpaper = new ItemWallpaper(id, cid, cat_name, img, img_thumb, colors, totalviews, totalrate, averagerate, "", tags, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url,type, isFav);
                dbHelper.addWallpaper(itemWallpaper, DBHelper.TABLE_WALL_BY_LATEST);
                arrayListLatest.add(itemWallpaper);
            }

            JSONArray jsonArray_pop = jsonObj.getJSONArray(Constant.TAG_POPULAR_WALL);
            for (int i = 0; i < jsonArray_pop.length(); i++) {
                JSONObject objJson = jsonArray_pop.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_WALL_ID);
                String cid = objJson.getString(Constant.TAG_CAT_ID);
                String cat_name = objJson.getString(Constant.TAG_CAT_NAME);
                String img = objJson.getString(Constant.TAG_WALL_IMAGE).replace(" ", "%20");
                String img_thumb = objJson.getString(Constant.TAG_WALL_IMAGE_THUMB).replace(" ", "%20");
                String totalviews = objJson.getString(Constant.TAG_WALL_VIEWS);
                String totalrate = objJson.getString(Constant.TAG_WALL_TOTAL_RATE);
                String averagerate = objJson.getString(Constant.TAG_WALL_AVG_RATE);
                String tags = objJson.getString(Constant.TAG_WALL_TAGS);
                String type = objJson.getString(Constant.TAG_WALL_TYPE);
                String wallUrl = objJson.getString(Constant.TAG_WALL_URL);
                String app1Url = objJson.getString(Constant.TAG_APP1_URL);
                String app2Name = objJson.getString(Constant.TAG_APP2_NAME);
                String app2Url = objJson.getString(Constant.TAG_APP2_URL);
                String app3Name = objJson.getString(Constant.TAG_APP3_NAME);
                String app3Url = objJson.getString(Constant.TAG_APP3_URL);
                String app4Name = objJson.getString(Constant.TAG_APP4_NAME);
                String app4Url = objJson.getString(Constant.TAG_APP4_URL);
                String app5Name = objJson.getString(Constant.TAG_APP5_NAME);
                String app5Url = objJson.getString(Constant.TAG_APP5_URL);

                String colors = objJson.getString(Constant.TAG_WALL_COLORS);
                boolean isFav = objJson.getBoolean(Constant.TAG_IS_FAV);

                ItemWallpaper itemWallpaper = new ItemWallpaper(id, cid, cat_name, img, img_thumb, colors, totalviews, totalrate, averagerate, "", tags,wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url, app4Name,app4Url,app5Name,app5Url,type, isFav);
                dbHelper.addWallpaper(itemWallpaper, DBHelper.TABLE_WALL_BY_LATEST);
                arrayListPopular.add(itemWallpaper);
            }

            JSONArray jsonArray_recent = jsonObj.getJSONArray(Constant.TAG_RECENT_WALL);
            for (int i = 0; i < jsonArray_recent.length(); i++) {
                JSONObject objJson = jsonArray_recent.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_WALL_ID);
                String cid = objJson.getString(Constant.TAG_CAT_ID);
                String cat_name = objJson.getString(Constant.TAG_CAT_NAME);
                String img = objJson.getString(Constant.TAG_WALL_IMAGE).replace(" ", "%20");
                String img_thumb = objJson.getString(Constant.TAG_WALL_IMAGE_THUMB).replace(" ", "%20");
                String totalviews = objJson.getString(Constant.TAG_WALL_VIEWS);
                String totalrate = objJson.getString(Constant.TAG_WALL_TOTAL_RATE);
                String averagerate = objJson.getString(Constant.TAG_WALL_AVG_RATE);
                String tags = objJson.getString(Constant.TAG_WALL_TAGS);
                String type = objJson.getString(Constant.TAG_WALL_TYPE);
                String wallUrl = objJson.getString(Constant.TAG_WALL_URL);
                String app1Url = objJson.getString(Constant.TAG_APP1_URL);
                String app2Name = objJson.getString(Constant.TAG_APP2_NAME);
                String app2Url = objJson.getString(Constant.TAG_APP2_URL);
                String app3Name = objJson.getString(Constant.TAG_APP3_NAME);
                String app3Url = objJson.getString(Constant.TAG_APP3_URL);
                String app4Name = objJson.getString(Constant.TAG_APP4_NAME);
                String app4Url = objJson.getString(Constant.TAG_APP4_URL);
                String app5Name = objJson.getString(Constant.TAG_APP5_NAME);
                String app5Url = objJson.getString(Constant.TAG_APP5_URL);

                String colors = objJson.getString(Constant.TAG_WALL_COLORS);
                boolean isFav = objJson.getBoolean(Constant.TAG_IS_FAV);

                ItemWallpaper itemWallpaper = new ItemWallpaper(id, cid, cat_name, img, img_thumb, colors, totalviews, totalrate, averagerate, "", tags, type, wallUrl, app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url,isFav);
                arrayListRecent.add(itemWallpaper);
            }

            JSONArray jsonArray_cat = jsonObj.getJSONArray(Constant.TAG_WALL_CAT);
            for (int i = 0; i < jsonArray_cat.length(); i++) {
                JSONObject objJson = jsonArray_cat.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_CAT_ID);
                String name = objJson.getString(Constant.TAG_CAT_NAME);
                String image = objJson.getString(Constant.TAG_CAT_IMAGE).replace(" ", "%20");
                String image_thumb = objJson.getString(Constant.TAG_CAT_IMAGE_THUMB).replace(" ", "%20");
                String tot_wall = objJson.getString(Constant.TAG_TOTAL_WALL);

                ItemCat itemCat = new ItemCat(id, name, image, image_thumb, tot_wall);
                arrayListCat.add(itemCat);
            }

            JSONArray jsonArray_color = jsonObj.getJSONArray(Constant.TAG_WALLPAPER_COLORS);
            for (int i = 0; i < jsonArray_color.length(); i++) {
                JSONObject objJson = jsonArray_color.getJSONObject(i);

                String id = objJson.getString(Constant.TAG_COLOR_ID);
                String name = objJson.getString(Constant.TAG_COLOR_NAME);
                String code = objJson.getString(Constant.TAG_COLOR_CODE);

                ItemColors itemColors = new ItemColors(id, name, code);
                arrayListColors.add(itemColors);
                dbHelper.addtoColorList(itemColors);
            }
            return true;
        } catch (Exception ee) {
            ee.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
        homeListener.onEnd(String.valueOf(s), arrayListFeatured, arrayListLatest, arrayListPopular, arrayListRecent, arrayListCat, arrayListColors);
        super.onPostExecute(s);
    }
}