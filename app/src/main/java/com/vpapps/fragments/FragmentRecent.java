package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vpapps.adapter.AdapterColors;
import com.vpapps.adapter.AdapterWallpaperSearch;
import com.vpapps.hdwallpaper.R;
import com.vpapps.hdwallpaper.SearchWallActivity;
import com.vpapps.hdwallpaper.WallPaperDetailsActivity;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.items.ItemWallpaper;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
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
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FragmentRecent extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView recyclerView, rv_colors;
    private AdapterWallpaperSearch adapter;
    private ArrayList<ItemWallpaper> arrayList, arrayListTemp;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    private GridLayoutManager grid;
    private String wallType;
    private FloatingActionButton fab;
    private AdapterColors adapterColors;
    private Button button_colors_go;
    private String color_ids = "";
    private SharedPref sharedPref;
    private RelativeLayout rl_colors;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        sharedPref = new SharedPref(getActivity());
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
                intent.putExtra("pos_type", 0);
                intent.putExtra("page", 0);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);
                Constant.arrayList.clear();
                Constant.arrayList.addAll(arrayListTemp);
                startActivity(intent);
            }
        };

        dbHelper = new DBHelper(getActivity());
        methods = new Methods(getActivity(), interAdListener);
        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        fab = rootView.findViewById(R.id.fab);
        progressBar = rootView.findViewById(R.id.pb_wall);
        textView_empty = rootView.findViewById(R.id.tv_empty_wall);

        rl_colors = rootView.findViewById(R.id.layout_colors);

        if (Constant.isColorOn && Constant.arrayListColors.size() > 0) {
            button_colors_go = rootView.findViewById(R.id.button_colors_go);

            rv_colors = rootView.findViewById(R.id.rv_wall_colors);
            rv_colors.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);
            rv_colors.setNestedScrollingEnabled(false);

            adapterColors = new AdapterColors(getActivity(), Constant.arrayListColors);
            rv_colors.setAdapter(adapterColors);

            button_colors_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color_ids = adapterColors.getSelected();
                    arrayList.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    getRecentData();
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

        recyclerView = rootView.findViewById(R.id.rv_wall);
        recyclerView.setHasFixedSize(true);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) >= 1000 ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);
        recyclerView.setNestedScrollingEnabled(false);

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

        getRecentData();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
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

    private void getRecentData() {
        arrayListTemp.clear();
        arrayListTemp.addAll(dbHelper.getRecentWallpapers(sharedPref.getWallType(), color_ids, "30"));

        if (Constant.isNativeAd) {

            for (int i = 0; i < arrayListTemp.size(); i++) {

                arrayList.add(arrayListTemp.get(i));

                int abc = arrayList.lastIndexOf(null);
                if ((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0) {
                    arrayList.add(null);
                }
            }
        } else {
            arrayList.clear();
            arrayList.addAll(dbHelper.getRecentWallpapers(sharedPref.getWallType(), color_ids, "30"));
        }

        setAdapter();
        progressBar.setVisibility(View.GONE);
    }

    public void setAdapter() {
        adapter = new AdapterWallpaperSearch(getActivity(), wallType, arrayList, new RecyclerViewClickListener() {
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
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}