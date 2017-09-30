package com.example.administrator.youtube.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;


import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.fragment.HomeFragment;

/**
 * Created by linh on 9/26/2017.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

   private HomeFragment homeFragment;
   private BottomNavigationView bottom_control;
   private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected( MenuItem item) {
           FragmentManager fragmentManager = getFragmentManager();
           FragmentTransaction transaction = fragmentManager.beginTransaction();
           switch (item.getItemId()) {
               case R.id.ic_ho:
                   transaction.replace(R.id.frame_layout,homeFragment).commit();
                   return true;
               case R.id.action_schedules:

                   return true;
               case R.id.action_music:
                   return true;
           }
           return false;
       }
   };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        homeFragment = new HomeFragment();
        bottom_control = (BottomNavigationView) findViewById(R.id.bottom_control);
        bottom_control.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,homeFragment).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
        }
        return false;
    }
}
