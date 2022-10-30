package com.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.app.adapter.AdapterColors;
import com.app.adapter.AdapterWallpaper;
import com.app.asyncTask.LoadWallpaper;
import com.app.screenie.R;
import com.app.screenie.SearchWallActivity;
import com.app.screenie.WallPaperDetailsActivity;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.WallListener;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.EndlessRecyclerViewScrollListener;
import com.app.utils.Methods;
import com.app.utils.RecyclerItemClickListener;
import com.app.utils.SharedPref;

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

public class FragmentRecentWall extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView recyclerView, rv_colors;
    private AdapterWallpaper adapter;
    private ArrayList<ItemWallpaper> arrayList, arrayListTemp;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    private GridLayoutManager grid;
    private int page = 1;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private String wallType;
    private FloatingActionButton fab;
    private AdapterColors adapterColors;
    private Button button_colors_go;
    private String color_ids = "";
    private SharedPref sharedPref;
    private RelativeLayout rl_colors;
    Boolean isWallTypeChanged = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        sharedPref = new SharedPref(getActivity());
        dbHelper = new DBHelper(getActivity());

        button_colors_go = rootView.findViewById(R.id.button_colors_go);
        rv_colors = rootView.findViewById(R.id.rv_wall_colors);

        grid = new GridLayoutManager(getActivity(), 3);

        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(getActivity(), WallPaperDetailsActivity.class);

                int real_pos = adapter.getRealPos(position, arrayListTemp);

                intent.putExtra("pos", real_pos);
                intent.putExtra("pos_type", 4);
                intent.putExtra("page", page);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);
                Constant.arrayList.clear();
                Constant.arrayList.addAll(arrayListTemp);
                startActivity(intent);
            }
        };

        methods = new Methods(getActivity(), interAdListener);
        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        fab = rootView.findViewById(R.id.fab);
        progressBar = rootView.findViewById(R.id.pb_wall);
        textView_empty = rootView.findViewById(R.id.tv_empty_wall);

        rl_colors = rootView.findViewById(R.id.layout_colors);

        recyclerView = rootView.findViewById(R.id.rv_wall);
        recyclerView.setHasFixedSize(true);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getLatestData();
                        }
                    }, 1000);
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


        setColorList();
        isLoading = true;
        isWallTypeChanged = false;
//        arrayList.clear();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
//        page = 1;
//        isScroll = false;
//        isOver = false;
        wallType = sharedPref.getWallType();
        if (wallType.equals(getString(R.string.landscape))) {
            grid.setSpanCount(2);
        } else {
            grid.setSpanCount(3);
        }

        getLatestData();


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

    private void setColorList() {
        if (Constant.isColorOn && Constant.arrayListColors.size() > 0) {
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

                    page = 1;
                    isScroll = false;
                    isOver = false;

                    getLatestData();
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
    }

    private void getLatestData() {
        if (methods.isNetworkAvailable()) {
            String ids = dbHelper.getRecentWallpapersID(wallType, color_ids);
            LoadWallpaper loadWallpaper = new LoadWallpaper(new WallListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
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
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arrayListTemp.addAll(arrayListWall);
                                            for (int i = 0; i < arrayListWall.size(); i++) {
                                                arrayList.add(arrayListWall.get(i));

                                                if (Constant.isNativeAd) {
                                                    int abc = arrayList.lastIndexOf(null);
                                                    if (((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0) && (arrayListWall.size() - 1 != i || arrayListTemp.size() != totalNumber)) {
                                                        arrayList.add(null);
                                                    }
                                                }
                                            }
                                            page = page + 1;
                                            setAdapter();
                                        }
                                    });
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_RECENT, page, ids, wallType, "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), "wallpaper"));
            loadWallpaper.execute();
        } else {
            setAdapter();
            isOver = true;
            progressBar.setVisibility(View.GONE);
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
            adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
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
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}