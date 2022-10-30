package com.vpapps.hdwallpaper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vpapps.asyncTask.LoadAbout;
import com.vpapps.asyncTask.LoadLogin;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.interfaces.LoginListener;
import com.vpapps.items.ItemUser;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    String cid = "0", cname = "";
    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideStatusBar();
        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        if (getIntent().hasExtra("cid")) {
            cid = getIntent().getStringExtra("cid");
            cname = getIntent().getStringExtra("cname");
        }

        if (sharedPref.getIsFirst()) {
            loadAboutData();
        } else {
            if (!sharedPref.getIsAutoLogin()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openMainActivity();
                    }
                }, 2000);
            } else {
                if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_FB)) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        loadLogin(Constant.LOGIN_TYPE_FB, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        loadLogin(Constant.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else {
                    loadLogin(Constant.LOGIN_TYPE_NORMAL, "");
                }
            }
        }

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constant.GRID_PADDING, r.getDisplayMetrics());
        Constant.columnWidth = (int) ((methods.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);
        Constant.columnHeight = (int) (Constant.columnWidth * 1.44);
    }

    private void loadLogin(final String loginType, final String authID) {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {

                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            sharedPref.setLoginDetails(user_id, user_name, sharedPref.getUserMobile(), sharedPref.getEmail(), authID, sharedPref.getIsRemember(), sharedPref.getPassword(), loginType);
                            sharedPref.setIsLogged(true);
                        }
                        openMainActivity();
                    } else {
                        openMainActivity();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, authID, loginType, "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", ""));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            String version = "";
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = String.valueOf(pInfo.versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constant.appUpdateMsg);
                            } else {
                                sharedPref.setAdDetails(Constant.isBannerAd, Constant.isInterAd, Constant.isNativeAd, Constant.bannerAdType,
                                        Constant.interstitialAdType, Constant.nativeAdType, Constant.ad_banner_id, Constant.ad_inter_id, Constant.ad_native_id, Constant.startapp_id, Constant.adInterstitialShow, Constant.adNativeShow);

                                dbHelper.addtoAbout();
                                openLoginActivity();
                            }
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.server_no_conn));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.internet_not_connected)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void openLoginActivity() {
        Intent intent;
        if (sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent;
        if (!cid.equals("0")) {
            intent = new Intent(SplashActivity.this, WallpaperByCatActivity.class);
            intent.putExtra("cid", cid);
            intent.putExtra("cname", cname);
            intent.putExtra("from", "noti");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("from", "");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}