package com.app.screenie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.adapter.AdapterColors;
import com.app.adapter.AdapterWallpaper;
import com.app.asyncTask.LoadWallpaper;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.WallListener;
import com.app.items.ItemColors;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.EndlessRecyclerViewScrollListener;
import com.app.utils.Methods;
import com.app.utils.RecyclerItemClickListener;
import com.app.utils.SharedPref;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class WallpaperByCatActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Toolbar toolbar;
    RecyclerView recyclerView, rv_colors;
    AdapterWallpaper adapter;
    ArrayList<ItemWallpaper> arrayList, arrayListTemp;
    ProgressBar progressBar;
    Methods methods;
    InterAdListener interAdListener;
    Boolean isOver = false, isScroll = false;
    TextView textView_empty;
    LoadWallpaper loadWallpaper;
    int page = 1;
    GridLayoutManager grid;
    String cid, cname, from = "";
    String wallType;
    FloatingActionButton fab;
    ArrayList<ItemColors> arrayListColors;
    AdapterColors adapterColors;
    String color_ids = "";
    SharedPref sharedPref;
    Button button_colors_go;
    RelativeLayout rl_colors;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_by_cat);

        sharedPref = new SharedPref(this);
        wallType = sharedPref.getWallType();

        grid = new GridLayoutManager(this, 3);
        // if (wallType.equals(getString(R.string.landscape))) {
        //     grid.setSpanCount(2);
        // } else {
        //     grid.setSpanCount(3);
        // }

        interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int real_pos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(WallpaperByCatActivity.this, WallPaperDetailsActivity.class);

                intent.putExtra("pos", real_pos);
                intent.putExtra("pos_type", 3);
                intent.putExtra("page", page);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);
                intent.putExtra("cid", cid);

                Constant.arrayList.clear();
                Constant.arrayList.addAll(arrayListTemp);
                startActivity(intent);
            }
        };

        dbHelper = new DBHelper(this);
        methods = new Methods(this, interAdListener);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        LinearLayout ll_ad = findViewById(R.id.ll_ad_search);
        dbHelper.getAbout();
        //methods.showBannerAd(ll_ad);

        cid = getIntent().getStringExtra("cid");
        cname = getIntent().getStringExtra("cname");
        from = getIntent().getStringExtra("from");

        toolbar = this.findViewById(R.id.toolbar_wall_by_cat);
        toolbar.setTitle(cname);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        rl_colors = findViewById(R.id.layout_colors);

        if (Constant.isColorOn && Constant.arrayListColors.size() > 0) {
            arrayListColors = new ArrayList<>();

            rv_colors = findViewById(R.id.rv_wall_colors);
            rv_colors.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);
            rv_colors.setNestedScrollingEnabled(false);

            adapterColors = new AdapterColors(this, Constant.arrayListColors);
            rv_colors.setAdapter(adapterColors);

            button_colors_go = findViewById(R.id.button_colors_go);
            button_colors_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color_ids = adapterColors.getSelected();
                    arrayList.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    page = 1;
                    isScroll = false;
                    isOver = false;
                    getWallpaperData();
                }
            });
            rv_colors.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    adapterColors.setSelected(position);
                }
            }));
        } else {
            rl_colors.setVisibility(View.GONE);
        }

        progressBar = findViewById(R.id.pb_wallcat);
        textView_empty = findViewById(R.id.tv_empty_wallcat);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);

        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getWallpaperData();
                        }
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = grid.findFirstVisibleItemPosition();

                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        getWallpaperData();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_item = s;
            Intent intent = new Intent(WallpaperByCatActivity.this, SearchWallActivity.class);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            loadWallpaper = new LoadWallpaper(new WallListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        dbHelper.removeWallByCat("catlist", cid);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWall, int totalNumber) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (arrayListWall.size() == 0) {
                                isOver = true;
                                try {
                                    adapter.hideHeader();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                setEmpty();
                            } else {
                                arrayListTemp.addAll(arrayListWall);
                                for (int i = 0; i < arrayListWall.size(); i++) {
                                    dbHelper.addWallpaper(arrayListWall.get(i), DBHelper.TABLE_WALL_BY_CAT);

                                    arrayList.add(arrayListWall.get(i));

                                    if(Constant.isNativeAd) {
                                        int abc = arrayList.lastIndexOf(null);
                                        if (((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0) && (arrayListWall.size()-1 != i || arrayListTemp.size() != totalNumber)) {
                                            arrayList.add(null);
                                        }
                                    }
                                }

                                page = page + 1;
                                setAdapter();
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        setEmpty();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }, methods.getAPIRequest(Constant.METHOD_WALLPAPER_BY_CAT, page, color_ids, wallType, cid, "", "", "", "", "", "", "", new SharedPref(WallpaperByCatActivity.this).getUserId(), ""));
            loadWallpaper.execute();
        } else {
            arrayList = dbHelper.getWallByCat(cid, wallType, color_ids);
            setAdapter();
            isOver = true;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterWallpaper(WallpaperByCatActivity.this, wallType, arrayList, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });
            AnimationAdapter adapterAnim = new ScaleInAnimationAdapter(adapter);
            adapterAnim.setFirstOnly(true);
            adapterAnim.setDuration(500);
            adapterAnim.setInterpolator(new OvershootInterpolator(.5f));
            recyclerView.setAdapter(adapterAnim);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setEmpty() {
        progressBar.setVisibility(View.INVISIBLE);
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (from.equals("noti")) {
            Intent intent = new Intent(WallpaperByCatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}