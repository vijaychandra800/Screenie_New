package com.app.screenie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

//import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.material.navigation.NavigationView;
import com.app.asyncTask.LoadAbout;
import com.app.fragments.FragmentDashboard;
import com.app.fragments.FragmentFavourite;
import com.app.fragments.FragmentGIFs;
import com.app.interfaces.AboutListener;
import com.app.interfaces.AdConsentListener;
import com.app.utils.AdConsent;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.Methods;
import com.app.utils.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
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


      /*  Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
*/

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
                //methods.showBannerAd(ll_ad);
                //MediationTestSuite.launch(MainActivity.this);  //Open Bidding Test
            }
        });



        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
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
                        }
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
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

        checkPer();

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
            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
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
            case R.id.nav_youtube:
                Intent yt = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yt_channel_link)));
                startActivity(yt);
                break;
            case R.id.nav_telegram:
                Intent tel = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tel_channel_link)));
                startActivity(tel);
                break;
            case R.id.nav_insta:
                Intent insta = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.insta_link)));
                startActivity(insta);
                break;
            case R.id.nav_mail:
                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"vijaychandra800@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Screenie - Setup Submission/Feedback");
                intent.putExtra(Intent.EXTRA_TEXT,"Submit your Setup's here by uploading Wallpaper,Setup Screenshot and Launcher Backup's and Details of the Setup. Please Upload KWGT Widget Backup only if it's a Custom Made Widget by You and Don't forget to give your Social Profile link for giving Credits(Twitter/Instagram");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
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

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (Constant.isLogged) {
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