package com.app.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.app.interfaces.CategoryListener;
import com.app.items.ItemCat;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCat extends AsyncTask<String,String,String> {

    private RequestBody requestBody;
    private CategoryListener categoryListener;
    private ArrayList<ItemCat> arrayList_cat;
    private DBHelper dbHelper;
    private String verifyStatus = "0", message = "";

    public LoadCat(Context context, CategoryListener categoryListener, RequestBody requestBody) {
        this.categoryListener = categoryListener;
        this.requestBody = requestBody;
        arrayList_cat = new ArrayList<>();
        dbHelper = new DBHelper(context);
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllCat();
        categoryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                if (!c.has(Constant.TAG_SUCCESS)) {
                    String id = c.getString(Constant.TAG_CAT_ID);
                    String name = c.getString(Constant.TAG_CAT_NAME);
                    String image = c.getString(Constant.TAG_CAT_IMAGE).replace(" ", "%20");
                    String image_thumb = c.getString(Constant.TAG_CAT_IMAGE_THUMB).replace(" ", "%20");
                    String tot_wall = c.getString(Constant.TAG_TOTAL_WALL);

                    ItemCat itemCat = new ItemCat(id, name, image, image_thumb, tot_wall);
                    arrayList_cat.add(itemCat);
                    dbHelper.addtoCatList(itemCat);
                } else {
                    verifyStatus = c.getString(Constant.TAG_SUCCESS);
                    message = c.getString(Constant.TAG_MSG);
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
        categoryListener.onEnd(s, verifyStatus, message, arrayList_cat);
        super.onPostExecute(s);
    }
}