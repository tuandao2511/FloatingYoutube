package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.youtube.fragment.FavouriteFragment;
import com.example.administrator.youtube.fragment.HistoryFragment;
import com.example.administrator.youtube.fragment.LibraryFragment;

/**
 * Created by Administrator on 10/3/2017.
 */

public class LibraryAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private final CharSequence tabTitle [] = new  String[]{"History","My Favourite"};
    private Context context;

    public LibraryAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) return new HistoryFragment();
        else return new FavouriteFragment();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
