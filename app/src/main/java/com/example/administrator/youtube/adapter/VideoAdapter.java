package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.administrator.youtube.R;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.model.Video;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/18/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private List<Video> videoList;
    private final static String LOG_TAG = VideoAdapter.class.getSimpleName();
    private onItemClickListener onItemClickListener;
    public VideoAdapter(List<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.MyViewHolder holder, int position) {
        final Video video = videoList.get(position);
        holder.title.setText(video.getTitle());
        holder.info.setText(video.getTitleChannel() + " - " + video.getViewCount() + " - " + video.getEslapedTime());
        holder.duration.setText(video.getDurationString());
        Picasso.with(holder.item.getContext()).
                load(video.getThumbnails()).
                into(holder.thumbnails);
        Picasso.with(holder.item.getContext()).
                load(video.getThumbnailsChannel()).
                into(holder.thumbnailsChannel);
        holder.thumbnails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!= null) onItemClickListener.onItemCLickListener(video.getID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnails,thumbnailsChannel;
        public TextView title, info,duration;
        public View item;


        public MyViewHolder(View itemView) {

            super(itemView);
            thumbnails = (ImageView) itemView.findViewById(R.id.img_thumnail);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            info = (TextView) itemView.findViewById(R.id.infoVideo);
            duration = (TextView) itemView.findViewById(R.id.tv_duration);
            thumbnailsChannel = (ImageView) itemView.findViewById(R.id.thumbnailsChannel);
            item = itemView;
        }
    }

    //    public  Bitmap lessResolution (String filePath, int width, int height) {
//        int reqHeight = height;
//        int reqWidth = width;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        return BitmapFactory.decodeFile(filePath, options);
//    }
//
//    private  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            // Calculate ratios of height and width to requested height and width
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//
//            // Choose the smallest ratio as inSampleSize value, this will guarantee
//            // a final image with both dimensions larger than or equal to the
//            // requested height and width.
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        return inSampleSize;
//    }
    public void loadMoreVideo(List<Video> videoLists) {
        videoList.addAll(videoLists);
        notifyDataSetChanged();
    }
    public interface  onItemClickListener{
        public void onItemCLickListener(String videoId);
    }
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener  = onItemClickListener;
    }
}
