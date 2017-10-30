package com.example.administrator.youtube.model;

import java.io.Serializable;

/**
 * Created by Administrator on 9/24/2017.
 */

public class Video implements Serializable {

    public void setmID(String mID) {
        this.mID = mID;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmThumbnails(String mThumbnails) {
        this.mThumbnails = mThumbnails;
    }

    public void setmViewCount(String mViewCount) {
        this.mViewCount = mViewCount;
    }

    public void setmTitleChannel(String mTitleChannel) {
        this.mTitleChannel = mTitleChannel;
    }

    public void setmDurationString(String mDurationString) {
        this.mDurationString = mDurationString;
    }

    public void setmDesciption(String mDesciption) {
        this.mDesciption = mDesciption;
    }

    public void setmChannelId(String mChannelId) {
        this.mChannelId = mChannelId;
    }

    public void setmEslapedTime(String mEslapedTime) {
        this.mEslapedTime = mEslapedTime;
    }

    private String mID;
    private String mTitle;
    private String mThumbnails;
    private String mViewCount;
    private String mTitleChannel;
    private String mDurationString;
    private String mDesciption;
    private String mChannelId;
    private String mEslapedTime;


    public Video() {

    }

    public Video(String id, String title, String thumbnails,
                 String titleChannel, String viewCount,
                 String durationString,
                 String channelId, String eslapedTime) {

        mID = id;
        mTitle = title;
        mThumbnails = thumbnails;
        mTitleChannel = titleChannel;
        mViewCount = viewCount;
        mDurationString = durationString;
        mChannelId = channelId;
        mEslapedTime = eslapedTime;
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

    public String getViewCount() {
        return mViewCount;
    }


    public String getTitleChannel() {
        return mTitleChannel;
    }

    public String getDurationString() {
        return mDurationString;
    }


    public String getChannelId() {
        return mChannelId;
    }

    public String getEslapedTime() {
        return mEslapedTime;
    }


}

