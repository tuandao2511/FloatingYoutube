package com.example.administrator.youtube.method;

import android.util.Log;

import com.example.administrator.youtube.model.Video;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.media.CamcorderProfile.get;
import static java.util.Calendar.HOUR_OF_DAY;

/**
 * Created by Administrator on 9/17/2017.
 */

public final class FetchDataVideos {

    private static final String LOG_TAG = FetchDataVideos.class.getSimpleName();
    private static final int REPONSE_CODE = 200;
    public static String nextPageToken;

    public static List<Video> FetchVideoData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);
        String jsonReposne = null;
        try {
            jsonReposne = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error closing input stream");
        }

        List<Video> videos = extractVideos(jsonReposne);

        return videos;
    }



    /*tao ket noi */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonReposne= "";

        if (url == null) return jsonReposne;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == REPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonReposne = readInputStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Http request is not conection ",e);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonReposne;
    }
    /*covert tu mot input stream sang mot string*/
    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder readInputStream = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                readInputStream.append(line);
                line = reader.readLine();
            }
        }
        return readInputStream.toString();
    }

    /*parse url from strng */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG,"uri la gi " +url);
        return url;
    }

    private static List<Video> extractVideos(String jsonReposne) {

        List<Video> videos = new ArrayList<>();

        try {
            JSONObject jsonRootObject = new JSONObject(jsonReposne);
            if (jsonRootObject.has("nextPageToken")) nextPageToken = jsonRootObject.optString("nextPageToken");
            else nextPageToken = "";
            Log.v(LOG_TAG,"token la gi " +nextPageToken);
            JSONArray jsonArray = jsonRootObject.getJSONArray("items");

            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonObjectSnippet = jsonObject.optJSONObject("snippet");
                JSONObject jsonObjectThumbnails = jsonObjectSnippet.optJSONObject("thumbnails");
                JSONObject jsonObjectDefault = jsonObjectThumbnails.optJSONObject("high");
                JSONObject jsonObjectContentDetails = jsonObject.optJSONObject("contentDetails");
                JSONObject jsonObjectStatistics = jsonObject.optJSONObject("statistics");


                String id = jsonObject.optString("id");
                Log.v(LOG_TAG,"id la gi " +id);
                String title = jsonObjectSnippet.optString("title");
                Log.v(LOG_TAG,"title la gi " +title);
                String thumbnails = jsonObjectDefault.optString("url");
                Log.v(LOG_TAG,"thumbnails la gi " +thumbnails);
                String channelTitle =jsonObjectSnippet.optString("channelTitle");
                Log.v(LOG_TAG,"channelTitle la gi " +channelTitle);
                int viewCount = jsonObjectStatistics.optInt("viewCount");
                Log.v(LOG_TAG,"viewcount la gi " +viewCount);
                int likeCount = jsonObjectStatistics.optInt("likeCount");
                Log.v(LOG_TAG,"likeCount la gi " +likeCount);
                int dislikeCount = jsonObjectStatistics.optInt("dislikeCount");
                Log.v(LOG_TAG,"dislikeCount la gi " +dislikeCount);
                String duration = jsonObjectContentDetails.optString("duration");
                Log.v(LOG_TAG,"duration la gi " +duration);
                String description = jsonObjectSnippet.optString("description");
                String channelId = jsonObjectSnippet.optString("channelId");
                Log.v(LOG_TAG,"channelId la gi " +channelId);
                String dateOfPublished = jsonObjectSnippet.optString("publishedAt");
                Log.v(LOG_TAG,"dateOfPublished la gi " +dateOfPublished);

                /*covert string to ...*/
                String period = calculateElapsedTime(dateOfPublished);
                String covertedDuration = covertDuration(duration);
                videos.add(new Video(id, title, thumbnails, channelTitle, viewCount,
                        likeCount, dislikeCount, covertedDuration, description,
                        channelId,period));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return videos;
    }

    private static String calculateElapsedTime(String date) throws ParseException {

        String elapsedTime = null;

        date = date.replaceAll("T"," ");
        date = date.replaceAll(".000Z","");
        Log.v(LOG_TAG,"formatter date " +date);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datePublished = format.parse(date);
        String newDateString = format.format(datePublished);
        Log.v(LOG_TAG,"date demo " +newDateString);
        Calendar calSet = Calendar.getInstance();
        calSet.setTime(datePublished);

        Log.v(LOG_TAG,"second "+calSet.get(Calendar.SECOND));
        Log.v(LOG_TAG,"Minute "+calSet.get(Calendar.MINUTE));
        Log.v(LOG_TAG,"hour "+ calSet.get(HOUR_OF_DAY));
        Log.v(LOG_TAG,"day of month "+calSet.get(Calendar.DAY_OF_MONTH));
        Log.v(LOG_TAG," month"+calSet.get(Calendar.MONTH ));

        calSet.get(Calendar.YEAR);
        Log.v(LOG_TAG," year "+calSet.get(Calendar.YEAR));
//        Date datePublished = calSet.getTime();
        Calendar calNow= Calendar.getInstance();
        Date dateNow = calNow.getTime();
        Log.v(LOG_TAG, " su khasc biet" +datePublished.getTime() +" " +dateNow.getTime());
        Log.v(LOG_TAG,"second now "+calNow.get(Calendar.SECOND));
        Log.v(LOG_TAG,"minute now "+calNow.get(Calendar.MINUTE));
        Log.v(LOG_TAG,"hour now  "+calNow.get(Calendar.HOUR_OF_DAY));
        Log.v(LOG_TAG,"day now "+calNow.get(Calendar.DAY_OF_MONTH));
        Log.v(LOG_TAG,"month now "+calNow.get(Calendar.MONTH));
        Log.v(LOG_TAG,"year now "+calNow.get(Calendar.YEAR));


        Interval interval = new Interval(datePublished.getTime(),dateNow.getTime());
        Period period = interval.toPeriod();
        if (period.getYears() > 0)  elapsedTime = period.getYears() + " years ago";
        else if (period.getMonths() > 0) elapsedTime = period.getMonths() + " months ago";
        else if (period.getDays() > 0) elapsedTime = period.getDays() + " months ago";
        else if (period.getHours() > 0) elapsedTime = period.getHours() +" hours ago";
        else if (period.getMinutes() > 0) elapsedTime = period.getMinutes() +" minutes ago";
        else if (period.getSeconds() > 0) elapsedTime = period.getSeconds() +" seconds ago";
        Log.v(LOG_TAG,elapsedTime);
        Log.v(LOG_TAG,"elapsedTime la gi "+elapsedTime);
        return elapsedTime;
    }

    private static String covertDuration(String sDuration){
        Log.v(LOG_TAG,"duration la gi " +sDuration);
        Calendar calendar = Calendar.getInstance();
        Period period = Period.parse(sDuration);
        long second = period.getSeconds();
        int hours=0 ;
        int mininus=0;

        while(second > 60) {
            if (second >= 3600) {
                hours = (int) (second/3600);
                second = second%3600;
            }
            else if (second >= 60) {
                mininus = (int) (second/60);
                second = second%60;
            }
        }
        calendar.set(HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,mininus);
        calendar.set(Calendar.SECOND,(int)second);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String formatted = format.format(calendar.getTime());

        Log.v(LOG_TAG,"formatted "+formatted);
        return formatted;
    }
}
