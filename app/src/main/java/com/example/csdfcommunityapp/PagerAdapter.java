package com.example.csdfcommunityapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    /*
    The pager adapter Handles the number of pages added.
    This is very useful in extending the Tab View
     */

    private int numOfTabs;

    public PagerAdapter(FragmentManager fm , int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new SosFragment();
            case 1:
                return new UploadFragment();
            case 2:
                return new MapFragment();
            default:
                return null;
        }



    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
