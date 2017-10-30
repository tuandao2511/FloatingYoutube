package com.example.administrator.youtube.youtubeplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.example.administrator.youtube.constant.Constant;

public class Utils {
    public static final String keyPref = "autoPlayPref";
    public static final String keyQuality = "qualityPref";
    public static final String keyClockScreen = "clockPref";
    public static String formatTime(float sec) {
        int minutes = (int) (sec / 60);
        int seconds = (int) (sec % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    public static void setAutoPlayofClockSreenPref(Context context,String keyPref,boolean autoPlay){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(keyPref, autoPlay);
        editor.commit();
    }
    public static void setQualityPref(Context context,String keyPref,String quality){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyPref, quality);
        editor.commit();
    }



    public static boolean getAutoPlayOrClockScreenPref( Context context,String keyPref) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(keyPref, false);
    }
    public static String getQualityPref( Context context,String keyPref) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(keyPref, Constant.AUTO);
    }
}
