package com.mobile.qg.qgtaxi.chart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;


/**
 * Created by 93922 on 2018/8/17.
 * 描述：
 */

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments ;
    private List<String>   mTitles ;
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }


}
