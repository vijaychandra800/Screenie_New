package com.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.app.screenie.LoginActivity;
import com.app.screenie.R;
import com.app.screenie.SetGIFAsWallpaperService;
import com.app.screenie.SetWallpaperActivity;
import com.app.interfaces.InterAdListener;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Methods {

    private Context context;
    private InterAdListener interAdListener;

    // constructor
    public Methods(Context context) {
        this.context = context;
    }

    // constructor
    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        point.y = display.getHeight();

        return point.y;
    }

    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void clickLogin() {
        SharedPref sharePref = new SharedPref(context);
        if (sharePref.isLogged()) {
            logout((Activity) context, sharePref);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
        }
    }

    public void logout(Activity activity, SharedPref sharePref) {
        if (sharePref.getLoginType().equals(Constant.LOGIN_TYPE_FB)) {
            LoginManager.getInstance().logOut();
        } else if (sharePref.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
            FirebaseAuth.getInstance().signOut();
        }

        sharePref.setIsAutoLogin(false);
        sharePref.setIsLogged(false);
        sharePref.setLoginDetails("", "", "", "", "", false, "", Constant.LOGIN_TYPE_NORMAL);
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }

    @SuppressLint("MissingPermission")
    public static void setAsGIFWallPaper(Context context, String str) {
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (DeviceDetectUtil.isMiUi()) {
//            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
//            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
//            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
//            context.startActivity(intent);
//            return;
//        }
        Intent intent2 = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
        intent2.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
        intent2.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent2);
    }

    public boolean isAdmobFBAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.bannerAdType.equals(Constant.AD_TYPE_FACEBOOK) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_FACEBOOK) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_FACEBOOK);
    }

    public boolean isStartAppAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_STARTAPP) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_STARTAPP) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_STARTAPP);
    }

    public boolean isApplovinAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_APPLOVIN) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_APPLOVIN) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_APPLOVIN);
    }

    public void initializeAds() {
        if(isAdmobFBAds()) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }

        if(isStartAppAds()) {
            if(!Constant.startapp_id.equals("")) {
                StartAppSDK.init(context, Constant.startapp_id, false);
                StartAppAd.disableSplash();
            }
        }

        if(isApplovinAds()) {
            if(!AppLovinSdk.getInstance(context).isInitialized()) {
                AppLovinSdk.initializeSdk(context);
                AppLovinSdk.getInstance(context).setMediationProvider("max");
                AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("bb6822d9-18de-41b0-994e-41d4245a4d63", "749d75a2-1ef2-4ff9-88a5-c50374843ac6"));
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                .build();
        adView.setAdUnitId(Constant.ad_banner_id);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    @SuppressLint("MissingPermission")
    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                .build();
        adView.setAdUnitId(Constant.ad_banner_id);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable() && Constant.isBannerAd) {
            switch (Constant.bannerAdType) {
                case Constant.AD_TYPE_ADMOB:
                case Constant.AD_TYPE_FACEBOOK:
                    if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                        showNonPersonalizedAds(linearLayout);
                    } else {
                        showPersonalizedAds(linearLayout);
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(context);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    MaxAdView adView = new MaxAdView(Constant.ad_banner_id, context);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    linearLayout.addView(adView);
                    adView.loadAd();
                    break;
            }
        }
    }

    public void showInter(final int pos, final String type) {
        if (Constant.isInterAd) {
            Constant.adCount = Constant.adCount + 1;
            if (Constant.adCount % Constant.adInterstitialShow == 0) {
                switch (Constant.interstitialAdType) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:
                        final AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(context);
                        if (adManagerInterAdmob.getAd() != null) {
                            adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }
                            });
                            adManagerInterAdmob.getAd().show((Activity) context);
                        } else {
                            AdManagerInterAdmob.setAd(null);
                            adManagerInterAdmob.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        final AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(context);
                        if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                            adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {

                                }

                                @Override
                                public void adClicked(Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterStartApp.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        final AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(context);
                        if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                            adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {

                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {

                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {

                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                            adManagerInterApplovin.getAd().showAd();
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterApplovin.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public void showSnackBar(View linearLayout, String message) {
        Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.drawable.bg_grt_toolbar);
        snackbar.show();
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String getDarkMode() {
        SharedPref sharedPref = new SharedPref(context);
        return sharedPref.getDarkMode();
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getRoundDrawableRadis(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(radius);
        return gd;
    }

    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public void showUpdateAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constant.appUpdateURL;
                if (url.equals("")) {
                    url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);

                ((Activity) context).finish();
            }
        });
        if (Constant.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
        }
        alertDialog.show();
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public String getImageThumbSize(String imagePath, String type) {
        if (type.equals(context.getString(R.string.portrait)) || type.equals("")) {
            imagePath = imagePath.replace("&size=300x300", "&size=200x350");
        } else if (type.equals(context.getString(R.string.landscape))) {
            imagePath = imagePath.replace("&size=300x300", "&size=350x200");
        } else if (type.equals(context.getString(R.string.square))) {
            imagePath = imagePath.replace("&size=300x300", "&size=300x300");
        } else if (type.equals(context.getString(R.string.details))) {
            imagePath = imagePath.replace("&size=300x300", "&size=500x500");
        } else if (type.equals(context.getString(R.string.home).concat(Constant.TAG_PORTRAIT))) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x550");
        } else if (type.equals(context.getString(R.string.home).concat(Constant.TAG_LANDSCAPE))) {
            imagePath = imagePath.replace("&size=300x300", "&size=550x400");
        } else if (type.equals(context.getString(R.string.home).concat(Constant.TAG_SQUARE))) {
            imagePath = imagePath.replace("&size=300x300", "&size=500x500");
        } else if (type.equals(context.getString(R.string.categories))) {
            imagePath = imagePath.replace("&size=300x300", "&size=300x250");
        }
        return imagePath;
    }

    public int getColumnWidth(int column, int grid_padding) {
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth() - ((column + 1) * padding)) / column);
    }

    public void saveImage(String img_url, String option, RelativeLayout coordinatorLayout, String postType) {
        new LoadShare(option, postType, coordinatorLayout).execute(img_url, FilenameUtils.getName(img_url));
    }

    public class LoadShare extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        RelativeLayout coordinatorLayout;
        String option, filePath, postType;
        File file;

        LoadShare(String option, String postType, RelativeLayout coordinatorLayout) {
            this.option = option;
            this.postType = postType;
            this.coordinatorLayout = coordinatorLayout;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            if (option.equals(context.getString(R.string.download))) {
                if(postType.equals("wallpaper")) {
                    pDialog.setMessage(context.getResources().getString(R.string.downloading_wallpaper));
                } else {
                    pDialog.setMessage(context.getResources().getString(R.string.downloading_gif));
                }
            } else {
                pDialog.setMessage(context.getResources().getString(R.string.please_wait));
            }
            pDialog.setIndeterminate(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[1];
            try {
                if (!option.equalsIgnoreCase(context.getString(R.string.download))) {
                    filePath = context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator + name;
                } else {
                    if(postType.equals("wallpaper")) {
                        filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.wallpapers) + File.separator + name;
                    } else {
                        filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.gifs) + File.separator + name;
                    }
                }
                file = new File(filePath);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;

                    if (strings[0].contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    }

                    if (option.equalsIgnoreCase(context.getString(R.string.download))) {
                        boolean isSaved = false;
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if(postType.equals("wallpaper")) {
                            isSaved = saveImage(bitmap, name, option, postType, null);
                        } else {
                            isSaved = saveImage(bitmap, name, option, postType, IOUtils.toByteArray(inputStream));
                        }

                        if(isSaved) {
                            return "1";
                        } else {
                            return "2";
                        }
                    } else {
                        if (file.createNewFile()) {
                            file.createNewFile();
                        }

                        FileOutputStream fileOutput = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        return "1";
                    }
                } else {
                    return "2";
                }
            } catch (MalformedURLException e) {
                return "0";
            } catch (IOException e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            pDialog.dismiss();

            if (option.equals(context.getString(R.string.download))) {
                if (s.equals("2")) {
                    if (postType.equals("wallpaper")) {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.wallpaper_already_saved));
                    } else {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.gif_already_saved));
                    }
                } else {
                    if (isNetworkAvailable()) {
                        GlobalBus.getBus().postSticky(new EventAction(context.getString(R.string.download)));
                    }
                    if (postType.equals("wallpaper")) {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.wallpaper_saved));
                    } else {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.gif_saved));
                    }
                }
            } else if (option.equals(context.getString(R.string.set_wallpaper))) {
                Constant.uri_set = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);
                if (postType.equals("wallpaper")) {
                    Intent intent = new Intent(context, SetWallpaperActivity.class);
                    context.startActivity(intent);
                } else {
                    SetGIFAsWallpaperService.setAsWallPaper(context);
                }
            } else if (option.equals(context.getString(R.string.share))) {
                    Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                if(postType.equals("wallpaper")) {
                    share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                } else {
                    share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_gif) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                }
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
            }

            super.onPostExecute(s);
        }
    }

    private boolean saveImage(Bitmap bitmap, String fileName, String type, String postType, byte[] bytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type.equalsIgnoreCase(context.getString(R.string.download))) {
            ContentValues values = new ContentValues();
            if (postType.equals("wallpaper")) {
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.wallpapers));
            } else {
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.gifs));
            }
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            File directory;

            if (!type.equals(context.getString(R.string.download))) {
                directory = new File(context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator);
            } else {
                if(postType.equals("wallpaper")) {
                    directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.wallpapers) + File.separator);
                } else {
                    directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.gifs) + File.separator);
                }
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);

            try {
                OutputStream outputStream = new FileOutputStream(file);

                if (postType.equals("wallpaper")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(bytes);
                    outputStream.flush();
                }
                outputStream.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 22);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
                    return false;
                }
            }
            return true;
        }
    }

    public RequestBody getAPIRequest(String method, int page, String colorID, String type, String cat_id, String searchText, String itemID, String rate, String name, String email, String password, String phone, String userID, String report_PostType) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_HOME:
                jsObj.addProperty("type", type);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("id", colorID);
                break;
            case Constant.METHOD_CAT:
                jsObj.addProperty("type", type);
                break;

            case Constant.METHOD_LATEST_WALL:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("type", type);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_LATEST_GIF:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_MOST_VIEWED:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("type", type);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_MOST_VIEWED_GIF:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_MOST_RATED_GIF:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_MOST_RATED:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("type", type);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_WALLPAPER_BY_CAT:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("type", type);
                jsObj.addProperty("cat_id", cat_id);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_WALL_SEARCH:
                jsObj.addProperty("search_text", searchText);
                jsObj.addProperty("type", type);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_GIF_SEARCH:
                jsObj.addProperty("gif_search_text", searchText);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_WALL_DOWNLOAD:
                jsObj.addProperty("wallpaper_id", itemID);
                break;

            case Constant.METHOD_GIF_DOWNLOAD:
                jsObj.addProperty("gif_id", itemID);
                break;

            case Constant.METHOD_WALL_SINGLE:
                jsObj.addProperty("wallpaper_id", itemID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_GIF_SINGLE:
                jsObj.addProperty("gif_id", itemID);
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_WALL_RATING:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("device_id", colorID);
                jsObj.addProperty("rate", rate);
                break;

            case Constant.METHOD_GIF_RATING:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("device_id", colorID);
                jsObj.addProperty("rate", rate);
                break;

            case Constant.METHOD_WALL_GET_RATING:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("device_id", colorID);
                break;

            case Constant.METHOD_GIF_GET_RATING:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("device_id", colorID);
                break;

            case Constant.METHOD_CHECK_FAVORITE:
                jsObj.addProperty("type", type);
                jsObj.addProperty("id", itemID);
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Constant.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("auth_id", colorID);
                jsObj.addProperty("type", type);
                break;

            case Constant.METHOD_REGISTRATION:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("auth_id", colorID);
                jsObj.addProperty("type", type);
                break;

            case Constant.METHOD_FORGOT_PASSWORD:
                jsObj.addProperty("email", email);
                break;

            case Constant.METHOD_PROFILE:
                jsObj.addProperty("user_id", userID);
                break;

            case Constant.METHOD_EDIT_PROFILE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;

            case Constant.METHOD_REPORT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("item_id", itemID);
                jsObj.addProperty("user_txt", searchText);
                jsObj.addProperty("report_for", report_PostType);
                break;

            case Constant.METHOD_FAV_WALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("type", type);
                jsObj.addProperty("fav_type", report_PostType);
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("color_id", colorID);
                break;

            case Constant.METHOD_RECENT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("id", colorID);
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("type", report_PostType);
                break;

            case Constant.METHOD_DO_FAV:
                jsObj.addProperty("fav_type", report_PostType);
                jsObj.addProperty("post_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;
        }

        Log.e("aaa", API.toBase64(jsObj.toString()));
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", API.toBase64(jsObj.toString()))
                .build();
    }
}