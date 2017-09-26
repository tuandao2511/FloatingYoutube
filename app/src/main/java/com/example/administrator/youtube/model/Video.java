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

    public Video(String id, String title, String thumbnails,
                 String titleChannel, int viewCount, int likeCount,
                 int dislikeCount, String durationString, String description,
                 String channelId, String eslapedTime) {

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
    }

    public String getmID() {
        return mID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmThumbnails() {
        return mThumbnails;
    }

    public int getmViewCount() {
        return mViewCount;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }

    public String getmTitleChannel() {
        return mTitleChannel;
    }

    public String getmDurationString() {
        return mDurationString;
    }

    public String getmDesciption() {
        return mDesciption;
    }

    public String getmChannelId() {
        return mChannelId;
    }

    public String getmEslapedTime() {
        return mEslapedTime;
    }

    public int getmDislikeCount() {
        return mDislikeCount;
    }
}

