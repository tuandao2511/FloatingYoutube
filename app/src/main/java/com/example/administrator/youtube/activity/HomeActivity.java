package com.example.administrator.youtube.activity;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.adapter.VideoAdapter;
import com.example.administrator.youtube.adapter.VideoLoader;
import com.example.administrator.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Video>> {

    private final static String KEY_API = "AIzaSyAcpdUiD8t256JRtSGY2241yhRzJfXtaY8";
    private final static String TREND_REQUEST_URL ="https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
            "chart=mostPopular&maxResults=50"+
            "&regionCode=US&key=" +KEY_API;
    private final static String LOG_TAG = HomeActivity.class.getSimpleName();
    private VideoAdapter mAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Log.v(LOG_TAG,"json la " +TREND_REQUEST_URL);
        mAdapter = new VideoAdapter(this,new ArrayList<Video>());
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(mAdapter);
        getLoaderManager().initLoader(0,null,this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"co gi o day");
        return new VideoLoader(this,TREND_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Video>> loader, List<Video> videos) {

        if (!videos.isEmpty()) {
            mAdapter.addAll(videos);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Video>> loader) {
        mAdapter.clear();
    }
}
