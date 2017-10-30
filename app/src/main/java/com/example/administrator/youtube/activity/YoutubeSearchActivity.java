package com.example.administrator.youtube.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.adapter.HistoryAdapter;
import com.example.administrator.youtube.adapter.LibraryAdapter;
import com.example.administrator.youtube.broadcast.DBStoreInHistoryChanged;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.database.VideoDbHelper;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.service.PlayService;
import com.example.administrator.youtube.youtubeplayer.YoutubeConnector;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linh on 10/13/2017.
 */

public class YoutubeSearchActivity extends AppCompatActivity implements View.OnClickListener,HistoryAdapter.OnItemClickListener {
    private RecyclerView listSearch;
    private HistoryAdapter adapter;
    private ArrayList<Video> searchResults = new ArrayList<>();
    private Handler handler = new Handler();
    private String keyword;
    private LinearLayout loadVideo;
    private View customToolBar;
    private ImageView back;
    private VideoDbHelper mDbHelper;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private  Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar);
        mDbHelper = new VideoDbHelper(this);
        Bundle bundle = getIntent().getExtras();
        loadVideo =(LinearLayout)findViewById(R.id.load);
        loadVideo.setVisibility(View.VISIBLE);
        listSearch = (RecyclerView) findViewById(R.id.list_search);
        listSearch.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(searchResults);
        listSearch.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        if (bundle != null) {
            keyword = bundle.getString("key");
            searchOnYoutube(keyword);
        }
    }
    private void initToolBar(Toolbar toolbar){
        Toolbar.LayoutParams lp1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
        customToolBar = LayoutInflater.from(this).inflate(R.layout.toolbar_settings_activity, null, false);
        back = (ImageView) customToolBar.findViewById(R.id.img_back);
        back.setOnClickListener(this);
        toolbar.addView(customToolBar, lp1);
        setSupportActionBar(toolbar);
    }

    private void searchOnYoutube(final String keywords) {
        setTitle(keywords);
        searchResults.clear();
        updateVideosFound();
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(YoutubeSearchActivity.this);
                try {
                    yc.search(keywords, searchResults);
                    handler.post(new Runnable() {
                        public void run() {
                            loadVideo.setVisibility(View.GONE);
                            updateVideosFound();

                         }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateVideosFound() {
        adapter.notifyDataSetChanged();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("YoutubeSearch Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemCLickListener(int position, ArrayList<Video> videos) {
        if (!isMyServiceRunning(PlayService.class)) {
            Intent intent = new Intent(YoutubeSearchActivity.this, FullPlayerScreenActivity.class);
            intent.setAction(Constant.PLAY_VIDEO_FROM_SEARCHACTIVITY);
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("video", videos.get(position));
            bundle.putSerializable("videos", passList(videos, position));
            intent.putExtras(bundle);
//        intent.putExtra("videoId", listId.get(position));
            YoutubeSearchActivity.this.startActivity(intent);
            Video video = videos.get(position);
            historySaved(videos, position);

        } else {
            Intent intent = new Intent(YoutubeSearchActivity.this, PlayService.class);
            intent.setAction(Constant.PLAY_VIDEO_FROM_MAIN_ACTIVITY_IN_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("curVideo", videos.get(position));
            bundle.putSerializable("videos", videos);
            intent.putExtras(bundle);
            YoutubeSearchActivity.this.startService(intent);
        }

    }


    @Override
    public void onPopupClickListener(Video video, View view) {

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void historySaved(ArrayList<Video> videos, int position) {
        Video video = videos.get(position);
        Cursor cursor = mDbHelper.historyQuery(video);
        if (cursor.getCount() == 0) {
            long i = mDbHelper.historyInsert(video);
            insertRelatedVideoList(videos, position);
        } else {
            int del = mDbHelper.historyDelete(video);
            long i1 = mDbHelper.historyInsert(video);
        }
        this.sendBroadcast(new Intent(DBStoreInHistoryChanged.ACTION_DATABASE_HISTORY_CHANGED));
    }
    private void insertRelatedVideoList(ArrayList<Video> videos, int position) {

    }
    private ArrayList<Video> passList(ArrayList<Video> list, int position) {
        ArrayList<Video> videos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i != position) {
                Video video = list.get(i);
                videos.add(video);
            }

        }
        return videos;
    }
}
