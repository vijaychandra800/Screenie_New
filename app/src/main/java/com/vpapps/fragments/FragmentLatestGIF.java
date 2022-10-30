package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vpapps.adapter.AdapterGIFs;
import com.vpapps.asyncTask.LoadGIF;
import com.vpapps.hdwallpaper.GIFsDetailsActivity;
import com.vpapps.hdwallpaper.R;
import com.vpapps.interfaces.GIFListener;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.items.ItemGIF;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.EndlessRecyclerViewScrollListener;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import okhttp3.RequestBody;

public class FragmentLatestGIF extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView recyclerView;
    private AdapterGIFs adapter;
    private ArrayList<ItemGIF> arrayList, arrayListTemp;
    private ProgressBar progressBar;
    private Boolean isScroll = false, isLoaded = false, isVisible = false;
    public static Boolean isOver = false;
    private TextView textView_empty;
    private int pos, page = 1;
    private GridLayoutManager grid;
    private FloatingActionButton fab;
    private RelativeLayout rl_colors;

    static FragmentLatestGIF newInstance(int position) {
        FragmentLatestGIF fragment = new FragmentLatestGIF();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        pos = getArguments().getInt("pos");

        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int realPos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(getActivity(), GIFsDetailsActivity.class);
                intent.putExtra("pos", realPos);
                Constant.arrayListGIF.clear();
                Constant.arrayListGIF.addAll(arrayListTemp);
                startActivity(intent);
            }
        };

        dbHelper = new DBHelper(getActivity());
        methods = new Methods(getActivity(), interAdListener);
        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        rl_colors = rootView.findViewById(R.id.layout_colors);
        rl_colors.setVisibility(View.GONE);

        progressBar = rootView.findViewById(R.id.pb_wall);
        textView_empty = rootView.findViewById(R.id.tv_empty_wall);

        fab = rootView.findViewById(R.id.fab);
        recyclerView = rootView.findViewById(R.id.rv_wall);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(getActivity(), 3);
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
                            getLatestData();
                        }
                    }, 500);
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

        if (isVisible && !isLoaded) {
            getLatestData();
            isLoaded = true;
        }
        return rootView;
    }

    private void getLatestData() {
        if (methods.isNetworkAvailable()) {
            RequestBody requestBody = null;
            if (pos == 0) {
                requestBody = methods.getAPIRequest(Constant.METHOD_LATEST_GIF, page, "", "", "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), "");
            } else if (pos == 1) {
                requestBody = methods.getAPIRequest(Constant.METHOD_MOST_VIEWED_GIF, page, "", "", "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), "");
            } else if (pos == 2) {
                requestBody = methods.getAPIRequest(Constant.METHOD_MOST_RATED_GIF, page, "", "", "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), "");
            }

            LoadGIF loadGIF = new LoadGIF(new GIFListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGIF> arrayListGIF, int totalNumber) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListGIF.size() == 0) {
                                    isOver = true;
                                    adapter.hideHeader();
                                    setEmpty();
                                } else {

                                    for (int i = 0; i < arrayListGIF.size(); i++) {
                                        arrayList.add(arrayListGIF.get(i));

                                        if (Constant.isNativeAd) {
                                            int abc = arrayList.lastIndexOf(null);
                                            if (((arrayList.size() - (abc + 1)) % Constant.adNativeShow == 0) && (arrayListGIF.size()-1 != i || arrayListTemp.size() != totalNumber)) {
                                                arrayList.add(null);
                                            }
                                        }
                                    }
                                    arrayListTemp.addAll(arrayListGIF);
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
            }, requestBody);
            loadGIF.execute();
        } else {
            arrayList = dbHelper.getGIFs();
            setAdapter();
            isOver = true;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterGIFs(getActivity(), arrayList, new RecyclerViewClickListener() {
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
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser && isAdded() && !isLoaded) {
            getLatestData();
            isLoaded = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}
