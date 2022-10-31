package com.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.adapter.AdapterCategories;
import com.app.asyncTask.LoadCat;
import com.app.screenie.MainActivity;
import com.app.screenie.R;
import com.app.interfaces.CategoryListener;
import com.app.interfaces.InterAdListener;
import com.app.items.ItemCat;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FragmentCategories extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView recyclerView;
    private AdapterCategories adapterCategories;
    private ArrayList<ItemCat> arrayList;
    //private CircularProgressBar progressBar;
    private ProgressBar progressBar;
    private TextView textView_empty;
    private SearchView searchView;
    private SharedPref sharedPref;
    private Boolean isLoading = false;
    Boolean isWallTypeChanged = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int pos, String type) {
                int position = getPosition(adapterCategories.getID(pos));

                FragmentWallByCat frag = new FragmentWallByCat();
                Bundle bundle = new Bundle();
                bundle.putString("cid", arrayList.get(position).getId());
                bundle.putString("cname", arrayList.get(position).getName());
                bundle.putString("from", "");
                frag.setArguments(bundle);
                FragmentTransaction ft = FragmentCategories.this.getParentFragment().getFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(FragmentCategories.this.getParentFragment());
                ft.add(R.id.frame_layout, frag, arrayList.get(position).getName());
                ft.addToBackStack(arrayList.get(position).getName());
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(arrayList.get(position).getName());
            }
        };

        sharedPref = new SharedPref(getActivity());
        dbHelper = new DBHelper(getActivity());
        methods = new Methods(getActivity(), interAdListener);

        arrayList = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_cat);
        textView_empty = rootView.findViewById(R.id.tv_empty_cat);
        recyclerView = rootView.findViewById(R.id.rv_cat);
        GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, "");
            }
        }));

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (!searchView.isIconified() && adapterCategories != null) {
                adapterCategories.getFilter().filter(s);
                adapterCategories.notifyDataSetChanged();
            }
            return false;
        }
    };

    private void loadCat() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(getActivity(), new CategoryListener() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListCat);
                                setAdapter();
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
            }, methods.getAPIRequest(Constant.METHOD_CAT, 0, "", sharedPref.getWallType(), "", "", "", "", "", "", "", "", "", ""));
            loadCat.execute();
        } else {
            arrayList = dbHelper.getCat();
            if (arrayList != null) {
                setAdapter();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter() {
        adapterCategories = new AdapterCategories(getActivity(), arrayList);
        AnimationAdapter adapterAnim = new ScaleInAnimationAdapter(adapterCategories);
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

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isWallTypeChanged && !isLoading) {
            isLoading = true;
            isWallTypeChanged = false;
            arrayList.clear();
            if(adapterCategories != null) {
                adapterCategories.notifyDataSetChanged();
            }
            loadCat();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}