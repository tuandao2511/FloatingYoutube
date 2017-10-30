package com.example.administrator.youtube.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 10/10/2017.
 */

public class DBStoreInMyFavouriteChanged extends BroadcastReceiver {
    public static String ACTION_DATABASE_MY_FAVOURITE_CHANGED = "com.youtube.DATABASE_MY_FAVOURITE_CHANGED";
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Intent detected.",Toast.LENGTH_SHORT).show();
    }
}
