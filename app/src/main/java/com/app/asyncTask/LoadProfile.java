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
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadProfile(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
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
                String user_id = c.getString("user_id");
                if(user_id != null) {
                    String user_name = c.getString("name");
                    String email = c.getString("email");
                    String phone = c.getString("phone");
                    String loginType = Constant.itemUser.getLoginType();
                    String authID = Constant.itemUser.getAuthID();

                    Constant.itemUser = new ItemUser(user_id, user_name, email, phone, authID, loginType);
                } else {
                    success = "0";
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, message);
        super.onPostExecute(s);
    }
}