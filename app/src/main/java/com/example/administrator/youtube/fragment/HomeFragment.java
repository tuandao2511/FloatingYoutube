package com.example.administrator.youtube.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.Loader;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.app.LoaderManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.administrator.youtube.R;
import com.example.administrator.youtube.adapter.VideoAdapter;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.method.FetchDataVideos;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.service.PlayService;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by Administrator on 9/26/2017.
 */

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Video>>,VideoAdapter.onItemClickListener {

    private final static String KEY_API = "AIzaSyAcpdUiD8t256JRtSGY2241yhRzJfXtaY8";
    private final static String TREND_REQUEST_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
            "chart=mostPopular&maxResults=10" +
            "&regionCode=US&key=" + KEY_API;
    private final static String LOG_TAG = HomeFragment.class.getSimpleName();
    private VideoAdapter mAdapter;
    private RecyclerView list;
    private List<Video> videoList = new ArrayList<>();
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager manager;
    private String nextpageToken = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        manager = new LinearLayoutManager(getActivity());
        Log.v(LOG_TAG, "json la " + TREND_REQUEST_URL);
        list = (RecyclerView) view.findViewById(R.id.list);
        new LoadVideo(getActivity(), list).execute();
        onRecycleviewScroll();

        return view;
    }

    @Override
    public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "co gi o day");
        return null;
    }


    @Override
    public void onLoadFinished(Loader<List<Video>> loader, List<Video> videos) {
        if (videos != null) {
            videoList.addAll(videos);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemCLickListener(String videoId) {
        Log.e(LOG_TAG,"onClick");
        Intent intent = new Intent(getActivity(), PlayService.class);
        intent.putExtra("videoId",videoId);
        getActivity().startService(intent);
    }

    private class LoadVideo extends AsyncTask<Void, Void, List<Video>> {
        RecyclerView recyclerView;
        Activity mContex;

        public LoadVideo(Activity contex, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.mContex = contex;
        }

        @Override
        protected List<Video> doInBackground(Void... params) {
            List<Video> videos = new ArrayList<>();
            videos = FetchDataVideos.FetchVideoData(TREND_REQUEST_URL);
            nextpageToken = FetchDataVideos.nextPageToken;
            Log.e(LOG_TAG, videos.size() + " ");
            return videos;
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            mAdapter = new VideoAdapter(videos);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(HomeFragment.this);
            Log.e(LOG_TAG, videos.size() + " ");

        }
    }

    private class LoadMoreVideo extends AsyncTask<Void, Void, List<Video>> {
        RecyclerView recyclerView;
        Activity mContex;

        public LoadMoreVideo(Activity contex, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.mContex = contex;
        }

        @Override
        protected List<Video> doInBackground(Void... params) {
            List<Video> videos = new ArrayList<>();
            if (!nextpageToken.equals("")) {
                String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
                        "chart=mostPopular&maxResults=10" +
                        "&pageToken=" + nextpageToken + "&regionCode=US&key=" + Constant.API_KEY;
                videos= (FetchDataVideos.FetchVideoData(url));

                nextpageToken = FetchDataVideos.nextPageToken;
            }
            Log.e(LOG_TAG, videos.size() + " ");
            return videos;
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            mAdapter.loadMoreVideo(videos);
            loading = true;
            Log.e(LOG_TAG, videos.size() + " ");

        }
    }

    @Override
    public void onLoaderReset(Loader<List<Video>> loader) {
        videoList.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void onRecycleviewScroll() {
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();

                    pastVisiblesItems = manager.findFirstVisibleItemPosition();
                    Log.e(LOG_TAG, visibleItemCount + " " + totalItemCount + " " + pastVisiblesItems);
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            new LoadMoreVideo(getActivity(), list).execute();
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                        }
                    }

                }
            }
        });
    }

}