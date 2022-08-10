package com.app.screenie;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.app.adapter.AdapterTags;
import com.app.asyncTask.GetRating;
import com.app.asyncTask.LoadFav;
import com.app.asyncTask.LoadRating;
import com.app.asyncTask.LoadReport;
import com.app.interfaces.GetRatingListener;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RatingListener;
import com.app.interfaces.SuccessListener;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.JSONParser;
import com.app.utils.Methods;
import com.app.utils.RecyclerItemClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.RequestBody;

public class GIFsDetailsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Toolbar toolbar;
    private Methods methods;
    private ViewPager viewpager;
    private RecyclerView rv_tags;
    private AdapterTags adapterTags;
    private ArrayList<String> arrayListTags;
    private int position;
    private LinearLayout ll_download, ll_share, ll_rate, ll_setas, ll_option_toggle;
    private LikeButton button_fav, fav_report;
    private TextView tv_views, tv_downloads, tv_res, tv_size;
    ImageView iv_option_toggle, iv_option_toggle2;
    private Dialog dialog_rate;
    private RatingBar ratingBar;
    private RelativeLayout coordinatorLayout;
    private String deviceId;
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog_setas;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_details);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        progressDialog = new ProgressDialog(GIFsDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        dbHelper = new DBHelper(this);
        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                methods.saveImage(Constant.arrayListGIF.get(viewpager.getCurrentItem()).getImage(), type, coordinatorLayout, "gif");
            }
        });
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_wall_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 55;
            toolbar.setLayoutParams(params);
        }
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("pos", 0);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        ll_option_toggle = findViewById(R.id.ll_option_toggle);
        fav_report = findViewById(R.id.fav_report);
        coordinatorLayout = findViewById(R.id.rl);
        button_fav = findViewById(R.id.button_wall_fav);
        ll_download = findViewById(R.id.ll_download);
        ll_share = findViewById(R.id.ll_share);
        ll_rate = findViewById(R.id.ll_rate);
        ll_setas = findViewById(R.id.ll_setas);
        tv_views = findViewById(R.id.tv_wall_details_views);
        tv_res = findViewById(R.id.tv_details_resolution);
        tv_size = findViewById(R.id.tv_details_size);
        tv_downloads = findViewById(R.id.tv_wall_details_downloads);
        ratingBar = findViewById(R.id.rating_wall_details);
        iv_option_toggle = findViewById(R.id.iv_option_toggle);
        iv_option_toggle2 = findViewById(R.id.iv_option_toggle2);

        loadViewed(position);

        methods.showBannerAd(ll_adView);

        rv_tags = findViewById(R.id.rv_tags);
        LinearLayoutManager llm = new LinearLayoutManager(GIFsDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv_tags.setLayoutManager(llm);
        rv_tags.setItemAnimator(new DefaultItemAnimator());

        arrayListTags = new ArrayList<>(Arrays.asList(Constant.arrayListGIF.get(position).getTags().split(",")));
        adapterTags = new AdapterTags(arrayListTags);
        rv_tags.setAdapter(adapterTags);

        setTotalView(Constant.arrayListGIF.get(position).getTotalViews());
        ratingBar.setRating(Float.parseFloat(Constant.arrayListGIF.get(position).getAveargeRate()));

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager = findViewById(R.id.vp_wall_details);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);

        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.showInter(0, getString(R.string.download));
                }
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.showInter(0, getString(R.string.share));
                }
            }
        });

        ll_setas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.showInter(0, getString(R.string.set_wallpaper));
                }
            }
        });

        if (Constant.isLogged) {
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
                    if (!Constant.isLogged) {
                        methods.clickLogin();
                    }
                }
            });
        }

        ll_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isLogged) {
                    openRateDialog();
                } else {
                    methods.clickLogin();
                }
            }
        });

        checkFav();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = viewpager.getCurrentItem();
                checkFav();
                ratingBar.setRating(Float.parseFloat(Constant.arrayListGIF.get(position).getAveargeRate()));
                setTotalView(Constant.arrayListGIF.get(position).getTotalViews());
                tv_res.setText(Constant.arrayListGIF.get(position).getResolution());
                tv_size.setText(Constant.arrayListGIF.get(position).getSize());
                loadViewed(position);

                arrayListTags.clear();
                arrayListTags.addAll(Arrays.asList(Constant.arrayListGIF.get(position).getTags().split(",")));
                adapterTags.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        rv_tags.addOnItemTouchListener(new RecyclerItemClickListener(GIFsDetailsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Constant.search_item = arrayListTags.get(position);
                Intent intent = new Intent(GIFsDetailsActivity.this, SearchGIFActivity.class);
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

        Animation anim_top = AnimationUtils.loadAnimation(GIFsDetailsActivity.this, R.anim.fade);
        anim_top.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_option_toggle.startAnimation(anim_top);
            }
        }, 500);

        Animation anim_top2 = AnimationUtils.loadAnimation(GIFsDetailsActivity.this, R.anim.fade);
        anim_top2.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_option_toggle2.startAnimation(anim_top2);
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wallpaper, menu);
        menu.findItem(R.id.menu_setwall).setVisible(false);
        return super.onCreateOptionsMenu(menu);
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

        button_fav.setLiked(dbHelper.isFavGIF(Constant.arrayListGIF.get(viewpager.getCurrentItem()).getId()));
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayListGIF.size();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.layout_vp_gif, container, false);
            assert imageLayout != null;
            final SimpleDraweeView imageView = imageLayout.findViewById(R.id.imagegif);
//            Picasso.get().load(Constant.arrayListGIF.get(position).getImage()).into(imageView);

            new AsyncTask<String, String, String>() {
                float aspect_ratio;

                @Override
                protected String doInBackground(String... strings) {
                    if (Constant.arrayListGIF.get(Integer.parseInt(strings[0])).getResolution().equals("")) {
                        final Bitmap image;
                        try {
                            image = Picasso.get().load(Constant.arrayListGIF.get(Integer.parseInt(strings[0])).getImage().replace(" ", "%20")).get();
                            float width = image.getWidth();
                            float height = image.getHeight();
                            aspect_ratio = width / height;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String[] a = Constant.arrayListGIF.get(Integer.parseInt(strings[0])).getResolution().split("X");
                        float width = Float.parseFloat(a[0]);
                        float height = Float.parseFloat(a[1]);
                        aspect_ratio = width / height;
                    }
                    return strings[0];
                }

                @Override
                protected void onPostExecute(String s) {
                    RelativeLayout.LayoutParams params;
                    if (aspect_ratio > 1) {
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    } else {
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    }
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    imageView.setLayoutParams(params);
                    imageView.setAspectRatio(aspect_ratio);
                    Uri uri = Uri.parse(Constant.arrayListGIF.get(Integer.parseInt(s)).getImage().replace(" ", "%20"));

                    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(imageRequest.getSourceUri())
                            .setControllerListener(controllerListener)
                            .setAutoPlayAnimations(true)
                            .build();
                    imageView.setController(controller);
                    super.onPostExecute(s);
                }
            }.execute(String.valueOf(position));

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (anim != null) {
                    anim.start();
                }
            }
        };

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void loadViewed(int pos) {
        try {
            if (methods.isNetworkAvailable()) {
                new MyTask("", pos, methods.getAPIRequest(Constant.METHOD_GIF_SINGLE, 0, "", "", "", "", Constant.arrayListGIF.get(pos).getId(), "", "", "", "", "", Constant.itemUser.getId(), "")).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        String downloads = "", type = "", res = "", size = "";
        int pos;
        RequestBody requestBody;

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
            Constant.arrayListGIF.get(p).setTotalDownload(downloads);
            setTotalDownloads(downloads);

            if (!type.equals("download")) {
                int tot = Integer.parseInt(Constant.arrayListGIF.get(p).getTotalViews());
                Constant.arrayListGIF.get(p).setTotalViews("" + (tot + 1));
            }

            if (pos == viewpager.getCurrentItem()) {
                tv_res.setText(res);
                tv_size.setText(size);
            }
            Constant.arrayListGIF.get(p).setResolution(res);
            Constant.arrayListGIF.get(p).setSize(size);

            dbHelper.updateViewGIF(Constant.arrayListGIF.get(p).getId(), Constant.arrayListGIF.get(p).getTotalViews(), Constant.arrayListGIF.get(p).getTotalDownload(), Constant.arrayListGIF.get(p).getResolution(), Constant.arrayListGIF.get(p).getSize());
        }
    }

    private void openRateDialog() {
        dialog_rate = new Dialog(GIFsDetailsActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_rating);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rating_add);
        ratingBar.setRating(1);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final TextView textView = dialog_rate.findViewById(R.id.tv_rate_dialog);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (Constant.arrayListGIF.get(viewpager.getCurrentItem()).getUserRating().equals("0")) {
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
                        textView.setText(getString(R.string.rate_this_gif));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_GIF_GET_RATING, 0, deviceId, "", "", "", Constant.arrayListGIF.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", "")).execute();
        } else {
            textView.setText(getString(R.string.thanks_for_rating));
            ratingBar.setRating(Float.parseFloat(Constant.arrayListGIF.get(viewpager.getCurrentItem()).getUserRating()));
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
                    Toast.makeText(GIFsDetailsActivity.this, getString(R.string.enter_rating), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(String rate) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(GIFsDetailsActivity.this);
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
                        Constant.arrayListGIF.get(viewpager.getCurrentItem()).setAveargeRate(String.valueOf(rating));
                        Constant.arrayListGIF.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rating));
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
        }, methods.getAPIRequest(Constant.METHOD_GIF_RATING, 0, deviceId, "", "", "", Constant.arrayListGIF.get(viewpager.getCurrentItem()).getId(), rate, "", "", "", "", "", ""));

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

        dialog_setas = new BottomSheetDialog(GIFsDetailsActivity.this);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        final EditText editText_report;
        Button button_submit;

        button_submit = dialog_setas.findViewById(R.id.button_report_submit);
        editText_report = dialog_setas.findViewById(R.id.et_report);
        TextView tv_report = dialog_setas.findViewById(R.id.tv_report);
        tv_report.setText(getString(R.string.report_gif_));

        button_submit.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(GIFsDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (Constant.isLogged) {
                        loadReportSubmit(editText_report.getText().toString());
//                        Toast.makeText(GIFsDetailsActivity.this, "Report is not available in demo app", Toast.LENGTH_SHORT).show();
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
                            dialog_setas.dismiss();
                            Toast.makeText(GIFsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GIFsDetailsActivity.this, getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REPORT, 0, "", "", "", report, Constant.arrayListGIF.get(position).getId(), "", "", "", "", "", Constant.itemUser.getId(), "gif"));
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

    private void loadFav(final int posi) {
        if (Constant.isLogged) {
            if (methods.isNetworkAvailable()) {
                LoadFav loadFav = new LoadFav(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            if (favSuccess.equals("1")) {
                                Constant.arrayListGIF.get(posi).setIsFav(true);
                            } else {
                                Constant.arrayListGIF.get(posi).setIsFav(false);
                            }
                            Toast.makeText(GIFsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, methods.getAPIRequest(Constant.METHOD_DO_FAV, 0, Constant.arrayListGIF.get(posi).getId(), "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "gif"));
                loadFav.execute();
            } else {
                Toast.makeText(GIFsDetailsActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.clickLogin();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDelete(EventAction eventAction) {
        try {
            new MyTask("download", viewpager.getCurrentItem(), methods.getAPIRequest(Constant.METHOD_GIF_DOWNLOAD, 0, "", "", "", "", Constant.arrayListGIF.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", "")).execute();
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