package com.example.administrator.youtube.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.FloatMath;
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
import com.example.administrator.youtube.activity.FullPlayerScreenActivity;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.youtubeplayer.AbstractYouTubeListener;
import com.example.administrator.youtube.youtubeplayer.Utils;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayer;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerFullScreenListener;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerView;
import com.example.administrator.youtube.youtubeplayer.YoutubePlayerRepeat;

import java.util.ArrayList;

import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;


/**
 * Created by linh on 9/27/2017.
 */

public class PlayService extends Service {
    private static final String TAG = PlayService.class.getSimpleName();
    private YouTubePlayerView youTubePlayerView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams playerParams, param_close, param_close_back;
    private LinearLayout serviceCloseBackground, serviceClose;
    private final Point size = new Point();
    private boolean isInsideClose = false;
    private int curDuration;
    private Video curVideo;
    private int position = 0;
    private ArrayList<Video> videos = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (intent.getAction().equals(Constant.PLAY_VIDEO_FROM_FULLSCREEN_ACTIVITY)) {

                curDuration = bundle.getInt("time");
                videos = (ArrayList<Video>) bundle.getSerializable("videos");
                curVideo = (Video) bundle.getSerializable("curVideo");

            } else if (intent.getAction().equals(Constant.PLAY_VIDEO_FROM_MAIN_ACTIVITY_IN_SERVICE)) {
                position = 0;
                curDuration = 0;
                videos = (ArrayList<Video>) bundle.getSerializable("videos");
                curVideo = (Video) bundle.getSerializable("curVideo");
            } else if (intent.getAction().equals(Constant.PLAY_VIDEO_FROM_SEARCHACTIVITY)) {
                curDuration = bundle.getInt("time");
                videos = (ArrayList<Video>) bundle.getSerializable("videos");
                curVideo = (Video) bundle.getSerializable("curVideo");
            }

        }

        Log.e(TAG, "onstartComand" + videos.size() + curVideo.getTitle());
//        final String videoId = listId.get(position);

        if (!youTubePlayerView.isInitialized()) {
            youTubePlayerView.initialize(new AbstractYouTubeListener() {

                @Override
                public void onReady() {
                    String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(curVideo.getID(), curDuration, quality);
                  //  youTubePlayerView.loadVideo(curVideo.getID(), curDuration);
                }


                @Override
                public void onStateChange(@YouTubePlayer.State.YouTubePlayerState int state) {
                    super.onStateChange(state);
                    if (state == YouTubePlayer.State.ENDED)
                        if (Utils.getAutoPlayOrClockScreenPref(PlayService.this,Utils.keyPref)) {
                            if (position < videos.size()) {
                                String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
                                youTubePlayerView.loadVideoWithQuality(videos.get(position + 1).getID(), 0, quality);
                               // youTubePlayerView.loadVideo(videos.get(position + 1).getID(), 0);
                                position = position + 1;
                            }
                        }
                }
            }, true);

        } else {
            String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
            youTubePlayerView.loadVideoWithQuality(curVideo.getID(), curDuration, quality);
          //  youTubePlayerView.loadVideo(curVideo.getID(), curDuration);
//            if (videoId != null)
//                youTubePlayerView.loadVideo(videoId, 0);
        }
        youTubePlayerView.setOnRepeatPlayListenner(new YoutubePlayerRepeat() {
            @Override
            public void playRepeat() {
                String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
                youTubePlayerView.loadVideoWithQuality(curVideo.getID(), 0, quality);
                //youTubePlayerView.loadVideo(curVideo.getID(), 0);
            }
        });


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

        display.getSize(size);
        youTubePlayerView = new YouTubePlayerView(PlayService.this);
        setCustomActionNextPrevious();
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                Intent intent = new Intent(PlayService.this, FullPlayerScreenActivity.class);
                intent.setAction(Constant.PLAY_VIDEO_FROM_PLAYSERVICE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                if (curVideo != null) Log.e(TAG, "!null");
                bundle.putSerializable("curVideos", curVideo);
                bundle.putInt("curDuration", youTubePlayerView.getProgress());
                bundle.putSerializable("videos", videos);
                intent.putExtras(bundle);
                startActivity(intent);
                if (youTubePlayerView != null)
                    windowManager.removeView(youTubePlayerView);
                if (serviceCloseBackground != null && serviceClose.getWindowToken() != null)
                    windowManager.removeView(serviceClose);
                if (serviceClose != null && serviceClose.getWindowToken() != null)
                    windowManager.removeView(serviceClose);
                youTubePlayerView.release();
                if (videos != null) videos.clear();
                stopForeground(true);
                stopSelf();

//                playerParams = new WindowManager.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//                        ,
//                        WindowManager.LayoutParams.TYPE_PHONE,
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                        PixelFormat.TRANSLUCENT);
//                playerParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//                playerParams.screenOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                windowManager.updateViewLayout(youTubePlayerView, playerParams);
//                setCustomActionNextPrevious();

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {

//                playerParams = new WindowManager.LayoutParams(
//                        7 * size.x / 10, 7 * size.x / 20
//                        ,
//                        WindowManager.LayoutParams.TYPE_PHONE,
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                        PixelFormat.TRANSLUCENT);
//                playerParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                windowManager.updateViewLayout(youTubePlayerView, playerParams);
//                setCustomActionNextPrevious();
            }
        });

        playerParams = new WindowManager.LayoutParams(
                7 * size.x / 10, 7 * size.x / 20
                ,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        playerParams.gravity = Gravity.CENTER;
        windowManager.addView(serviceClose, param_close);
        windowManager.addView(serviceCloseBackground, param_close_back);
        windowManager.addView(youTubePlayerView, playerParams);


        serviceClose.setVisibility(View.GONE);
        serviceCloseBackground.setVisibility(View.GONE);
        setDragListeners();

    }

    private void setCustomActionNextPrevious() {
        youTubePlayerView.setCustomActionLeft(ContextCompat.getDrawable(PlayService.this, R.drawable.previous), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 0) {
                    String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(videos.get(position - 1).getID(), 0, quality);
                   // youTubePlayerView.loadVideo(videos.get(position - 1).getID(), 0);
                    videos.add(videos.size(), curVideo);
                    curVideo = videos.get(position - 1);
                    if (videos.size() >= position)
                        videos.remove(videos.get(position - 1));


                    position = position - 1;
                }
            }
        }, true);
        youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(PlayService.this, R.drawable.next), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < videos.size()) {
                    String quality = Utils.getQualityPref(PlayService.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(videos.get(position + 1).getID(), 0, quality);
                  //  youTubePlayerView.loadVideo(videos.get(position + 1).getID(), 0);
                    curVideo = videos.get(position + 1);
                    position = position + 1;
                }

            }
        });
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
            public void onTouchListenner(View view, MotionEvent motionEvent) {
                serviceClose.setVisibility(View.VISIBLE);
                serviceCloseBackground.setVisibility(View.VISIBLE);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = playerParams.x;
                        initialY = playerParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                    case MotionEvent.ACTION_UP:
                        serviceClose.setVisibility(View.GONE);
                        serviceCloseBackground.setVisibility(View.GONE);
                        if (isInsideClose) {
                            if (youTubePlayerView != null)
                                windowManager.removeView(youTubePlayerView);
                            if (serviceCloseBackground != null && serviceClose.getWindowToken() != null)
                                windowManager.removeView(serviceClose);
                            if (serviceClose != null && serviceClose.getWindowToken() != null)
                                windowManager.removeView(serviceClose);
                            youTubePlayerView.release();
                            stopForeground(true);
                            stopSelf();
                        } else {

                            windowManager.updateViewLayout(serviceClose, param_close);
                            windowManager.updateViewLayout(serviceCloseBackground, param_close_back);
                        }


                        // youTubePlayerView.hideOverlay();

                    case MotionEvent.ACTION_MOVE:
                        if (isViewOverlapping(youTubePlayerView, serviceCloseBackground)) {
                            isInsideClose = true;
                        } else isInsideClose = false;

//                        if (!playerView.isDragging) {
//                            return false;
//                        }
                        playerParams.x = initialX
                                + (int) (motionEvent.getRawX() - initialTouchX);
                        playerParams.y = initialY
                                + (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(youTubePlayerView, playerParams);


                }

            }
        });

    }

    private void initParams() {
        param_close_back = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        param_close_back.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        //Close Image Params
        param_close = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
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
        int yOfPlayerView = firstPosition[1] + firstView.getMeasuredHeight() / 2;
        if (yOfPlayerView >= secondPosition[1]) return true;
        else return false;
    }

}
