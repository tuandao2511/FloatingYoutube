package com.example.administrator.youtube.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.activity.FullPlayerScreenActivity;
import com.example.administrator.youtube.adapter.HistoryAdapter;
import com.example.administrator.youtube.adapter.LibraryAdapter;
import com.example.administrator.youtube.broadcast.DBStoreInHistoryChanged;
import com.example.administrator.youtube.broadcast.DBStoreInMyFavouriteChanged;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.database.VideoDbHelper;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.service.PlayService;

import java.util.ArrayList;
import java.util.List;

import static cz.msebera.android.httpclient.client.methods.RequestBuilder.delete;

/**
 * Created by Administrator on 10/3/2017.
 */

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {



    public static final String LOG_TAG = HistoryFragment.class.getSimpleName();
    LinearLayoutManager manager;
    RecyclerView recyclerView ;
    VideoDbHelper mDbHelper;
    HistoryAdapter mAdapter;
    ArrayList<Video> historyList;
    ArrayList<Video> listId;

    /*notify my favourite changed */
    private DBStoreInHistoryChanged mReceiver = new DBStoreInHistoryChanged(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
//            if (intent.getAction().equalsIgnoreCase(DBStoreInHistoryChanged.ACTION_DATABASE_HISTORY_CHANGED)) {
                Log.v(LOG_TAG, "broad cast ");
                new LoadHistory().execute();
//            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycle_list_library,container,false);
        getContext().registerReceiver(mReceiver,new IntentFilter(DBStoreInHistoryChanged.ACTION_DATABASE_HISTORY_CHANGED));
        Log.v(LOG_TAG,"abcd efgh");
        manager  = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        mDbHelper = new VideoDbHelper(getContext());
        new LoadHistory().execute();

        return view;
    }


    private void delete(Video video) {
        int delete = mDbHelper.historyDelete(video);
        Log.v(LOG_TAG,"delete history " +delete);
        if (delete > 0) new LoadHistory().execute();
    }
    /*add favourite if exist*/
    private void favouriteSaved(Video video) {
        Cursor cursor = mDbHelper.favouriteQuery(video);
        long i =0;
        if (cursor.getCount() == 0) {
            i = mDbHelper.favouriteInsert(video);
            Log.v(LOG_TAG,"favourite saved " +i);
        }
        if (i > 0) getContext().sendBroadcast(new Intent(DBStoreInMyFavouriteChanged.ACTION_DATABASE_MY_FAVOURITE_CHANGED));

    }

    @Override
    public void onItemCLickListener(int position, ArrayList<Video> videos) {
        Log.e(LOG_TAG, "onClick");
        if(!isMyServiceRunning(PlayService.class)) {
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
            historySaved(video);

        }else {
            Intent intent = new Intent(getActivity(), PlayService.class);
            intent.setAction(Constant.PLAY_VIDEO_FROM_MAIN_ACTIVITY_IN_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("curVideo", videos.get(position));
            bundle.putSerializable("videos", videos);
            intent.putExtras(bundle);
            getActivity().startService(intent);
        }
    }

    private void historySaved(Video video) {

        Cursor cursor = mDbHelper.historyQuery(video);
        if (cursor.getCount() == 0) {
            long i = mDbHelper.historyInsert(video);
            Log.v(LOG_TAG," khi k co item nao " +i);
        } else {
            int del = mDbHelper.historyDelete(video);
            Log.v(LOG_TAG," del item trung  " +del);
            long i1 = mDbHelper.historyInsert(video);
            Log.v(LOG_TAG," them item trung   " +i1);
        }

    }

    private ArrayList<Video> passList(ArrayList<Video> list, int position) {
        ArrayList<Video> videos = new ArrayList<>();
        videos = mDbHelper.getRelatedList(list.get(position));
        if (videos.isEmpty()) videos = mDbHelper.defaultGetData();
        return videos;
    }
    @Override
    public void onPopupClickListener(final Video video, View view) {
        Log.v(LOG_TAG,"popup " +video.getID());
        RelativeLayout popup = (RelativeLayout) view.findViewById(R.id.popup);
        PopupMenu popupMenu = new PopupMenu(getContext(),popup);
        popupMenu.inflate(R.menu.history_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.library_delete:
                        delete(video);
                        return true;
                    case R.id.library_share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share to Friends");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,getContext().getResources().getString(R.string.share_to_friend)+video.getID()+" "+getContext().getResources().getString(R.string.share_to_friend_google_play)+getActivity().getApplicationContext().getPackageName());
                        getActivity().startActivity(Intent.createChooser(shareIntent, "Share to Friends"));
                        return true;
                    case R.id.library_add_favourite:
                        favouriteSaved(video);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager)getActivity(). getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

    /*load video store in database */
    private class LoadHistory extends AsyncTask<Void,Void,ArrayList<Video>> {

        public LoadHistory() {

        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {
            historyList = mDbHelper.historyGetData();
            Log.v(LOG_TAG,"get data the nao " +historyList.size());
            return historyList;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> histories) {
            if (histories !=null) {
                mAdapter = new HistoryAdapter(histories);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(HistoryFragment.this);
            }
            Log.e(LOG_TAG,"history size " +histories.size());
        }

    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.v(LOG_TAG,"on resume");
//        getContext().registerReceiver(mReceiver,new IntentFilter(DBStoreInMyFavouriteChanged.ACTION_DATABASE_MY_FAVOURITE_CHANGED));
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        getContext().unregisterReceiver(mReceiver);
//    }
        @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }
}
