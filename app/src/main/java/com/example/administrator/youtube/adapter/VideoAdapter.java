package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.administrator.youtube.R;
import com.example.administrator.youtube.interfaces.OnLoadMoreListener;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.model.Video;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

/**
 * Created by Administrator on 9/18/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerViews;
    private ArrayList<Video> videoList;
    private final static String LOG_TAG = VideoAdapter.class.getSimpleName();
    private onItemClickListener onItemClickListener;

    public VideoAdapter(ArrayList<Video> videoList, RecyclerView recyclerView) {
        this.videoList = videoList;
        this.recyclerViews = recyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerViews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleThreshold = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    Log.e(LOG_TAG, visibleThreshold + " " + totalItemCount + " " + lastVisibleItem);

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


            final Video video = videoList.get(position);
            holder.title.setText(video.getTitle());
            holder.info.setText(video.getTitleChannel() + " - " + video.getViewCount() + " - " + video.getEslapedTime());
            holder.duration.setText(video.getDurationString());
            Picasso.with(holder.item.getContext()).
                    load(video.getThumbnails()).
                    into(holder.thumbnails);
//        Picasso.with(holder.item.getContext()).
//                load(video.getThumbnailsChannel()).
//                into(holder.thumbnailsChannel);
            holder.thumbnails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemCLickListener(position, videoList);
                }
            });
            holder.popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onPopupClickListener(position, videoList, holder.itemView);
                }
            });

    }

    @Override
    public int getItemCount() {

        return videoList == null ? 0 : videoList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnails, thumbnailsChannel;
        RelativeLayout popup;
        TextView title, info, duration;
        View item;



        public MyViewHolder(View itemView) {

            super(itemView);
            thumbnails = (ImageView) itemView.findViewById(R.id.img_thumnail);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            info = (TextView) itemView.findViewById(R.id.infoVideo);
            duration = (TextView) itemView.findViewById(R.id.tv_duration);
            thumbnailsChannel = (ImageView) itemView.findViewById(R.id.thumbnailsChannel);
            popup = (RelativeLayout) itemView.findViewById(R.id.popup);
            item = itemView;
        }
    }

    public void loadMoreVideo(ArrayList<Video> videoLists) {
        videoList.addAll(videoLists);

        notifyDataSetChanged();
    }
    public void refeshList(ArrayList<Video> videos){
        videoList.clear();
        videoList.addAll(videos);
        notifyDataSetChanged();
    }

    public interface onItemClickListener {
        public void onItemCLickListener(int position, ArrayList<Video> videos);

        public void onPopupClickListener(int position, ArrayList<Video> videos, View view);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setmOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

}
