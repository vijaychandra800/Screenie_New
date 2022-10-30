package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vpapps.adapter.AdapterColors;
import com.vpapps.adapter.AdapterWallpaper;
import com.vpapps.asyncTask.LoadWallpaper;
import com.vpapps.hdwallpaper.R;
import com.vpapps.hdwallpaper.SearchWallActivity;
import com.vpapps.hdwallpaper.WallPaperDetailsActivity;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.interfaces.WallListener;
import com.vpapps.items.ItemWallpaper;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.EndlessRecyclerViewScrollListener;
import com.vpapps.utils.Methods;
import com.vpapps.utils.RecyclerItemClickListener;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FragmentWallByCat extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private AdapterWallpaper adapter;
    private ArrayList<ItemWallpaper> arrayList, arrayListTemp;
    private ProgressBar progressBar;
    private Methods methods;
    private Boolean isOver = false, isScroll = false;
    private TextView textView_empty;
    private int page = 1;
    private GridLayoutManager grid;
    private String cid;
    private String wallType;
    private FloatingActionButton fab;
    private Button button_colors_go;

    private AdapterColors adapterColors;
    private String color_ids = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wall_by_cat, container, false);

        SharedPref sharedPref = new SharedPref(getActivity());
        wallType = sharedPref.getWallType();

        grid = new GridLayoutManager(getActivity(), 3);
        if (wallType.equals(getString(R.string.landscape))) {
            grid.setSpanCount(2);
        } else {
            grid.setSpanCount(3);
        }

        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int real_pos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(getActivity(), WallPaperDetailsActivity.class);
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

        dbHelper = new DBHelper(getActivity());
        methods = new Methods(getActivity(), interAdListener);

        dbHelper.getAbout();

        cid = getArguments().getString("cid");

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        RelativeLayout rl_colors = rootView.findViewById(R.id.layout_colors);

        if (Constant.isColorOn && Constant.arrayListColors.size() > 0) {
            RecyclerView rv_colors = rootView.findViewById(R.id.rv_wall_colors);
            rv_colors.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);
            rv_colors.setNestedScrollingEnabled(false);

            adapterColors = new AdapterColors(getActivity(), Constant.arrayListColors);
            rv_colors.setAdapter(adapterColors);

            button_colors_go = rootView.findViewById(R.id.button_colors_go);
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
            rv_colors.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    adapterColors.setSelected(position);
                }
            }));
        } else {
            rl_colors.setVisibility(View.GONE);
        }

        progressBar = rootView.findViewById(R.id.pb_wallcat);
        textView_empty = rootView.findViewById(R.id.tv_empty_wallcat);

        fab = rootView.findViewById(R.id.fab);
        recyclerView = rootView.findViewById(R.id.rv_wall_by_cat);
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_item = s;
            Intent intent = new Intent(getActivity(), SearchWallActivity.class);
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
            LoadWallpaper loadWallpaper = new LoadWallpaper(new WallListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        dbHelper.removeWallByCat("catlist", cid);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWall, int totalNumber) {
                    if (getActivity() != null) {
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
                }
            }, methods.getAPIRequest(Constant.METHOD_WALLPAPER_BY_CAT, page, color_ids, wallType, cid, "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), ""));
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
            adapter = new AdapterWallpaper(getActivity(), wallType, arrayList, new RecyclerViewClickListener() {
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
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}