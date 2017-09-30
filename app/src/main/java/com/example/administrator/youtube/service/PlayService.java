package com.example.administrator.youtube.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.youtubeplayer.AbstractYouTubeListener;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerFullScreenListener;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerView;


/**
 * Created by linh on 9/27/2017.
 */

public class PlayService extends Service {
    private static final String TAG = PlayService.class.getSimpleName();
    private YouTubePlayerView youTubePlayerView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams playerParams,param_close,param_close_back;
    private LinearLayout serviceCloseBackground,serviceClose;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e(TAG, "onstartComand");
        final String videoId = intent.getStringExtra("videoId");

        if (!youTubePlayerView.isInitialized()) {
            youTubePlayerView.initialize(new AbstractYouTubeListener() {

                @Override
                public void onReady() {
                    youTubePlayerView.loadVideo(videoId, 0);
                }

            }, true);
        } else youTubePlayerView.loadVideo(videoId, 0);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        initParams();
        showPopub();


    }

    private void showPopub() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        serviceCloseBackground = (LinearLayout) inflater.inflate(R.layout.service_background_close, null, false);

        serviceClose = (LinearLayout) inflater.inflate(R.layout.service_close, null, false);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        youTubePlayerView = new YouTubePlayerView(PlayService.this);
        youTubePlayerView.setCustomActionLeft(ContextCompat.getDrawable(PlayService.this, R.drawable.previous), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youTubePlayerView.loadVideo("6JYIGclVQdw", 0);
            }
        });
        youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(PlayService.this, R.drawable.next), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youTubePlayerView.loadVideo("09R8_2nJtjg", 0);
            }
        });
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {

                playerParams = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        ,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT);
                playerParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                playerParams.screenOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                windowManager.updateViewLayout(youTubePlayerView, playerParams);
                youTubePlayerView.setCustomActionLeft(ContextCompat.getDrawable(PlayService.this, R.drawable.previous), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.loadVideo("6JYIGclVQdw", 0);
                    }
                });
                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(PlayService.this, R.drawable.next), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.loadVideo("09R8_2nJtjg", 0);
                    }
                });

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {

                playerParams = new WindowManager.LayoutParams(
                        7 * size.x / 10, 7 * size.x / 20
                        ,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT);
                playerParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                windowManager.updateViewLayout(youTubePlayerView, playerParams);
                youTubePlayerView.setCustomActionLeft(ContextCompat.getDrawable(PlayService.this, R.drawable.previous), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.loadVideo("6JYIGclVQdw", 0);
                    }
                });
                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(PlayService.this, R.drawable.next), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.loadVideo("09R8_2nJtjg", 0);
                    }
                });
            }
        });

        playerParams = new WindowManager.LayoutParams(
                7 * size.x / 10, 7 * size.x / 20
                ,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        playerParams.gravity = Gravity.CENTER;
        windowManager.addView(serviceClose, param_close);
        windowManager.addView(serviceCloseBackground, param_close_back);
        windowManager.addView(youTubePlayerView, playerParams);


        serviceClose.setVisibility(View.GONE);
        serviceCloseBackground.setVisibility(View.GONE);
        setDragListeners();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }

    private void setDragListeners() {
        Log.e(TAG, "onTouchs");
        youTubePlayerView.setOnTouchListeners(new YouTubePlayerView.onTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public void onTouchListenner(MotionEvent motionEvent) {
                serviceClose.setVisibility(View.VISIBLE);
                serviceCloseBackground.setVisibility(View.VISIBLE);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = playerParams.x;
                        initialY = playerParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                    case MotionEvent.ACTION_UP:
                        Log.e(TAG,"up");
                        serviceClose.setVisibility(View.GONE);
                        serviceCloseBackground.setVisibility(View.GONE);
                        windowManager.updateViewLayout(serviceClose,param_close);
                        windowManager.updateViewLayout(serviceCloseBackground,param_close_back);

                        // youTubePlayerView.hideOverlay();

                    case MotionEvent.ACTION_MOVE:
                        if(isViewOverlapping(youTubePlayerView,serviceClose)){
                            Log.e(TAG," true");
                        }else  Log.e(TAG," false");

//                        if (!playerView.isDragging) {
//                            return false;
//                        }
                        playerParams.x = initialX
                                + (int) (motionEvent.getRawX() - initialTouchX);
                        playerParams.y = initialY
                                + (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(youTubePlayerView, playerParams);
                        Log.e(TAG,"xy " + playerParams.x + "  " + playerParams.y + param_close_back.x + " " + param_close_back.y);

                }

            }
        });

    }
private void initParams(){
    param_close_back = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    param_close_back.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    //Close Image Params
    param_close = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    param_close.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
}
    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        // Rect constructor parameters: left, top, right, bottom
        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + firstView.getMeasuredWidth(), firstPosition[1] + firstView.getMeasuredHeight());
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + secondView.getMeasuredWidth(), secondPosition[1] + secondView.getMeasuredHeight());
        return rectFirstView.intersect(rectSecondView);
    }

}
