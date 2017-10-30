package com.example.administrator.youtube.method;

import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.youtube.constant.Constant;
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
    private static char[] c = new char[]{'K', 'M', 'B'};

    public static ArrayList<Video> FetchVideoData(String requestUrl) {
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

        ArrayList<Video> videos = extractVideos(jsonReposne);

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

    private static ArrayList<Video> extractVideos(String jsonReposne) {

        ArrayList<Video> videos = new ArrayList<>();

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
                JSONObject jsonObjectDefault = jsonObjectThumbnails.optJSONObject("medium");
                JSONObject jsonObjectContentDetails = jsonObject.optJSONObject("contentDetails");
                JSONObject jsonObjectStatistics = jsonObject.optJSONObject("statistics");


                String id = jsonObject.optString("id");
                String title = jsonObjectSnippet.optString("title");
                String thumbnails = jsonObjectDefault.optString("url");
                String channelTitle =jsonObjectSnippet.optString("channelTitle");
                long viewCount = jsonObjectStatistics.optLong("viewCount");
                String duration = jsonObjectContentDetails.optString("duration");
                String channelId = jsonObjectSnippet.optString("channelId");
                String dateOfPublished = jsonObjectSnippet.optString("publishedAt");

                /*covert string to ...*/
                String period = calculateElapsedTime(dateOfPublished);
                String covertedDuration = covertDuration(duration);
                String covertViewCount =  covertViewCount(viewCount,0);


                videos.add(new Video(id, title, thumbnails, channelTitle, covertViewCount,
                        covertedDuration,
                        channelId,period ));
                }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public static String covertViewCount(double viewCount,int iteration) {
        double d = ((long) viewCount / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                :covertViewCount(d, iteration+1));
    }

    public static String calculateElapsedTime(String date) throws ParseException {

        String elapsedTime = null;

        date = date.replaceAll("T"," ");
        date = date.replaceAll(".000Z","");
        Log.v(LOG_TAG,"date la gi " +date);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datePublished = format.parse(date);
        String newDateString = format.format(datePublished);
        Calendar calSet = Calendar.getInstance();
        calSet.setTime(datePublished);



        calSet.get(Calendar.YEAR);
//        Date datePublished = calSet.getTime();
        Calendar calNow= Calendar.getInstance();
        Date dateNow = calNow.getTime();


        Interval interval = new Interval(datePublished.getTime(),dateNow.getTime());
        Period period = interval.toPeriod();

        if (period.getYears() > 0)  elapsedTime = period.getYears() + Constant.ELAPSED_TIME_YEAR;
        else if (period.getMonths() > 0) elapsedTime = period.getMonths() + Constant.ELAPSED_TIME_MONTH;
        else if (period.getDays() > 0) elapsedTime = period.getDays() + Constant.ELAPSED_TIME_DAY;
        else if (period.getHours() > 0) elapsedTime = period.getHours() + Constant.ELAPSED_TIME_HOUR;
        else if (period.getMinutes() > 0) elapsedTime = period.getMinutes() + Constant.ELAPSED_TIME_MINUTE;
        else if (period.getSeconds() > 0) elapsedTime = period.getSeconds() + Constant.ELAPSED_TIME_SECOND;
        Log.v(LOG_TAG,elapsedTime);
        Log.v(LOG_TAG,"elapsedTime la gi "+elapsedTime);
        return elapsedTime;
    }

    public static String covertDuration(String isoDuration){
        Log.v(LOG_TAG,"duration la gi " +isoDuration);
        SimpleDateFormat simpleDateFormat = null;
        if (isoDuration.contains("H")){
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            if (!isoDuration.contains("M"))
                isoDuration = isoDuration.replace("H", "H00:");
            else if (!isoDuration.contains("S"))
                isoDuration = isoDuration + "00S";
        }
        else {
            simpleDateFormat = new SimpleDateFormat("mm:ss");
            if (!isoDuration.contains("M"))
                isoDuration = isoDuration.replace("PT", Constant.FORMAT_M);
            else if (!isoDuration.contains("S"))
                isoDuration = isoDuration + Constant.FORMAT_S;
        }
        Log.v(LOG_TAG,"iso duration " +isoDuration);

        String formatted = isoDuration.replace("PT","").replace("H",":").replace("M",":").replace("S","");
        Log.v(LOG_TAG,"thoi gian " +formatted);

        Date date = null;
        try {
            date = simpleDateFormat.parse(formatted);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedTime = simpleDateFormat.format(date);
        return formattedTime;
    }

}