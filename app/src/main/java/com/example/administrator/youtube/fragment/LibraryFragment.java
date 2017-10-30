package com.example.administrator.youtube.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.widget.TableLayout;


import com.example.administrator.youtube.R;
import com.example.administrator.youtube.adapter.LibraryAdapter;

/**
 * Created by Administrator on 10/2/2017.
 */

public class LibraryFragment extends Fragment {
    private View view;
    ViewPager viewPager;
    TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) return view;
        view = inflater.inflate(R.layout.library_fragment, container, false);


         viewPager = (ViewPager) view.findViewById(R.id.viewpager);
         tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        LibraryAdapter adapter = new LibraryAdapter(getActivity().getSupportFragmentManager(), getContext());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


}
