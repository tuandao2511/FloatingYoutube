package com.example.administrator.youtube.youtubeplayer;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.example.administrator.youtube.R;

import java.util.HashSet;
import java.util.Set;

public class YouTubePlayerView extends FrameLayout implements NetworkReceiver.NetworkListener,View.OnTouchListener {

    @NonNull
    private final NetworkReceiver networkReceiver;
    private  YoutubePlayerRepeat youtubePlayerRepeat;

    @NonNull
    private final YouTubePlayer youTubePlayer;

    @NonNull
    private final View playerControls;
    @NonNull
    private final PlayerControlsWrapper playerControlsWrapper;

    @NonNull
    private final PlaybackResumer playbackResumer;
    private  onTouchListener ontouchListener;

    private final Set<YouTubePlayerFullScreenListener> fullScreenListeners;

    private boolean isFullScreen;
    private boolean isInService;

    public YouTubePlayerView(Context context) {
        this(context, null);
    }

    public YouTubePlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YouTubePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        isFullScreen = false;

        youTubePlayer = new YouTubePlayer(context);
        isInService = false;
        addView(youTubePlayer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        playerControls = inflate(context, R.layout.player_controls, this);
        playerControlsWrapper = new PlayerControlsWrapper(this, playerControls);
        //playerControls.setOnTouchListener(this);
        playbackResumer = new PlaybackResumer(context,this);

        fullScreenListeners = new HashSet<>();
        fullScreenListeners.add(playerControlsWrapper);

        youTubePlayer.addListener(playerControlsWrapper);
        youTubePlayer.addListener(playbackResumer);
        youTubePlayer.setOnTouchListener(this);
        networkReceiver = new NetworkReceiver(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // if wrap content make the view 16:9
        if(getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int sixteenNineHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec) * 9 / 16, View.MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, sixteenNineHeight);
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Set a custom behaviour to the full screen button.
     */
    public void onFullScreenButtonListener(OnClickListener listener) {
        playerControlsWrapper.setOnFullScreenButtonListener(listener);
    }
public void onDropImageClickListener(){
    if(isInService)return;
}
    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void enterFullScreen() {
        if(isFullScreen)
            return;

        ViewGroup.LayoutParams viewParams = getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(viewParams);

        isFullScreen = true;

        for(YouTubePlayerFullScreenListener fullScreenListener : fullScreenListeners)
            fullScreenListener.onYouTubePlayerEnterFullScreen();
    }
public void setFullScreen(boolean isFullScreen){
    this.isFullScreen  = isFullScreen;
}
public boolean isInService(){
    return isInService;
}
    public void setInService(boolean isInService){
        this.isInService  = isInService;
    }
    public void exitFullScreen() {
        if(!isFullScreen)
            return;

        ViewGroup.LayoutParams viewParams = getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(viewParams);

        isFullScreen = false;

        for(YouTubePlayerFullScreenListener fullScreenListener : fullScreenListeners)
            fullScreenListener.onYouTubePlayerExitFullScreen();
    }

    public void toggleFullScreen() {
        if(isFullScreen)
            exitFullScreen();
        else
            enterFullScreen();
    }

    public boolean addFullScreenListener(@NonNull YouTubePlayerFullScreenListener fullScreenListener) {
        return fullScreenListeners.add(fullScreenListener);
    }

    public boolean removeFullScreenListener(@NonNull YouTubePlayerFullScreenListener fullScreenListener) {
        return fullScreenListeners.remove(fullScreenListener);
    }
    public void setOnRepeatPlayListenner(YoutubePlayerRepeat youtubePlayerRepeat){
        this.youtubePlayerRepeat = youtubePlayerRepeat;
    }
    public void playRepeat(){
        youtubePlayerRepeat.playRepeat();
    }
   public void onQualityChange(String quality){
       youTubePlayer.onPlaybackQualityChange(quality);
   }
    // calls to YouTubePlayer

    private boolean initialized = false;
    private Callable onNetworkAvailableCallback;

    /**
     * Initialize the player
     * @param youTubeListener lister for player events
     * @param handleNetworkEvents if <b>true</b> a broadcast receiver will be registered.<br/>If <b>false</b> you should handle network events with your broadcast receiver. See {@link YouTubePlayerView#onNetworkAvailable()} and {@link YouTubePlayerView#onNetworkUnavailable()}
     */
    public void initialize(@Nullable final YouTubePlayer.YouTubeListener youTubeListener, boolean handleNetworkEvents) {
        if(handleNetworkEvents)
            getContext().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if(!Utils.isOnline(getContext())) {
            Log.e("YouTubePlayerView", "Can't initialize because device is not connected to the internet.");

            onNetworkAvailableCallback = new Callable() {
                @Override
                public void call() {
                    Log.d("YouTubePlayerView", "Network available. Initializing player.");
                    youTubePlayer.initialize(youTubeListener);
                    initialized = true;

                    onNetworkAvailableCallback = null;
                }
            };

            return;
        }

        youTubePlayer.initialize(youTubeListener);
        initialized = true;
    }

    /**
     * See {@link YouTubePlayer#loadVideo(String, float)}
     */
    public void loadVideo(String videoId, float startSecond) {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.loadVideo(videoId, startSecond);
        playerControlsWrapper.onNewVideo();
    }

    /**
     * See {@link YouTubePlayer#cueVideo(String, float)}
     */
    public void cueVideo(String videoId, float startSeconds) {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.cueVideo(videoId, startSeconds);
        playerControlsWrapper.onNewVideo();
    }

    public void setPlaybackQuality(String quality){
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }
        youTubePlayer.setPlaybackQuality(quality);
    }
    public void loadVideoWithQuality(String videoId,float startSeconds,String quality){
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }
        youTubePlayer.loadVideoWithQuality(videoId,startSeconds,quality);
        playerControlsWrapper.onNewVideo();
    }
    public void getQuality(){
        youTubePlayer.getQuality();
    }

    /**
     * Calls {@link WebView#destroy()} on the player. And unregisters the broadcast receiver (for network events), if registered.
     * Call this method before destroying the host Fragment/Activity
     */
    public void release() {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.destroy();
        try {
            getContext().unregisterReceiver(networkReceiver);
        } catch (Exception ignore) {
        }
    }

    /**
     * See {@link YouTubePlayer#seekTo(int)}
     */
    public void seekTo(int time) {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.seekTo(time);
    }

public void nextVideo(){
    if(!initialized) {
        Log.e("YouTubePlayerView", "the player has not been initialized");
        return;
    }
    Log.e("next","onNext");
    youTubePlayer.nextVideo();
}
public void prevVideo(){
    if(!initialized) {
        Log.e("YouTubePlayerView", "the player has not been initialized");
        return;
    }
    youTubePlayer.prevVideo();
}

    /**
     * See {@link YouTubePlayer#play()}
     */
    public void playVideo() {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.play();
    }

    /**
     * See {@link YouTubePlayer#pause()}
     */
    public void pauseVideo() {
        if(!initialized) {
            Log.e("YouTubePlayerView", "the player has not been initialized");
            return;
        }

        youTubePlayer.pause();
    }

    public int getProgress(){
        return playerControlsWrapper.getProgress();
    }

    @Override
    public void onNetworkAvailable() {
        Log.d("YouTubePlayerView", "Network available.");
        if(!initialized && onNetworkAvailableCallback != null)
            onNetworkAvailableCallback.call();
        else
            playbackResumer.resume();
    }

    @Override
    public void onNetworkUnavailable() {

    }

    public void showTitle(boolean show) {
        playerControlsWrapper.showTitle(show);
    }

    public void setCustomActionRight(Drawable icon, View.OnClickListener clickListener) {
        playerControlsWrapper.setCustomActionRight(icon, clickListener);
    }
public void setCustomDropImage(boolean check,Drawable icon, View.OnClickListener clickListener){
    playerControlsWrapper.setCustomDropImage(check,icon, clickListener);
}
    public void setCustomActionLeft(Drawable icon, View.OnClickListener clickListener,boolean isPrev) {
        playerControlsWrapper.setCustomActionLeft(icon, clickListener,isPrev);
    }

    public void showFullScreenButton(boolean show) {
        playerControlsWrapper.showFullscreenButton(show);
    }

    public void hideUI(boolean hide) {
        playerControlsWrapper.hideUI(hide);
    }

public void setOnTouchListeners(onTouchListener onTouchListener){
    this.ontouchListener = onTouchListener;
}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(ontouchListener!=null) ontouchListener.onTouchListenner(view,motionEvent);
        return false;
    }

    public interface onTouchListener{
    public void onTouchListenner(View view,MotionEvent event);

}
public boolean isInitialized(){
    return initialized;
}
}
