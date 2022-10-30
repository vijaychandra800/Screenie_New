package com.vpapps.hdwallpaper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.vpapps.asyncTask.LoadAbout;
import com.vpapps.fragments.FragmentDashboard;
import com.vpapps.fragments.FragmentFavourite;
import com.vpapps.fragments.FragmentGIFs;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.interfaces.AdConsentListener;
import com.vpapps.utils.AdConsent;
import com.vpapps.utils.AdManagerInterAdmob;
import com.vpapps.utils.AdManagerInterApplovin;
import com.vpapps.utils.AdManagerInterStartApp;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
 
    Methods methods;
    DBHelper dbHelper;
    FragmentManager fm;
    LinearLayout ll_ad;
    Toolbar toolbar;
    AdConsent adConsent;
    DrawerLayout drawer;
    SharedPref sharedPref;
    NavigationView navigationView;
    MenuItem menu_login, menu_prof;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(), //Insert your own package name.
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_ad = findViewById(R.id.ll_adView);

        fm = getSupportFragmentManager();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.mipmap.nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);

        Constant.isGIFEnabled = sharedPref.getIsGIF();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideGIFMenu();

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_ad);
            }
        });

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
                            if (Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constant.appUpdateMsg);
                            } else {
                                adConsent.checkForConsent();
                                dbHelper.addtoAbout();

                                sharedPref.setIsGIF(Constant.isGIFEnabled);
                                hideGIFMenu();

                                methods.initializeAds();

                                sharedPref.setAdDetails(Constant.isBannerAd, Constant.isInterAd, Constant.isNativeAd, Constant.bannerAdType,
                                        Constant.interstitialAdType, Constant.nativeAdType, Constant.ad_banner_id, Constant.ad_inter_id, Constant.ad_native_id, Constant.startapp_id, Constant.adInterstitialShow, Constant.adNativeShow);

                                if (Constant.isInterAd) {
                                    switch (Constant.interstitialAdType) {
                                        case Constant.AD_TYPE_ADMOB:
                                        case Constant.AD_TYPE_FACEBOOK:
                                            AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(getApplicationContext());
                                            adManagerInterAdmob.createAd();
                                            break;
                                        case Constant.AD_TYPE_STARTAPP:
                                            AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(getApplicationContext());
                                            adManagerInterStartApp.createAd();
                                            break;
                                        case Constant.AD_TYPE_APPLOVIN:
                                            AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(MainActivity.this);
                                            adManagerInterApplovin.createAd();
                                            break;
                                    }
                                }
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }

            }, methods.getAPIRequest(Constant.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            loadAbout.execute();
        } else {
            FragmentDashboard f1 = new FragmentDashboard();
            loadFrag(f1, getResources().getString(R.string.home), fm);
            navigationView.setCheckedItem(R.id.nav_home);

            adConsent.checkForConsent();
            dbHelper.getAbout();
        }

        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_prof = menu.findItem(R.id.nav_profile);
        changeLoginName();

        FragmentDashboard f1 = new FragmentDashboard();
        loadFrag(f1, getResources().getString(R.string.home), fm);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() != 0) {
//            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
//            if (title.equals(getString(R.string.dashboard)) || title.equals(getString(R.string.home))) {
//                title = getString(R.string.home);

            int vp_pos = FragmentDashboard.mViewPager.getCurrentItem();
            switch (vp_pos) {
                case 0:
                    getSupportActionBar().setTitle(getString(R.string.home));
                    break;
                case 1:
                    getSupportActionBar().setTitle(getString(R.string.latest));
                    break;
                case 2:
                    getSupportActionBar().setTitle(getString(R.string.categories));
                    break;
                case 3:
                    getSupportActionBar().setTitle(getString(R.string.popular));
                    break;
                case 4:
                    getSupportActionBar().setTitle(getString(R.string.rated));
                    break;
            }

            navigationView.setCheckedItem(R.id.nav_home);
            super.onBackPressed();
        } else {
            exitDialog();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        clickNav(item.getItemId());
        return true;
    }

    private void clickNav(int item) {
        switch (item) {
            case R.id.nav_home:
                FragmentDashboard fhome = new FragmentDashboard();
                loadFrag(fhome, getResources().getString(R.string.home), fm);
                break;
            case R.id.nav_gif:
                FragmentGIFs fgif = new FragmentGIFs();
                loadFrag(fgif, getResources().getString(R.string.gifs), fm);
                break;

//            case R.id.nav_latest:
//                FragmentRecent fpop2 = new FragmentRecent();
//                Bundle args_pop2 = new Bundle();
//                args_pop2.putInt("pos", 0);
//                fpop2.setArguments(args_pop2);
//                loadFrag(fpop2, getResources().getString(R.string.latest), fm);
//                break;
//            case R.id.nav_popular:
//                FragmentRecent fpop = new FragmentRecent();
//                Bundle args_pop = new Bundle();
//                args_pop.putInt("pos", 1);
//                fpop.setArguments(args_pop);
//                loadFrag(fpop, getResources().getString(R.string.popular), fm);
//                break;
//            case R.id.nav_rated:
//                FragmentRecent frate = new FragmentRecent();
//                Bundle args_rate = new Bundle();
//                args_rate.putInt("pos", 2);
//                frate.setArguments(args_rate);
//                loadFrag(frate, getResources().getString(R.string.rated), fm);
//                break;
//            case R.id.nav_cat:
//                FragmentCategories fcat = new FragmentCategories();
//                loadFrag(fcat, getResources().getString(R.string.categories), fm);
//                break;
            case R.id.nav_fav:
                FragmentFavourite ffav = new FragmentFavourite();
                loadFrag(ffav, getResources().getString(R.string.favourite), fm);
                break;
            case R.id.nav_setting:
                Intent intent_set = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent_set);
                break;
            case R.id.nav_profile:
                Intent intent_pro = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent_pro);
                break;
            case R.id.nav_login:
                methods.clickLogin();
                break;
        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (!name.equals(getString(R.string.home))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.frame_layout, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.frame_layout, f1, name);
        }

        ft.commitAllowingStateLoss();

        getSupportActionBar().setTitle(name);
    }

    private void hideGIFMenu() {
        if (!Constant.isGIFEnabled && navigationView != null) {
            navigationView.getMenu().findItem(R.id.nav_gif).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_gif).setVisible(true);
        }
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialog);

        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (new SharedPref(MainActivity.this).isLogged()) {
                menu_prof.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.logout));
            } else {
                menu_prof.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.login));
            }
        }
    }

    @Override
    protected void onResume() {
        changeLoginName();
        super.onResume();
    }
}