package com.example.administrator.youtube.youtubeplayer;

import android.content.Context;
import android.util.Log;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.constant.Constant;
import com.example.administrator.youtube.method.FetchDataVideos;
import com.example.administrator.youtube.model.Video;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.duration;

/**
 * Created by linh on 10/13/2017.
 */

public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;
    YouTube.Videos.List list;

    public YoutubeConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {

            @Override
            public void initialize(HttpRequest hr) throws IOException {
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(Constant.API_KEY); //TODO: Create a class in the same package with your Youtube API key as a static string.
            query.setType("video");
            query.setMaxResults(10L);
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url" +
                    ",snippet/channelTitle,snippet/publishedAt)");
        } catch (IOException e) {
            Log.d("YoutubeConnector", "Could not initialize: " + e);
        }
    }

    public ArrayList<Video> search(String keywords, ArrayList<Video> items) throws IOException {
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            items.clear();

            list = youtube.videos().list("statistics,contentDetails");
            Log.e("item", results.size() + " ");
            for (SearchResult result : results) {
                list.setId(result.getId().getVideoId());
                list.setKey(Constant.API_KEY);
                com.google.api.services.youtube.model.Video v = list.execute().getItems().get(0);
                //com.google.api.services.youtube.model.Video v2 = list.execute().getItems().get(1);


                String title = result.getSnippet().getTitle();
                String titleChannel = result.getSnippet().getChannelTitle();
                String channelId = result.getSnippet().getChannelId();
                String description = result.getSnippet().getDescription();
                String thumbnails = result.getSnippet().getThumbnails().getDefault().getUrl();
                String viewCount = String.valueOf(v.getStatistics().getViewCount());
                String duration = v.getContentDetails().getDuration();
                String videoId = result.getId().getVideoId();
                DateTime dateOfPublished = result.getSnippet().getPublishedAt();
                Log.e("search i",dateOfPublished+ " ");
                String period = FetchDataVideos.calculateElapsedTime(String.valueOf(result.getSnippet().getPublishedAt()));
                String covertedDuration;
                Log.e("aduration: ",duration);
                if(duration!= null) covertedDuration= FetchDataVideos.covertDuration(duration);
                else covertedDuration = "0:00";
                String covertViewCount = FetchDataVideos.covertViewCount(Double.parseDouble(viewCount), 0);
                Video video = new Video(videoId, title, thumbnails, titleChannel, covertViewCount, covertedDuration, channelId, period);
                Log.e("duration: ", v.getContentDetails().getDuration());
                items.add(video);
            }

            Log.e("search i",items.size()+ " ");
        } catch (IOException e) {
            Log.d("YoutubeConnector", "Could not search: " + e);
            throw e;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void autocomplete(String keywords, List<String> items) {
        //TODO: implement autocomplete: http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&q=Query
        throw new UnsupportedOperationException("Not implemented");
    }
}
