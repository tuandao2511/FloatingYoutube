package com.example.administrator.youtube.fragment;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.administrator.youtube.R;
import com.example.administrator.youtube.activity.FullPlayerScreenActivity;
import com.example.administrator.youtube.adapter.VideoAdapter;
import com.example.administrator.youtube.broadcast.DBStoreInHistoryChanged;
import com.example.administrator.youtube.broadcast.DBStoreInMyFavouriteChanged;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.database.VideoDbHelper;
import com.example.administrator.youtube.database.VideoContract.VideoHistory;
import com.example.administrator.youtube.interfaces.OnLoadMoreListener;
import com.example.administrator.youtube.method.FetchDataVideos;
import com.example.administrator.youtube.method.FetchVideoId;
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


import static android.R.attr.id;
import static android.R.attr.key;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by Administrator on 9/26/2017.
 */

public class HomeFragment extends Fragment implements VideoAdapter.onItemClickListener, OnLoadMoreListener {

    private final static String KEY_API = "AIzaSyAcpdUiD8t256JRtSGY2241yhRzJfXtaY8";
//    private final static String TREND_REQUEST_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
//            "chart=mostPopular&maxResults=10" +
//            "&regionCode=US&key=" + KEY_API;
    private final static String LOG_TAG = HomeFragment.class.getSimpleName();
    private VideoAdapter mAdapter;
    private RecyclerView list;
    private ArrayList<Video> videoList = new ArrayList<>();
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager manager;
    private String nextpageToken = null;
    private View view;
    private VideoDbHelper mDbHelper;
    private RelativeLayout loadMore;
    private LinearLayout linearLayout;
    private YoutubeConnector youtubeConnector;
    private GoogleApiClient client;
    private Handler handler = new Handler();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view != null) return view;

        view = inflater.inflate(R.layout.fragment_home, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.progress);
        linearLayout.setVisibility(View.VISIBLE);
        loadMore = (RelativeLayout) view.findViewById(R.id.rl_load_more);
        mDbHelper = new VideoDbHelper(getContext());
        manager = new LinearLayoutManager(getActivity());
        list = (RecyclerView) view.findViewById(R.id.list);
        youtubeConnector = new YoutubeConnector(getContext());
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();

        new LoadVideo(getActivity(), list).execute();
        //      onRecycleviewScroll();
//        Cursor cursor = mDbHelper.queryAllContacts();
//        Log.v(LOG_TAG,"cursor la gi " +cursor.getCount() +" cursor " +cursor );
        return view;
    }


    @Override
    public void onItemCLickListener(int position, ArrayList<Video> videos) {
        Log.e(LOG_TAG, "onClick");
        if (!isMyServiceRunning(PlayService.class)) {
            Intent intent = new Intent(getActivity(), FullPlayerScreenActivity.class);
            intent.setAction(Constant.PLAY_VIDEO_FROM_MAINACTIVITY);
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("video", videos.get(position));
            bundle.putSerializable("videos", passList(videos, position));
            intent.putExtras(bundle);
//        intent.putExtra("videoId", listId.get(position));
            getActivity().startActivity(intent);
            Video video = videos.get(position);
            historySaved(videos, position);

        } else {
            Intent intent = new Intent(getActivity(), PlayService.class);
            intent.setAction(Constant.PLAY_VIDEO_FROM_MAIN_ACTIVITY_IN_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("curVideo", videos.get(position));
            bundle.putSerializable("videos", videos);
            intent.putExtras(bundle);
            getActivity().startService(intent);
        }

        if (isMyServiceRunning(PlayService.class)) Log.e("true", true + " ");
        else Log.e("false", false + " ");
    }

    private void historySaved(ArrayList<Video> videos, int position) {
        Video video = videos.get(position);
        Cursor cursor = mDbHelper.historyQuery(video);
        if (cursor.getCount() == 0) {
            long i = mDbHelper.historyInsert(video);

        } else {
            int del = mDbHelper.historyDelete(video);
            long i1 = mDbHelper.historyInsert(video);
        }
        insertRelatedVideoList(videos, position);
        getContext().sendBroadcast(new Intent(DBStoreInHistoryChanged.ACTION_DATABASE_HISTORY_CHANGED));
    }

    /*pass list releted video into table*/
    private void insertRelatedVideoList(ArrayList<Video> videos, int position) {
        Video rootVideo = videos.get(position);
        for (int i = 0; i < videos.size(); i++) {
            if (i != position) {
                Video relatedVideo = videos.get(i);
                long newId = mDbHelper.relatedInsert(relatedVideo,rootVideo);
                Log.v(LOG_TAG, "new id la " + newId);
            }
        }
    }

    @Override
    public void onPopupClickListener(final int position, final ArrayList<Video> videos, View view) {
        final Video video = videos.get(position);
        RelativeLayout popup = (RelativeLayout) view.findViewById(R.id.popup);
        PopupMenu popupMenu = new PopupMenu(getContext(), popup);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share to Friends");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getContext().getResources().getString(R.string.share_to_friend) + videos.get(position).getID() + getContext().getResources().getString(R.string.share_to_friend_google_play) + getActivity().getApplicationContext().getPackageName());
                        getActivity().startActivity(Intent.createChooser(shareIntent, "Share to Friends"));
                        return true;
                    case R.id.add_favourite_video:
                        favouriteSaved(video);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void favouriteSaved(Video video) {
        Cursor cursor = mDbHelper.favouriteQuery(video);
        long i = 0;
        if (cursor.getCount() == 0) {
            i = mDbHelper.favouriteInsert(video);
            Log.v(LOG_TAG, "favourite saved " + i);
        }
        if (i > 0)
            getContext().sendBroadcast(new Intent(DBStoreInMyFavouriteChanged.ACTION_DATABASE_MY_FAVOURITE_CHANGED));

    }

    @Override
    public void onLoadMore() {
        loadMore.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                new LoadMoreVideo(getActivity(), list).execute();
            }
        });
    }

    private class LoadVideo extends AsyncTask<Void, Void, ArrayList<Video>> {
        RecyclerView recyclerView;
        Activity mContex;

        public LoadVideo(Activity context, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.mContex = context;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {
            ArrayList<Video> videos = new ArrayList<>();
            String url_with_most_view = Constant.URL_DEFAULT_WITH_MOST_VIEW + getUrl() + "&key=" +Constant.API_KEY;
            Log.v("hihi", url_with_most_view);
            videos = FetchDataVideos.FetchVideoData(url_with_most_view);

//            nextpageToken = FetchDataVideos.nextPageToken;
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            linearLayout.setVisibility(View.GONE);
            if (videos != null) {
                 videoList.addAll(videos);
                recyclerView.setLayoutManager(manager);
                mAdapter = new VideoAdapter(videoList, recyclerView);
                mDbHelper.defaultInsert(videos);
                Log.e("sxx",videoList.size()+" ");
              //  videoList.addAll(videos);

                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(HomeFragment.this);
                mAdapter.setmOnLoadMoreListener(HomeFragment.this);
            }

        }
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

    private class LoadMoreVideo extends AsyncTask<Void, Void, ArrayList<Video>> {
        RecyclerView recyclerView;
        Activity mContex;

        public LoadMoreVideo(Activity contex, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.mContex = contex;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {
            ArrayList<Video> videos = new ArrayList<>();
//            if (!nextpageToken.equals("")) {
//                String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
//                        "chart=mostPopular&maxResults=10" +
//                        "&pageToken=" + nextpageToken + "&regionCode=US&key=" + Constant.API_KEY;
//                videos = (FetchDataVideos.FetchVideoData(url));
//
//                nextpageToken = FetchDataVideos.nextPageToken;
//            }
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            if (videos != null) {
                loadMore.setVisibility(View.GONE);
//                mAdapter.loadMoreVideo(videoList);
                mAdapter.setLoaded();
            }

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void updateAdapter(ArrayList<Video> videoList) {
        mAdapter.refeshList(videoList);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
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
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void updateList(final String keyword) {
        new Thread() {
            @Override
            public void run() {
                try {
                   videoList= youtubeConnector.search(keyword, videoList);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("size",videoList.size()+" ");
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e("search", e.toString() + " ");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public String getUrl (){
        StringBuilder builder = new StringBuilder();
        ArrayList<String> listId = FetchVideoId.loadJsonFromAsset(getContext(),"dataWithTheMostViewd.json");
        builder.append(listId.get(0));
        for (int i=1; i<listId.size(); i++) {
            builder.append(getString(R.string.addId) +listId.get(i));

        }
        return builder.toString();
    }
}