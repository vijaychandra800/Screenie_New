package com.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.screenie.R;
import com.app.screenie.SearchGIFActivity;
import com.app.utils.Constant;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class FragmentGIFs extends Fragment {

    private ViewPager mViewPager;
    private TextView tv_latest, tv_popular, tv_rated;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gif, container, false);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        tv_latest = rootView.findViewById(R.id.tv_gif_latest);
        tv_popular = rootView.findViewById(R.id.tv_gif_popular);
        tv_rated = rootView.findViewById(R.id.tv_gif_rated);

        mViewPager = rootView.findViewById(R.id.container_gifs);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        setTabsBG(0);

        tv_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabsBG(0);
                mViewPager.setCurrentItem(0);
            }
        });

        tv_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabsBG(1);
                mViewPager.setCurrentItem(1);
            }
        });

        tv_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabsBG(2);
                mViewPager.setCurrentItem(2);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabsBG(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
            Intent intent = new Intent(getActivity(), SearchGIFActivity.class);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentLatestGIF.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void setTabsBG(int tabs) {
        if (tabs == 0) {
            tv_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            tv_popular.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tv_rated.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

            tv_latest.setBackgroundResource(R.drawable.bg_tab_gifs_selected);
            tv_popular.setBackgroundResource(R.drawable.bg_tab_gifs);
            tv_rated.setBackgroundResource(R.drawable.bg_tab_gifs);
        } else if (tabs == 1) {
            tv_popular.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            tv_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tv_rated.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

            tv_popular.setBackgroundResource(R.drawable.bg_tab_gifs_selected);
            tv_latest.setBackgroundResource(R.drawable.bg_tab_gifs);
            tv_rated.setBackgroundResource(R.drawable.bg_tab_gifs);
        } else {
            tv_rated.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            tv_popular.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tv_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

            tv_rated.setBackgroundResource(R.drawable.bg_tab_gifs_selected);
            tv_popular.setBackgroundResource(R.drawable.bg_tab_gifs);
            tv_latest.setBackgroundResource(R.drawable.bg_tab_gifs);
        }
    }
}