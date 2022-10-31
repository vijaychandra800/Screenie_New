package com.app.asyncTask;

import android.os.AsyncTask;

import com.app.interfaces.WallListener;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadWallpaper extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private WallListener wallListener;
    private ArrayList<ItemWallpaper> arrayList;
    private String verifyStatus = "0", message = "";
    private int num = -1;

    public LoadWallpaper(WallListener wallListener, RequestBody requestBody) {
        this.wallListener = wallListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        wallListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constant.TAG_SUCCESS)) {
                        if(objJson.has("num")) {
                            num = Integer.parseInt(objJson.getString("num"));
                        }
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
                    arrayList.add(itemWallpaper);
                } else {
                    verifyStatus = objJson.getString(Constant.TAG_SUCCESS);
                    message = objJson.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        wallListener.onEnd(s, verifyStatus, message, arrayList, num);
        super.onPostExecute(s);
    }
}