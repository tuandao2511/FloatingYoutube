package com.example.administrator.youtube.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.adapter.UpNextPlayAdapter;
import com.example.administrator.youtube.broadcast.DBStoreInHistoryChanged;
import com.example.administrator.youtube.broadcast.DBStoreInMyFavouriteChanged;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.database.VideoDbHelper;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.service.PlayService;
import com.example.administrator.youtube.youtubeplayer.AbstractYouTubeListener;
import com.example.administrator.youtube.youtubeplayer.FullScreenManager;
import com.example.administrator.youtube.youtubeplayer.Utils;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayer;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerFullScreenListener;
import com.example.administrator.youtube.youtubeplayer.YouTubePlayerView;
import com.example.administrator.youtube.youtubeplayer.YoutubePlayerRepeat;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by linh on 10/3/2017.
 */

public class FullPlayerScreenActivity extends AppCompatActivity implements View.OnClickListener, UpNextPlayAdapter.onItemClickListeners, Switch.OnCheckedChangeListener {
    private RecyclerView recyclerView;
    private YouTubePlayerView youTubePlayerView;
    private Switch autoPlay;
    private RelativeLayout rl_share, rl_favourite;
    private UpNextPlayAdapter adapter;
    private ArrayList<Video> videos = new ArrayList<>();
    private FullScreenManager fullScreenManager;
    private Video video;
    private TextView titleCur, viewCur;
    private int position = 0;
    private Video curentVideo;
    private Bundle bundle;
    private int curDuration = 0;
    private Video oldVideo;
    private ArrayList<Video> listPrevId = new ArrayList<>();
    private ArrayList<Video> startVideos = new ArrayList<>();
    private Stack<ArrayList<Video>> startListStack = new Stack<>();
    private VideoDbHelper mDbHelper;
    private final static String LOG_TAG = FullPlayerScreenActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_player_activity);
        initIdView();
        initValue();
        initActionView();
    }

    private void initIdView() {
        recyclerView = (RecyclerView) findViewById(R.id.list_item_play);
        autoPlay = (Switch) findViewById(R.id.check_autoplay);
        rl_share = (RelativeLayout) findViewById(R.id.rl_share);
        rl_favourite = (RelativeLayout) findViewById(R.id.rl_favourite);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        titleCur = (TextView) findViewById(R.id.title_video);
        viewCur = (TextView) findViewById(R.id.view_video);
    }

    private void initActionView() {
        adapter.setOnItemClicklisteners(this);
        autoPlay.setOnCheckedChangeListener(this);
        rl_favourite.setOnClickListener(this);
        rl_share.setOnClickListener(this);

    }

    private void initValue() {
        mDbHelper = new VideoDbHelper(this);
        autoPlay.setChecked(Utils.getAutoPlayOrClockScreenPref(FullPlayerScreenActivity.this, Utils.keyPref));
        fullScreenManager = new FullScreenManager(this);
        if (getIntent().getAction().equals(Constant.PLAY_VIDEO_FROM_MAINACTIVITY)) {
            bundle = getIntent().getExtras();
            video = (Video) bundle.getSerializable("video");
            curDuration = 0;
        } else if (getIntent().getAction().equals(Constant.PLAY_VIDEO_FROM_PLAYSERVICE)) {
            bundle = getIntent().getExtras();
            video = (Video) bundle.getSerializable("curVideos");
            curDuration = bundle.getInt("curDuration");
        }else if (getIntent().getAction().equals(Constant.PLAY_VIDEO_FROM_SEARCHACTIVITY)) {
            bundle = getIntent().getExtras();
            video = (Video) bundle.getSerializable("video");
        }else if(getIntent().getAction().equals(Constant.PLAY_VIDEO_FROM_SEARCHACTIVITY)){
            bundle = getIntent().getExtras();
            video = (Video) bundle.getSerializable("video");
            curDuration = 0;
        }


        curentVideo = video;
        if (video != null) {
            titleCur.setText(video.getTitle());
            viewCur.setText(String.valueOf(video.getViewCount()));
        }
        videos = (ArrayList<Video>) bundle.getSerializable("videos");
        Log.v("meo hieu ", videos.get(0).getTitle());
        startVideos.addAll(videos);
        adapter = new UpNextPlayAdapter(this, videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        youTubePlayerView.initialize(new AbstractYouTubeListener() {
            @Override
            public void onReady() {
                // youTubePlayerView.loadVideo(video.getID(), curDuration);
                String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(video.getID(), curDuration, quality);

            }

            @Override
            public void onStateChange(@YouTubePlayer.State.YouTubePlayerState int state) {
                youTubePlayerView.getQuality();
                if (state == YouTubePlayer.State.ENDED) {
                    oldVideo = curentVideo;
                    if (Utils.getAutoPlayOrClockScreenPref(FullPlayerScreenActivity.this, Utils.keyPref)) {
                        listPrevId.add(curentVideo);
                        if (adapter.getVideos().size() > 0) {
                            ArrayList<Video> videos = new ArrayList<Video>();
                            videos.addAll(adapter.getVideos());
                            startListStack.push(videos);
                            titleCur.setText(adapter.getVideos().get(position).getTitle());
                            viewCur.setText(String.valueOf(adapter.getVideos().get(position).getViewCount()));
                            String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
                            youTubePlayerView.loadVideoWithQuality(adapter.getVideos().get(position).getID(), 0, quality);
                            adapter.playVideoFromUpNextList(adapter.getVideos().get(position), curentVideo);
                            curentVideo = adapter.getVideos().get(position);
                            historySaved(videos,0);
                        }
                    }
                }
            }
        }, true);
        setCustomActionNextPrevious();
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setCustomDropImage();
                fullScreenManager.enterFullScreen();
                youTubePlayerView.setFullScreen(true);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setCustomDropImageInPortrait();
                fullScreenManager.exitFullScreen();
                youTubePlayerView.setFullScreen(false);
            }
        });
        youTubePlayerView.setOnRepeatPlayListenner(new YoutubePlayerRepeat() {
            @Override
            public void playRepeat() {
                String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
                youTubePlayerView.loadVideoWithQuality(curentVideo.getID(), 0, quality);
            }
        });

    }
    private void historySaved(ArrayList<Video> videos,int position) {
        Video video = videos.get(position);
        Cursor cursor = mDbHelper.historyQuery(video);
        if (cursor.getCount() == 0) {
            long i = mDbHelper.historyInsert(video);
            Log.v(LOG_TAG,"insert khi co history" +i);
        } else {
            int del = mDbHelper.historyDelete(video);
            Log.v(LOG_TAG,"delete history" +del);
            long i1 = mDbHelper.historyInsert(video);
            Log.v(LOG_TAG,"insert khi co " +i1);
        }
        sendBroadcast(new Intent(DBStoreInHistoryChanged.ACTION_DATABASE_HISTORY_CHANGED));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_favourite:
                favouriteSaved(curentVideo);
                break;
            case R.id.rl_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share to Friends");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,getResources().getString(R.string.share_to_friend)+video.getID()+" "+getResources().getString(R.string.share_to_friend_google_play)+getApplicationContext().getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share to Friends"));
                break;
        }
    }

    private void favouriteSaved(Video video) {
        Cursor cursor = mDbHelper.favouriteQuery(video);
        long i = 0;
        if (cursor.getCount() == 0) {
            i = mDbHelper.favouriteInsert(video);
            Log.v(LOG_TAG, "favourite saved " + i);
        }
        if (i > 0)
            sendBroadcast(new Intent(DBStoreInMyFavouriteChanged.ACTION_DATABASE_MY_FAVOURITE_CHANGED));
    }
    @Override
    public void onItemClickListener(Video video, int position) {
        oldVideo = curentVideo;
        ArrayList<Video> videos = new ArrayList<Video>();
        videos.addAll(adapter.getVideos());
        startListStack.push(videos);
        listPrevId.add(curentVideo);
        titleCur.setText(video.getTitle());
        viewCur.setText(String.valueOf(video.getViewCount()));
        String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
        youTubePlayerView.loadVideoWithQuality(video.getID(), 0, quality);
        if (curentVideo != null)
            adapter.playVideoFromUpNextList(video, curentVideo);
        curentVideo = video;
        historySaved(videos, position);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Utils.setAutoPlayofClockSreenPref(FullPlayerScreenActivity.this, Utils.keyPref, isChecked);
    }

    @Override
    public void onBackPressed() {

        if (youTubePlayerView.isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreenManager.exitFullScreen();
            youTubePlayerView.exitFullScreen();
        } else {
            if (youTubePlayerView != null) youTubePlayerView.release();
            if (videos != null) videos.clear();

            finish();
        }
    }

    private void setCustomActionNextPrevious() {
        youTubePlayerView.setCustomActionLeft(ContextCompat.getDrawable(FullPlayerScreenActivity.this, R.drawable.previous), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldVideo != null && listPrevId.size() > 0) {
                    curentVideo = listPrevId.get(listPrevId.size() - 1);
                    titleCur.setText(curentVideo.getTitle());
                    viewCur.setText(String.valueOf(curentVideo.getViewCount()));
                    String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(listPrevId.get(listPrevId.size() - 1).getID(), 0, quality);
                    listPrevId.remove(listPrevId.size() - 1);
                    adapter.refeshItem(startListStack.pop());
                    Log.e("Log", startListStack.size() + " ");


                }
            }
        }, true);
        youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(FullPlayerScreenActivity.this, R.drawable.next), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getVideos().size() > 0) {

                    ArrayList<Video> videos = new ArrayList<Video>();
                    videos.addAll(adapter.getVideos());
                    startListStack.push(videos);
                    titleCur.setText(adapter.getVideos().get(position).getTitle());
                    viewCur.setText(String.valueOf(adapter.getVideos().get(position).getViewCount()));
                    oldVideo = curentVideo;
                    String quality = Utils.getQualityPref(FullPlayerScreenActivity.this, Utils.keyQuality);
                    youTubePlayerView.loadVideoWithQuality(adapter.getVideos().get(position).getID(), 0, quality);
                    Video cur = adapter.getVideos().get(position);
                    adapter.playVideoFromUpNextList(adapter.getVideos().get(position), curentVideo);
                    listPrevId.add(curentVideo);
                    curentVideo = cur;


                    Log.e("LOG", curentVideo.getTitle() + " " + startListStack.size());


                }
            }
        });

        youTubePlayerView.setCustomDropImage(true, ContextCompat.getDrawable(FullPlayerScreenActivity.this, R.drawable.ic_arrow_up), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubePlayerView != null) youTubePlayerView.release();
                Intent intent = new Intent(FullPlayerScreenActivity.this, PlayService.class);
                intent.setAction(Constant.PLAY_VIDEO_FROM_FULLSCREEN_ACTIVITY);
                Bundle bundle = new Bundle();
                bundle.putInt("time", youTubePlayerView.getProgress());
                bundle.putSerializable("curVideo", curentVideo);
                bundle.putSerializable("videos", adapter.getVideos());
                intent.putExtras(bundle);
                startService(intent);


                finish();
            }
        });
    }

    private void setCustomDropImage() {
        youTubePlayerView.setCustomDropImage(false, ContextCompat.getDrawable(FullPlayerScreenActivity.this, R.drawable.ic_arrow_up), null);
    }

    private void setCustomDropImageInPortrait() {
        youTubePlayerView.setCustomDropImage(true, ContextCompat.getDrawable(FullPlayerScreenActivity.this, R.drawable.ic_arrow_up), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullPlayerScreenActivity.this, PlayService.class);
                intent.setAction(Constant.PLAY_VIDEO_FROM_FULLSCREEN_ACTIVITY);
                Log.e("bundle","s "+youTubePlayerView.getProgress()+ " " + curentVideo.getID() + adapter.getVideos().size()+" ");
                Bundle bundle = new Bundle();
                bundle.putInt("time", youTubePlayerView.getProgress());
                bundle.putSerializable("curVideo", curentVideo);
                bundle.putSerializable("videos", adapter.getVideos());
                intent.putExtras(bundle);
                startService(intent);
                if (youTubePlayerView != null) youTubePlayerView.release();

                finish();
            }
        });
    }
}

