package com.app.asyncTask;

import android.os.AsyncTask;

import com.app.interfaces.SuccessListener;
import com.app.items.ItemUser;
import com.app.utils.Constant;
import com.app.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadProfile extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private ProfileListener profileListener;
    private String success = "0", message = "", user_id = "", user_name = "", email = "", phone = "";

    public LoadProfile(ProfileListener profileListener, RequestBody requestBody) {
        this.profileListener = profileListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        profileListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Constant.TAG_SUCCESS);
                user_id = c.getString("user_id");
                user_name = c.getString("name");
                email = c.getString("email");
                phone = c.getString("phone");
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        profileListener.onEnd(s, success, message, user_id, user_name, email, phone);
        super.onPostExecute(s);
    }
}