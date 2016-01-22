package com.jozzee.mysurvey.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 23/11/2558.
 */
public class PagerAdapterAnalyze extends FragmentStatePagerAdapter {
    private static String TAG = PagerAdapterAnalyze.class.getSimpleName();

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public PagerAdapterAnalyze(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    /*public CharSequence getPageTitle2(int position) {
        return mFragmentTitleList.get(position);
        //return null;
    }*/
}
