package com.app.screenie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosong.frescozoomablelib.zoomable.DoubleTapGestureListener;
import com.bosong.frescozoomablelib.zoomable.ZoomableController;
import com.bosong.frescozoomablelib.zoomable.ZoomableDraweeView;
import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.app.adapter.AdapterTags;
import com.app.asyncTask.GetRating;
import com.app.asyncTask.LoadFav;
import com.app.asyncTask.LoadRating;
import com.app.asyncTask.LoadReport;
import com.app.asyncTask.LoadWallpaper;
import com.app.interfaces.GetRatingListener;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RatingListener;
import com.app.interfaces.SuccessListener;
import com.app.interfaces.WallListener;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.JSONParser;
import com.app.utils.Methods;
import com.app.utils.RecyclerItemClickListener;
import com.app.utils.SharedPref;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WallPaperDetailsActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Toolbar toolbar;
    Methods methods;
    SharedPref sharedPref;
    ViewPager viewpager;
    ImagePagerAdapter pagerAdapter;
    RecyclerView rv_tags;
    AdapterTags adapterTags;
    ArrayList<String> arrayListTags;
    LinearLayoutManager llm;
    int position;
    ImageView iv_option_toggle, iv_option_toggle2;
    LinearLayout ll_download, ll_share, ll_rate, ll_setas,download_hd;
    LikeButton button_fav, fav_report;
    Button app1,app2,app3,app4,app5;
    TextView tv_views, tv_downloads, tv_cat, tv_res, tv_size;

    Dialog dialog_rate;
    RatingBar ratingBar;
    RelativeLayout coordinatorLayout;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    String deviceId;
    ProgressDialog progressDialog;
    BottomSheetDialog dialog_setas;
    LinearLayout ll_adView, ll_option_toggle;
    int height = 0, pos_type = 0, page = 2;
    String wallType = "", color_ids = "", cid = "1";
    Boolean isOver = false;

    String curr_wall_url;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_details);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        toolbar = findViewById(R.id.toolbar_wall_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 55;
            toolbar.setLayoutParams(params);
        }

        progressDialog = new ProgressDialog(WallPaperDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);



        // methods = new Methods(this, new InterAdListener() {
        //     @Override
        //     public void onClick(int position, String type) {
        //         Log.e("Type", type);

        //         curr_wall_url = Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl();

        //         switch (type) {
        //            /* case "download":
        //                 new SaveTask("save").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getImage());
        //                 break;*/
        //             case "share":
        //                 new SaveTask("share").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getImage());
        //                 break;
        //             case "set":

        //                 if(curr_wall_url.contains("screenie.atozhacks")){
        //                 // new SaveTask("set").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getImage());
        //                 new SaveTask("set").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl());
        //                 break;
        //             }
        //                 else{
        //                     ll_setas.setOnClickListener(new View.OnClickListener() {
        //                         @Override
        //                         public void onClick(View view) {
        //                             Toast.makeText(WallPaperDetailsActivity.this, "Wallpaper is from Paid App, Please Install It", Toast.LENGTH_SHORT).show();
        //                             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curr_wall_url));
        //                             startActivity(intent);

        //                         }
        //                     });
        //                 }

        //         }
        //     }
        // });

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                curr_wall_url = Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl();
                //methods.saveImage(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl(), type, coordinatorLayout, "wallpaper");
            }
        });
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("pos", 0);
        pos_type = getIntent().getIntExtra("pos_type", 0);
        page = getIntent().getIntExtra("page", 0);
        wallType = getIntent().getStringExtra("wallType");
        color_ids = getIntent().getStringExtra("color_ids");
        if (pos_type == 3) {
            cid = getIntent().getStringExtra("cid");
        }

        height = methods.getScreenHeight();

        ll_adView = findViewById(R.id.ll_adView);
        ll_option_toggle = findViewById(R.id.ll_option_toggle);
        fav_report = findViewById(R.id.fav_report);
        coordinatorLayout = findViewById(R.id.rl);
        button_fav = findViewById(R.id.button_wall_fav);
        ll_download = findViewById(R.id.ll_download);
        ll_share = findViewById(R.id.ll_share);
        ll_rate = findViewById(R.id.ll_rate);
        ll_setas = findViewById(R.id.ll_setas);
        tv_views = findViewById(R.id.tv_wall_details_views);
        tv_downloads = findViewById(R.id.tv_wall_details_downloads);
        tv_cat = findViewById(R.id.tv_details_cat);

        download_hd = findViewById(R.id.download_hd);
        app1 = findViewById(R.id.app1);
        app2 = findViewById(R.id.app2);
        app3 = findViewById(R.id.app3);
        app4 = findViewById(R.id.app4);
        app5 = findViewById(R.id.app5);

        tv_res = findViewById(R.id.tv_details_resolution);
        tv_size = findViewById(R.id.tv_details_size);
        ratingBar = findViewById(R.id.rating_wall_details);
        iv_option_toggle = findViewById(R.id.iv_option_toggle);
        iv_option_toggle2 = findViewById(R.id.iv_option_toggle2);
        loadViewed(position);

        methods.showBannerAd(ll_adView);

        rv_tags = findViewById(R.id.rv_tags);
        llm = new LinearLayoutManager(WallPaperDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv_tags.setLayoutManager(llm);
        rv_tags.setItemAnimator(new DefaultItemAnimator());

        arrayListTags = new ArrayList<>(Arrays.asList(Constant.arrayList.get(position).getTags().split(",")));
        adapterTags = new AdapterTags(arrayListTags);
        rv_tags.setAdapter(adapterTags);

        setTotalView(Constant.arrayList.get(position).getTotalViews());
        tv_cat.setText(Constant.arrayList.get(position).getCName());
        ratingBar.setRating(Float.parseFloat(Constant.arrayList.get(position).getAverageRate()));

        pagerAdapter = new ImagePagerAdapter();
        viewpager = findViewById(R.id.vp_wall_details);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(position);

        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPer()) {
                    methods.showInter(0, getString(R.string.download));
                }
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPer()) {
                    methods.showInter(0, "share");
                    //Not there in new code(1line)
                    methods.saveImage(Constant.arrayList.get(viewpager.getCurrentItem()).getImage(), "Share", coordinatorLayout, "wallpaper");
                }
            }
        });

        ll_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPref.isLogged()) {
                    openRateDialog();
                } else {
                    methods.clickLogin();
                }
            }
        });

        download_hd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        ll_setas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(0, "set");
                if(curr_wall_url.contains(".jpg")){
                    //new SaveTask("save").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl());

                    methods.saveImage(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl(), "Set As", coordinatorLayout, "wallpaper");
                }

                else{
                    Toast.makeText(WallPaperDetailsActivity.this, "Wallpaper is from Paid App, Please Install It", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curr_wall_url));
                    startActivity(intent);
                }

            }
        });


        if (sharedPref.isLogged()) {
            button_fav.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    try {
                        loadFav(viewpager.getCurrentItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    try {
                        loadFav(viewpager.getCurrentItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            button_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!sharedPref.isLogged()) {
                        methods.clickLogin();
                    }
                }
            });
        }

        checkFav();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = viewpager.getCurrentItem();
                checkFav();
                ratingBar.setRating(Float.parseFloat(Constant.arrayList.get(position).getAverageRate()));
                setTotalView(Constant.arrayList.get(position).getTotalViews());
                tv_cat.setText(Constant.arrayList.get(position).getCName());
                tv_res.setText(Constant.arrayList.get(position).getResolution());
                tv_size.setText(Constant.arrayList.get(position).getSize());

                final int finalPosition = position;
                app1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Constant.arrayList.get(finalPosition).getApp1Url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });

                if (!(Constant.arrayList.get(finalPosition).getApp2Name().equals(""))) {
                    app2.setText(Constant.arrayList.get(position).getApp2Name());
                    app2.setVisibility(View.VISIBLE);
                    app2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = Constant.arrayList.get(finalPosition).getApp2Url();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }

                if (!(Constant.arrayList.get(finalPosition).getApp3Name().equals(""))) {
                    app3.setText(Constant.arrayList.get(position).getApp3Name());
                    app3.setVisibility(View.VISIBLE);
                    app3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = Constant.arrayList.get(finalPosition).getApp3Url();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }

                if (!(Constant.arrayList.get(finalPosition).getApp4Name().equals(""))) {
                    app4.setText(Constant.arrayList.get(position).getApp4Name());
                    app4.setVisibility(View.VISIBLE);
                    app4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = Constant.arrayList.get(finalPosition).getApp4Url();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }

                if (!(Constant.arrayList.get(finalPosition).getApp5Name().equals(""))) {
                    app5.setText(Constant.arrayList.get(position).getApp5Name());
                    app5.setVisibility(View.VISIBLE);
                    app5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = Constant.arrayList.get(finalPosition).getApp5Url();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }

                loadViewed(position);

                arrayListTags.clear();
                arrayListTags.addAll(Arrays.asList(Constant.arrayList.get(position).getTags().split(",")));
                adapterTags.notifyDataSetChanged();

                if (page != 0 && !isOver && position == Constant.arrayList.size() - 1) {
                    getLatestData();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        rv_tags.addOnItemTouchListener(new RecyclerItemClickListener(WallPaperDetailsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Constant.search_item = arrayListTags.get(position);
                Intent intent = new Intent(WallPaperDetailsActivity.this, SearchWallActivity.class);
                startActivity(intent);
            }
        }));

        fav_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });

        showOptionDialog();

        Animation anim_top = AnimationUtils.loadAnimation(WallPaperDetailsActivity.this, R.anim.fade);
        anim_top.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_option_toggle.startAnimation(anim_top);
            }
        }, 500);

        Animation anim_top2 = AnimationUtils.loadAnimation(WallPaperDetailsActivity.this, R.anim.fade);
        anim_top2.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_option_toggle2.startAnimation(anim_top2);
            }
        }, 1000);
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.btn_hd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(WallPaperDetailsActivity.this, "HD", Toast.LENGTH_SHORT).show();
                // Log.e("Link HD", Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl());
                if (checkPer()) {
                    methods.showInter(0, "download");
                }

                // String curr_wall_url = Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl();
                if(curr_wall_url.contains(".jpg")){
                    //new SaveTask("save").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl());

                    methods.saveImage(Constant.arrayList.get(viewpager.getCurrentItem()).getWallUrl(), "Download", coordinatorLayout, "wallpaper");

                    dialog.dismiss();
                }

                else{
                    Toast.makeText(WallPaperDetailsActivity.this, "Wallpaper is from Paid App, Please Install It", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curr_wall_url));
                    startActivity(intent);
                }

            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.btn_normal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(WallPaperDetailsActivity.this, "Normal", Toast.LENGTH_SHORT).show();
                //Log.e("Link Normal", Constant.arrayList.get(viewpager.getCurrentItem()).getImage());
                if (checkPer()) {
                    methods.showInter(0, "download");
                }

                //new SaveTask("save").execute(Constant.arrayList.get(viewpager.getCurrentItem()).getImage());

                methods.saveImage(Constant.arrayList.get(viewpager.getCurrentItem()).getImage(), "Download", coordinatorLayout, "wallpaper");

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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

    public void checkFav() {
        button_fav.setLiked(Constant.arrayList.get(viewpager.getCurrentItem()).getIsFav());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList.size();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.layout_vp_wall, container, false);
            assert imageLayout != null;

            final ZoomableDraweeView draweeView = imageLayout.findViewById(R.id.my_image_view);
            draweeView.setTapListener(new DoubleTapGestureListener(draweeView));

            final CircularProgressBar progressBar = imageLayout.findViewById(R.id.pb_wall_details);

            Uri uri = Uri.parse(Constant.arrayList.get(position).getImage());
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setCallerContext("ZoomableApp-MyPagerAdapter")
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                            progressBar.setVisibility(View.GONE);
                            super.onFinalImageSet(id, imageInfo, animatable);
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            progressBar.setVisibility(View.GONE);
                            super.onFailure(id, throwable);
                        }
                    })
                    .build();
            draweeView.setController(controller);

            draweeView.setSwipeDownListener(new ZoomableController.OnSwipeDownListener() {
                @Override
                public void onSwipeDown(float translateY) {

                }

                @Override
                public void onSwipeRelease(float translateY) {
                    int a = (int) ((translateY / height) * 100);
                    if (a > 25) {
                        onBackPressed();
                        overridePendingTransition(0, android.R.anim.fade_out);
                    }
                }
            });

            container.addView(imageLayout, 0);

            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void loadViewed(int pos) {
        try {
            dbHelper.addToRecent(Constant.arrayList.get(pos));
            if (methods.isNetworkAvailable()) {
                new MyTask("", pos, methods.getAPIRequest(Constant.METHOD_WALL_SINGLE, 0, "", "", "", "", Constant.arrayList.get(pos).getId(), "", "", "", "", "", sharedPref.getUserId(), "")).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        RequestBody requestBody;
        int pos;
        String downloads = "", type = "", res = "", size = "";

        MyTask(String type, int pos, RequestBody requestBody) {
            this.type = type;
            this.pos = pos;
            this.requestBody = requestBody;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String json = JSONParser.okhttpPost(Constant.SERVER_URL, requestBody);
            try {
                JSONObject jOb = new JSONObject(json);
                JSONArray jsonArray = jOb.getJSONArray(Constant.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objJson = jsonArray.getJSONObject(i);
                    downloads = objJson.getString(Constant.TAG_WALL_DOWNLOADS);
                    res = objJson.getString(Constant.TAG_RESOLUTION);
                    size = objJson.getString(Constant.TAG_SIZE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return String.valueOf(pos);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int p = Integer.parseInt(result);
            Constant.arrayList.get(p).setTotalDownloads(downloads);
            setTotalDownloads(downloads);
            tv_downloads.setText(downloads);
            if (!type.equals("download")) {
                int tot = Integer.parseInt(Constant.arrayList.get(p).getTotalViews());
                Constant.arrayList.get(p).setTotalViews("" + (tot + 1));
//            tv_views.setText(Constant.arrayList.get(position).getTotalViews());
            }

            app1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Constant.arrayList.get(p).getApp1Url();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });

            if (!(Constant.arrayList.get(p).getApp2Name().equals(""))) {
                app2.setText(Constant.arrayList.get(position).getApp2Name());
                app2.setVisibility(View.VISIBLE);
                app2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Constant.arrayList.get(p).getApp2Url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }

            if (!(Constant.arrayList.get(p).getApp3Name().equals(""))) {
                app3.setText(Constant.arrayList.get(position).getApp3Name());
                app3.setVisibility(View.VISIBLE);
                app3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Constant.arrayList.get(p).getApp3Url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }

            if (!(Constant.arrayList.get(p).getApp4Name().equals(""))) {
                app4.setText(Constant.arrayList.get(position).getApp4Name());
                app4.setVisibility(View.VISIBLE);
                app4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Constant.arrayList.get(p).getApp4Url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }

            if (!(Constant.arrayList.get(p).getApp5Name().equals(""))) {
                app5.setText(Constant.arrayList.get(position).getApp5Name());
                app5.setVisibility(View.VISIBLE);
                app5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Constant.arrayList.get(p).getApp5Url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }



            if (pos == viewpager.getCurrentItem()) {
                tv_res.setText(res);
                tv_size.setText(size);
            }
            Constant.arrayList.get(p).setResolution(res);
            Constant.arrayList.get(p).setSize(size);

            dbHelper.updateView(Constant.arrayList.get(p).getId(), Constant.arrayList.get(p).getTotalViews(), Constant.arrayList.get(p).getTotalDownloads(), Constant.arrayList.get(p).getResolution(), Constant.arrayList.get(p).getSize());
        }
    }

    private void openRateDialog() {
        dialog_rate = new Dialog(WallPaperDetailsActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_rating);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rating_add);
        ratingBar.setRating(1);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final TextView textView = dialog_rate.findViewById(R.id.tv_rate_dialog);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (Constant.arrayList.get(viewpager.getCurrentItem()).getUserRating().equals("0")) {
            new GetRating(new GetRatingListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String message, float rating) {
                    if (rating != 0 && success.equals("true")) {
                        ratingBar.setRating(rating);
                        textView.setText(getString(R.string.thanks_for_rating));
                    } else {
                        textView.setText(getString(R.string.rate_this_wall));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_WALL_GET_RATING, 0, deviceId, "", "", "", Constant.arrayList.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", "")).execute();
        } else {
            textView.setText(getString(R.string.thanks_for_rating));
            ratingBar.setRating(Float.parseFloat(Constant.arrayList.get(viewpager.getCurrentItem()).getUserRating()));
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() != 0) {
                    if (methods.isNetworkAvailable()) {
                        loadRatingApi(String.valueOf(ratingBar.getRating()));
                    } else {
                        methods.showSnackBar(coordinatorLayout, getResources().getString(R.string.internet_not_connected));
                    }
                } else {
                    Toast.makeText(WallPaperDetailsActivity.this, getString(R.string.enter_rating), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(final String rate) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(WallPaperDetailsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        LoadRating loadRating = new LoadRating(new RatingListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String message, float rating) {
                if (success.equals("true")) {
                    methods.showSnackBar(coordinatorLayout, message);
                    if (!message.contains("already")) {
                        Constant.arrayList.get(viewpager.getCurrentItem()).setAverageRate(String.valueOf(rating));
                        Constant.arrayList.get(viewpager.getCurrentItem()).setTotalRate(String.valueOf(Integer.parseInt(Constant.arrayList.get(viewpager.getCurrentItem()).getTotalRate()) + 1));
                        Constant.arrayList.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rate));
                        ratingBar.setRating(rating);
                    }
                } else {
                    methods.showSnackBar(coordinatorLayout, getResources().getString(R.string.server_no_conn));
                }
                dialog_rate.dismiss();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_WALL_RATING, 0, deviceId, "", "", "", Constant.arrayList.get(viewpager.getCurrentItem()).getId(), rate, "", "", "", "", "", ""));

        loadRating.execute();
    }

    private void setTotalView(String views) {
        try {
            tv_views.setText(methods.format(Double.parseDouble(views)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTotalDownloads(String downloads) {
        try {
            tv_downloads.setText(methods.format(Double.parseDouble(downloads)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_report, null);

        dialog_setas = new BottomSheetDialog(WallPaperDetailsActivity.this);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        final EditText editText_report;
        Button button_submit;

        button_submit = dialog_setas.findViewById(R.id.button_report_submit);
        editText_report = dialog_setas.findViewById(R.id.et_report);

        button_submit.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(WallPaperDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (sharedPref.isLogged()) {
                        loadReportSubmit(editText_report.getText().toString());
//                        Toast.makeText(WallPaperDetailsActivity.this, "Report is not available in demo app", Toast.LENGTH_SHORT).show();
                    } else {
                        methods.clickLogin();
                    }
                }
            }
        });
    }

    public void loadReportSubmit(String report) {
        if (methods.isNetworkAvailable()) {
            LoadReport loadReport = new LoadReport(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            try {
                                dialog_setas.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(WallPaperDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WallPaperDetailsActivity.this, getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REPORT, 0, "", "", "", report, Constant.arrayList.get(position).getId(), "", "", "", "", "", sharedPref.getUserId(), "wallpaper"));
            loadReport.execute();
        } else {
            Toast.makeText(this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void showOptionDialog() {
        LinearLayout llBottomSheet = findViewById(R.id.ll_hideshow);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                ll_option_toggle.setRotation(v * 180);
            }
        });
    }

    private Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(WallPaperDetailsActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(WallPaperDetailsActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    private void getLatestData() {
        if (methods.isNetworkAvailable()) {
            RequestBody requestBody = null;
            if (pos_type == 0) {
                requestBody = methods.getAPIRequest(Constant.METHOD_LATEST_WALL, page, color_ids, wallType, "", "", "", "", "", "", "", "", sharedPref.getUserId(), "");
            } else if (pos_type == 1) {
                requestBody = methods.getAPIRequest(Constant.METHOD_MOST_VIEWED, page, color_ids, wallType, "", "", "", "", "", "", "", "", sharedPref.getUserId(), "");
            } else if (pos_type == 2) {
                requestBody = methods.getAPIRequest(Constant.METHOD_MOST_RATED, page, color_ids, wallType, "", "", "", "", "", "", "", "", sharedPref.getUserId(), "");
            } else if (pos_type == 3) {
                requestBody = methods.getAPIRequest(Constant.METHOD_WALLPAPER_BY_CAT, page, color_ids, wallType, cid, "", "", "", "", "", "", "", sharedPref.getUserId(), "");
            } else if (pos_type == 4) {
                requestBody = methods.getAPIRequest(Constant.METHOD_FAV_WALL, page, color_ids, wallType, "", "", "", "", "", "", "", "", sharedPref.getUserId(), "wallpaper");
            }

            LoadWallpaper loadWallpaper = new LoadWallpaper(new WallListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWall, int totalNumber) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (arrayListWall.size() == 0) {
                                isOver = true;
                            } else {
                                for (int i = 0; i < arrayListWall.size(); i++) {
                                    dbHelper.addWallpaper(arrayListWall.get(i), DBHelper.TABLE_WALL_BY_LATEST);
                                }
                                page = page + 1;
                                Constant.arrayList.addAll(arrayListWall);
                                pagerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    }
                }
            }, requestBody);
            loadWallpaper.execute();
        }
    }

    private void loadFav(final int posi) {
        if (sharedPref.isLogged()) {
            if (methods.isNetworkAvailable()) {
                LoadFav loadFav = new LoadFav(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            if (favSuccess.equals("1")) {
                                Constant.arrayList.get(posi).setIsFav(true);
                            } else {
                                Constant.arrayList.get(posi).setIsFav(false);
                            }
                            Toast.makeText(WallPaperDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, methods.getAPIRequest(Constant.METHOD_DO_FAV, 0, Constant.arrayList.get(posi).getId(), "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "wallpaper"));
                loadFav.execute();
            } else {
                Toast.makeText(WallPaperDetailsActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.clickLogin();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDelete(EventAction eventAction) {
        try {
            new MyTask("download", viewpager.getCurrentItem(), methods.getAPIRequest(Constant.METHOD_WALL_DOWNLOAD, 0, "", "", "", "", Constant.arrayList.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", "")).execute();
            GlobalBus.getBus().removeStickyEvent(eventAction);
        } catch (Exception e) {
            GlobalBus.getBus().removeStickyEvent(eventAction);
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }
}