package com.app.screenie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.adapter.AdapterColors;
import com.app.adapter.AdapterWallpaperSearch;
import com.app.asyncTask.LoadWallpaper;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.WallListener;
import com.app.items.ItemColors;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
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

public class SearchWallActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView, rv_colors;
    AdapterWallpaperSearch adapter;
    ArrayList<ItemWallpaper> arrayList, arrayListTemp;
    ProgressBar progressBar;
    Methods methods;
    InterAdListener interAdListener;
    TextView textView_empty;
    GridLayoutManager grid;
    String wallType;
    Button button_colors_go;

    ArrayList<ItemColors> arrayListColors;
    AdapterColors adapterColors;
    String color_ids = "";
    SharedPref sharedPref;
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

        grid = new GridLayoutManager(this, 2);
        // if (wallType.equals(getString(R.string.landscape))) {
        //     grid.setSpanCount(2);
        // } else {
        //     grid.setSpanCount(3);
        // }

        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) >= 1000 ? grid.getSpanCount() : 1;
            }
        });

        interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int realPos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(SearchWallActivity.this, WallPaperDetailsActivity.class);
                intent.putExtra("pos", realPos);
                intent.putExtra("page", 0);
                Constant.arrayList.clear();
                Constant.arrayList.addAll(arrayListTemp);
                startActivity(intent);
            }
        };

        methods = new Methods(this, interAdListener);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_wall_by_cat);
        toolbar.setTitle(getString(R.string.search));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout ll_ad = findViewById(R.id.ll_ad_search);
        //methods.showBannerAd(ll_ad);

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
                    arrayListTemp.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
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

        recyclerView = findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(grid);

        getWallpaperData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = (SearchView) item.getActionView();
        item.expandActionView();
        searchView.setQuery(Constant.search_item, false);
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_item = s;
            getWallpaperData();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

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

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            LoadWallpaper loadWallpaper = new LoadWallpaper(new WallListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    arrayListTemp.clear();
                    recyclerView.setVisibility(View.GONE);
                    textView_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWall, int totalNumber) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayListTemp.addAll(arrayListWall);
                            if (Constant.isNativeAd) {
                                for (int i = 0; i < arrayListWall.size(); i++) {
                                    arrayList.add(arrayListWall.get(i));
                                    int abc = arrayList.lastIndexOf(null);
                                    if ((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0 && i + 1 != totalNumber) {
                                        arrayList.add(null);
                                    }
                                }
                            } else {
                                arrayList.addAll(arrayListWall);
                            }

                            setAdapter();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        setEmpty();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }, methods.getAPIRequest(Constant.METHOD_WALL_SEARCH, 0, color_ids, wallType, "", Constant.search_item.replace(" ", "%20"), "", "", "", "", "", "", new SharedPref(SearchWallActivity.this).getUserId(), ""));
            loadWallpaper.execute();
        } else {
            setAdapter();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter() {
        if (adapter == null) {
            adapter = new AdapterWallpaperSearch(SearchWallActivity.this, wallType, arrayList, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });
            AnimationAdapter adapterAnim = new ScaleInAnimationAdapter(adapter);
            adapterAnim.setFirstOnly(true);
            adapterAnim.setDuration(500);
            adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
            recyclerView.setAdapter(adapterAnim);
        } else {
            adapter.notifyDataSetChanged();
        }
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}