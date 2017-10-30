package com.example.administrator.youtube.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.administrator.youtube.R;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.youtubeplayer.Utils;

/**
 * Created by linh on 10/10/2017.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {
    private ImageView back;
    private View viewChooseQuality,customToolBar;
    private Switch autoPlay, clockScreen;
    private TextView quality, auto, _144p, _360p, _480p, _720p, _1080p;
    private RelativeLayout changeQuality,rootView;
    private LinearLayout choose_view,root_view;
    private Animation slide_down,slide_up;
    private  Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar);
        initIdView();
        initValue();
        initActionView();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.auto:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.AUTO);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.tv_144p:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality_144));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.QUALITY_144P);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.tv_360p:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality_360));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.QUALITY_360P);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.tv_480p:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality_480));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.QUALITY_480P);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.tv_720p:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality_720));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.QUALITY_720P);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.tv_1080p:
                disable(root_view,true);
                disable(toolbar,true);
                quality.setText(getResources().getString(R.string.quality_1080));
                rootView.setBackgroundResource(R.color.colorWhite);
                customToolBar.setEnabled(true);
                choose_view.setAnimation(slide_down);
                Utils.setQualityPref(SettingActivity.this, Utils.keyQuality, Constant.QUALITY_1080P);
                choose_view.setVisibility(View.GONE);
                break;
            case R.id.choose_quality:
                rootView.setBackgroundResource(R.color.gray);
                disable(root_view,false);
                disable(toolbar,false);
               choose_view.setBackgroundResource(R.color.colorWhite);
                choose_view.setAnimation(slide_up);
                choose_view.setVisibility(View.VISIBLE);
                break;

        }


    }



    private void initActionView() {
        autoPlay.setOnCheckedChangeListener(this);
        clockScreen.setOnCheckedChangeListener(this);
        changeQuality.setOnClickListener(this);
        _144p.setOnClickListener(this);
        _360p.setOnClickListener(this);
        _480p.setOnClickListener(this);
        _720p.setOnClickListener(this);
        _1080p.setOnClickListener(this);
        auto.setOnClickListener(this);
    }

    private void initIdView() {
        root_view = (LinearLayout)findViewById(R.id.rootView);
        changeQuality = (RelativeLayout)findViewById(R.id.choose_quality);
        rootView= (RelativeLayout) findViewById(R.id.root_view);
        choose_view = (LinearLayout)findViewById(R.id.choose_view);
        autoPlay = (Switch) findViewById(R.id.chekcAutoPlay);
        clockScreen = (Switch) findViewById(R.id.switch_clock);
        quality = (TextView) findViewById(R.id.quality);
        auto = (TextView) findViewById(R.id.auto);
        _144p = (TextView) findViewById(R.id.tv_144p);
        _360p = (TextView) findViewById(R.id.tv_360p);
        _480p = (TextView) findViewById(R.id.tv_480p);
        _720p = (TextView) findViewById(R.id.tv_720p);
        _1080p = (TextView)findViewById(R.id.tv_1080p);

    }
    private void initToolBar(Toolbar toolbar){
        Toolbar.LayoutParams lp1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
         customToolBar = LayoutInflater.from(this).inflate(R.layout.toolbar_settings_activity, null, false);
        back = (ImageView) customToolBar.findViewById(R.id.img_back);
        back.setOnClickListener(this);
        toolbar.addView(customToolBar, lp1);
        setSupportActionBar(toolbar);
    }

    private void initValue() {
        autoPlay.setChecked(Utils.getAutoPlayOrClockScreenPref(this, Utils.keyPref));
        clockScreen.setChecked(Utils.getAutoPlayOrClockScreenPref(this,Utils.keyClockScreen));
        slide_up = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
         slide_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        String qualityPref = Utils.getQualityPref(this,Utils.keyQuality);
        if(qualityPref.equals(Constant.AUTO))quality.setText(getResources().getString(R.string.quality));
        else if(qualityPref.equals(Constant.QUALITY_144P))quality.setText(getResources().getString(R.string.quality_144));
        else if(qualityPref.equals(Constant.QUALITY_360P))quality.setText(getResources().getString(R.string.quality_360));
        else if(qualityPref.equals(Constant.QUALITY_480P))quality.setText(getResources().getString(R.string.quality_480));
        else if(qualityPref.equals(Constant.QUALITY_720P))quality.setText(getResources().getString(R.string.quality_720));
        else if(qualityPref.equals(Constant.QUALITY_1080P))quality.setText(getResources().getString(R.string.quality_1080));
    }

    private  void disable(ViewGroup layout,boolean isAble) {
        layout.setEnabled(isAble);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child,isAble);
            } else {
                child.setEnabled(isAble);
            }
        }

}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chekcAutoPlay:
                Utils.setAutoPlayofClockSreenPref(SettingActivity.this,Utils.keyPref,isChecked);
                break;
            case R.id.switch_clock:
                Utils.setAutoPlayofClockSreenPref(SettingActivity.this,Utils.keyClockScreen,isChecked);
                break;
        }
    }
}
