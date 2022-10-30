package com.vpapps.asyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.GIFListener;
import com.vpapps.items.ItemGIF;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadGIF extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private GIFListener gifListener;
    private ArrayList<ItemGIF> arrayList;
    private String verifyStatus = "0", message = "";
    private int num = -1;

    public LoadGIF(GIFListener gifListener, RequestBody requestBody) {
        this.gifListener = gifListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        gifListener.onStart();
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

                    String id = objJson.getString(Constant.TAG_GIF_ID);
                    String img = objJson.getString(Constant.TAG_GIF_IMAGE).replace(" ", "%20");
                    String totalviews = objJson.getString(Constant.TAG_GIF_VIEWS);
                    String totalRate = objJson.getString(Constant.TAG_GIF_TOTAL_RATE);
                    String avj_rate = objJson.getString(Constant.TAG_GIF_AVG_RATE);
                    String tags = objJson.getString(Constant.TAG_GIF_TAGS);
                    boolean isFav = objJson.getBoolean(Constant.TAG_IS_FAV);

                    ItemGIF itemGIF = new ItemGIF(id, img, totalviews, totalRate, avj_rate, "", tags, isFav);
                    arrayList.add(itemGIF);
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
        gifListener.onEnd(s, verifyStatus, message, arrayList, num);
        super.onPostExecute(s);
    }
}