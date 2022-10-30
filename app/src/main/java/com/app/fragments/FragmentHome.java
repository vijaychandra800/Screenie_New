package com.app.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;
import com.app.adapter.AdapterCategoriesHome;
import com.app.adapter.AdapterImageHome;
import com.app.asyncTask.LoadFav;
import com.app.asyncTask.LoadHome;
import com.app.screenie.MainActivity;
import com.app.screenie.R;
import com.app.screenie.SearchWallActivity;
import com.app.screenie.WallPaperDetailsActivity;
import com.app.interfaces.HomeListener;
import com.app.interfaces.InterAdListener;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.SuccessListener;
import com.app.items.ItemCat;
import com.app.items.ItemColors;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.Methods;
import com.app.utils.RecyclerItemClickListener;
import com.app.utils.SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;

public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView rv_recent, rv_latest, rv_popular, rv_cat;
    private AdapterCategoriesHome adapterCategories;
    private View view_recent, view_latest, view_popular;
    private AdapterImageHome adapter_recent, adapter_latest, adapter_popular;
    private ArrayList<ItemWallpaper> arrayList_featured, arrayList_recent, arrayList_latest, arrayList_popular;
    private ArrayList<ItemCat> arrayList_cat;
    private LinearLayout ll_recent, ll_latest, ll_popular, ll_cat;
    private Button button_recent, button_latest, button_popular, button_cat;
    private LinearLayout linearLayout;
    private CircularProgressBar progressBar;
    private SharedPref sharedPref;

    private EnchantedViewPager viewPager;
    private ImagePagerAdapter pagerAdapter;
    private FloatingActionButton fab_wallType;
    private Boolean isSingleWallType = false;
    private ShowcaseView showcaseView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DBHelper(getActivity());
        dbHelper.getAbout();
        sharedPref = new SharedPref(getActivity());
        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                hideShowCaseView();

                switch (type) {
                    case "featured":
                        Intent intent_feat = new Intent(getActivity(), WallPaperDetailsActivity.class);
                        Constant.arrayList.clear();
                        Constant.arrayList.addAll(arrayList_featured);
                        intent_feat.putExtra("pos", position);
                        intent_feat.putExtra("page", 0);
                        startActivity(intent_feat);
                        break;
                    case "recent":
                        Intent intent_lat = new Intent(getActivity(), WallPaperDetailsActivity.class);
                        Constant.arrayList.clear();
                        Constant.arrayList.addAll(arrayList_recent);
                        intent_lat.putExtra("pos", position);
                        intent_lat.putExtra("page", 0);
                        startActivity(intent_lat);
                        break;
                    case "latest":
                        Intent intent_view = new Intent(getActivity(), WallPaperDetailsActivity.class);
                        Constant.arrayList.clear();
                        Constant.arrayList.addAll(arrayList_latest);
                        intent_view.putExtra("pos", position);
                        intent_view.putExtra("page", 0);
                        startActivity(intent_view);
                        break;
                    case "popular":
                        Intent intent = new Intent(getActivity(), WallPaperDetailsActivity.class);
                        Constant.arrayList.clear();
                        Constant.arrayList.addAll(arrayList_popular);
                        intent.putExtra("pos", position);
                        intent.putExtra("page", 0);
                        startActivity(intent);
                        break;
                    case "cat":
                        FragmentWallByCat frag = new FragmentWallByCat();
                        Bundle bundle = new Bundle();
                        bundle.putString("cid", arrayList_cat.get(position).getId());
                        bundle.putString("cname", arrayList_cat.get(position).getName());
                        bundle.putString("from", "");
                        frag.setArguments(bundle);
                        FragmentTransaction ft = FragmentHome.this.getParentFragment().getFragmentManager().beginTransaction();
//                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.hide(FragmentHome.this.getParentFragment());
                        ft.add(R.id.frame_layout, frag, arrayList_cat.get(position).getName());
                        ft.addToBackStack(arrayList_cat.get(position).getName());
                        ft.commit();
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(arrayList_cat.get(position).getName());
                        break;
                }
            }
        });
        arrayList_featured = new ArrayList<>();
        arrayList_recent = new ArrayList<>();
        arrayList_latest = new ArrayList<>();
        arrayList_popular = new ArrayList<>();
        arrayList_cat = new ArrayList<>();

        fab_wallType = rootView.findViewById(R.id.fab_wallType);

        String wallTempType = "";

        if (Constant.isPortrait && Constant.isLandscape && Constant.isSquare) {
            isSingleWallType = false;
            wallTempType = sharedPref.getWallType();
        } else if ((Constant.isPortrait && Constant.isLandscape) || (Constant.isPortrait && Constant.isSquare)) {
            isSingleWallType = false;
            wallTempType = sharedPref.getWallType();
        } else if (Constant.isSquare && Constant.isLandscape) {
            isSingleWallType = false;
            if (sharedPref.getWallType().equals(Constant.TAG_PORTRAIT)) {
                wallTempType = Constant.TAG_LANDSCAPE;
            } else {
                wallTempType = sharedPref.getWallType();
            }
        } else {
            isSingleWallType = true;
            if (Constant.isPortrait) {
                wallTempType = Constant.TAG_PORTRAIT;
            } else if (Constant.isLandscape) {
                wallTempType = Constant.TAG_LANDSCAPE;
            } else if (Constant.isSquare) {
                wallTempType = Constant.TAG_SQUARE;
            }

            fab_wallType.setVisibility(View.GONE);
        }


        sharedPref.setWallType(wallTempType);
        setWallTypeFab();

        fab_wallType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    FragmentWallpapers frag1 = (FragmentWallpapers) FragmentDashboard.mViewPager
                            .getAdapter()
                            .instantiateItem(FragmentDashboard.mViewPager, 1);
                    frag1.isWallTypeChanged = true;

                    FragmentWallpapers frag2 = (FragmentWallpapers) FragmentDashboard.mViewPager
                            .getAdapter()
                            .instantiateItem(FragmentDashboard.mViewPager, 3);
                    frag2.isWallTypeChanged = true;

                    FragmentWallpapers frag3 = (FragmentWallpapers) FragmentDashboard.mViewPager
                            .getAdapter()
                            .instantiateItem(FragmentDashboard.mViewPager, 4);
                    frag3.isWallTypeChanged = true;

                    FragmentCategories frag_cat = (FragmentCategories) FragmentDashboard.mViewPager
                            .getAdapter()
                            .instantiateItem(FragmentDashboard.mViewPager, 2);
                    frag_cat.isWallTypeChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hideShowCaseView();

                switch (sharedPref.getWallType()) {
                    case Constant.TAG_PORTRAIT:
                        if (Constant.isLandscape) {
                            sharedPref.setWallType(Constant.TAG_LANDSCAPE);
                        } else if (Constant.isSquare) {
                            sharedPref.setWallType(Constant.TAG_SQUARE);
                        }
                        break;
                    case Constant.TAG_LANDSCAPE:
                        if (Constant.isSquare) {
                            sharedPref.setWallType(Constant.TAG_SQUARE);
                        } else if (Constant.isPortrait) {
                            sharedPref.setWallType(Constant.TAG_PORTRAIT);
                        }
                        break;
                    case Constant.TAG_SQUARE:
                        if (Constant.isPortrait) {
                            sharedPref.setWallType(Constant.TAG_PORTRAIT);
                        } else if (Constant.isLandscape) {
                            sharedPref.setWallType(Constant.TAG_LANDSCAPE);
                        }
                        break;
                }
                setWallTypeFab();
                changeSliderHeight();
                getWallpapers();
            }
        });

        ll_recent = rootView.findViewById(R.id.ll_recent);
        ll_latest = rootView.findViewById(R.id.ll_latest);
        ll_popular = rootView.findViewById(R.id.ll_popular);
        ll_cat = rootView.findViewById(R.id.ll_cat);

        button_cat = rootView.findViewById(R.id.button_cat_all);
        button_recent = rootView.findViewById(R.id.button_recent_all);
        button_latest = rootView.findViewById(R.id.button_latest_all);
        button_popular = rootView.findViewById(R.id.button_popular_all);

        view_recent = rootView.findViewById(R.id.view_recent);
        view_latest = rootView.findViewById(R.id.view_latest);
        view_popular = rootView.findViewById(R.id.view_popular);

        LinearLayout ll_adView1 = rootView.findViewById(R.id.ll_adView1);

        methods.showBannerAd(ll_adView1);

        rv_recent = rootView.findViewById(R.id.rv_home_recent);
        rv_recent.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recent.setLayoutManager(llm);

        rv_latest = rootView.findViewById(R.id.rv_home_latest);
        rv_latest.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_latest.setLayoutManager(llm2);

        rv_popular = rootView.findViewById(R.id.rv_home_popular);
        rv_popular.setHasFixedSize(true);
        LinearLayoutManager llm3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_popular.setLayoutManager(llm3);

        rv_cat = rootView.findViewById(R.id.rv_home_cat);
        rv_cat.setHasFixedSize(true);
        LinearLayoutManager llm_cat = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_cat.setLayoutManager(llm_cat);

        linearLayout = rootView.findViewById(R.id.ll_main_home);
        progressBar = rootView.findViewById(R.id.pb_home);

        Constant.isFav = false;

        button_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentRecentWall fragment = new FragmentRecentWall();

                FragmentTransaction ft = FragmentHome.this.getParentFragment().getFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(FragmentHome.this.getParentFragment());
                ft.add(R.id.frame_layout, fragment, getString(R.string.recent_wallpaper));
                ft.addToBackStack(getString(R.string.recent_wallpaper));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.recent_wallpaper));
            }
        });

        button_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (FragmentDashboard.mViewPager != null) {
                        FragmentDashboard.mViewPager.setCurrentItem(1);
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.latest));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (FragmentDashboard.mViewPager != null) {
                        FragmentDashboard.mViewPager.setCurrentItem(3);
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popular));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (FragmentDashboard.mViewPager != null) {
                        FragmentDashboard.mViewPager.setCurrentItem(2);
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.categories));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rv_cat.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, "cat");
            }
        }));

        viewPager = rootView.findViewById(R.id.infinitViewPager);
        viewPager.useAlpha();
        viewPager.useScale();
        changeSliderHeight();

        getWallpapers();

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

    private void changeSliderHeight() {
        int height, height_view;
        LinearLayout.LayoutParams params;

        Resources r = getResources();
        if (sharedPref.getWallType().equals(getString(R.string.portrait))) {
            height = (int) (methods.getScreenHeight() * 0.55);
            height_view = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, r.getDisplayMetrics());
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else if (sharedPref.getWallType().equals(getString(R.string.landscape))) {
            height = (int) (methods.getScreenHeight() * 0.30);
            height_view = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics());
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else if (sharedPref.getWallType().equals(getString(R.string.square))) {
            height = (int) (methods.getScreenWidth());
            height_view = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height - 200);
        } else {
            height = (int) (methods.getScreenHeight() * 0.55);
            height_view = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, r.getDisplayMetrics());
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }

        params.setMargins(0, 50, 0, 50);
        viewPager.setLayoutParams(params);

        RelativeLayout.LayoutParams params_view = new RelativeLayout.LayoutParams(90, height_view);
        params_view.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params_view.addRule(RelativeLayout.ALIGN_PARENT_END);
        view_popular.setLayoutParams(params_view);
        view_latest.setLayoutParams(params_view);
        view_recent.setLayoutParams(params_view);
    }

    private void getWallpapers() {
        if (methods.isNetworkAvailable()) {
            LoadHome loadHome = new LoadHome(getActivity(), new HomeListener() {
                @Override
                public void onStart() {
                    Constant.arrayListColors.clear();
                    arrayList_featured.clear();
                    arrayList_recent.clear();
                    arrayList_latest.clear();
                    arrayList_popular.clear();
                    arrayList_cat.clear();

                    if (pagerAdapter != null) {
                        pagerAdapter.notifyDataSetChanged();
                    }

                    linearLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemWallpaper> arrayListFeatured, ArrayList<ItemWallpaper> arrayListLatest, ArrayList<ItemWallpaper> arrayListPopular, ArrayList<ItemWallpaper> arrayListRecent, ArrayList<ItemCat> arrayListCat, ArrayList<ItemColors> arrayListColors) {
                    if (getActivity() != null) {
                        arrayList_featured.addAll(arrayListFeatured);
                        arrayList_latest.addAll(arrayListLatest);
                        arrayList_popular.addAll(arrayListPopular);
                        arrayList_recent.addAll(arrayListRecent);
                        arrayList_cat.addAll(arrayListCat);

                        Constant.arrayListColors.addAll(arrayListColors);
                        setAdapterToListview();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_HOME, 0, dbHelper.getRecentWallpapersID(sharedPref.getWallType(), ""), sharedPref.getWallType(), "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), ""));
            loadHome.execute();
        } else {
            arrayList_recent = dbHelper.getRecentWallpapers(sharedPref.getWallType(), "", "10");
            arrayList_featured = dbHelper.getWallpapers(DBHelper.TABLE_WALL_BY_FEATURED, "", sharedPref.getWallType(), "");
            arrayList_latest = dbHelper.getWallpapers(DBHelper.TABLE_WALL_BY_LATEST, "", sharedPref.getWallType(), "");
            arrayList_popular = dbHelper.getWallpapers(DBHelper.TABLE_WALL_BY_LATEST, "views", sharedPref.getWallType(), "");
            arrayList_cat = dbHelper.getCat();
            setAdapterToListview();
        }
    }

    private void setAdapterToListview() {
        adapter_recent = new AdapterImageHome(getActivity(), sharedPref.getWallType(), arrayList_recent, new RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "recent");
            }
        });
        AnimationAdapter adapterAnim_portrait = new SlideInRightAnimationAdapter(adapter_recent);
        adapterAnim_portrait.setFirstOnly(true);
        adapterAnim_portrait.setDuration(500);
        adapterAnim_portrait.setInterpolator(new OvershootInterpolator(.9f));
        rv_recent.setAdapter(adapterAnim_portrait);

        adapter_latest = new AdapterImageHome(getActivity(), sharedPref.getWallType(), arrayList_latest, new RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "latest");
            }
        });
        AnimationAdapter adapterAnim_land = new SlideInRightAnimationAdapter(adapter_latest);
        adapterAnim_land.setFirstOnly(true);
        adapterAnim_land.setDuration(500);
        adapterAnim_land.setInterpolator(new OvershootInterpolator(.9f));
        rv_latest.setAdapter(adapterAnim_land);

        adapter_popular = new AdapterImageHome(getActivity(), sharedPref.getWallType(), arrayList_popular, new RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "popular");
            }
        });
        AnimationAdapter adapterAnim_square = new SlideInRightAnimationAdapter(adapter_popular);
        adapterAnim_square.setFirstOnly(true);
        adapterAnim_square.setDuration(500);
        adapterAnim_square.setInterpolator(new OvershootInterpolator(.9f));
        rv_popular.setAdapter(adapterAnim_square);

        adapterCategories = new AdapterCategoriesHome(getActivity(), arrayList_cat);
        AnimationAdapter adapterAnim4 = new SlideInRightAnimationAdapter(adapterCategories);
        adapterAnim4.setFirstOnly(true);
        adapterAnim4.setDuration(500);
        adapterAnim4.setInterpolator(new OvershootInterpolator(.9f));
        rv_cat.setAdapter(adapterAnim4);

        if (pagerAdapter == null) {
            pagerAdapter = new ImagePagerAdapter();
            pagerAdapter.enableCarrousel();
            viewPager.setAdapter(pagerAdapter);
        } else {
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(0);

        }

        setExmptTextView();

        if (!isSingleWallType) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String message = "";
                    switch (sharedPref.getWallType()) {
                        case Constant.TAG_PORTRAIT:
                            if (Constant.isLandscape) {
                                message = getString(R.string.click_chage_landscape);
                            } else if (Constant.isSquare) {
                                message = getString(R.string.click_chage_square);
                            }

                            showcaseView = new ShowcaseView.Builder(getActivity())
                                    .withMaterialShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .setTarget(new ViewTarget(fab_wallType))
                                    .setContentTitle(getString(R.string.toggle_wall_type))
                                    .setContentText(message)
                                    .hideOnTouchOutside()
//                                    .replaceEndButton(button)
                                    .singleShot(111)
                                    .build();
                            showcaseView.hideButton();
                            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                            break;
                        case Constant.TAG_LANDSCAPE:
                            if (Constant.isSquare) {
                                message = getString(R.string.click_chage_square);
                            } else if (Constant.isPortrait) {
                                message = getString(R.string.click_chage_portrait);
                            }

                            showcaseView = new ShowcaseView.Builder(getActivity())
                                    .withMaterialShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .setTarget(new ViewTarget(fab_wallType))
                                    .setContentTitle(getString(R.string.toggle_wall_type))
                                    .setContentText(message)
                                    .hideOnTouchOutside()
//                                    .replaceEndButton(button)
                                    .singleShot(222)
                                    .build();
                            showcaseView.hideButton();
                            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                            break;
                        case Constant.TAG_SQUARE:
                            if (Constant.isPortrait) {
                                message = getString(R.string.click_chage_portrait);
                            } else if (Constant.isLandscape) {
                                message = getString(R.string.click_chage_landscape);
                            }

                            showcaseView = new ShowcaseView.Builder(getActivity())
                                    .withMaterialShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .setTarget(new ViewTarget(fab_wallType))
                                    .setContentTitle(getString(R.string.toggle_wall_type))
                                    .setContentText(message)
                                    .hideOnTouchOutside()
//                                    .replaceEndButton(button)
                                    .singleShot(333)
                                    .build();
                            showcaseView.hideButton();
                            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                            break;
                    }

                }
            }, 2000);
        }
    }

    private void setWallTypeFab() {
        switch (sharedPref.getWallType()) {
            case Constant.TAG_PORTRAIT:
                fab_wallType.setImageDrawable(getResources().getDrawable(R.drawable.portrait));
                break;
            case Constant.TAG_LANDSCAPE:
                fab_wallType.setImageDrawable(getResources().getDrawable(R.drawable.landscape));
                break;
            case Constant.TAG_SQUARE:
                fab_wallType.setImageDrawable(getResources().getDrawable(R.drawable.square));
                break;
        }
    }

    private void setExmptTextView() {
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        if (arrayList_recent.size() < 2) {
            ll_recent.setVisibility(View.GONE);
        } else {
            ll_recent.setVisibility(View.VISIBLE);
        }

        if (arrayList_latest.size() == 0) {
            ll_latest.setVisibility(View.GONE);
        } else {
            ll_latest.setVisibility(View.VISIBLE);
        }

        if (arrayList_popular.size() == 0) {
            ll_popular.setVisibility(View.GONE);
        } else {
            ll_popular.setVisibility(View.VISIBLE);
        }

        if (arrayList_cat.size() == 0) {
            ll_cat.setVisibility(View.GONE);
        } else {
            ll_cat.setVisibility(View.VISIBLE);
        }

        if (arrayList_featured.size() == 0) {
            viewPager.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    private class ImagePagerAdapter extends EnchantedViewPagerAdapter {

        private LayoutInflater inflater;

        private ImagePagerAdapter() {
            super(arrayList_featured);
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return arrayList_featured.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.layout_viewpager_home, container, false);
            RoundedImageView imageView = imageLayout.findViewById(R.id.iv_vp_home);
            TextView textViewCat = imageLayout.findViewById(R.id.tv_pager_cat);
            final RelativeLayout rl = imageLayout.findViewById(R.id.rl);

            LikeButton likeButton = imageLayout.findViewById(R.id.button_pager_fav);
            likeButton.setLiked(arrayList_featured.get(position).getIsFav());

            if (sharedPref.isLogged()) {
                likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        try {
                            loadFav(position, rl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        try {
                            loadFav(position, rl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!sharedPref.isLogged()) {
                            methods.clickLogin();
                        }
                    }
                });
            }

            textViewCat.setText(arrayList_featured.get(position).getCName());
            Picasso.get()
                    .load(methods.getImageThumbSize(arrayList_featured.get(position).getImageThumb(), getString(R.string.home).concat(sharedPref.getWallType())))
                    .placeholder(R.drawable.placeholder_wall)
                    .into(imageView);

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInter(viewPager.getCurrentItem(), "featured");
                }
            });

            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
            container.addView(imageLayout);
            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void loadFav(final int posi, RelativeLayout rl) {
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
                                arrayList_featured.get(posi).setIsFav(true);
                            } else {
                                arrayList_featured.get(posi).setIsFav(false);
                            }
                            methods.showSnackBar(rl, message);
                        }
                    }
                }, methods.getAPIRequest(Constant.METHOD_DO_FAV, 0, arrayList_featured.get(posi).getId(), "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "wallpaper"));
                loadFav.execute();
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.clickLogin();
        }
    }

    private void hideShowCaseView() {
        try {
            if (showcaseView != null && showcaseView.isShowing()) {
                showcaseView.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}