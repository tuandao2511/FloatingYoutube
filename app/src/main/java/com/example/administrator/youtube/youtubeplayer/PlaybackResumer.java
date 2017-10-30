package com.example.administrator.youtube.youtubeplayer;

import android.content.Context;
import android.util.Log;

public class PlaybackResumer implements YouTubePlayer.YouTubeListener {

    private boolean isPlaying = false;
    private int error = -1;

    private String videoId;
    private float currentSecond;
    private String quality;
    private YouTubePlayerView youTubePlayerView;
    private Context context;

    public PlaybackResumer(Context context, YouTubePlayerView youTubePlayerView) {
        this.youTubePlayerView = youTubePlayerView;
        this.context = context;
    }

    public void resume() {
        if (isPlaying && error == YouTubePlayer.Error.HTML_5_PLAYER) {
            quality = Utils.getQualityPref(context, Utils.keyQuality);
            youTubePlayerView.loadVideoWithQuality(videoId, 0, quality);
        }
        // youTubePlayerView.loadVideo(videoId, currentSecond);
        else if (!isPlaying && error == YouTubePlayer.Error.HTML_5_PLAYER)
            youTubePlayerView.cueVideo(videoId, currentSecond);

        error = -1;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onStateChange(@YouTubePlayer.State.YouTubePlayerState int state) {
        switch (state) {
            case YouTubePlayer.State.ENDED:
                isPlaying = false;
                break;
            case YouTubePlayer.State.PAUSED:
                isPlaying = false;
                break;
            case YouTubePlayer.State.PLAYING:
                isPlaying = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackQualityChange(@YouTubePlayer.PlaybackQuality.Quality int playbackQuality) {
        Log.e("qua", playbackQuality + " ");
    }

    @Override
    public void onPlaybackRateChange(double rate) {

    }

    @Override
    public void onError(@YouTubePlayer.Error.PlayerError int error) {
        if (error == YouTubePlayer.Error.HTML_5_PLAYER)
            this.error = error;
    }

    @Override
    public void onApiChange() {

    }

    @Override
    public void onCurrentSecond(float second) {
        this.currentSecond = second;
    }

    @Override
    public void onVideoDuration(float duration) {

    }

    @Override
    public void onLog(String log) {

    }

    @Override
    public void onVideoTitle(String videoTitle) {

    }

    @Override
    public void onVideoId(String videoId) {
        this.videoId = videoId;
    }


}
