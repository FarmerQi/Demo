package com.example.farmerqi.farm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by FarmerQi on 2018/5/20.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments){
        super(fragmentManager);
        this.fragments = fragments;

    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
