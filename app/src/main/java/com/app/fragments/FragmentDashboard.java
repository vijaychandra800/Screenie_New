package com.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.app.screenie.MainActivity;
import com.app.screenie.R;
import com.app.utils.Methods;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class FragmentDashboard extends Fragment {

    private Methods methods;
    public static ViewPager mViewPager;
    public static BottomNavigationView bottomNavigationMenu;
    private FragmentManager fm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        methods = new Methods(getActivity());
        fm = getFragmentManager();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = rootView.findViewById(R.id.vp_bottom);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationMenu.setSelectedItemId(R.id.nav_bottom_home);
                        break;
                    case 1:
                        bottomNavigationMenu.setSelectedItemId(R.id.nav_bottom_latest);
                        break;
                    case 2:
                        bottomNavigationMenu.setSelectedItemId(R.id.nav_bottom_cat);
                        break;
                    case 3:
                        bottomNavigationMenu.setSelectedItemId(R.id.nav_bottom_popular);
                        break;
                    case 4:
                        bottomNavigationMenu.setSelectedItemId(R.id.nav_bottom_rated);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationMenu = rootView.findViewById(R.id.navigation_bottom);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        FragmentHome f1 = new FragmentHome();
//        loadFrag(f1, getString(R.string.home));

        setHasOptionsMenu(true);
        return rootView;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
                    return new FragmentHome();
                case 1:
//                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.latest));
                    return FragmentWallpapers.newInstance(0);
                case 2:
//                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.categories));
                    return new FragmentCategories();
                case 3:
//                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popular));
                    return FragmentWallpapers.newInstance(1);
                case 4:
//                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.rated));
                    return FragmentWallpapers.newInstance(2);
                default:
                    return new FragmentHome();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_bottom_home:
//                    FragmentHome f1 = new FragmentHome();
//                    loadFrag(f1, getString(R.string.home));
                    mViewPager.setCurrentItem(0);
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
                    return true;
                case R.id.nav_bottom_latest:
//                    FragmentLatest2 flatest = new FragmentLatest2();
//                    Bundle args = new Bundle();
//                    args.putInt("pos", 0);
//                    flatest.setArguments(args);
//                    loadFrag(flatest, getString(R.string.latest));
                    mViewPager.setCurrentItem(1);
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.latest));
                    return true;
                case R.id.nav_bottom_popular:
//                    FragmentRecent fpop = new FragmentRecent();
//                    Bundle argsPop = new Bundle();
//                    argsPop.putInt("pos", 1);
//                    fpop.setArguments(argsPop);
//                    loadFrag(fpop, getString(R.string.popular));
                    mViewPager.setCurrentItem(3);
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popular));
                    return true;
                case R.id.nav_bottom_rated:
//                    FragmentRecent frate = new FragmentRecent();
//                    Bundle argsrate = new Bundle();
//                    argsrate.putInt("pos", 2);
//                    frate.setArguments(argsrate);
//                    loadFrag(frate, getString(R.string.rated));
                    mViewPager.setCurrentItem(4);
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.rated));
                    return true;
                case R.id.nav_bottom_cat:
//                    FragmentCategories fcat = new FragmentCategories();
//                    loadFrag(fcat, getString(R.string.categories));
                    mViewPager.setCurrentItem(2);
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.categories));
                    return true;
            }
            return false;
        }
    };

//    public void loadFrag(Fragment f1, String name) {
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        if (name.equals(getString(R.string.search))) {
//            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
//            ft.add(R.id.fragment_dash, f1, name);
//            ft.addToBackStack(name);
//        } else {
//            ft.replace(R.id.fragment_dash, f1, name);
//        }
//        ft.commitAllowingStateLoss();
//
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle(name);
//    }
}
