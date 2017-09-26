package com.example.administrator.youtube.adapter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.method.FetchDataVideos;
import com.example.administrator.youtube.model.Video;

import java.util.List;

/**
 * Created by Administrator on 9/16/2017.
 */

public class VideoLoader extends AsyncTaskLoader<List<Video>> {


    private static final String LOG_TAG = VideoLoader.class.getSimpleName();
    private String mUrl ;
    String nextPage ;
    public VideoLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, " onStartLoading đã dược implement ");
        super.onStartLoading();
        forceLoad();
    }


    @Override
    public List<Video> loadInBackground()
    {
        Log.i(LOG_TAG,"Du lieu dang dc fetch");
        List<Video> videos = null;
        videos = FetchDataVideos.FetchVideoData(mUrl);
        nextPage = FetchDataVideos.nextPageToken;
        while (nextPage != "") {
            String TREND_REQUEST_URL ="https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&" +
                    "chart=mostPopular&maxResults=50"+
                    "&pageToken="+nextPage+"&regionCode=US&key=" + Constant.API_KEY;
            Log.v(LOG_TAG,"tuan " +TREND_REQUEST_URL);
            videos.addAll(FetchDataVideos.FetchVideoData(TREND_REQUEST_URL));
            nextPage = FetchDataVideos.nextPageToken;
        }
        return videos;
    }
}
