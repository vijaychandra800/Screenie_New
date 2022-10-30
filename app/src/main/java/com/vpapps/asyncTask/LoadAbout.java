package com.vpapps.asyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.AboutListener;
import com.vpapps.items.ItemAbout;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAbout extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private AboutListener aboutListener;
    private String message = "", verifyStatus = "0";

    public LoadAbout(AboutListener aboutListener, RequestBody requestBody) {
        this.aboutListener = aboutListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Constant.TAG_ROOT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    if (!c.has(Constant.TAG_SUCCESS)) {
                        String appname = c.getString("app_name");
                        String applogo = c.getString("app_logo");
                        String desc = c.getString("app_description");
                        String appversion = c.getString("app_version");
                        String appauthor = c.getString("app_author");
                        String appcontact = c.getString("app_contact");
                        String email = c.getString("app_email");
                        String website = c.getString("app_website");
                        String privacy = c.getString("app_privacy_policy");
                        String developedby = c.getString("app_developed_by");

                        Constant.isBannerAd = Boolean.parseBoolean(c.getString("banner_ad"));
                        Constant.isInterAd = Boolean.parseBoolean(c.getString("interstital_ad"));
                        Constant.isNativeAd = Boolean.parseBoolean(c.getString("native_ad"));

                        Constant.bannerAdType = c.getString("banner_ad_type");
                        Constant.interstitialAdType = c.getString("interstital_ad_type");
                        Constant.nativeAdType = c.getString("native_ad_type");

                        Constant.ad_banner_id = c.getString("banner_ad_id");
                        Constant.ad_inter_id = c.getString("interstital_ad_id");
                        Constant.ad_native_id = c.getString("native_ad_id");

                        Constant.adInterstitialShow = Integer.parseInt(c.getString("interstital_ad_click"));
                        Constant.adNativeShow = Integer.parseInt(c.getString("native_position"));

                        Constant.startapp_id = c.getString("startapp_app_id");

                        Constant.ad_publisher_id = c.getString("publisher_id");

                        Constant.isGIFEnabled = Boolean.parseBoolean(c.getString("gif_on_off"));
                        Constant.isPortrait = Boolean.parseBoolean(c.getString("portrait"));
                        Constant.isLandscape = Boolean.parseBoolean(c.getString("landscape"));
                        Constant.isSquare = Boolean.parseBoolean(c.getString("square"));
                        Constant.packageName = c.getString("package_name");

                        Constant.showUpdateDialog = c.getBoolean("app_update_status");
                        Constant.appVersion = c.getString("app_new_version");
                        Constant.appUpdateMsg = c.getString("app_update_desc");
                        Constant.appUpdateURL = c.getString("app_redirect_url");
                        Constant.appUpdateCancel = c.getBoolean("cancel_update_status");

                        Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                    } else {
                        verifyStatus = c.getString(Constant.TAG_SUCCESS);
                        message = c.getString(Constant.TAG_MSG);
                    }
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
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}