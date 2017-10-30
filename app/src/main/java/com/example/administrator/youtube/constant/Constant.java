package com.example.administrator.youtube.constant;

/**
 * Created by Administrator on 9/25/2017.
 */

public final class Constant {
    public static final String URL_DEFAULT = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics";
    public static final String URL_DEFAULT_WITH_MOST_VIEW = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&maxResults=10&id=";
    public static final String CHART = "&chart=";
    public static final String MAX_RESULT = "&maxResults=";
    public static final String PAGE_TOKEN = "&pageToken=";
    public static final String REGION_CODE = "&regionCode=";
    public static final String API_KEY = "AIzaSyAcpdUiD8t256JRtSGY2241yhRzJfXtaY8";
    public static final String FORMAT_M =  "00M";
    public static final String FORMAT_H = "H";
    public static final String FORMAT_S = "00S";
    public static final String ELAPSED_TIME_YEAR = " years ago";
    public static final String ELAPSED_TIME_MONTH = " months ago";
    public static final String ELAPSED_TIME_DAY = " days ago";
    public static final String ELAPSED_TIME_HOUR = " hours ago";
    public static final String ELAPSED_TIME_MINUTE = " minutes ago";
    public static final String ELAPSED_TIME_SECOND = " seconds ago";
    public static final String URL_CHANNEL = "https://www.googleapis.com/youtube/v3/channels?part=snippet";
    public static final String PLAY_VIDEO_FROM_MAINACTIVITY = "playFromMain";
    public static final String PLAY_VIDEO_FROM_SEARCHACTIVITY = "playFromSearch";
    public static final String PLAY_VIDEO_FROM_PLAYSERVICE = "playFromService";
    public static  final String PLAY_VIDEO_FROM_FULLSCREEN_ACTIVITY  = "playfromFullActivity";
    public static  final String PLAY_VIDEO_FROM_MAIN_ACTIVITY_IN_SERVICE  = "playFromMainActivityInService";
    public static final String AUTO = "default";
    public static final String QUALITY_144P ="small";
    public static final String QUALITY_360P ="medium";
    public static final String QUALITY_480P ="large";
    public static final String QUALITY_720P ="hd720";
    public static final String QUALITY_1080P ="hd1080";

}
