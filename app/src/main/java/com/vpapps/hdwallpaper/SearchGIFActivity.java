package com.vpapps.hdwallpaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vpapps.adapter.AdapterGIFsSearch;
import com.vpapps.asyncTask.LoadGIF;
import com.vpapps.interfaces.GIFListener;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.items.ItemGIF;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class SearchGIFActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    AdapterGIFsSearch adapter;
    ArrayList<ItemGIF> arrayList, arrayListTemp;
    ProgressBar progressBar;
    Methods methods;
    InterAdListener interAdListener;
    TextView textView_empty;
    LoadGIF loadGIF;
    GridLayoutManager grid;
    RelativeLayout rl_colors;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_by_cat);

        interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int realPos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(SearchGIFActivity.this, GIFsDetailsActivity.class);
                intent.putExtra("pos", realPos);
                Constant.arrayListGIF.clear();
                Constant.arrayListGIF.addAll(arrayListTemp);
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
        methods.showBannerAd(ll_ad);

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();
        progressBar = findViewById(R.id.pb_wallcat);
        textView_empty = findViewById(R.id.tv_empty_wallcat);
        rl_colors = findViewById(R.id.layout_colors);
        rl_colors.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(SearchGIFActivity.this, 3);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) >= 1000 ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        getGIFData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            getGIFData();
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

    private void getGIFData() {
        if (methods.isNetworkAvailable()) {
            loadGIF = new LoadGIF(new GIFListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    arrayListTemp.clear();
                    recyclerView.setVisibility(View.GONE);
                    textView_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGIF> arrayListGIF, int totalNumber) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayListTemp.addAll(arrayListGIF);
                            if (Constant.isNativeAd) {
                                for (int i = 0; i < arrayListGIF.size(); i++) {
                                    arrayList.add(arrayListGIF.get(i));
                                    int abc = arrayList.lastIndexOf(null);
                                    if (((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0) && i+1 != totalNumber) {
                                        arrayList.add(null);
                                    }
                                }
                            } else {
                                arrayList.addAll(arrayListGIF);
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
            }, methods.getAPIRequest(Constant.METHOD_GIF_SEARCH, 0, "", "", "", Constant.search_item.replace(" ", "%20"), "", "", "", "", "", "",new SharedPref(SearchGIFActivity.this).getUserId(), ""));
            loadGIF.execute();
        } else {
            setAdapter();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        adapter = new AdapterGIFsSearch(SearchGIFActivity.this, arrayList, new RecyclerViewClickListener() {
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