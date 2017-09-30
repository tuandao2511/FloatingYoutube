package com.example.administrator.youtube.model;

/**
 * Created by Administrator on 9/24/2017.
 */

public class Video {
    private String mID ;
    private String mTitle;
    private String mThumbnails;
    private int mViewCount;
    private int mLikeCount;
    private String mTitleChannel;
    private String mDurationString;
    private String mDesciption;
    private String mChannelId;
    private String mEslapedTime;
    private int mDislikeCount;
    private String mThumbnailsChannel;



    public Video(String id, String title, String thumbnails,
                 String titleChannel, int viewCount, int likeCount,
                 int dislikeCount, String durationString, String description,
                 String channelId, String eslapedTime, String thumbnailsChannel) {

        mID = id;
        mTitle = title;
        mThumbnails = thumbnails;
        mTitleChannel = titleChannel;
        mViewCount = viewCount;
        mLikeCount = likeCount;
        mDislikeCount = dislikeCount;
        mDurationString = durationString;
        mDesciption = description;
        mChannelId = channelId;
        mEslapedTime = eslapedTime;
        mThumbnailsChannel = thumbnailsChannel;

    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnails() {
        return mThumbnails;
    }

    public int getViewCount() {
        return mViewCount;
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public String getTitleChannel() {
        return mTitleChannel;
    }

    public String getDurationString() {
        return mDurationString;
    }

    public String getDesciption() {
        return mDesciption;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public String getEslapedTime() {
        return mEslapedTime;
    }

    public int getDislikeCount() {
        return mDislikeCount;
    }

    public String getThumbnailsChannel() {
        return mThumbnailsChannel;
    }
}

