package com.app.screenie;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.app.asyncTask.LoadAbout;
import com.app.interfaces.AboutListener;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.Methods;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private TextView textView_appname, textView_email, textView_website, textView_company, textView_contact, textView_version;
    private ImageView imageView_logo;
    private LinearLayout ll_email, ll_website, ll_company, ll_contact;
    private String website, email, desc, applogo, appname, appversion, appauthor, appcontact;
    private DBHelper dbHelper;
    private ProgressDialog pbar;
    private Methods methods;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_about);
        toolbar.setTitle(getString(R.string.menu_about));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pbar = new ProgressDialog(this);
        pbar.setMessage(getResources().getString(R.string.loading));
        pbar.setCancelable(false);

        webView = findViewById(R.id.webView);
        textView_appname = findViewById(R.id.textView_about_appname);
        textView_email = findViewById(R.id.textView_about_email);
        textView_website = findViewById(R.id.textView_about_site);
        textView_company = findViewById(R.id.textView_about_company);
        textView_contact = findViewById(R.id.textView_about_contact);
        textView_version = findViewById(R.id.textView_about_appversion);
        imageView_logo = findViewById(R.id.imageView_about_logo);

        ll_email = findViewById(R.id.ll_email);
        ll_website = findViewById(R.id.ll_website);
        ll_contact = findViewById(R.id.ll_contact);
        ll_company = findViewById(R.id.ll_company);

        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {
                    pbar.show();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (pbar.isShowing()) {
                        pbar.dismiss();
                    }

                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            setVariables();
                            dbHelper.addtoAbout();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            loadAbout.execute();
        } else {
            if (dbHelper.getAbout()) {
                setVariables();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void setVariables() {

        appname = Constant.itemAbout.getAppName();
        applogo = Constant.itemAbout.getAppLogo();
        desc = Constant.itemAbout.getAppDesc();
        appversion = Constant.itemAbout.getAppVersion();
        appauthor = Constant.itemAbout.getAuthor();
        appcontact = Constant.itemAbout.getContact();
        email = Constant.itemAbout.getEmail();
        website = Constant.itemAbout.getWebsite();

        textView_appname.setText(appname);
        if (!email.trim().isEmpty()) {
            ll_email.setVisibility(View.VISIBLE);
            textView_email.setText(email);
        }

        if (!website.trim().isEmpty()) {
            ll_website.setVisibility(View.VISIBLE);
            textView_website.setText(website);
        }

        if (!appauthor.trim().isEmpty()) {
            ll_company.setVisibility(View.VISIBLE);
            textView_company.setText(appauthor);
        }

        if (!appcontact.trim().isEmpty()) {
            ll_contact.setVisibility(View.VISIBLE);
            textView_contact.setText(appcontact);
        }

        if (!appversion.trim().isEmpty()) {
            textView_version.setText(appversion);
        }

        if (applogo.trim().isEmpty()) {
            imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .get()
                    .load(Constant.URL_ABOUT_US_LOGO + applogo)
                    .into(imageView_logo);
        }

        String mimeType = "text/html";
        String encoding = "utf-8";

        String text = "";
        if (methods.isDarkMode()) {
            text = "<html><head>"
                    + "<style> body{color:#fff !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + desc
                    + "</body></html>";
        } else {
            text = "<html><head>"
                    + "<style> body{color:#000 !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + desc
                    + "</body></html>";
        }

        webView.setBackgroundColor(Color.TRANSPARENT);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            webView.loadData(text, mimeType, encoding);
        } else {
            webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }
    }
}